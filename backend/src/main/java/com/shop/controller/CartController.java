package com.shop.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.common.LanguageContext;
import com.shop.common.Result;
import com.shop.dto.CartAddDTO;
import com.shop.dto.CartUpdateDTO;
import com.shop.entity.OmsCartItem;
import com.shop.entity.PmsProduct;
import com.shop.entity.PmsSku;
import com.shop.service.OmsCartItemService;
import com.shop.service.PmsProductService;
import com.shop.service.PmsSkuService;
import com.shop.vo.CartItemVO;
import com.shop.vo.ProductVO;
import com.shop.vo.SkuVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

  private final OmsCartItemService cartItemService;
  private final PmsProductService productService;
  private final PmsSkuService skuService;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @PostMapping("/add")
  public Result<Void> add(@RequestBody @Validated CartAddDTO dto) {
    Long userId = StpUtil.getLoginIdAsLong();
    String lang = LanguageContext.getLanguage();

    // Check if item already exists in cart
    OmsCartItem existingItem = cartItemService.getOne(new QueryWrapper<OmsCartItem>()
        .eq("user_id", userId)
        .eq("product_id", dto.getProductId())
        .eq("sku_id", dto.getSkuId()));

    if (existingItem != null) {
      existingItem.setQuantity(existingItem.getQuantity() + dto.getQuantity());
      existingItem.setUpdateTime(LocalDateTime.now());
      PmsSku selectedSku = skuService.getById(existingItem.getSkuId());
      if (selectedSku != null) {
        existingItem.setSelectedAttributesSnapshot(
            objectMapper.valueToTree(SkuVO.buildSnapshot(selectedSku.getSpecs(), lang)));
      }
      cartItemService.updateById(existingItem);
    } else {
      PmsSku sku = skuService.getById(dto.getSkuId());
      OmsCartItem newItem = new OmsCartItem();
      newItem.setUserId(userId);
      newItem.setProductId(dto.getProductId());
      newItem.setSkuId(dto.getSkuId());
      newItem.setQuantity(dto.getQuantity());
      if (sku != null) {
        newItem.setSelectedAttributesSnapshot(
            objectMapper.valueToTree(SkuVO.buildSnapshot(sku.getSpecs(), lang)));
      }
      newItem.setCreateTime(LocalDateTime.now());
      newItem.setUpdateTime(LocalDateTime.now());
      cartItemService.save(newItem);
    }

    return Result.success(null);
  }

  @GetMapping("/list")
  public Result<List<CartItemVO>> list() {
    Long userId = StpUtil.getLoginIdAsLong();
    String lang = LanguageContext.getLanguage();
    List<OmsCartItem> list = cartItemService.list(new QueryWrapper<OmsCartItem>().eq("user_id", userId));
    List<CartItemVO> voList = list.stream().map(item -> {
      PmsSku sku = skuService.getById(item.getSkuId());
      PmsProduct product = productService.getById(item.getProductId());
      CartItemVO vo = new CartItemVO();
      vo.setCartItemId(item.getId());
      vo.setProductId(item.getProductId());
      vo.setSkuId(item.getSkuId());
      vo.setQuantity(item.getQuantity());
      if (sku != null) {
        vo.setProductPic(sku.getPic());
        vo.setUnitPrice(sku.getPrice());
        vo.setLineAmount(sku.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        vo.setStock(sku.getStock());
        vo.setAttributes(item.getSelectedAttributesSnapshot() != null
            ? item.getSelectedAttributesSnapshot()
            : SkuVO.buildSnapshot(sku.getSpecs(), lang));
      }
      if (product != null) {
        vo.setTitle(ProductVO.extractLang(product.getName(), lang));
      }
      return vo;
    }).collect(Collectors.toList());
    return Result.success(voList);
  }

  @PutMapping("/{id}")
  public Result<Void> updateQuantity(@PathVariable Long id, @RequestBody @Validated CartUpdateDTO dto) {
    Long userId = StpUtil.getLoginIdAsLong();
    OmsCartItem cartItem = cartItemService.getById(id);
    if (cartItem == null || !cartItem.getUserId().equals(userId)) {
      return Result.error(404, "Cart item not found");
    }
    cartItem.setQuantity(dto.getQuantity());
    cartItem.setUpdateTime(LocalDateTime.now());
    cartItemService.updateById(cartItem);
    return Result.success(null);
  }

  @DeleteMapping("/{id}")
  public Result<Void> deleteItem(@PathVariable Long id) {
    Long userId = StpUtil.getLoginIdAsLong();
    OmsCartItem cartItem = cartItemService.getById(id);
    if (cartItem == null || !cartItem.getUserId().equals(userId)) {
      return Result.error(404, "Cart item not found");
    }
    cartItemService.removeById(id);
    return Result.success(null);
  }
}
