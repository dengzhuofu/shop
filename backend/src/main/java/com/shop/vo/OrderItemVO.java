package com.shop.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItemVO {
    private Long id;
    private Long productId;
    private Long skuId;
    private String productName;
    private String slug;
    private String productPic;
    private String skuCode;
    private Object skuAttributesSnapshot;
    private Object addons;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineAmount;
}
