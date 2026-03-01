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
        <span class="signature-box">
          批准：
          <span class="signature-line">{{ formData.approver }}</span>
          <img v-if="formData.approveSignature" :src="formData.approveSignature" class="signature-img" alt="批准人签名" />
        </span>
        <span class="signature-box">
          审核：
          <span class="signature-line">{{ formData.recordReviewer }}</span>
          <img v-if="formData.reviewSignature" :src="formData.reviewSignature" class="signature-img" alt="审核人签名" />
        </span>
        <span class="signature-box">
          检测：
          <span class="signature-line">{{ formData.recordTester }}</span>
          <img v-if="formData.inspectSignature" :src="formData.inspectSignature" class="signature-img" alt="检测人签名" />
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
const resultId = ref(null)
const currentEntrustmentId = ref(null)

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
  recordTester: '',
  recordReviewer: '',
  filler: '',
  approver: '',
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
    // 统一状态名称
    case 0: return '草稿'
    case 1: return '已提交待审核'
    case 2: return '已打回'
    case 3: return '待签字'
    case 4: return '已签字待提交'
    case 5: return '审核通过'
    // 报告表状态 (10-15)
    case 10: return '草稿'
    case 11: return '已提交待审核'
    case 12: return '已打回'
    case 13: return '待签字'
    case 14: return '已签字待提交'
    case 15: return '审核通过'
    // 结果表状态 (20-25)
    case 20: return '草稿'
    case 21: return '已提交待审核'
    case 22: return '已打回'
    case 23: return '待签字'
    case 24: return '已签字待提交'
    case 25: return '审核通过'
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
    case 4: return '#17a2b8' // info
    case 5: return '#28a745' // success
    // 报告表状态 (10-15)
    case 10: return '#6c757d' // secondary
    case 11: return '#007bff' // primary
    case 12: return '#dc3545' // danger
    case 13: return '#ffc107' // warning
    case 14: return '#17a2b8' // info
    case 15: return '#28a745' // success
    // 结果表状态 (20-25)
    case 20: return '#6c757d' // secondary
    case 21: return '#007bff' // primary
    case 22: return '#dc3545' // danger
    case 23: return '#ffc107' // warning
    case 24: return '#17a2b8' // info
    case 25: return '#28a745' // success
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
        // Role check: Only recordTester can submit
        // Logic: formData.recordTester (if set)
        if (formData.recordTester && user.username !== formData.recordTester && user.fullName !== formData.recordTester) {
            alert('您不是该单据的检测人 (' + formData.recordTester + ')，无权提交')
            return
        }

        if (!formData.inspectSignature) {
            alert('请先进行检测人签字')
            return
        }
        signatureData = formData.inspectSignature.replace(/^data:image\/\w+;base64,/, '')
    } else if (action === 'AUDIT_PASS') {
        // Role check: Only reviewer can audit
        if (formData.recordReviewer && user.username !== formData.recordReviewer && user.fullName !== formData.recordReviewer) {
            alert('您不是该单据的复核人 (' + formData.recordReviewer + ')，无权操作')
            return
        }
        // Auto sign Reviewer
        if (!formData.reviewSignature) {
             try {
                const sigRes = await axios.post('/api/signature/get', { userAccount: user.username })
                if (sigRes.data.success && sigRes.data.data && sigRes.data.data.signatureBlob) {
                     formData.reviewSignature = `data:image/png;base64,${sigRes.data.data.signatureBlob}`
                     if (!formData.recordReviewer) {
                        formData.recordReviewer = user.fullName || user.username
                     }
                }
             } catch (e) {
                console.error('Auto sign error', e)
             }
        }
        if (formData.reviewSignature) {
            signatureData = formData.reviewSignature.replace(/^data:image\/\w+;base64,/, '')
        }
    } else if (action === 'SIGN_REVIEW') {
        // Role check: Only reviewer can sign
        if (formData.recordReviewer && user.username !== formData.recordReviewer && user.fullName !== formData.recordReviewer) {
            alert('您不是该单据的复核人 (' + formData.recordReviewer + ')，无权签字')
            return
        }
        
        // Auto sign Reviewer if missing
        if (!formData.reviewSignature) {
             try {
                const sigRes = await axios.post('/api/signature/get', { userAccount: user.username })
                if (sigRes.data.success && sigRes.data.data && sigRes.data.data.signatureBlob) {
                     formData.reviewSignature = `data:image/png;base64,${sigRes.data.data.signatureBlob}`
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
        signatureData = formData.reviewSignature.replace(/^data:image\/\w+;base64,/, '')
    } else if (action === 'SIGN_APPROVE') {
        // Role check: Only approver can sign
        if (formData.approver && user.username !== formData.approver && user.fullName !== formData.approver) {
            alert('您不是该单据的批准人 (' + formData.approver + ')，无权签字')
            return
        }
        
        // Auto sign Approver if missing
        if (!formData.approveSignature) {
             try {
                const sigRes = await axios.post('/api/signature/get', { userAccount: user.username })
                if (sigRes.data.success && sigRes.data.data && sigRes.data.data.signatureBlob) {
                     formData.approveSignature = `data:image/png;base64,${sigRes.data.data.signatureBlob}`
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
        let data = null
        // 1. Try to get by ID (assuming props.id is Record ID)
        let res = await axios.get(`/api/light-dynamic-penetration/${props.id}`)
        
        // 2. If not found, assume props.id might be Entrustment ID and try get-by-entrustment-id
        if (!res.data.success || !res.data.data) {
             try {
                const listRes = await axios.get(`/api/light-dynamic-penetration/get-by-entrustment-id`, {
                    params: { entrustmentId: props.id }
                })
                if (listRes.data.success && listRes.data.data && listRes.data.data.length > 0) {
                    // Filter records with status 5 (approved)
                    const approvedRecords = listRes.data.data.filter(record => record.status === 5)
                    if (approvedRecords.length > 0) {
                        data = approvedRecords[0]
                    } else {
                        console.log('记录表状态未审核通过，不自动填充数据')
                    }
                }
             } catch (e) {
                console.error('Failed to fetch by entrustmentId', e)
             }
        } else {
            data = res.data.data
        }

        if (data) {
            currentEntrustmentId.value = data.entrustmentId
            // 1. Initial population from Result entity fields
            formData.entrustingUnit = data.clientUnit || ''
            formData.unifiedNumber = data.wtNum || ''
            formData.projectName = data.projectName || ''
            formData.entrustDate = formatDate(data.commissionDate)
            formData.constructionPart = data.constructionPart || ''
            formData.testDate = data.testDate ? formatDate(data.testDate) : ''
            formData.soilProperty = data.soilProperty || ''
            
            // Default reportDate to today if missing
            formData.reportDate = data.reportDate ? formatDate(data.reportDate) : formatDate(new Date())
            
            formData.witnessUnit = data.witnessUnit || ''
            formData.witness = data.witness || ''
            formData.designCapacity = data.designCapacity || ''
            formData.hammerWeight = data.hammerWeight || ''
            formData.dropDistance = data.dropDistance || ''
            formData.testCategory = data.testCategory || ''
            formData.testBasis = data.testBasis || ''
            formData.equipment = data.equipment || ''
            formData.remarks = data.remarks || ''
            
            // Map roles
            formData.approver = data.approver || ''
            formData.recordReviewer = data.recordReviewer || data.reviewer || ''
            formData.recordTester = data.recordTester || data.tester || ''
            formData.filler = data.filler || ''
            
            formData.conclusion = data.conclusion || ''
            
            formData.approveSignature = data.approveSignaturePhoto || ''
            formData.reviewSignature = data.reviewSignaturePhoto || ''
            formData.inspectSignature = data.inspectSignaturePhoto || ''
            formData.status = data.status || 0
            formData.rejectReason = data.rejectReason || ''

            // 2. Parse dataJson if available and merge
            if (data.dataJson) {
                try {
                    const json = JSON.parse(data.dataJson)
                    // Detect format: explicit flag OR heuristic (indices > 14 imply 6-row format for 3 blocks? No, Record has max 14 (idx 0-14). Result has max 17 (idx 0-17).
                    // Record (3 blocks * 5 rows): indices 0-4, 5-9, 10-14. Max 14.
                    // Result (3 blocks * 6 rows): indices 0-5, 6-11, 12-17. Max 17.
                    const keys = Object.keys(json)
                    const depthKeys = keys.filter(k => k.startsWith('depth_L_'))
                    const maxIdx = depthKeys.length > 0 ? Math.max(...depthKeys.map(k => parseInt(k.split('_')[2]))) : -1
                    const is6Row = json.format === '6-row' || maxIdx > 14

                    // Merge but verify fields
                    if (json.soilProperties && !formData.soilProperty) formData.soilProperty = json.soilProperties
                    if (json.soilProperty && !formData.soilProperty) formData.soilProperty = json.soilProperty
                    
                    if (json.designCapacity) formData.designCapacity = json.designCapacity
                    if (json.hammerWeight) formData.hammerWeight = json.hammerWeight
                    if (json.dropDistance) formData.dropDistance = json.dropDistance
                    
                    if (json.commissionDate && !formData.entrustDate) formData.entrustDate = formatDate(json.commissionDate)
                    if (json.testDate && !formData.testDate) formData.testDate = json.testDate
                    if (json.reportDate) formData.reportDate = json.reportDate
                    
                    if (json.testBasis && !formData.testBasis) formData.testBasis = json.testBasis
                    if (json.equipment && !formData.equipment) formData.equipment = json.equipment
                    if (json.conclusion && !formData.conclusion) formData.conclusion = json.conclusion

                    // Map roles from JSON if missing
                    if (json.tester && !formData.recordTester) formData.recordTester = json.tester
                    if (json.reviewer && !formData.recordReviewer) formData.recordReviewer = json.reviewer
                    
                    // Merge specific data blocks
                    for (let b = 0; b < 3; b++) {
                         if (json[`pos_L_${b}`]) formData[`pos_L_${b}`] = json[`pos_L_${b}`]
                         if (json[`avg_L_${b}`]) formData[`avg_L_${b}`] = json[`avg_L_${b}`]
                         if (json[`capacity_L_${b}`]) formData[`capacity_L_${b}`] = json[`capacity_L_${b}`]
                         
                         if (json[`pos_R_${b}`]) formData[`pos_R_${b}`] = json[`pos_R_${b}`]
                         if (json[`avg_R_${b}`]) formData[`avg_R_${b}`] = json[`avg_R_${b}`]
                         if (json[`capacity_R_${b}`]) formData[`capacity_R_${b}`] = json[`capacity_R_${b}`]

                         if (is6Row) {
                             for (let s = 0; s < 6; s++) {
                                const idx = b * 6 + s
                                if (json[`depth_L_${idx}`]) formData[`depth_L_${idx}`] = json[`depth_L_${idx}`]
                                if (json[`actual_L_${idx}`]) formData[`actual_L_${idx}`] = json[`actual_L_${idx}`]
                                
                                if (json[`depth_R_${idx}`]) formData[`depth_R_${idx}`] = json[`depth_R_${idx}`]
                                if (json[`actual_R_${idx}`]) formData[`actual_R_${idx}`] = json[`actual_R_${idx}`]
                             }
                         } else {
                             // 5-row logic
                             for (let s = 0; s < 5; s++) {
                                const sourceIdx = b * 5 + s
                                const targetIdx = b * 6 + s
                                
                                if (json[`depth_L_${sourceIdx}`]) formData[`depth_L_${targetIdx}`] = json[`depth_L_${sourceIdx}`]
                                if (json[`actual_L_${sourceIdx}`]) formData[`actual_L_${targetIdx}`] = json[`actual_L_${sourceIdx}`]
                                
                                if (json[`depth_R_${sourceIdx}`]) formData[`depth_R_${targetIdx}`] = json[`depth_R_${sourceIdx}`]
                                if (json[`actual_R_${sourceIdx}`]) formData[`actual_R_${targetIdx}`] = json[`actual_R_${sourceIdx}`]
                             }
                         }
                    }
                } catch (e) {
                    console.error('JSON parse error', e)
                }
            }

            // 3. Fallback: Fetch from Entrustment if basic info is missing
            // If we successfully loaded 'data', it likely contains basic info. 
            // But if 'data' was a bare-bones record, we might need more from Entrustment.
            let ent = null
            if (formData.unifiedNumber) {
                 try {
                    const entRes = await axios.get(`/api/jc-core-wt-info/by-wt-num?wtNum=${encodeURIComponent(formData.unifiedNumber)}`)
                    if (entRes.data.success) ent = entRes.data.data
                 } catch (e) {}
            } else if (props.id) {
                 // Try to fetch entrustment by ID (if props.id is entrustment id)
                 try {
                     const entRes = await axios.get(`/api/jc-core-wt-info/detail?id=${props.id}`)
                     if (entRes.data.success) ent = entRes.data.data
                 } catch (e) {}
            }

            if (ent) {
                if (!formData.entrustingUnit) formData.entrustingUnit = ent.client || ent.clientUnit || ''
                if (!formData.projectName) formData.projectName = ent.projectName || ''
                if (!formData.entrustDate) formData.entrustDate = formatDate(ent.commissionDate || ent.wtDate)
                if (!formData.constructionPart) formData.constructionPart = ent.constructionPart || ''
                if (!formData.testCategory) formData.testCategory = ent.testCategory || ''
                if (!formData.witnessUnit) formData.witnessUnit = ent.witnessUnit || ent.supervisionUnit || ''
                if (!formData.witness) formData.witness = ent.witness || ''
            }

            // 4. Try to fetch Result using entrustmentId (Override Record data if Result exists)
            if (currentEntrustmentId.value) {
                try {
                    const resultRes = await axios.get(`/api/light-dynamic-penetration/result/get-by-entrustment-id?entrustmentId=${currentEntrustmentId.value}`)
                    if (resultRes.data.success && resultRes.data.data) {
                        const result = resultRes.data.data
                        resultId.value = result.id
                        // Override formData with Result JSON
                        if (result.dataJson) {
                            const json = JSON.parse(result.dataJson)
                            Object.assign(formData, json)
                            if (json.testDate) formData.testDate = json.testDate
                            if (json.reportDate) formData.reportDate = json.reportDate
                        }
                        // Restore status and other fields from Result entity
                        if (result.status !== undefined) formData.status = result.status
                        if (result.rejectReason) formData.rejectReason = result.rejectReason
                        if (result.approveSignaturePhoto) formData.approveSignature = result.approveSignaturePhoto
                        if (result.reviewSignaturePhoto) formData.reviewSignature = result.reviewSignaturePhoto
                        if (result.inspectSignaturePhoto) formData.inspectSignature = result.inspectSignaturePhoto
                        if (result.recordTester) formData.recordTester = result.recordTester
                        if (result.recordReviewer) formData.recordReviewer = result.recordReviewer
                        if (result.approver) formData.approver = result.approver
                    }
                } catch (e) {
                    console.log('No result found, using record as template')
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

      // Match Tester
      if (!formData.recordTester || formData.recordTester === currentAccount || formData.recordTester === currentRealName) {
        if (!formData.recordTester) formData.recordTester = currentRealName
        formData.inspectSignature = imgSrc
        signed = true
      }
      
      if (signed) {
        alert('签名成功')
      } else {
        alert(`当前用户(${currentRealName})与表单中的检测人员(${formData.recordTester})不匹配，无法签名`)
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
    if (!props.id && !currentEntrustmentId.value) {
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
        
        // Update dataJson with current formData, but exclude legacy/redundant fields
        const dataJsonObj = { ...formData, format: '6-row' }
        delete dataJsonObj.tester
        delete dataJsonObj.reviewer
        const dataJsonStr = JSON.stringify(dataJsonObj)

        const payload = {
            id: resultId.value,
            entrustmentId: currentEntrustmentId.value || props.id,
            soilProperty: formData.soilProperty,
            designCapacity: formData.designCapacity,
            hammerWeight: formData.hammerWeight,
            dropDistance: formData.dropDistance,
            testCategory: formData.testCategory,
            testBasis: formData.testBasis,
            equipment: formData.equipment,
            remarks: formData.remarks,
            conclusion: formData.conclusion,
            testDate: formData.testDate,
            reportDate: formData.reportDate,
            dataJson: dataJsonStr,
            approveSignaturePhoto: formData.approveSignature,
            reviewSignaturePhoto: formData.reviewSignature,
            inspectSignaturePhoto: formData.inspectSignature,
            recordTester: formData.recordTester,
            recordReviewer: formData.recordReviewer,
            filler: formData.filler,
            approver: formData.approver,
            tester: formData.recordTester, // Keep legacy fields sync
            reviewer: formData.recordReviewer,
            // approver: formData.approver
        }

        const res = await axios.post('/api/light-dynamic-penetration/result/save', payload)
        if (res.data.success) {
            alert('保存成功')
            if (res.data.data && res.data.data.id) {
                resultId.value = res.data.data.id
            }
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
