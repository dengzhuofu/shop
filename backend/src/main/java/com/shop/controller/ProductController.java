package com.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shop.common.Result;
import com.shop.entity.PmsProduct;
import com.shop.entity.PmsSku;
import com.shop.service.PmsProductService;
import com.shop.service.PmsSkuService;
import com.shop.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

  private final PmsProductService productService;
  private final PmsSkuService skuService;

  @GetMapping("/list")
  public Result<Page<ProductVO>> list(@RequestParam(defaultValue = "1") Integer pageNum,
      @RequestParam(defaultValue = "10") Integer pageSize) {
    Page<PmsProduct> page = new Page<>(pageNum, pageSize);
    Page<PmsProduct> productPage = productService.page(page);

    Page<ProductVO> voPage = new Page<>(pageNum, pageSize);
    voPage.setTotal(productPage.getTotal());

    List<ProductVO> voList = productPage.getRecords().stream()
        .map(p -> ProductVO.from(p, null))
        .collect(Collectors.toList());
    voPage.setRecords(voList);

    return Result.success(voPage);
  }

  @GetMapping("/{id}")
  public Result<ProductVO> detail(@PathVariable Long id) {
    PmsProduct product = productService.getById(id);
    if (product == null) {
      return Result.error(404, "Product not found");
    }

    List<PmsSku> skuList = skuService.list(new QueryWrapper<PmsSku>().eq("product_id", id));

    ProductVO vo = ProductVO.from(product, skuList);

    return Result.success(vo);
  }
}
