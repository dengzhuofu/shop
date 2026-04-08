package com.shop.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.common.AlipaySignatureUtils;
import com.shop.common.JsonLocaleUtils;
import com.shop.common.PaymentMethodCatalog;
import com.shop.common.Result;
import com.shop.config.PaymentProperties;
import com.shop.dto.PaymentIntentCreateDTO;
import com.shop.dto.PaymentMockCompleteDTO;
import com.shop.entity.OmsOrder;
import com.shop.entity.PayPaymentIntent;
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
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
  private final ObjectMapper objectMapper;

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
    return Result.success(processAlipayCallback(params));
  }

  @PostMapping("/alipay/notify")
  public String handleAlipayNotify(@RequestParam Map<String, String> params) {
    try {
      processAlipayCallback(params);
      return "success";
    } catch (Exception ex) {
      return "failure";
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
    response.setRedirectUrl(buildAlipayRedirectUrl(intent, order, alipay));
    response.setDisplayMessage(alipay.isSandbox()
        ? "Redirecting to Alipay sandbox..."
        : "Redirecting to Alipay...");
    response.setSandbox(alipay.isSandbox());
    return response;
  }

  private String buildAlipayRedirectUrl(PayPaymentIntent intent, OmsOrder order,
      PaymentProperties.AlipayProperties alipay) {
    try {
      Map<String, String> params = new LinkedHashMap<>();
      params.put("app_id", alipay.getAppId());
      params.put("method", "alipay.trade.page.pay");
      params.put("format", "JSON");
      params.put("charset", alipay.getCharset());
      params.put("sign_type", alipay.getSignType());
      params.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
      params.put("version", "1.0");
      if (hasText(alipay.getNotifyUrl())) {
        params.put("notify_url", alipay.getNotifyUrl());
      }
      params.put("return_url", alipay.getReturnUrl());
      params.put("biz_content", objectMapper.writeValueAsString(Map.of(
          "out_trade_no", intent.getIntentNo(),
          "total_amount", normalizeAmount(intent.getAmount()),
          "subject", buildAlipaySubject(order, alipay),
          "product_code", "FAST_INSTANT_TRADE_PAY"
      )));
      params.put("sign", AlipaySignatureUtils.sign(
          params,
          alipay.getAppPrivateKey(),
          alipay.getCharset(),
          alipay.getSignType()
      ));
      return alipay.resolvedGateway() + "?" + toQueryString(params);
    } catch (Exception ex) {
      throw new IllegalStateException("Failed to build Alipay redirect URL", ex);
    }
  }

  private PaymentIntentVO processAlipayCallback(Map<String, String> callbackParams) {
    PaymentProperties.AlipayProperties alipay = paymentProperties.getAlipay();
    if (!alipay.isConfigured()) {
      throw new IllegalStateException("Alipay is not configured");
    }
    if (!AlipaySignatureUtils.verify(
        callbackParams,
        alipay.getAlipayPublicKey(),
        alipay.getCharset(),
        alipay.getSignType()
    )) {
      throw new IllegalArgumentException("Invalid Alipay signature");
    }
    if (hasText(callbackParams.get("app_id")) && !alipay.getAppId().equals(callbackParams.get("app_id"))) {
      throw new IllegalArgumentException("Alipay appId mismatch");
    }

    String outTradeNo = callbackParams.get("out_trade_no");
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

    if (hasText(callbackParams.get("total_amount"))) {
      BigDecimal paidAmount = new BigDecimal(callbackParams.get("total_amount"));
      if (intent.getAmount() == null || intent.getAmount().compareTo(paidAmount) != 0) {
        throw new IllegalArgumentException("Paid amount mismatch");
      }
    }

    String tradeStatus = callbackParams.get("trade_status");
    if ("TRADE_SUCCESS".equalsIgnoreCase(tradeStatus) || "TRADE_FINISHED".equalsIgnoreCase(tradeStatus)) {
      markPaymentSucceeded(intent, order, callbackParams.get("trade_no"));
    } else if ("TRADE_CLOSED".equalsIgnoreCase(tradeStatus)) {
      markPaymentFailed(intent, order, "closed");
    }

    paymentIntentService.updateById(intent);
    orderService.updateById(order);

    PaymentIntentVO response = PaymentIntentVO.from(intent);
    response.setDisplayMessage(("TRADE_SUCCESS".equalsIgnoreCase(tradeStatus)
        || "TRADE_FINISHED".equalsIgnoreCase(tradeStatus))
        ? "Alipay payment confirmed."
        : "Alipay return received.");
    response.setSandbox(alipay.isSandbox());
    return response;
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

  private String normalizeAmount(BigDecimal amount) {
    return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
  }

  private String toQueryString(Map<String, String> params) {
    StringBuilder builder = new StringBuilder();
    for (Map.Entry<String, String> entry : params.entrySet()) {
      if (builder.length() > 0) {
        builder.append('&');
      }
      builder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
      builder.append('=');
      builder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
    }
    return builder.toString();
  }

  private boolean hasText(String value) {
    return value != null && !value.isBlank();
  }
}
