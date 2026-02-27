<template>
  <div class="waterReplacementRecord-container">


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
          @click="submitForm"
          class="btn btn-secondary btn-small"
        >
          保存
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
      </div>
    </div>

    <form id="pdfForm" ref="pdfForm" method="post">
    <h2>相对密度试验记录表（灌水法）</h2>

    <div class="header-info">
        <span>工程部位：<input type="text" v-model="formData.projectName"   name="projectName" style="width: 200px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>试验日期：<input type="text" v-model="formData.testDate"   name="testDate" style="width: 150px; border-bottom: 1px solid black;"></span>
    </div>
    <div class="header-info">
        <span>依据标准：<input type="text" v-model="formData.standard"   name="standard" style="width: 150px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>最大干密度：<input type="text" v-model="formData.maxDryDensity"   name="maxDryDensity" style="width: 80px; border-bottom: 1px solid black;"> g/cm³</span>
        <span>最小干密度：<input type="text" v-model="formData.minDryDensity"   name="minDryDensity" style="width: 80px; border-bottom: 1px solid black;"> g/cm³</span>
        <span>最优含水率：<input type="text" v-model="formData.optMoisture"   name="optMoisture" style="width: 80px; border-bottom: 1px solid black;"> %</span>
        <span>相对密度：<input type="text" v-model="formData.relativeDensity"   name="relativeDensity" style="width: 80px; border-bottom: 1px solid black;"></span>
        <span>统一编号：<input type="text" v-model="formData.unifiedNumber"   name="unifiedNumber" style="width: 150px; border-bottom: 1px solid black;"></span>
    </div>
    <div class="header-info">
        <span>水的密度：<input type="text" v-model="formData.waterDensity"   name="waterDensity" style="width: 80px; border-bottom: 1px solid black;"> g/cm³</span>
        <span>仪器设备：<input type="text" v-model="formData.equipment"   name="equipment" style="width: 150px; border-bottom: 1px solid black;"></span>
        <span>设计压实度：<input type="text" v-model="formData.designCompaction"   name="designCompaction" style="width: 80px; border-bottom: 1px solid black;"></span>
        <span>检测类别：<input type="text" v-model="formData.testCategory"   name="testCategory" style="width: 100px; border-bottom: 1px solid black;"></span>
    </div>

    <table>
        <!-- Table Header -->
        <tr>
            <td colspan="4" class="label" style="width: 20%;">取样位置</td>
            <template v-for="(n, i_idx) in 4" :key="i_idx">
            <td colspan="4"><input type="text" :name="'samplingLocation_' + i_idx" v-model="formData['samplingLocation_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td colspan="3" class="label">套环体积</td>
            <td class="label">cm³</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'ringVolume_' + i_idx" v-model="formData['ringVolume_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td rowspan="2" colspan="2" class="label">储水桶<br>水位</td>
            <td class="label">初始</td>
            <td class="label">cm</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'initialWaterLevel_' + i_idx" v-model="formData['initialWaterLevel_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td class="label">终了</td>
            <td class="label">cm</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'finalWaterLevel_' + i_idx" v-model="formData['finalWaterLevel_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td colspan="3" class="label">储水桶面积</td>
            <td class="label">cm²</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'tankArea_' + i_idx" v-model="formData['tankArea_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td colspan="3" class="label">试坑体积</td>
            <td class="label">cm³</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'pitVolume_' + i_idx" v-model="formData['pitVolume_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td colspan="3" class="label">试样质量</td>
            <td class="label">g</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'sampleMass_' + i_idx" v-model="formData['sampleMass_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td colspan="3" class="label">试样湿密度</td>
            <td class="label">g/cm³</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'wetDensity_' + i_idx" v-model="formData['wetDensity_' + i_idx]"></td>
            </template>
        </tr>

        <!-- Moisture Content Section -->
        <tr>
            <td rowspan="6" colspan="2" class="label" style="width: 5%;">试样<br>含水率</td>
            <td class="label">编号</td>
            <td class="label">/</td>
            <template v-for="(n, i_idx) in 16" :key="i_idx">
            <td><input type="text" :name="'moistureNo_' + i_idx" v-model="formData['moistureNo_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td class="label">容器质量</td>
            <td class="label">g</td>
            <template v-for="(n, i_idx) in 16" :key="i_idx">
            <td><input type="text" :name="'containerMass_' + i_idx" v-model="formData['containerMass_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td class="label">容器+湿样质量</td>
            <td class="label">g</td>
            <template v-for="(n, i_idx) in 16" :key="i_idx">
            <td><input type="text" :name="'wetSampleMass_' + i_idx" v-model="formData['wetSampleMass_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td class="label">容器+干样质量</td>
            <td class="label">g</td>
            <template v-for="(n, i_idx) in 16" :key="i_idx">
            <td><input type="text" :name="'drySampleMass_' + i_idx" v-model="formData['drySampleMass_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td class="label">含水率</td>
            <td class="label">%</td>
            <template v-for="(n, i_idx) in 16" :key="i_idx">
            <td><input type="text" :name="'moistureContent_' + i_idx" v-model="formData['moistureContent_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td class="label">平均含水率</td>
            <td class="label">%</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'avgMoisture_' + i_idx" v-model="formData['avgMoisture_' + i_idx]"></td>
            </template>
        </tr>

        <!-- Bottom Section -->
        <tr>
            <td colspan="3" class="label">实测干密度</td>
            <td class="label">g/cm³</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'measuredDryDensity_' + i_idx" v-model="formData['measuredDryDensity_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td colspan="3" class="label">平均实测干密度</td>
            <td class="label">g/cm³</td>
            <template v-for="(n, i_idx) in 4" :key="i_idx">
            <td colspan="4"><input type="text" :name="'avgMeasuredDryDensity_' + i_idx" v-model="formData['avgMeasuredDryDensity_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td colspan="3" class="label">相对密度</td>
            <td class="label"></td>
            <template v-for="(n, i_idx) in 4" :key="i_idx">
            <td colspan="4"><input type="text" :name="'relativeDensity_' + i_idx" v-model="formData['relativeDensity_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td colspan="4" class="label">备注</td>
            <td colspan="16" class="left-align" style="height: 60px; vertical-align: top;">
                <textarea v-model="formData.remarks"  name="remarks" style="width: 100%; height: 100%;"></textarea>
            </td>
        </tr>
    </table>

        <div class="footer-info">
            <span style="position: relative; margin-right: 20px;">
                试验：<span style="display: inline-block; width: 100px; border-bottom: 1px solid black; height: 1em;"></span>
                <div v-if="formData.testerSignature" style="position: absolute; top: -20px; left: 40px; pointer-events: none;">
                    <img :src="formData.testerSignature" style="width: 80px; height: 40px; mix-blend-mode: multiply;" />
                </div>
            </span>
            <span style="position: relative;">
                审核：<span style="display: inline-block; width: 100px; border-bottom: 1px solid black; height: 1em;"></span>
                <div v-if="formData.reviewerSignature" style="position: absolute; top: -20px; left: 40px; pointer-events: none;">
                    <img :src="formData.reviewerSignature" style="width: 80px; height: 40px; mix-blend-mode: multiply;" />
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

const navigateTo = inject('navigateTo')


const pdfForm = ref(null)

// 1:N State
const records = ref([])
const currentIndex = ref(0)
const totalRecords = computed(() => records.value.length)

const formData = reactive({
  id: '',
  entrustmentId: '',
  projectName: '',
  testDate: '',
  standard: '',
  maxDryDensity: '',
  minDryDensity: '',
  optMoisture: '',
  relativeDensity: '',
  unifiedNumber: '',
  waterDensity: '',
  equipment: '',
  designCompaction: '',
  testCategory: '',
  recordTester: '',
  recordReviewer: '',
  filler: '',
  remarks: '',
  reviewerSignature: '',
  testerSignature: '',
  status: 0 // 0:Draft, 1:PendingAudit, 2:Returned, 3:PendingSign, 5:Approved
})

// Status Text Helper
const getStatusText = (status) => {
    switch(status) {
        case 0: return '草稿'
        case 1: return '待审核'
        case 2: return '已打回'
        case 3: return '待签字'
        case 5: return '已通过'
        default: return '未知'
    }
}

const getStatusColor = (status) => {
    switch(status) {
        case 0: return '#9E9E9E' // Grey
        case 1: return '#2196F3' // Blue
        case 2: return '#F44336' // Red
        case 3: return '#FF9800' // Orange
        case 5: return '#4CAF50' // Green
        default: return '#000000'
    }
}

// Workflow Action Handler
const submitWorkflow = async (action) => {
    if (!formData.id) {
        alert('请先保存记录')
        return
    }
    
    // Get current user
    const user = JSON.parse(localStorage.getItem('userInfo'))
    if (!user || !user.username) {
        alert('请先登录')
        return
    }

    let signatureData = null
    
    // Prepare signature based on action
    if (action === 'SUBMIT') {
        if (!formData.testerSignature) {
            alert('请先进行检测人签字')
            return
        }
        
        // Check if current user is the tester
        if (formData.recordTester && user.username !== formData.recordTester && user.fullName !== formData.recordTester) {
            alert('您不是该单据的记录检测人 (' + formData.recordTester + ')，无权提交')
            return
        }
        
        signatureData = formData.testerSignature.replace(/^data:image\/\w+;base64,/, '')
    } else if (action === 'AUDIT_PASS') {
        // Role check
        if (formData.recordReviewer && user.username !== formData.recordReviewer && user.fullName !== formData.recordReviewer) {
            alert('您不是该单据的记录复核人 (' + formData.recordReviewer + ')，无权操作')
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
        signatureData = formData.reviewerSignature.replace(/^data:image\/\w+;base64,/, '')
    } else if (action === 'REJECT') {
        // Role check: Only recordReviewer can reject
        if (formData.recordReviewer && user.username !== formData.recordReviewer && user.fullName !== formData.recordReviewer) {
            alert('您不是该单据的记录复核人 (' + formData.recordReviewer + ')，无权操作')
            return
        }
    }

    const request = {
        tableType: 'WATER_REPLACEMENT',
        recordId: formData.id,
        action: action,
        userAccount: user.username,
        signatureData: signatureData,
        nextHandler: '' // Optional: could let user select next handler
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
            // Reload data to reflect status change
            loadData(formData.entrustmentId)
        } else {
            alert('操作失败: ' + response.data.message)
        }
    } catch (e) {
        console.error('Workflow error', e)
        alert('操作异常')
    }
}

// Initialize dynamic fields
const initDynamicFields = () => {
  for (let i_idx = 0; i_idx < 16; i_idx++) {
    formData['drySampleMass_' + i_idx] = ''
    formData['tankArea_' + i_idx] = ''
    formData['relativeDensity_' + i_idx] = ''
    formData['ringVolume_' + i_idx] = ''
    formData['pitVolume_' + i_idx] = ''
    formData['samplingLocation_' + i_idx] = ''
    formData['moistureContent_' + i_idx] = ''
    formData['avgMoisture_' + i_idx] = ''
    formData['wetDensity_' + i_idx] = ''
    formData['finalWaterLevel_' + i_idx] = ''
    formData['moistureNo_' + i_idx] = ''
    formData['initialWaterLevel_' + i_idx] = ''
    formData['measuredDryDensity_' + i_idx] = ''
    formData['avgMeasuredDryDensity_' + i_idx] = ''
    formData['containerMass_' + i_idx] = ''
    formData['wetSampleMass_' + i_idx] = ''
    formData['sampleMass_' + i_idx] = ''
  }
}

onMounted(async () => {
  initDynamicFields()
  const urlParams = new URLSearchParams(window.location.search)
  const wtNumParam = props.wtNum || urlParams.get('wtNum')
  const idParam = props.id || urlParams.get('id')

  if (wtNumParam) {
    try {
      const res = await axios.get('/api/jc-core-wt-info/by-wt-num', { params: { wtNum: wtNumParam } })
      if (res.data && res.data.success && res.data.data) {
        const wt = res.data.data
        formData.entrustmentId = wt.id || wtNumParam
        formData.unifiedNumber = wt.sampleNumber || wtNumParam
        loadData(formData.entrustmentId)
        return
      }
    } catch (e) {}
    formData.entrustmentId = wtNumParam
    formData.unifiedNumber = wtNumParam
    loadData(wtNumParam)
    return
  }

  if (idParam) {
    try {
      const res = await axios.get('/api/jc-core-wt-info/by-id', { params: { id: idParam } })
      if (res.data && res.data.success && res.data.data) {
        const wt = res.data.data
        formData.entrustmentId = wt.id || idParam
        formData.unifiedNumber = wt.sampleNumber || formData.unifiedNumber
        loadData(formData.entrustmentId)
        return
      }
    } catch (e) {}
    formData.entrustmentId = idParam
    loadData(idParam)
    return
  }

  const newRecord = {
    id: '',
    entrustmentId: '',
    dataJson: JSON.stringify(formData),
    reviewSignaturePhoto: '',
    inspectSignaturePhoto: ''
  }
  records.value = [newRecord]
  currentIndex.value = 0
  mapRecordToFormData(newRecord)
})

const mapRecordToFormData = (record) => {
  initDynamicFields()
  
  formData.id = record.id || ''
  formData.entrustmentId = record.entrustmentId || props.id
  formData.reviewerSignature = record.reviewSignaturePhoto || ''
  formData.testerSignature = record.inspectSignaturePhoto || ''
  
  if (record.dataJson) {
    try {
      const parsed = JSON.parse(record.dataJson)
      Object.keys(parsed).forEach(key => {
        formData[key] = parsed[key]
      })
    } catch (e) {
      console.error('JSON parse error', e)
    }
  }
  
  // Ensure entity fields override dataJson if they exist
  if (record.reviewSignaturePhoto) formData.reviewerSignature = record.reviewSignaturePhoto;
  if (record.inspectSignaturePhoto) formData.testerSignature = record.inspectSignaturePhoto;
  
  if (record.projectName) formData.projectName = record.projectName;
  
  // Correctly map unifiedNumber and entrustmentId
  if (record.wtNum) {
      formData.unifiedNumber = record.wtNum
  } else if (record.entrustmentId) {
      // If wtNum is not available, try to use entrustmentId (legacy behavior)
      // But don't overwrite if we already have a unifiedNumber
      if (!formData.unifiedNumber) formData.unifiedNumber = record.entrustmentId
  }

  // Added mappings
  if (record.clientUnit) formData.entrustingUnit = record.clientUnit
  if (record.constructionPart) formData.constructionPart = record.constructionPart
  if (record.testCategory) formData.testCategory = record.testCategory
  if (record.equipment) formData.equipment = record.equipment
  if (record.testBasis) formData.standard = record.testBasis
  if (record.commissionDate && !formData.testDate) formData.testDate = record.commissionDate // Fallback if testDate empty
  
  if (record.status !== undefined) {
      formData.status = record.status
  } else {
      formData.status = 0 // Default to Draft if not present
  }

  // Map Roles
  // Load defaults from directory if available
  const directory = JSON.parse(localStorage.getItem('currentDirectory') || '{}')
  
  // Filler - Priority: record.filler
  formData.filler = record.filler || ''
  
  // Record Tester - Priority: record.recordTester -> legacy tester
  formData.recordTester = record.recordTester || record.tester || ''
  
  // Record Reviewer - Priority: record.recordReviewer -> legacy reviewer
  if (record.recordReviewer || record.reviewer) {
    formData.recordReviewer = record.recordReviewer || record.reviewer
  }
  
  // If JSON has tester/reviewer but record fields are empty (legacy data in JSON), map them
  if (!formData.recordTester && formData.tester) formData.recordTester = formData.tester
  if (!formData.recordReviewer && formData.reviewer) formData.recordReviewer = formData.reviewer
}

const getCleanDataJson = () => {
  const dynamicData = {}
  
  // Static fields to include in JSON
  const staticFields = [
    'projectName', 'testDate', 'standard', 'maxDryDensity', 'minDryDensity', 
    'optMoisture', 'relativeDensity', 'waterDensity', 'equipment', 
    'designCompaction', 'testCategory', 'remarks'
  ]
  
  staticFields.forEach(field => {
    if (formData[field] !== undefined && formData[field] !== null) {
      dynamicData[field] = formData[field]
    }
  })

  // Dynamic fields
  Object.keys(formData).forEach(key => {
    // Match dynamic fields: samplingLocation_0, ringVolume_0, etc.
    if (key.match(/^(samplingLocation|ringVolume|initialWaterLevel|finalWaterLevel|tankArea|pitVolume|sampleMass|wetDensity|moistureNo|containerMass|wetSampleMass|drySampleMass|moistureContent|avgMoisture|measuredDryDensity|avgMeasuredDryDensity|relativeDensity)_\d+$/)) {
      dynamicData[key] = formData[key]
    }
  })
  
  return JSON.stringify(dynamicData)
}

const saveCurrentRecordState = () => {
  if (records.value.length === 0) return
  
  const record = records.value[currentIndex.value]
  record.id = formData.id
  record.entrustmentId = formData.entrustmentId || formData.unifiedNumber
  record.reviewSignaturePhoto = formData.reviewerSignature
  record.inspectSignaturePhoto = formData.testerSignature
  record.projectName = formData.projectName
  
  record.dataJson = getCleanDataJson()
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
    entrustmentId: props.wtNum || props.id || formData.unifiedNumber,
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
      const response = await axios.post('/api/water-replacement/delete', { id: currentRecord.id })
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

const loadData = async (entrustmentId) => {
  try {
    const response = await axios.get('/api/water-replacement/get-by-entrustment-id', {
      params: { entrustmentId }
    });
    
    if (response.data.success && response.data.data && response.data.data.length > 0) {
      records.value = response.data.data
      currentIndex.value = 0
      mapRecordToFormData(records.value[0])
    } else {
        // Create new record
        const newRecord = {
            id: '',
            entrustmentId: entrustmentId,
            dataJson: '{}'
        }
        
        // Fetch entrustment info to pre-fill
        try {
            let eData = null
            const byIdRes = await axios.get('/api/jc-core-wt-info/by-id', { params: { id: entrustmentId } })
            if (byIdRes.data && byIdRes.data.success && byIdRes.data.data) {
              eData = byIdRes.data.data
            } else {
              const detailRes = await axios.get('/api/jc-core-wt-info/detail', { params: { unifiedNumber: entrustmentId } })
              if (detailRes.data && detailRes.data.success) eData = detailRes.data.data
            }
            if (eData) {
              formData.entrustmentId = eData.id || entrustmentId
              formData.unifiedNumber = eData.sampleNumber || formData.unifiedNumber || entrustmentId
              formData.projectName = eData.projectName || ''
              formData.recordTester = ''
              formData.recordReviewer = ''
              formData.filler = ''
              newRecord.dataJson = JSON.stringify(formData)
            }
        } catch (e) {
            console.error('Failed to load entrustment details', e)
        }
        
        records.value = [newRecord]
        currentIndex.value = 0
        mapRecordToFormData(newRecord)
    }
  } catch (error) {
    console.error('Failed to load data:', error);
  }
}

const submitForm = async () => {
  try {
    const submitData = {
      id: formData.id,
      entrustmentId: formData.entrustmentId || formData.unifiedNumber,
      dataJson: getCleanDataJson(),
      reviewSignaturePhoto: formData.reviewerSignature,
      inspectSignaturePhoto: formData.testerSignature,
      // Add explicit role fields
      recordTester: formData.recordTester,
      recordReviewer: formData.recordReviewer,
      filler: formData.filler
    }
    
    const response = await axios.post('/api/water-replacement/save', submitData)
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
      const currentAccount = user.username
      const currentName = user.fullName || user.nickName || currentAccount

      // Match Tester
      if (formData.recordTester === currentName || formData.recordTester === currentAccount || !formData.recordTester) {
          if (!formData.recordTester) {
              formData.recordTester = currentName
          }
          formData.testerSignature = imgSrc
          signed = true
      }

      if (signed) {
        alert('签名成功')
      } else {
        alert(`当前用户(${currentName})与表单中的试验人员不匹配，无法签名`)
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
    navigateTo('WaterReplacementRecordList');
  }
}

const generatePdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/water_replacement_record/generate'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}

const previewPdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/water_replacement_record/preview'
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

        .waterReplacementRecord-container {
            font-family: 'SimSun', 'Songti SC', serif;
            width: 260mm; /* Using wider width similar to sand replacement record */
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
            flex-wrap: wrap;
        }
        .header-info span {
            margin-right: 20px;
            margin-bottom: 5px;
            white-space: nowrap;
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
            font-size: 14px;
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
            .waterReplacementRecord-container {
                width: 100%;
                margin: 0;
                padding: 0;
            }
            .no-print {
                display: none;
            }
        }
    
</style>
