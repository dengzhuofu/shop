package com.shop.service;

import com.shop.config.PaymentProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentOrderExpirationScheduler {

  private final OmsOrderService orderService;
  private final PaymentProperties paymentProperties;

  @Scheduled(initialDelay = 60000, fixedDelay = 60000)
  public void closeExpiredOrders() {
    int closedCount = orderService.closeExpiredOrders(paymentProperties.getExpirationCheckBatchSize());
    if (closedCount > 0) {
      log.info("Closed {} expired unpaid orders and released inventory", closedCount);
    }
  }
}
