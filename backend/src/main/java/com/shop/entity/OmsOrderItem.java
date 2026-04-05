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
@TableName(value = "oms_order_item", autoResultMap = true)
public class OmsOrderItem {
  @TableId(type = IdType.AUTO)
  private Long id;
  private Long orderId;
  private Long productId;
  private Long skuId;

  @TableField(typeHandler = JacksonTypeHandler.class)
  private JsonNode productName;
  private String productPic;
  private String skuCode;
  @TableField(typeHandler = JacksonTypeHandler.class)
  private JsonNode skuAttributesSnapshot;
  @TableField(typeHandler = JacksonTypeHandler.class)
  private JsonNode addonsSnapshot;

  private Integer quantity;
  private BigDecimal unitPrice;
  private BigDecimal lineAmount;
  private LocalDateTime createTime;
  private LocalDateTime updateTime;
}
