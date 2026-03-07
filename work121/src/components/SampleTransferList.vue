<template>
  <div class="sample-transfer-list-container">
    <div class="list-header">
      <h2>样品流转单列表</h2>
      <div class="header-actions">
        <div class="search-box">
          <input 
            v-model="searchSampleNumber" 
            placeholder="输入样品编号查询" 
            @keyup.enter="handleSearch"
            class="search-input"
          />
          <button class="btn btn-secondary search-btn" @click="handleSearch">查询</button>
        </div>
        <button class="btn btn-primary" @click="createNew">新建样品流转单</button>
      </div>
    </div>
    
    <div class="table-container">
      <table class="data-table">
        <thead>
          <tr>
            <th>样品编号</th>
            <th>样品名称</th>
            <th>检测室</th>
            <th>接样日期</th>
            <th>样品状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="6" class="text-center">加载中...</td>
          </tr>
          <tr v-else-if="list.length === 0">
            <td colspan="6" class="text-center">暂无数据</td>
          </tr>
          <tr 
            v-else 
            v-for="item in list" 
            :key="item.id" 
            class="data-row"
            @dblclick="handleDblClick(item)"
          >
            <td>{{ item.sampleNumber }}</td>
            <td>{{ item.sampleName }}</td>
            <td>{{ item.testingRoom }}</td>
            <td>{{ item.receiveDate }}</td>
            <td>{{ item.sampleStatus }}</td>
            <td class="action-buttons">
              <button class="btn btn-sm btn-primary" @click="editItem(item)">编辑</button>
              <button class="btn btn-sm btn-danger" @click="deleteItem(item.id)">删除</button>
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

const navigateTo = inject('navigateTo')

const list = ref([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchSampleNumber = ref('')

const loadData = async () => {
  loading.value = true
  try {
    let url = '/api/sample-circulation/list'
    if (searchSampleNumber.value) {
      url = `/api/sample-circulation/search?keyword=${encodeURIComponent(searchSampleNumber.value)}`
    }
    const response = await axios.get(url)
    if (response.data.success) {
      // 转换后端数据字段为前端需要的格式
      list.value = response.data.data.map(item => ({
        id: item.id,
        sampleNumber: item.sampleNumber,
        sampleName: item.sampleName,
        testingRoom: item.testLab,
        receiveDate: item.receiveDate ? new Date(item.receiveDate).toISOString().split('T')[0] : '',
        sampleStatus: item.sampleStatus
      }))
      total.value = list.value.length
    } else {
      console.error('Failed to load data:', response.data.message)
    }
  } catch (error) {
    console.error('Error loading data:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  loadData()
}

const changePage = (newPage) => {
  pageNum.value = newPage
  loadData()
}

const createNew = () => {
  navigateTo('SampleTransfer')
}

const editItem = (item) => {
  navigateTo('SampleTransfer', { id: item.id })
}

const deleteItem = async (id) => {
  if (confirm('确定要删除这条记录吗？')) {
    try {
      const response = await axios.delete(`/api/sample-circulation/delete/${id}`)
      if (response.data.success) {
        alert('删除成功')
        loadData()
      } else {
        alert('删除失败: ' + response.data.message)
      }
    } catch (error) {
      console.error('Error deleting item:', error)
      alert('删除出错')
    }
  }
}

const handleDblClick = (item) => {
  editItem(item)
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.sample-transfer-list-container {
  padding: 20px;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 10px;
}

.list-header h2 {
  margin: 0;
  color: #2c3e50;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.search-box {
  display: flex;
  gap: 5px;
}

.search-input {
  padding: 6px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.btn {
  padding: 6px 12px;
  border-radius: 4px;
  border: 1px solid transparent;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-primary {
  background-color: #3498db;
  color: white;
  border-color: #3498db;
}

.btn-secondary {
  background-color: #fff;
  border-color: #d0d7de;
  color: #34495e;
}

.btn-danger {
  background-color: #e74c3c;
  color: white;
  border-color: #e74c3c;
}

.btn-sm {
  padding: 4px 8px;
  font-size: 12px;
}

.btn:hover {
  filter: brightness(0.95);
}

.table-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  overflow: hidden;
  margin-bottom: 20px;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th,
.data-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #e0e0e0;
}

.data-table th {
  background-color: #f5f7fa;
  font-weight: 600;
  color: #2c3e50;
}

.data-row:hover {
  background-color: #f9f9f9;
  cursor: pointer;
}

.action-buttons {
  display: flex;
  gap: 5px;
}

.pagination-container {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.page-info {
  font-size: 14px;
  color: #666;
}

.text-center {
  text-align: center;
  padding: 20px;
  color: #999;
}

@media (max-width: 768px) {
  .list-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .header-actions {
    width: 100%;
  }
  
  .search-box {
    flex: 1;
  }
  
  .search-input {
    flex: 1;
  }
  
  .data-table {
    font-size: 12px;
  }
  
  .data-table th,
  .data-table td {
    padding: 8px;
  }
  
  .action-buttons {
    flex-direction: column;
  }
}
</style>