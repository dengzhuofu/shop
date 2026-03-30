package com.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("oms_order_item")
public class OmsOrderItem {
  @TableId(type = IdType.AUTO)
  private Long id;
  private Long orderId;
  private Long productId;
  private Long skuId;
  
  private String productName;
  private String productPic;
  private String skuCode;

  private Integer quantity;
  private BigDecimal price;
  private LocalDateTime createTime;
  private LocalDateTime updateTime;
}
