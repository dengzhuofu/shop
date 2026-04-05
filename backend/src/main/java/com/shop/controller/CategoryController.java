package com.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shop.common.Result;
import com.shop.entity.PmsCategory;
import com.shop.entity.PmsProduct;
import com.shop.service.PmsCategoryService;
import com.shop.service.PmsProductService;
import com.shop.vo.CategoryMenuBannerVO;
import com.shop.vo.CategoryMenuGroupVO;
import com.shop.vo.CategoryMenuVO;
import com.shop.vo.CategoryVO;
import com.shop.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

  private final PmsCategoryService categoryService;
  private final PmsProductService productService;

  @GetMapping("/tree")
  public Result<List<CategoryVO>> tree() {
    List<PmsCategory> categories = publishedCategories();
    Map<Long, List<PmsCategory>> childrenByParent = buildChildrenByParent(categories);

    List<CategoryVO> result = categories.stream()
        .filter(this::isRootCategory)
        .map(category -> CategoryVO.from(category, countProductsForCategoryTree(category, childrenByParent)))
        .collect(Collectors.toList());
    return Result.success(result);
  }

  @GetMapping("/menu")
  public Result<List<CategoryMenuVO>> menu(@RequestParam(defaultValue = "4") Integer productLimit) {
    List<PmsCategory> categories = publishedCategories();
    Map<Long, List<PmsCategory>> childrenByParent = buildChildrenByParent(categories);
    Map<Long, PmsCategory> categoryById = categories.stream()
        .collect(Collectors.toMap(PmsCategory::getId, category -> category, (left, right) -> left, LinkedHashMap::new));

    int safeLimit = Math.max(1, Math.min(productLimit, 8));
    List<CategoryMenuVO> result = categories.stream()
        .filter(this::isRootCategory)
        .map(root -> toMenuVO(root, childrenByParent, categoryById, safeLimit))
        .collect(Collectors.toList());
    return Result.success(result);
  }

  @GetMapping("/{slug}")
  public Result<CategoryVO> detail(@PathVariable String slug) {
    List<PmsCategory> categories = publishedCategories();
    Map<Long, List<PmsCategory>> childrenByParent = buildChildrenByParent(categories);
    PmsCategory category = categories.stream()
        .filter(item -> slug.equals(item.getSlug()))
        .findFirst()
        .orElse(null);
    if (category == null) {
      return Result.error(404, "Category not found");
    }
    return Result.success(CategoryVO.from(category, countProductsForCategoryTree(category, childrenByParent)));
  }

  @GetMapping("/{slug}/products")
  public Result<Page<ProductVO>> products(@PathVariable String slug,
      @RequestParam(defaultValue = "1") Integer pageNum,
      @RequestParam(defaultValue = "12") Integer pageSize,
      @RequestParam(required = false) String sort) {
    List<PmsCategory> categories = publishedCategories();
    Map<Long, List<PmsCategory>> childrenByParent = buildChildrenByParent(categories);
    Map<Long, PmsCategory> categoryById = categories.stream()
        .collect(Collectors.toMap(PmsCategory::getId, category -> category));
    PmsCategory category = categories.stream()
        .filter(item -> slug.equals(item.getSlug()))
        .findFirst()
        .orElse(null);
    if (category == null) {
      return Result.error(404, "Category not found");
    }

    List<Long> categoryIds = collectDescendantIds(category.getId(), childrenByParent);
    QueryWrapper<PmsProduct> wrapper = new QueryWrapper<PmsProduct>()
        .in("category_id", categoryIds)
        .eq("published", true)
        .orderByAsc("sort_order")
        .orderByAsc("id");
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
      vo.setCategorySlug(resolveRootSlug(product.getCategoryId(), categoryById));
      return vo;
    }).collect(Collectors.toList()));
    return Result.success(voPage);
  }

  private List<PmsCategory> publishedCategories() {
    return categoryService.list(new QueryWrapper<PmsCategory>()
        .eq("published", true)
        .orderByAsc("sort_order")
        .orderByAsc("id"));
  }

  private boolean isRootCategory(PmsCategory category) {
    return category.getParentId() == null;
  }

  private Map<Long, List<PmsCategory>> buildChildrenByParent(List<PmsCategory> categories) {
    Map<Long, List<PmsCategory>> result = new LinkedHashMap<>();
    for (PmsCategory category : categories) {
      if (category.getParentId() == null) {
        continue;
      }
      result.computeIfAbsent(category.getParentId(), key -> new ArrayList<>()).add(category);
    }
    return result;
  }

  private List<Long> collectDescendantIds(Long categoryId, Map<Long, List<PmsCategory>> childrenByParent) {
    List<Long> ids = new ArrayList<>();
    ids.add(categoryId);
    for (PmsCategory child : childrenByParent.getOrDefault(categoryId, List.of())) {
      ids.addAll(collectDescendantIds(child.getId(), childrenByParent));
    }
    return ids;
  }

  private long countProductsForCategoryTree(PmsCategory category, Map<Long, List<PmsCategory>> childrenByParent) {
    return productService.count(new QueryWrapper<PmsProduct>()
        .in("category_id", collectDescendantIds(category.getId(), childrenByParent))
        .eq("published", true));
  }

  private CategoryMenuVO toMenuVO(PmsCategory root, Map<Long, List<PmsCategory>> childrenByParent,
      Map<Long, PmsCategory> categoryById, int productLimit) {
    List<PmsCategory> menuGroups = childrenByParent.getOrDefault(root.getId(), List.of());
    if (menuGroups.isEmpty()) {
      menuGroups = List.of(root);
    }

    CategoryMenuVO vo = new CategoryMenuVO();
    vo.setId(root.getId());
    vo.setSlug(root.getSlug());
    vo.setName(CategoryVO.from(root, 0L).getName());
    vo.setDescription(CategoryVO.from(root, 0L).getDescription());
    vo.setHeroImage(root.getHeroImage());
    vo.setMenuImage(root.getMenuImage());
    vo.setProductCount(countProductsForCategoryTree(root, childrenByParent));
    vo.setBanner(buildMenuBanner(root, vo.getProductCount()));
    vo.setChildren(menuGroups.stream()
        .map(group -> toMenuGroupVO(group, root, childrenByParent, categoryById, productLimit))
        .collect(Collectors.toList()));
    return vo;
  }

  private CategoryMenuGroupVO toMenuGroupVO(PmsCategory group, PmsCategory root,
      Map<Long, List<PmsCategory>> childrenByParent, Map<Long, PmsCategory> categoryById, int productLimit) {
    List<Long> categoryIds = collectDescendantIds(group.getId(), childrenByParent);
    List<ProductVO> products = productService.list(new QueryWrapper<PmsProduct>()
            .in("category_id", categoryIds)
            .eq("published", true)
            .orderByAsc("sort_order")
            .orderByDesc("id")
            .last("LIMIT " + productLimit))
        .stream()
        .map(product -> {
          ProductVO vo = ProductVO.from(product, null);
          vo.setCategorySlug(resolveRootSlug(product.getCategoryId(), categoryById));
          return vo;
        })
        .collect(Collectors.toList());

    CategoryMenuGroupVO groupVO = new CategoryMenuGroupVO();
    groupVO.setId(group.getId());
    groupVO.setSlug(group.getSlug());
    groupVO.setName(CategoryVO.from(group, 0L).getName());
    groupVO.setDescription(CategoryVO.from(group, 0L).getDescription());
    groupVO.setProductCount(productService.count(new QueryWrapper<PmsProduct>()
        .in("category_id", categoryIds)
        .eq("published", true)));
    groupVO.setAllLinkUrl("/collections/" + root.getSlug());
    groupVO.setAllLinkText("All " + groupVO.getName());
    groupVO.setProducts(products);
    return groupVO;
  }

  private CategoryMenuBannerVO buildMenuBanner(PmsCategory root, Long productCount) {
    CategoryMenuBannerVO banner = new CategoryMenuBannerVO();
    banner.setTag("Save $20");
    banner.setTrustpilot("4.4");
    banner.setLinkUrl("/collections/" + root.getSlug());
    banner.setLinkText("All " + CategoryVO.from(root, productCount).getName() + " (" + productCount + ")");
    return banner;
  }

  private String resolveRootSlug(Long categoryId, Map<Long, PmsCategory> categoryById) {
    PmsCategory current = categoryById.get(categoryId);
    while (current != null && current.getParentId() != null) {
      current = categoryById.get(current.getParentId());
    }
    return current == null ? null : current.getSlug();
  }
}
