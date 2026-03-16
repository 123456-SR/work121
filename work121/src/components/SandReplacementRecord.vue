<template>
  <div class="sandReplacementRecord-container">


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
          @click="submitForm"
          class="btn btn-secondary btn-small"
        >
          保存
        </button>
        <button
          type="button"
          @click="openAnalysisModal"
          class="btn btn-secondary btn-small"
        >
          数据分析
        </button>
      </div>
    </div>

    <form id="pdfForm" ref="pdfForm" method="post">
    <!-- 电子签名隐藏字段 -->
    <input type="hidden" v-model="formData.testerSignature" name="testerSignature">
    <input type="hidden" v-model="formData.reviewerSignature" name="reviewerSignature">
    <h2>原位密度检测记录表（灌砂法）</h2>

    <div class="header-info">
        <span>单元工程名称：<input type="text" v-model="formData.projectName"   name="projectName" style="width: 150px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>试验日期：<input type="text" v-model="formData.testDate"   name="testDate" style="width: 120px; border-bottom: 1px solid black;"></span>
        <span>统一编号：<input type="text" v-model="formData.unifiedNumber"   name="unifiedNumber" style="width: 120px; border-bottom: 1px solid black;" disabled></span>
    </div>
     <div class="header-info">
        <span>依据标准：<input type="text" v-model="formData.standard"   name="standard" style="width: 100px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>最大干密度 (g/cm³)：<input type="text" v-model="formData.maxDryDensity"   name="maxDryDensity" style="width: 80px; border-bottom: 1px solid black;"></span>
        <span>最优含水率 (%)：<input type="text" v-model="formData.optMoisture"   name="optMoisture" style="width: 80px; border-bottom: 1px solid black;"></span>
        <span>最小干密度 (g/cm³)：<input type="text" v-model="formData.minDryDensity"   name="minDryDensity" style="width: 80px; border-bottom: 1px solid black;"></span>
    </div>
    <div class="header-info">
        <span>量砂密度：<input type="text" v-model="formData.sandDensity"   name="sandDensity" style="width: 80px; border-bottom: 1px solid black;"> g/cm³</span>
        <span>仪器设备：<input type="text" v-model="formData.equipment"   name="equipment" style="width: 120px; border-bottom: 1px solid black;"></span>
        <span>检测类别：<input type="text" v-model="formData.testCategory"   name="testCategory" style="width: 80px; border-bottom: 1px solid black;"></span>
        <span>设计压实度：<input type="text" v-model="formData.designCompaction"   name="designCompaction" style="width: 80px; border-bottom: 1px solid black;"></span>
    </div>


    <table>
        <!-- Table Header -->
        <tr>
            <td colspan="2" class="label" style="width: 20%;">检测部位<br>(桩号、高程)</td>
            <template v-for="(n, i_idx) in 4" :key="i_idx">
            <td colspan="4"><input type="text" :name="'location_' + i_idx" v-model="formData['location_' + i_idx]"></td>
            </template>
        </tr>

        <!-- Data Rows -->
        <tr>
            <td class="label">量砂容器+原有砂质量</td>
            <td class="label">g</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'totalSandMass_' + i_idx" v-model="formData['totalSandMass_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td class="label">量砂容器+剩余砂质量</td>
            <td class="label">g</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'remainingSandMass_' + i_idx" v-model="formData['remainingSandMass_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td class="label">锥体内砂质量</td>
            <td class="label">g</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'coneSandMass_' + i_idx" v-model="formData['coneSandMass_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td class="label">试坑耗砂质量</td>
            <td class="label">g</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'pitSandMass_' + i_idx" v-model="formData['pitSandMass_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td class="label">试坑体积</td>
            <td class="label">cm³</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'pitVolume_' + i_idx" v-model="formData['pitVolume_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td class="label">试样质量</td>
            <td class="label">g</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'sampleMass_' + i_idx" v-model="formData['sampleMass_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td class="label">试样湿密度</td>
            <td class="label">g/cm³</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'wetDensity_' + i_idx" v-model="formData['wetDensity_' + i_idx]"></td>
            </template>
        </tr>

        <!-- Moisture Content Section -->
        <tr>
            <td rowspan="6" class="label">试样含水率</td>
            <td class="label">盒号</td>
            <template v-for="(n, i_idx) in 16" :key="i_idx">
            <td><input type="text" :name="'boxNo_' + i_idx" v-model="formData['boxNo_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td class="label">盒质量 g</td>
            <template v-for="(n, i_idx) in 16" :key="i_idx">
            <td><input type="text" :name="'boxMass_' + i_idx" v-model="formData['boxMass_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td class="label">盒+湿样质量 g</td>
            <template v-for="(n, i_idx) in 16" :key="i_idx">
            <td><input type="text" :name="'boxWetMass_' + i_idx" v-model="formData['boxWetMass_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td class="label">盒+干样质量 g</td>
            <template v-for="(n, i_idx) in 16" :key="i_idx">
            <td><input type="text" :name="'boxDryMass_' + i_idx" v-model="formData['boxDryMass_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td class="label">含水率 %</td>
            <template v-for="(n, i_idx) in 16" :key="i_idx">
            <td><input type="text" :name="'moistureContent_' + i_idx" v-model="formData['moistureContent_' + i_idx]"></td>
            </template>
        </tr>
         <tr>
            <td class="label">平均含水率 %</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'avgMoisture_' + i_idx" v-model="formData['avgMoisture_' + i_idx]"></td>
            </template>
        </tr>


        <!-- Results -->
        <tr>
            <td colspan="2" class="label">实测干密度 g/cm³</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'dryDensity_' + i_idx" v-model="formData['dryDensity_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td colspan="2" class="label">平均实测干密度 g/cm³</td>
            <template v-for="(n, i_idx) in 4" :key="i_idx">
            <td colspan="4"><input type="text" :name="'avgDryDensity_' + i_idx" v-model="formData['avgDryDensity_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td colspan="2" class="label">实测压实度</td>
            <template v-for="(n, i_idx) in 4" :key="i_idx">
            <td colspan="4"><input type="text" :name="'compaction_' + i_idx" v-model="formData['compaction_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td colspan="2" class="label">备注</td>
            <td colspan="16" class="left-align"><input type="text" v-model="formData.remarks"   name="remarks"></td>
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
            试验：<span style="display: inline-block; width: 100px; border-bottom: 1px solid black; height: 1em;"></span>
            <div v-if="formData.testerSignature" style="position: absolute; top: -20px; left: 40px; pointer-events: none;">
                <img :src="formData.testerSignature" style="width: 80px; height: 40px; mix-blend-mode: multiply;" />
            </div>
        </span>
    </div>

    </form>

    <div v-if="showExportModal" class="modal-overlay no-print">
      <div class="modal-content">
        <h3>导出数据</h3>
        <div class="form-group">
          <div>表名称：{{ exportTemplateInfo.baseName }}</div>
          <div v-if="exportTemplateInfo.templates.length" class="template-files">
            <span v-for="t in exportTemplateInfo.templates" :key="t.fileName" class="template-file">{{ t.fileName }}</span>
          </div>
          <div v-else>已有模板：无</div>
        </div>
        <div class="format-buttons">
          <button
            v-for="fmt in exportTemplateInfo.formats"
            :key="fmt"
            @click="startExport(fmt)"
            class="btn btn-primary btn-small"
          >
            {{ String(fmt).toUpperCase() }}<span v-if="!hasTemplate(fmt)">（无模板）</span>
          </button>
        </div>
        <div class="modal-actions">
          <button @click="closeExportModal" class="btn btn-secondary btn-small">关闭</button>
        </div>
      </div>
    </div>

    <!-- 数据分析模态窗口 -->
    <div v-if="showAnalysisModal" class="modal-overlay">
      <div class="modal-content">
        <h3>数据分析</h3>
        <div class="form-group">
          <label>数据范围：</label>
          <div class="range-inputs">
            <span>从</span>
            <input type="number" v-model="analysisRange.start" min="1" max="4" placeholder="起始列" />
            <span>列至</span>
            <input type="number" v-model="analysisRange.end" min="1" max="4" placeholder="结束列" />
            <span>列</span>
          </div>
        </div>
        <div class="analysis-results">
          <h4>分析结果：</h4>
          <div class="result-item">
            <span>量砂容器+原有砂质量：</span>
            <div class="range-inputs">
              <input type="number" v-model="analysisResults.totalSandMassMin" placeholder="最小值" step="0.1" style="appearance: textfield; -moz-appearance: textfield;" />
              <span>至</span>
              <input type="number" v-model="analysisResults.totalSandMassMax" placeholder="最大值" step="0.1" style="appearance: textfield; -moz-appearance: textfield;" />
              <span>g</span>
            </div>
          </div>
          <div class="result-item">
            <span>量砂容器+剩余砂质量：</span>
            <div class="range-inputs">
              <input type="number" v-model="analysisResults.remainingSandMassMin" placeholder="最小值" step="0.1" style="appearance: textfield; -moz-appearance: textfield;" />
              <span>至</span>
              <input type="number" v-model="analysisResults.remainingSandMassMax" placeholder="最大值" step="0.1" style="appearance: textfield; -moz-appearance: textfield;" />
              <span>g</span>
            </div>
          </div>
          <div class="result-item">
            <span>锥体内砂质量：</span>
            <div class="range-inputs">
              <input type="number" v-model="analysisResults.coneSandMassMin" placeholder="最小值" step="0.1" style="appearance: textfield; -moz-appearance: textfield;" />
              <span>至</span>
              <input type="number" v-model="analysisResults.coneSandMassMax" placeholder="最大值" step="0.1" style="appearance: textfield; -moz-appearance: textfield;" />
              <span>g</span>
            </div>
          </div>
          <div class="result-item">
            <span>试坑耗砂质量：</span>
            <div class="range-inputs">
              <input type="number" v-model="analysisResults.pitSandMassMin" placeholder="最小值" step="0.1" style="appearance: textfield; -moz-appearance: textfield;" />
              <span>至</span>
              <input type="number" v-model="analysisResults.pitSandMassMax" placeholder="最大值" step="0.1" style="appearance: textfield; -moz-appearance: textfield;" />
              <span>g</span>
            </div>
          </div>
          <div class="result-item">
            <span>试坑体积：</span>
            <div class="range-inputs">
              <input type="number" v-model="analysisResults.pitVolumeMin" placeholder="最小值" step="0.1" style="appearance: textfield; -moz-appearance: textfield;" />
              <span>至</span>
              <input type="number" v-model="analysisResults.pitVolumeMax" placeholder="最大值" step="0.1" style="appearance: textfield; -moz-appearance: textfield;" />
              <span>cm³</span>
            </div>
          </div>
          <div class="result-item">
            <span>试样质量：</span>
            <div class="range-inputs">
              <input type="number" v-model="analysisResults.sampleMassMin" placeholder="最小值" step="0.1" style="appearance: textfield; -moz-appearance: textfield;" />
              <span>至</span>
              <input type="number" v-model="analysisResults.sampleMassMax" placeholder="最大值" step="0.1" style="appearance: textfield; -moz-appearance: textfield;" />
              <span>g</span>
            </div>
          </div>
          <div class="result-item">
            <span>试样湿密度：</span>
            <div class="range-inputs">
              <input type="number" v-model="analysisResults.wetDensityMin" placeholder="最小值" step="0.01" style="appearance: textfield; -moz-appearance: textfield;" />
              <span>至</span>
              <input type="number" v-model="analysisResults.wetDensityMax" placeholder="最大值" step="0.01" style="appearance: textfield; -moz-appearance: textfield;" />
              <span>g/cm³</span>
            </div>
          </div>
          <div class="result-item">
            <span>盒质量：</span>
            <div class="range-inputs">
              <input type="number" v-model="analysisResults.boxMassMin" placeholder="最小值" step="0.1" style="appearance: textfield; -moz-appearance: textfield;" />
              <span>至</span>
              <input type="number" v-model="analysisResults.boxMassMax" placeholder="最大值" step="0.1" style="appearance: textfield; -moz-appearance: textfield;" />
              <span>g</span>
            </div>
          </div>
          <div class="result-item">
            <span>盒+湿样质量：</span>
            <div class="range-inputs">
              <input type="number" v-model="analysisResults.boxWetMassMin" placeholder="最小值" step="0.1" style="appearance: textfield; -moz-appearance: textfield;" />
              <span>至</span>
              <input type="number" v-model="analysisResults.boxWetMassMax" placeholder="最大值" step="0.1" style="appearance: textfield; -moz-appearance: textfield;" />
              <span>g</span>
            </div>
          </div>
          <div class="result-item">
            <span>盒+干样质量：</span>
            <div class="range-inputs">
              <input type="number" v-model="analysisResults.boxDryMassMin" placeholder="最小值" step="0.1" style="appearance: textfield; -moz-appearance: textfield;" />
              <span>至</span>
              <input type="number" v-model="analysisResults.boxDryMassMax" placeholder="最大值" step="0.1" style="appearance: textfield; -moz-appearance: textfield;" />
              <span>g</span>
            </div>
          </div>
          <div class="result-item">
            <span>含水率：</span>
            <div class="range-inputs">
              <input type="number" v-model="analysisResults.moistureContentMin" placeholder="最小值" step="0.1" style="appearance: textfield; -moz-appearance: textfield;" />
              <span>至</span>
              <input type="number" v-model="analysisResults.moistureContentMax" placeholder="最大值" step="0.1" style="appearance: textfield; -moz-appearance: textfield;" />
              <span>%</span>
            </div>
          </div>
          <div class="result-item">
            <span>实测干密度：</span>
            <div class="range-inputs">
              <input type="number" v-model="analysisResults.dryDensityMin" placeholder="最小值" step="0.01" style="appearance: textfield; -moz-appearance: textfield;" />
              <span>至</span>
              <input type="number" v-model="analysisResults.dryDensityMax" placeholder="最大值" step="0.01" style="appearance: textfield; -moz-appearance: textfield;" />
              <span>g/cm³</span>
            </div>
          </div>
          <div class="result-item">
            <span>实测压实度：</span>
            <div class="range-inputs">
              <input type="number" v-model="analysisResults.compactionMin" placeholder="最小值" step="0.1" style="appearance: textfield; -moz-appearance: textfield;" />
              <span>至</span>
              <input type="number" v-model="analysisResults.compactionMax" placeholder="最大值" step="0.1" style="appearance: textfield; -moz-appearance: textfield;" />
              <span>%</span>
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

// 注入导航方法
const navigateTo = inject('navigateTo')

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

const draftMode = computed(() => props.draftMode)

const pdfForm = ref(null)

// Multi-record support
const records = ref([])
const currentIndex = ref(0)
const totalRecords = computed(() => records.value.length)

const formData = reactive({
  id: '',
  entrustmentId: '',
  projectName: '',
  testDate: '',
  unifiedNumber: '',
  standard: '',
  maxDryDensity: '',
  optMoisture: '',
  minDryDensity: '',
  sandDensity: '',
  equipment: '',
  testCategory: '',
  designCompaction: '',
  recordTester: '',
  recordReviewer: '',
  filler: '',
  remarks: '',
  reviewerSignature: '',
  testerSignature: '',
  status: 0
})

const canEditStructure = computed(() => {
  const s = Number(formData.status)
  if (Number.isNaN(s)) return true
  return s === 0 || s === 2
})

// 数据分析相关状态
const showAnalysisModal = ref(false)
const showExportModal = ref(false)
const exportTemplateInfo = reactive({
  baseName: '灌砂法记录表',
  templateDir: '',
  templates: [],
  formats: ['xls', 'xlsx', 'docx']
})

const hasTemplate = (fmt) => {
  const ext = String(fmt || '').toLowerCase()
  if (!ext) return false
  return Array.isArray(exportTemplateInfo.templates) && exportTemplateInfo.templates.some(t => String(t?.ext || '').toLowerCase() === ext)
}

const openExportModal = async () => {
  try {
    const res = await axios.get('/api/sand-replacement/export-formats', {
      params: { baseName: exportTemplateInfo.baseName }
    })
    if (res.data && res.data.success) {
      exportTemplateInfo.baseName = res.data.baseName || exportTemplateInfo.baseName
      exportTemplateInfo.templateDir = res.data.templateDir || ''
      exportTemplateInfo.templates = Array.isArray(res.data.templates) ? res.data.templates : []
      exportTemplateInfo.formats = Array.isArray(res.data.formats) && res.data.formats.length ? res.data.formats : exportTemplateInfo.formats
      showExportModal.value = true
    } else {
      alert((res.data && res.data.message) ? res.data.message : '获取可导出格式失败')
    }
  } catch (e) {
    alert('获取可导出格式失败')
  }
}

const closeExportModal = () => {
  showExportModal.value = false
}

const startExport = async (fmt) => {
  const ext = String(fmt || '').toLowerCase()
  if (!hasTemplate(ext)) {
    const dir = exportTemplateInfo.templateDir ? `\n模板目录：${exportTemplateInfo.templateDir}` : ''
    alert(`未找到模板：${exportTemplateInfo.baseName}.${ext}\n请先将对应模板文件放入“表”文件夹后再导出。${dir}`)
    return
  }
  showExportModal.value = false
  await exportByFormat(ext)
}
const analysisRange = reactive({ start: '', end: '' })
const analysisResults = reactive({
  totalSandMassMin: '',
  totalSandMassMax: '',
  remainingSandMassMin: '',
  remainingSandMassMax: '',
  coneSandMassMin: '',
  coneSandMassMax: '',
  pitSandMassMin: '',
  pitSandMassMax: '',
  pitVolumeMin: '',
  pitVolumeMax: '',
  sampleMassMin: '',
  sampleMassMax: '',
  wetDensityMin: '',
  wetDensityMax: '',
  boxMassMin: '',
  boxMassMax: '',
  boxWetMassMin: '',
  boxWetMassMax: '',
  boxDryMassMin: '',
  boxDryMassMax: '',
  moistureContentMin: '',
  moistureContentMax: '',
  dryDensityMin: '',
  dryDensityMax: '',
  compactionMin: '',
  compactionMax: ''
})

// Status Text Helper
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

// Workflow Action Handler
const submitWorkflow = async (action) => {
    saveCurrentRecordState()

    // Get current user
    const user = JSON.parse(localStorage.getItem('userInfo'))
    if (!user || !user.username) {
        alert('请先登录')
        return
    }

    let signatureData = null
    
    if (action === 'SUBMIT') {
        if (!formData.testerSignature) {
            try {
                const sigRes = await axios.post('/api/signature/get', { userAccount: user.username })
                if (sigRes.data.success && sigRes.data.data && sigRes.data.data.signatureBlob) {
                     formData.testerSignature = `data:image/png;base64,${sigRes.data.data.signatureBlob}`
                     if (!formData.recordTester) {
                        formData.recordTester = user.userName || user.username
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
        if (formData.recordTester && user.username !== formData.recordTester && user.userName !== formData.recordTester) {
            alert('您不是该单据的记录检测人 (' + formData.recordTester + ')，无权提交')
            return
        }
        
        signatureData = formData.testerSignature.replace(/^data:image\/\w+;base64,/, '')
    } else if (action === 'AUDIT_PASS') {
         if (formData.recordReviewer && user.username !== formData.recordReviewer && user.fullName !== formData.recordReviewer) {
            alert('您不是该单据的记录校核人 (' + formData.recordReviewer + ')，无权操作')
            return
         }

         if (!formData.reviewerSignature) {
             try {
                 const sigRes = await axios.post('/api/signature/get', { userAccount: user.username })
                 if (sigRes.data.success && sigRes.data.data && sigRes.data.data.signatureBlob) {
                      formData.reviewerSignature = `data:image/png;base64,${sigRes.data.data.signatureBlob}`
                      if (!formData.recordReviewer) {
                         formData.recordReviewer = user.userName || user.username
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
    } else if (action === 'REJECT' && formData.status === 1) {
         // Role check: Only recordReviewer can reject
         if (formData.recordReviewer && user.username !== formData.recordReviewer && user.fullName !== formData.recordReviewer) {
            alert('您不是该单据的记录校核人 (' + formData.recordReviewer + ')，无权操作')
            return
         }
    }


    try {
        if (action === 'SUBMIT' || action === 'AUDIT_PASS') {
            if (!records.value || records.value.length === 0) {
                alert('没有可操作的记录')
                return
            }

            const isTesterAction = action === 'SUBMIT'
            const signatureSrc = isTesterAction ? formData.testerSignature : formData.reviewerSignature

            for (let i = 0; i < records.value.length; i++) {
                const rec = records.value[i] || {}
                let pageData = {}
                try {
                    pageData = rec.dataJson ? JSON.parse(rec.dataJson) : {}
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
                    if (!pageData.recordTester) {
                        pageData.recordTester = formData.recordTester
                    }
                } else {
                    pageData.reviewerSignature = signatureSrc
                    if (!pageData.recordReviewer) {
                        pageData.recordReviewer = formData.recordReviewer
                    }
                }

                rec.inspectSignaturePhoto = pageData.testerSignature
                rec.reviewSignaturePhoto = pageData.reviewerSignature
                rec.dataJson = JSON.stringify(pageData)
            }

            for (let i = 0; i < records.value.length; i++) {
                const rec = records.value[i] || {}
                let pageData = {}
                try {
                    pageData = rec.dataJson ? JSON.parse(rec.dataJson) : {}
                } catch (e) {
                    pageData = {}
                }

                const dataJsonObj = { ...pageData }
                delete dataJsonObj.id
                delete dataJsonObj.status
                delete dataJsonObj.tester
                delete dataJsonObj.reviewer

                const dataToSave = {
                    id: rec.id,
                    entrustmentId: pageData.entrustmentId || formData.entrustmentId || props.id,
                    status: String(pageData.status ?? rec.status ?? formData.status),
                    dataJson: JSON.stringify(dataJsonObj),
                    reviewSignaturePhoto: pageData.reviewerSignature,
                    inspectSignaturePhoto: pageData.testerSignature,
                    tester: pageData.recordTester,
                    reviewer: pageData.recordReviewer,
                    recordTester: pageData.recordTester,
                    recordReviewer: pageData.recordReviewer,
                    filler: pageData.filler,
                    soilType: pageData.soilType
                }

                const saveRes = await axios.post('/api/sand-replacement/save', dataToSave)
                if (!saveRes.data || !saveRes.data.success) {
                    alert(`第${i + 1}页保存失败: ${(saveRes.data && saveRes.data.message) || ''}`)
                    return
                }
                if (saveRes.data.data) {
                    records.value[i] = saveRes.data.data
                }
            }

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
                const rec = records.value[i] || {}
                if (!rec.id) {
                    alert(`第${i + 1}页未保存，无法提交流程`)
                    return
                }
                if (!eligibleStatus(rec.status)) {
                    skippedCount++
                    continue
                }

                const request = {
                    tableType: 'SAND_REPLACEMENT',
                    recordId: rec.id,
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
                rec.status = response.data.data
                successCount++
            }

            alert(`操作成功：${successCount} 条${skippedCount ? `，跳过 ${skippedCount} 条` : ''}`)
            loadData(formData.entrustmentId || props.id)
            return
        }

        if (!formData.id) {
            alert('请先保存记录')
            return
        }

        const request = {
            tableType: 'SAND_REPLACEMENT',
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
            loadData(formData.entrustmentId || props.id)
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
  for (let i = 0; i < 16; i++) {
    formData[`totalSandMass_${i}`] = ''
    formData[`location_${i}`] = ''
    formData[`boxMass_${i}`] = ''
    formData[`avgDryDensity_${i}`] = ''
    formData[`pitVolume_${i}`] = ''
    formData[`compaction_${i}`] = ''
    formData[`moistureContent_${i}`] = ''
    formData[`boxNo_${i}`] = ''
    formData[`pitSandMass_${i}`] = ''
    formData[`wetDensity_${i}`] = ''
    formData[`avgMoisture_${i}`] = ''
    formData[`dryDensity_${i}`] = ''
    formData[`coneSandMass_${i}`] = ''
    formData[`boxWetMass_${i}`] = ''
    formData[`remainingSandMass_${i}`] = ''
    formData[`boxDryMass_${i}`] = ''
    formData[`sampleMass_${i}`] = ''
  }
}
initDynamicFields()

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  if (isNaN(d.getTime())) return ''
  const year = d.getFullYear()
  const month = ('0' + (d.getMonth() + 1)).slice(-2)
  const day = ('0' + d.getDate()).slice(-2)
  return `${year}-${month}-${day}`
}

const normalizeSignatureSrc = (v) => {
  if (!v) return ''
  const s = String(v).trim()
  if (!s) return ''
  if (s.startsWith('data:image')) return s
  if (/^[A-Za-z0-9+/=]+$/.test(s)) return `data:image/png;base64,${s}`
  return s
}

const mapRecordToFormData = (record) => {
  // Clear dynamic fields first
  initDynamicFields()

  formData.id = record.id || ''
  formData.entrustmentId = record.entrustmentId || props.id
  
  // Signatures
  formData.reviewerSignature = normalizeSignatureSrc(record.reviewSignaturePhoto || '')
  formData.testerSignature = normalizeSignatureSrc(record.inspectSignaturePhoto || '')
  
  // Map Roles
  // Load defaults from directory if available
  const directory = JSON.parse(localStorage.getItem('currentDirectory') || '{}')
  
  // Filler - Priority: record.filler
  formData.filler = record.filler || ''

  // Record Tester - Priority: record.recordTester -> legacy tester
  formData.recordTester = record.recordTester || record.tester || ''

  // Record Reviewer - Priority: record.recordReviewer -> legacy reviewer
  formData.recordReviewer = record.recordReviewer || record.reviewer || ''

  // Map fields from BusinessEntity/Entrustment (Always map these first as defaults)
  // 优先使用施工部位作为单元工程名称
  if (record.constructionPart) formData.projectName = record.constructionPart
  else if (record.projectName) formData.projectName = record.projectName
  if (record.wtNum) formData.unifiedNumber = record.wtNum
  if (record.commissionDate) formData.testDate = formatDate(record.commissionDate)
  if (record.entrustmentId) formData.unifiedNumber = record.entrustmentId
  if (record.clientUnit) formData.entrustingUnit = record.clientUnit
  if (record.constructionPart) formData.constructionPart = record.constructionPart
  if (record.testCategory) formData.testCategory = record.testCategory
  if (record.equipment) formData.equipment = record.equipment
  if (record.testBasis) formData.standard = record.testBasis

  if (record.dataJson) {
    try {
      const parsed = JSON.parse(record.dataJson)
      Object.keys(parsed).forEach(key => {
        // 不要覆盖id和status字段，因为dataJson中的这些字段可能不正确
        if (key !== 'id' && key !== 'status') {
          formData[key] = parsed[key]
        }
      })
      // Specific fields mapping if needed
      if (parsed.projectName) formData.projectName = parsed.projectName
      if (parsed.unifiedNumber) formData.unifiedNumber = parsed.unifiedNumber
      if (parsed.testDate) formData.testDate = parsed.testDate
      if (parsed.standard) formData.standard = parsed.standard
      if (parsed.equipment) formData.equipment = parsed.equipment
      if (parsed.testCategory) formData.testCategory = parsed.testCategory
    } catch (e) {
      console.error('JSON parse error', e)
    }
  }
  
  if (record.status !== null && record.status !== undefined) {
      formData.status = parseInt(record.status) || 0
  } else {
      formData.status = 0
  }
}

const saveCurrentRecordState = () => {
    if (records.value.length > 0 && currentIndex.value >= 0 && currentIndex.value < records.value.length) {
        const current = records.value[currentIndex.value]
        current.dataJson = JSON.stringify(formData)
        current.reviewer = formData.reviewer
        current.tester = formData.tester
        current.reviewSignaturePhoto = formData.reviewerSignature
        current.inspectSignaturePhoto = formData.testerSignature
        current.recordTester = formData.recordTester
        current.recordReviewer = formData.recordReviewer
        current.filler = formData.filler
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

const addRecord = async () => {
    if (!canEditStructure.value) {
        alert('提交后不能新增记录')
        return
    }
    saveCurrentRecordState()
    
    // Fetch Entrustment Info if available
    let entrustmentData = {}
    const wtNum = formData.unifiedNumber || props.wtNum
    if (wtNum) {
        try {
            const res = await axios.get(`/api/jc-core-wt-info/by-wt-num?wtNum=${encodeURIComponent(wtNum)}`)
            if (res.data.success && res.data.data) {
                const eData = res.data.data
                // 检查委托单状态是否为审核通过（状态值为5，支持字符串或数字类型）
                if (parseInt(eData.status) === 5) {
                    entrustmentData = eData
                } else {
                    console.log('委托单状态未审核通过，不自动填充数据')
                }
            }
        } catch (e) {
            console.error('Failed to fetch entrustment info', e)
        }
    }

    let newRecord = {
        id: null,
        entrustmentId: wtNum,
        projectName: entrustmentData.constructionPart || entrustmentData.projectName || '',
        unifiedNumber: wtNum || '',
        testDate: '',
        standard: '',
        equipment: '',
        testCategory: entrustmentData.testCategory || '',
        recordTester: formData.recordTester || '',
        recordReviewer: formData.recordReviewer || '',
        filler: formData.filler || '',
        tester: formData.recordTester || '',
        reviewer: formData.recordReviewer || '',
        status: 0, // 默认为草稿状态
    }
    
    // If we have previous records, copy some fields
    if (records.value.length > 0) {
        const last = records.value[records.value.length - 1]
        let lastData = {}
        try { lastData = JSON.parse(last.dataJson || '{}') } catch(e){}
        
        newRecord.projectName = lastData.projectName || last.projectName || newRecord.projectName
        newRecord.unifiedNumber = lastData.unifiedNumber || last.wtNum || newRecord.unifiedNumber
        newRecord.testDate = lastData.testDate || ''
        newRecord.standard = lastData.standard || ''
        newRecord.equipment = lastData.equipment || ''
        newRecord.testCategory = lastData.testCategory || last.testCategory || newRecord.testCategory
        
        newRecord.recordTester = newRecord.recordTester || last.recordTester || last.tester || ''
        newRecord.recordReviewer = newRecord.recordReviewer || last.recordReviewer || last.reviewer || ''
        newRecord.filler = newRecord.filler || last.filler || ''
        newRecord.tester = newRecord.recordTester
        newRecord.reviewer = newRecord.recordReviewer
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

    const current = records.value[currentIndex.value]
    if (current.id) {
        try {
            const res = await axios.post('/api/sand-replacement/delete', { id: current.id })
            if (!res.data.success) {
                alert('删除失败: ' + res.data.message)
                return
            }
        } catch (e) {
            console.error('Delete error', e)
            alert('删除出错')
            return
        }
    }
    
    records.value.splice(currentIndex.value, 1)
    if (records.value.length === 0) {
        await addRecord()
    } else {
        if (currentIndex.value >= records.value.length) {
            currentIndex.value = records.value.length - 1
        }
        mapRecordToFormData(records.value[currentIndex.value])
    }
}

onMounted(() => {
  // initDynamicFields is called inside mapRecordToFormData, but we can call it here too if needed
  // initDynamicFields()
  
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
  }
})

// 加载数据
const loadData = async (entrustmentId) => {
  try {
    const response = await axios.get(`/api/sand-replacement/get-by-entrustment-id`, {
        params: { entrustmentId: entrustmentId }
    })
    
    if (response.data.success) {
        if (response.data.data && Array.isArray(response.data.data) && response.data.data.length > 0) {
            records.value = response.data.data
            
             // Find record by ID if props.id is provided and it's not the entrustmentId
             let foundIndex = 0
             if (props.id && props.id !== entrustmentId) {
                   const idx = records.value.findIndex(r => r.id === props.id)
                   if (idx !== -1) foundIndex = idx
             }
             currentIndex.value = foundIndex
            mapRecordToFormData(records.value[foundIndex])
        } else {
            // Check if it returned a single object (legacy support or API behavior check)
            if (response.data.data && !Array.isArray(response.data.data) && response.data.data.id) {
                 records.value = [response.data.data]
                 currentIndex.value = 0
                 mapRecordToFormData(records.value[0])
            } else {
                // No records
                records.value = []
                await addRecord()
            }
        }
    } else {
        records.value = []
        await addRecord()
    }
  } catch (error) {
    console.error('Load error', error)
    records.value = []
    await addRecord()
  }
}

const submitForm = async () => {
    try {
        const dataJsonObj = { ...formData }
        delete dataJsonObj.id
        delete dataJsonObj.status
        delete dataJsonObj.tester
        delete dataJsonObj.reviewer

        const dataToSave = {
            id: formData.id,
            entrustmentId: formData.entrustmentId || props.id,
            status: String(formData.status), // 确保status是字符串类型
            dataJson: JSON.stringify(dataJsonObj),
            reviewSignaturePhoto: formData.reviewerSignature,
            inspectSignaturePhoto: formData.testerSignature,
            tester: formData.recordTester, // Sync legacy field
            reviewer: formData.recordReviewer, // Sync legacy field
            // Roles
            recordTester: formData.recordTester,
            recordReviewer: formData.recordReviewer,
            filler: formData.filler,
            
            // Map entity fields
            soilType: formData.soilType, // ensure this exists in formData if used
            // other fields...
        }
        
        const response = await axios.post('/api/sand-replacement/save', dataToSave)
        if (response.data.success) {
            alert('保存成功')
            if (response.data.data) {
                const saved = response.data.data
                records.value[currentIndex.value] = saved
                mapRecordToFormData(saved)
            }
        } else {
            alert('保存失败: ' + response.data.message)
        }
    } catch (e) {
        console.error('Save error', e)
        alert('保存失败')
    }
}



const printDocument = () => {
  window.print()
}

const exportExcel = async () => {
  await openExportModal()
}

const exportByFormat = async (format) => {
  try {
    saveCurrentRecordState()
    const list = records.value && records.value.length > 0 ? records.value : []
    const totalRecords = list.length > 0 ? list.length : 1
    const successPaths = []
    const failures = []

    const buildExportDataFromRecord = (record, index) => {
      let dynamicData = {}
      try {
        dynamicData = record && record.dataJson ? JSON.parse(record.dataJson) : {}
      } catch (e) {
        dynamicData = {}
      }

      const pageNo = index + 1
      return {
        ...dynamicData,
        entrustingUnit: record?.clientUnit || dynamicData.entrustingUnit || formData.entrustingUnit || '',
        unifiedNumber: record?.wtNum || dynamicData.unifiedNumber || formData.unifiedNumber || record?.entrustmentId || '',
        projectName: dynamicData.projectName || formData.projectName || record?.constructionPart || record?.projectName || '',
        testDate: dynamicData.testDate || formData.testDate || '',
        standard: dynamicData.standard || formData.standard || record?.testBasis || '',
        sandDensity: dynamicData.sandDensity || formData.sandDensity || '',
        equipment: dynamicData.equipment || formData.equipment || record?.equipment || '',
        testCategory: dynamicData.testCategory || formData.testCategory || record?.testCategory || '',
        designCompaction: dynamicData.designCompaction || formData.designCompaction || '',
        remarks: dynamicData.remarks || formData.remarks || '',
        pageNo,
        pageNum: pageNo,
        pageIndex: index,
        totalPages: totalRecords,
        pageTotal: totalRecords,
        pageText: `${pageNo}/${totalRecords}`,
        recordNo: index + 1,
        recordIndex: index,
        totalRecords
      }
    }

    for (let i = 0; i < totalRecords; i++) {
      const record = list.length > 0 ? list[i] : null
      const recordNo = i + 1
      const entrustmentId = (record && record.entrustmentId) ? record.entrustmentId : (props.id || formData.entrustmentId || '')
      const id = record && record.id ? record.id : formData.id

      const dataObj = record ? buildExportDataFromRecord(record, i) : (() => {
        const d = { ...formData }
        delete d.id
        delete d.status
        return {
          ...d,
          pageNo: recordNo,
          pageNum: recordNo,
          pageIndex: i,
          totalPages: totalRecords,
          pageTotal: totalRecords,
          pageText: `${recordNo}/${totalRecords}`,
          recordNo,
          recordIndex: i,
          totalRecords
        }
      })()

      try {
        const response = await axios.post('/api/sand-replacement/export-excel', {
          id,
          entrustmentId,
          recordNo,
          totalRecords,
          pageNo: recordNo,
          totalPages: totalRecords,
          data: dataObj,
          format,
          templateBaseName: exportTemplateInfo.baseName
        })

        if (response.data && response.data.success) {
          successPaths.push(response.data.path)
        } else {
          failures.push(`第${recordNo}条：${response.data && response.data.message ? response.data.message : '未知错误'}`)
        }
      } catch (e) {
        failures.push(`第${recordNo}条：${e && e.message ? e.message : '请求失败'}`)
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

// 返回列表
const goToList = () => {
  if (navigateTo) {
    navigateTo('SandReplacementRecordList')
  }
}

const openBackendPdfPreview = (actionUrl) => {
  if (!pdfForm.value) return
  const container = pdfForm.value.closest('.sandReplacementRecord-container')
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
  const pageWidthMm = 297
  const pageHeightMm = 210
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
      @page { size: A4 landscape; margin: 0; }
      html, body { margin: 0; padding: 0; background: #fff; width: 297mm; height: 210mm; overflow: hidden; }
      .pdf-sheet { width: 297mm; height: 210mm; padding: 6mm; box-sizing: border-box; overflow: hidden; }
      .pdf-page { width: 285mm; height: 198mm; overflow: hidden; box-sizing: border-box; position: relative; }
      .pdf-content { position: absolute; left: 0; top: 0; transform-origin: top left; }
      .pdf-preview input, .pdf-preview textarea, .pdf-preview select { display: none !important; }
      .pdf-preview { font-family: 'SimSun', 'Songti SC', serif; font-size: 16px; overflow: visible; }
      .pdf-preview * { page-break-inside: avoid; break-inside: avoid; }
      .pdf-preview h1, .pdf-preview h2 { font-size: 24px; font-weight: bold; }
      .pdf-preview [data-name] { display: inline-block; width: auto !important; max-width: 100% !important; box-sizing: border-box; overflow-wrap: anywhere; word-break: break-all; white-space: pre-wrap; }
      .pdf-preview .header-info { width: 100%; box-sizing: border-box; }
      .pdf-preview .header-info > span { display: flex; align-items: flex-end; flex: 1; min-width: 0; gap: 4px; }
      .pdf-preview .header-info > span > [data-name] { flex: 1; min-width: 0; width: auto !important; box-sizing: border-box; }
      .pdf-preview.sandReplacementRecord-container { width: 285mm; height: 198mm; max-width: 285mm; min-width: 0; margin: 0; padding: 0; box-sizing: border-box; display: flex; flex-direction: column; }
      .pdf-preview #pdfForm { flex: 1; min-height: 0; display: flex; flex-direction: column; }
      .pdf-preview #pdfForm > table { flex: 0 0 auto; height: auto; }
      .pdf-preview table { width: 100%; height: auto; box-sizing: border-box; table-layout: fixed; font-size: 16px; word-break: break-all; }
      .pdf-preview table td, .pdf-preview table th { box-sizing: border-box; padding: 6px 8px; line-height: 1.35; }
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
  openBackendPdfPreview('/api/pdf/sand_replacement_record/generate')
}

const previewPdf = () => {
  openBackendPdfPreview('/api/pdf/sand_replacement_record/preview')
}

// 数据分析相关方法
const openAnalysisModal = () => {
  showAnalysisModal.value = true
  Object.keys(analysisResults).forEach(key => {
    analysisResults[key] = ''
  })
  analysisRange.start = ''
  analysisRange.end = ''
}

const closeAnalysisModal = () => {
  showAnalysisModal.value = false
}

// 生成指定范围内的随机数
const getRandomInRange = (min, max, decimalPlaces) => {
  if (!min || !max) return null
  const minNum = parseFloat(min)
  const maxNum = parseFloat(max)
  if (isNaN(minNum) || isNaN(maxNum)) return null
  const random = Math.random() * (maxNum - minNum) + minNum
  return random.toFixed(decimalPlaces)
}

const autoAnalyzeAndFill = () => {
  // 将用户输入的列号（1-4）转换为索引（0-3）
  const startCol = (analysisRange.start === '' || analysisRange.start === null || analysisRange.start === undefined)
    ? 1
    : parseInt(String(analysisRange.start), 10)
  const endCol = (analysisRange.end === '' || analysisRange.end === null || analysisRange.end === undefined)
    ? 4
    : parseInt(String(analysisRange.end), 10)
  const start = (Number.isFinite(startCol) ? startCol : 1) - 1
  const end = (Number.isFinite(endCol) ? endCol : 4) - 1
  
  // 确保范围在有效范围内
  const validStart = Math.max(0, start)
  const validEnd = Math.min(3, end)
  
  if (validStart > validEnd) {
    alert('起始列不能大于结束列')
    return
  }
  
  // 收集数据
  const data = {
    totalSandMass: [],
    remainingSandMass: [],
    coneSandMass: [],
    pitSandMass: [],
    pitVolume: [],
    sampleMass: [],
    wetDensity: [],
    boxMass: [],
    boxWetMass: [],
    boxDryMass: [],
    moistureContent: [],
    dryDensity: [],
    compaction: []
  }
  
  // 收集每列的数据
  for (let col = validStart; col <= validEnd; col++) {
    // 每列有2个重复的测试数据（对应16个含水率测试）
    for (let i = col * 2; i < col * 2 + 2; i++) {
      // 基本参数
      const totalSandMass = parseFloat(formData[`totalSandMass_${i}`])
      const remainingSandMass = parseFloat(formData[`remainingSandMass_${i}`])
      const coneSandMass = parseFloat(formData[`coneSandMass_${i}`])
      const pitSandMass = parseFloat(formData[`pitSandMass_${i}`])
      const pitVolume = parseFloat(formData[`pitVolume_${i}`])
      const sampleMass = parseFloat(formData[`sampleMass_${i}`])
      const wetDensity = parseFloat(formData[`wetDensity_${i}`])
      const dryDensity = parseFloat(formData[`dryDensity_${i}`])
      
      // 含水率相关参数（每列有4个含水率测试）
      for (let j = col * 4; j < col * 4 + 4; j++) {
        const boxMass = parseFloat(formData[`boxMass_${j}`])
        const boxWetMass = parseFloat(formData[`boxWetMass_${j}`])
        const boxDryMass = parseFloat(formData[`boxDryMass_${j}`])
        const moistureContent = parseFloat(formData[`moistureContent_${j}`])
        
        if (!isNaN(boxMass)) data.boxMass.push(boxMass)
        if (!isNaN(boxWetMass)) data.boxWetMass.push(boxWetMass)
        if (!isNaN(boxDryMass)) data.boxDryMass.push(boxDryMass)
        if (!isNaN(moistureContent)) data.moistureContent.push(moistureContent)
      }
      
      if (!isNaN(totalSandMass)) data.totalSandMass.push(totalSandMass)
      if (!isNaN(remainingSandMass)) data.remainingSandMass.push(remainingSandMass)
      if (!isNaN(coneSandMass)) data.coneSandMass.push(coneSandMass)
      if (!isNaN(pitSandMass)) data.pitSandMass.push(pitSandMass)
      if (!isNaN(pitVolume)) data.pitVolume.push(pitVolume)
      if (!isNaN(sampleMass)) data.sampleMass.push(sampleMass)
      if (!isNaN(wetDensity)) data.wetDensity.push(wetDensity)
      if (!isNaN(dryDensity)) data.dryDensity.push(dryDensity)
    }
    
    // 压实度（每列1个）
    const compaction = parseFloat(formData[`compaction_${col}`])
    if (!isNaN(compaction)) data.compaction.push(compaction)
  }
  
  // 计算范围（如果用户没有手动输入）
  const calculateRange = (key, decimalPlaces) => {
    if (!analysisResults[`${key}Min`] || !analysisResults[`${key}Max`]) {
      if (data[key].length > 0) {
        const min = Math.min(...data[key])
        const max = Math.max(...data[key])
        analysisResults[`${key}Min`] = min.toFixed(decimalPlaces)
        analysisResults[`${key}Max`] = max.toFixed(decimalPlaces)
      }
    }
  }
  
  calculateRange('totalSandMass', 1)
  calculateRange('remainingSandMass', 1)
  calculateRange('coneSandMass', 1)
  calculateRange('pitSandMass', 1)
  calculateRange('pitVolume', 1)
  calculateRange('sampleMass', 1)
  calculateRange('wetDensity', 2)
  calculateRange('boxMass', 1)
  calculateRange('boxWetMass', 1)
  calculateRange('boxDryMass', 1)
  calculateRange('moistureContent', 1)
  calculateRange('dryDensity', 2)
  calculateRange('compaction', 1)
  
  // 计算或验证量砂密度
  const calculateSandDensity = () => {
    const sandDensityValues = []
    // 收集所有有效的量砂密度计算值
    for (let col = validStart; col <= validEnd; col++) {
      for (let i = col * 2; i < col * 2 + 2; i++) {
        const pitSandMass = parseFloat(formData[`pitSandMass_${i}`])
        const pitVolume = parseFloat(formData[`pitVolume_${i}`])
        if (!isNaN(pitSandMass) && !isNaN(pitVolume) && pitVolume > 0) {
          const sandDensity = pitSandMass / pitVolume
          sandDensityValues.push(sandDensity)
        }
      }
    }
    
    if (sandDensityValues.length > 0) {
      const avgSandDensity = sandDensityValues.reduce((sum, val) => sum + val, 0) / sandDensityValues.length
      return avgSandDensity.toFixed(3)
    } else {
      // 如果没有试坑耗砂质量数据，尝试根据试坑体积和试样质量计算
      const calculatedSandDensityValues = []
      for (let col = validStart; col <= validEnd; col++) {
        for (let i = col * 2; i < col * 2 + 2; i++) {
          const sampleMass = parseFloat(formData[`sampleMass_${i}`])
          const pitVolume = parseFloat(formData[`pitVolume_${i}`])
          if (!isNaN(sampleMass) && !isNaN(pitVolume) && pitVolume > 0) {
            // 计算试样湿密度
            const wetDensity = sampleMass / pitVolume
            // 根据湿密度估算量砂密度（量砂密度通常在1.4-1.6 g/cm³之间）
            let estimatedSandDensity
            if (wetDensity < 1.9) {
              estimatedSandDensity = 1.40
            } else if (wetDensity < 2.0) {
              estimatedSandDensity = 1.45
            } else if (wetDensity < 2.1) {
              estimatedSandDensity = 1.50
            } else {
              estimatedSandDensity = 1.55
            }
            calculatedSandDensityValues.push(estimatedSandDensity)
          }
        }
      }
      if (calculatedSandDensityValues.length > 0) {
        const avgCalculatedSandDensity = calculatedSandDensityValues.reduce((sum, val) => sum + val, 0) / calculatedSandDensityValues.length
        return avgCalculatedSandDensity.toFixed(3)
      }
    }
    return null
  }
  
  // 计算或验证量砂密度
  const calculatedSandDensity = calculateSandDensity()
  if (calculatedSandDensity) {
    formData.sandDensity = calculatedSandDensity
  }
  // 不再使用默认值，只使用计算结果或用户输入
  
  // 收集实际的干密度、含水率和压实度数据
  const actualDryDensities = []
  const actualMoistures = []
  const actualCompactions = []
  
  for (let col = validStart; col <= validEnd; col++) {
    for (let i = col * 2; i < col * 2 + 2; i++) {
      const dryDensity = parseFloat(formData[`dryDensity_${i}`])
      if (!isNaN(dryDensity)) actualDryDensities.push(dryDensity)
    }
    
    for (let j = col * 4; j < col * 4 + 4; j++) {
      const moisture = parseFloat(formData[`moistureContent_${j}`])
      if (!isNaN(moisture)) actualMoistures.push(moisture)
    }
    
    const compaction = parseFloat(formData[`compaction_${col}`])
    if (!isNaN(compaction)) actualCompactions.push(compaction)
  }
  
  // 计算实际数据的范围
  let actualDryDensityMin = null
  let actualDryDensityMax = null
  let actualMoistureMin = null
  let actualMoistureMax = null
  let actualCompactionMin = null
  let actualCompactionMax = null
  
  if (actualDryDensities.length > 0) {
    actualDryDensityMin = Math.min(...actualDryDensities)
    actualDryDensityMax = Math.max(...actualDryDensities)
  }
  
  if (actualMoistures.length > 0) {
    actualMoistureMin = Math.min(...actualMoistures)
    actualMoistureMax = Math.max(...actualMoistures)
  }
  
  if (actualCompactions.length > 0) {
    actualCompactionMin = Math.min(...actualCompactions)
    actualCompactionMax = Math.max(...actualCompactions)
  }
  
  // 填充设计参数（基于实际数据）
  // 最大干密度
  if (actualDryDensityMax) {
    formData.maxDryDensity = actualDryDensityMax.toFixed(2)
  } else if (!formData.maxDryDensity && analysisResults.dryDensityMax) {
    formData.maxDryDensity = analysisResults.dryDensityMax
  }
  
  // 最小干密度
  if (actualDryDensityMin) {
    formData.minDryDensity = actualDryDensityMin.toFixed(2)
  } else if (!formData.minDryDensity && analysisResults.dryDensityMin) {
    formData.minDryDensity = analysisResults.dryDensityMin
  }
  
  // 最优含水率
  if (actualMoistureMin && actualMoistureMax) {
    const avgMoisture = (actualMoistureMin + actualMoistureMax) / 2
    formData.optMoisture = avgMoisture.toFixed(1)
  } else if (actualMoistureMax) {
    formData.optMoisture = actualMoistureMax.toFixed(1)
  } else if (actualMoistureMin) {
    formData.optMoisture = actualMoistureMin.toFixed(1)
  } else if (!formData.optMoisture) {
    if (analysisResults.moistureContentMin && analysisResults.moistureContentMax) {
      const min = parseFloat(analysisResults.moistureContentMin)
      const max = parseFloat(analysisResults.moistureContentMax)
      if (!isNaN(min) && !isNaN(max)) {
        formData.optMoisture = ((min + max) / 2).toFixed(1)
      }
    } else if (analysisResults.moistureContentMax) {
      formData.optMoisture = analysisResults.moistureContentMax
    } else if (analysisResults.moistureContentMin) {
      formData.optMoisture = analysisResults.moistureContentMin
    }
  }
  
  // 设计压实度
  if (actualCompactionMax) {
    formData.designCompaction = actualCompactionMax.toFixed(1)
  } else if (actualCompactionMin) {
    formData.designCompaction = actualCompactionMin.toFixed(1)
  } else if (!formData.designCompaction) {
    if (analysisResults.compactionMax) {
      formData.designCompaction = analysisResults.compactionMax
    } else if (analysisResults.compactionMin) {
      formData.designCompaction = analysisResults.compactionMin
    }
  }
  
  // 填充数据
  for (let col = validStart; col <= validEnd; col++) {
    // 每列有2个重复的测试数据
    for (let i = col * 2; i < col * 2 + 2; i++) {
      // 填充基本参数
      if (!formData[`totalSandMass_${i}`]) {
        const randomValue = getRandomInRange(analysisResults.totalSandMassMin, analysisResults.totalSandMassMax, 1)
        if (randomValue) formData[`totalSandMass_${i}`] = randomValue
      }
      
      if (!formData[`remainingSandMass_${i}`]) {
        const randomValue = getRandomInRange(analysisResults.remainingSandMassMin, analysisResults.remainingSandMassMax, 1)
        if (randomValue) formData[`remainingSandMass_${i}`] = randomValue
      }
      
      if (!formData[`coneSandMass_${i}`]) {
        const randomValue = getRandomInRange(analysisResults.coneSandMassMin, analysisResults.coneSandMassMax, 1)
        if (randomValue) formData[`coneSandMass_${i}`] = randomValue
      }
      
      if (!formData[`pitSandMass_${i}`]) {
        const randomValue = getRandomInRange(analysisResults.pitSandMassMin, analysisResults.pitSandMassMax, 1)
        if (randomValue) formData[`pitSandMass_${i}`] = randomValue
      }
      
      if (!formData[`pitVolume_${i}`]) {
        const randomValue = getRandomInRange(analysisResults.pitVolumeMin, analysisResults.pitVolumeMax, 1)
        if (randomValue) formData[`pitVolume_${i}`] = randomValue
      }
      
      if (!formData[`sampleMass_${i}`]) {
        const randomValue = getRandomInRange(analysisResults.sampleMassMin, analysisResults.sampleMassMax, 1)
        if (randomValue) formData[`sampleMass_${i}`] = randomValue
      }
      
      if (!formData[`wetDensity_${i}`]) {
        const randomValue = getRandomInRange(analysisResults.wetDensityMin, analysisResults.wetDensityMax, 2)
        if (randomValue) formData[`wetDensity_${i}`] = randomValue
      }
      
      if (!formData[`dryDensity_${i}`]) {
        let min, max
        // 如果有最大干密度和最小干密度，使用它们作为范围
        if (formData.maxDryDensity && formData.minDryDensity) {
          min = parseFloat(formData.minDryDensity)
          max = parseFloat(formData.maxDryDensity)
          // 确保范围合理
          if (!isNaN(min) && !isNaN(max) && min < max) {
            // 实测干密度通常在最小和最大干密度之间
            const randomValue = getRandomInRange(min, max, 2)
            if (randomValue) formData[`dryDensity_${i}`] = randomValue
          } else {
            // 使用默认范围
            const randomValue = getRandomInRange(analysisResults.dryDensityMin, analysisResults.dryDensityMax, 2)
            if (randomValue) formData[`dryDensity_${i}`] = randomValue
          }
        } else {
          // 使用默认范围
          const randomValue = getRandomInRange(analysisResults.dryDensityMin, analysisResults.dryDensityMax, 2)
          if (randomValue) formData[`dryDensity_${i}`] = randomValue
        }
      }
      
      // 填充含水率相关参数
      for (let j = col * 4; j < col * 4 + 4; j++) {
        if (!formData[`boxMass_${j}`]) {
          const randomValue = getRandomInRange(analysisResults.boxMassMin, analysisResults.boxMassMax, 1)
          if (randomValue) formData[`boxMass_${j}`] = randomValue
        }
        
        if (!formData[`boxWetMass_${j}`]) {
          const randomValue = getRandomInRange(analysisResults.boxWetMassMin, analysisResults.boxWetMassMax, 1)
          if (randomValue) formData[`boxWetMass_${j}`] = randomValue
        }
        
        if (!formData[`boxDryMass_${j}`]) {
          const randomValue = getRandomInRange(analysisResults.boxDryMassMin, analysisResults.boxDryMassMax, 1)
          if (randomValue) formData[`boxDryMass_${j}`] = randomValue
        }
        
        if (!formData[`moistureContent_${j}`]) {
          const randomValue = getRandomInRange(analysisResults.moistureContentMin, analysisResults.moistureContentMax, 1)
          if (randomValue) formData[`moistureContent_${j}`] = randomValue
        }
      }
    }
    
    // 计算并填充平均含水率（每列有2个平均值，每个基于2个含水率测试值）
    for (let k = 0; k < 2; k++) {
      const moistureValues = []
      // 每组取2个含水率测试值
      for (let j = col * 4 + k * 2; j < col * 4 + k * 2 + 2; j++) {
        const moisture = parseFloat(formData[`moistureContent_${j}`])
        if (!isNaN(moisture)) moistureValues.push(moisture)
      }
      if (moistureValues.length > 0 && !formData[`avgMoisture_${col * 2 + k}`]) {
        const avg = moistureValues.reduce((sum, val) => sum + val, 0) / moistureValues.length
        formData[`avgMoisture_${col * 2 + k}`] = avg.toFixed(1)
      }
    }
    
    // 计算并填充平均实测干密度
    const dryDensityValues = []
    for (let i = col * 2; i < col * 2 + 2; i++) {
      const dryDensity = parseFloat(formData[`dryDensity_${i}`])
      if (!isNaN(dryDensity)) dryDensityValues.push(dryDensity)
    }
    if (dryDensityValues.length > 0 && !formData[`avgDryDensity_${col}`]) {
      const avg = dryDensityValues.reduce((sum, val) => sum + val, 0) / dryDensityValues.length
      formData[`avgDryDensity_${col}`] = avg.toFixed(2)
    }
    
    // 填充压实度
    if (!formData[`compaction_${col}`]) {
      // 如果有设计压实度，使用它作为参考
      if (formData.designCompaction) {
        const designValue = parseFloat(formData.designCompaction)
        if (!isNaN(designValue)) {
          // 压实度通常在设计值附近波动
          const min = Math.max(90, designValue - 5)
          const max = Math.min(100, designValue + 5)
          const randomValue = getRandomInRange(min, max, 1)
          if (randomValue) {
            formData[`compaction_${col}`] = randomValue
          } else {
            formData[`compaction_${col}`] = formData.designCompaction
          }
        } else {
          // 使用默认范围
          const randomValue = getRandomInRange(analysisResults.compactionMin, analysisResults.compactionMax, 1)
          if (randomValue) {
            formData[`compaction_${col}`] = randomValue
          }
        }
      } else {
        // 使用默认范围
        const randomValue = getRandomInRange(analysisResults.compactionMin, analysisResults.compactionMax, 1)
        if (randomValue) {
          formData[`compaction_${col}`] = randomValue
        }
      }
    }
  }
  
  alert('自动分析并填充完成')
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

        .toolbar-left,
        .toolbar-right {
            display: flex;
            align-items: center;
            flex-wrap: wrap;
            gap: 12px;
            flex: 1;
        }

        .toolbar-left {
            margin-left: 0;
        }

        .toolbar-right {
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

        .sandReplacementRecord-container {
            font-family: 'SimSun', 'Songti SC', serif;
            width: 297mm;
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
        .header-info {
            display: flex;
            justify-content: space-between;
            margin-bottom: 5px;
            font-weight: bold;
        }
        .header-info span {
            white-space: nowrap;
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
        }
        .label {
            font-weight: bold;
            white-space: normal;
            overflow-wrap: anywhere;
            word-break: break-all;
        }
        .left-align {
            text-align: left;
            padding-left: 10px;
        }
        input[type="text"], textarea {
            width: 98%;
            border: 1px solid #b3d9ff;
            border-radius: 4px;
            outline: none;
            font-family: inherit;
            font-size: inherit;
            background-color: transparent;
            text-align: center;
            padding: 1px 3px;
            box-sizing: border-box;
        }
        table input[type="text"], table textarea {
            width: 100%;
            border: 1px solid #b3d9ff;
            border-radius: 4px;
            padding: 1px 3px;
            box-sizing: border-box;
            display: block;
            min-height: 26px;
        }
        .left-align input[type="text"] {
            text-align: left;
        }
        input[type="text"]:focus, textarea:focus {
            background-color: #f0f8ff;
            border-color: #3498db;
        }
        .header-info input[type="text"]:focus {
            border-bottom-color: black;
        }
        table input[type="text"]:focus, table textarea:focus {
            border-color: #3498db;
        }
        input[type="text"]:disabled:focus, textarea:disabled:focus {
            background-color: transparent;
            border-color: black;
        }
        table input[type="text"]:disabled:focus, table textarea:disabled:focus {
            border-color: black;
        }

        /* 统一输入字段样式，确保与表格其他字段字体一致 */
        input[type="text"], textarea {
            font-family: inherit;
            font-size: inherit;
            color: inherit;
        }

        input[type="text"]:disabled, textarea:disabled {
            color: inherit;
            font-family: inherit;
            font-size: inherit;
        }
        textarea {
            resize: none;
            overflow: hidden;
            text-align: left;
        }
        .footer-info {
            display: flex;
            justify-content: space-around;
            margin-top: 20px;
            margin-bottom: 10px;
            font-size: 16px;
            font-weight: normal;
        }
        .page-footer {
            margin-top: 5px;
            display: flex;
            justify-content: space-between;
            font-size: 14px;
        }
        @media print {
            @page {
                size: A4 landscape;
                margin: 0;
            }
            .sandReplacementRecord-container {
                width: 100%;
                max-width: none;
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
        
        /* 模态窗口样式 */
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
            width: 600px;
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
        
        .analysis-results {
            margin: 25px 0;
            padding: 20px;
            background-color: #f8f9fa;
            border-radius: 8px;
            border: 1px solid #e0e0e0;
        }
        
        .analysis-results h4 {
            margin-top: 0;
            margin-bottom: 15px;
            color: #333;
            font-size: 16px;
        }
        
        .result-item {
            display: flex;
            justify-content: space-between;
            margin-bottom: 12px;
            align-items: center;
        }
        
        .result-item span:first-child {
            font-weight: 500;
            color: #555;
            flex: 1;
        }
        
        .result-item .range-inputs {
            flex: 2;
            margin-left: 15px;
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
            background-color: #fff;
            border-color: #d0d7de;
            color: #34495e;
        }
        
        .modal-actions .btn-secondary:hover {
            background-color: #f5f7fa;
        }
        
        /* 移除数值输入框的上下调节按钮 */
        input[type="number"] {
            appearance: textfield;
            -moz-appearance: textfield;
        }
        
        input[type="number"]::-webkit-inner-spin-button,
        input[type="number"]::-webkit-outer-spin-button {
            -webkit-appearance: none;
            margin: 0;
        }
    
</style>
