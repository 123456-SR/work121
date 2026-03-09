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
          <th>统一编号</th>
          <th>任务名称</th>
          <th>创建时间</th>
          <th>状态</th>
        </tr>
      </thead>
      <tbody>
        <tr v-if="loading">
          <td colspan="4" class="text-center">加载中...</td>
        </tr>
        <tr v-else-if="filteredTasks.length === 0">
          <td colspan="4" class="text-center">暂无待处理任务</td>
        </tr>
        <tr v-else v-for="task in filteredTasks" :key="task.unifiedNumber">
          <td>{{ task.unifiedNumber }}</td>
          <td>{{ task.name }}</td>
          <td>{{ task.createTime }}</td>
          <td>
            <span class="status-tag" :class="task.status === '待处理' ? 'status-pending' : 'status-processing'">
              {{ task.status }}
            </span>
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
const loading = ref(false)

// 获取当前登录用户信息
const getCurrentUser = () => {
  try {
    const userInfoStr = localStorage.getItem('userInfo')
    if (userInfoStr) {
      const userInfo = JSON.parse(userInfoStr)
      return userInfo.userAccount || userInfo.username || userInfo.userName || ''
    }
    return ''
  } catch (error) {
    console.error('获取用户信息失败:', error)
    return ''
  }
}

// 表单类型映射
const formTypeMap = {
  'ENTRUSTMENT_LIST': '检测委托单',
  'REBOUND_METHOD_RECORD': '回弹法检测混凝土抗压强度记录表',
  'LIGHT_DYNAMIC_PENETRATION_RECORD': '轻型动力触探检测记录表',
  'NUCLEAR_DENSITY_RECORD': '原位密度检测记录表（核子法）',
  'SAND_REPLACEMENT_RECORD': '原位密度检测记录表（灌砂法）',
  'WATER_REPLACEMENT_RECORD': '相对密度试验记录表（灌水法）',
  'CUTTING_RING_RECORD': '原位密度检测记录表（环刀法）',
  'BECKMAN_BEAM_RECORD': '路基路面回弹弯沉试验检测记录表',
  'DENSITY_TEST_REPORT': '原位密度检测报告',
  'DENSITY_TEST_RESULT': '原位密度检测结果',
  'LIGHT_DYNAMIC_PENETRATION_REPORT': '轻型动力触探检测报告',
  'LIGHT_DYNAMIC_PENETRATION_RESULT': '轻型动力触探检测结果',
  'REBOUND_METHOD_REPORT': '回弹法检测混凝土抗压强度报告',
  'BECKMAN_BEAM_REPORT': '路基路面回弹弯沉检测报告',
  'BECKMAN_BEAM_RESULT': '路基路面回弹弯沉检测结果'
}

// 格式化时间为年月日
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  try {
    const date = new Date(dateStr)
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${year}-${month}-${day}`
  } catch (error) {
    console.error('日期格式化失败:', error)
    return dateStr
  }
}

// 确定任务名称
const getTaskName = (task, userAccount) => {
  // 检查委托单角色
  if (task.wtUndertaker === userAccount || task.wtReviewer === userAccount) {
    return '检测委托单'
  }
  
  // 检查记录表角色
  if (task.jcTester === userAccount || task.jcReviewer === userAccount) {
    // 查找记录表类型
    for (let i = 1; i <= 10; i++) {
      const tableType = task[`table${i}Type`]
      if (tableType && tableType.includes('RECORD')) {
        return formTypeMap[tableType] || '记录表'
      }
    }
  }
  
  // 检查报告表和结果表角色
  if (task.bgTester === userAccount || task.bgReviewer === userAccount || task.bgApprover === userAccount) {
    // 查找报告表类型
    for (let i = 1; i <= 10; i++) {
      const tableType = task[`table${i}Type`]
      if (tableType && (tableType.includes('REPORT') || tableType.includes('RESULT'))) {
        return formTypeMap[tableType] || '报告表'
      }
    }
  }
  
  return '任务'
}

// 加载用户任务
const loadUserTasks = async () => {
  const userAccount = getCurrentUser()
  if (!userAccount) return
  
  loading.value = true
  try {
    // 调用API获取所有任务
    const response = await axios.post('/api/directory/getAll')
    
    if (response.data.success && response.data.data) {
      const allTasks = response.data.data
      
      // 过滤出分配给当前用户的任务
      const userTasks = allTasks.filter(task => {
        return task.wtUndertaker === userAccount ||
               task.wtReviewer === userAccount ||
               task.jcTester === userAccount ||
               task.jcReviewer === userAccount ||
               task.bgTester === userAccount ||
               task.bgReviewer === userAccount ||
               task.bgApprover === userAccount
      })
      
      // 转换任务数据
      tasks.value = userTasks.map(task => ({
        unifiedNumber: task.dirName,
        name: getTaskName(task, userAccount),
        createTime: formatDate(task.createTime || new Date()),
        status: task.status === 1 ? '待处理' : '处理中'
      }))
    }
  } catch (error) {
    console.error('加载用户任务失败:', error)
  } finally {
    loading.value = false
  }
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
const searchTasks = () => {
  // 这里可以实现实际的搜索逻辑
  console.log('搜索任务:', searchQuery.value)
}

// 组件挂载时获取任务列表
onMounted(() => {
  loadUserTasks()
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

.text-center {
  text-align: center;
}

</style>
