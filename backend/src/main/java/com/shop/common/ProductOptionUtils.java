package com.shop.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.shop.entity.PmsSku;
import com.shop.vo.SkuVO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ProductOptionUtils {

  private ProductOptionUtils() {
  }

  public static boolean hasSelectableOptions(List<PmsSku> skuList) {
    if (skuList == null || skuList.isEmpty()) {
      return false;
    }

    Map<String, List<String>> optionsByKey = new LinkedHashMap<>();
    for (PmsSku sku : skuList) {
      JsonNode specs = sku == null ? null : sku.getSpecs();
      Map<String, Object> attributes = SkuVO.resolveAttributes(specs, "en");
      for (Map.Entry<String, Object> entry : attributes.entrySet()) {
        String key = entry.getKey();
        String value = entry.getValue() == null ? "" : String.valueOf(entry.getValue()).trim();
        if (value.isBlank()) {
          continue;
        }
        optionsByKey.computeIfAbsent(key, ignored -> new ArrayList<>());
        if (!optionsByKey.get(key).contains(value)) {
          optionsByKey.get(key).add(value);
        }
      }
    }

    return optionsByKey.values().stream().anyMatch(values -> values.size() > 1);
  }

  public static Map<Long, Boolean> hasSelectableOptionsByProductId(List<PmsSku> skuList) {
    if (skuList == null || skuList.isEmpty()) {
      return Collections.emptyMap();
    }

    Map<Long, List<PmsSku>> skuByProductId = new LinkedHashMap<>();
    for (PmsSku sku : skuList) {
      if (sku == null || sku.getProductId() == null) {
        continue;
      }
      skuByProductId.computeIfAbsent(sku.getProductId(), ignored -> new ArrayList<>()).add(sku);
    }

    Map<Long, Boolean> result = new LinkedHashMap<>();
    for (Map.Entry<Long, List<PmsSku>> entry : skuByProductId.entrySet()) {
      result.put(entry.getKey(), hasSelectableOptions(entry.getValue()));
    }
    return result;
  }
}
