<template>
  <div class="page-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="月份">
          <el-date-picker v-model="searchForm.month" type="month" placeholder="选择月份"
            format="YYYY年MM月" value-format="YYYY-MM" style="width:160px" />
        </el-form-item>
        <el-form-item label="部门">
          <el-tree-select v-model="searchForm.deptId" :data="deptTree"
            :props="{ label: 'name', value: 'id', children: 'children' }"
            placeholder="请选择" clearable check-strictly style="width: 180px" />
        </el-form-item>
        <el-form-item label="员工">
          <el-select v-model="searchForm.userId" placeholder="请选择" clearable filterable style="width:160px">
            <el-option v-for="u in users" :key="u.id" :label="u.nickname || u.username" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadSummary"><el-icon><Search /></el-icon> 查询</el-button>
          <el-button @click="handleReset"><el-icon><Refresh /></el-icon> 重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table :data="summaryData" v-loading="loading" stripe border>
        <el-table-column prop="userId" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="deptName" label="部门" min-width="140" />
        <el-table-column prop="totalDays" label="应出勤" width="90" align="center" />
        <el-table-column prop="normalDays" label="正常" width="80" align="center">
          <template #default="{ row }">
            <el-tag type="success" size="small">{{ row.normalDays }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lateDays" label="迟到" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.lateDays > 0 ? 'danger' : 'info'" size="small">{{ row.lateDays }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="earlyDays" label="早退" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.earlyDays > 0 ? 'warning' : 'info'" size="small">{{ row.earlyDays }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="absentDays" label="缺勤" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.absentDays > 0 ? 'danger' : 'info'" size="small">{{ row.absentDays }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="showDetail(row)">详细记录</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="detailTitle" v-model="detailVisible" width="800px">
      <el-table :data="detailRecords" v-loading="detailLoading" stripe border size="small">
        <el-table-column prop="attendanceDate" label="日期" width="120" />
        <el-table-column label="上班打卡" width="180">
          <template #default="{ row }">
            {{ formatTime(row.clockInTime) || '-' }}
            <el-tag v-if="row.clockInStatus" :type="row.clockInStatus === '正常' ? 'success' : 'danger'" size="small" style="margin-left:4px">
              {{ row.clockInStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="下班打卡" width="180">
          <template #default="{ row }">
            {{ formatTime(row.clockOutTime) || '-' }}
            <el-tag v-if="row.clockOutStatus" :type="row.clockOutStatus === '正常' ? 'success' : 'warning'" size="small" style="margin-left:4px">
              {{ row.clockOutStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="120" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import { attendanceApi } from '@/api/attendance'
import { userApi, departmentApi } from '@/api/system'

const loading = ref(false)
const detailLoading = ref(false)
const detailVisible = ref(false)
const summaryData = ref([])
const detailRecords = ref([])
const users = ref([])
const deptTree = ref([])

const searchForm = reactive({
  month: new Date().toISOString().slice(0, 7),
  deptId: null,
  userId: null
})

const detailTitle = computed(() => '打卡详细记录')

const formatTime = (time) => {
  if (!time) return ''
  if (typeof time === 'string') return time.replace('T', ' ').substring(0, 19)
  return new Date(time).toLocaleString('zh-CN', { hour12: false })
}

const loadSummary = async () => {
  if (!searchForm.month) { ElMessage.warning('请选择月份'); return }
  loading.value = true
  try {
    const [year, month] = searchForm.month.split('-').map(Number)
    const res = await attendanceApi.getMonthlySummary({ year, month, userId: searchForm.userId, deptId: searchForm.deptId })
    summaryData.value = res.data || []
  } finally { loading.value = false }
}

const handleReset = () => {
  searchForm.month = new Date().toISOString().slice(0, 7)
  searchForm.deptId = null
  searchForm.userId = null
  loadSummary()
}

const showDetail = async (row) => {
  detailVisible.value = true
  detailLoading.value = true
  try {
    const [year, month] = searchForm.month.split('-').map(Number)
    const res = await attendanceApi.getMonthlyRecords(row.userId, year, month)
    detailRecords.value = res.data || []
  } finally { detailLoading.value = false }
}

const loadUsers = async () => {
  try {
    const res = await userApi.list({ page: 1, size: 1000 })
    users.value = res.data?.records || []
  } catch { users.value = [] }
}

const loadDeptTree = async () => {
  try {
    const res = await departmentApi.tree()
    deptTree.value = res.data || []
  } catch { deptTree.value = [] }
}

onMounted(() => { loadSummary(); loadUsers(); loadDeptTree() })
</script>

<style scoped>
.page-container { padding: 20px; }
.search-card { margin-bottom: 20px; }
.table-card { padding: 20px; }
</style>
