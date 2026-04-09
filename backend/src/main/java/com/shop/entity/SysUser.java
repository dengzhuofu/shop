package com.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class SysUser {
  @TableId(type = IdType.AUTO)
  private Long id;

  private String email;

  @TableField("password_hash")
  private String passwordHash;

  @TableField("first_name")
  private String firstName;

  @TableField("last_name")
  private String lastName;

  private String nickname;

  @TableField("email_verified")
  private Boolean emailVerified;

  private String status;

  @TableField("last_login_time")
  private LocalDateTime lastLoginTime;

  @TableField("create_time")
  private LocalDateTime createTime;

  @TableField("update_time")
  private LocalDateTime updateTime;
}
