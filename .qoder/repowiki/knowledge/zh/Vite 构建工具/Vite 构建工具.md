---
kind: external_dependency
name: Vite 构建工具
slug: vite
category: external_dependency
category_hints:
    - vendor_identity
    - framework_behavior
scope:
    - '**'
---

### Vite
- 角色：前端构建与开发服务器，提供热更新与快速打包。
- 集成点：`package.json` 中 `dev/build/preview` 脚本调用 `vite`；`vite.config.ts` 引入 `@vitejs/plugin-vue`，配置路径别名 `@ → ./src`、开发端口 `5173`，并将 `/api` 请求代理到 `http://localhost:8080`。
- 稳定用法：生产构建产物输出至 `frontend/dist`，由 Nginx 或后端静态资源服务托管。