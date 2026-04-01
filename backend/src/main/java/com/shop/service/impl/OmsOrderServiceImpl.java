package com.shop.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shop.common.LanguageContext;
import com.shop.dto.OrderCreateDTO;
import com.shop.dto.OrderPayDTO;
import com.shop.dto.OrderPreviewDTO;
import com.shop.entity.OmsCartItem;
import com.shop.entity.OmsOrder;
import com.shop.entity.OmsOrderItem;
import com.shop.entity.PmsProduct;
import com.shop.entity.PmsSku;
import com.shop.entity.UmsUserAddress;
import com.shop.mapper.OmsOrderMapper;
import com.shop.service.OmsCartItemService;
import com.shop.service.OmsOrderItemService;
import com.shop.service.OmsOrderService;
import com.shop.service.PmsProductService;
import com.shop.service.PmsSkuService;
import com.shop.service.UmsUserAddressService;
import com.shop.vo.CartItemVO;
import com.shop.vo.OrderPreviewVO;
import com.shop.vo.OrderItemVO;
import com.shop.vo.OrderVO;
import com.shop.vo.ProductVO;
import com.shop.vo.SkuVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OmsOrderServiceImpl extends ServiceImpl<OmsOrderMapper, OmsOrder> implements OmsOrderService {

  private final OmsCartItemService cartItemService;
  private final OmsOrderItemService orderItemService;
  private final PmsSkuService skuService;
  private final PmsProductService productService;
  private final UmsUserAddressService userAddressService;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public OrderPreviewVO previewOrder(OrderPreviewDTO dto, Long userId) {
    List<CartItemVO> items = resolvePreviewItems(dto, userId);
    BigDecimal subtotal = items.stream()
        .map(CartItemVO::getLineAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    OrderPreviewVO previewVO = new OrderPreviewVO();
    previewVO.setItems(items);
    previewVO.setSubtotal(subtotal);
    previewVO.setShippingAmount(BigDecimal.ZERO);
    previewVO.setDiscountAmount(BigDecimal.ZERO);
    previewVO.setTotalAmount(subtotal);
    return previewVO;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public OmsOrder createOrder(OrderCreateDTO dto, Long userId) {
    String lang = LanguageContext.getLanguage();
    List<OrderCreateDTO.Item> items = resolveCreateItems(dto, userId);
    if (items.isEmpty()) {
      throw new RuntimeException("Order items not found");
    }
    OmsOrder order = new OmsOrder();
    order.setUserId(userId);
    String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    order.setOrderSn("ORD" + dateStr + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
    order.setStatus(0);
    order.setPayStatus(0);
    order.setCheckoutSource(dto.getSource() == null ? "cart" : dto.getSource());
    order.setPaymentMethod(dto.getPaymentMethod());
    order.setShippingMethod(dto.getShippingMethod());
    order.setShippingAmount(BigDecimal.ZERO);
    order.setDiscountAmount(dto.getDiscountAmount() == null ? BigDecimal.ZERO : dto.getDiscountAmount());
    order.setCreateTime(LocalDateTime.now());
    order.setUpdateTime(LocalDateTime.now());

    OrderCreateDTO.AddressSnapshot addressSnapshot = dto.getAddressSnapshot();
    if (dto.getAddressId() != null) {
      UmsUserAddress address = userAddressService.getById(dto.getAddressId());
      if (address != null && address.getUserId().equals(userId)) {
        addressSnapshot = new OrderCreateDTO.AddressSnapshot();
        addressSnapshot.setCountry(address.getCountry());
        addressSnapshot.setFirstName(address.getFirstName());
        addressSnapshot.setLastName(address.getLastName());
        addressSnapshot.setPhone(address.getPhone());
        addressSnapshot.setAddressLine1(address.getAddressLine1());
        addressSnapshot.setAddressLine2(address.getAddressLine2());
        addressSnapshot.setCity(address.getCity());
        addressSnapshot.setState(address.getState());
        addressSnapshot.setZipCode(address.getZipCode());
      }
    }
    if (addressSnapshot != null) {
      order.setReceiverCountry(addressSnapshot.getCountry());
      order.setReceiverFirstName(addressSnapshot.getFirstName());
      order.setReceiverLastName(addressSnapshot.getLastName());
      order.setReceiverPhone(addressSnapshot.getPhone());
      order.setReceiverAddressLine1(addressSnapshot.getAddressLine1());
      order.setReceiverAddressLine2(addressSnapshot.getAddressLine2());
      order.setReceiverCity(addressSnapshot.getCity());
      order.setReceiverState(addressSnapshot.getState());
      order.setReceiverZipCode(addressSnapshot.getZipCode());
      order.setReceiverName((addressSnapshot.getFirstName() == null ? "" : addressSnapshot.getFirstName()) + " "
          + (addressSnapshot.getLastName() == null ? "" : addressSnapshot.getLastName()));
      order.setReceiverAddress(String.join(", ",
          nullToEmpty(addressSnapshot.getAddressLine1()),
          nullToEmpty(addressSnapshot.getCity()),
          nullToEmpty(addressSnapshot.getState())));
    }

    BigDecimal subtotal = BigDecimal.ZERO;
    for (OrderCreateDTO.Item item : items) {
      PmsSku sku = skuService.getById(item.getSkuId());
      if (sku == null || sku.getStock() < item.getQuantity()) {
        throw new RuntimeException("Insufficient stock for SKU: " + item.getSkuId());
      }
      subtotal = subtotal.add(sku.getPrice().multiply(new BigDecimal(item.getQuantity())));
      sku.setStock(sku.getStock() - item.getQuantity());
      skuService.updateById(sku);
    }
    BigDecimal totalAmount = subtotal.add(order.getShippingAmount()).subtract(order.getDiscountAmount());
    order.setTotalAmount(totalAmount.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : totalAmount);
    this.save(order);

    for (OrderCreateDTO.Item item : items) {
      PmsSku sku = skuService.getById(item.getSkuId());
      PmsProduct product = productService.getById(item.getProductId());
      OmsOrderItem orderItem = new OmsOrderItem();
      orderItem.setOrderId(order.getId());
      orderItem.setProductId(item.getProductId());
      orderItem.setSkuId(item.getSkuId());
      orderItem.setProductName(
          product == null ? "Product " + item.getProductId() : ProductVO.extractLang(product.getName(), lang));
      orderItem.setProductPic(sku.getPic());
      orderItem.setSkuCode(sku.getSkuCode());
      orderItem.setQuantity(item.getQuantity());
      orderItem.setPrice(sku.getPrice());
      orderItem.setSkuAttributesSnapshot(objectMapper.valueToTree(SkuVO.buildSnapshot(sku.getSpecs(), lang)));
      orderItem.setCreateTime(LocalDateTime.now());
      orderItem.setUpdateTime(LocalDateTime.now());
      orderItemService.save(orderItem);
    }

    if (!"direct".equalsIgnoreCase(order.getCheckoutSource()) && dto.getCartItemIds() != null
        && !dto.getCartItemIds().isEmpty()) {
      cartItemService.removeByIds(dto.getCartItemIds());
    }

    return order;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public OmsOrder payOrder(OrderPayDTO dto, Long userId) {
    OmsOrder order = this.getById(dto.getOrderId());
    if (order == null || !order.getUserId().equals(userId)) {
      throw new RuntimeException("Order not found");
    }
    if (order.getStatus() != 0) {
      throw new RuntimeException("Order status is not payable");
    }
    order.setPaymentMethod(dto.getPaymentMethod() == null ? order.getPaymentMethod() : dto.getPaymentMethod());
    if ("fail".equalsIgnoreCase(dto.getMockResult())) {
      order.setPayStatus(2);
      order.setStatus(0);
    } else {
      order.setPayStatus(1);
      order.setStatus(1);
      order.setPayTime(LocalDateTime.now());
      order.setPayTxnNo("MOCK_TXN_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    }
    order.setUpdateTime(LocalDateTime.now());
    this.updateById(order);
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
        itemVO.setSkuAttributesSnapshot(item.getSkuAttributesSnapshot());
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
      itemVO.setSkuAttributesSnapshot(item.getSkuAttributesSnapshot());
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

  private List<CartItemVO> resolvePreviewItems(OrderPreviewDTO dto, Long userId) {
    String lang = LanguageContext.getLanguage();
    List<CartItemVO> result = new ArrayList<>();
    if ("direct".equalsIgnoreCase(dto.getSource()) && dto.getItems() != null) {
      for (OrderCreateDTO.Item item : dto.getItems()) {
        PmsSku sku = skuService.getById(item.getSkuId());
        PmsProduct product = productService.getById(item.getProductId());
        if (sku == null || product == null) {
          continue;
        }
        CartItemVO vo = new CartItemVO();
        vo.setProductId(item.getProductId());
        vo.setSkuId(item.getSkuId());
        vo.setTitle(ProductVO.extractLang(product.getName(), lang));
        vo.setProductPic(sku.getPic());
        vo.setUnitPrice(sku.getPrice());
        vo.setQuantity(item.getQuantity());
        vo.setLineAmount(sku.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        vo.setAttributes(SkuVO.buildSnapshot(sku.getSpecs(), lang));
        vo.setStock(sku.getStock());
        result.add(vo);
      }
      return result;
    }
    if (dto.getCartItemIds() == null || dto.getCartItemIds().isEmpty()) {
      return result;
    }
    List<OmsCartItem> cartItems = cartItemService.listByIds(dto.getCartItemIds()).stream()
        .filter(item -> item.getUserId().equals(userId))
        .collect(Collectors.toList());
    for (OmsCartItem cartItem : cartItems) {
      PmsSku sku = skuService.getById(cartItem.getSkuId());
      PmsProduct product = productService.getById(cartItem.getProductId());
      if (sku == null || product == null) {
        continue;
      }
      CartItemVO vo = new CartItemVO();
      vo.setCartItemId(cartItem.getId());
      vo.setProductId(cartItem.getProductId());
      vo.setSkuId(cartItem.getSkuId());
      vo.setTitle(ProductVO.extractLang(product.getName(), lang));
      vo.setProductPic(sku.getPic());
      vo.setUnitPrice(sku.getPrice());
      vo.setQuantity(cartItem.getQuantity());
      vo.setLineAmount(sku.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
      vo.setAttributes(cartItem.getSelectedAttributesSnapshot() != null
          ? cartItem.getSelectedAttributesSnapshot()
          : SkuVO.buildSnapshot(sku.getSpecs(), lang));
      vo.setStock(sku.getStock());
      result.add(vo);
    }
    return result;
  }

  private List<OrderCreateDTO.Item> resolveCreateItems(OrderCreateDTO dto, Long userId) {
    if ("direct".equalsIgnoreCase(dto.getSource()) && dto.getItems() != null) {
      return dto.getItems();
    }
    if (dto.getCartItemIds() == null || dto.getCartItemIds().isEmpty()) {
      return new ArrayList<>();
    }
    List<OmsCartItem> cartItems = cartItemService.listByIds(dto.getCartItemIds()).stream()
        .filter(item -> item.getUserId().equals(userId))
        .collect(Collectors.toList());
    return cartItems.stream().map(item -> {
      OrderCreateDTO.Item orderItem = new OrderCreateDTO.Item();
      orderItem.setProductId(item.getProductId());
      orderItem.setSkuId(item.getSkuId());
      orderItem.setQuantity(item.getQuantity());
      if (item.getSelectedAttributesSnapshot() != null) {
        Map<String, Object> attributes = objectMapper.convertValue(item.getSelectedAttributesSnapshot(), Map.class);
        orderItem.setAttributes(attributes);
      }
      return orderItem;
    }).collect(Collectors.toList());
  }

  private String nullToEmpty(String value) {
    return value == null ? "" : value;
  }
}
