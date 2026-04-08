package com.shop.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.shop.support.BackendIntegrationTestSupport;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

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

    mockMvc.perform(get("/marketing/activities/current").param("lang", "zh"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.code").value("spring-ride-festival"))
        .andExpect(jsonPath("$.data.title").value("复活节促销"))
        .andExpect(jsonPath("$.data.enabled").value(true));

    mockMvc.perform(get("/category/menu").param("lang", "zh"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data[1].slug").value("electric-bike"))
        .andExpect(jsonPath("$.data[1].children[0].name").value("城市通勤"))
        .andExpect(jsonPath("$.data[1].children[0].products[0].categorySlug").value("electric-bike"));

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
    assertThat(productJson.path("data").path("skuList").size()).isEqualTo(6);
  }

  @Test
  void skateboardSeedDataProvidesMultipleSelectableVariantScenarios() throws Exception {
    String token = loginAndGetToken("admin@isinwheel.local", "123456");

    String productResponse = mockMvc.perform(get("/product/slug/isinwheel-v8-electric-skateboard-with-remote")
            .param("lang", "en")
            .header("Authorization", token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.slug").value("isinwheel-v8-electric-skateboard-with-remote"))
        .andExpect(jsonPath("$.data.skuAttributeOptions.color[1]").value("Forest Green"))
        .andExpect(jsonPath("$.data.skuAttributeOptions.bundle[2]").value("Travel Kit"))
        .andReturn()
        .getResponse()
        .getContentAsString();

    JsonNode skuList = objectMapper.readTree(productResponse).path("data").path("skuList");
    assertThat(skuList.size()).isEqualTo(5);

    Set<String> uniquePics = new HashSet<>();
    Set<String> uniquePrices = new HashSet<>();
    int selectableCount = 0;
    for (JsonNode sku : skuList) {
      uniquePics.add(sku.path("pic").asText());
      uniquePrices.add(sku.path("price").asText());
      if (sku.path("stock").asInt() > 0 && !"INACTIVE".equals(sku.path("status").asText())) {
        selectableCount++;
      }
    }

    assertThat(uniquePics.size()).isGreaterThanOrEqualTo(4);
    assertThat(uniquePrices.size()).isGreaterThanOrEqualTo(5);
    assertThat(selectableCount).isEqualTo(4);
  }

  @Test
  void seedDataExposesAuthenticSkuAttributeKeysAcrossProducts() throws Exception {
    String token = loginAndGetToken("admin@isinwheel.local", "123456");

    String kidsResponse = mockMvc.perform(get("/product/slug/isinwheel-long-range-3-wheel-kids-electric-scooter")
            .param("lang", "en")
            .header("Authorization", token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.slug").value("isinwheel-long-range-3-wheel-kids-electric-scooter"))
        .andReturn()
        .getResponse()
        .getContentAsString();

    JsonNode kidsData = objectMapper.readTree(kidsResponse).path("data");
    assertThat(kidsData.path("skuList").size()).isEqualTo(10);
    assertThat(textValues(kidsData.path("skuAttributeOptions").path("style")))
        .contains("Blue+Pink", "Pink+Pink");
    assertThat(textValues(kidsData.path("skuAttributeOptions").path("with gift box")))
        .contains("Mini Pro Standard", "Mini Pro with Gift Box");

    long kidsSelectableCount = 0;
    Set<String> kidsUniquePics = new HashSet<>();
    for (JsonNode sku : kidsData.path("skuList")) {
      kidsUniquePics.add(sku.path("pic").asText());
      if (sku.path("stock").asInt() > 0 && !"INACTIVE".equals(sku.path("status").asText())) {
        kidsSelectableCount++;
      }
    }
    assertThat(kidsSelectableCount).isGreaterThanOrEqualTo(8);
    assertThat(kidsUniquePics.size()).isGreaterThanOrEqualTo(8);

    String commuterResponse = mockMvc.perform(get("/product/slug/isinwheel-s9-pro-pneumatic-tire-electric-scooter-2026")
            .param("lang", "en")
            .header("Authorization", token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.slug").value("isinwheel-s9-pro-pneumatic-tire-electric-scooter-2026"))
        .andReturn()
        .getResponse()
        .getContentAsString();

    JsonNode commuterData = objectMapper.readTree(commuterResponse).path("data");
    assertThat(textValues(commuterData.path("skuAttributeOptions").path("buy more save more")))
        .containsExactlyInAnyOrder("S9 Pro*1", "S9 Pro*2");

    String helmetResponse = mockMvc.perform(get("/product/slug/adult-riding-helmet")
            .param("lang", "en")
            .header("Authorization", token))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    JsonNode helmetData = objectMapper.readTree(helmetResponse).path("data");
    assertThat(textValues(helmetData.path("skuAttributeOptions").path("size")))
        .containsExactlyInAnyOrder("M", "L");
  }

  private Set<String> textValues(JsonNode node) {
    Set<String> values = new HashSet<>();
    if (node == null || !node.isArray()) {
      return values;
    }
    for (JsonNode item : node) {
      values.add(item.asText());
    }
    return values;
  }
}
