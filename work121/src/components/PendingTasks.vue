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
          <th>创建时间</th>
          <th>状态</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="task in filteredTasks" :key="task.id">
          <td>{{ task.id }}</td>
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

const searchQuery = ref('')
const tasks = ref([
  { id: 'XT-2024-55711', name: '地基检测任务', createTime: '2024-03-01 10:00', status: '待处理' },
  { id: 'XT-2024-55131', name: '密度检测任务', createTime: '2024-03-02 14:30', status: '待处理' },
  { id: 'M-2024-53507', name: '动力触探任务', createTime: '2024-03-03 09:15', status: '待处理' }
])

// 过滤任务
const filteredTasks = computed(() => {
  if (!searchQuery.value) {
    return tasks.value
  }
  return tasks.value.filter(task => 
    task.id.includes(searchQuery.value) || 
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
  // 这里可以实现实际的获取任务列表逻辑
  console.log('获取待处理任务列表')
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
