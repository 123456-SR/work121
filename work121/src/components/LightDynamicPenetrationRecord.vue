<template>
  <div class="lightDynamicPenetrationRecord-container">


    <div class="no-print toolbar">
      <div class="toolbar-left">
        <button @click="goToList" class="link-button">&lt; 返回列表</button>
        <span v-if="records.length > 0" class="record-nav">
          <button
            @click="prevRecord"
            :disabled="currentIndex === 0"
            class="btn btn-secondary btn-small"
          >
            上一页
          </button>
          <span class="record-nav-info">
            页面 {{ currentIndex + 1 }} / {{ records.length }}
          </span>
          <button
            @click="nextRecord"
            :disabled="currentIndex === records.length - 1"
            class="btn btn-secondary btn-small"
          >
            下一页
          </button>
          <button
            @click="addRecord"
            :disabled="!canEditStructure"
            class="btn btn-primary btn-small"
          >
            添加页面
          </button>
          <button
            @click="deleteRecord"
            :disabled="records.length <= 1 || !canEditStructure"
            class="btn btn-danger btn-small"
          >
            删除当前页面
          </button>
        </span>
      </div>
      
      <div class="toolbar-right">
        <div v-if="formData.status !== undefined" class="status-text">
          状态:
          <span :style="{ color: getStatusColor(formData.status) }" class="status-label">
            {{ getStatusText(formData.status) }}
          </span>
        </div>

        <template v-if="!draftMode">
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
        </template>


        <button
          @click="saveData"
          class="btn btn-secondary btn-small"
          :disabled="!isEditable"
        >
          保存
        </button>
        <button
          v-if="!draftMode"
          @click="printDocument"
          class="btn btn-secondary btn-small"
        >
          打印此单
        </button>
        <button
          v-if="!draftMode"
          @click="generatePdf"
          class="btn btn-secondary btn-small"
        >
          下载PDF
        </button>
        <button
          v-if="!draftMode"
          @click="previewPdf"
          class="btn btn-secondary btn-small"
        >
          预览PDF
        </button>
        <button
          v-if="!draftMode"
          @click="exportExcel"
          class="btn btn-secondary btn-small"
        >
          导出数据
        </button>
        <button
          type="button"
          @click="openAnalysisModal"
          class="btn btn-secondary btn-small"
          :disabled="!isEditable"
        >
          数据分析
        </button>
      </div>
        
        <div v-if="formData.status === 2 && formData.rejectReason" style="background-color: #ffebee; color: #c62828; padding: 10px; border-radius: 4px; margin-top: 10px; border: 1px solid #ef9a9a; clear: both;">
            <strong>打回原因：</strong> {{ formData.rejectReason }}
        </div>
    </div>

    <form id="pdfForm" ref="pdfForm" method="post">
    <h2>轻型动力触探检测记录表</h2>

    <div class="header-info">
        <span>委托单位：<input type="text" v-model="formData.entrustingUnit"   name="entrustingUnit" style="width: 250px; border-bottom: 1px solid black; text-align: left;" :disabled="!isEditable"></span>
        <span>统一编号：<input type="text" v-model="formData.unifiedNumber"   name="unifiedNumber" style="width: 150px; border-bottom: 1px solid black;" disabled></span>
    </div>

    <table>
        <!-- Row 1 -->
        <tr>
            <td class="label">工程名称</td>
            <td colspan="4"><textarea v-model="formData.projectName"   name="projectName" :disabled="!isEditable" class="table-textarea"></textarea></td>
            <td class="label">委托日期</td>
            <td colspan="4"><textarea v-model="formData.commissionDate"   name="commissionDate" :disabled="!isEditable" class="table-textarea"></textarea></td>
        </tr>
        <!-- Row 2 -->
        <tr>
            <td class="label">施工部位</td>
            <td colspan="4"><textarea v-model="formData.constructionPart"   name="constructionPart" :disabled="!isEditable" class="table-textarea"></textarea></td>
            <td class="label">检测日期</td>
            <td colspan="4"><textarea v-model="formData.testDate"   name="testDate" :disabled="!isEditable" class="table-textarea"></textarea></td>
        </tr>
        <!-- Row 3 -->
        <tr>
            <td class="label">岩土性状</td>
            <td colspan="4"><textarea v-model="formData.soilProperties"   name="soilProperties" :disabled="!isEditable" class="table-textarea"></textarea></td>
            <td class="label">检测类别</td>
            <td colspan="4"><textarea v-model="formData.testCategory"   name="testCategory" :disabled="!isEditable" class="table-textarea"></textarea></td>
        </tr>
        <!-- Row 4: Params -->
        <tr>
            <td class="label">设计<br>承载力<br>(kPa)</td>
            <td colspan="2"><textarea v-model="formData.designCapacity"   name="designCapacity" :disabled="!isEditable" class="table-textarea"></textarea></td>
            <td class="label">锤重量<br>(kg)</td>
            <td colspan="2"><textarea v-model="formData.hammerWeight"   name="hammerWeight" :disabled="!isEditable" class="table-textarea"></textarea></td>
            <td class="label">落距<br>(cm)</td>
            <td colspan="3"><textarea v-model="formData.dropDistance"   name="dropDistance" :disabled="!isEditable" class="table-textarea"></textarea></td>
        </tr>

        <!-- Row 5: Table Header for Data -->
        <tr>
            <td class="label" style="width: 10%;">测点<br>位置</td>
            <td class="label" style="width: 10%;">贯入<br>深度<br>(cm)</td>
            <td class="label" style="width: 10%;">实测<br>锤击数</td>
            <td class="label" style="width: 10%;">平均<br>锤击数<br>N<sub>10</sub></td>
            <td class="label" style="width: 10%;">承载力<br>特征值<br>(kPa)</td>
            <td class="label" style="width: 10%;">测点<br>位置</td>
            <td class="label" style="width: 10%;">贯入<br>深度<br>(cm)</td>
            <td class="label" style="width: 10%;">实测<br>锤击数</td>
            <td class="label" style="width: 10%;">平均<br>锤击数<br>N<sub>10</sub></td>
            <td class="label" style="width: 10%;">承载力<br>特征值<br>(kPa)</td>
        </tr>

        <!-- Data Rows -->
        <template v-for="(b, b_idx) in (currentIndex === 0 ? 2 : 3)" :key="b_idx">
            <template v-for="(s, s_idx) in 7" :key="s_idx">
            <tr>
                <!-- 左栏 -->
                <td v-if="s_idx === 0" rowspan="7">
                    <textarea :name="'pos_L_' + b_idx" v-model="formData['pos_L_' + b_idx]" class="table-textarea" :disabled="!isEditable"></textarea>
                </td>

                <td><textarea :name="'depth_L_' + (b_idx * 7 + s_idx)" v-model="formData['depth_L_' + (b_idx * 7 + s_idx)]" :disabled="!isEditable" class="table-textarea"></textarea></td>
                <td><textarea :name="'actual_L_' + (b_idx * 7 + s_idx)" v-model="formData['actual_L_' + (b_idx * 7 + s_idx)]" :disabled="!isEditable" class="table-textarea"></textarea></td>

                <td v-if="s_idx === 0" rowspan="7"><textarea :name="'avg_L_' + b_idx" v-model="formData['avg_L_' + b_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
                <td v-if="s_idx === 0" rowspan="7"><textarea :name="'capacity_L_' + b_idx" v-model="formData['capacity_L_' + b_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>

                <!-- 右栏 -->
                <td v-if="s_idx === 0" rowspan="7">
                    <textarea :name="'pos_R_' + b_idx" v-model="formData['pos_R_' + b_idx]" class="table-textarea" :disabled="!isEditable"></textarea>
                </td>

                <td><textarea :name="'depth_R_' + (b_idx * 7 + s_idx)" v-model="formData['depth_R_' + (b_idx * 7 + s_idx)]" :disabled="!isEditable" class="table-textarea"></textarea></td>
                <td><textarea :name="'actual_R_' + (b_idx * 7 + s_idx)" v-model="formData['actual_R_' + (b_idx * 7 + s_idx)]" :disabled="!isEditable" class="table-textarea"></textarea></td>

                <td v-if="s_idx === 0" rowspan="7"><textarea :name="'avg_R_' + b_idx" v-model="formData['avg_R_' + b_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
                <td v-if="s_idx === 0" rowspan="7"><textarea :name="'capacity_R_' + b_idx" v-model="formData['capacity_R_' + b_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
            </tr>
            </template>
        </template>

        <!-- 第一页显示完整信息，第二页及以后只显示仪器设备 -->
        <template v-if="currentIndex === 0">
            <!-- Row: 检测依据 -->
            <tr>
                <td class="label">检测依据</td>
                <td colspan="9" class="left-align"><textarea v-model="formData.testBasis"   name="testBasis" :disabled="!isEditable" class="table-textarea left-align"></textarea></td>
            </tr>
            <!-- Row: 仪器设备 -->
            <tr>
                <td class="label">仪器设备</td>
                <td colspan="9" class="left-align"><textarea v-model="formData.equipment"   name="equipment" :disabled="!isEditable" class="table-textarea left-align"></textarea></td>
            </tr>
            <!-- Row: 检测结论 -->
            <tr>
                <td class="label" style="height: 60px;">检测结论</td>
                <td colspan="9" class="left-align" style="vertical-align: top;">
                    <textarea v-model="formData.conclusion"  name="conclusion" :disabled="!isEditable" class="table-textarea left-align"></textarea>
                </td>
            </tr>
            <!-- Row: 备注 -->
            <tr>
                <td class="label">备注</td>
                <td colspan="9" class="left-align"><textarea v-model="formData.remarks"   name="remarks" :disabled="!isEditable" class="table-textarea left-align"></textarea></td>
            </tr>
        </template>
        <template v-else>
            <!-- Row: 仪器设备 -->
            <tr>
                <td class="label">仪器设备</td>
                <td colspan="9" class="left-align"><textarea v-model="formData.equipment"   name="equipment" :disabled="!isEditable" class="table-textarea left-align"></textarea></td>
            </tr>
        </template>
    </table>

    <div class="footer-info">
        <span style="position: relative;">
            审核：<span style="display: inline-block; width: 100px; border-bottom: 1px solid black; height: 1em;"></span>
            <div v-if="formData.reviewerSignature" style="position: absolute; top: -20px; left: 40px; pointer-events: none;">
                <img :src="formData.reviewerSignature" style="width: 80px; height: 40px; mix-blend-mode: multiply;" />
            </div>
        </span>
        <span style="position: relative;">
            检测：<span style="display: inline-block; width: 100px; border-bottom: 1px solid black; height: 1em;"></span>
            <div v-if="formData.testerSignature" style="position: absolute; top: -20px; left: 40px; pointer-events: none;">
                <img :src="formData.testerSignature" style="width: 80px; height: 40px; mix-blend-mode: multiply;" />
            </div>
        </span>
    </div>


    </form>

    
    <div v-if="showAnalysisModal" class="modal-overlay no-print">
      <div class="modal-content">
        <h3>数据分析</h3>

        <div class="form-group">
          <label>区间范围（从第几区到第几区，最大1-4）：</label>
          <div class="range-inputs">
            <span>从</span>
            <input type="number" v-model.number="analysisResults.startBlock" min="1" max="4" placeholder="起始区" />
            <span>至</span>
            <input type="number" v-model.number="analysisResults.endBlock" min="1" max="4" placeholder="结束区" />
            <span>区</span>
          </div>
        </div>

        <div class="form-group">
          <label>实测锤击数（必填范围）：</label>
          <div class="range-inputs">
            <input type="number" v-model="analysisResults.actualMin" placeholder="最小值" step="1" />
            <span>至</span>
            <input type="number" v-model="analysisResults.actualMax" placeholder="最大值" step="1" />
          </div>
        </div>

        <div class="modal-actions">
          <button @click="autoAnalyzeAndFill" class="btn btn-primary btn-small">自动分析并填充</button>
          <button @click="closeAnalysisModal" class="btn btn-secondary btn-small">关闭</button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { reactive, ref, onMounted, defineProps, inject, watch, computed } from 'vue'
import axios from 'axios'

// 注入导航方法
const navigateTo = inject('navigateTo')

const showAnalysisModal = ref(false)
const analysisResults = reactive({
  startBlock: '',
  endBlock: '',
  actualMin: '',
  actualMax: ''
})

const props = defineProps({
  id: String,
  wtNum: {
    type: String,
    default: null
  },
  draftMode: {
    type: Boolean,
    default: false
  }
})

const pdfForm = ref(null)
const draftMode = computed(() => props.draftMode)

const records = ref([])
const currentIndex = ref(0)

const isEditable = computed(() => {
  return formData.status == 0 || formData.status == 1 || formData.status == 2
})

const formData = reactive({
  entrustingUnit: '',
  unifiedNumber: '',
  projectName: '',
  commissionDate: '',
  constructionPart: '',
  testDate: '',
  soilProperties: '',
  testCategory: '',
  designCapacity: '',
  hammerWeight: '',
  dropDistance: '',
  testBasis: '',
  equipment: '',
  remarks: '',
  reviewer: '',
  recordReviewer: '',
  calculator: '',
  tester: '',
  recordTester: '',
  filler: '',
  conclusion: '',
  testerSignature: '',
  reviewerSignature: '',
  calculatorSignature: '',
  status: 0,
  id: null
})

const canEditStructure = computed(() => {
  const s = Number(formData.status)
  if (Number.isNaN(s)) return true
  return s === 0 || s === 2
})

const getStatusText = (status) => {
    const s = parseInt(status)
    switch(s) {
        // 统一状态名称
        case 0: return '草稿'
        case 1: return '已提交待审核'
        case 2: return '已打回'
        case 4: return '审核通过'
        case 5: return '审核通过'
        // 报告表状态 (10-15)
        case 10: return '草稿'
        case 11: return '已提交待审核'
        case 12: return '已打回'
        case 14: return '审核通过'
        case 15: return '审核通过'
        // 结果表状态 (20-25)
        case 20: return '草稿'
        case 21: return '已提交待审核'
        case 22: return '已打回'
        case 24: return '审核通过'
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
        case 4: return '#28a745' // success
        case 5: return '#28a745' // success
        // 报告表状态 (10-15)
        case 10: return '#6c757d' // secondary
        case 11: return '#007bff' // primary
        case 12: return '#dc3545' // danger
        case 14: return '#28a745' // success
        case 15: return '#28a745' // success
        // 结果表状态 (20-25)
        case 20: return '#6c757d' // secondary
        case 21: return '#007bff' // primary
        case 22: return '#dc3545' // danger
        case 24: return '#28a745' // success
        case 25: return '#28a745' // success
        default: return '#6c757d'
    }
}

const normalizePersonText = (value) => {
    if (!value) return ''
    return String(value).trim().replace(/\s+/g, '').toLowerCase()
}

const getUserAccount = (user) => {
    return user?.username || user?.userAccount || user?.userName || ''
}

const getUserDisplayName = (user) => {
    return user?.fullName || user?.realName || user?.userName || user?.nickName || getUserAccount(user) || ''
}

const isCurrentUser = (assigned, user) => {
    if (!assigned) return true
    const target = normalizePersonText(assigned)
    const candidates = [
        user?.username,
        user?.userAccount,
        user?.userName,
        user?.fullName,
        user?.realName,
        user?.nickName
    ].filter(Boolean)
    return candidates.some(c => normalizePersonText(c) === target)
}

const normalizeSignatureSrc = (value) => {
    if (!value) return ''
    if (typeof value !== 'string') return ''
    if (value.startsWith('data:image')) return value
    return `data:image/png;base64,${value}`
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
        // Role check: Only recordTester can submit
        // Logic: formData.recordTester (if set)
        // if (formData.recordTester && !isCurrentUser(formData.recordTester, user)) {
        //      alert('您不是该单据的记录检测人 (' + formData.recordTester + ')，无权提交')
        //      return
        // }

        // Auto fetch signature if missing
        if (!formData.testerSignature) {
            try {
                const sigRes = await axios.post('/api/signature/get', { userAccount: user.username })
                if (sigRes.data.success && sigRes.data.data && sigRes.data.data.signatureBlob) {
                     formData.testerSignature = `data:image/png;base64,${sigRes.data.data.signatureBlob}`
                     // 保存签名到数据库
                     await saveData()
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
        
        signatureData = formData.testerSignature.replace(/^data:image\/\w+;base64,/, '')
    } else if (action === 'AUDIT_PASS' || (action === 'REJECT' && formData.status === 1)) {
        // Role check: Only recordReviewer can audit/reject at status 1
        // Logic: formData.recordReviewer (if set)
        // if (formData.recordReviewer && !isCurrentUser(formData.recordReviewer, user)) {
        //    alert('您不是该单据的记录审核人 (' + formData.recordReviewer + ')，无权操作')
        //    return
        // }

        if (action === 'AUDIT_PASS') {
            // Auto fetch signature if missing
            if (!formData.reviewerSignature) {
                try {
                    const sigRes = await axios.post('/api/signature/get', { userAccount: user.username })
                    if (sigRes.data.success && sigRes.data.data && sigRes.data.data.signatureBlob) {
                         formData.reviewerSignature = `data:image/png;base64,${sigRes.data.data.signatureBlob}`
                         // Save signature to database
                         await saveData()
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
        }
    }
    // else if (action === 'SIGN_REVIEW') {
    //    // Role check: Only reviewer can sign
    //    if (formData.reviewer && user.username !== formData.reviewer) {
    //        alert('您不是该单据的复核人 (' + formData.reviewer + ')，无权签字')
    //        return
    //    }
    //
    //    if (!formData.reviewerSignature) {
    //        alert('请先进行复核人签字')
    //        return
    //    }
    //    signatureData = formData.reviewerSignature
    //}

    const request = {
        tableType: 'LIGHT_DYNAMIC_PENETRATION',
        recordId: formData.id,
        action: action,
        userAccount: user.username,
        signatureData: signatureData
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
            // Refresh
            const record = records.value[currentIndex.value]
            if (record) {
                record.status = response.data.data // Update status
                formData.status = response.data.data
                if (action === 'REJECT' && request.rejectReason) {
                    formData.rejectReason = request.rejectReason
                }
            }
        } else {
            alert('操作失败: ' + response.data.message)
        }
    } catch (e) {
        console.error('Workflow error', e)
        alert('操作出错')
    }
}

// Add dynamic fields to formData
for (let b = 0; b < 2; b++) {
    formData[`pos_L_${b}`] = ''
    formData[`pos_L2_${b}`] = '' // 第二页及以后的左侧测点位置第二行
    formData[`pos_L3_${b}`] = ''
    formData[`avg_L_${b}`] = ''
    formData[`capacity_L_${b}`] = ''
    formData[`pos_R_${b}`] = ''
    formData[`pos_R2_${b}`] = '' // 第二页及以后的右侧测点位置第二行
    formData[`pos_R3_${b}`] = ''
    formData[`avg_R_${b}`] = ''
    formData[`capacity_R_${b}`] = ''
    
    for (let s = 0; s < 7; s++) {
        const idx = b * 7 + s
        formData[`depth_L_${idx}`] = ''
        formData[`actual_L_${idx}`] = ''
        formData[`depth_R_${idx}`] = ''
        formData[`actual_R_${idx}`] = ''
    }
}

const formatDate = (d) => {
    if (!d) return ''
    const date = new Date(d)
    if (isNaN(date.getTime())) return ''
    const year = date.getFullYear()
    const month = ('0' + (date.getMonth() + 1)).slice(-2)
    const day = ('0' + date.getDate()).slice(-2)
    return `${year}-${month}-${day}`
}

const applyTechnicalDefaults = () => {
    if (!formData.hammerWeight || String(formData.hammerWeight).trim() === '') {
        formData.hammerWeight = '10'
    }
    if (!formData.dropDistance || String(formData.dropDistance).trim() === '') {
        formData.dropDistance = '50'
    }
}

const clearFormData = () => {
    // Keep entrustment info, clear others
    const keepKeys = ['entrustingUnit', 'unifiedNumber', 'projectName', 'commissionDate']
    for (const key in formData) {
        if (!keepKeys.includes(key)) {
            if (key.match(/^(pos|depth|actual|avg|capacity)_[LR]_\d+$/)) {
                 formData[key] = ''
            } else if (!['reviewer', 'tester', 'calculator', 'reviewerSignature', 'testerSignature', 'calculatorSignature'].includes(key)) { // Optional: clear signatures?
                 formData[key] = ''
            }
        }
    }
}

const mapRecordToFormData = (record) => {
    
    // Clear dynamic fields first
    const pointCount = currentIndex.value === 0 ? 2 : 3
    for (let b = 0; b < pointCount; b++) {
        formData[`pos_L_${b}`] = ''
        formData[`pos_L2_${b}`] = ''
        formData[`pos_L3_${b}`] = ''
        formData[`avg_L_${b}`] = ''
        formData[`capacity_L_${b}`] = ''
        formData[`pos_R_${b}`] = ''
        formData[`pos_R2_${b}`] = ''
        formData[`pos_R3_${b}`] = ''
        formData[`avg_R_${b}`] = ''
        formData[`capacity_R_${b}`] = ''
        for (let s = 0; s < 7; s++) {
            const idx = b * 7 + s
            formData[`depth_L_${idx}`] = ''
            formData[`actual_L_${idx}`] = ''
            formData[`depth_R_${idx}`] = ''
            formData[`actual_R_${idx}`] = ''
        }
    }

    if (record.dataJson) {
        try {
            const json = JSON.parse(record.dataJson)
            
            // Detect format
            let is6Row = json.format === '6-row';
            if (!is6Row && json.format !== '5-row') {
                // Heuristic: Record (5-row) usually fills 0-4, then 5-9.
                // Report (6-row) fills 0-5, then 6-11.
                // If index 5 is empty but index 6 has data, likely 6-row format (where 5 is the empty 6th row of block 0)
                const hasIdx5 = json['depth_L_5'] !== undefined && json['depth_L_5'] !== '';
                const hasIdx6 = json['depth_L_6'] !== undefined && json['depth_L_6'] !== '';
                
                // Also check max index. 5-row max index is 9. 6-row max index can be 11.
                const keys = Object.keys(json)
                const depthKeys = keys.filter(k => k.startsWith('depth_L_'))
                const maxIdx = depthKeys.length > 0 ? Math.max(...depthKeys.map(k => parseInt(k.split('_')[2]))) : -1

                if ((!hasIdx5 && hasIdx6) || maxIdx > 9) {
                    is6Row = true;
                }
            }

            // Avoid overwriting status from JSON
            Object.keys(json).forEach(key => {
                if (key === 'status') return
                // Skip data keys if we are handling them specially below (for 6-row)
                if (is6Row && key.match(/^(depth|actual)_[LR]_\d+$/)) return;
                
                formData[key] = json[key]
            })

            if (is6Row) {
                // Map 6-row JSON to 7-row Form
                const pointCount = currentIndex.value === 0 ? 2 : 3
                for (let b = 0; b < pointCount; b++) {
                    for (let s = 0; s < 7; s++) { // We now have 7 slots
                        const recordIdx = b * 7 + s; // Our UI index (0-13 or 0-20)
                        const jsonIdx = b * 6 + s;   // JSON index (0-5, 6-11, 12-17)
                        
                        if (json[`depth_L_${jsonIdx}`]) formData[`depth_L_${recordIdx}`] = json[`depth_L_${jsonIdx}`]
                        if (json[`actual_L_${jsonIdx}`]) formData[`actual_L_${recordIdx}`] = json[`actual_L_${jsonIdx}`]
                        if (json[`depth_R_${jsonIdx}`]) formData[`depth_R_${recordIdx}`] = json[`depth_R_${jsonIdx}`]
                        if (json[`actual_R_${jsonIdx}`]) formData[`actual_R_${recordIdx}`] = json[`actual_R_${jsonIdx}`]
                    }
                }
            }

            if (json.testDate) formData.testDate = json.testDate
            if (json.calculatorSignature) formData.calculatorSignature = json.calculatorSignature
        } catch (e) {
            console.error('JSON parse error', e)
        }
    }

    // Ensure status is a Number and takes precedence over JSON
    formData.status = record.status !== null && record.status !== undefined ? Number(record.status) : 0
    formData.id = record.id || null
    formData.rejectReason = record.rejectReason || ''

    // Map fields from BusinessEntity/Entrustment (Override JSON to ensure sync)
    if (record.clientUnit) formData.entrustingUnit = record.clientUnit
    // Prioritize wtNum (Unified Number), fallback to entrustmentId
    if (record.wtNum || record.entrustmentId) formData.unifiedNumber = record.wtNum || record.entrustmentId
    
    if (record.projectName) formData.projectName = record.projectName
    if (record.commissionDate) formData.commissionDate = formatDate(record.commissionDate)
    if (record.constructionPart) formData.constructionPart = record.constructionPart
    if (record.testDate) formData.testDate = formatDate(record.testDate) // Use formatted date if from entity
    
    // Explicitly map entity fields to formData to handle cases where dataJson is empty or incomplete
    if (record.soilProperty) formData.soilProperties = record.soilProperty
    if (record.testCategory) formData.testCategory = record.testCategory
    if (record.designCapacity) formData.designCapacity = record.designCapacity
    if (record.hammerWeight) formData.hammerWeight = record.hammerWeight
    if (record.dropDistance) formData.dropDistance = record.dropDistance
    if (record.testBasis) formData.testBasis = record.testBasis
    if (record.equipment) formData.equipment = record.equipment
    if (record.remarks) formData.remarks = record.remarks

    applyTechnicalDefaults()
    
    // Map Roles
    // Filler - Priority: record.filler
    formData.filler = record.filler || ''

    // Record Tester - Priority: record.recordTester -> record.tester (legacy)
    formData.recordTester = record.recordTester || record.tester || ''

    // Record Reviewer - Priority: record.recordReviewer -> record.reviewer (legacy)
    formData.recordReviewer = record.recordReviewer || record.reviewer || ''

    if (record.conclusion) formData.conclusion = record.conclusion

    
    // Ensure signatures from entity override JSON
    if (record.inspectSignaturePhoto) formData.testerSignature = normalizeSignatureSrc(record.inspectSignaturePhoto)
    if (record.reviewSignaturePhoto) formData.reviewerSignature = normalizeSignatureSrc(record.reviewSignaturePhoto)
}

const saveCurrentToState = () => {
    if (records.value.length === 0) return

    const record = records.value[currentIndex.value]
    
    // Map formData back to record fields
    record.clientUnit = formData.entrustingUnit
    record.wtNum = formData.unifiedNumber
    record.projectName = formData.projectName
    // commissionDate is formatted string, entity expects Date?
    // If backend accepts string in JSON (Jackson), it's fine. 
    // But mapper inserts it. The entity field is Date.
    // We should convert back if needed, or rely on Axios to send string and Backend to parse.
    // Usually Spring Boot handles "yyyy-MM-dd".
    record.commissionDate = formData.commissionDate 
    
    record.constructionPart = formData.constructionPart
    record.testDate = formData.testDate
    record.soilProperty = formData.soilProperties
    record.testCategory = formData.testCategory
    record.designCapacity = formData.designCapacity
    record.hammerWeight = formData.hammerWeight
    record.dropDistance = formData.dropDistance
    record.testBasis = formData.testBasis
    record.equipment = formData.equipment
    record.remarks = formData.remarks
    // record.reviewer = formData.recordReviewer
    // record.tester = formData.recordTester
    record.recordTester = formData.recordTester
    record.recordReviewer = formData.recordReviewer
    record.filler = formData.filler
    
    // Sync legacy fields
    record.tester = formData.recordTester
    record.reviewer = formData.recordReviewer

    // Remove legacy fields from payload to avoid confusion - Wait, we WANT them in the payload for backend compatibility
    // But we don't want them in dataJson (handled below by dynamicData construction)
    // delete record.tester
    // delete record.reviewer
    record.conclusion = formData.conclusion
    record.inspectSignaturePhoto = formData.testerSignature
    record.reviewSignaturePhoto = formData.reviewerSignature

    const dynamicData = {}
    if (formData.testDate) dynamicData.testDate = formData.testDate
    if (formData.calculator) dynamicData.calculator = formData.calculator
    if (formData.calculatorSignature) dynamicData.calculatorSignature = formData.calculatorSignature
    
    for (const key in formData) {
        if (key.match(/^(pos|depth|actual|avg|capacity)_[LR]_\d+$/)) {
            dynamicData[key] = formData[key]
        }
    }
    record.dataJson = JSON.stringify(dynamicData)
}

const loadData = async () => {
    // Prioritize wtNum if available, otherwise fallback to id (assuming it might be entrustmentId)
    const queryId = props.wtNum || props.id

    try {
        if (queryId) {
            const res = await axios.get(`/api/light-dynamic-penetration/get-by-entrustment-id?entrustmentId=${queryId}`)
            if (res.data.success && res.data.data) {
                // 确保records数组至少有一个元素
                if (res.data.data.length > 0) {
                    records.value = res.data.data
                } else {
                    // 如果API返回空数组，创建一个新记录
                    records.value = [{
                        id: null,
                        entrustmentId: queryId,
                        clientUnit: '',
                        projectName: '',
                        commissionDate: '',
                        wtNum: queryId
                    }]
                }
                
                // If we have both wtNum and id, it means id is likely the specific Record ID
                // Try to navigate to that specific record
                if (props.wtNum && props.id) {
                    const targetIndex = records.value.findIndex(r => r.id === props.id)
                    if (targetIndex !== -1) {
                        currentIndex.value = targetIndex
                    } else {
                        currentIndex.value = 0
                    }
                } else {
                    currentIndex.value = 0
                }
                
                mapRecordToFormData(records.value[currentIndex.value])
            } else {
                // No records found, try to fetch entrustment info to initialize
                // Use queryId as the identifier (wtNum or ID)
                let ent = null
                try {
                    // Try fetching by wtNum first if it looks like one (or just try both endpoints)
                    // Actually, Entrustment.vue passes wtNum.
                    const entRes = await axios.get(`/api/jc-core-wt-info/by-wt-num?wtNum=${encodeURIComponent(queryId)}`)
                    if (entRes.data.success && entRes.data.data) {
                        ent = entRes.data.data
                    } else {
                         // Fallback to by-id
                         const entRes2 = await axios.get(`/api/jc-core-wt-info/by-id?id=${queryId}`)
                         if (entRes2.data.success && entRes2.data.data) {
                             ent = entRes2.data.data
                         }
                    }
                } catch (e) {
                    console.warn('Failed to fetch entrustment info', e)
                }

                let newRecord = {
                    id: null,
                    entrustmentId: queryId,
                    clientUnit: '',
                    projectName: '',
                    commissionDate: '',
                    wtNum: queryId // Default unified number to ID
                }
                
                if (ent) {
                    // 检查委托单状态是否为审核通过（状态值为5），支持字符串和数字比较
                    if (parseInt(ent.status) === 5) {
                        newRecord.clientUnit = ent.client || ent.clientUnit || ''
                        newRecord.projectName = ent.projectName || ''
                        newRecord.commissionDate = ent.commissionDate || ''
                        newRecord.wtNum = ent.wtNum || queryId
                        newRecord.entrustmentId = ent.id || newRecord.entrustmentId
                        // Populate other fields if needed
                        newRecord.constructionPart = ent.constructionPart || ''
                        newRecord.testCategory = ent.testCategory || ''
                        newRecord.testDate = ent.testDate || ''
                    } else {
                        console.log('委托单状态未审核通过，不自动填充数据')
                    }
                }

                records.value = [newRecord]
                currentIndex.value = 0
                mapRecordToFormData(newRecord)
            }
        } else {
            // 如果queryId不存在，创建一个默认记录
            records.value = [{
                id: null,
                entrustmentId: '',
                clientUnit: '',
                projectName: '',
                commissionDate: '',
                wtNum: ''
            }]
            currentIndex.value = 0
            mapRecordToFormData(records.value[0])
        }
    } catch (e) {
        console.error('Load error', e)
        // 即使出错，也要确保records数组至少有一个元素
        records.value = [{
            id: null,
            entrustmentId: queryId || '',
            clientUnit: '',
            projectName: '',
            commissionDate: '',
            wtNum: queryId || ''
        }]
        currentIndex.value = 0
        mapRecordToFormData(records.value[0])
    }
}

onMounted(() => {
    loadData()
})

const prevRecord = () => {
    if (currentIndex.value > 0) {
        saveCurrentToState()
        currentIndex.value--
        mapRecordToFormData(records.value[currentIndex.value])
    }
}

const nextRecord = () => {
    if (currentIndex.value < records.value.length - 1) {
        saveCurrentToState()
        currentIndex.value++
        mapRecordToFormData(records.value[currentIndex.value])
    }
}

const addRecord = () => {
    if (!canEditStructure.value) {
        alert('提交后不能新增页面')
        return
    }
    saveCurrentToState()
    
    // Clone basic info from current record
    const current = records.value[currentIndex.value]
    const newRecord = {
        id: null,
        entrustmentId: current.entrustmentId,
        clientUnit: current.clientUnit,
        projectName: current.projectName,
        commissionDate: current.commissionDate,
        wtNum: current.wtNum || props.wtNum || formData.unifiedNumber,
        constructionPart: '', // Clear specific fields
        testDate: current.testDate, // Maybe keep date?
        soilProperty: current.soilProperty,
        testCategory: current.testCategory,
        designCapacity: current.designCapacity,
        hammerWeight: current.hammerWeight,
        dropDistance: current.dropDistance,
        status: 0,
        testBasis: current.testBasis,
        equipment: current.equipment,
        remarks: '',
        reviewer: current.reviewer,
        tester: current.tester,
        recordTester: current.recordTester,
        recordReviewer: current.recordReviewer,
        filler: formData.filler || current.filler || '',
        conclusion: '',
        inspectSignaturePhoto: current.inspectSignaturePhoto,
        reviewSignaturePhoto: current.reviewSignaturePhoto,
        dataJson: '{}'
    }
    
    records.value.push(newRecord)
    currentIndex.value = records.value.length - 1
    mapRecordToFormData(newRecord)
}

const deleteRecord = async () => {
    if (!canEditStructure.value) {
        alert('提交后不能删除页面')
        return
    }
    if (records.value.length <= 1) {
        alert('至少保留一条记录')
        return
    }

    const record = records.value[currentIndex.value]
    if (record.id) {
        if (!confirm('确定要删除此页记录吗？')) return
        try {
            const res = await axios.post('/api/light-dynamic-penetration/delete', { id: record.id })
            if (res.data.success) {
                alert('删除成功')
                records.value.splice(currentIndex.value, 1)
                if (records.value.length === 0) {
                    // Add a default empty one if all deleted
                     const newRecord = {
                        id: null,
                        entrustmentId: props.id,
                        wtNum: props.id
                    }
                    records.value.push(newRecord)
                }
                if (currentIndex.value >= records.value.length) {
                    currentIndex.value = records.value.length - 1
                }
                mapRecordToFormData(records.value[currentIndex.value])
            } else {
                alert('删除失败: ' + res.data.message)
            }
        } catch (e) {
            alert('删除失败: ' + e.message)
        }
    } else {
        // Not saved yet, just remove from list
        records.value.splice(currentIndex.value, 1)
        if (records.value.length === 0) {
             const newRecord = {
                id: null,
                entrustmentId: props.id,
                wtNum: props.id
            }
            records.value.push(newRecord)
        }
        if (currentIndex.value >= records.value.length) {
            currentIndex.value = records.value.length - 1
        }
        mapRecordToFormData(records.value[currentIndex.value])
    }
}

const printDocument = () => {
  window.print()
}

// 返回列表
const goToList = () => {
  if (navigateTo) {
    navigateTo('LightDynamicPenetrationRecordList');
  }
}

const openBackendPdfPreview = (actionUrl) => {
  if (!pdfForm.value) return
  const container = pdfForm.value.closest('.lightDynamicPenetrationRecord-container')
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
  const rect = container.getBoundingClientRect()
  const contentWidthPx = Math.max(container.scrollWidth || 0, rect.width || 0, 1)
  const contentHeightPx = Math.max(container.scrollHeight || 0, rect.height || 0, 1)
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
        span.className = el.className || ''
        span.setAttribute('data-name', name)
        span.setAttribute('style', `${style};display:inline-block;white-space:pre-wrap;`)
        el.replaceWith(span)
        return
      }

      if (tag === 'textarea') {
        const div = document.createElement('div')
        div.textContent = el.textContent || el.value || ''
        div.className = el.className || ''
        div.setAttribute('data-name', name)
        div.setAttribute('style', `${style};white-space:pre-wrap;`)
        el.replaceWith(div)
        return
      }

      if (tag === 'select') {
        const span = document.createElement('span')
        const selected = el.querySelector('option:checked')
        span.textContent = selected ? selected.textContent : (el.value || '')
        span.className = el.className || ''
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
      .pdf-page { width: 198mm; height: 285mm; overflow: hidden; box-sizing: border-box; }
      .pdf-preview { overflow: hidden; }
      .pdf-preview * { page-break-inside: avoid; break-inside: avoid; }
      .pdf-preview [data-name] { width: auto !important; max-width: 100% !important; box-sizing: border-box; overflow-wrap: anywhere; word-break: break-all; white-space: pre-wrap; }
      .pdf-preview.lightDynamicPenetrationRecord-container { width: 198mm; height: 285mm; margin: 0; padding: 0; box-sizing: border-box; display: flex; flex-direction: column; }
      .pdf-preview #pdfForm { flex: 1; min-height: 0; display: flex; flex-direction: column; }
      .pdf-preview #pdfForm > table { flex: 1; height: 100%; }
      .pdf-preview table { width: 100%; height: 100%; }
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
  <body><div class="pdf-sheet"><div class="pdf-page">${clone.outerHTML}</div></div></body>
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
    saveCurrentToState() // Ensure current state is up to date
    // Note: PDF generation usually works on single record or all?
    // The endpoint is likely designed for single record via ID?
    // Or maybe it accepts the form data via POST?
    // The current form submits to /api/pdf/light_dynamic_penetration_record/generate
    // It sends the inputs in the form.
    // Since we are showing one record, it will print that record.
    // If we want to print ALL, we need a different approach.
    // For now, "Download PDF" prints current sheet.
    openBackendPdfPreview('/api/pdf/light_dynamic_penetration_record/generate')
  }
}

const previewPdf = () => {
  if (pdfForm.value) {
    saveCurrentToState()
    openBackendPdfPreview('/api/pdf/light_dynamic_penetration_record/preview')
  }
}

const exportExcel = async () => {
  try {
    saveCurrentToState()
    const list = records.value && records.value.length > 0 ? records.value : []
    const totalPages = list.length > 0 ? list.length : 1
    const successPaths = []
    const failures = []

    const buildExportDataFromRecord = (record, index) => {
      let dynamicData = {}
      try {
        dynamicData = record && record.dataJson ? JSON.parse(record.dataJson) : {}
      } catch (e) {
        dynamicData = {}
      }

      const dataObj = {
        ...dynamicData,
        entrustingUnit: record?.clientUnit || '',
        unifiedNumber: record?.wtNum || '',
        projectName: record?.projectName || '',
        commissionDate: formatDate(record?.commissionDate),
        constructionPart: record?.constructionPart || '',
        testDate: formatDate(record?.testDate),
        soilProperties: record?.soilProperty || '',
        testCategory: record?.testCategory || '',
        designCapacity: record?.designCapacity || '',
        hammerWeight: record?.hammerWeight || '',
        dropDistance: record?.dropDistance || '',
        testBasis: record?.testBasis || '',
        equipment: record?.equipment || '',
        remarks: record?.remarks || '',
        conclusion: record?.conclusion || '',
        filler: record?.filler || '',
        recordTester: record?.recordTester || record?.tester || '',
        recordReviewer: record?.recordReviewer || record?.reviewer || '',
        pageNo: index + 1,
        pageNum: index + 1,
        pageIndex: index,
        totalPages,
        pageTotal: totalPages,
        pageText: `${index + 1}/${totalPages}`
      }

      delete dataObj.id
      delete dataObj.status
      delete dataObj.tester
      delete dataObj.reviewer
      delete dataObj.testerSignature
      delete dataObj.reviewerSignature
      delete dataObj.calculatorSignature

      return dataObj
    }

    for (let i = 0; i < totalPages; i++) {
      const record = list.length > 0 ? list[i] : null
      const pageNo = i + 1
      const entrustmentId = (record && record.entrustmentId) ? record.entrustmentId : (props.id || '')
      const id = record && record.id ? record.id : formData.id

      const dataObj = record ? buildExportDataFromRecord(record, i) : (() => {
        const d = { ...formData }
        delete d.id
        delete d.status
        delete d.tester
        delete d.reviewer
        delete d.testerSignature
        delete d.reviewerSignature
        delete d.calculatorSignature
        d.pageNo = pageNo
        d.pageNum = pageNo
        d.pageIndex = i
        d.totalPages = totalPages
        d.pageTotal = totalPages
        d.pageText = `${pageNo}/${totalPages}`
        return d
      })()

      try {
        const response = await axios.post('/api/light-dynamic-penetration/export-excel', {
          id,
          entrustmentId,
          pageNo,
          totalPages,
          data: dataObj
        })

        if (response.data && response.data.success) {
          successPaths.push(response.data.path)
        } else {
          failures.push(`第${pageNo}页：${response.data && response.data.message ? response.data.message : '未知错误'}`)
        }
      } catch (e) {
        failures.push(`第${pageNo}页：${e && e.message ? e.message : '请求失败'}`)
      }
    }

    if (successPaths.length > 0 && failures.length === 0) {
      alert(`导出成功：\n${successPaths.join('\n')}`)
    } else if (successPaths.length > 0) {
      alert(`部分导出成功：\n${successPaths.join('\n')}\n\n失败：\n${failures.join('\n')}`)
    } else {
      alert(`导出失败：\n${failures.join('\n') || '未知错误'}`)
    }
  } catch (error) {
    console.error('Export error:', error)
    alert('导出失败')
  }
}

const handleSign = async () => {
    const user = JSON.parse(localStorage.getItem('userInfo'))
    if (!user) {
        alert('请先登录')
        return
    }
    
    const currentAccount = user.username
    const currentName = user.userName

    try {
        const res = await axios.post('/api/signature/get', { userAccount: currentAccount })
        if (res.data.success && res.data.data && res.data.data.signatureBlob) {
            const imgSrc = `data:image/png;base64,${res.data.data.signatureBlob}`
            let signed = false
            let signType = ''
            
            // Match Record Tester (记录检测人)
            if (!formData.recordTester || formData.recordTester === currentName || formData.recordTester === currentAccount) {
                formData.testerSignature = imgSrc
                signed = true
                signType = '检测人'
            }
            
            // Match Record Reviewer (记录审核人) - 如果检测人已经签了，或者当前用户是审核人
            if (!signed && (!formData.recordReviewer || formData.recordReviewer === currentName || formData.recordReviewer === currentAccount)) {
                formData.reviewerSignature = imgSrc
                signed = true
                signType = '审核人'
            }
            
            if (signed) {
                // Also set date if empty
                if (!formData.testDate) {
                    formData.testDate = formatDate(new Date())
                }
                // 保存签名到数据库
                await saveData()
                // 显示成功消息
                alert(`签名成功并已保存，您以${signType}身份签字`)
            } else {
                alert(`当前用户(${currentName}/${currentAccount})与表单中的检测人(${formData.recordTester})或审核人(${formData.recordReviewer})不匹配，无法签名`)
            }
        } else {
             alert('未找到您的电子签名，请先在个人中心上传')
        }
    } catch (e) {
        console.error('Sign error', e)
        alert('获取签名失败')
    }
}

const openAnalysisModal = () => {
  showAnalysisModal.value = true
  Object.keys(analysisResults).forEach(k => analysisResults[k] = '')
}

const closeAnalysisModal = () => {
  showAnalysisModal.value = false
}

const fillPenetrationDepth = () => {
    // 贯入深度值数组
    const depths = ['0-30', '30-60', '60-90', '90-120', '120-150', '150-180', '180-210']
    
    // 获取当前页面的测点数量
    const pointCount = currentIndex.value === 0 ? 2 : 3
    
    // 填充左侧和右侧的贯入深度
    for (let b = 0; b < pointCount; b++) {
        for (let s = 0; s < 7; s++) {
            const idx = b * 7 + s
            formData[`depth_L_${idx}`] = depths[s]
            formData[`depth_R_${idx}`] = depths[s]
        }
    }
    
    alert('贯入深度已自动填充')
}

const autoAnalyzeAndFill = () => {
  const toInt = (v) => {
    if (v === null || v === undefined || v === '') return null
    const n = parseInt(String(v), 10)
    return Number.isFinite(n) ? n : null
  }

  const pointCount = currentIndex.value === 0 ? 2 : 3
  const sb = toInt(analysisResults.startBlock) ?? 1
  const eb = toInt(analysisResults.endBlock) ?? pointCount
  const validStartBlock = Math.max(1, Math.min(4, sb))
  const validEndBlock = Math.max(1, Math.min(4, eb))
  if (validStartBlock > validEndBlock) {
    alert('起始区不能大于结束区')
    return
  }
  const blocksIndices = []
  for (let b = validStartBlock - 1; b <= validEndBlock - 1; b++) {
    if (b < pointCount) blocksIndices.push(b)
  }
  if (!blocksIndices.length) {
    alert('当前页面可填的区数为 ' + pointCount + '，请调整选择范围')
    return
  }
  const actualMin = toInt(analysisResults.actualMin)
  const actualMax = toInt(analysisResults.actualMax)
  if (actualMin === null || actualMax === null) {
    alert('请填写“实测锤击数”的最小值和最大值')
    return
  }

  const minVal = Math.min(actualMin, actualMax)
  const maxVal = Math.max(actualMin, actualMax)
  if (minVal <= 0 || maxVal <= 0) {
    alert('“实测锤击数”范围需为正整数')
    return
  }

  const randomIntBetween = (min, max) => {
    const lo = Math.min(min, max)
    const hi = Math.max(min, max)
    return Math.floor(Math.random() * (hi - lo + 1)) + lo
  }

  const setIfEmpty = (key, value) => {
    if (formData[key] === '' || formData[key] === null || formData[key] === undefined) {
      formData[key] = value
    }
  }

  const depths = ['0-30', '30-60', '60-90', '90-120', '120-150', '150-180', '180-210']
  setIfEmpty('hammerWeight', '10')
  setIfEmpty('dropDistance', '50')

  for (const b of blocksIndices) {
    for (let s = 0; s < 7; s++) {
      const idx = b * 7 + s
      setIfEmpty(`depth_L_${idx}`, depths[s])
      setIfEmpty(`depth_R_${idx}`, depths[s])

      for (const side of ['L', 'R']) {
        const key = `actual_${side}_${idx}`
        if (formData[key] === '' || formData[key] === null || formData[key] === undefined) {
          formData[key] = String(randomIntBetween(minVal, maxVal))
        }
      }
    }

    const calcBlockAvg = (sidePrefix) => {
      const values = []
      for (let s = 0; s < 7; s++) {
        const idx = b * 7 + s
        const v = toInt(formData[`${sidePrefix}_${idx}`])
        if (v !== null) values.push(v)
      }
      if (!values.length) return null
      return values.reduce((sum, v) => sum + v, 0) / values.length
    }

    const avgL = calcBlockAvg('actual_L')
    const avgR = calcBlockAvg('actual_R')

    if (avgL !== null) setIfEmpty(`avg_L_${b}`, avgL.toFixed(1))
    if (avgR !== null) setIfEmpty(`avg_R_${b}`, avgR.toFixed(1))

    const capacityFromAvg = (avg) => {
      if (avg === null) return null
      const v = Math.round(avg * 15 + 50)
      return Math.max(50, Math.min(1000, v))
    }

    const avgLNum = avgL ?? (() => {
      const n = parseFloat(String(formData[`avg_L_${b}`] || ''))
      return Number.isFinite(n) ? n : null
    })()
    const avgRNum = avgR ?? (() => {
      const n = parseFloat(String(formData[`avg_R_${b}`] || ''))
      return Number.isFinite(n) ? n : null
    })()

    const capL = capacityFromAvg(avgLNum)
    const capR = capacityFromAvg(avgRNum)

    if (capL !== null) setIfEmpty(`capacity_L_${b}`, String(capL))
    if (capR !== null) setIfEmpty(`capacity_R_${b}`, String(capR))
  }

  if (!formData.designCapacity) {
    const caps = []
    for (let b = 0; b < pointCount; b++) {
      const l = parseFloat(String(formData[`capacity_L_${b}`] || ''))
      const r = parseFloat(String(formData[`capacity_R_${b}`] || ''))
      if (Number.isFinite(l)) caps.push(l)
      if (Number.isFinite(r)) caps.push(r)
    }
    if (caps.length) {
      const minCap = Math.min(...caps)
      formData.designCapacity = String(Math.max(50, Math.round(minCap * 0.9)))
    }
  }

  showAnalysisModal.value = false
  alert('自动分析并填充完成')
}

const saveData = async () => {
    // 保存后状态不变
    
    saveCurrentToState()
    const record = records.value[currentIndex.value]
    // 确保状态字段被传递给后端
    record.status = formData.status
    
    console.log('=== 开始保存记录表 ===')
    console.log('保存的记录数据:', {
        id: record.id,
        entrustmentId: record.entrustmentId,
        wtNum: record.wtNum,
        status: record.status,
        clientUnit: record.clientUnit,
        projectName: record.projectName,
        filler: record.filler,
        recordTester: record.recordTester,
        recordReviewer: record.recordReviewer,
        tester: record.tester,
        reviewer: record.reviewer,
        dataJson: record.dataJson ? JSON.parse(record.dataJson) : null
    })
    
    try {
        console.log('调用后端保存接口: /api/light-dynamic-penetration/save')
        const res = await axios.post('/api/light-dynamic-penetration/save', record)
        console.log('后端返回结果:', res.data)
        
        if (res.data.success) {
            console.log('保存成功，返回数据:', res.data.data)
            alert('保存成功')
            // Update record with returned data (especially ID)
            if (res.data.data) {
                records.value[currentIndex.value] = res.data.data
                // Update formData in case backend normalized something
                mapRecordToFormData(records.value[currentIndex.value])
                console.log('更新本地记录后的数据:', records.value[currentIndex.value])
            }
        } else {
            console.error('保存失败:', res.data.message)
            alert('保存失败: ' + (res.data.message || '未知错误'))
        }
    } catch (e) {
        console.error('保存过程中发生错误:', e)
        alert('保存失败: ' + e.message)
    } finally {
        console.log('=== 保存记录表操作完成 ===')
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

        .record-nav {
            display: inline-flex;
            align-items: center;
            gap: 10px;
            margin-left: 16px;
        }

        .record-nav-info {
            font-size: 14px;
            color: var(--text-light);
            white-space: nowrap;
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

        .modal-overlay {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0, 0, 0, 0.5);
            display: flex;
            justify-content: center;
            align-items: center;
            z-index: 1000;
        }

        .modal-content {
            background-color: white;
            padding: 30px;
            border-radius: 10px;
            width: 450px;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
            border: 1px solid #e0e0e0;
        }

        .modal-content h3 {
            margin-top: 0;
            margin-bottom: 25px;
            text-align: center;
            color: #333;
            font-size: 18px;
            font-weight: bold;
        }

        .form-group {
            margin-bottom: 25px;
        }

        .form-group label {
            display: block;
            margin-bottom: 8px;
            font-weight: 500;
            color: #555;
        }

        .range-inputs {
            display: flex;
            align-items: center;
            gap: 10px;
            padding: 15px;
            background-color: #f5f7fa;
            border-radius: 6px;
            border: 1px solid #e0e0e0;
        }

        .range-inputs input {
            width: 80px;
            padding: 6px 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 14px;
        }

        .modal-actions {
            display: flex;
            justify-content: flex-end;
            gap: 10px;
            margin-top: 16px;
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

        .lightDynamicPenetrationRecord-container {
            font-family: 'SimSun', 'Songti SC', serif;
            width: 210mm;
            font-size: 16px;
            color: #000;
            margin: 0 auto;
            padding: 24px;
            background-color: var(--bg-card);
            border-radius: 8px;
            box-shadow: var(--shadow);
            box-sizing: border-box;
        }
        h2 {
            text-align: center;
            margin-bottom: 12px;
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
            line-height: 1.2;
        }
        .label {
            font-weight: bold;
            white-space: nowrap;
        }
        .left-align {
            text-align: left;
            padding-left: 6px;
        }
        input[type="text"], textarea, select {
            width: 98%;
            border: 1px solid #b3d9ff;
            border-radius: 4px;
            outline: none;
            font-family: inherit;
            font-size: inherit;
            background-color: transparent;
            text-align: center;
            padding: 1px 3px;
            line-height: 1.2;
        }
        .left-align input[type="text"] {
            text-align: left;
        }
        input[type="text"]:focus, textarea:focus, select:focus {
            background-color: #f0f8ff;
            border-color: #3498db;
        }
        input[type="text"]:disabled:focus {
            background-color: transparent;
            outline: none;
            border-color: black;
        }
        textarea {
            resize: none;
            overflow: hidden;
            text-align: left;
            line-height: 1.2;
        }
        .table-textarea {
            width: 100%;
            height: 100%;
            border: 1px solid #b3d9ff;
            border-radius: 4px;
            outline: none;
            font-family: inherit;
            font-size: inherit;
            background-color: transparent;
            text-align: center;
            padding: 1px 3px;
            resize: none;
            overflow: hidden;
            line-height: 1.2;
        }
        .table-textarea.left-align {
            text-align: left;
        }
        .table-textarea:focus {
            background-color: #f0f8ff;
            border-color: #3498db;
        }
        .table-textarea:disabled:focus {
            background-color: transparent;
            outline: none;
            border-color: black;
        }

        /* 统一输入字段样式，确保与表格其他字段字体一致 */
        input[type="text"], textarea, .table-textarea {
            font-family: inherit;
            font-size: inherit;
            color: inherit;
        }

        input[type="text"]:disabled, textarea:disabled, .table-textarea:disabled {
            color: inherit;
            font-family: inherit;
            font-size: inherit;
        }
        .footer-info {
            display: flex;
            justify-content: space-between;
            margin-top: 15px;
            margin-bottom: 10px;
            font-size: 16px;
            font-weight: normal;
            padding: 0 30px;
        }
        .page-footer {
            margin-top: 10px;
            font-size: 14px;
            margin-bottom: 20px;
        }
        @media print {
            .lightDynamicPenetrationRecord-container {
                width: 100%;
                margin: 0;
                padding: 0;
                background-color: transparent;
                box-shadow: none;
            }
            input[type="text"], textarea {
                background-color: transparent !important;
            }
            .no-print {
                display: none;
            }
        }
    
</style>
