<template>
  <div class="entrustment-list-container">
    <div class="list-header">
      <h2>检测委托单列表</h2>
      <button class="btn btn-primary" @click="createNew">新建委托单</button>
    </div>
    
    <div class="table-container">
      <table class="data-table">
        <thead>
          <tr>
            <th>委托单编号</th>
            <th>工程名称</th>
            <th>委托单位</th>
            <th>委托日期</th>
            <th>登记人</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="5" class="text-center">加载中...</td>
          </tr>
          <tr v-else-if="list.length === 0">
            <td colspan="5" class="text-center">暂无数据</td>
          </tr>
          <tr 
            v-else 
            v-for="item in list" 
            :key="item.id" 
            @dblclick="handleRowDblClick(item)"
            class="data-row"
            title="双击查看详情"
          >
            <td>{{ item.wtNum }}</td>
            <td>{{ item.projectName }}</td>
            <td>{{ item.clientUnit }}</td>
            <td>{{ formatDate(item.commissionDate) }}</td>
            <td>{{ item.clientRegName }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import axios from 'axios'

const list = ref([])
const loading = ref(false)
const navigateTo = inject('navigateTo')

// 获取当前用户信息
const getCurrentUser = () => {
  const userStr = localStorage.getItem('userInfo')
  if (userStr) {
    try {
      return JSON.parse(userStr)
    } catch (e) {
      console.error('Failed to parse user info', e)
    }
  }
  return null
}

// 加载数据
const loadData = async () => {
  const user = getCurrentUser()
  if (!user || !user.userName) {
    alert('未找到用户信息，请重新登录')
    return
  }

  try {
    loading.value = true
    // 使用用户真实姓名查询
    const response = await axios.get(`/api/jc-core-wt-info/by-reg-name?regName=${encodeURIComponent(user.userName)}`)
    if (response.data.success) {
      list.value = response.data.data
    } else {
      console.error('Failed to load data:', response.data.message)
    }
  } catch (error) {
    console.error('Error loading data:', error)
  } finally {
    loading.value = false
  }
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString()
}

// 双击行
const handleRowDblClick = (item) => {
  if (navigateTo) {
    navigateTo('Entrustment', { id: item.id })
  }
}

// 新建
const createNew = () => {
  if (navigateTo) {
    navigateTo('Entrustment', { id: null }) // null ID for new
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.entrustment-list-container {
  padding: 20px;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.table-container {
  overflow-x: auto;
  background: white;
  border-radius: 4px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th, .data-table td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

.data-table th {
  background-color: #f8f9fa;
  font-weight: 600;
  color: #333;
}

.data-row:hover {
  background-color: #f5f5f5;
  cursor: pointer;
}

.text-center {
  text-align: center;
}

.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.btn-primary {
  background-color: #007bff;
  color: white;
}

.btn-primary:hover {
  background-color: #0056b3;
}
</style>