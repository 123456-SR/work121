<template>
  <div class="nuclearDensityRecord-container">


    <div class="no-print toolbar">
      <div class="toolbar-left">
        <button @click="goToList" class="link-button">&lt; 返回列表</button>
        <span v-if="!draftMode" class="record-nav">
          <button @click="prevRecord" :disabled="currentIndex <= 0" class="btn btn-secondary btn-small">上一条</button>
          <span class="record-nav-info">记录 {{ currentIndex + 1 }} / {{ totalRecords }}</span>
          <button @click="nextRecord" :disabled="currentIndex >= totalRecords - 1" class="btn btn-secondary btn-small">下一条</button>
          <button @click="addRecord" class="btn btn-primary btn-small">添加记录</button>
          <button @click="deleteRecord" class="btn btn-danger btn-small">删除当前记录</button>
        </span>
      </div>
      
      <div class="toolbar-right">
        <div v-if="formData.status !== undefined" class="status-text">
          状态:
          <span :style="{color: getStatusColor(formData.status)}" class="status-label">{{ getStatusText(formData.status) }}</span>
        </div>

        <!-- 只要不是草稿预览模式，就显示流程按钮；具体是否已保存由 submitWorkflow 再校验 -->
        <template v-if="!draftMode">
          <button
            v-if="formData.status === 0 || formData.status === 2 || formData.status === 4"
            @click="submitWorkflow('SUBMIT')"
            class="btn btn-primary btn-small"
          >
            提交审核
          </button>
          <button
            v-if="formData.status === 1"
            @click="submitWorkflow('AUDIT_PASS')"
            class="btn btn-primary btn-small"
          >
            审核通过
          </button>
          <button
            v-if="formData.status === 1"
            @click="submitWorkflow('REJECT')"
            class="btn btn-danger btn-small"
          >
            打回
          </button>
        </template>

        <button @click="saveData" class="btn btn-secondary btn-small">保存</button>
        <button v-if="!draftMode" @click="handleSign" class="btn btn-secondary btn-small">签字</button>
        <button v-if="!draftMode" @click="printDocument" class="btn btn-secondary btn-small">打印此单</button>
        <button v-if="!draftMode" @click="generatePdf" class="btn btn-secondary btn-small">下载PDF</button>
        <button v-if="!draftMode" @click="previewPdf" class="btn btn-secondary btn-small">预览PDF</button>
      </div>
    </div>

    <form id="pdfForm" ref="pdfForm" method="post">
    <!-- 电子签名隐藏字段 -->
    <input type="hidden" v-model="formData.testerSignature" name="testerSignature">
    <input type="hidden" v-model="formData.reviewerSignature" name="reviewerSignature">
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
          审核：<span style="display: inline-block; width: 100px; border-bottom: 1px solid black; height: 1em;"></span>
          <div v-if="formData.reviewerSignature" style="position: absolute; top: -20px; left: 40px; pointer-events: none;">
            <img :src="formData.reviewerSignature" style="width: 80px; height: 40px; mix-blend-mode: multiply;" />
          </div>
        </span>
        <span style="position: relative;">
          检测：<span style="display: inline-block; width: 100px; border-bottom: 1px solid black; height: 1em;"></span>
          <div v-if="formData.testerSignature" style="position: absolute; top: -20px; left: 40px; pointer-events: none;">
            <img :src="formData.testerSignature" style="width: 80px; height: 40px; mix-blend-mode: multiply;" />
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
  recordTester: '',
  recordReviewer: '',
  filler: '',
  // approverSignature: '',
  reviewerSignature: '',
  testerSignature: '',
  status: 0
})

const getStatusText = (status) => {
    const s = parseInt(status)
    switch(s) {
        // 统一状态名称
        case 0: return '草稿'
        case 1: return '已提交待审核'
        case 2: return '已打回'
        case 3: return '待签字'
        case 4: return '已签字待提交'
        case 5: return '审核通过'
        // 报告表状态 (10-15)
        case 10: return '草稿'
        case 11: return '已提交待审核'
        case 12: return '已打回'
        case 13: return '待签字'
        case 14: return '已签字待提交'
        case 15: return '审核通过待批准'
        // 结果表状态 (20-25)
        case 20: return '草稿'
        case 21: return '已提交待审核'
        case 22: return '已打回'
        case 23: return '待签字'
        case 24: return '已签字待提交'
        case 25: return '审核通过待批准'
        default: return '未知'
    }
}

const getStatusColor = (status) => {
    const s = parseInt(status)
    switch(s) {
        // 记录表状态 (0-5)
        case 0: return '#6c757d' // secondary
        case 1: return '#007bff' // primary
        case 2: return '#dc3545' // danger
        case 3: return '#ffc107' // warning
        case 4: return '#17a2b8' // info
        case 5: return '#28a745' // success
        // 报告表状态 (10-15)
        case 10: return '#6c757d' // secondary
        case 11: return '#007bff' // primary
        case 12: return '#dc3545' // danger
        case 13: return '#ffc107' // warning
        case 14: return '#17a2b8' // info
        case 15: return '#28a745' // success
        // 结果表状态 (20-25)
        case 20: return '#6c757d' // secondary
        case 21: return '#007bff' // primary
        case 22: return '#dc3545' // danger
        case 23: return '#ffc107' // warning
        case 24: return '#17a2b8' // info
        case 25: return '#28a745' // success
        default: return '#6c757d'
    }
}

const submitWorkflow = async (action) => {
    console.log('submitWorkflow called with action:', action)
    console.log('formData.id:', formData.id)
    console.log('formData.tester:', formData.tester)
    console.log('formData.reviewer:', formData.reviewer)
    console.log('formData.status:', formData.status)
    
    if (!formData.id) {
        alert('请先保存记录')
        return
    }
    
    // 先保存当前记录状态，确保所有字段都被保存
    saveCurrentRecordState()
    
    // 保存到数据库，确保字段被持久化
    try {
        const formDataCopy = { ...formData }
        if (formDataCopy.tester) delete formDataCopy.tester
        if (formDataCopy.reviewer) delete formDataCopy.reviewer

        const dataToSave = {
            id: formData.id,
            entrustmentId: formData.entrustmentId || props.id,
            status: formData.status,
            dataJson: JSON.stringify(formDataCopy),
            reviewSignaturePhoto: formData.reviewerSignature,
            inspectSignaturePhoto: formData.testerSignature,
            recordTester: formData.recordTester,
            recordReviewer: formData.recordReviewer,
            tester: formData.recordTester,
            reviewer: formData.recordReviewer,
            filler: formData.filler,
            nuclearModel: formData.equipment,
            clientUnit: formData.entrustingUnit,
            projectName: formData.projectName,
            commissionDate: formData.commissionDate ? new Date(formData.commissionDate).toISOString().split('T')[0] : '',
            constructionPart: formData.constructionPart,
            testCategory: formData.testCategory
        }

        await axios.post('/api/nuclear-density/save', dataToSave)
    } catch (e) {
        console.error('Save before workflow error', e)
    }
    
    const user = JSON.parse(localStorage.getItem('userInfo'))
    console.log('Current user:', user)
    if (!user || !user.username) {
        alert('请先登录')
        return
    }

    let signatureData = null
    
    if (action === 'SUBMIT') {
        // Role check: Only recordTester can submit
        if (formData.recordTester && user.username !== formData.recordTester && user.fullName !== formData.recordTester) {
            alert('您不是该单据的记录检测人 (' + formData.recordTester + ')，无权提交')
            return
        }

        if (!formData.testerSignature) {
            alert('请先进行检测人签字')
            return
        }
        signatureData = formData.testerSignature
    } else if (action === 'AUDIT_PASS') {
        // Role check: Only recordReviewer can audit
        if (formData.recordReviewer && user.username !== formData.recordReviewer && user.fullName !== formData.recordReviewer) {
            alert('您不是该单据的记录校核人 (' + formData.recordReviewer + ')，无权操作')
            return
        }

        // Auto fetch signature if missing
        if (!formData.reviewerSignature) {
            try {
                const sigRes = await axios.post('/api/signature/get', { userAccount: user.username })
                if (sigRes.data.success && sigRes.data.data && sigRes.data.data.signatureBlob) {
                     formData.reviewerSignature = `data:image/png;base64,${sigRes.data.data.signatureBlob}`
                     if (!formData.recordReviewer) {
                        formData.recordReviewer = user.fullName || user.username
                     }
                } else {
                     alert('未找到您的电子签名，无法自动签名')
                     return
                }
            } catch (e) {
                console.error('Auto sign error', e)
                alert('自动签名失败')
                return
            }
        }
        signatureData = formData.reviewerSignature
    } else if (action === 'REJECT') {
        // Role check: Only recordReviewer can reject
        if (formData.recordReviewer && user.username !== formData.recordReviewer && user.fullName !== formData.recordReviewer) {
            alert('您不是该单据的记录校核人 (' + formData.recordReviewer + ')，无权操作')
            return
        }
    } else if (action === 'SIGN_TEST') {
        // Role check: Only recordTester can sign
        if (formData.recordTester && user.username !== formData.recordTester && user.fullName !== formData.recordTester) {
            alert('您不是该单据的记录检测人 (' + formData.recordTester + ')，无权签字')
            return
        }

        if (!formData.testerSignature) {
            alert('请先进行检测人签字')
            return
        }
        signatureData = formData.testerSignature
    }

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
            // 重新按委托号加载一次，刷新最新的 STATUS（例如从 0 -> 1）
            const reloadId = formData.entrustmentId || props.wtNum || formData.unifiedNumber
            if (reloadId) {
              loadData(reloadId)
            }
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
  // Basic fields
  formData.id = record.id || ''
  formData.entrustmentId = record.entrustmentId || props.id
  // 状态统一转成数字，避免后端返回字符串导致严格等于判断失效（影响按钮显示）
  formData.status = record.status !== undefined ? Number(record.status) : 0
  
  // Signature photos
  formData.reviewerSignature = record.reviewSignaturePhoto || ''
  formData.testerSignature = record.inspectSignaturePhoto || ''
  
  // Person names
  formData.reviewer = record.reviewer || record.REVIEWER || record.recordReviewer || record.RECORD_REVIEWER || ''
  formData.tester = record.tester || record.TESTER || record.recordTester || record.RECORD_TESTER || ''

  // Map Roles
  
  // Filler - Priority: record.filler
  formData.filler = record.filler || ''
  
  // Record Tester - Priority: record.recordTester -> record.tester
  formData.recordTester = record.recordTester || record.tester || ''
  
  // Record Reviewer - Priority: record.recordReviewer -> record.reviewer
  formData.recordReviewer = record.recordReviewer || record.reviewer || ''

  // Parse JSON data if available first, so we have the most up-to-date values
  let jsonData = {}
  if (record.dataJson) {
    try {
      jsonData = JSON.parse(record.dataJson)
    } catch (e) {
      console.error('JSON parse error', e)
    }
  }

  // Map fields with priority: JSON data > record fields
  formData.entrustingUnit = jsonData.entrustingUnit || record.clientUnit || ''
  formData.unifiedNumber = jsonData.unifiedNumber || record.wtNum || record.entrustmentId || ''
  formData.projectName = jsonData.projectName || record.projectName || ''
  // 确保委托日期只显示年月日部分
  if (jsonData.commissionDate) {
    formData.commissionDate = new Date(jsonData.commissionDate).toISOString().split('T')[0]
  } else if (record.commissionDate) {
    formData.commissionDate = new Date(record.commissionDate).toISOString().split('T')[0]
  } else if (formData.commissionDate) {
    formData.commissionDate = new Date(formData.commissionDate).toISOString().split('T')[0]
  }
  formData.constructionPart = jsonData.constructionPart || record.constructionPart || ''
  formData.testCategory = jsonData.testCategory || record.testCategory || ''
  formData.equipment = jsonData.equipment || record.equipment || ''
  formData.sampleNameStatus = jsonData.sampleNameStatus || record.sampleName || ''
  formData.standard = jsonData.standard || record.testBasis || ''
  formData.designCompaction = jsonData.designCompaction || ''
  formData.maxDryDensity = jsonData.maxDryDensity || ''
  formData.optimumMoisture = jsonData.optimumMoisture || ''
  formData.minDryDensity = jsonData.minDryDensity || ''

  // Map dynamic fields
  for (let i = 0; i < 20; i++) {
    formData[`sampleId_${i}`] = jsonData[`sampleId_${i}`] || ''
    formData[`location_${i}`] = jsonData[`location_${i}`] || ''
    formData[`date_${i}`] = jsonData[`date_${i}`] || ''
    formData[`wetDensity_${i}`] = jsonData[`wetDensity_${i}`] || ''
    formData[`dryDensity_${i}`] = jsonData[`dryDensity_${i}`] || ''
    formData[`moisture_${i}`] = jsonData[`moisture_${i}`] || ''
    formData[`compaction_${i}`] = jsonData[`compaction_${i}`] || ''
    formData[`remarks_${i}`] = jsonData[`remarks_${i}`] || ''
  }
}

const saveCurrentRecordState = () => {
  if (records.value.length > 0 && currentIndex.value >= 0 && currentIndex.value < records.value.length) {
     const current = records.value[currentIndex.value]
     // Create a copy of formData and remove legacy fields before saving
     const formDataCopy = { ...formData }
     if (formDataCopy.tester) delete formDataCopy.tester
     if (formDataCopy.reviewer) delete formDataCopy.reviewer
     // Update current record object with latest formData values
     current.dataJson = JSON.stringify(formDataCopy)
     current.reviewer = formData.reviewer
     current.tester = formData.tester
     // Update role fields
     current.recordTester = formData.recordTester
     current.recordReviewer = formData.recordReviewer
     current.filler = formData.filler
     
     current.reviewSignaturePhoto = formData.reviewerSignature
     current.inspectSignaturePhoto = formData.testerSignature
     // Also update flattened fields if needed for list view or other logic
     current.clientUnit = formData.entrustingUnit
     current.commissionDate = formData.commissionDate
     current.constructionPart = formData.constructionPart
     current.testCategory = formData.testCategory
     current.projectName = formData.projectName
     current.equipment = formData.equipment
     current.sampleName = formData.sampleNameStatus
     current.testBasis = formData.standard
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
  let isEntrustmentApproved = false
  const wtNum = formData.unifiedNumber || props.wtNum
  if (wtNum) {
      try {
          const res = await axios.get(`/api/jc-core-wt-info/by-wt-num?wtNum=${encodeURIComponent(wtNum)}`)
          if (res.data.success && res.data.data) {
              entrustmentData = res.data.data
              // 检查委托单状态是否为审核通过（状态值为5）
              isEntrustmentApproved = entrustmentData.status === 5
          }
      } catch (e) {
          console.error('Failed to fetch entrustment info', e)
      }
  }

  const directory = JSON.parse(localStorage.getItem('currentDirectory') || '{}')

  const newRecord = {
    id: '', // New record has no ID yet
    entrustmentId: wtNum,
    constructionPart: isEntrustmentApproved ? (entrustmentData.constructionPart || '') : '',
    projectName: isEntrustmentApproved ? (entrustmentData.projectName || '') : '',
    testCategory: isEntrustmentApproved ? (entrustmentData.testCategory || '') : '',
    entrustingUnit: isEntrustmentApproved ? (entrustmentData.clientUnit || '') : '', // Map clientUnit to entrustingUnit
    // commissionDate: entrustmentData.commissionDate || '',
    // Initialize Roles
    recordTester: isEntrustmentApproved ? (entrustmentData.testerName || entrustmentData.tester || directory.jcTester || '') : (directory.jcTester || ''),
    recordReviewer: isEntrustmentApproved ? (entrustmentData.reviewerName || entrustmentData.reviewer || directory.jcReviewer || '') : (directory.jcReviewer || ''),
    filler: directory.jcFiller || '',
    
    dataJson: '{}',
    status: 0 // Draft
  }
  
  if (!isEntrustmentApproved && Object.keys(entrustmentData).length > 0) {
    console.log('委托单状态未审核通过，不自动填充数据')
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
          // Find record by ID: first try current formData.id, then props.id
          let foundIndex = 0
          // First try to find the current record by its ID
          if (formData.id) {
                const idx = records.value.findIndex(r => r.id === formData.id)
                if (idx !== -1) foundIndex = idx
          }
          // If not found, try props.id (if it's not the entrustmentId)
          else if (props.id && props.id !== entrustmentId) {
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
    // 如果状态是草稿(0)，保存后改为待签字(3)
    if (formData.status === 0) {
      formData.status = 3
    }
    // 如果状态是已签字待提交(4)，保持状态不变
    else if (formData.status === 4) {
      formData.status = 4
    }
    
    // Create a copy of formData and remove legacy fields before saving
    const formDataCopy = { ...formData }
    if (formDataCopy.tester) delete formDataCopy.tester
    if (formDataCopy.reviewer) delete formDataCopy.reviewer

    const dataToSave = {
      id: formData.id,
      entrustmentId: formData.entrustmentId || props.id,
      status: formData.status, // 传递状态字段给后端
      dataJson: JSON.stringify(formDataCopy),
      reviewSignaturePhoto: formData.reviewerSignature,
      inspectSignaturePhoto: formData.testerSignature,
      // Role fields
      recordTester: formData.recordTester,
      recordReviewer: formData.recordReviewer,
      tester: formData.recordTester, // Sync legacy
      reviewer: formData.recordReviewer, // Sync legacy
      filler: formData.filler,
      
      // Map other fields required by entity
      nuclearModel: formData.equipment,
      clientUnit: formData.entrustingUnit,
      commissionDate: formData.commissionDate ? new Date(formData.commissionDate).toISOString().split('T')[0] : '',
      constructionPart: formData.constructionPart,
      testCategory: formData.testCategory,
      // testDepth: ... 
    }
    
    const response = await axios.post('/api/nuclear-density/save', dataToSave)
    if (response.data.success) {
      alert('保存成功，状态已更新为待签字')
      if (response.data.data) {
           // Update current record in list
           const saved = response.data.data
           records.value[currentIndex.value] = saved
           mapRecordToFormData(saved)
      }
      // 保存成功后重新加载数据，确保显示最新状态
      if (formData.entrustmentId) {
        await loadData(formData.entrustmentId)
      }
      // 保存成功后留在当前页面，不返回列表
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
  if (!user) {
    alert('请先登录')
    return
  }

  // 获取用户账号，支持多种字段名
  const currentAccount = user.username || user.userAccount || user.userName
  const currentName = user.fullName || user.nickName || currentAccount
  
  if (!currentAccount) {
    alert('无法获取用户账号信息')
    return
  }

  try {
    const response = await axios.post('/api/signature/get', {
      userAccount: currentAccount
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

      // Match Tester
      if (!formData.recordTester || formData.recordTester === currentName || formData.recordTester === currentAccount) {
        if (!formData.recordTester) {
            formData.recordTester = currentName
        }
        formData.testerSignature = imgSrc
        signed = true
      }

      if (signed) {
        // 直接更新状态为已签字待提交(4)
        formData.status = 4
        // 保存签名和状态到数据库
        await saveData()
        // 强制更新状态，确保不会被服务器返回的数据覆盖
        formData.status = 4
        alert('签名成功并已保存，状态已更新为已签字待提交')
      } else {
        alert(`当前用户(${currentName})与表单中的检测人(${formData.recordTester})不匹配，无法签名`)
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

        .status-text {
            font-size: 14px;
            font-weight: 500;
            color: #666;
            margin-right: 8px;
        }

        .status-label {
            margin-left: 4px;
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
