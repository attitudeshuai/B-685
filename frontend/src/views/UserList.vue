<template>
  <div class="page-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="searchForm.nickname" placeholder="请输入昵称" clearable />
        </el-form-item>
        <el-form-item label="部门">
          <el-tree-select v-model="searchForm.deptId" :data="deptTree"
            :props="{ label: 'name', value: 'id', children: 'children' }"
            placeholder="请选择" clearable check-strictly style="width: 180px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 120px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon> 查询</el-button>
          <el-button @click="handleReset"><el-icon><Refresh /></el-icon> 重置</el-button>
          <el-button type="success" @click="handleAdd"><el-icon><Plus /></el-icon> 新增</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="unitName" label="所属单位" min-width="150" />
        <el-table-column prop="deptName" label="所属部门" min-width="120" />
        <el-table-column prop="positionName" label="岗位" width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column label="角色" min-width="150">
          <template #default="{ row }">
            <el-tag v-for="(role, i) in (row.roleNames || [])" :key="i" size="small" style="margin-right:4px">{{ role }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link @click="handleResetPwd(row)">重置密码</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.size"
        :total="pagination.total" :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData" @current-change="loadData" class="pagination" />
    </el-card>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" placeholder="请输入用户名" :disabled="!!form.id" />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="!form.id">
            <el-form-item label="密码" prop="password">
              <el-input v-model="form.password" type="password" placeholder="请输入密码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="昵称">
              <el-input v-model="form.nickname" placeholder="请输入昵称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属单位">
              <el-select v-model="form.unitId" placeholder="请选择" style="width: 100%" @change="onUnitChange">
                <el-option v-for="u in units" :key="u.id" :label="u.name" :value="u.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属部门">
              <el-tree-select v-model="form.deptId" :data="deptTree"
                :props="{ label: 'name', value: 'id', children: 'children' }"
                placeholder="请选择" check-strictly style="width: 100%" @change="onDeptChange" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="岗位">
              <el-select v-model="form.positionId" placeholder="请选择" style="width: 100%">
                <el-option v-for="p in positions" :key="p.id" :label="p.name" :value="p.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色">
              <el-select v-model="form.roleIds" multiple placeholder="请选择" style="width: 100%">
                <el-option v-for="r in roles" :key="r.id" :label="r.name" :value="r.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio :value="1">启用</el-radio>
                <el-radio :value="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { userApi, departmentApi, positionApi, roleApi } from '@/api/system'
import { unitApi } from '@/api/unit'

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增用户')
const tableData = ref([])
const units = ref([])
const deptTree = ref([])
const positions = ref([])
const roles = ref([])
const formRef = ref(null)

const searchForm = reactive({ username: '', nickname: '', deptId: null, status: null })
const pagination = reactive({ page: 1, size: 10, total: 0 })
const form = reactive({
  id: null, username: '', password: '', nickname: '', unitId: null, deptId: null,
  positionId: null, phone: '', email: '', status: 1, remark: '', roleIds: []
})

const rules = { username: [{ required: true, message: '请输入用户名', trigger: 'blur' }] }

const loadData = async () => {
  loading.value = true
  try {
    const res = await userApi.list({ ...searchForm, page: pagination.page, size: pagination.size })
    tableData.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } finally { loading.value = false }
}

const loadUnits = async () => { const res = await unitApi.all(); units.value = res.data || [] }
const loadDeptTree = async () => { const res = await departmentApi.tree(); deptTree.value = res.data || [] }
const loadRoles = async () => { const res = await roleApi.all(); roles.value = res.data || [] }

const onUnitChange = () => { form.deptId = null; form.positionId = null; loadDeptTree() }
const onDeptChange = async (deptId) => {
  form.positionId = null
  if (deptId) {
    const res = await positionApi.byDept(deptId)
    positions.value = res.data || []
  } else { positions.value = [] }
}

const handleSearch = () => { pagination.page = 1; loadData() }
const handleReset = () => { Object.assign(searchForm, { username: '', nickname: '', deptId: null, status: null }); handleSearch() }

const handleAdd = () => {
  dialogTitle.value = '新增用户'
  Object.assign(form, { id: null, username: '', password: '', nickname: '', unitId: null, deptId: null, positionId: null, phone: '', email: '', status: 1, remark: '', roleIds: [] })
  positions.value = []
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  dialogTitle.value = '编辑用户'
  const res = await userApi.getById(row.id)
  Object.assign(form, res.data || row)
  if (form.deptId) await onDeptChange(form.deptId)
  dialogVisible.value = true
}

const handleResetPwd = (row) => {
  ElMessageBox.confirm('确定要重置该用户密码吗？', '提示', { type: 'warning' }).then(async () => {
    await userApi.resetPassword(row.id, '123456')
    ElMessage.success('密码已重置为123456')
  }).catch(() => {})
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该用户吗？', '提示', { type: 'warning' }).then(async () => {
    await userApi.delete(row.id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (form.id) { await userApi.update(form.id, form); ElMessage.success('更新成功') }
    else { await userApi.create(form); ElMessage.success('创建成功') }
    dialogVisible.value = false
    loadData()
  } finally { submitting.value = false }
}

const resetForm = () => formRef.value?.resetFields()
onMounted(() => { loadData(); loadUnits(); loadDeptTree(); loadRoles() })
</script>

<style scoped>
.page-container { padding: 20px; }
.search-card { margin-bottom: 20px; }
.table-card { padding: 20px; }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
