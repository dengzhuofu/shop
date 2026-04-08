package com.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shop.entity.SysUser;
import com.shop.mapper.SysUserMapper;
import com.shop.service.SysUserService;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
  @Override
  public SysUser getByEmail(String email) {
    if (email == null) {
      return null;
    }
    return this.getOne(new QueryWrapper<SysUser>().eq("email", email.trim().toLowerCase()));
  }
}
