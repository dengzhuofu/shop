package com.shop.vo;

import com.fasterxml.jackson.databind.JsonNode;
import com.shop.common.LanguageContext;
import com.shop.entity.PmsSku;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class SkuVO {
    private Long id;
    private Long productId;
    private String skuCode;
    private BigDecimal price;
    private Integer stock;
    private String pic;
    private String description;
    private Object specs;

    public static SkuVO from(PmsSku sku) {
        SkuVO vo = new SkuVO();
        vo.setId(sku.getId());
        vo.setProductId(sku.getProductId());
        vo.setSkuCode(sku.getSkuCode());
        vo.setPrice(sku.getPrice());
        vo.setStock(sku.getStock());
        vo.setPic(sku.getPic());
        
        String lang = LanguageContext.getLanguage();
        vo.setDescription(ProductVO.extractLang(sku.getDescription(), lang));
        
        // Extract specs based on language
        JsonNode specsNode = sku.getSpecs();
        if (specsNode != null && specsNode.has(lang)) {
            vo.setSpecs(specsNode.get(lang));
        } else if (specsNode != null && specsNode.has("zh")) {
            vo.setSpecs(specsNode.get("zh"));
        } else {
            vo.setSpecs(specsNode);
        }
        
        return vo;
    }
}
