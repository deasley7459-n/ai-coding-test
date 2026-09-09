package com.aicoding.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 新增 / 更新用户的请求参数。
 */
public record UserRequest(

        @NotBlank(message = "姓名不能为空")
        @Size(max = 50, message = "姓名长度不能超过 50 个字符")
        String name,

        @NotBlank(message = "邮箱不能为空")
        @Email(message = "邮箱格式不正确")
        @Size(max = 100, message = "邮箱长度不能超过 100 个字符")
        String email,

        @Size(max = 20, message = "手机号长度不能超过 20 个字符")
        String phone
) {
}
