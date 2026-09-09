package com.aicoding.backend.controller;

import com.aicoding.backend.dto.ApiResponse;
import com.aicoding.backend.dto.UserRequest;
import com.aicoding.backend.model.User;
import com.aicoding.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户管理 REST API。
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** 查询用户列表 */
    @GetMapping
    public ApiResponse<List<User>> list() {
        return ApiResponse.success(userService.listUsers());
    }

    /** 按 ID 查询用户 */
    @GetMapping("/{id}")
    public ApiResponse<User> get(@PathVariable Long id) {
        return ApiResponse.success(userService.getUser(id));
    }

    /** 新增用户 */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<User> create(@Valid @RequestBody UserRequest request) {
        return ApiResponse.success("创建成功", userService.createUser(request));
    }

    /** 更新用户 */
    @PutMapping("/{id}")
    public ApiResponse<User> update(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        return ApiResponse.success("更新成功", userService.updateUser(id, request));
    }

    /** 删除用户 */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.success("删除成功", null);
    }
}
