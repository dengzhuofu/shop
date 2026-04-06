package com.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shop.common.ProductOptionUtils;
import com.shop.common.Result;
import com.shop.entity.PmsCategory;
import com.shop.entity.PmsProduct;
import com.shop.entity.PmsSku;
import com.shop.service.PmsCategoryService;
import com.shop.service.PmsProductService;
import com.shop.service.PmsSkuService;
import com.shop.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

  private final PmsProductService productService;
  private final PmsSkuService skuService;
  private final PmsCategoryService categoryService;

  @GetMapping("/list")
  public Result<Page<ProductVO>> list(@RequestParam(defaultValue = "1") Integer pageNum,
      @RequestParam(defaultValue = "12") Integer pageSize,
      @RequestParam(required = false) String categorySlug,
      @RequestParam(required = false) String sort,
      @RequestParam(required = false) String stock,
      @RequestParam(required = false) BigDecimal minPrice,
      @RequestParam(required = false) BigDecimal maxPrice) {
    Page<PmsProduct> page = new Page<>(pageNum, pageSize);
    QueryWrapper<PmsProduct> wrapper = new QueryWrapper<>();
    wrapper.eq("published", true);
    List<PmsCategory> categories = categoryService.list(new QueryWrapper<PmsCategory>()
        .eq("published", true)
        .orderByAsc("sort_order")
        .orderByAsc("id"));
    Map<Long, PmsCategory> categoryById = categories.stream()
        .collect(Collectors.toMap(PmsCategory::getId, category -> category));

    if (categorySlug != null && !categorySlug.isBlank() && !"all".equalsIgnoreCase(categorySlug)) {
      PmsCategory category = categories.stream()
          .filter(item -> categorySlug.equals(item.getSlug()))
          .findFirst()
          .orElse(null);
      if (category == null) {
        return Result.success(new Page<>(pageNum, pageSize));
      }
      wrapper.in("category_id", collectDescendantIds(category.getId(), categories));
    }

    if ("in-stock".equalsIgnoreCase(stock)) {
      wrapper.gt("stock", 0);
    } else if ("out-of-stock".equalsIgnoreCase(stock)) {
      wrapper.le("stock", 0);
    }
    if (minPrice != null) {
      wrapper.ge("price", minPrice);
    }
    if (maxPrice != null) {
      wrapper.le("price", maxPrice);
    }
    applyProductSort(wrapper, sort);

    Page<PmsProduct> productPage = productService.page(page, wrapper);

    Page<ProductVO> voPage = new Page<>(pageNum, pageSize);
    voPage.setTotal(productPage.getTotal());
    voPage.setPages(productPage.getPages());
    voPage.setCurrent(productPage.getCurrent());
    voPage.setSize(productPage.getSize());
    Map<Long, Boolean> hasOptionsByProductId = loadHasOptionsByProductIds(productPage.getRecords().stream()
        .map(PmsProduct::getId)
        .collect(Collectors.toList()));

    List<ProductVO> voList = productPage.getRecords().stream()
        .map(product -> toListItem(product, categoryById, hasOptionsByProductId.getOrDefault(product.getId(), false)))
        .collect(Collectors.toList());
    voPage.setRecords(voList);

    return Result.success(voPage);
  }

  @GetMapping("/{id}")
  public Result<ProductVO> detail(@PathVariable Long id) {
    PmsProduct product = productService.getById(id);
    if (product == null || !Boolean.TRUE.equals(product.getPublished())) {
      return Result.error(404, "Product not found");
    }

    List<PmsSku> skuList = skuService.list(new QueryWrapper<PmsSku>().eq("product_id", id));
    ProductVO vo = buildProductVO(product, skuList, publishedCategoryMap());
    return Result.success(vo);
  }

  @GetMapping("/slug/{slug}")
  public Result<ProductVO> detailBySlug(@PathVariable String slug) {
    PmsProduct product = productService.getOne(new QueryWrapper<PmsProduct>()
        .eq("slug", slug)
        .eq("published", true));
    if (product == null) {
      return Result.error(404, "Product not found");
    }

    List<PmsSku> skuList = skuService.list(new QueryWrapper<PmsSku>().eq("product_id", product.getId()));
    ProductVO vo = buildProductVO(product, skuList, publishedCategoryMap());
    return Result.success(vo);
  }

  private ProductVO toListItem(PmsProduct product, Map<Long, PmsCategory> categoryById, boolean hasOptions) {
    ProductVO vo = ProductVO.from(product, null);
    vo.setCategorySlug(resolveRootSlug(product.getCategoryId(), categoryById));
    vo.setHasOptions(hasOptions);
    return vo;
  }

  private ProductVO buildProductVO(PmsProduct product, List<PmsSku> skuList, Map<Long, PmsCategory> categoryById) {
    ProductVO vo = ProductVO.from(product, skuList);
    vo.setCategorySlug(resolveRootSlug(product.getCategoryId(), categoryById));
    vo.setHasOptions(ProductOptionUtils.hasSelectableOptions(skuList));
    return vo;
  }

  private Map<Long, Boolean> loadHasOptionsByProductIds(List<Long> productIds) {
    if (productIds == null || productIds.isEmpty()) {
      return Collections.emptyMap();
    }

    return ProductOptionUtils.hasSelectableOptionsByProductId(skuService.list(new QueryWrapper<PmsSku>()
            .in("product_id", productIds)
            .orderByAsc("product_id")
            .orderByAsc("id")));
  }

  private Map<Long, PmsCategory> publishedCategoryMap() {
    return categoryService.list(new QueryWrapper<PmsCategory>()
            .eq("published", true)
            .orderByAsc("sort_order")
            .orderByAsc("id"))
        .stream()
        .collect(Collectors.toMap(PmsCategory::getId, category -> category));
  }

  private List<Long> collectDescendantIds(Long categoryId, List<PmsCategory> categories) {
    List<Long> ids = List.of(categoryId);
    List<Long> childIds = categories.stream()
        .filter(category -> categoryId.equals(category.getParentId()))
        .map(PmsCategory::getId)
        .toList();
    if (childIds.isEmpty()) {
      return ids;
    }
    return childIds.stream()
        .flatMap(childId -> collectDescendantIds(childId, categories).stream())
        .collect(Collectors.collectingAndThen(Collectors.toList(), collected -> {
          List<Long> result = new java.util.ArrayList<>();
          result.add(categoryId);
          result.addAll(collected);
          return result;
        }));
  }

  private String resolveRootSlug(Long categoryId, Map<Long, PmsCategory> categoryById) {
    PmsCategory current = categoryById.get(categoryId);
    while (current != null && current.getParentId() != null) {
      current = categoryById.get(current.getParentId());
    }
    return current == null ? null : current.getSlug();
  }

  private void applyProductSort(QueryWrapper<PmsProduct> wrapper, String sort) {
    if ("best-selling".equalsIgnoreCase(sort)) {
      wrapper.orderByDesc("stock").orderByAsc("sort_order").orderByDesc("id");
      return;
    }
    if ("title-ascending".equalsIgnoreCase(sort)) {
      wrapper.orderByAsc("slug").orderByAsc("id");
      return;
    }
    if ("title-descending".equalsIgnoreCase(sort)) {
      wrapper.orderByDesc("slug").orderByDesc("id");
      return;
    }
    if ("price-ascending".equalsIgnoreCase(sort)) {
      wrapper.orderByAsc("price").orderByAsc("sort_order").orderByDesc("id");
      return;
    }
    if ("price-descending".equalsIgnoreCase(sort)) {
      wrapper.orderByDesc("price").orderByAsc("sort_order").orderByDesc("id");
      return;
    }
    if ("created-ascending".equalsIgnoreCase(sort)) {
      wrapper.orderByAsc("create_time").orderByAsc("id");
      return;
    }
    if ("created-descending".equalsIgnoreCase(sort)) {
      wrapper.orderByDesc("create_time").orderByDesc("id");
      return;
    }
    wrapper.orderByAsc("sort_order").orderByDesc("id");
  }
}
