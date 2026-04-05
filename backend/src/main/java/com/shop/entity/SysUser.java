package com.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
  private String passwordHash;
  private String firstName;
  private String lastName;
  private String nickname;
  private Boolean emailVerified;
  private String status;
  private LocalDateTime lastLoginTime;
  private LocalDateTime createTime;
  private LocalDateTime updateTime;
}
