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
@TableName(value = "pms_category", autoResultMap = true)
public class PmsCategory {
  @TableId(type = IdType.AUTO)
  private Long id;
  private Long parentId;
  private String slug;

  @TableField(typeHandler = JacksonTypeHandler.class)
  private JsonNode name;

  @TableField(typeHandler = JacksonTypeHandler.class)
  private JsonNode description;

  private String heroImage;
  private String menuImage;
  private Integer sortOrder;
  private Boolean published;
  private LocalDateTime createTime;
  private LocalDateTime updateTime;
}
