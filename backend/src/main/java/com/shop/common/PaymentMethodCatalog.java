package com.shop.common;

import com.shop.vo.PaymentMethodVO;
import com.shop.config.PaymentProperties;

import java.util.List;

public final class PaymentMethodCatalog {

  private PaymentMethodCatalog() {
  }

  public static List<PaymentMethodVO> methods(String language, PaymentProperties properties, String currency) {
    boolean test = properties.isMockEnabled();
    boolean alipay = properties.getAlipay().supportsCurrency(currency)
        && (properties.getAlipay().isProductionGateway() || test);
    return List.of(
        new PaymentMethodVO("alipay", test && properties.getAlipay().isSandbox() ? "Alipay (test)" : "Alipay", "wallet", alipay),
        new PaymentMethodVO("credit_card", test ? "Card (test)" : "Visa / Mastercard", "card", test),
        new PaymentMethodVO("klarna", "Klarna", "installment", false));
  }
}
