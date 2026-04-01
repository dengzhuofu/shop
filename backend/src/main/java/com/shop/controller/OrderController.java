package com.shop.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shop.common.Result;
import com.shop.dto.OrderCreateDTO;
import com.shop.dto.OrderPayDTO;
import com.shop.dto.OrderPreviewDTO;
import com.shop.entity.OmsOrder;
import com.shop.service.OmsOrderService;
import com.shop.vo.OrderPreviewVO;
import com.shop.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

  private final OmsOrderService orderService;

  @PostMapping("/preview")
  public Result<OrderPreviewVO> preview(@RequestBody OrderPreviewDTO dto) {
    Long userId = StpUtil.getLoginIdAsLong();
    try {
      OrderPreviewVO previewVO = orderService.previewOrder(dto, userId);
      return Result.success(previewVO);
    } catch (Exception e) {
      return Result.error(500, e.getMessage());
    }
  }

  @PostMapping("/create")
  public Result<OmsOrder> create(@RequestBody OrderCreateDTO dto) {
    Long userId = StpUtil.getLoginIdAsLong();

    try {
      OmsOrder order = orderService.createOrder(dto, userId);
      return Result.success(order);
    } catch (Exception e) {
      return Result.error(500, e.getMessage());
    }
  }

  @PostMapping("/pay")
  public Result<OmsOrder> pay(@RequestBody OrderPayDTO dto) {
    Long userId = StpUtil.getLoginIdAsLong();
    try {
      OmsOrder order = orderService.payOrder(dto, userId);
      return Result.success(order);
    } catch (Exception e) {
      return Result.error(500, e.getMessage());
    }
  }

  @GetMapping("/list")
  public Result<Page<OrderVO>> list(
      @RequestParam(defaultValue = "1") Integer pageNum,
      @RequestParam(defaultValue = "10") Integer pageSize,
      @RequestParam(required = false) Integer status) {
    Long userId = StpUtil.getLoginIdAsLong();
    
    Page<OrderVO> page = orderService.getUserOrders(userId, status, pageNum, pageSize);
    return Result.success(page);
  }

  @GetMapping("/{id}")
  public Result<OrderVO> detail(@PathVariable Long id) {
    Long userId = StpUtil.getLoginIdAsLong();

    OrderVO orderVO = orderService.getOrderDetail(id, userId);
    if (orderVO == null) {
      return Result.error(404, "Order not found");
    }
    return Result.success(orderVO);
  }

  @PutMapping("/{id}/cancel")
  public Result<Void> cancel(@PathVariable Long id) {
    Long userId = StpUtil.getLoginIdAsLong();

    boolean success = orderService.cancelOrder(id, userId);
    if (success) {
      return Result.success(null);
    } else {
      return Result.error(400, "Failed to cancel order");
    }
  }
}
