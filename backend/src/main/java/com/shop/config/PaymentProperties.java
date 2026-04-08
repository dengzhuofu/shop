package com.shop.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "payment")
public class PaymentProperties {

  private String defaultProvider = "mock";
  private AlipayProperties alipay = new AlipayProperties();

  @Data
  public static class AlipayProperties {
    private boolean enabled = false;
    private boolean sandbox = true;
    private boolean fallbackToMock = true;
    private String gateway;
    private String appId;
    private String appPrivateKey;
    private String alipayPublicKey;
    private String notifyUrl;
    private String returnUrl;
    private String charset = "UTF-8";
    private String signType = "RSA2";
    private String subjectPrefix = "Shop";

    public boolean isConfigured() {
      return enabled
          && hasText(appId)
          && hasText(appPrivateKey)
          && hasText(alipayPublicKey)
          && hasText(returnUrl);
    }

    public String resolvedGateway() {
      if (hasText(gateway)) {
        return gateway;
      }
      return sandbox
          ? "https://openapi-sandbox.dl.alipaydev.com/gateway.do"
          : "https://openapi.alipay.com/gateway.do";
    }

    private boolean hasText(String value) {
      return value != null && !value.isBlank();
    }
  }
}
