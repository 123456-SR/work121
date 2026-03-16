<template>
  <div class="densityTestReport-container" ref="containerRef">


    <div class="no-print toolbar">
      <div class="toolbar-left">
        <button @click="goToList" class="link-button">&lt; 返回列表</button>
      </div>

      <div class="toolbar-right">
        <div class="page-nav">
          <button class="btn btn-small" @click="currentIndex = 0" :disabled="currentIndex === 0">首页</button>
          <button class="btn btn-small" @click="prevPage" :disabled="currentIndex === 0">上一页</button>
          <span class="page-info">{{ currentIndex + 1 }} / {{ 1 + resultPages.length }}</span>
          <button class="btn btn-small" @click="nextPage" :disabled="currentIndex >= resultPages.length">下一页</button>
          <button class="btn btn-small" @click="currentIndex = resultPages.length" :disabled="currentIndex >= resultPages.length">末页</button>
        </div>
        <span v-if="formData.status !== undefined" class="status-text">
          状态:
          <span :style="{ color: getStatusColor(formData.status) }" class="status-label">
            {{ getStatusText(formData.status) }}
          </span>
        </span>

        <button
          @click="approveAndSave"
          class="btn btn-primary btn-small"
        >
          批准
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
        <button
          @click="exportExcel"
          class="btn btn-secondary btn-small"
        >
          导出数据
        </button>
      </div>
    </div>

    <form id="pdfForm" ref="pdfForm" method="post" v-show="currentIndex === 0">
    <h2>原位密度检测报告</h2>

    <div class="header-info">
        <!-- 委托单位应对应委托单里的单位(clientUnit)，而不是委托人(client) -->
        <span>委托单位：<input type="text" v-model="formData.clientUnit"   name="clientUnit" style="width: 250px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>统一编号：<input type="text" v-model="formData.unifiedNumber"   name="unifiedNumber" style="width: 150px; border-bottom: 1px solid black;" disabled></span>
        <!-- 兼容后端 PDF 旧字段名：把当前委托单位再同步一份到 entrustingUnit，确保 PDF 一定有值 -->
        <input type="hidden" name="entrustingUnit" :value="formData.clientUnit" />
    </div>

    <table>
        <!-- Header Rows -->
        <tr>
            <td class="label">工程名称</td>
            <td colspan="6"><input type="text" v-model="formData.projectName"   name="projectName"></td>
            <td class="label">委托日期</td>
            <td colspan="3"><input type="text" v-model="formData.commissionDate"   name="commissionDate"></td>
        </tr>
        <tr>
            <td class="label">施工部位</td>
            <td colspan="6"><input type="text" v-model="formData.constructionPart"   name="constructionPart"></td>
            <td class="label">检测日期</td>
            <td colspan="3"><input type="text" v-model="formData.testDate"   name="testDate"></td>
        </tr>
        <tr>
            <td class="label">样品名称<br>及状态</td>
            <td colspan="6"><input type="text" v-model="formData.sampleNameStatus"   name="sampleNameStatus"></td>
            <td class="label">报告日期</td>
            <td colspan="3"><input type="text" v-model="formData.reportDate"   name="reportDate"></td>
        </tr>
        <tr>
            <td class="label">仪器设备</td>
            <td colspan="6"><input type="text" v-model="formData.equipment"   name="equipment"></td>
            <td class="label">检测方法</td>
            <td colspan="3"><input type="text" v-model="formData.testMethod"   name="testMethod"></td>
        </tr>
        <tr>
            <td class="label">检测依据</td>
            <td colspan="6"><input type="text" v-model="formData.testBasis"   name="testBasis"></td>
            <td class="label">检测类别</td>
            <td colspan="3"><input type="text" v-model="formData.testCategory"   name="testCategory"></td>
        </tr>
        <tr>
            <td class="label">最大干密度<br>(g/cm³)</td>
            <td colspan="3"><input type="text" v-model="formData.maxDryDensity"   name="maxDryDensity"></td>
            <td class="label">最优含水率 %</td>
            <td colspan="3"><input type="text" v-model="formData.optimumMoisture"   name="optimumMoisture"></td>
            <td class="label">最小干密度<br>(g/cm³)</td>
            <td colspan="2"><input type="text" v-model="formData.minDryDensity"   name="minDryDensity"></td>
        </tr>
        <tr>
            <td class="label">设计指标</td>
            <td colspan="10" class="left-align"><input type="text" v-model="formData.designIndex"   name="designIndex"></td>
        </tr>
        <tr>
            <td class="label">检测结果</td>
            <td colspan="10" class="left-align"><input type="text" v-model="formData.testResult"   name="testResult"></td>
        </tr>

        <tr v-if="isNuclearMethod">
            <td class="label" style="width: 10%;">样品编号</td>
            <td class="label" style="width: 20%;" colspan="3">检测部位<br>(桩号、高程)</td>
            <td class="label" style="width: 14%;" colspan="2">检测日期</td>
            <td class="label" style="width: 14%;">湿密度<br>(g/cm³)</td>
            <td class="label" style="width: 14%;">干密度<br>(g/cm³)</td>
            <td class="label" style="width: 14%;">含水率<br>%</td>
            <td class="label" style="width: 14%;" colspan="2">压实度%</td>
        </tr>
        <tr v-else>
            <td class="label" style="width: 10%;">样品编号</td>
            <td class="label" style="width: 20%;" colspan="3">检测部位<br>(桩号、高程)</td>
            <td class="label" style="width: 14%;" colspan="2">检测日期</td>
            <td class="label" style="width: 14%;">湿密度<br>(g/cm³)</td>
            <td class="label" style="width: 14%;">干密度<br>(g/cm³)</td>
            <td class="label" style="width: 14%;">含水率<br>%</td>
            <td class="label" style="width: 14%;">平均干密度<br>(g/cm³)</td>
            <td class="label" style="width: 14%;">压实度%</td>
        </tr>

        <template v-for="(n, i_idx) in 7" :key="i_idx">
          <tr v-if="isNuclearMethod">
            <td><input type="text" :name="'sampleId_' + i_idx" v-model="formData['sampleId_' + i_idx]"></td>
            <td colspan="3"><input type="text" :name="'location_' + i_idx" v-model="formData['location_' + i_idx]"></td>
            <td colspan="2"><input type="text" :name="'date_' + i_idx" v-model="formData['date_' + i_idx]"></td>
            <td><input type="text" :name="'wetDensity_' + i_idx" v-model="formData['wetDensity_' + i_idx]"></td>
            <td><input type="text" :name="'dryDensity_' + i_idx" v-model="formData['dryDensity_' + i_idx]"></td>
            <td><input type="text" :name="'moisture_' + i_idx" v-model="formData['moisture_' + i_idx]"></td>
            <td colspan="2"><input type="text" :name="'compaction_' + i_idx" v-model="formData['compaction_' + i_idx]"></td>
          </tr>
          <tr v-else>
            <td><input type="text" :name="'sampleId_' + i_idx" v-model="formData['sampleId_' + i_idx]"></td>
            <td colspan="3"><input type="text" :name="'location_' + i_idx" v-model="formData['location_' + i_idx]"></td>
            <td colspan="2"><input type="text" :name="'date_' + i_idx" v-model="formData['date_' + i_idx]"></td>
            <td>
              <div class="two-inputs">
                <input type="text" :name="'wetDensity_' + i_idx" v-model="formData['wetDensity_' + i_idx]">
                <input type="text" :name="'wetDensity2_' + i_idx" v-model="formData['wetDensity2_' + i_idx]">
              </div>
            </td>
            <td>
              <div class="two-inputs">
                <input type="text" :name="'dryDensity_' + i_idx" v-model="formData['dryDensity_' + i_idx]">
                <input type="text" :name="'dryDensity2_' + i_idx" v-model="formData['dryDensity2_' + i_idx]">
              </div>
            </td>
            <td>
              <div class="two-inputs">
                <input type="text" :name="'moisture_' + i_idx" v-model="formData['moisture_' + i_idx]">
                <input type="text" :name="'moisture2_' + i_idx" v-model="formData['moisture2_' + i_idx]">
              </div>
            </td>
            <td><input type="text" :name="'avgDryDensity_' + i_idx" v-model="formData['avgDryDensity_' + i_idx]"></td>
            <td><input type="text" :name="'compaction_' + i_idx" v-model="formData['compaction_' + i_idx]"></td>
          </tr>
        </template>

        <!-- Remarks Section -->
        <tr>
            <td class="label">备 注</td>
            <td colspan="10" class="left-align" style="height: 100px; vertical-align: top;">
                <textarea v-model="formData.remarks"  name="remarks" style="width: 100%; height: 100%;"></textarea>
            </td>
        </tr>
    </table>

    <div class="footer-info">
        <span class="signature-box">
            批准：
            <span class="signature-line"></span>
            <img v-if="formData.approverSignature" :src="formData.approverSignature" class="signature-img" alt="批准人签名" />
        </span>
        <span class="signature-box">
            审核：
            <span class="signature-line"></span>
            <img v-if="formData.reviewerSignature" :src="formData.reviewerSignature" class="signature-img" alt="审核人签名" />
        </span>
        <span class="signature-box">
            检测：
            <span class="signature-line"></span>
            <img v-if="formData.testerSignature" :src="formData.testerSignature" class="signature-img" alt="检测人签名" />
        </span>
    </div>
    <!-- 电子签名隐藏字段 -->
    <input type="hidden" name="testerSignature" :value="formData.testerSignature">
    <input type="hidden" name="reviewerSignature" :value="formData.reviewerSignature">
    <input type="hidden" name="approverSignature" :value="formData.approverSignature">

    <div class="statement">
        声明：<br>
        1. 对本检测报告的复印件未加盖公司检验检测专用章无效。<br>
        2. 对检验结果如有异议，应在收到报告之日起十五日之内向本公司提出。
    </div>

    <div class="company-info" style="display: block;">
        <div>公司名称：河北金涛建设工程质量检测有限公司</div>
        <div style="display: flex; justify-content: space-between; margin-top: 5px;">
            <span>公司地址：石家庄高新区方亿科技工业园区A区第2号楼</span>
            <span>电话：0311—86107634 67300616</span>
        </div>
    </div>


    </form>

    <div v-if="currentIndex > 0">
      <h2>原位密度检测结果</h2>
      <div class="header-info" style="justify-content: space-between;">
        <span>统一编号：<input type="text" :value="formData.unifiedNumber" style="width: 150px; border-bottom: 1px solid black; text-align: left;" disabled></span>
        <span></span>
      </div>

      <table>
        <tr>
          <td class="label" style="width: 15%;">施工部位</td>
          <td colspan="9" class="left-align"><input type="text" v-model="currentResultFormData.constructionPart"></td>
        </tr>
        <tr>
          <td class="label">最大干密度<br>(g/cm³)</td>
          <td colspan="2"><input type="text" v-model="currentResultFormData.maxDryDensity"></td>
          <td class="label">最优含水率 %</td>
          <td colspan="2"><input type="text" v-model="currentResultFormData.optimumMoisture"></td>
          <td class="label">最小干密度<br>(g/cm³)</td>
          <td colspan="3"><input type="text" v-model="currentResultFormData.minDryDensity"></td>
        </tr>
        <tr v-if="isNuclearResultMethod">
          <td class="label" style="width: 10%;">样品编号</td>
          <td class="label" style="width: 25%;" colspan="2">检测部位<br>(桩号、高程)</td>
          <td class="label" style="width: 15%;" colspan="2">检测日期</td>
          <td class="label" style="width: 12.5%;">湿密度<br>(g/cm³)</td>
          <td class="label" style="width: 12.5%;">干密度<br>(g/cm³)</td>
          <td class="label" style="width: 12.5%;">含水率<br>%</td>
          <td class="label" style="width: 12.5%;" colspan="2">压实度%</td>
        </tr>
        <tr v-else>
          <td class="label" style="width: 10%;">样品编号</td>
          <td class="label" style="width: 25%;" colspan="2">检测部位<br>(桩号、高程)</td>
          <td class="label" style="width: 15%;" colspan="2">检测日期</td>
          <td class="label" style="width: 12.5%;">湿密度<br>(g/cm³)</td>
          <td class="label" style="width: 12.5%;">干密度<br>(g/cm³)</td>
          <td class="label" style="width: 12.5%;">含水率<br>%</td>
          <td class="label" style="width: 12.5%;">平均干密度<br>(g/cm³)</td>
          <td class="label" style="width: 12.5%;">压实度%</td>
        </tr>
        <template v-for="(n, i_idx) in 20" :key="'r-'+i_idx">
          <tr v-if="isNuclearResultMethod">
            <td><input type="text" v-model="currentResultFormData['sampleId_' + i_idx]"></td>
            <td colspan="2"><input type="text" v-model="currentResultFormData['location_' + i_idx]"></td>
            <td colspan="2"><input type="text" v-model="currentResultFormData['date_' + i_idx]"></td>
            <td><input type="text" v-model="currentResultFormData['wetDensity_' + i_idx]"></td>
            <td><input type="text" v-model="currentResultFormData['dryDensity_' + i_idx]"></td>
            <td><input type="text" v-model="currentResultFormData['moisture_' + i_idx]"></td>
            <td colspan="2"><input type="text" v-model="currentResultFormData['compaction_' + i_idx]"></td>
          </tr>
          <tr v-else>
            <td><input type="text" v-model="currentResultFormData['sampleId_' + i_idx]"></td>
            <td colspan="2"><input type="text" v-model="currentResultFormData['location_' + i_idx]"></td>
            <td colspan="2"><input type="text" v-model="currentResultFormData['date_' + i_idx]"></td>
            <td>
              <div class="two-inputs">
                <input type="text" v-model="currentResultFormData['wetDensity_' + i_idx]">
                <input type="text" v-model="currentResultFormData['wetDensity2_' + i_idx]">
              </div>
            </td>
            <td>
              <div class="two-inputs">
                <input type="text" v-model="currentResultFormData['dryDensity_' + i_idx]">
                <input type="text" v-model="currentResultFormData['dryDensity2_' + i_idx]">
              </div>
            </td>
            <td>
              <div class="two-inputs">
                <input type="text" v-model="currentResultFormData['moisture_' + i_idx]">
                <input type="text" v-model="currentResultFormData['moisture2_' + i_idx]">
              </div>
            </td>
            <td><input type="text" v-model="currentResultFormData['avgDryDensity_' + i_idx]"></td>
            <td><input type="text" v-model="currentResultFormData['compaction_' + i_idx]"></td>
          </tr>
        </template>
      </table>
    </div>

    <div v-if="showExportModal" class="modal-overlay no-print">
      <div class="modal-content">
        <h3>导出数据</h3>
        <div class="form-group">
          <div>报告模板：{{ exportTemplateInfo.reportBaseName }}.xlsx</div>
          <div v-if="exportTemplateInfo.reportTemplates.length" class="template-files">
            <span v-for="t in exportTemplateInfo.reportTemplates" :key="t.fileName" class="template-file">{{ t.fileName }}</span>
          </div>
          <div v-else>报告模板：无</div>
          <div style="margin-top: 10px;">结果模板：{{ exportTemplateInfo.resultBaseName }}.xlsx</div>
          <div v-if="exportTemplateInfo.resultTemplates.length" class="template-files">
            <span v-for="t in exportTemplateInfo.resultTemplates" :key="t.fileName" class="template-file">{{ t.fileName }}</span>
          </div>
          <div v-else>结果模板：无</div>
        </div>
        <div class="format-buttons">
          <button
            v-for="fmt in exportTemplateInfo.formats"
            :key="fmt"
            @click="startExport(fmt)"
            class="btn btn-primary btn-small"
          >
            {{ String(fmt).toUpperCase() }}
          </button>
        </div>
        <div class="modal-actions">
          <button @click="closeExportModal" class="btn btn-secondary btn-small">关闭</button>
        </div>
      </div>
    </div>



  </div>
</template>

<script setup>
import { reactive, ref, onMounted, inject, defineProps, computed, watch, nextTick } from 'vue'
import axios from 'axios'

const showExportModal = ref(false)
const exportTemplateInfo = reactive({
  templateDir: '',
  reportBaseName: '非核子法原位密度报告表',
  reportTemplates: [],
  resultBaseName: '非核子法原位密度结果表',
  resultTemplates: [],
  formats: ['xlsx']
})

const getExportTemplateBaseNames = () => {
  const methodType = detectDensityMethodType(formData.testMethod, formData.testCategory)
  const isNuclear = methodType === 'nuclear'
  return {
    reportBaseName: isNuclear ? '核子法原位密度报告表' : '非核子法原位密度报告表',
    resultBaseName: isNuclear ? '核子法原位密度结果表' : '非核子法原位密度结果表'
  }
}

const hasTemplate = (templates, fmt) => {
  const ext = String(fmt || '').toLowerCase()
  if (!ext) return false
  return Array.isArray(templates) && templates.some(t => String(t?.ext || '').toLowerCase() === ext)
}

const openExportModal = async () => {
  try {
    const names = getExportTemplateBaseNames()
    exportTemplateInfo.reportBaseName = names.reportBaseName
    exportTemplateInfo.resultBaseName = names.resultBaseName

    const [reportRes, resultRes] = await Promise.all([
      axios.get('/api/density-test/export-formats', { params: { baseName: exportTemplateInfo.reportBaseName } }),
      axios.get('/api/density-test/export-formats', { params: { baseName: exportTemplateInfo.resultBaseName } })
    ])

    const reportOk = reportRes.data && reportRes.data.success
    const resultOk = resultRes.data && resultRes.data.success
    if (reportOk) {
      exportTemplateInfo.templateDir = reportRes.data.templateDir || (resultOk ? resultRes.data.templateDir : '') || ''
      exportTemplateInfo.reportBaseName = reportRes.data.baseName || exportTemplateInfo.reportBaseName
      exportTemplateInfo.reportTemplates = Array.isArray(reportRes.data.templates) ? reportRes.data.templates : []
      exportTemplateInfo.resultBaseName = resultOk ? (resultRes.data.baseName || exportTemplateInfo.resultBaseName) : exportTemplateInfo.resultBaseName
      exportTemplateInfo.resultTemplates = resultOk && Array.isArray(resultRes.data.templates) ? resultRes.data.templates : []
      showExportModal.value = true
    } else {
      const msg = (reportRes.data && reportRes.data.message) || (resultRes.data && resultRes.data.message) || '获取可导出格式失败'
      alert(msg)
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
  const hasReport = hasTemplate(exportTemplateInfo.reportTemplates, ext)
  const hasResult = hasTemplate(exportTemplateInfo.resultTemplates, ext)
  const needsResult = (resultPages.value && resultPages.value.length > 0)
  if (!hasReport || (needsResult && !hasResult)) {
    const dir = exportTemplateInfo.templateDir ? `\n模板目录：${exportTemplateInfo.templateDir}` : ''
    const missing = [
      hasReport ? '' : `${exportTemplateInfo.reportBaseName}.${ext}`,
      !needsResult || hasResult ? '' : `${exportTemplateInfo.resultBaseName}.${ext}`
    ].filter(Boolean).join('、')
    alert(`未找到模板：${missing}\n请先将对应模板文件放入“表”文件夹后再导出。${dir}`)
    return
  }
  showExportModal.value = false
  await exportByFormat(ext)
}

const props = defineProps({
  id: {
    type: String,
    required: false
  },
  wtNum: {
    type: String,
    required: false
  }
})

const navigateTo = inject('navigateTo')
const currentIndex = ref(0)
const resultPages = ref([])
const resultPageFormData = ref([])
const containerRef = ref(null)

const goToList = () => {
  if (navigateTo) {
    navigateTo('DensityTestReportList')
  }
}

const pdfForm = ref(null)

const formData = reactive({
  id: '',
  entrustmentId: '',
  status: 0,
  client: '',
  clientUnit: '',
  unifiedNumber: '',
  projectName: '',
  commissionDate: '',
  constructionPart: '',
  testDate: '',
  sampleNameStatus: '',
  reportDate: '',
  equipment: '',
  testMethod: '',
  testBasis: '',
  testCategory: '',
  maxDryDensity: '',
  optimumMoisture: '',
  minDryDensity: '',
  designIndex: '',
  testResult: '',
  approver: '',
  recordReviewer: '',
  recordTester: '',
  filler: '',
  companyName: '',
  companyAddress: '',
  companyPhone: '',
  witness: '',
  witnessUnit: '',
  remarks: '附原位密度检测结果。\n见证人：\n见证单位：',
  reviewerSignature: '',
  testerSignature: '',
  approverSignature: ''
})

const formatDate = (d) => {
    if (!d) return ''
    const date = new Date(d)
    const year = date.getFullYear()
    const month = ('0' + (date.getMonth() + 1)).slice(-2)
    const day = ('0' + date.getDate()).slice(-2)
    return `${year}-${month}-${day}`
}

// 根据检测方法自动判断是否为“核子法”
const isNuclearMethod = computed(() => {
  const method = (formData.testMethod || formData.testCategory || '').toString()
  return method.includes('核子')
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
    case 4: return '审核通过待批准'
    case 5: return '审核通过待批准'
    case 6: return '已批准'
    case 7: return '驳回'
    // 报告表状态 (10-17)
    case 10: return '草稿'
    case 11: return '已提交待审核'
    case 12: return '已打回'
    case 13: return '待签字'
    case 14: return '审核通过待批准'
    case 15: return '审核通过待批准'
    case 16: return '已批准'
    case 17: return '驳回'
    // 结果表状态 (20-27)
    case 20: return '草稿'
    case 21: return '已提交待审核'
    case 22: return '已打回'
    case 23: return '待签字'
    case 24: return '审核通过待批准'
    case 25: return '审核通过待批准'
    case 26: return '已批准'
    case 27: return '驳回'
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
    case 0: return '#6c757d' // secondary
    case 1: return '#007bff' // primary
    case 2: return '#dc3545' // danger
    case 3: return '#ffc107' // warning
    case 4: return '#ff8c00' // orange
    case 5: return '#ff8c00' // orange
    case 6: return '#28a745' // success
    case 7: return '#dc3545' // danger
    // 报告表状态 (10-17)
    case 10: return '#6c757d' // secondary
    case 11: return '#007bff' // primary
    case 12: return '#dc3545' // danger
    case 13: return '#ffc107' // warning
    case 14: return '#ff8c00' // orange
    case 15: return '#ff8c00' // orange
    case 16: return '#28a745' // success
    case 17: return '#dc3545' // danger
    // 结果表状态 (20-27)
    case 20: return '#6c757d' // secondary
    case 21: return '#007bff' // primary
    case 22: return '#dc3545' // danger
    case 23: return '#ffc107' // warning
    case 24: return '#ff8c00' // orange
    case 25: return '#ff8c00' // orange
    case 26: return '#28a745' // success
    case 27: return '#dc3545' // danger
    default: return '#6c757d'
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
    if (formData.recordReviewer && user.username !== formData.recordReviewer && user.fullName !== formData.recordReviewer) {
      alert('您不是该单据的记录审核人 (' + formData.recordReviewer + ')，无权审核')
      return
    }
    // Auto sign Reviewer
    if (!formData.reviewerSignature) {
         try {
            const sigRes = await axios.post('/api/signature/get', { userAccount: user.username })
            if (sigRes.data.success && sigRes.data.data && sigRes.data.data.signatureBlob) {
                 formData.reviewerSignature = `data:image/png;base64,${sigRes.data.data.signatureBlob}`
            }
         } catch (e) {
            console.error('Auto sign error', e)
         }
    }
    if (formData.reviewerSignature) {
        signatureData = formData.reviewerSignature.replace(/^data:image\/\w+;base64,/, '')
    }
  } else if (action === 'REJECT') {
    if (formData.recordReviewer && user.username !== formData.recordReviewer) {
      alert('您不是该单据的记录审核人 (' + formData.recordReviewer + ')，无权退回')
      return
    }
  } else if (action === 'SIGN_REVIEW') {
    if (formData.recordReviewer && user.username !== formData.recordReviewer && user.fullName !== formData.recordReviewer) {
      alert('您不是该单据的记录审核人 (' + formData.recordReviewer + ')，无权签字')
      return
    }
    
    // Auto sign Reviewer if missing
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
  } else if (action === 'SIGN_APPROVE') {
    if (formData.approver && user.username !== formData.approver && user.fullName !== formData.approver) {
      alert('您不是该单据的批准人 (' + formData.approver + ')，无权签字')
      return
    }
    
    // Auto sign Approver if missing
    if (!formData.approverSignature) {
         try {
            const sigRes = await axios.post('/api/signature/get', { userAccount: user.username })
            if (sigRes.data.success && sigRes.data.data && sigRes.data.data.signatureBlob) {
                 formData.approverSignature = `data:image/png;base64,${sigRes.data.data.signatureBlob}`
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
    signatureData = formData.approverSignature.replace(/^data:image\/\w+;base64,/, '')
  }

  const request = {
    tableType: 'DENSITY_TEST', // Maps to handleDensityTest
    recordId: formData.id, // Assuming DensityTestReport ID matches DensityTest ID or is handled
    action: action,
    userAccount: user.username,
    signatureData: signatureData,
    nextHandler: '' // Optional
  }

  if (action === 'REJECT') {
    const reason = prompt('请输入打回原因:')
    if (!reason) return
    request.rejectReason = reason
  }

  try {
    const response = await axios.post('/api/workflow/handle', request)
    if (response.data.success) {
      // 如果是批准操作，保存签名到数据库
      if (action === 'SIGN_APPROVE') {
        await saveData()
      }
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

onMounted(() => {
  // Initialize dynamic fields for loop variable 'i_idx'
    // Please verify the loop count match the template
    for (let i_idx = 0; i_idx < 7; i_idx++) {
    formData['sampleId_' + i_idx] = ''
    formData['location_' + i_idx] = ''
    formData['compaction_' + i_idx] = ''
    formData['wetDensity2_' + i_idx] = ''
    formData['wetDensity_' + i_idx] = ''
    formData['dryDensity2_' + i_idx] = ''
    formData['moisture_' + i_idx] = ''
    formData['date_' + i_idx] = ''
    formData['moisture2_' + i_idx] = ''
    formData['dryDensity_' + i_idx] = ''
  }

  loadData()
  nextTick(() => {
    applyReadOnly()
  })
})

watch(currentIndex, () => {
  nextTick(() => {
    applyReadOnly()
  })
})

const applyReadOnly = () => {
  const root = containerRef.value
  if (!root) return
  root.querySelectorAll('input, textarea').forEach((el) => {
    const type = (el.getAttribute('type') || '').toLowerCase()
    if (type === 'checkbox' || type === 'radio' || type === 'file') {
      el.setAttribute('disabled', 'true')
      return
    }
    el.setAttribute('readonly', 'true')
  })
  root.querySelectorAll('select, button[contenteditable], [contenteditable="true"]').forEach((el) => {
    el.setAttribute('disabled', 'true')
  })
}

const prevPage = () => {
  if (currentIndex.value > 0) currentIndex.value--
}
const nextPage = () => {
  if (currentIndex.value < resultPages.value.length) currentIndex.value++
}
const currentResultFormData = computed(() => {
  if (currentIndex.value <= 0) return {}
  return resultPageFormData.value[currentIndex.value - 1] || {}
})
const isNuclearResultMethod = computed(() => {
  const v = currentResultFormData.value?.testMethod || currentResultFormData.value?.testCategory || formData.testMethod || formData.testCategory
  return String(v || '').includes('核子')
})

const normalizeEntrustmentKey = (v) => {
  const s = (v ?? '').toString().trim()
  if (!s) return ''
  if (s.startsWith('{') && s.endsWith('}')) {
    return s.slice(1, -1).trim()
  }
  return s
}

const fillHeaderFromEntrustment = async (unifiedNumber) => {
  const u = normalizeEntrustmentKey(unifiedNumber)
  if (!u) return
  try {
    const resp = await axios.get('/api/jc-core-wt-info/detail', { params: { unifiedNumber: u } })
    if (resp.data && resp.data.success && resp.data.data) {
      const e = resp.data.data
      if (!formData.unifiedNumber && e.wtNum) formData.unifiedNumber = e.wtNum
      if (!formData.projectName && e.projectName) formData.projectName = e.projectName
      if (!formData.clientUnit && e.clientUnit) formData.clientUnit = e.clientUnit
      if (!formData.commissionDate && e.commissionDate) formData.commissionDate = formatDate(e.commissionDate)
      if (!formData.constructionPart && e.constructionPart) formData.constructionPart = e.constructionPart
      if (!formData.testCategory && e.testCategory) formData.testCategory = e.testCategory
      if (!formData.testDate && e.testDate) formData.testDate = formatDate(e.testDate)
      if (!formData.witness && e.witness) formData.witness = e.witness
      if (!formData.witnessUnit && e.witnessUnit) formData.witnessUnit = e.witnessUnit
      if (!formData.sampleNameStatus && (e.sampleName || e.sampleStatus)) {
        const eSampleName = e.sampleName || ''
        const eSampleStatus = e.sampleStatus || ''
        formData.sampleNameStatus = eSampleName && eSampleStatus ? `${eSampleName}，${eSampleStatus}` : (eSampleName || eSampleStatus || '')
      }
    }
  } catch (e) {
  }
}

const normalizeSignatureSrc = (v) => {
  if (!v) return ''
  const s = String(v).trim()
  if (!s) return ''
  if (s.startsWith('data:image')) return s
  if (/^[A-Za-z0-9+/=]+$/.test(s)) return `data:image/png;base64,${s}`
  return s
}

const applyWitnessToRemarks = () => {
  const w = (formData.witness ?? '').toString().trim()
  const wu = (formData.witnessUnit ?? '').toString().trim()
  if (!w && !wu) return
  const raw = (formData.remarks ?? '').toString()
  const lines = raw.split(/\r?\n/)
  const upsert = (label, value) => {
    if (!value) return
    const idx = lines.findIndex(l => l.trim().startsWith(label))
    if (idx === -1) {
      lines.push(label + value)
      return
    }
    const current = lines[idx].trim().slice(label.length).trim()
    if (!current) lines[idx] = label + value
  }
  upsert('见证人：', w)
  upsert('见证单位：', wu)
  formData.remarks = lines.join('\n')
}

const fillReportRemarksFromParsed = (parsed) => {
  const isFilled = (v) => v !== undefined && v !== null && String(v).trim() !== ''
  if (!parsed) return

  const looksLikePlaceholder = (v) => {
    const s = (v ?? '').toString()
    if (!s.trim()) return true
    const lines = s.split(/\r?\n/).map(l => l.trim()).filter(Boolean)
    if (lines.length === 0) return true
    const hasMain = lines.some(l => l.includes('附原位密度检测结果'))
    const hasWitness = lines.some(l => l.startsWith('见证人：'))
    const hasWitnessUnit = lines.some(l => l.startsWith('见证单位：'))
    if (!hasMain) return false
    if (!hasWitness && !hasWitnessUnit) return false
    return lines.length <= 3
  }

  const current = formData.remarks
  const canOverwrite = !isFilled(current) || looksLikePlaceholder(current)
  if (!canOverwrite) return

  const candidate = parsed.remarks
  if (isFilled(candidate)) {
    formData.remarks = String(candidate)
    return
  }

  const uniq = new Set()
  for (let i = 0; i < 20; i++) {
    const v = parsed['remarks_' + i]
    if (!isFilled(v)) continue
    uniq.add(String(v).trim())
    if (uniq.size > 1) break
  }
  if (uniq.size === 1) {
    formData.remarks = Array.from(uniq)[0]
  }
}

const fetchNuclearRecordsForKey = async (key) => {
  const primary = normalizeEntrustmentKey(key)
  if (!primary) return []

  const sortPages = (list) => {
    const toTime = (v) => {
      if (!v) return Number.POSITIVE_INFINITY
      const t = new Date(v).getTime()
      return Number.isFinite(t) ? t : Number.POSITIVE_INFINITY
    }
    return (Array.isArray(list) ? list : []).slice().sort((a, b) => {
      const ta = toTime(a?.createTime)
      const tb = toTime(b?.createTime)
      if (ta !== tb) return ta - tb
      const ua = toTime(a?.updateTime)
      const ub = toTime(b?.updateTime)
      if (ua !== ub) return ua - ub
      return String(a?.id || '').localeCompare(String(b?.id || ''))
    })
  }

  try {
    const r1 = await axios.get('/api/nuclear-density/get-by-entrustment-id', { params: { entrustmentId: primary } })
    if (r1.data && r1.data.success && Array.isArray(r1.data.data) && r1.data.data.length > 0) {
      return sortPages(r1.data.data)
    }
  } catch (e) {
  }

  try {
    const wtResp = await axios.get('/api/jc-core-wt-info/detail', { params: { unifiedNumber: primary } })
    const wtInfo = wtResp?.data?.data
    const wtId = normalizeEntrustmentKey(wtInfo?.id)
    if (wtId && wtId !== primary) {
      const r2 = await axios.get('/api/nuclear-density/get-by-entrustment-id', { params: { entrustmentId: wtId } })
      if (r2.data && r2.data.success && Array.isArray(r2.data.data)) {
        return sortPages(r2.data.data || [])
      }
    }
  } catch (e) {
  }

  return []
}

const detectDensityMethodType = (testMethod, testCategory) => {
  const m = (testMethod || testCategory || '').toString()
  if (m.includes('核子')) return 'nuclear'
  if (m.includes('灌砂')) return 'sand'
  if (m.includes('灌水')) return 'water'
  if (m.includes('环刀')) return 'ring'
  return ''
}

const sortByTimeDesc = (list) => {
  const toTime = (v) => {
    if (!v) return Number.NEGATIVE_INFINITY
    const t = new Date(v).getTime()
    return Number.isFinite(t) ? t : Number.NEGATIVE_INFINITY
  }
  return (Array.isArray(list) ? list : []).slice().sort((a, b) => {
    const ua = toTime(a?.updateTime)
    const ub = toTime(b?.updateTime)
    if (ua !== ub) return ub - ua
    const ta = toTime(a?.createTime)
    const tb = toTime(b?.createTime)
    if (ta !== tb) return tb - ta
    return String(b?.id || '').localeCompare(String(a?.id || ''))
  })
}

const pickApprovedRecord = (list) => {
  const sorted = sortByTimeDesc(list)
  const approved = sorted.filter(r => {
    const s = parseInt(r?.status)
    return s === 4 || s === 5 || s === 6
  })
  return approved[0] || sorted[0]
}

const fetchSimpleRecordsForKey = async (apiPath, key) => {
  const primary = normalizeEntrustmentKey(key)
  if (!primary) return []

  try {
    const r1 = await axios.get(apiPath, { params: { entrustmentId: primary } })
    if (r1.data && r1.data.success && Array.isArray(r1.data.data) && r1.data.data.length > 0) {
      return sortByTimeDesc(r1.data.data)
    }
  } catch (e) {
  }

  try {
    const wtResp = await axios.get('/api/jc-core-wt-info/detail', { params: { unifiedNumber: primary } })
    const wtInfo = wtResp?.data?.data
    const wtId = normalizeEntrustmentKey(wtInfo?.id)
    if (wtId && wtId !== primary) {
      const r2 = await axios.get(apiPath, { params: { entrustmentId: wtId } })
      if (r2.data && r2.data.success && Array.isArray(r2.data.data)) {
        return sortByTimeDesc(r2.data.data || [])
      }
    }
  } catch (e) {
  }

  return []
}

const fetchSandRecordsForKey = async (key) => {
  return fetchSimpleRecordsForKey('/api/sand-replacement/get-by-entrustment-id', key)
}

const fetchWaterRecordsForKey = async (key) => {
  return fetchSimpleRecordsForKey('/api/water-replacement/get-by-entrustment-id', key)
}

const fetchCuttingRingRecordsForKey = async (key) => {
  return fetchSimpleRecordsForKey('/api/cutting-ring/get-by-entrustment-id', key)
}

const safeJsonParse = (v) => {
  try {
    return JSON.parse(v || '{}')
  } catch (e) {
    return {}
  }
}

const appendRowsFromSandParsed = (parsed, out) => {
  if (!parsed) return
  for (let col = 0; col < 4; col++) {
    const idx1 = col * 2
    const idx2 = col * 2 + 1
    const row = {}
    const loc = parsed['location_' + col]
    if (loc !== undefined) row.location = loc
    if (parsed.testDate !== undefined) row.date = parsed.testDate
    const w1 = parsed['wetDensity_' + idx1]
    const w2 = parsed['wetDensity_' + idx2]
    const d1 = parsed['dryDensity_' + idx1]
    const d2 = parsed['dryDensity_' + idx2]
    const m1 = parsed['avgMoisture_' + idx1]
    const m2 = parsed['avgMoisture_' + idx2]
    const ad = parsed['avgDryDensity_' + col]
    const c = parsed['compaction_' + col]
    if (w1 !== undefined) row.wetDensity = w1
    if (w2 !== undefined) row.wetDensity2 = w2
    if (d1 !== undefined) row.dryDensity = d1
    if (d2 !== undefined) row.dryDensity2 = d2
    if (m1 !== undefined) row.moisture = m1
    if (m2 !== undefined) row.moisture2 = m2
    if (ad !== undefined) row.avgDryDensity = ad
    if (c !== undefined) row.compaction = c
    out.push(row)
  }
}

const appendRowsFromWaterParsed = (parsed, out) => {
  if (!parsed) return
  const total = Number(parsed.totalPages || 1) || 1
  for (let page = 0; page < total; page++) {
    for (let col = 0; col < 4; col++) {
      const idx1 = col * 2
      const idx2 = col * 2 + 1
      const row = {}
      const loc = parsed[`samplingLocation_page${page}_${col}`]
      if (loc !== undefined) row.location = loc
      if (parsed.testDate !== undefined) row.date = parsed.testDate
      const w1 = parsed[`wetDensity_page${page}_${idx1}`]
      const w2 = parsed[`wetDensity_page${page}_${idx2}`]
      const d1 = parsed[`measuredDryDensity_page${page}_${idx1}`]
      const d2 = parsed[`measuredDryDensity_page${page}_${idx2}`]
      const m1 = parsed[`avgMoisture_page${page}_${idx1}`]
      const m2 = parsed[`avgMoisture_page${page}_${idx2}`]
      const ad = parsed[`avgMeasuredDryDensity_page${page}_${col}`]
      if (w1 !== undefined) row.wetDensity = w1
      if (w2 !== undefined) row.wetDensity2 = w2
      if (d1 !== undefined) row.dryDensity = d1
      if (d2 !== undefined) row.dryDensity2 = d2
      if (m1 !== undefined) row.moisture = m1
      if (m2 !== undefined) row.moisture2 = m2
      if (ad !== undefined) row.avgDryDensity = ad
      out.push(row)
    }
  }
}

const appendRowsFromCuttingRingParsed = (parsed, out) => {
  if (!parsed) return
  const total = Number(parsed.totalPages || 1) || 1
  for (let page = 0; page < total; page++) {
    for (let i = 0; i < 4; i++) {
      const row = {}
      const sid = parsed[`sampleNo_page${page}_${i}`]
      const loc = parsed[`location_page${page}_${i}`]
      const w1 = parsed[`wetDensity1_page${page}_${i}`]
      const w2 = parsed[`wetDensity2_page${page}_${i}`]
      const d1 = parsed[`dryDensity1_page${page}_${i}`]
      const d2 = parsed[`dryDensity2_page${page}_${i}`]
      const m1 = parsed[`avgMoisture1_page${page}_${i}`]
      const m2 = parsed[`avgMoisture2_page${page}_${i}`]
      const ad = parsed[`avgDryDensity1_page${page}_${i}`]
      const c = parsed[`compaction1_page${page}_${i}`]
      if (sid !== undefined) row.sampleId = sid
      if (loc !== undefined) row.location = loc
      if (parsed.testDate !== undefined) row.date = parsed.testDate
      if (w1 !== undefined) row.wetDensity = w1
      if (w2 !== undefined) row.wetDensity2 = w2
      if (d1 !== undefined) row.dryDensity = d1
      if (d2 !== undefined) row.dryDensity2 = d2
      if (m1 !== undefined) row.moisture = m1
      if (m2 !== undefined) row.moisture2 = m2
      if (ad !== undefined) row.avgDryDensity = ad
      if (c !== undefined) row.compaction = c
      out.push(row)
    }
  }
}

const fillReportRowsFromRowData = (rows) => {
  const isFilled = (v) => v !== undefined && v !== null && String(v).trim() !== ''
  const max = Math.min(7, Array.isArray(rows) ? rows.length : 0)
  for (let i = 0; i < max; i++) {
    const r = rows[i] || {}
    const map = {
      sampleId: 'sampleId_',
      location: 'location_',
      date: 'date_',
      wetDensity: 'wetDensity_',
      wetDensity2: 'wetDensity2_',
      dryDensity: 'dryDensity_',
      dryDensity2: 'dryDensity2_',
      moisture: 'moisture_',
      moisture2: 'moisture2_',
      avgDryDensity: 'avgDryDensity_',
      compaction: 'compaction_'
    }
    Object.keys(map).forEach(k => {
      const v = r[k]
      if (v === undefined) return
      const targetKey = map[k] + i
      if (!isFilled(formData[targetKey])) {
        formData[targetKey] = v
      }
    })
  }
}

const fetchResultPages = async (unifiedNumber) => {
  const u = normalizeEntrustmentKey(unifiedNumber)
  if (!u) {
    resultPages.value = []
    resultPageFormData.value = []
    return
  }

  try {
    const isFilled = (v) => v !== undefined && v !== null && String(v).trim() !== ''

    const toParsed = (r) => {
      try {
        const parsed = JSON.parse((r && r.dataJson) ? r.dataJson : '{}')
        if (!parsed.constructionPart && formData.constructionPart) parsed.constructionPart = formData.constructionPart
        if (!parsed.testMethod && formData.testMethod) parsed.testMethod = formData.testMethod
        if (!parsed.testCategory && formData.testCategory) parsed.testCategory = formData.testCategory
        if (!parsed.unifiedNumber && formData.unifiedNumber) parsed.unifiedNumber = formData.unifiedNumber
        return parsed
      } catch (e) {
        return {}
      }
    }

    const rowFields = ['sampleId', 'location', 'date', 'wetDensity', 'dryDensity', 'moisture', 'avgDryDensity', 'compaction', 'wetDensity2', 'dryDensity2', 'moisture2', 'remarks']
    const allRowData = []
    let sourceFirstParsed = {}
    let firstPageHeadRows = {}
    const sameTrim = (a, b) => String(a ?? '').trim() === String(b ?? '').trim()
    const methodType = detectDensityMethodType(formData.testMethod, formData.testCategory)

    if (methodType === 'nuclear') {
      const nuclearPages = await fetchNuclearRecordsForKey(u)
      nuclearPages.forEach((nRecord, pageIndex) => {
        if (!nRecord || !nRecord.dataJson) return
        const nParsed = safeJsonParse(nRecord.dataJson)
        if (pageIndex === 0) {
          for (let i = 7; i < 20; i++) {
            const rowData = {}
            rowFields.forEach(field => {
              const v = nParsed[field + '_' + i]
              if (v !== undefined) rowData[field] = v
            })
            allRowData.push(rowData)
          }
        } else {
          for (let i = 0; i < 20; i++) {
            const rowData = {}
            rowFields.forEach(field => {
              const v = nParsed[field + '_' + i]
              if (v !== undefined) rowData[field] = v
            })
            allRowData.push(rowData)
          }
        }
      })
      sourceFirstParsed = nuclearPages[0]?.dataJson ? safeJsonParse(nuclearPages[0].dataJson) : {}
      for (let idx = 0; idx < 7; idx++) {
        rowFields.forEach(f => {
          const k = f + '_' + idx
          if (sourceFirstParsed[k] !== undefined) firstPageHeadRows[k] = sourceFirstParsed[k]
        })
      }
    } else if (methodType === 'sand') {
      const list = await fetchSandRecordsForKey(u)
      const picked = pickApprovedRecord(list)
      const parsed = safeJsonParse(picked?.dataJson)
      sourceFirstParsed = parsed
      appendRowsFromSandParsed(parsed, allRowData)
    } else if (methodType === 'water') {
      const list = await fetchWaterRecordsForKey(u)
      const picked = pickApprovedRecord(list)
      const parsed = safeJsonParse(picked?.dataJson)
      sourceFirstParsed = parsed
      appendRowsFromWaterParsed(parsed, allRowData)
    } else if (methodType === 'ring') {
      const list = await fetchCuttingRingRecordsForKey(u)
      const picked = pickApprovedRecord(list)
      const parsed = safeJsonParse(picked?.dataJson)
      sourceFirstParsed = parsed
      appendRowsFromCuttingRingParsed(parsed, allRowData)
    }

    const derivedPages = []
    if (allRowData.some(row => Object.values(row).some(isFilled))) {
      const baseCommon = {}
      Object.keys(sourceFirstParsed || {}).forEach(k => {
        if (k.includes('_page')) return
        if (k.match(/_\d+$/)) return
        baseCommon[k] = sourceFirstParsed[k]
      })
      if (!baseCommon.constructionPart && formData.constructionPart) baseCommon.constructionPart = formData.constructionPart
      if (!baseCommon.testMethod && formData.testMethod) baseCommon.testMethod = formData.testMethod
      if (!baseCommon.testCategory && formData.testCategory) baseCommon.testCategory = formData.testCategory
      if (!baseCommon.unifiedNumber && formData.unifiedNumber) baseCommon.unifiedNumber = formData.unifiedNumber

      const pageSize = 20
      const totalPages = Math.ceil(allRowData.length / pageSize)
      for (let page = 0; page < totalPages; page++) {
        const startIdx = page * pageSize
        const endIdx = Math.min(startIdx + pageSize, allRowData.length)
        const pageData = allRowData.slice(startIdx, endIdx)
        const pageForm = { ...baseCommon }
        pageData.forEach((row, idx) => {
          rowFields.forEach(f => {
            if (row[f] !== undefined) pageForm[f + '_' + idx] = row[f]
          })
        })
        derivedPages.push(pageForm)
      }
    }

    const res = await axios.get('/api/density-test/get-by-entrustment-id', { params: { entrustmentId: u } })
    const existing = (res.data && res.data.success && Array.isArray(res.data.data)) ? res.data.data : []
    const existingForms = existing.map(toParsed)

    const mergedLength = Math.max(existingForms.length, derivedPages.length)
    const mergedPages = []
    for (let i = 0; i < mergedLength; i++) {
      const base = derivedPages[i] ? { ...derivedPages[i] } : {}
      const overlay = existingForms[i] || {}
      Object.keys(overlay).forEach(k => {
        if (!isFilled(overlay[k])) return
        if (i === 0 && firstPageHeadRows[k] !== undefined && sameTrim(overlay[k], firstPageHeadRows[k])) return
        base[k] = overlay[k]
      })
      if (!base.constructionPart && formData.constructionPart) base.constructionPart = formData.constructionPart
      if (!base.testMethod && formData.testMethod) base.testMethod = formData.testMethod
      if (!base.testCategory && formData.testCategory) base.testCategory = formData.testCategory
      if (!base.unifiedNumber && formData.unifiedNumber) base.unifiedNumber = formData.unifiedNumber
      mergedPages.push(base)
    }

    if (mergedPages.length > 0) {
      resultPages.value = Array.from({ length: mergedPages.length }, (_, i) => {
        const r = existing[i]
        if (r) return r
        return { id: '', entrustmentId: u, dataJson: '{}' }
      })
      resultPageFormData.value = mergedPages
      if (currentIndex.value > resultPages.value.length) currentIndex.value = 0
      return
    }

    if (existing.length > 0) {
      resultPages.value = existing
      resultPageFormData.value = existingForms
      if (currentIndex.value > resultPages.value.length) currentIndex.value = 0
      return
    }
  } catch (e) {
  }

  resultPages.value = []
  resultPageFormData.value = []
  if (currentIndex.value > 0) currentIndex.value = 0
}

  const loadData = async () => {
  // 这里的 id 来自列表的 item.id（委托 WT_ID），wtNum 是统一编号 XT-2024-54301
  // 原位密度报告/结果的 ENTRUSTMENT_ID 我们现在统一用“统一编号”存，所以优先用 wtNum 作为 key
  const key = props.wtNum || props.id
  if (key) {
    try {
      currentIndex.value = 0
      const response = await axios.get('/api/density-test/report/get-by-entrustment-id', {
        params: { entrustmentId: key }
      })

      if (response.data.success && response.data.data) {
        const data = response.data.data
        if (data.dataJson) {
          const parsed = JSON.parse(data.dataJson)
          Object.assign(formData, parsed)

          // Map legacy fields to new record fields
          if (!formData.recordTester && parsed.tester) formData.recordTester = parsed.tester
          if (!formData.recordReviewer && parsed.reviewer) formData.recordReviewer = parsed.reviewer
          // 统一编号：recordData 里用 wtNum 存储，这里回填到 unifiedNumber
          if (!formData.unifiedNumber && parsed.wtNum) {
            formData.unifiedNumber = parsed.wtNum
          }
          
          // 从record对象中获取字段值，优先于dataJson
          if (data.projectName) formData.projectName = data.projectName
          if (data.commissionDate) formData.commissionDate = new Date(data.commissionDate).toISOString().split('T')[0]
          if (data.constructionPart) formData.constructionPart = data.constructionPart
          if (data.testCategory) formData.testCategory = data.testCategory
          if (data.clientUnit) formData.clientUnit = data.clientUnit
          
          // 兼容旧数据：如果 JSON 里只有 entrustingUnit 或 clientUnit，就统一回填到 clientUnit，用于“委托单位”显示
          if (!formData.clientUnit && parsed.entrustingUnit) {
            formData.clientUnit = parsed.entrustingUnit
          }
          if (!formData.clientUnit && parsed.clientUnit) {
            formData.clientUnit = parsed.clientUnit
          }

          // 兼容灌砂法等记录表的字段差异：
          // 1）最优含水率：记录表使用 optMoisture，这里需要填到 optimumMoisture
          if (!formData.optimumMoisture && parsed.optMoisture !== undefined) {
            formData.optimumMoisture = parsed.optMoisture
          }
          
          // 2）环刀法字段映射：sampleNo_ -> sampleId_, moisture1_ -> moisture_
          for (let i = 0; i < 20; i++) {
            // 样品编号映射
            if (!formData['sampleId_' + i] && parsed['sampleNo_' + i] !== undefined && parsed['sampleNo_' + i] !== null && parsed['sampleNo_' + i] !== '') {
              formData['sampleId_' + i] = parsed['sampleNo_' + i]
            }
            // 含水率映射（环刀法使用 moisture1_ 作为第一行含水率，报告表使用 moisture_）
            // moisture2_ 不需要映射，直接使用
            if (!formData['moisture_' + i] && parsed['moisture1_' + i] !== undefined && parsed['moisture1_' + i] !== null && parsed['moisture1_' + i] !== '') {
              formData['moisture_' + i] = parsed['moisture1_' + i]
            }
          }
          // 2）干密度和湿密度：如果 DATA_JSON 里是原始结构（有 avgDryDensity_i 但没有 dryDensity2_i），需要重排
          const hasAvgDry = Object.keys(parsed).some(k => k.startsWith('avgDryDensity_'))
          const hasDry2 = Object.keys(parsed).some(k => k.startsWith('dryDensity2_'))
          if (hasAvgDry && !hasDry2) {
            // 先清除 Object.assign 可能已经复制过来的所有干密度和湿密度值（避免重复显示）
            // 清除范围：dryDensity_0..7, dryDensity2_0..7, wetDensity_0..7, wetDensity2_0..7
            for (let i = 0; i < 8; i++) {
              delete formData['dryDensity_' + i]
              delete formData['dryDensity2_' + i]
              delete formData['wetDensity_' + i]
              delete formData['wetDensity2_' + i]
            }
            // 收集记录表里的纵向 dryDensity_0..7
            const sandDry = []
            for (let i = 0; i < 8; i++) {
              const v = parsed['dryDensity_' + i]
              if (v !== undefined && v !== null && v !== '') {
                sandDry.push(v)
              }
            }
            // 每两个值一组，映射到同一个检测部位的两行干密度
            // 记录表：dryDensity_0="干1", dryDensity_1="干2", dryDensity_2="干3", dryDensity_3="干4"
            // 报告：测试部位1 → dryDensity_0="干1", dryDensity2_0="干2"
            //      测试部位2 → dryDensity_1="干3", dryDensity2_1="干4"
            for (let row = 0; row < 4; row++) {
              const v1 = sandDry[row * 2]      // 第一行
              const v2 = sandDry[row * 2 + 1] // 第二行
              if (v1 !== undefined) formData['dryDensity_' + row] = v1
              if (v2 !== undefined) formData['dryDensity2_' + row] = v2
            }
            // 湿密度也需要同样的重排
            const sandWet = []
            for (let i = 0; i < 8; i++) {
              const v = parsed['wetDensity_' + i]
              if (v !== undefined && v !== null && v !== '') {
                sandWet.push(v)
              }
            }
            for (let row = 0; row < 4; row++) {
              const v1 = sandWet[row * 2]      // 第一行
              const v2 = sandWet[row * 2 + 1] // 第二行
              if (v1 !== undefined) formData['wetDensity_' + row] = v1
              if (v2 !== undefined) formData['wetDensity2_' + row] = v2
            }
          }
          
          // 3）处理新格式的多页数据（带 _page 后缀的字段）
          const hasPageData = Object.keys(parsed).some(k => k.includes('_page'))
          if (hasPageData) {
            // 收集所有页面的样品数据
            const pageData = []
            let sampleIndex = 0
            
            // 遍历所有页面
            const pageIndices = new Set()
            Object.keys(parsed).forEach(key => {
              const match = key.match(/_page(\d+)_/)
              if (match) {
                pageIndices.add(parseInt(match[1]))
              }
            })
            
            // 按页面顺序处理
            const sortedPages = Array.from(pageIndices).sort((a, b) => a - b)
            sortedPages.forEach(pageIndex => {
              // 处理每页的5个样品
              for (let i = 0; i < 5; i++) {
                if (sampleIndex >= 7) break // 报告表最多显示7个样品
                
                const pagePrefix = '_page' + pageIndex + '_' + i
                
                // 样品编号
                const sampleNoKey = 'sampleNo' + pagePrefix
                if (parsed[sampleNoKey] !== undefined && parsed[sampleNoKey] !== null && parsed[sampleNoKey] !== '') {
                  formData['sampleId_' + sampleIndex] = parsed[sampleNoKey]
                }
                
                // 检测部位
                const locationKey = 'location' + pagePrefix
                if (parsed[locationKey] !== undefined && parsed[locationKey] !== null && parsed[locationKey] !== '') {
                  formData['location_' + sampleIndex] = parsed[locationKey]
                }
                
                // 湿密度
                const wetDensityKey = 'wetDensity' + pagePrefix
                if (parsed[wetDensityKey] !== undefined && parsed[wetDensityKey] !== null && parsed[wetDensityKey] !== '') {
                  formData['wetDensity_' + sampleIndex] = parsed[wetDensityKey]
                }
                
                // 干密度
                const dryDensityKey = 'dryDensity' + pagePrefix
                if (parsed[dryDensityKey] !== undefined && parsed[dryDensityKey] !== null && parsed[dryDensityKey] !== '') {
                  formData['dryDensity_' + sampleIndex] = parsed[dryDensityKey]
                }
                
                // 含水率
                const moistureKey = 'moisture1' + pagePrefix
                if (parsed[moistureKey] !== undefined && parsed[moistureKey] !== null && parsed[moistureKey] !== '') {
                  formData['moisture_' + sampleIndex] = parsed[moistureKey]
                }
                
                // 压实度
                const compactionKey = 'compaction' + pagePrefix
                if (parsed[compactionKey] !== undefined && parsed[compactionKey] !== null && parsed[compactionKey] !== '') {
                  formData['compaction_' + sampleIndex] = parsed[compactionKey]
                }
                
                // 检测日期
                if (parsed.testDate) {
                  formData['date_' + sampleIndex] = parsed.testDate
                }
                
                sampleIndex++
              }
            })
          }
        }
        formData.id = data.id
        formData.status = data.status !== undefined ? data.status : 0
        formData.entrustmentId = data.entrustmentId
        formData.reviewerSignature = normalizeSignatureSrc(data.reviewSignaturePhoto || '')
        formData.testerSignature = normalizeSignatureSrc(data.inspectSignaturePhoto || '')
        formData.approverSignature = normalizeSignatureSrc(data.approveSignaturePhoto || '')

        await fillHeaderFromEntrustment(formData.unifiedNumber || key)

        const isFilled = (v) => v !== undefined && v !== null && String(v).trim() !== ''
        const hasAnyRow = Array.from({ length: 7 }, (_, i) => (
          isFilled(formData['sampleId_' + i]) ||
          isFilled(formData['location_' + i]) ||
          isFilled(formData['wetDensity_' + i]) ||
          isFilled(formData['dryDensity_' + i]) ||
          isFilled(formData['moisture_' + i]) ||
          isFilled(formData['compaction_' + i])
        )).some(Boolean)

        const nuclearKey = formData.unifiedNumber || props.wtNum || key
        if (nuclearKey && !hasAnyRow) {
          try {
            const nuclearList = await fetchNuclearRecordsForKey(nuclearKey)
            if (nuclearList && nuclearList.length > 0) {
              const pickScore = (r) => {
                try {
                  const p = JSON.parse(r?.dataJson || '{}')
                  let c = 0
                  for (let i = 0; i < 20; i++) {
                    const v = p['sampleId_' + i]
                    if (v !== undefined && v !== null && String(v).trim() !== '') c++
                  }
                  return c
                } catch (e) {
                  return 0
                }
              }
              const nRecord = nuclearList
                .filter(r => r && r.dataJson)
                .sort((a, b) => pickScore(b) - pickScore(a))[0] || nuclearList[0]
              const firstPageRecord = nuclearList[0]
              const bestRecord = nRecord
              if (bestRecord && bestRecord.dataJson) {
                const nParsed = JSON.parse(bestRecord.dataJson)
                const commonFields = ['entrustingUnit', 'unifiedNumber', 'projectName', 'commissionDate', 'constructionPart', 'testCategory', 'testMethod', 'equipment', 'sampleNameStatus', 'standard', 'designCompaction', 'maxDryDensity', 'optimumMoisture', 'minDryDensity']
                commonFields.forEach(field => {
                  if (!isFilled(formData[field]) && isFilled(nParsed[field])) {
                    formData[field] = field === 'commissionDate'
                      ? new Date(nParsed[field]).toISOString().split('T')[0]
                      : nParsed[field]
                  }
                })
                fillReportRemarksFromParsed(nParsed)
                if (!isFilled(formData.testMethod) && String(formData.testCategory || '').includes('核子')) {
                  formData.testMethod = '核子法'
                }
                if (!isFilled(formData.testBasis)) {
                  const basis = nParsed.testBasis !== undefined ? nParsed.testBasis : nParsed.standard
                  if (isFilled(basis)) formData.testBasis = basis
                }
                if (!isFilled(formData.designIndex) && isFilled(nParsed.designCompaction)) {
                  formData.designIndex = nParsed.designCompaction
                }
                if (!isFilled(formData.witness) && bestRecord.witness) formData.witness = bestRecord.witness
                if (!isFilled(formData.witnessUnit) && bestRecord.witnessUnit) formData.witnessUnit = bestRecord.witnessUnit
                if (!isFilled(formData.recordTester) && bestRecord.recordTester) formData.recordTester = bestRecord.recordTester
                if (!isFilled(formData.recordReviewer) && bestRecord.recordReviewer) formData.recordReviewer = bestRecord.recordReviewer
                if (!isFilled(formData.testerSignature) && bestRecord.inspectSignaturePhoto) {
                  formData.testerSignature = normalizeSignatureSrc(bestRecord.inspectSignaturePhoto)
                }
                if (!isFilled(formData.reviewerSignature) && bestRecord.reviewSignaturePhoto) {
                  formData.reviewerSignature = normalizeSignatureSrc(bestRecord.reviewSignaturePhoto)
                }
              }

              if (firstPageRecord && firstPageRecord.dataJson) {
                try {
                  const firstParsed = JSON.parse(firstPageRecord.dataJson)
                  const rowFields = ['sampleId', 'location', 'date', 'wetDensity', 'dryDensity', 'moisture', 'compaction']
                  for (let i = 0; i < 7; i++) {
                    rowFields.forEach(field => {
                      const k = field + '_' + i
                      if (!isFilled(formData[k]) && isFilled(firstParsed[k])) {
                        formData[k] = firstParsed[k]
                      }
                    })
                  }
                  fillReportRemarksFromParsed(firstParsed)
                } catch (e) {
                }
              }
            }
          } catch (e) {
            console.error('density report nuclear autofill fallback error', e)
          }
        }

        if (!isFilled(formData.reportDate)) {
          formData.reportDate = formatDate(new Date())
        }
        applyWitnessToRemarks()
      } else {
        const entrustmentResponse = await axios.get('/api/jc-core-wt-info/detail', {
          params: { unifiedNumber: key }
        })
        if (entrustmentResponse.data.success) {
          const eData = entrustmentResponse.data.data
          formData.entrustmentId = key
          formData.unifiedNumber = eData.wtNum || key || ''
          formData.projectName = eData.projectName || ''
          // 委托单位：后端字段叫 clientUnit，这里既保存在 clientUnit，又映射到页面使用的 client
          formData.clientUnit = eData.clientUnit || ''
          formData.client = eData.clientUnit || ''
          formData.commissionDate = eData.commissionDate ? new Date(eData.commissionDate).toISOString().split('T')[0] : ''
          formData.constructionPart = eData.constructionPart || ''
          formData.testCategory = eData.testCategory || ''
          formData.witness = eData.witness || formData.witness || ''
          formData.witnessUnit = eData.witnessUnit || formData.witnessUnit || ''
          // 样品名称及状态：从委托单 sampleName + sampleStatus 拼接，中间用顿号隔开
          const eSampleName = eData.sampleName || ''
          const eSampleStatus = eData.sampleStatus || ''
          if (eSampleName || eSampleStatus) {
            formData.sampleNameStatus = eSampleName && eSampleStatus
              ? `${eSampleName}，${eSampleStatus}`
              : (eSampleName || eSampleStatus || '')
          }
          formData.testDate = new Date().toISOString().split('T')[0]
          
          // Directory fallback logic
          const directory = JSON.parse(localStorage.getItem('currentDirectory'))
          if (directory) {
             formData.recordTester = directory.jcTester || eData.tester || ''
             formData.recordReviewer = directory.jcReviewer || eData.reviewer || ''
             formData.approver = directory.bgApprover || eData.approver || ''
             formData.filler = directory.jcFiller || ''
          } else {
             if (eData.tester) formData.recordTester = eData.tester
             if (eData.reviewer) formData.recordReviewer = eData.reviewer
             if (eData.approver) formData.approver = eData.approver
          }

          try {
            const resultResponse = await axios.get('/api/density-test/get-by-entrustment-id', {
              params: { entrustmentId: formData.unifiedNumber }
            })
            if (resultResponse.data.success && resultResponse.data.data && resultResponse.data.data.length > 0) {
              const approvedRecords = resultResponse.data.data.filter(record => {
                const s = parseInt(record.status)
                return s === 4 || s === 5
              })
              if (approvedRecords.length > 0) {
                const record = approvedRecords[0]
              if (record.dataJson) {
                try {
                  const parsed = JSON.parse(record.dataJson)

                  // 1）汇总区字段：优先用标准键名，如果不存在则做兼容映射
                  const summaryKeys = ['maxDryDensity', 'optimumMoisture', 'minDryDensity', 'designIndex', 'testResult']
                  summaryKeys.forEach(k => {
                    if (parsed[k] !== undefined) formData[k] = parsed[k]
                  })
                  // 兼容灌砂法里使用的 optMoisture 字段名
                  if (!formData.optimumMoisture && parsed.optMoisture !== undefined) {
                    formData.optimumMoisture = parsed.optMoisture
                  }

                  // 2）判断是否是灌砂法记录
                  const isSandReplacement = parsed.testCategory === '灌砂法' || formData.testCategory === '灌砂法'
                  
                  // 3）明细行通用字段：sampleId/location/date/含水率/压实度等，直接复制
                  //    如果是灌砂法，跳过 wetDensity_（后面会专门重排）
                  for (let i = 0; i < 7; i++) {
                    const prefixes = [
                      'sampleId_',
                      'location_',
                      'date_',
                      'moisture_',
                      'compaction_',
                      'moisture2_'
                    ]
                    // 核子法等已有 wetDensity2 结构的，直接复制
                    if (!isSandReplacement) {
                      prefixes.push('wetDensity_', 'wetDensity2_')
                    }
                    prefixes.forEach(prefix => {
                      const key = prefix + i
                      if (parsed[key] !== undefined) formData[key] = parsed[key]
                    })
                  }

                  // 4）灌砂法专用：根据 dryDensity_0..7 和 wetDensity_0..7 重排为“每个检测部位两行”
                  //    约定：dryDensity_0=\"干1\", dryDensity_1=\"干2\", dryDensity_2=\"干3\", dryDensity_3=\"干4\" ...
                  //         报告：测试部位1 → dryDensity_0, dryDensity2_0；测试部位2 → dryDensity_1, dryDensity2_1
                  if (isSandReplacement) {
                    // 先清除可能存在的多余字段（避免重复显示）
                    for (let i = 4; i < 8; i++) {
                      delete formData['dryDensity_' + i]
                      delete formData['dryDensity2_' + i]
                      delete formData['wetDensity_' + i]
                      delete formData['wetDensity2_' + i]
                    }
                    // 重排干密度：每两个值一组，映射到同一个检测部位的两行
                    for (let row = 0; row < 4; row++) {
                      const idx1 = row * 2
                      const idx2 = row * 2 + 1
                      const v1 = parsed['dryDensity_' + idx1]
                      const v2 = parsed['dryDensity_' + idx2]
                      if (v1 !== undefined && v1 !== null && v1 !== '') {
                        formData['dryDensity_' + row] = v1
                      }
                      if (v2 !== undefined && v2 !== null && v2 !== '') {
                        formData['dryDensity2_' + row] = v2
                      }
                    }
                    // 重排湿密度：每两个值一组，映射到同一个检测部位的两行
                    for (let row = 0; row < 4; row++) {
                      const idx1 = row * 2
                      const idx2 = row * 2 + 1
                      const v1 = parsed['wetDensity_' + idx1]
                      const v2 = parsed['wetDensity_' + idx2]
                      if (v1 !== undefined && v1 !== null && v1 !== '') {
                        formData['wetDensity_' + row] = v1
                      }
                      if (v2 !== undefined && v2 !== null && v2 !== '') {
                        formData['wetDensity2_' + row] = v2
                      }
                    }
                  } else {
                    // 其他检测方法（如核子法）：直接复制 dryDensity_/dryDensity2_
                    for (let i = 0; i < 7; i++) {
                      const d1 = parsed['dryDensity_' + i]
                      const d2 = parsed['dryDensity2_' + i]
                      if (d1 !== undefined) formData['dryDensity_' + i] = d1
                      if (d2 !== undefined) formData['dryDensity2_' + i] = d2
                    }
                  }
                } catch (e) {
                  console.error('density report auto-fill parse error', e)
                }
              }
              } else {
                console.log('记录表状态未审核通过，不自动填充数据')
              }
            }
            
            {
              try {
                const nuclearList = await fetchNuclearRecordsForKey(formData.unifiedNumber)
                if (nuclearList && nuclearList.length > 0) {
                  const pickScore = (r) => {
                    try {
                      const p = JSON.parse(r?.dataJson || '{}')
                      let c = 0
                      for (let i = 0; i < 20; i++) {
                        const v = p['sampleId_' + i]
                        if (v !== undefined && v !== null && String(v).trim() !== '') c++
                      }
                      return c
                    } catch (e) {
                      return 0
                    }
                  }
                  const nRecord = nuclearList
                    .filter(r => r && r.dataJson)
                    .sort((a, b) => pickScore(b) - pickScore(a))[0] || nuclearList[0]

                  if (nRecord && nRecord.dataJson) {
                    const nParsed = JSON.parse(nRecord.dataJson)
                    const isFilled = (v) => v !== undefined && v !== null && String(v).trim() !== ''
                    const nuclearHasAny = pickScore(nRecord) > 0 ||
                      isFilled(nParsed.standard) ||
                      isFilled(nParsed.equipment) ||
                      isFilled(nParsed.testMethod)
                    if (!nuclearHasAny) {
                      throw new Error('nuclear record has no meaningful data')
                    }

                    const commonFields = ['entrustingUnit', 'unifiedNumber', 'projectName', 'commissionDate', 'constructionPart', 'testCategory', 'testMethod', 'equipment', 'sampleNameStatus', 'standard', 'designCompaction', 'maxDryDensity', 'optimumMoisture', 'minDryDensity']
                    commonFields.forEach(field => {
                      const topVal = nRecord[field]
                      const jsonVal = nParsed[field]
                      if (!isFilled(formData[field]) && isFilled(topVal)) {
                        formData[field] = field === 'commissionDate' ? new Date(topVal).toISOString().split('T')[0] : topVal
                      } else if (!isFilled(formData[field]) && isFilled(jsonVal)) {
                        formData[field] = field === 'commissionDate' ? new Date(jsonVal).toISOString().split('T')[0] : jsonVal
                      }
                    })
                    fillReportRemarksFromParsed(nParsed)
                    if (!isFilled(formData.testMethod)) formData.testMethod = '核子法'
                    if (!isFilled(formData.testBasis)) {
                      const basis = nParsed.testBasis !== undefined ? nParsed.testBasis : nParsed.standard
                      if (isFilled(basis)) formData.testBasis = basis
                    }
                    if (!isFilled(formData.designIndex) && isFilled(nParsed.designCompaction)) {
                      formData.designIndex = nParsed.designCompaction
                    }
                    if (!isFilled(formData.witness) && nRecord.witness) formData.witness = nRecord.witness
                    if (!isFilled(formData.witnessUnit) && nRecord.witnessUnit) formData.witnessUnit = nRecord.witnessUnit
                    if (!isFilled(formData.recordTester) && nRecord.recordTester) formData.recordTester = nRecord.recordTester
                    if (!isFilled(formData.recordReviewer) && nRecord.recordReviewer) formData.recordReviewer = nRecord.recordReviewer
                    if (!isFilled(formData.testerSignature) && nRecord.inspectSignaturePhoto) {
                      formData.testerSignature = normalizeSignatureSrc(nRecord.inspectSignaturePhoto)
                    }
                    if (!isFilled(formData.reviewerSignature) && nRecord.reviewSignaturePhoto) {
                      formData.reviewerSignature = normalizeSignatureSrc(nRecord.reviewSignaturePhoto)
                    }

                    if (!isFilled(formData.clientUnit) && isFilled(nRecord.clientUnit)) {
                      formData.clientUnit = nRecord.clientUnit
                    } else if (!isFilled(formData.clientUnit) && isFilled(nParsed.entrustingUnit)) {
                      formData.clientUnit = nParsed.entrustingUnit
                    }

                    const rowFields = ['sampleId', 'location', 'date', 'wetDensity', 'dryDensity', 'moisture', 'compaction']
                    for (let i = 0; i < 7; i++) {
                      rowFields.forEach(field => {
                        const k = field + '_' + i
                        if (!isFilled(formData[k]) && isFilled(nParsed[k])) {
                          formData[k] = nParsed[k]
                        }
                      })
                    }
                  }
                }
              } catch (e) {
                // ignore
              }
            }

            if (!formData.reportDate) {
              formData.reportDate = formatDate(new Date())
            }
            applyWitnessToRemarks()

            // 如果是灌砂法，尝试直接从灌砂法记录获取数据
            if (formData.testCategory && formData.testCategory.includes('灌砂')) {
                try {
                  const sandRes = await axios.get('/api/sand-replacement/get-by-entrustment-id', {
                     params: { entrustmentId: formData.unifiedNumber }
                  })
                  if (sandRes.data.success && sandRes.data.data && sandRes.data.data.length > 0) {
                     const sRecord = sandRes.data.data[0]
                     // 检查记录表状态是否为审核通过（状态值为5）
                     const sStatus = parseInt(sRecord.status)
                     if (sStatus === 4 || sStatus === 5) {
                         if (sRecord.dataJson) {
                             const sParsed = JSON.parse(sRecord.dataJson)
                             
                             // 1. 基础字段合并
                             // 注意：先不要直接 Object.assign 覆盖 dryDensity_X，因为灌砂法的 dryDensity_X 需要重排
                             // 我们先提取非数组字段
                             const { ...otherFields } = sParsed
                             // 排除掉 dryDensity_X, wetDensity_X 等数组字段，避免直接覆盖导致错乱？
                             // 其实直接 assign 也没关系，因为后面会重写。但是为了保险，我们可以先 assign。
                             Object.assign(formData, sParsed)
                             
                             // 2. 映射特殊字段
                             if (sParsed.optMoisture !== undefined) {
                                formData.optimumMoisture = sParsed.optMoisture
                             }

                             // 3. 灌砂法专用重排逻辑：将 8 个平铺的密度值映射到 4 个检测点（每个点 2 个值）

                             // 重排干密度
                             for (let row = 0; row < 4; row++) {
                                const idx1 = row * 2
                                const idx2 = row * 2 + 1
                                // 从 sParsed 中获取
                                const v1 = sParsed['dryDensity_' + idx1]
                                const v2 = sParsed['dryDensity_' + idx2]
                                
                                // 赋值给 formData 的 dryDensity_row 和 dryDensity2_row
                                if (v1 !== undefined && v1 !== null && v1 !== '') {
                                    formData['dryDensity_' + row] = v1
                                }
                                if (v2 !== undefined && v2 !== null && v2 !== '') {
                                    formData['dryDensity2_' + row] = v2
                                }
                             }

                             // 重排湿密度
                             for (let row = 0; row < 4; row++) {
                                const idx1 = row * 2
                                const idx2 = row * 2 + 1
                                const v1 = sParsed['wetDensity_' + idx1]
                                const v2 = sParsed['wetDensity_' + idx2]
                                
                                if (v1 !== undefined && v1 !== null && v1 !== '') {
                                    formData['wetDensity_' + row] = v1
                                }
                                if (v2 !== undefined && v2 !== null && v2 !== '') {
                                    formData['wetDensity2_' + row] = v2
                                }
                             }
                             
                             // 重排含水率 (moisture)
                             for (let row = 0; row < 4; row++) {
                                const idx1 = row * 2
                                const idx2 = row * 2 + 1
                                const v1 = sParsed['moisture_' + idx1]
                                const v2 = sParsed['moisture_' + idx2]
                                
                                if (v1 !== undefined && v1 !== null && v1 !== '') {
                                    formData['moisture_' + row] = v1
                                }
                                if (v2 !== undefined && v2 !== null && v2 !== '') {
                                    formData['moisture2_' + row] = v2
                                }
                             }
                         }
                     } else {
                         console.log('灌砂法记录表状态未审核通过，不自动填充数据')
                     }
                  }
                } catch (e) {
                   console.error('Failed to auto-fill from Sand Replacement Record', e)
                }
            }
          } catch (e) {
            console.error('density report auto-fill error', e)
          }
        }
      }

      if (!formData.reportDate) {
        formData.reportDate = formatDate(new Date())
      }
      if (!formData.testMethod && String(formData.testCategory || '').includes('核子')) {
        formData.testMethod = '核子法'
      }
      if (!formData.testBasis && formData.standard) {
        formData.testBasis = formData.standard
      }
      if (!formData.designIndex && formData.designCompaction) {
        formData.designIndex = formData.designCompaction
      }
      await fetchResultPages(formData.unifiedNumber || key)
      applyWitnessToRemarks()
    } catch (error) {
      console.error('Error loading data:', error)
    }
  }
}

const saveData = async (navigateBack = true) => {
  try {
    // 确保日期格式正确
    if (formData.commissionDate) {
      formData.commissionDate = formatDate(formData.commissionDate)
    }
    if (formData.testDate) {
      formData.testDate = formatDate(formData.testDate)
    }
    if (formData.reportDate) {
      formData.reportDate = formatDate(formData.reportDate)
    }
    // 确保数据行中的日期格式正确
    for (let i = 0; i < 7; i++) {
      if (formData['date_' + i]) {
        formData['date_' + i] = formatDate(formData['date_' + i])
      }
    }
    
    const dataToSave = {
      id: formData.id,
      // 报告表里的 ENTRUSTMENT_ID 统一使用“统一编号”存储，优先用 wtNum
      entrustmentId: formData.entrustmentId || props.wtNum || props.id,
      status: formData.status, // 传递状态字段给后端
      dataJson: JSON.stringify(formData),
      reviewSignaturePhoto: formData.reviewerSignature,
      inspectSignaturePhoto: formData.testerSignature,
      approveSignaturePhoto: formData.approverSignature,
      recordTester: formData.recordTester,
      recordReviewer: formData.recordReviewer,
      filler: formData.filler,
      approver: formData.approver
    }
    
    const response = await axios.post('/api/density-test/report/save', dataToSave)
    if (response.data.success) {
      alert('保存成功')
      if (!formData.id && response.data.data && response.data.data.id) {
           formData.id = response.data.data.id
      }
      // 保存成功后返回列表页面，确保列表显示更新后的状态
      if (navigateBack && navigateTo) {
        navigateTo('DensityTestReportList')
      }
    } else {
      alert('保存失败: ' + response.data.message)
    }
  } catch (error) {
    console.error('Save error:', error)
    alert('保存失败')
  }
}

const saveCurrentPage = async () => {
  if (currentIndex.value === 0) {
    await saveData(false)
    return
  }

  const idx = currentIndex.value - 1
  const record = resultPages.value[idx] || {}
  const pageData = resultPageFormData.value[idx] || {}

  try {
    const dataJsonObj = { ...pageData }
    if (!dataJsonObj.unifiedNumber && formData.unifiedNumber) dataJsonObj.unifiedNumber = formData.unifiedNumber
    if (!dataJsonObj.constructionPart && formData.constructionPart) dataJsonObj.constructionPart = formData.constructionPart
    if (!dataJsonObj.testMethod && formData.testMethod) dataJsonObj.testMethod = formData.testMethod
    if (!dataJsonObj.testCategory && formData.testCategory) dataJsonObj.testCategory = formData.testCategory

    for (let i = 0; i < 20; i++) {
      if (dataJsonObj['date_' + i]) {
        dataJsonObj['date_' + i] = formatDate(dataJsonObj['date_' + i])
      }
    }

    const dataToSave = {
      id: record.id,
      entrustmentId: record.entrustmentId || formData.unifiedNumber || props.wtNum || props.id,
      status: formData.status,
      dataJson: JSON.stringify(dataJsonObj),
      reviewSignaturePhoto: formData.reviewerSignature,
      inspectSignaturePhoto: formData.testerSignature,
      approveSignaturePhoto: formData.approverSignature,
      recordTester: formData.recordTester,
      recordReviewer: formData.recordReviewer,
      filler: formData.filler,
      approver: formData.approver
    }

    const resp = await axios.post('/api/density-test/save', dataToSave)
    if (resp.data && resp.data.success) {
      if (!record.id && resp.data.data && resp.data.data.id) record.id = resp.data.data.id
      record.dataJson = dataToSave.dataJson
      alert('保存成功')
    } else {
      alert('保存失败: ' + (resp.data?.message || '未知错误'))
    }
  } catch (e) {
    console.error('Save current page error', e)
    alert('保存失败')
  }
}

const approveAndSave = async () => {
  const user = JSON.parse(localStorage.getItem('userInfo'))
  if (!user || !user.username) {
    alert('请先登录')
    return
  }

  try {
    const sigRes = await axios.post('/api/signature/get', { userAccount: user.username })
    const signatureBlob = sigRes?.data?.data?.signatureBlob
    if (!sigRes?.data?.success || !signatureBlob) {
      alert('未找到您的电子签名，请先去“电子签名”页面设置')
      return
    }

    const currentName = user.fullName || user.userName || user.username
    formData.approver = currentName
    formData.approverSignature = normalizeSignatureSrc(signatureBlob)
    formData.status = 6

    await saveData(false)
    for (let i = 0; i < resultPages.value.length; i++) {
      const record = resultPages.value[i] || {}
      const pageData = resultPageFormData.value[i] || {}
      const dataJsonObj = { ...pageData }
      if (!dataJsonObj.unifiedNumber && formData.unifiedNumber) dataJsonObj.unifiedNumber = formData.unifiedNumber
      if (!dataJsonObj.constructionPart && formData.constructionPart) dataJsonObj.constructionPart = formData.constructionPart
      if (!dataJsonObj.testMethod && formData.testMethod) dataJsonObj.testMethod = formData.testMethod
      if (!dataJsonObj.testCategory && formData.testCategory) dataJsonObj.testCategory = formData.testCategory

      for (let j = 0; j < 20; j++) {
        if (dataJsonObj['date_' + j]) dataJsonObj['date_' + j] = formatDate(dataJsonObj['date_' + j])
      }

      await axios.post('/api/density-test/save', {
        id: record.id,
        entrustmentId: record.entrustmentId || formData.unifiedNumber || props.wtNum || props.id,
        status: formData.status,
        dataJson: JSON.stringify(dataJsonObj),
        reviewSignaturePhoto: formData.reviewerSignature,
        inspectSignaturePhoto: formData.testerSignature,
        approveSignaturePhoto: formData.approverSignature,
        recordTester: formData.recordTester,
        recordReviewer: formData.recordReviewer,
        filler: formData.filler,
        approver: formData.approver
      })
    }
    if (navigateTo) navigateTo('DensityTestReportList')
  } catch (e) {
    console.error('Approve error:', e)
    alert('批准失败')
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
      const currentName = user.userName
      // Match Tester
      if (!formData.recordTester || formData.recordTester === currentName || formData.recordTester === currentAccount) {
        if (!formData.recordTester) formData.recordTester = currentName
        formData.testerSignature = imgSrc
        signed = true
      }
      
      if (signed) {
        // 将签名同步到所有页的表单数据
        for (let i = 0; i < resultPages.value.length; i++) {
          const pageData = resultPageFormData.value[i] || {}
          pageData.testerSignature = imgSrc
          resultPageFormData.value[i] = pageData
        }
        // 保存报告封面页
        await saveData()
        // 同步保存每一页记录的签名
        for (let i = 0; i < resultPages.value.length; i++) {
          const record = resultPages.value[i] || {}
          const pageData = resultPageFormData.value[i] || {}
          const dataJsonObj = { ...pageData }
          if (!dataJsonObj.unifiedNumber && formData.unifiedNumber) dataJsonObj.unifiedNumber = formData.unifiedNumber
          if (!dataJsonObj.constructionPart && formData.constructionPart) dataJsonObj.constructionPart = formData.constructionPart
          if (!dataJsonObj.testMethod && formData.testMethod) dataJsonObj.testMethod = formData.testMethod
          if (!dataJsonObj.testCategory && formData.testCategory) dataJsonObj.testCategory = formData.testCategory
          for (let j = 0; j < 20; j++) {
            if (dataJsonObj['date_' + j]) dataJsonObj['date_' + j] = formatDate(dataJsonObj['date_' + j])
          }
          await axios.post('/api/density-test/save', {
            id: record.id,
            entrustmentId: record.entrustmentId || formData.unifiedNumber || props.wtNum || props.id,
            status: formData.status,
            dataJson: JSON.stringify(dataJsonObj),
            reviewSignaturePhoto: formData.reviewerSignature,
            inspectSignaturePhoto: formData.testerSignature,
            approveSignaturePhoto: formData.approverSignature,
            recordTester: formData.recordTester,
            recordReviewer: formData.recordReviewer,
            filler: formData.filler,
            approver: formData.approver
          })
        }
        await submitWorkflow('SIGN_REVIEW')
        alert('签名成功并已保存')
      } else {
        alert(`当前用户(${currentName})与表单中的检测人员(${formData.recordTester})不匹配，无法签名`)
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

const exportExcel = async () => {
  await openExportModal()
}

const exportByFormat = async (format) => {
  try {
    const names = getExportTemplateBaseNames()
    exportTemplateInfo.reportBaseName = names.reportBaseName
    exportTemplateInfo.resultBaseName = names.resultBaseName

    const reportData = { ...formData }
    if (reportData.commissionDate) reportData.commissionDate = formatDate(reportData.commissionDate)
    if (reportData.testDate) reportData.testDate = formatDate(reportData.testDate)
    if (reportData.reportDate) reportData.reportDate = formatDate(reportData.reportDate)
    for (let i = 0; i < 7; i++) {
      if (reportData['date_' + i]) reportData['date_' + i] = formatDate(reportData['date_' + i])
    }

    const resultTotal = resultPages.value.length
    const totalPages = 1 + resultTotal
    const successPaths = []
    const failures = []

    const entrustmentId = formData.unifiedNumber || props.wtNum || props.id || ''

    try {
      const resp = await axios.post('/api/density-test/export-excel', {
        entrustmentId,
        pageNo: 1,
        totalPages,
        data: {
          ...reportData,
          pageNo: 1,
          pageNum: 1,
          pageIndex: 0,
          totalPages,
          pageTotal: totalPages,
          pageText: `1/${totalPages}`
        },
        format,
        templateBaseName: exportTemplateInfo.reportBaseName
      })
      if (resp.data && resp.data.success) {
        successPaths.push(resp.data.path)
      } else {
        failures.push(`报告页：${resp.data && resp.data.message ? resp.data.message : '未知错误'}`)
      }
    } catch (e) {
      failures.push(`报告页：${e && e.message ? e.message : '请求失败'}`)
    }

    for (let i = 0; i < resultTotal; i++) {
      const pageNo = i + 2
      const resultData = { ...(resultPageFormData.value[i] || {}) }
      if (!resultData.unifiedNumber && formData.unifiedNumber) resultData.unifiedNumber = formData.unifiedNumber
      if (!resultData.constructionPart && formData.constructionPart) resultData.constructionPart = formData.constructionPart
      if (!resultData.testMethod && formData.testMethod) resultData.testMethod = formData.testMethod
      if (!resultData.testCategory && formData.testCategory) resultData.testCategory = formData.testCategory
      if (!resultData.maxDryDensity && formData.maxDryDensity) resultData.maxDryDensity = formData.maxDryDensity
      if (!resultData.optimumMoisture && formData.optimumMoisture) resultData.optimumMoisture = formData.optimumMoisture
      if (!resultData.minDryDensity && formData.minDryDensity) resultData.minDryDensity = formData.minDryDensity
      for (let j = 0; j < 20; j++) {
        if (resultData['date_' + j]) resultData['date_' + j] = formatDate(resultData['date_' + j])
      }

      const mergedData = { ...reportData }
      const isEmpty = (v) => v === undefined || v === null || String(v).trim() === ''
      const sampleKeyRe = /^(sampleId|location|date|wetDensity|dryDensity|moisture|avgDryDensity|compaction|remarks)_\d+$/
      Object.keys(resultData).forEach((k) => {
        const v = resultData[k]
        if (sampleKeyRe.test(k)) {
          mergedData[k] = v
          return
        }
        if (isEmpty(mergedData[k]) && !isEmpty(v)) {
          mergedData[k] = v
        }
      })

      try {
        const response = await axios.post('/api/density-test/export-excel', {
          entrustmentId,
          pageNo,
          totalPages,
          data: {
            ...mergedData,
            pageNo,
            pageNum: pageNo,
            pageIndex: pageNo - 1,
            totalPages,
            pageTotal: totalPages,
            pageText: `${pageNo}/${totalPages}`
          },
          format,
          templateBaseName: exportTemplateInfo.resultBaseName
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

const openClientPdfPreview = () => {
  if (!pdfForm.value) return
  const container = pdfForm.value.closest('.densityTestReport-container')
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
      .pdf-preview.densityTestReport-container { width: 100%; height: 100%; margin: 0; padding: 0; box-sizing: border-box; display: flex; flex-direction: column; }
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
const openBackendPdfPreview = (actionUrl) => {
  if (!pdfForm.value) return
  const container = pdfForm.value.closest('.densityTestReport-container')
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
      .pdf-preview.densityTestReport-container { width: 198mm; height: 285mm; max-width: 198mm; min-width: 0; margin: 0; padding: 0; box-sizing: border-box; display: flex; flex-direction: column; }
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
  if (pdfForm.value) {
    openBackendPdfPreview('/api/pdf/density_test_report/generate')
  }
}

const previewPdf = () => {
  if (pdfForm.value) {
    openBackendPdfPreview('/api/pdf/density_test_report/preview')
  }
}
</script>

<style scoped>
.densityTestReport-container {
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

/* Add signature overlay style */
.signature-overlay {
  position: absolute;
  pointer-events: none;
  z-index: 10;
}
.signature-overlay img {
  height: 18px;
  width: auto;
  max-width: 80px;
  opacity: 0.8;
}

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
  top: -16px;
  height: 18px;
  width: auto;
  max-width: 80px;
  object-fit: contain;
  mix-blend-mode: multiply;
  pointer-events: none;
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

  .densityTestReport-container {
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
        }
        .two-inputs {
            display: flex;
            flex-direction: column;
            gap: 6px;
        }
        .two-inputs input {
            width: 100%;
            box-sizing: border-box;
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
        input[type="text"]:disabled:focus, textarea:disabled:focus {
            background-color: transparent;
            outline: none;
            border-color: black;
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
        .statement {
            font-size: 12px;
            line-height: 1.6;
            margin-top: 5px;
        }
        .company-info {
             display: flex;
             justify-content: space-between;
             font-size: 12px;
             margin-top: 5px;
        }
        .page-footer {
            margin-top: 5px;
            display: flex;
            justify-content: space-between;
            font-size: 12px;
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
            padding: 20px;
            border-radius: 8px;
            width: 90%;
            max-width: 520px;
            max-height: 80vh;
            overflow-y: auto;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
        }
        .template-files {
            margin-top: 8px;
            display: flex;
            flex-wrap: wrap;
            gap: 8px;
        }
        .template-file {
            font-size: 12px;
            background: #f1f5f9;
            border: 1px solid #e2e8f0;
            padding: 3px 6px;
            border-radius: 4px;
        }
        .format-buttons {
            display: flex;
            flex-wrap: wrap;
            gap: 10px;
            justify-content: center;
            margin-top: 12px;
        }
        @media print {
            .densityTestReport-container {
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
