package com.shop.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.shop.config.PricingProperties;
import com.shop.support.BackendIntegrationTestSupport;
import java.math.BigDecimal;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CurrencyCheckoutIntegrationTest extends BackendIntegrationTestSupport {
  @Autowired PricingProperties pricing;

  private JsonNode postJson(String path, String token, Object payload) throws Exception {
    return objectMapper.readTree(mockMvc.perform(post(path).header("Authorization", token)
        .contentType(APPLICATION_JSON).content(objectMapper.writeValueAsString(payload)))
        .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
  }
  private Map<String, Object> address(String country, String city) {
    return Map.of("country", country, "firstName", "Alex", "lastName", "Chen", "phone", "+8613310853090",
        "addressLine1", "10 Test Street", "city", city, "state", "Test Province", "zipCode", "100000");
  }
  private String login() throws Exception { return loginAndGetToken("admin@isinwheel.local", "123456"); }
  private long add(String token, int quantity, java.util.List<String> addons) throws Exception {
    JsonNode result = postJson("/cart/add", token, Map.of("productId",1,"skuId",1,"quantity",quantity,"addonCodes",addons));
    assertThat(result.path("code").asInt()).isEqualTo(200);
    JsonNode cart = objectMapper.readTree(mockMvc.perform(get("/cart/list").header("Authorization",token))
        .andReturn().getResponse().getContentAsString());
    return cart.path("data").get(0).path("cartItemId").asLong();
  }

  @Test void cnyManualAddressConvertsSkuAddonsAndCouponAndSnapshotsOrder() throws Exception {
    String token=login(); long cartId=add(token,3,java.util.List.of("warranty-1y","cable-lock"));
    var payload=new java.util.HashMap<String,Object>(Map.of("source","cart","cartItemIds",java.util.List.of(cartId),
        "addressSnapshot",address("CN","Beijing"),"currency","CNY","couponUserId",2));
    JsonNode preview=postJson("/order/preview",token,payload).path("data");
    assertThat(preview.path("currency").asText()).isEqualTo("CNY");
    assertThat(preview.path("pricingExchangeRate").decimalValue()).isEqualByComparingTo("7");
    assertThat(preview.path("items").get(0).path("unitPrice").decimalValue()).isEqualByComparingTo("1889.93");
    assertThat(preview.path("subtotal").decimalValue()).isEqualByComparingTo("7979.37");
    assertThat(preview.path("discountAmount").decimalValue()).isEqualByComparingTo("350");
    assertThat(preview.path("taxAmount").decimalValue()).isEqualByComparingTo("0");
    assertThat(preview.path("totalAmount").decimalValue()).isEqualByComparingTo("7629.37");
    payload.put("previewToken",preview.path("previewToken").asText());
    JsonNode response=postJson("/order/create",token,payload);
    assertThat(response.path("code").asInt()).isEqualTo(200);
    JsonNode order=response.path("data");
    assertThat(order.path("currency").asText()).isEqualTo("CNY");
    assertThat(order.path("pricingExchangeRate").decimalValue()).isEqualByComparingTo("7");
    assertThat(order.path("totalAmount").decimalValue()).isEqualByComparingTo("7629.37");
  }

  @Test void rejectsCurrencyCountryMismatchAndInvalidatesTokenOnAddressAndRateChanges() throws Exception {
    String token=login(); long cartId=add(token,1,java.util.List.of());
    var payload=new java.util.HashMap<String,Object>(Map.of("source","cart","cartItemIds",java.util.List.of(cartId),
        "addressSnapshot",address("CN","Beijing"),"currency","USD"));
    assertThat(postJson("/order/preview",token,payload).path("code").asInt()).isEqualTo(400);
    payload.put("currency","CNY");
    String previewToken=postJson("/order/preview",token,payload).path("data").path("previewToken").asText();
    payload.put("previewToken",previewToken);payload.put("addressSnapshot",address("CN","Shanghai"));
    assertThat(postJson("/order/create",token,payload).path("code").asInt()).isEqualTo(400);
    payload.put("addressSnapshot",address("CN","Beijing"));
    BigDecimal saved=pricing.getUsdCnyRate();
    try {
      pricing.setUsdCnyRate(new BigDecimal("7.01"));
      assertThat(postJson("/order/create",token,payload).path("code").asInt()).isEqualTo(400);
    } finally { pricing.setUsdCnyRate(saved); }
  }

  @Test void unsupportedProductionPaymentsCannotCreateIntentOrSimulateSuccess() throws Exception {
    String token=login(); long cartId=add(token,1,java.util.List.of());
    var payload=new java.util.HashMap<String,Object>(Map.of("cartItemIds",java.util.List.of(cartId),"addressId",1,"currency","USD"));
    JsonNode preview=postJson("/order/preview",token,payload).path("data");
    assertThat(preview.path("totalAmount").decimalValue()).isEqualByComparingTo("269.99");
    payload.put("previewToken",preview.path("previewToken").asText());
    long orderId=postJson("/order/create",token,payload).path("data").path("id").asLong();
    for(String method:java.util.List.of("alipay","credit_card","klarna")) {
      assertThat(postJson("/payment/intent",token,Map.of("orderId",orderId,"paymentMethod",method)).path("code").asInt()).isEqualTo(400);
    }
    assertThat(postJson("/payment/mock/complete",token,Map.of("paymentIntentId",1,"mockResult","success")).path("code").asInt()).isEqualTo(403);
    mockMvc.perform(get("/order/"+orderId).header("Authorization",token).param("currency","CNY"))
        .andExpect(jsonPath("$.data.currency").value("USD"))
        .andExpect(jsonPath("$.data.totalAmount").value(269.99))
        .andExpect(jsonPath("$.data.paymentStatus").value("PENDING"));
  }

  @Test void productCategoryAndCouponsReportCnyWithoutChangingCanonicalPrices() throws Exception {
    mockMvc.perform(get("/product/1").param("currency","CNY").param("lang","en"))
        .andExpect(jsonPath("$.data.price").value(1889.93))
        .andExpect(jsonPath("$.data.currency").value("CNY"))
        .andExpect(jsonPath("$.data.skuList[0].currency").value("CNY"));
    mockMvc.perform(get("/product/1").param("currency","USD"))
        .andExpect(jsonPath("$.data.price").value(269.99));
    mockMvc.perform(get("/product/slug/isinwheel-s9-pro-pneumatic-tire-electric-scooter"))
        .andExpect(jsonPath("$.data.slug").value("cbjj-s9-pro-pneumatic-tire-electric-scooter"));
    mockMvc.perform(get("/category/electric-scooters/products").param("currency","CNY").param("maxPrice","1900"))
        .andExpect(jsonPath("$.data.records[0].currency").value("CNY"));
  }
}
