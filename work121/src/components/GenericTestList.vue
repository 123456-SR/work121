<template>
  <div class="generic-list-container">
    <div class="list-header">
      <h2>{{ title }}列表</h2>
      <div class="header-actions">
        <div class="search-box">
          <input 
            v-model="searchWtNum" 
            placeholder="输入统一编号查询" 
            @keyup.enter="handleSearch"
            class="search-input"
          />
          <button class="btn btn-secondary search-btn" @click="handleSearch">查询</button>
        </div>
        <button class="btn btn-primary" @click="createNew">新建{{ title }}</button>
      </div>
    </div>
    
    <div class="table-container">
      <table class="data-table">
        <thead>
          <tr>
            <th>统一编号</th>
            <th>工程名称</th>
            <th>委托单位</th>
            <th>委托日期</th>
            <th>登记人</th>
            <th>检测人</th>
            <th>校核人</th>
            <th>状态</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="8" class="text-center">加载中...</td>
          </tr>
          <tr v-else-if="list.length === 0">
            <td colspan="8" class="text-center">暂无数据</td>
          </tr>
          <tr 
            v-else 
            v-for="item in list" 
            :key="item.id" 
            @dblclick="handleRowDblClick(item)"
            class="data-row"
            title="双击编辑"
          >
            <td>{{ item.wtNum }}</td>
            <td>{{ item.projectName }}</td>
            <td>{{ item.clientUnit }}</td>
            <td>{{ formatDate(item.commissionDate) }}</td>
            <td>{{ item.clientRegRealName || item.clientRegName }}</td>
            <td>{{ item.testerName }}</td>
            <td>{{ item.reviewerName }}</td>
            <td>
              <span :class="['status-badge', getStatusClass(getEffectiveStatus(item))]">
                {{ getStatusText(getEffectiveStatus(item)) }}
              </span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="pagination-container">
      <button class="btn btn-secondary" :disabled="pageNum <= 1" @click="changePage(pageNum - 1)">上一页</button>
      <span class="page-info">第 {{ pageNum }} 页 / 共 {{ Math.ceil(total / pageSize) || 1 }} 页 (共 {{ total }} 条)</span>
      <button class="btn btn-secondary" :disabled="pageNum * pageSize >= total" @click="changePage(pageNum + 1)">下一页</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, inject, watch, computed } from 'vue'
import axios from 'axios'

const props = defineProps({
  title: {
    type: String,
    default: '记录'
  },
  category: {
    type: String,
    required: true
  },
  formComponent: {
    type: String,
    required: true
  },
  dataType: {
    type: String,
    default: 'entrustment', // 'entrustment', 'record', 'report', 'result'
    validator: (value) => {
      return ['entrustment', 'record', 'report', 'result'].includes(value)
    }
  }
})

const list = ref([])
const loading = ref(false)
const navigateTo = inject('navigateTo')
const pageNum = ref(1)
const pageSize = ref(7)
const total = ref(0)
const searchWtNum = ref('')

// 计算API端点
const apiEndpoint = computed(() => {
  switch (props.dataType) {
    case 'record':
      return '/api/jc-core-test-record'
    case 'report':
      return '/api/jc-core-test-report'
    case 'result':
      return '/api/jc-core-test-result'
    case 'entrustment':
    default:
      return '/api/jc-core-wt-info'
  }
})

// 计算当前行应显示的状态：
// - 所有“记录表”列表：优先使用后端返回的 recordStatus（各记录表自己的 STATUS）
// - 其他列表（委托单 / 报告等）：保持原有行为，使用 status（委托状态）
const getEffectiveStatus = (item) => {
  // 只对“记录”类列表启用 recordStatus
  if (props.dataType === 'record' && item && item.recordStatus !== undefined && item.recordStatus !== null) {
    return item.recordStatus
  }
  return item?.status
}

// 获取当前登录用户
const getCurrentUser = () => {
  try {
    const userInfoStr = localStorage.getItem('userInfo')
    if (userInfoStr) {
      const userInfo = JSON.parse(userInfoStr)
      // LoginController returns 'username' key for the account
      // DirectoryList/User entity uses 'userAccount'
      // We check both to be safe, prioritizing the one from LoginController (username)
      return userInfo.username || userInfo.userAccount || userInfo.userName || ''
    }
    return ''
  } catch (error) {
    console.error('获取用户信息失败:', error)
    return ''
  }
}

// 加载数据
const loadData = async () => {
  try {
    loading.value = true
    const regName = getCurrentUser()
    // 使用检测类别和登记人查询
    const response = await axios.get(`${apiEndpoint.value}/by-category?category=${encodeURIComponent(props.category)}&regName=${encodeURIComponent(regName)}&wtNum=${encodeURIComponent(searchWtNum.value)}&pageNum=${pageNum.value}&pageSize=${pageSize.value}`)
    if (response.data.success) {
      list.value = response.data.data.list
      total.value = response.data.data.total
    } else {
      console.error('Failed to load data:', response.data.message)
      // 如果没有数据或接口报错，清空列表
      list.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('Error loading data:', error)
    list.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 监听category变化，重新加载
watch(() => props.category, () => {
  pageNum.value = 1
  loadData()
})

// 翻页
const changePage = (newPage) => {
  pageNum.value = newPage
  loadData()
}

// 搜索
const handleSearch = () => {
  pageNum.value = 1
  loadData()
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString()
}

// 状态文本
const getStatusText = (status) => {
  const s = parseInt(status)
  switch(s) {
    case 0: return '草稿'
    case 1: return '待审核'
    case 2: return '已打回'
    case 3: return '待签字'
    case 4: return '待批准'
    case 5: return '已通过'
    default: return '未知'
  }
}

const getStatusClass = (status) => {
  const s = parseInt(status)
  switch(s) {
    case 0: return 'status-draft'
    case 1: return 'status-pending'
    case 2: return 'status-rejected'
    case 3: return 'status-signing'
    case 4: return 'status-approving'
    case 5: return 'status-completed'
    default: return ''
  }
}

// 编辑/双击
const handleEdit = (item) => {
  if (navigateTo && props.formComponent) {
    navigateTo(props.formComponent, { id: item.id, wtNum: item.wtNum })
  }
}

const handleRowDblClick = (item) => {
  handleEdit(item)
}

// 新建
const createNew = () => {
  if (navigateTo && props.formComponent) {
    navigateTo(props.formComponent, { id: null })
  }
}

// 删除
const handleDelete = async (item) => {
  if (confirm(`确定要删除 ${item.wtNum} 吗？`)) {
    try {
      const response = await axios.post(`${apiEndpoint.value}/delete`, { id: item.id })
      if (response.data.success) {
        alert('删除成功')
        loadData()
      } else {
        alert('删除失败: ' + response.data.message)
      }
    } catch (error) {
      console.error('删除请求失败:', error)
      alert('删除请求失败')
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.generic-list-container {
  padding: 20px;
  background-color: #f8f9fa;
  min-height: 100vh;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  background-color: white;
  padding: 16px 24px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.list-header h2 {
  margin: 0;
  color: #2c3e50;
  font-weight: 600;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 8px;
}

.search-input {
  padding: 8px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 14px;
  outline: none;
  width: 200px;
  transition: border-color 0.2s;
}

.search-input:focus {
  border-color: #3498db;
}

.search-btn {
  padding: 8px 16px;
}

.table-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  overflow: hidden;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th, .data-table td {
  padding: 16px 24px;
  text-align: left;
  border-bottom: 1px solid #edf2f7;
}

.data-table th {
  background-color: #f8f9fa;
  font-weight: 600;
  color: #5d6d7e;
  font-size: 14px;
  text-transform: uppercase;
}

.data-row:hover {
  background-color: #f8f9fa;
  cursor: pointer;
}

.text-center {
  text-align: center;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;
}

.btn-primary {
  background-color: #3498db;
  color: white;
  box-shadow: 0 2px 4px rgba(52, 152, 219, 0.3);
}

.btn-primary:hover {
  background-color: #2980b9;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-top: 24px;
  gap: 16px;
}

.btn-secondary {
  background-color: white;
  color: #4a5568;
  border: 1px solid #e2e8f0;
}

.btn-secondary:disabled {
  background-color: #f7fafc;
  color: #cbd5e0;
  cursor: not-allowed;
}

.actions-cell {
  white-space: nowrap;
}

.action-link {
  color: #3498db;
  cursor: pointer;
  margin: 0 4px;
  font-size: 13px;
  font-weight: 500;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.action-link:hover {
  background-color: #ebf8ff;
  color: #2980b9;
  text-decoration: none;
}

.delete-link {
  color: #e74c3c;
}

.delete-link:hover {
  background-color: #fee2e2;
  color: #c0392b;
}

.separator {
  color: #cbd5e0;
  margin: 0 2px;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.status-completed {
  background-color: #e8f5e9;
  color: #2e7d32;
}

.status-pending {
  background-color: #e3f2fd;
  color: #1976d2;
}

.status-draft {
  background-color: #f5f5f5;
  color: #616161;
}

.status-rejected {
  background-color: #ffebee;
  color: #c62828;
}

.status-signing {
  background-color: #fff3e0;
  color: #ef6c00;
}

.status-approving {
  background-color: #f3e5f5;
  color: #7b1fa2;
}
</style>
