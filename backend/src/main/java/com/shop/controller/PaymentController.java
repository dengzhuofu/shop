package com.shop.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.shop.common.JsonLocaleUtils;
import com.shop.common.PaymentMethodCatalog;
import com.shop.common.Result;
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
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

  private final OmsOrderService orderService;
  private final PayPaymentIntentService paymentIntentService;

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
    intent.setProviderKey("mock");
    intent.setStatus("CREATED");
    intent.setClientSecret("mock_secret_" + UUID.randomUUID().toString().replace("-", ""));
    intent.setMockResult("pending");
    intent.setCreateTime(LocalDateTime.now());
    intent.setUpdateTime(LocalDateTime.now());
    paymentIntentService.save(intent);

    order.setPaymentMethod(dto.getPaymentMethod());
    order.setPaymentIntentId(intent.getId());
    order.setStatus("PAYMENT_PROCESSING");
    order.setPaymentStatus("PENDING");
    order.setUpdateTime(LocalDateTime.now());
    orderService.updateById(order);

    return Result.success(PaymentIntentVO.from(intent));
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

    OmsOrder order = orderService.getById(intent.getOrderId());
    if (order == null || !order.getUserId().equals(userId)) {
      return Result.error(404, "Order not found");
    }

    if ("success".equalsIgnoreCase(dto.getMockResult())) {
      intent.setStatus("SUCCEEDED");
      intent.setMockResult("success");
      intent.setPaidTime(LocalDateTime.now());
      order.setStatus("PAID");
      order.setPaymentStatus("PAID");
      order.setPayTxnNo("MOCK_TXN_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
      order.setPayTime(LocalDateTime.now());
    } else {
      intent.setStatus("FAILED");
      intent.setMockResult("failed");
      order.setStatus("PENDING_PAYMENT");
      order.setPaymentStatus("FAILED");
    }

    intent.setUpdateTime(LocalDateTime.now());
    paymentIntentService.updateById(intent);
    order.setUpdateTime(LocalDateTime.now());
    orderService.updateById(order);
    return Result.success(PaymentIntentVO.from(intent));
  }
}
