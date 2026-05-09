<template>
  <div class="auth-container">
    <!-- 左侧品牌区 -->
    <div class="auth-brand">
      <div class="brand-content">
        <div class="brand-icon">
          <el-icon size="80"><OfficeBuilding /></el-icon>
        </div>
        <h1>单位管理系统</h1>
        <p>Unit Management System</p>
        <div class="features">
          <div class="feature-item">
            <el-icon><CircleCheck /></el-icon>
            <span>组织架构管理</span>
          </div>
          <div class="feature-item">
            <el-icon><CircleCheck /></el-icon>
            <span>部门岗位配置</span>
          </div>
          <div class="feature-item">
            <el-icon><CircleCheck /></el-icon>
            <span>用户角色权限</span>
          </div>
        </div>
      </div>
      <div class="brand-footer">
        <p>© 2024 Unit Management. All rights reserved.</p>
      </div>
    </div>

    <!-- 右侧表单区 -->
    <div class="auth-form-container">
      <div class="auth-form-wrapper">
        <div class="auth-tabs">
          <div :class="['tab', { active: mode === 'login' }]" @click="mode = 'login'">登录</div>
          <div :class="['tab', { active: mode === 'register' }]" @click="mode = 'register'">注册</div>
        </div>

        <!-- 登录表单 -->
        <transition name="slide-fade" mode="out-in">
          <el-form v-if="mode === 'login'" ref="loginFormRef" :model="loginForm" :rules="loginRules" class="auth-form" key="login">
            <h2>欢迎回来</h2>
            <p class="subtitle">请输入您的账号信息登录系统</p>
            
            <el-form-item prop="username">
              <el-input v-model="loginForm.username" placeholder="用户名" size="large">
                <template #prefix><el-icon><User /></el-icon></template>
              </el-input>
            </el-form-item>
            
            <el-form-item prop="password">
              <el-input v-model="loginForm.password" type="password" placeholder="密码" size="large" show-password @keyup.enter="handleLogin">
                <template #prefix><el-icon><Lock /></el-icon></template>
              </el-input>
            </el-form-item>

            <div class="form-options">
              <el-checkbox v-model="rememberMe">记住我</el-checkbox>
            </div>
            
            <el-button type="primary" size="large" :loading="loading" @click="handleLogin" class="submit-btn">
              登 录
            </el-button>

            <div class="demo-account">
              <el-divider>演示账号</el-divider>
              <div class="demo-info">
                <code>admin / 123456</code>
              </div>
            </div>
          </el-form>

          <!-- 注册表单 -->
          <el-form v-else ref="registerFormRef" :model="registerForm" :rules="registerRules" class="auth-form" key="register">
            <h2>创建账号</h2>
            <p class="subtitle">填写以下信息注册新账号</p>
            
            <el-form-item prop="username">
              <el-input v-model="registerForm.username" placeholder="用户名 (登录账号)" size="large">
                <template #prefix><el-icon><User /></el-icon></template>
              </el-input>
            </el-form-item>

            <el-form-item prop="nickname">
              <el-input v-model="registerForm.nickname" placeholder="昵称 (显示名称)" size="large">
                <template #prefix><el-icon><UserFilled /></el-icon></template>
              </el-input>
            </el-form-item>
            
            <el-form-item prop="password">
              <el-input v-model="registerForm.password" type="password" placeholder="密码 (至少6位)" size="large" show-password>
                <template #prefix><el-icon><Lock /></el-icon></template>
              </el-input>
            </el-form-item>

            <el-form-item prop="confirmPassword">
              <el-input v-model="registerForm.confirmPassword" type="password" placeholder="确认密码" size="large" show-password @keyup.enter="handleRegister">
                <template #prefix><el-icon><Lock /></el-icon></template>
              </el-input>
            </el-form-item>
            
            <el-button type="primary" size="large" :loading="loading" @click="handleRegister" class="submit-btn">
              注 册
            </el-button>
          </el-form>
        </transition>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { OfficeBuilding, User, Lock, UserFilled, CircleCheck } from '@element-plus/icons-vue'
import { authApi } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const loginFormRef = ref(null)
const registerFormRef = ref(null)
const loading = ref(false)
const mode = ref('login')
const rememberMe = ref(false)

const loginForm = reactive({ username: '', password: '' })
const registerForm = reactive({ username: '', nickname: '', password: '', confirmPassword: '' })

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度3-20位', trigger: 'blur' }
  ],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: (rule, value, callback) => {
      if (value !== registerForm.password) callback(new Error('两次密码不一致'))
      else callback()
    }, trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  await loginFormRef.value.validate()
  loading.value = true
  try {
    const res = await authApi.login(loginForm)
    if (res.code === 200) {
      authStore.setUser(res.data)
      ElMessage.success('登录成功')
      router.push('/')
    } else {
      ElMessage.error(res.message || '登录失败')
    }
  } catch (e) {
    ElMessage.error('登录失败，请检查用户名密码')
  } finally {
    loading.value = false
  }
}

const handleRegister = async () => {
  await registerFormRef.value.validate()
  loading.value = true
  try {
    const res = await authApi.register(registerForm)
    if (res.code === 200) {
      ElMessage.success('注册成功，请登录')
      mode.value = 'login'
      loginForm.username = registerForm.username
      Object.assign(registerForm, { username: '', nickname: '', password: '', confirmPassword: '' })
    } else {
      ElMessage.error(res.message || '注册失败')
    }
  } catch (e) {
    ElMessage.error('注册失败，请稍后重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-container {
  width: 100%;
  height: 100vh;
  display: flex;
}

/* 左侧品牌区 */
.auth-brand {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 40px;
  color: white;
  position: relative;
  overflow: hidden;
}

.auth-brand::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 60%);
  animation: pulse 15s infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); opacity: 0.5; }
  50% { transform: scale(1.1); opacity: 0.8; }
}

.brand-content {
  text-align: center;
  z-index: 1;
}

.brand-icon {
  background: rgba(255,255,255,0.2);
  width: 140px;
  height: 140px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 30px;
  backdrop-filter: blur(10px);
  animation: float 6s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-20px); }
}

.brand-content h1 {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 10px;
  text-shadow: 0 2px 10px rgba(0,0,0,0.2);
}

.brand-content p {
  font-size: 16px;
  opacity: 0.9;
  margin-bottom: 40px;
}

.features {
  text-align: left;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 16px 0;
  font-size: 16px;
  opacity: 0.95;
}

.feature-item .el-icon {
  font-size: 20px;
}

.brand-footer {
  position: absolute;
  bottom: 30px;
  font-size: 12px;
  opacity: 0.7;
}

/* 右侧表单区 */
.auth-form-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8fafc;
  padding: 40px;
}

.auth-form-wrapper {
  width: 100%;
  max-width: 420px;
  background: white;
  padding: 40px;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.1);
}

.auth-tabs {
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
  border-bottom: 1px solid #e5e7eb;
  padding-bottom: 15px;
}

.tab {
  font-size: 16px;
  color: #9ca3af;
  cursor: pointer;
  padding-bottom: 15px;
  margin-bottom: -16px;
  border-bottom: 2px solid transparent;
  transition: all 0.3s;
}

.tab.active {
  color: #667eea;
  border-bottom-color: #667eea;
  font-weight: 600;
}

.tab:hover {
  color: #667eea;
}

.auth-form h2 {
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 8px;
}

.subtitle {
  color: #6b7280;
  margin-bottom: 30px;
  font-size: 14px;
}

.auth-form :deep(.el-input__wrapper) {
  padding: 12px 15px;
  border-radius: 10px;
  box-shadow: none !important;
  border: 1px solid #e5e7eb;
}

.auth-form :deep(.el-input__wrapper:hover) {
  border-color: #667eea;
}

.auth-form :deep(.el-input__wrapper.is-focus) {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.15) !important;
}

/* 覆盖浏览器自动填充的浅蓝色背景 */
.auth-form :deep(input:-webkit-autofill),
.auth-form :deep(input:-webkit-autofill:hover),
.auth-form :deep(input:-webkit-autofill:focus),
.auth-form :deep(input:-webkit-autofill:active) {
  -webkit-box-shadow: 0 0 0 30px white inset !important;
  box-shadow: 0 0 0 30px white inset !important;
  -webkit-text-fill-color: #1f2937 !important;
  transition: background-color 5000s ease-in-out 0s;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.forgot-link {
  color: #667eea;
  text-decoration: none;
  font-size: 14px;
}

.forgot-link:hover {
  text-decoration: underline;
}

.submit-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.demo-account {
  margin-top: 30px;
}

.demo-account :deep(.el-divider__text) {
  color: #9ca3af;
  font-size: 12px;
}

.demo-info {
  text-align: center;
}

.demo-info code {
  background: #f3f4f6;
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 14px;
  color: #374151;
}

/* 过渡动画 */
.slide-fade-enter-active,
.slide-fade-leave-active {
  transition: all 0.3s ease;
}

.slide-fade-enter-from {
  transform: translateX(20px);
  opacity: 0;
}

.slide-fade-leave-to {
  transform: translateX(-20px);
  opacity: 0;
}

/* 响应式 */
@media (max-width: 900px) {
  .auth-brand {
    display: none;
  }
  
  .auth-form-container {
    padding: 20px;
  }
}
</style>
