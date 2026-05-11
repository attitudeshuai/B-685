<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <span>考勤管理</span>
      </template>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="打卡记录" name="records">
          <div class="search-form">
            <el-form :inline="true" :model="searchForm">
              <el-form-item label="用户">
                <el-select v-model="searchForm.userId" placeholder="选择用户" clearable style="width: 150px">
                  <el-option v-for="user in userList" :key="user.id" :label="user.nickname || user.username" :value="user.id" />
                </el-select>
              </el-form-item>
              <el-form-item label="日期范围">
                <el-date-picker
                  v-model="searchForm.dateRange"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  value-format="YYYY-MM-DD"
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="loadAttendanceList">查询</el-button>
                <el-button @click="resetSearch">重置</el-button>
              </el-form-item>
            </el-form>
          </div>

          <el-table :data="attendanceList" border>
            <el-table-column prop="username" label="用户名" width="120" />
            <el-table-column prop="nickname" label="姓名" width="120" />
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
            <el-table-column prop="workHours" label="工作时长" width="100">
              <template #default="{ row }">
                {{ row.workHours ? row.workHours.toFixed(2) + 'h' : '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="checkInLocation" label="上班地点" />
            <el-table-column prop="checkOutLocation" label="下班地点" />
          </el-table>

          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.size"
            :total="pagination.total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="loadAttendanceList"
            @current-change="loadAttendanceList"
            style="margin-top: 20px; justify-content: flex-end"
          />
        </el-tab-pane>

        <el-tab-pane label="月度汇总" name="summary">
          <div class="search-form">
            <el-form :inline="true" :model="summaryForm">
              <el-form-item label="单位">
                <el-select v-model="summaryForm.unitId" placeholder="选择单位" clearable style="width: 150px">
                  <el-option v-for="unit in unitList" :key="unit.id" :label="unit.name" :value="unit.id" />
                </el-select>
              </el-form-item>
              <el-form-item label="部门">
                <el-select v-model="summaryForm.deptId" placeholder="选择部门" clearable style="width: 150px">
                  <el-option v-for="dept in deptList" :key="dept.id" :label="dept.name" :value="dept.id" />
                </el-select>
              </el-form-item>
              <el-form-item label="月份">
                <el-date-picker
                  v-model="summaryForm.month"
                  type="month"
                  placeholder="选择月份"
                  value-format="YYYY-MM"
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="loadSummaryList">查询</el-button>
              </el-form-item>
            </el-form>
          </div>

          <el-table :data="summaryList" border>
            <el-table-column prop="username" label="用户名" width="120" />
            <el-table-column prop="nickname" label="姓名" width="120" />
            <el-table-column prop="checkInDays" label="上班天数" width="100">
              <template #default="{ row }">{{ row.checkInDays }}/{{ row.totalWorkDays }}</template>
            </el-table-column>
            <el-table-column prop="checkOutDays" label="下班天数" width="100" />
            <el-table-column prop="lateDays" label="迟到天数" width="100">
              <template #default="{ row }">
                <el-tag type="warning" size="small" v-if="row.lateDays > 0">{{ row.lateDays }}</el-tag>
                <span v-else>0</span>
              </template>
            </el-table-column>
            <el-table-column prop="earlyLeaveDays" label="早退天数" width="100">
              <template #default="{ row }">
                <el-tag type="warning" size="small" v-if="row.earlyLeaveDays > 0">{{ row.earlyLeaveDays }}</el-tag>
                <span v-else>0</span>
              </template>
            </el-table-column>
            <el-table-column prop="absentDays" label="缺勤天数" width="100">
              <template #default="{ row }">
                <el-tag type="danger" size="small" v-if="row.absentDays > 0">{{ row.absentDays }}</el-tag>
                <span v-else>0</span>
              </template>
            </el-table-column>
            <el-table-column prop="totalWorkHours" label="总工时" width="120">
              <template #default="{ row }">{{ row.totalWorkHours ? row.totalWorkHours.toFixed(2) + 'h' : '0h' }}</template>
            </el-table-column>
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link size="small" @click="viewDetail(row)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="detailDialogVisible" title="考勤详情" width="800px">
      <div v-if="currentUser">
        <h4>{{ currentUser.nickname || currentUser.username }} - {{ summaryForm.month }} 考勤详情</h4>
        <el-table :data="userDetailList" border>
          <el-table-column prop="attendanceDate" label="日期" width="120" />
          <el-table-column prop="checkInTime" label="上班时间" width="160">
            <template #default="{ row }">{{ formatTime(row.checkInTime) }}</template>
          </el-table-column>
          <el-table-column prop="checkOutTime" label="下班时间" width="160">
            <template #default="{ row }">{{ formatTime(row.checkOutTime) }}</template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" size="small">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="workHours" label="工时" width="100">
            <template #default="{ row }">{{ row.workHours ? row.workHours.toFixed(2) + 'h' : '-' }}</template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { attendanceApi } from '@/api/attendance'
import { userApi } from '@/api/system'
import { unitApi, departmentApi } from '@/api/system'

const activeTab = ref('records')
const userList = ref([])
const unitList = ref([])
const deptList = ref([])
const attendanceList = ref([])
const summaryList = ref([])
const userDetailList = ref([])
const detailDialogVisible = ref(false)
const currentUser = ref(null)

const searchForm = reactive({
  userId: null,
  dateRange: []
})

const summaryForm = reactive({
  unitId: null,
  deptId: null,
  month: new Date().toISOString().substring(0, 7)
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

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

const loadUsers = async () => {
  try {
    const res = await userApi.list({ page: 1, size: 100 })
    userList.value = res.data?.records || []
  } catch (e) {
    console.error('加载用户列表失败', e)
  }
}

const loadUnits = async () => {
  try {
    const res = await unitApi.all()
    unitList.value = res.data || []
  } catch (e) {
    console.error('加载单位列表失败', e)
  }
}

const loadDepts = async () => {
  try {
    const res = await departmentApi.all()
    deptList.value = res.data || []
  } catch (e) {
    console.error('加载部门列表失败', e)
  }
}

const loadAttendanceList = async () => {
  const params = {
    page: pagination.page,
    size: pagination.size
  }
  if (searchForm.userId) params.userId = searchForm.userId
  if (searchForm.dateRange?.length === 2) {
    params.startDate = searchForm.dateRange[0]
    params.endDate = searchForm.dateRange[1]
  }
  try {
    const res = await attendanceApi.getAttendanceList(params)
    attendanceList.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (e) {
    ElMessage.error('加载考勤记录失败')
  }
}

const loadSummaryList = async () => {
  if (!summaryForm.month) {
    ElMessage.warning('请选择月份')
    return
  }
  const [year, month] = summaryForm.month.split('-').map(Number)
  const params = {
    year,
    month
  }
  if (summaryForm.unitId) params.unitId = summaryForm.unitId
  if (summaryForm.deptId) params.deptId = summaryForm.deptId
  try {
    const res = await attendanceApi.getMonthlySummaryList(params)
    summaryList.value = res.data || []
  } catch (e) {
    ElMessage.error('加载考勤汇总失败')
  }
}

const viewDetail = async (row) => {
  currentUser.value = row
  const [year, month] = summaryForm.month.split('-').map(Number)
  const startDate = `${year}-${String(month).padStart(2, '0')}-01`
  const endDate = new Date(year, month, 0).toISOString().split('T')[0]
  try {
    const res = await attendanceApi.getMyAttendance(row.userId, startDate, endDate)
    userDetailList.value = res.data || []
    detailDialogVisible.value = true
  } catch (e) {
    ElMessage.error('加载详情失败')
  }
}

const resetSearch = () => {
  searchForm.userId = null
  searchForm.dateRange = []
  pagination.page = 1
  loadAttendanceList()
}

onMounted(() => {
  loadUsers()
  loadUnits()
  loadDepts()
  loadAttendanceList()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}
</style>
