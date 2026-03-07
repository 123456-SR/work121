<template>
  <div class="waterReplacementRecord-container">


    <div class="no-print toolbar">
      <div class="toolbar-left">
        <button @click="goToList" class="link-button">&lt; 返回列表</button>
        <span v-if="!draftMode" class="record-nav">
          <button
            @click="prevPage"
            :disabled="currentPage <= 0"
            class="btn btn-secondary btn-small"
          >
            上一页
          </button>
          <span class="record-nav-info">
            页面 {{ currentPage + 1 }} / {{ totalPages }}
          </span>
          <button
            @click="nextPage"
            :disabled="currentPage >= totalPages - 1"
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

        <template v-if="formData.id && !draftMode">
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
        <span>试验日期：<input type="text" v-model="formData.testDate"   name="testDate" style="width: 150px; border-bottom: 1px solid black;" :disabled="!isEditable"></span>
    </div>
    <div class="header-info">
        <span>依据标准：<input type="text" v-model="formData.standard"   name="standard" style="width: 150px; border-bottom: 1px solid black; text-align: left;" :disabled="!isEditable"></span>
        <span>最大干密度：<input type="text" v-model="formData.maxDryDensity"   name="maxDryDensity" style="width: 80px; border-bottom: 1px solid black;" :disabled="!isEditable"> g/cm³</span>
        <span>最小干密度：<input type="text" v-model="formData.minDryDensity"   name="minDryDensity" style="width: 80px; border-bottom: 1px solid black;" :disabled="!isEditable"> g/cm³</span>
        <span>最优含水率：<input type="text" v-model="formData.optMoisture"   name="optMoisture" style="width: 80px; border-bottom: 1px solid black;" :disabled="!isEditable"> %</span>
        <span>相对密度：<input type="text" v-model="formData.relativeDensity"   name="relativeDensity" style="width: 80px; border-bottom: 1px solid black;" :disabled="!isEditable"></span>
        <span>统一编号：<input type="text" v-model="formData.unifiedNumber"   name="unifiedNumber" style="width: 150px; border-bottom: 1px solid black;" disabled></span>
    </div>
    <div class="header-info">
        <span>水的密度：<input type="text" v-model="formData.waterDensity"   name="waterDensity" style="width: 80px; border-bottom: 1px solid black;" :disabled="!isEditable"> g/cm³</span>
        <span>仪器设备：<input type="text" v-model="formData.equipment"   name="equipment" style="width: 150px; border-bottom: 1px solid black;" :disabled="!isEditable"></span>
        <span>设计压实度：<input type="text" v-model="formData.designCompaction"   name="designCompaction" style="width: 80px; border-bottom: 1px solid black;" :disabled="!isEditable"></span>
        <span>检测类别：<input type="text" v-model="formData.testCategory"   name="testCategory" style="width: 100px; border-bottom: 1px solid black;" :disabled="!isEditable"></span>
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



  </div>
</template>

<script setup>
import { reactive, ref, onMounted, inject, computed } from 'vue'
import axios from 'axios'

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
  optMoisture: '',
  relativeDensity: '',
  unifiedNumber: '',
  waterDensity: '',
  equipment: '',
  designCompaction: '',
  testCategory: '',
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
    if (!formData.id) {
        alert('请先保存记录')
        return
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
                     if (!formData.recordTester) {
                        formData.recordTester = user.userName || user.username
                     }
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
                     if (!formData.recordReviewer) {
                        formData.recordReviewer = user.userName || user.username
                     }
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
                          'optMoisture', 'relativeDensity', 'waterDensity', 'equipment', 
                          'designCompaction', 'testCategory', 'remarks', 'totalPages']
      
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
  if (record.testCategory !== null && record.testCategory !== undefined) formData.testCategory = record.testCategory
  if ((record.standard !== null && record.standard !== undefined) || (record.testBasis !== null && record.testBasis !== undefined)) formData.standard = record.standard || record.testBasis
  if (record.testDate !== null && record.testDate !== undefined) formData.testDate = record.testDate
  
  // 这些字段通常不在委托单中，只在记录表的 JSON 中
  if (record.maxDryDensity !== null && record.maxDryDensity !== undefined) formData.maxDryDensity = record.maxDryDensity
  if (record.minDryDensity !== null && record.minDryDensity !== undefined) formData.minDryDensity = record.minDryDensity
  if (record.optMoisture !== null && record.optMoisture !== undefined) formData.optMoisture = record.optMoisture
  if (record.relativeDensity !== null && record.relativeDensity !== undefined) formData.relativeDensity = record.relativeDensity
  if (record.waterDensity !== null && record.waterDensity !== undefined) formData.waterDensity = record.waterDensity
  if (record.equipment !== null && record.equipment !== undefined) formData.equipment = record.equipment
  if (record.designCompaction !== null && record.designCompaction !== undefined) formData.designCompaction = record.designCompaction

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
    'optMoisture', 'relativeDensity', 'waterDensity', 'equipment', 
    'designCompaction', 'testCategory', 'remarks', 'totalPages'
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
      // 检查委托单状态是否为审核通过（状态值为5）
      if (entrustmentData.status === 5) {
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

const generatePdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/water_replacement_record/generate'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}

const previewPdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/water_replacement_record/preview'
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
            flex-wrap: wrap;
            padding: 0 30px;
            box-sizing: border-box;
            width: 100%;
            max-width: 100%;
            overflow-x: hidden;
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
            width: 260mm; /* Using wider width similar to sand replacement record */
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
            flex-wrap: wrap;
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
