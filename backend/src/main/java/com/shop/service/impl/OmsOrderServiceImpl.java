package com.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.common.JsonLocaleUtils;
import com.shop.common.PaymentMethodCatalog;
import com.shop.common.PreviewTokenUtils;
import com.shop.common.ProductAddonUtils;
import com.shop.config.PaymentProperties;
import com.shop.dto.OrderCreateDTO;
import com.shop.dto.OrderPreviewDTO;
import com.shop.entity.OmsCartItem;
import com.shop.entity.OmsOrder;
import com.shop.entity.OmsOrderItem;
import com.shop.entity.PayPaymentIntent;
import com.shop.entity.PmsProduct;
import com.shop.entity.PmsSku;
import com.shop.entity.SmsCoupon;
import com.shop.entity.SmsCouponUser;
import com.shop.entity.UmsUserAddress;
import com.shop.mapper.OmsOrderMapper;
import com.shop.service.OmsCartItemService;
import com.shop.service.OmsOrderItemService;
import com.shop.service.OmsOrderService;
import com.shop.service.PayPaymentIntentService;
import com.shop.service.PmsProductService;
import com.shop.service.PmsSkuService;
import com.shop.service.AlipayGatewayService;
import com.shop.service.SmsCouponService;
import com.shop.service.SmsCouponUserService;
import com.shop.service.UmsUserAddressService;
import com.shop.vo.CartItemVO;
import com.shop.vo.CouponVO;
import com.shop.vo.OrderItemVO;
import com.shop.vo.OrderPreviewVO;
import com.shop.vo.OrderVO;
import com.shop.vo.ProductVO;
import com.shop.vo.SkuVO;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OmsOrderServiceImpl extends ServiceImpl<OmsOrderMapper, OmsOrder> implements OmsOrderService {

  private static final String DEFAULT_CURRENCY = "USD";
  private static final String DEFAULT_COUNTRY = "US";
  private static final String DEFAULT_SHIPPING_METHOD = "UPS Ground/FedEx Home Delivery(2-5 Business Days)";
  private static final BigDecimal FIXED_SHIPPING_AMOUNT = BigDecimal.ZERO;
  private static final BigDecimal FIXED_TAX_RATE = new BigDecimal("0.0825");

  private final OmsCartItemService cartItemService;
  private final OmsOrderItemService orderItemService;
  private final PmsSkuService skuService;
  private final PmsProductService productService;
  private final UmsUserAddressService userAddressService;
  private final SmsCouponService couponService;
  private final SmsCouponUserService couponUserService;
  private final PayPaymentIntentService paymentIntentService;
  private final PaymentProperties paymentProperties;
  private final AlipayGatewayService alipayGatewayService;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public OrderPreviewVO previewOrder(OrderPreviewDTO dto, Long userId) {
    String language = JsonLocaleUtils.currentLanguage();
    List<ResolvedCheckoutItem> resolvedItems = resolveCheckoutItems(dto.getCartItemIds(), userId, language);
    if (resolvedItems.isEmpty()) {
      throw new RuntimeException("No checkout items found");
    }

    BigDecimal subtotal = resolvedItems.stream()
        .map(ResolvedCheckoutItem::getLineAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    CouponResolution couponResolution = resolveCoupon(dto.getCouponUserId(), userId, subtotal);
    BigDecimal discountAmount = couponResolution.getDiscountAmount();
    BigDecimal shippingAmount = FIXED_SHIPPING_AMOUNT;
    BigDecimal taxableAmount = subtotal.subtract(discountAmount).add(shippingAmount);
    BigDecimal taxAmount = roundCurrency(taxableAmount.multiply(FIXED_TAX_RATE));
    BigDecimal totalAmount = roundCurrency(taxableAmount.add(taxAmount));

    OrderPreviewVO previewVO = new OrderPreviewVO();
    previewVO.setItems(resolvedItems.stream().map(ResolvedCheckoutItem::getCartItemVO).collect(Collectors.toList()));
    previewVO.setCurrency(DEFAULT_CURRENCY);
    previewVO.setCountry(DEFAULT_COUNTRY);
    previewVO.setSubtotal(roundCurrency(subtotal));
    previewVO.setShippingAmount(shippingAmount);
    previewVO.setTaxAmount(taxAmount);
    previewVO.setDiscountAmount(discountAmount);
    previewVO.setTotalAmount(totalAmount);
    previewVO.setCoupon(couponResolution.getCouponVO());
    previewVO.setPaymentMethods(PaymentMethodCatalog.methods(language));
    previewVO.setPreviewToken(buildPreviewToken(userId, resolvedItems, dto.getCouponUserId(), shippingAmount, taxAmount,
        totalAmount));
    return previewVO;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public OmsOrder createOrder(OrderCreateDTO dto, Long userId) {
    OrderPreviewDTO previewDTO = new OrderPreviewDTO();
    previewDTO.setSource(dto.getSource());
    previewDTO.setCartItemIds(dto.getCartItemIds());
    previewDTO.setAddressId(dto.getAddressId());
    previewDTO.setShippingMethod(dto.getShippingMethod());
    previewDTO.setCouponUserId(dto.getCouponUserId());

    OrderPreviewVO preview = previewOrder(previewDTO, userId);
    if (dto.getPreviewToken() == null || !dto.getPreviewToken().equals(preview.getPreviewToken())) {
      throw new RuntimeException("Preview token mismatch. Please preview again before creating the order.");
    }

    List<ResolvedCheckoutItem> resolvedItems = resolveCheckoutItems(dto.getCartItemIds(), userId, JsonLocaleUtils.currentLanguage());
    if (resolvedItems.isEmpty()) {
      throw new RuntimeException("Order items not found");
    }

    OmsOrder order = new OmsOrder();
    order.setUserId(userId);
    order.setOrderSn(buildOrderSn());
    order.setSubtotalAmount(preview.getSubtotal());
    order.setTaxAmount(preview.getTaxAmount());
    order.setTotalAmount(preview.getTotalAmount());
    order.setStatus("PENDING_PAYMENT");
    order.setPaymentStatus("PENDING");
    order.setCurrency(DEFAULT_CURRENCY);
    order.setCountry(DEFAULT_COUNTRY);
    order.setPreviewToken(preview.getPreviewToken());
    order.setCouponUserId(dto.getCouponUserId());
    order.setCouponCode(extractCouponCode(preview.getCoupon()));
    order.setCouponDiscountAmount(preview.getDiscountAmount());
    order.setCheckoutSource(dto.getSource() == null ? "cart" : dto.getSource());
    order.setShippingMethod(dto.getShippingMethod() == null ? DEFAULT_SHIPPING_METHOD : dto.getShippingMethod());
    order.setShippingAmount(preview.getShippingAmount());
    order.setDiscountAmount(preview.getDiscountAmount());
    order.setRemark(dto.getRemark());
    order.setCreateTime(LocalDateTime.now());
    order.setUpdateTime(LocalDateTime.now());
    order.setPaymentExpireTime(LocalDateTime.now().plusMinutes(paymentProperties.getOrderTimeoutMinutes()));

    applyAddress(order, dto.getAddressId(), dto.getAddressSnapshot(), userId);
    this.save(order);
    log.info("AUDIT order_created orderId={} orderSn={} userId={} totalAmount={} expiresAt={}",
        order.getId(), order.getOrderSn(), userId, order.getTotalAmount(), order.getPaymentExpireTime());

    for (ResolvedCheckoutItem item : resolvedItems) {
      PmsSku sku = item.getSku();
      if (!"ACTIVE".equalsIgnoreCase(sku.getStatus()) || sku.getStock() < item.getQuantity()) {
        throw new RuntimeException("Insufficient stock for SKU: " + sku.getId());
      }
      sku.setStock(sku.getStock() - item.getQuantity());
      skuService.updateById(sku);
      log.info("AUDIT inventory_locked orderId={} skuId={} quantity={} remainingStock={}",
          order.getId(), sku.getId(), item.getQuantity(), sku.getStock());

      OmsOrderItem orderItem = new OmsOrderItem();
      orderItem.setOrderId(order.getId());
      orderItem.setProductId(item.getProduct().getId());
      orderItem.setSkuId(sku.getId());
      orderItem.setProductName(item.getProduct().getName());
      orderItem.setProductPic(sku.getPic());
      orderItem.setSkuCode(sku.getSkuCode());
      orderItem.setSkuAttributesSnapshot(item.getCartItem().getSelectedAttributesSnapshot());
      orderItem.setAddonsSnapshot(item.getCartItem().getSelectedAddonsSnapshot());
      orderItem.setQuantity(item.getQuantity());
      orderItem.setUnitPrice(sku.getPrice());
      orderItem.setLineAmount(item.getLineAmount());
      orderItem.setCreateTime(LocalDateTime.now());
      orderItem.setUpdateTime(LocalDateTime.now());
      orderItemService.save(orderItem);
    }

    if (dto.getCartItemIds() != null && !dto.getCartItemIds().isEmpty()) {
      cartItemService.removeByIds(dto.getCartItemIds());
    }

    return order;
  }

  @Override
  public Page<OrderVO> getUserOrders(Long userId, String status, Integer pageNum, Integer pageSize) {
    Page<OmsOrder> page = new Page<>(pageNum, pageSize);
    QueryWrapper<OmsOrder> wrapper = new QueryWrapper<>();
    wrapper.eq("user_id", userId);
    if (status != null && !status.isBlank()) {
      wrapper.eq("status", status);
    }
    wrapper.orderByDesc("create_time");

    Page<OmsOrder> orderPage = this.page(page, wrapper);
    orderPage.getRecords().forEach(this::expireOrderIfNeeded);

    Page<OrderVO> voPage = new Page<>(pageNum, pageSize);
    voPage.setTotal(orderPage.getTotal());
    voPage.setCurrent(orderPage.getCurrent());
    voPage.setSize(orderPage.getSize());
    voPage.setPages(orderPage.getPages());
    voPage.setRecords(orderPage.getRecords().stream().map(this::toOrderVO).collect(Collectors.toList()));
    return voPage;
  }

  @Override
  public OrderVO getOrderDetail(Long orderId, Long userId) {
    OmsOrder order = this.getById(orderId);
    if (order == null || !order.getUserId().equals(userId)) {
      return null;
    }
    expireOrderIfNeeded(order);
    return toOrderVO(order);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean cancelOrder(Long orderId, Long userId) {
    OmsOrder order = this.getById(orderId);
    if (order == null || !order.getUserId().equals(userId)) {
      return false;
    }
    if (expireOrderIfNeeded(order)) {
      return false;
    }
    if (!"PENDING_PAYMENT".equals(order.getStatus()) && !"PAYMENT_PROCESSING".equals(order.getStatus())) {
      return false;
    }
    order.setStatus("CANCELLED");
    order.setPaymentStatus("CANCELLED");
    order.setPayTxnNo(null);
    order.setPayTime(null);
    order.setPaymentExpireTime(null);
    order.setUpdateTime(LocalDateTime.now());

    releaseInventory(order.getId());
    expireActivePaymentIntents(order.getId(), "cancelled");
    boolean updated = this.updateById(order);
    if (updated) {
      log.info("AUDIT order_cancelled orderId={} userId={}", order.getId(), userId);
    }
    return updated;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean expireOrderIfNeeded(OmsOrder order) {
    if (order == null) {
      return false;
    }

    OmsOrder currentOrder = this.getById(order.getId());
    if (currentOrder == null || !isAwaitingPayment(currentOrder) || !isOrderExpired(currentOrder)) {
      if (currentOrder != null) {
        BeanUtils.copyProperties(currentOrder, order);
      }
      return false;
    }

    LocalDateTime now = LocalDateTime.now();
    boolean updated = this.lambdaUpdate()
        .eq(OmsOrder::getId, currentOrder.getId())
        .in(OmsOrder::getStatus, List.of("PENDING_PAYMENT", "PAYMENT_PROCESSING"))
        .set(OmsOrder::getStatus, "EXPIRED")
        .set(OmsOrder::getPaymentStatus, "EXPIRED")
        .set(OmsOrder::getPayTxnNo, null)
        .set(OmsOrder::getPayTime, null)
        .set(OmsOrder::getPaymentExpireTime, now)
        .set(OmsOrder::getUpdateTime, now)
        .update();

    if (!updated) {
      OmsOrder latestOrder = this.getById(order.getId());
      if (latestOrder != null) {
        BeanUtils.copyProperties(latestOrder, order);
      }
      return false;
    }

    releaseInventory(order.getId());
    expireActivePaymentIntents(order.getId(), "expired");

    applyExpiredOrderState(order, now);
    log.info("AUDIT order_expired orderId={} userId={} expiredAt={}",
        order.getId(), currentOrder.getUserId(), now);
    return true;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public int closeExpiredOrders(int batchSize) {
    int effectiveBatchSize = batchSize > 0 ? batchSize : paymentProperties.getExpirationCheckBatchSize();
    List<OmsOrder> expiredOrders = this.list(new QueryWrapper<OmsOrder>()
        .in("status", List.of("PENDING_PAYMENT", "PAYMENT_PROCESSING"))
        .isNotNull("payment_expire_time")
        .lt("payment_expire_time", LocalDateTime.now())
        .orderByAsc("payment_expire_time")
        .last("LIMIT " + effectiveBatchSize));

    int closedCount = 0;
    for (OmsOrder order : expiredOrders) {
      if (expireOrderIfNeeded(order)) {
        closedCount++;
      }
    }
    return closedCount;
  }

  private List<ResolvedCheckoutItem> resolveCheckoutItems(List<Long> cartItemIds, Long userId, String language) {
    if (cartItemIds == null || cartItemIds.isEmpty()) {
      return new ArrayList<>();
    }

    List<OmsCartItem> cartItems = cartItemService.listByIds(cartItemIds).stream()
        .filter(item -> item.getUserId().equals(userId))
        .sorted(Comparator.comparing(OmsCartItem::getId))
        .collect(Collectors.toList());

    List<ResolvedCheckoutItem> result = new ArrayList<>();
    for (OmsCartItem cartItem : cartItems) {
      PmsSku sku = skuService.getById(cartItem.getSkuId());
      PmsProduct product = productService.getById(cartItem.getProductId());
      if (sku == null || product == null || !Boolean.TRUE.equals(product.getPublished())) {
        continue;
      }

      BigDecimal addonAmount = resolveAddonAmount(product.getUpsells(), cartItem.getSelectedAddonsSnapshot(), language);
      BigDecimal lineAmount = sku.getPrice().add(addonAmount).multiply(BigDecimal.valueOf(cartItem.getQuantity()));

      CartItemVO itemVO = new CartItemVO();
      itemVO.setCartItemId(cartItem.getId());
      itemVO.setProductId(product.getId());
      itemVO.setSkuId(sku.getId());
      itemVO.setTitle(ProductVO.extractLang(product.getName(), language));
      itemVO.setSlug(product.getSlug());
      itemVO.setProductPic(sku.getPic());
      itemVO.setUnitPrice(sku.getPrice());
      itemVO.setAddonAmount(addonAmount.multiply(BigDecimal.valueOf(cartItem.getQuantity())));
      itemVO.setQuantity(cartItem.getQuantity());
      itemVO.setLineAmount(roundCurrency(lineAmount));
      itemVO.setAttributes(cartItem.getSelectedAttributesSnapshot() != null
          ? objectMapper.convertValue(cartItem.getSelectedAttributesSnapshot(), Object.class)
          : SkuVO.buildSnapshot(sku.getSpecs(), language));
      itemVO.setAddons(cartItem.getSelectedAddonsSnapshot() == null ? List.of()
          : objectMapper.convertValue(cartItem.getSelectedAddonsSnapshot(), Object.class));
      itemVO.setStock(sku.getStock());

      ResolvedCheckoutItem resolved = new ResolvedCheckoutItem();
      resolved.setCartItem(cartItem);
      resolved.setProduct(product);
      resolved.setSku(sku);
      resolved.setQuantity(cartItem.getQuantity());
      resolved.setAddonAmount(addonAmount);
      resolved.setLineAmount(roundCurrency(lineAmount));
      resolved.setCartItemVO(itemVO);
      result.add(resolved);
    }
    return result;
  }

  private CouponResolution resolveCoupon(Long couponUserId, Long userId, BigDecimal subtotal) {
    CouponResolution resolution = new CouponResolution();
    resolution.setDiscountAmount(BigDecimal.ZERO);

    if (couponUserId == null) {
      return resolution;
    }

    SmsCouponUser couponUser = couponUserService.getById(couponUserId);
    if (couponUser == null || !couponUser.getUserId().equals(userId)) {
      throw new RuntimeException("Coupon not found");
    }

    SmsCoupon coupon = couponService.getById(couponUser.getCouponId());
    if (coupon == null || !Boolean.TRUE.equals(coupon.getActive())) {
      throw new RuntimeException("Coupon is unavailable");
    }
    if (subtotal.compareTo(coupon.getThresholdAmount()) < 0) {
      throw new RuntimeException("Coupon threshold not met");
    }

    resolution.setDiscountAmount(coupon.getDiscountAmount());
    resolution.setCouponVO(CouponVO.from(coupon, couponUser));
    return resolution;
  }

  private void applyAddress(OmsOrder order, Long addressId, OrderCreateDTO.AddressSnapshot addressSnapshot, Long userId) {
    OrderCreateDTO.AddressSnapshot resolvedSnapshot = addressSnapshot;
    if (addressId != null) {
      UmsUserAddress address = userAddressService.getById(addressId);
      if (address == null || !address.getUserId().equals(userId)) {
        throw new RuntimeException("Address not found");
      }
      resolvedSnapshot = new OrderCreateDTO.AddressSnapshot();
      resolvedSnapshot.setCountry(address.getCountry());
      resolvedSnapshot.setFirstName(address.getFirstName());
      resolvedSnapshot.setLastName(address.getLastName());
      resolvedSnapshot.setPhone(address.getPhone());
      resolvedSnapshot.setAddressLine1(address.getAddressLine1());
      resolvedSnapshot.setAddressLine2(address.getAddressLine2());
      resolvedSnapshot.setCity(address.getCity());
      resolvedSnapshot.setState(address.getState());
      resolvedSnapshot.setZipCode(address.getZipCode());
    }

    if (resolvedSnapshot == null) {
      throw new RuntimeException("Address is required");
    }
    validateAddressSnapshot(resolvedSnapshot);

    order.setReceiverCountry(resolvedSnapshot.getCountry());
    order.setReceiverFirstName(resolvedSnapshot.getFirstName());
    order.setReceiverLastName(resolvedSnapshot.getLastName());
    order.setReceiverPhone(resolvedSnapshot.getPhone());
    order.setReceiverAddressLine1(resolvedSnapshot.getAddressLine1());
    order.setReceiverAddressLine2(resolvedSnapshot.getAddressLine2());
    order.setReceiverCity(resolvedSnapshot.getCity());
    order.setReceiverState(resolvedSnapshot.getState());
    order.setReceiverZipCode(resolvedSnapshot.getZipCode());
    order.setReceiverName((resolvedSnapshot.getFirstName() == null ? "" : resolvedSnapshot.getFirstName()) + " "
        + (resolvedSnapshot.getLastName() == null ? "" : resolvedSnapshot.getLastName()));
    order.setReceiverAddress(String.join(", ",
        nullToEmpty(resolvedSnapshot.getAddressLine1()),
        nullToEmpty(resolvedSnapshot.getCity()),
        nullToEmpty(resolvedSnapshot.getState()),
        nullToEmpty(resolvedSnapshot.getZipCode())));
  }

  private void validateAddressSnapshot(OrderCreateDTO.AddressSnapshot addressSnapshot) {
    if (isBlank(addressSnapshot.getCountry())
        || isBlank(addressSnapshot.getFirstName())
        || isBlank(addressSnapshot.getLastName())
        || isBlank(addressSnapshot.getAddressLine1())
        || isBlank(addressSnapshot.getCity())
        || isBlank(addressSnapshot.getState())
        || isBlank(addressSnapshot.getZipCode())) {
      throw new RuntimeException("Incomplete shipping address");
    }
  }

  private OrderVO toOrderVO(OmsOrder order) {
    String language = JsonLocaleUtils.currentLanguage();
    OrderVO vo = new OrderVO();
    BeanUtils.copyProperties(order, vo);

    List<OmsOrderItem> items = orderItemService.list(new QueryWrapper<OmsOrderItem>().eq("order_id", order.getId()));
    List<OrderItemVO> itemVOs = items.stream().map(item -> {
      OrderItemVO itemVO = new OrderItemVO();
      itemVO.setId(item.getId());
      itemVO.setProductId(item.getProductId());
      itemVO.setSkuId(item.getSkuId());
      itemVO.setProductName(JsonLocaleUtils.localizedText(item.getProductName(), language));
      itemVO.setProductPic(item.getProductPic());
      itemVO.setSkuCode(item.getSkuCode());
      itemVO.setSkuAttributesSnapshot(item.getSkuAttributesSnapshot() == null ? null
          : objectMapper.convertValue(item.getSkuAttributesSnapshot(), Object.class));
      itemVO.setAddons(item.getAddonsSnapshot() == null ? List.of()
          : objectMapper.convertValue(item.getAddonsSnapshot(), Object.class));
      itemVO.setQuantity(item.getQuantity());
      itemVO.setUnitPrice(item.getUnitPrice());
      itemVO.setLineAmount(item.getLineAmount());
      PmsProduct product = productService.getById(item.getProductId());
      itemVO.setSlug(product == null ? null : product.getSlug());
      return itemVO;
    }).collect(Collectors.toList());

    vo.setItems(itemVOs);
    return vo;
  }

  private BigDecimal resolveAddonAmount(JsonNode upsellsNode, JsonNode selectedAddonsSnapshot, String language) {
    List<String> addonCodes = extractAddonCodes(selectedAddonsSnapshot);
    return ProductAddonUtils.resolveAddonAmount(upsellsNode, addonCodes, language);
  }

  private List<String> extractAddonCodes(JsonNode selectedAddonsSnapshot) {
    if (selectedAddonsSnapshot == null || !selectedAddonsSnapshot.isArray()) {
      return List.of();
    }
    List<String> addonCodes = new ArrayList<>();
    for (JsonNode addon : selectedAddonsSnapshot) {
      String code = addon.path("code").asText();
      if (code != null && !code.isBlank()) {
        addonCodes.add(code);
      }
    }
    return addonCodes;
  }

  private String buildPreviewToken(Long userId, List<ResolvedCheckoutItem> items, Long couponUserId,
      BigDecimal shippingAmount, BigDecimal taxAmount, BigDecimal totalAmount) {
    StringBuilder payload = new StringBuilder();
    payload.append(couponUserId == null ? "none" : couponUserId).append("|")
        .append(shippingAmount).append("|")
        .append(taxAmount).append("|")
        .append(totalAmount);
    for (ResolvedCheckoutItem item : items) {
      payload.append("|").append(item.getProduct().getId())
          .append(":").append(item.getSku().getId())
          .append(":").append(item.getQuantity())
          .append(":").append(item.getLineAmount())
          .append(":").append(String.join(",", extractAddonCodes(item.getCartItem().getSelectedAddonsSnapshot())));
    }
    return PreviewTokenUtils.buildToken(userId, payload.toString());
  }

  private String buildOrderSn() {
    String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    return "ORD" + dateStr + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
  }

  private BigDecimal roundCurrency(BigDecimal amount) {
    return amount.setScale(2, RoundingMode.HALF_UP);
  }

  private String extractCouponCode(Object couponObject) {
    if (couponObject instanceof CouponVO couponVO) {
      return couponVO.getCode();
    }
    if (!(couponObject instanceof Map<?, ?> couponMap)) {
      return null;
    }
    Object code = couponMap.get("code");
    return code == null ? null : String.valueOf(code);
  }

  private String nullToEmpty(String value) {
    return value == null ? "" : value;
  }

  private boolean isBlank(String value) {
    return value == null || value.isBlank();
  }

  private boolean isAwaitingPayment(OmsOrder order) {
    return "PENDING_PAYMENT".equals(order.getStatus()) || "PAYMENT_PROCESSING".equals(order.getStatus());
  }

  private boolean isOrderExpired(OmsOrder order) {
    return order.getPaymentExpireTime() != null && !order.getPaymentExpireTime().isAfter(LocalDateTime.now());
  }

  private void applyExpiredOrderState(OmsOrder order, LocalDateTime expiredAt) {
    order.setStatus("EXPIRED");
    order.setPaymentStatus("EXPIRED");
    order.setPayTxnNo(null);
    order.setPayTime(null);
    order.setUpdateTime(expiredAt);
    order.setPaymentExpireTime(expiredAt);
  }

  private void releaseInventory(Long orderId) {
    List<OmsOrderItem> items = orderItemService.list(new QueryWrapper<OmsOrderItem>().eq("order_id", orderId));
    for (OmsOrderItem item : items) {
      PmsSku sku = skuService.getById(item.getSkuId());
      if (sku != null) {
        sku.setStock(sku.getStock() + item.getQuantity());
        skuService.updateById(sku);
        log.info("AUDIT inventory_released orderId={} skuId={} quantity={} restoredStock={}",
            orderId, sku.getId(), item.getQuantity(), sku.getStock());
      }
    }
  }

  private void expireActivePaymentIntents(Long orderId, String result) {
    List<PayPaymentIntent> intents = paymentIntentService.list(new QueryWrapper<PayPaymentIntent>()
        .eq("order_id", orderId)
        .in("status", List.of("CREATED")));
    LocalDateTime now = LocalDateTime.now();
    for (PayPaymentIntent intent : intents) {
      closeProviderTradeIfNeeded(intent);
      intent.setStatus("FAILED");
      intent.setMockResult(result);
      intent.setPaidTime(null);
      intent.setUpdateTime(now);
      paymentIntentService.updateById(intent);
      log.info("AUDIT payment_intent_closed orderId={} intentId={} intentNo={} result={}",
          orderId, intent.getId(), intent.getIntentNo(), result);
    }
  }

  private void closeProviderTradeIfNeeded(PayPaymentIntent intent) {
    if (intent.getProviderKey() == null || !intent.getProviderKey().startsWith("alipay")) {
      return;
    }
    if (!paymentProperties.getAlipay().isConfigured()) {
      return;
    }

    try {
      alipayGatewayService.closeTrade(paymentProperties.getAlipay(), intent.getIntentNo(), null);
    } catch (Exception ex) {
      log.warn("Failed to close Alipay trade for expired order intent {}", intent.getIntentNo(), ex);
    }
  }

  @Data
  private static class CouponResolution {
    private BigDecimal discountAmount = BigDecimal.ZERO;
    private CouponVO couponVO;
  }

  @Data
  private static class ResolvedCheckoutItem {
    private OmsCartItem cartItem;
    private PmsProduct product;
    private PmsSku sku;
    private Integer quantity;
    private BigDecimal addonAmount;
    private BigDecimal lineAmount;
    private CartItemVO cartItemVO;
  }
}
