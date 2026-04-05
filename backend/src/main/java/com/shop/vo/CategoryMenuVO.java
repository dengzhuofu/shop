package com.shop.vo;

import lombok.Data;

import java.util.List;

@Data
public class CategoryMenuVO {
  private Long id;
  private String slug;
  private String name;
  private String description;
  private String heroImage;
  private String menuImage;
  private Long productCount;
  private CategoryMenuBannerVO banner;
  private List<CategoryMenuGroupVO> children;
}
