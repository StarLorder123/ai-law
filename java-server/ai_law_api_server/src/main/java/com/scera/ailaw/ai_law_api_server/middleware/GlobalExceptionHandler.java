package com.scera.ailaw.ai_law_api_server.middleware;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;


@RestControllerAdvice
public class GlobalExceptionHandler {

    // 通用异常
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception ex) {
        return ErrorResponse.of(500, "服务器内部错误: " + ex.getMessage());
    }

    // 参数校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldError().getDefaultMessage();
        return ErrorResponse.of(400, "参数验证失败: " + msg);
    }

    // 参数解析错误（比如 JSON 格式错误）
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleParseError(HttpMessageNotReadableException ex) {
        return ErrorResponse.of(400, "请求格式错误: " + ex.getMessage());
    }

    // 自定义非法参数异常（例：业务中手动抛出 IllegalArgumentException）
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgument(IllegalArgumentException ex) {
        return ErrorResponse.of(400, "非法参数: " + ex.getMessage());
    }

}

