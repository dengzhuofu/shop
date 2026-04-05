package com.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("pay_payment_intent")
public class PayPaymentIntent {
  @TableId(type = IdType.AUTO)
  private Long id;
  private String intentNo;
  private Long orderId;
  private Long userId;
  private BigDecimal amount;
  private String currency;
  private String methodCode;
  private String providerKey;
  private String status;
  private String clientSecret;
  private String mockResult;
  private LocalDateTime paidTime;
  private LocalDateTime createTime;
  private LocalDateTime updateTime;
}
