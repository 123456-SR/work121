<template>
  <div class="nuclearDensityRecord-container">


    <div class="no-print toolbar">
      <div class="toolbar-left">
        <button @click="goToList" class="link-button">&lt; 返回列表</button>
        <span v-if="!draftMode" class="record-nav">
          <button @click="prevRecord" :disabled="totalRecords <= 0 || currentIndex <= 0" class="btn btn-secondary btn-small">上一条</button>
          <span class="record-nav-info">记录 {{ totalRecords > 0 ? currentIndex + 1 : 0 }} / {{ totalRecords }}</span>
          <button @click="nextRecord" :disabled="totalRecords <= 0 || currentIndex >= totalRecords - 1" class="btn btn-secondary btn-small">下一条</button>
          <button @click="addRecord" class="btn btn-primary btn-small">添加记录</button>
          <button @click="deleteRecord" :disabled="totalRecords <= 0" class="btn btn-danger btn-small">删除当前记录</button>
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
            v-if="parseInt(formData.status) === 0 || parseInt(formData.status) === 2"
            @click="submitWorkflow('SUBMIT')"
            class="btn btn-primary btn-small"
          >
            提交审核
          </button>
          <button
            v-if="parseInt(formData.status) === 1"
            @click="submitWorkflow('AUDIT_PASS')"
            class="btn btn-primary btn-small"
          >
            审核通过
          </button>
          <button
            v-if="parseInt(formData.status) === 1"
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
        <button v-if="!draftMode" @click="exportExcel" class="btn btn-secondary btn-small">导出数据</button>
        <button @click="openAnalysisModal" class="btn btn-secondary btn-small">数据分析</button>
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

    <!-- 数据分析模态窗口 -->
    <div v-if="showAnalysisModal" class="modal-overlay">
      <div class="modal-content">
        <h3>数据分析</h3>
        <div class="form-group">
          <label>数据范围：</label>
          <div class="range-inputs">
            <span>从</span>
            <input type="number" v-model="analysisRange.start" min="1" max="20" placeholder="起始" />
            <span>行至</span>
            <input type="number" v-model="analysisRange.end" min="1" max="20" placeholder="结束" />
            <span>行</span>
          </div>
        </div>
        <div class="analysis-results">
          <h4>分析结果：</h4>
          <div class="result-item">
            <span>湿密度：</span>
            <div class="range-inputs">
              <input type="number" v-model="analysisResults.wetDensityMin" placeholder="最小值" step="0.01" style="appearance: textfield; -moz-appearance: textfield;" />
              <span>至</span>
              <input type="number" v-model="analysisResults.wetDensityMax" placeholder="最大值" step="0.01" style="appearance: textfield; -moz-appearance: textfield;" />
              <span>g/cm³</span>
            </div>
          </div>
          <div class="result-item">
            <span>干密度：</span>
            <div class="range-inputs">
              <input type="number" v-model="analysisResults.dryDensityMin" placeholder="最小值" step="0.01" style="appearance: textfield; -moz-appearance: textfield;" />
              <span>至</span>
              <input type="number" v-model="analysisResults.dryDensityMax" placeholder="最大值" step="0.01" style="appearance: textfield; -moz-appearance: textfield;" />
              <span>g/cm³</span>
            </div>
          </div>
          <div class="result-item">
            <span>含水率：</span>
            <div class="range-inputs">
              <input type="number" v-model="analysisResults.moistureMin" placeholder="最小值" step="0.1" style="appearance: textfield; -moz-appearance: textfield;" />
              <span>至</span>
              <input type="number" v-model="analysisResults.moistureMax" placeholder="最大值" step="0.1" style="appearance: textfield; -moz-appearance: textfield;" />
              <span>%</span>
            </div>
          </div>
          <div class="result-item">
            <span>压实度：</span>
            <div class="range-inputs">
              <input type="number" v-model="analysisResults.compactionMin" placeholder="最小值" step="0.1" style="appearance: textfield; -moz-appearance: textfield;" />
              <span>至</span>
              <input type="number" v-model="analysisResults.compactionMax" placeholder="最大值" step="0.1" style="appearance: textfield; -moz-appearance: textfield;" />
              <span>%</span>
            </div>
          </div>
        </div>
        <div class="modal-actions">
          <button @click="autoAnalyzeAndFill" class="btn btn-primary btn-small">自动分析并填充</button>
          <button @click="closeAnalysisModal" class="btn btn-secondary btn-small">关闭</button>
        </div>
      </div>
    </div>

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

// 数据分析相关状态
const showAnalysisModal = ref(false)
const analysisRange = reactive({ start: '', end: '' })
const analysisResults = reactive({
  wetDensityMin: '',
  wetDensityMax: '',
  dryDensityMin: '',
  dryDensityMax: '',
  moistureMin: '',
  moistureMax: '',
  compactionMin: '',
  compactionMax: ''
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
  formData.status = record.status !== null && record.status !== undefined ? parseInt(record.status) || 0 : 0
  
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
    // Remove legacy fields from formData before saving
    if (formData.tester) delete formData.tester
    if (formData.reviewer) delete formData.reviewer

    // 准备保存的数据，移除status字段，避免将其保存到dataJson中
    const dataJsonObj = { ...formData }
    delete dataJsonObj.id
    delete dataJsonObj.status
    delete dataJsonObj.tester
    delete dataJsonObj.reviewer

    const dataToSave = {
      id: formData.id,
      entrustmentId: formData.entrustmentId || props.id,
      status: String(formData.status), // 确保status是字符串类型
      dataJson: JSON.stringify(dataJsonObj),
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

const exportExcel = async () => {
  try {
    const dataJsonObj = { ...formData }
    delete dataJsonObj.id
    delete dataJsonObj.status
    delete dataJsonObj.tester
    delete dataJsonObj.reviewer
    const response = await axios.post('/api/nuclear-density/export-excel', {
      id: formData.id,
      entrustmentId: formData.entrustmentId || props.id,
      data: dataJsonObj
    })
    if (response.data && response.data.success) {
      alert(`导出成功：${response.data.path}`)
    } else {
      alert('导出失败: ' + (response.data && response.data.message ? response.data.message : '未知错误'))
    }
  } catch (error) {
    console.error('Export error:', error)
    alert('导出失败')
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
        // 保存签名到数据库
        await saveData()
        // 显示成功消息
        alert(`签名成功并已保存，您以${signType}身份签字`)
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

const openBackendPdfPreview = (actionUrl) => {
  if (!pdfForm.value) return
  const container = pdfForm.value.closest('.nuclearDensityRecord-container')
  if (!container) return

  const escapeAttr = (v) => String(v ?? '')
    .replace(/&/g, '&amp;')
    .replace(/"/g, '&quot;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')

  const toBase64Utf8 = (text) => {
    const bytes = new TextEncoder().encode(text)
    let binary = ''
    for (let i = 0; i < bytes.length; i++) binary += String.fromCharCode(bytes[i])
    return btoa(binary)
  }

  const mmToPx = (mm) => mm * 96 / 25.4
  const pageWidthMm = 210
  const pageHeightMm = 297
  const marginMm = 6
  const availableWidthPx = mmToPx(pageWidthMm - marginMm * 2)
  const availableHeightPx = mmToPx(pageHeightMm - marginMm * 2)
  const measureNode = container.cloneNode(true)
  measureNode.classList.add('pdf-preview')
  measureNode.querySelectorAll('.no-print').forEach(el => el.remove())
  measureNode.style.position = 'fixed'
  measureNode.style.left = '-100000px'
  measureNode.style.top = '0'
  measureNode.style.visibility = 'hidden'
  measureNode.style.width = `${pageWidthMm}mm`
  measureNode.style.height = 'auto'
  measureNode.style.maxHeight = 'none'
  measureNode.style.overflow = 'visible'
  measureNode.style.maxWidth = '100%'
  measureNode.style.minWidth = '0'
  measureNode.style.margin = '0'
  measureNode.style.padding = '0'
  measureNode.style.boxSizing = 'border-box'
  document.body.appendChild(measureNode)
  const rect = measureNode.getBoundingClientRect()
  const contentWidthPx = Math.max(measureNode.scrollWidth || 0, rect.width || 0, 1)
  const contentHeightPx = Math.max(measureNode.scrollHeight || 0, rect.height || 0, 1)
  measureNode.remove()
  const pdfScale = Math.min(availableWidthPx / contentWidthPx, availableHeightPx / contentHeightPx)
  const scaledWidthPx = contentWidthPx * pdfScale
  const scaledHeightPx = contentHeightPx * pdfScale
  const pdfOffsetXPx = Math.max(0, (availableWidthPx - scaledWidthPx) / 2)
  const pdfOffsetYPx = Math.max(0, (availableHeightPx - scaledHeightPx) / 2)

  const buildHtmlSnapshotBase64 = () => {
    const clone = container.cloneNode(true)
    clone.classList.add('pdf-preview')
    clone.querySelectorAll('.no-print').forEach(el => el.remove())

    const originalFields = container.querySelectorAll('input, textarea, select')
    const clonedFields = clone.querySelectorAll('input, textarea, select')
    const len = Math.min(originalFields.length, clonedFields.length)

    for (let i = 0; i < len; i++) {
      const src = originalFields[i]
      const dst = clonedFields[i]
      const tag = dst.tagName.toLowerCase()

      if (tag === 'textarea') {
        dst.textContent = src.value || ''
        continue
      }

      if (tag === 'select') {
        const srcValue = src.value
        Array.from(dst.options).forEach(opt => {
          opt.selected = opt.value === srcValue
        })
        continue
      }

      const type = (dst.getAttribute('type') || '').toLowerCase()
      if (type === 'checkbox' || type === 'radio') {
        if (src.checked) dst.setAttribute('checked', '')
        else dst.removeAttribute('checked')
        continue
      }

      dst.setAttribute('value', src.value ?? '')
    }

    clone.querySelectorAll('input, textarea, select').forEach(el => {
      const tag = el.tagName.toLowerCase()
      const style = el.getAttribute('style') || ''
      const name = el.getAttribute('name') || ''

      if (tag === 'input') {
        const type = (el.getAttribute('type') || 'text').toLowerCase()
        if (type === 'hidden') {
          el.remove()
          return
        }

        if (type === 'checkbox' || type === 'radio') {
          const box = document.createElement('span')
          const checked = el.checked || el.hasAttribute('checked')
          box.className = checked ? 'pdf-box checked' : 'pdf-box'
          box.setAttribute('aria-hidden', 'true')
          box.setAttribute('style', style)
          el.replaceWith(box)
          return
        }

        const span = document.createElement('span')
        span.textContent = el.getAttribute('value') || el.value || ''
        span.setAttribute('data-name', name)
        span.setAttribute('style', `${style};display:inline-block;white-space:pre-wrap;`)
        el.replaceWith(span)
        return
      }

      if (tag === 'textarea') {
        const div = document.createElement('div')
        div.textContent = el.textContent || el.value || ''
        div.setAttribute('data-name', name)
        div.setAttribute('style', `${style};white-space:pre-wrap;`)
        el.replaceWith(div)
        return
      }

      if (tag === 'select') {
        const span = document.createElement('span')
        const selected = el.querySelector('option:checked')
        span.textContent = selected ? selected.textContent : (el.value || '')
        span.setAttribute('data-name', name)
        span.setAttribute('style', `${style};display:inline-block;white-space:pre-wrap;`)
        el.replaceWith(span)
      }
    })

    const styleNodes = Array.from(document.querySelectorAll('link[rel="stylesheet"], style'))
    const stylesHtml = styleNodes.map(n => n.outerHTML).join('\n')
    const html = `<!doctype html>
<html lang="zh-CN">
  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    ${stylesHtml}
    <style>
      @page { size: A4 portrait; margin: 0; }
      html, body { margin: 0; padding: 0; background: #fff; width: 210mm; height: 297mm; overflow: hidden; }
      .pdf-sheet { width: 210mm; height: 297mm; padding: 6mm; box-sizing: border-box; overflow: hidden; }
      .pdf-page { width: 198mm; height: 285mm; overflow: hidden; box-sizing: border-box; position: relative; }
      .pdf-content { position: absolute; left: 0; top: 0; transform-origin: top left; }
      .pdf-preview.nuclearDensityRecord-container { width: 198mm; height: 285mm; max-width: 198mm; min-width: 0; margin: 0; padding: 0; box-sizing: border-box; display: flex; flex-direction: column; }
      .pdf-preview { overflow: visible; }
      .pdf-preview * { page-break-inside: avoid; break-inside: avoid; }
      .pdf-preview [data-name] { width: auto !important; max-width: 100% !important; box-sizing: border-box; overflow-wrap: anywhere; word-break: break-all; white-space: pre-wrap; }
      .pdf-preview #pdfForm { flex: 1; min-height: 0; display: flex; flex-direction: column; }
      .pdf-preview #pdfForm > table { flex: 0 0 auto; height: auto; }
      .pdf-preview table { width: 100%; height: auto; }
      .pdf-preview #pdfForm .footer-info { margin-top: auto !important; margin-bottom: 0 !important; align-items: flex-end; }
      .pdf-preview .pdf-box {
        width: 13px;
        height: 13px;
        border: 1px solid #000;
        display: inline-block;
        position: relative;
        vertical-align: middle;
        margin-right: 6px;
        box-sizing: border-box;
      }
      .pdf-preview .pdf-box.checked::after {
        content: '';
        position: absolute;
        top: 50%;
        left: 50%;
        width: 4px;
        height: 8px;
        border: solid #000;
        border-width: 0 2px 2px 0;
        transform: translate(-50%, -65%) rotate(45deg);
      }
    </style>
  </head>
  <body><div class="pdf-sheet"><div class="pdf-page"><div class="pdf-content" style="width: ${contentWidthPx}px; height: ${contentHeightPx}px; transform: translate(${pdfOffsetXPx}px, ${pdfOffsetYPx}px) scale(${pdfScale});">${clone.outerHTML}</div></div></div></body>
</html>`
    return toBase64Utf8(html)
  }

  const fields = Array.from(pdfForm.value.querySelectorAll('input, textarea, select'))
  const snapshotBase64 = buildHtmlSnapshotBase64()
  const inputsHtml = fields.map((el) => {
    const name = el.getAttribute('name')
    if (!name) return ''

    if (el.tagName.toLowerCase() === 'select') {
      return `<input type="hidden" name="${escapeAttr(name)}" value="${escapeAttr(el.value)}" />`
    }

    if (el.tagName.toLowerCase() === 'textarea') {
      return `<input type="hidden" name="${escapeAttr(name)}" value="${escapeAttr(el.value)}" />`
    }

    const type = (el.getAttribute('type') || '').toLowerCase()
    if (type === 'file' || type === 'button' || type === 'submit' || type === 'reset') return ''

    if (type === 'checkbox' || type === 'radio') {
      if (!el.checked) return ''
      return `<input type="hidden" name="${escapeAttr(name)}" value="${escapeAttr(el.value || 'on')}" />`
    }

    return `<input type="hidden" name="${escapeAttr(name)}" value="${escapeAttr(el.value)}" />`
  }).join('\n') + `\n<input type="hidden" name="__pdf_html_base64" value="${escapeAttr(snapshotBase64)}" />\n`

  const html = `<!doctype html>
<html lang="zh-CN">
  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>PDF预览</title>
    <style>
      html, body { height: 100%; margin: 0; }
      body { padding: 28px 60px; box-sizing: border-box; background: #f2f2f2; }
      .frame-shell {
        height: calc(100vh - 56px);
        background: #fff;
        border-radius: 8px;
        box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
        overflow: hidden;
      }
      iframe { width: 100%; height: 100%; border: 0; background: #fff; }
    </style>
  </head>
  <body onload="var f=document.getElementById('pdfPostForm'); if (f) f.submit();">
    <div class="frame-shell">
      <iframe name="pdfFrame" title="PDF预览"></iframe>
    </div>
    <form id="pdfPostForm" method="post" action="${escapeAttr(actionUrl)}" target="pdfFrame">
      ${inputsHtml}
    </form>
  </body>
</html>`
  const w = window.open('', '_blank')
  if (!w) return
  w.document.open()
  w.document.write(html)
  w.document.close()
}

const generatePdf = () => {
  if (pdfForm.value) {
    openBackendPdfPreview('/api/pdf/nuclear_density_record/generate')
  }
}

const previewPdf = () => {
  if (pdfForm.value) {
    openBackendPdfPreview('/api/pdf/nuclear_density_record/preview')
  }
}

// 数据分析相关方法
const openAnalysisModal = () => {
  showAnalysisModal.value = true
  // 重置分析结果
  Object.keys(analysisResults).forEach(key => {
    analysisResults[key] = ''
  })
  analysisRange.start = ''
  analysisRange.end = ''
}

const closeAnalysisModal = () => {
  showAnalysisModal.value = false
}

// 生成指定范围内的随机数
const getRandomInRange = (min, max, decimalPlaces) => {
  if (!min || !max) return null
  const minNum = parseFloat(min)
  const maxNum = parseFloat(max)
  if (isNaN(minNum) || isNaN(maxNum)) return null
  const random = Math.random() * (maxNum - minNum) + minNum
  return random.toFixed(decimalPlaces)
}

const autoAnalyzeAndFill = () => {
  // 将用户输入的行号（1-20）转换为数组索引（0-19）
  const start = (parseInt(analysisRange.start) || 1) - 1
  const end = (parseInt(analysisRange.end) || 20) - 1
  
  // 确保范围在有效范围内
  const validStart = Math.max(0, start)
  const validEnd = Math.min(19, end)
  
  if (validStart > validEnd) {
    alert('起始行不能大于结束行')
    return
  }
  
  // 收集数据
  const wetDensities = []
  const dryDensities = []
  const moistures = []
  const compactions = []
  
  for (let i = validStart; i <= validEnd; i++) {
    const wetDensity = parseFloat(formData[`wetDensity_${i}`])
    const dryDensity = parseFloat(formData[`dryDensity_${i}`])
    const moisture = parseFloat(formData[`moisture_${i}`])
    const compaction = parseFloat(formData[`compaction_${i}`])
    
    if (!isNaN(wetDensity)) wetDensities.push(wetDensity)
    if (!isNaN(dryDensity)) dryDensities.push(dryDensity)
    if (!isNaN(moisture)) moistures.push(moisture)
    if (!isNaN(compaction)) compactions.push(compaction)
  }
  
  // 计算范围（如果用户没有手动输入）
  if (!analysisResults.wetDensityMin || !analysisResults.wetDensityMax) {
    if (wetDensities.length > 0) {
      const min = Math.min(...wetDensities)
      const max = Math.max(...wetDensities)
      analysisResults.wetDensityMin = min.toFixed(2)
      analysisResults.wetDensityMax = max.toFixed(2)
    }
  }
  
  if (!analysisResults.dryDensityMin || !analysisResults.dryDensityMax) {
    // 如果最大干密度和最小干密度已经有值，使用它们作为范围
    if (formData.maxDryDensity && formData.minDryDensity) {
      analysisResults.dryDensityMin = formData.minDryDensity
      analysisResults.dryDensityMax = formData.maxDryDensity
    } else if (dryDensities.length > 0) {
      const min = Math.min(...dryDensities)
      const max = Math.max(...dryDensities)
      analysisResults.dryDensityMin = min.toFixed(2)
      analysisResults.dryDensityMax = max.toFixed(2)
    }
  }
  
  if (!analysisResults.moistureMin || !analysisResults.moistureMax) {
    // 如果最优含水率已经有值，使用它作为含水率的最大值
    if (formData.optimumMoisture) {
      // 含水率最小值可以设置为最优含水率的0.8倍左右，作为合理范围
      const optimumMoisture = parseFloat(formData.optimumMoisture)
      if (!isNaN(optimumMoisture)) {
        analysisResults.moistureMin = (optimumMoisture * 0.8).toFixed(1)
        analysisResults.moistureMax = formData.optimumMoisture
      }
    } else if (moistures.length > 0) {
      const min = Math.min(...moistures)
      const max = Math.max(...moistures)
      analysisResults.moistureMin = min.toFixed(1)
      analysisResults.moistureMax = max.toFixed(1)
    }
  }
  
  if (!analysisResults.compactionMin || !analysisResults.compactionMax) {
    if (compactions.length > 0) {
      const min = Math.min(...compactions)
      const max = Math.max(...compactions)
      analysisResults.compactionMin = min.toFixed(1)
      analysisResults.compactionMax = max.toFixed(1)
    }
  }
  
  // 收集实际的干密度、含水率和压实度数据
  const actualDryDensities = []
  const actualMoistures = []
  const actualCompactions = []
  
  for (let i = validStart; i <= validEnd; i++) {
    const dryDensity = parseFloat(formData[`dryDensity_${i}`])
    const moisture = parseFloat(formData[`moisture_${i}`])
    const compaction = parseFloat(formData[`compaction_${i}`])
    
    if (!isNaN(dryDensity)) actualDryDensities.push(dryDensity)
    if (!isNaN(moisture)) actualMoistures.push(moisture)
    if (!isNaN(compaction)) actualCompactions.push(compaction)
  }
  
  // 计算实际数据的范围
  let actualDryDensityMin = null
  let actualDryDensityMax = null
  let actualMoistureMin = null
  let actualMoistureMax = null
  let actualCompactionMin = null
  let actualCompactionMax = null
  
  if (actualDryDensities.length > 0) {
    actualDryDensityMin = Math.min(...actualDryDensities)
    actualDryDensityMax = Math.max(...actualDryDensities)
  }
  
  if (actualMoistures.length > 0) {
    actualMoistureMin = Math.min(...actualMoistures)
    actualMoistureMax = Math.max(...actualMoistures)
  }
  
  if (actualCompactions.length > 0) {
    actualCompactionMin = Math.min(...actualCompactions)
    actualCompactionMax = Math.max(...actualCompactions)
  }
  
  // 填充设计参数（基于实际数据）
  // 最大干密度
  if (actualDryDensityMax) {
    formData.maxDryDensity = actualDryDensityMax.toFixed(2)
  } else if (!formData.maxDryDensity && analysisResults.dryDensityMax) {
    formData.maxDryDensity = analysisResults.dryDensityMax
  }
  
  // 最小干密度
  if (actualDryDensityMin) {
    formData.minDryDensity = actualDryDensityMin.toFixed(2)
  } else if (!formData.minDryDensity && analysisResults.dryDensityMin) {
    formData.minDryDensity = analysisResults.dryDensityMin
  }
  
  // 最优含水率
  if (actualMoistureMin && actualMoistureMax) {
    const avgMoisture = (actualMoistureMin + actualMoistureMax) / 2
    formData.optimumMoisture = avgMoisture.toFixed(1)
  } else if (actualMoistureMax) {
    formData.optimumMoisture = actualMoistureMax.toFixed(1)
  } else if (actualMoistureMin) {
    formData.optimumMoisture = actualMoistureMin.toFixed(1)
  } else if (!formData.optimumMoisture) {
    if (analysisResults.moistureMin && analysisResults.moistureMax) {
      const min = parseFloat(analysisResults.moistureMin)
      const max = parseFloat(analysisResults.moistureMax)
      if (!isNaN(min) && !isNaN(max)) {
        formData.optimumMoisture = ((min + max) / 2).toFixed(1)
      }
    } else if (analysisResults.moistureMax) {
      formData.optimumMoisture = analysisResults.moistureMax
    } else if (analysisResults.moistureMin) {
      formData.optimumMoisture = analysisResults.moistureMin
    }
  }
  
  // 设计压实度
  if (actualCompactionMax) {
    formData.designCompaction = actualCompactionMax.toFixed(1)
  } else if (actualCompactionMin) {
    formData.designCompaction = actualCompactionMin.toFixed(1)
  } else if (!formData.designCompaction) {
    if (analysisResults.compactionMax) {
      formData.designCompaction = analysisResults.compactionMax
    } else if (analysisResults.compactionMin) {
      formData.designCompaction = analysisResults.compactionMin
    }
  }
  
  // 填充湿密度、干密度、含水率、压实度的输入框（使用范围内的随机数）
  for (let i = validStart; i <= validEnd; i++) {
    // 填充湿密度
    if (!formData[`wetDensity_${i}`]) {
      const randomValue = getRandomInRange(analysisResults.wetDensityMin, analysisResults.wetDensityMax, 2)
      if (randomValue) {
        formData[`wetDensity_${i}`] = randomValue
      }
    }
    
    // 填充干密度
    if (!formData[`dryDensity_${i}`]) {
      const randomValue = getRandomInRange(analysisResults.dryDensityMin, analysisResults.dryDensityMax, 2)
      if (randomValue) {
        formData[`dryDensity_${i}`] = randomValue
      }
    }
    
    // 填充含水率
    if (!formData[`moisture_${i}`]) {
      const randomValue = getRandomInRange(analysisResults.moistureMin, analysisResults.moistureMax, 1)
      if (randomValue) {
        formData[`moisture_${i}`] = randomValue
      }
    }
    
    // 填充压实度
    if (!formData[`compaction_${i}`]) {
      const randomValue = getRandomInRange(analysisResults.compactionMin, analysisResults.compactionMax, 1)
      if (randomValue) {
        formData[`compaction_${i}`] = randomValue
      } else if (formData.designCompaction) {
        formData[`compaction_${i}`] = formData.designCompaction
      }
    }
  }
  
  alert('自动分析并填充完成')
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
            font-size: 16px;
            color: #000;
            max-width: 100%;
            min-width: 800px;
            margin: 0 auto;
            padding: 16px;
            background-color: var(--bg-card);
            border-radius: 8px;
            box-shadow: var(--shadow);
            box-sizing: border-box;
        }
        h2 {
            text-align: center;
            margin-bottom: 20px;
            font-size: 24px;
            font-weight: bold;
            color: black;
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
            table-layout: fixed;
            word-break: break-all;
        }
        td {
            border: 1px solid black;
            padding: 8px 5px;
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

        /* 统一输入字段样式，确保与表格其他字段字体一致 */
        input[type="text"], textarea, select {
            font-family: inherit;
            font-size: inherit;
            color: inherit;
        }

        input[type="text"]:disabled, textarea:disabled, select:disabled {
            color: inherit;
            font-family: inherit;
            font-size: inherit;
        }
        
        /* 移除数值输入框的上下调节按钮 */
        input[type="number"] {
            appearance: textfield;
            -moz-appearance: textfield;
        }
        
        input[type="number"]::-webkit-inner-spin-button,
        input[type="number"]::-webkit-outer-spin-button {
            -webkit-appearance: none;
            margin: 0;
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
            font-weight: normal;
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
        
        /* 模态窗口样式 */
        .modal-overlay {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0, 0, 0, 0.5);
            display: flex;
            justify-content: center;
            align-items: center;
            z-index: 1000;
        }
        
        .modal-content {
            background-color: white;
            padding: 30px;
            border-radius: 10px;
            width: 450px;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
            border: 1px solid #e0e0e0;
        }
        
        .modal-content h3 {
            margin-top: 0;
            margin-bottom: 25px;
            text-align: center;
            color: #333;
            font-size: 18px;
            font-weight: bold;
        }
        
        .form-group {
            margin-bottom: 25px;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 8px;
            font-weight: 500;
            color: #555;
        }
        
        .range-inputs {
            display: flex;
            align-items: center;
            gap: 10px;
            padding: 15px;
            background-color: #f5f7fa;
            border-radius: 6px;
            border: 1px solid #e0e0e0;
        }
        
        .range-inputs input {
            width: 80px;
            padding: 6px 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 14px;
        }
        
        .analysis-results {
            margin: 25px 0;
            padding: 20px;
            background-color: #f8f9fa;
            border-radius: 8px;
            border: 1px solid #e0e0e0;
        }
        
        .analysis-results h4 {
            margin-top: 0;
            margin-bottom: 15px;
            color: #333;
            font-size: 16px;
        }
        
        .result-item {
            display: flex;
            justify-content: space-between;
            margin-bottom: 12px;
            align-items: center;
        }
        
        .result-item span:first-child {
            font-weight: 500;
            color: #555;
        }
        
        .result-item .range-inputs {
            flex: 1;
            margin-left: 15px;
        }
        
        .modal-actions {
            display: flex;
            justify-content: flex-end;
            gap: 12px;
            margin-top: 25px;
            padding-top: 20px;
            border-top: 1px solid #e0e0e0;
        }
        
        .modal-actions .btn {
            padding: 8px 16px;
            border-radius: 4px;
            font-size: 14px;
            cursor: pointer;
            transition: all 0.2s;
        }
        
        .modal-actions .btn-primary {
            background-color: #3498db;
            color: white;
            border: none;
        }
        
        .modal-actions .btn-primary:hover {
            background-color: #2980b9;
        }
        
        .modal-actions .btn-secondary {
            background-color: #f5f7fa;
            color: #333;
            border: 1px solid #ddd;
        }
        
        .modal-actions .btn-secondary:hover {
            background-color: #e0e0e0;
        }
    
</style>
