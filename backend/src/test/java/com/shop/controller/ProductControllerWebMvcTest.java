package com.shop.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.common.LanguageContext;
import com.shop.config.LanguageInterceptor;
import com.shop.entity.PmsProduct;
import com.shop.entity.PmsSku;
import com.shop.service.PmsCategoryService;
import com.shop.service.PmsProductService;
import com.shop.service.PmsSkuService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProductControllerWebMvcTest {

  @Mock
  private PmsProductService productService;

  @Mock
  private PmsSkuService skuService;

  @Mock
  private PmsCategoryService categoryService;

  private final ObjectMapper objectMapper = new ObjectMapper();

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders
        .standaloneSetup(new ProductController(productService, skuService, categoryService))
        .addInterceptors(new LanguageInterceptor())
        .build();
  }

  @AfterEach
  void tearDown() {
    LanguageContext.clear();
  }

  @Test
  void detailReturnsLocalizedProduct() throws Exception {
    PmsProduct product = new PmsProduct();
    product.setId(1L);
    product.setName(objectMapper.readTree("{\"zh\":\"测试商品\",\"en\":\"Test Product\"}"));
    product.setDescription(objectMapper.readTree("{\"zh\":\"<p>中文描述</p>\",\"en\":\"<p>English description</p>\"}"));
    product.setPrice(new BigDecimal("269.99"));
    product.setCompareAtPrice(new BigDecimal("399.99"));
    product.setStock(100);
    product.setPublished(true);
    product.setTags(objectMapper.readTree("[\"Spring Sale\"]"));
    product.setImages(objectMapper.readTree("[\"https://example.com/product.jpg\"]"));

    when(productService.getById(1L)).thenReturn(product);
    when(skuService.list(org.mockito.ArgumentMatchers.<Wrapper<PmsSku>>any()))
        .thenReturn(Collections.<PmsSku>emptyList());

    mockMvc.perform(get("/product/1").param("lang", "en"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.title").value("Test Product"));
  }
}
