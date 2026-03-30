package com.shop.common;

public class LanguageContext {
  private static final ThreadLocal<String> CONTEXT = new ThreadLocal<>();

  public static void setLanguage(String lang) {
    CONTEXT.set(lang);
  }

  public static String getLanguage() {
    String lang = CONTEXT.get();
    return (lang != null && !lang.isEmpty()) ? lang : "zh"; // Default to zh
  }

  public static void clear() {
    CONTEXT.remove();
  }
}
