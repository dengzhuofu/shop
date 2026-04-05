package com.shop.vo;

import com.shop.common.JsonLocaleUtils;
import com.shop.entity.PmsCategory;
import lombok.Data;

@Data
public class CategoryVO {
  private Long id;
  private Long parentId;
  private String slug;
  private String name;
  private String description;
  private String heroImage;
  private String menuImage;
  private Long productCount;

  public static CategoryVO from(PmsCategory category, Long productCount) {
    CategoryVO vo = new CategoryVO();
    vo.setId(category.getId());
    vo.setParentId(category.getParentId());
    vo.setSlug(category.getSlug());
    vo.setName(JsonLocaleUtils.localizedText(category.getName()));
    vo.setDescription(JsonLocaleUtils.localizedText(category.getDescription()));
    vo.setHeroImage(category.getHeroImage());
    vo.setMenuImage(category.getMenuImage());
    vo.setProductCount(productCount == null ? 0L : productCount);
    return vo;
  }
}
