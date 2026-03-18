<template>
  <div class="densityTestResult-container" ref="containerRef">


    <div class="no-print toolbar">
      <div class="toolbar-left">
        <button @click="goToList" class="link-button">&lt; 返回列表</button>
        <span class="record-nav">
          <button
            @click="prevRecord"
            :disabled="totalRecords <= 0 || currentIndex <= 0"
            class="btn btn-secondary btn-small"
          >
            上一页
          </button>
          <span class="record-nav-info">
            记录 {{ totalRecords > 0 ? currentIndex + 1 : 0 }} / {{ totalRecords }}
          </span>
          <button
            @click="nextRecord"
            :disabled="totalRecords <= 0 || currentIndex >= totalRecords - 1"
            class="btn btn-secondary btn-small"
          >
            下一页
          </button>
          <button
            class="btn btn-primary btn-small"
            disabled
          >
            添加记录
          </button>
          <button
            :disabled="true"
            class="btn btn-danger btn-small"
          >
            删除当前记录
          </button>
        </span>
      </div>
      <div class="toolbar-right">
        <span class="status-text" v-if="formData.status !== undefined">
          状态:
          <span :style="{ color: getStatusColor(formData.status) }" class="status-label">
            {{ getStatusText(formData.status) }}
          </span>
        </span>
        <button
          @click="approveAndSave"
          class="btn btn-primary btn-small"
        >
          批准
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
    <h2>原位密度检测结果</h2>

    <div class="header-info" style="justify-content: space-between;">
        <span>统一编号：<input type="text" v-model="formData.unifiedNumber"   name="unifiedNumber" style="width: 150px; border-bottom: 1px solid black; text-align: left;" disabled></span>
        <span></span> <!-- Placeholder for layout balance if needed -->
    </div>

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

        <tr v-if="isNuclearMethod">
            <td class="label" style="width: 10%;">样品编号</td>
            <td class="label" style="width: 25%;" colspan="2">检测部位<br>(桩号、高程)</td>
            <td class="label" style="width: 15%;" colspan="2">检测日期</td>
            <td class="label" style="width: 12.5%;">湿密度<br>(g/cm³)</td>
            <td class="label" style="width: 12.5%;">干密度<br>(g/cm³)</td>
            <td class="label" style="width: 12.5%;">含水率<br>%</td>
            <td class="label" style="width: 12.5%;" colspan="2">压实度%</td>
        </tr>
        <tr v-else>
            <td class="label" style="width: 10%;">样品编号</td>
            <td class="label" style="width: 25%;" colspan="2">检测部位<br>(桩号、高程)</td>
            <td class="label" style="width: 15%;" colspan="2">检测日期</td>
            <td class="label" style="width: 12.5%;">湿密度<br>(g/cm³)</td>
            <td class="label" style="width: 12.5%;">干密度<br>(g/cm³)</td>
            <td class="label" style="width: 12.5%;">含水率<br>%</td>
            <td class="label" style="width: 12.5%;">平均干密度<br>(g/cm³)</td>
            <td class="label" style="width: 12.5%;">压实度%</td>
        </tr>

        <template v-for="(n, i_idx) in 20" :key="i_idx">
          <tr v-if="isNuclearMethod">
            <td><input type="text" :name="'sampleId_' + i_idx" v-model="formData['sampleId_' + i_idx]"></td>
            <td colspan="2"><input type="text" :name="'location_' + i_idx" v-model="formData['location_' + i_idx]"></td>
            <td colspan="2"><input type="text" :name="'date_' + i_idx" v-model="formData['date_' + i_idx]"></td>
            <td><input type="text" :name="'wetDensity_' + i_idx" v-model="formData['wetDensity_' + i_idx]"></td>
            <td><input type="text" :name="'dryDensity_' + i_idx" v-model="formData['dryDensity_' + i_idx]"></td>
            <td><input type="text" :name="'moisture_' + i_idx" v-model="formData['moisture_' + i_idx]"></td>
            <td colspan="2"><input type="text" :name="'compaction_' + i_idx" v-model="formData['compaction_' + i_idx]"></td>
          </tr>
          <tr v-else>
            <td><input type="text" :name="'sampleId_' + i_idx" v-model="formData['sampleId_' + i_idx]"></td>
            <td colspan="2"><input type="text" :name="'location_' + i_idx" v-model="formData['location_' + i_idx]"></td>
            <td colspan="2"><input type="text" :name="'date_' + i_idx" v-model="formData['date_' + i_idx]"></td>
            <td>
              <div class="two-inputs">
                <input type="text" :name="'wetDensity_' + i_idx" v-model="formData['wetDensity_' + i_idx]">
                <input type="text" :name="'wetDensity2_' + i_idx" v-model="formData['wetDensity2_' + i_idx]">
              </div>
            </td>
            <td>
              <div class="two-inputs">
                <input type="text" :name="'dryDensity_' + i_idx" v-model="formData['dryDensity_' + i_idx]">
                <input type="text" :name="'dryDensity2_' + i_idx" v-model="formData['dryDensity2_' + i_idx]">
              </div>
            </td>
            <td>
              <div class="two-inputs">
                <input type="text" :name="'moisture_' + i_idx" v-model="formData['moisture_' + i_idx]">
                <input type="text" :name="'moisture2_' + i_idx" v-model="formData['moisture2_' + i_idx]">
              </div>
            </td>
            <td><input type="text" :name="'avgDryDensity_' + i_idx" v-model="formData['avgDryDensity_' + i_idx]"></td>
            <td><input type="text" :name="'compaction_' + i_idx" v-model="formData['compaction_' + i_idx]"></td>
          </tr>
        </template>
    </table>

    <div class="footer-info">
            <div class="signature-box">
                批准：
                <span class="signature-line"></span>
                <img v-if="formData.approverSignature" :src="formData.approverSignature" class="signature-img" alt="批准人签名" />
            </div>
            <div class="signature-box">
                审核：
                <span class="signature-line"></span>
                <img v-if="formData.reviewerSignature" :src="formData.reviewerSignature" class="signature-img" alt="审核人签名" />
            </div>
            <div class="signature-box">
                检验：
                <span class="signature-line"></span>
                <img v-if="formData.testerSignature" :src="formData.testerSignature" class="signature-img" alt="检测人签名" />
            </div>
        </div>

    <!-- 隐藏字段：将检测方法/类别同步给后端 PDF，用于判断核子法版式 -->
    <input type="hidden" name="testMethod" :value="formData.testMethod || formData.testCategory" />
    </form>



  </div>
</template>

<script setup>
import { reactive, ref, onMounted, inject, defineProps, computed, watch, nextTick } from 'vue'
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
const containerRef = ref(null)

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
  testMethod: '',
  testCategory: '',
  constructionPart: '',
  maxDryDensity: '',
  optimumMoisture: '',
  minDryDensity: '',
  recordReviewer: '',
  recordTester: '',
  filler: '',
  approver: '',
  reviewerSignature: '',
  testerSignature: '',
  approverSignature: ''
})

const formatDate = (d) => {
    if (!d) return ''
    const date = new Date(d)
    const year = date.getFullYear()
    const month = ('0' + (date.getMonth() + 1)).slice(-2)
    const day = ('0' + date.getDate()).slice(-2)
    return `${year}-${month}-${day}`
}

const normalizeSignatureSrc = (v) => {
  if (!v) return ''
  const s = String(v).trim()
  if (!s) return ''
  if (s.startsWith('data:image')) return s
  if (/^[A-Za-z0-9+/=]+$/.test(s)) return `data:image/png;base64,${s}`
  return s
}

// 根据检测方法/类别自动判断是否为“核子法”
const isNuclearMethod = computed(() => {
  const method = (formData.testMethod || formData.testCategory || '').toString()
  return method.includes('核子')
})

// Initialize dynamic fields
const initDynamicFields = () => {
  for (let i_idx = 0; i_idx < 20; i_idx++) {
    formData['sampleId_' + i_idx] = ''
    formData['location_' + i_idx] = ''
    formData['compaction_' + i_idx] = ''
    formData['avgDryDensity_' + i_idx] = ''
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
  nextTick(() => {
    applyReadOnly()
  })
})

watch(currentIndex, () => {
  nextTick(() => {
    applyReadOnly()
  })
})

const applyReadOnly = () => {
  const root = containerRef.value
  if (!root) return
  root.querySelectorAll('input, textarea').forEach((el) => {
    const type = (el.getAttribute('type') || '').toLowerCase()
    if (type === 'checkbox' || type === 'radio' || type === 'file') {
      el.setAttribute('disabled', 'true')
      return
    }
    el.setAttribute('readonly', 'true')
  })
  root.querySelectorAll('select, button[contenteditable], [contenteditable="true"]').forEach((el) => {
    el.setAttribute('disabled', 'true')
  })
}

const mapRecordToFormData = (record) => {
  initDynamicFields()
  
  formData.id = record.id || ''
  formData.entrustmentId = record.entrustmentId || props.id
  formData.reviewerSignature = normalizeSignatureSrc(record.reviewSignaturePhoto || '')
  formData.testerSignature = normalizeSignatureSrc(record.inspectSignaturePhoto || '')
  formData.approverSignature = normalizeSignatureSrc(record.approveSignaturePhoto || '')
  
  if (record.dataJson) {
    try {
      const parsed = JSON.parse(record.dataJson)
      // Map legacy fields to new record fields if they exist in JSON
      if (parsed.tester && !parsed.recordTester) parsed.recordTester = parsed.tester
      if (parsed.reviewer && !parsed.recordReviewer) parsed.recordReviewer = parsed.reviewer
      
      Object.assign(formData, parsed)
    } catch (e) {
      console.error('JSON parse error', e)
    }
  }
  
  // Explicit fields override JSON
  if (record.reviewSignaturePhoto) formData.reviewerSignature = normalizeSignatureSrc(record.reviewSignaturePhoto)
  if (record.inspectSignaturePhoto) formData.testerSignature = normalizeSignatureSrc(record.inspectSignaturePhoto)
  if (record.approveSignaturePhoto) formData.approverSignature = normalizeSignatureSrc(record.approveSignaturePhoto)
  if (record.entrustmentId) formData.unifiedNumber = record.entrustmentId
  
  if (record.recordTester) formData.recordTester = record.recordTester
  if (record.recordReviewer) formData.recordReviewer = record.recordReviewer
  if (record.filler) formData.filler = record.filler
  if (record.approver) formData.approver = record.approver

  formData.reviewerSignature = normalizeSignatureSrc(formData.reviewerSignature)
  formData.testerSignature = normalizeSignatureSrc(formData.testerSignature)
  formData.approverSignature = normalizeSignatureSrc(formData.approverSignature)
}

// 从委托信息中补充头部字段（只在当前字段为空时填充，避免覆盖用户修改）
const fillHeaderFromEntrustment = async (unifiedNumber) => {
  if (!unifiedNumber) return
  try {
    const resp = await axios.get('/api/jc-core-wt-info/detail', {
      params: { unifiedNumber }
    })
    if (resp.data && resp.data.success && resp.data.data) {
      const eData = resp.data.data
      if (!formData.constructionPart && eData.constructionPart) {
        formData.constructionPart = eData.constructionPart
      }
    }
  } catch (e) {
    console.error('fillHeaderFromEntrustment error', e)
  }
}

const saveCurrentRecordState = () => {
  if (records.value.length === 0) return
  
  const record = records.value[currentIndex.value]
  record.id = formData.id
  record.entrustmentId = formData.entrustmentId
  record.reviewSignaturePhoto = formData.reviewerSignature
  record.inspectSignaturePhoto = formData.testerSignature
  record.approveSignaturePhoto = formData.approverSignature
  
  record.recordTester = formData.recordTester
  record.recordReviewer = formData.recordReviewer
  record.filler = formData.filler
  record.approver = formData.approver
  
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
  const current = records.value[currentIndex.value]
  const newRecord = {
    id: '',
    entrustmentId: props.id,
    recordTester: formData.recordTester || current?.recordTester || '',
    recordReviewer: formData.recordReviewer || current?.recordReviewer || '',
    filler: formData.filler || current?.filler || '',
    approver: formData.approver || current?.approver || '',
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

const normalizeEntrustmentKey = (v) => {
  const s = (v ?? '').toString().trim()
  if (!s) return ''
  if (s.startsWith('{') && s.endsWith('}')) {
    return s.slice(1, -1).trim()
  }
  return s
}

const detectDensityMethodType = (testMethod, testCategory) => {
  const m = (testMethod || testCategory || '').toString()
  if (m.includes('核子')) return 'nuclear'
  if (m.includes('灌砂')) return 'sand'
  if (m.includes('灌水')) return 'water'
  if (m.includes('环刀')) return 'ring'
  return ''
}

const sortByTimeDesc = (list) => {
  const toTime = (v) => {
    if (!v) return Number.NEGATIVE_INFINITY
    const t = new Date(v).getTime()
    return Number.isFinite(t) ? t : Number.NEGATIVE_INFINITY
  }
  return (Array.isArray(list) ? list : []).slice().sort((a, b) => {
    const ua = toTime(a?.updateTime)
    const ub = toTime(b?.updateTime)
    if (ua !== ub) return ub - ua
    const ta = toTime(a?.createTime)
    const tb = toTime(b?.createTime)
    if (ta !== tb) return tb - ta
    return String(b?.id || '').localeCompare(String(a?.id || ''))
  })
}

const pickApprovedRecord = (list) => {
  const sorted = sortByTimeDesc(list)
  const approved = sorted.filter(r => {
    const s = parseInt(r?.status)
    return s === 4 || s === 5 || s === 6
  })
  return approved[0] || sorted[0]
}

const fetchSimpleRecordsForKey = async (apiPath, key) => {
  const primary = normalizeEntrustmentKey(key)
  if (!primary) return []

  try {
    const r1 = await axios.get(apiPath, { params: { entrustmentId: primary } })
    if (r1.data && r1.data.success && Array.isArray(r1.data.data) && r1.data.data.length > 0) {
      return sortByTimeDesc(r1.data.data)
    }
  } catch (e) {
  }

  try {
    const wtResp = await axios.get('/api/jc-core-wt-info/detail', { params: { unifiedNumber: primary } })
    const wtInfo = wtResp?.data?.data
    const wtId = normalizeEntrustmentKey(wtInfo?.id)
    if (wtId && wtId !== primary) {
      const r2 = await axios.get(apiPath, { params: { entrustmentId: wtId } })
      if (r2.data && r2.data.success && Array.isArray(r2.data.data)) {
        return sortByTimeDesc(r2.data.data || [])
      }
    }
  } catch (e) {
  }

  return []
}

const fetchNuclearRecordsForKey = async (key) => {
  const primary = normalizeEntrustmentKey(key)
  if (!primary) return []

  try {
    const r1 = await axios.get('/api/nuclear-density/get-by-entrustment-id', { params: { entrustmentId: primary } })
    if (r1.data && r1.data.success && Array.isArray(r1.data.data) && r1.data.data.length > 0) {
      return r1.data.data
    }
  } catch (e) {
  }

  try {
    const wtResp = await axios.get('/api/jc-core-wt-info/detail', { params: { unifiedNumber: primary } })
    const wtInfo = wtResp?.data?.data
    const wtId = normalizeEntrustmentKey(wtInfo?.id)
    if (wtId && wtId !== primary) {
      const r2 = await axios.get('/api/nuclear-density/get-by-entrustment-id', { params: { entrustmentId: wtId } })
      if (r2.data && r2.data.success && Array.isArray(r2.data.data)) {
        return r2.data.data || []
      }
    }
  } catch (e) {
  }

  return []
}

const fetchSandRecordsForKey = async (key) => {
  return fetchSimpleRecordsForKey('/api/sand-replacement/get-by-entrustment-id', key)
}

const fetchWaterRecordsForKey = async (key) => {
  return fetchSimpleRecordsForKey('/api/water-replacement/get-by-entrustment-id', key)
}

const fetchCuttingRingRecordsForKey = async (key) => {
  return fetchSimpleRecordsForKey('/api/cutting-ring/get-by-entrustment-id', key)
}

const safeJsonParse = (v) => {
  try {
    return JSON.parse(v || '{}')
  } catch (e) {
    return {}
  }
}

const copyCommonFieldsFromParsed = (parsed, isFilled) => {
  if (!parsed) return
  Object.keys(parsed).forEach(k => {
    if (k.includes('_page')) return
    if (k.match(/_\d+$/)) return
    if (!isFilled(formData[k]) && parsed[k] !== undefined) {
      formData[k] = parsed[k]
    }
  })
}

const appendRowsFromSandParsed = (parsed, out) => {
  if (!parsed) return
  for (let col = 0; col < 4; col++) {
    const idx1 = col * 2
    const idx2 = col * 2 + 1
    const row = {}
    const loc = parsed['location_' + col]
    if (loc !== undefined) row.location = loc
    if (parsed.testDate !== undefined) row.date = parsed.testDate
    const w1 = parsed['wetDensity_' + idx1]
    const w2 = parsed['wetDensity_' + idx2]
    const d1 = parsed['dryDensity_' + idx1]
    const d2 = parsed['dryDensity_' + idx2]
    const m1 = parsed['avgMoisture_' + idx1]
    const m2 = parsed['avgMoisture_' + idx2]
    const ad = parsed['avgDryDensity_' + col]
    const c = parsed['compaction_' + col]
    if (w1 !== undefined) row.wetDensity = w1
    if (w2 !== undefined) row.wetDensity2 = w2
    if (d1 !== undefined) row.dryDensity = d1
    if (d2 !== undefined) row.dryDensity2 = d2
    if (m1 !== undefined) row.moisture = m1
    if (m2 !== undefined) row.moisture2 = m2
    if (ad !== undefined) row.avgDryDensity = ad
    if (c !== undefined) row.compaction = c
    out.push(row)
  }
}

const appendRowsFromWaterParsed = (parsed, out) => {
  if (!parsed) return
  const total = Number(parsed.totalPages || 1) || 1
  for (let page = 0; page < total; page++) {
    for (let col = 0; col < 4; col++) {
      const idx1 = col * 2
      const idx2 = col * 2 + 1
      const row = {}
      const loc = parsed[`samplingLocation_page${page}_${col}`]
      if (loc !== undefined) row.location = loc
      if (parsed.testDate !== undefined) row.date = parsed.testDate
      const w1 = parsed[`wetDensity_page${page}_${idx1}`]
      const w2 = parsed[`wetDensity_page${page}_${idx2}`]
      const d1 = parsed[`measuredDryDensity_page${page}_${idx1}`]
      const d2 = parsed[`measuredDryDensity_page${page}_${idx2}`]
      const m1 = parsed[`avgMoisture_page${page}_${idx1}`]
      const m2 = parsed[`avgMoisture_page${page}_${idx2}`]
      const ad = parsed[`avgMeasuredDryDensity_page${page}_${col}`]
      if (w1 !== undefined) row.wetDensity = w1
      if (w2 !== undefined) row.wetDensity2 = w2
      if (d1 !== undefined) row.dryDensity = d1
      if (d2 !== undefined) row.dryDensity2 = d2
      if (m1 !== undefined) row.moisture = m1
      if (m2 !== undefined) row.moisture2 = m2
      if (ad !== undefined) row.avgDryDensity = ad
      out.push(row)
    }
  }
}

const appendRowsFromCuttingRingParsed = (parsed, out) => {
  if (!parsed) return
  const total = Number(parsed.totalPages || 1) || 1
  for (let page = 0; page < total; page++) {
    for (let i = 0; i < 4; i++) {
      const row = {}
      const sid = parsed[`sampleNo_page${page}_${i}`]
      const loc = parsed[`location_page${page}_${i}`]
      const w1 = parsed[`wetDensity1_page${page}_${i}`]
      const w2 = parsed[`wetDensity2_page${page}_${i}`]
      const d1 = parsed[`dryDensity1_page${page}_${i}`]
      const d2 = parsed[`dryDensity2_page${page}_${i}`]
      const m1 = parsed[`avgMoisture1_page${page}_${i}`]
      const m2 = parsed[`avgMoisture2_page${page}_${i}`]
      const ad = parsed[`avgDryDensity1_page${page}_${i}`]
      const c = parsed[`compaction1_page${page}_${i}`]
      if (sid !== undefined) row.sampleId = sid
      if (loc !== undefined) row.location = loc
      if (parsed.testDate !== undefined) row.date = parsed.testDate
      if (w1 !== undefined) row.wetDensity = w1
      if (w2 !== undefined) row.wetDensity2 = w2
      if (d1 !== undefined) row.dryDensity = d1
      if (d2 !== undefined) row.dryDensity2 = d2
      if (m1 !== undefined) row.moisture = m1
      if (m2 !== undefined) row.moisture2 = m2
      if (ad !== undefined) row.avgDryDensity = ad
      if (c !== undefined) row.compaction = c
      out.push(row)
    }
  }
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
        // 补齐施工部位等头部信息，使之与检测报告一致
        await fillHeaderFromEntrustment(key)

        const isFilled = (v) => v !== undefined && v !== null && String(v).trim() !== ''
        let hasAnyRow = false
        let missingRow = false
        try {
          const firstParsed = JSON.parse((records.value[0] && records.value[0].dataJson) ? records.value[0].dataJson : '{}')
          for (let i = 0; i < 20; i++) {
            const rowHas = (
              isFilled(firstParsed['sampleId_' + i]) ||
              isFilled(firstParsed['location_' + i]) ||
              isFilled(firstParsed['wetDensity_' + i]) ||
              isFilled(firstParsed['dryDensity_' + i]) ||
              isFilled(firstParsed['moisture_' + i]) ||
              isFilled(firstParsed['compaction_' + i])
            )
            if (rowHas) {
              hasAnyRow = true
            } else if (i >= 7) {
              missingRow = true
            }
          }
        } catch (e) {
        }

        if (!hasAnyRow || missingRow || records.value.length < 2) {
          try {
            const methodType = detectDensityMethodType(formData.testMethod, formData.testCategory)
            const existing = records.value.slice()
            const allRowData = []
            const rowFields = ['sampleId', 'location', 'date', 'wetDensity', 'dryDensity', 'moisture', 'avgDryDensity', 'compaction', 'wetDensity2', 'dryDensity2', 'moisture2', 'remarks']

            let sourceList = []
            if (methodType === 'nuclear') sourceList = await fetchNuclearRecordsForKey(key)
            if (methodType === 'sand') sourceList = await fetchSandRecordsForKey(key)
            if (methodType === 'water') sourceList = await fetchWaterRecordsForKey(key)
            if (methodType === 'ring') sourceList = await fetchCuttingRingRecordsForKey(key)

            if (sourceList && sourceList.length > 0) {
              const signatureRecord = sourceList.find(r =>
                (r && (r.reviewSignaturePhoto || r.inspectSignaturePhoto || r.approveSignaturePhoto))
              ) || sourceList[0]
              if (!formData.recordTester && signatureRecord?.recordTester) formData.recordTester = signatureRecord.recordTester
              if (!formData.recordReviewer && signatureRecord?.recordReviewer) formData.recordReviewer = signatureRecord.recordReviewer
              if (!formData.reviewerSignature && signatureRecord?.reviewSignaturePhoto) {
                formData.reviewerSignature = normalizeSignatureSrc(signatureRecord.reviewSignaturePhoto)
              }
              if (!formData.testerSignature && signatureRecord?.inspectSignaturePhoto) {
                formData.testerSignature = normalizeSignatureSrc(signatureRecord.inspectSignaturePhoto)
              }
              if (!formData.approverSignature && signatureRecord?.approveSignaturePhoto) {
                formData.approverSignature = normalizeSignatureSrc(signatureRecord.approveSignaturePhoto)
              }

              if (methodType === 'nuclear') {
                sourceList.forEach((nRecord, pageIndex) => {
                  if (!nRecord || !nRecord.dataJson) return
                  const nParsed = safeJsonParse(nRecord.dataJson)
                  if (pageIndex === 0) {
                    for (let i = 7; i < 20; i++) {
                      const rowData = {}
                      rowFields.forEach(field => {
                        if (field === 'date') {
                          const val = nParsed['date_' + i] ?? nParsed.testDate ?? nParsed.test_date
                          if (val !== undefined) rowData[field] = val
                          return
                        }
                        if (field === 'avgDryDensity') {
                          const val = nParsed['avgDryDensity_' + i]
                          const fallback = nParsed['dryDensity_' + i]
                          const picked = val !== undefined ? val : fallback
                          if (picked !== undefined) rowData[field] = picked
                          return
                        }
                        const key = field + '_' + i
                        const val = nParsed[key]
                        if (val !== undefined) rowData[field] = val
                      })
                      allRowData.push(rowData)
                    }
                  } else {
                    for (let i = 0; i < 20; i++) {
                      const rowData = {}
                      rowFields.forEach(field => {
                        if (field === 'date') {
                          const val = nParsed['date_' + i] ?? nParsed.testDate ?? nParsed.test_date
                          if (val !== undefined) rowData[field] = val
                          return
                        }
                        if (field === 'avgDryDensity') {
                          const val = nParsed['avgDryDensity_' + i]
                          const fallback = nParsed['dryDensity_' + i]
                          const picked = val !== undefined ? val : fallback
                          if (picked !== undefined) rowData[field] = picked
                          return
                        }
                        const key = field + '_' + i
                        const val = nParsed[key]
                        if (val !== undefined) rowData[field] = val
                      })
                      allRowData.push(rowData)
                    }
                  }
                  if (pageIndex === 0) {
                    copyCommonFieldsFromParsed(nParsed, isFilled)
                  }
                })
              } else if (methodType === 'sand') {
                const picked = pickApprovedRecord(sourceList)
                const parsed = safeJsonParse(picked?.dataJson)
                copyCommonFieldsFromParsed(parsed, isFilled)
                appendRowsFromSandParsed(parsed, allRowData)
              } else if (methodType === 'water') {
                const picked = pickApprovedRecord(sourceList)
                const parsed = safeJsonParse(picked?.dataJson)
                copyCommonFieldsFromParsed(parsed, isFilled)
                appendRowsFromWaterParsed(parsed, allRowData)
              } else if (methodType === 'ring') {
                const picked = pickApprovedRecord(sourceList)
                const parsed = safeJsonParse(picked?.dataJson)
                copyCommonFieldsFromParsed(parsed, isFilled)
                appendRowsFromCuttingRingParsed(parsed, allRowData)
              }

              if (allRowData.some(r => Object.values(r).some(isFilled))) {
                const pageSize = 20
                const totalPages = Math.ceil(allRowData.length / pageSize)
                const newRecords = []
                for (let page = 0; page < totalPages; page++) {
                  const startIdx = page * pageSize
                  const endIdx = Math.min(startIdx + pageSize, allRowData.length)
                  const pageData = allRowData.slice(startIdx, endIdx)

                  let pageFormData = { ...formData }
                  if (existing[page] && existing[page].dataJson) {
                    pageFormData = { ...pageFormData, ...safeJsonParse(existing[page].dataJson) }
                  }
                  pageData.forEach((rowData, idx) => {
                    rowFields.forEach(field => {
                      if (rowData[field] !== undefined) {
                        const k = field + '_' + idx
                        if (!isFilled(pageFormData[k])) {
                          pageFormData[k] = rowData[field]
                        }
                      }
                    })
                  })

                  const base = existing[page] ? { ...existing[page] } : { id: '', entrustmentId: key, dataJson: '{}' }
                  base.entrustmentId = key
                  base.dataJson = JSON.stringify(pageFormData)
                  newRecords.push(base)
                }

                if (newRecords.length > 0) {
                  records.value = newRecords
                  currentIndex.value = 0
                  mapRecordToFormData(records.value[0])
                }
              }
            }
          } catch (e) {
            console.error('density result nuclear autofill fallback error', e)
          }
        }
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
          formData.testCategory = eData.testCategory || formData.testCategory || ''
          
          if (eData.tester) formData.tester = eData.tester
          if (eData.reviewer) formData.reviewer = eData.reviewer
          if (eData.approver) formData.approver = eData.approver
          
          // Directory fallback mapping
          const currentDirectory = JSON.parse(localStorage.getItem('currentDirectory') || '{}')
          if (currentDirectory.tester) {
            formData.recordTester = currentDirectory.tester
          } else if (eData.tester) {
            formData.recordTester = eData.tester
          }
          
          if (currentDirectory.reviewer) {
            formData.recordReviewer = currentDirectory.reviewer
          } else if (eData.reviewer) {
            formData.recordReviewer = eData.reviewer
          }
          
          if (currentDirectory.approver) {
            formData.approver = currentDirectory.approver
          }
          
          try {
            const isFilled = (v) => v !== undefined && v !== null && String(v).trim() !== ''
            const methodType = detectDensityMethodType(formData.testMethod, formData.testCategory)
            const rowFields = ['sampleId', 'location', 'date', 'wetDensity', 'dryDensity', 'moisture', 'avgDryDensity', 'compaction', 'wetDensity2', 'dryDensity2', 'moisture2', 'remarks']
            const allRowData = []

            let sourceList = []
            if (methodType === 'nuclear') sourceList = await fetchNuclearRecordsForKey(key)
            if (methodType === 'sand') sourceList = await fetchSandRecordsForKey(key)
            if (methodType === 'water') sourceList = await fetchWaterRecordsForKey(key)
            if (methodType === 'ring') sourceList = await fetchCuttingRingRecordsForKey(key)

            if (sourceList && sourceList.length > 0) {
              const signatureRecord = sourceList.find(r =>
                (r && (r.reviewSignaturePhoto || r.inspectSignaturePhoto || r.approveSignaturePhoto))
              ) || sourceList[0]
              if (!formData.recordTester && signatureRecord?.recordTester) formData.recordTester = signatureRecord.recordTester
              if (!formData.recordReviewer && signatureRecord?.recordReviewer) formData.recordReviewer = signatureRecord.recordReviewer
              if (!formData.reviewerSignature && signatureRecord?.reviewSignaturePhoto) {
                formData.reviewerSignature = normalizeSignatureSrc(signatureRecord.reviewSignaturePhoto)
              }
              if (!formData.testerSignature && signatureRecord?.inspectSignaturePhoto) {
                formData.testerSignature = normalizeSignatureSrc(signatureRecord.inspectSignaturePhoto)
              }
              if (!formData.approverSignature && signatureRecord?.approveSignaturePhoto) {
                formData.approverSignature = normalizeSignatureSrc(signatureRecord.approveSignaturePhoto)
              }

              if (methodType === 'nuclear') {
                sourceList.forEach((nRecord, pageIndex) => {
                  if (!nRecord?.dataJson) return
                  const nParsed = safeJsonParse(nRecord.dataJson)
                  if (pageIndex === 0) {
                    for (let i = 7; i < 20; i++) {
                      const rowData = {}
                      rowFields.forEach(field => {
                        if (field === 'date') {
                          const val = nParsed['date_' + i] ?? nParsed.testDate ?? nParsed.test_date
                          if (val !== undefined) rowData[field] = val
                          return
                        }
                        if (field === 'avgDryDensity') {
                          const val = nParsed['avgDryDensity_' + i]
                          const fallback = nParsed['dryDensity_' + i]
                          const picked = val !== undefined ? val : fallback
                          if (picked !== undefined) rowData[field] = picked
                          return
                        }
                        const key = field + '_' + i
                        const val = nParsed[key]
                        if (val !== undefined) rowData[field] = val
                      })
                      allRowData.push(rowData)
                    }
                  } else {
                    for (let i = 0; i < 20; i++) {
                      const rowData = {}
                      rowFields.forEach(field => {
                        if (field === 'date') {
                          const val = nParsed['date_' + i] ?? nParsed.testDate ?? nParsed.test_date
                          if (val !== undefined) rowData[field] = val
                          return
                        }
                        if (field === 'avgDryDensity') {
                          const val = nParsed['avgDryDensity_' + i]
                          const fallback = nParsed['dryDensity_' + i]
                          const picked = val !== undefined ? val : fallback
                          if (picked !== undefined) rowData[field] = picked
                          return
                        }
                        const key = field + '_' + i
                        const val = nParsed[key]
                        if (val !== undefined) rowData[field] = val
                      })
                      allRowData.push(rowData)
                    }
                  }
                  if (pageIndex === 0) {
                    Object.keys(nParsed).forEach(k => {
                      if (!k.match(/_\d+$/)) formData[k] = nParsed[k]
                    })
                  }
                })
              } else if (methodType === 'sand') {
                const picked = pickApprovedRecord(sourceList)
                const parsed = safeJsonParse(picked?.dataJson)
                copyCommonFieldsFromParsed(parsed, isFilled)
                appendRowsFromSandParsed(parsed, allRowData)
              } else if (methodType === 'water') {
                const picked = pickApprovedRecord(sourceList)
                const parsed = safeJsonParse(picked?.dataJson)
                copyCommonFieldsFromParsed(parsed, isFilled)
                appendRowsFromWaterParsed(parsed, allRowData)
              } else if (methodType === 'ring') {
                const picked = pickApprovedRecord(sourceList)
                const parsed = safeJsonParse(picked?.dataJson)
                copyCommonFieldsFromParsed(parsed, isFilled)
                appendRowsFromCuttingRingParsed(parsed, allRowData)
              }

              if (allRowData.some(r => Object.values(r).some(isFilled))) {
                const pageSize = 20
                const totalPages = Math.ceil(allRowData.length / pageSize)
                records.value = []
                for (let page = 0; page < totalPages; page++) {
                  const startIdx = page * pageSize
                  const endIdx = Math.min(startIdx + pageSize, allRowData.length)
                  const pageData = allRowData.slice(startIdx, endIdx)
                  const pageRecord = { id: '', entrustmentId: key, dataJson: '{}' }
                  const pageFormData = { ...formData }
                  pageData.forEach((rowData, idx) => {
                    rowFields.forEach(field => {
                      if (rowData[field] !== undefined) {
                        pageFormData[field + '_' + idx] = rowData[field]
                      }
                    })
                  })
                  pageRecord.dataJson = JSON.stringify(pageFormData)
                  records.value.push(pageRecord)
                }
                if (records.value.length === 0) {
                  records.value.push({ id: '', entrustmentId: key, dataJson: JSON.stringify(formData) })
                }
                currentIndex.value = 0
                mapRecordToFormData(records.value[0])
                return
              }
            }
          } catch (e) {
            console.error('Failed to auto-fill from Nuclear Record', e)
          }

          newRecord.dataJson = JSON.stringify(formData)
        }
        
        if (records.value.length === 0) {
          records.value = [newRecord]
          currentIndex.value = 0
          mapRecordToFormData(newRecord)
        }
      }
    } catch (error) {
      console.error('Error loading data:', error)
    }
  }
}

const getStatusText = (status) => {
  const s = parseInt(status)
  switch(s) {
    // 统一状态名称
    case 0: return '草稿'
    case 1: return '已提交待审核'
    case 2: return '已打回'
    case 3: return '待签字'
    case 4: return '待批准'
    case 5: return '待批准'
    case 6: return '已批准'
    case 7: return '驳回'
    // 报告表状态 (10-17)
    case 10: return '草稿'
    case 11: return '已提交待审核'
    case 12: return '已打回'
    case 13: return '待签字'
    case 14: return '待批准'
    case 15: return '待批准'
    case 16: return '已批准'
    case 17: return '驳回'
    // 结果表状态 (20-27)
    case 20: return '草稿'
    case 21: return '已提交待审核'
    case 22: return '已打回'
    case 23: return '待签字'
    case 24: return '待批准'
    case 25: return '待批准'
    case 26: return '已批准'
    case 27: return '驳回'
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
    case 4: return '#ff8c00' // orange
    case 5: return '#ff8c00' // orange
    case 6: return '#28a745' // success
    case 7: return '#dc3545' // danger
    // 报告表状态 (10-17)
    case 10: return '#6c757d' // secondary
    case 11: return '#007bff' // primary
    case 12: return '#dc3545' // danger
    case 13: return '#ffc107' // warning
    case 14: return '#ff8c00' // orange
    case 15: return '#ff8c00' // orange
    case 16: return '#28a745' // success
    case 17: return '#dc3545' // danger
    // 结果表状态 (20-27)
    case 20: return '#6c757d' // secondary
    case 21: return '#007bff' // primary
    case 22: return '#dc3545' // danger
    case 23: return '#ffc107' // warning
    case 24: return '#ff8c00' // orange
    case 25: return '#ff8c00' // orange
    case 26: return '#28a745' // success
    case 27: return '#dc3545' // danger
    default: return '#6c757d'
  }
}

const saveData = async () => {
  try {
    // 确保日期格式正确
    for (let i = 0; i < 20; i++) {
      if (formData['date_' + i]) {
        formData['date_' + i] = formatDate(formData['date_' + i])
      }
    }
    
    // 保存当前页的数据
    saveCurrentRecordState()
    
    // 遍历所有页的数据并保存
    let successCount = 0
    for (let i = 0; i < records.value.length; i++) {
      const record = records.value[i]
      const recordData = JSON.parse(record.dataJson || '{}')
      
      // 确保recordData中的日期格式正确
      for (let j = 0; j < 20; j++) {
        if (recordData['date_' + j]) {
          recordData['date_' + j] = formatDate(recordData['date_' + j])
        }
      }
      
      // 确保recordData也有正确的状态
      recordData.status = formData.status
      record.dataJson = JSON.stringify(recordData)
      
      const dataToSave = {
        id: record.id,
        entrustmentId: record.entrustmentId || props.wtNum || props.id,
        status: formData.status, // 传递状态字段给后端
        dataJson: record.dataJson,
        reviewSignaturePhoto: record.reviewSignaturePhoto || recordData.reviewerSignature,
        inspectSignaturePhoto: record.inspectSignaturePhoto || recordData.testerSignature,
        approveSignaturePhoto: record.approveSignaturePhoto || recordData.approverSignature,
        recordTester: record.recordTester || recordData.recordTester,
        recordReviewer: record.recordReviewer || recordData.recordReviewer,
        filler: record.filler || recordData.filler,
        approver: record.approver || recordData.approver
      }
      
      const response = await axios.post('/api/density-test/save', dataToSave)
      if (response.data.success) {
        successCount++
        if (response.data.data && response.data.data.id) {
           record.id = response.data.data.id
           // 更新当前页的id
           if (i === currentIndex.value) {
               formData.id = response.data.data.id
           }
        }
      }
    }
    
    if (successCount > 0) {
      alert(`保存成功，共保存了 ${successCount} 页数据`)
      // 保存成功后返回列表页面，确保列表显示更新后的状态
      if (navigateTo) {
        navigateTo('DensityTestResultList')
      }
    } else {
      alert('保存失败: 没有可保存的数据')
    }
  } catch (error) {
    console.error('Save error:', error)
    alert('保存失败')
  }
}

const approveAndSave = async () => {
  const user = JSON.parse(localStorage.getItem('userInfo'))
  if (!user || !user.username) {
    alert('请先登录')
    return
  }

  try {
    const sigRes = await axios.post('/api/signature/get', { userAccount: user.username })
    const signatureBlob = sigRes?.data?.data?.signatureBlob
    if (!sigRes?.data?.success || !signatureBlob) {
      alert('未找到您的电子签名，请先去“电子签名”页面设置')
      return
    }

    const currentName = user.fullName || user.userName || user.username
    formData.approver = currentName
    formData.approverSignature = normalizeSignatureSrc(signatureBlob)
    formData.status = 6

    await saveData()
  } catch (e) {
    console.error('Approve error:', e)
    alert('批准失败')
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
    if (formData.recordReviewer && user.username !== formData.recordReviewer && user.fullName !== formData.recordReviewer) {
      alert('您不是该单据的记录审核人 (' + formData.recordReviewer + ')，无权审核')
      return
    }
    // Auto sign Reviewer
    if (!formData.reviewerSignature) {
         try {
            const sigRes = await axios.post('/api/signature/get', { userAccount: user.username })
            if (sigRes.data.success && sigRes.data.data && sigRes.data.data.signatureBlob) {
                 formData.reviewerSignature = `data:image/png;base64,${sigRes.data.data.signatureBlob}`
            }
         } catch (e) {
            console.error('Auto sign error', e)
         }
    }
    if (formData.reviewerSignature) {
        signatureData = formData.reviewerSignature.replace(/^data:image\/\w+;base64,/, '')
    }
  } else if (action === 'REJECT') {
    if (formData.recordReviewer && user.username !== formData.recordReviewer) {
      alert('您不是该单据的记录审核人 (' + formData.recordReviewer + ')，无权退回')
      return
    }
  } else if (action === 'SIGN_REVIEW') {
    if (formData.recordReviewer && user.username !== formData.recordReviewer && user.fullName !== formData.recordReviewer) {
      alert('您不是该单据的记录审核人 (' + formData.recordReviewer + ')，无权签字')
      return
    }
    // Auto sign Reviewer if missing
    if (!formData.reviewerSignature) {
         try {
            const sigRes = await axios.post('/api/signature/get', { userAccount: user.username })
            if (sigRes.data.success && sigRes.data.data && sigRes.data.data.signatureBlob) {
                 formData.reviewerSignature = `data:image/png;base64,${sigRes.data.data.signatureBlob}`
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
  } else if (action === 'SIGN_APPROVE') {
    if (formData.approver && user.username !== formData.approver && user.fullName !== formData.approver) {
      alert('您不是该单据的批准人 (' + formData.approver + ')，无权签字')
      return
    }
    // Auto sign Approver if missing
    if (!formData.approverSignature) {
         try {
            const sigRes = await axios.post('/api/signature/get', { userAccount: user.username })
            if (sigRes.data.success && sigRes.data.data && sigRes.data.data.signatureBlob) {
                 formData.approverSignature = `data:image/png;base64,${sigRes.data.data.signatureBlob}`
                 if (!formData.approver) {
                    formData.approver = user.fullName || user.username
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
    signatureData = formData.approverSignature.replace(/^data:image\/\w+;base64,/, '')
  }

  const request = {
    tableType: 'DENSITY_TEST', // Assuming this endpoint handles density test results too or needs specific type
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
      // 如果是批准操作，保存签名到数据库
      if (action === 'SIGN_APPROVE') {
        await saveData()
      }
      alert('操作成功')
      // Refresh data
      // For result list, we might need to reload current record
      // Re-fetch logic similar to loadData but for current ID
      // Simplified: reload page or call a refresh method
      window.location.reload() 
    } else {
      alert('操作失败: ' + response.data.message)
    }
  } catch (e) {
    console.error('Workflow error', e)
    alert('操作异常')
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
      const currentName = user.userName

      // Only allow Tester to sign via button
      if (!formData.recordTester || formData.recordTester === currentName || formData.recordTester === currentAccount) {
        if (!formData.recordTester) formData.recordTester = currentName
        formData.testerSignature = imgSrc
        signed = true
      }
      
      if (signed) {
        saveCurrentRecordState()
        for (let i = 0; i < records.value.length; i++) {
          const record = records.value[i]
          let recordData = {}
          try {
            recordData = JSON.parse(record.dataJson || '{}')
          } catch (e) {
            recordData = {}
          }
          recordData.testerSignature = imgSrc
          record.dataJson = JSON.stringify(recordData)
          record.inspectSignaturePhoto = imgSrc
        }
        // 保存签名到数据库
        await saveData()
        alert('签名成功并已保存')
      } else {
        alert(`当前用户(${currentName})与表单中的检测人员(${formData.recordTester})不匹配，无法签名`)
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

const openBackendPdfPreview = (actionUrl) => {
  if (!pdfForm.value) return
  const container = pdfForm.value.closest('.densityTestResult-container')
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
      .pdf-preview.densityTestResult-container { width: 198mm; height: 285mm; max-width: 198mm; min-width: 0; margin: 0; padding: 0; box-sizing: border-box; display: flex; flex-direction: column; }
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
    openBackendPdfPreview('/api/pdf/density_test_result/generate')
  }
}

const previewPdf = () => {
  if (pdfForm.value) {
    openBackendPdfPreview('/api/pdf/density_test_result/preview')
  }
}
</script>

<style scoped>
/* Signature styles */
.signature-box {
  position: relative;
  display: inline-block;
  min-width: 120px;
  vertical-align: bottom;
}
.signature-line {
  display: inline-block;
  min-width: 80px;
  border-bottom: 1px solid black;
  text-align: center;
  padding: 0 5px;
  height: 20px;
  line-height: 20px;
}
.signature-img {
  position: absolute;
  left: 40px;
  top: -16px;
  height: 18px;
  width: auto;
  max-width: 80px;
  object-fit: contain;
  mix-blend-mode: multiply;
}
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
    background: var(--card-blue);
    border: none;
    border-radius: 4px;
    color: var(--color-blue);
    cursor: pointer;
    font-size: 14px;
    padding: 6px 12px;
    transition: all 0.2s;
    display: inline-flex;
    align-items: center;
    gap: 5px;
    white-space: nowrap;
  }

  .link-button:hover {
    background-color: var(--color-blue);
    color: white;
    text-decoration: none;
  }

  .record-nav {
    display: inline-flex;
    align-items: center;
    gap: 10px;
    margin-left: 16px;
  }

  .record-nav-info {
    font-size: 14px;
    color: var(--text-light);
    white-space: nowrap;
  }

  .btn {
    padding: 6px 12px;
    border-radius: 4px;
    border: none;
    font-size: 14px;
    cursor: pointer;
    background-color: var(--card-blue);
    color: var(--color-blue);
    transition: all 0.2s;
    white-space: nowrap;
  }

  .btn-small {
    padding: 6px 12px;
    font-size: 14px;
  }

  .btn-primary {
    background-color: var(--color-blue);
    color: white;
  }

  .btn-secondary {
    background-color: var(--card-blue);
    color: var(--color-blue);
  }

  .btn-danger {
    background-color: #FFCDD2;
    color: #D32F2F;
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
            margin-bottom: 20px;
            table-layout: fixed; /* Fixed column width */
            word-break: break-all;
        }
        th, td {
            border: 1px solid black;
            text-align: center;
            padding: 8px 5px;
            font-size: inherit;
        }
        .two-inputs {
            display: flex;
            flex-direction: column;
            gap: 6px;
            align-items: center;
        }
        .two-inputs input[type="text"] {
            width: 90%;
        }
        .label {
            font-weight: bold;
        }
        .footer-info {
            display: flex;
            justify-content: space-between;
            margin-top: 20px;
            font-size: 16px;
            font-weight: normal;
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
        input[type="text"]:disabled:focus {
            background-color: transparent;
            outline: none;
            border-color: black;
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
