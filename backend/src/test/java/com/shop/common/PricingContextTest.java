package com.shop.common;

import com.shop.config.PaymentProperties;
import java.math.BigDecimal;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class PricingContextTest {
  @AfterEach void clear() { PricingContext.clear(); }

  @Test void convertsUnitsBeforeMultiplyingQuantity() {
    PricingContext.set("CNY", new BigDecimal("7.00"));
    assertThat(PricingContext.money(new BigDecimal("269.99"))).isEqualByComparingTo("1889.93");
    assertThat(PricingContext.money(new BigDecimal("0.01")).multiply(new BigDecimal("3"))).isEqualByComparingTo("0.21");
    assertThat(PricingContext.baseAmount(new BigDecimal("1889.93"))).isEqualByComparingTo("269.99");
    assertThat(PricingContext.prices(Map.of("price", "29.99", "code", "lock")))
        .isEqualTo(Map.of("price", new BigDecimal("209.93"), "code", "lock"));
  }

  @Test void rejectsUnknownCurrenciesCountriesAndInvalidRates() {
    assertThatThrownBy(() -> PricingContext.set("CAD", new BigDecimal("7"))).isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> PricingContext.set("CNY", BigDecimal.ZERO)).isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> PricingContext.country("GB")).isInstanceOf(IllegalArgumentException.class);
    assertThat(PricingContext.currencyForCountry("China")).isEqualTo("CNY");
    assertThat(PricingContext.currencyForCountry("Canada")).isEqualTo("USD");
    PricingContext.set("USD", new BigDecimal("7"));
    assertThat(PricingContext.money(new BigDecimal("269.99"))).isEqualByComparingTo("269.99");
  }

  @Test void productionChannelsRequireImplementedCurrencyAndCompleteConfiguration() {
    PaymentProperties properties = new PaymentProperties();
    assertThat(PaymentMethodCatalog.methods("en", properties, "USD")).allMatch(m -> !m.getEnabled());
    var alipay = properties.getAlipay();
    alipay.setEnabled(true); alipay.setSandbox(false); alipay.setAppId("test-id");
    alipay.setAppPrivateKey("test-private"); alipay.setAlipayPublicKey("test-public");
    alipay.setReturnUrl("https://cbjjpower.com/payment/alipay/return");
    alipay.setNotifyUrl("https://cbjjpower.com/api/payment/alipay/notify");
    assertThat(PaymentMethodCatalog.methods("en", properties, "CNY").get(0).getEnabled()).isTrue();
    alipay.setGateway("https://openapi-sandbox.dl.alipaydev.com/gateway.do");
    assertThat(PaymentMethodCatalog.methods("en", properties, "CNY").get(0).getEnabled()).isFalse();
    alipay.setGateway("http://openapi.alipay.com/gateway.do");
    assertThat(PaymentMethodCatalog.methods("en", properties, "CNY").get(0).getEnabled()).isFalse();
    alipay.setGateway(null);
    assertThat(PaymentMethodCatalog.methods("en", properties, "USD")).allMatch(m -> !m.getEnabled());
    alipay.setSandbox(true);
    assertThat(PaymentMethodCatalog.methods("en", properties, "CNY")).allMatch(m -> !m.getEnabled());
    alipay.setSandbox(false); alipay.setPartner("cross-border-partner");
    assertThat(PaymentMethodCatalog.methods("en", properties, "CNY")).allMatch(m -> !m.getEnabled());
    alipay.setPartner(null); alipay.setNotifyUrl(null);
    assertThat(PaymentMethodCatalog.methods("en", properties, "CNY")).allMatch(m -> !m.getEnabled());
  }
}
