import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', public: true }
  },
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    redirect: '/units',
    children: [
      {
        path: 'units',
        name: 'UnitList',
        component: () => import('@/views/UnitList.vue'),
        meta: { title: '单位列表' }
      },
      {
        path: 'unit-tree',
        name: 'UnitTree',
        component: () => import('@/views/UnitTree.vue'),
        meta: { title: '组织架构' }
      },
      {
        path: 'departments',
        name: 'DepartmentList',
        component: () => import('@/views/DepartmentList.vue'),
        meta: { title: '部门管理' }
      },
      {
        path: 'positions',
        name: 'PositionList',
        component: () => import('@/views/PositionList.vue'),
        meta: { title: '岗位管理' }
      },
      {
        path: 'users',
        name: 'UserList',
        component: () => import('@/views/UserList.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'roles',
        name: 'RoleList',
        component: () => import('@/views/RoleList.vue'),
        meta: { title: '角色管理' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        meta: { title: '个人设置' }
      },
      {
        path: 'attendance/check',
        name: 'AttendanceCheck',
        component: () => import('@/views/AttendanceCheck.vue'),
        meta: { title: '考勤打卡' }
      },
      {
        path: 'attendance/admin',
        name: 'AttendanceAdmin',
        component: () => import('@/views/AttendanceAdmin.vue'),
        meta: { title: '考勤统计' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const user = localStorage.getItem('user')
  if (!to.meta.public && !user) {
    next('/login')
  } else if (to.path === '/login' && user) {
    next('/')
  } else {
    next()
  }
})

export default router
