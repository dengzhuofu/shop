package com.shop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentIntentCreateDTO {
  @NotNull(message = "Order ID cannot be null")
  private Long orderId;

  @NotBlank(message = "Payment method cannot be empty")
  private String paymentMethod;
}
