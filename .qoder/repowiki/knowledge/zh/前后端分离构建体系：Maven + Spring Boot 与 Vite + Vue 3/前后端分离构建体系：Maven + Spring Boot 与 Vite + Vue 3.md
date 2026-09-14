---
kind: build_system
name: 前后端分离构建体系：Maven + Spring Boot 与 Vite + Vue 3
category: build_system
scope:
    - '**'
source_files:
    - backend/pom.xml
    - frontend/package.json
    - frontend/vite.config.ts
---

## 1. 使用的构建系统

本项目采用**前后端双工程、各自独立构建**的架构，没有统一的顶层 Makefile 或 CI 流水线。

- **后端（Spring Boot）**：基于 Maven，继承 `spring-boot-starter-parent:3.3.4`，使用 `spring-boot-maven-plugin` 打包为可执行 JAR。
- **前端（Vue 3）**：基于 Vite + TypeScript，通过 `package.json` 中的 npm scripts 管理构建流程。

## 2. 关键文件

- `backend/pom.xml`：后端依赖与构建配置，声明 Java 21、Spring Boot Web/Validation/Test 依赖，以及 `spring-boot-maven-plugin`。
- `frontend/package.json`：前端脚本与依赖声明，包含 `dev`、`build`、`preview` 三个命令。
- `frontend/vite.config.ts`：Vite 开发服务器配置，端口 5173，并将 `/api` 请求代理到 `http://localhost:8080`（即本地运行的 Spring Boot 后端）。

## 3. 构建流程与约定

### 后端构建
- 通过 `mvn package`（由 spring-boot-maven-plugin 驱动）生成可执行 JAR。
- 版本遵循 Maven 快照约定：`0.0.1-SNAPSHOT`。
- 运行入口为 `BackendApplication`，默认监听 8080 端口。

### 前端构建
- `npm run dev`：启动 Vite 开发服务器（端口 5173），内置热重载。
- `npm run build`：先执行 `vue-tsc --noEmit` 进行类型检查，再执行 `vite build` 输出静态资源到 `dist/`。
- `npm run preview`：预览构建产物。
- 源码路径别名 `@` 指向 `src/` 目录。

### 前后端联调
- 开发模式下，前端通过 Vite 的 proxy 将 `/api/*` 转发到后端的 `http://localhost:8080`，实现跨域透明调用。
- 生产部署时，前端静态资源需单独部署，并通过反向代理（如 Nginx）将 `/api` 指向后端服务。

## 4. 约束与缺失

- **无容器化**：仓库中不存在 Dockerfile、docker-compose 等容器编排文件。
- **无 CI/CD**：未发现 `.github/workflows`、Jenkinsfile、Makefile、build.sh 等自动化构建或发布脚本。
- **无统一入口脚本**：根目录没有聚合脚本同时构建前后端，开发者需分别进入 `backend/` 和 `frontend/` 目录执行各自的构建命令。
- **版本策略**：后端使用 Maven 快照版本，前端使用 npm 语义化版本（当前 `0.0.1`），两者独立管理，无同步机制。

## 5. 总结

该项目的构建体系是**最小化的双工程模式**：后端用 Maven + Spring Boot，前端用 Vite + Vue 3，两者通过 npm script 和 Maven plugin 各自完成编译、打包与运行。项目目前仅满足本地开发与基础构建需求，尚未引入容器化、CI/CD 或统一的发布流程。