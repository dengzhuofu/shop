package com.shop.dto;

import lombok.Data;

@Data
public class OrderPayDTO {
  private Long orderId;
  private String paymentMethod;
  private String mockResult;
}
