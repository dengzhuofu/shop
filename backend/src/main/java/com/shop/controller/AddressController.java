package com.shop.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shop.common.Result;
import com.shop.dto.AddressCreateDTO;
import com.shop.dto.AddressUpdateDTO;
import com.shop.entity.UmsUserAddress;
import com.shop.service.UmsUserAddressService;
import com.shop.vo.AddressVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressController {

  private final UmsUserAddressService userAddressService;

  @GetMapping("/list")
  public Result<List<AddressVO>> list() {
    Long userId = StpUtil.getLoginIdAsLong();
    List<UmsUserAddress> addresses = userAddressService.list(
        new QueryWrapper<UmsUserAddress>().eq("user_id", userId).orderByDesc("is_default").orderByDesc("id"));
    return Result.success(addresses.stream().map(AddressVO::from).collect(Collectors.toList()));
  }

  @PostMapping
  public Result<AddressVO> create(@RequestBody AddressCreateDTO dto) {
    Long userId = StpUtil.getLoginIdAsLong();
    UmsUserAddress address = new UmsUserAddress();
    BeanUtils.copyProperties(dto, address);
    address.setUserId(userId);
    address.setCreateTime(LocalDateTime.now());
    address.setUpdateTime(LocalDateTime.now());
    if (Boolean.TRUE.equals(dto.getIsDefault())) {
      clearDefault(userId);
      address.setIsDefault(true);
    }
    userAddressService.save(address);
    return Result.success(AddressVO.from(address));
  }

  @PutMapping("/{id}")
  public Result<AddressVO> update(@PathVariable Long id, @RequestBody AddressUpdateDTO dto) {
    Long userId = StpUtil.getLoginIdAsLong();
    UmsUserAddress address = userAddressService.getById(id);
    if (address == null || !address.getUserId().equals(userId)) {
      return Result.error(404, "Address not found");
    }
    BeanUtils.copyProperties(dto, address);
    if (Boolean.TRUE.equals(dto.getIsDefault())) {
      clearDefault(userId);
      address.setIsDefault(true);
    }
    address.setUpdateTime(LocalDateTime.now());
    userAddressService.updateById(address);
    return Result.success(AddressVO.from(address));
  }

  @DeleteMapping("/{id}")
  public Result<Void> delete(@PathVariable Long id) {
    Long userId = StpUtil.getLoginIdAsLong();
    UmsUserAddress address = userAddressService.getById(id);
    if (address == null || !address.getUserId().equals(userId)) {
      return Result.error(404, "Address not found");
    }
    userAddressService.removeById(id);
    return Result.success(null);
  }

  @PutMapping("/{id}/default")
  public Result<Void> setDefault(@PathVariable Long id) {
    Long userId = StpUtil.getLoginIdAsLong();
    UmsUserAddress address = userAddressService.getById(id);
    if (address == null || !address.getUserId().equals(userId)) {
      return Result.error(404, "Address not found");
    }
    clearDefault(userId);
    address.setIsDefault(true);
    address.setUpdateTime(LocalDateTime.now());
    userAddressService.updateById(address);
    return Result.success(null);
  }

  private void clearDefault(Long userId) {
    List<UmsUserAddress> addresses = userAddressService.list(new QueryWrapper<UmsUserAddress>().eq("user_id", userId));
    for (UmsUserAddress item : addresses) {
      if (Boolean.TRUE.equals(item.getIsDefault())) {
        item.setIsDefault(false);
        item.setUpdateTime(LocalDateTime.now());
        userAddressService.updateById(item);
      }
    }
  }
}
