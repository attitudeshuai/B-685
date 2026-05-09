import request from './request'

// 部门管理API
export const departmentApi = {
  list: (params) => request.get('/departments', { params }),
  getById: (id) => request.get(`/departments/${id}`),
  create: (data) => request.post('/departments', data),
  update: (id, data) => request.put(`/departments/${id}`, data),
  delete: (id) => request.delete(`/departments/${id}`),
  tree: (unitId) => request.get('/departments/tree', { params: { unitId } }),
  all: () => request.get('/departments/all')
}

// 岗位管理API
export const positionApi = {
  list: (params) => request.get('/positions', { params }),
  getById: (id) => request.get(`/positions/${id}`),
  create: (data) => request.post('/positions', data),
  update: (id, data) => request.put(`/positions/${id}`, data),
  delete: (id) => request.delete(`/positions/${id}`),
  all: () => request.get('/positions/all'),
  byDept: (deptId) => request.get(`/positions/by-dept/${deptId}`)
}

// 用户管理API
export const userApi = {
  list: (params) => request.get('/users', { params }),
  getById: (id) => request.get(`/users/${id}`),
  create: (data) => request.post('/users', data),
  update: (id, data) => request.put(`/users/${id}`, data),
  delete: (id) => request.delete(`/users/${id}`),
  resetPassword: (id, password) => request.put(`/users/${id}/reset-password`, { password })
}

// 角色管理API
export const roleApi = {
  list: (params) => request.get('/roles', { params }),
  getById: (id) => request.get(`/roles/${id}`),
  create: (data) => request.post('/roles', data),
  update: (id, data) => request.put(`/roles/${id}`, data),
  delete: (id) => request.delete(`/roles/${id}`),
  all: () => request.get('/roles/all')
}
