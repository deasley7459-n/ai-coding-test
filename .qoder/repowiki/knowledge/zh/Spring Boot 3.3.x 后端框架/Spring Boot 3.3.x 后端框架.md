---
kind: external_dependency
name: Spring Boot 3.3.x 后端框架
slug: spring-boot
category: external_dependency
category_hints:
    - vendor_identity
    - framework_behavior
scope:
    - '**'
---

### Spring Boot
- 角色：后端 Web 框架，提供内嵌 Tomcat、自动配置、Web MVC、Bean Validation（Jakarta）能力。
- 稳定用法：应用以 `server.port=8080` 暴露 REST API（`/api/users`），通过 `application.yml` 配置日志级别与应用名；CORS 在 `WebCorsConfig` 中实现 `WebMvcConfigurer` 对 `/api/**` 放行前端来源。
- 注意：数据层当前为内存 `ConcurrentHashMap`，生产需替换为 JPA/MyBatis 等持久化实现。