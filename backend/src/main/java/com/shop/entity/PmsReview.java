package com.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "pms_review", autoResultMap = true)
public class PmsReview {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long productId;

    private Long userId;

    private String userName;

    private Integer rating;

    private String title;

    private String content;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> images;

    private Boolean verifiedPurchase;

    private LocalDateTime createTime;
}