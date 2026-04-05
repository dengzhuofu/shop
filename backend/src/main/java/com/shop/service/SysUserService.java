package com.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shop.entity.SysUser;

public interface SysUserService extends IService<SysUser> {
  SysUser getByEmail(String email);
}
