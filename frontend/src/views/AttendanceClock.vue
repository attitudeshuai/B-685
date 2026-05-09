<template>
  <div class="page-container">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="clock-card">
          <template #header>
            <div class="card-header">
              <el-icon size="24"><Clock /></el-icon>
              <span>员工考勤打卡</span>
            </div>
          </template>
          <div class="clock-section">
            <div class="current-time">{{ currentTime }}</div>
            <div class="current-date">{{ currentDate }}</div>
            <div class="clock-buttons">
              <el-button type="primary" size="large" :loading="clockInLoading" :disabled="!!todayRecord?.clockInTime"
                @click="handleClockIn" class="clock-btn">
                <el-icon size="20"><Check /></el-icon>
                <span>上班打卡</span>
              </el-button>
              <el-button type="success" size="large" :loading="clockOutLoading" :disabled="!todayRecord?.clockInTime || !!todayRecord?.clockOutTime"
                @click="handleClockOut" class="clock-btn">
                <el-icon size="20"><Check /></el-icon>
                <span>下班打卡</span>
              </el-button>
            </div>
            <div class="status-info" v-if="todayRecord">
              <el-descriptions :column="1" border size="small">
                <el-descriptions-item label="上班打卡时间">
                  {{ formatTime(todayRecord.clockInTime) }}
                  <el-tag v-if="todayRecord.clockInStatus" :type="todayRecord.clockInStatus === '正常' ? 'success' : 'danger'" size="small" style="margin-left:8px">
                    {{ todayRecord.clockInStatus }}
                  </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="下班打卡时间">
                  {{ formatTime(todayRecord.clockOutTime) || '未打卡' }}
                  <el-tag v-if="todayRecord.clockOutStatus" :type="todayRecord.clockOutStatus === '正常' ? 'success' : 'warning'" size="small" style="margin-left:8px">
                    {{ todayRecord.clockOutStatus }}
                  </el-tag>
                </el-descriptions-item>
              </el-descriptions>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="record-card">
          <template #header>
            <div class="card-header">
              <span>本月打卡记录</span>
              <el-date-picker v-model="currentMonth" type="month" placeholder="选择月份"
                format="YYYY年MM月" value-format="YYYY-MM" @change="loadMonthlyRecords" style="width:160px" />
            </div>
          </template>
          <el-table :data="monthlyRecords" v-loading="monthlyLoading" stripe border size="small" max-height="400">
            <el-table-column prop="attendanceDate" label="日期" width="120" />
            <el-table-column label="上班" width="160">
              <template #default="{ row }">
                {{ formatTime(row.clockInTime) || '-' }}
                <el-tag v-if="row.clockInStatus" :type="row.clockInStatus === '正常' ? 'success' : 'danger'" size="small">
                  {{ row.clockInStatus }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="下班" width="160">
              <template #default="{ row }">
                {{ formatTime(row.clockOutTime) || '-' }}
                <el-tag v-if="row.clockOutStatus" :type="row.clockOutStatus === '正常' ? 'success' : 'warning'" size="small">
                  {{ row.clockOutStatus }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Clock, Check } from '@element-plus/icons-vue'
import { attendanceApi } from '@/api/attendance'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const userId = computed(() => authStore.user?.id)

const currentTime = ref('')
const currentDate = ref('')
const todayRecord = ref(null)
const monthlyRecords = ref([])
const clockInLoading = ref(false)
const clockOutLoading = ref(false)
const monthlyLoading = ref(false)
const currentMonth = ref(new Date().toISOString().slice(0, 7))

let timer = null

const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleTimeString('zh-CN', { hour12: false })
  currentDate.value = now.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' })
}

const formatTime = (time) => {
  if (!time) return ''
  if (typeof time === 'string') return time.replace('T', ' ').substring(0, 19)
  return new Date(time).toLocaleString('zh-CN', { hour12: false })
}

const loadTodayRecord = async () => {
  if (!userId.value) return
  try {
    const res = await attendanceApi.getTodayRecord(userId.value)
    todayRecord.value = res.data
  } catch { todayRecord.value = null }
}

const loadMonthlyRecords = async () => {
  if (!userId.value || !currentMonth.value) return
  monthlyLoading.value = true
  try {
    const [year, month] = currentMonth.value.split('-').map(Number)
    const res = await attendanceApi.getMonthlyRecords(userId.value, year, month)
    monthlyRecords.value = res.data || []
  } finally { monthlyLoading.value = false }
}

const handleClockIn = async () => {
  if (!userId.value) { ElMessage.warning('请先登录'); return }
  clockInLoading.value = true
  try {
    const res = await attendanceApi.clockIn(userId.value)
    ElMessage.success('上班打卡成功')
    todayRecord.value = res.data
    loadMonthlyRecords()
  } finally { clockInLoading.value = false }
}

const handleClockOut = async () => {
  if (!userId.value) { ElMessage.warning('请先登录'); return }
  clockOutLoading.value = true
  try {
    const res = await attendanceApi.clockOut(userId.value)
    ElMessage.success('下班打卡成功')
    todayRecord.value = res.data
    loadMonthlyRecords()
  } finally { clockOutLoading.value = false }
}

onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 1000)
  loadTodayRecord()
  loadMonthlyRecords()
})

onUnmounted(() => { if (timer) clearInterval(timer) })
</script>

<style scoped>
.page-container { padding: 20px; }
.clock-card, .record-card { height: 100%; }
.card-header { display: flex; align-items: center; justify-content: space-between; gap: 8px; }
.clock-section { text-align: center; padding: 20px 0; }
.current-time { font-size: 56px; font-weight: 700; color: #303133; font-family: 'Courier New', monospace; }
.current-date { font-size: 16px; color: #909399; margin: 12px 0 30px; }
.clock-buttons { display: flex; justify-content: center; gap: 30px; margin-bottom: 30px; }
.clock-btn { width: 160px; height: 60px; font-size: 18px; border-radius: 12px; }
.status-info { max-width: 400px; margin: 0 auto; }
</style>
