package com.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shop.entity.UmsRecentlyViewedProduct;
import com.shop.vo.RecentlyViewedProductVO;

import java.util.List;

public interface UmsRecentlyViewedProductService extends IService<UmsRecentlyViewedProduct> {
  void recordView(Long userId, Long productId);

  void recordViews(Long userId, List<Long> productIds);

  List<RecentlyViewedProductVO> listRecent(Long userId, int limit);
}
