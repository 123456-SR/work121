<template>
  <div class="nuclearDensityRecord-container">


    <div class="no-print" style="margin-bottom: 20px; display: flex; align-items: center; justify-content: space-between;">
        <div>
            <button @click="goToList" style="text-decoration: none; color: blue; background: none; border: none; cursor: pointer; padding: 0;">&lt; 返回列表</button>
            <span v-if="!draftMode" style="margin-left: 20px;">
                <button @click="prevRecord" :disabled="currentIndex <= 0">上一页</button>
                <span style="margin: 0 10px;">记录 {{ currentIndex + 1 }} / {{ totalRecords }}</span>
                <button @click="nextRecord" :disabled="currentIndex >= totalRecords - 1">下一页</button>
                <button @click="addRecord" style="margin-left: 10px;">添加记录</button>
                <button @click="deleteRecord" style="margin-left: 10px; color: red;">删除当前记录</button>
            </span>
        </div>
        
        <div style="display: flex; align-items: center;">
            <div v-if="formData.status !== undefined" style="margin-right: 20px; font-weight: bold; color: #666;">
                状态: <span :style="{color: getStatusColor(formData.status)}">{{ getStatusText(formData.status) }}</span>
            </div>

            <!-- Workflow Buttons -->
            <template v-if="formData.id && !draftMode">
                <button v-if="formData.status === 0 || formData.status === 2" @click="submitWorkflow('SUBMIT')" style="margin-right: 10px; background-color: #4CAF50; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;">提交审核</button>
                
                <button v-if="formData.status === 1" @click="submitWorkflow('AUDIT_PASS')" style="margin-right: 10px; background-color: #2196F3; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;">审核通过</button>
                <button v-if="formData.status === 1" @click="submitWorkflow('REJECT')" style="margin-right: 10px; background-color: #f44336; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;">打回</button>
            </template>

            <button @click="saveData">保存</button>
            <button v-if="!draftMode" @click="handleSign" style="margin-left: 10px;">签字</button>
            <button v-if="!draftMode" @click="printDocument" style="margin-left: 10px;">打印此单</button>
            <button v-if="!draftMode" @click="generatePdf" style="margin-left: 10px;">下载PDF</button>
            <button v-if="!draftMode" @click="previewPdf" style="margin-left: 10px;">预览PDF</button>
        </div>
    </div>

    <form id="pdfForm" ref="pdfForm" method="post">
    <h2>原位密度检测记录表（核子法）</h2>

    <div class="header-info">
        <span>委托单位：<input type="text" v-model="formData.entrustingUnit"   name="entrustingUnit" style="width: 250px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>统一编号：<input type="text" v-model="formData.unifiedNumber"   name="unifiedNumber" style="width: 150px; border-bottom: 1px solid black;"></span>
    </div>

    <table>
        <!-- Header Section -->
        <tr>
            <td class="label">工程名称</td>
            <td colspan="4"><input type="text" v-model="formData.projectName"   name="projectName"></td>
            <td class="label">委托日期</td>
            <td colspan="2"><input type="text" v-model="formData.commissionDate"   name="commissionDate"></td>
        </tr>
        <tr>
            <td class="label">施工部位</td>
            <td colspan="4"><input type="text" v-model="formData.constructionPart"   name="constructionPart"></td>
            <td class="label">检测类别</td>
            <td colspan="2"><input type="text" v-model="formData.testCategory"   name="testCategory"></td>
        </tr>
        <tr>
            <td class="label">仪器设备</td>
            <td colspan="4"><input type="text" v-model="formData.equipment"   name="equipment"></td>
            <td class="label">检测方法</td>
            <td colspan="2"><input type="text" v-model="formData.testMethod"   name="testMethod" value="核子法"></td>
        </tr>
        <tr>
            <td class="label">样品名称及<br>状态</td>
            <td colspan="4"><input type="text" v-model="formData.sampleNameStatus"   name="sampleNameStatus"></td>
            <td class="label">依据标准</td>
            <td colspan="2"><input type="text" v-model="formData.standard"   name="standard"></td>
        </tr>
        <tr>
            <td class="label">设计压实度%</td>
            <td colspan="7"><input type="text" v-model="formData.designCompaction"   name="designCompaction"></td>
        </tr>
        <tr>
            <td class="label">最大干密度<br>(g/cm³)</td>
            <td colspan="2"><input type="text" v-model="formData.maxDryDensity"   name="maxDryDensity"></td>
            <td class="label">最优含水率 %</td>
            <td colspan="2"><input type="text" v-model="formData.optimumMoisture"   name="optimumMoisture"></td>
            <td class="label">最小干密度<br>(g/cm³)</td>
            <td><input type="text" v-model="formData.minDryDensity"   name="minDryDensity"></td>
        </tr>

        <!-- Data Header -->
        <tr>
            <td class="label" style="width: 10%;">样品编号</td>
            <td class="label" style="width: 20%;">检测部位<br>(桩号、高程)</td>
            <td class="label" style="width: 12%;">检测日期</td>
            <td class="label" style="width: 12%;">湿密度<br>(g/cm³)</td>
            <td class="label" style="width: 12%;">干密度<br>(g/cm³)</td>
            <td class="label" style="width: 12%;">含水率<br>%</td>
            <td class="label" style="width: 12%;">压实度%</td>
            <td class="label" style="width: 10%;">备注</td>
        </tr>

        <!-- Data Rows -->
        <template v-for="(n, i_idx) in 20" :key="i_idx">
        <tr>
            <td><input type="text" :name="'sampleId_' + i_idx" v-model="formData['sampleId_' + i_idx]"></td>
            <td><input type="text" :name="'location_' + i_idx" v-model="formData['location_' + i_idx]"></td>
            <td><input type="text" :name="'date_' + i_idx" v-model="formData['date_' + i_idx]"></td>
            <td><input type="text" :name="'wetDensity_' + i_idx" v-model="formData['wetDensity_' + i_idx]"></td>
            <td><input type="text" :name="'dryDensity_' + i_idx" v-model="formData['dryDensity_' + i_idx]"></td>
            <td><input type="text" :name="'moisture_' + i_idx" v-model="formData['moisture_' + i_idx]"></td>
            <td><input type="text" :name="'compaction_' + i_idx" v-model="formData['compaction_' + i_idx]"></td>
            <td><input type="text" :name="'remarks_' + i_idx" v-model="formData['remarks_' + i_idx]"></td>
        </tr>
        </template>
    </table>

    <div class="footer-info">

        <span style="position: relative;">
          审核：<input type="text" v-model="formData.reviewer" name="reviewer" style="width: 100px; border-bottom: 1px solid black;" readonly>
        </span>
        <span style="position: relative;">
          检测：<input type="text" v-model="formData.tester" name="tester" style="width: 100px; border-bottom: 1px solid black;">
          <div v-if="formData.testerSignature" style="position: absolute; top: -20px; left: 40px; pointer-events: none;">
            <img :src="formData.testerSignature" style="width: 80px; height: auto;" />
          </div>
        </span>
    </div>


    </form>



  </div>
</template>

<script setup>
import { reactive, ref, onMounted, inject, computed } from 'vue'
import axios from 'axios'

const props = defineProps({
  id: {
    type: String,
    default: ''
  },
  wtNum: {
    type: String,
    default: null
  },
  draftMode: {
    type: Boolean,
    default: false
  }
})

const draftMode = computed(() => props.draftMode)

// 注入导航方法
const navigateTo = inject('navigateTo')

const pdfForm = ref(null)

// Multi-record support
const records = ref([])
const currentIndex = ref(0)
const totalRecords = computed(() => records.value.length)

const formData = reactive({
  id: '',
  entrustmentId: '',
  entrustingUnit: '',
  unifiedNumber: '',
  projectName: '',
  commissionDate: '',
  constructionPart: '',
  testCategory: '',
  equipment: '',
  testMethod: '核子法',
  sampleNameStatus: '',
  standard: '',
  designCompaction: '',
  maxDryDensity: '',
  optimumMoisture: '',
  minDryDensity: '',
  // approver: '',
  reviewer: '',
  tester: '',
  // approverSignature: '',
  reviewerSignature: '',
  testerSignature: '',
  status: 0
})

const getStatusText = (status) => {
    const s = parseInt(status)
    switch(s) {
        case 0: return '草稿'
        case 1: return '待审核'
        case 2: return '已打回'
        case 3: return '待签字'
        case 4: return '待批准'
        case 5: return '已通过'
        default: return '未知'
    }
}

const getStatusColor = (status) => {
    const s = parseInt(status)
    switch(s) {
        case 0: return '#9E9E9E' // Grey
        case 1: return '#2196F3' // Blue
        case 2: return '#F44336' // Red
        case 3: return '#FF9800' // Orange
        case 4: return '#9C27B0' // Purple
        case 5: return '#4CAF50' // Green
        default: return '#000000'
    }
}

const submitWorkflow = async (action) => {
    if (!formData.id) {
        alert('请先保存记录')
        return
    }
    
    const user = JSON.parse(localStorage.getItem('userInfo'))
    if (!user || !user.username) {
        alert('请先登录')
        return
    }

    let signatureData = null
    
    if (action === 'SUBMIT') {
        // Role check: Only tester can submit
        if (formData.tester && user.username !== formData.tester) {
            alert('您不是该单据的检测人 (' + formData.tester + ')，无权提交')
            return
        }

        if (!formData.testerSignature) {
            alert('请先进行检测人签字')
            return
        }
        signatureData = formData.testerSignature
    } else if (action === 'AUDIT_PASS' || (action === 'REJECT' && formData.status === 1)) {
        // Role check: Only reviewer can audit/reject at status 1
        if (formData.reviewer && user.username !== formData.reviewer) {
            alert('您不是该单据的复核人 (' + formData.reviewer + ')，无权操作')
            return
        }
    }
    // else if (action === 'SIGN_REVIEW') {
    //    // Role check: Only reviewer can sign
    //    if (formData.reviewer && user.username !== formData.reviewer) {
    //        alert('您不是该单据的复核人 (' + formData.reviewer + ')，无权签字')
    //        return
    //    }
    //
    //    if (!formData.reviewerSignature) {
    //        alert('请先进行复核人签字')
    //        return
    //    }
    //    signatureData = formData.reviewerSignature
    //
    //}

    const request = {
        tableType: 'NUCLEAR_DENSITY',
        recordId: formData.id,
        action: action,
        userAccount: user.username,
        signatureData: signatureData,
        nextHandler: ''
    }

    if (action === 'REJECT') {
        const reason = prompt('请输入打回原因:')
        if (!reason) return
        request.rejectReason = reason
    }

    try {
        const response = await axios.post('/api/workflow/handle', request)
        if (response.data.success) {
            alert('操作成功')
            loadData()
        } else {
            alert('操作失败: ' + response.data.message)
        }
    } catch (e) {
        console.error('Workflow error', e)
        alert('操作异常')
    }
}

const initDynamicFields = () => {
    for (let i = 0; i < 20; i++) {
        formData[`sampleId_${i}`] = ''
        formData[`location_${i}`] = ''
        formData[`date_${i}`] = ''
        formData[`wetDensity_${i}`] = ''
        formData[`dryDensity_${i}`] = ''
        formData[`moisture_${i}`] = ''
        formData[`compaction_${i}`] = ''
        formData[`remarks_${i}`] = ''
    }
}

const mapRecordToFormData = (record) => {
  // Clear existing dynamic fields
  for (let i = 0; i < 20; i++) {
    formData[`sampleId_${i}`] = ''
    formData[`location_${i}`] = ''
    formData[`date_${i}`] = ''
    formData[`wetDensity_${i}`] = ''
    formData[`dryDensity_${i}`] = ''
    formData[`moisture_${i}`] = ''
    formData[`compaction_${i}`] = ''
    formData[`remarks_${i}`] = ''
  }

  // Basic fields
  formData.id = record.id || ''
  formData.entrustmentId = record.entrustmentId || props.id
  formData.status = record.status !== undefined ? record.status : 0
  
  // Signature photos
  formData.reviewerSignature = record.reviewSignaturePhoto || ''
  formData.testerSignature = record.inspectSignaturePhoto || ''
  
  // Person names
  formData.reviewer = record.reviewer || ''
  formData.tester = record.tester || ''

  // Map fields from BusinessEntity/Entrustment (Always map these first as defaults)
  if (record.clientUnit) formData.entrustingUnit = record.clientUnit
  if (record.wtNum) formData.unifiedNumber = record.wtNum
  if (record.projectName) formData.projectName = record.projectName
  if (record.commissionDate) formData.commissionDate = record.commissionDate
  if (record.constructionPart) formData.constructionPart = record.constructionPart
  if (record.testCategory) formData.testCategory = record.testCategory
  if (record.equipment) formData.equipment = record.equipment
  if (record.sampleName) formData.sampleNameStatus = record.sampleName
  if (record.testBasis) formData.standard = record.testBasis
  if (record.entrustmentId) formData.unifiedNumber = record.entrustmentId // Ensure entrustmentId also maps

  // Parse JSON data if available
  if (record.dataJson) {
    try {
      const parsed = JSON.parse(record.dataJson)
      // Merge parsed data into formData
      // Explicitly handle dynamic fields to ensure they update
      Object.keys(parsed).forEach(key => {
        formData[key] = parsed[key]
      })
      // Ensure specific fields are set if they exist in parsed
      if (parsed.entrustingUnit) formData.entrustingUnit = parsed.entrustingUnit
      if (parsed.unifiedNumber) formData.unifiedNumber = parsed.unifiedNumber
      if (parsed.projectName) formData.projectName = parsed.projectName
      if (parsed.commissionDate) formData.commissionDate = parsed.commissionDate
      if (parsed.constructionPart) formData.constructionPart = parsed.constructionPart
      if (parsed.testCategory) formData.testCategory = parsed.testCategory
      if (parsed.equipment) formData.equipment = parsed.equipment
      if (parsed.sampleNameStatus) formData.sampleNameStatus = parsed.sampleNameStatus
      if (parsed.standard) formData.standard = parsed.standard
      if (parsed.designCompaction) formData.designCompaction = parsed.designCompaction
      if (parsed.maxDryDensity) formData.maxDryDensity = parsed.maxDryDensity
      if (parsed.optimumMoisture) formData.optimumMoisture = parsed.optimumMoisture
      if (parsed.minDryDensity) formData.minDryDensity = parsed.minDryDensity
    } catch (e) {
      console.error('JSON parse error', e)
    }
  } else {
    // If no JSON data, try to set defaults from entrustment info (if available on record)
    // But usually record should have dataJson. If new record, we set defaults in addRecord/loadData
    if (record.wtNum) formData.unifiedNumber = record.wtNum
    if (record.clientUnit) formData.entrustingUnit = record.clientUnit
    if (record.projectName) formData.projectName = record.projectName
    if (record.commissionDate) formData.commissionDate = record.commissionDate
    if (record.constructionPart) formData.constructionPart = record.constructionPart
    if (record.testCategory) formData.testCategory = record.testCategory
    if (record.sampleName) formData.sampleNameStatus = record.sampleName
  }
}

const saveCurrentRecordState = () => {
  if (records.value.length > 0 && currentIndex.value >= 0 && currentIndex.value < records.value.length) {
     const current = records.value[currentIndex.value]
     // Update current record object with latest formData values
     current.dataJson = JSON.stringify(formData)
     current.reviewer = formData.reviewer
     current.tester = formData.tester
     current.reviewSignaturePhoto = formData.reviewerSignature
     current.inspectSignaturePhoto = formData.testerSignature
     // Also update flattened fields if needed for list view or other logic
  }
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

const addRecord = async () => {
  // Save current record state if any
  if (records.value.length > 0) {
      saveCurrentRecordState()
  }

  // Fetch Entrustment Info if available
  let entrustmentData = {}
  const wtNum = formData.unifiedNumber || props.wtNum
  if (wtNum) {
      try {
          const res = await axios.get(`/api/jc-core-wt-info/by-wt-num?wtNum=${encodeURIComponent(wtNum)}`)
          if (res.data.success && res.data.data) {
              entrustmentData = res.data.data
          }
      } catch (e) {
          console.error('Failed to fetch entrustment info', e)
      }
  }

  const newRecord = {
    id: '', // New record has no ID yet
    entrustmentId: wtNum,
    constructionPart: entrustmentData.constructionPart || '',
    projectName: entrustmentData.projectName || '',
    testCategory: entrustmentData.testCategory || '',
    entrustingUnit: entrustmentData.clientUnit || '', // Map clientUnit to entrustingUnit
    // commissionDate: entrustmentData.commissionDate || '',
    dataJson: '{}',
    status: 0 // Draft
  }

  if (records.value.length > 0 && currentIndex.value >= 0 && currentIndex.value < records.value.length) {
    const current = records.value[currentIndex.value]
    const sourceJson = current.dataJson ? JSON.parse(current.dataJson) : {}
    const copiedJson = {}
    const fieldsToCopy = [
      'projectName',
      'commissionDate',
      'constructionPart',
      'testCategory',
      'entrustingUnit',
      'equipment',
      'designCompaction',
      'maxDryDensity',
      'optimumMoisture',
      'minDryDensity',
      'tester',
      'reviewer',
      'sampleNameStatus'
    ]
    fieldsToCopy.forEach(key => {
      const value = sourceJson[key]
      if (value !== undefined && value !== null && value !== '') {
        copiedJson[key] = value
      }
    })
    if (Object.keys(copiedJson).length > 0) {
      newRecord.dataJson = JSON.stringify(copiedJson)
    }
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

  const current = records.value[currentIndex.value]
  if (current.id) {
    try {
      const res = await axios.post('/api/nuclear-density/delete', { id: current.id })
      if (!res.data.success) {
        alert('删除失败: ' + res.data.message)
        return
      }
    } catch (e) {
      console.error('Delete error', e)
      alert('删除出错')
      return
    }
  }

  records.value.splice(currentIndex.value, 1)
  if (records.value.length === 0) {
    // If all deleted, add a new empty one
    await addRecord()
  } else {
    if (currentIndex.value >= records.value.length) {
      currentIndex.value = records.value.length - 1
    }
    mapRecordToFormData(records.value[currentIndex.value])
  }
}

const loadData = async (entrustmentId) => {
  if (entrustmentId) {
    try {
      const response = await axios.get('/api/nuclear-density/get-by-entrustment-id', {
        params: { entrustmentId: entrustmentId }
      })
      if (response.data.success) {
        records.value = response.data.data
        
        if (records.value.length === 0) {
          // Create a new default record
          await addRecord()
        } else {
          // Find record by ID if props.id is provided and it's not the entrustmentId
          let foundIndex = 0
          if (props.id && props.id !== entrustmentId) {
                const idx = records.value.findIndex(r => r.id === props.id)
                if (idx !== -1) foundIndex = idx
          }
          currentIndex.value = foundIndex
          mapRecordToFormData(records.value[foundIndex])
        }
      } else {
        console.error('Failed to load records:', response.data.message)
        // Fallback: Create new record
        await addRecord()
      }
    } catch (error) {
      console.error('Failed to load data:', error)
      await addRecord()
    }
  }
}

onMounted(() => {
    initDynamicFields()
    
    // Get entrustment ID (wtNum) from props or URL
    let wtNum = props.wtNum
    if (!wtNum) {
        const urlParams = new URLSearchParams(window.location.search)
        wtNum = urlParams.get('wtNum') || urlParams.get('id') // Fallback to id if wtNum missing
    }

    if (wtNum) {
        formData.entrustmentId = wtNum
        formData.unifiedNumber = wtNum
        loadData(wtNum)
    } else if (props.id) {
        // If only id is provided (legacy or direct link), try using it as wtNum
        formData.entrustmentId = props.id
        formData.unifiedNumber = props.id
        loadData(props.id)
    } else {
        // No ID, just init empty
    }
})

const saveData = async () => {
  try {
    const dataToSave = {
      id: formData.id,
      entrustmentId: formData.entrustmentId || props.id,
      dataJson: JSON.stringify(formData),
      reviewSignaturePhoto: formData.reviewerSignature,
      inspectSignaturePhoto: formData.testerSignature,
      tester: formData.tester,
      reviewer: formData.reviewer,
      
      // Map other fields required by entity
      nuclearModel: formData.equipment,
      // testDepth: ... 
    }
    
    const response = await axios.post('/api/nuclear-density/save', dataToSave)
    if (response.data.success) {
      alert('保存成功')
      if (response.data.data) {
           // Update current record in list
           const saved = response.data.data
           records.value[currentIndex.value] = saved
           mapRecordToFormData(saved)
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
      // if (formData.reviewer === currentName) {
      //   formData.reviewerSignature = imgSrc
      //   signed = true
      // }

      // Match Approver
      // if (formData.approver === currentName) {
      //   formData.approverSignature = imgSrc
      //   signed = true
      // }
      
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

// 返回列表
const goToList = () => {
  if (navigateTo) {
    navigateTo('NuclearDensityRecordList');
  }
}

const generatePdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/nuclear_density_record/generate'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}

const previewPdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/nuclear_density_record/preview'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}
</script>

<style scoped>

        .nuclearDensityRecord-container {
            font-family: 'SimSun', 'Songti SC', serif;
            width: 210mm;
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
            border: 2px solid black;
        }
        td {
            border: 1px solid black;
            padding: 5px;
            vertical-align: middle;
            text-align: center;
        }
        .label {
            font-weight: bold;
            white-space: nowrap;
        }
        .left-align {
            text-align: left;
            padding-left: 10px;
        }
        input[type="text"], textarea {
            width: 98%;
            border: none;
            outline: none;
            font-family: inherit;
            font-size: inherit;
            background-color: transparent;
            text-align: center;
        }
        .left-align input[type="text"] {
            text-align: left;
        }
        input[type="text"]:focus, textarea:focus {
            background-color: #f0f8ff;
        }
        textarea {
            resize: none;
            overflow: hidden;
            text-align: left;
        }
        .footer-info {
            display: flex;
            justify-content: space-between;
            margin-top: 30px;
            margin-bottom: 20px;
            font-size: 16px;
            font-weight: bold;
            padding: 0 50px;
        }
        .page-footer {
            margin-top: 10px;
            font-size: 14px;
            margin-bottom: 20px;
        }
        @media print {
            .nuclearDensityRecord-container {
                width: 100%;
                margin: 0;
                padding: 0;
            }
            input[type="text"], textarea {
                background-color: transparent !important;
            }
            .no-print {
                display: none;
            }
        }
    
</style>
