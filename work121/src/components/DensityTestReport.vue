<template>
  <div class="densityTestReport-container">


    <div class="no-print toolbar">
      <div class="toolbar-left">
        <button @click="goToList" class="link-button">&lt; 返回列表</button>
      </div>

      <div class="toolbar-right">
        <span v-if="formData.status !== undefined" class="status-text">
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
    <h2>原位密度检测报告</h2>

    <div class="header-info">
        <!-- 委托单位应对应委托单里的单位(clientUnit)，而不是委托人(client) -->
        <span>委托单位：<input type="text" v-model="formData.clientUnit"   name="clientUnit" style="width: 250px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>统一编号：<input type="text" v-model="formData.unifiedNumber"   name="unifiedNumber" style="width: 150px; border-bottom: 1px solid black;" disabled></span>
        <!-- 兼容后端 PDF 旧字段名：把当前委托单位再同步一份到 entrustingUnit，确保 PDF 一定有值 -->
        <input type="hidden" name="entrustingUnit" :value="formData.clientUnit" />
    </div>

    <table>
        <!-- Header Rows -->
        <tr>
            <td class="label">工程名称</td>
            <td colspan="6"><input type="text" v-model="formData.projectName"   name="projectName"></td>
            <td class="label">委托日期</td>
            <td colspan="3"><input type="text" v-model="formData.commissionDate"   name="commissionDate"></td>
        </tr>
        <tr>
            <td class="label">施工部位</td>
            <td colspan="6"><input type="text" v-model="formData.constructionPart"   name="constructionPart"></td>
            <td class="label">检测日期</td>
            <td colspan="3"><input type="text" v-model="formData.testDate"   name="testDate"></td>
        </tr>
        <tr>
            <td class="label">样品名称<br>及状态</td>
            <td colspan="6"><input type="text" v-model="formData.sampleNameStatus"   name="sampleNameStatus"></td>
            <td class="label">报告日期</td>
            <td colspan="3"><input type="text" v-model="formData.reportDate"   name="reportDate"></td>
        </tr>
        <tr>
            <td class="label">仪器设备</td>
            <td colspan="6"><input type="text" v-model="formData.equipment"   name="equipment"></td>
            <td class="label">检测方法</td>
            <td colspan="3"><input type="text" v-model="formData.testMethod"   name="testMethod"></td>
        </tr>
        <tr>
            <td class="label">检测依据</td>
            <td colspan="6"><input type="text" v-model="formData.testBasis"   name="testBasis"></td>
            <td class="label">检测类别</td>
            <td colspan="3"><input type="text" v-model="formData.testCategory"   name="testCategory"></td>
        </tr>
        <tr>
            <td class="label">最大干密度<br>(g/cm³)</td>
            <td colspan="3"><input type="text" v-model="formData.maxDryDensity"   name="maxDryDensity"></td>
            <td class="label">最优含水率 %</td>
            <td colspan="3"><input type="text" v-model="formData.optimumMoisture"   name="optimumMoisture"></td>
            <td class="label">最小干密度<br>(g/cm³)</td>
            <td colspan="2"><input type="text" v-model="formData.minDryDensity"   name="minDryDensity"></td>
        </tr>
        <tr>
            <td class="label">设计指标</td>
            <td colspan="10" class="left-align"><input type="text" v-model="formData.designIndex"   name="designIndex"></td>
        </tr>
        <tr>
            <td class="label">检测结果</td>
            <td colspan="10" class="left-align"><input type="text" v-model="formData.testResult"   name="testResult"></td>
        </tr>

        <!-- Data Header -->
        <tr>
            <td class="label" style="width: 10%;">样品编号</td>
            <td class="label" style="width: 20%;" colspan="3">检测部位<br>(桩号、高程)</td>
            <td class="label" style="width: 14%;" colspan="2">检测日期</td>
            <td class="label" style="width: 14%;">湿密度<br>(g/cm³)</td>
            <td class="label" style="width: 14%;">干密度<br>(g/cm³)</td>
            <td class="label" style="width: 14%;">含水率<br>%</td>
            <td class="label" style="width: 14%;" colspan="2">压实度%</td>
        </tr>

        <!-- Data Rows (8 rows)：
             - 核子法：一行样品编号对应一行湿密度/干密度/含水率（不需要第二行）
             - 其他方法：保持原来的两行结构 -->
        <template v-for="(n, i_idx) in 7" :key="i_idx">
          <!-- 非核子法：两行一组 -->
          <template v-if="!isNuclearMethod">
            <tr>
              <td rowspan="2"><input type="text" :name="'sampleId_' + i_idx" v-model="formData['sampleId_' + i_idx]"></td>
              <td rowspan="2" colspan="3"><input type="text" :name="'location_' + i_idx" v-model="formData['location_' + i_idx]"></td>
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

          <!-- 核子法：一行样品编号对应一行检测数据 -->
          <tr v-else>
            <td><input type="text" :name="'sampleId_' + i_idx" v-model="formData['sampleId_' + i_idx]"></td>
            <td colspan="3"><input type="text" :name="'location_' + i_idx" v-model="formData['location_' + i_idx]"></td>
            <td colspan="2"><input type="text" :name="'date_' + i_idx" v-model="formData['date_' + i_idx]"></td>
            <td><input type="text" :name="'wetDensity_' + i_idx" v-model="formData['wetDensity_' + i_idx]"></td>
            <td><input type="text" :name="'dryDensity_' + i_idx" v-model="formData['dryDensity_' + i_idx]"></td>
            <td><input type="text" :name="'moisture_' + i_idx" v-model="formData['moisture_' + i_idx]"></td>
            <td colspan="2"><input type="text" :name="'compaction_' + i_idx" v-model="formData['compaction_' + i_idx]"></td>
          </tr>
        </template>

        <!-- Remarks Section -->
        <tr>
            <td class="label">备 注</td>
            <td colspan="10" class="left-align" style="height: 100px; vertical-align: top;">
                <textarea v-model="formData.remarks"  name="remarks" style="width: 100%; height: 100%;"></textarea>
            </td>
        </tr>
    </table>

    <div class="footer-info">
        <span class="signature-box">
            批准：
            <span class="signature-line"></span>
            <img v-if="formData.approverSignature" :src="formData.approverSignature" class="signature-img" alt="批准人签名" />
        </span>
        <span class="signature-box">
            审核：
            <span class="signature-line"></span>
            <img v-if="formData.reviewerSignature" :src="formData.reviewerSignature" class="signature-img" alt="审核人签名" />
        </span>
        <span class="signature-box">
            检测：
            <span class="signature-line"></span>
            <img v-if="formData.testerSignature" :src="formData.testerSignature" class="signature-img" alt="检测人签名" />
        </span>
    </div>
    <!-- 电子签名隐藏字段 -->
    <input type="hidden" name="testerSignature" :value="formData.testerSignature">
    <input type="hidden" name="reviewerSignature" :value="formData.reviewerSignature">
    <input type="hidden" name="approverSignature" :value="formData.approverSignature">

    <div class="statement">
        声明：<br>
        1. 对本检测报告的复印件未加盖公司检验检测专用章无效。<br>
        2. 对检验结果如有异议，应在收到报告之日起十五日之内向本公司提出。
    </div>

    <div class="company-info" style="display: block;">
        <div>公司名称：河北金涛建设工程质量检测有限公司</div>
        <div style="display: flex; justify-content: space-between; margin-top: 5px;">
            <span>公司地址：石家庄高新区方亿科技工业园区A区第2号楼</span>
            <span>电话：0311—86107634 67300616</span>
        </div>
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
    navigateTo('DensityTestReportList')
  }
}

const pdfForm = ref(null)

const formData = reactive({
  id: '',
  entrustmentId: '',
  status: 0,
  client: '',
  clientUnit: '',
  unifiedNumber: '',
  projectName: '',
  commissionDate: '',
  constructionPart: '',
  testDate: '',
  sampleNameStatus: '',
  reportDate: '',
  equipment: '',
  testMethod: '',
  testBasis: '',
  testCategory: '',
  maxDryDensity: '',
  optimumMoisture: '',
  minDryDensity: '',
  designIndex: '',
  testResult: '',
  approver: '',
  recordReviewer: '',
  recordTester: '',
  filler: '',
  companyName: '',
  companyAddress: '',
  companyPhone: '',
  witness: '',
  witnessUnit: '',
  remarks: '附原位密度检测结果。\n见证人：\n见证单位：',
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

// 根据检测方法自动判断是否为“核子法”
const isNuclearMethod = computed(() => {
  const method = (formData.testMethod || formData.testCategory || '').toString()
  return method.includes('核子')
})

const getStatusText = (status) => {
  if (status === null || status === undefined || status === '') {
    return '草稿'
  }
  const s = parseInt(status)
  if (isNaN(s)) {
    return '草稿'
  }
  switch(s) {
    // 统一状态名称
    case 0: return '草稿'
    case 1: return '已提交待审核'
    case 2: return '已打回'
    case 3: return '待签字'
    case 4: return '审核通过待批准'
    case 5: return '审核通过待批准'
    case 6: return '已批准'
    case 7: return '驳回'
    // 报告表状态 (10-17)
    case 10: return '草稿'
    case 11: return '已提交待审核'
    case 12: return '已打回'
    case 13: return '待签字'
    case 14: return '审核通过待批准'
    case 15: return '审核通过待批准'
    case 16: return '已批准'
    case 17: return '驳回'
    // 结果表状态 (20-27)
    case 20: return '草稿'
    case 21: return '已提交待审核'
    case 22: return '已打回'
    case 23: return '待签字'
    case 24: return '审核通过待批准'
    case 25: return '审核通过待批准'
    case 26: return '已批准'
    case 27: return '驳回'
    default: return '未知'
  }
}

const getStatusColor = (status) => {
  if (status === null || status === undefined || status === '') {
    return '#6c757d' // secondary (草稿)
  }
  const s = parseInt(status)
  if (isNaN(s)) {
    return '#6c757d' // secondary (草稿)
  }
  switch(s) {
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
    tableType: 'DENSITY_TEST', // Maps to handleDensityTest
    recordId: formData.id, // Assuming DensityTestReport ID matches DensityTest ID or is handled
    action: action,
    userAccount: user.username,
    signatureData: signatureData,
    nextHandler: '' // Optional
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
      loadData()
    } else {
      alert('操作失败: ' + response.data.message)
    }
  } catch (e) {
    console.error('Workflow error', e)
    alert('操作异常')
  }
}

onMounted(() => {
  // Initialize dynamic fields for loop variable 'i_idx'
    // Please verify the loop count match the template
    for (let i_idx = 0; i_idx < 7; i_idx++) {
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

  loadData()
})

const normalizeEntrustmentKey = (v) => {
  const s = (v ?? '').toString().trim()
  if (!s) return ''
  if (s.startsWith('{') && s.endsWith('}')) {
    return s.slice(1, -1).trim()
  }
  return s
}

const fillHeaderFromEntrustment = async (unifiedNumber) => {
  const u = normalizeEntrustmentKey(unifiedNumber)
  if (!u) return
  try {
    const resp = await axios.get('/api/jc-core-wt-info/detail', { params: { unifiedNumber: u } })
    if (resp.data && resp.data.success && resp.data.data) {
      const e = resp.data.data
      if (!formData.unifiedNumber && e.wtNum) formData.unifiedNumber = e.wtNum
      if (!formData.projectName && e.projectName) formData.projectName = e.projectName
      if (!formData.clientUnit && e.clientUnit) formData.clientUnit = e.clientUnit
      if (!formData.commissionDate && e.commissionDate) formData.commissionDate = formatDate(e.commissionDate)
      if (!formData.constructionPart && e.constructionPart) formData.constructionPart = e.constructionPart
      if (!formData.testCategory && e.testCategory) formData.testCategory = e.testCategory
      if (!formData.testDate && e.testDate) formData.testDate = formatDate(e.testDate)
      if (!formData.witness && e.witness) formData.witness = e.witness
      if (!formData.witnessUnit && e.witnessUnit) formData.witnessUnit = e.witnessUnit
      if (!formData.sampleNameStatus && (e.sampleName || e.sampleStatus)) {
        const eSampleName = e.sampleName || ''
        const eSampleStatus = e.sampleStatus || ''
        formData.sampleNameStatus = eSampleName && eSampleStatus ? `${eSampleName}，${eSampleStatus}` : (eSampleName || eSampleStatus || '')
      }
    }
  } catch (e) {
  }
}

const normalizeSignatureSrc = (v) => {
  if (!v) return ''
  const s = String(v).trim()
  if (!s) return ''
  if (s.startsWith('data:image')) return s
  if (/^[A-Za-z0-9+/=]+$/.test(s)) return `data:image/png;base64,${s}`
  return s
}

const applyWitnessToRemarks = () => {
  const w = (formData.witness ?? '').toString().trim()
  const wu = (formData.witnessUnit ?? '').toString().trim()
  if (!w && !wu) return
  const raw = (formData.remarks ?? '').toString()
  const lines = raw.split(/\r?\n/)
  const upsert = (label, value) => {
    if (!value) return
    const idx = lines.findIndex(l => l.trim().startsWith(label))
    if (idx === -1) {
      lines.push(label + value)
      return
    }
    const current = lines[idx].trim().slice(label.length).trim()
    if (!current) lines[idx] = label + value
  }
  upsert('见证人：', w)
  upsert('见证单位：', wu)
  formData.remarks = lines.join('\n')
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

  const loadData = async () => {
  // 这里的 id 来自列表的 item.id（委托 WT_ID），wtNum 是统一编号 XT-2024-54301
  // 原位密度报告/结果的 ENTRUSTMENT_ID 我们现在统一用“统一编号”存，所以优先用 wtNum 作为 key
  const key = props.wtNum || props.id
  if (key) {
    try {
      const response = await axios.get('/api/density-test/report/get-by-entrustment-id', {
        params: { entrustmentId: key }
      })

      if (response.data.success && response.data.data) {
        const data = response.data.data
        if (data.dataJson) {
          const parsed = JSON.parse(data.dataJson)
          Object.assign(formData, parsed)

          // Map legacy fields to new record fields
          if (!formData.recordTester && parsed.tester) formData.recordTester = parsed.tester
          if (!formData.recordReviewer && parsed.reviewer) formData.recordReviewer = parsed.reviewer
          // 统一编号：recordData 里用 wtNum 存储，这里回填到 unifiedNumber
          if (!formData.unifiedNumber && parsed.wtNum) {
            formData.unifiedNumber = parsed.wtNum
          }
          
          // 从record对象中获取字段值，优先于dataJson
          if (data.projectName) formData.projectName = data.projectName
          if (data.commissionDate) formData.commissionDate = new Date(data.commissionDate).toISOString().split('T')[0]
          if (data.constructionPart) formData.constructionPart = data.constructionPart
          if (data.testCategory) formData.testCategory = data.testCategory
          if (data.clientUnit) formData.clientUnit = data.clientUnit
          
          // 兼容旧数据：如果 JSON 里只有 entrustingUnit 或 clientUnit，就统一回填到 clientUnit，用于“委托单位”显示
          if (!formData.clientUnit && parsed.entrustingUnit) {
            formData.clientUnit = parsed.entrustingUnit
          }
          if (!formData.clientUnit && parsed.clientUnit) {
            formData.clientUnit = parsed.clientUnit
          }

          // 兼容灌砂法等记录表的字段差异：
          // 1）最优含水率：记录表使用 optMoisture，这里需要填到 optimumMoisture
          if (!formData.optimumMoisture && parsed.optMoisture !== undefined) {
            formData.optimumMoisture = parsed.optMoisture
          }
          
          // 2）环刀法字段映射：sampleNo_ -> sampleId_, moisture1_ -> moisture_
          for (let i = 0; i < 20; i++) {
            // 样品编号映射
            if (!formData['sampleId_' + i] && parsed['sampleNo_' + i] !== undefined && parsed['sampleNo_' + i] !== null && parsed['sampleNo_' + i] !== '') {
              formData['sampleId_' + i] = parsed['sampleNo_' + i]
            }
            // 含水率映射（环刀法使用 moisture1_ 作为第一行含水率，报告表使用 moisture_）
            // moisture2_ 不需要映射，直接使用
            if (!formData['moisture_' + i] && parsed['moisture1_' + i] !== undefined && parsed['moisture1_' + i] !== null && parsed['moisture1_' + i] !== '') {
              formData['moisture_' + i] = parsed['moisture1_' + i]
            }
          }
          // 2）干密度和湿密度：如果 DATA_JSON 里是原始结构（有 avgDryDensity_i 但没有 dryDensity2_i），需要重排
          const hasAvgDry = Object.keys(parsed).some(k => k.startsWith('avgDryDensity_'))
          const hasDry2 = Object.keys(parsed).some(k => k.startsWith('dryDensity2_'))
          if (hasAvgDry && !hasDry2) {
            // 先清除 Object.assign 可能已经复制过来的所有干密度和湿密度值（避免重复显示）
            // 清除范围：dryDensity_0..7, dryDensity2_0..7, wetDensity_0..7, wetDensity2_0..7
            for (let i = 0; i < 8; i++) {
              delete formData['dryDensity_' + i]
              delete formData['dryDensity2_' + i]
              delete formData['wetDensity_' + i]
              delete formData['wetDensity2_' + i]
            }
            // 收集记录表里的纵向 dryDensity_0..7
            const sandDry = []
            for (let i = 0; i < 8; i++) {
              const v = parsed['dryDensity_' + i]
              if (v !== undefined && v !== null && v !== '') {
                sandDry.push(v)
              }
            }
            // 每两个值一组，映射到同一个检测部位的两行干密度
            // 记录表：dryDensity_0="干1", dryDensity_1="干2", dryDensity_2="干3", dryDensity_3="干4"
            // 报告：测试部位1 → dryDensity_0="干1", dryDensity2_0="干2"
            //      测试部位2 → dryDensity_1="干3", dryDensity2_1="干4"
            for (let row = 0; row < 4; row++) {
              const v1 = sandDry[row * 2]      // 第一行
              const v2 = sandDry[row * 2 + 1] // 第二行
              if (v1 !== undefined) formData['dryDensity_' + row] = v1
              if (v2 !== undefined) formData['dryDensity2_' + row] = v2
            }
            // 湿密度也需要同样的重排
            const sandWet = []
            for (let i = 0; i < 8; i++) {
              const v = parsed['wetDensity_' + i]
              if (v !== undefined && v !== null && v !== '') {
                sandWet.push(v)
              }
            }
            for (let row = 0; row < 4; row++) {
              const v1 = sandWet[row * 2]      // 第一行
              const v2 = sandWet[row * 2 + 1] // 第二行
              if (v1 !== undefined) formData['wetDensity_' + row] = v1
              if (v2 !== undefined) formData['wetDensity2_' + row] = v2
            }
          }
          
          // 3）处理新格式的多页数据（带 _page 后缀的字段）
          const hasPageData = Object.keys(parsed).some(k => k.includes('_page'))
          if (hasPageData) {
            // 收集所有页面的样品数据
            const pageData = []
            let sampleIndex = 0
            
            // 遍历所有页面
            const pageIndices = new Set()
            Object.keys(parsed).forEach(key => {
              const match = key.match(/_page(\d+)_/)
              if (match) {
                pageIndices.add(parseInt(match[1]))
              }
            })
            
            // 按页面顺序处理
            const sortedPages = Array.from(pageIndices).sort((a, b) => a - b)
            sortedPages.forEach(pageIndex => {
              // 处理每页的5个样品
              for (let i = 0; i < 5; i++) {
                if (sampleIndex >= 7) break // 报告表最多显示7个样品
                
                const pagePrefix = '_page' + pageIndex + '_' + i
                
                // 样品编号
                const sampleNoKey = 'sampleNo' + pagePrefix
                if (parsed[sampleNoKey] !== undefined && parsed[sampleNoKey] !== null && parsed[sampleNoKey] !== '') {
                  formData['sampleId_' + sampleIndex] = parsed[sampleNoKey]
                }
                
                // 检测部位
                const locationKey = 'location' + pagePrefix
                if (parsed[locationKey] !== undefined && parsed[locationKey] !== null && parsed[locationKey] !== '') {
                  formData['location_' + sampleIndex] = parsed[locationKey]
                }
                
                // 湿密度
                const wetDensityKey = 'wetDensity' + pagePrefix
                if (parsed[wetDensityKey] !== undefined && parsed[wetDensityKey] !== null && parsed[wetDensityKey] !== '') {
                  formData['wetDensity_' + sampleIndex] = parsed[wetDensityKey]
                }
                
                // 干密度
                const dryDensityKey = 'dryDensity' + pagePrefix
                if (parsed[dryDensityKey] !== undefined && parsed[dryDensityKey] !== null && parsed[dryDensityKey] !== '') {
                  formData['dryDensity_' + sampleIndex] = parsed[dryDensityKey]
                }
                
                // 含水率
                const moistureKey = 'moisture1' + pagePrefix
                if (parsed[moistureKey] !== undefined && parsed[moistureKey] !== null && parsed[moistureKey] !== '') {
                  formData['moisture_' + sampleIndex] = parsed[moistureKey]
                }
                
                // 压实度
                const compactionKey = 'compaction' + pagePrefix
                if (parsed[compactionKey] !== undefined && parsed[compactionKey] !== null && parsed[compactionKey] !== '') {
                  formData['compaction_' + sampleIndex] = parsed[compactionKey]
                }
                
                // 检测日期
                if (parsed.testDate) {
                  formData['date_' + sampleIndex] = parsed.testDate
                }
                
                sampleIndex++
              }
            })
          }
        }
        formData.id = data.id
        formData.status = data.status !== undefined ? data.status : 0
        formData.entrustmentId = data.entrustmentId
        formData.reviewerSignature = normalizeSignatureSrc(data.reviewSignaturePhoto || '')
        formData.testerSignature = normalizeSignatureSrc(data.inspectSignaturePhoto || '')
        formData.approverSignature = normalizeSignatureSrc(data.approveSignaturePhoto || '')

        await fillHeaderFromEntrustment(formData.unifiedNumber || key)

        const isFilled = (v) => v !== undefined && v !== null && String(v).trim() !== ''
        const hasAnyRow = Array.from({ length: 7 }, (_, i) => (
          isFilled(formData['sampleId_' + i]) ||
          isFilled(formData['location_' + i]) ||
          isFilled(formData['wetDensity_' + i]) ||
          isFilled(formData['dryDensity_' + i]) ||
          isFilled(formData['moisture_' + i]) ||
          isFilled(formData['compaction_' + i])
        )).some(Boolean)

        const nuclearKey = formData.unifiedNumber || props.wtNum || key
        if (nuclearKey && !hasAnyRow) {
          try {
            const nuclearList = await fetchNuclearRecordsForKey(nuclearKey)
            if (nuclearList && nuclearList.length > 0) {
              const pickScore = (r) => {
                try {
                  const p = JSON.parse(r?.dataJson || '{}')
                  let c = 0
                  for (let i = 0; i < 20; i++) {
                    const v = p['sampleId_' + i]
                    if (v !== undefined && v !== null && String(v).trim() !== '') c++
                  }
                  return c
                } catch (e) {
                  return 0
                }
              }
              const nRecord = nuclearList
                .filter(r => r && r.dataJson)
                .sort((a, b) => pickScore(b) - pickScore(a))[0] || nuclearList[0]
              if (nRecord && nRecord.dataJson) {
                const nParsed = JSON.parse(nRecord.dataJson)
                const commonFields = ['entrustingUnit', 'unifiedNumber', 'projectName', 'commissionDate', 'constructionPart', 'testCategory', 'testMethod', 'equipment', 'sampleNameStatus', 'standard', 'designCompaction', 'maxDryDensity', 'optimumMoisture', 'minDryDensity']
                commonFields.forEach(field => {
                  if (!isFilled(formData[field]) && isFilled(nParsed[field])) {
                    formData[field] = field === 'commissionDate'
                      ? new Date(nParsed[field]).toISOString().split('T')[0]
                      : nParsed[field]
                  }
                })
                if (!isFilled(formData.testMethod) && String(formData.testCategory || '').includes('核子')) {
                  formData.testMethod = '核子法'
                }
                if (!isFilled(formData.testBasis)) {
                  const basis = nParsed.testBasis !== undefined ? nParsed.testBasis : nParsed.standard
                  if (isFilled(basis)) formData.testBasis = basis
                }
                if (!isFilled(formData.designIndex) && isFilled(nParsed.designCompaction)) {
                  formData.designIndex = nParsed.designCompaction
                }
                if (!isFilled(formData.witness) && nRecord.witness) formData.witness = nRecord.witness
                if (!isFilled(formData.witnessUnit) && nRecord.witnessUnit) formData.witnessUnit = nRecord.witnessUnit
                if (!isFilled(formData.recordTester) && nRecord.recordTester) formData.recordTester = nRecord.recordTester
                if (!isFilled(formData.recordReviewer) && nRecord.recordReviewer) formData.recordReviewer = nRecord.recordReviewer
                if (!isFilled(formData.testerSignature) && nRecord.inspectSignaturePhoto) {
                  formData.testerSignature = normalizeSignatureSrc(nRecord.inspectSignaturePhoto)
                }
                if (!isFilled(formData.reviewerSignature) && nRecord.reviewSignaturePhoto) {
                  formData.reviewerSignature = normalizeSignatureSrc(nRecord.reviewSignaturePhoto)
                }
                const rowFields = ['sampleId', 'location', 'date', 'wetDensity', 'dryDensity', 'moisture', 'compaction', 'remarks']
                for (let i = 0; i < 7; i++) {
                  rowFields.forEach(field => {
                    const k = field + '_' + i
                    if (!isFilled(formData[k]) && isFilled(nParsed[k])) {
                      formData[k] = nParsed[k]
                    }
                  })
                }
              }
            }
          } catch (e) {
            console.error('density report nuclear autofill fallback error', e)
          }
        }

        if (!isFilled(formData.reportDate)) {
          formData.reportDate = formatDate(new Date())
        }
        applyWitnessToRemarks()
      } else {
        const entrustmentResponse = await axios.get('/api/jc-core-wt-info/detail', {
          params: { unifiedNumber: key }
        })
        if (entrustmentResponse.data.success) {
          const eData = entrustmentResponse.data.data
          formData.entrustmentId = key
          formData.unifiedNumber = eData.wtNum || key || ''
          formData.projectName = eData.projectName || ''
          // 委托单位：后端字段叫 clientUnit，这里既保存在 clientUnit，又映射到页面使用的 client
          formData.clientUnit = eData.clientUnit || ''
          formData.client = eData.clientUnit || ''
          formData.commissionDate = eData.commissionDate ? new Date(eData.commissionDate).toISOString().split('T')[0] : ''
          formData.constructionPart = eData.constructionPart || ''
          formData.testCategory = eData.testCategory || ''
          formData.witness = eData.witness || formData.witness || ''
          formData.witnessUnit = eData.witnessUnit || formData.witnessUnit || ''
          // 样品名称及状态：从委托单 sampleName + sampleStatus 拼接，中间用顿号隔开
          const eSampleName = eData.sampleName || ''
          const eSampleStatus = eData.sampleStatus || ''
          if (eSampleName || eSampleStatus) {
            formData.sampleNameStatus = eSampleName && eSampleStatus
              ? `${eSampleName}，${eSampleStatus}`
              : (eSampleName || eSampleStatus || '')
          }
          formData.testDate = new Date().toISOString().split('T')[0]
          
          // Directory fallback logic
          const directory = JSON.parse(localStorage.getItem('currentDirectory'))
          if (directory) {
             formData.recordTester = directory.jcTester || eData.tester || ''
             formData.recordReviewer = directory.jcReviewer || eData.reviewer || ''
             formData.approver = directory.bgApprover || eData.approver || ''
             formData.filler = directory.jcFiller || ''
          } else {
             if (eData.tester) formData.recordTester = eData.tester
             if (eData.reviewer) formData.recordReviewer = eData.reviewer
             if (eData.approver) formData.approver = eData.approver
          }

          try {
            const resultResponse = await axios.get('/api/density-test/get-by-entrustment-id', {
              params: { entrustmentId: formData.unifiedNumber }
            })
            if (resultResponse.data.success && resultResponse.data.data && resultResponse.data.data.length > 0) {
              const approvedRecords = resultResponse.data.data.filter(record => {
                const s = parseInt(record.status)
                return s === 4 || s === 5
              })
              if (approvedRecords.length > 0) {
                const record = approvedRecords[0]
              if (record.dataJson) {
                try {
                  const parsed = JSON.parse(record.dataJson)

                  // 1）汇总区字段：优先用标准键名，如果不存在则做兼容映射
                  const summaryKeys = ['maxDryDensity', 'optimumMoisture', 'minDryDensity', 'designIndex', 'testResult']
                  summaryKeys.forEach(k => {
                    if (parsed[k] !== undefined) formData[k] = parsed[k]
                  })
                  // 兼容灌砂法里使用的 optMoisture 字段名
                  if (!formData.optimumMoisture && parsed.optMoisture !== undefined) {
                    formData.optimumMoisture = parsed.optMoisture
                  }

                  // 2）判断是否是灌砂法记录
                  const isSandReplacement = parsed.testCategory === '灌砂法' || formData.testCategory === '灌砂法'
                  
                  // 3）明细行通用字段：sampleId/location/date/含水率/压实度等，直接复制
                  //    如果是灌砂法，跳过 wetDensity_（后面会专门重排）
                  for (let i = 0; i < 7; i++) {
                    const prefixes = [
                      'sampleId_',
                      'location_',
                      'date_',
                      'moisture_',
                      'compaction_',
                      'moisture2_'
                    ]
                    // 核子法等已有 wetDensity2 结构的，直接复制
                    if (!isSandReplacement) {
                      prefixes.push('wetDensity_', 'wetDensity2_')
                    }
                    prefixes.forEach(prefix => {
                      const key = prefix + i
                      if (parsed[key] !== undefined) formData[key] = parsed[key]
                    })
                  }

                  // 4）灌砂法专用：根据 dryDensity_0..7 和 wetDensity_0..7 重排为“每个检测部位两行”
                  //    约定：dryDensity_0=\"干1\", dryDensity_1=\"干2\", dryDensity_2=\"干3\", dryDensity_3=\"干4\" ...
                  //         报告：测试部位1 → dryDensity_0, dryDensity2_0；测试部位2 → dryDensity_1, dryDensity2_1
                  if (isSandReplacement) {
                    // 先清除可能存在的多余字段（避免重复显示）
                    for (let i = 4; i < 8; i++) {
                      delete formData['dryDensity_' + i]
                      delete formData['dryDensity2_' + i]
                      delete formData['wetDensity_' + i]
                      delete formData['wetDensity2_' + i]
                    }
                    // 重排干密度：每两个值一组，映射到同一个检测部位的两行
                    for (let row = 0; row < 4; row++) {
                      const idx1 = row * 2
                      const idx2 = row * 2 + 1
                      const v1 = parsed['dryDensity_' + idx1]
                      const v2 = parsed['dryDensity_' + idx2]
                      if (v1 !== undefined && v1 !== null && v1 !== '') {
                        formData['dryDensity_' + row] = v1
                      }
                      if (v2 !== undefined && v2 !== null && v2 !== '') {
                        formData['dryDensity2_' + row] = v2
                      }
                    }
                    // 重排湿密度：每两个值一组，映射到同一个检测部位的两行
                    for (let row = 0; row < 4; row++) {
                      const idx1 = row * 2
                      const idx2 = row * 2 + 1
                      const v1 = parsed['wetDensity_' + idx1]
                      const v2 = parsed['wetDensity_' + idx2]
                      if (v1 !== undefined && v1 !== null && v1 !== '') {
                        formData['wetDensity_' + row] = v1
                      }
                      if (v2 !== undefined && v2 !== null && v2 !== '') {
                        formData['wetDensity2_' + row] = v2
                      }
                    }
                  } else {
                    // 其他检测方法（如核子法）：直接复制 dryDensity_/dryDensity2_
                    for (let i = 0; i < 7; i++) {
                      const d1 = parsed['dryDensity_' + i]
                      const d2 = parsed['dryDensity2_' + i]
                      if (d1 !== undefined) formData['dryDensity_' + i] = d1
                      if (d2 !== undefined) formData['dryDensity2_' + i] = d2
                    }
                  }
                } catch (e) {
                  console.error('density report auto-fill parse error', e)
                }
              }
              } else {
                console.log('记录表状态未审核通过，不自动填充数据')
              }
            }
            
            {
              try {
                const nuclearList = await fetchNuclearRecordsForKey(formData.unifiedNumber)
                if (nuclearList && nuclearList.length > 0) {
                  const pickScore = (r) => {
                    try {
                      const p = JSON.parse(r?.dataJson || '{}')
                      let c = 0
                      for (let i = 0; i < 20; i++) {
                        const v = p['sampleId_' + i]
                        if (v !== undefined && v !== null && String(v).trim() !== '') c++
                      }
                      return c
                    } catch (e) {
                      return 0
                    }
                  }
                  const nRecord = nuclearList
                    .filter(r => r && r.dataJson)
                    .sort((a, b) => pickScore(b) - pickScore(a))[0] || nuclearList[0]

                  if (nRecord && nRecord.dataJson) {
                    const nParsed = JSON.parse(nRecord.dataJson)
                    const isFilled = (v) => v !== undefined && v !== null && String(v).trim() !== ''
                    const nuclearHasAny = pickScore(nRecord) > 0 ||
                      isFilled(nParsed.standard) ||
                      isFilled(nParsed.equipment) ||
                      isFilled(nParsed.testMethod)
                    if (!nuclearHasAny) {
                      throw new Error('nuclear record has no meaningful data')
                    }

                    const commonFields = ['entrustingUnit', 'unifiedNumber', 'projectName', 'commissionDate', 'constructionPart', 'testCategory', 'testMethod', 'equipment', 'sampleNameStatus', 'standard', 'designCompaction', 'maxDryDensity', 'optimumMoisture', 'minDryDensity']
                    commonFields.forEach(field => {
                      const topVal = nRecord[field]
                      const jsonVal = nParsed[field]
                      if (!isFilled(formData[field]) && isFilled(topVal)) {
                        formData[field] = field === 'commissionDate' ? new Date(topVal).toISOString().split('T')[0] : topVal
                      } else if (!isFilled(formData[field]) && isFilled(jsonVal)) {
                        formData[field] = field === 'commissionDate' ? new Date(jsonVal).toISOString().split('T')[0] : jsonVal
                      }
                    })
                    if (!isFilled(formData.testMethod)) formData.testMethod = '核子法'
                    if (!isFilled(formData.testBasis)) {
                      const basis = nParsed.testBasis !== undefined ? nParsed.testBasis : nParsed.standard
                      if (isFilled(basis)) formData.testBasis = basis
                    }
                    if (!isFilled(formData.designIndex) && isFilled(nParsed.designCompaction)) {
                      formData.designIndex = nParsed.designCompaction
                    }
                    if (!isFilled(formData.witness) && nRecord.witness) formData.witness = nRecord.witness
                    if (!isFilled(formData.witnessUnit) && nRecord.witnessUnit) formData.witnessUnit = nRecord.witnessUnit
                    if (!isFilled(formData.recordTester) && nRecord.recordTester) formData.recordTester = nRecord.recordTester
                    if (!isFilled(formData.recordReviewer) && nRecord.recordReviewer) formData.recordReviewer = nRecord.recordReviewer
                    if (!isFilled(formData.testerSignature) && nRecord.inspectSignaturePhoto) {
                      formData.testerSignature = normalizeSignatureSrc(nRecord.inspectSignaturePhoto)
                    }
                    if (!isFilled(formData.reviewerSignature) && nRecord.reviewSignaturePhoto) {
                      formData.reviewerSignature = normalizeSignatureSrc(nRecord.reviewSignaturePhoto)
                    }

                    if (!isFilled(formData.clientUnit) && isFilled(nRecord.clientUnit)) {
                      formData.clientUnit = nRecord.clientUnit
                    } else if (!isFilled(formData.clientUnit) && isFilled(nParsed.entrustingUnit)) {
                      formData.clientUnit = nParsed.entrustingUnit
                    }

                    const rowFields = ['sampleId', 'location', 'date', 'wetDensity', 'dryDensity', 'moisture', 'compaction', 'remarks']
                    for (let i = 0; i < 7; i++) {
                      rowFields.forEach(field => {
                        const k = field + '_' + i
                        if (!isFilled(formData[k]) && isFilled(nParsed[k])) {
                          formData[k] = nParsed[k]
                        }
                      })
                    }
                  }
                }
              } catch (e) {
                // ignore
              }
            }

            if (!formData.reportDate) {
              formData.reportDate = formatDate(new Date())
            }
            applyWitnessToRemarks()

            // 如果是灌砂法，尝试直接从灌砂法记录获取数据
            if (formData.testCategory && formData.testCategory.includes('灌砂')) {
                try {
                  const sandRes = await axios.get('/api/sand-replacement/get-by-entrustment-id', {
                     params: { entrustmentId: formData.unifiedNumber }
                  })
                  if (sandRes.data.success && sandRes.data.data && sandRes.data.data.length > 0) {
                     const sRecord = sandRes.data.data[0]
                     // 检查记录表状态是否为审核通过（状态值为5）
                     const sStatus = parseInt(sRecord.status)
                     if (sStatus === 4 || sStatus === 5) {
                         if (sRecord.dataJson) {
                             const sParsed = JSON.parse(sRecord.dataJson)
                             
                             // 1. 基础字段合并
                             // 注意：先不要直接 Object.assign 覆盖 dryDensity_X，因为灌砂法的 dryDensity_X 需要重排
                             // 我们先提取非数组字段
                             const { ...otherFields } = sParsed
                             // 排除掉 dryDensity_X, wetDensity_X 等数组字段，避免直接覆盖导致错乱？
                             // 其实直接 assign 也没关系，因为后面会重写。但是为了保险，我们可以先 assign。
                             Object.assign(formData, sParsed)
                             
                             // 2. 映射特殊字段
                             if (sParsed.optMoisture !== undefined) {
                                formData.optimumMoisture = sParsed.optMoisture
                             }

                             // 3. 灌砂法专用重排逻辑：将 8 个平铺的密度值映射到 4 个检测点（每个点 2 个值）

                             // 重排干密度
                             for (let row = 0; row < 4; row++) {
                                const idx1 = row * 2
                                const idx2 = row * 2 + 1
                                // 从 sParsed 中获取
                                const v1 = sParsed['dryDensity_' + idx1]
                                const v2 = sParsed['dryDensity_' + idx2]
                                
                                // 赋值给 formData 的 dryDensity_row 和 dryDensity2_row
                                if (v1 !== undefined && v1 !== null && v1 !== '') {
                                    formData['dryDensity_' + row] = v1
                                }
                                if (v2 !== undefined && v2 !== null && v2 !== '') {
                                    formData['dryDensity2_' + row] = v2
                                }
                             }

                             // 重排湿密度
                             for (let row = 0; row < 4; row++) {
                                const idx1 = row * 2
                                const idx2 = row * 2 + 1
                                const v1 = sParsed['wetDensity_' + idx1]
                                const v2 = sParsed['wetDensity_' + idx2]
                                
                                if (v1 !== undefined && v1 !== null && v1 !== '') {
                                    formData['wetDensity_' + row] = v1
                                }
                                if (v2 !== undefined && v2 !== null && v2 !== '') {
                                    formData['wetDensity2_' + row] = v2
                                }
                             }
                             
                             // 重排含水率 (moisture)
                             for (let row = 0; row < 4; row++) {
                                const idx1 = row * 2
                                const idx2 = row * 2 + 1
                                const v1 = sParsed['moisture_' + idx1]
                                const v2 = sParsed['moisture_' + idx2]
                                
                                if (v1 !== undefined && v1 !== null && v1 !== '') {
                                    formData['moisture_' + row] = v1
                                }
                                if (v2 !== undefined && v2 !== null && v2 !== '') {
                                    formData['moisture2_' + row] = v2
                                }
                             }
                         }
                     } else {
                         console.log('灌砂法记录表状态未审核通过，不自动填充数据')
                     }
                  }
                } catch (e) {
                   console.error('Failed to auto-fill from Sand Replacement Record', e)
                }
            }
          } catch (e) {
            console.error('density report auto-fill error', e)
          }
        }
      }

      if (!formData.reportDate) {
        formData.reportDate = formatDate(new Date())
      }
      if (!formData.testMethod && String(formData.testCategory || '').includes('核子')) {
        formData.testMethod = '核子法'
      }
      if (!formData.testBasis && formData.standard) {
        formData.testBasis = formData.standard
      }
      if (!formData.designIndex && formData.designCompaction) {
        formData.designIndex = formData.designCompaction
      }
      applyWitnessToRemarks()
    } catch (error) {
      console.error('Error loading data:', error)
    }
  }
}

const saveData = async () => {
  try {
    // 确保日期格式正确
    if (formData.commissionDate) {
      formData.commissionDate = formatDate(formData.commissionDate)
    }
    if (formData.testDate) {
      formData.testDate = formatDate(formData.testDate)
    }
    if (formData.reportDate) {
      formData.reportDate = formatDate(formData.reportDate)
    }
    // 确保数据行中的日期格式正确
    for (let i = 0; i < 7; i++) {
      if (formData['date_' + i]) {
        formData['date_' + i] = formatDate(formData['date_' + i])
      }
    }
    
    const dataToSave = {
      id: formData.id,
      // 报告表里的 ENTRUSTMENT_ID 统一使用“统一编号”存储，优先用 wtNum
      entrustmentId: formData.entrustmentId || props.wtNum || props.id,
      status: formData.status, // 传递状态字段给后端
      dataJson: JSON.stringify(formData),
      reviewSignaturePhoto: formData.reviewerSignature,
      inspectSignaturePhoto: formData.testerSignature,
      approveSignaturePhoto: formData.approverSignature,
      recordTester: formData.recordTester,
      recordReviewer: formData.recordReviewer,
      filler: formData.filler,
      approver: formData.approver
    }
    
    const response = await axios.post('/api/density-test/report/save', dataToSave)
    if (response.data.success) {
      alert('保存成功')
      if (!formData.id && response.data.data && response.data.data.id) {
           formData.id = response.data.data.id
      }
      // 保存成功后返回列表页面，确保列表显示更新后的状态
      if (navigateTo) {
        navigateTo('DensityTestReportList')
      }
    } else {
      alert('保存失败: ' + response.data.message)
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
      // Match Tester
      if (!formData.recordTester || formData.recordTester === currentName || formData.recordTester === currentAccount) {
        if (!formData.recordTester) formData.recordTester = currentName
        formData.testerSignature = imgSrc
        signed = true
      }
      
      if (signed) {
        // 保存签名
        await saveData()
        await submitWorkflow('SIGN_REVIEW')
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

const openClientPdfPreview = () => {
  if (!pdfForm.value) return
  const container = pdfForm.value.closest('.densityTestReport-container')
  if (!container) return
  const mmToPx = (mm) => mm * 96 / 25.4
  const pageWidthMm = 210
  const pageHeightMm = 297
  const availableWidthPx = mmToPx(pageWidthMm)
  const availableHeightPx = mmToPx(pageHeightMm)
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
  const marginWantedMm = 2
  const marginWantedPx = mmToPx(marginWantedMm)
  const targetWidthPx = Math.max(availableWidthPx - marginWantedPx * 2, 1)
  const targetHeightPx = Math.max(availableHeightPx - marginWantedPx * 2, 1)
  const pdfScale = Math.min(targetWidthPx / contentWidthPx, targetHeightPx / contentHeightPx)
  const scaledWidthPx = contentWidthPx * pdfScale
  const scaledHeightPx = contentHeightPx * pdfScale
  const pdfOffsetXPx = Math.max(marginWantedPx, (availableWidthPx - scaledWidthPx) / 2)
  const pdfOffsetYPx = Math.max(marginWantedPx, (availableHeightPx - scaledHeightPx) / 2)
  const clone = container.cloneNode(true)
  clone.classList.add('pdf-preview')
  clone.querySelectorAll('.no-print').forEach(el => el.remove())
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
      .pdf-sheet { width: 210mm; height: 297mm; padding: 0; box-sizing: border-box; overflow: hidden; }
      .pdf-page { width: 210mm; height: 297mm; overflow: hidden; box-sizing: border-box; position: relative; }
      .pdf-content { position: absolute; left: 0; top: 0; transform-origin: top left; }
      .pdf-preview.densityTestReport-container { width: 100%; height: 100%; margin: 0; padding: 0; box-sizing: border-box; display: flex; flex-direction: column; }
      .pdf-preview { overflow: visible; }
      .pdf-preview * { page-break-inside: avoid; break-inside: avoid; }
      .pdf-preview #pdfForm { flex: 1; min-height: 0; display: flex; flex-direction: column; }
      .pdf-preview #pdfForm > table { flex: 0 0 auto; height: auto; margin-top: auto; margin-bottom: auto; }
    </style>
  </head>
  <body><div class="pdf-sheet"><div class="pdf-page"><div class="pdf-content" style="width: ${contentWidthPx}px; height: ${contentHeightPx}px; transform: translate(${pdfOffsetXPx}px, ${pdfOffsetYPx}px) scale(${pdfScale});">${clone.outerHTML}</div></div></div></body>
</html>`
  const w = window.open('', '_blank')
  if (!w) return
  w.document.open()
  w.document.write(html)
  w.document.close()
}
const openBackendPdfPreview = (actionUrl) => {
  if (!pdfForm.value) return
  const container = pdfForm.value.closest('.densityTestReport-container')
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
      .pdf-preview.densityTestReport-container { width: 198mm; height: 285mm; max-width: 198mm; min-width: 0; margin: 0; padding: 0; box-sizing: border-box; display: flex; flex-direction: column; }
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
    openBackendPdfPreview('/api/pdf/density_test_report/generate')
  }
}

const previewPdf = () => {
  if (pdfForm.value) {
    openBackendPdfPreview('/api/pdf/density_test_report/preview')
  }
}
</script>

<style scoped>
.densityTestReport-container {
    font-family: 'SimSun', 'Songti SC', serif;
    width: 210mm;
    max-width: 100%;
    min-width: 800px;
    margin: 0 auto;
    padding: 16px;
    background-color: var(--bg-card);
    border-radius: 8px;
    box-shadow: var(--shadow);
    box-sizing: border-box;
  }

/* Add signature overlay style */
.signature-overlay {
  position: absolute;
  pointer-events: none;
  z-index: 10;
}
.signature-overlay img {
  height: 18px;
  width: auto;
  max-width: 80px;
  opacity: 0.8;
}

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
  pointer-events: none;
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

  .status-text {
    font-size: 14px;
    font-weight: 500;
    color: var(--text-light);
    white-space: nowrap;
  }

  .status-label {
    margin-left: 6px;
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

  .densityTestReport-container {
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
            white-space: normal;
            overflow-wrap: anywhere;
            word-break: break-all;
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
        input[type="text"]:disabled:focus, textarea:disabled:focus {
            background-color: transparent;
            outline: none;
            border-color: black;
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
            font-weight: normal;
        }
        .statement {
            font-size: 12px;
            line-height: 1.6;
            margin-top: 5px;
        }
        .company-info {
             display: flex;
             justify-content: space-between;
             font-size: 12px;
             margin-top: 5px;
        }
        .page-footer {
            margin-top: 5px;
            display: flex;
            justify-content: space-between;
            font-size: 12px;
        }
        @media print {
            .densityTestReport-container {
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
