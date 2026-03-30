package com.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName(value = "pms_product", autoResultMap = true)
public class PmsProduct {
  @TableId(type = IdType.AUTO)
  private Long id;

  @TableField(typeHandler = JacksonTypeHandler.class)
  private JsonNode name;

  @TableField(typeHandler = JacksonTypeHandler.class)
  private JsonNode description;

  private BigDecimal price;
  private BigDecimal compareAtPrice;
  private Integer stock;
  private String pic;

  @TableField(typeHandler = JacksonTypeHandler.class)
  private JsonNode tags;

  @TableField(typeHandler = JacksonTypeHandler.class)
  private JsonNode images;

  private String appImage;

  @TableField(typeHandler = JacksonTypeHandler.class)
  private JsonNode specs;

  @TableField(typeHandler = JacksonTypeHandler.class)
  private JsonNode quickKnow;

  @TableField(typeHandler = JacksonTypeHandler.class)
  private JsonNode upsells;

  private LocalDateTime createTime;
  private LocalDateTime updateTime;
}
