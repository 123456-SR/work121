<template>
  <div class="beckmanBeamReport-container">

    <div class="no-print" style="margin-bottom: 20px;">
        <button @click="goToList" style="text-decoration: none; color: blue; background: none; border: none; cursor: pointer; padding: 0;">&lt; 返回列表</button>
        
        <!-- Status Badge -->
        <span v-if="formData.status !== undefined" :style="{ backgroundColor: getStatusColor(formData.status), color: 'white', padding: '3px 8px', borderRadius: '3px', marginLeft: '10px', fontSize: '12px' }">
          {{ getStatusText(formData.status) }}
        </span>

        <div style="float: right;">
            <!-- Workflow Buttons -->
            <button v-if="formData.status === 0 || formData.status === 2" @click="saveData" style="margin-left: 10px;">保存</button>
            <button v-if="formData.status === 0 || formData.status === 2" @click="handleSign" style="margin-left: 10px;">签字</button>
            <button v-if="formData.status === 0 || formData.status === 2" @click="submitWorkflow('SUBMIT')" style="margin-left: 10px; background-color: #ffc107; color: black;">提交</button>
            
            <button v-if="formData.status === 1" @click="submitWorkflow('AUDIT_PASS')" style="margin-left: 10px; background-color: #17a2b8; color: white;">审核通过</button>
            <button v-if="formData.status === 1" @click="submitWorkflow('REJECT')" style="margin-left: 10px; background-color: #dc3545; color: white;">退回</button>
            
            <button v-if="formData.status === 3" @click="handleSign" style="margin-left: 10px;">签字</button>
            <button v-if="formData.status === 3" @click="submitWorkflow('SIGN_REVIEW')" style="margin-left: 10px; background-color: #28a745; color: white;">复核签字</button>
            
            <button v-if="formData.status === 4" @click="handleSign" style="margin-left: 10px;">签字</button>
            <button v-if="formData.status === 4" @click="submitWorkflow('SIGN_APPROVE')" style="margin-left: 10px; background-color: #28a745; color: white;">批准签字</button>

            <button @click="printDocument" style="margin-left: 10px;">打印此单</button>
            <button @click="generatePdf" style="margin-left: 10px; background-color: #28a745; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;">下载PDF</button>
            <button @click="previewPdf" style="margin-left: 10px; background-color: #17a2b8; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;">预览PDF</button>
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
            <div>
                批准：
                <div style="display: inline-block; position: relative; width: 100px;">
                    <input type="text" v-model="formData.approver" name="approver" style="width: 100%; border-bottom: 1px solid black;" :style="{ opacity: formData.approverSignature ? 0 : 1 }">
                    <img v-if="formData.approverSignature" :src="formData.approverSignature" style="position: absolute; bottom: 0; left: 0; width: 100%; height: 40px; object-fit: contain; pointer-events: none; background-color: transparent;">
                </div>
            </div>
            <div>
                审核：
                <div style="display: inline-block; position: relative; width: 100px;">
                    <input type="text" v-model="formData.reviewer" name="reviewer" style="width: 100%; border-bottom: 1px solid black;" :style="{ opacity: formData.reviewerSignature ? 0 : 1 }">
                    <img v-if="formData.reviewerSignature" :src="formData.reviewerSignature" style="position: absolute; bottom: 0; left: 0; width: 100%; height: 40px; object-fit: contain; pointer-events: none; background-color: transparent;">
                </div>
            </div>
            <div>
                检测：
                <div style="display: inline-block; position: relative; width: 100px;">
                    <input type="text" v-model="formData.tester" name="tester" style="width: 100%; border-bottom: 1px solid black;" :style="{ opacity: formData.testerSignature ? 0 : 1 }">
                    <img v-if="formData.testerSignature" :src="formData.testerSignature" style="position: absolute; bottom: 0; left: 0; width: 100%; height: 40px; object-fit: contain; pointer-events: none; background-color: transparent;">
                </div>
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
  reviewer: '',
  tester: '',
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
  // Please verify the loop count match the template
  for (let i_idx = 0; i_idx < 5; i_idx++) {
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
    if (!formData.testerSignature) {
      alert('请先进行检测人签字')
      return
    }
    signatureData = formData.testerSignature.replace(/^data:image\/\w+;base64,/, '')
  } else if (action === 'SIGN_REVIEW') {
    if (!formData.reviewerSignature) {
      alert('请先进行复核人签字')
      return
    }
    signatureData = formData.reviewerSignature.replace(/^data:image\/\w+;base64,/, '')
  } else if (action === 'SIGN_APPROVE') {
    if (!formData.approverSignature) {
      alert('请先进行批准人签字')
      return
    }
    signatureData = formData.approverSignature.replace(/^data:image\/\w+;base64,/, '')
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
      formData.tester = ent.tester
      formData.reviewer = ent.reviewer
    }

    const response = await axios.get('/api/beckman-beam/report/get-by-entrustment-id', {
      params: { entrustmentId }
    })

    let sourceJson = null

    if (response.data.success && response.data.data) {
      const data = response.data.data
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
          if (record.dataJson) {
            sourceJson = record.dataJson
          }
        }
      } catch (e) {
        console.error('beckman report autofill error', e)
      }
    }

    if (sourceJson) {
      try {
        const parsedData = JSON.parse(sourceJson)
        Object.assign(formData, parsedData)
      } catch (e) {
        console.error('beckman report autofill parse error', e)
      }
    }
  } catch (error) {
    console.error('Failed to load data', error)
  }
}

const saveData = async () => {
    try {
        const dataToSave = {
            id: formData.id,
            entrustmentId: formData.entrustmentId || props.id,
            dataJson: JSON.stringify(formData),
            reviewSignaturePhoto: formData.reviewerSignature,
            inspectSignaturePhoto: formData.testerSignature,
            approveSignaturePhoto: formData.approverSignature
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

            let signed = false
            const currentName = user.fullName || user.username

            // Match Tester
            if (formData.tester === currentName) {
                formData.testerSignature = imgSrc
                signed = true
            }

            // Match Reviewer
            if (formData.reviewer === currentName) {
                formData.reviewerSignature = imgSrc
                signed = true
            }

            // Match Approver
            if (formData.approver === currentName) {
                formData.approverSignature = imgSrc
                signed = true
            }
            
            if (signed) {
                alert('签名成功')
            } else {
                alert(`当前用户(${currentName})与表单中的检测/审核/批准人员不匹配，无法签名`)
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
    
</style>
