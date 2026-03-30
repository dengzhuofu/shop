package com.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shop.dto.OrderCreateDTO;
import com.shop.entity.OmsCartItem;
import com.shop.entity.OmsOrder;
import com.shop.entity.OmsOrderItem;
import com.shop.entity.PmsSku;
import com.shop.mapper.OmsOrderMapper;
import com.shop.service.OmsCartItemService;
import com.shop.service.OmsOrderItemService;
import com.shop.service.OmsOrderService;
import com.shop.service.PmsSkuService;
import com.shop.vo.OrderItemVO;
import com.shop.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OmsOrderServiceImpl extends ServiceImpl<OmsOrderMapper, OmsOrder> implements OmsOrderService {

  private final OmsCartItemService cartItemService;
  private final OmsOrderItemService orderItemService;
  private final PmsSkuService skuService;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public OmsOrder createOrder(OrderCreateDTO dto, Long userId) {
    List<OmsCartItem> cartItems = cartItemService.listByIds(dto.getCartItemIds());
    if (cartItems.isEmpty()) {
      throw new RuntimeException("Cart items not found");
    }

    BigDecimal totalAmount = BigDecimal.ZERO;
    OmsOrder order = new OmsOrder();
    order.setUserId(userId);
    String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    order.setOrderSn("ORD" + dateStr + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
    order.setStatus(0);
    order.setCreateTime(LocalDateTime.now());
    order.setUpdateTime(LocalDateTime.now());

    for (OmsCartItem item : cartItems) {
      PmsSku sku = skuService.getById(item.getSkuId());
      if (sku == null || sku.getStock() < item.getQuantity()) {
        throw new RuntimeException("Insufficient stock for SKU: " + item.getSkuId());
      }
      totalAmount = totalAmount.add(sku.getPrice().multiply(new BigDecimal(item.getQuantity())));

      // Deduct stock
      sku.setStock(sku.getStock() - item.getQuantity());
      skuService.updateById(sku);
    }

    order.setTotalAmount(totalAmount);
    this.save(order);

    for (OmsCartItem item : cartItems) {
      PmsSku sku = skuService.getById(item.getSkuId());
      OmsOrderItem orderItem = new OmsOrderItem();
      orderItem.setOrderId(order.getId());
      orderItem.setProductId(item.getProductId());
      orderItem.setSkuId(item.getSkuId());
      // For real app, product name and pic should be fetched from PmsProduct
      orderItem.setProductName("Product " + item.getProductId()); 
      orderItem.setProductPic(sku.getPic());
      orderItem.setSkuCode(sku.getSkuCode());
      orderItem.setQuantity(item.getQuantity());
      orderItem.setPrice(sku.getPrice());
      orderItem.setCreateTime(LocalDateTime.now());
      orderItem.setUpdateTime(LocalDateTime.now());
      orderItemService.save(orderItem);
    }

    // Remove from cart
    cartItemService.removeByIds(dto.getCartItemIds());

    return order;
  }

  @Override
  public Page<OrderVO> getUserOrders(Long userId, Integer status, Integer pageNum, Integer pageSize) {
    Page<OmsOrder> page = new Page<>(pageNum, pageSize);
    QueryWrapper<OmsOrder> wrapper = new QueryWrapper<>();
    wrapper.eq("user_id", userId);
    if (status != null && status >= 0) {
      wrapper.eq("status", status);
    }
    wrapper.orderByDesc("create_time");

    Page<OmsOrder> orderPage = this.page(page, wrapper);

    Page<OrderVO> voPage = new Page<>(pageNum, pageSize);
    voPage.setTotal(orderPage.getTotal());

    List<OrderVO> voList = orderPage.getRecords().stream().map(order -> {
      OrderVO vo = new OrderVO();
      BeanUtils.copyProperties(order, vo);

      List<OmsOrderItem> items = orderItemService.list(new QueryWrapper<OmsOrderItem>().eq("order_id", order.getId()));
      List<OrderItemVO> itemVOs = items.stream().map(item -> {
        OrderItemVO itemVO = new OrderItemVO();
        BeanUtils.copyProperties(item, itemVO);
        return itemVO;
      }).collect(Collectors.toList());

      vo.setItems(itemVOs);
      return vo;
    }).collect(Collectors.toList());

    voPage.setRecords(voList);
    return voPage;
  }

  @Override
  public OrderVO getOrderDetail(Long orderId, Long userId) {
    OmsOrder order = this.getById(orderId);
    if (order == null || !order.getUserId().equals(userId)) {
      return null;
    }

    OrderVO vo = new OrderVO();
    BeanUtils.copyProperties(order, vo);

    List<OmsOrderItem> items = orderItemService.list(new QueryWrapper<OmsOrderItem>().eq("order_id", order.getId()));
    List<OrderItemVO> itemVOs = items.stream().map(item -> {
      OrderItemVO itemVO = new OrderItemVO();
      BeanUtils.copyProperties(item, itemVO);
      return itemVO;
    }).collect(Collectors.toList());

    vo.setItems(itemVOs);
    return vo;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean cancelOrder(Long orderId, Long userId) {
    OmsOrder order = this.getById(orderId);
    if (order == null || !order.getUserId().equals(userId)) {
      return false;
    }
    if (order.getStatus() != 0) {
      return false; // Only pending payment can be cancelled
    }
    order.setStatus(4); // Cancelled
    order.setUpdateTime(LocalDateTime.now());
    
    // Add stock back
    List<OmsOrderItem> items = orderItemService.list(new QueryWrapper<OmsOrderItem>().eq("order_id", order.getId()));
    for (OmsOrderItem item : items) {
      PmsSku sku = skuService.getById(item.getSkuId());
      if (sku != null) {
        sku.setStock(sku.getStock() + item.getQuantity());
        skuService.updateById(sku);
      }
    }
    
    return this.updateById(order);
  }
}
