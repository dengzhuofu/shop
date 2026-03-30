package com.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName(value = "pms_sku", autoResultMap = true)
public class PmsSku {
  @TableId(type = IdType.AUTO)
  private Long id;
  private Long productId;
  private String skuCode;
  private BigDecimal price;
  private Integer stock;

  private String pic;

  @TableField(typeHandler = JacksonTypeHandler.class)
  private JsonNode description;

  @TableField(typeHandler = JacksonTypeHandler.class)
  private JsonNode specs;

  private LocalDateTime createTime;
  private LocalDateTime updateTime;
}
