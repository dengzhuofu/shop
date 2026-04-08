package com.shop.exception;

import cn.dev33.satoken.exception.NotLoginException;
import com.shop.common.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String LOGIN_EXPIRED_MESSAGE = "Login expired, please sign in again";

    @ExceptionHandler(NotLoginException.class)
    public Result<Void> handleNotLoginException(NotLoginException e) {
        return Result.error(401, LOGIN_EXPIRED_MESSAGE);
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        return Result.error(500, e.getMessage());
    }
}
