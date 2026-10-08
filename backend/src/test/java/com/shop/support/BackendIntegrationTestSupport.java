package com.shop.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.sql.DataSource;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class BackendIntegrationTestSupport {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

  @Resource
  private DataSource dataSource;

  @BeforeEach
  void resetDatabase() {
    try (var connection = dataSource.getConnection()) {
      if (!connection.getCatalog().startsWith("shop_cbjj_test")) {
        throw new IllegalStateException("Integration tests may reset only an isolated shop_cbjj_test database");
      }
    } catch (java.sql.SQLException exception) {
      throw new IllegalStateException("Cannot validate isolated test database", exception);
    }
    ResourceDatabasePopulator populator = new ResourceDatabasePopulator(new ClassPathResource("schema.sql"),
        new ClassPathResource("db/migration/V5__cbjj_catalog_content.sql"));
    populator.execute(dataSource);
  }

  protected String loginAndGetToken(String email, String password) throws Exception {
    MvcResult result = mockMvc.perform(post("/auth/login")
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new LoginPayload(email, password))))
        .andExpect(status().isOk())
        .andReturn();

    JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
    return json.path("data").path("tokenValue").asText();
  }

  protected record LoginPayload(String email, String password) {
  }
}
