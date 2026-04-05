package com.shop.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shop.common.Result;
import com.shop.dto.CouponValidateDTO;
import com.shop.entity.SmsCoupon;
import com.shop.entity.SmsCouponUser;
import com.shop.service.SmsCouponService;
import com.shop.service.SmsCouponUserService;
import com.shop.vo.CouponVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/coupon")
@RequiredArgsConstructor
public class CouponController {

  private final SmsCouponService couponService;
  private final SmsCouponUserService couponUserService;

  @GetMapping("/available")
  public Result<List<CouponVO>> available() {
    Long userId = StpUtil.getLoginIdAsLong();
    List<SmsCoupon> coupons = couponService.list(new QueryWrapper<SmsCoupon>()
        .eq("active", true)
        .orderByAsc("threshold_amount")
        .orderByDesc("discount_amount"));
    List<CouponVO> result = coupons.stream().map(coupon -> {
      SmsCouponUser couponUser = couponUserService.getOne(new QueryWrapper<SmsCouponUser>()
          .eq("coupon_id", coupon.getId())
          .eq("user_id", userId));
      return CouponVO.from(coupon, couponUser);
    }).collect(Collectors.toList());
    return Result.success(result);
  }

  @PostMapping("/{couponId}/claim")
  public Result<CouponVO> claim(@PathVariable Long couponId) {
    Long userId = StpUtil.getLoginIdAsLong();
    SmsCoupon coupon = couponService.getById(couponId);
    if (coupon == null || !Boolean.TRUE.equals(coupon.getActive())) {
      return Result.error(404, "Coupon not found");
    }

    SmsCouponUser existing = couponUserService.getOne(new QueryWrapper<SmsCouponUser>()
        .eq("coupon_id", couponId)
        .eq("user_id", userId));
    if (existing != null) {
      return Result.success(CouponVO.from(coupon, existing));
    }

    SmsCouponUser couponUser = new SmsCouponUser();
    couponUser.setCouponId(couponId);
    couponUser.setUserId(userId);
    couponUser.setStatus("CLAIMED");
    couponUser.setClaimedTime(LocalDateTime.now());
    couponUser.setCreateTime(LocalDateTime.now());
    couponUser.setUpdateTime(LocalDateTime.now());
    couponUserService.save(couponUser);
    return Result.success(CouponVO.from(coupon, couponUser));
  }

  @GetMapping("/my")
  public Result<List<CouponVO>> myCoupons() {
    Long userId = StpUtil.getLoginIdAsLong();
    List<SmsCouponUser> couponUsers = couponUserService.list(new QueryWrapper<SmsCouponUser>()
        .eq("user_id", userId)
        .orderByDesc("claimed_time"));
    List<CouponVO> result = couponUsers.stream()
        .map(couponUser -> CouponVO.from(couponService.getById(couponUser.getCouponId()), couponUser))
        .collect(Collectors.toList());
    return Result.success(result);
  }

  @PostMapping("/validate")
  public Result<CouponVO> validate(@RequestBody CouponValidateDTO dto) {
    Long userId = StpUtil.getLoginIdAsLong();
    SmsCouponUser couponUser = couponUserService.getById(dto.getCouponUserId());
    if (couponUser == null || !couponUser.getUserId().equals(userId)) {
      return Result.error(404, "Coupon not found");
    }
    SmsCoupon coupon = couponService.getById(couponUser.getCouponId());
    if (coupon == null || !Boolean.TRUE.equals(coupon.getActive())) {
      return Result.error(400, "Coupon is unavailable");
    }
    if (dto.getSubtotalAmount().compareTo(coupon.getThresholdAmount()) < 0) {
      return Result.error(400, "Coupon threshold not met");
    }
    BigDecimal maxDiscount = dto.getSubtotalAmount().min(coupon.getDiscountAmount());
    CouponVO vo = CouponVO.from(coupon, couponUser);
    vo.setDiscountAmount(maxDiscount);
    return Result.success(vo);
  }
}
