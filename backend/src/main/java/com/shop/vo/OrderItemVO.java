package com.shop.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderItemVO {
    private Long id;
    private Long productId;
    private Long skuId;
    private String productName;
    private String productPic;
    private String skuCode;
    private Integer quantity;
    private BigDecimal price;
}