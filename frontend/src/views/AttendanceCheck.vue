<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <span>考勤打卡</span>
      </template>

      <div class="check-in-section">
        <div class="current-time">
          <div class="date">{{ currentDate }}</div>
          <div class="time">{{ currentTime }}</div>
        </div>

        <div class="check-buttons">
          <el-button
            type="primary"
            size="large"
            :class="['check-btn', 'check-in-btn']"
            :disabled="todayAttendance?.checkInTime"
            :loading="checkingIn"
            @click="handleCheckIn"
          >
            {{ todayAttendance?.checkInTime ? '已打卡上班' : '上班打卡' }}
          </el-button>
          <el-button
            type="success"
            size="large"
            :class="['check-btn', 'check-out-btn']"
            :disabled="!todayAttendance?.checkInTime || todayAttendance?.checkOutTime"
            :loading="checkingOut"
            @click="handleCheckOut"
          >
            {{ todayAttendance?.checkOutTime ? '已打卡下班' : '下班打卡' }}
          </el-button>
        </div>

        <div v-if="todayAttendance" class="today-record">
          <h4>今日打卡记录</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="上班时间">
              {{ formatTime(todayAttendance.checkInTime) }}
            </el-descriptions-item>
            <el-descriptions-item label="下班时间">
              {{ formatTime(todayAttendance.checkOutTime) }}
            </el-descriptions-item>
            <el-descriptions-item label="上班状态">
              <el-tag :type="getStatusType(todayAttendance.status)">
                {{ getStatusText(todayAttendance.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="工作时长">
              {{ todayAttendance.workHours ? todayAttendance.workHours.toFixed(2) + ' 小时' : '-' }}
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
    </el-card>

    <el-card style="margin-top: 20px">
      <template #header>
        <span>我的考勤记录</span>
        <div style="float: right">
          <el-date-picker
            v-model="dateRange"
            type="monthrange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="loadMyAttendance"
          />
        </div>
      </template>

      <el-table :data="myAttendanceList" border>
        <el-table-column prop="attendanceDate" label="日期" width="120" />
        <el-table-column prop="checkInTime" label="上班时间" width="180">
          <template #default="{ row }">{{ formatTime(row.checkInTime) }}</template>
        </el-table-column>
        <el-table-column prop="checkOutTime" label="下班时间" width="180">
          <template #default="{ row }">{{ formatTime(row.checkOutTime) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="workHours" label="工作时长" width="120">
          <template #default="{ row }">
            {{ row.workHours ? row.workHours.toFixed(2) + 'h' : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="checkInLocation" label="上班地点" />
        <el-table-column prop="checkOutLocation" label="下班地点" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { attendanceApi } from '@/api/attendance'

const authStore = useAuthStore()
const checkingIn = ref(false)
const checkingOut = ref(false)
const todayAttendance = ref(null)
const myAttendanceList = ref([])
const currentTime = ref('')
const currentDate = ref('')

const dateRange = ref([
  new Date(new Date().getFullYear(), new Date().getMonth(), 1).toISOString().split('T')[0],
  new Date().toISOString().split('T')[0]
])

let timer = null

const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleTimeString('zh-CN', { hour12: false })
  currentDate.value = now.toLocaleDateString('zh-CN', { 
    year: 'numeric', 
    month: 'long', 
    day: 'numeric',
    weekday: 'long'
  })
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN', { hour12: false })
}

const getStatusType = (status) => {
  const map = {
    NORMAL: 'success',
    LATE: 'warning',
    EARLY_LEAVE: 'warning',
    LATE_AND_EARLY: 'danger'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    NORMAL: '正常',
    LATE: '迟到',
    EARLY_LEAVE: '早退',
    LATE_AND_EARLY: '迟到早退'
  }
  return map[status] || '未知'
}

const loadTodayAttendance = async () => {
  const user = authStore.getUser()
  if (!user) return
  try {
    const res = await attendanceApi.getTodayAttendance(user.id)
    todayAttendance.value = res.data
  } catch (e) {
    console.error('加载今日考勤失败', e)
  }
}

const loadMyAttendance = async () => {
  const user = authStore.getUser()
  if (!user || !dateRange.value || dateRange.value.length < 2) return
  try {
    const res = await attendanceApi.getMyAttendance(user.id, dateRange.value[0], dateRange.value[1])
    myAttendanceList.value = res.data || []
  } catch (e) {
    ElMessage.error('加载考勤记录失败')
  }
}

const handleCheckIn = async () => {
  const user = authStore.getUser()
  if (!user) return
  checkingIn.value = true
  try {
    await attendanceApi.checkIn(user.id)
    ElMessage.success('上班打卡成功')
    await loadTodayAttendance()
  } catch (e) {
    ElMessage.error(e.message || '打卡失败')
  } finally {
    checkingIn.value = false
  }
}

const handleCheckOut = async () => {
  const user = authStore.getUser()
  if (!user) return
  checkingOut.value = true
  try {
    await attendanceApi.checkOut(user.id)
    ElMessage.success('下班打卡成功')
    await loadTodayAttendance()
    await loadMyAttendance()
  } catch (e) {
    ElMessage.error(e.message || '打卡失败')
  } finally {
    checkingOut.value = false
  }
}

onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 1000)
  loadTodayAttendance()
  loadMyAttendance()
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.check-in-section {
  text-align: center;
  padding: 30px 0;
}

.current-time {
  margin-bottom: 40px;
}

.current-time .date {
  font-size: 18px;
  color: #666;
  margin-bottom: 10px;
}

.current-time .time {
  font-size: 48px;
  font-weight: bold;
  color: #333;
  font-family: 'Courier New', monospace;
}

.check-buttons {
  display: flex;
  justify-content: center;
  gap: 40px;
  margin-bottom: 40px;
}

.check-btn {
  width: 150px;
  height: 50px;
  font-size: 18px;
  border-radius: 8px;
}

.check-in-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.check-out-btn {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  border: none;
}

.today-record {
  max-width: 600px;
  margin: 0 auto;
  text-align: left;
}

.today-record h4 {
  margin-bottom: 15px;
  color: #333;
}
</style>
