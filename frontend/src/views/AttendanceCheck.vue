<template>
  <div class="page-container">
    <el-card class="check-card">
      <div class="check-header">
        <div class="date-info">
          <div class="date">{{ currentDate }}</div>
          <div class="weekday">{{ currentWeekday }}</div>
          <div class="time">{{ currentTime }}</div>
        </div>
      </div>

      <div class="check-status">
        <div class="status-item">
          <div class="status-label">上班打卡</div>
          <div class="status-time" :class="{ checked: todayRecord?.checkInTime }">
            {{ todayRecord?.checkInTime ? formatTime(todayRecord.checkInTime) : '--:--' }}
          </div>
          <el-tag v-if="todayRecord?.checkInTime" :type="getStatusType(todayRecord.status, 'checkIn')" size="small">
            {{ getCheckInStatusText(todayRecord) }}
          </el-tag>
        </div>
        <div class="status-divider">
          <el-icon :size="24" color="#409EFF"><ArrowRight /></el-icon>
        </div>
        <div class="status-item">
          <div class="status-label">下班打卡</div>
          <div class="status-time" :class="{ checked: todayRecord?.checkOutTime }">
            {{ todayRecord?.checkOutTime ? formatTime(todayRecord.checkOutTime) : '--:--' }}
          </div>
          <el-tag v-if="todayRecord?.checkOutTime" :type="getStatusType(todayRecord.status, 'checkOut')" size="small">
            {{ getCheckOutStatusText(todayRecord) }}
          </el-tag>
        </div>
      </div>

      <div class="check-buttons">
        <el-button type="primary" size="large" :disabled="!!todayRecord?.checkInTime" :loading="checkingIn" @click="handleCheckIn">
          <el-icon><Sunny /></el-icon> 上班打卡
        </el-button>
        <el-button type="success" size="large" :disabled="!todayRecord?.checkInTime || !!todayRecord?.checkOutTime" :loading="checkingOut" @click="handleCheckOut">
          <el-icon><Moon /></el-icon> 下班打卡
        </el-button>
      </div>
    </el-card>

    <el-card class="records-card">
      <template #header>
        <div class="card-header">
          <span>本月考勤记录</span>
        </div>
      </template>
      <el-table :data="myRecords" v-loading="loadingRecords" stripe border>
        <el-table-column prop="recordDate" label="日期" width="120" />
        <el-table-column prop="checkInTime" label="上班时间" width="180">
          <template #default="{ row }">{{ row.checkInTime ? formatDateTime(row.checkInTime) : '--' }}</template>
        </el-table-column>
        <el-table-column prop="checkOutTime" label="下班时间" width="180">
          <template #default="{ row }">{{ row.checkOutTime ? formatDateTime(row.checkOutTime) : '--' }}</template>
        </el-table-column>
        <el-table-column prop="statusText" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getTagType(row.status)" size="small">{{ row.statusText }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Sunny, Moon, ArrowRight } from '@element-plus/icons-vue'
import { attendanceApi } from '@/api/attendance'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const userId = ref(authStore.user?.id)

const todayRecord = ref(null)
const myRecords = ref([])
const checkingIn = ref(false)
const checkingOut = ref(false)
const loadingRecords = ref(false)
const currentTime = ref('')
let timer = null

const currentDate = new Date().toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' })
const currentWeekday = new Date().toLocaleDateString('zh-CN', { weekday: 'long' })

const updateTime = () => {
  currentTime.value = new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit', second: '2-digit' })
}

const loadTodayRecord = async () => {
  const res = await attendanceApi.getTodayRecord(userId.value)
  todayRecord.value = res.data
}

const loadMyRecords = async () => {
  loadingRecords.value = true
  try {
    const res = await attendanceApi.getMyRecords(userId.value)
    myRecords.value = res.data || []
  } finally { loadingRecords.value = false }
}

const handleCheckIn = async () => {
  checkingIn.value = true
  try {
    await attendanceApi.checkIn(userId.value)
    ElMessage.success('上班打卡成功')
    loadTodayRecord()
    loadMyRecords()
  } finally { checkingIn.value = false }
}

const handleCheckOut = async () => {
  checkingOut.value = true
  try {
    await attendanceApi.checkOut(userId.value)
    ElMessage.success('下班打卡成功')
    loadTodayRecord()
    loadMyRecords()
  } finally { checkingOut.value = false }
}

const formatTime = (dateTime) => {
  const d = new Date(dateTime)
  return d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

const formatDateTime = (dateTime) => {
  const d = new Date(dateTime)
  return d.toLocaleString('zh-CN', { hour: '2-digit', minute: '2-digit', second: '2-digit' })
}

const getStatusType = (status, type) => {
  if (type === 'checkIn' && status === 2) return 'warning'
  if (type === 'checkOut' && status === 3) return 'warning'
  return 'success'
}

const getCheckInStatusText = (record) => {
  if (record.status === 2) return '迟到'
  return '正常'
}

const getCheckOutStatusText = (record) => {
  if (record.status === 3) return '早退'
  return '正常'
}

const getTagType = (status) => {
  switch (status) {
    case 1: return 'success'
    case 2: return 'warning'
    case 3: return 'warning'
    case 4: return 'danger'
    case 5: return 'danger'
    default: return 'info'
  }
}

onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 1000)
  loadTodayRecord()
  loadMyRecords()
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.page-container { padding: 20px; }
.check-card { margin-bottom: 20px; }
.check-header { text-align: center; padding: 20px; }
.date-info .date { font-size: 24px; font-weight: 600; color: #303133; }
.date-info .weekday { font-size: 16px; color: #606266; margin: 8px 0; }
.date-info .time { font-size: 36px; font-weight: 700; color: #409EFF; font-family: 'Courier New', monospace; }

.check-status { display: flex; justify-content: center; align-items: center; gap: 40px; padding: 30px 0; }
.status-item { text-align: center; min-width: 120px; }
.status-label { font-size: 14px; color: #909399; margin-bottom: 8px; }
.status-time { font-size: 28px; font-weight: 600; color: #909399; margin-bottom: 8px; }
.status-time.checked { color: #67C23A; }
.status-divider { opacity: 0.3; }

.check-buttons { display: flex; justify-content: center; gap: 40px; padding: 20px 0; }
.check-buttons .el-button { min-width: 160px; height: 56px; font-size: 16px; border-radius: 28px; }

.card-header { font-weight: 600; }
</style>
