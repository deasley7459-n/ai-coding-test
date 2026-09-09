<template>
  <el-container class="page-layout">
    <el-header class="page-header">
      <div class="header-left">
        <h1 class="page-title">用户管理</h1>
        <el-tag type="info" effect="plain">Spring Boot 3 + Vue 3 + Element Plus</el-tag>
      </div>
      <el-button :icon="Refresh" @click="fetchUsers">刷新</el-button>
    </el-header>

    <el-main>
      <el-card shadow="never">
        <div class="toolbar">
          <el-input
            v-model="keyword"
            class="search-input"
            placeholder="按姓名 / 邮箱搜索"
            clearable
            :prefix-icon="Search"
          />
          <el-button type="primary" :icon="Plus" @click="openCreateDialog">新增用户</el-button>
        </div>

        <el-table v-loading="loading" :data="pagedUsers" border stripe>
          <el-table-column prop="id" label="ID" width="70" align="center" />
          <el-table-column prop="name" label="姓名" min-width="120" />
          <el-table-column prop="email" label="邮箱" min-width="200" />
          <el-table-column label="手机号" min-width="140">
            <template #default="{ row }">
              <span>{{ row.phone || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" min-width="170">
            <template #default="{ row }">
              <span>{{ formatDateTime(row.createdAt) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="170" align="center" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" :icon="Edit" @click="openEditDialog(row)">
                编辑
              </el-button>
              <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="page"
            v-model:page-size="pageSize"
            :total="filteredUsers.length"
            :page-sizes="[5, 10, 20]"
            layout="total, sizes, prev, pager, next, jumper"
            background
          />
        </div>
      </el-card>
    </el-main>
  </el-container>

  <el-dialog
    v-model="dialogVisible"
    :title="isEdit ? '编辑用户' : '新增用户'"
    width="480px"
    :close-on-click-modal="false"
    @closed="resetForm"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="姓名" prop="name">
        <el-input v-model="form.name" placeholder="请输入姓名" maxlength="50" />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="form.email" placeholder="请输入邮箱" maxlength="100" />
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="form.phone" placeholder="请输入手机号（可选）" maxlength="20" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Delete, Edit, Plus, Refresh, Search } from '@element-plus/icons-vue'

import { createUser, deleteUser, listUsers, updateUser } from '@/api/user'
import type { User, UserForm } from '@/types/user'

const loading = ref(false)
const submitting = ref(false)
const users = ref<User[]>([])
const keyword = ref('')

const page = ref(1)
const pageSize = ref(10)

const dialogVisible = ref(false)
const isEdit = ref(false)
const editingId = ref<number | null>(null)

const formRef = ref<FormInstance>()
const form = reactive<UserForm>({
  name: '',
  email: '',
  phone: ''
})

const rules: FormRules<UserForm> = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: ['blur', 'change'] }
  ]
}

/** 关键字过滤（姓名 / 邮箱） */
const filteredUsers = computed(() => {
  const key = keyword.value.trim().toLowerCase()
  if (!key) return users.value
  return users.value.filter(
    (user) => user.name.toLowerCase().includes(key) || user.email.toLowerCase().includes(key)
  )
})

/** 当前页数据（前端分页） */
const pagedUsers = computed(() => {
  const start = (page.value - 1) * pageSize.value
  return filteredUsers.value.slice(start, start + pageSize.value)
})

// 过滤后总页数变少时，自动回退到最后一页
watch(filteredUsers, () => {
  const maxPage = Math.max(1, Math.ceil(filteredUsers.value.length / pageSize.value))
  if (page.value > maxPage) {
    page.value = maxPage
  }
})

async function fetchUsers() {
  loading.value = true
  try {
    const response = await listUsers()
    users.value = response.data
  } catch {
    // 错误已在 axios 拦截器中统一提示
  } finally {
    loading.value = false
  }
}

function openCreateDialog() {
  isEdit.value = false
  editingId.value = null
  dialogVisible.value = true
}

function openEditDialog(user: User) {
  isEdit.value = true
  editingId.value = user.id
  form.name = user.name
  form.email = user.email
  form.phone = user.phone ?? ''
  dialogVisible.value = true
}

function resetForm() {
  formRef.value?.resetFields()
  form.name = ''
  form.email = ''
  form.phone = ''
  editingId.value = null
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().then(() => true).catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const payload: UserForm = {
      name: form.name.trim(),
      email: form.email.trim(),
      phone: form.phone?.trim() || undefined
    }
    if (isEdit.value && editingId.value !== null) {
      await updateUser(editingId.value, payload)
      ElMessage.success('更新成功')
    } else {
      await createUser(payload)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    await fetchUsers()
  } catch {
    // 错误已在 axios 拦截器中统一提示
  } finally {
    submitting.value = false
  }
}

async function handleDelete(user: User) {
  const confirmed = await ElMessageBox.confirm(`确定删除用户「${user.name}」吗？`, '删除确认', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(() => true)
    .catch(() => false)

  if (!confirmed) return

  try {
    await deleteUser(user.id)
    ElMessage.success('删除成功')
    await fetchUsers()
  } catch {
    // 错误已在 axios 拦截器中统一提示
  }
}

function formatDateTime(value: string) {
  if (!value) return '-'
  return value.replace('T', ' ').slice(0, 19)
}

onMounted(fetchUsers)
</script>

<style scoped>
.page-layout {
  min-height: 100vh;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #ffffff;
  border-bottom: 1px solid #e4e7ed;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-title {
  margin: 0;
  font-size: 18px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}

.search-input {
  width: 280px;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
