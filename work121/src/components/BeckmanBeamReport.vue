<template>
  <div class="beckmanBeamReport-container">

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
    <!-- 电子签名隐藏字段 -->
    <input type="hidden" v-model="formData.testerSignature" name="testerSignature">
    <input type="hidden" v-model="formData.reviewerSignature" name="reviewerSignature">
    <input type="hidden" v-model="formData.approverSignature" name="approverSignature">
    <h1>路基路面回弹弯沉（回弹模量）检测报告</h1>

    <div class="header-info">
        <div>委托单位：<input type="text" v-model="formData.entrustingUnit"   name="entrustingUnit" style="width: 200px; border-bottom: 1px solid black; text-align: left;"></div>
        <div>统一编号：<input type="text" v-model="formData.unifiedNumber"   name="unifiedNumber" style="width: 150px; border-bottom: 1px solid black;" disabled></div>
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
            <div>1.对本检测报告的复印件未加盖公司检验检测专用章无效。</div>
            <div>2.对检测结果如有异议，应在收到报告之日起十五日之内向本公司提出。</div>
            <div style="margin-top: 10px;">公司名称：河北金涛建设工程质量检测有限公司</div>
            <div class="footer-line" style="margin-top: 5px;">
                <div>公司地址：石家庄高新区方亿科技工业园区A区第2号楼</div>
                <div>电话：0311—86107634 67300616</div>
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
  const s = parseInt(status)
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
      } else if (formData.status === 5) {
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
      // 如果是批准操作，保存签名到数据库
      if (action === 'SIGN_APPROVE') {
        await saveData()
      }
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
      formData.commissionDate = formatDate(ent.commissionDate)
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

  } catch (error) {
    console.error('Failed to load data', error)
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
        
        const dataJsonObj = { ...formData }
        // Remove legacy fields from JSON
        delete dataJsonObj.tester
        delete dataJsonObj.reviewer

        const dataToSave = {
            id: formData.id,
            entrustmentId: formData.entrustmentId || props.id,
            status: formData.status, // 传递状态字段给后端
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
            // 保存成功后返回列表页面，确保列表显示更新后的状态
            if (navigateTo) {
                navigateTo('BeckmanBeamReportList')
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

            const currentName = user.userName

            // 点击签名时只在检测那里加上电子签名
            formData.testerSignature = imgSrc
            if (!formData.recordTester) {
                formData.recordTester = currentName
            }

            // 保存签名到数据库
            await saveData()
            await submitWorkflow('SIGN_REVIEW')
            alert('检测人签名成功并已保存')
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

const openBackendPdfPreview = () => {
  if (!pdfForm.value) return
  const container = pdfForm.value.closest('.beckmanBeamReport-container')
  if (!container) return

  const escapeAttr = (v) => String(v ?? '')
    .replace(/&/g, '&amp;')
    .replace(/"/g, '&quot;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')

  const mmToPx = (mm) => mm * 96 / 25.4
  const pageWidthMm = 210
  const pageHeightMm = 297
  const marginMm = 0
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
  const marginWantedMm = 2
  const marginWantedPx = mmToPx(marginWantedMm)
  const targetWidthPx = Math.max(availableWidthPx - marginWantedPx * 2, 1)
  const targetHeightPx = Math.max(availableHeightPx - marginWantedPx * 2, 1)
  const pdfScale = Math.min(targetWidthPx / contentWidthPx, targetHeightPx / contentHeightPx)
  const scaledWidthPx = contentWidthPx * pdfScale
  const scaledHeightPx = contentHeightPx * pdfScale
  const pdfOffsetXPx = Math.max(marginWantedPx, (availableWidthPx - scaledWidthPx) / 2)
  const pdfOffsetYPx = Math.max(marginWantedPx, (availableHeightPx - scaledHeightPx) / 2)

  const buildHtmlSnapshotHtml = () => {
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
      .pdf-sheet { width: 210mm; height: 297mm; padding: 0; box-sizing: border-box; overflow: hidden; }
      .pdf-page { width: 210mm; height: 297mm; overflow: hidden; box-sizing: border-box; position: relative; }
      .pdf-content { position: absolute; left: 0; top: 0; transform-origin: top left; }
      .pdf-preview.beckmanBeamReport-container { width: 100%; height: 100%; max-width: 100%; min-width: 0; margin: 0; padding: 0; box-sizing: border-box; display: flex; flex-direction: column; }
      .pdf-preview { overflow: visible; }
      .pdf-preview * { page-break-inside: avoid; break-inside: avoid; }
      .pdf-preview [data-name] { width: auto !important; max-width: 100% !important; box-sizing: border-box; overflow-wrap: anywhere; word-break: break-all; white-space: pre-wrap; }
      .pdf-preview #pdfForm { flex: 1; min-height: 0; display: flex; flex-direction: column; }
      .pdf-preview #pdfForm > table { flex: 0 0 auto; height: auto; margin-top: auto; margin-bottom: auto; }
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
    return html
  }

  const html = buildHtmlSnapshotHtml()
  const w = window.open('', '_blank')
  if (!w) return
  w.document.open()
  w.document.write(html)
  w.document.close()
}

const generatePdf = () => {
  openBackendPdfPreview()
}

const previewPdf = () => {
  openBackendPdfPreview()
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

        .beckmanBeamReport-container {
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
        h1 {
            text-align: center;
            font-size: 24px;
            margin-bottom: 20px;
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
            margin-bottom: -1px; /* Collapse borders between tables */
        }
        td, th {
            border: 1px solid black;
            padding: 8px 5px;
            text-align: center;
            font-size: inherit;
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
        input[type="text"]:disabled:focus {
            background-color: transparent;
            outline: none;
            border-color: black;
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
                margin: 0;
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
            font-size: 12px;
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
            top: -16px;
            height: 18px;
            width: auto;
            max-width: 80px;
            object-fit: contain;
            mix-blend-mode: multiply;
        }
    
</style>
