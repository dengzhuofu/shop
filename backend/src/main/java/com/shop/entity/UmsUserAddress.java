package com.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

// import java.time.LocalDateTime;
// import java.time.LocalDateTime;
import java.time.LocalDateTime;

@Data
@TableName("ums_user_address")
public class UmsUserAddress {
  @TableId(type = IdType.AUTO)
  private Long id;
  private Long userId;
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
  // private LocalDateTime createTime;
  private LocalDateTime createTime;

  private LocalDateTime updateTime;
}
