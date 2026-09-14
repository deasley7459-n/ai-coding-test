---
kind: dependency_management
name: 前后端双栈依赖管理（Maven + npm）
category: dependency_management
scope:
    - '**'
source_files:
    - backend/pom.xml
    - frontend/package.json
    - frontend/package-lock.json
---

## 1. 使用的系统/工具

本项目采用**前后端分离的双栈依赖管理**：
- **后端（Java/Spring Boot）**：使用 **Maven**，通过 `backend/pom.xml` 声明依赖，继承 `spring-boot-starter-parent:3.3.4` 作为父 POM 统一管理 Spring 生态组件版本。
- **前端（Vue 3 + Vite）**：使用 **npm**，通过 `frontend/package.json` 声明运行时与开发依赖，并通过 `frontend/package-lock.json`（lockfileVersion 3）锁定精确版本。

未使用私有仓库或代理配置，所有包均从默认公共源拉取（npm 指向 `https://registry.npmjs.org`）。

## 2. 关键文件

- `backend/pom.xml`：后端唯一依赖清单，定义 Java 21、Spring Boot Starter Web/Validation/Test 等依赖及构建插件。
- `frontend/package.json`：前端依赖清单，区分 `dependencies`（vue、axios、element-plus 等）与 `devDependencies`（vite、typescript、vue-tsc 等）。
- `frontend/package-lock.json`：npm 锁文件，记录完整依赖树及每个包的精确版本与 integrity hash。
- `frontend/node_modules/`：已安装的依赖目录（由 npm 生成，见 `.gitignore` 排除）。
- `backend/target/`：Maven 编译产物目录（同样被忽略）。

## 3. 架构与约定

- **父 POM 版本托管**：后端通过 `<parent>` 引入 `spring-boot-starter-parent`，不显式指定各 Spring 组件版本，由父 POM 的 BOM 统一协调，避免版本冲突。
- **语言版本固定**：`pom.xml` 中 `<properties><java.version>21</java.version></properties>` 明确限定运行环境为 JDK 21。
- **依赖范围划分**：测试依赖使用 `<scope>test</scope>`（如 `spring-boot-starter-test`），生产依赖仅包含运行时必需项。
- **前端依赖按用途拆分**：`dependencies` 仅包含应用启动所需的库；构建、类型检查、打包工具放在 `devDependencies`，确保生产构建最小化。
- **锁文件策略**：前端提交 `package-lock.json`，保证团队成员和 CI 安装得到完全一致的依赖树；后端无 lock 文件，依赖解析由 Maven 远程仓库决定。
- **无 vendoring**：项目未将第三方源码纳入版本控制（`node_modules` 与 `target` 均在 `.gitignore` 中），依赖以“可重现安装”的方式管理。

## 4. 约定与约束

- 新增后端依赖需添加到 `backend/pom.xml` 的 `<dependencies>` 段，并遵循 Maven 坐标（groupId/artifactId/version）规范。
- 新增前端依赖需写入 `frontend/package.json` 对应区域（`dependencies` 或 `devDependencies`），并通过 `npm install` 更新 `package-lock.json`。
- 版本升级应优先通过各自包管理器（`mvn versions` / `npm update`）进行，并同步提交锁文件变更以保证可重复构建。
- 未配置私有 npm registry 或 Maven mirror，所有依赖解析依赖默认公共源可达性。