package com.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("oms_order")
public class OmsOrder {
  @TableId(type = IdType.AUTO)
  private Long id;
  private Long userId;
  private String orderSn;
  private BigDecimal totalAmount;
  private Integer status; // 0: 待付款, 1: 已付款, 2: 已发货, 3: 已完成, 4: 已取消
  
  private String receiverName;
  private String receiverPhone;
  private String receiverAddress;
  private Integer payType; // 1: Alipay, 2: Wechat, 3: Credit Card, 4: PayPal
  private LocalDateTime payTime;
  private String deliveryCompany;
  private String deliverySn;

  private LocalDateTime createTime;
  private LocalDateTime updateTime;
}
