package com.shop.config;

import java.math.BigDecimal;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "shop.pricing")
public class PricingProperties {
  private BigDecimal usdCnyRate = new BigDecimal("7.00");
  @jakarta.annotation.PostConstruct
  public void validate() {
    if (usdCnyRate == null || usdCnyRate.signum() <= 0) {
      throw new IllegalArgumentException("SHOP_USD_CNY_RATE must be positive");
    }
  }
}
