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

class AuthCatalogIntegrationTest extends BackendIntegrationTestSupport {

  @Test
  void registerLoginAndFetchLocalizedCatalog() throws Exception {
    String registerPayload = """
        {
          "email": "newrider@isinwheel.local",
          "password": "abc12345",
          "firstName": "New",
          "lastName": "Rider"
        }
        """;

    String registerResponse = mockMvc.perform(post("/auth/register/email")
            .contentType(APPLICATION_JSON)
            .content(registerPayload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.user.email").value("newrider@isinwheel.local"))
        .andReturn()
        .getResponse()
        .getContentAsString();

    String token = objectMapper.readTree(registerResponse).path("data").path("tokenValue").asText();
    assertThat(token).isNotBlank();

    mockMvc.perform(get("/auth/me")
            .header("Authorization", token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.firstName").value("New"))
        .andExpect(jsonPath("$.data.lastName").value("Rider"));

    mockMvc.perform(get("/category/tree").param("lang", "zh"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data[0].slug").value("electric-scooters"))
        .andExpect(jsonPath("$.data[0].name").value("电动滑板车"));

    String loginToken = loginAndGetToken("admin@isinwheel.local", "123456");
    assertThat(loginToken).isNotBlank();

    String productResponse = mockMvc.perform(get("/product/slug/s-nova-pro-commuting-electric-scooter")
            .param("lang", "zh")
            .header("Authorization", loginToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.slug").value("s-nova-pro-commuting-electric-scooter"))
        .andExpect(jsonPath("$.data.title").value("S Nova Pro 通勤电动滑板车"))
        .andExpect(jsonPath("$.data.skuList[0].attributes.color").value("石墨黑"))
        .andExpect(jsonPath("$.data.skuAttributeOptions.color[0]").value("石墨黑"))
        .andReturn()
        .getResponse()
        .getContentAsString();

    JsonNode productJson = objectMapper.readTree(productResponse);
    assertThat(productJson.path("data").path("skuList").size()).isEqualTo(2);
  }
}
