import request from './request'
import type { ApiResponse } from '@/types/api'
import type { User, UserForm } from '@/types/user'

/** 查询用户列表 */
export function listUsers() {
  return request.get<ApiResponse<User[]>, ApiResponse<User[]>>('/users')
}

/** 查询单个用户 */
export function getUser(id: number) {
  return request.get<ApiResponse<User>, ApiResponse<User>>(`/users/${id}`)
}

/** 新增用户 */
export function createUser(data: UserForm) {
  return request.post<ApiResponse<User>, ApiResponse<User>, UserForm>('/users', data)
}

/** 更新用户 */
export function updateUser(id: number, data: UserForm) {
  return request.put<ApiResponse<User>, ApiResponse<User>, UserForm>(`/users/${id}`, data)
}

/** 删除用户 */
export function deleteUser(id: number) {
  return request.delete<ApiResponse<void>, ApiResponse<void>>(`/users/${id}`)
}
