package com.shop.vo;

import com.shop.entity.PayPaymentIntent;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentIntentVO {
  private Long id;
  private String intentNo;
  private Long orderId;
  private BigDecimal amount;
  private String currency;
  private String methodCode;
  private String providerKey;
  private String status;
  private String clientSecret;
  private String mockResult;
  private LocalDateTime paidTime;
  private String nextAction;
  private String redirectUrl;
  private String displayMessage;
  private Boolean sandbox;

  public static PaymentIntentVO from(PayPaymentIntent intent) {
    PaymentIntentVO vo = new PaymentIntentVO();
    vo.setId(intent.getId());
    vo.setIntentNo(intent.getIntentNo());
    vo.setOrderId(intent.getOrderId());
    vo.setAmount(intent.getAmount());
    vo.setCurrency(intent.getCurrency());
    vo.setMethodCode(intent.getMethodCode());
    vo.setProviderKey(intent.getProviderKey());
    vo.setStatus(intent.getStatus());
    vo.setClientSecret(intent.getClientSecret());
    vo.setMockResult(intent.getMockResult());
    vo.setPaidTime(intent.getPaidTime());
    return vo;
  }
}
