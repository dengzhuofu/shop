package com.shop.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class OrderCreateDTO {
  private String source;
  private List<Long> cartItemIds;
  private List<Item> items;
  private Long addressId;
  private AddressSnapshot addressSnapshot;
  private String shippingMethod;
  private Long couponUserId;
  private String previewToken;
  private String remark;

  @Data
  public static class Item {
    private Long productId;
    private Long skuId;
    private Integer quantity;
    private List<String> addonCodes;
    private Map<String, Object> attributes;
  }

  @Data
  public static class AddressSnapshot {
    private String country;
    private String firstName;
    private String lastName;
    private String phone;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String zipCode;
  }
}
