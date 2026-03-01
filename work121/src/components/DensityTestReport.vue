<template>
  <div class="densityTestReport-container">


    <div class="no-print toolbar">
      <div class="toolbar-left">
        <button @click="goToList" class="link-button">&lt; 返回列表</button>
        <span v-if="formData.status !== undefined" class="status-text">
          状态:
          <span :style="{ color: getStatusColor(formData.status) }" class="status-label">
            {{ getStatusText(formData.status) }}
          </span>
        </span>
      </div>

      <div class="toolbar-right">
        <button
          v-if="formData.status === 0 || formData.status === 2"
          @click="saveData"
          class="btn btn-secondary btn-small"
        >
          保存
        </button>
        <button
          v-if="formData.status === 0 || formData.status === 2"
          @click="handleSign"
          class="btn btn-secondary btn-small"
        >
          签字
        </button>
        <button
          v-if="formData.status === 0 || formData.status === 2"
          @click="submitWorkflow('SUBMIT')"
          class="btn btn-primary btn-small"
        >
          提交
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
          退回
        </button>

        <button
          v-if="formData.status === 3"
          @click="handleSign"
          class="btn btn-secondary btn-small"
        >
          签字
        </button>
        <button
          v-if="formData.status === 3"
          @click="submitWorkflow('SIGN_REVIEW')"
          class="btn btn-primary btn-small"
        >
          复核签字
        </button>

        <button
          v-if="formData.status === 4"
          @click="handleSign"
          class="btn btn-secondary btn-small"
        >
          签字
        </button>
        <button
          v-if="formData.status === 4"
          @click="submitWorkflow('SIGN_APPROVE')"
          class="btn btn-primary btn-small"
        >
          批准签字
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
        <span>统一编号：<input type="text" v-model="formData.unifiedNumber"   name="unifiedNumber" style="width: 150px; border-bottom: 1px solid black;"></span>
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
        <template v-for="(n, i_idx) in 8" :key="i_idx">
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

    <div class="statement">
        声明：<br>
        1. 对本检测报告的复印件未加盖公司检验检测专用章无效。 2. 对检验结果如有异议，应在收到报告之日起十五日之内向本公司提出。
    </div>

    <div class="company-info" style="display: block;">
        <div>公司名称：<input type="text" v-model="formData.companyName"   name="companyName" style="width: 70%; border: none; text-align: left;" value="河北金涛建设工程质量检测有限公司"></div>
        <div style="display: flex; justify-content: space-between; margin-top: 5px;">
            <span>公司地址：<input type="text" v-model="formData.companyAddress"   name="companyAddress" style="width: 300px; border: none; text-align: left;" value="石家庄高新区方亿科技工业园A区第2号楼。"></span>
            <span>电话：<input type="text" v-model="formData.companyPhone"   name="companyPhone" style="width: 200px; border: none;" value="0311—86107634  0311—67300616"></span>
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
  remarks: '附原位密度检测结果。\n见证人：\n见证单位：',
  reviewerSignature: '',
  testerSignature: '',
  approverSignature: ''
})

// 根据检测方法自动判断是否为“核子法”
const isNuclearMethod = computed(() => {
  const method = (formData.testMethod || '').toString()
  // 只要检测方法里包含“核子”两个字，就按核子法版式渲染
  return method.includes('核子')
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
    case 5: return '审核通过待批准'
    case 6: return '已批准'
    case 7: return '驳回'
    // 报告表状态 (10-17)
    case 10: return '草稿'
    case 11: return '已提交待审核'
    case 12: return '已打回'
    case 13: return '待签字'
    case 14: return '已签字待提交'
    case 15: return '审核通过待批准'
    case 16: return '已批准'
    case 17: return '驳回'
    // 结果表状态 (20-27)
    case 20: return '草稿'
    case 21: return '已提交待审核'
    case 22: return '已打回'
    case 23: return '待签字'
    case 24: return '已签字待提交'
    case 25: return '审核通过待批准'
    case 26: return '已批准'
    case 27: return '驳回'
    default: return '未知'
  }
}

const getStatusColor = (status) => {
  const s = parseInt(status)
  switch(s) {
    case 0: return '#6c757d' // secondary
    case 1: return '#007bff' // primary
    case 2: return '#dc3545' // danger
    case 3: return '#ffc107' // warning
    case 4: return '#17a2b8' // info
    case 5: return '#ff8c00' // orange
    case 6: return '#28a745' // success
    case 7: return '#dc3545' // danger
    // 报告表状态 (10-17)
    case 10: return '#6c757d' // secondary
    case 11: return '#007bff' // primary
    case 12: return '#dc3545' // danger
    case 13: return '#ffc107' // warning
    case 14: return '#17a2b8' // info
    case 15: return '#ff8c00' // orange
    case 16: return '#28a745' // success
    case 17: return '#dc3545' // danger
    // 结果表状态 (20-27)
    case 20: return '#6c757d' // secondary
    case 21: return '#007bff' // primary
    case 22: return '#dc3545' // danger
    case 23: return '#ffc107' // warning
    case 24: return '#17a2b8' // info
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
    signatureData = formData.testerSignature
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
                 if (!formData.recordReviewer) {
                    formData.recordReviewer = user.fullName || user.username
                 }
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
    for (let i_idx = 0; i_idx < 8; i_idx++) {
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
        }
        formData.id = data.id
        formData.status = data.status !== undefined ? data.status : 0
        formData.entrustmentId = data.entrustmentId
        formData.reviewerSignature = data.reviewSignaturePhoto || ''
        formData.testerSignature = data.inspectSignaturePhoto || ''
        formData.approverSignature = data.approveSignaturePhoto || ''
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
          formData.commissionDate = eData.commissionDate || ''
          formData.constructionPart = eData.constructionPart || ''
          formData.testCategory = eData.testCategory || ''
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
              // Filter records with status 5 (approved)
              const approvedRecords = resultResponse.data.data.filter(record => record.status === 5)
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
                  for (let i = 0; i < 8; i++) {
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
                    for (let i = 0; i < 8; i++) {
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
            
            // 无论是否有检测结果记录，如果是核子法，都尝试直接从核子法记录获取数据以确保完整性
            if (formData.testCategory && formData.testCategory.includes('核子')) {
                try {
                  const nuclearRes = await axios.get('/api/nuclear-density/get-by-entrustment-id', {
                     params: { entrustmentId: formData.unifiedNumber }
                  })
                  if (nuclearRes.data.success && nuclearRes.data.data && nuclearRes.data.data.length > 0) {
                     const nRecord = nuclearRes.data.data[0]
                     // 检查记录表状态是否为审核通过（状态值为5）
                     if (nRecord.status === 5) {
                         if (nRecord.dataJson) {
                             const nParsed = JSON.parse(nRecord.dataJson)
                             
                             // 映射标准击实参数和其他通用字段
                             const commonFields = ['entrustingUnit', 'unifiedNumber', 'projectName', 'commissionDate', 'constructionPart', 'testCategory', 'equipment', 'sampleNameStatus', 'standard', 'designCompaction', 'maxDryDensity', 'optimumMoisture', 'minDryDensity']
                             commonFields.forEach(field => {
                                 if (nParsed[field] !== undefined) {
                                     formData[field] = nParsed[field]
                                 }
                             })
                             
                             // 只填充前8行数据到报告表（从核子法记录表第一页）
                             const rowFields = ['sampleId', 'location', 'date', 'wetDensity', 'dryDensity', 'moisture', 'compaction', 'remarks']
                             for (let i = 0; i < 8; i++) {
                                 rowFields.forEach(field => {
                                     const key = field + '_' + i
                                     if (nParsed[key] !== undefined) {
                                         formData[key] = nParsed[key]
                                     }
                                 })
                             }
                         }
                     } else {
                         console.log('核子密度仪记录表状态未审核通过，不自动填充数据')
                     }
                  }
                } catch (e) {
                   console.error('Failed to auto-fill from Nuclear Record', e)
                }
            }

            // 如果是灌砂法，尝试直接从灌砂法记录获取数据
            if (formData.testCategory && formData.testCategory.includes('灌砂')) {
                try {
                  const sandRes = await axios.get('/api/sand-replacement/get-by-entrustment-id', {
                     params: { entrustmentId: formData.unifiedNumber }
                  })
                  if (sandRes.data.success && sandRes.data.data && sandRes.data.data.length > 0) {
                     const sRecord = sandRes.data.data[0]
                     // 检查记录表状态是否为审核通过（状态值为5）
                     if (sRecord.status === 5) {
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
    } catch (error) {
      console.error('Error loading data:', error)
    }
  }
}

const saveData = async () => {
  try {
    const dataToSave = {
      id: formData.id,
      // 报告表里的 ENTRUSTMENT_ID 统一使用“统一编号”存储，优先用 wtNum
      entrustmentId: formData.entrustmentId || props.wtNum || props.id,
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
      if (!formData.recordTester || formData.recordTester === currentName || formData.recordTester === currentAccount) {
        if (!formData.recordTester) formData.recordTester = currentName
        formData.testerSignature = imgSrc
        signed = true
      }
      
      if (signed) {
        alert('签名成功')
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

const generatePdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/density_test_report/generate'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}

const previewPdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/density_test_report/preview'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}
</script>

<style scoped>
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

        .densityTestReport-container {
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
            justify-content: space-around;
            margin-top: 20px;
            margin-bottom: 10px;
            font-size: 16px;
            font-weight: bold;
        }
        .statement {
            font-size: 12px;
            line-height: 1.6;
            margin-top: 5px;
            border-top: 2px solid black; /* Separate statement from footer info */
            padding-top: 5px;
        }
        .company-info {
             display: flex;
             justify-content: space-between;
             font-size: 14px;
             margin-top: 5px;
             border-bottom: 2px solid black; /* Bottom line */
             padding-bottom: 5px;
        }
        .page-footer {
            margin-top: 5px;
            display: flex;
            justify-content: space-between;
            font-size: 14px;
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
