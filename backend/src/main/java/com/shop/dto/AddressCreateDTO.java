package com.shop.dto;

import lombok.Data;

@Data
public class AddressCreateDTO {
  private String country;
  private String firstName;
  private String lastName;
  private String phone;
  private String addressLine1;
  private String addressLine2;
  private String city;
  private String state;
  private String zipCode;
  private Boolean isDefault;
}
