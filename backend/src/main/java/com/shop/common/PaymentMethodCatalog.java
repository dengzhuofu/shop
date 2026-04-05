package com.shop.common;

import com.shop.vo.PaymentMethodVO;

import java.util.List;

public final class PaymentMethodCatalog {

  private PaymentMethodCatalog() {
  }

  public static List<PaymentMethodVO> methods(String language) {
    boolean zh = "zh".equalsIgnoreCase(language);
    return List.of(
        new PaymentMethodVO("credit_card", zh ? "信用卡 / 借记卡" : "Credit / Debit Card", "card", true),
        new PaymentMethodVO("paypal", "PayPal", "wallet", true),
        new PaymentMethodVO("shop_pay", "Shop Pay", "wallet", true),
        new PaymentMethodVO("apple_pay", "Apple Pay", "wallet", true),
        new PaymentMethodVO("google_pay", "Google Pay", "wallet", true),
        new PaymentMethodVO("affirm", "Affirm", "installment", true),
        new PaymentMethodVO("klarna", "Klarna", "installment", true));
  }
}
