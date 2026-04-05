package com.shop.common;

import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class ProductAddonUtils {

  private ProductAddonUtils() {
  }

  public static List<Object> resolveSelectedAddons(JsonNode upsellsNode, List<String> addonCodes, String language) {
    List<Object> result = new ArrayList<>();
    if (upsellsNode == null || addonCodes == null || addonCodes.isEmpty()) {
      return result;
    }

    JsonNode localized = JsonLocaleUtils.localizeNode(upsellsNode, language);
    if (localized == null || !localized.isArray()) {
      return result;
    }

    for (JsonNode item : localized) {
      String code = item.path("code").asText();
      if (addonCodes.contains(code)) {
        result.add(JsonLocaleUtils.localizedObject(item, language));
      }
    }
    return result;
  }

  public static BigDecimal resolveAddonAmount(JsonNode upsellsNode, List<String> addonCodes, String language) {
    if (upsellsNode == null || addonCodes == null || addonCodes.isEmpty()) {
      return BigDecimal.ZERO;
    }

    BigDecimal total = BigDecimal.ZERO;
    JsonNode localized = JsonLocaleUtils.localizeNode(upsellsNode, language);
    if (localized == null || !localized.isArray()) {
      return total;
    }

    for (JsonNode item : localized) {
      if (addonCodes.contains(item.path("code").asText())) {
        total = total.add(item.path("price").decimalValue());
      }
    }
    return total;
  }
}
