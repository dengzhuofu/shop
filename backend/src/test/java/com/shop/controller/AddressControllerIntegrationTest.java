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

class AddressControllerIntegrationTest extends BackendIntegrationTestSupport {

  @Test
  void creatingTheSameAddressTwiceReturnsExistingRecord() throws Exception {
    String token = loginAndGetToken("admin@isinwheel.local", "123456");

    String payload = """
        {
          "country": "United States",
          "firstName": "Admin",
          "lastName": "User",
          "phone": "4155550123",
          "addressLine1": "100 Market Street",
          "addressLine2": "Suite 8",
          "city": "San Francisco",
          "state": "CA",
          "zipCode": "94105",
          "isDefault": true
        }
        """;

    String firstResponse = mockMvc.perform(post("/address")
            .header("Authorization", token)
            .contentType(APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andReturn()
        .getResponse()
        .getContentAsString();

    String secondResponse = mockMvc.perform(post("/address")
            .header("Authorization", token)
            .contentType(APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andReturn()
        .getResponse()
        .getContentAsString();

    JsonNode firstJson = objectMapper.readTree(firstResponse);
    JsonNode secondJson = objectMapper.readTree(secondResponse);
    assertThat(secondJson.path("data").path("id").asLong())
        .isEqualTo(firstJson.path("data").path("id").asLong());

    mockMvc.perform(get("/address/list")
            .header("Authorization", token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.length()").value(1));
  }
}
