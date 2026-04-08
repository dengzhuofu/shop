package com.shop.service;

import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.AlipayApiException;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.shop.config.PaymentProperties;
import com.shop.entity.OmsOrder;
import com.shop.entity.PayPaymentIntent;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class AlipayGatewayService {

  public String createPageRedirectUrl(PaymentProperties.AlipayProperties alipay,
      PayPaymentIntent intent,
      OmsOrder order,
      String subject) {
    try {
      AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
      if (hasText(alipay.getNotifyUrl())) {
        request.setNotifyUrl(alipay.getNotifyUrl());
      }
      request.setReturnUrl(alipay.getReturnUrl());

      AlipayTradePagePayModel model = new AlipayTradePagePayModel();
      model.setOutTradeNo(intent.getIntentNo());
      model.setTotalAmount(normalizeAmount(intent.getAmount()));
      model.setSubject(subject);
      model.setProductCode("FAST_INSTANT_TRADE_PAY");
      request.setBizModel(model);

      AlipayTradePagePayResponse response = buildClient(alipay).pageExecute(request, "GET");
      if (response == null || !hasText(response.getBody())) {
        throw new IllegalStateException("Alipay page pay response body is empty");
      }
      return response.getBody();
    } catch (Exception ex) {
      throw new IllegalStateException("Failed to create Alipay page pay request", ex);
    }
  }

  public TradeQueryResult queryTrade(PaymentProperties.AlipayProperties alipay,
      String outTradeNo,
      String tradeNo) {
    try {
      AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
      AlipayTradeQueryModel model = new AlipayTradeQueryModel();
      if (hasText(outTradeNo)) {
        model.setOutTradeNo(outTradeNo);
      }
      if (hasText(tradeNo)) {
        model.setTradeNo(tradeNo);
      }
      request.setBizModel(model);

      AlipayTradeQueryResponse response = buildClient(alipay).execute(request);
      return new TradeQueryResult(
          response != null && response.isSuccess(),
          response == null ? null : response.getTradeStatus(),
          response == null ? null : response.getTradeNo(),
          response == null ? null : parseAmount(response.getTotalAmount()),
          response == null ? null : response.getBody()
      );
    } catch (Exception ex) {
      throw new IllegalStateException("Failed to query Alipay trade status", ex);
    }
  }

  private AlipayClient buildClient(PaymentProperties.AlipayProperties alipay) {
    try {
      AlipayConfig config = new AlipayConfig();
      config.setServerUrl(alipay.resolvedGateway());
      config.setAppId(alipay.getAppId());
      config.setPrivateKey(alipay.getAppPrivateKey());
      config.setFormat("json");
      config.setCharset(alipay.getCharset());
      config.setSignType(alipay.getSignType());
      config.setAlipayPublicKey(alipay.getAlipayPublicKey());
      return new DefaultAlipayClient(config);
    } catch (AlipayApiException ex) {
      throw new IllegalStateException("Failed to initialize Alipay client", ex);
    }
  }

  private String normalizeAmount(BigDecimal amount) {
    return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
  }

  private BigDecimal parseAmount(String amountText) {
    return hasText(amountText) ? new BigDecimal(amountText) : null;
  }

  private boolean hasText(String value) {
    return value != null && !value.isBlank();
  }

  public record TradeQueryResult(
      boolean success,
      String tradeStatus,
      String tradeNo,
      BigDecimal totalAmount,
      String rawBody
  ) {
  }
}
