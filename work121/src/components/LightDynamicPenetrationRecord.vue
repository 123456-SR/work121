<template>
  <div class="lightDynamicPenetrationRecord-container">


    <div class="no-print" style="margin-bottom: 20px;">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;">
            <div>
                <button @click="goToList" style="text-decoration: none; color: blue; background: none; border: none; cursor: pointer; padding: 0;">&lt; 返回列表</button>
            </div>
            <div v-if="!draftMode">
                <span v-if="records.length > 0" style="margin-right: 10px;">
                    第 {{ currentIndex + 1 }} / {{ records.length }} 页
                </span>
                <button @click="prevRecord" :disabled="currentIndex === 0" style="margin-right: 5px;">上一页</button>
                <button @click="nextRecord" :disabled="currentIndex === records.length - 1" style="margin-right: 5px;">下一页</button>
                <button @click="addRecord" style="margin-right: 5px; background-color: #28a745; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;">新增页</button>
                <button @click="deleteRecord" style="margin-right: 5px; background-color: #dc3545; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;" :disabled="records.length === 0">删除页</button>
            </div>
        </div>
        
        <div style="display: flex; justify-content: flex-end; align-items: center;">
            <div v-if="formData.status !== undefined" style="margin-right: 20px; font-weight: bold; color: #666;">
                状态: <span :style="{color: getStatusColor(formData.status)}">{{ getStatusText(formData.status) }}</span>
            </div>

            <!-- Workflow Buttons -->
            <template v-if="formData.id && !draftMode">
                <button v-if="formData.status === 0 || formData.status === 2" @click="submitWorkflow('SUBMIT')" style="margin-right: 10px; background-color: #4CAF50; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;">提交审核</button>
                
                <button v-if="formData.status === 1" @click="submitWorkflow('AUDIT_PASS')" style="margin-right: 10px; background-color: #2196F3; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;">审核通过</button>
                <button v-if="formData.status === 1" @click="submitWorkflow('REJECT')" style="margin-right: 10px; background-color: #f44336; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;">打回</button>
            </template>

            <button v-if="!draftMode" @click="handleSign" style="margin-left: 10px; background-color: #17a2b8; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;">签字</button>
            <button @click="saveData" style="margin-left: 10px; background-color: #007bff; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;">保存</button>
            <button v-if="!draftMode" @click="printDocument" style="margin-left: 10px;">打印此单</button>
            <button v-if="!draftMode" @click="generatePdf" style="margin-left: 10px;">下载PDF</button>
            <button v-if="!draftMode" @click="previewPdf" style="margin-left: 10px;">预览PDF</button>
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
        <span>
            审核：
            <div style="display: inline-block; position: relative; width: 100px;">
                <input type="text" v-model="formData.reviewer" style="width: 100%; border-bottom: 1px solid black;" readonly>
            </div>
        </span>
        <span>
            计算：
            <div style="display: inline-block; position: relative; width: 100px;">
                <input type="text" v-model="formData.calculator" style="width: 100%; border-bottom: 1px solid black;" :style="{ opacity: formData.calculatorSignature ? 0 : 1 }">
                <img v-if="formData.calculatorSignature" :src="formData.calculatorSignature" style="position: absolute; bottom: 0; left: 0; width: 100%; height: 40px; object-fit: contain; pointer-events: none; background-color: transparent;">
            </div>
        </span>
        <span>
            检测：
            <div style="display: inline-block; position: relative; width: 100px;">
                <input type="text" v-model="formData.tester" style="width: 100%; border-bottom: 1px solid black;" :style="{ opacity: formData.testerSignature ? 0 : 1 }">
                <img v-if="formData.testerSignature" :src="formData.testerSignature" style="position: absolute; bottom: 0; left: 0; width: 100%; height: 40px; object-fit: contain; pointer-events: none; background-color: transparent;">
            </div>
        </span>
    </div>


    </form>

    

  </div>
</template>

<script setup>
import { reactive, ref, onMounted, defineProps, inject, watch } from 'vue'
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
const draftMode = ref(props.draftMode)

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
  // approver: '',
  calculator: '',
  tester: '',
  conclusion: '',
  testerSignature: '',
  // reviewerSignature: '',
  // approverSignature: '',
  calculatorSignature: '',
  status: 0
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
        // Role check: Only tester can submit
        if (formData.tester && user.username !== formData.tester) {
            alert('您不是该单据的检测人 (' + formData.tester + ')，无权提交')
            return
        }

        if (!formData.testerSignature) {
            alert('请先进行检测人签字')
            return
        }
        
        // Ensure calculator has signed if a calculator is specified
        if (formData.calculator && !formData.calculatorSignature) {
            alert('请先进行计算人签字')
            return
        }
        
        signatureData = formData.testerSignature
    } else if (action === 'AUDIT_PASS' || (action === 'REJECT' && formData.status === 1)) {
        // Role check: Only reviewer can audit/reject at status 1
        if (formData.reviewer && user.username !== formData.reviewer) {
            alert('您不是该单据的复核人 (' + formData.reviewer + ')，无权操作')
            return
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
            } else if (!['reviewer', 'approver', 'tester', 'calculator', 'reviewerSignature', 'approverSignature', 'testerSignature', 'calculatorSignature'].includes(key)) { // Optional: clear signatures?
                 formData[key] = ''
            }
        }
    }
}

const mapRecordToFormData = (record) => {
    formData.status = record.status !== undefined ? record.status : 0
    formData.rejectReason = record.rejectReason || ''
    
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
            Object.assign(formData, json)
            if (json.testDate) formData.testDate = json.testDate
            if (json.calculatorSignature) formData.calculatorSignature = json.calculatorSignature
        } catch (e) {
            console.error('JSON parse error', e)
        }
    }

    // Map fields from BusinessEntity/Entrustment (Override JSON to ensure sync)
    if (record.clientUnit) formData.entrustingUnit = record.clientUnit
    // Prioritize wtNum (Unified Number), fallback to entrustmentId
    if (record.wtNum || record.entrustmentId) formData.unifiedNumber = record.wtNum || record.entrustmentId
    
    if (record.projectName) formData.projectName = record.projectName
    if (record.commissionDate) formData.commissionDate = formatDate(record.commissionDate)
    if (record.constructionPart) formData.constructionPart = record.constructionPart
    if (record.testDate) formData.testDate = formatDate(record.testDate) // Use formatted date if from entity
    if (record.soilProperty) formData.soilProperties = record.soilProperty
    if (record.testCategory) formData.testCategory = record.testCategory
    if (record.designCapacity) formData.designCapacity = record.designCapacity
    if (record.hammerWeight) formData.hammerWeight = record.hammerWeight
    if (record.dropDistance) formData.dropDistance = record.dropDistance
    if (record.testBasis) formData.testBasis = record.testBasis
    if (record.equipment) formData.equipment = record.equipment
    if (record.remarks) formData.remarks = record.remarks
    if (record.reviewer) formData.reviewer = record.reviewer
    if (record.tester) formData.tester = record.tester
    if (record.conclusion) formData.conclusion = record.conclusion
    
    // Ensure signatures from entity override JSON
    if (record.inspectSignaturePhoto) formData.testerSignature = record.inspectSignaturePhoto
    if (record.reviewSignaturePhoto) formData.reviewerSignature = record.reviewSignaturePhoto
    if (record.approveSignaturePhoto) formData.approverSignature = record.approveSignaturePhoto
}

const saveCurrentToState = () => {
    if (records.value.length === 0) return

    const record = records.value[currentIndex.value]
    
    // Map formData back to record fields
    record.clientUnit = formData.entrustingUnit
    record.entrustmentId = formData.unifiedNumber // Assuming unifiedNumber is the ID
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
    record.reviewer = formData.reviewer
    record.approver = formData.approver
    record.tester = formData.tester
    record.conclusion = formData.conclusion
    record.inspectSignaturePhoto = formData.testerSignature
    record.reviewSignaturePhoto = formData.reviewerSignature
    record.approveSignaturePhoto = formData.approverSignature

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
        entrustmentId: props.wtNum || props.id || current.entrustmentId,
        clientUnit: current.clientUnit,
        projectName: current.projectName,
        commissionDate: current.commissionDate,
        wtNum: current.wtNum, // Unified number should be same
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
        approver: current.approver,
        tester: current.tester,
        conclusion: '',
        inspectSignaturePhoto: current.inspectSignaturePhoto,
        reviewSignaturePhoto: current.reviewSignaturePhoto,
        approveSignaturePhoto: current.approveSignaturePhoto,
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
      if (formData.tester === user.username || formData.tester === currentName) {
        formData.testerSignature = imgSrc
        signed = true
      }

      // Match Reviewer
      // if (formData.reviewer === user.username || formData.reviewer === currentName) {
      //   formData.reviewerSignature = imgSrc
      //   signed = true
      // }

      // Match Approver
      // if (formData.approver === user.username || formData.approver === currentName) {
      //   formData.approverSignature = imgSrc
      //   signed = true
      // }
      
      // Match Calculator
      if (formData.calculator === user.username || formData.calculator === currentName) {
        formData.calculatorSignature = imgSrc
        signed = true
      }
      
      if (signed) {
        alert('签名成功')
      } else {
        alert(`当前用户(${currentName})与表单中的人员不匹配，无法签名`)
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
