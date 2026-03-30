package com.shop.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shop.common.Result;
import com.shop.dto.OrderCreateDTO;
import com.shop.entity.OmsOrder;
import com.shop.service.OmsOrderService;
import com.shop.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

  private final OmsOrderService orderService;

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

  @GetMapping("/list")
  public Result<Page<OrderVO>> list(
      @RequestParam(defaultValue = "1") Integer pageNum,
      @RequestParam(defaultValue = "10") Integer pageSize,
      @RequestParam(required = false) Integer status) {
    // 假设未接入真正认证时用 1L 作为 Mock 用户 ID 测试
    Long userId = 1L;
    try {
      userId = StpUtil.getLoginIdAsLong();
    } catch (Exception e) {
      // Mock for UI testing
    }
    
    Page<OrderVO> page = orderService.getUserOrders(userId, status, pageNum, pageSize);
    return Result.success(page);
  }

  @GetMapping("/{id}")
  public Result<OrderVO> detail(@PathVariable Long id) {
    Long userId = 1L;
    try {
      userId = StpUtil.getLoginIdAsLong();
    } catch (Exception e) {
      // Mock for UI testing
    }

    OrderVO orderVO = orderService.getOrderDetail(id, userId);
    if (orderVO == null) {
      return Result.error(404, "Order not found");
    }
    return Result.success(orderVO);
  }

  @PutMapping("/{id}/cancel")
  public Result<Void> cancel(@PathVariable Long id) {
    Long userId = 1L;
    try {
      userId = StpUtil.getLoginIdAsLong();
    } catch (Exception e) {
      // Mock for UI testing
    }

    boolean success = orderService.cancelOrder(id, userId);
    if (success) {
      return Result.success(null);
    } else {
      return Result.error(400, "Failed to cancel order");
    }
  }
}
