<template>
  <div class="beckmanBeamReport-container">

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
    <h1>路基路面回弹弯沉（回弹模量）检测报告</h1>

    <div class="header-info">
        <div>委托单位：<input type="text" v-model="formData.entrustingUnit"   name="entrustingUnit" style="width: 200px; border-bottom: 1px solid black; text-align: left;"></div>
        <div>统一编号：<input type="text" v-model="formData.unifiedNumber"   name="unifiedNumber" style="width: 150px; border-bottom: 1px solid black;"></div>
    </div>

    <!-- Table 1: Header Info (Rows 1-6) -->
    <table>
        <colgroup>
            <col style="width: 15%;">
            <col style="width: 35%;">
            <col style="width: 15%;">
            <col style="width: 35%;">
        </colgroup>
        <tr>
            <td class="label">工程名称</td>
            <td><input type="text" v-model="formData.projectName"   name="projectName"></td>
            <td class="label">委托日期</td>
            <td><input type="text" v-model="formData.commissionDate"   name="commissionDate" class="date-input"></td>
        </tr>
        <tr>
            <td class="label">施工部位</td>
            <td><input type="text" v-model="formData.constructionPart"   name="constructionPart"></td>
            <td class="label">检测日期</td>
            <td><input type="text" v-model="formData.testDate"   name="testDate" class="date-input"></td>
        </tr>
        <tr>
            <td class="label">仪器设备及编码</td>
            <td><input type="text" v-model="formData.equipmentCode"   name="equipmentCode"></td>
            <td class="label">报告日期</td>
            <td><input type="text" v-model="formData.reportDate"   name="reportDate" class="date-input"></td>
        </tr>
        <tr>
            <td class="label">样品名称及状态</td>
            <td><input type="text" v-model="formData.sampleStatus"   name="sampleStatus"></td>
            <td class="label">检测类别</td>
            <td><input type="text" v-model="formData.testCategory"   name="testCategory"></td>
        </tr>
        <tr>
            <td class="label">依据标准</td>
            <td><input type="text" v-model="formData.standard"   name="standard"></td>
            <td class="label">检测方法</td>
            <td><input type="text" v-model="formData.testMethod"   name="testMethod"></td>
        </tr>
        <tr>
            <td class="label">见证单位</td>
            <td><input type="text" v-model="formData.witnessUnit"   name="witnessUnit"></td>
            <td class="label">见证人</td>
            <td><input type="text" v-model="formData.witness"   name="witness"></td>
        </tr>
    </table>

    <!-- Table 2: Technical Parameters (Rows 7-8) -->
    <table>
        <colgroup>
            <col style="width: 15%;">
            <col style="width: 20%;">
            <col style="width: 15%;">
            <col style="width: 15%;">
            <col style="width: 20%;">
            <col style="width: 15%;">
        </colgroup>
        <tr>
            <td class="label">路面结构类型</td>
            <td><input type="text" v-model="formData.pavementType"   name="pavementType"></td>
            <td class="label">路面厚度<br>(mm)</td>
            <td><input type="text" v-model="formData.pavementThickness"   name="pavementThickness"></td>
            <td class="label">温度修正系数</td>
            <td><input type="text" v-model="formData.tempCorrection"   name="tempCorrection"></td>
        </tr>
        <tr>
            <td class="label">设计弯沉值<br>(0.01mm)</td>
            <td><input type="text" v-model="formData.designDeflection"   name="designDeflection"></td>
            <td class="label">沥青面层平均温度 (℃)</td>
            <td><input type="text" v-model="formData.avgAsphaltTemp"   name="avgAsphaltTemp"></td>
            <td class="label">泊松比μ</td>
            <td><input type="text" v-model="formData.poissonRatio"   name="poissonRatio"></td>
        </tr>
    </table>

    <!-- Table 3: Data Table -->
    <table>
        <colgroup>
            <col style="width: 10%;"> <!-- 序号 -->
            <col style="width: 25%;"> <!-- 测点桩号 -->
            <col style="width: 15%;"> <!-- 车道 -->
            <col style="width: 15%;"> <!-- 左侧 -->
            <col style="width: 20%;"> <!-- 右侧 -->
            <col style="width: 15%;"> <!-- 备注 -->
        </colgroup>
        <thead>
            <tr>
                <th>序号</th>
                <th>测点桩号 (幅段)</th>
                <th>车道</th>
                <th>左侧回弹弯沉值 (0.01mm)</th>
                <th>右侧回弹弯沉值 (0.01mm)</th>
                <th>备注</th>
            </tr>
        </thead>
        <tbody>
            <template v-for="(n, i_idx) in 5" :key="i_idx">
            <tr>
                <td>{{ (i_idx + 1) }}</td>
                <td><input type="text" :name="'station_' + (i_idx + 1)" v-model="formData['station_' + (i_idx + 1)]"></td>
                <td><input type="text" :name="'lane_' + (i_idx + 1)" v-model="formData['lane_' + (i_idx + 1)]"></td>
                <td><input type="text" :name="'leftDeflection_' + (i_idx + 1)" v-model="formData['leftDeflection_' + (i_idx + 1)]"></td>
                <td><input type="text" :name="'rightDeflection_' + (i_idx + 1)" v-model="formData['rightDeflection_' + (i_idx + 1)]"></td>
                <td><input type="text" :name="'remarks_' + (i_idx + 1)" v-model="formData['remarks_' + (i_idx + 1)]"></td>
            </tr>
            </template>
        </tbody>
    </table>

    <!-- Table 4: Statistics -->
    <table>
        <colgroup>
            <col style="width: 15%;">
            <col style="width: 10%;">
            <col style="width: 15%;">
            <col style="width: 10%;">
            <col style="width: 15%;">
            <col style="width: 10%;">
            <col style="width: 15%;">
            <col style="width: 10%;">
        </colgroup>
        <tr>
            <td class="label">(温度修正后)<br>平均弯沉值<br>(0.01mm)</td>
            <td><input type="text" v-model="formData.avgDeflection"   name="avgDeflection"></td>
            <td class="label">标准差<br>(0.01mm)</td>
            <td><input type="text" v-model="formData.stdDev"   name="stdDev"></td>
            <td class="label">弯沉代表值<br>(0.01mm)</td>
            <td><input type="text" v-model="formData.repDeflection"   name="repDeflection"></td>
            <td class="label">回弹模量<br>E<sub>1</sub>(MPa)</td>
            <td><input type="text" v-model="formData.resilientModulus"   name="resilientModulus"></td>
        </tr>
    </table>

    <!-- Table 5: Conclusion and Notes -->
    <table>
        <colgroup>
            <col style="width: 15%;">
            <col style="width: 85%;">
        </colgroup>
        <tr>
            <td class="label">检测结论</td>
            <td><input type="text" v-model="formData.testConclusion"   name="testConclusion" style="width: 100%; text-align: left;"></td>
        </tr>
        <tr>
            <td class="label">报告说明</td>
            <td><input type="text" v-model="formData.reportDesc"   name="reportDesc" style="width: 100%; text-align: left;"></td>
        </tr>
    </table>

    <div class="footer-section">
        <div class="footer-line">
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
                检测：
                <span class="signature-line"></span>
                <img v-if="formData.testerSignature" :src="formData.testerSignature" class="signature-img" alt="检测人签名" />
            </div>
        </div>

        <div class="company-info">
            <div>声明：</div>
            <div>1.对本检测报告的复印件未加盖公司检验检测专用章无效。2.对检测结果如有异议，应在收到报告之日起十五日之内向本公司提出。</div>
            <div style="margin-top: 10px;">公司名称：<input type="text" v-model="formData.companyName"   name="companyName" style="width: 300px; border-bottom: 1px solid black; text-align: left;"></div>
            <div class="footer-line" style="margin-top: 5px;">
                <div>公司地址：<input type="text" v-model="formData.companyAddress"   name="companyAddress" style="width: 300px; border-bottom: 1px solid black; text-align: left;"></div>
                <div>电话：<input type="text" v-model="formData.companyPhone"   name="companyPhone" style="width: 150px; border-bottom: 1px solid black; text-align: left;"></div>
            </div>
        </div>


    </div>
    </form>

    

  </div>
</template>

<script setup>
import { reactive, ref, onMounted, inject, defineProps } from 'vue'
import axios from 'axios'

const props = defineProps({
    id: String
})

const navigateTo = inject('navigateTo')

const goToList = () => {
  if (navigateTo) {
    navigateTo('BeckmanBeamReportList')
  }
}

const pdfForm = ref(null)

const formData = reactive({
  id: '',
  entrustmentId: '',
  status: 0,
  entrustingUnit: '',
  unifiedNumber: '',
  projectName: '',
  commissionDate: '',
  constructionPart: '',
  testDate: '',
  equipmentCode: '',
  reportDate: '',
  sampleStatus: '',
  testCategory: '',
  standard: '',
  testMethod: '',
  witnessUnit: '',
  witness: '',
  pavementType: '',
  pavementThickness: '',
  tempCorrection: '',
  designDeflection: '',
  avgAsphaltTemp: '',
  poissonRatio: '',
  avgDeflection: '',
  stdDev: '',
  repDeflection: '',
  resilientModulus: '',
  testConclusion: '',
  reportDesc: '',
  approver: '',
  recordReviewer: '',
  recordTester: '',
  filler: '',
  approverSignature: '',
  reviewerSignature: '',
  testerSignature: '',
  companyName: '',
  companyAddress: '',
  companyPhone: '',
})

onMounted(() => {
  if (props.id) {
    loadData(props.id)
  }

  // Initialize dynamic fields for loop variable 'i_idx'
  // 模板中使用的是 (i_idx + 1)，所以这里应该从 1 开始到 5
  for (let i_idx = 1; i_idx <= 5; i_idx++) {
    formData['lane_' + i_idx] = ''
    formData['station_' + i_idx] = ''
    formData['leftDeflection_' + i_idx] = ''
    formData['remarks_' + i_idx] = ''
    formData['rightDeflection_' + i_idx] = ''
  }

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
    // Role check
    // Logic: formData.recordTester (if set)
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
      // Role check
      // Logic: formData.recordReviewer (if set)
      if (formData.recordReviewer && user.username !== formData.recordReviewer && user.fullName !== formData.recordReviewer) {
          alert('您不是该单据的记录审核人 (' + formData.recordReviewer + ')，无权审核')
          return
      }
      // Auto-fetch signature for Reviewer
      try {
        const sigRes = await axios.post('/api/signature/get', { userAccount: user.username })
        if (sigRes.data.success && sigRes.data.data && sigRes.data.data.signatureBlob) {
             signatureData = sigRes.data.data.signatureBlob
             formData.reviewerSignature = `data:image/png;base64,${signatureData}`
             if (!formData.recordReviewer) {
                 formData.recordReviewer = user.fullName || user.username
             }
        } else {
             alert('未找到您的电子签名，无法审核通过')
             return
        }
      } catch (e) {
        console.error('Fetch signature error', e)
        alert('获取签名失败')
        return
      }
  } else if (action === 'SIGN_REVIEW') {
    if (formData.recordReviewer && user.username !== formData.recordReviewer && user.fullName !== formData.recordReviewer) {
        alert('您不是该单据的记录审核人 (' + formData.recordReviewer + ')，无权签字')
        return
    }
    // Auto-fetch signature for Reviewer (if using this action)
    try {
        const sigRes = await axios.post('/api/signature/get', { userAccount: user.username })
        if (sigRes.data.success && sigRes.data.data && sigRes.data.data.signatureBlob) {
             signatureData = sigRes.data.data.signatureBlob
        } else {
             alert('未找到您的电子签名，无法签字')
             return
        }
    } catch (e) {
        console.error('Fetch signature error', e)
        alert('获取签名失败')
        return
    }
  } else if (action === 'SIGN_APPROVE') {
    if (formData.approver && user.username !== formData.approver && user.fullName !== formData.approver) {
        alert('您不是该单据的批准人 (' + formData.approver + ')，无权签字')
        return
    }
    // Auto-fetch signature for Approver
    try {
        const sigRes = await axios.post('/api/signature/get', { userAccount: user.username })
        if (sigRes.data.success && sigRes.data.data && sigRes.data.data.signatureBlob) {
             signatureData = sigRes.data.data.signatureBlob
             formData.approverSignature = `data:image/png;base64,${signatureData}`
             if (!formData.approver) {
                 formData.approver = user.fullName || user.username
             }
        } else {
             alert('未找到您的电子签名，无法批准')
             return
        }
    } catch (e) {
        console.error('Fetch signature error', e)
        alert('获取签名失败')
        return
    }
  } else if (action === 'REJECT') {
      if (formData.status === 1) {
          if (formData.recordReviewer && user.username !== formData.recordReviewer && user.fullName !== formData.recordReviewer) {
              alert('您不是该单据的记录审核人，无权退回')
              return
          }
      } else if (formData.status === 4) {
          if (formData.approver && user.username !== formData.approver && user.fullName !== formData.approver) {
               alert('您不是该单据的批准人，无权退回')
               return
          }
      }
  }

  const request = {
    tableType: 'BECKMAN_BEAM',
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
      loadData(formData.entrustmentId || props.id)
    } else {
      alert('操作失败: ' + response.data.message)
    }
  } catch (e) {
    console.error('Workflow error', e)
    alert('操作异常')
  }
}

const loadData = async (entrustmentId) => {
  try {
    const entrustResponse = await axios.get('/api/jc-core-wt-info/by-id', {
      params: { id: entrustmentId }
    })
    if (entrustResponse.data.success) {
      const ent = entrustResponse.data.data
      formData.entrustmentId = entrustmentId
      formData.entrustingUnit = ent.clientUnit
      formData.unifiedNumber = ent.wtNum
      formData.projectName = ent.projectName
      formData.commissionDate = ent.commissionDate
      formData.constructionPart = ent.constructionPart
      formData.testCategory = ent.testCategory
      formData.witnessUnit = ent.witnessUnit
      formData.witness = ent.witness
      // Don't set tester/reviewer here, use directory fallback
    }

    const response = await axios.get('/api/beckman-beam/report/get-by-entrustment-id', {
      params: { entrustmentId }
    })

    let sourceJson = null
    let dbData = {}

    if (response.data.success && response.data.data) {
      const data = response.data.data
      dbData = data
      formData.id = data.id
      formData.entrustmentId = data.entrustmentId
      formData.status = data.status !== undefined ? data.status : 0

      if (data.dataJson) {
        sourceJson = data.dataJson
      }

      if (data.reviewSignaturePhoto) formData.reviewerSignature = data.reviewSignaturePhoto
      if (data.inspectSignaturePhoto) formData.testerSignature = data.inspectSignaturePhoto
      if (data.approveSignaturePhoto) formData.approverSignature = data.approveSignaturePhoto
    }

    if (!sourceJson) {
      try {
        const recordRes = await axios.get('/api/beckman-beam/get-by-entrustment-id', {
          params: { entrustmentId }
        })
        if (recordRes.data.success && recordRes.data.data && recordRes.data.data.length > 0) {
          const record = recordRes.data.data[0]
          // 检查记录表状态，只有审核通过(状态值5)才自动填充数据
          if (record.status === 5) {
            if (record.dataJson) {
              sourceJson = record.dataJson
            }
          } else {
            console.log('记录表状态未审核通过，不自动填充数据')
          }
        }
      } catch (e) {
        console.error('beckman report autofill error', e)
      }
    }

    if (sourceJson) {
      try {
        const parsedData = JSON.parse(sourceJson)
        console.log('BeckmanBeamReport loadData - parsedData keys:', Object.keys(parsedData).filter(k => k.startsWith('station_') || k.startsWith('lane_') || k.startsWith('leftDeflection_') || k.startsWith('rightDeflection_')))
        
        // Map legacy fields
        if (parsedData.tester && !parsedData.recordTester) parsedData.recordTester = parsedData.tester
        if (parsedData.reviewer && !parsedData.recordReviewer) parsedData.recordReviewer = parsedData.reviewer
        
        // 字段名映射：后端 -> 前端
        // tempCorrectionK -> tempCorrection
        if (parsedData.tempCorrectionK && !parsedData.tempCorrection) {
          parsedData.tempCorrection = parsedData.tempCorrectionK
        }
        // tempCorrectedAvg -> avgDeflection
        if (parsedData.tempCorrectedAvg && !parsedData.avgDeflection) {
          parsedData.avgDeflection = parsedData.tempCorrectedAvg
        }
        // sampleNameStatus -> sampleStatus
        if (parsedData.sampleNameStatus && !parsedData.sampleStatus) {
          parsedData.sampleStatus = parsedData.sampleNameStatus
        }
        
        // 合并所有字段，包括测点数据
        Object.assign(formData, parsedData)
        
        console.log('BeckmanBeamReport loadData - after assign, formData station_1:', formData.station_1, 'lane_1:', formData.lane_1, 'leftDeflection_1:', formData.leftDeflection_1)
      } catch (e) {
        console.error('beckman report autofill parse error', e)
      }
    }
    
    // Directory Fallback
    // const currentDirectory = JSON.parse(localStorage.getItem('currentDirectory') || '{}')
    const ent = entrustResponse.data.data || {}
    
    // Filler - Priority: record.filler
    formData.filler = formData.filler || ''
    
    // Record Tester - Priority: record.recordTester -> ent.tester
    formData.recordTester = formData.recordTester || ent.tester || ''
    
    // Record Reviewer - Priority: record.recordReviewer -> ent.reviewer
    formData.recordReviewer = formData.recordReviewer || ent.reviewer || ''
    
    // Approver - Priority: record.approver
    formData.approver = formData.approver || ''

    // Set filler to current user if new
    const user = JSON.parse(localStorage.getItem('userInfo') || '{}')
    if (user.username && !formData.filler) {
       formData.filler = user.username
    }

  } catch (error) {
    console.error('Failed to load data', error)
  }
}

const saveData = async () => {
    try {
        const dataJsonObj = { ...formData }
        // Remove legacy fields from JSON
        delete dataJsonObj.tester
        delete dataJsonObj.reviewer

        const dataToSave = {
            id: formData.id,
            entrustmentId: formData.entrustmentId || props.id,
            dataJson: JSON.stringify(dataJsonObj),
            reviewSignaturePhoto: formData.reviewerSignature,
            inspectSignaturePhoto: formData.testerSignature,
            approveSignaturePhoto: formData.approverSignature,
            recordTester: formData.recordTester,
            recordReviewer: formData.recordReviewer,
            // Sync legacy fields
            tester: formData.recordTester,
            reviewer: formData.recordReviewer,
            filler: formData.filler,
            approver: formData.approver,
            // Other fields
            testDate: formData.testDate,
            reportDate: formData.reportDate
        }
        
        const response = await axios.post('/api/beckman-beam/report/save', dataToSave)
        if (response.data.success) {
            alert('保存成功')
            // If new record, update id
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

            const currentName = user.fullName || user.username

            // 点击签名时只在检测那里加上电子签名
            formData.testerSignature = imgSrc
            if (!formData.recordTester) {
                formData.recordTester = currentName
            }

            alert('检测人签名成功')
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
    pdfForm.value.action = '/api/pdf/beckman_beam_report/generate'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}

const previewPdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/beckman_beam_report/preview'
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
        .status-text {
            font-size: 14px;
            font-weight: 500;
            color: #666;
            margin-left: 8px;
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

        .beckmanBeamReport-container {
            font-family: 'SimSun', 'Songti SC', serif;
            width: 210mm;
            margin: 0 auto;
            padding: 20px;
        }
        h1 {
            text-align: center;
            font-size: 24px;
            margin-bottom: 20px;
        }
        .header-info {
            display: flex;
            justify-content: space-between;
            margin-bottom: 5px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            border: 1px solid black;
            table-layout: fixed;
            margin-bottom: -1px; /* Collapse borders between tables */
        }
        td, th {
            border: 1px solid black;
            padding: 5px;
            text-align: center;
            font-size: 14px;
            vertical-align: middle;
            word-wrap: break-word;
        }
        input[type="text"] {
            width: 95%;
            border: none;
            outline: none;
            text-align: center;
            font-family: inherit;
            font-size: inherit;
            background: transparent;
        }
        .label {
            text-align: center;
        }
        /* Hide navigation buttons when printing */
        @media print {
            .no-print {
                display: none;
            }
            .beckmanBeamReport-container {
                width: 100%;
                margin: 0;
                padding: 0;
            }
            @page {
                size: A4 portrait;
                margin: 1cm;
            }
        }
        .nav-button {
            display: inline-block;
            margin: 10px;
            padding: 10px 20px;
            background-color: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 5px;
        }
        .footer-section {
            margin-top: 20px;
        }
        .footer-line {
            display: flex;
            justify-content: space-between;
            margin-bottom: 10px;
        }
        .company-info {
            margin-top: 20px;
            font-size: 14px;
        }

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
            top: -20px;
            width: 80px;
            height: 40px;
            mix-blend-mode: multiply;
        }
    
</style>
