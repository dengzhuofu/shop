package com.shop.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderPreviewDTO {
  private String source;
  private List<Long> cartItemIds;
  private List<OrderCreateDTO.Item> items;
  private Long addressId;
  private String shippingMethod;
  private Long couponUserId;
}
