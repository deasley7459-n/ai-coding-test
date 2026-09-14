---
kind: error_handling
name: 前后端统一错误处理体系：Spring Boot 全局异常 + Axios 响应拦截
category: error_handling
scope:
    - '**'
source_files:
    - backend/src/main/java/com/aicoding/backend/dto/ApiResponse.java
    - backend/src/main/java/com/aicoding/backend/exception/GlobalExceptionHandler.java
    - backend/src/main/java/com/aicoding/backend/exception/UserNotFoundException.java
    - frontend/src/types/api.ts
    - frontend/src/api/request.ts
---

## 1. 整体方案

该项目采用**前后端分离的统一错误协议**：后端通过 Spring Boot `@RestControllerAdvice` 将各类异常转换为统一的 `ApiResponse<T>` JSON 结构，前端通过 Axios 响应拦截器统一捕获并弹窗提示。错误码约定为 `code = 0` 表示成功，非 0 表示失败。

## 2. 后端（Spring Boot）

### 2.1 统一响应体
- `backend/src/main/java/com/aicoding/backend/dto/ApiResponse.java`：使用 Java record 定义 `{ code, message, data }`，提供 `success(...)` / `error(code, message)` 静态工厂方法。约定 `code=0` 为成功。

### 2.2 业务异常类型
- `backend/src/main/java/com/aicoding/backend/exception/UserNotFoundException.java`：自定义 `RuntimeException`，构造时携带用户 ID 并生成可读消息（如 `"用户不存在: id=123"`）。

### 2.3 全局异常处理器
- `backend/src/main/java/com/aicoding/backend/exception/GlobalExceptionHandler.java`：通过 `@RestControllerAdvice` 集中处理所有控制器异常，映射关系如下：
  - `UserNotFoundException` → `404 NOT_FOUND` → `ApiResponse.error(404, ...)`
  - `MethodArgumentNotValidException`（JSR-303 Bean 校验失败）→ `400 BAD_REQUEST`，拼接所有字段错误为 `field: message; field2: message2` 形式
  - `HttpMessageNotReadableException`（请求体缺失或 JSON 解析失败）→ `400 BAD_REQUEST`，固定消息 `"请求体格式错误"`
  - `MethodArgumentTypeMismatchException`（路径参数类型不匹配，如 `/api/users/abc`）→ `400 BAD_REQUEST`，提示 `"参数类型不正确: <参数名>"`
  - `Exception`（兜底）→ `500 INTERNAL_SERVER_ERROR`，返回 `"服务器内部错误: <异常信息>"`

### 2.4 调用方式
- Service 层通过 `throw new UserNotFoundException(id)` 抛出业务异常，由全局处理器自动转换，无需在 Controller 中 try-catch。

## 3. 前端（Vue 3 + Axios）

### 3.1 统一响应类型
- `frontend/src/types/api.ts`：定义 TypeScript 接口 `ApiResponse<T>`，与后端 `ApiResponse` 字段一一对应（`code`, `message`, `data`）。

### 3.2 Axios 实例与响应拦截器
- `frontend/src/api/request.ts`：创建带 `baseURL=/api`、`timeout=10000` 的 axios 实例；注册响应拦截器：
  - 成功分支：直接返回 `response.data`（即后端的 `ApiResponse<T>`），调用方拿到的是泛型数据而非完整包装对象。
  - 失败分支：优先取 `error.response.data.message`（后端统一错误消息），回退到 `error.message`，再回退到 `'请求失败'`；通过 Element Plus 的 `ElMessage.error(message)` 弹出错误提示；最后 `Promise.reject(error)` 交由调用方继续处理。

### 3.3 调用约定
- 各 API 模块（如 `src/api/user.ts`）直接使用 `request` 实例发起请求，由于成功时已解包 `data`，业务代码只需判断返回值即可；错误由拦截器统一弹窗，无需在每个调用处重复处理。

## 4. 架构与约定

| 层面 | 约定 | 依据 |
|---|---|---|
| 后端响应 | `code=0` 成功，非 0 失败；错误走 `ApiResponse.error(code, message)` | `ApiResponse` 类注释与实现 |
| 业务异常 | 通过抛出自定义 `RuntimeException`（如 `UserNotFoundException`）表达业务错误 | `GlobalExceptionHandler` 中的 `@ExceptionHandler` 映射 |
| 参数校验 | 使用 JSR-303 Bean Validation，校验失败由 `MethodArgumentNotValidException` 统一收集并拼接 | `GlobalExceptionHandler.handleValidation` |
| 网络错误 | 前端 Axios 拦截器统一弹窗，不再让未捕获错误静默失败 | `request.interceptors.response.use` 的 error 回调 |
| HTTP 状态码 | 后端按语义设置 `HttpStatus`（400/404/500），前端仅关心 `ApiResponse.code` | `@ResponseStatus` 注解与前端拦截器逻辑 |

## 5. 约束与规则

- **禁止在 Controller 中自行 try-catch 业务异常**：业务异常应向上抛出，由 `GlobalExceptionHandler` 统一收敛，保证响应格式一致。
- **新增业务异常必须被 `GlobalExceptionHandler` 显式处理**：否则会被兜底的 `Exception` 处理器以 500 返回，丢失语义化错误码。
- **前端所有 HTTP 请求必须通过 `request.ts` 导出的 axios 实例**：确保 baseURL、超时、错误弹窗策略一致。
- **前端调用方不得再次对 `ApiResponse.code` 做分支判断**：因为响应拦截器已在成功时解包 `data`，错误已通过 `ElMessage.error` 展示。

## 6. 关键文件

- `backend/src/main/java/com/aicoding/backend/dto/ApiResponse.java` — 统一响应结构
- `backend/src/main/java/com/aicoding/backend/exception/GlobalExceptionHandler.java` — 全局异常映射
- `backend/src/main/java/com/aicoding/backend/exception/UserNotFoundException.java` — 业务异常示例
- `frontend/src/types/api.ts` — 前端统一响应类型
- `frontend/src/api/request.ts` — Axios 实例与响应拦截器