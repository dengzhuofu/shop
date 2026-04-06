package com.shop.vo;

import com.fasterxml.jackson.databind.JsonNode;
import com.shop.common.JsonLocaleUtils;
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
  private Long categoryId;
  private String categorySlug;
  private String slug;
  private String title;
  private String subtitle;
  private String description;
  private BigDecimal price;
  private BigDecimal compareAtPrice;
  private Integer stock;
  private String pic;
  private Boolean isNew;
  private Object tags;
  private Object images;
  private String appImage;
  private Object specs;
  private Object quickKnow;
  private Object specTable;
  private Object boxItems;
  private Object faqs;
  private Object upsells;
  private Object skuAttributeOptions;
  private Object skuAttributeDisplayOptions;
  private List<SkuVO> skuList;
  private Boolean hasOptions;

  public static ProductVO from(PmsProduct product, List<com.shop.entity.PmsSku> skus) {
    ProductVO vo = new ProductVO();
    vo.setId(product.getId());
    vo.setCategoryId(product.getCategoryId());
    vo.setSlug(product.getSlug());
    vo.setPrice(product.getPrice());
    vo.setCompareAtPrice(product.getCompareAtPrice());
    vo.setStock(product.getStock());
    vo.setPic(product.getPic());
    vo.setIsNew(Boolean.TRUE.equals(product.getIsNew()));
    vo.setAppImage(product.getAppImage());

    String lang = JsonLocaleUtils.currentLanguage();
    vo.setTitle(extractLang(product.getName(), lang));
    vo.setSubtitle(extractLang(product.getSubtitle(), lang));
    vo.setDescription(extractLang(product.getDescription(), lang));
    vo.setTags(extractLangObject(product.getTags(), lang));
    vo.setImages(extractLangObject(product.getImages(), lang));
    vo.setSpecs(extractLangObject(product.getSpecs(), lang));
    vo.setQuickKnow(extractLangObject(product.getQuickKnow(), lang));
    vo.setSpecTable(extractLangObject(product.getSpecTable(), lang));
    vo.setBoxItems(extractLangObject(product.getBoxItems(), lang));
    vo.setFaqs(extractLangObject(product.getFaqs(), lang));
    vo.setUpsells(extractLangObject(product.getUpsells(), lang));

    if (skus != null) {
      vo.setSkuList(skus.stream().map(SkuVO::from).collect(Collectors.toList()));
      vo.setSkuAttributeOptions(buildSkuAttributeOptions(vo.getSkuList()));
      vo.setSkuAttributeDisplayOptions(buildSkuAttributeDisplayOptions(vo.getSkuList()));
      vo.setHasOptions(hasSelectableOptions(vo.getSkuList()));
    } else {
      vo.setSkuList(new ArrayList<>());
      vo.setHasOptions(false);
    }
    return vo;
  }

  public static String extractLang(JsonNode node, String lang) {
    return JsonLocaleUtils.localizedText(node, lang);
  }

  public static Object extractLangObject(JsonNode node, String lang) {
    return JsonLocaleUtils.localizedObject(node, lang);
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

  public static boolean hasSelectableOptions(List<SkuVO> skuList) {
    if (skuList == null || skuList.isEmpty()) {
      return false;
    }
    Map<String, List<String>> options = buildSkuAttributeOptions(skuList);
    return options.values().stream().anyMatch(values -> values.size() > 1);
  }
}
