---
kind: logging_system
name: 基于 Spring Boot 默认日志框架的轻量级日志配置
category: logging_system
scope:
    - '**'
source_files:
    - backend/src/main/resources/application.yml
    - backend/src/main/java/com/aicoding/backend/exception/GlobalExceptionHandler.java
    - frontend/src/api/request.ts
---

## 1. 使用的系统/方案

后端采用 Spring Boot 内置的日志体系（底层为 SLF4J + Logback），通过 `application.yml` 中的 `logging.level` 配置项控制日志级别，未引入任何第三方日志库或自定义 Logger 组件。前端未使用浏览器控制台以外的结构化日志机制，仅通过 Element Plus 的 `ElMessage.error` 在请求拦截器中向用户展示错误提示。

## 2. 关键文件

- `backend/src/main/resources/application.yml`：唯一与日志相关的配置文件，定义了应用名及包级日志级别。
- `backend/src/main/java/com/aicoding/backend/exception/GlobalExceptionHandler.java`：全局异常处理器，将异常信息以统一 JSON 响应返回，但未显式记录日志。
- `frontend/src/api/request.ts`：Axios 请求拦截器，捕获网络/业务错误并通过 `ElMessage.error` 弹出提示，属于前端“用户可见”的错误反馈而非服务端日志。

## 3. 架构与约定

- **日志框架**：Spring Boot 自动装配 SLF4J 门面与 Logback 实现；由于项目中没有任何 `import org.slf4j`、`@Slf4j`、`LoggerFactory` 等代码，所有日志输出均由 Spring Boot 内部组件（如 Tomcat、Hibernate、Web MVC）产生。
- **日志级别策略**：在 `application.yml` 中将 `com.aicoding.backend` 包下的日志级别设置为 `info`，即该包内类只输出 `INFO` 及以上级别（WARN、ERROR）的日志；Spring Boot 自身组件使用其默认级别。
- **无业务日志**：Controller、Service、Repository、DTO、Exception 等核心业务类中均未出现日志调用，说明该项目作为演示用途，刻意省略了业务层面的日志埋点。
- **前端错误反馈**：前端不输出结构化日志，仅在 Axios 拦截器中对错误进行统一处理，通过 `ElMessage.error` 向用户展示错误消息，这属于 UI 层的错误提示，不属于服务端日志范畴。

## 4. 约定与约束

- **包级日志级别集中管理**：日志级别通过 `application.yml` 的 `logging.level.com.aicoding.backend: info` 统一设定，未在代码中硬编码日志级别。
- **无自定义日志格式/输出源**：仓库中没有 `logback.xml`、`logback-spring.xml` 或其他日志配置文件，也未定义自定义 Appender、PatternLayout 或日志路由规则。
- **无结构化日志字段**：由于没有业务日志输出，不存在统一的日志字段规范（如 traceId、userId、requestId 等）。
- **异常不记录日志**：`GlobalExceptionHandler` 直接构造 `ApiResponse.error(...)` 返回给客户端，未调用任何日志接口记录异常堆栈，这意味着生产环境中服务器端不会保留异常上下文日志。
- **前端无日志模块**：前端未集成 `console.log` 调试输出或第三方日志 SDK，错误仅通过 UI 提示暴露给用户。

总结：该项目的日志系统处于最简状态——仅依赖 Spring Boot 默认配置，开启 `com.aicoding.backend` 包的 `INFO` 级别日志，业务代码中未主动埋点，也没有自定义日志格式或输出目标。