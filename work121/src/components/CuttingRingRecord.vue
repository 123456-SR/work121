<template>
  <div class="cuttingRingRecord-container">


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
          @click="saveData"
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
    <h2>原位密度检测记录表（环刀法）</h2>

    <div class="header-info">
        <span>委托单位：<input type="text" v-model="formData.entrustingUnit"   name="entrustingUnit" style="width: 200px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>工程名称：<input type="text" v-model="formData.projectName"   name="projectName" style="width: 200px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>统一编号：<input type="text" v-model="formData.unifiedNumber"   name="unifiedNumber" style="width: 150px; border-bottom: 1px solid black;"></span>
    </div>
    <div class="header-info">
        <span>施工部位：<input type="text" v-model="formData.constructionLocation"   name="constructionLocation" style="width: 200px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>最大干密度 (g/cm³)：<input type="text" v-model="formData.maxDryDensity"   name="maxDryDensity" style="width: 80px; border-bottom: 1px solid black;"></span>
        <span>最优含水率 (%)：<input type="text" v-model="formData.optMoisture"   name="optMoisture" style="width: 80px; border-bottom: 1px solid black;"></span>
        <span>检测类别：<input type="text" v-model="formData.testType"   name="testType" style="width: 100px; border-bottom: 1px solid black;"></span>
    </div>
    <div class="header-info">
        <span>依据标准：<input type="text" v-model="formData.standard"   name="standard" style="width: 200px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>设计压实度：<input type="text" v-model="formData.designCompaction"   name="designCompaction" style="width: 80px; border-bottom: 1px solid black;"></span>
        <span>检测日期：<input type="text" v-model="formData.testDate"   name="testDate" style="width: 150px; border-bottom: 1px solid black;"></span>
    </div>

    <table>
        <thead>
            <tr>
                <td rowspan="2" class="label" style="width: 5%;">样品<br>编号</td>
                <td rowspan="2" class="label" style="width: 10%;">检测部位<br>(桩号、<br>高程)</td>
                <td rowspan="2" class="label" style="width: 5%;">样品<br>状态</td>
                <td rowspan="2" class="label">环刀<br>号</td>
                <td rowspan="2" class="label">环刀<br>质量g</td>
                <td rowspan="2" class="label">环刀+湿<br>土质量g</td>
                <td rowspan="2" class="label">环刀体<br>积cm³</td>
                <td colspan="5" class="label">含水率测定</td>
                <td rowspan="2" class="label">平均<br>含水<br>率%</td>
                <td rowspan="2" class="label">湿密度<br>g/cm³</td>
                <td rowspan="2" class="label">干密度<br>g/cm³</td>
                <td rowspan="2" class="label">平均干<br>密度<br>g/cm³</td>
                <td rowspan="2" class="label">压实<br>度%</td>
            </tr>
            <tr>
                <td class="label">盒号</td>
                <td class="label">盒质量<br>g</td>
                <td class="label">盒+湿土<br>质量g</td>
                <td class="label">盒+干土<br>质量g</td>
                <td class="label">含水率<br>%</td>
            </tr>
        </thead>
        <tbody>
            <template v-for="(n, i_idx) in 5" :key="i_idx">
            <tr>
                <td rowspan="2"><input type="text" :name="'sampleNo_' + i_idx" v-model="formData['sampleNo_' + i_idx]"></td>
                <td rowspan="2"><input type="text" :name="'location_' + i_idx" v-model="formData['location_' + i_idx]"></td>
                <td rowspan="2"><input type="text" :name="'status_' + i_idx" v-model="formData['status_' + i_idx]"></td>
                <td rowspan="2"><input type="text" :name="'ringNo_' + i_idx" v-model="formData['ringNo_' + i_idx]"></td>
                <td rowspan="2"><input type="text" :name="'ringMass_' + i_idx" v-model="formData['ringMass_' + i_idx]"></td>
                <td rowspan="2"><input type="text" :name="'ringWetMass_' + i_idx" v-model="formData['ringWetMass_' + i_idx]"></td>
                <td rowspan="2"><input type="text" :name="'ringVolume_' + i_idx" v-model="formData['ringVolume_' + i_idx]"></td>
                
                <!-- Moisture Box 1 -->
                <td><input type="text" :name="'boxNo1_' + i_idx" v-model="formData['boxNo1_' + i_idx]"></td>
                <td><input type="text" :name="'boxMass1_' + i_idx" v-model="formData['boxMass1_' + i_idx]"></td>
                <td><input type="text" :name="'boxWetMass1_' + i_idx" v-model="formData['boxWetMass1_' + i_idx]"></td>
                <td><input type="text" :name="'boxDryMass1_' + i_idx" v-model="formData['boxDryMass1_' + i_idx]"></td>
                <td><input type="text" :name="'moisture1_' + i_idx" v-model="formData['moisture1_' + i_idx]"></td>

                <td rowspan="2"><input type="text" :name="'avgMoisture_' + i_idx" v-model="formData['avgMoisture_' + i_idx]"></td>
                <td rowspan="2"><input type="text" :name="'wetDensity_' + i_idx" v-model="formData['wetDensity_' + i_idx]"></td>
                <td rowspan="2"><input type="text" :name="'dryDensity_' + i_idx" v-model="formData['dryDensity_' + i_idx]"></td>
                <td rowspan="2"><input type="text" :name="'avgDryDensity_' + i_idx" v-model="formData['avgDryDensity_' + i_idx]"></td>
                <td rowspan="2"><input type="text" :name="'compaction_' + i_idx" v-model="formData['compaction_' + i_idx]"></td>
            </tr>
            <tr>
                <!-- Moisture Box 2 -->
                <td><input type="text" :name="'boxNo2_' + i_idx" v-model="formData['boxNo2_' + i_idx]"></td>
                <td><input type="text" :name="'boxMass2_' + i_idx" v-model="formData['boxMass2_' + i_idx]"></td>
                <td><input type="text" :name="'boxWetMass2_' + i_idx" v-model="formData['boxWetMass2_' + i_idx]"></td>
                <td><input type="text" :name="'boxDryMass2_' + i_idx" v-model="formData['boxDryMass2_' + i_idx]"></td>
                <td><input type="text" :name="'moisture2_' + i_idx" v-model="formData['moisture2_' + i_idx]"></td>
            </tr>
            </template>
            <!-- Empty rows to fill space if needed, or just these 5 sample blocks (10 rows) -->
        </tbody>
        <tfoot>
            <tr>
                <td colspan="2" class="label">备注</td>
                <td colspan="15" class="left-align" style="height: 60px; vertical-align: top;">
                    <textarea v-model="formData.remarks"  name="remarks" style="width: 100%; height: 100%;"></textarea>
                </td>
            </tr>
        </tfoot>
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
import { reactive, ref, onMounted, inject, defineProps, computed } from 'vue'
import axios from 'axios'

const props = defineProps({
  id: {
    type: String,
    required: false
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

const goToList = () => {
  if (navigateTo) {
    navigateTo('CuttingRingRecordList')
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
      entrustingUnit: '',
      projectName: '',
      unifiedNumber: '',
      constructionLocation: '',
      maxDryDensity: '',
      optMoisture: '',
      testType: '',
      standard: '',
      designCompaction: '',
      testDate: '',
      remarks: '',
      // Role fields
      recordTester: '',
      recordReviewer: '',
      filler: '',
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

    try {
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
        signatureData = formData.testerSignature.replace(/^data:image\/\w+;base64,/, '')
    }

    const request = {
        tableType: 'CUTTING_RING',
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

    if (action === 'AUDIT_PASS') {
        // Role check: Only recordReviewer can audit
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
        request.signatureData = signatureData
    }
    
    // Add SIGN_APPROVE support if needed (though CuttingRing might use AUDIT_PASS for approval flow depending on implementation, 
    // but assuming standard flow: SUBMIT -> AUDIT_PASS -> APPROVED. If there is a separate approval step, it should be handled.
    // Based on other files, often AUDIT_PASS leads to status 4 or 5.
    // Cutting Ring code shows: AUDIT_PASS -> 5 (if status 1->5? No, previous code didn't show next status logic explicitly in submitWorkflow, 
    // it relies on backend or nextHandler? 
    // Wait, the original code had:
    // else if (action === 'REJECT') ...
    
    // Let's just fix the REJECT check for now.
    
    if (action === 'REJECT') {
        // Role check: Only recordReviewer can reject
        if (formData.recordReviewer && user.username !== formData.recordReviewer && user.fullName !== formData.recordReviewer) {
            alert('您不是该单据的记录复核人 (' + formData.recordReviewer + ')，无权操作')
            return
        }
    }
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

// Initialize dynamic fields
const initDynamicFields = () => {
  for (let i_idx = 0; i_idx < 50; i_idx++) {
    formData['location_' + i_idx] = ''
    formData['boxMass2_' + i_idx] = ''
    formData['boxWetMass2_' + i_idx] = ''
    formData['ringWetMass_' + i_idx] = ''
    formData['ringMass_' + i_idx] = ''
    formData['boxDryMass2_' + i_idx] = ''
    formData['moisture1_' + i_idx] = ''
    formData['ringNo_' + i_idx] = ''
    formData['compaction_' + i_idx] = ''
    formData['boxWetMass1_' + i_idx] = ''
    formData['boxNo2_' + i_idx] = ''
    formData['boxDryMass1_' + i_idx] = ''
    formData['sampleNo_' + i_idx] = ''
    formData['wetDensity_' + i_idx] = ''
    formData['boxNo1_' + i_idx] = ''
    formData['dryDensity_' + i_idx] = ''
    formData['avgDryDensity_' + i_idx] = ''
    formData['ringVolume_' + i_idx] = ''
    formData['avgMoisture_' + i_idx] = ''
    formData['boxMass1_' + i_idx] = ''
    formData['moisture2_' + i_idx] = ''
    formData['status_' + i_idx] = ''
  }
}

onMounted(() => {
  initDynamicFields()
  
  let wtNum = props.wtNum
  if (!wtNum) {
      const urlParams = new URLSearchParams(window.location.search)
      wtNum = urlParams.get('wtNum') || urlParams.get('id')
  }

  if (wtNum) {
      loadData(wtNum)
  } else if (props.id) {
      loadData(props.id)
  }
})

const formatDate = (dateVal) => {
  if (!dateVal) return ''
  const d = new Date(dateVal)
  if (isNaN(d.getTime())) return ''
  const year = d.getFullYear()
  const month = ('0' + (d.getMonth() + 1)).slice(-2)
  const day = ('0' + d.getDate()).slice(-2)
  return `${year}-${month}-${day}`
}

const mapRecordToFormData = (record) => {
  // Clear existing dynamic fields first
  initDynamicFields()
  
  formData.id = record.id || ''
  formData.entrustmentId = record.entrustmentId || props.id
  formData.status = record.status !== undefined ? record.status : 0
  formData.reviewerSignature = normalizeSignatureSrc(record.reviewSignaturePhoto || '')
  formData.testerSignature = normalizeSignatureSrc(record.inspectSignaturePhoto || '')

  if (record.dataJson) {
    try {
      const parsed = JSON.parse(record.dataJson)
      // Merge parsed data into formData
      Object.keys(parsed).forEach(key => {
        formData[key] = parsed[key]
      })
      
      // Legacy mapping
      if (parsed.tester && !formData.recordTester) formData.recordTester = parsed.tester
      if (parsed.reviewer && !formData.recordReviewer) formData.recordReviewer = parsed.reviewer
    } catch (e) {
      console.error('JSON parse error', e)
    }
  }

  // Map fields from BusinessEntity/Entrustment (Override JSON to ensure sync)
  if (record.clientUnit) formData.entrustingUnit = record.clientUnit
  if (record.projectName) formData.projectName = record.projectName
  if (record.wtNum) formData.unifiedNumber = record.wtNum
  if (record.entrustmentId) formData.unifiedNumber = record.entrustmentId // Fallback

  if (record.constructionPart) formData.constructionLocation = record.constructionPart
  if (record.testCategory) formData.testType = record.testCategory
  if (record.standard) formData.standard = record.standard
  if (record.testDate) formData.testDate = formatDate(record.testDate)
  if (record.maxDryDensity) formData.maxDryDensity = record.maxDryDensity
  if (record.optMoisture) formData.optMoisture = record.optMoisture
  if (record.designCompaction) formData.designCompaction = record.designCompaction

  // Ensure entity fields override JSON if present
  if (record.reviewSignaturePhoto) formData.reviewerSignature = normalizeSignatureSrc(record.reviewSignaturePhoto)
  if (record.inspectSignaturePhoto) formData.testerSignature = normalizeSignatureSrc(record.inspectSignaturePhoto)

  // Map Roles
  // Load defaults from directory if available
  const directory = JSON.parse(localStorage.getItem('currentDirectory') || '{}')
  
  // Filler - Priority: record.filler
  formData.filler = record.filler || ''
  
  // Record Tester - Priority: record.recordTester -> record.tester
  formData.recordTester = record.recordTester || record.tester || ''
  
  // Record Reviewer - Priority: record.recordReviewer -> record.reviewer (legacy)
  if (record.recordReviewer || record.reviewer) {
    formData.recordReviewer = record.recordReviewer || record.reviewer
  }
}

const saveCurrentRecordState = () => {
  if (records.value.length === 0) return
  
  const record = records.value[currentIndex.value]
  record.id = formData.id
  record.entrustmentId = formData.entrustmentId
  record.reviewSignaturePhoto = formData.reviewerSignature
  record.inspectSignaturePhoto = formData.testerSignature
  
  record.recordTester = formData.recordTester
  record.recordReviewer = formData.recordReviewer
  record.tester = formData.recordTester // Sync legacy
  record.reviewer = formData.recordReviewer // Sync legacy
  
  // Update dataJson with current formData state
  // Exclude legacy fields from JSON
  const dataJsonObj = { ...formData }
  delete dataJsonObj.tester
  delete dataJsonObj.reviewer
  record.dataJson = JSON.stringify(dataJsonObj)
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
    entrustmentId: props.wtNum || props.id || formData.entrustmentId,
    dataJson: '{}',
    status: 0
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
      const response = await axios.post('/api/cutting-ring/delete', { id: currentRecord.id })
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

const loadData = async (paramId) => {
  const idOrWtNum = paramId || props.wtNum || props.id
  if (idOrWtNum) {
    try {
      // 1. Try to fetch existing records (List)
      // Note: We use idOrWtNum as entrustmentId. The backend should support querying by wtNum if it's stored as such
      const response = await axios.get('/api/cutting-ring/get-by-entrustment-id', {
        params: { entrustmentId: idOrWtNum }
      })

      if (response.data.success && response.data.data && response.data.data.length > 0) {
        records.value = response.data.data
        currentIndex.value = 0
        mapRecordToFormData(records.value[0])
      } else {
        // 2. If no record, create one and fetch entrustment info
        // Try fetching by unifiedNumber (wtNum)
        const entrustmentResponse = await axios.get('/api/jc-core-wt-info/detail', {
          params: { unifiedNumber: idOrWtNum }
        })
        
        const newRecord = {
          id: '',
          entrustmentId: idOrWtNum,
          dataJson: '{}'
        }
        
        if (entrustmentResponse.data.success) {
          const eData = entrustmentResponse.data.data
          // Pre-fill some data into formData then save to newRecord
          formData.entrustmentId = idOrWtNum
          formData.constructionLocation = eData.constructionPart || ''
          formData.testDate = new Date().toISOString().split('T')[0]
          // Add other fields
          formData.testType = eData.testCategory || ''
          formData.standard = eData.standard || ''
          
          // Auto-fill roles from directory (Removed)
          // const directory = JSON.parse(localStorage.getItem('currentDirectory') || '{}')
          formData.recordTester = ''
          formData.recordReviewer = ''
          // formData.approver = ''
          
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
    const dataJsonObj = { ...formData }
    delete dataJsonObj.tester
    delete dataJsonObj.reviewer

    const dataToSave = {
      id: formData.id,
      entrustmentId: formData.entrustmentId || props.id,
      dataJson: JSON.stringify(dataJsonObj),
      // Roles
      recordTester: formData.recordTester,
      recordReviewer: formData.recordReviewer,
      tester: formData.recordTester, // Sync legacy
      reviewer: formData.recordReviewer, // Sync legacy
      filler: formData.filler,
      
      // Signatures
      testerSignature: formData.testerSignature,
      reviewerSignature: formData.reviewerSignature
    }
    
    const response = await axios.post('/api/cutting-ring/save', dataToSave)
    if (response.data.success) {
      alert('保存成功')
      // If new record, update ID
      if (!formData.id && response.data.data && response.data.data.id) {
           formData.id = response.data.data.id
      }
      // If the backend returns a message or data, handle it
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

const generatePdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/cutting_ring_record/generate'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}

const previewPdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/cutting_ring_record/preview'
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

        .cuttingRingRecord-container {
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
            .cuttingRingRecord-container {
                width: 100%;
                margin: 0;
                padding: 0;
            }
            .no-print {
                display: none;
            }
        }
    
</style>
