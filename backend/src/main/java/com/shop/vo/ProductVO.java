package com.shop.vo;

import com.fasterxml.jackson.databind.JsonNode;
import com.shop.common.LanguageContext;
import com.shop.entity.PmsProduct;
import lombok.Data;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
  private Object quickKnow;
  private Object upsells;
  private Object skuAttributeOptions;
  private Object skuAttributeDisplayOptions;

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
    vo.setQuickKnow(extractLangObject(product.getQuickKnow(), lang));

    // Multi-language JSON fields handling
    vo.setTags(extractLangObject(product.getTags(), lang));
    vo.setImages(extractLangObject(product.getImages(), lang));
    vo.setSpecs(extractLangObject(product.getSpecs(), lang));
    vo.setUpsells(extractLangObject(product.getUpsells(), lang));

    if (skus != null) {
      vo.setSkuList(skus.stream().map(SkuVO::from).collect(Collectors.toList()));
      vo.setSkuAttributeOptions(buildSkuAttributeOptions(vo.getSkuList()));
      vo.setSkuAttributeDisplayOptions(buildSkuAttributeDisplayOptions(vo.getSkuList()));
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

  private static Map<String, List<String>> buildSkuAttributeOptions(List<SkuVO> skuList) {
    Map<String, List<String>> options = new LinkedHashMap<>();
    for (SkuVO sku : skuList) {
      if (sku.getAttributes() instanceof Map<?, ?> attributesMap) {
        for (Map.Entry<?, ?> entry : attributesMap.entrySet()) {
          String key = String.valueOf(entry.getKey());
          String value = String.valueOf(entry.getValue());
          options.putIfAbsent(key, new ArrayList<>());
          if (!options.get(key).contains(value)) {
            options.get(key).add(value);
          }
        }
      }
    }
    return options;
  }

  private static Map<String, List<String>> buildSkuAttributeDisplayOptions(List<SkuVO> skuList) {
    Map<String, List<String>> options = new LinkedHashMap<>();
    for (SkuVO sku : skuList) {
      if (sku.getAttributeDisplay() instanceof Map<?, ?> displayMap) {
        for (Map.Entry<?, ?> entry : displayMap.entrySet()) {
          String key = String.valueOf(entry.getKey());
          String value = String.valueOf(entry.getValue());
          options.putIfAbsent(key, new ArrayList<>());
          if (!options.get(key).contains(value)) {
            options.get(key).add(value);
          }
        }
      }
    }
    return options;
  }
}
