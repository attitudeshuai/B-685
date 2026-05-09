<template>
  <div class="page-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="部门名称">
          <el-input v-model="searchForm.name" placeholder="请输入部门名称" clearable />
        </el-form-item>
        <el-form-item label="所属单位">
          <el-select v-model="searchForm.unitId" placeholder="请选择" clearable style="width: 180px">
            <el-option v-for="u in units" :key="u.id" :label="u.name" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 120px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon> 查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon> 重置
          </el-button>
          <el-button type="success" @click="handleAdd">
            <el-icon><Plus /></el-icon> 新增
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="部门名称" min-width="150" />
        <el-table-column prop="code" label="部门编码" width="120" />
        <el-table-column prop="leader" label="负责人" width="100" />
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
        class="pagination"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="所属单位" prop="unitId">
          <el-select v-model="form.unitId" placeholder="请选择" style="width: 100%">
            <el-option v-for="u in units" :key="u.id" :label="u.name" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="上级部门">
          <el-tree-select v-model="form.parentId" :data="deptTree" 
            :props="{ label: 'name', value: 'id', children: 'children' }"
            placeholder="请选择上级部门" clearable check-strictly style="width: 100%" />
        </el-form-item>
        <el-form-item label="部门名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="部门编码">
          <el-input v-model="form.code" placeholder="请输入部门编码" />
        </el-form-item>
        <el-form-item label="负责人">
          <el-input v-model="form.leader" placeholder="请输入负责人" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
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
import { departmentApi } from '@/api/system'
import { unitApi } from '@/api/unit'

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增部门')
const tableData = ref([])
const units = ref([])
const deptTree = ref([])
const formRef = ref(null)

const searchForm = reactive({ name: '', unitId: null, status: null })
const pagination = reactive({ page: 1, size: 10, total: 0 })
const form = reactive({
  id: null, name: '', code: '', unitId: null, parentId: null,
  leader: '', phone: '', email: '', sortOrder: 0, status: 1, remark: ''
})

const rules = {
  unitId: [{ required: true, message: '请选择所属单位', trigger: 'change' }],
  name: [{ required: true, message: '请输入部门名称', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await departmentApi.list({
      ...searchForm, page: pagination.page, size: pagination.size
    })
    tableData.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const loadUnits = async () => {
  const res = await unitApi.all()
  units.value = res.data || []
}

const loadDeptTree = async () => {
  const res = await departmentApi.tree()
  deptTree.value = res.data || []
}

const handleSearch = () => { pagination.page = 1; loadData() }
const handleReset = () => {
  Object.assign(searchForm, { name: '', unitId: null, status: null })
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增部门'
  Object.assign(form, { id: null, name: '', code: '', unitId: null, parentId: null, leader: '', phone: '', email: '', sortOrder: 0, status: 1, remark: '' })
  loadDeptTree()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑部门'
  Object.assign(form, row)
  loadDeptTree()
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该部门吗？', '提示', { type: 'warning' }).then(async () => {
    await departmentApi.delete(row.id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (form.id) {
      await departmentApi.update(form.id, form)
      ElMessage.success('更新成功')
    } else {
      await departmentApi.create(form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    submitting.value = false
  }
}

const resetForm = () => formRef.value?.resetFields()

onMounted(() => { loadData(); loadUnits() })
</script>

<style scoped>
.page-container { padding: 20px; }
.search-card { margin-bottom: 20px; }
.table-card { padding: 20px; }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
