package com.shop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentMockCompleteDTO {
  @NotNull(message = "Payment intent ID cannot be null")
  private Long paymentIntentId;

  @NotBlank(message = "Mock result cannot be empty")
  private String mockResult;
}
