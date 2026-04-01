package com.shop.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderPreviewVO {
  private List<CartItemVO> items;
  private BigDecimal subtotal;
  private BigDecimal shippingAmount;
  private BigDecimal discountAmount;
  private BigDecimal totalAmount;
}
