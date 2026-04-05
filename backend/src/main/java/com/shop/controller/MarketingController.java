package com.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shop.common.Result;
import com.shop.entity.CmsPromotionActivity;
import com.shop.service.CmsPromotionActivityService;
import com.shop.vo.PromotionActivityVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/marketing")
@RequiredArgsConstructor
public class MarketingController {

  private final CmsPromotionActivityService promotionActivityService;

  @GetMapping("/activities")
  public Result<List<PromotionActivityVO>> activities(
      @RequestParam(defaultValue = "false") boolean enabledOnly) {
    QueryWrapper<CmsPromotionActivity> wrapper = new QueryWrapper<>();
    if (enabledOnly) {
      wrapper.eq("enabled", true);
    }
    wrapper.orderByAsc("sort_order").orderByDesc("id");
    return Result.success(promotionActivityService.list(wrapper).stream().map(PromotionActivityVO::from).toList());
  }

  @GetMapping("/activities/current")
  public Result<PromotionActivityVO> current() {
    List<CmsPromotionActivity> activities = promotionActivityService.list(new QueryWrapper<CmsPromotionActivity>()
        .eq("enabled", true)
        .orderByAsc("sort_order")
        .orderByDesc("id")
        .last("LIMIT 1"));
    CmsPromotionActivity activity = activities.isEmpty() ? null : activities.get(0);
    if (activity == null) {
      return Result.success(null);
    }
    return Result.success(PromotionActivityVO.from(activity));
  }
}
