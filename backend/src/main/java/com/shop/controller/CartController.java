package com.shop.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shop.common.Result;
import com.shop.dto.CartAddDTO;
import com.shop.entity.OmsCartItem;
import com.shop.service.OmsCartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

  private final OmsCartItemService cartItemService;

  @PostMapping("/add")
  public Result<Void> add(@RequestBody @Validated CartAddDTO dto) {
    Long userId = StpUtil.getLoginIdAsLong();

    // Check if item already exists in cart
    OmsCartItem existingItem = cartItemService.getOne(new QueryWrapper<OmsCartItem>()
        .eq("user_id", userId)
        .eq("product_id", dto.getProductId())
        .eq("sku_id", dto.getSkuId()));

    if (existingItem != null) {
      existingItem.setQuantity(existingItem.getQuantity() + dto.getQuantity());
      existingItem.setUpdateTime(LocalDateTime.now());
      cartItemService.updateById(existingItem);
    } else {
      OmsCartItem newItem = new OmsCartItem();
      newItem.setUserId(userId);
      newItem.setProductId(dto.getProductId());
      newItem.setSkuId(dto.getSkuId());
      newItem.setQuantity(dto.getQuantity());
      newItem.setCreateTime(LocalDateTime.now());
      newItem.setUpdateTime(LocalDateTime.now());
      cartItemService.save(newItem);
    }

    return Result.success(null);
  }

  @GetMapping("/list")
  public Result<List<OmsCartItem>> list() {
    Long userId = StpUtil.getLoginIdAsLong();
    List<OmsCartItem> list = cartItemService.list(new QueryWrapper<OmsCartItem>().eq("user_id", userId));
    return Result.success(list);
  }
}
