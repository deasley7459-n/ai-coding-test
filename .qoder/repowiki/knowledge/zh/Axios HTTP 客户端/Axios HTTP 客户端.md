---
kind: external_dependency
name: Axios HTTP 客户端
slug: axios
category: external_dependency
category_hints:
    - vendor_identity
    - framework_behavior
scope:
    - '**'
---

### Axios
- 角色：前端 HTTP 客户端，封装统一的 baseURL、超时、拦截器与错误处理。
- 稳定用法：业务 API（`api/user.ts`）仅调用封装后的函数，不直接操作 axios 实例；开发环境由 Vite 代理将 `/api/**` 转发到后端 `http://localhost:8080`。