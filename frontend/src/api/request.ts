import axios from 'axios'
import { ElMessage } from 'element-plus'
import type { ApiResponse } from '@/types/api'

/**
 * axios 实例：统一 baseURL、超时时间与响应拦截（错误统一弹窗提示）。
 */
const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.response.use(
  (response) => response.data,
  (error) => {
    const message =
      (error.response?.data as ApiResponse<unknown> | undefined)?.message ??
      error.message ??
      '请求失败'
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default request
