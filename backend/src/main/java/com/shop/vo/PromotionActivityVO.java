package com.shop.vo;

import com.shop.common.JsonLocaleUtils;
import com.shop.entity.CmsPromotionActivity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PromotionActivityVO {
  private Long id;
  private String code;
  private String title;
  private String subtitle;
  private String tag;
  private LocalDateTime countdownEndAt;
  private String desktopBg;
  private String mobileBg;
  private String linkUrl;
  private String lang;
  private Boolean enabled;

  public static PromotionActivityVO from(CmsPromotionActivity activity) {
    PromotionActivityVO vo = new PromotionActivityVO();
    vo.setId(activity.getId());
    vo.setCode(activity.getCode());
    vo.setTitle(JsonLocaleUtils.localizedText(activity.getTitle()));
    vo.setSubtitle(JsonLocaleUtils.localizedText(activity.getSubtitle()));
    vo.setTag(JsonLocaleUtils.localizedText(activity.getTag()));
    vo.setCountdownEndAt(activity.getCountdownEndAt());
    vo.setDesktopBg(activity.getDesktopBg());
    vo.setMobileBg(activity.getMobileBg());
    vo.setLinkUrl(activity.getLinkUrl());
    vo.setLang(activity.getLang());
    vo.setEnabled(activity.getEnabled());
    return vo;
  }
}
