---
kind: frontend_style
name: 基于 Element Plus 的 Vue 3 前端样式体系
category: frontend_style
scope:
    - '**'
source_files:
    - frontend/src/styles/index.css
    - frontend/src/views/UserListView.vue
    - frontend/package.json
    - frontend/src/App.vue
---

## 1. 采用的样式方案

前端采用 **Vue 3 + Vite + Element Plus** 的组合，UI 风格完全由 Element Plus 组件库提供。项目没有引入 CSS 预处理器（如 Sass/Less）、CSS-in-JS、Tailwind 或任何原子化框架，仅使用原生 CSS 与 Vue 单文件组件的 `<style scoped>`。

- 构建工具：Vite（`vite.config.ts`），通过 `@vitejs/plugin-vue` 支持 `.vue` 文件的 `<style scoped>`。
- UI 组件库：`element-plus` v2.9.1 及 `@element-plus/icons-vue`，所有页面布局、表单、表格、分页、弹窗等均由 Element Plus 组件构成。
- 全局样式入口：`src/styles/index.css` 在应用初始化时注入，负责基础 reset 和主题基色。

## 2. 关键文件

- `frontend/src/styles/index.css`：全局样式重置与主题基色定义。
- `frontend/src/views/UserListView.vue`：唯一业务视图，集中展示了组件库的使用方式与局部样式组织。
- `frontend/package.json`：声明了 `element-plus`、`@element-plus/icons-vue`、`axios`、`vue-router` 等依赖。
- `frontend/src/App.vue`：根组件，仅包含 `<router-view />`，无额外样式。

## 3. 架构与约定

### 3.1 全局样式策略
`src/styles/index.css` 定义了最少的全局规则：
- 对 `html`、`body`、`#app` 设置 `height: 100%` 与 `margin: 0`，确保全屏布局。
- 为 `body` 指定字体栈 `'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif`，背景色 `#f5f7fa`，文字色 `#303133`，并启用 `-webkit-font-smoothing: antialiased`。
- 未定义任何设计令牌（token）变量或 CSS 自定义属性；颜色值以硬编码形式出现。

### 3.2 组件级样式策略
业务视图 `UserListView.vue` 使用 Vue 的 `<style scoped>` 块组织局部样式，命名采用 BEM/语义化类名（如 `.page-layout`、`.page-header`、`.header-left`、`.toolbar`、`.pagination-wrapper`）。这些样式仅作用于当前组件模板中的 DOM，避免污染全局命名空间。

### 3.3 主题与品牌色
项目未自定义 Element Plus 主题（未配置 `el-config-provider` 的主题 token 覆盖），直接沿用 Element Plus 默认浅色主题。页面中出现的品牌/强调色均通过 Element Plus 的内置类型（如 `type="primary"`、`type="danger"`、`type="info"`）获得，而非通过 CSS 变量覆盖。

### 3.4 响应式策略
未发现媒体查询或响应式设计代码。页面布局依赖 Element Plus 的栅格与弹性布局能力（如 `el-container`、`el-header`、`el-main`），但并未针对移动端做特殊适配。

## 4. 约定与约束

- **样式来源单一**：所有视觉呈现来自 Element Plus 组件 + 少量 `<style scoped>` 局部样式，不编写独立的全局业务 CSS 文件。
- **不使用 CSS 预处理器**：项目中不存在 `.scss` / `.less` 文件，也未安装 sass/less 依赖。
- **不使用 CSS 模块化或 CSS Modules**：组件内样式全部通过 Vue 的 `scoped` 机制隔离，未见 `.module.css` 或 `css-loader` 相关配置。
- **颜色与字体集中在全局**：全局字体栈与基础背景/文字色集中在 `src/styles/index.css`，业务组件不再重复定义。
- **图标统一通过 Element Plus Icons**：所有图标从 `@element-plus/icons-vue` 导入（如 `Plus`、`Edit`、`Delete`、`Refresh`、`Search`），未引入第三方图标库。
- **无设计令牌系统**：未使用 CSS 自定义属性、SCSS 变量或 Tailwind 配置来抽象颜色/间距/字号，新增样式时需遵循现有硬编码数值风格。
- **无响应式断点**：未定义 `@media` 查询，布局假设桌面端宽度。

总体而言，该项目的样式体系非常轻量：以 Element Plus 作为唯一的视觉与交互风格来源，辅以极少量的全局 reset 和组件级 `scoped` 样式，适合小型演示型前端应用的快速开发。