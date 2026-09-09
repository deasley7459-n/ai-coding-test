/** 用户实体（后端返回） */
export interface User {
  id: number
  name: string
  email: string
  phone: string | null
  createdAt: string
}

/** 新增 / 编辑用户提交的表单 */
export interface UserForm {
  name: string
  email: string
  phone?: string
}
