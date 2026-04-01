package com.shop.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItemVO {
    private Long id;
    private Long productId;
    private Long skuId;
    private String productName;
    private String productPic;
    private String skuCode;
    private Object skuAttributesSnapshot;
    private Integer quantity;
    private BigDecimal price;
}
