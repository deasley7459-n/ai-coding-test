package com.aicoding.backend.model;

import java.time.LocalDateTime;

/**
 * 用户实体。
 */
public record User(
        Long id,
        String name,
        String email,
        String phone,
        LocalDateTime createdAt
) {
}
