<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="aside">
      <div class="logo">
        <el-icon size="28" color="#fff"><OfficeBuilding /></el-icon>
        <span v-show="!isCollapse" class="title">单位管理系统</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        router
        background-color="#1f2937"
        text-color="#9ca3af"
        active-text-color="#fff"
        class="sidebar-menu"
      >
        <el-sub-menu index="org">
          <template #title>
            <el-icon><OfficeBuilding /></el-icon>
            <span>组织管理</span>
          </template>
          <el-menu-item index="/units">
            <el-icon><List /></el-icon>
            <span>单位列表</span>
          </el-menu-item>
          <el-menu-item index="/unit-tree">
            <el-icon><Share /></el-icon>
            <span>组织架构</span>
          </el-menu-item>
          <el-menu-item index="/departments">
            <el-icon><Folder /></el-icon>
            <span>部门管理</span>
          </el-menu-item>
          <el-menu-item index="/positions">
            <el-icon><Briefcase /></el-icon>
            <span>岗位管理</span>
          </el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/users">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/roles">
            <el-icon><Key /></el-icon>
            <span>角色管理</span>
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <el-container class="main-container">
      <!-- 顶栏 -->
      <el-header class="header">
        <div class="header-left">
          <el-button :icon="isCollapse ? Expand : Fold" circle @click="toggleCollapse" />
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-dropdown">
              <el-avatar :size="32" :icon="UserFilled" />
              <span class="username">{{ currentUser?.nickname || currentUser?.username || '用户' }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon> 个人设置
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon> 退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容 -->
      <el-main class="main">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import {
  OfficeBuilding, List, Share, Folder, Briefcase,
  Setting, User, Key, Fold, Expand, UserFilled, ArrowDown, SwitchButton
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const isCollapse = ref(false)

const activeMenu = computed(() => route.path)
const currentTitle = computed(() => route.meta?.title || '首页')
const currentUser = computed(() => authStore.user)

const toggleCollapse = () => { isCollapse.value = !isCollapse.value }

const handleCommand = (command) => {
  if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', { type: 'warning' }).then(() => {
      authStore.logout()
      ElMessage.success('已退出登录')
      router.push('/login')
    }).catch(() => {})
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.aside {
  background: linear-gradient(180deg, #1f2937 0%, #111827 100%);
  transition: width 0.3s;
  overflow-x: hidden;
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}

.logo .title {
  font-size: 16px;
  font-weight: 600;
  color: #fff;
  white-space: nowrap;
}

.sidebar-menu {
  border-right: none;
}

.sidebar-menu :deep(.el-sub-menu__title),
.sidebar-menu :deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background: #409EFF;
  border-radius: 8px;
  margin: 4px 8px;
}

.main-container {
  background: #f3f4f6;
}

.header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
  height: 64px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #374151;
}

.username {
  font-size: 14px;
}

.main {
  padding: 0;
  overflow-y: auto;
  background: #f3f4f6;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
