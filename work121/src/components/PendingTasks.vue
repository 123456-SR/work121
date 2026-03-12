<template>
  <div class="pending-tasks">
    <h2>待处理任务</h2>
    <div class="toolbar">
      <div class="search-bar">
        <input type="text" placeholder="搜索任务..." v-model="searchQuery">
        <button class="btn" @click="searchTasks">搜索</button>
      </div>
      <div class="task-type-selector">
        <select v-model="selectedTaskType" @change="switchTaskType">
          <option value="audit">待审核任务</option>
          <option value="submit">待提交任务</option>
          <option value="approval">待批准任务</option>
        </select>
      </div>
    </div>
    <table class="task-table">
      <thead>
        <tr>
          <th>任务编号</th>
          <th>任务名称</th>
          <th>委托单位</th>
          <th>工程名称</th>
          <th>{{ selectedTaskType === 'submit' ? '填写人' : selectedTaskType === 'approval' ? '批准人' : '审核人' }}</th>
          <th>创建时间</th>
          <th>状态</th>
          <th v-if="selectedTaskType !== 'submit'">操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="task in paginatedTasks" :key="task.id">
          <td>{{ task.unifiedNumber }}</td>
          <td>{{ task.name }}</td>
          <td>{{ task.clientUnit || '-' }}</td>
          <td>{{ task.projectName || '-' }}</td>
          <td>{{ task.reviewer || '-' }}</td>
          <td>{{ formatTime(task.createTime) }}</td>
          <td>
            <span class="status-tag" :class="task.status === '待处理' ? 'status-pending' : 'status-processing'">
              {{ task.status }}
            </span>
          </td>
          <td v-if="selectedTaskType !== 'submit'">
            <button v-if="selectedTaskType === 'audit'" class="btn btn-primary btn-small" @click="approveTask(task)">审核通过</button>
            <button v-else-if="selectedTaskType === 'approval'" class="btn btn-primary btn-small" @click="approveTask(task)">批准</button>
          </td>
        </tr>
      </tbody>
    </table>
    <!-- 分页控件 -->
    <div class="pagination" v-if="totalPages > 1">
      <button class="btn btn-small" @click="currentPage = 1" :disabled="currentPage === 1">首页</button>
      <button class="btn btn-small" @click="currentPage--" :disabled="currentPage === 1">上一页</button>
      <span class="page-info">
        {{ currentPage }} / {{ totalPages }}
      </span>
      <button class="btn btn-small" @click="currentPage++" :disabled="currentPage === totalPages">下一页</button>
      <button class="btn btn-small" @click="currentPage = totalPages" :disabled="currentPage === totalPages">末页</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, defineProps, inject } from 'vue'
import axios from 'axios'

// 格式化时间函数
const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  if (isNaN(date.getTime())) return ''
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const props = defineProps({
  taskType: {
    type: String,
    default: ''
  }
})

const searchQuery = ref('')
const tasks = ref([])
const currentPage = ref(1)
const pageSize = ref(13)
const totalItems = ref(0)
const navigateTo = inject('navigateTo')
const selectedTaskType = ref(props.taskType || 'audit')

// 将页面类型映射为后端表的状态值：待提交=0，待审核=1，待批准=5
const statusParam = computed(() => {
  if (selectedTaskType.value === 'submit') return '0'
  if (selectedTaskType.value === 'audit') return '1'
  if (selectedTaskType.value === 'approval') return '5'
  // 默认返回待审核状态
  return '1'
})

// 切换任务类型
const switchTaskType = async () => {
  currentPage.value = 1 // 重置到第一页
  await loadTasks()
}

const getCurrentUserAccount = () => {
  const userInfoStr = localStorage.getItem('userInfo')
  if (userInfoStr) {
    const userInfo = JSON.parse(userInfoStr)
    return userInfo.username || userInfo.userAccount || ''
  }
  return ''
}

// 过滤任务
const filteredTasks = computed(() => {
  let filtered = tasks.value
  if (searchQuery.value) {
    filtered = tasks.value.filter(task => 
      task.unifiedNumber.includes(searchQuery.value) || 
      task.name.includes(searchQuery.value)
    )
  }
  totalItems.value = filtered.length
  return filtered
})

// 分页数据
const paginatedTasks = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredTasks.value.slice(start, end)
})

// 总页数
const totalPages = computed(() => {
  return Math.ceil(totalItems.value / pageSize.value)
})

// 搜索任务
const searchTasks = async () => {
  try {
    currentPage.value = 1 // 重置到第一页
    const userAccount = getCurrentUserAccount()
    const response = await axios.get('/api/pending-tasks/search', {
      params: {
        // 兼容后端：常见会要求 taskStatus/taskType；同时附带 keyword/userAccount 以适配“多表查询展示”接口
        taskStatus: statusParam.value,
        taskType: selectedTaskType.value,
        keyword: searchQuery.value,
        userAccount: userAccount
      }
    })
    if (response.data.success) {
      tasks.value = response.data.data.map(item => ({
        id: item.data_id || item.DATA_ID || item.dataId || '未知ID',
        tableType: item.table_type || item.TABLE_TYPE || item.tableType || '未知类型',
        name: (item.table_type || item.TABLE_TYPE || item.tableType || '未知类型') + (selectedTaskType.value === 'audit' ? '审核任务' : selectedTaskType.value === 'approval' ? '批准任务' : selectedTaskType.value === 'submit' ? '提交任务' : '审核任务'),
        reviewer: item.reviewer || item.REVIEWER || '',
        createTime: item.create_time || item.CREATE_TIME || item.createTime || new Date().toLocaleString(),
        status: selectedTaskType.value === 'audit' ? '待审核' : selectedTaskType.value === 'approval' ? '待批准' : selectedTaskType.value === 'submit' ? '待提交' : '待处理',
        unifiedNumber: item.unified_number || item.UNIFIED_NUMBER || item.unifiedNumber || '未知编号',
        clientUnit: item.client_unit || item.CLIENT_UNIT || item.clientUnit || '',
        projectName: item.project_name || item.PROJECT_NAME || item.projectName || ''
      }))
    }
  } catch (e) {
    console.error('搜索任务失败', e)
  }
}

// 打开任务对应的表单页面
const openTask = (task) => {
  if (!navigateTo) return
  const type = String(task.tableType || '').toUpperCase()
  const componentMap = {
    // 委托单
    'ENTRUSTMENT': 'Entrustment',
    'ENTRUSTMENT_LIST': 'Entrustment',
    // 记录表
    'NUCLEAR_DENSITY': 'NuclearDensityRecord',
    'SAND_REPLACEMENT': 'SandReplacementRecord',
    'WATER_REPLACEMENT': 'WaterReplacementRecord',
    'CUTTING_RING': 'CuttingRingRecord',
    'REBOUND_METHOD': 'ReboundMethodRecord',
    'LIGHT_DYNAMIC_PENETRATION': 'LightDynamicPenetrationRecord',
    'BECKMAN_BEAM': 'BeckmanBeamRecord',
    // 报告/结果（如有待办）
    'DENSITY_TEST': 'DensityTestReport'
  }

  const component = componentMap[type]
  if (!component) {
    alert('暂不支持打开该任务类型：' + (task.tableType || type))
    return
  }

  // 优先用统一编号做联动（各表基本都支持 wtNum），id 作为兜底
  const propsToPass = {
    wtNum: task.unifiedNumber && task.unifiedNumber !== '未知编号' ? task.unifiedNumber : undefined,
    id: task.id && task.id !== '未知ID' ? task.id : undefined
  }
  navigateTo(component, propsToPass)
}

// 审核通过任务
const approveTask = async (task) => {
  try {
    const userAccount = getCurrentUserAccount()
    const response = await axios.post('/api/pending-tasks/approve', {
      taskType: task.tableType || task.name.replace(/审核任务|批准任务|提交任务/, ''),
      taskId: task.id,
      userAccount: userAccount,
      taskStatus: selectedTaskType.value
    })
    if (response.data.success) {
      alert(selectedTaskType.value === 'audit' ? '审核通过成功' : selectedTaskType.value === 'approval' ? '批准成功' : '操作成功')
      // 重新获取任务列表
      await loadTasks()
    } else {
      alert('操作失败: ' + response.data.message)
    }
  } catch (e) {
    console.error('操作失败', e)
    alert('操作失败')
  }
}

// 提交任务
const submitTask = async (task) => {
  try {
    const userAccount = getCurrentUserAccount()
    const response = await axios.post('/api/pending-tasks/submit', {
      taskType: task.tableType || task.name.replace(/审核任务|批准任务|提交任务/, ''),
      taskId: task.id,
      userAccount: userAccount
    })
    if (response.data.success) {
      alert('提交成功')
      // 重新获取任务列表
      await loadTasks()
    } else {
      alert('提交失败: ' + response.data.message)
    }
  } catch (e) {
    console.error('提交失败', e)
    alert('提交失败')
  }
}

// 获取任务列表
const loadTasks = async () => {
  try {
    currentPage.value = 1 // 重置到第一页
    const userAccount = getCurrentUserAccount()
    const response = await axios.get('/api/pending-tasks/get-by-user', {
      params: {
        userAccount: userAccount,
        taskStatus: statusParam.value
      }
    })
    if (response.data.success) {
      tasks.value = response.data.data.map(item => ({
        id: item.data_id || item.DATA_ID || item.dataId || '未知ID',
        tableType: item.table_type || item.TABLE_TYPE || item.tableType || '未知类型',
        name: (item.table_type || item.TABLE_TYPE || item.tableType || '未知类型') + (selectedTaskType.value === 'audit' ? '审核任务' : selectedTaskType.value === 'approval' ? '批准任务' : selectedTaskType.value === 'submit' ? '提交任务' : '审核任务'),
        reviewer: item.reviewer || item.REVIEWER || '',
        createTime: item.create_time || item.CREATE_TIME || item.createTime || new Date().toLocaleString(),
        status: selectedTaskType.value === 'audit' ? '待审核' : selectedTaskType.value === 'approval' ? '待批准' : selectedTaskType.value === 'submit' ? '待提交' : '待处理',
        unifiedNumber: item.unified_number || item.UNIFIED_NUMBER || item.unifiedNumber || '未知编号',
        clientUnit: item.client_unit || item.CLIENT_UNIT || item.clientUnit || '',
        projectName: item.project_name || item.PROJECT_NAME || item.projectName || ''
      }))
    }
  } catch (e) {
    console.error('获取任务列表失败', e)
  }
}

// 组件挂载时获取任务列表
onMounted(async () => {
  await loadTasks()
})

// 监听selectedTaskType变化，重新加载任务列表
watch(() => selectedTaskType.value, async (newType) => {
  await loadTasks()
}, { immediate: false })
</script>

<style scoped>
.pending-tasks {
  background-color: white;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.search-bar {
  display: flex;
  gap: 12px;
  align-items: center;
}

.search-bar input {
  padding: 8px 12px;
  border: 1px solid #E0E0E0;
  border-radius: 4px;
  font-size: 14px;
  width: 300px;
}

.task-type-selector select {
  padding: 8px 12px;
  border: 1px solid #E0E0E0;
  border-radius: 4px;
  font-size: 14px;
  background-color: white;
  cursor: pointer;
}

.task-type-selector select:focus {
  outline: none;
  border-color: var(--card-blue);
  box-shadow: 0 0 0 2px rgba(213, 230, 255, 0.5);
}

.task-type-selector select option:checked {
  background-color: var(--card-blue);
  color: var(--color-blue);
}

.task-type-selector select option:hover {
  background-color: var(--card-blue);
  color: var(--color-blue);
}

.task-table {
  width: 100%;
  border-collapse: collapse;
}

.task-table th,
.task-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #E0E0E0;
}

.task-table th {
  background-color: var(--card-blue);
  color: var(--color-blue);
  font-weight: 500;
}

.task-table tr:hover {
  background-color: var(--bg-primary);
}

.status-tag {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.status-pending {
  background-color: var(--card-yellow);
  color: var(--color-yellow);
}

.status-processing {
  background-color: var(--card-green);
  color: var(--color-green);
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 24px;
  gap: 8px;
}

.page-info {
  margin: 0 12px;
  font-size: 14px;
  color: #666;
}

.btn-small {
  padding: 4px 12px;
  font-size: 14px;
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

</style>
