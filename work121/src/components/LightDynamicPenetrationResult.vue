<template>
  <div class="lightDynamicPenetrationResult-container">


    <div class="no-print toolbar">
      <div class="toolbar-left">
        <button @click="goToList" class="link-button">&lt; 返回列表</button>
        <div v-if="formData.status !== undefined" class="status-text">
          状态:
          <span :style="{ color: getStatusColor(formData.status) }" class="status-label">
            {{ getStatusText(formData.status) }}
          </span>
        </div>
      </div>

      <div class="toolbar-right">
        <template v-if="props.id">
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
            审核通过
          </button>
          <button
            v-if="formData.status === 1"
            @click="submitWorkflow('REJECT')"
            class="btn btn-danger btn-small"
          >
            打回
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
            @click="submitWorkflow('SIGN_APPROVE')"
            class="btn btn-primary btn-small"
          >
            批准签字
          </button>
          <button
            v-if="formData.status === 4"
            @click="submitWorkflow('REJECT')"
            class="btn btn-danger btn-small"
          >
            打回
          </button>
        </template>

        <button
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

    <div
      v-if="formData.status === 2 && formData.rejectReason"
      style="background-color: #ffebee; color: #c62828; padding: 10px; border-radius: 4px; margin-top: 10px; border: 1px solid #ef9a9a; clear: both;"
    >
      <strong>打回原因：</strong> {{ formData.rejectReason }}
    </div>

    <form id="pdfForm" ref="pdfForm" method="post">
    <h2>轻型动力触探检测结果</h2>

    <div class="header-info">
        <span>委托单位：<input type="text" v-model="formData.entrustingUnit"   name="entrustingUnit" style="width: 200px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>统一编号：<input type="text" v-model="formData.unifiedNumber"   name="unifiedNumber" style="width: 150px; border-bottom: 1px solid black;"></span>
    </div>

    <table>
        <!-- Row 1 -->
        <tr>
            <td class="label" style="width: 15%;">工程名称</td>
            <td colspan="4"><input type="text" v-model="formData.projectName"   name="projectName"></td>
            <td class="label" style="width: 15%;">委托日期</td>
            <td colspan="4"><input type="text" v-model="formData.entrustDate"   name="entrustDate"></td>
        </tr>
        <!-- Row 2 -->
        <tr>
            <td class="label">施工部位</td>
            <td colspan="4"><input type="text" v-model="formData.constructionPart"   name="constructionPart"></td>
            <td class="label">检测日期</td>
            <td colspan="4"><input type="text" v-model="formData.testDate"   name="testDate"></td>
        </tr>
        <!-- Row 3 -->
        <tr>
            <td class="label">岩土性状</td>
            <td colspan="4"><input type="text" v-model="formData.soilProperty"   name="soilProperty"></td>
            <td class="label">报告日期</td>
            <td colspan="4"><input type="text" v-model="formData.reportDate"   name="reportDate"></td>
        </tr>
        <!-- Row 4 -->
        <tr>
            <td class="label">见证单位</td>
            <td colspan="4"><input type="text" v-model="formData.witnessUnit"   name="witnessUnit"></td>
            <td class="label">见证人</td>
            <td colspan="4"><input type="text" v-model="formData.witness"   name="witness"></td>
        </tr>
        <!-- Row 5 -->
        <tr>
            <td class="label">设计<br>承载力<br>(kPa)</td>
            <td colspan="2"><input type="text" v-model="formData.designCapacity"   name="designCapacity"></td>
            <td class="label">锤重量<br>(kg)</td>
            <td><input type="text" v-model="formData.hammerWeight"   name="hammerWeight"></td>
            <td class="label">落距<br>(cm)</td>
            <td><input type="text" v-model="formData.dropDistance"   name="dropDistance"></td>
            <td class="label">检测类别</td>
            <td colspan="2"><input type="text" v-model="formData.testCategory"   name="testCategory"></td>
        </tr>
        <!-- Row 6: Table Header for Data -->
        <tr>
            <td class="label">测点位置</td>
            <td class="label">贯入<br>深度<br>(cm)</td>
            <td class="label">实测<br>锤击数</td>
            <td class="label">平均<br>锤击数<br>N<sub>10</sub></td>
            <td class="label">承载力<br>特征值<br>(kPa)</td>
            <td class="label">测点位置</td>
            <td class="label">贯入<br>深度<br>(cm)</td>
            <td class="label">实测<br>锤击数</td>
            <td class="label">平均<br>锤击数<br>N<sub>10</sub></td>
            <td class="label">承载力<br>特征值<br>(kPa)</td>
        </tr>

        <!-- Data Rows：3 组测点，每组 6 行深度/锤击数 -->
        <template v-for="(b, b_idx) in 3" :key="b_idx">
            <template v-for="(s, s_idx) in 6" :key="s_idx">
            <tr>
                <!-- 左栏：一个测点位置对应 6 行深度/锤击数 -->
                <td v-if="s_idx === 0" rowspan="6">
                  <textarea
                    :name="'pos_L_' + b_idx"
                    v-model="formData['pos_L_' + b_idx]"
                    style="height: 100%; width: 100%; border: none; text-align: center; padding-top: 10px;"
                  ></textarea>
                </td>

                <td>
                  <input
                    type="text"
                    :name="'depth_L_' + (b_idx * 6 + s_idx)"
                    v-model="formData['depth_L_' + (b_idx * 6 + s_idx)]"
                  />
                </td>
                <td>
                  <input
                    type="text"
                    :name="'actual_L_' + (b_idx * 6 + s_idx)"
                    v-model="formData['actual_L_' + (b_idx * 6 + s_idx)]"
                  />
                </td>

                <td v-if="s_idx === 0" rowspan="6">
                  <input
                    type="text"
                    :name="'avg_L_' + b_idx"
                    v-model="formData['avg_L_' + b_idx]"
                    style="height: 100%;"
                  />
                </td>
                <td v-if="s_idx === 0" rowspan="6">
                  <input
                    type="text"
                    :name="'capacity_L_' + b_idx"
                    v-model="formData['capacity_L_' + b_idx]"
                    style="height: 100%;"
                  />
                </td>

                <!-- 右栏 -->
                <td v-if="s_idx === 0" rowspan="6">
                  <textarea
                    :name="'pos_R_' + b_idx"
                    v-model="formData['pos_R_' + b_idx]"
                    style="height: 100%; width: 100%; border: none; text-align: center; padding-top: 10px;"
                  ></textarea>
                </td>

                <td>
                  <input
                    type="text"
                    :name="'depth_R_' + (b_idx * 6 + s_idx)"
                    v-model="formData['depth_R_' + (b_idx * 6 + s_idx)]"
                  />
                </td>
                <td>
                  <input
                    type="text"
                    :name="'actual_R_' + (b_idx * 6 + s_idx)"
                    v-model="formData['actual_R_' + (b_idx * 6 + s_idx)]"
                  />
                </td>

                <td v-if="s_idx === 0" rowspan="6">
                  <input
                    type="text"
                    :name="'avg_R_' + b_idx"
                    v-model="formData['avg_R_' + b_idx]"
                    style="height: 100%;"
                  />
                </td>
                <td v-if="s_idx === 0" rowspan="6">
                  <input
                    type="text"
                    :name="'capacity_R_' + b_idx"
                    v-model="formData['capacity_R_' + b_idx]"
                    style="height: 100%;"
                  />
                </td>
            </tr>
            </template>
        </template>

        <!-- Row: 仪器设备 -->
        <tr>
            <td class="label">仪器设备</td>
            <td colspan="9" class="left-align"><input type="text" v-model="formData.equipment"   name="equipment"></td>
        </tr>
    </table>

    <div class="footer-info">
        <span>
            批准：
            <div style="display: inline-block; position: relative; width: 100px;">
                <input type="text" v-model="formData.approve" style="width: 100%; border-bottom: 1px solid black;" :style="{ opacity: formData.approveSignature ? 0 : 1 }">
                <img v-if="formData.approveSignature" :src="formData.approveSignature" style="position: absolute; bottom: 0; left: 0; width: 100%; height: 40px; object-fit: contain; pointer-events: none; background-color: transparent;">
            </div>
        </span>
        <span>
            审核：
            <div style="display: inline-block; position: relative; width: 100px;">
                <input type="text" v-model="formData.review" style="width: 100%; border-bottom: 1px solid black;" :style="{ opacity: formData.reviewSignature ? 0 : 1 }">
                <img v-if="formData.reviewSignature" :src="formData.reviewSignature" style="position: absolute; bottom: 0; left: 0; width: 100%; height: 40px; object-fit: contain; pointer-events: none; background-color: transparent;">
            </div>
        </span>
        <span>
            检验：
            <div style="display: inline-block; position: relative; width: 100px;">
                <input type="text" v-model="formData.inspect" style="width: 100%; border-bottom: 1px solid black;" :style="{ opacity: formData.inspectSignature ? 0 : 1 }">
                <img v-if="formData.inspectSignature" :src="formData.inspectSignature" style="position: absolute; bottom: 0; left: 0; width: 100%; height: 40px; object-fit: contain; pointer-events: none; background-color: transparent;">
            </div>
        </span>
    </div>

    </form>



  </div>
</template>

<script setup>
import { reactive, ref, onMounted, defineProps, inject } from 'vue'
import axios from 'axios'

const navigateTo = inject('navigateTo')

const goToList = () => {
  if (navigateTo) {
    navigateTo('LightDynamicPenetrationResultList')
  }
}

const props = defineProps({
  id: String
})

const pdfForm = ref(null)

const formData = reactive({
  entrustingUnit: '',
  unifiedNumber: '',
  projectName: '',
  entrustDate: '',
  constructionPart: '',
  testDate: '',
  soilProperty: '',
  reportDate: '',
  witnessUnit: '',
  witness: '',
  designCapacity: '',
  hammerWeight: '',
  dropDistance: '',
  testCategory: '',
  testBasis: '',
  equipment: '',
  remarks: '',
  approve: '',
  review: '',
  inspect: '',
  conclusion: '',
  approveSignature: '',
  reviewSignature: '',
  inspectSignature: '',
  status: 0,
  rejectReason: ''
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
        case 0: return '#9E9E9E' // Grey
        case 1: return '#2196F3' // Blue
        case 2: return '#F44336' // Red
        case 3: return '#FF9800' // Orange
        case 4: return '#9C27B0' // Purple
        case 5: return '#4CAF50' // Green
        default: return '#000000'
    }
}

const submitWorkflow = async (action) => {
    if (!props.id) {
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
        // Role check: Only tester can submit
        if (formData.inspect && user.username !== formData.inspect) {
            alert('您不是该单据的检测人 (' + formData.inspect + ')，无权提交')
            return
        }

        if (!formData.inspectSignature) {
            alert('请先进行检测人签字')
            return
        }
        signatureData = formData.inspectSignature.replace(/^data:image\/\w+;base64,/, '')
    } else if (action === 'AUDIT_PASS' || (action === 'REJECT' && formData.status === 1)) {
        // Role check: Only reviewer can audit/reject at status 1
        if (formData.review && user.username !== formData.review) {
            alert('您不是该单据的复核人 (' + formData.review + ')，无权操作')
            return
        }
    } else if (action === 'SIGN_REVIEW') {
        // Role check: Only reviewer can sign
        if (formData.review && user.username !== formData.review) {
            alert('您不是该单据的复核人 (' + formData.review + ')，无权签字')
            return
        }
        
        if (!formData.reviewSignature) {
            alert('请先进行复核人签字')
            return
        }
        signatureData = formData.reviewSignature.replace(/^data:image\/\w+;base64,/, '')
    } else if (action === 'SIGN_APPROVE') {
        // Role check: Only approver can sign
        if (formData.approve && user.username !== formData.approve) {
            alert('您不是该单据的批准人 (' + formData.approve + ')，无权签字')
            return
        }
        
        if (!formData.approveSignature) {
            alert('请先进行批准人签字')
            return
        }
        signatureData = formData.approveSignature.replace(/^data:image\/\w+;base64,/, '')
    }

    const request = {
        tableType: 'LIGHT_DYNAMIC_PENETRATION_RESULT', // Assuming this is the type
        recordId: props.id,
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
            loadData()
        } else {
            alert('操作失败: ' + response.data.message)
        }
    } catch (e) {
        console.error('Workflow error', e)
        alert('操作异常')
    }
}

const formatDate = (d) => {
    if (!d) return ''
    const date = new Date(d)
    const year = date.getFullYear()
    const month = ('0' + (date.getMonth() + 1)).slice(-2)
    const day = ('0' + date.getDate()).slice(-2)
    return `${year}-${month}-${day}`
}

const loadData = async () => {
    if (!props.id) return
    try {
        const res = await axios.get(`/api/light-dynamic-penetration/${props.id}`)
        if (res.data.success && res.data.data) {
            const data = res.data.data
            formData.entrustingUnit = data.clientUnit || ''
            formData.unifiedNumber = data.wtNum || ''
            formData.projectName = data.projectName || ''
            formData.entrustDate = formatDate(data.commissionDate)
            formData.constructionPart = data.constructionPart || ''
            formData.soilProperty = data.soilProperty || ''
            formData.reportDate = formatDate(data.reportDate)
            formData.witnessUnit = data.witnessUnit || ''
            formData.witness = data.witness || ''
            formData.designCapacity = data.designCapacity || ''
            formData.hammerWeight = data.hammerWeight || ''
            formData.dropDistance = data.dropDistance || ''
            formData.testCategory = data.testCategory || ''
            formData.testBasis = data.testBasis || ''
            formData.equipment = data.equipment || ''
            formData.remarks = data.remarks || ''
            formData.approve = data.approver || ''
            formData.review = data.reviewer || ''
            formData.inspect = data.tester || ''
            formData.conclusion = data.conclusion || ''
            
            formData.approveSignature = data.approveSignaturePhoto || ''
            formData.reviewSignature = data.reviewSignaturePhoto || ''
            formData.inspectSignature = data.inspectSignaturePhoto || ''
            formData.status = data.status || 0
            formData.rejectReason = data.rejectReason || ''
            
            if (data.dataJson) {
                try {
                    const json = JSON.parse(data.dataJson)
                    Object.assign(formData, json)
                    if (json.testDate) formData.testDate = json.testDate
                } catch (e) {
                    console.error('JSON parse error', e)
                }
            }
        }
    } catch (e) {
        console.error('Load error', e)
    }
}

const handleSign = async () => {
  const user = JSON.parse(localStorage.getItem('userInfo'))
  if (!user || !user.username) {
    alert('请先登录')
    return
  }

  const currentAccount = user.username || user.userAccount || user.userName
  const currentRealName = user.fullName || user.userName || currentAccount

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

      // Match Approve by account (兼容姓名)
      if (formData.approve === currentAccount || formData.approve === currentRealName) {
        formData.approveSignature = imgSrc
        signed = true
      }

      // Match Review by account (兼容姓名)
      if (formData.review === currentAccount || formData.review === currentRealName) {
        formData.reviewSignature = imgSrc
        signed = true
      }
      
      // Match Inspect by account (兼容姓名)
      if (formData.inspect === currentAccount || formData.inspect === currentRealName) {
        formData.inspectSignature = imgSrc
        signed = true
      }
      
      if (signed) {
        alert('签名成功')
      } else {
        const formPerson = formData.approve || formData.review || formData.inspect || ''
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

const saveData = async () => {
    if (!props.id) {
        alert('无效的委托单ID')
        return
    }
    try {
        const dynamicData = {}
        if (formData.testDate) dynamicData.testDate = formData.testDate
        
        for (const key in formData) {
            if (key.match(/^(pos|depth|actual|avg|capacity)_[LR]_\d+$/)) {
                dynamicData[key] = formData[key]
            }
        }

        const payload = {
            id: props.id,
            soilProperty: formData.soilProperty,
            designCapacity: formData.designCapacity,
            hammerWeight: formData.hammerWeight,
            dropDistance: formData.dropDistance,
            testBasis: formData.testBasis,
            equipment: formData.equipment,
            remarks: formData.remarks,
            approver: formData.approve,
            reviewer: formData.review,
            tester: formData.inspect,
            conclusion: formData.conclusion,
            approveSignaturePhoto: formData.approveSignature,
            reviewSignaturePhoto: formData.reviewSignature,
            inspectSignaturePhoto: formData.inspectSignature,
            // reportDate should be handled carefully. If input type=text, it's string.
            // But entity expects Date. If I send string 'yyyy-MM-dd', Spring might parse it if configured.
            // Or I can send timestamp.
            // Let's try sending string and see. If backend is configured with Jackson, it might work.
            // Or I can send null if empty.
            reportDate: formData.reportDate ? new Date(formData.reportDate) : null,
            dataJson: JSON.stringify(dynamicData)
        }

        const res = await axios.post('/api/light-dynamic-penetration/save', payload)
        if (res.data.success) {
            alert('保存成功')
        } else {
            alert('保存失败: ' + (res.data.message || '未知错误'))
        }
    } catch (e) {
        alert('保存失败: ' + e.message)
    }
}

onMounted(() => {
  loadData()
})

const printDocument = () => {
  window.print()
}



const generatePdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/light_dynamic_penetration_result/generate'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}

const previewPdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/light_dynamic_penetration_result/preview'
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
        .lightDynamicPenetrationResult-container {
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
            margin-top: 30px;
            margin-bottom: 20px;
            font-size: 16px;
            font-weight: bold;
        }
        .statement {
            font-size: 12px;
            line-height: 1.6;
            margin-top: 10px;
        }
        .page-footer {
            margin-top: 10px;
            text-align: right;
            font-size: 14px;
            margin-bottom: 20px;
        }
        @media print {
            .lightDynamicPenetrationResult-container {
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
