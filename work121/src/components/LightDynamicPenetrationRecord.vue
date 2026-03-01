<template>
  <div class="lightDynamicPenetrationRecord-container">


    <div class="no-print toolbar">
      <div class="toolbar-left">
        <button @click="goToList" class="link-button">&lt; 返回列表</button>
        <span v-if="!draftMode && records.length > 0" class="record-nav">
          <span class="record-nav-info">第 {{ currentIndex + 1 }} / {{ records.length }} 页</span>
          <button
            @click="prevRecord"
            :disabled="currentIndex === 0"
            class="btn btn-secondary btn-small"
          >
            上一页
          </button>
          <button
            @click="nextRecord"
            :disabled="currentIndex === records.length - 1"
            class="btn btn-secondary btn-small"
          >
            下一页
          </button>
          <button
            @click="addRecord"
            class="btn btn-primary btn-small"
          >
            新增页
          </button>
          <button
            @click="deleteRecord"
            :disabled="records.length === 0"
            class="btn btn-danger btn-small"
          >
            删除页
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
          v-if="!draftMode"
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
      </div>
        
        <div v-if="formData.status === 2 && formData.rejectReason" style="background-color: #ffebee; color: #c62828; padding: 10px; border-radius: 4px; margin-top: 10px; border: 1px solid #ef9a9a; clear: both;">
            <strong>打回原因：</strong> {{ formData.rejectReason }}
        </div>
    </div>

    <form id="pdfForm" ref="pdfForm" method="post">
    <h2>轻型动力触探检测记录表</h2>

    <div class="header-info">
        <span>委托单位：<input type="text" v-model="formData.entrustingUnit"   name="entrustingUnit" style="width: 250px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>统一编号：<input type="text" v-model="formData.unifiedNumber"   name="unifiedNumber" style="width: 150px; border-bottom: 1px solid black;"></span>
    </div>

    <table>
        <!-- Row 1 -->
        <tr>
            <td class="label">工程名称</td>
            <td colspan="4"><input type="text" v-model="formData.projectName"   name="projectName"></td>
            <td class="label">委托日期</td>
            <td colspan="4"><input type="text" v-model="formData.commissionDate"   name="commissionDate"></td>
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
            <td colspan="4"><input type="text" v-model="formData.soilProperties"   name="soilProperties"></td>
            <td class="label">检测类别</td>
            <td colspan="4"><input type="text" v-model="formData.testCategory"   name="testCategory"></td>
        </tr>
        <!-- Row 4: Params -->
        <tr>
            <td colspan="3" class="label">设计承载力<br>(kPa)</td>
            <td colspan="3" class="label">锤重量<br>(kg)</td>
            <td colspan="4" class="label">落距<br>(cm)</td>
        </tr>
        <tr>
            <td colspan="3"><input type="text" v-model="formData.designCapacity"   name="designCapacity"></td>
            <td colspan="3"><input type="text" v-model="formData.hammerWeight"   name="hammerWeight"></td>
            <td colspan="4"><input type="text" v-model="formData.dropDistance"   name="dropDistance"></td>
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
        <template v-for="(b, b_idx) in 2" :key="b_idx">
            <template v-for="(s, s_idx) in 5" :key="s_idx">
            <tr>
                <!-- 左栏 -->
                <td v-if="s_idx === 0" rowspan="5"><textarea :name="'pos_L_' + b_idx" v-model="formData['pos_L_' + b_idx]" style="height: 100%; width: 100%; border: none; text-align: center; padding-top: 10px;"></textarea></td>

                <td><input type="text" :name="'depth_L_' + (b_idx * 5 + s_idx)" v-model="formData['depth_L_' + (b_idx * 5 + s_idx)]"></td>
                <td><input type="text" :name="'actual_L_' + (b_idx * 5 + s_idx)" v-model="formData['actual_L_' + (b_idx * 5 + s_idx)]"></td>

                <td v-if="s_idx === 0" rowspan="5"><input type="text" :name="'avg_L_' + b_idx" v-model="formData['avg_L_' + b_idx]" style="height: 100%;"></td>
                <td v-if="s_idx === 0" rowspan="5"><input type="text" :name="'capacity_L_' + b_idx" v-model="formData['capacity_L_' + b_idx]" style="height: 100%;"></td>

                <!-- 右栏 -->
                <td v-if="s_idx === 0" rowspan="5"><textarea :name="'pos_R_' + b_idx" v-model="formData['pos_R_' + b_idx]" style="height: 100%; width: 100%; border: none; text-align: center; padding-top: 10px;"></textarea></td>

                <td><input type="text" :name="'depth_R_' + (b_idx * 5 + s_idx)" v-model="formData['depth_R_' + (b_idx * 5 + s_idx)]"></td>
                <td><input type="text" :name="'actual_R_' + (b_idx * 5 + s_idx)" v-model="formData['actual_R_' + (b_idx * 5 + s_idx)]"></td>

                <td v-if="s_idx === 0" rowspan="5"><input type="text" :name="'avg_R_' + b_idx" v-model="formData['avg_R_' + b_idx]" style="height: 100%;"></td>
                <td v-if="s_idx === 0" rowspan="5"><input type="text" :name="'capacity_R_' + b_idx" v-model="formData['capacity_R_' + b_idx]" style="height: 100%;"></td>
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
            <td class="label" style="height: 60px;">检测结论</td>
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

    

  </div>
</template>

<script setup>
import { reactive, ref, onMounted, defineProps, inject, watch, computed } from 'vue'
import axios from 'axios'

// 注入导航方法
const navigateTo = inject('navigateTo')

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

const getStatusText = (status) => {
    const s = parseInt(status)
    switch(s) {
        case 0: return '草稿'
        case 1: return '待审核'
        case 2: return '已打回'
        case 3: return '待签字'
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

        if (!formData.testerSignature) {
            alert('请先进行检测人签字')
            return
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
    formData[`avg_L_${b}`] = ''
    formData[`capacity_L_${b}`] = ''
    formData[`pos_R_${b}`] = ''
    formData[`avg_R_${b}`] = ''
    formData[`capacity_R_${b}`] = ''
    
    for (let s = 0; s < 5; s++) {
        const idx = b * 5 + s
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
    for (let b = 0; b < 2; b++) {
        formData[`pos_L_${b}`] = ''
        formData[`avg_L_${b}`] = ''
        formData[`capacity_L_${b}`] = ''
        formData[`pos_R_${b}`] = ''
        formData[`avg_R_${b}`] = ''
        formData[`capacity_R_${b}`] = ''
        for (let s = 0; s < 5; s++) {
            const idx = b * 5 + s
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
                // Map 6-row JSON to 5-row Form
                for (let b = 0; b < 2; b++) {
                    for (let s = 0; s < 5; s++) { // We only have 5 slots
                        const recordIdx = b * 5 + s; // Our UI index (0-9)
                        const jsonIdx = b * 6 + s;   // JSON index (0-4, 6-10) - skipping 5 and 11
                        
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
    formData.status = record.status !== undefined ? Number(record.status) : 0
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
    if (!queryId) return

    try {
        const res = await axios.get(`/api/light-dynamic-penetration/get-by-entrustment-id?entrustmentId=${queryId}`)
        if (res.data.success && res.data.data && res.data.data.length > 0) {
            records.value = res.data.data
            
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
                newRecord.clientUnit = ent.client || ent.clientUnit || ''
                newRecord.projectName = ent.projectName || ''
                newRecord.commissionDate = ent.commissionDate || ''
                newRecord.wtNum = ent.wtNum || queryId
                newRecord.entrustmentId = ent.id || newRecord.entrustmentId
                // Populate other fields if needed
                newRecord.constructionPart = ent.constructionPart || ''
                newRecord.testCategory = ent.testCategory || ''
                newRecord.testDate = ent.testDate || ''
            }

            records.value = [newRecord]
            currentIndex.value = 0
            mapRecordToFormData(newRecord)
        }
    } catch (e) {
        console.error('Load error', e)
        records.value = [{ id: null, entrustmentId: queryId }]
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
    pdfForm.value.action = '/api/pdf/light_dynamic_penetration_record/generate'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}

const previewPdf = () => {
  if (pdfForm.value) {
    saveCurrentToState()
    pdfForm.value.action = '/api/pdf/light_dynamic_penetration_record/preview'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}

const handleSign = () => {
    // Only Tester can sign
    // if (formData.tester && !isCurrentUser(formData.tester, JSON.parse(localStorage.getItem('userInfo')))) {
    //    alert('您不是该单据的检测人 (' + formData.tester + ')，无权签字')
    //    return
    // }
    
    // In a real app, this would open a signature pad
    // For now, we'll simulate fetching a signature from the user's profile
    const user = JSON.parse(localStorage.getItem('userInfo'))
    if (!user) {
        alert('请先登录')
        return
    }

    axios.post('/api/signature/get', { userAccount: getUserAccount(user) })
        .then(res => {
            if (res.data.success && res.data.data && res.data.data.signatureBlob) {
                 formData.testerSignature = `data:image/png;base64,${res.data.data.signatureBlob}`
                 // Also set date if empty
                 if (!formData.testDate) {
                     formData.testDate = formatDate(new Date())
                 }
            } else {
                 alert('未找到您的电子签名，请先在个人中心上传')
            }
        })
        .catch(e => {
            console.error('Sign error', e)
            alert('获取签名失败')
        })
}

const saveData = async () => {
    saveCurrentToState()
    const record = records.value[currentIndex.value]
    
    try {
        const res = await axios.post('/api/light-dynamic-penetration/save', record)
        if (res.data.success) {
            alert('保存成功')
            // Update record with returned data (especially ID)
            if (res.data.data) {
                records.value[currentIndex.value] = res.data.data
                // Update formData in case backend normalized something
                mapRecordToFormData(records.value[currentIndex.value])
            }
        } else {
            alert('保存失败: ' + (res.data.message || '未知错误'))
        }
    } catch (e) {
        alert('保存失败: ' + e.message)
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

        .record-nav {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            margin-left: 16px;
        }

        .record-nav-info {
            font-size: 13px;
            color: #666;
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

        .lightDynamicPenetrationRecord-container {
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
            justify-content: space-between;
            margin-top: 30px;
            margin-bottom: 20px;
            font-size: 16px;
            font-weight: bold;
            padding: 0 50px;
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
            }
            input[type="text"], textarea {
                background-color: transparent !important;
            }
            .no-print {
                display: none;
            }
        }
    
</style>
