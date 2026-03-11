<template>
  <div class="waterReplacementRecord-container">


    <div class="no-print toolbar">
      <div class="toolbar-left">
        <button @click="goToList" class="link-button">&lt; 返回列表</button>
        <span v-if="!draftMode" class="record-nav">
          <button
            @click="prevPage"
            :disabled="totalPages <= 0 || currentPage <= 0"
            class="btn btn-secondary btn-small"
          >
            上一页
          </button>
          <span class="record-nav-info">
            页面 {{ totalPages > 0 ? currentPage + 1 : 0 }} / {{ totalPages }}
          </span>
          <button
            @click="nextPage"
            :disabled="totalPages <= 0 || currentPage >= totalPages - 1"
            class="btn btn-secondary btn-small"
          >
            下一页
          </button>
          <button
            @click="addPage"
            class="btn btn-primary btn-small"
          >
            添加页面
          </button>
          <button
            v-if="totalPages > 1"
            @click="deletePage"
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
            :disabled="Number(formData.status) === 1"
            v-if="Number(formData.status) === 0 || Number(formData.status) === 2"
            @click="submitWorkflow('SUBMIT')"
            class="btn btn-primary btn-small"
          >
            提交审核
          </button>
          <button
            v-if="Number(formData.status) === 1"
            @click="submitWorkflow('AUDIT_PASS')"
            class="btn btn-primary btn-small"
          >
            审核通过
          </button>
          <button
            v-if="Number(formData.status) === 1"
            @click="submitWorkflow('REJECT')"
            class="btn btn-danger btn-small"
          >
            打回
          </button>
        </template>

        <button
          @click="showAnalysisModal"
          class="btn btn-secondary btn-small"
          :disabled="!isEditable"
        >
          数据分析
        </button>

        <button
          @click="submitForm"
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
      </div>
    </div>

    <form id="pdfForm" ref="pdfForm" method="post">
    <!-- 电子签名隐藏字段 -->
    <input type="hidden" v-model="formData.testerSignature" name="testerSignature">
    <input type="hidden" v-model="formData.reviewerSignature" name="reviewerSignature">
    <h2>相对密度试验记录表（灌水法）</h2>

    <div class="header-info">
        <span>工程部位：<input type="text" v-model="formData.constructionPart"   name="constructionPart" style="width: 200px; border-bottom: 1px solid black; text-align: left;" :disabled="!isEditable"></span>
        <span style="margin-left: auto;">试验日期：<input type="text" v-model="formData.testDate"   name="testDate" style="width: 150px; border-bottom: 1px solid black;" :disabled="!isEditable"></span>
    </div>
    <div class="header-info">
        <span>依据标准：<input type="text" v-model="formData.standard"   name="standard" style="width: 90px; border-bottom: 1px solid black; text-align: left;" :disabled="!isEditable"></span>
        <span>最大干密度：<input type="text" v-model="formData.maxDryDensity"   name="maxDryDensity" style="width: 50px; border-bottom: 1px solid black;" :disabled="!isEditable"> g/cm³</span>
        <span>最小干密度：<input type="text" v-model="formData.minDryDensity"   name="minDryDensity" style="width: 50px; border-bottom: 1px solid black;" :disabled="!isEditable"> g/cm³</span>
        <span>设计相对密度：<input type="text" v-model="formData.relativeDensity"   name="relativeDensity" style="width: 50px; border-bottom: 1px solid black;" :disabled="!isEditable"></span>
        <span>统一编号：<input type="text" v-model="formData.unifiedNumber"   name="unifiedNumber" style="width: 90px; border-bottom: 1px solid black;" disabled></span>
    </div>
    <div class="header-info">
        <span>水的密度：<input type="text" v-model="formData.waterDensity"   name="waterDensity" style="width: 80px; border-bottom: 1px solid black;" :disabled="!isEditable"> g/cm³</span>
        <span>仪器设备：<input type="text" v-model="formData.equipment"   name="equipment" style="width: 150px; border-bottom: 1px solid black;" :disabled="!isEditable"></span>
    </div>

    <table>
        <!-- Table Header -->
        <tr>
            <td colspan="4" class="label" style="width: 20%;">取样位置</td>
            <template v-for="(n, i_idx) in 4" :key="i_idx">
            <td colspan="4"><textarea :name="'samplingLocation_page' + currentPage + '_' + i_idx" v-model="formData['samplingLocation_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
            </template>
        </tr>
        <tr>
            <td colspan="3" class="label">套环体积</td>
            <td class="label">cm³</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><textarea :name="'ringVolume_page' + currentPage + '_' + i_idx" v-model="formData['ringVolume_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
            </template>
        </tr>
        <tr>
            <td rowspan="2" colspan="2" class="label">储水桶<br>水位</td>
            <td class="label">初始</td>
            <td class="label">cm</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><textarea :name="'initialWaterLevel_page' + currentPage + '_' + i_idx" v-model="formData['initialWaterLevel_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
            </template>
        </tr>
        <tr>
            <td class="label">终了</td>
            <td class="label">cm</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><textarea :name="'finalWaterLevel_page' + currentPage + '_' + i_idx" v-model="formData['finalWaterLevel_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
            </template>
        </tr>
        <tr>
            <td colspan="3" class="label">储水桶面积</td>
            <td class="label">cm²</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><textarea :name="'tankArea_page' + currentPage + '_' + i_idx" v-model="formData['tankArea_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
            </template>
        </tr>
        <tr>
            <td colspan="3" class="label">试坑体积</td>
            <td class="label">cm³</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><textarea :name="'pitVolume_page' + currentPage + '_' + i_idx" v-model="formData['pitVolume_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
            </template>
        </tr>
        <tr>
            <td colspan="3" class="label">试样质量</td>
            <td class="label">g</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><textarea :name="'sampleMass_page' + currentPage + '_' + i_idx" v-model="formData['sampleMass_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
            </template>
        </tr>
        <tr>
            <td colspan="3" class="label">试样湿密度</td>
            <td class="label">g/cm³</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><textarea :name="'wetDensity_page' + currentPage + '_' + i_idx" v-model="formData['wetDensity_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
            </template>
        </tr>

        <!-- Moisture Content Section -->
        <tr>
            <td rowspan="6" colspan="2" class="label" style="width: 5%;">试样<br>含水率</td>
            <td class="label">编号</td>
            <td class="label">/</td>
            <template v-for="(n, i_idx) in 16" :key="i_idx">
            <td><textarea :name="'moistureNo_page' + currentPage + '_' + i_idx" v-model="formData['moistureNo_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
            </template>
        </tr>
        <tr>
            <td class="label">容器质量</td>
            <td class="label">g</td>
            <template v-for="(n, i_idx) in 16" :key="i_idx">
            <td><textarea :name="'containerMass_page' + currentPage + '_' + i_idx" v-model="formData['containerMass_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
            </template>
        </tr>
        <tr>
            <td class="label">容器+湿样质量</td>
            <td class="label">g</td>
            <template v-for="(n, i_idx) in 16" :key="i_idx">
            <td><textarea :name="'wetSampleMass_page' + currentPage + '_' + i_idx" v-model="formData['wetSampleMass_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
            </template>
        </tr>
        <tr>
            <td class="label">容器+干样质量</td>
            <td class="label">g</td>
            <template v-for="(n, i_idx) in 16" :key="i_idx">
            <td><textarea :name="'drySampleMass_page' + currentPage + '_' + i_idx" v-model="formData['drySampleMass_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
            </template>
        </tr>
        <tr>
            <td class="label">含水率</td>
            <td class="label">%</td>
            <template v-for="(n, i_idx) in 16" :key="i_idx">
            <td><textarea :name="'moistureContent_page' + currentPage + '_' + i_idx" v-model="formData['moistureContent_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
            </template>
        </tr>
        <tr>
            <td class="label">平均含水率</td>
            <td class="label">%</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><textarea :name="'avgMoisture_page' + currentPage + '_' + i_idx" v-model="formData['avgMoisture_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
            </template>
        </tr>

        <!-- Bottom Section -->
        <tr>
            <td colspan="3" class="label">实测干密度</td>
            <td class="label">g/cm³</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><textarea :name="'measuredDryDensity_page' + currentPage + '_' + i_idx" v-model="formData['measuredDryDensity_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
            </template>
        </tr>
        <tr>
            <td colspan="3" class="label">平均实测干密度</td>
            <td class="label">g/cm³</td>
            <template v-for="(n, i_idx) in 4" :key="i_idx">
            <td colspan="4"><textarea :name="'avgMeasuredDryDensity_page' + currentPage + '_' + i_idx" v-model="formData['avgMeasuredDryDensity_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
            </template>
        </tr>
        <tr>
            <td colspan="3" class="label">相对密度</td>
            <td class="label"></td>
            <template v-for="(n, i_idx) in 4" :key="i_idx">
            <td colspan="4"><textarea :name="'relativeDensity_page' + currentPage + '_' + i_idx" v-model="formData['relativeDensity_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
            </template>
        </tr>
        <tr>
            <td colspan="4" class="label">备注</td>
            <td colspan="16" class="left-align" style="height: 60px; vertical-align: top;">
                <textarea v-model="formData.remarks"  name="remarks" style="width: 100%; height: 100%;" :disabled="!isEditable"></textarea>
            </td>
        </tr>
    </table>

        <div class="footer-info">
            <span style="position: relative; margin-right: 20px;">
                试验：<span style="display: inline-block; width: 100px; border-bottom: 1px solid black; height: 1em;"></span>
                <div v-if="formData.testerSignature" style="position: absolute; top: -20px; left: 40px; pointer-events: none;">
                    <img :src="formData.testerSignature" style="width: 80px; height: 40px; mix-blend-mode: multiply;" />
                </div>
            </span>
            <span style="position: relative;">
                审核：<span style="display: inline-block; width: 100px; border-bottom: 1px solid black; height: 1em;"></span>
                <div v-if="formData.reviewerSignature" style="position: absolute; top: -20px; left: 40px; pointer-events: none;">
                    <img :src="formData.reviewerSignature" style="width: 80px; height: 40px; mix-blend-mode: multiply;" />
                </div>
            </span>
        </div>


    </form>

    <!-- 数据分析模态窗口 -->
    <div v-if="showModal" class="modal-overlay" @click="closeAnalysisModal">
      <div class="modal-content" @click.stop style="position: relative;">
        <h3>数据分析</h3>
        <button class="close-btn" @click="closeAnalysisModal">&times;</button>
        <div class="modal-body">
          <div class="form-group">
            <label>数据范围：</label>
            <input type="number" v-model.number="analysisRange.start" min="1" max="4" placeholder="从" class="modal-input">
            <span>列</span>
            <input type="number" v-model.number="analysisRange.end" min="1" max="4" placeholder="至" class="modal-input">
            <span>列</span>
          </div>
          
          <div class="form-group">
            <label>套环体积 (cm³)：</label>
            <input type="number" v-model.number="analysisResults.ringVolumeMin" step="0.01" placeholder="最小值" class="modal-input">
            <input type="number" v-model.number="analysisResults.ringVolumeMax" step="0.01" placeholder="最大值" class="modal-input">
          </div>
          
          <div class="form-group">
            <label>储水桶水位 (cm)：</label>
            <input type="number" v-model.number="analysisResults.waterLevelMin" step="0.01" placeholder="最小值" class="modal-input">
            <input type="number" v-model.number="analysisResults.waterLevelMax" step="0.01" placeholder="最大值" class="modal-input">
          </div>
          
          <div class="form-group">
            <label>储水桶面积 (cm²)：</label>
            <input type="number" v-model.number="analysisResults.tankAreaMin" step="0.01" placeholder="最小值" class="modal-input">
            <input type="number" v-model.number="analysisResults.tankAreaMax" step="0.01" placeholder="最大值" class="modal-input">
          </div>
          
          <div class="form-group">
            <label>试坑体积 (cm³)：</label>
            <input type="number" v-model.number="analysisResults.pitVolumeMin" step="0.01" placeholder="最小值" class="modal-input">
            <input type="number" v-model.number="analysisResults.pitVolumeMax" step="0.01" placeholder="最大值" class="modal-input">
          </div>
          
          <div class="form-group">
            <label>试样质量 (g)：</label>
            <input type="number" v-model.number="analysisResults.sampleMassMin" step="0.01" placeholder="最小值" class="modal-input">
            <input type="number" v-model.number="analysisResults.sampleMassMax" step="0.01" placeholder="最大值" class="modal-input">
          </div>
          
          <div class="form-group">
            <label>试样湿密度 (g/cm³)：</label>
            <input type="number" v-model.number="analysisResults.wetDensityMin" step="0.01" placeholder="最小值" class="modal-input">
            <input type="number" v-model.number="analysisResults.wetDensityMax" step="0.01" placeholder="最大值" class="modal-input">
          </div>
          
          <div class="form-group">
            <label>容器质量 (g)：</label>
            <input type="number" v-model.number="analysisResults.containerMassMin" step="0.01" placeholder="最小值" class="modal-input">
            <input type="number" v-model.number="analysisResults.containerMassMax" step="0.01" placeholder="最大值" class="modal-input">
          </div>
          
          <div class="form-group">
            <label>容器+湿样质量 (g)：</label>
            <input type="number" v-model.number="analysisResults.wetSampleMassMin" step="0.01" placeholder="最小值" class="modal-input">
            <input type="number" v-model.number="analysisResults.wetSampleMassMax" step="0.01" placeholder="最大值" class="modal-input">
          </div>
          
          <div class="form-group">
            <label>容器+干样质量 (g)：</label>
            <input type="number" v-model.number="analysisResults.drySampleMassMin" step="0.01" placeholder="最小值" class="modal-input">
            <input type="number" v-model.number="analysisResults.drySampleMassMax" step="0.01" placeholder="最大值" class="modal-input">
          </div>
          
          <div class="form-group">
            <label>含水率 (%)：</label>
            <input type="number" v-model.number="analysisResults.moistureContentMin" step="0.01" placeholder="最小值" class="modal-input">
            <input type="number" v-model.number="analysisResults.moistureContentMax" step="0.01" placeholder="最大值" class="modal-input">
          </div>
          
          <div class="form-group">
            <label>实测干密度 (g/cm³)：</label>
            <input type="number" v-model.number="analysisResults.measuredDryDensityMin" step="0.01" placeholder="最小值" class="modal-input">
            <input type="number" v-model.number="analysisResults.measuredDryDensityMax" step="0.01" placeholder="最大值" class="modal-input">
          </div>
          
          <div class="form-group">
            <label>相对密度：</label>
            <input type="number" v-model.number="analysisResults.relativeDensityMin" step="0.01" placeholder="最小值" class="modal-input">
            <input type="number" v-model.number="analysisResults.relativeDensityMax" step="0.01" placeholder="最大值" class="modal-input">
          </div>
        </div>
        <div class="modal-actions">
          <button @click="closeAnalysisModal" class="btn btn-secondary">取消</button>
          <button @click="autoAnalyzeAndFill" class="btn btn-primary">自动分析并填充</button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { reactive, ref, onMounted, inject, computed } from 'vue'
import axios from 'axios'

// 数据分析相关变量
const showModal = ref(false)
const analysisRange = reactive({ start: '', end: '' })
const analysisResults = reactive({
  ringVolumeMin: '',
  ringVolumeMax: '',
  waterLevelMin: '',
  waterLevelMax: '',
  tankAreaMin: '',
  tankAreaMax: '',
  pitVolumeMin: '',
  pitVolumeMax: '',
  sampleMassMin: '',
  sampleMassMax: '',
  wetDensityMin: '',
  wetDensityMax: '',
  containerMassMin: '',
  containerMassMax: '',
  wetSampleMassMin: '',
  wetSampleMassMax: '',
  drySampleMassMin: '',
  drySampleMassMax: '',
  moistureContentMin: '',
  moistureContentMax: '',
  measuredDryDensityMin: '',
  measuredDryDensityMax: '',
  relativeDensityMin: '',
  relativeDensityMax: '',
  waterDensity: ''
})

const props = defineProps({
  id: {
    type: String,
    default: ''
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

const navigateTo = inject('navigateTo')


const pdfForm = ref(null)

// 单条记录 + 多页数据
const currentRecord = ref(null)
const currentPage = ref(0)  // 当前页面索引（从0开始）
const totalPages = computed(() => {
  // 从 formData 中获取总页数，如果没有则默认为1
  return formData.totalPages || 1
})

const isEditable = computed(() => {
  return formData.status == 0 || formData.status == 2
})

const formData = reactive({
  id: '',
  entrustmentId: '',
  projectName: '',
  constructionPart: '',
  testDate: '',
  standard: '',
  maxDryDensity: '',
  minDryDensity: '',
  relativeDensity: '',
  unifiedNumber: '',
  waterDensity: '',
  equipment: '',
  recordTester: '',
  recordReviewer: '',
  filler: '',
  remarks: '',
  reviewerSignature: '',
  testerSignature: '',
  status: 0, // 0:Draft, 1:PendingAudit, 2:Returned, 3:PendingSign, 5:Approved
  // 页面管理
  totalPages: 1  // 总页数
})

// Status Text Helper
const getStatusText = (status) => {
    const s = parseInt(status)
    switch(s) {
        // 统一状态名称
        case 0: return '草稿'
        case 1: return '已提交待审核'
        case 2: return '已打回'
        case 4: return '已签字待提交'
        case 5: return '审核通过'
        // 报告表状态 (10-15)
        case 10: return '草稿'
        case 11: return '已提交待审核'
        case 12: return '已打回'
        case 14: return '已签字待提交'
        case 15: return '审核通过待批准'
        // 结果表状态 (20-25)
        case 20: return '草稿'
        case 21: return '已提交待审核'
        case 22: return '已打回'
        case 24: return '已签字待提交'
        case 25: return '审核通过待批准'
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
        case 4: return '#17a2b8' // info
        case 5: return '#28a745' // success
        // 报告表状态 (10-15)
        case 10: return '#6c757d' // secondary
        case 11: return '#007bff' // primary
        case 12: return '#dc3545' // danger
        case 14: return '#17a2b8' // info
        case 15: return '#28a745' // success
        // 结果表状态 (20-25)
        case 20: return '#6c757d' // secondary
        case 21: return '#007bff' // primary
        case 22: return '#dc3545' // danger
        case 24: return '#17a2b8' // info
        case 25: return '#28a745' // success
        default: return '#6c757d'
    }
}

// Workflow Action Handler
const submitWorkflow = async (action) => {
    // 如果记录还未保存，先保存记录
    if (!formData.id) {
        await submitForm()
        if (!formData.id) {
            alert('保存记录失败，请重试')
            return
        }
    }
    
    // 保存当前页面的数据
    saveCurrentPageData()
    saveCurrentRecordState()
    
    // Get current user
    const user = JSON.parse(localStorage.getItem('userInfo'))
    if (!user || !user.username) {
        alert('请先登录')
        return
    }

    let signatureData = null
    
    // Prepare signature based on action
    if (action === 'SUBMIT') {
        // Auto fetch signature if missing
        if (!formData.testerSignature) {
            try {
                const sigRes = await axios.post('/api/signature/get', { userAccount: user.username })
                if (sigRes.data.success && sigRes.data.data && sigRes.data.data.signatureBlob) {
                     formData.testerSignature = `data:image/png;base64,${sigRes.data.data.signatureBlob}`
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
        
        // Check if current user is the tester
        if (formData.recordTester && user.username !== formData.recordTester && user.userName !== formData.recordTester) {
            alert('您不是该单据的记录检测人 (' + formData.recordTester + ')，无权提交')
            return
        }
        
        signatureData = formData.testerSignature.replace(/^data:image\/\w+;base64,/, '')
    } else if (action === 'AUDIT_PASS') {
        // Role check
        if (formData.recordReviewer && user.username !== formData.recordReviewer && user.userName !== formData.recordReviewer) {
            alert('您不是该单据的记录复核人 (' + formData.recordReviewer + ')，无权操作')
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
        if (formData.recordReviewer && user.username !== formData.recordReviewer && user.userName !== formData.recordReviewer) {
            alert('您不是该单据的记录复核人 (' + formData.recordReviewer + ')，无权操作')
            return
        }
    }

    const request = {
        tableType: 'WATER_REPLACEMENT',
        recordId: formData.id,
        action: action,
        userAccount: user.username,
        signatureData: signatureData,
        nextHandler: '' // Optional: could let user select next handler
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
            // 保存当前页面索引，重新加载后恢复
            const savedPage = currentPage.value
            const idOrWtNum = formData.entrustmentId || props.wtNum || props.id
            if (idOrWtNum) {
                await loadData(idOrWtNum)
                // 恢复页面索引
                if (savedPage < totalPages.value) {
                    currentPage.value = savedPage
                    if (currentRecord.value && currentRecord.value.dataJson) {
                        const json = JSON.parse(currentRecord.value.dataJson)
                        loadPageData(savedPage, json)
                    }
                }
            } else {
                loadData()
            }
        } else {
            alert('操作失败: ' + response.data.message)
        }
    } catch (e) {
        console.error('Workflow error', e)
        alert('操作异常')
    }
}

// 初始化指定页面的动态字段
const initDynamicFieldsForPage = (pageIndex) => {
  for (let i_idx = 0; i_idx < 16; i_idx++) {
    const pagePrefix = '_page' + pageIndex + '_' + i_idx
    formData['drySampleMass' + pagePrefix] = ''
    formData['tankArea' + pagePrefix] = ''
    formData['relativeDensity' + pagePrefix] = ''
    formData['ringVolume' + pagePrefix] = ''
    formData['pitVolume' + pagePrefix] = ''
    formData['samplingLocation' + pagePrefix] = ''
    formData['moistureContent' + pagePrefix] = ''
    formData['avgMoisture' + pagePrefix] = ''
    formData['wetDensity' + pagePrefix] = ''
    formData['finalWaterLevel' + pagePrefix] = ''
    formData['moistureNo' + pagePrefix] = ''
    formData['initialWaterLevel' + pagePrefix] = ''
    formData['measuredDryDensity' + pagePrefix] = ''
    formData['avgMeasuredDryDensity' + pagePrefix] = ''
    formData['containerMass' + pagePrefix] = ''
    formData['wetSampleMass' + pagePrefix] = ''
    formData['sampleMass' + pagePrefix] = ''
  }
}

// 初始化所有页面的字段（用于兼容旧数据）
const initDynamicFields = () => {
  // 兼容旧数据：如果没有 totalPages，初始化第一页
  if (!formData.totalPages || formData.totalPages < 1) {
    formData.totalPages = 1
  }
  // 初始化当前页面的字段
  initDynamicFieldsForPage(currentPage.value)
}

onMounted(async () => {
  // 初始化第一页的字段
  currentPage.value = 0
  formData.totalPages = 1
  initDynamicFieldsForPage(0)
  
  const urlParams = new URLSearchParams(window.location.search)
  const wtNumParam = props.wtNum || urlParams.get('wtNum')
  const idParam = props.id || urlParams.get('id')

  if (wtNumParam) {
    loadData(wtNumParam)
    return
  }

  if (idParam) {
    loadData(idParam)
    return
  }

  const newRecord = {
    id: '',
    entrustmentId: '',
    dataJson: JSON.stringify({ ...formData, totalPages: 1 }),
    reviewSignaturePhoto: '',
    inspectSignaturePhoto: ''
  }
  
  currentRecord.value = newRecord
  currentPage.value = 0
  formData.totalPages = 1
  initDynamicFieldsForPage(0)
  mapRecordToFormData(newRecord)
})

// 加载指定页面的数据
const loadPageData = (pageIndex, parsedJson = null) => {
  const json = parsedJson || (currentRecord.value && currentRecord.value.dataJson ? JSON.parse(currentRecord.value.dataJson) : {})
  
  // 初始化该页面的字段
  initDynamicFieldsForPage(pageIndex)
  
  // 从 JSON 中加载该页面的数据
  for (let i_idx = 0; i_idx < 16; i_idx++) {
    const pagePrefix = '_page' + pageIndex + '_' + i_idx
    const fieldNames = [
      'drySampleMass', 'tankArea', 'relativeDensity', 'ringVolume', 'pitVolume',
      'samplingLocation', 'moistureContent', 'avgMoisture', 'wetDensity',
      'finalWaterLevel', 'moistureNo', 'initialWaterLevel', 'measuredDryDensity',
      'avgMeasuredDryDensity', 'containerMass', 'wetSampleMass', 'sampleMass'
    ]
    
    fieldNames.forEach(fieldName => {
      // 先尝试新格式：fieldName_pageX_Y
      const newKey = fieldName + pagePrefix
      if (json[newKey] !== undefined && json[newKey] !== null) {
        formData[newKey] = json[newKey]
      } else {
        // 兼容旧格式：fieldName_index（需要计算原始索引）
        const oldIndex = pageIndex * 16 + i_idx
        const oldKey = fieldName + '_' + oldIndex
        
        if (json[oldKey] !== undefined && json[oldKey] !== null) {
          formData[newKey] = json[oldKey]
        }
      }
    })
  }
}

const mapRecordToFormData = (record) => {
  // 保存当前记录引用
  currentRecord.value = record
  
  formData.id = record.id || ''
  formData.entrustmentId = record.entrustmentId || props.id
  // 状态统一转成数字，避免后端返回字符串或null导致显示"未知"
  if (record.status !== undefined && record.status !== null && record.status !== '') {
    const statusNum = Number(record.status)
    formData.status = isNaN(statusNum) ? 0 : statusNum
  } else {
    formData.status = 0
  }
  formData.reviewerSignature = record.reviewSignaturePhoto || ''
  formData.testerSignature = record.inspectSignaturePhoto || ''

  let parsedJson = {}
  if (record.dataJson) {
    try {
      parsedJson = JSON.parse(record.dataJson)
      // 定义基础字段列表（这些字段应该优先从委托单获取，JSON 中的空值不应该覆盖）
  const basicFields = ['projectName', 'constructionPart', 'testDate', 'standard', 'maxDryDensity', 'minDryDensity', 
                      'relativeDensity', 'waterDensity', 'equipment', 
                      'remarks', 'totalPages']
      
      // Merge parsed data into formData
      Object.keys(parsedJson).forEach(key => {
        // 跳过 status 字段，使用数据库中的 status
        if (key === 'status') return
        
        // 跳过带页面后缀的字段（这些字段由 loadPageData 处理）
        if (key.includes('_page')) return
        
        const value = parsedJson[key]
        // 对于基础字段，如果 JSON 中是空值，不覆盖已有的委托单数据
        if (basicFields.includes(key)) {
          if (value !== undefined && value !== null && value !== '') {
            formData[key] = value
          }
        } else {
          // 对于其他全局字段，直接合并
          if (value !== undefined && value !== null) {
            formData[key] = value
          }
        }
      })
      
      // Legacy mapping
      if (parsedJson.tester && !formData.recordTester) formData.recordTester = parsedJson.tester
      if (parsedJson.reviewer && !formData.recordReviewer) formData.recordReviewer = parsedJson.reviewer
      
      // 获取总页数
      if (parsedJson.totalPages) {
        formData.totalPages = parsedJson.totalPages
      } else {
        // 检查是否有带页面后缀的字段来确定页数
        const pageIndices = new Set()
        let hasPageSuffix = false
        Object.keys(parsedJson).forEach(key => {
          const match = key.match(/_page(\d+)_/)
          if (match) {
            hasPageSuffix = true
            pageIndices.add(parseInt(match[1]))
          }
        })
        
        if (hasPageSuffix) {
          // 新格式：有页面后缀
          formData.totalPages = pageIndices.size > 0 ? Math.max(...Array.from(pageIndices)) + 1 : 1
        } else {
          // 旧格式：没有页面后缀，需要转换
          // 检测数据字段的最大索引，计算页数（假设每页16个位置）
          const dataFieldPrefixes = [
            'drySampleMass_', 'tankArea_', 'relativeDensity_', 'ringVolume_', 'pitVolume_',
            'samplingLocation_', 'moistureContent_', 'avgMoisture_', 'wetDensity_',
            'finalWaterLevel_', 'moistureNo_', 'initialWaterLevel_', 'measuredDryDensity_',
            'avgMeasuredDryDensity_', 'containerMass_', 'wetSampleMass_', 'sampleMass_'
          ]
          
          let maxIndex = -1
          Object.keys(parsedJson).forEach(key => {
            for (const prefix of dataFieldPrefixes) {
              if (key.startsWith(prefix)) {
                try {
                  const suffix = key.substring(prefix.length)
                  const idx = parseInt(suffix)
                  if (!isNaN(idx) && idx > maxIndex) {
                    maxIndex = idx
                  }
                } catch (e) {
                  // 忽略
                }
                break
              }
            }
          })
          
          // 计算页数：每页16个位置
          const itemsPerPage = 16
          formData.totalPages = maxIndex >= 0 ? Math.ceil((maxIndex + 1) / itemsPerPage) : 1
          
          // 将旧格式数据转换为新格式（添加 _page0_ 后缀）
          const convertedData = {}
          Object.keys(parsedJson).forEach(key => {
            let isDataField = false
            let matchedPrefix = null
            let suffix = null
            
            // 检查是否是数据字段
            for (const prefix of dataFieldPrefixes) {
              if (key.startsWith(prefix)) {
                try {
                  suffix = key.substring(prefix.length)
                  const idx = parseInt(suffix)
                  if (!isNaN(idx)) {
                    isDataField = true
                    matchedPrefix = prefix
                    break
                  }
                } catch (e) {
                  // 忽略
                }
              }
            }
            
            if (isDataField && matchedPrefix && suffix !== null) {
              // 计算应该属于哪一页和哪个位置（每页 16 个位置）
              const fieldIndex = parseInt(suffix)
              const pageIndex = Math.floor(fieldIndex / itemsPerPage)
              const positionInPage = fieldIndex % itemsPerPage
              
              // 转换为新格式：fieldName_pageX_Y
              const newKey = matchedPrefix.replace('_', '') + '_page' + pageIndex + '_' + positionInPage
              convertedData[newKey] = parsedJson[key]
            } else {
              // 非数据字段，直接保留
              convertedData[key] = parsedJson[key]
            }
          })
          
          // 更新 parsedJson 为转换后的数据
          Object.keys(convertedData).forEach(key => {
            parsedJson[key] = convertedData[key]
          })
          
          // 删除旧格式的字段
          Object.keys(parsedJson).forEach(key => {
            if (dataFieldPrefixes.some(prefix => key.startsWith(prefix) && !key.includes('_page'))) {
              delete parsedJson[key]
            }
          })
          
          // 设置总页数
          parsedJson.totalPages = formData.totalPages
          
          console.log('检测到旧格式数据，已转换为新格式。总页数:', formData.totalPages)
        }
      }
    } catch (e) {
      console.error('JSON parse error', e)
    }
  } else {
    formData.totalPages = 1
  }

  // Map fields from BusinessEntity/Entrustment (Override JSON only if entity fields are not null)
  // 基础字段：只有当record中的值不为null时才覆盖formData中的值（formData可能已从dataJson加载了值）
  if (record.clientUnit !== null && record.clientUnit !== undefined) formData.entrustingUnit = record.clientUnit
  if (record.projectName !== null && record.projectName !== undefined) formData.projectName = record.projectName
  if (record.wtNum !== null && record.wtNum !== undefined) formData.unifiedNumber = record.wtNum
  if (record.entrustmentId !== null && record.entrustmentId !== undefined && !formData.unifiedNumber) formData.unifiedNumber = record.entrustmentId

  if (record.constructionPart !== null && record.constructionPart !== undefined) formData.constructionPart = record.constructionPart
  if ((record.standard !== null && record.standard !== undefined) || (record.testBasis !== null && record.testBasis !== undefined)) formData.standard = record.standard || record.testBasis
  if (record.testDate !== null && record.testDate !== undefined) formData.testDate = record.testDate
  
  // 这些字段通常不在委托单中，只在记录表的 JSON 中
  if (record.maxDryDensity !== null && record.maxDryDensity !== undefined) formData.maxDryDensity = record.maxDryDensity
  if (record.minDryDensity !== null && record.minDryDensity !== undefined) formData.minDryDensity = record.minDryDensity
  if (record.relativeDensity !== null && record.relativeDensity !== undefined) formData.relativeDensity = record.relativeDensity
  if (record.waterDensity !== null && record.waterDensity !== undefined) formData.waterDensity = record.waterDensity
  if (record.equipment !== null && record.equipment !== undefined) formData.equipment = record.equipment

  // Ensure entity fields override dataJson if present
  if (record.reviewSignaturePhoto) formData.reviewerSignature = record.reviewSignaturePhoto
  if (record.inspectSignaturePhoto) formData.testerSignature = record.inspectSignaturePhoto

  // Map Roles
  const directory = JSON.parse(localStorage.getItem('currentDirectory') || '{}')
  
  formData.filler = record.filler || ''
  formData.recordTester = record.recordTester || record.tester || ''
  
  if (record.recordReviewer || record.reviewer) {
    formData.recordReviewer = record.recordReviewer || record.reviewer
  }
  
  // 加载所有页面的数据（如果存在）
  // 先加载当前页面的数据
  loadPageData(currentPage.value, parsedJson)
  
  // 如果有多个页面，确保所有页面的数据都被加载到 formData 中
  // 这样切换页面时数据已经存在，不需要重新从 JSON 解析
  if (formData.totalPages > 1) {
    for (let pageIdx = 0; pageIdx < formData.totalPages; pageIdx++) {
      if (pageIdx !== currentPage.value) {
        loadPageData(pageIdx, parsedJson)
      }
    }
  }
}

const getCleanDataJson = () => {
  const dynamicData = {}
  
  // Static fields to include in JSON
  const staticFields = [
    'projectName', 'constructionPart', 'testDate', 'standard', 'maxDryDensity', 'minDryDensity', 
    'relativeDensity', 'waterDensity', 'equipment', 
    'remarks', 'totalPages'
  ]
  
  staticFields.forEach(field => {
    if (formData[field] !== undefined && formData[field] !== null) {
      dynamicData[field] = formData[field]
    }
  })

  // Dynamic fields - include all page data
  Object.keys(formData).forEach(key => {
    // Match dynamic fields: samplingLocation_page0_0, ringVolume_page0_0, etc.
    if (key.match(/^(samplingLocation|ringVolume|initialWaterLevel|finalWaterLevel|tankArea|pitVolume|sampleMass|wetDensity|moistureNo|containerMass|wetSampleMass|drySampleMass|moistureContent|avgMoisture|measuredDryDensity|avgMeasuredDryDensity|relativeDensity)_page\d+_\d+$/)) {
      dynamicData[key] = formData[key]
    }
  })
  
  return JSON.stringify(dynamicData)
}

const saveCurrentRecordState = () => {
  if (!currentRecord.value) return
  
  // 保存当前页面的数据
  saveCurrentPageData()
  
  // 更新记录的基本信息
  currentRecord.value.id = formData.id
  currentRecord.value.entrustmentId = formData.entrustmentId || formData.unifiedNumber
  currentRecord.value.reviewSignaturePhoto = formData.reviewerSignature
  currentRecord.value.inspectSignaturePhoto = formData.testerSignature
  
  currentRecord.value.recordTester = formData.recordTester
  currentRecord.value.recordReviewer = formData.recordReviewer
  currentRecord.value.tester = formData.recordTester
  currentRecord.value.reviewer = formData.recordReviewer
  
  // 更新 dataJson：包含所有页面的数据和全局字段
  currentRecord.value.dataJson = getCleanDataJson()
}

// 保存当前页面的数据到 formData（实际上数据已经在 formData 中，这个函数主要是为了确保数据完整性）
const saveCurrentPageData = () => {
  // 当前页面的所有字段已经在 formData 中，带页面后缀
  // 不需要额外操作，因为数据是双向绑定的
}

const prevPage = () => {
  if (currentPage.value > 0) {
    saveCurrentPageData()
    saveCurrentRecordState()  // 保存到数据库记录
    currentPage.value--
    // 从 currentRecord 中加载该页面的数据
    if (currentRecord.value && currentRecord.value.dataJson) {
      try {
        const json = JSON.parse(currentRecord.value.dataJson)
        loadPageData(currentPage.value, json)
      } catch (e) {
        console.error('Error loading page data:', e)
        initDynamicFieldsForPage(currentPage.value)
      }
    } else {
      initDynamicFieldsForPage(currentPage.value)
    }
  }
}

const nextPage = () => {
  saveCurrentPageData()
  saveCurrentRecordState()  // 保存到数据库记录
  
  // 如果下一页不存在，初始化它
  if (currentPage.value >= totalPages.value - 1) {
    currentPage.value++
    initDynamicFieldsForPage(currentPage.value)
    formData.totalPages = currentPage.value + 1
  } else {
    currentPage.value++
    // 从 currentRecord 中加载该页面的数据
    if (currentRecord.value && currentRecord.value.dataJson) {
      try {
        const json = JSON.parse(currentRecord.value.dataJson)
        loadPageData(currentPage.value, json)
      } catch (e) {
        console.error('Error loading page data:', e)
        initDynamicFieldsForPage(currentPage.value)
      }
    } else {
      initDynamicFieldsForPage(currentPage.value)
    }
  }
}

const addPage = () => {
  saveCurrentPageData()
  saveCurrentRecordState()  // 保存当前状态
  
  // 添加新页面
  const newPageIndex = totalPages.value
  currentPage.value = newPageIndex
  initDynamicFieldsForPage(newPageIndex)
  formData.totalPages = newPageIndex + 1
}

const deletePage = async () => {
  if (totalPages.value <= 1) {
    alert('至少保留一页')
    return
  }
  
  if (!confirm('确定要删除当前页面吗？')) return
  
  saveCurrentPageData()
  saveCurrentRecordState()
  
  // 从 dataJson 中删除当前页面的所有字段
  if (currentRecord.value && currentRecord.value.dataJson) {
    try {
      const json = JSON.parse(currentRecord.value.dataJson)
      const keysToDelete = []
      Object.keys(json).forEach(key => {
        if (key.includes('_page' + currentPage.value + '_')) {
          keysToDelete.push(key)
        }
      })
      keysToDelete.forEach(key => delete json[key])
      
      // 更新总页数
      formData.totalPages = Math.max(1, formData.totalPages - 1)
      json.totalPages = formData.totalPages
      
      // 如果删除的不是最后一页，需要重新编号后面的页面
      if (currentPage.value < formData.totalPages) {
        const renumberMap = {}
        Object.keys(json).forEach(key => {
          const match = key.match(/^(.+)_page(\d+)_(.+)$/)
          if (match) {
            const [, fieldName, pageIndex, suffix] = match
            const oldPageIndex = parseInt(pageIndex)
            if (oldPageIndex > currentPage.value) {
              const newKey = `${fieldName}_page${oldPageIndex - 1}_${suffix}`
              renumberMap[key] = newKey
            }
          }
        })
        
        // 执行重新编号
        Object.keys(renumberMap).forEach(oldKey => {
          json[renumberMap[oldKey]] = json[oldKey]
          delete json[oldKey]
        })
      }
      
      currentRecord.value.dataJson = JSON.stringify(json)
      
      // 调整当前页面索引
      if (currentPage.value >= formData.totalPages) {
        currentPage.value = formData.totalPages - 1
      }
      
      // 加载当前页面的数据
      loadPageData(currentPage.value, json)
    } catch (e) {
      console.error('Delete page error', e)
      alert('删除页面失败')
    }
  }
}

const loadData = async (entrustmentId) => {
  try {
    // 1. First, always load entrustment info to fill basic fields
    let entrustmentData = null
    try {
      const entrustmentResponse = await axios.get('/api/jc-core-wt-info/detail', {
        params: { unifiedNumber: entrustmentId }
      })
      // If not found by unifiedNumber, try by ID
      if (!entrustmentResponse.data.success || !entrustmentResponse.data.data) {
        const byIdResponse = await axios.get('/api/jc-core-wt-info/by-id', {
          params: { id: entrustmentId }
        })
        if (byIdResponse.data.success && byIdResponse.data.data) {
          entrustmentData = byIdResponse.data.data
        }
      } else {
        entrustmentData = entrustmentResponse.data.data
      }
    } catch (e) {
      console.warn('Failed to load entrustment info:', e)
    }
    
    // 2. Fill basic fields from entrustment, but only if entrustment is approved
    if (entrustmentData) {
      // 检查委托单状态是否为审核通过（状态值为5），支持字符串和数字比较
      if (parseInt(entrustmentData.status) === 5) {
        formData.entrustmentId = entrustmentId
        formData.unifiedNumber = entrustmentData.wtNum || entrustmentId
        formData.entrustingUnit = entrustmentData.clientUnit || ''
        formData.projectName = entrustmentData.projectName || ''
        formData.constructionPart = entrustmentData.constructionPart || ''
        formData.testType = entrustmentData.testCategory || ''
        formData.standard = entrustmentData.testBasis || entrustmentData.standard || ''
        if (!formData.testDate) {
          formData.testDate = new Date().toISOString().split('T')[0]
        }
      } else {
        console.log('委托单状态未审核通过，不自动填充数据')
      }
    }
    
    // 3. Try to fetch existing records (List)
    const response = await axios.get('/api/water-replacement/get-by-entrustment-id', {
      params: { entrustmentId: entrustmentId }
    })

    if (response.data.success && response.data.data && response.data.data.length > 0) {
      // 使用第一条记录（现在只支持单条记录多页）
      const record = response.data.data[0]
      currentRecord.value = record
      currentPage.value = 0
      mapRecordToFormData(record)
    } else {
      // 4. If no record, create one
      const newRecord = {
        id: '',
        entrustmentId: entrustmentId,
        dataJson: JSON.stringify({ ...formData, totalPages: 1 })
      }
      
      currentRecord.value = newRecord
      currentPage.value = 0
      formData.totalPages = 1
      initDynamicFieldsForPage(0)
    }
  } catch (error) {
    console.error('Error loading data:', error)
  }
}

const submitForm = async () => {
  try {
    // 保存当前页面的数据
    saveCurrentPageData()
    saveCurrentRecordState()
    
    // 保存后状态不变
    
    const submitData = {
      id: formData.id,
      entrustmentId: formData.entrustmentId || formData.unifiedNumber,
      status: formData.status, // 传递状态字段给后端
      dataJson: getCleanDataJson(),
      reviewSignaturePhoto: formData.reviewerSignature,
      inspectSignaturePhoto: formData.testerSignature,
      // Add explicit role fields
      tester: formData.recordTester,
      reviewer: formData.recordReviewer,
      recordTester: formData.recordTester,
      recordReviewer: formData.recordReviewer,
      filler: formData.filler
    }
    
    const response = await axios.post('/api/water-replacement/save', submitData)
    if (response.data.success) {
      alert('保存成功')
      if (response.data.data && response.data.data.id) {
         formData.id = response.data.data.id
         // Update current record
         if (currentRecord.value) {
             currentRecord.value.id = formData.id
         }
      }
    } else {
      alert('保存失败: ' + response.data.message)
    }
  } catch (error) {
    console.error('Save error:', error)
    alert('保存失败')
  }
}




const printDocument = () => {
  window.print()
}

// 返回列表
const goToList = () => {
  if (navigateTo) {
    navigateTo('WaterReplacementRecordList');
  }
}

const openBackendPdfPreview = (actionUrl) => {
  if (!pdfForm.value) return
  const container = pdfForm.value.closest('.waterReplacementRecord-container')
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
  const marginMm = 12
  const availableWidthPx = mmToPx(pageWidthMm - marginMm * 2)
  const availableHeightPx = mmToPx(pageHeightMm - marginMm * 2)
  const rect = container.getBoundingClientRect()
  const contentWidthPx = Math.max(container.scrollWidth || 0, rect.width || 0, 1)
  const contentHeightPx = Math.max(container.scrollHeight || 0, rect.height || 0, 1)
  const pdfScale = Math.min(1, availableWidthPx / contentWidthPx, availableHeightPx / contentHeightPx)
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
      html, body { margin: 0; padding: 0; background: #fff; }
      .pdf-sheet { width: 210mm; height: 297mm; padding: 12mm; box-sizing: border-box; overflow: hidden; }
      .pdf-page { width: 186mm; height: 273mm; overflow: hidden; position: relative; }
      .pdf-transform { position: absolute; left: 0; top: 0; display: inline-block; transform: translate(${pdfOffsetXPx}px, ${pdfOffsetYPx}px) scale(${pdfScale}); transform-origin: top left; }
      .pdf-preview input, .pdf-preview textarea, .pdf-preview select { display: none !important; }
      .pdf-preview table [data-name] { display: block; width: 100% !important; box-sizing: border-box; }
      .pdf-preview .header-info { width: 100%; box-sizing: border-box; }
      .pdf-preview .header-info > span { display: flex; align-items: flex-end; flex: 1; min-width: 0; gap: 4px; }
      .pdf-preview .header-info > span > [data-name] { flex: 1; min-width: 0; width: auto !important; box-sizing: border-box; }
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
  <body><div class="pdf-sheet"><div class="pdf-page"><div class="pdf-transform">${clone.outerHTML}</div></div></div></body>
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
    openBackendPdfPreview('/api/pdf/water_replacement_record/generate')
  }
}

const previewPdf = () => {
  if (pdfForm.value) {
    openBackendPdfPreview('/api/pdf/water_replacement_record/preview')
  }
}

// 显示数据分析模态窗口
const showAnalysisModal = () => {
  showModal.value = true
  // 重置分析结果
  Object.keys(analysisResults).forEach(key => {
    analysisResults[key] = ''
  })
  // 重置范围
  analysisRange.start = ''
  analysisRange.end = ''
}

// 关闭数据分析模态窗口
const closeAnalysisModal = () => {
  showModal.value = false
}

// 生成范围内的随机数
const getRandomInRange = (min, max, decimals = 2) => {
  if (min === '' || max === '') return ''
  const minNum = parseFloat(min)
  const maxNum = parseFloat(max)
  if (isNaN(minNum) || isNaN(maxNum)) return ''
  const random = Math.random() * (maxNum - minNum) + minNum
  return random.toFixed(decimals)
}

// 自动分析并填充数据
const autoAnalyzeAndFill = () => {
  // 将用户输入的列号（1-4）转换为数组索引（0-3）
  const start = (parseInt(analysisRange.start) || 1) - 1
  const end = (parseInt(analysisRange.end) || 4) - 1
  
  // 确保范围在有效范围内
  const validStart = Math.max(0, start)
  const validEnd = Math.min(3, end)
  
  if (validStart > validEnd) {
    alert('起始列不能大于结束列')
    return
  }
  
  // 收集数据
  const ringVolumes = []
  const waterLevels = []
  const tankAreas = []
  const pitVolumes = []
  const sampleMasses = []
  const wetDensities = []
  const containerMasses = []
  const wetSampleMasses = []
  const drySampleMasses = []
  const moistureContents = []
  const measuredDryDensities = []
  const relativeDensities = []
  
  for (let col = validStart; col <= validEnd; col++) {
    // 收集基本字段数据（每列2个数据点）
    for (let i = col * 2; i < col * 2 + 2; i++) {
      // 套环体积
      const ringVolume = parseFloat(formData[`ringVolume_page${currentPage.value}_${i}`])
      if (!isNaN(ringVolume)) ringVolumes.push(ringVolume)
      
      // 储水桶水位（取初始和终了的平均值）
      const initialWaterLevel = parseFloat(formData[`initialWaterLevel_page${currentPage.value}_${i}`])
      const finalWaterLevel = parseFloat(formData[`finalWaterLevel_page${currentPage.value}_${i}`])
      if (!isNaN(initialWaterLevel) && !isNaN(finalWaterLevel)) {
        waterLevels.push((initialWaterLevel + finalWaterLevel) / 2)
      }
      
      // 储水桶面积
      const tankArea = parseFloat(formData[`tankArea_page${currentPage.value}_${i}`])
      if (!isNaN(tankArea)) tankAreas.push(tankArea)
      
      // 试坑体积
      const pitVolume = parseFloat(formData[`pitVolume_page${currentPage.value}_${i}`])
      if (!isNaN(pitVolume)) pitVolumes.push(pitVolume)
      
      // 试样质量
      const sampleMass = parseFloat(formData[`sampleMass_page${currentPage.value}_${i}`])
      if (!isNaN(sampleMass)) sampleMasses.push(sampleMass)
      
      // 试样湿密度
      const wetDensity = parseFloat(formData[`wetDensity_page${currentPage.value}_${i}`])
      if (!isNaN(wetDensity)) wetDensities.push(wetDensity)
      
      // 实测干密度
      const measuredDryDensity = parseFloat(formData[`measuredDryDensity_page${currentPage.value}_${i}`])
      if (!isNaN(measuredDryDensity)) measuredDryDensities.push(measuredDryDensity)
      
      // 相对密度
      const relativeDensity = parseFloat(formData[`relativeDensity_page${currentPage.value}_${i}`])
      if (!isNaN(relativeDensity)) relativeDensities.push(relativeDensity)
    }
    
    // 收集试样含水率相关字段数据（每列4个数据点）
    for (let i = col * 4; i < col * 4 + 4; i++) {
      // 容器质量
      const containerMass = parseFloat(formData[`containerMass_page${currentPage.value}_${i}`])
      if (!isNaN(containerMass)) containerMasses.push(containerMass)
      
      // 容器+湿样质量
      const wetSampleMass = parseFloat(formData[`wetSampleMass_page${currentPage.value}_${i}`])
      if (!isNaN(wetSampleMass)) wetSampleMasses.push(wetSampleMass)
      
      // 容器+干样质量
      const drySampleMass = parseFloat(formData[`drySampleMass_page${currentPage.value}_${i}`])
      if (!isNaN(drySampleMass)) drySampleMasses.push(drySampleMass)
      
      // 含水率
      const moistureContent = parseFloat(formData[`moistureContent_page${currentPage.value}_${i}`])
      if (!isNaN(moistureContent)) moistureContents.push(moistureContent)
    }
  }
  
  // 计算范围（如果用户没有手动输入）
  if (!analysisResults.ringVolumeMin || !analysisResults.ringVolumeMax) {
    if (ringVolumes.length > 0) {
      const min = Math.min(...ringVolumes)
      const max = Math.max(...ringVolumes)
      analysisResults.ringVolumeMin = min.toFixed(2)
      analysisResults.ringVolumeMax = max.toFixed(2)
    } else {
      // 默认范围
      analysisResults.ringVolumeMin = '100.00'
      analysisResults.ringVolumeMax = '200.00'
    }
  }
  
  if (!analysisResults.waterLevelMin || !analysisResults.waterLevelMax) {
    if (waterLevels.length > 0) {
      const min = Math.min(...waterLevels)
      const max = Math.max(...waterLevels)
      analysisResults.waterLevelMin = min.toFixed(2)
      analysisResults.waterLevelMax = max.toFixed(2)
    } else {
      // 默认范围
      analysisResults.waterLevelMin = '10.00'
      analysisResults.waterLevelMax = '30.00'
    }
  }
  
  if (!analysisResults.tankAreaMin || !analysisResults.tankAreaMax) {
    if (tankAreas.length > 0) {
      const min = Math.min(...tankAreas)
      const max = Math.max(...tankAreas)
      analysisResults.tankAreaMin = min.toFixed(2)
      analysisResults.tankAreaMax = max.toFixed(2)
    } else {
      // 默认范围
      analysisResults.tankAreaMin = '100.00'
      analysisResults.tankAreaMax = '300.00'
    }
  }
  
  if (!analysisResults.pitVolumeMin || !analysisResults.pitVolumeMax) {
    if (pitVolumes.length > 0) {
      const min = Math.min(...pitVolumes)
      const max = Math.max(...pitVolumes)
      analysisResults.pitVolumeMin = min.toFixed(2)
      analysisResults.pitVolumeMax = max.toFixed(2)
    } else {
      // 默认范围
      analysisResults.pitVolumeMin = '1000.00'
      analysisResults.pitVolumeMax = '3000.00'
    }
  }
  
  if (!analysisResults.sampleMassMin || !analysisResults.sampleMassMax) {
    if (sampleMasses.length > 0) {
      const min = Math.min(...sampleMasses)
      const max = Math.max(...sampleMasses)
      analysisResults.sampleMassMin = min.toFixed(2)
      analysisResults.sampleMassMax = max.toFixed(2)
    } else {
      // 默认范围
      analysisResults.sampleMassMin = '1500.00'
      analysisResults.sampleMassMax = '4500.00'
    }
  }
  
  if (!analysisResults.wetDensityMin || !analysisResults.wetDensityMax) {
    if (wetDensities.length > 0) {
      const min = Math.min(...wetDensities)
      const max = Math.max(...wetDensities)
      analysisResults.wetDensityMin = min.toFixed(2)
      analysisResults.wetDensityMax = max.toFixed(2)
    } else {
      // 默认范围
      analysisResults.wetDensityMin = '1.80'
      analysisResults.wetDensityMax = '2.20'
    }
  }
  
  if (!analysisResults.containerMassMin || !analysisResults.containerMassMax) {
    if (containerMasses.length > 0) {
      const min = Math.min(...containerMasses)
      const max = Math.max(...containerMasses)
      analysisResults.containerMassMin = min.toFixed(2)
      analysisResults.containerMassMax = max.toFixed(2)
    } else {
      // 默认范围
      analysisResults.containerMassMin = '10.00'
      analysisResults.containerMassMax = '30.00'
    }
  }
  
  if (!analysisResults.wetSampleMassMin || !analysisResults.wetSampleMassMax) {
    if (wetSampleMasses.length > 0) {
      const min = Math.min(...wetSampleMasses)
      const max = Math.max(...wetSampleMasses)
      analysisResults.wetSampleMassMin = min.toFixed(2)
      analysisResults.wetSampleMassMax = max.toFixed(2)
    } else {
      // 默认范围
      analysisResults.wetSampleMassMin = '50.00'
      analysisResults.wetSampleMassMax = '100.00'
    }
  }
  
  if (!analysisResults.drySampleMassMin || !analysisResults.drySampleMassMax) {
    if (drySampleMasses.length > 0) {
      const min = Math.min(...drySampleMasses)
      const max = Math.max(...drySampleMasses)
      analysisResults.drySampleMassMin = min.toFixed(2)
      analysisResults.drySampleMassMax = max.toFixed(2)
    } else {
      // 默认范围
      analysisResults.drySampleMassMin = '40.00'
      analysisResults.drySampleMassMax = '90.00'
    }
  }
  
  if (!analysisResults.moistureContentMin || !analysisResults.moistureContentMax) {
    if (moistureContents.length > 0) {
      const min = Math.min(...moistureContents)
      const max = Math.max(...moistureContents)
      analysisResults.moistureContentMin = min.toFixed(2)
      analysisResults.moistureContentMax = max.toFixed(2)
    } else {
      // 默认范围
      analysisResults.moistureContentMin = '5.00'
      analysisResults.moistureContentMax = '15.00'
    }
  }
  
  if (!analysisResults.measuredDryDensityMin || !analysisResults.measuredDryDensityMax) {
    if (measuredDryDensities.length > 0) {
      const min = Math.min(...measuredDryDensities)
      const max = Math.max(...measuredDryDensities)
      analysisResults.measuredDryDensityMin = min.toFixed(2)
      analysisResults.measuredDryDensityMax = max.toFixed(2)
    } else {
      // 默认范围
      analysisResults.measuredDryDensityMin = '1.60'
      analysisResults.measuredDryDensityMax = '1.90'
    }
  }
  
  if (!analysisResults.relativeDensityMin || !analysisResults.relativeDensityMax) {
    if (relativeDensities.length > 0) {
      const min = Math.min(...relativeDensities)
      const max = Math.max(...relativeDensities)
      analysisResults.relativeDensityMin = min.toFixed(2)
      analysisResults.relativeDensityMax = max.toFixed(2)
    } else {
      // 默认范围
      analysisResults.relativeDensityMin = '0.60'
      analysisResults.relativeDensityMax = '0.90'
    }
  }
  
  // 填充数据
  for (let col = validStart; col <= validEnd; col++) {
    // 填充基本字段（每列2个数据点）
    for (let i = col * 2; i < col * 2 + 2; i++) {
      // 套环体积
      if (!formData[`ringVolume_page${currentPage.value}_${i}`]) {
        const randomValue = getRandomInRange(analysisResults.ringVolumeMin, analysisResults.ringVolumeMax, 2)
        if (randomValue) {
          formData[`ringVolume_page${currentPage.value}_${i}`] = randomValue
        }
      }
      
      // 储水桶水位
      if (!formData[`initialWaterLevel_page${currentPage.value}_${i}`]) {
        const randomValue = getRandomInRange(analysisResults.waterLevelMin, analysisResults.waterLevelMax, 2)
        if (randomValue) {
          formData[`initialWaterLevel_page${currentPage.value}_${i}`] = randomValue
        }
      }
      if (!formData[`finalWaterLevel_page${currentPage.value}_${i}`]) {
        const initial = parseFloat(formData[`initialWaterLevel_page${currentPage.value}_${i}`]) || parseFloat(analysisResults.waterLevelMin)
        const randomValue = getRandomInRange(initial - 5, initial - 1, 2)
        if (randomValue) {
          formData[`finalWaterLevel_page${currentPage.value}_${i}`] = randomValue
        }
      }
      
      // 储水桶面积
      if (!formData[`tankArea_page${currentPage.value}_${i}`]) {
        const randomValue = getRandomInRange(analysisResults.tankAreaMin, analysisResults.tankAreaMax, 2)
        if (randomValue) {
          formData[`tankArea_page${currentPage.value}_${i}`] = randomValue
        }
      }
      
      // 试坑体积
      if (!formData[`pitVolume_page${currentPage.value}_${i}`]) {
        const randomValue = getRandomInRange(analysisResults.pitVolumeMin, analysisResults.pitVolumeMax, 2)
        if (randomValue) {
          formData[`pitVolume_page${currentPage.value}_${i}`] = randomValue
        }
      }
      
      // 试样质量
      if (!formData[`sampleMass_page${currentPage.value}_${i}`]) {
        const randomValue = getRandomInRange(analysisResults.sampleMassMin, analysisResults.sampleMassMax, 2)
        if (randomValue) {
          formData[`sampleMass_page${currentPage.value}_${i}`] = randomValue
        }
      }
      
      // 试样湿密度
      if (!formData[`wetDensity_page${currentPage.value}_${i}`]) {
        const randomValue = getRandomInRange(analysisResults.wetDensityMin, analysisResults.wetDensityMax, 2)
        if (randomValue) {
          formData[`wetDensity_page${currentPage.value}_${i}`] = randomValue
        }
      }
      
      // 实测干密度
      if (!formData[`measuredDryDensity_page${currentPage.value}_${i}`]) {
        let min, max
        // 如果有最大干密度和最小干密度，使用它们作为范围
        if (formData.maxDryDensity && formData.minDryDensity) {
          min = parseFloat(formData.minDryDensity)
          max = parseFloat(formData.maxDryDensity)
          // 确保范围合理
          if (!isNaN(min) && !isNaN(max) && min < max) {
            // 实测干密度通常在最小和最大干密度之间
            const randomValue = getRandomInRange(min, max, 2)
            if (randomValue) {
              formData[`measuredDryDensity_page${currentPage.value}_${i}`] = randomValue
            }
          } else {
            // 使用默认范围
            const randomValue = getRandomInRange(analysisResults.measuredDryDensityMin, analysisResults.measuredDryDensityMax, 2)
            if (randomValue) {
              formData[`measuredDryDensity_page${currentPage.value}_${i}`] = randomValue
            }
          }
        } else {
          // 使用默认范围
          const randomValue = getRandomInRange(analysisResults.measuredDryDensityMin, analysisResults.measuredDryDensityMax, 2)
          if (randomValue) {
            formData[`measuredDryDensity_page${currentPage.value}_${i}`] = randomValue
          }
        }
      }
      
      // 相对密度
      if (!formData[`relativeDensity_page${currentPage.value}_${i}`]) {
        let min, max
        // 如果有设计相对密度，使用它作为参考
        if (formData.relativeDensity) {
          const designValue = parseFloat(formData.relativeDensity)
          if (!isNaN(designValue)) {
            // 相对密度通常在0-1之间，围绕设计值波动
            min = Math.max(0, designValue - 0.1)
            max = Math.min(1, designValue + 0.1)
            const randomValue = getRandomInRange(min, max, 2)
            if (randomValue) {
              formData[`relativeDensity_page${currentPage.value}_${i}`] = randomValue
            }
          } else {
            // 使用默认范围
            const randomValue = getRandomInRange(analysisResults.relativeDensityMin, analysisResults.relativeDensityMax, 2)
            if (randomValue) {
              formData[`relativeDensity_page${currentPage.value}_${i}`] = randomValue
            }
          }
        } else {
          // 使用默认范围
          const randomValue = getRandomInRange(analysisResults.relativeDensityMin, analysisResults.relativeDensityMax, 2)
          if (randomValue) {
            formData[`relativeDensity_page${currentPage.value}_${i}`] = randomValue
          }
        }
      }
    }
    
    // 填充试样含水率相关字段（每列4个数据点）
    for (let i = col * 4; i < col * 4 + 4; i++) {
      // 容器质量
      if (!formData[`containerMass_page${currentPage.value}_${i}`]) {
        const randomValue = getRandomInRange(analysisResults.containerMassMin, analysisResults.containerMassMax, 2)
        if (randomValue) {
          formData[`containerMass_page${currentPage.value}_${i}`] = randomValue
        }
      }
      
      // 容器+湿样质量
      if (!formData[`wetSampleMass_page${currentPage.value}_${i}`]) {
        const containerMass = parseFloat(formData[`containerMass_page${currentPage.value}_${i}`]) || parseFloat(analysisResults.containerMassMin)
        const randomValue = getRandomInRange(containerMass + 30, containerMass + 70, 2)
        if (randomValue) {
          formData[`wetSampleMass_page${currentPage.value}_${i}`] = randomValue
        }
      }
      
      // 容器+干样质量
      if (!formData[`drySampleMass_page${currentPage.value}_${i}`]) {
        const wetSampleMass = parseFloat(formData[`wetSampleMass_page${currentPage.value}_${i}`]) || parseFloat(analysisResults.wetSampleMassMin)
        const randomValue = getRandomInRange(wetSampleMass - 15, wetSampleMass - 5, 2)
        if (randomValue) {
          formData[`drySampleMass_page${currentPage.value}_${i}`] = randomValue
        }
      }
      
      // 含水率
      if (!formData[`moistureContent_page${currentPage.value}_${i}`]) {
        const randomValue = getRandomInRange(analysisResults.moistureContentMin, analysisResults.moistureContentMax, 2)
        if (randomValue) {
          formData[`moistureContent_page${currentPage.value}_${i}`] = randomValue
        }
      }
    }
    
    // 计算平均含水率（每列2个平均值，每个平均值基于2个含水率数据）
    for (let avgIdx = 0; avgIdx < 2; avgIdx++) {
      const moisture1 = parseFloat(formData[`moistureContent_page${currentPage.value}_${col * 4 + avgIdx * 2}`])
      const moisture2 = parseFloat(formData[`moistureContent_page${currentPage.value}_${col * 4 + avgIdx * 2 + 1}`])
      if (!isNaN(moisture1) && !isNaN(moisture2)) {
        const avgMoisture = ((moisture1 + moisture2) / 2).toFixed(2)
        formData[`avgMoisture_page${currentPage.value}_${col * 2 + avgIdx}`] = avgMoisture
      }
    }
    
    // 计算平均实测干密度
    const dryDensity1 = parseFloat(formData[`measuredDryDensity_page${currentPage.value}_${col * 2}`])
    const dryDensity2 = parseFloat(formData[`measuredDryDensity_page${currentPage.value}_${col * 2 + 1}`])
    if (!isNaN(dryDensity1) && !isNaN(dryDensity2)) {
      const avgDryDensity = ((dryDensity1 + dryDensity2) / 2).toFixed(2)
      formData[`avgMeasuredDryDensity_page${currentPage.value}_${col}`] = avgDryDensity
    }
  }
  
  // 更新设计参数
  if (!formData.maxDryDensity) {
    if (measuredDryDensities.length > 0) {
      const max = Math.max(...measuredDryDensities)
      formData.maxDryDensity = (max + 0.1).toFixed(2)
    } else {
      formData.maxDryDensity = '2.00'
    }
  }
  
  if (!formData.minDryDensity) {
    if (measuredDryDensities.length > 0) {
      const min = Math.min(...measuredDryDensities)
      formData.minDryDensity = (min - 0.1).toFixed(2)
    } else {
      formData.minDryDensity = '1.50'
    }
  }
  
  if (!formData.relativeDensity) {
    if (relativeDensities.length > 0) {
      const avg = relativeDensities.reduce((sum, val) => sum + val, 0) / relativeDensities.length
      formData.relativeDensity = avg.toFixed(2)
    } else {
      formData.relativeDensity = '0.75'
    }
  }
  
  if (!formData.waterDensity) {
    formData.waterDensity = '1.000'
  }
  
  alert('自动分析并填充完成')
  closeAnalysisModal()
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

        .waterReplacementRecord-container {
            font-family: 'SimSun', 'Songti SC', serif;
            width: 210mm;
            margin: 0 auto;
            padding: 24px;
            background-color: var(--bg-card);
            border-radius: 8px;
            box-shadow: var(--shadow);
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
            flex-wrap: wrap;
            margin-bottom: 5px;
            font-weight: bold;
        }
        .header-info span {
            margin-right: 20px;
            margin-bottom: 5px;
            white-space: nowrap;
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
            font-size: 14px;
        }
        .label {
            font-weight: bold;
            white-space: nowrap;
        }
        .left-align {
            text-align: left;
            padding-left: 10px;
        }
        input[type="text"], textarea, select, .table-textarea {
            width: 98%;
            border: 1px solid #b3d9ff;
            border-radius: 4px;
            outline: none;
            font-family: 'SimSun', 'Songti SC', serif;
            font-size: 14px;
            color: #000000;
            background-color: transparent;
            text-align: center;
            padding: 2px 4px;
        }
        .table-textarea {
            width: 100%;
            height: 100%;
        }
        input[type="text"]:focus, textarea:focus, select:focus, .table-textarea:focus {
            background-color: #f0f8ff;
            border-color: #3498db;
        }
        input[type="text"]:disabled, textarea:disabled, select:disabled, .table-textarea:disabled {
            background-color: transparent;
            color: #000000;
            font-family: 'SimSun', 'Songti SC', serif;
            font-size: 14px;
        }
        input[type="text"]:disabled:focus, textarea:disabled:focus, select:disabled:focus, .table-textarea:disabled:focus {
            background-color: transparent;
            outline: none;
            border-color: black;
        }
        textarea, .table-textarea {
            resize: none;
            overflow: hidden;
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
        
        .modal-input {
            width: 120px;
            padding: 6px 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            margin-right: 10px;
            font-size: 14px;
        }
        
        /* 隐藏输入框的上下箭头按钮 */
        input[type=number]::-webkit-inner-spin-button, 
        input[type=number]::-webkit-outer-spin-button {
            -webkit-appearance: none;
            margin: 0;
        }
        
        input[type=number] {
            -moz-appearance: textfield;
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
        
        .btn-info {
            background-color: #17a2b8;
            border-color: #17a2b8;
            color: white;
        }
        
        .btn-info:hover {
            background-color: #138496;
            border-color: #117a8b;
        }
        
        .left-align {
            text-align: left;
        }
        
        .footer-info {
            display: flex;
            justify-content: space-between;
            margin-top: 10px;
            font-size: 16px;
            font-weight: bold;
        }
        .page-footer {
            margin-top: 5px;
            display: flex;
            justify-content: space-between;
            font-size: 14px;
        }
        @media print {
            @page {
                size: A4 portrait;
                margin: 0;
            }
            .waterReplacementRecord-container {
                width: 100%;
                margin: 0;
                padding: 0;
            }
            .no-print {
                display: none;
            }
        }
    
</style>
