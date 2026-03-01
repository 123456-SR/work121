<template>
  <div class="beckmanBeamRecord-container">

    <div class="no-print toolbar">
      <div class="toolbar-left">
        <button @click="goToList" class="link-button">&lt; 返回列表</button>
        <span v-if="!draftMode" class="record-nav">
          <button
            @click="prevRecord"
            :disabled="currentIndex <= 0"
            class="btn btn-secondary btn-small"
          >
            上一页
          </button>
          <span class="record-nav-info">
            记录 {{ currentIndex + 1 }} / {{ totalRecords }}
          </span>
          <button
            @click="nextRecord"
            :disabled="currentIndex >= totalRecords - 1"
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
          @click="submitForm"
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
    </div>

    <form id="pdfForm" ref="pdfForm" method="post">
    <h1>路基路面回弹弯沉试验检测记录表（贝克曼梁法）</h1>

    <div class="header-info">
        <div>委托单位：<input type="text" v-model="formData.entrustingUnit"   name="entrustingUnit" style="width: 200px; border-bottom: 1px solid black; text-align: left;"></div>
        <div>统一编号：<input type="text" v-model="formData.unifiedNumber"   name="unifiedNumber" style="width: 150px; border-bottom: 1px solid black;"></div>
    </div>

    <!-- Top Info Table -->
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
            <td class="label">检测类别</td>
            <td><input type="text" v-model="formData.testCategory"   name="testCategory"></td>
        </tr>
        <tr>
            <td class="label">样品名称及状态</td>
            <td><input type="text" :value="sampleNameStatusDisplay" @input="handleSampleNameStatusInput"   name="sampleStatus"></td>
            <td class="label">检测方法</td>
            <td>贝克曼梁法</td>
        </tr>
    </table>

    <!-- Detailed Info Table (6 columns) -->
    <table>
        <colgroup>
            <col style="width: 16%;">
            <col style="width: 17%;">
            <col style="width: 16%;">
            <col style="width: 17%;">
            <col style="width: 16%;">
            <col style="width: 18%;">
        </colgroup>
        <tr>
            <td class="label">依据标准</td>
            <td><input type="text" v-model="formData.standard"   name="standard"></td>
            <td class="label">设计弯沉值(0.01mm)</td>
            <td><input type="text" v-model="formData.designDeflection"   name="designDeflection"></td>
            <td class="label">温度修正系数K</td>
            <td><input type="text" v-model="formData.tempCorrectionK"   name="tempCorrectionK"></td>
        </tr>
        <tr>
            <td class="label">测试车车型</td>
            <td><input type="text" v-model="formData.vehicleModel"   name="vehicleModel"></td>
            <td class="label">沥青面层平均温度<br>t=(t<sub>25</sub>+t<sub>m</sub>+t<sub>e</sub>)/3</td>
            <td><input type="text" v-model="formData.avgAsphaltTemp"   name="avgAsphaltTemp"></td>
            <td class="label">轮胎传压面积(cm<sup>2</sup>)</td>
            <td><input type="text" v-model="formData.tireArea"   name="tireArea"></td>
        </tr>
        <tr>
            <td class="label">前5天平均气温的平均值(℃)</td>
            <td><input type="text" v-model="formData.avgTempPrev5Days"   name="avgTempPrev5Days"></td>
            <td class="label">路面结构类型</td>
            <td><input type="text" v-model="formData.pavementType"   name="pavementType"></td>
            <td class="label">路面厚度(mm)</td>
            <td><input type="text" v-model="formData.pavementThickness"   name="pavementThickness"></td>
        </tr>
        <tr>
            <td class="label">后轴重(kN)</td>
            <td><input type="text" v-model="formData.rearAxleWeight"   name="rearAxleWeight"></td>
            <td class="label">轮胎气压左(MPa)</td>
            <td><input type="text" v-model="formData.tirePressureLeft"   name="tirePressureLeft"></td>
            <td class="label">轮胎气压右(MPa)</td>
            <td><input type="text" v-model="formData.tirePressureRight"   name="tirePressureRight"></td>
        </tr>
    </table>

    <!-- Main Data Table -->
    <table>
        <colgroup>
            <col style="width: 5%;"> <!-- 序号 -->
            <col style="width: 11%;"> <!-- 测点桩号 -->
            <col style="width: 7%;"> <!-- 车道 -->
            <col style="width: 10%;"> <!-- 路表温度 -->
            <col style="width: 11%;"> <!-- 左-初 -->
            <col style="width: 11%;"> <!-- 左-终 -->
            <col style="width: 11%;"> <!-- 左-回 -->
            <col style="width: 11%;"> <!-- 右-初 -->
            <col style="width: 11%;"> <!-- 右-终 -->
            <col style="width: 12%;"> <!-- 右-回 -->
        </colgroup>
        <thead>
            <tr>
                <th rowspan="2">序号</th>
                <th rowspan="2">测点桩号<br>(幅段)</th>
                <th rowspan="2">车道</th>
                <th rowspan="2">路表温度<br>(℃)</th>
                <th colspan="3">左侧(0.01mm)</th>
                <th colspan="3">右侧(0.01mm)</th>
            </tr>
            <tr>
                <th>初读数</th>
                <th>终读数</th>
                <th>回弹弯沉</th>
                <th>初读数</th>
                <th>终读数</th>
                <th>回弹弯沉</th>
            </tr>
        </thead>
        <tbody>
            <template v-for="(n, i_idx) in 15" :key="i_idx">
            <tr>
                <td>{{ (i_idx + 1) }}</td>
                <td><input type="text" :name="'station_' + (i_idx + 1)" v-model="formData['station_' + (i_idx + 1)]"></td>
                <td><input type="text" :name="'lane_' + (i_idx + 1)" v-model="formData['lane_' + (i_idx + 1)]"></td>
                <td><input type="text" :name="'surfaceTemp_' + (i_idx + 1)" v-model="formData['surfaceTemp_' + (i_idx + 1)]"></td>
                <td><input type="text" :name="'leftInitial_' + (i_idx + 1)" v-model="formData['leftInitial_' + (i_idx + 1)]"></td>
                <td><input type="text" :name="'leftFinal_' + (i_idx + 1)" v-model="formData['leftFinal_' + (i_idx + 1)]"></td>
                <td><input type="text" :name="'leftDeflection_' + (i_idx + 1)" v-model="formData['leftDeflection_' + (i_idx + 1)]"></td>
                <td><input type="text" :name="'rightInitial_' + (i_idx + 1)" v-model="formData['rightInitial_' + (i_idx + 1)]"></td>
                <td><input type="text" :name="'rightFinal_' + (i_idx + 1)" v-model="formData['rightFinal_' + (i_idx + 1)]"></td>
                <td><input type="text" :name="'rightDeflection_' + (i_idx + 1)" v-model="formData['rightDeflection_' + (i_idx + 1)]"></td>
            </tr>
            </template>
        </tbody>
    </table>

    <!-- Statistics Table 1 -->
    <table>
        <colgroup>
            <col style="width: 10%;">
            <col style="width: 5%;">
            <col style="width: 5%;">
            <col style="width: 10%;">
            <col style="width: 10%;">
            <col style="width: 10%;">
            <col style="width: 10%;">
            <col style="width: 15%;">
            <col style="width: 5%;">
            <col style="width: 10%;">
        </colgroup>
        <tr>
            <td class="label">测试间距 (m)</td>
            <td class="label">车道数</td>
            <td class="label">测试总点数</td>
            <td class="label">测试公里 (km)</td>
            <td class="label">全部平均弯沉值<br>(0.01mm)</td>
            <td class="label">标准差<br>(0.01mm)</td>
            <td class="label">弯沉代表值<br>(0.01mm)</td>
            <td class="label">温度修正后平均弯沉值<br>(0.01mm)</td>
            <td class="label">特异点个数</td>
            <td class="label">是否合格</td>
        </tr>
        <tr>
            <td><input type="text" v-model="formData.testInterval"   name="testInterval"></td>
            <td><input type="text" v-model="formData.laneCount"   name="laneCount"></td>
            <td><input type="text" v-model="formData.totalPoints"   name="totalPoints"></td>
            <td><input type="text" v-model="formData.testKm"   name="testKm"></td>
            <td><input type="text" v-model="formData.totalAvgDeflection"   name="totalAvgDeflection"></td>
            <td><input type="text" v-model="formData.stdDev"   name="stdDev"></td>
            <td><input type="text" v-model="formData.repDeflection"   name="repDeflection"></td>
            <td><input type="text" v-model="formData.tempCorrectedAvg"   name="tempCorrectedAvg"></td>
            <td><input type="text" v-model="formData.outlierCount"   name="outlierCount"></td>
            <td><input type="text" v-model="formData.isQualified"   name="isQualified"></td>
        </tr>
    </table>

    <!-- Statistics Table 2 -->
    <table>
        <colgroup>
            <col style="width: 15%;">
            <col style="width: 10%;">
            <col style="width: 15%;">
            <col style="width: 15%;">
            <col style="width: 15%;">
            <col style="width: 20%;">
        </colgroup>
        <tr>
            <td class="label">特异值下限</td>
            <td class="label">特异值上限</td>
            <td class="label">去特异点平均弯沉值(0.01mm)</td>
            <td class="label">去特异点标准差<br>(0.01mm)</td>
            <td class="label">去特异点弯沉代表值(0.01mm)</td>
            <td class="label">去特异点温度修正平均弯沉值(0.01mm)</td>
        </tr>
        <tr>
            <td><input type="text" v-model="formData.outlierLower"   name="outlierLower"></td>
            <td><input type="text" v-model="formData.outlierUpper"   name="outlierUpper"></td>
            <td><input type="text" v-model="formData.cleanAvg"   name="cleanAvg"></td>
            <td><input type="text" v-model="formData.cleanStdDev"   name="cleanStdDev"></td>
            <td><input type="text" v-model="formData.cleanRepDeflection"   name="cleanRepDeflection"></td>
            <td><input type="text" v-model="formData.cleanTempCorrectedAvg"   name="cleanTempCorrectedAvg"></td>
        </tr>
    </table>

    <!-- Footer -->
    <table>
        <tr>
            <td style="width: 10%; text-align: center;">备注</td>
            <td><input type="text" v-model="formData.remarks"   name="remarks" style="width: 100%; text-align: left;"></td>
        </tr>
    </table>

    <div style="margin-top: 10px; display: flex; justify-content: space-between;">
        <div class="footer-info">
            <span style="position: relative;">
                审核：<span style="display: inline-block; width: 100px; border-bottom: 1px solid black; height: 1em;"></span>
                <div v-if="formData.reviewerSignature" style="position: absolute; top: -20px; left: 40px; pointer-events: none;">
                    <img :src="formData.reviewerSignature" style="width: 80px; height: 40px; mix-blend-mode: multiply;" />
                </div>
            </span>
        </div>
        <div class="footer-info">
            <span style="position: relative;">
                检测：<span style="display: inline-block; width: 100px; border-bottom: 1px solid black; height: 1em;"></span>
                <div v-if="formData.testerSignature" style="position: absolute; top: -20px; left: 40px; pointer-events: none;">
                    <img :src="formData.testerSignature" style="width: 80px; height: 40px; mix-blend-mode: multiply;" />
                </div>
            </span>
        </div>
    </div>


    </form>

    

  </div>
</template>

<script setup>
import { reactive, ref, onMounted, inject, computed } from 'vue'
import axios from 'axios'

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

const draftMode = computed(() => props.draftMode)

// 注入导航方法
const navigateTo = inject('navigateTo')


const pdfForm = ref(null)

// 1:N State
const records = ref([])
const currentIndex = ref(0)
const totalRecords = computed(() => records.value.length)

const formData = reactive({
  id: '',
  entrustmentId: '',
  entrustingUnit: '',
  unifiedNumber: '',
  projectName: '',
  commissionDate: '',
  constructionPart: '',
  testDate: '',
  equipmentCode: '',
  testCategory: '',
  sampleName: '', // 样品名称（从委托单获取）
  sampleStatus: '', // 样品状态（从委托单获取）
  sampleNameStatus: '', // 拼接后的样品名称及状态
  standard: '',
  designDeflection: '',
  tempCorrectionK: '',
  vehicleModel: '',
  avgAsphaltTemp: '',
  tireArea: '',
  avgTempPrev5Days: '',
  pavementType: '',
  pavementThickness: '',
  rearAxleWeight: '',
  tirePressureLeft: '',
  tirePressureRight: '',
  testInterval: '',
  laneCount: '',
  totalPoints: '',
  testKm: '',
  totalAvgDeflection: '',
  stdDev: '',
  repDeflection: '',
  tempCorrectedAvg: '',
  outlierCount: '',
  isQualified: '',
  outlierLower: '',
  outlierUpper: '',
  cleanAvg: '',
  cleanStdDev: '',
  cleanRepDeflection: '',
  cleanTempCorrectedAvg: '',
  remarks: '',
  recordTester: '',
  recordReviewer: '',
  filler: '',
  reviewerSignature: '',
  testerSignature: '',
  status: 0
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

// 计算属性：拼接样品名称和状态
const sampleNameStatusDisplay = computed(() => {
    const name = formData.sampleName || ''
    const status = formData.sampleStatus || ''
    if (name && status) {
        return `${name} / ${status}`
    } else if (name) {
        return name
    } else if (status) {
        return status
    }
    return formData.sampleNameStatus || ''
})

// 处理输入：当用户编辑时，更新 sampleNameStatus（保存时使用）
const handleSampleNameStatusInput = (event) => {
    formData.sampleNameStatus = event.target.value
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
        if (formData.recordTester && user.username !== formData.recordTester && user.fullName !== formData.recordTester) {
            alert('您不是该单据的记录检测人 (' + formData.recordTester + ')，无权提交')
            return
        }

        if (!formData.testerSignature) {
            alert('请先进行检测人签字')
            return
        }
        signatureData = formData.testerSignature.replace(/^data:image\/\w+;base64,/, '')
    } else if (action === 'AUDIT_PASS') {
        // Role check: Only recordReviewer can audit
        if (formData.recordReviewer && user.username !== formData.recordReviewer && user.fullName !== formData.recordReviewer) {
            alert('您不是该单据的记录校核人 (' + formData.recordReviewer + ')，无权操作')
            return
        }

        // Auto fetch signature if missing
        if (!formData.reviewerSignature) {
            try {
                const sigRes = await axios.post('/api/signature/get', { userAccount: user.username })
                if (sigRes.data.success && sigRes.data.data && sigRes.data.data.signatureBlob) {
                     formData.reviewerSignature = `data:image/png;base64,${sigRes.data.data.signatureBlob}`
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
        signatureData = formData.reviewerSignature.replace(/^data:image\/\w+;base64,/, '')
    } else if (action === 'REJECT') {
        // Role check: Only recordReviewer can reject
        if (formData.recordReviewer && user.username !== formData.recordReviewer && user.fullName !== formData.recordReviewer) {
            alert('您不是该单据的记录校核人 (' + formData.recordReviewer + ')，无权操作')
            return
        }
    }

    const request = {
        tableType: 'BECKMAN_BEAM',
        recordId: formData.id,
        action: action,
        userAccount: user.username,
        signatureData: signatureData ? signatureData.replace(/^data:image\/\w+;base64,/, '') : null,
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
            // 重新按委托号加载一次，刷新最新的 STATUS（例如从 0 -> 1）
            const reloadId = formData.entrustmentId || props.wtNum || formData.unifiedNumber
            if (reloadId) {
                await loadData(reloadId)
            } else if (formData.id) {
                // 如果没有委托号，尝试通过记录ID重新加载
                try {
                    const reloadRes = await axios.get('/api/beckman-beam/get-by-entrustment-id', {
                        params: { entrustmentId: formData.entrustmentId || formData.unifiedNumber }
                    })
                    if (reloadRes.data.success && reloadRes.data.data && reloadRes.data.data.length > 0) {
                        const foundRecord = reloadRes.data.data.find(r => r.id === formData.id)
                        if (foundRecord) {
                            const foundIndex = reloadRes.data.data.indexOf(foundRecord)
                            records.value = reloadRes.data.data
                            currentIndex.value = foundIndex
                            mapRecordToFormData(foundRecord)
                        } else {
                            // 如果找不到，重新加载第一条记录
                            records.value = reloadRes.data.data
                            currentIndex.value = 0
                            mapRecordToFormData(reloadRes.data.data[0])
                        }
                    }
                } catch (reloadError) {
                    console.error('Failed to reload data after workflow', reloadError)
                }
            }
        } else {
            alert('操作失败: ' + response.data.message)
        }
    } catch (e) {
        console.error('Workflow error', e)
        alert('操作异常')
    }
}

// Initialize dynamic fields
const initDynamicFields = () => {
  for (let i_idx = 1; i_idx <= 15; i_idx++) {
    formData['station_' + i_idx] = ''
    formData['lane_' + i_idx] = ''
    formData['surfaceTemp_' + i_idx] = ''
    formData['leftInitial_' + i_idx] = ''
    formData['leftFinal_' + i_idx] = ''
    formData['leftDeflection_' + i_idx] = ''
    formData['rightInitial_' + i_idx] = ''
    formData['rightFinal_' + i_idx] = ''
    formData['rightDeflection_' + i_idx] = ''
  }
}

onMounted(() => {
  initDynamicFields()
  
  let wtNum = props.wtNum
  if (!wtNum) {
      const urlParams = new URLSearchParams(window.location.search)
      wtNum = urlParams.get('wtNum') || urlParams.get('id')
  }

  if (wtNum) {
      loadData(wtNum)
  } else if (props.id) {
      loadData(props.id)
  }
})

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  if (isNaN(d.getTime())) return ''
  const year = d.getFullYear()
  const month = ('0' + (d.getMonth() + 1)).slice(-2)
  const day = ('0' + d.getDate()).slice(-2)
  return `${year}-${month}-${day}`
}

const mapRecordToFormData = (record) => {
  initDynamicFields()
  
  formData.id = record.id || ''
  formData.entrustmentId = record.entrustmentId || formData.unifiedNumber
  // 状态统一转成数字，避免后端返回字符串导致严格等于判断失效（影响按钮显示）
  formData.status = record.status !== undefined ? Number(record.status) : 0
  
  // Map signature photos
  formData.reviewerSignature = normalizeSignatureSrc(record.reviewSignaturePhoto || '')
  formData.testerSignature = normalizeSignatureSrc(record.inspectSignaturePhoto || '')
  
  // Map explicit columns
  if (record.subgradeType) formData.pavementType = record.subgradeType
  if (record.deflectometerType) formData.equipmentCode = record.deflectometerType
  if (record.entrustmentId) formData.unifiedNumber = record.entrustmentId
  
  if (record.dataJson) {
    try {
      const json = JSON.parse(record.dataJson)
      // Merge json into formData
      Object.assign(formData, json)
      // 如果 JSON 中有 sampleNameStatus，优先使用；否则从 sampleName 和 sampleStatus 拼接
      if (!json.sampleNameStatus && (json.sampleName || json.sampleStatus)) {
        if (json.sampleName && json.sampleStatus) {
          formData.sampleNameStatus = `${json.sampleName} / ${json.sampleStatus}`
        } else if (json.sampleName) {
          formData.sampleNameStatus = json.sampleName
        } else if (json.sampleStatus) {
          formData.sampleNameStatus = json.sampleStatus
        }
      }
    } catch (e) {
      console.error('JSON parse error', e)
    }
  }

  // Map fields from BusinessEntity/Entrustment (Override JSON to ensure sync)
  if (record.clientUnit) formData.entrustingUnit = record.clientUnit
  if (record.wtNum && !formData.unifiedNumber) formData.unifiedNumber = record.wtNum
  if (record.projectName) formData.projectName = record.projectName
  if (record.commissionDate) formData.commissionDate = formatDate(record.commissionDate)
  if (record.constructionPart) formData.constructionPart = record.constructionPart
  if (record.testCategory) formData.testCategory = record.testCategory
  if (record.sampleName) formData.sampleName = record.sampleName
  // 拼接样品名称和状态（实体字段优先）
  if (formData.sampleName && formData.sampleStatus) {
    formData.sampleNameStatus = `${formData.sampleName} / ${formData.sampleStatus}`
  } else if (formData.sampleName && !formData.sampleNameStatus) {
    formData.sampleNameStatus = formData.sampleName
  } else if (formData.sampleStatus && !formData.sampleNameStatus) {
    formData.sampleNameStatus = formData.sampleStatus
  }

  // Map Roles
  // Load defaults from directory if available
  const directory = JSON.parse(localStorage.getItem('currentDirectory') || '{}')
  
  // Filler - Priority: record.filler
  // Note: We check formData.filler (from JSON) to avoid overwriting if entity column is empty
  formData.filler = record.filler || (formData.filler && formData.filler !== records.value[currentIndex.value === 0 && records.value.length > 1 ? 1 : 0]?.filler ? formData.filler : '') || ''
  
  // Record Tester - Priority: record.recordTester -> record.tester (legacy)
  formData.recordTester = record.recordTester || record.tester || ''
  
  // Record Reviewer - Priority: record.recordReviewer -> record.reviewer (legacy)
  formData.recordReviewer = record.recordReviewer || record.reviewer || ''
  
  // Ensure entity fields override JSON if present
  if (record.reviewSignaturePhoto) formData.reviewerSignature = normalizeSignatureSrc(record.reviewSignaturePhoto)
  if (record.inspectSignaturePhoto) formData.testerSignature = normalizeSignatureSrc(record.inspectSignaturePhoto)
}

const getCleanDataJson = () => {
  const dynamicData = {}
  
  // Fields that should NOT be in dataJson (because they have their own columns or are UI state)
  const excludeFields = [
    'id', 'entrustmentId', 'status', 'reviewerSignature', 'testerSignature',
    'recordTester', 'recordReviewer', 'filler', 
    'pavementType', 'equipmentCode' // These map to subgradeType/deflectometerType
  ]

  Object.keys(formData).forEach(key => {
    if (!excludeFields.includes(key)) {
      dynamicData[key] = formData[key]
    }
  })
  
  return JSON.stringify(dynamicData)
}

const saveCurrentRecordState = () => {
  if (records.value.length === 0) return
  
  const record = records.value[currentIndex.value]
  record.id = formData.id
  record.entrustmentId = formData.entrustmentId || formData.unifiedNumber
  
  // Update signatures
  record.reviewSignaturePhoto = formData.reviewerSignature
  record.inspectSignaturePhoto = formData.testerSignature
  
  // Update explicit columns
  record.subgradeType = formData.pavementType
  record.deflectometerType = formData.equipmentCode
  
  // Update dataJson
  record.dataJson = getCleanDataJson()
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

const addRecord = () => {
  saveCurrentRecordState()
  const newRecord = {
    id: '',
    entrustmentId: formData.unifiedNumber,
    dataJson: '{}',
    status: 0
  }
  // Pre-fill some fields from current record
  const current = records.value[currentIndex.value]
  if (current && current.dataJson) {
      try {
          const json = JSON.parse(current.dataJson)
          const newJson = {}
          const fieldsToCopy = [
            'projectName', 'commissionDate', 'constructionPart', 'testDate',
            'equipmentCode', 'testCategory', 'sampleStatus', 'standard',
            'designDeflection', 'tempCorrectionK', 'vehicleModel', 'avgAsphaltTemp',
            'tireArea', 'avgTempPrev5Days', 'pavementType', 'pavementThickness',
            'rearAxleWeight', 'tirePressureLeft', 'tirePressureRight',
            'testInterval', 'laneCount', 'recordTester', 'recordReviewer', 'filler'
          ]
          fieldsToCopy.forEach(key => {
            if (json[key] !== undefined) newJson[key] = json[key]
          })
          newRecord.dataJson = JSON.stringify(newJson)
      } catch(e) {
          console.error('Error copying fields', e)
      }
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
  
  if (!confirm('确定要删除当前记录吗？')) return

  const currentRecord = records.value[currentIndex.value]
  
  if (currentRecord.id) {
    try {
      const response = await axios.post('/api/beckman-beam/delete', { id: currentRecord.id })
      if (!response.data.success) {
        alert('删除失败: ' + response.data.message)
        return
      }
    } catch (e) {
      console.error('Delete error', e)
      alert('删除失败')
      return
    }
  }
  
  records.value.splice(currentIndex.value, 1)
  if (currentIndex.value >= records.value.length) {
    currentIndex.value = records.value.length - 1
  }
  mapRecordToFormData(records.value[currentIndex.value])
}

const loadData = async (identifier) => {
  if (!identifier) return
  
  try {
    // 1. First get the entrustment info
    // Try as wtNum (Unified Number) first
    let wtRes = await axios.get('/api/jc-core-wt-info/detail', {
        params: { unifiedNumber: identifier }
    })
    
    // If not found, try as ID
    if (!wtRes.data.success || !wtRes.data.data) {
        wtRes = await axios.get('/api/jc-core-wt-info/by-id', {
            params: { id: identifier }
        })
    }

    if (wtRes.data.success && wtRes.data.data) {
        const wtInfo = wtRes.data.data
        const wtNum = wtInfo.wtNum
        const isEntrustmentApproved = wtInfo.status === 5
        
        // Auto-fill basic info from Entrustment only if approved
        if (isEntrustmentApproved) {
            formData.entrustingUnit = wtInfo.clientUnit || ''
            formData.unifiedNumber = wtNum
            formData.entrustmentId = wtNum
            formData.projectName = wtInfo.projectName || ''
            formData.commissionDate = formatDate(wtInfo.commissionDate)
            formData.constructionPart = wtInfo.constructionPart || ''
            formData.testCategory = wtInfo.testCategory || ''
            formData.sampleName = wtInfo.sampleName || ''
            formData.sampleStatus = wtInfo.sampleStatus || ''
            // 拼接样品名称和状态
            if (formData.sampleName && formData.sampleStatus) {
                formData.sampleNameStatus = `${formData.sampleName} / ${formData.sampleStatus}`
            } else if (formData.sampleName) {
                formData.sampleNameStatus = formData.sampleName
            } else if (formData.sampleStatus) {
                formData.sampleNameStatus = formData.sampleStatus
            }
        } else {
            console.log('委托单状态未审核通过，不自动填充数据')
            // 仅设置统一编号和委托ID，不填充其他字段
            formData.unifiedNumber = wtNum
            formData.entrustmentId = wtNum
        }
        
        // Tester/Reviewer might be pre-filled from task assignment if available
        if (wtInfo.tester) formData.recordTester = wtInfo.tester
        if (wtInfo.reviewer) formData.recordReviewer = wtInfo.reviewer

        // 2. Now try to get existing BeckmanBeam record(s)
        try {
            const recordRes = await axios.get('/api/beckman-beam/get-by-entrustment-id', {
                params: { entrustmentId: wtNum }
            })
            
            if (recordRes.data.success && recordRes.data.data && recordRes.data.data.length > 0) {
                records.value = recordRes.data.data
                currentIndex.value = 0
                mapRecordToFormData(records.value[0])
            } else {
                // Create new record
                const newRecord = {
                    id: '',
                    entrustmentId: wtNum,
                    dataJson: '{}'
                }
                // Pre-fill user names if not set (Removed to prevent auto-fill)
                const userInfoStr = localStorage.getItem('userInfo')
                if (userInfoStr) {
                    // const userInfo = JSON.parse(userInfoStr)
                    // const directory = JSON.parse(localStorage.getItem('currentDirectory') || '{}')
                    if (!formData.recordTester) formData.recordTester = ''
                    if (!formData.recordReviewer) formData.recordReviewer = ''
                    if (!formData.filler) formData.filler = ''
                }
                
                records.value = [newRecord]
                currentIndex.value = 0
                // Update newRecord dataJson with pre-filled formData
                newRecord.dataJson = JSON.stringify(formData)
                // No need to map back since formData is already set with entrustment info
                // But we should ensure ID is cleared
                formData.id = ''
            }
        } catch (e) {
            console.warn('No existing BeckmanBeam record found, using default/entrustment data', e)
            // Fallback to new record
            const newRecord = {
                id: '',
                entrustmentId: wtNum,
                dataJson: JSON.stringify(formData)
            }
            records.value = [newRecord]
            currentIndex.value = 0
            formData.id = ''
        }
    } else {
        console.log('未找到委托单信息')
    }
  } catch (e) {
    console.error('Failed to load data', e)
    alert('加载数据失败')
  }
}

const submitForm = async () => {
    if (!formData.entrustmentId) {
        alert('缺少委托编号，无法保存')
        return
    }
    
    try {
        const payload = {
            id: formData.id || null, 
            entrustmentId: formData.entrustmentId,
            dataJson: getCleanDataJson(),
            // Roles
            recordTester: formData.recordTester,
            recordReviewer: formData.recordReviewer,
            filler: formData.filler,
            // Sync legacy fields for backward compatibility
            tester: formData.recordTester,
            reviewer: formData.recordReviewer,
            // Map signatures to columns
            inspectSignaturePhoto: formData.testerSignature,
            reviewSignaturePhoto: formData.reviewerSignature,
            // Map some fields to columns if needed by backend for searching/indexing
            subgradeType: formData.pavementType,
            deflectometerType: formData.equipmentCode
        }
        
        const response = await axios.post('/api/beckman-beam/save', payload)
        
        if (response.data.success) {
            alert('保存成功')
            if (response.data.data && response.data.data.id) {
                formData.id = response.data.data.id
                // Update current record in list
                if (records.value[currentIndex.value]) {
                    records.value[currentIndex.value].id = formData.id
                }
            }
        } else {
            alert('保存失败: ' + response.data.message)
        }
    } catch (e) {
        console.error('Save error', e)
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
      const currentAccount = user.username
      const currentName = user.fullName || user.nickName || currentAccount

      // Match Tester - Only Tester can use this button
      if (formData.recordTester === currentName || formData.recordTester === currentAccount || !formData.recordTester) {
        formData.testerSignature = imgSrc
        signed = true
      }
      
      // Reviewer signature is handled in AUDIT_PASS action
      
      if (signed) {
        alert('签名成功')
      } else {
        alert(`当前用户(${currentName})不是该单据的记录检测人，无法签字`)
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

// 返回列表
const goToList = () => {
  if (navigateTo) {
    navigateTo('BeckmanBeamRecordList');
  }
}

const generatePdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/beckman_beam_record/generate'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}

const previewPdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/beckman_beam_record/preview'
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

        .beckmanBeamRecord-container {
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
            .beckmanBeamRecord-container {
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
    
</style>
