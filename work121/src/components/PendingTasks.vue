<template>
  <div class="pending-tasks">
    <h2>待处理任务</h2>
    <div class="toolbar">
      <div class="search-bar">
        <input type="text" placeholder="搜索任务..." v-model="searchQuery">
        <button class="btn" @click="searchTasks">搜索</button>
      </div>
    </div>
    <table class="task-table">
      <thead>
        <tr>
          <th>任务编号</th>
          <th>任务名称</th>
          <th>审核人</th>
          <th>创建时间</th>
          <th>状态</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="task in filteredTasks" :key="task.id">
          <td>{{ task.unifiedNumber }}</td>
          <td>{{ task.name }}</td>
          <td>{{ task.reviewer || '-' }}</td>
          <td>{{ task.createTime }}</td>
          <td>
            <span class="status-tag" :class="task.status === '待处理' ? 'status-pending' : 'status-processing'">
              {{ task.status }}
            </span>
          </td>
          <td>
            <button class="btn btn-primary btn-small" @click="approveTask(task)">审核通过</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'

const searchQuery = ref('')
const tasks = ref([])

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
  if (!searchQuery.value) {
    return tasks.value
  }
  return tasks.value.filter(task => 
    task.unifiedNumber.includes(searchQuery.value) || 
    task.name.includes(searchQuery.value)
  )
})

// 搜索任务
const searchTasks = async () => {
  try {
    const response = await axios.get('/api/pending-tasks/search', {
      params: { taskType: searchQuery.value }
    })
    if (response.data.success) {
      tasks.value = response.data.data.map(item => ({
        id: item.data_id || item.DATA_ID || item.dataId || '未知ID',
        name: (item.table_type || item.TABLE_TYPE || item.tableType || '未知类型') + '审核任务',
        reviewer: item.reviewer || item.REVIEWER || '',
        createTime: new Date().toLocaleString(),
        status: '待处理',
        unifiedNumber: item.unified_number || item.UNIFIED_NUMBER || item.unifiedNumber || '未知编号'
      }))
    }
  } catch (e) {
    console.error('搜索任务失败', e)
  }
}

// 审核通过任务
const approveTask = async (task) => {
  try {
    const userAccount = getCurrentUserAccount()
    const response = await axios.post('/api/pending-tasks/approve', {
      taskType: task.name.replace('审核任务', ''),
      taskId: task.id,
      userAccount: userAccount
    })
    if (response.data.success) {
      alert('审核通过成功')
      // 重新获取任务列表
      await loadTasks()
    } else {
      alert('审核通过失败: ' + response.data.message)
    }
  } catch (e) {
    console.error('审核通过失败', e)
    alert('审核通过失败')
  }
}

// 获取任务列表
const loadTasks = async () => {
  try {
    const response = await axios.get('/api/pending-tasks/get-all')
    if (response.data.success) {
      tasks.value = response.data.data.map(item => ({
        id: item.data_id || item.DATA_ID || item.dataId || '未知ID',
        name: (item.table_type || item.TABLE_TYPE || item.tableType || '未知类型') + '审核任务',
        reviewer: item.reviewer || item.REVIEWER || '',
        createTime: new Date().toLocaleString(),
        status: '待处理',
        unifiedNumber: item.unified_number || item.UNIFIED_NUMBER || item.unifiedNumber || '未知编号'
      }))
    }
  } catch (e) {
    console.error('获取待处理任务列表失败', e)
  }
}

// 组件挂载时获取任务列表
onMounted(async () => {
  await loadTasks()
})
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
  justify-content: flex-start;
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


</style>
