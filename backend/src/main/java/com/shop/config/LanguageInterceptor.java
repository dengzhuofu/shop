package com.shop.config;

import com.shop.common.LanguageContext;
import com.shop.common.PricingContext;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class LanguageInterceptor implements HandlerInterceptor {
  private final PricingProperties pricing;
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    LanguageContext.clear();
    PricingContext.clear();
    PricingContext.set(request.getParameter("currency"), pricing.getUsdCnyRate());
    String lang = request.getParameter("lang");
    if (lang == null || lang.isEmpty()) {
      lang = request.getHeader("Accept-Language");
      if (lang != null && lang.startsWith("en")) {
        lang = "en";
      } else if (lang != null && lang.startsWith("zh")) {
        lang = "zh";
      } else {
        lang = "en";
      }
    }
    LanguageContext.setLanguage(lang.startsWith("zh") ? "zh" : "en");
    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    LanguageContext.clear();
    PricingContext.clear();
  }
}
