package com.shop.controller;


import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shop.common.AlipaySignatureUtils;
import com.shop.common.JsonLocaleUtils;
import com.shop.common.PaymentMethodCatalog;
import com.shop.common.Result;
import com.shop.config.PaymentProperties;
import com.shop.dto.PaymentIntentCreateDTO;
import com.shop.dto.PaymentMockCompleteDTO;
import com.shop.entity.OmsOrder;
import com.shop.entity.PayPaymentIntent;
import com.shop.service.AlipayGatewayService;
import com.shop.service.OmsOrderService;
import com.shop.service.PayPaymentIntentService;
import com.shop.vo.PaymentIntentVO;
import com.shop.vo.PaymentMethodVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

  private final OmsOrderService orderService;
  private final PayPaymentIntentService paymentIntentService;
  private final PaymentProperties paymentProperties;
  private final AlipayGatewayService alipayGatewayService;

  @GetMapping("/methods")
  public Result<List<PaymentMethodVO>> methods() {
    return Result.success(PaymentMethodCatalog.methods(JsonLocaleUtils.currentLanguage()));
  }

  @PostMapping("/intent")
  public Result<PaymentIntentVO> createIntent(@RequestBody PaymentIntentCreateDTO dto) {
    Long userId = StpUtil.getLoginIdAsLong();
    OmsOrder order = orderService.getById(dto.getOrderId());
    if (order == null || !order.getUserId().equals(userId)) {
      return Result.error(404, "Order not found");
    }
    if (!"PENDING_PAYMENT".equals(order.getStatus()) && !"PAYMENT_PROCESSING".equals(order.getStatus())) {
      return Result.error(400, "Order is not payable");
    }

    PayPaymentIntent intent = new PayPaymentIntent();
    intent.setIntentNo("PI_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase());
    intent.setOrderId(order.getId());
    intent.setUserId(userId);
    intent.setAmount(order.getTotalAmount());
    intent.setCurrency(order.getCurrency());
    intent.setMethodCode(dto.getPaymentMethod());
    intent.setCreateTime(LocalDateTime.now());
    intent.setUpdateTime(LocalDateTime.now());

    PaymentIntentVO response = isAlipayMethod(dto.getPaymentMethod())
        ? prepareAlipayIntent(intent, order)
        : prepareMockIntent(intent, false);

    paymentIntentService.save(intent);

    order.setPaymentMethod(dto.getPaymentMethod());
    order.setPaymentIntentId(intent.getId());
    order.setStatus("PAYMENT_PROCESSING");
    order.setPaymentStatus("PENDING");
    order.setUpdateTime(LocalDateTime.now());
    orderService.updateById(order);

    PaymentIntentVO savedResponse = PaymentIntentVO.from(intent);
    savedResponse.setNextAction(response.getNextAction());
    savedResponse.setRedirectUrl(response.getRedirectUrl());
    savedResponse.setDisplayMessage(response.getDisplayMessage());
    savedResponse.setSandbox(response.getSandbox());
    return Result.success(savedResponse);
  }

  @GetMapping("/intent/{id}")
  public Result<PaymentIntentVO> detail(@PathVariable Long id) {
    Long userId = StpUtil.getLoginIdAsLong();
    PayPaymentIntent intent = paymentIntentService.getById(id);
    if (intent == null || !intent.getUserId().equals(userId)) {
      return Result.error(404, "Payment intent not found");
    }
    return Result.success(PaymentIntentVO.from(intent));
  }

  @PostMapping("/mock/complete")
  public Result<PaymentIntentVO> mockComplete(@RequestBody PaymentMockCompleteDTO dto) {
    Long userId = StpUtil.getLoginIdAsLong();
    PayPaymentIntent intent = paymentIntentService.getById(dto.getPaymentIntentId());
    if (intent == null || !intent.getUserId().equals(userId)) {
      return Result.error(404, "Payment intent not found");
    }
    if (!"mock".equalsIgnoreCase(intent.getProviderKey())) {
      return Result.error(400, "Only mock payment intents can be completed here");
    }

    OmsOrder order = orderService.getById(intent.getOrderId());
    if (order == null || !order.getUserId().equals(userId)) {
      return Result.error(404, "Order not found");
    }

    if ("success".equalsIgnoreCase(dto.getMockResult())) {
      markPaymentSucceeded(intent, order, "MOCK_TXN_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    } else {
      markPaymentFailed(intent, order, "failed");
    }

    paymentIntentService.updateById(intent);
    orderService.updateById(order);
    return Result.success(PaymentIntentVO.from(intent));
  }

  @PostMapping("/alipay/return/confirm")
  public Result<PaymentIntentVO> confirmAlipayReturn(@RequestBody Map<String, String> params) {
    return Result.success(processAlipayCallback(params, true));
  }

  @PostMapping("/alipay/notify")
  public String handleAlipayNotify(@RequestParam Map<String, String> params) {
    try {
      processAlipayCallback(params, false);
      return "success";
    } catch (Exception ex) {
      return "fail";
    }
  }

  private PaymentIntentVO prepareMockIntent(PayPaymentIntent intent, boolean fallbackFromAlipay) {
    intent.setProviderKey("mock");
    intent.setStatus("CREATED");
    intent.setClientSecret("mock_secret_" + UUID.randomUUID().toString().replace("-", ""));
    intent.setMockResult("pending");

    PaymentIntentVO response = PaymentIntentVO.from(intent);
    response.setNextAction("MOCK_COMPLETE");
    response.setDisplayMessage(fallbackFromAlipay
        ? "Alipay sandbox is not configured yet, so checkout fell back to mock payment."
        : "Mock payment is ready.");
    response.setSandbox(false);
    return response;
  }

  private PaymentIntentVO prepareAlipayIntent(PayPaymentIntent intent, OmsOrder order) {
    PaymentProperties.AlipayProperties alipay = paymentProperties.getAlipay();
    if (!alipay.isConfigured()) {
      if (!alipay.isFallbackToMock()) {
        throw new IllegalStateException("Alipay sandbox is enabled but not fully configured");
      }
      return prepareMockIntent(intent, true);
    }

    intent.setProviderKey(alipay.isSandbox() ? "alipay_sandbox" : "alipay");
    intent.setStatus("CREATED");
    intent.setClientSecret(null);
    intent.setMockResult(null);

    PaymentIntentVO response = PaymentIntentVO.from(intent);
    response.setNextAction("REDIRECT");
    response.setRedirectUrl(alipayGatewayService.createPageRedirectUrl(
        alipay,
        intent,
        order,
        buildAlipaySubject(order, alipay)
    ));
    response.setDisplayMessage(alipay.isSandbox()
        ? "Redirecting to Alipay sandbox..."
        : "Redirecting to Alipay...");
    response.setSandbox(alipay.isSandbox());
    return response;
  }

  private PaymentIntentVO processAlipayCallback(Map<String, String> callbackParams, boolean preferAuthoritativeQuery) {
    Map<String, String> normalizedParams = normalizeAlipayCallbackParams(callbackParams);
    PaymentProperties.AlipayProperties alipay = paymentProperties.getAlipay();
    if (!alipay.isConfigured()) {
      throw new IllegalStateException("Alipay is not configured");
    }
    if (!AlipaySignatureUtils.verify(
        normalizedParams,
        alipay.getAlipayPublicKey(),
        alipay.getCharset(),
        alipay.getSignType()
    )) {
      throw new IllegalArgumentException("Invalid Alipay signature");
    }
    if (!alipay.isCrossBorderMode()
        && hasText(normalizedParams.get("app_id"))
        && !alipay.getAppId().equals(normalizedParams.get("app_id"))) {
      throw new IllegalArgumentException("Alipay appId mismatch");
    }

    String outTradeNo = normalizedParams.get("out_trade_no");
    if (!hasText(outTradeNo)) {
      throw new IllegalArgumentException("Missing out_trade_no");
    }

    PayPaymentIntent intent = paymentIntentService.getOne(new QueryWrapper<PayPaymentIntent>()
        .eq("intent_no", outTradeNo)
        .last("LIMIT 1"), false);
    if (intent == null) {
      throw new IllegalArgumentException("Payment intent not found");
    }
    if (intent.getProviderKey() == null || !intent.getProviderKey().startsWith("alipay")) {
      throw new IllegalArgumentException("Payment intent provider mismatch");
    }

    OmsOrder order = orderService.getById(intent.getOrderId());
    if (order == null) {
      throw new IllegalArgumentException("Order not found");
    }

    String tradeStatus = normalizedParams.get("trade_status");
    String tradeNo = normalizedParams.get("trade_no");
    BigDecimal paidAmount = parseAmount(firstNonBlank(
        normalizedParams.get("total_amount"),
        normalizedParams.get("receipt_amount"),
        normalizedParams.get("buyer_pay_amount"),
        normalizedParams.get("total_fee")
    ));

    if (preferAuthoritativeQuery || !hasText(tradeStatus)) {
      AlipayGatewayService.TradeQueryResult tradeQueryResult = alipayGatewayService.queryTrade(alipay, outTradeNo, tradeNo);
      if (tradeQueryResult.success()) {
        tradeStatus = firstNonBlank(tradeQueryResult.tradeStatus(), tradeStatus);
        tradeNo = firstNonBlank(tradeQueryResult.tradeNo(), tradeNo);
        if (paidAmount == null) {
          paidAmount = tradeQueryResult.totalAmount();
        }
      }
    }

    if (paidAmount != null && (intent.getAmount() == null || intent.getAmount().compareTo(paidAmount) != 0)) {
      throw new IllegalArgumentException("Paid amount mismatch");
    }

    if ("TRADE_SUCCESS".equalsIgnoreCase(tradeStatus) || "TRADE_FINISHED".equalsIgnoreCase(tradeStatus)) {
      markPaymentSucceeded(intent, order, tradeNo);
    } else if ("TRADE_CLOSED".equalsIgnoreCase(tradeStatus)) {
      markPaymentFailed(intent, order, "closed");
    }

    paymentIntentService.updateById(intent);
    orderService.updateById(order);

    PaymentIntentVO response = PaymentIntentVO.from(intent);
    response.setDisplayMessage(resolveAlipayMessage(tradeStatus));
    response.setSandbox(alipay.isSandbox());
    return response;
  }

  private Map<String, String> normalizeAlipayCallbackParams(Map<String, String> callbackParams) {
    Map<String, String> normalized = new LinkedHashMap<>();
    callbackParams.forEach((key, value) -> {
      if (!hasText(key) || "lang".equalsIgnoreCase(key)) {
        return;
      }
      normalized.put(key, "sign".equalsIgnoreCase(key) && value != null
          ? value.replace(' ', '+')
          : value);
    });
    return normalized;
  }

  private void markPaymentSucceeded(PayPaymentIntent intent, OmsOrder order, String transactionNo) {
    LocalDateTime now = LocalDateTime.now();
    intent.setStatus("SUCCEEDED");
    intent.setMockResult("success");
    intent.setPaidTime(now);
    intent.setUpdateTime(now);

    order.setStatus("PAID");
    order.setPaymentStatus("PAID");
    order.setPayTxnNo(hasText(transactionNo)
        ? transactionNo
        : "PAY_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    order.setPayTime(now);
    order.setUpdateTime(now);
  }

  private void markPaymentFailed(PayPaymentIntent intent, OmsOrder order, String result) {
    LocalDateTime now = LocalDateTime.now();
    intent.setStatus("FAILED");
    intent.setMockResult(result);
    intent.setUpdateTime(now);

    order.setStatus("PENDING_PAYMENT");
    order.setPaymentStatus("FAILED");
    order.setUpdateTime(now);
  }

  private boolean isAlipayMethod(String paymentMethod) {
    return "alipay".equalsIgnoreCase(paymentMethod) || "alipay_sandbox".equalsIgnoreCase(paymentMethod);
  }

  private String buildAlipaySubject(OmsOrder order, PaymentProperties.AlipayProperties alipay) {
    String subject = alipay.getSubjectPrefix() + " " + order.getOrderSn();
    return subject.length() > 256 ? subject.substring(0, 256) : subject;
  }

  private boolean hasText(String value) {
    return value != null && !value.isBlank();
  }

  private BigDecimal parseAmount(String value) {
    return hasText(value) ? new BigDecimal(value) : null;
  }

  private String firstNonBlank(String... values) {
    for (String value : values) {
      if (hasText(value)) {
        return value;
      }
    }
    return null;
  }

  private String resolveAlipayMessage(String tradeStatus) {
    if ("TRADE_SUCCESS".equalsIgnoreCase(tradeStatus) || "TRADE_FINISHED".equalsIgnoreCase(tradeStatus)) {
      return "Alipay payment confirmed.";
    }
    if ("WAIT_BUYER_PAY".equalsIgnoreCase(tradeStatus)) {
      return "Alipay payment is still pending.";
    }
    if ("TRADE_CLOSED".equalsIgnoreCase(tradeStatus)) {
      return "Alipay payment was closed.";
    }
    return "Alipay return received.";
  }
}
