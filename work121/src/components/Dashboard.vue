<template>
  <div class="dashboard-container">
    <!-- 数据卡片区 -->
    <div class="data-cards">
      <div class="data-card blue">
        <div class="label">我的总任务</div>
        <div class="num">{{ totalTasks }}</div>
      </div>
      <div class="data-card yellow">
        <div class="label">待处理任务</div>
        <div class="num">{{ pendingTasks }}</div>
      </div>
      <div class="data-card green">
        <div class="label">已完成任务</div>
        <div class="num">{{ completedTasks }}</div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="chart-area">
      <div class="chart-card">
        <div class="chart-title">任务状态分布</div>
        <div class="chart-container" style="height: 300px;">
          <!-- 这里可以添加ECharts图表 -->
          <div class="placeholder-chart">图表区域</div>
        </div>
      </div>
      <div class="chart-card">
        <div class="chart-title">任务类型分布</div>
        <div class="chart-container" style="height: 300px;">
          <!-- 这里可以添加ECharts图表 -->
          <div class="placeholder-chart">图表区域</div>
        </div>
      </div>
    </div>

    <!-- 最近任务列表 -->
    <div class="list-card">
      <div class="list-title">最近任务</div>
      <div class="search-bar">
        <input placeholder="搜索任务" />
        <button class="btn">查询</button>
      </div>
      <table class="task-table">
        <thead>
          <tr>
            <th>任务名称</th>
            <th>类型</th>
            <th>状态</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>轻型动力触探检测</td>
            <td>记录表</td>
            <td><span class="status-tag status-active">进行中</span></td>
            <td>2024-01-15</td>
            <td>
              <button class="btn btn-small">查看</button>
            </td>
          </tr>
          <tr>
            <td>原位密度检测</td>
            <td>报告表</td>
            <td><span class="status-tag status-active">待审核</span></td>
            <td>2024-01-14</td>
            <td>
              <button class="btn btn-small">查看</button>
            </td>
          </tr>
          <tr>
            <td>回弹法检测</td>
            <td>结果表</td>
            <td><span class="status-tag status-active">已完成</span></td>
            <td>2024-01-13</td>
            <td>
              <button class="btn btn-small">查看</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

// 任务数据
const totalTasks = ref(0)
const pendingTasks = ref(0)
const completedTasks = ref(0)

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

// 加载用户任务数据
const loadUserTasks = async () => {
  const userAccount = getCurrentUser()
  if (!userAccount) return
  
  try {
    const fetchCount = async (status) => {
      const res = await axios.get('/api/pending-tasks/get-all', {
        params: { taskStatus: status, userAccount }
      })
      if (res.data && res.data.success && Array.isArray(res.data.data)) {
        return res.data.data.length
      }
      return 0
    }

    const [submitCount, auditCount, approvalCount, doneCount] = await Promise.all([
      fetchCount('0'),
      fetchCount('1'),
      fetchCount('4'),
      fetchCount('5')
    ])

    pendingTasks.value = submitCount + auditCount + approvalCount
    completedTasks.value = doneCount
    totalTasks.value = pendingTasks.value + completedTasks.value
  } catch (error) {
    console.error('加载用户任务失败:', error)
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadUserTasks()
})
</script>

<style scoped>
.dashboard-container {
  padding: 24px;
}

/* 数据卡片区 */
.data-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 32px;
}

.data-card {
  background-color: var(--bg-card);
  border-radius: 8px;
  padding: 24px;
  box-shadow: var(--shadow);
  text-align: center;
  transition: transform 0.2s;
}

.data-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.data-card.blue {
  border-left: 4px solid var(--color-blue);
}

.data-card.yellow {
  border-left: 4px solid var(--color-yellow);
}

.data-card.green {
  border-left: 4px solid var(--color-green);
}

.data-card .label {
  font-size: 14px;
  color: var(--text-light);
  margin-bottom: 8px;
}

.data-card .num {
  font-size: 32px;
  font-weight: 600;
  color: var(--text-primary);
}

/* 图表区域 */
.chart-area {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
  margin-bottom: 32px;
}

.chart-card {
  background-color: var(--bg-card);
  border-radius: 8px;
  padding: 20px;
  box-shadow: var(--shadow);
}

.chart-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--border-color);
}

.placeholder-chart {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  background-color: #f8f9fa;
  border-radius: 4px;
  color: #6c757d;
}

/* 最近任务列表 */
.list-card {
  background-color: var(--bg-card);
  border-radius: 8px;
  padding: 20px;
  box-shadow: var(--shadow);
}

.list-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--border-color);
}

.search-bar {
  margin-bottom: 16px;
  display: flex;
  gap: 16px;
  align-items: center;
}

.search-bar input {
  flex: 1;
  max-width: 300px;
  padding: 8px 12px;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  outline: none;
}

.search-bar input:focus {
  border-color: var(--color-blue);
}

/* 表格样式 */
.task-table {
  width: 100%;
  border-collapse: collapse;
}

.task-table th {
  background-color: var(--card-blue);
  color: var(--color-blue);
  padding: 12px 16px;
  text-align: left;
  font-weight: 500;
}

.task-table td {
  padding: 12px 16px;
  border-bottom: 1px solid var(--border-color);
}

.task-table tr:hover {
  background-color: var(--bg-primary);
}

.status-tag {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.status-active {
  background-color: var(--card-green);
  color: var(--color-green);
}

.btn-small {
  padding: 4px 8px;
  font-size: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .data-cards {
    grid-template-columns: 1fr;
  }
  
  .chart-area {
    grid-template-columns: 1fr;
  }
}
</style>
