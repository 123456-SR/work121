<template>
  <div class="reboundMethodReport-container">


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
          @click="submitForm"
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

    <h2>回弹法检测混凝土抗压强度报告</h2>

    <form id="pdfForm" ref="pdfForm" method="post">
    <div class="header-top">
        <span>委托单位：<input type="text" v-model="formData.entrustingUnit"   name="entrustingUnit" style="width: 200px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>统一编号：<input type="text" v-model="formData.unifiedNumber"   name="unifiedNumber" style="width: 150px; border-bottom: 1px solid black;"></span>
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
            <span class="signature-line">{{ formData.approver }}</span>
            <img v-if="formData.approverSignature" :src="formData.approverSignature" class="signature-img" alt="批准人签名" />
        </div>
        <div class="signature-box">
            审核：
            <span class="signature-line">{{ formData.recordReviewer }}</span>
            <img v-if="formData.reviewerSignature" :src="formData.reviewerSignature" class="signature-img" alt="审核人签名" />
        </div>
        <div class="signature-box">
            检验：
            <span class="signature-line">{{ formData.recordTester }}</span>
            <img v-if="formData.testerSignature" :src="formData.testerSignature" class="signature-img" alt="检测人签名" />
        </div>
    </div>

    <div class="disclaimer">
        声明：<br>
        1. 对本检验报告的复印件未加盖公司检验检测专用章无效。 2. 对检验结果如有异议，应在收到报告之日起十五日之内向本公司提出。
    </div>

    <div class="company-info">
        <div>公司名称：<input type="text" v-model="formData.companyName"   name="companyName" style="width: 70%; border-bottom: 1px solid black; text-align: left;" value="河北金涛建设工程质量检测有限公司"></div>
        <div style="display: flex; justify-content: space-between; margin-top: 5px;">
            <span>公司地址：<input type="text" v-model="formData.companyAddress"   name="companyAddress" style="width: 300px; border-bottom: 1px solid black; text-align: left;" value="石家庄高新区方亿科技工业园A区第2号楼。"></span>
            <span>电话：<input type="text" v-model="formData.companyPhone"   name="companyPhone" style="width: 200px; border-bottom: 1px solid black;" value="0311—86107634  0311—67300616"></span>
        </div>
    </div>


    </form>

    


  </div>
</template>

<script setup>
import { reactive, ref, onMounted, inject, defineProps } from 'vue'
import axios from 'axios'

const navigateTo = inject('navigateTo')

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
})

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
      if (!formData.filler && userInfo) {
          formData.filler = userInfo.userName
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
    case 0: return '#6c757d' // secondary
    case 1: return '#007bff' // primary
    case 2: return '#dc3545' // danger
    case 3: return '#ffc107' // warning
    case 4: return '#17a2b8' // info
    case 5: return '#28a745' // success
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
    if (formData.recordTester && user.username !== formData.recordTester && user.fullName !== formData.recordTester) {
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
      if (formData.recordReviewer && user.username !== formData.recordReviewer && user.fullName !== formData.recordReviewer) {
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
    if (formData.approver && user.username !== formData.approver && user.fullName !== formData.approver) {
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
      if (formData.status === 1 && formData.recordReviewer && user.username !== formData.recordReviewer && user.fullName !== formData.recordReviewer) {
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
      const currentName = user.fullName || user.username
      const currentAccount = user.username

      // Match Tester
      if (!formData.recordTester || formData.recordTester === currentName || formData.recordTester === currentAccount) {
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

const goToList = () => {
  if (navigateTo) {
    navigateTo('ReboundMethodReportList')
  }
}

const pdfForm = ref(null)

const printDocument = () => {
  window.print()
}

const generatePdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/rebound_method_report/generate'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}

const previewPdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/rebound_method_report/preview'
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
        .reboundMethodReport-container {
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
        .disclaimer {
            margin-top: 20px;
            font-size: 12px;
            line-height: 1.5;
        }
        .company-info {
            margin-top: 10px;
            font-size: 14px;
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
            top: -20px;
            width: 80px;
            height: 40px;
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
