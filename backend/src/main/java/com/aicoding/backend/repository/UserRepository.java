package com.aicoding.backend.repository;

import com.aicoding.backend.model.User;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 内存版用户仓储，仅用于演示；生产环境请替换为数据库实现（JPA / MyBatis 等）。
 */
@Repository
public class UserRepository {

    private final Map<Long, User> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong();

    /** 新增用户并自动分配 ID */
    public User insert(User user) {
        long id = idGenerator.incrementAndGet();
        LocalDateTime createdAt = user.createdAt() != null ? user.createdAt() : LocalDateTime.now();
        User saved = new User(id, user.name(), user.email(), user.phone(), createdAt);
        store.put(id, saved);
        return saved;
    }

    /** 更新用户（调用方需保证用户存在） */
    public User update(User user) {
        store.put(user.id(), user);
        return user;
    }

    public Optional<User> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(store.get(id));
    }

    public List<User> findAll() {
        return store.values().stream()
                .sorted(Comparator.comparing(User::id))
                .toList();
    }

    public boolean deleteById(Long id) {
        return id != null && store.remove(id) != null;
    }
}
