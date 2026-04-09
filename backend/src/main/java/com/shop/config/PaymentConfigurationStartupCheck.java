package com.shop.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentConfigurationStartupCheck implements ApplicationRunner {

  private final PaymentProperties paymentProperties;

  @Override
  public void run(ApplicationArguments args) {
    PaymentProperties.AlipayProperties alipay = paymentProperties.getAlipay();
    log.info("PAYMENT CONFIG defaultProvider={} alipayEnabled={} sandbox={} fallbackToMock={} crossBorderMode={} gateway={} appId={} partner={} returnUrl={} notifyUrl={}",
        paymentProperties.getDefaultProvider(),
        alipay.isEnabled(),
        alipay.isSandbox(),
        alipay.isFallbackToMock(),
        alipay.isCrossBorderMode(),
        alipay.resolvedGateway(),
        maskMiddle(alipay.getAppId()),
        maskMiddle(alipay.getPartner()),
        alipay.getReturnUrl(),
        alipay.getNotifyUrl());

    boolean alipayExpected = alipay.isEnabled() || "alipay".equalsIgnoreCase(paymentProperties.getDefaultProvider());
    if (!alipayExpected) {
      return;
    }

    List<String> issues = new ArrayList<>();
    if (!alipay.isEnabled()) {
      issues.add("payment.alipay.enabled=false");
    }
    issues.addAll(findMissingFields(alipay));

    if (!issues.isEmpty()) {
      String message = "Alipay is expected in this environment but configuration is incomplete: " + String.join(", ", issues);
      log.error(message);
      throw new IllegalStateException(message);
    }
  }

  private List<String> findMissingFields(PaymentProperties.AlipayProperties alipay) {
    List<String> missing = new ArrayList<>();
    if (isBlank(alipay.getAppPrivateKey())) {
      missing.add("payment.alipay.app-private-key");
    }
    if (isBlank(alipay.getAlipayPublicKey())) {
      missing.add("payment.alipay.alipay-public-key");
    }
    if (isBlank(alipay.getReturnUrl())) {
      missing.add("payment.alipay.return-url");
    }
    if (alipay.isCrossBorderMode()) {
      if (isBlank(alipay.getPartner())) {
        missing.add("payment.alipay.partner");
      }
    } else if (isBlank(alipay.getAppId())) {
      missing.add("payment.alipay.app-id");
    }
    return missing;
  }

  private boolean isBlank(String value) {
    return value == null || value.isBlank();
  }

  private String maskMiddle(String value) {
    if (isBlank(value)) {
      return "<empty>";
    }
    if (value.length() <= 6) {
      return "***";
    }
    return value.substring(0, 3) + "***" + value.substring(value.length() - 3);
  }
}
