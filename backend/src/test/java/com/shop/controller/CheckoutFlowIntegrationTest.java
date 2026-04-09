package com.shop.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.shop.entity.OmsOrder;
import com.shop.entity.PmsSku;
import com.shop.service.OmsOrderService;
import com.shop.service.PmsSkuService;
import com.shop.support.BackendIntegrationTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CheckoutFlowIntegrationTest extends BackendIntegrationTestSupport {

  @Autowired
  private OmsOrderService orderService;

  @Autowired
  private PmsSkuService skuService;

  @Test
  void cartPreviewCreateOrderAndCompleteMockPayment() throws Exception {
    String token = loginAndGetToken("admin@isinwheel.local", "123456");

    mockMvc.perform(post("/cart/add")
            .header("Authorization", token)
            .contentType(APPLICATION_JSON)
            .content("""
                {
                  "productId": 2,
                  "skuId": 3,
                  "quantity": 1,
                  "addonCodes": ["warranty-2y"]
                }
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));

    String cartResponse = mockMvc.perform(get("/cart/list")
            .param("lang", "en")
            .header("Authorization", token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data[0].title").value("S Nova Pro Commuting Electric Scooter"))
        .andExpect(jsonPath("$.data[0].addons[0].code").value("warranty-2y"))
        .andReturn()
        .getResponse()
        .getContentAsString();

    JsonNode cartJson = objectMapper.readTree(cartResponse);
    long cartItemId = cartJson.path("data").get(0).path("cartItemId").asLong();

    String previewResponse = mockMvc.perform(post("/order/preview")
            .header("Authorization", token)
            .contentType(APPLICATION_JSON)
            .content("""
                {
                  "source": "cart",
                  "cartItemIds": [%d],
                  "addressId": 1,
                  "shippingMethod": "UPS Ground/FedEx Home Delivery(2-5 Business Days)",
                  "couponUserId": 2
                }
                """.formatted(cartItemId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.coupon.code").value("SAVE50"))
        .andExpect(jsonPath("$.data.paymentMethods[0].code").value("credit_card"))
        .andReturn()
        .getResponse()
        .getContentAsString();

    JsonNode previewJson = objectMapper.readTree(previewResponse);
    String previewToken = previewJson.path("data").path("previewToken").asText();
    assertThat(previewToken).isNotBlank();
    assertThat(previewJson.path("data").path("totalAmount").decimalValue()).isEqualByComparingTo("584.53");

    String createResponse = mockMvc.perform(post("/order/create")
            .header("Authorization", token)
            .contentType(APPLICATION_JSON)
            .content("""
                {
                  "source": "cart",
                  "cartItemIds": [%d],
                  "addressId": 1,
                  "shippingMethod": "UPS Ground/FedEx Home Delivery(2-5 Business Days)",
                  "couponUserId": 2,
                  "previewToken": "%s",
                  "remark": "Integration checkout"
                }
                """.formatted(cartItemId, previewToken)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.status").value("PENDING_PAYMENT"))
        .andReturn()
        .getResponse()
        .getContentAsString();

    JsonNode createJson = objectMapper.readTree(createResponse);
    long orderId = createJson.path("data").path("id").asLong();

    String paymentResponse = mockMvc.perform(post("/payment/intent")
            .header("Authorization", token)
            .contentType(APPLICATION_JSON)
            .content("""
                {
                  "orderId": %d,
                  "paymentMethod": "credit_card"
                }
                """.formatted(orderId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.status").value("CREATED"))
        .andReturn()
        .getResponse()
        .getContentAsString();

    long paymentIntentId = objectMapper.readTree(paymentResponse).path("data").path("id").asLong();

    mockMvc.perform(post("/payment/mock/complete")
            .header("Authorization", token)
            .contentType(APPLICATION_JSON)
            .content("""
                {
                  "paymentIntentId": %d,
                  "mockResult": "success"
                }
                """.formatted(paymentIntentId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.status").value("SUCCEEDED"));

    mockMvc.perform(get("/order/" + orderId)
            .param("lang", "en")
            .header("Authorization", token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.status").value("PAID"))
        .andExpect(jsonPath("$.data.paymentStatus").value("PAID"))
        .andExpect(jsonPath("$.data.couponCode").value("SAVE50"))
        .andExpect(jsonPath("$.data.items[0].slug").value("s-nova-pro-commuting-electric-scooter"))
        .andExpect(jsonPath("$.data.items[0].addons[0].code").value("warranty-2y"));
  }

  @Test
  void unpaidOrderExpiresAfterTimeoutAndReleasesInventory() throws Exception {
    String token = loginAndGetToken("admin@isinwheel.local", "123456");
    int initialStock = skuService.getById(3L).getStock();

    mockMvc.perform(post("/cart/add")
            .header("Authorization", token)
            .contentType(APPLICATION_JSON)
            .content("""
                {
                  "productId": 2,
                  "skuId": 3,
                  "quantity": 1
                }
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));

    String cartResponse = mockMvc.perform(get("/cart/list")
            .param("lang", "en")
            .header("Authorization", token))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    long cartItemId = objectMapper.readTree(cartResponse).path("data").get(0).path("cartItemId").asLong();

    String previewResponse = mockMvc.perform(post("/order/preview")
            .header("Authorization", token)
            .contentType(APPLICATION_JSON)
            .content("""
                {
                  "source": "cart",
                  "cartItemIds": [%d],
                  "addressId": 1,
                  "shippingMethod": "UPS Ground/FedEx Home Delivery(2-5 Business Days)"
                }
                """.formatted(cartItemId)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    String previewToken = objectMapper.readTree(previewResponse).path("data").path("previewToken").asText();

    String createResponse = mockMvc.perform(post("/order/create")
            .header("Authorization", token)
            .contentType(APPLICATION_JSON)
            .content("""
                {
                  "source": "cart",
                  "cartItemIds": [%d],
                  "addressId": 1,
                  "shippingMethod": "UPS Ground/FedEx Home Delivery(2-5 Business Days)",
                  "previewToken": "%s",
                  "remark": "Expiration verification"
                }
                """.formatted(cartItemId, previewToken)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.status").value("PENDING_PAYMENT"))
        .andExpect(jsonPath("$.data.paymentExpireTime").isNotEmpty())
        .andReturn()
        .getResponse()
        .getContentAsString();

    long orderId = objectMapper.readTree(createResponse).path("data").path("id").asLong();
    PmsSku lockedSku = skuService.getById(3L);
    assertThat(lockedSku.getStock()).isEqualTo(initialStock - 1);

    OmsOrder order = orderService.getById(orderId);
    order.setPaymentExpireTime(LocalDateTime.now().minusMinutes(1));
    orderService.updateById(order);

    assertThat(orderService.closeExpiredOrders(10)).isEqualTo(1);

    mockMvc.perform(get("/order/" + orderId)
            .param("lang", "en")
            .header("Authorization", token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.status").value("EXPIRED"))
        .andExpect(jsonPath("$.data.paymentStatus").value("EXPIRED"));

    mockMvc.perform(post("/payment/intent")
            .header("Authorization", token)
            .contentType(APPLICATION_JSON)
            .content("""
                {
                  "orderId": %d,
                  "paymentMethod": "credit_card"
                }
                """.formatted(orderId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(400))
        .andExpect(jsonPath("$.message").value("Order payment window expired"));

    PmsSku releasedSku = skuService.getById(3L);
    assertThat(releasedSku.getStock()).isEqualTo(initialStock);
  }
}
