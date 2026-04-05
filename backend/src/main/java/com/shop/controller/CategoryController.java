package com.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shop.common.Result;
import com.shop.entity.PmsCategory;
import com.shop.entity.PmsProduct;
import com.shop.service.PmsCategoryService;
import com.shop.service.PmsProductService;
import com.shop.vo.CategoryVO;
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
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

  private final PmsCategoryService categoryService;
  private final PmsProductService productService;

  @GetMapping("/tree")
  public Result<List<CategoryVO>> tree() {
    List<PmsCategory> categories = categoryService.list(new QueryWrapper<PmsCategory>()
        .eq("published", true)
        .orderByAsc("sort_order")
        .orderByAsc("id"));

    List<CategoryVO> result = categories.stream()
        .map(category -> CategoryVO.from(category, productService.count(new QueryWrapper<PmsProduct>()
            .eq("category_id", category.getId())
            .eq("published", true))))
        .collect(Collectors.toList());
    return Result.success(result);
  }

  @GetMapping("/{slug}")
  public Result<CategoryVO> detail(@PathVariable String slug) {
    PmsCategory category = categoryService.getOne(new QueryWrapper<PmsCategory>()
        .eq("slug", slug)
        .eq("published", true));
    if (category == null) {
      return Result.error(404, "Category not found");
    }
    Long productCount = productService.count(new QueryWrapper<PmsProduct>()
        .eq("category_id", category.getId())
        .eq("published", true));
    return Result.success(CategoryVO.from(category, productCount));
  }

  @GetMapping("/{slug}/products")
  public Result<Page<ProductVO>> products(@PathVariable String slug,
      @RequestParam(defaultValue = "1") Integer pageNum,
      @RequestParam(defaultValue = "12") Integer pageSize,
      @RequestParam(required = false) String sort) {
    PmsCategory category = categoryService.getOne(new QueryWrapper<PmsCategory>()
        .eq("slug", slug)
        .eq("published", true));
    if (category == null) {
      return Result.error(404, "Category not found");
    }

    QueryWrapper<PmsProduct> wrapper = new QueryWrapper<PmsProduct>()
        .eq("category_id", category.getId())
        .eq("published", true);
    if ("price-ascending".equalsIgnoreCase(sort)) {
      wrapper.orderByAsc("price");
    } else if ("price-descending".equalsIgnoreCase(sort)) {
      wrapper.orderByDesc("price");
    } else {
      wrapper.orderByAsc("sort_order").orderByDesc("id");
    }

    Page<PmsProduct> page = productService.page(new Page<>(pageNum, pageSize), wrapper);
    Page<ProductVO> voPage = new Page<>(pageNum, pageSize);
    voPage.setTotal(page.getTotal());
    voPage.setPages(page.getPages());
    voPage.setCurrent(page.getCurrent());
    voPage.setSize(page.getSize());
    voPage.setRecords(page.getRecords().stream().map(product -> {
      ProductVO vo = ProductVO.from(product, null);
      vo.setCategorySlug(category.getSlug());
      return vo;
    }).collect(Collectors.toList()));
    return Result.success(voPage);
  }
}
