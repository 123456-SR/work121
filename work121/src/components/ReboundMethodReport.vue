<template>
  <div class="reboundMethodReport-container" ref="containerRef">


    <div class="no-print toolbar">
      <div class="toolbar-left">
        <button @click="goToList" class="link-button">&lt; 返回列表</button>
      </div>

      <div class="toolbar-right">
        <div v-if="formData.status !== undefined" class="status-text">
          状态:
          <span :style="{ color: getStatusColor(formData.status) }" class="status-label">
            {{ getStatusText(formData.status) }}
          </span>
        </div>
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

    <h2>回弹法检测混凝土抗压强度报告</h2>

    <form id="pdfForm" ref="pdfForm" method="post">
    <!-- 电子签名隐藏字段 -->
    <input type="hidden" v-model="formData.testerSignature" name="testerSignature">
    <input type="hidden" v-model="formData.reviewerSignature" name="reviewerSignature">
    <input type="hidden" v-model="formData.approverSignature" name="approverSignature">
    <div class="header-top">
        <span>委托单位：<input type="text" v-model="formData.entrustingUnit"   name="entrustingUnit" style="width: 200px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>统一编号：<input type="text" v-model="formData.unifiedNumber"   name="unifiedNumber" style="width: 150px; border-bottom: 1px solid black;" disabled></span>
    </div>
    <div class="header-top">
        <span>样品编号：<input type="text" v-model="formData.sampleNumber"   name="sampleNumber" style="width: 200px; border-bottom: 1px solid black; text-align: left;"></span>
    </div>

    <!-- Table with 12-column grid -->
    <table>
        <colgroup>
            <template v-for="(n, i_idx) in 12" :key="i_idx">
            <col style="width: 8.33%;">
            </template>
        </colgroup>

        <!-- Row 1 -->
        <tr>
            <td colspan="2" class="label">工程名称</td>
            <td colspan="6"><input type="text" v-model="formData.projectName"   name="projectName"></td>
            <td colspan="2" class="label">委托日期</td>
            <td colspan="2"><input type="text" v-model="formData.commissionDate"   name="commissionDate"></td>
        </tr>

        <!-- Row 2 -->
        <tr>
            <td colspan="2" class="label">结构部位</td>
            <td colspan="6"><input type="text" v-model="formData.structurePart"   name="structurePart"></td>
            <td colspan="2" class="label">检测日期</td>
            <td colspan="2"><input type="text" v-model="formData.testDate"   name="testDate"></td>
        </tr>

        <!-- Row 3 -->
        <tr>
            <td colspan="2" class="label">样品状态</td>
            <td colspan="2"><input type="text" v-model="formData.sampleStatus"   name="sampleStatus"></td>
            <td colspan="2" class="label">测试角度</td>
            <td colspan="2"><input type="text" v-model="formData.testAngle"   name="testAngle"></td>
            <td colspan="2" class="label">报告日期</td>
            <td colspan="2"><input type="text" v-model="formData.reportDate"   name="reportDate"></td>
        </tr>

        <!-- Row 4 -->
        <tr>
            <td colspan="2" class="label">见证单位</td>
            <td colspan="6"><input type="text" v-model="formData.witnessUnit"   name="witnessUnit"></td>
            <td colspan="2" class="label">见证人</td>
            <td colspan="2"><input type="text" v-model="formData.witnessPerson"   name="witnessPerson"></td>
        </tr>

        <!-- Row 5 -->
        <tr>
            <td colspan="2" class="label">设计指标</td>
            <td colspan="2"><input type="text" v-model="formData.designIndex"   name="designIndex"></td>
            <td colspan="2" class="label">检测类别</td>
            <td colspan="2"><input type="text" v-model="formData.testCategory"   name="testCategory"></td>
            <td colspan="2" class="label">浇筑日期</td>
            <td colspan="2"><input type="text" v-model="formData.pourDate"   name="pourDate"></td>
        </tr>

        <!-- Row 6 -->
        <tr>
            <td colspan="2" class="label">碳化深度 mm</td>
            <td colspan="2"><input type="text" v-model="formData.carbonDepth"   name="carbonDepth"></td>
            <td colspan="4" class="label">构件厚度或骨料最大粒径</td>
            <td colspan="4"><input type="text" v-model="formData.aggregateSize"   name="aggregateSize"></td>
        </tr>

        <!-- Row 7: Zone No (10 zones) -->
        <tr>
            <td colspan="2" class="label">测区号</td>
            <template v-for="(n, i_idx) in 10" :key="i_idx">
            <td colspan="1">{{ (i_idx + 1) }}</td>
            </template>
        </tr>

        <!-- Row 8: Carbonation Corrected Strength -->
        <tr>
            <td colspan="2" class="label">碳化修正强度<br>MPa</td>
            <template v-for="(n, i_idx) in 10" :key="i_idx">
            <td colspan="1"><input type="text" :name="'correctedStrength_' + (i_idx + 1)" v-model="formData['correctedStrength_' + (i_idx + 1)]"></td>
            </template>
        </tr>

        <!-- Row 9: Strength Estimation -->
        <tr>
            <td colspan="2" class="label">构件强度推定<br>值 MPa</td>
            <td colspan="2"><input type="text" v-model="formData.compEstimatedStrength"   name="compEstimatedStrength"></td>
            <td colspan="2" class="label">标准差 MPa</td>
            <td colspan="2"><input type="text" v-model="formData.stdDev"   name="stdDev"></td>
            <td colspan="2" class="label">变异系数%</td>
            <td colspan="2"><input type="text" v-model="formData.coefVariation"   name="coefVariation"></td>
        </tr>

        <!-- Row 10: Equipment -->
        <tr>
            <td colspan="2" class="label">仪器设备</td>
            <td colspan="10" class="left-align"><input type="text" v-model="formData.equipment"   name="equipment" style="text-align: left;"></td>
        </tr>

        <!-- Row 11: Standard -->
        <tr>
            <td colspan="2" class="label">依据标准</td>
            <td colspan="10" class="left-align"><input type="text" v-model="formData.standard"   name="standard" style="text-align: left;"></td>
        </tr>

        <!-- Row 12: Conclusion -->
        <tr>
            <td colspan="2" class="label">结论</td>
            <td colspan="10" class="left-align" style="height: 60px;">
                <textarea v-model="formData.conclusion"  name="conclusion" style="width: 100%; height: 100%;"></textarea>
            </td>
        </tr>

        <!-- Row 13: Diagram -->
        <tr>
            <td colspan="12" class="left-align" style="height: 150px; vertical-align: top;">
                <span class="label">测区示意图：</span>
                <!-- Placeholder for diagram -->
            </td>
        </tr>

        <!-- Row 14: Remarks -->
        <tr>
            <td colspan="1" class="label">备注：</td>
            <td colspan="11" class="left-align" style="height: 40px;">
                <textarea v-model="formData.remarks"  name="remarks" style="width: 100%; height: 100%;"></textarea>
            </td>
        </tr>
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

    <div class="disclaimer">
        声明：<br>
        1. 对本检验报告的复印件未加盖公司检验检测专用章无效。<br>
        2. 对检验结果如有异议，应在收到报告之日起十五日之内向本公司提出。
    </div>

    <div class="company-info">
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
import { reactive, ref, onMounted, inject, defineProps, nextTick } from 'vue'
import axios from 'axios'

const navigateTo = inject('navigateTo')
const containerRef = ref(null)

// 定义props
const props = defineProps({
  id: {
    type: String,
    default: null
  }
})

const formData = reactive({
  id: '',
  entrustingUnit: '',
  status: 0,
  unifiedNumber: '',
  sampleNumber: '',
  projectName: '',
  commissionDate: '',
  structurePart: '',
  testDate: '',
  sampleStatus: '',
  testAngle: '',
  reportDate: '',
  witnessUnit: '',
  witnessPerson: '',
  designIndex: '',
  testCategory: '',
  pourDate: '',
  carbonDepth: '',
  aggregateSize: '',
  compEstimatedStrength: '',
  stdDev: '',
  coefVariation: '',
  equipment: '',
  standard: '',
  recordTester: '',
  recordReviewer: '',
  filler: '',
  approver: '',
  companyName: '',
  companyAddress: '',
  companyPhone: '',
  conclusion: '',
  remarks: '',
  approverSignature: '',
  reviewerSignature: '',
  testerSignature: ''
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

  // Initialize dynamic fields for loop variable 'i_idx'
  // Please verify the loop count match the template
  for (let i_idx = 0; i_idx < 10; i_idx++) {
    formData['correctedStrength_' + i_idx] = ''
  }

  // Load data if ID is present
  if (props.id) {
    loadData(props.id)
  } else {
    // Check URL params
    const urlParams = new URLSearchParams(window.location.search)
    const id = urlParams.get('id')
    if (id) {
      loadData(id)
    }
  }

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

// 加载数据
const loadData = async (id) => {
  try {
    const response = await axios.get(`/api/reboundMethod/report/get-by-entrustment-id`, {
        params: { entrustmentId: id }
    })
    
    if (response.data.success && response.data.data) {
      const data = response.data.data
      
      // Store the record ID (Primary Key)
      formData.id = data.id

      // Merge data into formData
      // First, if dataJson exists, parse it
      if (data.dataJson) {
        try {
            const jsonData = JSON.parse(data.dataJson)
            Object.assign(formData, jsonData)
        } catch (e) {
            console.error('Failed to parse dataJson', e)
        }
      }

      formData.status = data.status !== undefined ? data.status : 0
      
      // Basic fields mapping (if not covered by dataJson or to ensure entity fields take precedence)
      // Note: ReboundMethodReport entity fields might be different from formData keys
      // formData uses 'entrustingUnit', entity uses 'clientUnit'. 
      // Need to map standard BusinessEntity fields if they are not in dataJson or if we want to sync with entity.
      // Usually dataJson contains the full formData state.
      
      // Signatures
      if (data.approveSignaturePhoto) formData.approverSignature = data.approveSignaturePhoto
      if (data.reviewSignaturePhoto) formData.reviewerSignature = data.reviewSignaturePhoto
      if (data.inspectSignaturePhoto) formData.testerSignature = data.inspectSignaturePhoto
      
      // Ensure user names are populated if missing
      const userInfoStr = localStorage.getItem('userInfo')
      let userInfo = null
      if (userInfoStr) {
          userInfo = JSON.parse(userInfoStr)
      }

      // 1. Map from dataJson
      if (data.dataJson) {
        try {
            const jsonData = JSON.parse(data.dataJson)
            Object.assign(formData, jsonData)
            // Legacy mapping
            if (jsonData.tester && !formData.recordTester) formData.recordTester = jsonData.tester
            if (jsonData.reviewer && !formData.recordReviewer) formData.recordReviewer = jsonData.reviewer
            // 见证人字段映射：witness -> witnessPerson
            if (jsonData.witness && !formData.witnessPerson) {
                formData.witnessPerson = jsonData.witness
            }
            if (jsonData.witnessUnit && !formData.witnessUnit) {
                formData.witnessUnit = jsonData.witnessUnit
            }
            // 样品编号字段映射：兼容 sampleNumber / sampleNo / sampleName
            if (!formData.sampleNumber) {
                if (jsonData.sampleNumber) {
                    formData.sampleNumber = jsonData.sampleNumber
                } else if (jsonData.sampleNo) {
                    formData.sampleNumber = jsonData.sampleNo
                } else if (jsonData.sampleName) {
                    formData.sampleNumber = jsonData.sampleName
                }
            }
        } catch (e) {
            console.error('Failed to parse dataJson', e)
        }
      }

      // 2. Map from record entity fields (if they exist in backend entity)
      if (data.recordTester) formData.recordTester = data.recordTester
      if (data.recordReviewer) formData.recordReviewer = data.recordReviewer
      if (data.filler) formData.filler = data.filler
      if (data.approver) formData.approver = data.approver

      // 3. Directory fallback for new/empty fields
      const directory = JSON.parse(localStorage.getItem('currentDirectory') || '{}')
      
      if (!formData.recordTester) {
          formData.recordTester = directory.tester || directory.jcTester || (userInfo ? userInfo.userName : '')
      }
      if (!formData.recordReviewer) {
          formData.recordReviewer = directory.reviewer || directory.jcReviewer || ''
      }
      if (!formData.approver) {
          formData.approver = directory.approver || directory.bgApprover || ''
      }

    } else {
        console.log('No report data found, trying to load record data or init defaults')
        // Optional: Load basic info from entrustment or record if report doesn't exist yet
    }
  } catch (error) {
    console.error('Failed to load data:', error)
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

const submitWorkflow = async (action) => {
  if (!props.id && !formData.unifiedNumber) {
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
    // Priority: formData.recordTester -> directory.jcTester
    if (formData.recordTester && user.username !== formData.recordTester && user.userName !== formData.recordTester) {
        const directory = JSON.parse(localStorage.getItem('currentDirectory') || '{}')
        if (directory.jcTester && directory.jcTester !== user.username) {
            alert('您不是该单据的记录检测人 (' + formData.recordTester + ')，无权提交')
            return
        }
    }
    if (!formData.testerSignature) {
      alert('请先进行检测人签字')
      return
    }
    signatureData = formData.testerSignature.replace(/^data:image\/\w+;base64,/, '')
  } else if (action === 'AUDIT_PASS') {
      // Role check
      // Priority: formData.recordReviewer -> directory.jcReviewer
      if (formData.recordReviewer && user.username !== formData.recordReviewer && user.userName !== formData.recordReviewer) {
          const directory = JSON.parse(localStorage.getItem('currentDirectory') || '{}')
          if (directory.jcReviewer && directory.jcReviewer !== user.username) {
              alert('您不是该单据的记录审核人 (' + formData.recordReviewer + ')，无权审核')
              return
          }
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
    if (formData.recordReviewer && user.username !== formData.recordReviewer && user.userName !== formData.recordReviewer) {
        const directory = JSON.parse(localStorage.getItem('currentDirectory') || '{}')
        if (directory.jcReviewer && directory.jcReviewer !== user.username) {
            alert('您不是该单据的记录审核人 (' + formData.recordReviewer + ')，无权签字')
            return
        }
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
    if (formData.approver && user.username !== formData.approver && user.userName !== formData.approver) {
        const directory = JSON.parse(localStorage.getItem('currentDirectory') || '{}')
        if (directory.bgApprover && directory.bgApprover !== user.username) {
            alert('您不是该单据的批准人 (' + formData.approver + ')，无权签字')
            return
        }
    }
    // Auto-fetch signature for Approver
    try {
        const sigRes = await axios.post('/api/signature/get', { userAccount: user.username })
        if (sigRes.data.success && sigRes.data.data && sigRes.data.data.signatureBlob) {
             signatureData = sigRes.data.data.signatureBlob
             formData.approverSignature = `data:image/png;base64,${signatureData}`
             if (!formData.approver) {
                 formData.approver = user.userName || user.username
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
      if (formData.status === 1 && formData.recordReviewer && user.username !== formData.recordReviewer && user.userName !== formData.recordReviewer) {
          const directory = JSON.parse(localStorage.getItem('currentDirectory') || '{}')
          if (directory.jcReviewer && directory.jcReviewer !== user.username) {
              alert('您不是该单据的记录审核人，无权退回')
              return
          }
      }
  }

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
      // 如果是批准操作，保存签名到数据库
      if (action === 'SIGN_APPROVE') {
        await submitForm()
      }
      alert('操作成功')
      loadData(props.id)
    } else {
      alert('操作失败: ' + response.data.message)
    }
  } catch (e) {
    console.error('Workflow error', e)
    alert('操作异常')
  }
}

const submitForm = async () => {
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
    if (formData.pourDate) {
      formData.pourDate = formatDate(formData.pourDate)
    }
    
    // 构建提交数据
    const submitData = {
      entrustmentId: props.id || formData.unifiedNumber, // Use props.id as entrustmentId
      ...formData, // Include all form data
      // Sync legacy fields
      tester: formData.recordTester,
      reviewer: formData.recordReviewer,
      approveSignaturePhoto: formData.approverSignature,
      reviewSignaturePhoto: formData.reviewerSignature,
      inspectSignaturePhoto: formData.testerSignature
    };

    // Serialize to dataJson
    // Remove legacy fields from dataJson to avoid confusion
    const dataJsonObj = { ...formData }
    delete dataJsonObj.tester
    delete dataJsonObj.reviewer
    submitData.dataJson = JSON.stringify(dataJsonObj);

    // 发送请求
    const response = await axios.post('/api/reboundMethod/report/save', submitData);

    if (response.data.success) {
      alert('保存成功');
      // 保存成功后返回列表页面，确保列表显示更新后的状态
      if (navigateTo) {
        navigateTo('ReboundMethodReportList');
      }
    } else {
      alert('保存失败: ' + response.data.message);
    }
  } catch (error) {
    console.error('保存错误:', error);
    alert('保存失败，请稍后重试');
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

    await submitForm()
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
        formData.testerSignature = imgSrc
        signed = true
      }

      if (signed) {
        // 保存签名到数据库
        await submitForm()
        await submitWorkflow('SIGN_REVIEW')
        alert('签名成功并已保存')
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

const goToList = () => {
  if (navigateTo) {
    navigateTo('ReboundMethodReportList')
  }
}

const pdfForm = ref(null)

const printDocument = () => {
  window.print()
}

const openClientPdfPreview = () => {
  if (!pdfForm.value) return
  const container = pdfForm.value.closest('.reboundMethodReport-container')
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
      .pdf-preview.reboundMethodReport-container { width: 100%; height: 100%; margin: 0; padding: 0; box-sizing: border-box; display: flex; flex-direction: column; }
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
  const container = pdfForm.value.closest('.reboundMethodReport-container')
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

    const styleNodes = Array.from(document.querySelectorAll('link[rel=\"stylesheet\"], style'))
    const stylesHtml = styleNodes.map(n => n.outerHTML).join('\n')
    const html = `<!doctype html>
<html lang=\"zh-CN\">
  <head>
    <meta charset=\"utf-8\" />
    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />
    ${stylesHtml}
    <style>
      @page { size: A4 portrait; margin: 0; }
      html, body { margin: 0; padding: 0; background: #fff; width: 210mm; height: 297mm; overflow: hidden; }
      .pdf-sheet { width: 210mm; height: 297mm; padding: 6mm; box-sizing: border-box; overflow: hidden; }
      .pdf-page { width: 198mm; height: 285mm; overflow: hidden; box-sizing: border-box; position: relative; }
      .pdf-content { position: absolute; left: 0; top: 0; transform-origin: top left; }
      .pdf-preview.reboundMethodReport-container { width: 198mm; height: 285mm; max-width: 198mm; min-width: 0; margin: 0; padding: 0; box-sizing: border-box; display: flex; flex-direction: column; }
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
      return `<input type=\"hidden\" name=\"${escapeAttr(name)}\" value=\"${escapeAttr(el.value)}\" />`
    }

    if (el.tagName.toLowerCase() === 'textarea') {
      return `<input type=\"hidden\" name=\"${escapeAttr(name)}\" value=\"${escapeAttr(el.value)}\" />`
    }

    const type = (el.getAttribute('type') || '').toLowerCase()
    if (type === 'file' || type === 'button' || type === 'submit' || type === 'reset') return ''

    if (type === 'checkbox' || type === 'radio') {
      if (!el.checked) return ''
      return `<input type=\"hidden\" name=\"${escapeAttr(name)}\" value=\"${escapeAttr(el.value || 'on')}\" />`
    }

    return `<input type=\"hidden\" name=\"${escapeAttr(name)}\" value=\"${escapeAttr(el.value)}\" />`
  }).join('\n') + `\n<input type=\"hidden\" name=\"__pdf_html_base64\" value=\"${escapeAttr(snapshotBase64)}\" />\n`

  const html = `<!doctype html>
<html lang=\"zh-CN\">
  <head>
    <meta charset=\"utf-8\" />
    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />
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
  <body onload=\"var f=document.getElementById('pdfPostForm'); if (f) f.submit();\">
    <div class=\"frame-shell\">
      <iframe name=\"pdfFrame\" title=\"PDF预览\"></iframe>
    </div>
    <form id=\"pdfPostForm\" method=\"post\" action=\"${escapeAttr(actionUrl)}\" target=\"pdfFrame\">
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
    openClientPdfPreview()
  }
}

const previewPdf = () => {
  if (pdfForm.value) {
    openClientPdfPreview()
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
        .reboundMethodReport-container {
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
            word-break: break-all;
        }
        td {
            border: 1px solid black;
            padding: 8px 5px;
            vertical-align: middle;
            text-align: center;
            font-size: inherit;
            overflow: hidden;
        }
        .label {
            font-weight: bold;
            white-space: nowrap;
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
        input[type="text"]:disabled:focus, textarea:disabled:focus {
            background-color: transparent;
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
            font-weight: normal;
        }
        .page-footer {
            margin-top: 5px;
            display: flex;
            justify-content: space-between;
            font-size: 12px;
        }
        .disclaimer {
            margin-top: 20px;
            font-size: 12px;
            line-height: 1.5;
        }
        .company-info {
            margin-top: 10px;
            font-size: 12px;
        }

        /* Signature styles */
        .signature-box {
            position: relative;
            display: inline-block;
            min-width: 120px;
            vertical-align: bottom;
            margin-right: 20px;
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
        
        @media print {
            .reboundMethodReport-container {
                width: 100%;
                margin: 0;
                padding: 0;
            }
            .no-print {
                display: none;
            }
        }
    
</style>
