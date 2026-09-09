# aicoding-test-java

前后端分离的全栈示例项目，实现一个简单的「用户管理」REST API 及对应的前端页面（CRUD 演示）。

## 技术栈

**后端（backend/）**

- Java 21 + Spring Boot 3.3.x（Web / Validation）
- Maven 构建，内存存储（ConcurrentHashMap，可替换为 JPA / MyBatis）

**前端（frontend/）**

- Vue 3（Composition API + `<script setup>`）+ TypeScript
- Vite 5 构建，Vue Router 4 路由
- Element Plus UI 组件库（含图标库）+ Axios 请求封装

## 目录结构

```text
aicoding-test-java/
├── backend/                                  # Spring Boot 后端（端口 8080）
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/aicoding/backend/
│       │   │   ├── BackendApplication.java        # 启动类
│       │   │   ├── config/WebCorsConfig.java      # 跨域配置
│       │   │   ├── controller/UserController.java # REST API
│       │   │   ├── dto/ApiResponse.java           # 统一响应结构
│       │   │   ├── dto/UserRequest.java           # 请求参数 + 校验规则
│       │   │   ├── exception/                     # 业务异常 + 全局异常处理
│       │   │   ├── model/User.java                # 用户实体（record）
│       │   │   ├── repository/UserRepository.java # 内存仓储
│       │   │   └── service/UserService.java       # 业务逻辑 + 示例数据
│       │   └── resources/application.yml
│       └── test/                                  # 集成测试（随机端口）
└── frontend/                                 # Vue 3 前端（端口 5173）
    ├── package.json / vite.config.ts / tsconfig.json
    ├── index.html
    └── src/
        ├── main.ts                          # 入口，注册 Element Plus / Router
        ├── App.vue
        ├── api/                             # axios 封装 + 用户接口
        ├── router/                          # 路由
        ├── styles/                          # 全局样式
        ├── types/                           # TS 类型定义
        └── views/UserListView.vue           # 用户管理页面（表格/搜索/弹窗表单/分页）
```

## 环境要求

| 工具 | 版本 | 说明 |
| --- | --- | --- |
| JDK | 21 | 后端编译运行 |
| Maven | 3.8+ | 后端构建 |
| Node.js | 18+（推荐 20+） | 前端构建 |
| npm | 9+ | 随 Node.js 安装 |

## 快速开始

### 1. 启动后端（端口 8080）

```bash
cd backend
mvn spring-boot:run
```

### 2. 启动前端（端口 5173）

```bash
cd frontend
npm install
npm run dev
```

浏览器访问 http://localhost:5173 即可看到用户管理页面。

> 前端开发服务器已将 `/api` 请求代理到 `http://localhost:8080`；后端同时开启了 CORS，支持前后端分别独立部署调试。

### 3. 生产构建

```bash
# 后端：打包可运行 jar（产物 backend-0.0.1-SNAPSHOT.jar）
cd backend && mvn package
java -jar target/backend-0.0.1-SNAPSHOT.jar

# 前端：构建产物位于 frontend/dist
cd frontend && npm run build
```

## REST API

统一响应结构：`{ "code": 0, "message": "success", "data": ... }`（code = 0 成功，非 0 失败）

| 方法 | 路径 | 说明 | 请求体 |
| --- | --- | --- | --- |
| GET | /api/users | 用户列表 | - |
| GET | /api/users/{id} | 查询单个用户 | - |
| POST | /api/users | 新增用户 | `{ "name", "email", "phone" }` |
| PUT | /api/users/{id} | 更新用户 | `{ "name", "email", "phone" }` |
| DELETE | /api/users/{id} | 删除用户 | - |

### curl 示例

```bash
curl http://localhost:8080/api/users

curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"张三","email":"zhangsan@example.com","phone":"13800138000"}'
```

## 运行测试

```bash
cd backend && mvn test
```

## 说明

- 后端数据保存在内存中，服务重启后重置（启动时自动写入 3 条示例数据）。
- 该项目用于演示标准的前后端分离结构，可在此基础上扩展数据库访问、鉴权、更多页面等能力。
