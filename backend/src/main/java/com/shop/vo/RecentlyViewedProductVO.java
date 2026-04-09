package com.shop.vo;

import com.shop.entity.PmsProduct;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RecentlyViewedProductVO {
  private Long id;
  private String slug;
  private String title;
  private String subtitle;
  private String pic;
  private BigDecimal price;
  private BigDecimal compareAtPrice;
  private Boolean isNew;
  private Object tags;
  private String categorySlug;
  private LocalDateTime viewedAt;
  private Integer viewCount;

  public static RecentlyViewedProductVO from(
      PmsProduct product,
      String categorySlug,
      LocalDateTime viewedAt,
      Integer viewCount
  ) {
    RecentlyViewedProductVO vo = new RecentlyViewedProductVO();
    String lang = com.shop.common.JsonLocaleUtils.currentLanguage();
    vo.setId(product.getId());
    vo.setSlug(product.getSlug());
    vo.setTitle(ProductVO.extractLang(product.getName(), lang));
    vo.setSubtitle(ProductVO.extractLang(product.getSubtitle(), lang));
    vo.setPic(product.getPic());
    vo.setPrice(product.getPrice());
    vo.setCompareAtPrice(product.getCompareAtPrice());
    vo.setIsNew(Boolean.TRUE.equals(product.getIsNew()));
    vo.setTags(ProductVO.extractLangObject(product.getTags(), lang));
    vo.setCategorySlug(categorySlug);
    vo.setViewedAt(viewedAt);
    vo.setViewCount(viewCount == null ? 1 : viewCount);
    return vo;
  }
}
