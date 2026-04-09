package com.shop.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentMethodVO {
  private String code;
  private String name;
  // private String group;
  private String group;

  private Boolean enabled;
}
