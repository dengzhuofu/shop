package com.shop.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderPreviewVO {
  private List<CartItemVO> items;
  private String currency;
  private String country;
  private BigDecimal subtotal;
  private BigDecimal shippingAmount;
  private BigDecimal taxAmount;
  private BigDecimal discountAmount;
  private BigDecimal totalAmount;
  private String previewToken;
  private Object coupon;
  private List<PaymentMethodVO> paymentMethods;
}
