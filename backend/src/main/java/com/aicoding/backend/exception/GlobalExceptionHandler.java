package com.aicoding.backend.exception;

import com.aicoding.backend.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

/**
 * 全局异常处理：将异常转换为统一的 ApiResponse 结构。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 用户不存在 */
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleUserNotFound(UserNotFoundException e) {
        return ApiResponse.error(404, e.getMessage());
    }

    /** 参数校验失败 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ApiResponse.error(400, message);
    }

    /** 请求体缺失或 JSON 格式错误 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleUnreadable(HttpMessageNotReadableException e) {
        return ApiResponse.error(400, "请求体格式错误");
    }

    /** 路径参数类型不匹配，例如 /api/users/abc */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        return ApiResponse.error(400, "参数类型不正确: " + e.getName());
    }

    /** 兜底：未知异常 */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleUnexpected(Exception e) {
        return ApiResponse.error(500, "服务器内部错误: " + e.getMessage());
    }
}
