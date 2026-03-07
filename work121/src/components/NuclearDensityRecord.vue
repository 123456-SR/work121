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
            v-if="formData.status === 0 || formData.status === 2"
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
        <span>统一编号：<input type="text" v-model="formData.unifiedNumber"   name="unifiedNumber" style="width: 150px; border-bottom: 1px solid black;" disabled></span>
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
            <!-- 需求：检测类别固定展示为“委托”，不再自动填充/保存后台数据 -->
            <td colspan="2">委托</td>
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

// 统一日期格式化为 YYYY-MM-DD，避免显示完整时间串
const formatDate = (dateVal) => {
  if (!dateVal) return ''
  const d = new Date(dateVal)
  if (isNaN(d.getTime())) return ''
  const year = d.getFullYear()
  const month = ('0' + (d.getMonth() + 1)).slice(-2)
  const day = ('0' + d.getDate()).slice(-2)
  return `${year}-${month}-${day}`
}

// 过滤误用为流程状态码的“样品状态”（例如 0-7 这些数字），只保留真正的文字描述
const cleanSampleStatus = (val) => {
  if (val === null || val === undefined) return ''
  const s = String(val).trim()
  // 常见流程状态码：0-7、10-27，都不认为是样品状态
  if (/^\d+$/.test(s)) return ''
  return s
}

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
    
    const user = JSON.parse(localStorage.getItem('userInfo'))
    console.log('Current user:', user)
    if (!user || !user.username) {
        alert('请先登录')
        return
    }

    let signatureData = null
    
    if (action === 'SUBMIT') {
        // Role check: Only recordTester can submit
        if (formData.recordTester && user.username !== formData.recordTester && user.userName !== formData.recordTester) {
            alert('您不是该单据的记录检测人 (' + formData.recordTester + ')，无权提交')
            return
        }

        // Auto fetch signature if missing
        if (!formData.testerSignature) {
            try {
                const sigRes = await axios.post('/api/signature/get', { userAccount: user.username })
                if (sigRes.data.success && sigRes.data.data && sigRes.data.data.signatureBlob) {
                     formData.testerSignature = `data:image/png;base64,${sigRes.data.data.signatureBlob}`
                     if (!formData.recordTester) {
                        formData.recordTester = user.userName || user.username
                     }
                     // 保存签名到数据库
                     await saveData()
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
                     // 保存签名到数据库
                     await saveData()
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

  // Map fields from BusinessEntity/Entrustment (Always map these first as defaults)
  if (record.clientUnit) formData.entrustingUnit = record.clientUnit
  if (record.wtNum) formData.unifiedNumber = record.wtNum
  if (record.projectName) formData.projectName = record.projectName
  if (record.commissionDate) formData.commissionDate = formatDate(record.commissionDate)
  if (record.constructionPart) formData.constructionPart = record.constructionPart
  if (record.equipment) formData.equipment = record.equipment
  // 样品名称及状态：优先用实体字段拼接；样品状态会先过滤掉纯数字的流程状态码（0-7、10-27 等）
  if (record.sampleName || record.sampleStatus) {
    const name = record.sampleName ? String(record.sampleName).trim() : ''
    const status = cleanSampleStatus(record.sampleStatus)
    formData.sampleNameStatus = name && status ? `${name}，${status}` : (name || status || '')
  }
  if (record.testBasis) formData.standard = record.testBasis
  if (record.entrustmentId) formData.unifiedNumber = record.entrustmentId // Ensure entrustmentId also maps

  // Parse JSON data if available
  if (record.dataJson) {
    try {
      const parsed = JSON.parse(record.dataJson)
      // Merge parsed data into formData
      // 注意：永远不要让 JSON 里的 status 覆盖数据库里的 status
      Object.keys(parsed).forEach(key => {
        if (key === 'status') return
        formData[key] = parsed[key]
      })

      // Ensure specific fields are set if they exist in parsed
      if (parsed.entrustingUnit) formData.entrustingUnit = parsed.entrustingUnit
      if (parsed.unifiedNumber) formData.unifiedNumber = parsed.unifiedNumber
      if (parsed.projectName) formData.projectName = parsed.projectName
      if (parsed.commissionDate) formData.commissionDate = formatDate(parsed.commissionDate)
      if (parsed.constructionPart) formData.constructionPart = parsed.constructionPart
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
    if (record.commissionDate) formData.commissionDate = formatDate(record.commissionDate)
    if (record.constructionPart) formData.constructionPart = record.constructionPart
    if (record.sampleName || record.sampleStatus) {
      const name = record.sampleName ? String(record.sampleName).trim() : ''
      const status = cleanSampleStatus(record.sampleStatus)
      formData.sampleNameStatus = name && status ? `${name}，${status}` : (name || status || '')
    }
  }
}

const saveCurrentRecordState = () => {
  if (records.value.length > 0 && currentIndex.value >= 0 && currentIndex.value < records.value.length) {
     const current = records.value[currentIndex.value]
     // Update current record object with latest formData values
     current.dataJson = JSON.stringify(formData)
     current.reviewer = formData.reviewer
     current.tester = formData.tester
     // Update role fields
     current.recordTester = formData.recordTester
     current.recordReviewer = formData.recordReviewer
     current.filler = formData.filler
     
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
  let isEntrustmentApproved = false
  const wtNum = formData.unifiedNumber || props.wtNum
  if (wtNum) {
      try {
          const res = await axios.get(`/api/jc-core-wt-info/by-wt-num?wtNum=${encodeURIComponent(wtNum)}`)
          if (res.data.success && res.data.data) {
              entrustmentData = res.data.data
              // 检查委托单状态是否为审核通过（状态值为5，后端可能返回字符串或数字，这里统一转成数字判断）
              const statusNum = Number(entrustmentData.status)
              isEntrustmentApproved = !isNaN(statusNum) && statusNum === 5
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
    // 为了兼容 mapRecordToFormData，这里同时带上 clientUnit 和 commissionDate 字段
    entrustingUnit: isEntrustmentApproved ? (entrustmentData.clientUnit || '') : '', // 直接用于表单显示
    clientUnit: isEntrustmentApproved ? (entrustmentData.clientUnit || '') : '',      // 供 mapRecordToFormData 使用
    commissionDate: isEntrustmentApproved ? formatDate(entrustmentData.commissionDate || entrustmentData.clientDate || '') : '',
    // 样品名称及状态：从委托单 sampleName + sampleStatus 带到记录实体上，由 mapRecordToFormData 统一拼接
    sampleName: isEntrustmentApproved ? (entrustmentData.sampleName || '') : '',
    sampleStatus: isEntrustmentApproved ? cleanSampleStatus(entrustmentData.sampleStatus || '') : '',
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
    // 如果状态是草稿(0)，保存后改为待签字(3)
    if (formData.status === 0) {
      formData.status = 3
    }
    
    // Remove legacy fields from formData before saving
    if (formData.tester) delete formData.tester
    if (formData.reviewer) delete formData.reviewer

    const dataToSave = {
      id: formData.id,
      entrustmentId: formData.entrustmentId || props.id,
      status: formData.status, // 传递状态字段给后端
      dataJson: JSON.stringify(formData),
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

  // 直接使用后端返回的字段
  const currentAccount = user.username
  const currentName = user.userName
  
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
      let signType = ''

      // Match Tester (记录检测人)
      if (!formData.recordTester || formData.recordTester === currentName || formData.recordTester === currentAccount) {
        if (!formData.recordTester) {
            formData.recordTester = currentName
        }
        formData.testerSignature = imgSrc
        signed = true
        signType = '检测人'
      }
      
      // Match Reviewer (记录审核人) - 如果检测人已经签了，或者当前用户是审核人
      if (!signed && (!formData.recordReviewer || formData.recordReviewer === currentName || formData.recordReviewer === currentAccount)) {
        if (!formData.recordReviewer) {
            formData.recordReviewer = currentName
        }
        formData.reviewerSignature = imgSrc
        signed = true
        signType = '审核人'
      }

      if (signed) {
        // 如果两个人都签了，先更新状态为已签字待提交
        if (formData.testerSignature && formData.reviewerSignature) {
          formData.status = 4
        }
        // 保存签名到数据库
        await saveData()
        // 显示成功消息
        if (formData.status === 4) {
          alert('签名成功并已保存，检测人和审核人都已签字，状态已更新为已签字待提交')
        } else {
          alert(`签名成功并已保存，您以${signType}身份签字`)
        }
      } else {
        alert(`当前用户(${currentName}/${currentAccount})与表单中的检测人(${formData.recordTester})或审核人(${formData.recordReviewer})不匹配，无法签名`)
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
            flex-wrap: wrap;
            padding: 0 24px;
            box-sizing: border-box;
            width: 100%;
            max-width: 100%;
            overflow-x: hidden;
            background-color: var(--bg-card);
            border-radius: 8px;
            box-shadow: var(--shadow);
            padding: 16px 24px;
        }

        .toolbar-left {
            display: flex;
            align-items: center;
            gap: 16px;
            flex-wrap: wrap;
            margin-left: 0;
            flex: 1;
        }

        .toolbar-right {
            display: flex;
            align-items: center;
            gap: 12px;
            flex-wrap: wrap;
            flex: 1;
            justify-content: flex-end;
        }

        .link-button {
            background: #f8f9fa;
            border: 1px solid #dee2e6;
            border-radius: 4px;
            color: #3498db;
            cursor: pointer;
            font-size: 16px;
            padding: 8px 16px;
            transition: all 0.2s ease;
            display: inline-flex;
            align-items: center;
            gap: 5px;
            white-space: nowrap;
        }

        .link-button:hover {
            background: #e9ecef;
            border-color: #adb5bd;
            text-decoration: none;
        }

        .record-nav {
            display: inline-flex;
            align-items: center;
            gap: 10px;
            margin-left: 16px;
        }

        .record-nav-info {
            font-size: 15px;
            color: #666;
            white-space: nowrap;
        }

        .status-text {
            font-size: 15px;
            font-weight: 500;
            color: #666;
            white-space: nowrap;
            margin-right: 8px;
        }

        .status-label {
            margin-left: 6px;
        }

        .btn {
            padding: 8px 16px;
            border-radius: 4px;
            border: 1px solid transparent;
            font-size: 14px;
            cursor: pointer;
            background-color: #f5f7fa;
            color: #333;
            transition: all 0.2s;
            white-space: nowrap;
        }

        .btn-small {
            padding: 6px 12px;
            font-size: 13px;
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
            padding: 24px;
            background-color: var(--bg-card);
            border-radius: 8px;
            box-shadow: var(--shadow);
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
        input[type="text"], textarea, select {
            width: 98%;
            border: 1px solid #b3d9ff;
            border-radius: 4px;
            outline: none;
            font-family: inherit;
            font-size: inherit;
            background-color: transparent;
            text-align: center;
            padding: 2px 4px;
        }
        .left-align input[type="text"] {
            text-align: left;
        }
        input[type="text"]:focus, textarea:focus, select:focus {
            background-color: #f0f8ff;
            border-color: #3498db;
        }
        input[type="text"]:disabled:focus, textarea:disabled:focus, select:disabled:focus {
            background-color: transparent;
            border-color: #b3d9ff;
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
