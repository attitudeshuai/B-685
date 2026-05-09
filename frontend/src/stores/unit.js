import { defineStore } from 'pinia'
import { ref } from 'vue'
import { unitApi } from '@/api/unit'

export const useUnitStore = defineStore('unit', () => {
  const units = ref([])
  const unitTree = ref([])
  const total = ref(0)
  const loading = ref(false)
  const currentUnit = ref(null)

  // 获取单位列表
  const fetchUnits = async (params = {}) => {
    loading.value = true
    try {
      const res = await unitApi.getList(params)
      if (res.code === 200) {
        units.value = res.data.records
        total.value = res.data.total
      }
      return res
    } finally {
      loading.value = false
    }
  }

  // 获取单位树
  const fetchTree = async () => {
    loading.value = true
    try {
      const res = await unitApi.getTree()
      if (res.code === 200) {
        unitTree.value = res.data
      }
      return res
    } finally {
      loading.value = false
    }
  }

  // 获取所有单位
  const fetchAllUnits = async () => {
    const res = await unitApi.getAll()
    if (res.code === 200) {
      return res.data
    }
    return []
  }

  // 创建单位
  const createUnit = async (data) => {
    const res = await unitApi.create(data)
    return res
  }

  // 更新单位
  const updateUnit = async (id, data) => {
    const res = await unitApi.update(id, data)
    return res
  }

  // 删除单位
  const deleteUnit = async (id) => {
    const res = await unitApi.delete(id)
    return res
  }

  return {
    units,
    unitTree,
    total,
    loading,
    currentUnit,
    fetchUnits,
    fetchTree,
    fetchAllUnits,
    createUnit,
    updateUnit,
    deleteUnit
  }
})
