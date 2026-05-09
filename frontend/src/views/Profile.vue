<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <span>个人设置</span>
      </template>
      
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px" style="max-width: 500px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号（选填）" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱（选填）" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 20px">
      <template #header>
        <span>修改密码</span>
      </template>
      
      <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="100px" style="max-width: 500px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="pwdForm.oldPassword" type="password" placeholder="请输入原密码" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="pwdForm.newPassword" type="password" placeholder="请输入新密码" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="pwdForm.confirmPassword" type="password" placeholder="请确认新密码" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleChangePwd" :loading="changingPwd">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { authApi } from '@/api/auth'

const authStore = useAuthStore()
const formRef = ref(null)
const pwdFormRef = ref(null)
const saving = ref(false)
const changingPwd = ref(false)

const form = reactive({
  id: null,
  username: '',
  nickname: '',
  phone: '',
  email: ''
})

// 手机号验证器（可选）
const validatePhone = (rule, value, callback) => {
  if (!value || value === '') {
    callback() // 空值允许通过
  } else if (!/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('请输入正确的手机号'))
  } else {
    callback()
  }
}

// 邮箱验证器（可选）
const validateEmail = (rule, value, callback) => {
  if (!value || value === '') {
    callback() // 空值允许通过
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
    callback(new Error('请输入正确的邮箱'))
  } else {
    callback()
  }
}

const formRules = {
  phone: [{ validator: validatePhone, trigger: 'blur' }],
  email: [{ validator: validateEmail, trigger: 'blur' }]
}

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }, { min: 6, message: '密码至少6位', trigger: 'blur' }],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: (rule, value, callback) => {
      if (value !== pwdForm.newPassword) callback(new Error('两次密码不一致'))
      else callback()
    }, trigger: 'blur' }
  ]
}

const loadProfile = () => {
  const user = authStore.getUser()
  if (user) {
    Object.assign(form, {
      id: user.id,
      username: user.username || '',
      nickname: user.nickname || '',
      phone: user.phone || '',
      email: user.email || ''
    })
  }
}

const handleSave = async () => {
  await formRef.value.validate() // 验证表单
  saving.value = true
  try {
    await authApi.updateProfile(form)
    const user = authStore.getUser()
    authStore.setUser({ ...user, ...form })
    ElMessage.success('保存成功')
  } catch (e) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

const handleChangePwd = async () => {
  await pwdFormRef.value.validate()
  changingPwd.value = true
  try {
    const user = authStore.getUser()
    await authApi.changePassword({
      userId: user.id,
      ...pwdForm
    })
    ElMessage.success('密码修改成功')
    Object.assign(pwdForm, { oldPassword: '', newPassword: '', confirmPassword: '' })
  } catch (e) {
    ElMessage.error('密码修改失败')
  } finally {
    changingPwd.value = false
  }
}

onMounted(loadProfile)
</script>

<style scoped>
.page-container { padding: 20px; }
</style>
