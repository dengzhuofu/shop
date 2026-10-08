package com.shop.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

/** Request-scoped presentation currency. Database catalog prices remain in USD. */
public final class PricingContext {
  private record Pricing(String currency, BigDecimal rate) {}
  private static final ThreadLocal<Pricing> CURRENT = new ThreadLocal<>();
  private PricingContext() {}

  public static void set(String currency, BigDecimal usdCnyRate) {
    String normalized = currency == null || currency.isBlank() ? "USD" : currency.trim().toUpperCase();
    if (!List.of("USD", "CNY").contains(normalized)) throw new IllegalArgumentException("Unsupported currency");
    if (usdCnyRate == null || usdCnyRate.signum() <= 0) throw new IllegalArgumentException("Invalid pricing exchange rate");
    CURRENT.set(new Pricing(normalized, "CNY".equals(normalized) ? usdCnyRate : BigDecimal.ONE));
  }
  public static String currency() { return CURRENT.get() == null ? "USD" : CURRENT.get().currency(); }
  public static BigDecimal rate() { return CURRENT.get() == null ? BigDecimal.ONE : CURRENT.get().rate(); }
  public static void clear() { CURRENT.remove(); }
  public static BigDecimal money(BigDecimal usd) {
    return usd == null ? null : usd.multiply(rate()).setScale(2, RoundingMode.HALF_UP);
  }
  public static BigDecimal baseAmount(BigDecimal amount) {
    return amount == null ? null : amount.divide(rate(), 8, RoundingMode.HALF_UP);
  }
  public static String country(String value) {
    if (value == null) throw new IllegalArgumentException("Shipping country is required");
    return switch (value.trim().toUpperCase()) {
      case "US", "USA", "UNITED STATES", "美国", "美國" -> "US";
      case "CA", "CANADA", "加拿大" -> "CA";
      case "CN", "CHINA", "CHINA MAINLAND", "中国", "中國", "中国大陆", "中國大陸" -> "CN";
      default -> throw new IllegalArgumentException("Shipping is available only to the US, Canada and mainland China");
    };
  }
  public static String currencyForCountry(String country) { return "CN".equals(country(country)) ? "CNY" : "USD"; }

  public static Object prices(Object value) {
    if (value instanceof List<?> list) return list.stream().map(PricingContext::prices).toList();
    if (value instanceof Map<?, ?> map) {
      Map<String, Object> result = new LinkedHashMap<>();
      map.forEach((key, item) -> {
        String name = String.valueOf(key);
        if (List.of("price", "compareAtPrice", "thresholdAmount", "discountAmount").contains(name)
            && item != null) result.put(name, money(new BigDecimal(String.valueOf(item))));
        else result.put(name, prices(item));
      });
      return result;
    }
    return value;
  }
}
