package com.shop.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.shop.common.Result;
import com.shop.dto.RecentlyViewedSyncDTO;
import com.shop.entity.PmsProduct;
import com.shop.service.PmsProductService;
import com.shop.service.UmsRecentlyViewedProductService;
import com.shop.vo.RecentlyViewedProductVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/product/recently-viewed")
@RequiredArgsConstructor
public class ProductRecentlyViewedController {

  private final PmsProductService productService;
  private final UmsRecentlyViewedProductService recentlyViewedProductService;

  @GetMapping
  public Result<List<RecentlyViewedProductVO>> list(
      @RequestParam(defaultValue = "6") Integer limit
  ) {
    if (!StpUtil.isLogin()) {
      return Result.success(Collections.emptyList());
    }
    return Result.success(recentlyViewedProductService.listRecent(
        StpUtil.getLoginIdAsLong(),
        normalizedLimit(limit)));
  }

  @PostMapping("/{productId}")
  public Result<Void> record(@PathVariable Long productId) {
    PmsProduct product = productService.getById(productId);
    if (product == null || !Boolean.TRUE.equals(product.getPublished())) {
      return Result.error(404, "Product not found");
    }

    if (StpUtil.isLogin()) {
      recentlyViewedProductService.recordView(StpUtil.getLoginIdAsLong(), productId);
    }
    return Result.success();
  }

  @PostMapping("/sync")
  public Result<List<RecentlyViewedProductVO>> sync(
      @RequestBody(required = false) RecentlyViewedSyncDTO dto,
      @RequestParam(defaultValue = "6") Integer limit
  ) {
    if (!StpUtil.isLogin()) {
      return Result.success(Collections.emptyList());
    }

    List<Long> productIds = dto == null ? Collections.emptyList() : dto.getProductIds();
    recentlyViewedProductService.recordViews(StpUtil.getLoginIdAsLong(), productIds);
    return Result.success(recentlyViewedProductService.listRecent(
        StpUtil.getLoginIdAsLong(),
        normalizedLimit(limit)));
  }

  private int normalizedLimit(Integer limit) {
    if (limit == null) {
      return 6;
    }
    return Math.max(1, Math.min(limit, 12));
  }
}
