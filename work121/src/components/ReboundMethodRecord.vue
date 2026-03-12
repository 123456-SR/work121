<template>
  <div class="reboundMethodRecord-container">


    <div class="no-print toolbar">
      <div class="toolbar-left">
        <button @click="goToList" class="link-button">&lt; 返回列表</button>
        <span v-if="!draftMode" class="record-nav">
          <button
            @click="prevRecord"
            :disabled="totalRecords <= 0 || currentIndex <= 0"
            class="btn btn-secondary btn-small"
          >
            上一页
          </button>
          <span class="record-nav-info">
            记录 {{ totalRecords > 0 ? currentIndex + 1 : 0 }} / {{ totalRecords }}
          </span>
          <button
            @click="nextRecord"
            :disabled="totalRecords <= 0 || currentIndex >= totalRecords - 1"
            class="btn btn-secondary btn-small"
          >
            下一页
          </button>
          <button
            @click="addRecord"
            class="btn btn-primary btn-small"
          >
            添加记录
          </button>
          <button
            @click="deleteRecord"
            :disabled="totalRecords <= 0"
            class="btn btn-danger btn-small"
          >
            删除当前记录
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

        <!-- 只要不是草稿预览模式，就显示流程按钮；具体是否已保存由 submitWorkflow 再校验 -->
        <template v-if="!draftMode">
          <button
            v-if="parseInt(formData.status) === 0 || parseInt(formData.status) === 2 || parseInt(formData.status) === 4"
            @click="submitWorkflow('SUBMIT')"
            class="btn btn-primary btn-small"
          >
            提交审核
          </button>
          <button
            v-if="parseInt(formData.status) === 1"
            @click="submitWorkflow('AUDIT_PASS')"
            class="btn btn-primary btn-small"
          >
            升级为已审核
          </button>
          <button
            v-if="parseInt(formData.status) === 1"
            @click="submitWorkflow('REJECT')"
            class="btn btn-danger btn-small"
          >
            打回
          </button>
        </template>


        <button
          v-if="!draftMode"
          @click="printDocument"
          class="btn btn-secondary btn-small"
        >
          打印此单
        </button>
        <button
          v-if="isEditable"
          @click="showAnalysis = true"
          class="btn btn-secondary btn-small"
        >
          数据分析
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
          @click="submitForm"
          class="btn btn-secondary btn-small"
          :disabled="!isEditable"
        >
          保存
        </button>
      </div>
    </div>

    <form id="pdfForm" ref="pdfForm" method="post">
    <h2>回弹法检测混凝土抗压强度记录表</h2>

    <div class="header-top">
        <span>委托单位：<input type="text" v-model="formData.entrustingUnit"   name="entrustingUnit" style="width: 200px; border-bottom: 1px solid black; text-align: left;" :disabled="!isEditable"></span>
        <span>统一编号：<input type="text" v-model="formData.unifiedNumber"   name="unifiedNumber" style="width: 150px; border-bottom: 1px solid black;" disabled></span>
    </div>
    <div class="header-top">
        <span>样品编号：<input type="text" v-model="formData.sampleNo"   name="sampleNo" style="width: 200px; border-bottom: 1px solid black; text-align: left;" :disabled="!isEditable"></span>
    </div>

    <table>
        <colgroup>
            <col style="width: 4%;"> <!-- Col 1: Index -->
            <template v-for="(n, i_idx) in 16" :key="i_idx">
            <col style="width: 3%;"> <!-- Cols 2-17: Values (48%) -->
            </template>
            <col style="width: 12%;"> <!-- Col 18: Avg -->
            <col style="width: 12%;"> <!-- Col 19: Depth -->
            <col style="width: 12%;"> <!-- Col 20: Est Str -->
            <col style="width: 12%;"> <!-- Col 21: Corr Str -->
        </colgroup>

        <!-- Header Info -->
        <tr>
            <td colspan="2" class="label">工程名称</td>
            <td colspan="15" class="left-align"><textarea v-model="formData.projectName"   name="projectName" :disabled="!isEditable" class="table-textarea left-align"></textarea></td>
            <td colspan="3" class="label">委托日期</td>
            <td colspan="1"><textarea v-model="formData.commissionDate"   name="commissionDate" :disabled="!isEditable" class="table-textarea"></textarea></td>
        </tr>
        <tr>
            <td colspan="2" class="label">结构部位</td>
            <td colspan="5"><textarea v-model="formData.structurePart"   name="structurePart" :disabled="!isEditable" class="table-textarea"></textarea></td>
            <td colspan="3" class="label">检测类别</td>
            <td colspan="7"><textarea v-model="formData.testCategory"   name="testCategory" :disabled="!isEditable" class="table-textarea"></textarea></td>
            <td colspan="3" class="label">浇筑日期</td>
            <td colspan="1"><textarea v-model="formData.pourDate"   name="pourDate" :disabled="!isEditable" class="table-textarea"></textarea></td>
        </tr>
        <tr>
            <td colspan="2" class="label">样品状态</td>
            <td colspan="5"><textarea v-model="formData.sampleStatus"   name="sampleStatus" :disabled="!isEditable" class="table-textarea"></textarea></td>
            <td colspan="3" class="label">测试角度</td>
            <td colspan="7"><textarea v-model="formData.testAngle"   name="testAngle" :disabled="!isEditable" class="table-textarea"></textarea></td>
            <td colspan="3" class="label">检测日期</td>
            <td colspan="1"><textarea v-model="formData.testDate"   name="testDate" :disabled="!isEditable" class="table-textarea"></textarea></td>
        </tr>
        <tr>
            <td colspan="2" class="label">设计指标</td>
            <td colspan="5"><textarea v-model="formData.designIndex"   name="designIndex" :disabled="!isEditable" class="table-textarea"></textarea></td>
            <td colspan="10" class="label">构件厚度或骨料最大粒径</td>
            <td colspan="4"><textarea v-model="formData.aggregateSize"   name="aggregateSize" :disabled="!isEditable" class="table-textarea"></textarea></td>
        </tr>

        <!-- Data Table Header -->
        <tr>
            <td rowspan="2" class="label">测<br>区</td>
            <td colspan="16" class="label">回弹值</td>
            <td rowspan="2" class="label">平均<br>回弹<br>值</td>
            <td rowspan="2" class="label">碳化<br>深度<br>mm</td>
            <td rowspan="2" class="label">推定<br>强度<br>值MPa</td>
            <td rowspan="2" class="label">碳化修<br>正强度<br>值MPa</td>
        </tr>
        <tr>
            <template v-for="(n, i_idx) in 16" :key="i_idx">
            <td class="label">{{ (i_idx + 1) }}</td>
            </template>
        </tr>

        <!-- Data Rows -->
        <template v-for="(n, i_idx) in 10" :key="i_idx">
        <tr>
            <td>{{ (i_idx + 1) }}</td>
            <template v-for="(n, j_idx) in 16" :key="j_idx">
            <td><textarea :name="'reboundValue_' + (i_idx + 1) + '_' + (j_idx + 1)" v-model="formData['reboundValue_' + (i_idx + 1) + '_' + (j_idx + 1)]" :disabled="!isEditable" class="table-textarea"></textarea></td>
            </template>
            <td><textarea :name="'avgRebound_' + (i_idx + 1)" v-model="formData['avgRebound_' + (i_idx + 1)]" :disabled="!isEditable" class="table-textarea"></textarea></td>
            <td><textarea :name="'carbonDepth_' + (i_idx + 1)" v-model="formData['carbonDepth_' + (i_idx + 1)]" :disabled="!isEditable" class="table-textarea"></textarea></td>
            <td><textarea :name="'estimatedStrength_' + (i_idx + 1)" v-model="formData['estimatedStrength_' + (i_idx + 1)]" :disabled="!isEditable" class="table-textarea"></textarea></td>
            <td><textarea :name="'correctedStrength_' + (i_idx + 1)" v-model="formData['correctedStrength_' + (i_idx + 1)]" :disabled="!isEditable" class="table-textarea"></textarea></td>
        </tr>
        </template>

        <!-- Summary Section -->
        <tr>
            <td colspan="3" class="label">平均强度值<br>MPa</td>
            <td colspan="2"><textarea v-model="formData.avgStrength"   name="avgStrength" :disabled="!isEditable" class="table-textarea"></textarea></td>
            <td colspan="3" class="label">标准差<br>MPa</td>
            <td colspan="2"><textarea v-model="formData.stdDev"   name="stdDev" :disabled="!isEditable" class="table-textarea"></textarea></td>
            <td colspan="3" class="label">变异系<br>数%</td>
            <td colspan="2"><textarea v-model="formData.coefVariation"   name="coefVariation" :disabled="!isEditable" class="table-textarea"></textarea></td>
            <td colspan="3" class="label">构件强度推<br>定值 MPa</td>
            <td colspan="3"><textarea v-model="formData.compEstimatedStrength"   name="compEstimatedStrength" :disabled="!isEditable" class="table-textarea"></textarea></td>
        </tr>
        <tr>
            <td colspan="2" class="label">仪器设备</td>
            <td colspan="11" class="left-align"><textarea v-model="formData.equipment"   name="equipment" :disabled="!isEditable" class="table-textarea left-align"></textarea></td>
            <td colspan="4" class="label">碳化平均值<br>mm</td>
            <td colspan="4"><textarea v-model="formData.avgCarbonation"   name="avgCarbonation" :disabled="!isEditable" class="table-textarea"></textarea></td>
        </tr>
        <tr>
            <td colspan="2" class="label">依据标准</td>
            <td colspan="11" class="left-align"><textarea v-model="formData.standard"   name="standard" :disabled="!isEditable" class="table-textarea left-align"></textarea></td>
            <td colspan="4" class="label">率定值</td>
            <td colspan="4" class="left-align">

                检测前：<textarea v-model="formData.calibrationBefore"   name="calibrationBefore" :disabled="!isEditable" class="table-textarea small-textarea"></textarea><br>
                检测后：<textarea v-model="formData.calibrationAfter"   name="calibrationAfter" :disabled="!isEditable" class="table-textarea small-textarea"></textarea>
            </td>
        </tr>
        <tr>
            <td colspan="2" class="label">结论</td>
            <td colspan="19" class="left-align" style="height: 40px;">
                <textarea v-model="formData.conclusion"   name="conclusion" :disabled="!isEditable" class="table-textarea left-align"></textarea>
            </td>
        </tr>
        <tr>
            <td colspan="2" class="label" style="height: 150px; vertical-align: top;">测区示意图：</td>
            <td colspan="19" style="height: 150px;">
                <!-- Placeholder for diagram -->
            </td>
        </tr>
        <tr>
            <td colspan="2" class="label">备注</td>
            <td colspan="19" class="left-align" style="height: 40px;">
                <textarea v-model="formData.remarks"  name="remarks" :disabled="!isEditable" class="table-textarea left-align"></textarea>
            </td>
        </tr>
    </table>

    <div class="footer-info">
        <span style="position: relative;">
            审核：<span style="display: inline-block; width: 100px; border-bottom: 1px solid black; height: 1em;"></span>
            <div v-if="formData.recordReviewSign" style="position: absolute; top: -20px; left: 40px; pointer-events: none;">
                <img :src="formData.recordReviewSign" style="width: 80px; height: 40px; mix-blend-mode: multiply;" />
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
  <div v-if="showAnalysis" class="modal-overlay" @click="showAnalysis=false">
    <div class="modal-content" @click.stop style="position: relative;">
      <h3>数据分析</h3>
      <button class="close-btn" @click="showAnalysis=false">&times;</button>
      <div class="modal-body">
        <div class="form-group">
          <label>数据范围：</label>
          <div class="range-inputs">
            <span>从</span>
            <input type="number" v-model.number="analysisConfig.startRow" min="1" max="10" placeholder="起始" />
            <span>行至</span>
            <input type="number" v-model.number="analysisConfig.endRow" min="1" max="10" placeholder="结束" />
            <span>行</span>
          </div>
        </div>
        <div class="form-group">
          <label>回弹值：</label>
          <div class="range-inputs">
            <input type="number" step="0.01" v-model.number="analysisConfig.reboundMin" placeholder="最小值" />
            <span>至</span>
            <input type="number" step="0.01" v-model.number="analysisConfig.reboundMax" placeholder="最大值" />
          </div>
        </div>
        <div class="form-group">
          <label>碳化深度(mm)：</label>
          <div class="range-inputs">
            <input type="number" step="0.01" v-model.number="analysisConfig.depthMin" placeholder="最小值" />
            <span>至</span>
            <input type="number" step="0.01" v-model.number="analysisConfig.depthMax" placeholder="最大值" />
          </div>
        </div>
        <div class="form-group">
          <label>推定强度值(MPa)：</label>
          <div class="range-inputs">
            <input type="number" step="0.01" v-model.number="analysisConfig.estMin" placeholder="最小值" />
            <span>至</span>
            <input type="number" step="0.01" v-model.number="analysisConfig.estMax" placeholder="最大值" />
            <span>MPa</span>
          </div>
        </div>
        <div class="form-group">
          <label>碳化修正强度值(MPa)：</label>
          <div class="range-inputs">
            <input type="number" step="0.01" v-model.number="analysisConfig.corrMin" placeholder="最小值" />
            <span>至</span>
            <input type="number" step="0.01" v-model.number="analysisConfig.corrMax" placeholder="最大值" />
            <span>MPa</span>
          </div>
        </div>
      </div>
      <div class="modal-actions">
        <button @click="showAnalysis=false" class="btn btn-secondary">取消</button>
        <button @click="performAnalysis" class="btn btn-primary">自动分析并填充</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, inject, defineProps, computed } from 'vue'
import axios from 'axios'

// 注入导航方法
const navigateTo = inject('navigateTo')

// 定义props
const props = defineProps({
  id: {
    type: String,
    default: null
  },
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
const totalRecords = computed(() => records.value.length)

const isEditable = computed(() => {
  return formData.status == 0 || formData.status == 2
})

const formData = reactive({
  id: '',
  entrustmentId: '',
  entrustingUnit: '',
  unifiedNumber: '',
  sampleNo: '',
  projectName: '',
  commissionDate: '',
  structurePart: '',
  testCategory: '',
  pourDate: '',
  sampleStatus: '',
  testAngle: '',
  testDate: '',
  designIndex: '',
  aggregateSize: '',
  avgStrength: '',
  stdDev: '',
  coefVariation: '',
  compEstimatedStrength: '',
  equipment: '',
  avgCarbonation: '',
  standard: '',
  calibrationBefore: '',
  calibrationAfter: '',
  conclusion: '',
  recordTester: '',
  recordReviewer: '',
  recordReviewSign: '',
  filler: '',
  remarks: '',
  reviewerSignature: '',
  testerSignature: '',
  status: 0
})

const getStatusText = (status) => {
    // 处理 null、undefined、空字符串、NaN 等情况
    if (status === null || status === undefined || status === '' || status === 'null' || status === 'undefined') {
        return '草稿'
    }
    const s = parseInt(status)
    if (isNaN(s)) {
        return '草稿'
    }
    switch(s) {
        case 0: return '草稿'
        case 1: return '待审核'
        case 2: return '已打回'
        case 4: return '已签字待提交'
        case 5: return '已通过'
        default: return '未知'
    }
}

const getStatusColor = (status) => {
    // 处理 null、undefined、空字符串、NaN 等情况
    if (status === null || status === undefined || status === '' || status === 'null' || status === 'undefined') {
        return '#9E9E9E' // Grey
    }
    const s = parseInt(status)
    if (isNaN(s)) {
        return '#9E9E9E' // Grey
    }
    switch(s) {
        case 0: return '#9E9E9E' // Grey
        case 1: return '#2196F3' // Blue
        case 2: return '#F44336' // Red
        case 4: return '#17a2b8' // Info
        case 5: return '#4CAF50' // Green
        default: return '#9E9E9E' // Grey
    }
}

// 日期格式化函数
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  if (isNaN(date.getTime())) return dateString
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const normalizeSignatureSrc = (value) => {
  if (!value) return ''
  if (typeof value !== 'string') return ''
  if (value.startsWith('data:image')) return value
  return `data:image/png;base64,${value}`
}

const submitWorkflow = async (action) => {
    console.log('submitWorkflow - formData.id:', formData.id, 'type:', typeof formData.id)
    console.log('submitWorkflow - formData:', formData)
    // 检查 ID：空字符串、null、undefined 都视为无效
    if (!formData.id || formData.id.trim() === '') {
        console.warn('提交审核失败：formData.id 为空', formData.id)
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
        // Priority: formData.recordTester
        if (formData.recordTester && user.username !== formData.recordTester && user.userName !== formData.recordTester) {
            alert('您不是该单据的记录检测人 (' + formData.recordTester + ')，无权提交')
            return
        }

        // Auto fetch signature if missing
        if (!formData.testerSignature) {
            try {
                const sigRes = await axios.post('/api/signature/get', { userAccount: user.username })
                if (sigRes.data.success && sigRes.data.data && sigRes.data.data.signatureBlob) {
                     formData.testerSignature = `data:image/png;base64,${sigRes.data.data.signatureBlob}`
                     // 保存签名到数据库
                     await submitForm()
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
    } else if (action === 'AUDIT_PASS') {
        // Role check: Only recordReviewer can audit/reject at status 1
        // Priority: formData.recordReviewer
        if (formData.recordReviewer && user.username !== formData.recordReviewer && user.fullName !== formData.recordReviewer) {
            alert('您不是该单据的记录审核人 (' + formData.recordReviewer + ')，无权操作')
            return
        }

        // Auto fetch signature if missing
        if (!formData.recordReviewSign) {
            try {
                const sigRes = await axios.post('/api/signature/get', { userAccount: user.username })
                if (sigRes.data.success && sigRes.data.data && sigRes.data.data.signatureBlob) {
                     const sigData = `data:image/png;base64,${sigRes.data.data.signatureBlob}`
                     formData.recordReviewSign = sigData
                     formData.reviewerSignature = sigData
                     // Save signature to database
                     await submitForm()
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
        signatureData = formData.recordReviewSign.replace(/^data:image\/\w+;base64,/, '')
    } else if (action === 'REJECT' && parseInt(formData.status) === 1) {
        // Role check: Only recordReviewer can audit/reject at status 1
        if (formData.recordReviewer && user.username !== formData.recordReviewer && user.fullName !== formData.recordReviewer) {
            alert('您不是该单据的记录审核人 (' + formData.recordReviewer + ')，无权操作')
            return
        }
    }
    // else if (action === 'SIGN_REVIEW') {
    //    // Role check: Only recordReviewer can sign
    //    if (formData.recordReviewer && user.username !== formData.recordReviewer) {
    //        alert('您不是该单据的记录审核人 (' + formData.recordReviewer + ')，无权签字')
    //        return
    //    }
    //
    //    if (!formData.recordReviewSign) {
    //        alert('请先进行记录审核人签字')
    //        return
    //    }
    //    signatureData = formData.recordReviewSign
    // }

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
            // Refresh data - 重新加载数据以获取最新状态
            // 保存当前记录的 ID，确保重新加载后能定位到正确的记录
            const currentRecordId = formData.id
            const entrustmentId = formData.entrustmentId || formData.unifiedNumber
            
            // 重新加载数据
            await loadData(entrustmentId)
            
            // 如果重新加载后找到了当前记录，切换到该记录
            if (currentRecordId) {
                const foundIndex = records.value.findIndex(r => r.id === currentRecordId)
                if (foundIndex !== -1) {
                    currentIndex.value = foundIndex
                    mapRecordToFormData(records.value[foundIndex])
                    console.log('After workflow reload, found record at index:', foundIndex, 'formData.status:', formData.status, 'formData.id:', formData.id)
                } else {
                    console.warn('After workflow reload, record not found with id:', currentRecordId, 'total records:', records.value.length)
                    // 如果找不到记录，可能是记录被删除了，或者查询返回了不同的记录
                    // 不要自动切换到其他记录，保持当前状态，避免误操作
                    if (records.value.length > 0) {
                        console.warn('Warning: Current record not found, but other records exist. Keeping current state to avoid accidental operations.')
                        // 不自动切换，让用户手动选择
                    } else {
                        // 如果没有任何记录，清空表单
                        formData.id = ''
                        currentIndex.value = -1
                    }
                }
            } else {
                console.log('After workflow reload, no currentRecordId, formData.status:', formData.status)
            }
        } else {
            alert('操作失败: ' + response.data.message)
        }
    } catch (e) {
        console.error('Workflow error', e)
        alert('操作异常')
    }
}

const initDynamicFields = () => {
  for (let i = 1; i <= 10; i++) {
    for (let j = 1; j <= 16; j++) {
      formData['reboundValue_' + i + '_' + j] = ''
    }
    formData['avgRebound_' + i] = ''
    formData['correctedStrength_' + i] = ''
    formData['carbonDepth_' + i] = ''
    formData['estimatedStrength_' + i] = ''
  }
}

onMounted(() => {
  initDynamicFields()
  
  // Get entrustment ID (wtNum) from props or URL
  let wtNum = props.wtNum
  if (!wtNum) {
    const urlParams = new URLSearchParams(window.location.search)
    wtNum = urlParams.get('wtNum') || urlParams.get('id') // Fallback to id if wtNum missing
  }

  if (wtNum) {
    formData.entrustmentId = wtNum
    formData.unifiedNumber = wtNum
    loadData(wtNum)
  } else if (props.id) {
    // If only id is provided (legacy or direct link), try using it as wtNum
    formData.entrustmentId = props.id
    formData.unifiedNumber = props.id
    loadData(props.id)
  } else {
    // 如果没有提供任何 ID，初始化空记录列表
    records.value = []
    currentIndex.value = -1
  }
})

const mapRecordToFormData = (record) => {
  if (!record) {
    console.warn('mapRecordToFormData: record is null or undefined')
    return
  }
  
  initDynamicFields()
  
  // 只有当 record.id 存在且不为空时，才更新 formData.id
  // 这样可以避免在保存后调用 mapRecordToFormData 时覆盖已设置的 ID
  if (record.id && typeof record.id === 'string' && record.id.trim() !== '') {
    const oldId = formData.id
    formData.id = record.id
    // 如果 ID 被覆盖，记录警告（但这是正常的，因为我们在切换记录）
    if (oldId && oldId !== record.id) {
      console.log('mapRecordToFormData: ID changed from', oldId, 'to', record.id)
    }
  } else {
    // 如果 record.id 不存在或为空字符串，清空 formData.id
    // 这样可以避免在切换记录时，formData.id 保留之前记录的值，导致误操作
    const oldId = formData.id
    formData.id = ''
    if (oldId) {
      console.warn('mapRecordToFormData: Record has no id, cleared formData.id from', oldId)
    }
  }
  
  formData.entrustmentId = record.entrustmentId || formData.unifiedNumber
  // 状态统一转成数字，避免后端返回字符串导致严格等于判断失效（影响按钮显示）
  // 处理 null、undefined、空字符串、'null'、'undefined' 等情况
  if (record.status === null || record.status === undefined || record.status === '' || record.status === 'null' || record.status === 'undefined') {
    formData.status = 0
  } else {
    const statusNum = Number(record.status)
    formData.status = isNaN(statusNum) ? 0 : statusNum
  }
  console.log('mapRecordToFormData - record.status:', record.status, 'formData.status:', formData.status)
  
  // Map signature photos
  formData.reviewerSignature = normalizeSignatureSrc(record.reviewSignaturePhoto || '')
  formData.testerSignature = normalizeSignatureSrc(record.inspectSignaturePhoto || '')
  formData.recordReviewSign = normalizeSignatureSrc(record.recordReviewSign || record.reviewSignaturePhoto || '')
  
  // Map explicit columns
  if (record.structurePart) formData.structurePart = record.structurePart
  // If structurePart is empty, try constructionPart (from BusinessEntity)
  if (!formData.structurePart && record.constructionPart) formData.structurePart = record.constructionPart
  
  if (record.testResult) formData.conclusion = record.testResult // Assuming testResult maps to conclusion
  if (record.remarks) formData.remarks = record.remarks
  if (record.entrustmentId) formData.unifiedNumber = record.entrustmentId
  if (record.wtNum && !formData.unifiedNumber) formData.unifiedNumber = record.wtNum
  
  // Map fields from BusinessEntity/Entrustment（先映射除样品编号以外的）
  if (record.clientUnit) formData.entrustingUnit = record.clientUnit
  if (record.projectName) formData.projectName = record.projectName
  if (record.commissionDate) formData.commissionDate = formatDate(record.commissionDate)
  if (record.testCategory) formData.testCategory = record.testCategory
  
  // Map Roles
  // Load defaults from directory if available
  const directory = JSON.parse(localStorage.getItem('currentDirectory') || '{}')

  // Filler - Priority: record.filler
  formData.filler = record.filler || ''
  // formData.approver = ''

  // Record Tester - Priority: record.recordTester -> record.tester (legacy)
  formData.recordTester = record.recordTester || record.tester || ''

  // Record Reviewer - Priority: record.recordReviewer -> record.reviewer (legacy)
  if (record.recordReviewer || record.reviewer) {
    formData.recordReviewer = record.recordReviewer || record.reviewer
  }
  
  if (record.dataJson) {
    try {
      const json = JSON.parse(record.dataJson)
      // 保存当前状态，避免被 JSON 覆盖
      const currentStatus = formData.status
      // Merge json into formData
      Object.assign(formData, json)
      // 确保实体字段的状态优先于 JSON 中的状态
      // 处理 null、undefined、空字符串、'null'、'undefined' 等情况
      if (record.status !== null && record.status !== undefined && record.status !== '' && record.status !== 'null' && record.status !== 'undefined') {
        const statusNum = Number(record.status)
        formData.status = isNaN(statusNum) ? currentStatus : statusNum
      } else {
        formData.status = currentStatus
      }
      // 样品编号兼容：优先使用 JSON 中的各种可能字段
      if (!formData.sampleNo) {
        if (json.sampleNo) {
          formData.sampleNo = json.sampleNo
        } else if (json.sampleNumber) {
          formData.sampleNo = json.sampleNumber
        } else if (json.sampleName) {
          formData.sampleNo = json.sampleName
        }
      }
      
      // Map legacy fields
      if (json.tester && !formData.recordTester) formData.recordTester = json.tester
      if (json.reviewer && !formData.recordReviewer) formData.recordReviewer = json.reviewer
      
      // Ensure entity fields override JSON if present
      if (record.reviewSignaturePhoto) formData.reviewerSignature = normalizeSignatureSrc(record.reviewSignaturePhoto)
      if (record.inspectSignaturePhoto) formData.testerSignature = normalizeSignatureSrc(record.inspectSignaturePhoto)
      if (record.recordReviewSign || record.reviewSignaturePhoto) {
        formData.recordReviewSign = normalizeSignatureSrc(record.recordReviewSign || record.reviewSignaturePhoto)
      }
      if (record.recordTester) formData.recordTester = record.recordTester
      if (record.recordReviewer) formData.recordReviewer = record.recordReviewer
      if (record.filler) formData.filler = record.filler
    } catch (e) {
      console.error('JSON parse error', e)
    }
  }

  // 样品编号兜底：如果上面都没有填到，就用委托单上的字段
  if (!formData.sampleNo) {
    if (record.sampleName) {
      formData.sampleNo = record.sampleName
    } else if (record.sampleNumber) {
      formData.sampleNo = record.sampleNumber
    }
  }
}

const saveCurrentRecordState = () => {
  if (records.value.length > 0 && currentIndex.value >= 0 && currentIndex.value < records.value.length) {
    const currentRecord = records.value[currentIndex.value]
    // Update dataJson
    const dataToSave = { ...formData }
    // Remove transient fields if any, or just save everything
    currentRecord.dataJson = JSON.stringify(dataToSave)
    
    // Update explicit fields
    currentRecord.structurePart = formData.structurePart
    currentRecord.testResult = formData.conclusion
    currentRecord.remarks = formData.remarks
    currentRecord.reviewSignaturePhoto = formData.reviewerSignature
    currentRecord.inspectSignaturePhoto = formData.testerSignature
    currentRecord.recordReviewSign = formData.recordReviewSign
    currentRecord.recordTester = formData.recordTester
    currentRecord.recordReviewer = formData.recordReviewer
    currentRecord.filler = formData.filler
  }
}

// 加载数据
const loadData = async (entrustmentId) => {
  try {
    const response = await axios.get(`/api/reboundMethod/get-by-entrustment-id?entrustmentId=${entrustmentId}`)
    if (response.data.success) {
      records.value = response.data.data || []
      
      if (records.value.length === 0) {
        // 如果后端返回空数组，不自动创建记录，让用户手动创建
        // 这样可以避免在刷新页面或重新加载时自动创建多条记录
        console.log('未找到记录，等待用户手动创建')
        // 清空表单，但不创建新记录
        formData.id = ''
        formData.entrustmentId = entrustmentId
        formData.unifiedNumber = entrustmentId
        currentIndex.value = -1
      } else {
        // Find record by ID if props.id is provided and it's not the entrustmentId
        let foundIndex = 0
        if (props.id && props.id !== entrustmentId) {
             const idx = records.value.findIndex(r => r.id === props.id)
             if (idx !== -1) foundIndex = idx
        }
        currentIndex.value = foundIndex
        // 确保 record 存在且有 id 才映射，避免误操作
        if (records.value[foundIndex] && records.value[foundIndex].id) {
          mapRecordToFormData(records.value[foundIndex])
        } else {
          console.warn('loadData: Record at index', foundIndex, 'has no id, skipping mapRecordToFormData')
        }
      }
    } else {
      console.error('Failed to load records:', response.data.message)
      // 加载失败时，不自动创建记录，避免重复创建
      records.value = []
      currentIndex.value = -1
    }
  } catch (error) {
    console.error('Failed to load data:', error)
    // 发生错误时，不自动创建记录，避免重复创建
    records.value = []
    currentIndex.value = -1
  }
}

const addRecord = async () => {
  saveCurrentRecordState()
  
  // 检查是否已经存在未保存的记录（id 为空或未定义）
  const hasUnsavedRecord = records.value.some(r => !r.id || r.id.trim() === '')
  if (hasUnsavedRecord) {
    // 如果已有未保存的记录，切换到第一条未保存的记录
    const unsavedIndex = records.value.findIndex(r => !r.id || r.id.trim() === '')
    if (unsavedIndex !== -1) {
      currentIndex.value = unsavedIndex
      mapRecordToFormData(records.value[unsavedIndex])
      alert('已有未保存的记录，请先保存或删除后再创建新记录')
      return
    }
  }
  
  // Fetch Entrustment Info if available
  let entrustmentData = {}
  const wtNum = formData.unifiedNumber || props.wtNum
  if (wtNum) {
      try {
          const res = await axios.get(`/api/jc-core-wt-info/by-wt-num?wtNum=${encodeURIComponent(wtNum)}`)
          if (res.data.success && res.data.data) {
              entrustmentData = res.data.data
          }
      } catch (e) {
          console.error('Failed to fetch entrustment info', e)
      }
  }

  const newRecord = {
    id: '', // New record has no ID yet
    entrustmentId: wtNum,
    structurePart: entrustmentData.constructionPart || '',
    testResult: '',
    remarks: '',
    // Map other fields from entrustmentData
    projectName: entrustmentData.projectName,
    testCategory: entrustmentData.testCategory,
    clientUnit: entrustmentData.clientUnit,
    sampleName: entrustmentData.sampleName,
    commissionDate: entrustmentData.commissionDate,
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
    if (!confirm('确定要删除这条已保存的记录吗？')) return
    
    try {
      const response = await axios.post('/api/reboundMethod/delete', { id: record.id })
      if (response.data.success) {
        records.value.splice(currentIndex.value, 1)
        if (currentIndex.value >= records.value.length) {
          currentIndex.value = records.value.length - 1
        }
        mapRecordToFormData(records.value[currentIndex.value])
      } else {
        alert('删除失败: ' + response.data.message)
      }
    } catch (e) {
      alert('删除失败')
    }
  } else {
    records.value.splice(currentIndex.value, 1)
    if (currentIndex.value >= records.value.length) {
      currentIndex.value = records.value.length - 1
    }
    mapRecordToFormData(records.value[currentIndex.value])
  }
}

const prevRecord = () => {
  if (currentIndex.value > 0) {
    saveCurrentRecordState()
    currentIndex.value--
    mapRecordToFormData(records.value[currentIndex.value])
  }
}

const nextRecord = () => {
  if (currentIndex.value < records.value.length - 1) {
    saveCurrentRecordState()
    currentIndex.value++
    mapRecordToFormData(records.value[currentIndex.value])
  }
}

const printDocument = () => {
  window.print()
}

const openClientPdfPreview = () => {
  if (!pdfForm.value) return
  const container = pdfForm.value.closest('.reboundMethodRecord-container')
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
      .pdf-preview.reboundMethodRecord-container { width: 100%; height: 100%; margin: 0; padding: 0; box-sizing: border-box; display: flex; flex-direction: column; }
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
// 返回列表
const goToList = () => {
  if (navigateTo) {
    navigateTo('ReboundMethodRecordList');
  }
}

const openBackendPdfPreview = (actionUrl) => {
  if (!pdfForm.value) return
  const container = pdfForm.value.closest('.reboundMethodRecord-container')
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
      .pdf-page { width: 198mm; height: 285mm; overflow: hidden; box-sizing: border-box; position: relative; }
      .pdf-content { position: absolute; left: 0; top: 0; transform-origin: top left; }
      .pdf-preview.reboundMethodRecord-container { width: 198mm; height: 285mm; max-width: 198mm; min-width: 0; margin: 0; padding: 0; box-sizing: border-box; display: flex; flex-direction: column; }
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
  openBackendPdfPreview('/api/pdf/rebound_method_record/generate')
}

const previewPdf = () => {
  openBackendPdfPreview('/api/pdf/rebound_method_record/preview')
}

const submitForm = async () => {
  try {
    // 保存后状态不变
    
    // 确保日期格式正确
    if (formData.commissionDate) {
      formData.commissionDate = formatDate(formData.commissionDate)
    }
    if (formData.testDate) {
      formData.testDate = formatDate(formData.testDate)
    }
    if (formData.pourDate) {
      formData.pourDate = formatDate(formData.pourDate)
    }
    
    saveCurrentRecordState()
    
    // 获取当前登录用户信息
    const userInfoStr = localStorage.getItem('userInfo')
    let userInfo = null
    if (userInfoStr) {
      userInfo = JSON.parse(userInfoStr)
    }

    // 构建提交数据 based on current formData
    const submitData = {
      // 只有当 id 存在且不为空字符串时，才传递 id（用于更新）
      // 如果 id 为空或空字符串，不传递 id，让后端自动生成新的 UUID
      ...(formData.id && formData.id.trim() !== '' ? { id: formData.id } : {}),
      entrustmentId: formData.entrustmentId || formData.unifiedNumber,
      status: formData.status, // 传递状态字段给后端
      // Map entity fields
      structurePart: formData.structurePart,
      testResult: formData.conclusion,
      remarks: formData.remarks,
      
      // JSON fields
      entrustingUnit: formData.entrustingUnit || '',
      projectName: formData.projectName || '',
      testCategory: formData.testCategory || '',
      sampleStatus: formData.sampleStatus || '',
      testAngle: formData.testAngle || '',
      designIndex: formData.designIndex || '',
      aggregateSize: formData.aggregateSize || '',
      avgStrength: formData.avgStrength || '',
      stdDev: formData.stdDev || '',
      coefVariation: formData.coefVariation || '',
      compEstimatedStrength: formData.compEstimatedStrength || '',
      equipment: formData.equipment || '',
      avgCarbonation: formData.avgCarbonation || '',
      standard: formData.standard || '',
      calibrationBefore: formData.calibrationBefore || '',
      calibrationAfter: formData.calibrationAfter || '',
      conclusion: formData.conclusion || '',
      recordTester: formData.recordTester,
      recordReviewer: formData.recordReviewer,
      filler: formData.filler,
      // Sync legacy fields
      tester: formData.recordTester,
      reviewer: formData.recordReviewer,
      reviewSignaturePhoto: formData.reviewerSignature,
      inspectSignaturePhoto: formData.testerSignature
    };

    // Include dataJson for persistence
    const dataJsonObj = { ...formData }
    // Remove legacy fields from dataJson to avoid confusion
    delete dataJsonObj.id
    delete dataJsonObj.status
    delete dataJsonObj.tester
    delete dataJsonObj.reviewer
    submitData.dataJson = JSON.stringify(dataJsonObj);

    // 发送请求
    const response = await axios.post('/api/reboundMethod/save', submitData);

    if (response.data.success) {
      alert('保存成功');
      // Update the current record with the saved data (especially ID)
      console.log('保存响应数据:', response.data)
      if (response.data.data) {
        const savedRecord = response.data.data
        console.log('保存后的记录数据:', savedRecord)
        console.log('保存后的记录 ID:', savedRecord.id, 'type:', typeof savedRecord.id)
        console.log('保存前的 formData.id:', formData.id)
        
        // 确保 ID 被正确设置（优先使用 savedRecord.id）
        if (savedRecord.id) {
          formData.id = savedRecord.id
          console.log('直接设置 formData.id =', formData.id)
        } else {
          console.warn('警告：savedRecord.id 不存在或为空！', savedRecord)
        }
        
        // 更新 records 数组，确保 ID 被正确设置
        if (records.value[currentIndex.value]) {
          // 合并数据，但确保 savedRecord.id 优先（如果存在）
          const mergedRecord = { ...records.value[currentIndex.value], ...savedRecord }
          // 如果 savedRecord.id 存在，确保它被设置
          if (savedRecord.id) {
            mergedRecord.id = savedRecord.id
          }
          records.value[currentIndex.value] = mergedRecord
        } else {
          records.value[currentIndex.value] = savedRecord
        }
        
        // 确保 records 中的 ID 被正确设置
        if (savedRecord.id && records.value[currentIndex.value]) {
          records.value[currentIndex.value].id = savedRecord.id
        }
        
        console.log('更新后的 records[currentIndex].id:', records.value[currentIndex.value]?.id)
        
        // 重新映射表单数据，确保所有字段（包括 ID）都被正确更新
        const idBeforeMap = formData.id
        mapRecordToFormData(records.value[currentIndex.value])
        console.log('保存成功，mapRecordToFormData 前的 formData.id:', idBeforeMap)
        console.log('保存成功，mapRecordToFormData 后的 formData.id:', formData.id)
        console.log('mapRecordToFormData 接收的 record.id:', records.value[currentIndex.value]?.id)
        
        // 如果 mapRecordToFormData 覆盖了 ID，重新设置
        if (savedRecord.id && formData.id !== savedRecord.id) {
          console.warn('警告：mapRecordToFormData 覆盖了 ID，重新设置')
          formData.id = savedRecord.id
        }
        
        console.log('最终 formData.id:', formData.id)
        
        // 保存成功后重新加载数据，确保显示最新状态
        const entrustmentId = formData.entrustmentId || formData.unifiedNumber
        if (entrustmentId) {
          await loadData(entrustmentId)
        }
      } else {
        // 如果后端没有返回数据，尝试重新查询
        console.warn('后端未返回保存的数据，尝试重新查询...')
        const entrustmentId = formData.entrustmentId || formData.unifiedNumber
        if (entrustmentId) {
          await loadData(entrustmentId)
        }
      }
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
      let signType = ''
      const currentAccount = user.username
      const currentName = user.userName

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
        // 如果两个人都签了，先更新状态为已签字待提交
        if (formData.testerSignature && formData.reviewerSignature) {
          formData.status = 4
        }
        // 保存签名到数据库
        await submitForm()
        // 显示成功消息
        if (parseInt(formData.status) === 4) {
          alert('签名成功并已保存，检测人和审核人都已签字，状态已更新为已签字待提交')
        } else {
          alert(`签名成功并已保存，您以${signType}身份签字`)
        }
      } else {
        alert(`当前用户(${currentName}/${currentAccount})与表单中的检测人(${formData.recordTester})或审核人(${formData.recordReviewer})不匹配，无法签名`)
      }
    } else {
      alert('未找到您的电子签名，请先去“电子签名”页面设置')
    }
  } catch (error) {
    console.error('Sign error:', error)
    alert('签名失败')
  }
}

const showAnalysis = ref(false)
const analysisConfig = reactive({
  startRow: 1,
  endRow: 10,
  reboundMin: 20,
  reboundMax: 50,
  depthMin: 0,
  depthMax: 10,
  estMin: 10,
  estMax: 50,
  corrMin: 10,
  corrMax: 50
})

const performAnalysis = () => {
  const sr = Math.max(1, Math.min(10, Number(analysisConfig.startRow || 1)))
  const er = Math.max(sr, Math.min(10, Number(analysisConfig.endRow || 10)))
  const rnd = (min, max) => {
    const a = Number(min || 0)
    const b = Number(max || 0)
    const lo = Math.min(a, b)
    const hi = Math.max(a, b)
    const val = lo + Math.random() * (hi - lo)
    return Number(val.toFixed(2))
  }
  const isBlank = (v) => {
    if (v === null || v === undefined) return true
    if (typeof v === 'string') return v.trim() === ''
    return false
  }
  const toNum = (v) => {
    const n = parseFloat(String(v).trim())
    return isNaN(n) ? null : n
  }
  const requiredRanges = [
    ['回弹值', analysisConfig.reboundMin, analysisConfig.reboundMax],
    ['碳化深度', analysisConfig.depthMin, analysisConfig.depthMax],
    ['推定强度值MPa', analysisConfig.estMin, analysisConfig.estMax],
    ['碳化修正强度值MPa', analysisConfig.corrMin, analysisConfig.corrMax]
  ]
  for (const [label, minVal, maxVal] of requiredRanges) {
    const a = Number(minVal)
    const b = Number(maxVal)
    if (minVal === '' || maxVal === '' || minVal === null || maxVal === null || minVal === undefined || maxVal === undefined || isNaN(a) || isNaN(b)) {
      alert(`${label}需要填写最小值和最大值`)
      return
    }
  }
  const strengths = []
  const depths = []
  for (let i = sr; i <= er; i++) {
    const vals = []
    for (let j = 1; j <= 16; j++) {
      const key = 'reboundValue_' + i + '_' + j
      const existing = toNum(formData[key])
      if (existing === null || isBlank(formData[key])) {
        const v = rnd(analysisConfig.reboundMin, analysisConfig.reboundMax)
        vals.push(v)
        formData[key] = String(v.toFixed(2))
      } else {
        vals.push(existing)
      }
    }
    const avgReb = vals.reduce((s, x) => s + x, 0) / vals.length
    const avgKey = 'avgRebound_' + i
    if (isBlank(formData[avgKey])) {
      formData[avgKey] = String(avgReb.toFixed(2))
    }
    const depthKey = 'carbonDepth_' + i
    const existingDepth = toNum(formData[depthKey])
    const depth = (existingDepth === null || isBlank(formData[depthKey])) ? rnd(analysisConfig.depthMin, analysisConfig.depthMax) : existingDepth
    depths.push(depth)
    if (existingDepth === null || isBlank(formData[depthKey])) {
      formData[depthKey] = String(depth.toFixed(2))
    }
    const estKey = 'estimatedStrength_' + i
    const existingEst = toNum(formData[estKey])
    const est = (existingEst === null || isBlank(formData[estKey])) ? rnd(analysisConfig.estMin, analysisConfig.estMax) : existingEst
    if (existingEst === null || isBlank(formData[estKey])) {
      formData[estKey] = String(Number(est).toFixed(2))
    }
    const corrKey = 'correctedStrength_' + i
    const existingCorr = toNum(formData[corrKey])
    const corr = (existingCorr === null || isBlank(formData[corrKey])) ? rnd(analysisConfig.corrMin, analysisConfig.corrMax) : existingCorr
    strengths.push(corr)
    if (existingCorr === null || isBlank(formData[corrKey])) {
      formData[corrKey] = String(Number(corr).toFixed(2))
    }
  }
  if (strengths.length > 0) {
    const avg = strengths.reduce((s, x) => s + x, 0) / strengths.length
    const std = Math.sqrt(strengths.reduce((s, x) => s + Math.pow(x - avg, 2), 0) / strengths.length)
    if (isBlank(formData.avgStrength)) formData.avgStrength = String(avg.toFixed(2))
    if (isBlank(formData.stdDev)) formData.stdDev = String(std.toFixed(2))
    if (isBlank(formData.coefVariation)) formData.coefVariation = avg !== 0 ? String(((std / avg) * 100).toFixed(2)) : ''
    if (isBlank(formData.compEstimatedStrength)) formData.compEstimatedStrength = String(avg.toFixed(2))
  }
  if (depths.length > 0) {
    const avgDepth = depths.reduce((s, x) => s + x, 0) / depths.length
    if (isBlank(formData.avgCarbonation)) formData.avgCarbonation = String(avgDepth.toFixed(2))
  }
  if (isBlank(formData.calibrationBefore) && !isBlank(formData['avgRebound_' + sr])) {
    formData.calibrationBefore = String(formData['avgRebound_' + sr])
  }
  if (isBlank(formData.calibrationAfter) && !isBlank(formData['avgRebound_' + er])) {
    formData.calibrationAfter = String(formData['avgRebound_' + er])
  }
  showAnalysis.value = false
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
            background: #f8f9fa;
            border: 1px solid #dee2e6;
            border-radius: 4px;
            color: #3498db;
            cursor: pointer;
            font-size: 16px;
            padding: 8px 16px;
            transition: all 0.2s ease;
            display: inline-flex;
            align-items: center;
            gap: 5px;
            white-space: nowrap;
        }

        .link-button:hover {
            background: #e9ecef;
            border-color: #adb5bd;
            text-decoration: none;
        }

        .record-nav {
            display: inline-flex;
            align-items: center;
            gap: 10px;
            margin-left: 16px;
        }

        .record-nav-info {
            font-size: 15px;
            color: #666;
            white-space: nowrap;
        }

        .status-text {
            font-size: 15px;
            font-weight: 500;
            color: #666;
            white-space: nowrap;
        }

        .status-label {
            margin-left: 6px;
        }

        .btn {
            padding: 8px 16px;
            border-radius: 4px;
            border: 1px solid transparent;
            font-size: 14px;
            cursor: pointer;
            background-color: #f5f7fa;
            color: #333;
            transition: all 0.2s;
            white-space: nowrap;
        }

        .btn-small {
            padding: 6px 12px;
            font-size: 13px;
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

        .reboundMethodRecord-container {
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
            white-space: normal;
        }
        .left-align {
            text-align: left;
            padding-left: 5px;
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
            padding: 2px 4px;
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
            padding: 2px 4px;
            resize: none;
            overflow: hidden;
        }
        .table-textarea.left-align {
            text-align: left;
        }
        .table-textarea.small-textarea {
            width: 60px;
            border-bottom: 1px solid #ccc;
            text-align: center;
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
            margin-top: 10px;
            font-size: 16px;
            font-weight: normal;
        }
        .page-footer {
            margin-top: 5px;
            display: flex;
            justify-content: space-between;
            font-size: 14px;
        }
        /* 模态窗口样式统一 */
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
            width: 90%;
            max-width: 600px;
            max-height: 80vh;
            overflow-y: auto;
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
        .close-btn {
            background: none;
            border: none;
            font-size: 24px;
            cursor: pointer;
            color: #999;
            position: absolute;
            top: 15px;
            right: 20px;
        }
        .close-btn:hover {
            color: #333;
        }
        .modal-body {
            margin-bottom: 20px;
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
            gap: 12px;
            margin-top: 25px;
            padding-top: 20px;
            border-top: 1px solid #e0e0e0;
        }
        .modal-actions .btn {
            padding: 8px 16px;
            border-radius: 4px;
            font-size: 14px;
            cursor: pointer;
            transition: all 0.2s;
        }
        .modal-actions .btn-primary {
            background-color: #3498db;
            color: white;
            border: none;
        }
        .modal-actions .btn-primary:hover {
            background-color: #2980b9;
        }
        .modal-actions .btn-secondary {
            background-color: #f5f7fa;
            color: #333;
            border: 1px solid #ddd;
        }
        .modal-actions .btn-secondary:hover {
            background-color: #e9ecef;
        }
        @media print {
            @page {
                size: A4 portrait;
                margin: 0;
            }
            .reboundMethodRecord-container {
                width: 100%;
                margin: 0;
                padding: 0;
            }
            .no-print {
                display: none;
            }
        }
    
</style>
