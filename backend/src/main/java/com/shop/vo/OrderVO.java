package com.shop.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderVO {
    private Long id;
    private String orderSn;
    private BigDecimal subtotalAmount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private String status;
    private String paymentStatus;
    private String currency;
    private String country;
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
    private String couponCode;
    private BigDecimal couponDiscountAmount;
    private Long paymentIntentId;
    private String remark;
    private LocalDateTime payTime;
    private String deliveryCompany;
    private String deliverySn;
    private LocalDateTime createTime;
    
    private List<OrderItemVO> items;
}
