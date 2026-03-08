<template>
  <div class="sample-transfer-container">
    <div class="no-print toolbar">
      <div class="toolbar-left">
        <button @click="goToList" class="link-button">&lt; 返回列表</button>
      </div>
      <div class="toolbar-right">
        <button
          @click="saveForm"
          class="btn btn-secondary btn-small"
        >
          保存
        </button>
        <button
          @click="printDocument"
          class="btn btn-secondary btn-small"
        >
          打印此单
        </button>
        <button
          @click="generatePdf"
          class="btn btn-secondary btn-small"
        >
          下载PDF
        </button>
        <button
          @click="previewPdf"
          class="btn btn-secondary btn-small"
        >
          预览PDF
        </button>
      </div>
    </div>

    <form id="sampleTransferForm" ref="pdfForm" method="post">
      <h2>样品流转单</h2>

      <table class="sample-transfer-table">
        <!-- 第一行 -->
        <tr>
          <td class="label">检测室</td>
          <td><input type="text" v-model="formData.testingRoom" name="testingRoom"></td>
          <td class="label">样品名称</td>
          <td><input type="text" v-model="formData.sampleName" name="sampleName"></td>
          <td class="label">规格/型号</td>
          <td><input type="text" v-model="formData.spec" name="spec"></td>
        </tr>
        <!-- 第二行 -->
        <tr>
          <td class="label">样品编号</td>
          <td><input type="text" v-model="formData.sampleNumber" name="sampleNumber"></td>
          <td class="label">接样日期</td>
          <td><input type="text" v-model="formData.receiveDate" name="receiveDate"></td>
          <td class="label">样品状态</td>
          <td><input type="text" v-model="formData.sampleStatus" name="sampleStatus"></td>
        </tr>
        <!-- 第三行 -->
        <tr>
          <td class="label">样品数量</td>
          <td><input type="text" v-model="formData.sampleQuantity" name="sampleQuantity"></td>
          <td class="label" rowspan="3">样品历史及概况</td>
          <td colspan="3" rowspan="3">
            <textarea v-model="formData.sampleHistory" name="sampleHistory"></textarea>
          </td>
        </tr>
        <!-- 第四行 -->
        <tr>
          <td class="label" rowspan="2">检测项目<br>检测依据</td>
          <td rowspan="2">
            <textarea v-model="formData.testItems" name="testItems"></textarea>
          </td>
        </tr>
        <!-- 第五行 -->
        <tr>
          <!-- 空行，用于对齐 -->
        </tr>
        <!-- 第六行 -->
        <tr>
          <td class="label">领样人</td>
          <td><input type="text" v-model="formData.sampleTaker" name="sampleTaker"></td>
          <td class="label">接样人/发样人</td>
          <td colspan="3"><input type="text" v-model="formData.sampleReceiver" name="sampleReceiver"></td>
        </tr>
        <!-- 第七行 -->
        <tr>
          <td class="label">备注</td>
          <td colspan="5"><input type="text" v-model="formData.remarks" name="remarks"></td>
        </tr>
      </table>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, inject } from 'vue'
import axios from 'axios'

// 注入导航方法
const navigateTo = inject('navigateTo')

const props = defineProps({
  id: {
    type: String,
    default: null
  },
  wtNum: {
    type: String,
    default: null
  }
})

const pdfForm = ref(null)

const formData = reactive({
  testingRoom: '',
  sampleName: '',
  spec: '',
  sampleNumber: '',
  receiveDate: '',
  sampleStatus: '',
  sampleQuantity: '',
  sampleHistory: '',
  testItems: '',
  sampleTaker: '',
  sampleReceiver: '',
  remarks: ''
})

const currentId = ref(props.id)

// 格式化日期
const formatDate = (dateStr, format) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  if (isNaN(date.getTime())) return ''
  
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  
  if (format === 'YYYY-MM-DD') {
    return `${y}-${m}-${d}`
  }
  return `${y}-${m}-${d}`
}

const mapDataToForm = (data) => {
  currentId.value = data.id
  formData.testingRoom = data.testLab || ''
  formData.sampleName = data.sampleName || ''
  formData.spec = data.specModel || ''
  formData.sampleNumber = data.sampleNumber || ''
  formData.receiveDate = data.receiveDate ? formatDate(data.receiveDate, 'YYYY-MM-DD') : ''
  formData.sampleStatus = data.sampleStatus || ''
  formData.sampleQuantity = data.sampleQuantity || ''
  formData.sampleHistory = data.sampleHistory || ''
  formData.testItems = data.testItems || ''
  formData.sampleTaker = data.withdrawPerson || ''
  formData.sampleReceiver = data.receivePerson || ''
  formData.remarks = data.remarks || ''
}

const loadDataByWtNum = async (wtNum) => {
  if (!wtNum) return
  try {
    console.log('Fetching data for wtNum:', wtNum)
    const response = await axios.get(`/api/sample-circulation/by-sample-number?sampleNumber=${encodeURIComponent(wtNum)}`)
    if (response.data.success && response.data.data) {
      mapDataToForm(response.data.data)
    }
  } catch (error) {
    console.error('Error loading data by sample number:', error)
  }
}

const saveForm = async (silent = false) => {
  const idToSave = currentId.value || props.id
  
  // Get current user
  const user = JSON.parse(localStorage.getItem('userInfo') || '{}')
  const currentUserName = user ? (user.userName || user.username) : '未知用户'
  const currentUserId = user ? (user.username || user.id) : ''
  
  const payload = {
    id: idToSave,
    testLab: formData.testingRoom,
    sampleName: formData.sampleName,
    specModel: formData.spec,
    sampleNumber: formData.sampleNumber,
    receiveDate: formData.receiveDate,
    sampleStatus: formData.sampleStatus,
    sampleQuantity: formData.sampleQuantity,
    testItems: formData.testItems,
    testStandard: '', // 检测依据，前端表单中没有这个字段
    sampleHistory: formData.sampleHistory,
    receivePerson: formData.sampleReceiver,
    receivePersonId: '', // 接样人ID，前端表单中没有这个字段
    withdrawPerson: formData.sampleTaker,
    withdrawPersonId: '', // 领样人ID，前端表单中没有这个字段
    remarks: formData.remarks,
    createBy: currentUserName,
    updateBy: currentUserName
  }
  
  try {
    const response = await axios.post('/api/sample-circulation/save', payload)
    if (response.data.success) {
      if (response.data.data && response.data.data.id) {
        currentId.value = response.data.data.id
      }
      if (!silent) alert('保存成功')
      return true
    } else {
      if (!silent) alert('保存失败: ' + response.data.message)
      return false
    }
  } catch (error) {
    console.error('Save error:', error)
    alert('保存出错')
    return false
  }
}

const printDocument = () => {
  window.print()
}

const generatePdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/generate'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}

const previewPdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/preview'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}

const goToList = () => {
  navigateTo('SampleTransferList')
}

onMounted(() => {
  if (props.wtNum) {
    loadDataByWtNum(props.wtNum)
  }
  // 设置默认值
  formData.receiveDate = new Date().toISOString().split('T')[0]
  formData.sampleStatus = '正常'
})
</script>

<style scoped>
.no-print {
  margin-bottom: 20px;
}
.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}
.toolbar-left,
.toolbar-right {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}
.link-button {
  background: none;
  border: none;
  color: #3498db;
  cursor: pointer;
  font-size: 14px;
  padding: 0;
}
.link-button:hover {
  text-decoration: underline;
}
.btn {
  padding: 6px 12px;
  border-radius: 4px;
  border: 1px solid transparent;
  font-size: 13px;
  cursor: pointer;
  background-color: #f5f7fa;
  color: #333;
  transition: all 0.2s;
}
.btn-small {
  padding: 4px 10px;
  font-size: 12px;
}
.btn-secondary {
  background-color: #fff;
  border-color: #d0d7de;
  color: #34495e;
}
.btn-secondary:hover {
  filter: brightness(0.95);
}

.sample-transfer-container {
  font-family: 'SimSun', 'Songti SC', serif;
  width: 260mm;
  margin: 0 auto;
  padding: 20px;
}

h2 {
  text-align: center;
  margin-bottom: 20px;
  font-size: 24px;
  font-weight: bold;
  color: black;
}

.sample-transfer-table {
  width: 100%;
  border-collapse: collapse;
  border: 2px solid black;
  margin-top: 20px;
}

.sample-transfer-table td {
  border: 1px solid black;
  padding: 5px;
  vertical-align: middle;
  text-align: center;
  font-size: 14px;
}

.sample-transfer-table .label {
  font-weight: bold;
  background-color: transparent;
  text-align: left;
  padding-left: 10px;
  width: auto;
}

.sample-transfer-table input,
.sample-transfer-table textarea {
  width: 98%;
  border: none;
  outline: none;
  font-family: inherit;
  font-size: inherit;
  background-color: transparent;
  text-align: center;
}

.sample-transfer-table input:focus,
.sample-transfer-table textarea:focus {
  background-color: #f0f8ff;
}

.sample-transfer-table textarea {
  resize: none;
  overflow: hidden;
  text-align: left;
  height: 100%;
  min-height: 80px;
}

@media print {
  .no-print {
    display: none;
  }
  .sample-transfer-container {
    width: 100%;
    margin: 0;
    padding: 0;
  }
  .sample-transfer-table {
    font-size: 12px;
  }
  .sample-transfer-table td {
    padding: 4px;
  }
}
</style>