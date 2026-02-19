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
            <th>统一编号</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, index) in processes" :key="item.id">
            <td>{{ index + 1 }}</td>
            <td>{{ item.dirName }}</td>
            <td>
              <span :class="['status-badge', 'status-' + item.status]">
                {{ getStatusText(item.status) }}
              </span>
            </td>
            <td>
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
              <!-- 左侧：统一编号和队列显示 -->
              <div class="left-panel">
                <div class="form-group">
                  <label>统一编号：</label>
                  <input type="text" v-model="formData.dirName" required placeholder="请输入委托单统一编号">
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
                  <select id="status" v-model="formData.status">
                    <option value="1">启用</option>
                    <option value="2">草稿</option>
                    <option value="3">审核中</option>
                    <option value="4">已审核</option>
                    <option value="5">已归档</option>
                  </select>
                </div>

                <div v-if="needsEntrustmentRoles" class="role-section">
                  <h4>委托单角色配置</h4>
                  <div class="form-group">
                    <label for="wtUndertaker">委托承接人 <span style="color: red">*</span></label>
                    <select id="wtUndertaker" v-model="formData.wtUndertaker" required>
                      <option value="">请选择委托承接人</option>
                      <option v-for="user in userList" :key="user.id" :value="user.userAccount">
                        {{ user.userAccount }} ({{ user.userName }})
                      </option>
                    </select>
                  </div>
                  <div class="form-group">
                    <label for="wtReviewer">委托审核人 <span style="color: red">*</span></label>
                    <select id="wtReviewer" v-model="formData.wtReviewer" required>
                      <option value="">请选择委托审核人</option>
                      <option v-for="user in userList" :key="user.id" :value="user.userAccount">
                        {{ user.userAccount }} ({{ user.userName }})
                      </option>
                    </select>
                  </div>
                </div>

                <div v-if="needsRecordRoles" class="role-section">
                  <h4>记录表角色配置</h4>
                  <div class="form-group">
                    <label for="jcFiller">记录填写人</label>
                    <select id="jcFiller" v-model="formData.jcFiller">
                      <option value="">请选择记录填写人</option>
                      <option v-for="user in userList" :key="user.id" :value="user.userAccount">
                        {{ user.userAccount }} ({{ user.userName }})
                      </option>
                    </select>
                  </div>
                  <div class="form-group">
                    <label for="jcTester">记录检测人 <span style="color: red">*</span></label>
                    <select id="jcTester" v-model="formData.jcTester" required>
                      <option value="">请选择记录检测人</option>
                      <option v-for="user in userList" :key="user.id" :value="user.userAccount">
                        {{ user.userAccount }} ({{ user.userName }})
                      </option>
                    </select>
                  </div>
                  <div class="form-group">
                    <label for="jcReviewer">记录审核人 <span style="color: red">*</span></label>
                    <select id="jcReviewer" v-model="formData.jcReviewer" required>
                      <option value="">请选择记录审核人</option>
                      <option v-for="user in userList" :key="user.id" :value="user.userAccount">
                        {{ user.userAccount }} ({{ user.userName }})
                      </option>
                    </select>
                  </div>
                </div>

                <div v-if="needsReportRoles" class="role-section">
                  <h4>报告/结果表角色配置</h4>
                  <div class="form-group">
                    <label for="bgTester">报告/结果检测人 <span style="color: red">*</span></label>
                    <select id="bgTester" v-model="formData.bgTester" required>
                      <option value="">请选择报告/结果检测人</option>
                      <option v-for="user in userList" :key="user.id" :value="user.userAccount">
                        {{ user.userAccount }} ({{ user.userName }})
                      </option>
                    </select>
                  </div>
                  <div class="form-group">
                    <label for="bgReviewer">报告/结果审核人 <span style="color: red">*</span></label>
                    <select id="bgReviewer" v-model="formData.bgReviewer" required>
                      <option value="">请选择报告/结果审核人</option>
                      <option v-for="user in userList" :key="user.id" :value="user.userAccount">
                        {{ user.userAccount }} ({{ user.userName }})
                      </option>
                    </select>
                  </div>
                  <div class="form-group">
                    <label for="bgApprover">报告/结果批准人 <span style="color: red">*</span></label>
                    <select id="bgApprover" v-model="formData.bgApprover" required>
                      <option value="">请选择报告/结果批准人</option>
                      <option v-for="user in userList" :key="user.id" :value="user.userAccount">
                        {{ user.userAccount }} ({{ user.userName }})
                      </option>
                    </select>
                  </div>
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
import { ref, reactive, onMounted, inject, computed } from 'vue'
import axios from 'axios'

// 注入导航方法
const navigateTo = inject('navigateTo')

const processes = ref([])
const showModal = ref(false)
const isEditing = ref(false)
const userList = ref([])

// 选中的表单队列
const selectedForms = ref([])

// 计算属性：根据选中的表单类型决定需要显示的角色选择框
const needsEntrustmentRoles = computed(() => {
  return selectedForms.value.some(f => f.value === 'ENTRUSTMENT_LIST')
})

const needsRecordRoles = computed(() => {
  return selectedForms.value.some(f => f.value.includes('RECORD'))
})

const needsReportRoles = computed(() => {
  return selectedForms.value.some(f => 
    f.value.includes('REPORT') || 
    f.value.includes('RESULT')
  )
})

// needsSharedApprover removed as it is now part of needsReportRoles


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
  status: 1,
  wtUndertaker: '',
  wtReviewer: '',
  jcFiller: '',
  jcTester: '',
  jcReviewer: '',
  bgTester: '',
  bgReviewer: '',
  bgApprover: ''
})

onMounted(() => {
  loadProcesses()
  loadUsers()
})

const loadUsers = async () => {
  try {
    const response = await axios.get('/api/user/list')
    if (response.data.success) {
      userList.value = response.data.data
    }
  } catch (error) {
    console.error('加载用户列表失败:', error)
  }
}

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
  formData.wtUndertaker = item.wtUndertaker || ''
  formData.wtReviewer = item.wtReviewer || ''
  formData.jcFiller = item.jcFiller || ''
  formData.jcTester = item.jcTester || ''
  formData.jcReviewer = item.jcReviewer || ''
  formData.bgTester = item.bgTester || ''
  formData.bgReviewer = item.bgReviewer || ''
  formData.bgApprover = item.bgApprover || ''
  
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
  formData.wtUndertaker = ''
  formData.wtReviewer = ''
  formData.jcTester = ''
  formData.jcReviewer = ''
  formData.bgTester = ''
  formData.bgReviewer = ''
  formData.bgApprover = ''
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
    console.log('开始保存流程')
    
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
    
    console.log('发送保存请求，表单数据:', formData)
    console.log('axios实例:', axios)
    console.log('请求URL:', '/api/directory/save')
    
    // 测试axios是否能发送简单请求
    try {
      console.log('开始发送测试请求')
      const testResponse = await axios.get('https://jsonplaceholder.typicode.com/todos/1')
      console.log('测试请求成功:', testResponse.data)
    } catch (testError) {
      console.error('测试请求失败:', testError)
    }
    
    const response = await axios.post('/api/directory/save', formData)
    
    console.log('保存请求响应:', response.data)
    
    if (response.data.success) {
      // 保存成功后，显示返回的流程对象，检查table1Id-table10Id是否已生成
      if (response.data.data) {
        console.log('后端返回的流程对象:', response.data.data)
        console.log('table1Id:', response.data.data.table1Id)
        console.log('table2Id:', response.data.data.table2Id)
        console.log('table3Id:', response.data.data.table3Id)
        console.log('table4Id:', response.data.data.table4Id)
      }
      
      alert('保存成功')
      closeModal()
      loadProcesses()
    } else {
      alert('保存失败: ' + response.data.message)
    }
  } catch (error) {
    console.error('保存流程失败:', error)
    console.error('错误类型:', error.constructor.name)
    console.error('错误消息:', error.message)
    console.error('错误堆栈:', error.stack)
    if (error.response) {
      console.error('响应数据:', error.response.data)
      console.error('响应状态:', error.response.status)
      console.error('响应头:', error.response.headers)
    } else if (error.request) {
      console.error('请求配置:', error.request)
    }
    alert('保存失败，请稍后重试')
  }
}

const deleteProcess = async (id) => {
  if (confirm('确定要删除该流程吗？')) {
    try {
      const response = await axios.post('/api/directory/delete', { id })
      if (response.data.success) {
        alert('删除成功')
        loadProcesses()
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
.process-list-container {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  padding: 24px;
  background-color: #f8f9fa;
  min-height: 100vh;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  background-color: white;
  padding: 16px 24px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.header h2 {
  margin: 0;
  color: #2c3e50;
  font-weight: 600;
}

.add-btn {
  background-color: #3498db;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: background-color 0.2s;
  box-shadow: 0 2px 4px rgba(52, 152, 219, 0.3);
}

.add-btn:hover {
  background-color: #2980b9;
}

.process-table {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  overflow: hidden;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  padding: 16px 24px;
  text-align: left;
  border-bottom: 1px solid #edf2f7;
}

th {
  background-color: #f8f9fa;
  color: #5d6d7e;
  font-weight: 600;
  font-size: 14px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

tr:last-child td {
  border-bottom: none;
}

tr:hover {
  background-color: #f8f9fa;
}

/* Status Badges */
.status-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.status-1 { /* 启用 */
  background-color: #e8f5e9;
  color: #2e7d32;
}

.status-0 { /* 禁用 */
  background-color: #ffebee;
  color: #c62828;
}

.status-2 { /* 草稿 */
  background-color: #fff3e0;
  color: #ef6c00;
}

.status-3 { /* 审核中 */
  background-color: #e3f2fd;
  color: #1565c0;
}

.status-4 { /* 已审核 */
  background-color: #e0f2f1;
  color: #00695c;
}

.status-5 { /* 已归档 */
  background-color: #f3e5f5;
  color: #6a1b9a;
}

/* Action Buttons */
.edit-btn, .delete-btn {
  padding: 6px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  transition: all 0.2s;
  margin-right: 8px;
}

.edit-btn {
  background-color: #fff3e0;
  color: #f57c00;
}

.edit-btn:hover {
  background-color: #ffe0b2;
}

.delete-btn {
  background-color: #ffebee;
  color: #d32f2f;
}

.delete-btn:hover {
  background-color: #ffcdd2;
}

/* Modal Styles Refined */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(2px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  border-radius: 12px;
  width: 800px;
  max-width: 90%;
  max-height: 85vh;
  overflow-y: auto;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #edf2f7;
}

.modal-header h3 {
  margin: 0;
  color: #2c3e50;
  font-size: 18px;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #95a5a6;
  transition: color 0.2s;
}

.close-btn:hover {
  color: #34495e;
}

.modal-body {
  padding: 24px;
}

.form-layout {
  display: flex;
  gap: 32px;
}

.left-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.right-panel {
  width: 260px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #34495e;
  font-size: 14px;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #dfe6e9;
  border-radius: 6px;
  box-sizing: border-box;
  font-size: 14px;
  transition: border-color 0.2s;
}

.form-group input:focus,
.form-group select:focus {
  border-color: #3498db;
  outline: none;
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
}

.queue-container {
  min-height: 240px;
  border: 1px solid #dfe6e9;
  border-radius: 6px;
  padding: 12px;
  background-color: #f8f9fa;
}

.queue-item {
  display: flex;
  align-items: center;
  padding: 10px 14px;
  background-color: white;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.02);
  margin-bottom: 8px;
  transition: transform 0.1s;
}

.queue-item:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.form-button {
  background-color: white;
  color: #4a5568;
  border: 1px solid #e2e8f0;
  padding: 10px 16px;
  border-radius: 6px;
  cursor: pointer;
  text-align: left;
  font-size: 13px;
  transition: all 0.2s;
}

.form-button:hover {
  background-color: #ebf8ff;
  border-color: #3498db;
  color: #2c5282;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #edf2f7;
  gap: 12px;
}

.save-btn {
  background-color: #3498db;
  color: white;
  padding: 10px 24px;
  border: none;
  border-radius: 6px;
  font-weight: 500;
  margin-left: 0;
}

.save-btn:hover {
  background-color: #2980b9;
}

.cancel-btn {
  background-color: white;
  color: #7f8c8d;
  border: 1px solid #dfe6e9;
  padding: 10px 24px;
  margin-left: 0;
}

.cancel-btn:hover {
  background-color: #f5f6fa;
  color: #2c3e50;
}
</style>
