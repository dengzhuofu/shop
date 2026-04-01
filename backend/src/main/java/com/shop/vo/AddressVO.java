package com.shop.vo;

import com.shop.entity.UmsUserAddress;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class AddressVO {
  private Long id;
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

  public static AddressVO from(UmsUserAddress address) {
    AddressVO vo = new AddressVO();
    BeanUtils.copyProperties(address, vo);
    return vo;
  }
}
