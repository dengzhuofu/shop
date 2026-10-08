package com.shop.vo;

import com.shop.common.JsonLocaleUtils;
import com.shop.common.PricingContext;
import com.shop.entity.SmsCoupon;
import com.shop.entity.SmsCouponUser;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CouponVO {
  private Long couponId;
  private String currency;
  private Long couponUserId;
  private String code;
  private String title;
  private String description;
  private BigDecimal thresholdAmount;
  private BigDecimal discountAmount;
  private Boolean claimed;

  public static CouponVO from(SmsCoupon coupon, SmsCouponUser couponUser) {
    CouponVO vo = new CouponVO();
    vo.setCouponId(coupon.getId());
    vo.setCouponUserId(couponUser == null ? null : couponUser.getId());
    vo.setCode(coupon.getCode());
    vo.setTitle(JsonLocaleUtils.localizedText(coupon.getTitle()));
    vo.setDescription(JsonLocaleUtils.localizedText(coupon.getDescription()));
    vo.setCurrency(PricingContext.currency());
    vo.setThresholdAmount(PricingContext.money(coupon.getThresholdAmount()));
    vo.setDiscountAmount(PricingContext.money(coupon.getDiscountAmount()));
    vo.setClaimed(couponUser != null);
    return vo;
  }
}
