package com.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName(value = "cms_promotion_activity", autoResultMap = true)
public class CmsPromotionActivity {
  @TableId(type = IdType.AUTO)
  private Long id;
  private String code;

  @TableField(typeHandler = JacksonTypeHandler.class)
  private JsonNode title;

  @TableField(typeHandler = JacksonTypeHandler.class)
  private JsonNode subtitle;

  @TableField(typeHandler = JacksonTypeHandler.class)
  private JsonNode tag;

  private LocalDateTime countdownEndAt;
  private String desktopBg;
  private String mobileBg;
  private String linkUrl;
  private String lang;
  private Boolean enabled;
  private Integer sortOrder;
  private LocalDateTime createTime;
  private LocalDateTime updateTime;
}
