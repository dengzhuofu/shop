package com.shop.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.shop.common.Result;
import com.shop.dto.LoginDTO;
import com.shop.dto.RegisterEmailDTO;
import com.shop.entity.SysUser;
import com.shop.service.SysUserService;
import com.shop.vo.AuthUserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
  private final PasswordEncoder passwordEncoder;

  @PostMapping("/login")
  public Result<Map<String, Object>> login(@RequestBody @Validated LoginDTO loginDTO) {
    SysUser user = sysUserService.getByEmail(loginDTO.getEmail());

    if (user == null || !matchesPassword(loginDTO.getPassword(), user.getPasswordHash())) {
      return Result.error(401, "Invalid email or password");
    }

    upgradeLegacyPasswordIfNeeded(loginDTO.getPassword(), user);
    StpUtil.login(user.getId());

    user.setLastLoginTime(java.time.LocalDateTime.now());
    sysUserService.updateById(user);

    Map<String, Object> tokenInfo = new HashMap<>();
    tokenInfo.put("tokenName", StpUtil.getTokenName());
    tokenInfo.put("tokenValue", StpUtil.getTokenValue());
    tokenInfo.put("user", AuthUserVO.from(user));

    return Result.success(tokenInfo);
  }

  @PostMapping("/register/email")
  public Result<Map<String, Object>> registerByEmail(@RequestBody @Validated RegisterEmailDTO registerDTO) {
    if (sysUserService.getByEmail(registerDTO.getEmail()) != null) {
      return Result.error(400, "Email already registered");
    }

    SysUser user = new SysUser();
    user.setEmail(registerDTO.getEmail().trim().toLowerCase());
    user.setPasswordHash(passwordEncoder.encode(registerDTO.getPassword()));
    user.setFirstName(registerDTO.getFirstName().trim());
    user.setLastName(registerDTO.getLastName().trim());
    user.setNickname((registerDTO.getFirstName() + " " + registerDTO.getLastName()).trim());
    user.setEmailVerified(false);
    user.setStatus("ACTIVE");
    user.setCreateTime(java.time.LocalDateTime.now());
    user.setUpdateTime(java.time.LocalDateTime.now());
    sysUserService.save(user);

    StpUtil.login(user.getId());

    Map<String, Object> payload = new HashMap<>();
    payload.put("tokenName", StpUtil.getTokenName());
    payload.put("tokenValue", StpUtil.getTokenValue());
    payload.put("user", AuthUserVO.from(user));
    return Result.success(payload);
  }

  @GetMapping("/me")
  public Result<AuthUserVO> me() {
    Long userId = StpUtil.getLoginIdAsLong();
    SysUser user = sysUserService.getById(userId);
    if (user == null) {
      return Result.error(404, "User not found");
    }
    return Result.success(AuthUserVO.from(user));
  }

  @PostMapping("/logout")
  public Result<Void> logout() {
    StpUtil.logout();
    return Result.success(null);
  }

  private boolean matchesPassword(String rawPassword, String storedPassword) {
    if (storedPassword == null || storedPassword.isBlank()) {
      return false;
    }
    if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$") || storedPassword.startsWith("$2y$")) {
      return passwordEncoder.matches(rawPassword, storedPassword);
    }
    return storedPassword.equals(rawPassword);
  }

  private void upgradeLegacyPasswordIfNeeded(String rawPassword, SysUser user) {
    String storedPassword = user.getPasswordHash();
    if (storedPassword == null || storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$")
        || storedPassword.startsWith("$2y$")) {
      return;
    }
    if (storedPassword.equals(rawPassword)) {
      user.setPasswordHash(passwordEncoder.encode(rawPassword));
      user.setUpdateTime(java.time.LocalDateTime.now());
    }
  }
}
