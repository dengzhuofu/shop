package com.shop.common;

import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public final class AlipaySignatureUtils {

  private AlipaySignatureUtils() {
  }

  public static String sign(Map<String, String> params, String privateKey, String charset, String signType) {
    try {
      Signature signature = Signature.getInstance(resolveAlgorithm(signType));
      signature.initSign(loadPrivateKey(privateKey));
      signature.update(buildSignContent(params).getBytes(Charset.forName(charset)));
      return Base64.getEncoder().encodeToString(signature.sign());
    } catch (Exception ex) {
      throw new IllegalStateException("Failed to sign Alipay request", ex);
    }
  }

  public static boolean verify(Map<String, String> params, String publicKey, String charset, String signType) {
    String sign = params.get("sign");
    if (sign == null || sign.isBlank()) {
      return false;
    }

    try {
      Signature signature = Signature.getInstance(resolveAlgorithm(signType));
      signature.initVerify(loadPublicKey(publicKey));
      signature.update(buildSignContent(params).getBytes(Charset.forName(charset)));
      return signature.verify(Base64.getDecoder().decode(sign));
    } catch (Exception ex) {
      return false;
    }
  }

  public static String buildSignContent(Map<String, String> params) {
    List<Map.Entry<String, String>> entries = new ArrayList<>(params.entrySet());
    entries.removeIf(entry -> {
      String key = entry.getKey();
      String value = entry.getValue();
      return value == null
          || value.isBlank()
          || "sign".equalsIgnoreCase(key)
          || "sign_type".equalsIgnoreCase(key);
    });
    entries.sort(Comparator.comparing(Map.Entry::getKey));

    StringBuilder builder = new StringBuilder();
    for (Map.Entry<String, String> entry : entries) {
      if (builder.length() > 0) {
        builder.append('&');
      }
      builder.append(entry.getKey()).append('=').append(entry.getValue());
    }
    return builder.toString();
  }

  private static PrivateKey loadPrivateKey(String rawKey) throws Exception {
    byte[] bytes = Base64.getDecoder().decode(normalizeKey(rawKey));
    return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(bytes));
  }

  private static PublicKey loadPublicKey(String rawKey) throws Exception {
    byte[] bytes = Base64.getDecoder().decode(normalizeKey(rawKey));
    return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(bytes));
  }

  private static String normalizeKey(String rawKey) {
    return rawKey
        .replace("\\n", "\n")
        .replace("-----BEGIN PRIVATE KEY-----", "")
        .replace("-----END PRIVATE KEY-----", "")
        .replace("-----BEGIN PUBLIC KEY-----", "")
        .replace("-----END PUBLIC KEY-----", "")
        .replaceAll("\\s+", "");
  }

  private static String resolveAlgorithm(String signType) {
    return "RSA2".equalsIgnoreCase(signType) ? "SHA256withRSA" : "SHA1withRSA";
  }
}
