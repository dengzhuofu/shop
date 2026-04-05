package com.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sms_coupon_user")
public class SmsCouponUser {
  @TableId(type = IdType.AUTO)
  private Long id;
  private Long couponId;
  private Long userId;
  private String status;
  private LocalDateTime claimedTime;
  private LocalDateTime createTime;
  private LocalDateTime updateTime;
}
