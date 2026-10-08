package com.shop.controller;

import com.shop.common.AlipaySignatureUtils;
import com.shop.config.PaymentProperties;
import com.shop.entity.OmsOrder;
import com.shop.entity.PayPaymentIntent;
import com.shop.service.OmsOrderService;
import com.shop.service.PayPaymentIntentService;
import com.shop.support.BackendIntegrationTestSupport;
import java.math.BigDecimal;
import java.security.KeyPairGenerator;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

class PaymentCallbackCurrencyIntegrationTest extends BackendIntegrationTestSupport {
  @Autowired PaymentProperties properties;
  @Autowired OmsOrderService orders;
  @Autowired PayPaymentIntentService intents;
  PaymentProperties.AlipayProperties previous;
  String signingKey;
  PayPaymentIntent intent;

  @BeforeEach void prepareSignedTestChannel() throws Exception {
    previous=properties.getAlipay();
    var generator=KeyPairGenerator.getInstance("RSA");generator.initialize(2048);var pair=generator.generateKeyPair();
    signingKey=Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded());
    var channel=new PaymentProperties.AlipayProperties();channel.setEnabled(true);channel.setSandbox(false);
    channel.setAppId("cbjj-test-app");channel.setAppPrivateKey(signingKey);
    channel.setAlipayPublicKey(Base64.getEncoder().encodeToString(pair.getPublic().getEncoded()));
    channel.setNotifyUrl("https://cbjjpower.com/api/payment/alipay/notify");
    channel.setReturnUrl("https://cbjjpower.com/payment/alipay/return");properties.setAlipay(channel);
    OmsOrder order=orders.getById(1L);order.setCurrency("CNY");order.setCountry("CN");order.setTotalAmount(new BigDecimal("1889.93"));
    order.setStatus("PENDING_PAYMENT");order.setPaymentStatus("PENDING");order.setPayTxnNo(null);order.setPayTime(null);
    order.setPaymentExpireTime(java.time.LocalDateTime.now().plusMinutes(15));orders.updateById(order);
    intent=new PayPaymentIntent();intent.setIntentNo("PI_CBJJ_CALLBACK_TEST");intent.setOrderId(1L);intent.setUserId(1L);
    intent.setAmount(order.getTotalAmount());intent.setCurrency("CNY");intent.setMethodCode("alipay");intent.setProviderKey("alipay");
    intent.setStatus("CREATED");intent.setCreateTime(java.time.LocalDateTime.now());intents.save(intent);
  }
  @AfterEach void restoreChannel(){properties.setAlipay(previous);}

  private Map<String,String> callback(){
    Map<String,String> fields=new LinkedHashMap<>();fields.put("app_id","cbjj-test-app");fields.put("out_trade_no",intent.getIntentNo());
    fields.put("trade_no","REAL_CHANNEL_TEST_TXN");fields.put("trade_status","TRADE_SUCCESS");fields.put("total_amount","1889.93");
    fields.put("sign_type","RSA2");return fields;
  }
  private String notify(Map<String,String> fields) throws Exception {
    fields.put("sign",AlipaySignatureUtils.sign(fields,signingKey,"UTF-8","RSA2"));
    var request=post("/payment/alipay/notify");fields.forEach(request::param);
    return mockMvc.perform(request).andReturn().getResponse().getContentAsString();
  }

  @Test void signedCallbackRequiresBothSnapshotAmounts() throws Exception {
    var fields=callback();fields.put("total_amount","1889.92");
    assertThat(notify(fields)).isEqualTo("fail");
    assertThat(orders.getById(1L).getPaymentStatus()).isEqualTo("PENDING");
    fields=callback();fields.remove("total_amount");assertThat(notify(fields)).isEqualTo("fail");
    intent.setAmount(new BigDecimal("1.00"));intents.updateById(intent);
    assertThat(notify(callback())).isEqualTo("fail");
  }

  @Test void rejectsForeignCurrencyAndWrongPaymentEnvironment() throws Exception {
    var fields=callback();fields.put("currency","USD");assertThat(notify(fields)).isEqualTo("fail");
    intent.setCurrency("USD");intents.updateById(intent);assertThat(notify(callback())).isEqualTo("fail");
    intent.setCurrency("CNY");intent.setProviderKey("alipay_sandbox");intents.updateById(intent);
    assertThat(notify(callback())).isEqualTo("fail");
    assertThat(orders.getById(1L).getPaymentStatus()).isEqualTo("PENDING");
  }

  @Test void matchingCallbackSucceedsIdempotentlyAndAppMismatchFails() throws Exception {
    var fields=callback();fields.put("app_id","another-merchant");assertThat(notify(fields)).isEqualTo("fail");
    fields=callback();fields.remove("app_id");assertThat(notify(fields)).isEqualTo("fail");
    fields=callback();fields.remove("trade_no");assertThat(notify(fields)).isEqualTo("fail");
    assertThat(notify(callback())).isEqualTo("success");
    assertThat(notify(callback())).isEqualTo("success");
    assertThat(orders.getById(1L).getPaymentStatus()).isEqualTo("PAID");
    assertThat(orders.getById(1L).getTotalAmount()).isEqualByComparingTo("1889.93");
  }
}
