package com.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("oms_cart_item")
public class OmsCartItem {
  @TableId(type = IdType.AUTO)
  private Long id;
  private Long userId;
  private Long productId;
  private Long skuId;
  private Integer quantity;
  @TableField(typeHandler = JacksonTypeHandler.class)
  private JsonNode selectedAttributesSnapshot;
  private LocalDateTime createTime;
  private LocalDateTime updateTime;
}
