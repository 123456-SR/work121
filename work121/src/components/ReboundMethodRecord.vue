<template>
  <div class="reboundMethodRecord-container">


    <div class="no-print toolbar">
      <div class="toolbar-left">
        <button @click="goToList" class="link-button">&lt; 返回列表</button>
        <span v-if="!draftMode" class="record-nav">
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
        <div v-if="formData.status !== undefined" class="status-text">
          状态:
          <span :style="{ color: getStatusColor(formData.status) }" class="status-label">
            {{ getStatusText(formData.status) }}
          </span>
        </div>

        <template v-if="formData.id && !draftMode">
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
            升级为已审核
          </button>
          <button
            v-if="formData.status === 1"
            @click="submitWorkflow('REJECT')"
            class="btn btn-danger btn-small"
          >
            打回
          </button>
        </template>

        <button
          v-if="!draftMode"
          @click="handleSign"
          class="btn btn-secondary btn-small"
        >
          签字
        </button>
        <button
          v-if="!draftMode"
          @click="printDocument"
          class="btn btn-secondary btn-small"
        >
          打印此单
        </button>
        <button
          v-if="!draftMode"
          @click="generatePdf"
          class="btn btn-secondary btn-small"
        >
          下载PDF
        </button>
        <button
          v-if="!draftMode"
          @click="previewPdf"
          class="btn btn-secondary btn-small"
        >
          预览PDF
        </button>
        <button
          @click="submitForm"
          class="btn btn-secondary btn-small"
        >
          保存
        </button>
      </div>
    </div>

    <form id="pdfForm" ref="pdfForm" method="post">
    <h2>回弹法检测混凝土抗压强度记录表</h2>

    <div class="header-top">
        <span>委托单位：<input type="text" v-model="formData.entrustingUnit"   name="entrustingUnit" style="width: 200px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>统一编号：<input type="text" v-model="formData.unifiedNumber"   name="unifiedNumber" style="width: 150px; border-bottom: 1px solid black;"></span>
    </div>
    <div class="header-top">
        <span>样品编号：<input type="text" v-model="formData.sampleNo"   name="sampleNo" style="width: 200px; border-bottom: 1px solid black; text-align: left;"></span>
    </div>

    <table>
        <colgroup>
            <col style="width: 4%;"> <!-- Col 1: Index -->
            <template v-for="(n, i_idx) in 16" :key="i_idx">
            <col style="width: 3%;"> <!-- Cols 2-17: Values (48%) -->
            </template>
            <col style="width: 12%;"> <!-- Col 18: Avg -->
            <col style="width: 12%;"> <!-- Col 19: Depth -->
            <col style="width: 12%;"> <!-- Col 20: Est Str -->
            <col style="width: 12%;"> <!-- Col 21: Corr Str -->
        </colgroup>

        <!-- Header Info -->
        <tr>
            <td colspan="2" class="label">工程名称</td>
            <td colspan="13" class="left-align"><input type="text" v-model="formData.projectName"   name="projectName" style="text-align: left;"></td>
            <td colspan="3" class="label">委托日期</td>
            <td colspan="3"><input type="text" v-model="formData.commissionDate"   name="commissionDate"></td>
        </tr>
        <tr>
            <td colspan="2" class="label">结构部位</td>
            <td colspan="5"><input type="text" v-model="formData.structurePart"   name="structurePart"></td>
            <td colspan="3" class="label">检测类别</td>
            <td colspan="4"><input type="text" v-model="formData.testCategory"   name="testCategory"></td>
            <td colspan="3" class="label">浇筑日期</td>
            <td colspan="4"><input type="text" v-model="formData.pourDate"   name="pourDate"></td>
        </tr>
        <tr>
            <td colspan="2" class="label">样品状态</td>
            <td colspan="5"><input type="text" v-model="formData.sampleStatus"   name="sampleStatus"></td>
            <td colspan="3" class="label">测试角度</td>
            <td colspan="4"><input type="text" v-model="formData.testAngle"   name="testAngle"></td>
            <td colspan="3" class="label">检测日期</td>
            <td colspan="4"><input type="text" v-model="formData.testDate"   name="testDate"></td>
        </tr>
        <tr>
            <td colspan="2" class="label">设计指标</td>
            <td colspan="5"><input type="text" v-model="formData.designIndex"   name="designIndex"></td>
            <td colspan="7" class="label">构件厚度或骨料最大粒径</td>
            <td colspan="7"><input type="text" v-model="formData.aggregateSize"   name="aggregateSize"></td>
        </tr>

        <!-- Data Table Header -->
        <tr>
            <td rowspan="2" class="label">测<br>区</td>
            <td colspan="16" class="label">回弹值</td>
            <td rowspan="2" class="label">平均<br>回弹<br>值</td>
            <td rowspan="2" class="label">碳化<br>深度<br>mm</td>
            <td rowspan="2" class="label">推定<br>强度<br>值MPa</td>
            <td rowspan="2" class="label">碳化修<br>正强度<br>值MPa</td>
        </tr>
        <tr>
            <template v-for="(n, i_idx) in 16" :key="i_idx">
            <td class="label">{{ (i_idx + 1) }}</td>
            </template>
        </tr>

        <!-- Data Rows -->
        <template v-for="(n, i_idx) in 10" :key="i_idx">
        <tr>
            <td>{{ (i_idx + 1) }}</td>
            <template v-for="(n, j_idx) in 16" :key="j_idx">
            <td><input type="text" :name="'reboundValue_' + (i_idx + 1) + '_' + (j_idx + 1)" v-model="formData['reboundValue_' + (i_idx + 1) + '_' + (j_idx + 1)]"></td>
            </template>
            <td><input type="text" :name="'avgRebound_' + (i_idx + 1)" v-model="formData['avgRebound_' + (i_idx + 1)]"></td>
            <td><input type="text" :name="'carbonDepth_' + (i_idx + 1)" v-model="formData['carbonDepth_' + (i_idx + 1)]"></td>
            <td><input type="text" :name="'estimatedStrength_' + (i_idx + 1)" v-model="formData['estimatedStrength_' + (i_idx + 1)]"></td>
            <td><input type="text" :name="'correctedStrength_' + (i_idx + 1)" v-model="formData['correctedStrength_' + (i_idx + 1)]"></td>
        </tr>
        </template>

        <!-- Summary Section -->
        <tr>
            <td colspan="3" class="label">平均强度值<br>MPa</td>
            <td colspan="2"><input type="text" v-model="formData.avgStrength"   name="avgStrength"></td>
            <td colspan="3" class="label">标准差<br>MPa</td>
            <td colspan="2"><input type="text" v-model="formData.stdDev"   name="stdDev"></td>
            <td colspan="3" class="label">变异系<br>数%</td>
            <td colspan="2"><input type="text" v-model="formData.coefVariation"   name="coefVariation"></td>
            <td colspan="3" class="label">构件强度推<br>定值 MPa</td>
            <td colspan="3"><input type="text" v-model="formData.compEstimatedStrength"   name="compEstimatedStrength"></td>
        </tr>
        <tr>
            <td colspan="2" class="label">仪器设备</td>
            <td colspan="11" class="left-align"><input type="text" v-model="formData.equipment"   name="equipment" style="text-align: left;"></td>
            <td colspan="4" class="label">碳化平均值<br>mm</td>
            <td colspan="4"><input type="text" v-model="formData.avgCarbonation"   name="avgCarbonation"></td>
        </tr>
        <tr>
            <td colspan="2" class="label">依据标准</td>
            <td colspan="11" class="left-align"><input type="text" v-model="formData.standard"   name="standard" style="text-align: left;"></td>
            <td colspan="4" class="label">率定值</td>
            <td colspan="4" class="left-align">

                检测前：<input type="text" v-model="formData.calibrationBefore"   name="calibrationBefore" style="width: 60px; border-bottom: 1px solid #ccc; text-align: center;"><br>
                检测后：<input type="text" v-model="formData.calibrationAfter"   name="calibrationAfter" style="width: 60px; border-bottom: 1px solid #ccc; text-align: center;">
            </td>
        </tr>
        <tr>
            <td colspan="2" class="label">结论</td>
            <td colspan="19" class="left-align" style="height: 40px;">
                <input type="text" v-model="formData.conclusion"   name="conclusion" style="text-align: left; width: 100%;">
            </td>
        </tr>
        <tr>
            <td colspan="2" class="label" style="height: 150px; vertical-align: top;">测区示意图：</td>
            <td colspan="19" style="height: 150px;">
                <!-- Placeholder for diagram -->
            </td>
        </tr>
        <tr>
            <td colspan="2" class="label">备注</td>
            <td colspan="19" class="left-align" style="height: 40px;">
                <textarea v-model="formData.remarks"  name="remarks" style="width: 100%; height: 100%;"></textarea>
            </td>
        </tr>
    </table>

    <div class="footer-info">
        <span style="position: relative;">
            审核：<span style="display: inline-block; width: 100px; border-bottom: 1px solid black; height: 1em;"></span>
            <div v-if="formData.recordReviewSign" style="position: absolute; top: -20px; left: 40px; pointer-events: none;">
                <img :src="formData.recordReviewSign" style="width: 80px; height: 40px; mix-blend-mode: multiply;" />
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
import { reactive, ref, onMounted, inject, defineProps, computed } from 'vue'
import axios from 'axios'

// 注入导航方法
const navigateTo = inject('navigateTo')

// 定义props
const props = defineProps({
  id: {
    type: String,
    default: null
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

const pdfForm = ref(null)
const draftMode = computed(() => props.draftMode)
const records = ref([])
const currentIndex = ref(0)
const totalRecords = computed(() => records.value.length)

const formData = reactive({
  id: '',
  entrustmentId: '',
  entrustingUnit: '',
  unifiedNumber: '',
  sampleNo: '',
  projectName: '',
  commissionDate: '',
  structurePart: '',
  testCategory: '',
  pourDate: '',
  sampleStatus: '',
  testAngle: '',
  testDate: '',
  designIndex: '',
  aggregateSize: '',
  avgStrength: '',
  stdDev: '',
  coefVariation: '',
  compEstimatedStrength: '',
  equipment: '',
  avgCarbonation: '',
  standard: '',
  calibrationBefore: '',
  calibrationAfter: '',
  conclusion: '',
  recordTester: '',
  recordReviewer: '',
  recordReviewSign: '',
  filler: '',
  remarks: '',
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
        case 5: return '#4CAF50' // Green
        default: return '#000000'
    }
}

const normalizeSignatureSrc = (value) => {
  if (!value) return ''
  if (typeof value !== 'string') return ''
  if (value.startsWith('data:image')) return value
  return `data:image/png;base64,${value}`
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
        // Role check: Only recordTester can submit
        // Priority: formData.recordTester
        if (formData.recordTester && user.username !== formData.recordTester && user.fullName !== formData.recordTester) {
            alert('您不是该单据的记录检测人 (' + formData.recordTester + ')，无权提交')
            return
        }

        if (!formData.testerSignature) {
            alert('请先进行检测人签字')
            return
        }
        signatureData = formData.testerSignature.replace(/^data:image\/\w+;base64,/, '')
    } else if (action === 'AUDIT_PASS') {
        // Role check: Only recordReviewer can audit/reject at status 1
        // Priority: formData.recordReviewer
        if (formData.recordReviewer && user.username !== formData.recordReviewer && user.fullName !== formData.recordReviewer) {
            alert('您不是该单据的记录审核人 (' + formData.recordReviewer + ')，无权操作')
            return
        }

        // Auto fetch signature if missing
        if (!formData.recordReviewSign) {
            try {
                const sigRes = await axios.post('/api/signature/get', { userAccount: user.username })
                if (sigRes.data.success && sigRes.data.data && sigRes.data.data.signatureBlob) {
                     const sigData = `data:image/png;base64,${sigRes.data.data.signatureBlob}`
                     formData.recordReviewSign = sigData
                     formData.reviewerSignature = sigData
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
        signatureData = formData.recordReviewSign.replace(/^data:image\/\w+;base64,/, '')
    } else if (action === 'REJECT' && formData.status === 1) {
        // Role check: Only recordReviewer can audit/reject at status 1
        if (formData.recordReviewer && user.username !== formData.recordReviewer && user.fullName !== formData.recordReviewer) {
            alert('您不是该单据的记录审核人 (' + formData.recordReviewer + ')，无权操作')
            return
        }
    }
    // else if (action === 'SIGN_REVIEW') {
    //    // Role check: Only recordReviewer can sign
    //    if (formData.recordReviewer && user.username !== formData.recordReviewer) {
    //        alert('您不是该单据的记录审核人 (' + formData.recordReviewer + ')，无权签字')
    //        return
    //    }
    //
    //    if (!formData.recordReviewSign) {
    //        alert('请先进行记录审核人签字')
    //        return
    //    }
    //    signatureData = formData.recordReviewSign
    // }

    const request = {
        tableType: 'REBOUND_METHOD',
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
            // Refresh data - 重新加载数据以获取最新状态
            const entrustmentId = formData.entrustmentId || formData.unifiedNumber
            await loadData(entrustmentId)
            console.log('After workflow reload, formData.status:', formData.status)
        } else {
            alert('操作失败: ' + response.data.message)
        }
    } catch (e) {
        console.error('Workflow error', e)
        alert('操作异常')
    }
}

const initDynamicFields = () => {
  for (let i = 1; i <= 10; i++) {
    for (let j = 1; j <= 16; j++) {
      formData['reboundValue_' + i + '_' + j] = ''
    }
    formData['avgRebound_' + i] = ''
    formData['correctedStrength_' + i] = ''
    formData['carbonDepth_' + i] = ''
    formData['estimatedStrength_' + i] = ''
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
  }
})

const mapRecordToFormData = (record) => {
  initDynamicFields()
  
  formData.id = record.id || ''
  formData.entrustmentId = record.entrustmentId || formData.unifiedNumber
  // 状态统一转成数字，避免后端返回字符串导致严格等于判断失效（影响按钮显示）
  formData.status = record.status !== undefined ? Number(record.status) : 0
  console.log('mapRecordToFormData - record.status:', record.status, 'formData.status:', formData.status)
  
  // Map signature photos
  formData.reviewerSignature = normalizeSignatureSrc(record.reviewSignaturePhoto || '')
  formData.testerSignature = normalizeSignatureSrc(record.inspectSignaturePhoto || '')
  formData.recordReviewSign = normalizeSignatureSrc(record.recordReviewSign || record.reviewSignaturePhoto || '')
  
  // Map explicit columns
  if (record.structurePart) formData.structurePart = record.structurePart
  // If structurePart is empty, try constructionPart (from BusinessEntity)
  if (!formData.structurePart && record.constructionPart) formData.structurePart = record.constructionPart
  
  if (record.testResult) formData.conclusion = record.testResult // Assuming testResult maps to conclusion
  if (record.remarks) formData.remarks = record.remarks
  if (record.entrustmentId) formData.unifiedNumber = record.entrustmentId
  if (record.wtNum && !formData.unifiedNumber) formData.unifiedNumber = record.wtNum
  
  // Map fields from BusinessEntity/Entrustment（先映射除样品编号以外的）
  if (record.clientUnit) formData.entrustingUnit = record.clientUnit
  if (record.projectName) formData.projectName = record.projectName
  if (record.commissionDate) formData.commissionDate = record.commissionDate
  if (record.testCategory) formData.testCategory = record.testCategory
  
  // Map Roles
  // Load defaults from directory if available
  const directory = JSON.parse(localStorage.getItem('currentDirectory') || '{}')

  // Filler - Priority: record.filler
  formData.filler = record.filler || ''
  // formData.approver = ''

  // Record Tester - Priority: record.recordTester -> record.tester (legacy)
  formData.recordTester = record.recordTester || record.tester || ''

  // Record Reviewer - Priority: record.recordReviewer -> record.reviewer (legacy)
  if (record.recordReviewer || record.reviewer) {
    formData.recordReviewer = record.recordReviewer || record.reviewer
  }
  
  if (record.dataJson) {
    try {
      const json = JSON.parse(record.dataJson)
      // 保存当前状态，避免被 JSON 覆盖
      const currentStatus = formData.status
      // Merge json into formData
      Object.assign(formData, json)
      // 确保实体字段的状态优先于 JSON 中的状态
      if (record.status !== undefined) {
        formData.status = Number(record.status)
      } else {
        formData.status = currentStatus
      }
      // 样品编号兼容：优先使用 JSON 中的各种可能字段
      if (!formData.sampleNo) {
        if (json.sampleNo) {
          formData.sampleNo = json.sampleNo
        } else if (json.sampleNumber) {
          formData.sampleNo = json.sampleNumber
        } else if (json.sampleName) {
          formData.sampleNo = json.sampleName
        }
      }
      
      // Map legacy fields
      if (json.tester && !formData.recordTester) formData.recordTester = json.tester
      if (json.reviewer && !formData.recordReviewer) formData.recordReviewer = json.reviewer
      
      // Ensure entity fields override JSON if present
      if (record.reviewSignaturePhoto) formData.reviewerSignature = normalizeSignatureSrc(record.reviewSignaturePhoto)
      if (record.inspectSignaturePhoto) formData.testerSignature = normalizeSignatureSrc(record.inspectSignaturePhoto)
      if (record.recordReviewSign || record.reviewSignaturePhoto) {
        formData.recordReviewSign = normalizeSignatureSrc(record.recordReviewSign || record.reviewSignaturePhoto)
      }
      if (record.recordTester) formData.recordTester = record.recordTester
      if (record.recordReviewer) formData.recordReviewer = record.recordReviewer
      if (record.filler) formData.filler = record.filler
    } catch (e) {
      console.error('JSON parse error', e)
    }
  }

  // 样品编号兜底：如果上面都没有填到，就用委托单上的字段
  if (!formData.sampleNo) {
    if (record.sampleName) {
      formData.sampleNo = record.sampleName
    } else if (record.sampleNumber) {
      formData.sampleNo = record.sampleNumber
    }
  }
}

const saveCurrentRecordState = () => {
  if (records.value.length > 0 && currentIndex.value >= 0 && currentIndex.value < records.value.length) {
    const currentRecord = records.value[currentIndex.value]
    // Update dataJson
    const dataToSave = { ...formData }
    // Remove transient fields if any, or just save everything
    currentRecord.dataJson = JSON.stringify(dataToSave)
    
    // Update explicit fields
    currentRecord.structurePart = formData.structurePart
    currentRecord.testResult = formData.conclusion
    currentRecord.remarks = formData.remarks
    currentRecord.reviewSignaturePhoto = formData.reviewerSignature
    currentRecord.inspectSignaturePhoto = formData.testerSignature
    currentRecord.recordReviewSign = formData.recordReviewSign
    currentRecord.recordTester = formData.recordTester
    currentRecord.recordReviewer = formData.recordReviewer
    currentRecord.filler = formData.filler
  }
}

// 加载数据
const loadData = async (entrustmentId) => {
  try {
    const response = await axios.get(`/api/reboundMethod/get-by-entrustment-id?entrustmentId=${entrustmentId}`)
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

const addRecord = async () => {
  saveCurrentRecordState()
  
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
    structurePart: entrustmentData.constructionPart || '',
    testResult: '',
    remarks: '',
    // Map other fields from entrustmentData
    projectName: entrustmentData.projectName,
    testCategory: entrustmentData.testCategory,
    clientUnit: entrustmentData.clientUnit,
    sampleName: entrustmentData.sampleName,
    commissionDate: entrustmentData.commissionDate,
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
  
  const record = records.value[currentIndex.value]
  if (record.id) {
    if (!confirm('确定要删除这条已保存的记录吗？')) return
    
    try {
      const response = await axios.post('/api/reboundMethod/delete', { id: record.id })
      if (response.data.success) {
        records.value.splice(currentIndex.value, 1)
        if (currentIndex.value >= records.value.length) {
          currentIndex.value = records.value.length - 1
        }
        mapRecordToFormData(records.value[currentIndex.value])
      } else {
        alert('删除失败: ' + response.data.message)
      }
    } catch (e) {
      alert('删除失败')
    }
  } else {
    records.value.splice(currentIndex.value, 1)
    if (currentIndex.value >= records.value.length) {
      currentIndex.value = records.value.length - 1
    }
    mapRecordToFormData(records.value[currentIndex.value])
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

const printDocument = () => {
  window.print()
}

// 返回列表
const goToList = () => {
  if (navigateTo) {
    navigateTo('ReboundMethodRecordList');
  }
}

const generatePdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/rebound_method_record/generate'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}

const previewPdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/rebound_method_record/preview'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}

const submitForm = async () => {
  try {
    saveCurrentRecordState()
    
    // 获取当前登录用户信息
    const userInfoStr = localStorage.getItem('userInfo')
    let userInfo = null
    if (userInfoStr) {
      userInfo = JSON.parse(userInfoStr)
    }

    // 构建提交数据 based on current formData
    const submitData = {
      id: formData.id || '', // Use formData.id (might be empty for new record)
      entrustmentId: formData.entrustmentId || formData.unifiedNumber,
      // Map entity fields
      structurePart: formData.structurePart,
      testResult: formData.conclusion,
      remarks: formData.remarks,
      
      // JSON fields
      entrustingUnit: formData.entrustingUnit || '',
      projectName: formData.projectName || '',
      testCategory: formData.testCategory || '',
      sampleStatus: formData.sampleStatus || '',
      testAngle: formData.testAngle || '',
      designIndex: formData.designIndex || '',
      aggregateSize: formData.aggregateSize || '',
      avgStrength: formData.avgStrength || '',
      stdDev: formData.stdDev || '',
      coefVariation: formData.coefVariation || '',
      compEstimatedStrength: formData.compEstimatedStrength || '',
      equipment: formData.equipment || '',
      avgCarbonation: formData.avgCarbonation || '',
      standard: formData.standard || '',
      calibrationBefore: formData.calibrationBefore || '',
      calibrationAfter: formData.calibrationAfter || '',
      conclusion: formData.conclusion || '',
      recordTester: formData.recordTester,
      recordReviewer: formData.recordReviewer,
      filler: formData.filler,
      // Sync legacy fields
      tester: formData.recordTester,
      reviewer: formData.recordReviewer,
      reviewSignaturePhoto: formData.reviewerSignature,
      inspectSignaturePhoto: formData.testerSignature
    };

    // Include dataJson for persistence
    const dataJsonObj = { ...formData }
    // Remove legacy fields from dataJson to avoid confusion
    delete dataJsonObj.tester
    delete dataJsonObj.reviewer
    submitData.dataJson = JSON.stringify(dataJsonObj);

    // 发送请求
    const response = await axios.post('/api/reboundMethod/save', submitData);

    if (response.data.success) {
      alert('保存成功');
      // Update the current record with the saved data (especially ID)
      if (response.data.data) {
        const savedRecord = response.data.data
        records.value[currentIndex.value] = savedRecord
        mapRecordToFormData(savedRecord)
      }
    } else {
      alert('保存失败: ' + response.data.message);
    }
  } catch (error) {
    console.error('保存错误:', error);
    alert('保存失败，请稍后重试');
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
      const currentAccount = user.username
      const currentName = user.fullName || user.nickName || currentAccount

      // Match Record Tester (UI field)
      if (formData.recordTester === currentName || formData.recordTester === currentAccount || !formData.recordTester) {
        formData.testerSignature = imgSrc
        signed = true
      }

      if (signed) {
        alert('签名成功')
      } else {
        alert(`当前用户(${currentName})与表单中的检测人员不匹配，无法签名`)
      }
    } else {
      alert('未找到您的电子签名，请先去“电子签名”页面设置')
    }
  } catch (error) {
    console.error('Sign error:', error)
    alert('签名失败')
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

        .reboundMethodRecord-container {
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
        }
        .header-top {
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
        }
        td {
            border: 1px solid black;
            padding: 2px;
            vertical-align: middle;
            text-align: center;
            font-size: 14px;
            overflow: hidden;
        }
        .label {
            font-weight: bold;
            white-space: normal;
        }
        .left-align {
            text-align: left;
            padding-left: 5px;
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
            margin-top: 10px;
            font-size: 16px;
            font-weight: bold;
        }
        .page-footer {
            margin-top: 5px;
            display: flex;
            justify-content: space-between;
            font-size: 14px;
        }
        @media print {
            .reboundMethodRecord-container {
                width: 100%;
                margin: 0;
                padding: 0;
            }
            .no-print {
                display: none;
            }
        }
    
</style>
