package com.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shop.entity.PmsCategory;
import com.shop.entity.PmsProduct;
import com.shop.entity.UmsRecentlyViewedProduct;
import com.shop.mapper.UmsRecentlyViewedProductMapper;
import com.shop.service.PmsCategoryService;
import com.shop.service.PmsProductService;
import com.shop.service.UmsRecentlyViewedProductService;
import com.shop.vo.RecentlyViewedProductVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UmsRecentlyViewedProductServiceImpl
    extends ServiceImpl<UmsRecentlyViewedProductMapper, UmsRecentlyViewedProduct>
    implements UmsRecentlyViewedProductService {

  private final PmsProductService productService;
  private final PmsCategoryService categoryService;

  @Override
  public void recordView(Long userId, Long productId) {
    if (userId == null || productId == null || !isPublishedProduct(productId)) {
      return;
    }

    upsertView(userId, productId, LocalDateTime.now());
  }

  @Override
  public void recordViews(Long userId, List<Long> productIds) {
    if (userId == null || productIds == null || productIds.isEmpty()) {
      return;
    }

    List<Long> normalizedIds = normalizeProductIds(productIds);
    if (normalizedIds.isEmpty()) {
      return;
    }

    Set<Long> publishedIds = productService.listByIds(normalizedIds).stream()
        .filter(product -> Boolean.TRUE.equals(product.getPublished()))
        .map(PmsProduct::getId)
        .collect(Collectors.toSet());
    if (publishedIds.isEmpty()) {
      return;
    }

    LocalDateTime baseTime = LocalDateTime.now();
    for (int index = 0; index < normalizedIds.size(); index++) {
      Long productId = normalizedIds.get(index);
      if (!publishedIds.contains(productId)) {
        continue;
      }
      upsertView(userId, productId, baseTime.minusSeconds(index));
    }
  }

  @Override
  public List<RecentlyViewedProductVO> listRecent(Long userId, int limit) {
    if (userId == null || limit <= 0) {
      return Collections.emptyList();
    }

    List<UmsRecentlyViewedProduct> records = this.list(new QueryWrapper<UmsRecentlyViewedProduct>()
        .eq("user_id", userId)
        .orderByDesc("last_viewed_time")
        .orderByDesc("id")
        .last("LIMIT " + Math.min(limit, 12)));
    if (records.isEmpty()) {
      return Collections.emptyList();
    }

    Map<Long, PmsProduct> productById = productService.listByIds(records.stream()
            .map(UmsRecentlyViewedProduct::getProductId)
            .collect(Collectors.toList()))
        .stream()
        .filter(product -> Boolean.TRUE.equals(product.getPublished()))
        .collect(Collectors.toMap(PmsProduct::getId, product -> product));
    if (productById.isEmpty()) {
      return Collections.emptyList();
    }

    Map<Long, PmsCategory> categoryById = categoryService.list(new QueryWrapper<PmsCategory>()
            .eq("published", true)
            .orderByAsc("sort_order")
            .orderByAsc("id"))
        .stream()
        .collect(Collectors.toMap(PmsCategory::getId, category -> category));

    List<RecentlyViewedProductVO> result = new ArrayList<>();
    for (UmsRecentlyViewedProduct record : records) {
      PmsProduct product = productById.get(record.getProductId());
      if (product == null) {
        continue;
      }
      result.add(RecentlyViewedProductVO.from(
          product,
          resolveRootSlug(product.getCategoryId(), categoryById),
          record.getLastViewedTime(),
          record.getViewCount()));
    }
    return result;
  }

  private List<Long> normalizeProductIds(List<Long> productIds) {
    Set<Long> seen = new LinkedHashSet<>();
    for (Long productId : productIds) {
      if (productId != null) {
        seen.add(productId);
      }
    }
    return new ArrayList<>(seen);
  }

  private boolean isPublishedProduct(Long productId) {
    PmsProduct product = productService.getById(productId);
    return product != null && Boolean.TRUE.equals(product.getPublished());
  }

  private void upsertView(Long userId, Long productId, LocalDateTime viewedAt) {
    UmsRecentlyViewedProduct existing = this.getOne(new QueryWrapper<UmsRecentlyViewedProduct>()
        .eq("user_id", userId)
        .eq("product_id", productId)
        .last("LIMIT 1"));
    if (existing != null) {
      existing.setViewCount((existing.getViewCount() == null ? 0 : existing.getViewCount()) + 1);
      existing.setLastViewedTime(viewedAt);
      existing.setUpdateTime(viewedAt);
      this.updateById(existing);
      return;
    }

    UmsRecentlyViewedProduct record = new UmsRecentlyViewedProduct();
    record.setUserId(userId);
    record.setProductId(productId);
    record.setViewCount(1);
    record.setLastViewedTime(viewedAt);
    record.setCreateTime(viewedAt);
    record.setUpdateTime(viewedAt);
    this.save(record);
  }

  private String resolveRootSlug(Long categoryId, Map<Long, PmsCategory> categoryById) {
    PmsCategory current = categoryById.get(categoryId);
    while (current != null && current.getParentId() != null) {
      current = categoryById.get(current.getParentId());
    }
    return current == null ? null : current.getSlug();
  }
}
