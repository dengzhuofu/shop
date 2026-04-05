package com.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

import java.util.List;
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
      @RequestParam(required = false) String sort) {
    Page<PmsProduct> page = new Page<>(pageNum, pageSize);
    QueryWrapper<PmsProduct> wrapper = new QueryWrapper<>();
    wrapper.eq("published", true);
    if (categorySlug != null && !categorySlug.isBlank() && !"all".equalsIgnoreCase(categorySlug)) {
      PmsCategory category = categoryService.getOne(new QueryWrapper<PmsCategory>().eq("slug", categorySlug));
      if (category == null) {
        return Result.success(new Page<>(pageNum, pageSize));
      }
      wrapper.eq("category_id", category.getId());
    }

    if ("price-ascending".equalsIgnoreCase(sort)) {
      wrapper.orderByAsc("price");
    } else if ("price-descending".equalsIgnoreCase(sort)) {
      wrapper.orderByDesc("price");
    } else {
      wrapper.orderByAsc("sort_order").orderByDesc("id");
    }

    Page<PmsProduct> productPage = productService.page(page, wrapper);

    Page<ProductVO> voPage = new Page<>(pageNum, pageSize);
    voPage.setTotal(productPage.getTotal());
    voPage.setPages(productPage.getPages());
    voPage.setCurrent(productPage.getCurrent());
    voPage.setSize(productPage.getSize());

    List<ProductVO> voList = productPage.getRecords().stream()
        .map(this::toListItem)
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
    ProductVO vo = buildProductVO(product, skuList);
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
    ProductVO vo = buildProductVO(product, skuList);
    return Result.success(vo);
  }

  private ProductVO toListItem(PmsProduct product) {
    ProductVO vo = ProductVO.from(product, null);
    PmsCategory category = categoryService.getById(product.getCategoryId());
    if (category != null) {
      vo.setCategorySlug(category.getSlug());
    }
    return vo;
  }

  private ProductVO buildProductVO(PmsProduct product, List<PmsSku> skuList) {
    ProductVO vo = ProductVO.from(product, skuList);
    PmsCategory category = categoryService.getById(product.getCategoryId());
    if (category != null) {
      vo.setCategorySlug(category.getSlug());
    }
    return vo;
  }
}
