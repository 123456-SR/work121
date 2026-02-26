<template>
  <div class="sandReplacementRecord-container">


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
    <h2>原位密度检测记录表（灌砂法）</h2>

    <div class="header-info">
        <span>单元工程名称：<input type="text" v-model="formData.projectName"   name="projectName" style="width: 200px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>试验日期：<input type="text" v-model="formData.testDate"   name="testDate" style="width: 150px; border-bottom: 1px solid black;"></span>
        <span>统一编号：<input type="text" v-model="formData.unifiedNumber"   name="unifiedNumber" style="width: 150px; border-bottom: 1px solid black;"></span>
    </div>
     <div class="header-info">
        <span>依据标准：<input type="text" v-model="formData.standard"   name="standard" style="width: 150px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>最大干密度 (g/cm³)：<input type="text" v-model="formData.maxDryDensity"   name="maxDryDensity" style="width: 80px; border-bottom: 1px solid black;"></span>
        <span>最优含水率 (%)：<input type="text" v-model="formData.optMoisture"   name="optMoisture" style="width: 80px; border-bottom: 1px solid black;"></span>
        <span>最小干密度 (g/cm³)：<input type="text" v-model="formData.minDryDensity"   name="minDryDensity" style="width: 80px; border-bottom: 1px solid black;"></span>
    </div>
    <div class="header-info">
        <span>量砂密度：<input type="text" v-model="formData.sandDensity"   name="sandDensity" style="width: 80px; border-bottom: 1px solid black;"> g/cm³</span>
        <span>仪器设备：<input type="text" v-model="formData.equipment"   name="equipment" style="width: 150px; border-bottom: 1px solid black;"></span>
        <span>检测类别：<input type="text" v-model="formData.testCategory"   name="testCategory" style="width: 100px; border-bottom: 1px solid black;"></span>
        <span>设计压实度：<input type="text" v-model="formData.designCompaction"   name="designCompaction" style="width: 100px; border-bottom: 1px solid black;"></span>
    </div>


    <table>
        <!-- Table Header -->
        <tr>
            <td colspan="2" class="label" style="width: 20%;">检测部位<br>(桩号、高程)</td>
            <template v-for="(n, i_idx) in 4" :key="i_idx">
            <td colspan="4"><input type="text" :name="'location_' + i_idx" v-model="formData['location_' + i_idx]"></td>
            </template>
        </tr>

        <!-- Data Rows -->
        <tr>
            <td class="label">量砂容器+原有砂质量</td>
            <td class="label">g</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'totalSandMass_' + i_idx" v-model="formData['totalSandMass_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td class="label">量砂容器+剩余砂质量</td>
            <td class="label">g</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'remainingSandMass_' + i_idx" v-model="formData['remainingSandMass_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td class="label">锥体内砂质量</td>
            <td class="label">g</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'coneSandMass_' + i_idx" v-model="formData['coneSandMass_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td class="label">试坑耗砂质量</td>
            <td class="label">g</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'pitSandMass_' + i_idx" v-model="formData['pitSandMass_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td class="label">试坑体积</td>
            <td class="label">cm³</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'pitVolume_' + i_idx" v-model="formData['pitVolume_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td class="label">试样质量</td>
            <td class="label">g</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'sampleMass_' + i_idx" v-model="formData['sampleMass_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td class="label">试样湿密度</td>
            <td class="label">g/cm³</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'wetDensity_' + i_idx" v-model="formData['wetDensity_' + i_idx]"></td>
            </template>
        </tr>

        <!-- Moisture Content Section -->
        <tr>
            <td rowspan="6" class="label">试样含水率</td>
            <td class="label">盒号</td>
            <template v-for="(n, i_idx) in 16" :key="i_idx">
            <td><input type="text" :name="'boxNo_' + i_idx" v-model="formData['boxNo_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td class="label">盒质量 g</td>
            <template v-for="(n, i_idx) in 16" :key="i_idx">
            <td><input type="text" :name="'boxMass_' + i_idx" v-model="formData['boxMass_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td class="label">盒+湿样质量 g</td>
            <template v-for="(n, i_idx) in 16" :key="i_idx">
            <td><input type="text" :name="'boxWetMass_' + i_idx" v-model="formData['boxWetMass_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td class="label">盒+干样质量 g</td>
            <template v-for="(n, i_idx) in 16" :key="i_idx">
            <td><input type="text" :name="'boxDryMass_' + i_idx" v-model="formData['boxDryMass_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td class="label">含水率 %</td>
            <template v-for="(n, i_idx) in 16" :key="i_idx">
            <td><input type="text" :name="'moistureContent_' + i_idx" v-model="formData['moistureContent_' + i_idx]"></td>
            </template>
        </tr>
         <tr>
            <td class="label">平均含水率 %</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'avgMoisture_' + i_idx" v-model="formData['avgMoisture_' + i_idx]"></td>
            </template>
        </tr>


        <!-- Results -->
        <tr>
            <td colspan="2" class="label">实测干密度 g/cm³</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'dryDensity_' + i_idx" v-model="formData['dryDensity_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td colspan="2" class="label">平均实测干密度 g/cm³</td>
            <template v-for="(n, i_idx) in 4" :key="i_idx">
            <td colspan="4"><input type="text" :name="'avgDryDensity_' + i_idx" v-model="formData['avgDryDensity_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td colspan="2" class="label">实测压实度</td>
            <template v-for="(n, i_idx) in 4" :key="i_idx">
            <td colspan="4"><input type="text" :name="'compaction_' + i_idx" v-model="formData['compaction_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td colspan="2" class="label">备注</td>
            <td colspan="16" class="left-align"><input type="text" v-model="formData.remarks"   name="remarks"></td>
        </tr>
    </table>

    <div class="footer-info">

        <span style="position: relative;">
            审核：<input type="text" v-model="formData.reviewer" name="reviewer" style="width: 100px; border-bottom: 1px solid black;" readonly>
        </span>
        <span style="position: relative;">
            试验：<input type="text" v-model="formData.tester" name="tester" style="width: 100px; border-bottom: 1px solid black;">
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
  },
  draftMode: {
    type: Boolean,
    default: false
  }
})

const draftMode = computed(() => props.draftMode)

const pdfForm = ref(null)

// Multi-record support
const records = ref([])
const currentIndex = ref(0)
const totalRecords = computed(() => records.value.length)

const formData = reactive({
  id: '',
  entrustmentId: '',
  projectName: '',
  testDate: '',
  unifiedNumber: '',
  standard: '',
  maxDryDensity: '',
  optMoisture: '',
  minDryDensity: '',
  sandDensity: '',
  equipment: '',
  testCategory: '',
  designCompaction: '',
  reviewer: '',
  tester: '',
  remarks: '',
  reviewerSignature: '',
  testerSignature: '',
  status: 0
})

// Status Text Helper
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
        signatureData = formData.testerSignature
    }


    const request = {
        tableType: 'SAND_REPLACEMENT',
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
            // Reload data to reflect status change
            loadData(formData.entrustmentId || props.id)
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
  for (let i = 0; i < 16; i++) {
    formData[`totalSandMass_${i}`] = ''
    formData[`location_${i}`] = ''
    formData[`boxMass_${i}`] = ''
    formData[`avgDryDensity_${i}`] = ''
    formData[`pitVolume_${i}`] = ''
    formData[`compaction_${i}`] = ''
    formData[`moistureContent_${i}`] = ''
    formData[`boxNo_${i}`] = ''
    formData[`pitSandMass_${i}`] = ''
    formData[`wetDensity_${i}`] = ''
    formData[`avgMoisture_${i}`] = ''
    formData[`dryDensity_${i}`] = ''
    formData[`coneSandMass_${i}`] = ''
    formData[`boxWetMass_${i}`] = ''
    formData[`remainingSandMass_${i}`] = ''
    formData[`boxDryMass_${i}`] = ''
    formData[`sampleMass_${i}`] = ''
  }
}
initDynamicFields()

const mapRecordToFormData = (record) => {
  // Clear dynamic fields first
  initDynamicFields()

  formData.id = record.id || ''
  formData.entrustmentId = record.entrustmentId || props.id
  
  // Signatures
  formData.reviewerSignature = record.reviewSignaturePhoto || ''
  formData.testerSignature = record.inspectSignaturePhoto || ''
  
  // 人员账号 / 名称（兼容多种字段，与核子法逻辑保持一致）
  formData.reviewer = record.reviewer || record.REVIEWER || record.recordReviewer || record.RECORD_REVIEWER || ''
  formData.tester = record.tester || record.TESTER || record.recordTester || record.RECORD_TESTER || ''

  // Map fields from BusinessEntity/Entrustment (Always map these first as defaults)
  if (record.projectName) formData.projectName = record.projectName
  if (record.wtNum) formData.unifiedNumber = record.wtNum
  if (record.commissionDate) formData.testDate = record.commissionDate
  if (record.entrustmentId) formData.unifiedNumber = record.entrustmentId
  if (record.clientUnit) formData.entrustingUnit = record.clientUnit
  if (record.constructionPart) formData.constructionPart = record.constructionPart
  if (record.testCategory) formData.testCategory = record.testCategory
  if (record.equipment) formData.equipment = record.equipment
  if (record.testBasis) formData.standard = record.testBasis

  if (record.dataJson) {
    try {
      const parsed = JSON.parse(record.dataJson)
      Object.keys(parsed).forEach(key => {
        // 不允许 JSON 里的旧 status 覆盖当前数据库状态
        if (key === 'status') return
        formData[key] = parsed[key]
      })
      // Specific fields mapping if needed
      if (parsed.projectName) formData.projectName = parsed.projectName
      if (parsed.unifiedNumber) formData.unifiedNumber = parsed.unifiedNumber
      if (parsed.testDate) formData.testDate = parsed.testDate
      if (parsed.standard) formData.standard = parsed.standard
      if (parsed.equipment) formData.equipment = parsed.equipment
      if (parsed.testCategory) formData.testCategory = parsed.testCategory
    } catch (e) {
      console.error('JSON parse error', e)
    }
  }
  
  if (record.status !== undefined) {
      formData.status = Number(record.status)
  } else {
      formData.status = 0
  }
}

const saveCurrentRecordState = () => {
    if (records.value.length > 0 && currentIndex.value >= 0 && currentIndex.value < records.value.length) {
        const current = records.value[currentIndex.value]
        current.dataJson = JSON.stringify(formData)
        current.reviewer = formData.reviewer
        current.tester = formData.tester
        current.reviewSignaturePhoto = formData.reviewerSignature
        current.inspectSignaturePhoto = formData.testerSignature
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

    let newRecord = {
        id: null,
        entrustmentId: wtNum,
        projectName: entrustmentData.projectName || '',
        unifiedNumber: wtNum || '',
        testDate: '',
        standard: '',
        equipment: '',
        testCategory: entrustmentData.testCategory || '',
        tester: '',
        reviewer: '',
    }
    
    // If we have previous records, copy some fields
    if (records.value.length > 0) {
        const last = records.value[records.value.length - 1]
        let lastData = {}
        try { lastData = JSON.parse(last.dataJson || '{}') } catch(e){}
        
        newRecord.projectName = lastData.projectName || last.projectName || newRecord.projectName
        newRecord.unifiedNumber = lastData.unifiedNumber || last.wtNum || newRecord.unifiedNumber
        newRecord.testDate = lastData.testDate || ''
        newRecord.standard = lastData.standard || ''
        newRecord.equipment = lastData.equipment || ''
        newRecord.testCategory = lastData.testCategory || last.testCategory || newRecord.testCategory
        
        newRecord.tester = last.tester || ''
        newRecord.reviewer = last.reviewer || ''
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
            const res = await axios.post('/api/sand-replacement/delete', { id: current.id })
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
        await addRecord()
    } else {
        if (currentIndex.value >= records.value.length) {
            currentIndex.value = records.value.length - 1
        }
        mapRecordToFormData(records.value[currentIndex.value])
    }
}

onMounted(() => {
  // initDynamicFields is called inside mapRecordToFormData, but we can call it here too if needed
  // initDynamicFields()
  
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

// 从目录流程中获取记录表角色配置（记录检测人/记录审核人），并打印调试信息
const loadProcessRoles = async (wtNum) => {
  try {
    if (!wtNum) return
    console.log('正在获取流程信息，wtNum:', wtNum)
    const processResponse = await axios.post('/api/directory/get-by-dirname', {
      dirName: wtNum
    })
    console.log('流程信息响应:', processResponse.data)
    if (processResponse.data.success && processResponse.data.data) {
      const processData = processResponse.data.data
      console.log('流程数据:', processData)
      console.log('流程中的jcTester:', processData.jcTester)
      console.log('流程中的jcReviewer:', processData.jcReviewer)
      console.log('当前的formData.tester:', formData.tester)
      console.log('当前的formData.reviewer:', formData.reviewer)

      // 如果表单里还没带人，就用流程里配置的记录检测人/记录审核人补上
      if (processData.jcTester && !formData.tester) {
        formData.tester = processData.jcTester
        console.log('设置formData.tester为:', processData.jcTester)
      }
      if (processData.jcReviewer && !formData.reviewer) {
        formData.reviewer = processData.jcReviewer
        console.log('设置formData.reviewer为:', processData.jcReviewer)
      }

      console.log('最终formData.tester:', formData.tester)
      console.log('最终formData.reviewer:', formData.reviewer)
    }
  } catch (error) {
    console.error('Failed to load process information:', error)
  }
}

// 加载数据
const loadData = async (entrustmentId) => {
  try {
    const response = await axios.get(`/api/sand-replacement/get-by-entrustment-id`, {
        params: { entrustmentId: entrustmentId }
    })
    
    if (response.data.success) {
        if (response.data.data && Array.isArray(response.data.data) && response.data.data.length > 0) {
            records.value = response.data.data
            
             // Find record by ID if props.id is provided and it's not the entrustmentId
             let foundIndex = 0
             if (props.id && props.id !== entrustmentId) {
                   const idx = records.value.findIndex(r => r.id === props.id)
                   if (idx !== -1) foundIndex = idx
             }
             currentIndex.value = foundIndex
            mapRecordToFormData(records.value[foundIndex])
            await loadProcessRoles(entrustmentId)
        } else {
            // Check if it returned a single object (legacy support or API behavior check)
            if (response.data.data && !Array.isArray(response.data.data) && response.data.data.id) {
                 records.value = [response.data.data]
                 currentIndex.value = 0
                 mapRecordToFormData(records.value[0])
                 await loadProcessRoles(entrustmentId)
            } else {
                // No records
                records.value = []
                await addRecord()
                await loadProcessRoles(entrustmentId)
            }
        }
    } else {
        records.value = []
        await addRecord()
        await loadProcessRoles(entrustmentId)
    }
  } catch (error) {
    console.error('Load error', error)
    records.value = []
    await addRecord()
    await loadProcessRoles(entrustmentId)
  }
}

const submitForm = async () => {
    try {
        const dataToSave = {
            id: formData.id,
            entrustmentId: formData.entrustmentId || props.id,
            dataJson: JSON.stringify(formData),
            approveSignaturePhoto: formData.approverSignature,
            reviewSignaturePhoto: formData.reviewerSignature,
            inspectSignaturePhoto: formData.testerSignature,
            tester: formData.tester,
            reviewer: formData.reviewer,
            approver: formData.approver,
            
            // Map entity fields
            soilType: formData.soilType, // ensure this exists in formData if used
            // other fields...
        }
        
        const response = await axios.post('/api/sand-replacement/save', dataToSave)
        if (response.data.success) {
            alert('保存成功')
            if (response.data.data) {
                const saved = response.data.data
                records.value[currentIndex.value] = saved
                mapRecordToFormData(saved)
            }
        } else {
            alert('保存失败: ' + response.data.message)
        }
    } catch (e) {
        console.error('Save error', e)
        alert('保存失败')
    }
}

const handleSign = async () => {
  const user = JSON.parse(localStorage.getItem('userInfo'))
  if (!user) {
    alert('请先登录')
    return
  }

  // 和核子法保持一致：优先按账号匹配
  const currentAccount = user.username || user.userAccount || user.userName
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

      // 记录表检测人：支持多种字段（账号），与核子法逻辑保持一致
      if (
        formData.tester === currentAccount ||
        formData.TESTER === currentAccount ||
        formData.recordTester === currentAccount ||
        formData.RECORD_TESTER === currentAccount
      ) {
        formData.testerSignature = imgSrc
        signed = true
      }

      // 记录表审核人签字：要求检测人已签
      if (
        formData.reviewer === currentAccount ||
        formData.REVIEWER === currentAccount ||
        formData.recordReviewer === currentAccount ||
        formData.RECORD_REVIEWER === currentAccount
      ) {
        if (!formData.testerSignature) {
          alert('检测人尚未签字，审核人无法签字')
          return
        }
        formData.reviewerSignature = imgSrc
        signed = true
      }
      
      if (signed) {
        alert('签名成功')
      } else {
        const currentRealName = user.fullName || currentAccount
        const formPerson =
          formData.tester ||
          formData.TESTER ||
          formData.reviewer ||
          formData.REVIEWER ||
          formData.recordTester ||
          formData.recordReviewer ||
          formData.RECORD_TESTER ||
          formData.RECORD_REVIEWER ||
          ''
        alert(`当前用户(${currentAccount}/${currentRealName})与表单中的人员(${formPerson})不匹配，无法签名`)
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
    navigateTo('SandReplacementRecordList')
  }
}

const generatePdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/sand_replacement_record/generate'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}

const previewPdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/sand_replacement_record/preview'
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

        .sandReplacementRecord-container {
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
        }
        .header-info span {
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
            justify-content: space-around;
            margin-top: 20px;
            margin-bottom: 10px;
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
            .sandReplacementRecord-container {
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
