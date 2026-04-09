package com.shop.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shop.dto.OrderCreateDTO;
import com.shop.dto.OrderPreviewDTO;
import com.shop.entity.OmsOrder;
import com.shop.vo.OrderPreviewVO;
import com.shop.vo.OrderVO;

public interface OmsOrderService extends IService<OmsOrder> {

  OmsOrder createOrder(OrderCreateDTO dto, Long userId);
  OrderPreviewVO previewOrder(OrderPreviewDTO dto, Long userId);

  Page<OrderVO> getUserOrders(Long userId, String status, Integer pageNum, Integer pageSize);

  OrderVO getOrderDetail(Long orderId, Long userId);

  boolean cancelOrder(Long orderId, Long userId);

  boolean expireOrderIfNeeded(OmsOrder order);

  int closeExpiredOrders(int batchSize);
}
