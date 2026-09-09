package com.aicoding.backend.exception;

/**
 * 用户不存在异常。
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("用户不存在: id=" + id);
    }
}
