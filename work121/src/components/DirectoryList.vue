<template>
  <div class="process-list-container">
    <div class="header">
      <h2>流程列表</h2>
      <button @click="openAddModal" class="add-btn">+ 增加流程</button>
    </div>

    <div class="process-table">
      <table>
        <thead>
          <tr>
            <th>序号</th>
            <th>流程名称</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, index) in processes" :key="item.id">
            <td>{{ index + 1 }}</td>
            <td>{{ item.dirName }}</td>
            <td>{{ getStatusText(item.status) }}</td>
            <td>
              <button @click="viewProcess(item)" class="view-btn">查看详情</button>
              <button @click="editProcess(item)" class="edit-btn">编辑</button>
              <button @click="deleteProcess(item.id)" class="delete-btn">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 增加/编辑流程弹窗 -->
    <div v-if="showModal" class="modal-overlay">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ isEditing ? '编辑流程' : '增加流程' }}</h3>
          <button @click="closeModal" class="close-btn">×</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="saveProcess">
            <div class="form-layout">
              <!-- 左侧：流程名称和队列显示 -->
              <div class="left-panel">
                <div class="form-group">
                  <label>流程名称：</label>
                  <input type="text" v-model="formData.dirName" required>
                </div>
                
                <div class="form-group">
                  <label>已选表单队列：</label>
                  <div class="queue-container">
                    <div v-if="selectedForms.length === 0" class="empty-queue">
                      点击右侧表单选项添加到队列
                    </div>
                    <div v-else class="queue-items">
                      <div v-for="(form, index) in selectedForms" :key="index" class="queue-item">
                        <span class="queue-item-index">{{ index + 1 }}.</span>
                        <span class="queue-item-name">{{ form.name }}</span>
                        <button type="button" class="queue-item-remove" @click="removeFromQueue(index)">
                          ×
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
                
                <div class="form-group">
                  <label>状态：</label>
                  <select v-model="formData.status">
                    <option value="0">禁用</option>
                    <option value="1">启用</option>
                    <option value="2">草稿</option>
                    <option value="3">审核中</option>
                    <option value="4">已审核</option>
                    <option value="5">已归档</option>
                  </select>
                </div>
              </div>
              
              <!-- 右侧：表类型选项按钮列表 -->
              <div class="right-panel">
                <h4>可选表单类型</h4>
                <div class="form-buttons">
                  <button 
                    v-for="formType in formTypes" 
                    :key="formType.value"
                    type="button" 
                    class="form-button"
                    @click="addToQueue(formType)"
                  >
                    {{ formType.name }}
                  </button>
                </div>
              </div>
            </div>
            
            <div class="form-actions">
              <button type="button" @click="closeModal" class="cancel-btn">取消</button>
              <button type="submit" class="save-btn">保存</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, inject } from 'vue'
import axios from 'axios'

// 注入导航方法
const navigateTo = inject('navigateTo')

const processes = ref([])
const showModal = ref(false)
const isEditing = ref(false)

// 选中的表单队列
const selectedForms = ref([])

// 表单类型列表
const formTypes = [
  { value: 'ENTRUSTMENT_LIST', name: '检测委托单' },
  { value: 'REBOUND_METHOD_RECORD', name: '回弹法检测混凝土抗压强度记录表' },
  { value: 'LIGHT_DYNAMIC_PENETRATION_RECORD', name: '轻型动力触探检测记录表' },
  { value: 'NUCLEAR_DENSITY_RECORD', name: '原位密度检测记录表（核子法）' },
  { value: 'SAND_REPLACEMENT_RECORD', name: '原位密度检测记录表（灌砂法）' },
  { value: 'WATER_REPLACEMENT_RECORD', name: '相对密度试验记录表（灌水法）' },
  { value: 'CUTTING_RING_RECORD', name: '原位密度检测记录表（环刀法）' },
  { value: 'BECKMAN_BEAM_RECORD', name: '路基路面回弹弯沉试验检测记录表' },
  { value: 'SIGNATURE', name: '电子签名' },
  { value: 'DENSITY_TEST_REPORT', name: '原位密度检测报告' },
  { value: 'DENSITY_TEST_RESULT', name: '原位密度检测结果' },
  { value: 'LIGHT_DYNAMIC_PENETRATION', name: '轻型动力触探检测报告' },
  { value: 'LIGHT_DYNAMIC_PENETRATION_RESULT', name: '轻型动力触探检测结果' },
  { value: 'REBOUND_METHOD_REPORT', name: '回弹法检测混凝土抗压强度报告' },
  { value: 'BECKMAN_BEAM_REPORT', name: '路基路面回弹弯沉检测报告' },
  { value: 'BECKMAN_BEAM_RESULT', name: '路基路面回弹弯沉检测结果' }
]

const formData = reactive({
  id: '',
  dirId: '',
  dirName: '',
  table1Type: '',
  table2Type: '',
  table3Type: '',
  table4Type: '',
  table5Type: '',
  table6Type: '',
  table7Type: '',
  table8Type: '',
  table9Type: '',
  table10Type: '',
  status: 1
})

onMounted(() => {
  loadProcesses()
})

const loadProcesses = async () => {
  try {
    const response = await axios.post('/api/directory/getAll')
    if (response.data.success) {
      processes.value = response.data.data
    }
  } catch (error) {
    console.error('加载流程列表失败:', error)
  }
}

const openAddModal = () => {
  resetForm()
  isEditing.value = false
  showModal.value = true
}

const editProcess = (item) => {
  // 重置表单
  resetForm()
  
  // 复制基本属性
  formData.id = item.id || ''
  formData.dirId = item.dirId || ''
  formData.dirName = item.dirName || ''
  formData.status = item.status || 1
  
  // 从table1Type-table10Type构建队列
  selectedForms.value = []
  for (let i = 1; i <= 10; i++) {
    const type = item[`table${i}Type`]
    if (type) {
      const formType = formTypes.find(ft => ft.value === type)
      if (formType) {
        selectedForms.value.push({ ...formType })
      }
    }
  }
  
  isEditing.value = true
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
  resetForm()
}

const resetForm = () => {
  formData.id = ''
  formData.dirId = ''
  formData.dirName = ''
  formData.table1Type = ''
  formData.table2Type = ''
  formData.table3Type = ''
  formData.table4Type = ''
  formData.table5Type = ''
  formData.table6Type = ''
  formData.table7Type = ''
  formData.table8Type = ''
  formData.table9Type = ''
  formData.table10Type = ''
  formData.status = 1
  selectedForms.value = []
}

// 添加到队列
const addToQueue = (formType) => {
  // 检查队列是否已满（最多10个）
  if (selectedForms.value.length >= 10) {
    alert('队列最多只能添加10个表单类型')
    return
  }
  
  // 允许重复添加相同类型的表单
  selectedForms.value.push({ ...formType })
}

// 从队列中移除
const removeFromQueue = (index) => {
  selectedForms.value.splice(index, 1)
}

const saveProcess = async () => {
  try {
    // 将队列中的表单类型映射到table1Type-table10Type
    for (let i = 1; i <= 10; i++) {
      formData[`table${i}Type`] = selectedForms.value[i - 1]?.value || ''
    }
    
    // 获取当前登录用户信息
    try {
      const userInfoStr = localStorage.getItem('userInfo')
      if (userInfoStr) {
        const userInfo = JSON.parse(userInfoStr)
        formData.createBy = userInfo.userName || userInfo.username || 'admin'
      } else {
        formData.createBy = 'admin'
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
      formData.createBy = 'admin'
    }
    
    const response = await axios.post('/api/directory/save', formData)
    if (response.data.success) {
      alert('保存成功')
      closeModal()
      loadProcesses()
    } else {
      alert('保存失败: ' + response.data.message)
    }
  } catch (error) {
    console.error('保存流程失败:', error)
    alert('保存失败，请稍后重试')
  }
}

const viewProcess = (item) => {
  // 保存流程详情到localStorage，用于后续跳转
  localStorage.setItem('currentDirectory', JSON.stringify(item))
  
  // 跳转到表1类型对应的页面
  if (item.table1Type) {
    const componentMap = {
      'ENTRUSTMENT_LIST': 'Entrustment',
      'REBOUND_METHOD_RECORD': 'ReboundMethodRecord',
      'LIGHT_DYNAMIC_PENETRATION_RECORD': 'LightDynamicPenetrationRecord',
      'NUCLEAR_DENSITY_RECORD': 'NuclearDensityRecord',
      'SAND_REPLACEMENT_RECORD': 'SandReplacementRecord',
      'WATER_REPLACEMENT_RECORD': 'WaterReplacementRecord',
      'CUTTING_RING_RECORD': 'CuttingRingRecord',
      'BECKMAN_BEAM_RECORD': 'BeckmanBeamRecord',
      'SIGNATURE': 'Signature',
      'DENSITY_TEST_REPORT': 'DensityTestReport',
      'DENSITY_TEST_RESULT': 'DensityTestResult',
      'LIGHT_DYNAMIC_PENETRATION': 'LightDynamicPenetration',
      'LIGHT_DYNAMIC_PENETRATION_RESULT': 'LightDynamicPenetrationResult',
      'REBOUND_METHOD_REPORT': 'ReboundMethodReport',
      'BECKMAN_BEAM_REPORT': 'BeckmanBeamReport',
      'BECKMAN_BEAM_RESULT': 'BeckmanBeamResult'
    }
    
    const componentName = componentMap[item.table1Type]
    if (componentName && navigateTo) {
      // 保存当前表单的状态
      localStorage.setItem('currentFormType', item.table1Type)
      localStorage.setItem('currentFormIndex', '0')
      
      // 构建参数，传递流程中的ID
      const props = {}
      if (item.table1Id) {
        props.id = item.table1Id
      }
      
      // 使用navigateTo方法导航到对应的组件
      navigateTo(componentName, props)
    } else {
      alert('暂不支持该类型的页面跳转')
    }
  } else {
    alert('该流程未关联任何表单')
  }
}

const deleteProcess = async (id) => {
  if (confirm('确定要删除该流程吗？')) {
    try {
      const response = await axios.post('/api/directory/delete', { id })
      if (response.data.success) {
        alert('删除成功')
        loadDirectories()
      } else {
        alert('删除失败: ' + response.data.message)
      }
    } catch (error) {
      console.error('删除目录失败:', error)
      alert('删除失败，请稍后重试')
    }
  }
}

const getStatusText = (status) => {
  const statusMap = {
    0: '禁用',
    1: '启用',
    2: '草稿',
    3: '审核中',
    4: '已审核',
    5: '已归档'
  }
  return statusMap[status] || '未知'
}
</script>

<style scoped>
.directory-list-container {
  font-family: Arial, sans-serif;
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h2 {
  margin: 0;
  color: #333;
}

.add-btn {
  background-color: #4CAF50;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.add-btn:hover {
  background-color: #45a049;
}

.directory-table {
  overflow-x: auto;
}

.directory-table table {
  width: 100%;
  border-collapse: collapse;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.directory-table th, .directory-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

.directory-table th {
  background-color: #f2f2f2;
  font-weight: bold;
}

.directory-table tr:hover {
  background-color: #f5f5f5;
}

.view-btn, .edit-btn, .delete-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  margin-right: 5px;
}

.view-btn {
  background-color: #2196F3;
  color: white;
}

.edit-btn {
  background-color: #ff9800;
  color: white;
}

.delete-btn {
  background-color: #f44336;
  color: white;
}

.view-btn:hover {
  background-color: #0b7dda;
}

.edit-btn:hover {
  background-color: #e68a00;
}

.delete-btn:hover {
  background-color: #da190b;
}

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  border-radius: 8px;
  width: 800px;
  max-width: 90%;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #ddd;
}

.modal-header h3 {
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #999;
}

.modal-body {
  padding: 20px;
}

/* 表单布局 - 左右两栏 */
.form-layout {
  display: flex;
  gap: 20px;
}

.left-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.right-panel {
  width: 250px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.form-group {
  margin-bottom: 0;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
  color: #333;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-sizing: border-box;
}

/* 队列容器 */
.queue-container {
  min-height: 200px;
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 10px;
  background-color: #f9f9f9;
}

.empty-queue {
  text-align: center;
  color: #999;
  padding: 40px 20px;
  font-style: italic;
}

.queue-items {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.queue-item {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.queue-item-index {
  font-weight: bold;
  margin-right: 10px;
  color: #666;
  min-width: 20px;
}

.queue-item-name {
  flex: 1;
}

.queue-item-remove {
  background-color: #f44336;
  color: white;
  border: none;
  border-radius: 50%;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 16px;
  line-height: 1;
}

.queue-item-remove:hover {
  background-color: #da190b;
}

/* 表单按钮列表 */
.form-buttons {
  display: flex;
  flex-direction: column;
  gap: 8px;
  max-height: 400px;
  overflow-y: auto;
  padding-right: 5px;
}

.form-button {
  background-color: #f1f1f1;
  color: #333;
  border: 1px solid #ddd;
  padding: 10px 15px;
  border-radius: 4px;
  cursor: pointer;
  text-align: left;
  font-size: 14px;
  transition: all 0.2s ease;
}

.form-button:hover {
  background-color: #e3f2fd;
  border-color: #2196F3;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px solid #ddd;
}

.cancel-btn, .save-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-left: 10px;
}

.cancel-btn {
  background-color: #f1f1f1;
  color: #333;
}

.save-btn {
  background-color: #4CAF50;
  color: white;
}

.cancel-btn:hover {
  background-color: #e0e0e0;
}

.save-btn:hover {
  background-color: #45a049;
}

/* 滚动条样式 */
.form-buttons::-webkit-scrollbar {
  width: 6px;
}

.form-buttons::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.form-buttons::-webkit-scrollbar-thumb {
  background: #ccc;
  border-radius: 3px;
}

.form-buttons::-webkit-scrollbar-thumb:hover {
  background: #999;
}
</style>
