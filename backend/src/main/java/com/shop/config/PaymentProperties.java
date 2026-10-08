package com.shop.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.net.URI;

@Data
@Component
@ConfigurationProperties(prefix = "payment")
public class PaymentProperties {

  private String defaultProvider = "none";
  private boolean mockEnabled = false;
  private int orderTimeoutMinutes = 15;
  private int expirationCheckBatchSize = 200;
  private AlipayProperties alipay = new AlipayProperties();

  @Data
  public static class AlipayProperties {
    private boolean enabled = false;
    private boolean sandbox = true;
    private boolean fallbackToMock = false;
    private String gateway;
    private String partner;
    private String appId;
    private String appPrivateKey;
    private String alipayPublicKey;
    private String notifyUrl;
    private String returnUrl;
    private String charset = "UTF-8";
    private String signType = "RSA2";
    private String subjectPrefix = "CBJJ";

    public boolean supportsCurrency(String currency) {
      // The implemented OpenAPI page-pay SDK supports CNY. MAPI is not implemented.
      return isConfigured() && !isCrossBorderMode() && "CNY".equalsIgnoreCase(currency);
    }
    private String crossBorderProductCode = "NEW_OVERSEAS_SELLER";

    public boolean isConfigured() {
      return enabled
          && hasText(appPrivateKey)
          && hasText(alipayPublicKey)
          && hasText(returnUrl)
          && hasText(notifyUrl)
          && (isCrossBorderMode() ? hasText(partner) : hasText(appId));
    }

    public boolean isCrossBorderMode() {
      return hasText(partner);
    }

    public boolean isProductionGateway() {
      if (sandbox || isCrossBorderMode()) return false;
      try {
        URI endpoint = URI.create(resolvedGateway());
        return "https".equalsIgnoreCase(endpoint.getScheme())
            && "openapi.alipay.com".equalsIgnoreCase(endpoint.getHost())
            && (endpoint.getPort() == -1 || endpoint.getPort() == 443)
            && "/gateway.do".equals(endpoint.getPath())
            && endpoint.getUserInfo() == null && endpoint.getQuery() == null && endpoint.getFragment() == null;
      } catch (IllegalArgumentException invalid) {
        return false;
      }
    }

    public String resolvedGateway() {
      if (hasText(gateway)) {
        return gateway;
      }
      if (isCrossBorderMode()) {
        return sandbox
            ? "https://mapi.alipaydev.com/gateway.do"
            : "https://mapi.alipay.com/gateway.do";
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
