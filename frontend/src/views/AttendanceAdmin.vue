<template>
  <div class="page-container">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="考勤明细" name="records">
        <el-card class="search-card">
          <el-form :inline="true" :model="searchForm" class="search-form">
            <el-form-item label="用户">
              <el-select v-model="searchForm.userId" placeholder="请选择" clearable filterable style="width: 180px">
                <el-option v-for="u in users" :key="u.id" :label="u.nickname || u.username" :value="u.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="日期范围">
              <el-date-picker v-model="dateRange" type="daterange" range-separator="至"
                start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon> 查询</el-button>
              <el-button @click="handleReset"><el-icon><Refresh /></el-icon> 重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card class="table-card">
          <el-table :data="tableData" v-loading="loading" stripe border>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="username" label="用户名" width="120" />
            <el-table-column prop="nickname" label="姓名" width="120" />
            <el-table-column prop="recordDate" label="考勤日期" width="120" />
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
          <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.size"
            :total="pagination.total" :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="loadRecords" @current-change="loadRecords" class="pagination" />
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="月度汇总" name="summary">
        <el-card class="search-card">
          <el-form :inline="true" :model="summaryForm" class="search-form">
            <el-form-item label="月份">
              <el-date-picker v-model="monthPicker" type="month" placeholder="选择月份" value-format="YYYY-MM" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadSummary"><el-icon><Search /></el-icon> 查询</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card class="table-card">
          <el-table :data="summaryData" v-loading="loadingSummary" stripe border>
            <el-table-column prop="username" label="用户名" width="120" />
            <el-table-column prop="nickname" label="姓名" width="120" />
            <el-table-column prop="year" label="年" width="80" />
            <el-table-column prop="month" label="月" width="80" />
            <el-table-column prop="totalDays" label="考勤天数" width="100" />
            <el-table-column prop="normalDays" label="正常" width="80">
              <template #default="{ row }"><el-tag type="success" size="small">{{ row.normalDays }}</el-tag></template>
            </el-table-column>
            <el-table-column prop="lateDays" label="迟到" width="80">
              <template #default="{ row }"><el-tag type="warning" size="small">{{ row.lateDays }}</el-tag></template>
            </el-table-column>
            <el-table-column prop="earlyDays" label="早退" width="80">
              <template #default="{ row }"><el-tag type="warning" size="small">{{ row.earlyDays }}</el-tag></template>
            </el-table-column>
            <el-table-column prop="absentDays" label="缺勤" width="80">
              <template #default="{ row }"><el-tag type="danger" size="small">{{ row.absentDays }}</el-tag></template>
            </el-table-column>
            <el-table-column prop="attendanceRate" label="出勤率" width="120">
              <template #default="{ row }">
                <el-progress :percentage="Math.round(row.attendanceRate || 0)" :stroke-width="12" />
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search, Refresh } from '@element-plus/icons-vue'
import { attendanceApi } from '@/api/attendance'
import { userApi } from '@/api/system'

const activeTab = ref('records')
const loading = ref(false)
const loadingSummary = ref(false)
const tableData = ref([])
const summaryData = ref([])
const users = ref([])
const dateRange = ref([])
const monthPicker = ref(new Date().toISOString().slice(0, 7))

const searchForm = reactive({ userId: null, startDate: null, endDate: null })
const summaryForm = reactive({ year: new Date().getFullYear(), month: new Date().getMonth() + 1 })
const pagination = reactive({ page: 1, size: 10, total: 0 })

const formatDateTime = (dateTime) => {
  const d = new Date(dateTime)
  return d.toLocaleString('zh-CN', { hour: '2-digit', minute: '2-digit', second: '2-digit' })
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

const loadUsers = async () => {
  const res = await userApi.list({ page: 1, size: 100 })
  users.value = res.data?.records || []
}

const loadRecords = async () => {
  loading.value = true
  try {
    const params = {
      userId: searchForm.userId,
      startDate: dateRange.value?.[0] || null,
      endDate: dateRange.value?.[1] || null,
      page: pagination.page,
      size: pagination.size
    }
    const res = await attendanceApi.getAllRecords(params)
    tableData.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } finally { loading.value = false }
}

const loadSummary = async () => {
  if (monthPicker.value) {
    const [year, month] = monthPicker.value.split('-').map(Number)
    summaryForm.year = year
    summaryForm.month = month
  }
  loadingSummary.value = true
  try {
    const res = await attendanceApi.getMonthlySummary(summaryForm.year, summaryForm.month)
    summaryData.value = res.data || []
  } finally { loadingSummary.value = false }
}

const handleSearch = () => { pagination.page = 1; loadRecords() }
const handleReset = () => { searchForm.userId = null; dateRange.value = []; handleSearch() }

onMounted(() => {
  loadUsers()
  loadRecords()
  loadSummary()
})
</script>

<style scoped>
.page-container { padding: 20px; }
.search-card { margin-bottom: 20px; }
.table-card { padding: 20px; }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
