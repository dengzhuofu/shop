package com.shop.config;

import com.shop.common.LanguageContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LanguageInterceptor implements HandlerInterceptor {
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    String lang = request.getParameter("lang");
    if (lang == null || lang.isEmpty()) {
      lang = request.getHeader("Accept-Language");
      if (lang != null && lang.startsWith("en")) {
        lang = "en";
      } else {
        lang = "zh";
      }
    }
    LanguageContext.setLanguage(lang);
    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    LanguageContext.clear();
  }
}
