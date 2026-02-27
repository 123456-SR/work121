<template>
  <div class="lightDynamicPenetration-container">


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
    
    <div v-if="formData.status === 2 && formData.rejectReason" style="background-color: #ffebee; color: #c62828; padding: 10px; border-radius: 4px; margin-bottom: 10px; border: 1px solid #ef9a9a;">
        <strong>打回原因：</strong> {{ formData.rejectReason }}
    </div>

    <form id="pdfForm" ref="pdfForm" method="post">
    <h2>轻型动力触探检测报告</h2>

    <div class="header-info">
        <span>委托单位：<input type="text" v-model="formData.entrustingUnit"   name="entrustingUnit" style="width: 200px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>统一编号：<input type="text" v-model="formData.unifiedNumber"   name="unifiedNumber" style="width: 150px; border-bottom: 1px solid black;"></span>
    </div>

    <table>
        <!-- Row 1 -->
        <tr>
            <td class="label" style="width: 10%;">工程名称</td>
            <!-- 左侧内容占 6 列，右侧标签占 1 列，右侧内容占 3 列：1+6+1+2=10 -->
            <td colspan="6"><input type="text" v-model="formData.projectName"   name="projectName"></td>
            <td class="label" style="width: 10%;">委托日期</td>
            <td colspan="2"><input type="text" v-model="formData.entrustDate"   name="entrustDate"></td>
        </tr>
        <!-- Row 2 -->
        <tr>
            <td class="label">施工部位</td>
            <td colspan="6"><input type="text" v-model="formData.constructionPart"   name="constructionPart"></td>
            <td class="label">检测日期</td>
            <td colspan="2"><input type="text" v-model="formData.testDate"   name="testDate"></td>
        </tr>
        <!-- Row 3 -->
        <tr>
            <td class="label">岩土性状</td>
            <td colspan="6"><input type="text" v-model="formData.soilProperty"   name="soilProperty"></td>
            <td class="label">报告日期</td>
            <td colspan="2"><input type="text" v-model="formData.reportDate"   name="reportDate"></td>
        </tr>
        <!-- Row 4 -->
        <tr>
            <td class="label">见证单位</td>
            <td colspan="6"><input type="text" v-model="formData.witnessUnit"   name="witnessUnit"></td>
            <td class="label">见证人</td>
            <td colspan="2"><input type="text" v-model="formData.witness"   name="witness"></td>
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

        <!-- Data Rows -->
        <template v-for="(block, blockIndex) in formData.dataBlocks" :key="blockIndex">
            <template v-for="(depth, subIndex) in block.depths" :key="`${blockIndex}-${subIndex}`">
                <tr>
                    <!-- 左栏：一个测点位置对应 6 行深度/锤击数 -->
                    <td v-if="subIndex === 0" rowspan="6">
                      <textarea
                        v-model="block.pos_L"
                        :name="`pos_L_${blockIndex}`"
                        style="height: 100%; width: 100%; border: none; text-align: center; padding-top: 10px;"
                      ></textarea>
                    </td>

                    <td><input type="text" v-model="depth.depth_L" :name="`depth_L_${blockIndex * 6 + subIndex}`"></td>
                    <td><input type="text" v-model="depth.actual_L" :name="`actual_L_${blockIndex * 6 + subIndex}`"></td>

                    <td v-if="subIndex === 0" rowspan="6">
                      <input type="text" v-model="block.avg_L" :name="`avg_L_${blockIndex}`" style="height: 100%;" />
                    </td>
                    <td v-if="subIndex === 0" rowspan="6">
                      <input type="text" v-model="block.capacity_L" :name="`capacity_L_${blockIndex}`" style="height: 100%;" />
                    </td>

                    <!-- 右栏 -->
                    <td v-if="subIndex === 0" rowspan="6">
                      <textarea
                        v-model="block.pos_R"
                        :name="`pos_R_${blockIndex}`"
                        style="height: 100%; width: 100%; border: none; text-align: center; padding-top: 10px;"
                      ></textarea>
                    </td>

                    <td><input type="text" v-model="block.depths_R[subIndex].depth_R" :name="`depth_R_${blockIndex * 6 + subIndex}`"></td>
                    <td><input type="text" v-model="block.depths_R[subIndex].actual_R" :name="`actual_R_${blockIndex * 6 + subIndex}`"></td>

                    <td v-if="subIndex === 0" rowspan="6">
                      <input type="text" v-model="block.avg_R" :name="`avg_R_${blockIndex}`" style="height: 100%;" />
                    </td>
                    <td v-if="subIndex === 0" rowspan="6">
                      <input type="text" v-model="block.capacity_R" :name="`capacity_R_${blockIndex}`" style="height: 100%;" />
                    </td>
                </tr>
            </template>
        </template>

        <!-- Row: 检测依据 -->
        <tr>
            <td class="label">检测依据</td>
            <td colspan="9" class="left-align"><input type="text" v-model="formData.testBasis"   name="testBasis"></td>
        </tr>
        <!-- Row: 仪器设备 -->
        <tr>
            <td class="label">仪器设备</td>
            <td colspan="9" class="left-align"><input type="text" v-model="formData.equipment"   name="equipment"></td>
        </tr>
        <!-- Row: 检测结论 -->
        <tr>
            <td class="label" style="height: 80px;">检测结论</td>
            <td colspan="9" class="left-align" style="vertical-align: top;">
                <textarea v-model="formData.conclusion"  name="conclusion" rows="3" style="width: 100%; height: 100%;"></textarea>
            </td>
        </tr>
        <!-- Row: 备注 -->
        <tr>
            <td class="label">备注</td>
            <td colspan="9" class="left-align"><input type="text" v-model="formData.remarks"   name="remarks"></td>
        </tr>
    </table>

    <div class="footer-info">
        <div style="position: relative; display: inline-block;">
            <span>批准：<input type="text" v-model="formData.approver" name="approver" style="width: 100px; border-bottom: 1px solid black;"></span>
            <img v-if="formData.approverSignature" :src="formData.approverSignature" style="position: absolute; top: -30px; left: 40px; width: 100px; height: 50px; pointer-events: none;" />
        </div>
        <div style="position: relative; display: inline-block;">
            <span>审核：<input type="text" v-model="formData.reviewer" name="reviewer" style="width: 100px; border-bottom: 1px solid black;"></span>
            <img v-if="formData.reviewerSignature" :src="formData.reviewerSignature" style="position: absolute; top: -30px; left: 40px; width: 100px; height: 50px; pointer-events: none;" />
        </div>
        <div style="position: relative; display: inline-block;">
            <span>检验：<input type="text" v-model="formData.tester" name="tester" style="width: 100px; border-bottom: 1px solid black;"></span>
            <img v-if="formData.testerSignature" :src="formData.testerSignature" style="position: absolute; top: -30px; left: 40px; width: 100px; height: 50px; pointer-events: none;" />
        </div>
    </div>

    <div class="statement" style="font-size: 12px; margin-top: 10px;">
        声明：<br>
        1. 对本检测报告的复印件未加盖公司检验检测专用章无效。<br>
        2. 对检测结果如有异议，应在收到报告之日起十五日之内向本公司提出。
    </div>

    <div class="company-info" style="display: block; margin-top: 5px; font-size: 14px; font-weight: bold;">
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
import { reactive, ref, onMounted, defineProps, inject } from 'vue'
import axios from 'axios'

const navigateTo = inject('navigateTo')

const goToList = () => {
  if (navigateTo) {
    navigateTo('LightDynamicPenetrationReportList')
  }
}

const props = defineProps({
  id: String
})

const pdfForm = ref(null)

const formData = reactive({
  entrustingUnit: '',
  status: 0,
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
  approver: '',
  reviewer: '',
  tester: '',
  approverSignature: '',
  reviewerSignature: '',
  testerSignature: '',
  companyName: '',
  companyAddress: '',
  companyPhone: '',
  conclusion: '',
  // 2 组测点，每组左/右各 6 行深度/锤击数
  dataBlocks: [
    { pos_L: '', depths: Array.from({ length: 6 }, () => ({ depth_L: '', actual_L: '' })), avg_L: '', capacity_L: '', pos_R: '', depths_R: Array.from({ length: 6 }, () => ({ depth_R: '', actual_R: '' })), avg_R: '', capacity_R: '' },
    { pos_L: '', depths: Array.from({ length: 6 }, () => ({ depth_L: '', actual_L: '' })), avg_L: '', capacity_L: '', pos_R: '', depths_R: Array.from({ length: 6 }, () => ({ depth_R: '', actual_R: '' })), avg_R: '', capacity_R: '' }
  ]
})

const formatDate = (d) => {
    if (!d) return ''
    const date = new Date(d)
    const year = date.getFullYear()
    const month = ('0' + (date.getMonth() + 1)).slice(-2)
    const day = ('0' + date.getDate()).slice(-2)
    return `${year}-${month}-${day}`
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
    if (formData.tester && user.username !== formData.tester) {
        alert('您不是该单据的检测人 (' + formData.tester + ')，无权提交')
        return
    }

    if (!formData.testerSignature) {
      alert('请先进行检测人签字')
      return
    }
    signatureData = formData.testerSignature.replace(/^data:image\/\w+;base64,/, '')
  } else if (action === 'AUDIT_PASS' || (action === 'REJECT' && formData.status === 1)) {
    // Role check: Only reviewer can audit/reject at status 1
    if (formData.reviewer && user.username !== formData.reviewer) {
        alert('您不是该单据的复核人 (' + formData.reviewer + ')，无权操作')
        return
    }
  } else if (action === 'SIGN_REVIEW') {
    // Role check: Only reviewer can sign
    if (formData.reviewer && user.username !== formData.reviewer) {
        alert('您不是该单据的复核人 (' + formData.reviewer + ')，无权签字')
        return
    }

    if (!formData.reviewerSignature) {
      alert('请先进行复核人签字')
      return
    }
    signatureData = formData.reviewerSignature.replace(/^data:image\/\w+;base64,/, '')
  } else if (action === 'SIGN_APPROVE' || (action === 'REJECT' && formData.status === 4)) {
    // Role check: Only approver can sign/reject at status 4
    if (formData.approver && user.username !== formData.approver) {
        alert('您不是该单据的批准人 (' + formData.approver + ')，无权操作')
        return
    }

    if (action === 'SIGN_APPROVE') {
        if (!formData.approverSignature) {
            alert('请先进行批准人签字')
            return
        }
        signatureData = formData.approverSignature.replace(/^data:image\/\w+;base64,/, '')
    }
  }

  const request = {
    tableType: 'LIGHT_DYNAMIC_PENETRATION',
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
      formData.approver = data.approver || ''
      formData.reviewer = data.reviewer || ''
      formData.tester = data.tester || ''
      formData.approve = data.approver || ''
      formData.review = data.reviewer || ''
      formData.inspect = data.tester || ''
      formData.approverSignature = data.approveSignaturePhoto || ''
      formData.reviewerSignature = data.reviewSignaturePhoto || ''
      formData.testerSignature = data.inspectSignaturePhoto || ''
      formData.conclusion = data.conclusion || ''
      formData.status = data.status !== undefined ? data.status : 0
      formData.rejectReason = data.rejectReason || ''
      formData.companyName = '河北金涛建设工程质量检测有限公司'
      formData.companyAddress = '石家庄高新区方亿科技工业园A区第2号楼。'
      formData.companyPhone = '0311—86107634  0311—67300616'

      let sourceJson = data.dataJson

      if (!sourceJson && data.entrustmentId) {
        try {
          const resultRes = await axios.get('/api/light-dynamic-penetration/get-by-entrustment-id', {
            params: { entrustmentId: data.entrustmentId }
          })
          if (resultRes.data.success && resultRes.data.data && resultRes.data.data.length > 0) {
            const record = resultRes.data.data[0]
            if (record.dataJson) {
              sourceJson = record.dataJson
            }
          }
        } catch (e) {
          console.error('light dynamic report autofill error', e)
        }
      }

          if (sourceJson) {
        try {
          const json = JSON.parse(sourceJson)
          if (json.testDate) formData.testDate = json.testDate

          for (let b = 0; b < 2; b++) {
            formData.dataBlocks[b].pos_L = json[`pos_L_${b}`] || ''
            formData.dataBlocks[b].avg_L = json[`avg_L_${b}`] || ''
            formData.dataBlocks[b].capacity_L = json[`capacity_L_${b}`] || ''

            formData.dataBlocks[b].pos_R = json[`pos_R_${b}`] || ''
            formData.dataBlocks[b].avg_R = json[`avg_R_${b}`] || ''
            formData.dataBlocks[b].capacity_R = json[`capacity_R_${b}`] || ''

            for (let s = 0; s < 6; s++) {
              const idx = b * 6 + s
              formData.dataBlocks[b].depths[s].depth_L = json[`depth_L_${idx}`] || ''
              formData.dataBlocks[b].depths[s].actual_L = json[`actual_L_${idx}`] || ''

              formData.dataBlocks[b].depths_R[s].depth_R = json[`depth_R_${idx}`] || ''
              formData.dataBlocks[b].depths_R[s].actual_R = json[`actual_R_${idx}`] || ''
            }
          }
        } catch (e) {
          console.error('JSON parse error', e)
        }
      }
    }
  } catch (e) {
    console.error('Load error', e)
  }
}

onMounted(() => {
    loadData()
})

const handleSign = async () => {
  const user = JSON.parse(localStorage.getItem('userInfo'))
  if (!user || !user.userAccount) {
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
      if (formData.tester === user.username || formData.tester === currentName) {
        formData.testerSignature = imgSrc
        signed = true
      }

      // Match Reviewer
      if (formData.reviewer === user.username || formData.reviewer === currentName) {
        formData.reviewerSignature = imgSrc
        signed = true
      }

      // Match Approver
      if (formData.approver === user.username || formData.approver === currentName) {
        formData.approverSignature = imgSrc
        signed = true
      }
      
      if (signed) {
        alert('签名成功')
      } else {
        alert(`当前用户(${currentName})与表单中的检验/审核/批准人员不匹配，无法签名`)
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
    pdfForm.value.action = '/api/pdf/light_dynamic_penetration/generate'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}

const previewPdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/light_dynamic_penetration/preview'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}

const submitForm = async () => {
  try {
    // 获取当前登录用户信息
    const userInfoStr = localStorage.getItem('userInfo');
    const userInfo = userInfoStr ? JSON.parse(userInfoStr) : {};
    
    // Convert dataBlocks to flat keys
    const dynamicData = {}
    if (formData.testDate) dynamicData.testDate = formData.testDate

    for (let b = 0; b < 2; b++) {
        const block = formData.dataBlocks[b]
        if (block.pos_L) dynamicData[`pos_L_${b}`] = block.pos_L
        if (block.avg_L) dynamicData[`avg_L_${b}`] = block.avg_L
        if (block.capacity_L) dynamicData[`capacity_L_${b}`] = block.capacity_L
        
        if (block.pos_R) dynamicData[`pos_R_${b}`] = block.pos_R
        if (block.avg_R) dynamicData[`avg_R_${b}`] = block.avg_R
        if (block.capacity_R) dynamicData[`capacity_R_${b}`] = block.capacity_R

        for (let s = 0; s < 6; s++) {
            const idx = b * 6 + s
            if (block.depths[s].depth_L) dynamicData[`depth_L_${idx}`] = block.depths[s].depth_L
            if (block.depths[s].actual_L) dynamicData[`actual_L_${idx}`] = block.depths[s].actual_L
            
            if (block.depths_R[s].depth_R) dynamicData[`depth_R_${idx}`] = block.depths_R[s].depth_R
            if (block.depths_R[s].actual_R) dynamicData[`actual_R_${idx}`] = block.depths_R[s].actual_R
        }
    }

    // 构建提交数据
    const submitData = {
      id: props.id, // Ensure ID is passed
      unifiedNumber: formData.unifiedNumber, // These might be ignored if using ID, but good to have
      entrustingUnit: formData.entrustingUnit,
      projectName: formData.projectName,
      // entrustDate: formData.entrustDate, // Read-only usually
      // constructionPart: formData.constructionPart, // Read-only
      
      // Editable fields
      soilProperty: formData.soilProperty,
      designCapacity: formData.designCapacity,
      hammerWeight: formData.hammerWeight,
      dropDistance: formData.dropDistance,
      testBasis: formData.testBasis,
      equipment: formData.equipment,
      remarks: formData.remarks,
      approver: formData.approver,
      reviewer: formData.reviewer,
      tester: formData.tester,
      approveSignaturePhoto: formData.approverSignature,
      reviewSignaturePhoto: formData.reviewerSignature,
      inspectSignaturePhoto: formData.testerSignature,
      conclusion: formData.conclusion,
      reportDate: formData.reportDate ? new Date(formData.reportDate) : null,
      
      dataJson: JSON.stringify(dynamicData)
    };
    
    // 发送请求
    const response = await axios.post('/api/light-dynamic-penetration/save', submitData);
    
    if (response.data.success) {
      alert('提交成功');
    } else {
      alert('提交失败: ' + response.data.message);
    }
  } catch (error) {
    console.error('提交错误:', error);
    alert('提交失败，请稍后重试');
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
        .lightDynamicPenetration-container {
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
            .lightDynamicPenetration-container {
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
