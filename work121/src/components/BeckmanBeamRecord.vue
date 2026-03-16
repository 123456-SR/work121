<template>
  <div class="beckmanBeamRecord-container">

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
            :disabled="!canEditStructure"
            class="btn btn-primary btn-small"
          >
            添加记录
          </button>
          <button
            @click="deleteRecord"
            :disabled="totalRecords <= 0 || !canEditStructure"
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
            v-if="parseInt(formData.status) === 0 || parseInt(formData.status) === 2"
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
            审核通过
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
          @click="submitForm"
          class="btn btn-secondary btn-small"
        >
          保存
        </button>
        <button
          type="button"
          @click="openAnalysisModal"
          class="btn btn-primary btn-small"
        >
          数据分析
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
        <div>统一编号：<input type="text" v-model="formData.unifiedNumber"   name="unifiedNumber" style="width: 150px; border-bottom: 1px solid black;" disabled></div>
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
            <td><textarea v-model="formData.projectName"   name="projectName" class="table-textarea"></textarea></td>
            <td class="label">委托日期</td>
            <td><textarea v-model="formData.commissionDate"   name="commissionDate" class="table-textarea date-input"></textarea></td>
        </tr>
        <tr>
            <td class="label">施工部位</td>
            <td><textarea v-model="formData.constructionPart"   name="constructionPart" class="table-textarea"></textarea></td>
            <td class="label">检测日期</td>
            <td><textarea v-model="formData.testDate"   name="testDate" class="table-textarea date-input"></textarea></td>
        </tr>
        <tr>
            <td class="label">仪器设备及编码</td>
            <td><textarea v-model="formData.equipmentCode"   name="equipmentCode" class="table-textarea"></textarea></td>
            <td class="label">检测类别</td>
            <td><textarea v-model="formData.testCategory"   name="testCategory" class="table-textarea"></textarea></td>
        </tr>
        <tr>
            <td class="label">样品名称及状态</td>
            <td><textarea :value="sampleNameStatusDisplay" @input="handleSampleNameStatusInput"   name="sampleStatus" class="table-textarea"></textarea></td>
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
            <td><textarea v-model="formData.standard"   name="standard" class="table-textarea"></textarea></td>
            <td class="label">设计弯沉值(0.01mm)</td>
            <td><textarea v-model="formData.designDeflection"   name="designDeflection" class="table-textarea"></textarea></td>
            <td class="label">温度修正系数K</td>
            <td><textarea v-model="formData.tempCorrectionK"   name="tempCorrectionK" class="table-textarea"></textarea></td>
        </tr>
        <tr>
            <td class="label">测试车车型</td>
            <td><textarea v-model="formData.vehicleModel"   name="vehicleModel" class="table-textarea"></textarea></td>
            <td class="label">沥青面层平均温度<br>t=(t<sub>25</sub>+t<sub>m</sub>+t<sub>e</sub>)/3</td>
            <td><textarea v-model="formData.avgAsphaltTemp"   name="avgAsphaltTemp" class="table-textarea"></textarea></td>
            <td class="label">轮胎传压面积(cm<sup>2</sup>)</td>
            <td><textarea v-model="formData.tireArea"   name="tireArea" class="table-textarea"></textarea></td>
        </tr>
        <tr>
            <td class="label">前5天平均气温的平均值(℃)</td>
            <td><textarea v-model="formData.avgTempPrev5Days"   name="avgTempPrev5Days" class="table-textarea"></textarea></td>
            <td class="label">路面结构类型</td>
            <td><textarea v-model="formData.pavementType"   name="pavementType" class="table-textarea"></textarea></td>
            <td class="label">路面厚度(mm)</td>
            <td><textarea v-model="formData.pavementThickness"   name="pavementThickness" class="table-textarea"></textarea></td>
        </tr>
        <tr>
            <td class="label">后轴重(kN)</td>
            <td><textarea v-model="formData.rearAxleWeight"   name="rearAxleWeight" class="table-textarea"></textarea></td>
            <td class="label">轮胎气压左(MPa)</td>
            <td><textarea v-model="formData.tirePressureLeft"   name="tirePressureLeft" class="table-textarea"></textarea></td>
            <td class="label">轮胎气压右(MPa)</td>
            <td><textarea v-model="formData.tirePressureRight"   name="tirePressureRight" class="table-textarea"></textarea></td>
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
                <td><textarea :name="'station_' + (i_idx + 1)" v-model="formData['station_' + (i_idx + 1)]" class="table-textarea"></textarea></td>
                <td><textarea :name="'lane_' + (i_idx + 1)" v-model="formData['lane_' + (i_idx + 1)]" class="table-textarea"></textarea></td>
                <td><textarea :name="'surfaceTemp_' + (i_idx + 1)" v-model="formData['surfaceTemp_' + (i_idx + 1)]" class="table-textarea"></textarea></td>
                <td><textarea :name="'leftInitial_' + (i_idx + 1)" v-model="formData['leftInitial_' + (i_idx + 1)]" class="table-textarea"></textarea></td>
                <td><textarea :name="'leftFinal_' + (i_idx + 1)" v-model="formData['leftFinal_' + (i_idx + 1)]" class="table-textarea"></textarea></td>
                <td><textarea :name="'leftDeflection_' + (i_idx + 1)" v-model="formData['leftDeflection_' + (i_idx + 1)]" class="table-textarea"></textarea></td>
                <td><textarea :name="'rightInitial_' + (i_idx + 1)" v-model="formData['rightInitial_' + (i_idx + 1)]" class="table-textarea"></textarea></td>
                <td><textarea :name="'rightFinal_' + (i_idx + 1)" v-model="formData['rightFinal_' + (i_idx + 1)]" class="table-textarea"></textarea></td>
                <td><textarea :name="'rightDeflection_' + (i_idx + 1)" v-model="formData['rightDeflection_' + (i_idx + 1)]" class="table-textarea"></textarea></td>
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
            <td><textarea v-model="formData.testInterval"   name="testInterval" class="table-textarea"></textarea></td>
            <td><textarea v-model="formData.laneCount"   name="laneCount" class="table-textarea"></textarea></td>
            <td><textarea v-model="formData.totalPoints"   name="totalPoints" class="table-textarea"></textarea></td>
            <td><textarea v-model="formData.testKm"   name="testKm" class="table-textarea"></textarea></td>
            <td><textarea v-model="formData.totalAvgDeflection"   name="totalAvgDeflection" class="table-textarea"></textarea></td>
            <td><textarea v-model="formData.stdDev"   name="stdDev" class="table-textarea"></textarea></td>
            <td><textarea v-model="formData.repDeflection"   name="repDeflection" class="table-textarea"></textarea></td>
            <td><textarea v-model="formData.tempCorrectedAvg"   name="tempCorrectedAvg" class="table-textarea"></textarea></td>
            <td><textarea v-model="formData.outlierCount"   name="outlierCount" class="table-textarea"></textarea></td>
            <td><textarea v-model="formData.isQualified"   name="isQualified" class="table-textarea"></textarea></td>
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
            <td><textarea v-model="formData.outlierLower"   name="outlierLower" class="table-textarea"></textarea></td>
            <td><textarea v-model="formData.outlierUpper"   name="outlierUpper" class="table-textarea"></textarea></td>
            <td><textarea v-model="formData.cleanAvg"   name="cleanAvg" class="table-textarea"></textarea></td>
            <td><textarea v-model="formData.cleanStdDev"   name="cleanStdDev" class="table-textarea"></textarea></td>
            <td><textarea v-model="formData.cleanRepDeflection"   name="cleanRepDeflection" class="table-textarea"></textarea></td>
            <td><textarea v-model="formData.cleanTempCorrectedAvg"   name="cleanTempCorrectedAvg" class="table-textarea"></textarea></td>
        </tr>
    </table>

    <!-- Footer -->
    <table>
        <tr>
            <td style="width: 10%; text-align: center;">备注</td>
            <td><textarea v-model="formData.remarks"   name="remarks" class="table-textarea left-align"></textarea></td>
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

    <div v-if="analysisModalVisible" class="modal-overlay no-print">
      <div class="modal-content">
        <h3>数据分析</h3>

        <div class="form-group">
          <label>数据范围：</label>
          <div class="range-inputs">
            <span>从</span>
            <input type="number" v-model.number="analysisRange.start" min="1" max="15" placeholder="起始" />
            <span>行至</span>
            <input type="number" v-model.number="analysisRange.end" min="1" max="15" placeholder="结束" />
            <span>行</span>
          </div>
        </div>

        <div class="analysis-grid">
          <div class="form-group">
            <label>路表温度(℃)（必填范围）：</label>
            <div class="range-inputs">
              <input type="number" v-model.number="analysisResults.surfaceTempMin" placeholder="最小值" step="0.1" />
              <span>至</span>
              <input type="number" v-model.number="analysisResults.surfaceTempMax" placeholder="最大值" step="0.1" />
            </div>
          </div>

          <div class="form-group">
            <label>左侧初读数(0.01mm)（必填范围）：</label>
            <div class="range-inputs">
              <input type="number" v-model.number="analysisResults.leftInitialMin" placeholder="最小值" step="1" />
              <span>至</span>
              <input type="number" v-model.number="analysisResults.leftInitialMax" placeholder="最大值" step="1" />
            </div>
          </div>
          <div class="form-group">
            <label>左侧终读数(0.01mm)（必填范围）：</label>
            <div class="range-inputs">
              <input type="number" v-model.number="analysisResults.leftFinalMin" placeholder="最小值" step="1" />
              <span>至</span>
              <input type="number" v-model.number="analysisResults.leftFinalMax" placeholder="最大值" step="1" />
            </div>
          </div>
          <div class="form-group">
            <label>左侧回弹弯沉(0.01mm)（必填范围）：</label>
            <div class="range-inputs">
              <input type="number" v-model.number="analysisResults.leftDeflectionMin" placeholder="最小值" step="1" />
              <span>至</span>
              <input type="number" v-model.number="analysisResults.leftDeflectionMax" placeholder="最大值" step="1" />
            </div>
          </div>

          <div class="form-group">
            <label>右侧初读数(0.01mm)（必填范围）：</label>
            <div class="range-inputs">
              <input type="number" v-model.number="analysisResults.rightInitialMin" placeholder="最小值" step="1" />
              <span>至</span>
              <input type="number" v-model.number="analysisResults.rightInitialMax" placeholder="最大值" step="1" />
            </div>
          </div>
          <div class="form-group">
            <label>右侧终读数(0.01mm)（必填范围）：</label>
            <div class="range-inputs">
              <input type="number" v-model.number="analysisResults.rightFinalMin" placeholder="最小值" step="1" />
              <span>至</span>
              <input type="number" v-model.number="analysisResults.rightFinalMax" placeholder="最大值" step="1" />
            </div>
          </div>
          <div class="form-group">
            <label>右侧回弹弯沉(0.01mm)（必填范围）：</label>
            <div class="range-inputs">
              <input type="number" v-model.number="analysisResults.rightDeflectionMin" placeholder="最小值" step="1" />
              <span>至</span>
              <input type="number" v-model.number="analysisResults.rightDeflectionMax" placeholder="最大值" step="1" />
            </div>
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
import { reactive, ref, onMounted, inject, computed } from 'vue'
import axios from 'axios'

const analysisModalVisible = ref(false)
const analysisRange = reactive({ start: '', end: '' })
const analysisResults = reactive({
  surfaceTempMin: '',
  surfaceTempMax: '',
  leftInitialMin: '',
  leftInitialMax: '',
  leftFinalMin: '',
  leftFinalMax: '',
  leftDeflectionMin: '',
  leftDeflectionMax: '',
  rightInitialMin: '',
  rightInitialMax: '',
  rightFinalMin: '',
  rightFinalMax: '',
  rightDeflectionMin: '',
  rightDeflectionMax: ''
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
  constructionUnit: '', // 施工单位
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

const canEditStructure = computed(() => {
  const s = Number(formData.status)
  if (Number.isNaN(s)) return true
  return s === 0 || s === 2
})

const getStatusText = (status) => {
    if (status === null || status === undefined || status === '') {
        return '草稿'
    }
    const s = parseInt(status)
    if (isNaN(s)) {
        return '草稿'
    }
    switch(s) {
        // 统一状态名称
        case 0: return '草稿'
        case 1: return '已提交待审核'
        case 2: return '已打回'
        case 3: return '待签字'
        case 4: return '审核通过'
        case 5: return '审核通过'
        // 报告表状态 (10-15)
        case 10: return '草稿'
        case 11: return '已提交待审核'
        case 12: return '已打回'
        case 13: return '待签字'
        case 14: return '审核通过待批准'
        case 15: return '审核通过待批准'
        // 结果表状态 (20-25)
        case 20: return '草稿'
        case 21: return '已提交待审核'
        case 22: return '已打回'
        case 23: return '待签字'
        case 24: return '审核通过待批准'
        case 25: return '审核通过待批准'
        default: return '未知'
    }
}

const getStatusColor = (status) => {
    if (status === null || status === undefined || status === '') {
        return '#6c757d' // secondary (草稿)
    }
    const s = parseInt(status)
    if (isNaN(s)) {
        return '#6c757d' // secondary (草稿)
    }
    switch(s) {
        // 记录表状态 (0-5)
        case 0: return '#6c757d' // secondary
        case 1: return '#007bff' // primary
        case 2: return '#dc3545' // danger
        case 3: return '#ffc107' // warning
        case 4: return '#28a745' // success
        case 5: return '#28a745' // success
        // 报告表状态 (10-15)
        case 10: return '#6c757d' // secondary
        case 11: return '#007bff' // primary
        case 12: return '#dc3545' // danger
        case 13: return '#ffc107' // warning
        case 14: return '#28a745' // success
        case 15: return '#28a745' // success
        // 结果表状态 (20-25)
        case 20: return '#6c757d' // secondary
        case 21: return '#007bff' // primary
        case 22: return '#dc3545' // danger
        case 23: return '#ffc107' // warning
        case 24: return '#28a745' // success
        case 25: return '#28a745' // success
        default: return '#6c757d'
    }
}

// 计算属性：拼接样品名称和状态
const sampleNameStatusDisplay = computed(() => {
    const name = formData.sampleName || ''
    const status = formData.sampleStatus || ''
    if (name && status) {
        return `${name}，${status}`
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
    const user = JSON.parse(localStorage.getItem('userInfo'))
    if (!user || !user.username) {
        alert('请先登录')
        return
    }

    let signatureData = null

    if (action === 'SUBMIT') {
        // Role check: Only recordTester can submit
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
        signatureData = formData.reviewerSignature.replace(/^data:image\/\w+;base64,/, '')
    } else if (action === 'REJECT') {
        // Role check: Only recordReviewer can reject
        if (formData.recordReviewer && user.username !== formData.recordReviewer && user.fullName !== formData.recordReviewer) {
            alert('您不是该单据的记录校核人 (' + formData.recordReviewer + ')，无权操作')
            return
        }
    }

    try {
        saveCurrentRecordState()

        if (action === 'SUBMIT' || action === 'AUDIT_PASS') {
            if (!records.value || records.value.length === 0) {
                alert('没有可操作的记录')
                return
            }

            const isTesterAction = action === 'SUBMIT'
            const signatureSrc = isTesterAction ? formData.testerSignature : formData.reviewerSignature

            for (let i = 0; i < records.value.length; i++) {
                const r = records.value[i] || {}
                let pageData = {}
                try {
                    pageData = r.dataJson ? JSON.parse(r.dataJson) : {}
                } catch (e) {
                    pageData = {}
                }

                const assigned = isTesterAction ? pageData.recordTester : pageData.recordReviewer
                if (assigned) {
                    const ok = isTesterAction
                        ? (user.username === assigned || user.userName === assigned)
                        : (user.username === assigned || user.fullName === assigned)
                    if (!ok) {
                        alert(`第${i + 1}页的${isTesterAction ? '记录检测人' : '记录校核人'}为 ${assigned}，您无权操作`)
                        return
                    }
                }

                if (isTesterAction) {
                    pageData.testerSignature = signatureSrc
                    pageData.inspectSignaturePhoto = signatureSrc
                    if (!pageData.recordTester) {
                        pageData.recordTester = formData.recordTester
                    }
                } else {
                    pageData.reviewerSignature = signatureSrc
                    pageData.reviewSignaturePhoto = signatureSrc
                    if (!pageData.recordReviewer) {
                        pageData.recordReviewer = formData.recordReviewer
                    }
                }

                r.inspectSignaturePhoto = pageData.testerSignature
                r.reviewSignaturePhoto = pageData.reviewerSignature
                r.dataJson = JSON.stringify(pageData)
            }

            await submitForm()

            const eligibleStatus = (status) => {
                const s = parseInt(status)
                if (Number.isNaN(s)) return false
                if (action === 'SUBMIT') return s === 0 || s === 2
                if (action === 'AUDIT_PASS') return s === 1
                return false
            }

            let successCount = 0
            let skippedCount = 0

            for (let i = 0; i < records.value.length; i++) {
                const r = records.value[i] || {}
                if (!r.id) {
                    alert(`第${i + 1}页未保存，无法提交流程`)
                    return
                }
                if (!eligibleStatus(r.status)) {
                    skippedCount++
                    continue
                }

                const request = {
                    tableType: 'BECKMAN_BEAM',
                    recordId: r.id,
                    action: action,
                    userAccount: user.username,
                    signatureData: signatureData,
                    nextHandler: ''
                }

                const response = await axios.post('/api/workflow/handle', request)
                if (!response.data.success) {
                    alert(`第${i + 1}页操作失败: ${response.data.message}`)
                    return
                }
                r.status = response.data.data
                if (i === currentIndex.value) {
                    formData.status = response.data.data
                }
                successCount++
            }

            alert(`操作成功：${successCount} 条${skippedCount ? `，跳过 ${skippedCount} 条` : ''}`)

            if (action === 'AUDIT_PASS') {
                try {
                    await generateReportAndResult()
                } catch (genError) {
                    console.error('生成报告表和结果表失败', genError)
                    alert('审核通过，但生成报告表和结果表失败，请手动生成')
                }
            }

            const reloadId = formData.entrustmentId || props.wtNum || formData.unifiedNumber
            if (reloadId) {
                await loadData(reloadId)
            }
            return
        }

        if (!formData.id) {
            alert('请先保存记录')
            return
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

        const response = await axios.post('/api/workflow/handle', request)
        if (response.data.success) {
            alert('操作成功')

            if (action === 'AUDIT_PASS') {
                try {
                    await generateReportAndResult()
                } catch (genError) {
                    console.error('生成报告表和结果表失败', genError)
                    alert('审核通过，但生成报告表和结果表失败，请手动生成')
                }
            }

            const reloadId = formData.entrustmentId || props.wtNum || formData.unifiedNumber
            if (reloadId) {
                await loadData(reloadId)
            } else if (formData.id) {
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

const generateReportAndResult = async () => {
  try {
    const response = await axios.post('/api/beckman-beam/generate-report-result', {
      recordId: formData.id,
      entrustmentId: formData.entrustmentId
    });
    if (!response.data.success) {
      throw new Error('生成报告表和结果表失败');
    }
  } catch (error) {
    console.error('Generate report and result error', error);
    throw error;
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
          formData.sampleNameStatus = `${json.sampleName}，${json.sampleStatus}`
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
  if (record.constructionUnit) formData.constructionUnit = record.constructionUnit
  if (record.testCategory) formData.testCategory = record.testCategory
  if (record.sampleName) formData.sampleName = record.sampleName
  // 拼接样品名称和状态（实体字段优先）
  if (formData.sampleName && formData.sampleStatus) {
    formData.sampleNameStatus = `${formData.sampleName}，${formData.sampleStatus}`
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
  if (!canEditStructure.value) {
    alert('提交后不能新增记录')
    return
  }
  saveCurrentRecordState()
  const current = records.value[currentIndex.value]
  const newRecord = {
    id: '',
    entrustmentId: formData.unifiedNumber,
    dataJson: '{}',
    status: 0,
    recordTester: formData.recordTester || current?.recordTester || current?.tester || '',
    recordReviewer: formData.recordReviewer || current?.recordReviewer || current?.reviewer || '',
    filler: formData.filler || current?.filler || '',
    tester: formData.recordTester || current?.recordTester || current?.tester || '',
    reviewer: formData.recordReviewer || current?.recordReviewer || current?.reviewer || ''
  }
  // Pre-fill some fields from current record
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
  if (!canEditStructure.value) {
    alert('提交后不能删除记录')
    return
  }
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
            formData.constructionPart = wtInfo.constructionPart || wtInfo.projectArea || ''
            formData.constructionUnit = wtInfo.constructionUnit || ''
            formData.testCategory = wtInfo.testCategory || ''
            formData.sampleName = wtInfo.sampleName || ''
            formData.sampleStatus = wtInfo.sampleStatus || ''
            // 拼接样品名称和状态
            if (formData.sampleName && formData.sampleStatus) {
            formData.sampleNameStatus = `${formData.sampleName}，${formData.sampleStatus}`
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
        // 1. 保存当前页数据到records数组
        saveCurrentRecordState()

        // 2. 保存所有页数据
        let successCount = 0
        let totalCount = records.value.length

        for (let i = 0; i < records.value.length; i++) {
            // 构建当前页的payload
            const currentRecord = records.value[i]
            const currentFormData = JSON.parse(currentRecord.dataJson)

            const payload = {
                id: currentRecord.id || null,
                entrustmentId: formData.entrustmentId,
                status: currentFormData.status, // 传递状态字段给后端
                dataJson: JSON.stringify(currentFormData),
                // Roles
                recordTester: currentFormData.recordTester,
                recordReviewer: currentFormData.recordReviewer,
                filler: currentFormData.filler,
                // Sync legacy fields for backward compatibility
                tester: currentFormData.recordTester,
                reviewer: currentFormData.recordReviewer,
                // Map signatures to columns
                inspectSignaturePhoto: currentFormData.testerSignature,
                reviewSignaturePhoto: currentFormData.reviewerSignature,
                // Map some fields to columns if needed by backend for searching/indexing
                subgradeType: currentFormData.pavementType,
                deflectometerType: currentFormData.equipmentCode
            }

            const response = await axios.post('/api/beckman-beam/save', payload)

            if (response.data.success) {
                successCount++
                if (response.data.data && response.data.data.id) {
                    // 更新记录ID
                    records.value[i].id = response.data.data.id
                    // 如果是当前页，更新formData.id
                    if (i === currentIndex.value) {
                        formData.id = response.data.data.id
                    }
                }
            }
        }

        // 3. 显示保存结果
        if (successCount === totalCount) {
            alert(`保存成功，共保存 ${successCount} 页数据`)
        } else {
            alert(`保存完成，成功 ${successCount} 页，失败 ${totalCount - successCount} 页`)
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
    saveCurrentRecordState()
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
        saveCurrentRecordState()
        const signatureKey = signType === '审核人' ? 'reviewerSignature' : 'testerSignature'
        const photoKey = signType === '审核人' ? 'reviewSignaturePhoto' : 'inspectSignaturePhoto'
        for (let i = 0; i < records.value.length; i++) {
          const rec = records.value[i]
          let pageData = {}
          try {
            pageData = rec && rec.dataJson ? JSON.parse(rec.dataJson) : {}
          } catch (e) {
            pageData = {}
          }

          pageData[signatureKey] = imgSrc
          if (rec) {
            rec[photoKey] = imgSrc
            rec.dataJson = JSON.stringify(pageData)
          }
        }
        // 保存签名到数据库
        await submitForm()
        // 显示成功消息
        alert(`签名成功并已保存，您以${signType}身份签字`)
      } else {
        alert(`当前用户(${currentName}/${currentAccount})与表单中的检测人(${formData.recordTester})或审核人(${formData.recordReviewer})不匹配，无法签字`)
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

const openBackendPdfPreview = (actionUrl) => {
  if (!pdfForm.value) return
  const container = pdfForm.value.closest('.beckmanBeamRecord-container')
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
      .pdf-preview.beckmanBeamRecord-container { width: 198mm; height: 285mm; max-width: 198mm; min-width: 0; margin: 0; padding: 0; box-sizing: border-box; display: flex; flex-direction: column; }
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

const openClientPdfPreview = () => {
  if (!pdfForm.value) return
  const container = pdfForm.value.closest('.beckmanBeamRecord-container')
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
  const a4Html = `<!doctype html>
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
      .pdf-preview.beckmanBeamRecord-container { width: 100%; height: 100%; margin: 0; padding: 0; box-sizing: border-box; display: flex; flex-direction: column; }
      .pdf-preview { overflow: visible; }
      .pdf-preview * { page-break-inside: avoid; break-inside: avoid; }
      .pdf-preview #pdfForm { flex: 1; min-height: 0; display: flex; flex-direction: column; }
      .pdf-preview #pdfForm > table { flex: 0 0 auto; height: auto; margin-top: auto; margin-bottom: auto; }
    </style>
  </head>
  <body><div class="pdf-sheet"><div class="pdf-page"><div class="pdf-content" style="width: ${contentWidthPx}px; height: ${contentHeightPx}px; transform: translate(${pdfOffsetXPx}px, ${pdfOffsetYPx}px) scale(${pdfScale});">${clone.outerHTML}</div></div></div></body>
</html>`
  const w2 = window.open('', '_blank')
  if (!w2) return
  w2.document.open()
  w2.document.write(a4Html)
  w2.document.close()
}

const generatePdf = () => {
  if (pdfForm.value) {
    openBackendPdfPreview('/api/pdf/beckman_beam_record/generate')
  }
}

const previewPdf = () => {
  if (pdfForm.value) {
    openBackendPdfPreview('/api/pdf/beckman_beam_record/preview')
  }
}

const openAnalysisModal = () => {
  analysisModalVisible.value = true
  analysisRange.start = ''
  analysisRange.end = ''
  Object.keys(analysisResults).forEach(k => analysisResults[k] = '')
}

const closeAnalysisModal = () => {
  analysisModalVisible.value = false
}

const autoAnalyzeAndFill = () => {
  const toNumber = (v) => {
    if (v === null || v === undefined || v === '') return null
    const n = typeof v === 'number' ? v : parseFloat(String(v))
    return Number.isFinite(n) ? n : null
  }

  const startIdx = (analysisRange.start === '' || analysisRange.start === null || analysisRange.start === undefined)
    ? 1
    : parseInt(String(analysisRange.start), 10)
  const endIdx = (analysisRange.end === '' || analysisRange.end === null || analysisRange.end === undefined)
    ? 15
    : parseInt(String(analysisRange.end), 10)
  const validStart = Math.max(1, startIdx)
  const validEnd = Math.min(15, endIdx)
  if (validStart > validEnd) {
    alert('起始行不能大于结束行')
    return
  }

  const randomBetween = (min, max, decimals = 0) => {
    if (min === null || max === null) return null
    const lo = Math.min(min, max)
    const hi = Math.max(min, max)
    const r = Math.random() * (hi - lo) + lo
    return +r.toFixed(decimals)
  }

  const formatStation = (meters) => {
    const m = Math.max(0, Math.round(meters))
    const km = Math.floor(m / 1000)
    const rest = m % 1000
    return `K${km}+${String(rest).padStart(3, '0')}`
  }

  const setIfEmpty = (key, value) => {
    if (formData[key] === '' || formData[key] === null || formData[key] === undefined) {
      formData[key] = value
    }
  }

  const computeMean = (values) => {
    if (!values.length) return null
    return values.reduce((sum, v) => sum + v, 0) / values.length
  }

  const computeStdDevSample = (values, mean) => {
    if (values.length < 2 || mean === null) return 0
    const variance = values.reduce((sum, v) => sum + (v - mean) * (v - mean), 0) / (values.length - 1)
    return Math.sqrt(variance)
  }

  const generateReadings = ({ initialMin, initialMax, finalMin, finalMax, deflectionMin, deflectionMax }) => {
    const tries = 30
    const iMin = toNumber(initialMin)
    const iMax = toNumber(initialMax)
    const fMin = toNumber(finalMin)
    const fMax = toNumber(finalMax)
    const dMin = toNumber(deflectionMin)
    const dMax = toNumber(deflectionMax)

    for (let t = 0; t < tries; t++) {
      const initial = randomBetween(iMin, iMax, 0)
      const deflection = randomBetween(dMin, dMax, 0)
      if (initial === null || deflection === null) break
      const final = initial - deflection
      if (fMin !== null && fMax !== null) {
        const lo = Math.min(fMin, fMax)
        const hi = Math.max(fMin, fMax)
        if (final < lo || final > hi) continue
      }
      return { initial, final, deflection }
    }

    const initial = randomBetween(iMin, iMax, 0)
    const final = randomBetween(fMin, fMax, 0)
    if (initial !== null && final !== null) {
      const deflection = Math.max(0, Math.round(initial - final))
      if (dMin !== null && dMax !== null) {
        const lo = Math.min(dMin, dMax)
        const hi = Math.max(dMin, dMax)
        if (deflection < lo || deflection > hi) {
          const def2 = randomBetween(dMin, dMax, 0)
          return { initial, final: initial - def2, deflection: def2 }
        }
      }
      return { initial, final, deflection }
    }

    return {
      initial: initial ?? '',
      final: final ?? '',
      deflection: randomBetween(dMin, dMax, 0) ?? ''
    }
  }

  setIfEmpty('designDeflection', String(Math.round(randomBetween(180, 280, 0))))
  if (toNumber(formData.tempCorrectionK) === null) {
    setIfEmpty('tempCorrectionK', randomBetween(0.95, 1.05, 3).toFixed(3))
  }
  setIfEmpty('vehicleModel', '标准测试车')
  setIfEmpty('tireArea', String(Math.round(randomBetween(650, 850, 0))))
  setIfEmpty('avgTempPrev5Days', randomBetween(15, 35, 1).toFixed(1))
  setIfEmpty('pavementType', '沥青路面')
  setIfEmpty('pavementThickness', String(Math.round(randomBetween(120, 240, 0))))
  setIfEmpty('rearAxleWeight', randomBetween(95, 105, 1).toFixed(1))
  setIfEmpty('tirePressureLeft', randomBetween(0.65, 0.75, 2).toFixed(2))
  setIfEmpty('tirePressureRight', randomBetween(0.65, 0.75, 2).toFixed(2))
  if (toNumber(formData.testInterval) === null) {
    setIfEmpty('testInterval', '10')
  }
  if (toNumber(formData.laneCount) === null) {
    setIfEmpty('laneCount', '1')
  }

  const interval = toNumber(formData.testInterval) ?? 10
  const laneCountNum = Math.max(1, Math.round(toNumber(formData.laneCount) ?? 1))

  const surfaceTempMin = toNumber(analysisResults.surfaceTempMin)
  const surfaceTempMax = toNumber(analysisResults.surfaceTempMax)

  const surfaceTemps = []

  for (let idx = validStart; idx <= validEnd; idx++) {
    const stationKey = `station_${idx}`
    const laneKey = `lane_${idx}`
    const surfaceTempKey = `surfaceTemp_${idx}`

    setIfEmpty(stationKey, formatStation((idx - 1) * interval))
    setIfEmpty(laneKey, String(((idx - 1) % laneCountNum) + 1))

    if (formData[surfaceTempKey] === '' || formData[surfaceTempKey] === null || formData[surfaceTempKey] === undefined) {
      const tVal = randomBetween(surfaceTempMin, surfaceTempMax, 1)
      if (tVal !== null) {
        formData[surfaceTempKey] = tVal.toFixed(1)
      }
    }
    const tempNum = toNumber(formData[surfaceTempKey])
    if (tempNum !== null) surfaceTemps.push(tempNum)

    const leftKeys = {
      initialKey: `leftInitial_${idx}`,
      finalKey: `leftFinal_${idx}`,
      deflectionKey: `leftDeflection_${idx}`
    }
    const rightKeys = {
      initialKey: `rightInitial_${idx}`,
      finalKey: `rightFinal_${idx}`,
      deflectionKey: `rightDeflection_${idx}`
    }

    const fillSide = (sideKeys, ranges) => {
      const existingInitial = toNumber(formData[sideKeys.initialKey])
      const existingFinal = toNumber(formData[sideKeys.finalKey])
      const existingDef = toNumber(formData[sideKeys.deflectionKey])

      if (existingInitial !== null && existingFinal !== null && existingDef === null) {
        const d = Math.max(0, Math.round(existingInitial - existingFinal))
        formData[sideKeys.deflectionKey] = String(d)
        return
      }

      if (existingDef !== null && existingInitial !== null && existingFinal === null) {
        formData[sideKeys.finalKey] = String(Math.round(existingInitial - existingDef))
        return
      }

      if (existingDef !== null && existingFinal !== null && existingInitial === null) {
        formData[sideKeys.initialKey] = String(Math.round(existingFinal + existingDef))
        return
      }

      if (existingInitial !== null && existingFinal !== null && existingDef !== null) return

      const generated = generateReadings(ranges)
      if (generated.initial !== '' && (formData[sideKeys.initialKey] === '' || formData[sideKeys.initialKey] === null || formData[sideKeys.initialKey] === undefined)) {
        formData[sideKeys.initialKey] = String(generated.initial)
      }
      if (generated.final !== '' && (formData[sideKeys.finalKey] === '' || formData[sideKeys.finalKey] === null || formData[sideKeys.finalKey] === undefined)) {
        formData[sideKeys.finalKey] = String(Math.round(generated.final))
      }
      if (generated.deflection !== '' && (formData[sideKeys.deflectionKey] === '' || formData[sideKeys.deflectionKey] === null || formData[sideKeys.deflectionKey] === undefined)) {
        formData[sideKeys.deflectionKey] = String(generated.deflection)
      }
    }

    fillSide(leftKeys, {
      initialMin: analysisResults.leftInitialMin,
      initialMax: analysisResults.leftInitialMax,
      finalMin: analysisResults.leftFinalMin,
      finalMax: analysisResults.leftFinalMax,
      deflectionMin: analysisResults.leftDeflectionMin,
      deflectionMax: analysisResults.leftDeflectionMax
    })

    fillSide(rightKeys, {
      initialMin: analysisResults.rightInitialMin,
      initialMax: analysisResults.rightInitialMax,
      finalMin: analysisResults.rightFinalMin,
      finalMax: analysisResults.rightFinalMax,
      deflectionMin: analysisResults.rightDeflectionMin,
      deflectionMax: analysisResults.rightDeflectionMax
    })
  }

  if (toNumber(formData.avgAsphaltTemp) === null && surfaceTemps.length > 0) {
    const avgTemp = computeMean(surfaceTemps)
    if (avgTemp !== null) formData.avgAsphaltTemp = avgTemp.toFixed(1)
  }

  const pointMeans = []
  for (let idx = validStart; idx <= validEnd; idx++) {
    const l = toNumber(formData[`leftDeflection_${idx}`])
    const r = toNumber(formData[`rightDeflection_${idx}`])
    if (l !== null && r !== null) {
      pointMeans.push((l + r) / 2)
    } else if (l !== null) {
      pointMeans.push(l)
    } else if (r !== null) {
      pointMeans.push(r)
    }
  }

  setIfEmpty('totalPoints', String(pointMeans.length))
  if (toNumber(formData.testKm) === null) {
    const km = (pointMeans.length * interval) / 1000
    setIfEmpty('testKm', km.toFixed(3))
  }

  const avg = computeMean(pointMeans)
  if (avg !== null) {
    formData.totalAvgDeflection = avg.toFixed(2)
  }

  const std = computeStdDevSample(pointMeans, avg)
  formData.stdDev = std.toFixed(2)

  const rep = (avg ?? 0) + 1.645 * std
  formData.repDeflection = rep.toFixed(2)

  const k = toNumber(formData.tempCorrectionK) ?? 1
  formData.tempCorrectedAvg = ((avg ?? 0) * k).toFixed(2)

  let lower = toNumber(formData.outlierLower)
  let upper = toNumber(formData.outlierUpper)
  if (lower === null || upper === null) {
    const baseAvg = avg ?? 0
    lower = baseAvg - 2 * std
    upper = baseAvg + 2 * std
    formData.outlierLower = lower.toFixed(2)
    formData.outlierUpper = upper.toFixed(2)
  }

  const outliers = pointMeans.filter(v => v < lower || v > upper)
  formData.outlierCount = String(outliers.length)

  const cleanValues = pointMeans.filter(v => v >= lower && v <= upper)
  const cleanAvg = computeMean(cleanValues) ?? 0
  const cleanStd = computeStdDevSample(cleanValues, cleanAvg)
  const cleanRep = cleanAvg + 1.645 * cleanStd

  formData.cleanAvg = cleanAvg.toFixed(2)
  formData.cleanStdDev = cleanStd.toFixed(2)
  formData.cleanRepDeflection = cleanRep.toFixed(2)
  formData.cleanTempCorrectedAvg = (cleanAvg * k).toFixed(2)

  const design = toNumber(formData.designDeflection)
  const repToUse = cleanValues.length ? cleanRep : rep
  if (design !== null) {
    formData.isQualified = repToUse <= design ? '合格' : '不合格'
  } else {
    formData.isQualified = ''
  }

  analysisModalVisible.value = false
  alert('自动分析并填充完成')
}
</script>

<style scoped>
    .beckmanBeamRecord-container {
        font-family: 'SimSun', 'Songti SC', serif;
        width: 210mm;
        max-width: 100%;
        min-width: 800px;
        margin: 0 auto;
        padding: 16px;
        background-color: var(--bg-card);
        border-radius: 8px;
        box-shadow: var(--shadow);
        box-sizing: border-box;
    }

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

        .beckmanBeamRecord-container {
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
        h1 {
            text-align: center;
            font-size: 24px;
            margin-bottom: 20px;
            color: #000;
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
            margin-bottom: -1px; /* Collapse borders between tables */
        }
        td, th {
            border: 1px solid black;
            padding: 8px 5px;
            text-align: center;
            font-size: inherit;
            vertical-align: middle;
            word-wrap: break-word;
        }
        input[type="text"], textarea, select {
            width: 95%;
            border: 1px solid #b3d9ff;
            border-radius: 4px;
            outline: none;
            text-align: center;
            font-family: inherit;
            font-size: inherit;
            background: transparent;
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
        .label {
            text-align: center;
        }
        .table-textarea {
            width: 100%;
            height: 100%;
            border: 1px solid #b3d9ff;
            border-radius: 4px;
            outline: none;
            text-align: center;
            font-family: inherit;
            font-size: inherit;
            background: transparent;
            padding: 2px 4px;
            resize: none;
            overflow: hidden;
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
                margin: 0;
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
            max-height: calc(100vh - 80px);
            overflow-y: auto;
            overflow-x: hidden;
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

        .analysis-grid {
            display: block;
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
            text-align: left;
        }

        .modal-actions {
            display: flex;
            justify-content: flex-end;
            gap: 10px;
            margin-top: 16px;
        }

</style>
