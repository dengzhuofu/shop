package com.shop.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderVO {
    private Long id;
    private String orderSn;
    private BigDecimal totalAmount;
    private Integer status;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private Integer payType;
    private LocalDateTime payTime;
    private String deliveryCompany;
    private String deliverySn;
    private LocalDateTime createTime;
    
    private List<OrderItemVO> items;
}