package com.shop.common;

import lombok.Data;

@Data
public class Result<T> {
  private Integer code;
  private String message;
  private T data;

  public static <T> Result<T> success(T data) {
    Result<T> result = new Result<>();
    result.setCode(200);
    // 这里根据当前语言上下文返回多语言成功提示
    String lang = LanguageContext.getLanguage();
    result.setMessage("zh".equals(lang) ? "操作成功" : "Success");
    result.setData(data);
    return result;
  }

  public static <T> Result<T> success() {
    return success(null);
  }

  public static <T> Result<T> error(String message) {
    Result<T> result = new Result<>();
    result.setCode(500);
    // 如果没有传 message，则返回默认多语言报错
    if (message == null || message.isEmpty()) {
        String lang = LanguageContext.getLanguage();
        result.setMessage("zh".equals(lang) ? "操作失败" : "Failed");
    } else {
        result.setMessage(message);
    }
    return result;
  }

  public static <T> Result<T> error(Integer code, String message) {
    Result<T> result = new Result<>();
    result.setCode(code);
    result.setMessage(message);
    return result;
  }
}
