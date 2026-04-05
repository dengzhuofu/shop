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
  private BigDecimal subtotalAmount;
  private BigDecimal taxAmount;
  private BigDecimal totalAmount;
  private String status;
  private String paymentStatus;
  private String currency;
  private String country;
  private String previewToken;
  private String couponCode;
  private Long couponUserId;
  private BigDecimal couponDiscountAmount;
  private Long paymentIntentId;
  
  private String receiverName;
  private String receiverPhone;
  private String receiverAddress;
  private String receiverCountry;
  private String receiverFirstName;
  private String receiverLastName;
  private String receiverAddressLine1;
  private String receiverAddressLine2;
  private String receiverCity;
  private String receiverState;
  private String receiverZipCode;
  private String paymentMethod;
  private String payTxnNo;
  private String checkoutSource;
  private String shippingMethod;
  private BigDecimal shippingAmount;
  private BigDecimal discountAmount;
  private String remark;
  private LocalDateTime payTime;
  private String deliveryCompany;
  private String deliverySn;

  private LocalDateTime createTime;
  private LocalDateTime updateTime;
}
