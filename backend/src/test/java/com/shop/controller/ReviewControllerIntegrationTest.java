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

class ReviewControllerIntegrationTest extends BackendIntegrationTestSupport {

  @Test
  void seededSnoVaReviewsExposeSummaryPhotosAndMerchantReplies() throws Exception {
    String summaryResponse = mockMvc.perform(get("/review/product/15/summary"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.totalReviews").value(6))
        .andExpect(jsonPath("$.data.ratingDistribution.5").value(5))
        .andExpect(jsonPath("$.data.ratingDistribution.4").value(1))
        .andReturn()
        .getResponse()
        .getContentAsString();

    JsonNode summary = objectMapper.readTree(summaryResponse).path("data");
    assertThat(summary.path("averageRating").asDouble()).isGreaterThan(4.8);

    mockMvc.perform(get("/review/product/15").param("pageNum", "1").param("pageSize", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.records.length()").value(6))
        .andExpect(jsonPath("$.data.records[0].images[0]").isNotEmpty())
        .andExpect(jsonPath("$.data.records[4].merchantReply").isNotEmpty())
        .andExpect(jsonPath("$.data.records[4].verifiedPurchase").value(true));
  }

  @Test
  void signedInUserCanCreateReviewAndReceiveVerifiedPurchaseBadge() throws Exception {
    String token = loginAndGetToken("admin@isinwheel.local", "123456");

    String payload = """
        {
          "rating": 5,
          "title": "City test ride",
          "content": "The top tab bar and the new reviews section make this product much easier to evaluate.",
          "images": [
            "https://images.unsplash.com/photo-1511994298241-608e28f14fde?auto=format&fit=crop&q=80&w=1200"
          ]
        }
        """;

    mockMvc.perform(post("/review/product/15")
            .header("Authorization", token)
            .contentType(APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.userName").value("Admin User"))
        .andExpect(jsonPath("$.data.verifiedPurchase").value(true))
        .andExpect(jsonPath("$.data.images[0]").value("https://images.unsplash.com/photo-1511994298241-608e28f14fde?auto=format&fit=crop&q=80&w=1200"));

    mockMvc.perform(get("/review/product/15/summary"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.totalReviews").value(7))
        .andExpect(jsonPath("$.data.ratingDistribution.5").value(6));
  }
}
