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
            <td class="actions-cell">
              <span class="action-link" @click="editItem(item)">编辑</span>
              <span class="separator">|</span>
              <span class="action-link delete-link" @click="deleteItem(item.id)">删除</span>
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
  color: black;
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

@media (max-width: 768px) {
  .list-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .header-actions {
    width: 100%;
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .search-box {
    width: 100%;
  }
  
  .search-input {
    flex: 1;
  }
  
  .data-table th,
  .data-table td {
    padding: 12px;
  }
  
  .actions-cell {
    white-space: normal;
  }
  
  .action-link {
    display: block;
    margin: 4px 0;
  }
  
  .separator {
    display: none;
  }
  
  .pagination-container {
    flex-direction: column;
    gap: 12px;
  }
}
</style>