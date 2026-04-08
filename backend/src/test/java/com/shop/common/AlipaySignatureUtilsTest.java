package com.shop.common;

import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AlipaySignatureUtilsTest {

  @Test
  void verifyShouldAcceptSignWithSpacesInsteadOfPlus() throws Exception {
    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
    keyPairGenerator.initialize(2048);
    KeyPair keyPair = keyPairGenerator.generateKeyPair();

    String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
    String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());

    Map<String, String> params = new LinkedHashMap<>();
    params.put("out_trade_no", "PI_TEST_001");
    params.put("trade_status", "TRADE_SUCCESS");
    params.put("total_amount", "0.01");
    params.put("charset", "UTF-8");
    params.put("sign_type", "RSA2");

    String sign = AlipaySignatureUtils.sign(params, privateKey, "UTF-8", "RSA2");
    assertThat(sign).contains("+");

    Map<String, String> paramsWithSpaceSign = new LinkedHashMap<>(params);
    paramsWithSpaceSign.put("sign", sign.replace('+', ' '));

    assertThat(AlipaySignatureUtils.verify(paramsWithSpaceSign, publicKey, "UTF-8", "RSA2")).isTrue();
  }
}
