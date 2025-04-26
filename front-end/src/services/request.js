import axios from 'axios'
import { message } from 'ant-design-vue'

// 创建axios实例
const request = axios.create({
  baseURL: '/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    return config
  },
  (error) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  },
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    if (response.data.code === 200) {
      return response.data
    } else {
      message.error(response.data.message)
      return Promise.reject(response.data)
    }
  },
  (error) => {
    // 处理响应错误
    let errorMessage = '请求失败，请稍后再试'

    if (error.response) {
      // 服务器返回了错误状态码
      const { status, data } = error.response

      if (data && data.message) {
        errorMessage = data.message
      } else {
        switch (status) {
          case 400:
            errorMessage = '请求参数错误'
            break
          case 401:
            errorMessage = '未授权，请重新登录'
            break
          case 403:
            errorMessage = '没有权限执行此操作'
            break
          case 404:
            errorMessage = '请求的资源不存在'
            break
          case 500:
            errorMessage = '服务器内部错误'
            break
          default:
            errorMessage = `请求失败(${status})`
        }
      }
    } else if (error.request) {
      // 请求已发送但没有收到响应
      errorMessage = '服务器无响应，请检查网络连接'
    }

    message.error(errorMessage)
    return Promise.reject(error)
  },
)

// 封装GET请求
export const get = (url, params) => {
  return request.get(url, { params })
}

// 封装POST请求
export const post = (url, data) => {
  return request.post(url, data)
}

// 封装PUT请求
export const put = (url, data) => {
  return request.put(url, data)
}

// 封装DELETE请求
export const del = (url, params) => {
  return request.delete(url, { params })
}

export default request
