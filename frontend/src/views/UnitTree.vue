<template>
  <div class="unit-tree">
    <!-- 操作区域 -->
    <el-card class="action-card" shadow="never">
      <div class="action-bar">
        <div class="action-left">
          <el-input
            v-model="filterText"
            placeholder="搜索单位名称"
            :prefix-icon="Search"
            clearable
            style="width: 240px"
          />
        </div>
        <div class="action-right">
          <el-button :icon="Refresh" @click="loadTree">刷新</el-button>
          <el-button type="primary" @click="expandAll">全部展开</el-button>
          <el-button @click="collapseAll">全部收起</el-button>
        </div>
      </div>
    </el-card>

    <!-- 树形结构 -->
    <el-card class="tree-card" shadow="never">
      <template #header>
        <div class="card-header">
          <el-icon class="header-icon"><Share /></el-icon>
          <span class="title">组织架构图</span>
        </div>
      </template>

      <div v-loading="loading" class="tree-container">
        <el-tree
          ref="treeRef"
          :data="treeData"
          :props="treeProps"
          :filter-node-method="filterNode"
          node-key="id"
          default-expand-all
          :expand-on-click-node="false"
          highlight-current
        >
          <template #default="{ node, data }">
            <div class="tree-node">
              <div class="node-content">
                <el-icon class="node-icon" :class="{ 'has-children': data.children && data.children.length }">
                  <OfficeBuilding v-if="!data.parentId" />
                  <Folder v-else-if="data.children && data.children.length" />
                  <Document v-else />
                </el-icon>
                <span class="node-label">{{ node.label }}</span>
                <el-tag v-if="data.code" size="small" type="info" effect="plain" class="node-code">
                  {{ data.code }}
                </el-tag>
                <el-tag
                  size="small"
                  :type="data.status === 1 ? 'success' : 'danger'"
                  effect="light"
                  class="node-status"
                >
                  {{ data.status === 1 ? '启用' : '禁用' }}
                </el-tag>
              </div>
              <div class="node-actions">
                <el-button type="primary" link size="small" @click.stop="handleViewDetail(data)">
                  <el-icon><View /></el-icon>
                </el-button>
              </div>
            </div>
          </template>
        </el-tree>

        <el-empty v-if="!loading && treeData.length === 0" description="暂无组织架构数据" />
      </div>
    </el-card>

    <!-- 详情抽屉 -->
    <el-drawer
      v-model="drawerVisible"
      title="单位详情"
      direction="rtl"
      size="400px"
    >
      <div v-if="currentUnit" class="unit-detail">
        <div class="detail-header">
          <el-icon class="detail-icon"><OfficeBuilding /></el-icon>
          <h3 class="detail-title">{{ currentUnit.name }}</h3>
        </div>

        <el-descriptions :column="1" border class="detail-desc">
          <el-descriptions-item label="单位编码">
            <el-tag type="info" effect="plain">{{ currentUnit.code || '-' }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="currentUnit.status === 1 ? 'success' : 'danger'" effect="light">
              {{ currentUnit.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="负责人">
            {{ currentUnit.leader || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="联系电话">
            {{ currentUnit.phone || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="电子邮箱">
            {{ currentUnit.email || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="单位地址">
            {{ currentUnit.address || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="排序号">
            {{ currentUnit.sortOrder }}
          </el-descriptions-item>
        </el-descriptions>

        <div v-if="currentUnit.children && currentUnit.children.length" class="sub-units">
          <h4 class="sub-title">
            <el-icon><Folder /></el-icon>
            下级单位 ({{ currentUnit.children.length }})
          </h4>
          <div class="sub-list">
            <div
              v-for="child in currentUnit.children"
              :key="child.id"
              class="sub-item"
              @click="handleViewDetail(child)"
            >
              <el-icon><Document /></el-icon>
              <span>{{ child.name }}</span>
            </div>
          </div>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { Search, Refresh } from '@element-plus/icons-vue'
import { unitApi } from '@/api/unit'

// 状态
const loading = ref(false)
const treeData = ref([])
const filterText = ref('')
const treeRef = ref(null)
const drawerVisible = ref(false)
const currentUnit = ref(null)

const treeProps = {
  label: 'name',
  children: 'children'
}

// 加载树形数据
const loadTree = async () => {
  loading.value = true
  try {
    const res = await unitApi.getTree()
    if (res.code === 200) {
      treeData.value = res.data
    }
  } finally {
    loading.value = false
  }
}

// 过滤节点
const filterNode = (value, data) => {
  if (!value) return true
  return data.name.toLowerCase().includes(value.toLowerCase())
}

// 监听搜索文本变化
watch(filterText, (val) => {
  treeRef.value?.filter(val)
})

// 全部展开
const expandAll = () => {
  const tree = treeRef.value
  if (!tree) return
  
  const allNodes = tree.store.nodesMap
  for (const key in allNodes) {
    allNodes[key].expanded = true
  }
}

// 全部收起
const collapseAll = () => {
  const tree = treeRef.value
  if (!tree) return
  
  const allNodes = tree.store.nodesMap
  for (const key in allNodes) {
    allNodes[key].expanded = false
  }
}

// 查看详情
const handleViewDetail = (data) => {
  currentUnit.value = data
  drawerVisible.value = true
}

// 初始化
onMounted(() => {
  loadTree()
})
</script>

<style lang="scss" scoped>
.unit-tree {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.action-card {
  border-radius: 12px;
  
  .action-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .action-right {
    display: flex;
    gap: 12px;
  }
}

.tree-card {
  border-radius: 12px;
  
  .card-header {
    display: flex;
    align-items: center;
    gap: 10px;
    
    .header-icon {
      font-size: 20px;
      color: #3b82f6;
    }
    
    .title {
      font-size: 16px;
      font-weight: 600;
      color: #1e293b;
    }
  }
}

.tree-container {
  min-height: 400px;
  padding: 16px 0;
}

.tree-node {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  padding: 8px 12px;
  border-radius: 8px;
  transition: all 0.2s ease;
  
  &:hover {
    background: #f0f7ff;
    
    .node-actions {
      opacity: 1;
    }
  }
}

.node-content {
  display: flex;
  align-items: center;
  gap: 10px;
}

.node-icon {
  font-size: 18px;
  color: #64748b;
  
  &.has-children {
    color: #f59e0b;
  }
}

.node-label {
  font-size: 14px;
  font-weight: 500;
  color: #1e293b;
}

.node-code {
  margin-left: 8px;
}

.node-status {
  margin-left: 4px;
}

.node-actions {
  opacity: 0;
  transition: opacity 0.2s ease;
}

:deep(.el-tree-node__content) {
  height: auto;
  padding: 4px 8px;
}

:deep(.el-tree-node__expand-icon) {
  font-size: 16px;
  color: #94a3b8;
  
  &.is-leaf {
    color: transparent;
  }
}

.unit-detail {
  padding: 0 8px;
}

.detail-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e2e8f0;
  
  .detail-icon {
    font-size: 32px;
    color: #3b82f6;
  }
  
  .detail-title {
    font-size: 20px;
    font-weight: 600;
    color: #1e293b;
    margin: 0;
  }
}

.detail-desc {
  margin-bottom: 24px;
}

.sub-units {
  .sub-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 15px;
    font-weight: 600;
    color: #1e293b;
    margin-bottom: 12px;
    
    .el-icon {
      color: #f59e0b;
    }
  }
  
  .sub-list {
    display: flex;
    flex-direction: column;
    gap: 8px;
  }
  
  .sub-item {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 12px 16px;
    background: #f8fafc;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.2s ease;
    
    &:hover {
      background: #e2e8f0;
    }
    
    .el-icon {
      color: #64748b;
    }
    
    span {
      font-size: 14px;
      color: #334155;
    }
  }
}
</style>
