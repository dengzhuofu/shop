package com.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName(value = "sms_coupon", autoResultMap = true)
public class SmsCoupon {
  @TableId(type = IdType.AUTO)
  private Long id;
  private String code;

  @TableField(typeHandler = JacksonTypeHandler.class)
  private JsonNode title;

  @TableField(typeHandler = JacksonTypeHandler.class)
  private JsonNode description;

  private BigDecimal thresholdAmount;
  private BigDecimal discountAmount;
  private Boolean active;
  private LocalDateTime createTime;
  private LocalDateTime updateTime;
}
