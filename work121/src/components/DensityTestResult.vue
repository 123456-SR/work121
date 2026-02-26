<template>
  <div class="densityTestResult-container">


    <div class="no-print toolbar">
      <div class="toolbar-left">
        <button @click="goToList" class="link-button">&lt; 返回列表</button>
        <span class="record-nav">
          <button
            @click="prevRecord"
            :disabled="currentIndex <= 0"
            class="btn btn-secondary btn-small"
          >
            上一页
          </button>
          <span class="record-nav-info">
            记录 {{ currentIndex + 1 }} / {{ totalRecords }}
          </span>
          <button
            @click="nextRecord"
            :disabled="currentIndex >= totalRecords - 1"
            class="btn btn-secondary btn-small"
          >
            下一页
          </button>
          <button
            @click="addRecord"
            class="btn btn-primary btn-small"
          >
            添加记录
          </button>
          <button
            @click="deleteRecord"
            class="btn btn-danger btn-small"
          >
            删除当前记录
          </button>
        </span>
      </div>
      <div class="toolbar-right">
        <button
          @click="handleSign"
          class="btn btn-secondary btn-small"
        >
          签字
        </button>
        <button
          @click="saveData"
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

    <form id="pdfForm" ref="pdfForm" method="post">
    <div class="header-info" style="justify-content: space-between;">
        <span>统一编号：<input type="text" v-model="formData.unifiedNumber"   name="unifiedNumber" style="width: 150px; border-bottom: 1px solid black; text-align: left;"></span>
        <span></span> <!-- Placeholder for layout balance if needed -->
    </div>

    <h2>原位密度检测结果</h2>

    <table>
        <!-- Header Rows -->
        <tr>
            <td class="label" style="width: 15%;">施工部位</td>
            <td colspan="9" class="left-align"><input type="text" v-model="formData.constructionPart"   name="constructionPart"></td>
        </tr>
        <tr>
            <td class="label">最大干密度<br>(g/cm³)</td>
            <td colspan="2"><input type="text" v-model="formData.maxDryDensity"   name="maxDryDensity"></td>
            <td class="label">最优含水率 %</td>
            <td colspan="2"><input type="text" v-model="formData.optimumMoisture"   name="optimumMoisture"></td>
            <td class="label">最小干密度<br>(g/cm³)</td>
            <td colspan="3"><input type="text" v-model="formData.minDryDensity"   name="minDryDensity"></td>
        </tr>

        <!-- Data Header -->
        <tr>
            <td class="label" style="width: 10%;">样品编号</td>
            <td class="label" style="width: 25%;" colspan="2">检测部位<br>(桩号、高程)</td>
            <td class="label" style="width: 15%;" colspan="2">检测日期</td>
            <td class="label" style="width: 12.5%;">湿密度<br>(g/cm³)</td>
            <td class="label" style="width: 12.5%;">干密度<br>(g/cm³)</td>
            <td class="label" style="width: 12.5%;">含水率<br>%</td>
            <td class="label" style="width: 12.5%;" colspan="2">压实度%</td>
        </tr>

        <!-- Data Rows (20 rows to fill the page) -->
        <template v-for="(n, i_idx) in 20" :key="i_idx">
        <tr>
            <td rowspan="2"><input type="text" :name="'sampleId_' + i_idx" v-model="formData['sampleId_' + i_idx]"></td>
            <td rowspan="2" colspan="2"><input type="text" :name="'location_' + i_idx" v-model="formData['location_' + i_idx]"></td>
            <td rowspan="2" colspan="2"><input type="text" :name="'date_' + i_idx" v-model="formData['date_' + i_idx]"></td>
            <td><input type="text" :name="'wetDensity_' + i_idx" v-model="formData['wetDensity_' + i_idx]"></td>
            <td><input type="text" :name="'dryDensity_' + i_idx" v-model="formData['dryDensity_' + i_idx]"></td>
            <td><input type="text" :name="'moisture_' + i_idx" v-model="formData['moisture_' + i_idx]"></td>
            <td rowspan="2" colspan="2"><input type="text" :name="'compaction_' + i_idx" v-model="formData['compaction_' + i_idx]"></td>
        </tr>
        <tr>
            <td><input type="text" :name="'wetDensity2_' + i_idx" v-model="formData['wetDensity2_' + i_idx]"></td>
            <td><input type="text" :name="'dryDensity2_' + i_idx" v-model="formData['dryDensity2_' + i_idx]"></td>
            <td><input type="text" :name="'moisture2_' + i_idx" v-model="formData['moisture2_' + i_idx]"></td>
        </tr>
        </template>
    </table>

    <div class="footer-info">
        <span style="position: relative;">
          批准：
          <input type="text" v-model="formData.approver" style="width: 100px; border-bottom: 1px solid black;">
          <div v-if="formData.approverSignature" class="signature-overlay" style="left: 40px; top: -20px;">
            <img :src="formData.approverSignature" alt="批准人签名" />
          </div>
        </span>
        <span style="position: relative;">
          审核：
          <input type="text" v-model="formData.reviewer" style="width: 100px; border-bottom: 1px solid black;">
          <div v-if="formData.reviewerSignature" class="signature-overlay" style="left: 40px; top: -20px;">
            <img :src="formData.reviewerSignature" alt="审核人签名" />
          </div>
        </span>
        <span style="position: relative;">
          试验：
          <input type="text" v-model="formData.tester" style="width: 100px; border-bottom: 1px solid black;">
          <div v-if="formData.testerSignature" class="signature-overlay" style="left: 40px; top: -20px;">
            <img :src="formData.testerSignature" alt="试验人签名" />
          </div>
        </span>
    </div>
    </form>



  </div>
</template>

<script setup>
import { reactive, ref, onMounted, inject, defineProps, computed } from 'vue'
import axios from 'axios'

const props = defineProps({
  id: {
    type: String,
    required: false
  },
  wtNum: {
    type: String,
    required: false
  }
})

const navigateTo = inject('navigateTo')

const goToList = () => {
  if (navigateTo) {
    navigateTo('DensityTestResultList')
  }
}

const pdfForm = ref(null)

// 1:N State
const records = ref([])
const currentIndex = ref(0)
const totalRecords = computed(() => records.value.length)

const formData = reactive({
  id: '',
  entrustmentId: '',
  unifiedNumber: '',
  constructionPart: '',
  maxDryDensity: '',
  optimumMoisture: '',
  minDryDensity: '',
  reviewer: '',
  tester: '',
  approver: '',
  reviewerSignature: '',
  testerSignature: '',
  approverSignature: ''
})

// Initialize dynamic fields
const initDynamicFields = () => {
  for (let i_idx = 0; i_idx < 20; i_idx++) {
    formData['sampleId_' + i_idx] = ''
    formData['location_' + i_idx] = ''
    formData['compaction_' + i_idx] = ''
    formData['wetDensity2_' + i_idx] = ''
    formData['wetDensity_' + i_idx] = ''
    formData['dryDensity2_' + i_idx] = ''
    formData['moisture_' + i_idx] = ''
    formData['date_' + i_idx] = ''
    formData['moisture2_' + i_idx] = ''
    formData['dryDensity_' + i_idx] = ''
  }
}

onMounted(() => {
  initDynamicFields()
  loadData()
})

const mapRecordToFormData = (record) => {
  initDynamicFields()
  
  formData.id = record.id || ''
  formData.entrustmentId = record.entrustmentId || props.id
  formData.reviewerSignature = record.reviewSignaturePhoto || ''
  formData.testerSignature = record.inspectSignaturePhoto || ''
  formData.approverSignature = record.approveSignaturePhoto || ''
  
  if (record.dataJson) {
    try {
      const parsed = JSON.parse(record.dataJson)
      Object.assign(formData, parsed)
    } catch (e) {
      console.error('JSON parse error', e)
    }
  }
  
  // Explicit fields override JSON
  if (record.reviewSignaturePhoto) formData.reviewerSignature = record.reviewSignaturePhoto
  if (record.inspectSignaturePhoto) formData.testerSignature = record.inspectSignaturePhoto
  if (record.approveSignaturePhoto) formData.approverSignature = record.approveSignaturePhoto
  if (record.entrustmentId) formData.unifiedNumber = record.entrustmentId
}

const saveCurrentRecordState = () => {
  if (records.value.length === 0) return
  
  const record = records.value[currentIndex.value]
  record.id = formData.id
  record.entrustmentId = formData.entrustmentId
  record.reviewSignaturePhoto = formData.reviewerSignature
  record.inspectSignaturePhoto = formData.testerSignature
  record.approveSignaturePhoto = formData.approverSignature
  
  // Serialize all formData to dataJson
  const dataToSave = { ...formData }
  record.dataJson = JSON.stringify(dataToSave)
}

const prevRecord = () => {
  if (currentIndex.value > 0) {
    saveCurrentRecordState()
    currentIndex.value--
    mapRecordToFormData(records.value[currentIndex.value])
  }
}

const nextRecord = () => {
  if (currentIndex.value < records.value.length - 1) {
    saveCurrentRecordState()
    currentIndex.value++
    mapRecordToFormData(records.value[currentIndex.value])
  }
}

const addRecord = () => {
  saveCurrentRecordState()
  const newRecord = {
    id: '',
    entrustmentId: props.id,
    dataJson: '{}'
  }
  records.value.push(newRecord)
  currentIndex.value = records.value.length - 1
  mapRecordToFormData(newRecord)
}

const deleteRecord = async () => {
  if (records.value.length <= 1) {
    alert('至少保留一条记录')
    return
  }
  
  if (!confirm('确定要删除当前记录吗？')) return

  const currentRecord = records.value[currentIndex.value]
  
  if (currentRecord.id) {
    try {
      const response = await axios.post('/api/density-test/delete', { id: currentRecord.id })
      if (!response.data.success) {
        alert('删除失败: ' + response.data.message)
        return
      }
    } catch (e) {
      console.error('Delete error', e)
      alert('删除失败')
      return
    }
  }
  
  records.value.splice(currentIndex.value, 1)
  if (currentIndex.value >= records.value.length) {
    currentIndex.value = records.value.length - 1
  }
  mapRecordToFormData(records.value[currentIndex.value])
}

const loadData = async () => {
  const key = props.wtNum || props.id
  if (key) {
    try {
      // 1. Try to fetch existing result (List)
      const response = await axios.get('/api/density-test/get-by-entrustment-id', {
        params: { entrustmentId: key }
      })

      if (response.data.success && response.data.data && response.data.data.length > 0) {
        records.value = response.data.data
        currentIndex.value = 0
        mapRecordToFormData(records.value[0])
      } else {
        // 2. If no result, fetch entrustment info
        const entrustmentResponse = await axios.get('/api/jc-core-wt-info/detail', {
          params: { unifiedNumber: key }
        })
        
        const newRecord = {
          id: '',
          entrustmentId: key,
          dataJson: '{}'
        }
        
        if (entrustmentResponse.data.success) {
          const eData = entrustmentResponse.data.data
          formData.entrustmentId = key
          formData.unifiedNumber = eData.wtNum || key || ''
          formData.constructionPart = eData.constructionPart || ''
          
          if (eData.tester) formData.tester = eData.tester
          if (eData.reviewer) formData.reviewer = eData.reviewer
          if (eData.approver) formData.approver = eData.approver
          
          newRecord.dataJson = JSON.stringify(formData)
        }
        
        records.value = [newRecord]
        currentIndex.value = 0
        mapRecordToFormData(newRecord)
      }
    } catch (error) {
      console.error('Error loading data:', error)
    }
  }
}

const saveData = async () => {
  try {
    const dataToSave = {
      id: formData.id,
      entrustmentId: formData.entrustmentId || props.wtNum || props.id,
      dataJson: JSON.stringify(formData),
      reviewSignaturePhoto: formData.reviewerSignature,
      inspectSignaturePhoto: formData.testerSignature,
      approveSignaturePhoto: formData.approverSignature
    }
    
    const response = await axios.post('/api/density-test/save', dataToSave)
    if (response.data.success) {
      alert('保存成功')
      if (response.data.data && response.data.data.id) {
         formData.id = response.data.data.id
         // Update current record in list
         if (records.value[currentIndex.value]) {
             records.value[currentIndex.value].id = formData.id
         }
      }
    } else {
      alert('保存失败: ' + response.data.message)
    }
  } catch (error) {
    console.error('Save error:', error)
    alert('保存失败')
  }
}

const handleSign = async () => {
  const user = JSON.parse(localStorage.getItem('userInfo'))
  if (!user || !user.username) {
    alert('请先登录')
    return
  }

  try {
    const response = await axios.post('/api/signature/get', {
      userAccount: user.username
    })

    if (response.data.success && response.data.data && response.data.data.signatureBlob) {
      const signatureBlob = response.data.data.signatureBlob
      let imgSrc = ''
      
      if (typeof signatureBlob === 'string') {
        imgSrc = `data:image/png;base64,${signatureBlob}`
      } else {
        alert('签名数据格式不支持')
        return
      }

      let signed = false
      const currentName = user.fullName || user.username

      // Match Tester
      if (formData.tester === currentName) {
        formData.testerSignature = imgSrc
        signed = true
      }

      // Match Reviewer
      if (formData.reviewer === currentName) {
        formData.reviewerSignature = imgSrc
        signed = true
      }

      // Match Approver
      if (formData.approver === currentName) {
        formData.approverSignature = imgSrc
        signed = true
      }
      
      if (signed) {
        alert('签名成功')
      } else {
        alert(`当前用户(${currentName})与表单中的试验/审核/批准人员不匹配，无法签名`)
      }
    } else {
      alert('未找到您的电子签名，请先去“电子签名”页面设置')
    }
  } catch (error) {
    console.error('Sign error:', error)
    alert('签名失败')
  }
}

const printDocument = () => {
  window.print()
}

const generatePdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/density_test_result/generate'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}

const previewPdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/density_test_result/preview'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}
</script>

<style scoped>
/* Add signature overlay style */
.signature-overlay {
  position: absolute;
  pointer-events: none;
  z-index: 10;
}
.signature-overlay img {
  width: 80px;
  height: auto;
  opacity: 0.8;
}
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
.record-nav {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  margin-left: 16px;
}
.record-nav-info {
  font-size: 13px;
  color: #666;
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
.btn-primary {
  background-color: #3498db;
  color: #fff;
  border-color: #3498db;
}
.btn-secondary {
  background-color: #fff;
  border-color: #d0d7de;
  color: #34495e;
}
.btn-danger {
  background-color: #e74c3c;
  border-color: #e74c3c;
  color: #fff;
}
.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.btn:not(:disabled):hover {
  filter: brightness(0.95);
}

        .densityTestResult-container {
            font-family: 'SimSun', 'Songti SC', serif;
            width: 210mm; /* A4 width */
            margin: 0 auto;
            padding: 20px;
        }
        h2 {
            text-align: center;
            margin-bottom: 20px;
            font-size: 24px;
            font-weight: bold;
        }
        .header-info {
            display: flex;
            justify-content: space-between;
            margin-bottom: 5px;
            font-weight: bold;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
            table-layout: fixed; /* Fixed column width */
        }
        table, th, td {
            border: 1px solid black;
        }
        th, td {
            text-align: center;
            padding: 2px; /* Reduce padding */
            font-size: 12px; /* Smaller font */
            height: 25px; /* Fixed height */
        }
        .label {
            font-weight: bold;
        }
        .footer-info {
            display: flex;
            justify-content: space-between;
            margin-top: 20px;
            font-weight: bold;
        }
        input[type="text"] {
            border: none;
            width: 90%;
            text-align: center;
            outline: none;
            font-family: inherit; /* Inherit font */
            font-size: inherit;
            background-color: transparent; /* 与周围背景保持一致 */
        }
        .left-align input {
            text-align: left;
        }
        
        @media print {
            .no-print {
                display: none;
            }
            .densityTestResult-container {
                width: 100%;
                margin: 0;
                padding: 0;
            }
        }
</style>
