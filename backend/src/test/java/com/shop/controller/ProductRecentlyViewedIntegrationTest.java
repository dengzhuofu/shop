package com.shop.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.shop.support.BackendIntegrationTestSupport;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductRecentlyViewedIntegrationTest extends BackendIntegrationTestSupport {

  @Test
  void loggedInUserCanRecordAndListRecentlyViewedProducts() throws Exception {
    String token = loginAndGetToken("admin@isinwheel.local", "123456");

    mockMvc.perform(post("/product/recently-viewed/2")
            .header("Authorization", token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));

    mockMvc.perform(post("/product/recently-viewed/15")
            .header("Authorization", token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200));

    String listResponse = mockMvc.perform(post("/product/recently-viewed/sync")
            .header("Authorization", token)
            .contentType(APPLICATION_JSON)
            .content("""
                {
                  "productIds": [2, 15, 2]
                }
                """)
            .param("lang", "zh"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data[0].id").value(2))
        .andExpect(jsonPath("$.data[0].slug").value("s-nova-pro-commuting-electric-scooter"))
        .andExpect(jsonPath("$.data[0].viewCount").value(2))
        .andExpect(jsonPath("$.data[1].id").value(15))
        .andReturn()
        .getResponse()
        .getContentAsString();

    JsonNode data = objectMapper.readTree(listResponse).path("data");
    assertThat(data.size()).isEqualTo(2);

    String guestList = mockMvc.perform(get("/product/recently-viewed").param("lang", "en"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andReturn()
        .getResponse()
        .getContentAsString();
    assertThat(objectMapper.readTree(guestList).path("data").size()).isEqualTo(0);
  }
}
