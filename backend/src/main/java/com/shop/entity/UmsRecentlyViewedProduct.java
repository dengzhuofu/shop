package com.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ums_recently_viewed_product")
public class UmsRecentlyViewedProduct {
  @TableId(type = IdType.AUTO)
  private Long id;
  private Long userId;
  private Long productId;
  private Integer viewCount;
  private LocalDateTime lastViewedTime;
  private LocalDateTime createTime;
  private LocalDateTime updateTime;
}
