package com.shop.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemVO {
  private Long cartItemId;
  private Long productId;
  private Long skuId;
  private String title;
  private String slug;
  private String productPic;
  private BigDecimal unitPrice;
  private BigDecimal addonAmount;
  private Integer quantity;
  private BigDecimal lineAmount;
  private Object attributes;
  private Object addons;
  private Integer stock;
}
