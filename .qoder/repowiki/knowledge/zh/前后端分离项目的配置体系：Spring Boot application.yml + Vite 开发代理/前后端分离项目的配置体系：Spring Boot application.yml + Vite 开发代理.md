---
kind: configuration_system
name: 前后端分离项目的配置体系：Spring Boot application.yml + Vite 开发代理
category: configuration_system
scope:
    - '**'
source_files:
    - backend/src/main/resources/application.yml
    - backend/src/main/java/com/aicoding/backend/config/WebCorsConfig.java
    - frontend/vite.config.ts
    - frontend/package.json
    - backend/pom.xml
---

## 1. 使用的系统与工具

本项目采用**前后端分离**架构，因此存在两套独立的配置系统：
- **后端（Spring Boot）**：使用 Spring Boot 默认的 `application.yml` 配置文件，通过 `@Configuration` + `WebMvcConfigurer` 以 Java 代码形式声明式配置跨域（CORS）。
- **前端（Vite + Vue 3）**：使用 `vite.config.ts` 作为构建与开发服务器配置入口，并通过 `package.json` 的 scripts 定义运行命令。

项目未引入外部配置中心、环境变量注入框架或 `.env` 文件，所有运行时参数均以硬编码方式集中在上述配置文件中。

## 2. 关键文件

- `backend/src/main/resources/application.yml`：Spring Boot 应用主配置，定义服务端口、应用名称与日志级别。
- `backend/src/main/java/com/aicoding/backend/config/WebCorsConfig.java`：以 `@Configuration` 类实现 `WebMvcConfigurer`，集中声明 `/api/**` 的 CORS 策略。
- `frontend/vite.config.ts`：Vite 构建与开发服务器配置，定义别名、端口与 `/api` 反向代理到 `http://localhost:8080`。
- `frontend/package.json`：定义 `dev`/`build`/`preview` 脚本以及依赖版本。
- `backend/pom.xml`：Maven 工程元数据，声明 Spring Boot 3.3.4 父 POM、Java 21 目标版本及 web/validation/test starter。

## 3. 架构与设计约定

### 后端配置
- 所有可配置项集中在 `application.yml` 中：`server.port=8080`、`spring.application.name=aicoding-backend`、`logging.level.com.aicoding.backend=info`。
- 非数据类的行为配置（如跨域）通过 Java 配置类 `WebCorsConfig` 声明，而非 YAML。该配置类注释明确说明“允许本地前端开发服务器（Vite 默认 5173 端口）访问后端接口”，并将 allowed origin patterns 固定为 `http://localhost:5173` 与 `http://127.0.0.1:5173`。
- 数据库等外部资源未在配置中出现——结合仓库结构可知后端使用内存仓储（`UserRepository`），因此不存在数据库连接串、JPA/Hibernate 等配置项。

### 前端配置
- 开发环境通过 Vite 的 `server.proxy` 将浏览器对 `/api` 的请求转发到 `http://localhost:8080`，并开启 `changeOrigin: true`，从而绕过同源限制，配合后端的 CORS 配置完成联调。
- 路径别名 `@` 指向 `./src`，由 `fileURLToPath(new URL('./src', import.meta.url))` 解析，便于在 TypeScript/Vue 中使用 `@/xxx` 导入。
- 构建产物输出目录为默认的 `dist/`，无自定义打包输出路径配置。

### 前后端协作约定
- 后端对外暴露的 API 统一以 `/api` 前缀开头（见 `WebCorsConfig` 中的 `addMapping("/api/**")`）。
- 前端 Axios 请求也统一走 `/api` 路径，在开发模式下由 Vite 代理转发，在生产模式下需由部署网关或 Nginx 做相同转发。

## 4. 约定与约束

| 领域 | 约定 / 约束 | 依据 |
|---|---|---|
| 后端端口 | 默认监听 `8080` | `application.yml` 中 `server.port: 8080` |
| 应用命名 | 应用名为 `aicoding-backend` | `spring.application.name` |
| 日志级别 | 包级日志 `com.aicoding.backend` 设为 `info` | `application.yml` 的 `logging.level` |
| CORS 白名单 | 仅允许 `http://localhost:5173` 与 `http://127.0.0.1:5173` 两个来源 | `WebCorsConfig.allowedOriginPatterns` |
| CORS 方法 | 允许 GET/POST/PUT/DELETE/OPTIONS | `WebCorsConfig.allowedMethods` |
| 凭证 | 允许携带 Cookie（`allowCredentials(true)`） | `WebCorsConfig` |
| 前端开发端口 | Vite 默认 `5173` | `vite.config.ts` 的 `server.port` 与 CORS 白名单相互呼应 |
| API 路由前缀 | 统一使用 `/api` | 前后端两处配置共同约定 |
| 构建脚本 | 使用 `npm run dev/build/preview` | `package.json` scripts |
| Java 版本 | 编译目标为 Java 21 | `pom.xml` 的 `<java.version>21</java.version>` |
| 依赖管理 | Maven 继承 Spring Boot 3.3.4 父 POM，使用 starter 模式 | `pom.xml` parent 与 dependencies |

## 5. 缺失与边界

- 项目中**没有**任何 `.env`、`.env.*`、`application-{profile}.yml`、`bootstrap.yml`、`config/` 下的多环境配置，也未使用 `@Value`/`@ConfigurationProperties` 读取外部化配置，因此不具备多环境切换能力。
- 未引入任何密钥管理（如 Vault、AWS Secrets Manager）或 Feature Flag 机制，所有开关均为硬编码。
- 前端同样没有区分开发/生产的环境变量（如 `VITE_API_BASE_URL`），API 地址直接写死在 Vite 代理中。

综上，本项目的配置系统是一个**最小化的演示级配置**：后端用 Spring Boot 内置的 `application.yml` + 一个 Java `@Configuration` 类，前端用 `vite.config.ts` 完成开发代理，两者通过 `/api` 前缀与端口约定协同工作。