package com.shop.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.common.LanguageContext;
import com.shop.common.ProductAddonUtils;
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
import java.util.Objects;
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
    PmsProduct product = productService.getById(dto.getProductId());
    PmsSku sku = skuService.getById(dto.getSkuId());
    if (product == null || sku == null) {
      return Result.error(404, "Product or SKU not found");
    }
    if (!Objects.equals(sku.getProductId(), dto.getProductId())) {
      return Result.error(400, "SKU does not belong to the selected product");
    }
    if (!"ACTIVE".equalsIgnoreCase(sku.getStatus()) || sku.getStock() == null || sku.getStock() <= 0) {
      return Result.error(400, "SKU is out of stock");
    }

    com.fasterxml.jackson.databind.JsonNode selectedAttributesSnapshot =
        objectMapper.valueToTree(SkuVO.buildSnapshot(sku.getSpecs(), lang));
    com.fasterxml.jackson.databind.JsonNode selectedAddonsSnapshot = objectMapper.valueToTree(
        ProductAddonUtils.resolveSelectedAddons(product.getUpsells(), dto.getAddonCodes(), lang));

    List<OmsCartItem> existingItems = cartItemService.list(new QueryWrapper<OmsCartItem>()
        .eq("user_id", userId)
        .eq("product_id", dto.getProductId())
        .eq("sku_id", dto.getSkuId()));
    OmsCartItem existingItem = existingItems.stream()
        .filter(item -> Objects.equals(
            normalizeJson(item.getSelectedAddonsSnapshot()),
            normalizeJson(selectedAddonsSnapshot)))
        .findFirst()
        .orElse(null);

    if (existingItem != null) {
      existingItem.setQuantity(existingItem.getQuantity() + dto.getQuantity());
      existingItem.setUpdateTime(LocalDateTime.now());
      existingItem.setSelectedAttributesSnapshot(selectedAttributesSnapshot);
      existingItem.setSelectedAddonsSnapshot(selectedAddonsSnapshot);
      cartItemService.updateById(existingItem);
    } else {
      OmsCartItem newItem = new OmsCartItem();
      newItem.setUserId(userId);
      newItem.setProductId(dto.getProductId());
      newItem.setSkuId(dto.getSkuId());
      newItem.setQuantity(dto.getQuantity());
      newItem.setSelectedAttributesSnapshot(selectedAttributesSnapshot);
      newItem.setSelectedAddonsSnapshot(selectedAddonsSnapshot);
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
        BigDecimal addonAmount = ProductAddonUtils.resolveAddonAmount(
            product == null ? null : product.getUpsells(),
            extractAddonCodes(item.getSelectedAddonsSnapshot()),
            lang);
        vo.setProductPic(sku.getPic());
        vo.setUnitPrice(sku.getPrice());
        vo.setAddonAmount(addonAmount.multiply(BigDecimal.valueOf(item.getQuantity())));
        vo.setLineAmount(sku.getPrice().add(addonAmount).multiply(BigDecimal.valueOf(item.getQuantity())));
        vo.setStock(sku.getStock());
        vo.setAttributes(item.getSelectedAttributesSnapshot() != null
            ? item.getSelectedAttributesSnapshot()
            : SkuVO.buildSnapshot(sku.getSpecs(), lang));
        vo.setAddons(item.getSelectedAddonsSnapshot());
      }
      if (product != null) {
        vo.setTitle(ProductVO.extractLang(product.getName(), lang));
        vo.setSlug(product.getSlug());
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
    if (dto.getAddonCodes() != null) {
      PmsProduct product = productService.getById(cartItem.getProductId());
      cartItem.setSelectedAddonsSnapshot(objectMapper.valueToTree(
          ProductAddonUtils.resolveSelectedAddons(product == null ? null : product.getUpsells(), dto.getAddonCodes(),
              LanguageContext.getLanguage())));
    }
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

  private List<String> extractAddonCodes(com.fasterxml.jackson.databind.JsonNode addonsSnapshot) {
    if (addonsSnapshot == null || !addonsSnapshot.isArray()) {
      return List.of();
    }
    return java.util.stream.StreamSupport.stream(addonsSnapshot.spliterator(), false)
        .map(item -> item.path("code").asText())
        .filter(code -> code != null && !code.isBlank())
        .collect(Collectors.toList());
  }

  private String normalizeJson(com.fasterxml.jackson.databind.JsonNode node) {
    return node == null || node.isNull() ? "" : node.toString();
  }
}
