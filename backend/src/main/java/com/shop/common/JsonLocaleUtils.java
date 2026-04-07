package com.shop.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

// public final class JsonLocaleUtils {
public final class JsonLocaleUtils {


  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private JsonLocaleUtils() {
  }

  public static String currentLanguage() {
    String language = LanguageContext.getLanguage();
    return (language == null || language.isBlank()) ? "en" : language;
  }

  public static String localizedText(JsonNode node) {
    return localizedText(node, currentLanguage());
  }

  public static String localizedText(JsonNode node, String language) {
    JsonNode localized = localizeNode(node, language);
    if (localized == null || localized.isNull()) {
      return null;
    }
    return localized.isValueNode() ? localized.asText() : localized.toString();
  }

  public static Object localizedObject(JsonNode node) {
    return localizedObject(node, currentLanguage());
  }

  public static Object localizedObject(JsonNode node, String language) {
    JsonNode localized = localizeNode(node, language);
    if (localized == null || localized.isNull()) {
      return null;
    }
    return OBJECT_MAPPER.convertValue(localized, Object.class);
  }

  public static JsonNode localizeNode(JsonNode node, String language) {
    if (node == null || node.isNull()) {
      return null;
    }

    if (isLocalizedObject(node)) {
      if (node.has(language)) {
        return node.get(language);
      }
      if (node.has("en")) {
        return node.get("en");
      }
      if (node.has("zh")) {
        return node.get("zh");
      }
    }

    if (node.isArray()) {
      ArrayNode arrayNode = OBJECT_MAPPER.createArrayNode();
      for (JsonNode item : node) {
        arrayNode.add(localizeEmbeddedFields(item, language));
      }
      return arrayNode;
    }

    if (node.isObject()) {
      return localizeEmbeddedFields(node, language);
    }

    return node;
  }

  public static Map<String, Object> localizedMap(JsonNode node, String language) {
    JsonNode localized = localizeNode(node, language);
    if (localized == null || !localized.isObject()) {
      return new LinkedHashMap<>();
    }

    Map<String, Object> result = new LinkedHashMap<>();
    Iterator<Map.Entry<String, JsonNode>> fields = localized.fields();
    while (fields.hasNext()) {
      Map.Entry<String, JsonNode> entry = fields.next();
      result.put(entry.getKey(), OBJECT_MAPPER.convertValue(entry.getValue(), Object.class));
    }
    return result;
  }

  public static boolean isLocalizedObject(JsonNode node) {
    return node != null
        && node.isObject()
        && (node.has("en") || node.has("zh"));
  }

  private static JsonNode localizeEmbeddedFields(JsonNode node, String language) {
    if (node == null || node.isNull()) {
      return null;
    }
    if (!node.isObject()) {
      return node;
    }

    ObjectNode localized = OBJECT_MAPPER.createObjectNode();
    Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
    while (fields.hasNext()) {
      Map.Entry<String, JsonNode> entry = fields.next();
      JsonNode value = entry.getValue();
      if (isLocalizedObject(value)) {
        localized.set(entry.getKey(), localizeNode(value, language));
      } else if (value.isArray()) {
        ArrayNode arrayNode = OBJECT_MAPPER.createArrayNode();
        for (JsonNode item : value) {
          arrayNode.add(localizeEmbeddedFields(item, language));
        }
        localized.set(entry.getKey(), arrayNode);
      } else if (value.isObject()) {
        localized.set(entry.getKey(), localizeEmbeddedFields(value, language));
      } else {
        localized.set(entry.getKey(), value);
      }
    }
    return localized;
  }
}
