---
kind: external_dependency
name: Vue 3 前端框架
slug: vue-3
category: external_dependency
category_hints:
    - vendor_identity
    - framework_behavior
scope:
    - '**'
---

### Vue 3
- 角色：前端 UI 框架，采用 Composition API + `<script setup>` 语法组织组件逻辑。
- 稳定用法：配合 `vue-router 4` 做单页路由（`createWebHistory` 模式），页面按路由懒加载；全局样式与 Element Plus 在入口统一注册。