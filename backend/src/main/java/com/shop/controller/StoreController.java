package com.shop.controller;

import com.shop.common.Result;
import com.shop.config.PricingProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
public class StoreController {
  private final PricingProperties pricing;
  @GetMapping("/config")
  public Result<Map<String, Object>> config() {
    return Result.success(Map.of("brand", "CBJJ", "baseCurrency", "USD", "usdCnyRate", pricing.getUsdCnyRate(),
        "currencies", List.of("USD", "CNY"), "countries", List.of("US", "CA", "CN")));
  }
}
