import { post } from './request'

// 登录
export const login = (username, password) => {
  return post('/auth/login', { username, password })
}

// 注册
export const register = (userData) => {
  return post('/auth/register', userData)
}

// 用户相关API可以在这里扩展
export const userApi = {
  // 获取用户信息
  getUserInfo: (userId) => post('/user/info', { userId }),

  // 更新用户信息
  updateUserInfo: (userInfo) => post('/user/update', userInfo),
}

// 电影相关API
export const movieApi = {
  // 获取电影列表
  getMovies: (params) => post('/movie/list', params),

  // 获取电影详情
  getMovieDetail: (movieId) => post('/movie/detail', { movieId }),

  // 收藏电影
  collectMovie: (userId, movieId) => post('/movie/collect', { userId, movieId }),
}

// 管理员相关API
export const adminApi = {
  // 获取用户列表
  getUsers: (params) => post('/admin/users', params),

  // 获取订单列表
  getOrders: (params) => post('/admin/orders', params),

  // 管理电影
  manageMovie: (movieData) => post('/admin/movie/manage', movieData),
}
