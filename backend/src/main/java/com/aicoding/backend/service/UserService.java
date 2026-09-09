package com.aicoding.backend.service;

import com.aicoding.backend.dto.UserRequest;
import com.aicoding.backend.exception.UserNotFoundException;
import com.aicoding.backend.model.User;
import com.aicoding.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户业务逻辑，并负责初始化演示数据。
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        initSampleData();
    }

    private void initSampleData() {
        createUser(new UserRequest("张三", "zhangsan@example.com", "13800138000"));
        createUser(new UserRequest("李四", "lisi@example.com", "13900139000"));
        createUser(new UserRequest("王五", "wangwu@example.com", null));
    }

    public List<User> listUsers() {
        return userRepository.findAll();
    }

    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public User createUser(UserRequest request) {
        User user = new User(null, request.name(), request.email(), request.phone(), LocalDateTime.now());
        return userRepository.insert(user);
    }

    public User updateUser(Long id, UserRequest request) {
        User existing = getUser(id);
        User updated = new User(existing.id(), request.name(), request.email(), request.phone(), existing.createdAt());
        return userRepository.update(updated);
    }

    public void deleteUser(Long id) {
        if (!userRepository.deleteById(id)) {
            throw new UserNotFoundException(id);
        }
    }
}
