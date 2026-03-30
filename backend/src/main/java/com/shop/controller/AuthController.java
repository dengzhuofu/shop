package com.shop.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shop.common.Result;
import com.shop.dto.LoginDTO;
import com.shop.entity.SysUser;
import com.shop.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final SysUserService sysUserService;

  @PostMapping("/login")
  public Result<Map<String, String>> login(@RequestBody @Validated LoginDTO loginDTO) {
    SysUser user = sysUserService.getOne(new QueryWrapper<SysUser>().eq("username", loginDTO.getUsername()));

    if (user == null || !user.getPassword().equals(loginDTO.getPassword())) {
      return Result.error(401, "Invalid username or password");
    }

    StpUtil.login(user.getId());

    Map<String, String> tokenInfo = new HashMap<>();
    tokenInfo.put("tokenName", StpUtil.getTokenName());
    tokenInfo.put("tokenValue", StpUtil.getTokenValue());

    return Result.success(tokenInfo);
  }

  @PostMapping("/logout")
  public Result<Void> logout() {
    StpUtil.logout();
    return Result.success(null);
  }
}
