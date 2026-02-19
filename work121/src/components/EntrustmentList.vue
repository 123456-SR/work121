<template>
  <div class="entrustment-list-container">
    <div class="list-header">
      <h2>检测委托单列表</h2>
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
        <button class="btn btn-primary" @click="createNew">新建委托单</button>
      </div>
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
            <th>检测人</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="7" class="text-center">加载中...</td>
          </tr>
          <tr v-else-if="list.length === 0">
            <td colspan="7" class="text-center">暂无数据</td>
          </tr>
          <tr 
            v-else 
            v-for="item in list" 
            :key="item.id" 
            class="data-row"
            @dblclick="handleDblClick(item)"
          >
            <td>{{ item.wtNum }}</td>
            <td>{{ item.projectName }}</td>
            <td>{{ item.clientUnit }}</td>
            <td>{{ formatDate(item.commissionDate) }}</td>
            <td>{{ item.clientRegRealName || item.clientRegName }}</td>
            <td>{{ item.testerName }}</td>
            <td class="actions-cell">
              <span 
                v-for="(action, index) in getActions(item)" 
                :key="index"
              >
                <span v-if="index > 0" class="separator">|</span>
                <a class="action-link" @click.stop="executeAction(item, action)">
                  {{ action.label }}
                </a>
              </span>
              <span v-if="getActions(item).length === 0" style="color: #999; font-size: 12px;">无操作</span>
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
import { ref, onMounted, inject } from 'vue'
import axios from 'axios'

const list = ref([])
const loading = ref(false)
const navigateTo = inject('navigateTo')
const pageNum = ref(1)
const pageSize = ref(7)
const total = ref(0)
const searchWtNum = ref('')

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

// 获取操作列表
const getActions = (item) => {
  const user = getCurrentUser()
  if (!user || !user.username) return []
  
  const actions = []
  const username = user.username
  
  // 默认为轻型动力触探，如果有testCategory则使用映射
  // 这里简化处理，根据testCategory判断，如果没有则默认为LightDynamicPenetration
  // 实际项目中可能需要更完善的映射表
  
  // 匹配规则
  const isTester = item.tester === username || item.clientRegName === username // 登记人也可以填写
  const isCreator = item.createBy === username || item.clientRegName === username // 创建者
  const isReviewer = item.reviewer === username
  const isApprover = item.approver === username
  
  if (isTester || isCreator) {
    actions.push({ label: '填写数据', type: 'record', component: getRecordComponent(item.testCategory) })
  }
  
  if (isCreator || isTester) {
     // 避免重复添加（如果既是检测人又是创建者）
     if (!actions.some(a => a.type === 'delete')) {
        actions.push({ label: '删除', type: 'delete' })
     }
  }
  
  if (isReviewer) {
    actions.push({ label: '签名', type: 'sign', component: getReportComponent(item.testCategory) }) 
  }
  
  if (isApprover) {
    actions.push({ label: '审批', type: 'approve', component: getReportComponent(item.testCategory) }) 
  }
  
  // 如果没有匹配的角色，但用户是管理员或有权限查看结果
  if (actions.length === 0 || item.status === 'completed') {
    actions.push({ label: '查看结果', type: 'result', component: getReportComponent(item.testCategory) })
  }
  
  return actions
}

const getRecordComponent = (category) => {
    if (!category) return 'LightDynamicPenetrationRecord'
    
    if (category.includes('灌砂')) return 'SandReplacementRecord'
    if (category.includes('灌水')) return 'WaterReplacementRecord'
    if (category.includes('环刀')) return 'CuttingRingRecord'
    if (category.includes('核子')) return 'NuclearDensityRecord'
    if (category.includes('回弹')) return 'ReboundMethodRecord'
    if (category.includes('弯沉')) return 'BeckmanBeamRecord'
    if (category.includes('触探')) return 'LightDynamicPenetrationRecord'
    
    return 'LightDynamicPenetrationRecord'
}

const getReportComponent = (category) => {
    if (!category) return 'LightDynamicPenetration' 
    
    if (category.includes('密度') || category.includes('灌砂') || category.includes('灌水') || category.includes('环刀') || category.includes('核子')) {
        return 'DensityTestReport'
    }
    if (category.includes('回弹')) return 'ReboundMethodReport'
    if (category.includes('弯沉')) return 'BeckmanBeamReport'
    if (category.includes('触探')) return 'LightDynamicPenetration' 
    
    return 'LightDynamicPenetration'
}

const executeAction = (item, action) => {
  if (action.type === 'record') {
     const status = parseInt(item.status) || 0
     if (status < 3) {
       alert('委托单未审核通过，无法填写记录表')
       return
     }
  }

  if (action.type === 'delete') {
    if (confirm('确定要删除这条委托单吗？此操作不可恢复。')) {
      handleDelete(item.id)
    }
    return
  }

  if (navigateTo && action.component) {
    navigateTo(action.component, { id: item.id, wtNum: item.wtNum })
  }
}

const handleDblClick = (item) => {
  if (navigateTo) {
    navigateTo('Entrustment', { wtNum: item.wtNum })
  }
}

// 加载数据
const loadData = async () => {
  const user = getCurrentUser()
  if (!user || !user.username) { // Changed userName to username to match Login.vue
    alert('未找到用户信息，请重新登录')
    return
  }

  try {
    loading.value = true
    // 使用用户真实姓名查询
    // 注意：Login.vue存储的是 username 和 fullName
    // JcCoreWtInfoMapper 使用 regName 匹配 (WT_REG_NAME, TESTER, etc.)
    // 这里传入 username
    const response = await axios.get(`/api/jc-core-wt-info/by-reg-name?regName=${encodeURIComponent(user.username)}&wtNum=${encodeURIComponent(searchWtNum.value)}&pageNum=${pageNum.value}&pageSize=${pageSize.value}`)
    if (response.data.success) {
      list.value = response.data.data.list
      total.value = response.data.data.total
    } else {
      console.error('Failed to load data:', response.data.message)
    }
  } catch (error) {
    console.error('Error loading data:', error)
  } finally {
    loading.value = false
  }
}

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

// 打开报告 (保留原有方法兼容性，虽然不再直接使用)
const openReport = (item, componentName) => {
  if (navigateTo) {
    navigateTo(componentName, { id: item.id })
  }
}

// 新建
const createNew = () => {
  if (navigateTo) {
    navigateTo('Entrustment', { id: null }) // null ID for new
  }
}

// 删除委托单
const handleDelete = async (id) => {
  try {
    const response = await axios.post('/api/jc-core-wt-info/delete', { id })
    if (response.data.success) {
      alert('删除成功')
      loadData() // 重新加载列表
    } else {
      alert('删除失败: ' + response.data.message)
    }
  } catch (error) {
    console.error('Delete error:', error)
    alert('删除异常: ' + error.message)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.entrustment-list-container {
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

.btn-secondary:hover:not(:disabled) {
  background-color: #edf2f7;
  color: #2d3748;
}

.page-info {
  color: #718096;
  font-size: 14px;
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

.separator {
  color: #cbd5e0;
  margin: 0 2px;
}
</style>