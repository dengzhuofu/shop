package com.shop.vo;

import lombok.Data;

import java.util.List;

@Data
public class CategoryMenuGroupVO {
  private Long id;
  private String slug;
  private String name;
  private String description;
  private Long productCount;
  private String allLinkUrl;
  private String allLinkText;
  private List<ProductVO> products;
}
