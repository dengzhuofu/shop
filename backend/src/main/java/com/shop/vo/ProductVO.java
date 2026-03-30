package com.shop.vo;

import com.fasterxml.jackson.databind.JsonNode;
import com.shop.common.LanguageContext;
import com.shop.entity.PmsProduct;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ProductVO {
  private Long id;
  private String title; // Mapping name to title for frontend
  private String description;
  private BigDecimal price;
  private BigDecimal compareAtPrice;
  private Integer stock;
  private String pic;

  private Object tags;
  private Object images;
  private String appImage;
  private Object specs;
  private String quickKnow; // 富文本
  private Object upsells;

  private List<SkuVO> skuList;

  public static ProductVO from(PmsProduct product, List<com.shop.entity.PmsSku> skus) {
    ProductVO vo = new ProductVO();
    vo.setId(product.getId());
    vo.setPrice(product.getPrice());
    vo.setCompareAtPrice(product.getCompareAtPrice());
    vo.setStock(product.getStock());
    vo.setPic(product.getPic());
    vo.setAppImage(product.getAppImage());

    String lang = LanguageContext.getLanguage();
    vo.setTitle(extractLang(product.getName(), lang));
    vo.setDescription(extractLang(product.getDescription(), lang));
    vo.setQuickKnow(extractLang(product.getQuickKnow(), lang));

    // Multi-language JSON fields handling
    vo.setTags(extractLangObject(product.getTags(), lang));
    vo.setImages(extractLangObject(product.getImages(), lang));
    vo.setSpecs(extractLangObject(product.getSpecs(), lang));
    vo.setUpsells(extractLangObject(product.getUpsells(), lang));

    if (skus != null) {
      vo.setSkuList(skus.stream().map(SkuVO::from).collect(Collectors.toList()));
    }
    return vo;
  }

  public static String extractLang(JsonNode node, String lang) {
    if (node == null || node.isNull())
      return null;
    if (node.has(lang)) {
      return node.get(lang).asText();
    }
    if (node.has("zh")) {
      return node.get("zh").asText();
    }
    return node.toString();
  }

  public static Object extractLangObject(JsonNode node, String lang) {
    if (node == null || node.isNull())
      return null;
    // If the node itself is a localized object like {"zh": [...], "en": [...]}
    if (node.isObject() && (node.has("zh") || node.has("en"))) {
      if (node.has(lang)) {
        return node.get(lang);
      }
      if (node.has("zh")) {
        return node.get("zh");
      }
    }
    // If it's a regular array or object without localization at root
    return node;
  }
}
