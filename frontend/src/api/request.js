import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const request = axios.create({
  baseURL: '/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 可以在这里添加token等
    return config
  },
  (error) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    const res = response.data
    // 如果响应码不是200，显示错误信息
    if (res.code && res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  (error) => {
    console.error('响应错误:', error)
    let message = '网络错误，请稍后重试'
    if (error.response) {
      const { status, data } = error.response
      switch (status) {
        case 400:
          message = data?.message || '请求参数错误'
          break
        case 404:
          message = data?.message || '请求的资源不存在'
          break
        case 500:
          message = data?.message || '服务器内部错误'
          break
        default:
          message = data?.message || `请求失败 (${status})`
      }
    }
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default request
