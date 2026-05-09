import request from './request'

// 单位管理API
export const unitApi = {
  // 获取单位列表（分页）
  getList(params) {
    return request({
      url: '/units',
      method: 'get',
      params
    })
  },

  // 获取所有单位
  getAll() {
    return request({
      url: '/units/all',
      method: 'get'
    })
  },

  // 获取所有单位（别名）
  all() {
    return request.get('/units/all')
  },

  // 获取单位树形结构
  getTree() {
    return request({
      url: '/units/tree',
      method: 'get'
    })
  },

  // 获取单位详情
  getById(id) {
    return request({
      url: `/units/${id}`,
      method: 'get'
    })
  },

  // 创建单位
  create(data) {
    return request({
      url: '/units',
      method: 'post',
      data
    })
  },

  // 更新单位
  update(id, data) {
    return request({
      url: `/units/${id}`,
      method: 'put',
      data
    })
  },

  // 删除单位
  delete(id) {
    return request({
      url: `/units/${id}`,
      method: 'delete'
    })
  },

  // 检查编码是否存在
  checkCode(code, excludeId) {
    return request({
      url: '/units/check-code',
      method: 'get',
      params: { code, excludeId }
    })
  }
}
