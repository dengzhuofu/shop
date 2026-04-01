package com.shop.vo;

import com.fasterxml.jackson.databind.JsonNode;
import com.shop.common.LanguageContext;
import com.shop.entity.PmsSku;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

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
  private Object attributes;
  private Object attributeDisplay;

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

    JsonNode specsNode = sku.getSpecs();
    vo.setSpecs(specsNode);
    vo.setAttributes(resolveAttributes(specsNode, lang));
    vo.setAttributeDisplay(resolveAttributes(specsNode, lang));

    return vo;
  }

  public static Map<String, Object> resolveAttributes(JsonNode specsNode, String lang) {
    Map<String, Object> attributes = new LinkedHashMap<>();
    if (specsNode == null || !specsNode.isObject()) {
      return attributes;
    }
    JsonNode sourceNode = resolveLocalizedNode(specsNode, lang);
    Iterator<Map.Entry<String, JsonNode>> iterator = sourceNode.fields();
    while (iterator.hasNext()) {
      Map.Entry<String, JsonNode> entry = iterator.next();
      String normalizedKey = normalizeKey(entry.getKey());
      attributes.put(normalizedKey, entry.getValue().isValueNode() ? entry.getValue().asText() : entry.getValue());
    }
    return attributes;
  }

  public static Map<String, Object> buildSnapshot(JsonNode specsNode, String lang) {
    Map<String, Object> snapshot = new LinkedHashMap<>();
    snapshot.put("attributes", resolveAttributes(specsNode, lang));
    snapshot.put("attributeDisplay", resolveAttributes(specsNode, lang));
    snapshot.put("lang", lang);
    return snapshot;
  }

  private static JsonNode resolveLocalizedNode(JsonNode specsNode, String lang) {
    if (specsNode.has(lang) && specsNode.get(lang).isObject()) {
      return specsNode.get(lang);
    }
    if (specsNode.has("en") && specsNode.get("en").isObject()) {
      return specsNode.get("en");
    }
    if (specsNode.has("zh") && specsNode.get("zh").isObject()) {
      return specsNode.get("zh");
    }
    return specsNode;
  }

  private static String normalizeKey(String rawKey) {
    if (rawKey == null) {
      return "";
    }
    String key = rawKey.trim().toLowerCase();
    if ("style".equals(key) || "款式".equals(rawKey)) {
      return "style";
    }
    if ("bundle".equals(key) || "套餐".equals(rawKey) || "组合".equals(rawKey)) {
      return "bundle";
    }
    if ("color".equals(key) || "颜色".equals(rawKey)) {
      return "color";
    }
    return key;
  }
}
