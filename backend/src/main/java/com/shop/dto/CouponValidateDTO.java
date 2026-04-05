package com.shop.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CouponValidateDTO {
  @NotNull(message = "Coupon user ID cannot be null")
  private Long couponUserId;

  @NotNull(message = "Subtotal amount cannot be null")
  private BigDecimal subtotalAmount;
}
