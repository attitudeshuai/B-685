<template>
  <div class="unit-list">
    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="单位名称">
          <el-input
            v-model="searchForm.name"
            placeholder="请输入单位名称"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="单位编码">
          <el-input
            v-model="searchForm.code"
            placeholder="请输入单位编码"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="RefreshRight" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span class="title">单位列表</span>
          <el-button type="primary" :icon="Plus" @click="handleAdd">新增单位</el-button>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="units"
        stripe
        row-key="id"
        style="width: 100%"
      >
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="name" label="单位名称" min-width="150" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="unit-name">
              <el-icon class="name-icon"><OfficeBuilding /></el-icon>
              <span>{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="code" label="单位编码" width="120" align="center">
          <template #default="{ row }">
            <el-tag type="info" effect="plain">{{ row.code || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="leader" label="负责人" width="100" align="center" />
        <el-table-column prop="phone" label="联系电话" width="140" align="center" />
        <el-table-column prop="address" label="单位地址" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" effect="light">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      destroy-on-close
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        class="unit-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="单位名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入单位名称" maxlength="100" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单位编码" prop="code">
              <el-input v-model="form.code" placeholder="请输入单位编码" maxlength="50" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="上级单位" prop="parentId">
              <el-tree-select
                v-model="form.parentId"
                :data="treeData"
                :props="{ label: 'name', value: 'id', children: 'children' }"
                placeholder="请选择上级单位"
                clearable
                check-strictly
                filterable
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="负责人" prop="leader">
              <el-input v-model="form.leader" placeholder="请输入负责人" maxlength="50" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入联系电话" maxlength="20" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="电子邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入电子邮箱" maxlength="100" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="单位地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入单位地址" maxlength="255" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="排序号" prop="sortOrder">
              <el-input-number v-model="form.sortOrder" :min="0" :max="9999" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio :label="1">启用</el-radio>
                <el-radio :label="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, RefreshRight, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { useUnitStore } from '@/stores/unit'

const unitStore = useUnitStore()

// 状态
const loading = computed(() => unitStore.loading)
const units = computed(() => unitStore.units)
const total = computed(() => unitStore.total)

const dialogVisible = ref(false)
const dialogTitle = ref('新增单位')
const submitting = ref(false)
const formRef = ref(null)
const treeData = ref([])
const editingId = ref(null)

// 搜索表单
const searchForm = reactive({
  name: '',
  code: '',
  status: null
})

// 分页
const pagination = reactive({
  page: 1,
  size: 10
})

// 表单数据
const defaultForm = {
  name: '',
  code: '',
  parentId: null,
  address: '',
  phone: '',
  email: '',
  leader: '',
  sortOrder: 0,
  status: 1,
  remark: ''
}

const form = reactive({ ...defaultForm })

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入单位名称', trigger: 'blur' },
    { max: 100, message: '单位名称长度不能超过100个字符', trigger: 'blur' }
  ],
  code: [
    { max: 50, message: '单位编码长度不能超过50个字符', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^$|^1[3-9]\d{9}$|^0\d{2,3}-?\d{7,8}$/, message: '联系电话格式不正确', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '电子邮箱格式不正确', trigger: 'blur' }
  ]
}

// 加载数据
const loadData = async () => {
  await unitStore.fetchUnits({
    ...searchForm,
    page: pagination.page,
    size: pagination.size
  })
}

// 加载单位树
const loadTree = async () => {
  const res = await unitStore.fetchTree()
  if (res.code === 200) {
    treeData.value = res.data
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadData()
}

// 重置
const handleReset = () => {
  searchForm.name = ''
  searchForm.code = ''
  searchForm.status = null
  pagination.page = 1
  loadData()
}

// 分页变化
const handleSizeChange = () => {
  pagination.page = 1
  loadData()
}

const handleCurrentChange = () => {
  loadData()
}

// 新增
const handleAdd = () => {
  editingId.value = null
  dialogTitle.value = '新增单位'
  Object.assign(form, defaultForm)
  loadTree()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  editingId.value = row.id
  dialogTitle.value = '编辑单位'
  Object.assign(form, {
    name: row.name,
    code: row.code,
    parentId: row.parentId,
    address: row.address,
    phone: row.phone,
    email: row.email,
    leader: row.leader,
    sortOrder: row.sortOrder,
    status: row.status,
    remark: row.remark
  })
  loadTree()
  dialogVisible.value = true
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除单位"${row.name}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const res = await unitStore.deleteUnit(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadData()
    }
  } catch (e) {
    // 取消删除
  }
}

// 提交表单
const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    let res
    if (editingId.value) {
      res = await unitStore.updateUnit(editingId.value, form)
    } else {
      res = await unitStore.createUnit(form)
    }
    
    if (res.code === 200) {
      ElMessage.success(editingId.value ? '更新成功' : '创建成功')
      dialogVisible.value = false
      loadData()
    }
  } finally {
    submitting.value = false
  }
}

// 对话框关闭
const handleDialogClose = () => {
  formRef.value?.resetFields()
  Object.assign(form, defaultForm)
}

// 初始化
onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.unit-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.search-card {
  border-radius: 12px;
  
  .search-form {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    
    .el-form-item {
      margin-bottom: 0;
      margin-right: 16px;
    }
  }
}

.table-card {
  border-radius: 12px;
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .title {
      font-size: 16px;
      font-weight: 600;
      color: #1e293b;
    }
  }
}

.unit-name {
  display: flex;
  align-items: center;
  gap: 8px;
  
  .name-icon {
    color: #3b82f6;
    font-size: 18px;
  }
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.unit-form {
  .el-input-number {
    width: 100%;
  }
}

:deep(.el-table) {
  .el-table__header th {
    background-color: #f8fafc !important;
  }
}
</style>
