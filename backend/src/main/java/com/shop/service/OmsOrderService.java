package com.shop.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shop.dto.OrderCreateDTO;
import com.shop.entity.OmsOrder;
import com.shop.vo.OrderVO;

public interface OmsOrderService extends IService<OmsOrder> {

  OmsOrder createOrder(OrderCreateDTO dto, Long userId);

  Page<OrderVO> getUserOrders(Long userId, Integer status, Integer pageNum, Integer pageSize);

  OrderVO getOrderDetail(Long orderId, Long userId);

  boolean cancelOrder(Long orderId, Long userId);
}
