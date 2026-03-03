<template>
  <div class="cuttingRingRecord-container">


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
            升级为已审核
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
    </div>

    <form id="pdfForm" ref="pdfForm" method="post">
    <h2>原位密度检测记录表（环刀法）</h2>

    <div class="header-info">
        <span>委托单位：<input type="text" v-model="formData.entrustingUnit"   name="entrustingUnit" style="width: 200px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>工程名称：<input type="text" v-model="formData.projectName"   name="projectName" style="width: 200px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>统一编号：<input type="text" v-model="formData.unifiedNumber"   name="unifiedNumber" style="width: 150px; border-bottom: 1px solid black;"></span>
    </div>
    <div class="header-info">
        <span>施工部位：<input type="text" v-model="formData.constructionLocation"   name="constructionLocation" style="width: 200px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>最大干密度 (g/cm³)：<input type="text" v-model="formData.maxDryDensity"   name="maxDryDensity" style="width: 80px; border-bottom: 1px solid black;"></span>
        <span>最优含水率 (%)：<input type="text" v-model="formData.optMoisture"   name="optMoisture" style="width: 80px; border-bottom: 1px solid black;"></span>
    </div>
    <!-- 隐藏字段：检测类别不在页面上展示，但仍随表单提交 -->
    <input type="hidden" v-model="formData.testType" name="testType" />
    <div class="header-info">
        <span>依据标准：<input type="text" v-model="formData.standard"   name="standard" style="width: 200px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>设计压实度：<input type="text" v-model="formData.designCompaction"   name="designCompaction" style="width: 80px; border-bottom: 1px solid black;"></span>
        <span>检测日期：<input type="text" v-model="formData.testDate"   name="testDate" style="width: 150px; border-bottom: 1px solid black;"></span>
    </div>

    <table>
        <thead>
            <tr>
                <td rowspan="2" class="label" style="width: 5%;">样品<br>编号</td>
                <td rowspan="2" class="label" style="width: 10%;">检测部位<br>(桩号、<br>高程)</td>
                <td rowspan="2" class="label" style="width: 5%;">样品<br>状态</td>
                <td rowspan="2" class="label">环刀<br>号</td>
                <td rowspan="2" class="label">环刀<br>质量g</td>
                <td rowspan="2" class="label">环刀+湿<br>土质量g</td>
                <td rowspan="2" class="label">环刀体<br>积cm³</td>
                <td colspan="5" class="label">含水率测定</td>
                <td rowspan="2" class="label">平均<br>含水<br>率%</td>
                <td rowspan="2" class="label">湿密度<br>g/cm³</td>
                <td rowspan="2" class="label">干密度<br>g/cm³</td>
                <td rowspan="2" class="label">平均干<br>密度<br>g/cm³</td>
                <td rowspan="2" class="label">压实<br>度%</td>
            </tr>
            <tr>
                <td class="label">盒号</td>
                <td class="label">盒质量<br>g</td>
                <td class="label">盒+湿土<br>质量g</td>
                <td class="label">盒+干土<br>质量g</td>
                <td class="label">含水率<br>%</td>
            </tr>
        </thead>
        <tbody>
            <template v-for="(n, i_idx) in 4" :key="i_idx">
            <!-- 含水率测定改为 4 行结构 -->
            <tr>
                <!-- 样品基本信息列：首行展示 -->
                <td rowspan="4"><input type="text" :name="'sampleNo_page' + currentPage + '_' + i_idx" v-model="formData['sampleNo_page' + currentPage + '_' + i_idx]"></td>
                <td rowspan="4"><input type="text" :name="'location_page' + currentPage + '_' + i_idx" v-model="formData['location_page' + currentPage + '_' + i_idx]"></td>
                <td rowspan="4"><input type="text" :name="'status_page' + currentPage + '_' + i_idx" v-model="formData['status_page' + currentPage + '_' + i_idx]"></td>
                <!-- 环刀相关：第一把环刀（上 2 行一格） -->
                <td rowspan="2"><input type="text" :name="'ringNo_page' + currentPage + '_' + i_idx" v-model="formData['ringNo_page' + currentPage + '_' + i_idx]"></td>
                <td rowspan="2"><input type="text" :name="'ringMass_page' + currentPage + '_' + i_idx" v-model="formData['ringMass_page' + currentPage + '_' + i_idx]"></td>
                <td rowspan="2"><input type="text" :name="'ringWetMass_page' + currentPage + '_' + i_idx" v-model="formData['ringWetMass_page' + currentPage + '_' + i_idx]"></td>
                <td rowspan="2"><input type="text" :name="'ringVolume_page' + currentPage + '_' + i_idx" v-model="formData['ringVolume_page' + currentPage + '_' + i_idx]"></td>
                
                <!-- Moisture Box 1 -->
                <td><input type="text" :name="'boxNo1_page' + currentPage + '_' + i_idx" v-model="formData['boxNo1_page' + currentPage + '_' + i_idx]"></td>
                <td><input type="text" :name="'boxMass1_page' + currentPage + '_' + i_idx" v-model="formData['boxMass1_page' + currentPage + '_' + i_idx]"></td>
                <td><input type="text" :name="'boxWetMass1_page' + currentPage + '_' + i_idx" v-model="formData['boxWetMass1_page' + currentPage + '_' + i_idx]"></td>
                <td><input type="text" :name="'boxDryMass1_page' + currentPage + '_' + i_idx" v-model="formData['boxDryMass1_page' + currentPage + '_' + i_idx]"></td>
                <td><input type="text" :name="'moisture1_page' + currentPage + '_' + i_idx" v-model="formData['moisture1_page' + currentPage + '_' + i_idx]"></td>

                <!-- 结果列：第一把环刀对应的数据（行 1-2） -->
                <td rowspan="2"><input type="text" :name="'avgMoisture1_page' + currentPage + '_' + i_idx" v-model="formData['avgMoisture1_page' + currentPage + '_' + i_idx]"></td>
                <td rowspan="2"><input type="text" :name="'wetDensity1_page' + currentPage + '_' + i_idx" v-model="formData['wetDensity1_page' + currentPage + '_' + i_idx]"></td>
                <td rowspan="2"><input type="text" :name="'dryDensity1_page' + currentPage + '_' + i_idx" v-model="formData['dryDensity1_page' + currentPage + '_' + i_idx]"></td>
                <td rowspan="4"><input type="text" :name="'avgDryDensity1_page' + currentPage + '_' + i_idx" v-model="formData['avgDryDensity1_page' + currentPage + '_' + i_idx]"></td>
                <td rowspan="4"><input type="text" :name="'compaction1_page' + currentPage + '_' + i_idx" v-model="formData['compaction1_page' + currentPage + '_' + i_idx]"></td>
            </tr>
            <tr>
                <!-- Moisture Box 2 -->
                <td><input type="text" :name="'boxNo2_page' + currentPage + '_' + i_idx" v-model="formData['boxNo2_page' + currentPage + '_' + i_idx]"></td>
                <td><input type="text" :name="'boxMass2_page' + currentPage + '_' + i_idx" v-model="formData['boxMass2_page' + currentPage + '_' + i_idx]"></td>
                <td><input type="text" :name="'boxWetMass2_page' + currentPage + '_' + i_idx" v-model="formData['boxWetMass2_page' + currentPage + '_' + i_idx]"></td>
                <td><input type="text" :name="'boxDryMass2_page' + currentPage + '_' + i_idx" v-model="formData['boxDryMass2_page' + currentPage + '_' + i_idx]"></td>
                <td><input type="text" :name="'moisture2_page' + currentPage + '_' + i_idx" v-model="formData['moisture2_page' + currentPage + '_' + i_idx]"></td>
            </tr>
            <tr>
                <!-- 第二把环刀（下 2 行一格），使用独立字段 ringNo2_* 等。
                     注意：必须先画环刀列，再画含水率列，才能保证列与表头对齐。 -->
                <td rowspan="2"><input type="text" :name="'ringNo2_page' + currentPage + '_' + i_idx" v-model="formData['ringNo2_page' + currentPage + '_' + i_idx]"></td>
                <td rowspan="2"><input type="text" :name="'ringMass2_page' + currentPage + '_' + i_idx" v-model="formData['ringMass2_page' + currentPage + '_' + i_idx]"></td>
                <td rowspan="2"><input type="text" :name="'ringWetMass2_page' + currentPage + '_' + i_idx" v-model="formData['ringWetMass2_page' + currentPage + '_' + i_idx]"></td>
                <td rowspan="2"><input type="text" :name="'ringVolume2_page' + currentPage + '_' + i_idx" v-model="formData['ringVolume2_page' + currentPage + '_' + i_idx]"></td>
                <!-- Moisture Box 3 -->
                <td><input type="text" :name="'boxNo3_page' + currentPage + '_' + i_idx" v-model="formData['boxNo3_page' + currentPage + '_' + i_idx]"></td>
                <td><input type="text" :name="'boxMass3_page' + currentPage + '_' + i_idx" v-model="formData['boxMass3_page' + currentPage + '_' + i_idx]"></td>
                <td><input type="text" :name="'boxWetMass3_page' + currentPage + '_' + i_idx" v-model="formData['boxWetMass3_page' + currentPage + '_' + i_idx]"></td>
                <td><input type="text" :name="'boxDryMass3_page' + currentPage + '_' + i_idx" v-model="formData['boxDryMass3_page' + currentPage + '_' + i_idx]"></td>
                <td><input type="text" :name="'moisture3_page' + currentPage + '_' + i_idx" v-model="formData['moisture3_page' + currentPage + '_' + i_idx]"></td>
                <!-- 结果列：第二把环刀对应的数据（行 3-4） -->
                <td rowspan="2"><input type="text" :name="'avgMoisture2_page' + currentPage + '_' + i_idx" v-model="formData['avgMoisture2_page' + currentPage + '_' + i_idx]"></td>
                <td rowspan="2"><input type="text" :name="'wetDensity2_page' + currentPage + '_' + i_idx" v-model="formData['wetDensity2_page' + currentPage + '_' + i_idx]"></td>
                <td rowspan="2"><input type="text" :name="'dryDensity2_page' + currentPage + '_' + i_idx" v-model="formData['dryDensity2_page' + currentPage + '_' + i_idx]"></td>

            </tr>
            <tr>
                <!-- Moisture Box 4 -->
                <td><input type="text" :name="'boxNo4_page' + currentPage + '_' + i_idx" v-model="formData['boxNo4_page' + currentPage + '_' + i_idx]"></td>
                <td><input type="text" :name="'boxMass4_page' + currentPage + '_' + i_idx" v-model="formData['boxMass4_page' + currentPage + '_' + i_idx]"></td>
                <td><input type="text" :name="'boxWetMass4_page' + currentPage + '_' + i_idx" v-model="formData['boxWetMass4_page' + currentPage + '_' + i_idx]"></td>
                <td><input type="text" :name="'boxDryMass4_page' + currentPage + '_' + i_idx" v-model="formData['boxDryMass4_page' + currentPage + '_' + i_idx]"></td>
                <td><input type="text" :name="'moisture4_page' + currentPage + '_' + i_idx" v-model="formData['moisture4_page' + currentPage + '_' + i_idx]"></td>
            </tr>
            </template>
            <!-- Empty rows to fill space if needed, or just these 5 sample blocks (10 rows) -->
        </tbody>
        <tfoot>
            <tr>
                <td colspan="2" class="label">备注</td>
                <td colspan="15" class="left-align" style="height: 60px; vertical-align: top;">
                    <textarea v-model="formData.remarks"  name="remarks" style="width: 100%; height: 100%;"></textarea>
                </td>
            </tr>
        </tfoot>
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
import { reactive, ref, onMounted, inject, defineProps, computed } from 'vue'
import axios from 'axios'

const props = defineProps({
  id: {
    type: String,
    required: false
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

const goToList = () => {
  if (navigateTo) {
    navigateTo('CuttingRingRecordList')
  }
}

const pdfForm = ref(null)

// 单条记录 + 多页数据
const currentRecord = ref(null)
const currentPage = ref(0)  // 当前页面索引（从0开始）
const totalPages = computed(() => {
  // 从 formData 中获取总页数，如果没有则默认为1
  return formData.totalPages || 1
})

const formData = reactive({
      id: '',
      entrustmentId: '',
      entrustingUnit: '',
      projectName: '',
      unifiedNumber: '',
      constructionLocation: '',
      maxDryDensity: '',
      optMoisture: '',
      testType: '',
      standard: '',
      designCompaction: '',
      testDate: '',
      remarks: '',
      // Role fields
      recordTester: '',
      recordReviewer: '',
      filler: '',
      reviewerSignature: '',
      testerSignature: '',
      status: 0,
      // 页面管理
      totalPages: 1  // 总页数
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
    if (status === null || status === undefined || status === '') {
        return '#9E9E9E' // Grey (草稿)
    }
    const s = parseInt(status)
    if (isNaN(s)) {
        return '#9E9E9E' // Grey (草稿)
    }
    switch(s) {
        // 记录表状态 (0-5)
        case 0: return '#9E9E9E' // Grey (草稿)
        case 1: return '#2196F3' // Blue (待审核)
        case 2: return '#F44336' // Red (已打回)
        case 3: return '#FF9800' // Orange (待签字)
        case 4: return '#17a2b8' // Info (待批准)
        case 5: return '#4CAF50' // Green (已通过)
        // 报告表状态 (10-15)
        case 10: return '#9E9E9E' // Grey (报告草稿)
        case 11: return '#2196F3' // Blue (报告待审核)
        case 12: return '#F44336' // Red (报告已打回)
        case 13: return '#FF9800' // Orange (报告待签字)
        case 14: return '#17a2b8' // Info (报告待批准)
        case 15: return '#4CAF50' // Green (报告已通过)
        // 结果表状态 (20-25)
        case 20: return '#9E9E9E' // Grey (结果草稿)
        case 21: return '#2196F3' // Blue (结果待审核)
        case 22: return '#F44336' // Red (结果已打回)
        case 23: return '#FF9800' // Orange (结果待签字)
        case 24: return '#17a2b8' // Info (结果待批准)
        case 25: return '#4CAF50' // Green (结果已通过)
        default: return '#9E9E9E' // Grey (默认草稿)
    }
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
    
    // 保存当前页面的数据
    saveCurrentPageData()
    saveCurrentRecordState()
    
    const user = JSON.parse(localStorage.getItem('userInfo'))
    if (!user || !user.username) {
        alert('请先登录')
        return
    }

    try {
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
    }

    const request = {
        tableType: 'CUTTING_RING',
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

    if (action === 'AUDIT_PASS') {
        // Role check: Only recordReviewer can audit
        if (formData.recordReviewer && user.username !== formData.recordReviewer && user.fullName !== formData.recordReviewer) {
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
        request.signatureData = signatureData
    }
    
    // Add SIGN_APPROVE support if needed (though CuttingRing might use AUDIT_PASS for approval flow depending on implementation, 
    // but assuming standard flow: SUBMIT -> AUDIT_PASS -> APPROVED. If there is a separate approval step, it should be handled.
    // Based on other files, often AUDIT_PASS leads to status 4 or 5.
    // Cutting Ring code shows: AUDIT_PASS -> 5 (if status 1->5? No, previous code didn't show next status logic explicitly in submitWorkflow, 
    // it relies on backend or nextHandler? 
    // Wait, the original code had:
    // else if (action === 'REJECT') ...
    
    // Let's just fix the REJECT check for now.
    
    if (action === 'REJECT') {
        // Role check: Only recordReviewer can reject
        if (formData.recordReviewer && user.username !== formData.recordReviewer && user.fullName !== formData.recordReviewer) {
            alert('您不是该单据的记录复核人 (' + formData.recordReviewer + ')，无权操作')
            return
        }
    }
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
  for (let i_idx = 0; i_idx < 4; i_idx++) {
    const pagePrefix = '_page' + pageIndex + '_' + i_idx
    formData['location' + pagePrefix] = ''
    formData['boxMass2' + pagePrefix] = ''
    formData['boxWetMass2' + pagePrefix] = ''
    formData['ringWetMass' + pagePrefix] = ''
    formData['ringMass' + pagePrefix] = ''
    // 第二把环刀相关字段
    formData['ringWetMass2' + pagePrefix] = ''
    formData['ringMass2' + pagePrefix] = ''
    formData['boxDryMass2' + pagePrefix] = ''
    formData['moisture1' + pagePrefix] = ''
    formData['ringNo' + pagePrefix] = ''
    formData['ringNo2' + pagePrefix] = ''
    formData['compaction' + pagePrefix] = ''
    formData['boxWetMass1' + pagePrefix] = ''
    formData['boxNo2' + pagePrefix] = ''
    formData['boxDryMass1' + pagePrefix] = ''
    formData['sampleNo' + pagePrefix] = ''
    formData['wetDensity' + pagePrefix] = ''
    formData['boxNo1' + pagePrefix] = ''
    formData['dryDensity' + pagePrefix] = ''
    formData['avgDryDensity' + pagePrefix] = ''
    formData['ringVolume' + pagePrefix] = ''
    formData['ringVolume2' + pagePrefix] = ''
    formData['avgMoisture' + pagePrefix] = ''
    formData['boxMass1' + pagePrefix] = ''
    formData['moisture2' + pagePrefix] = ''
    formData['status' + pagePrefix] = ''
    // 结果字段：为每把环刀分别初始化（1=第一把环刀，2=第二把环刀）
    formData['avgMoisture1' + pagePrefix] = ''
    formData['avgMoisture2' + pagePrefix] = ''
    formData['wetDensity1' + pagePrefix] = ''
    formData['wetDensity2' + pagePrefix] = ''
    formData['dryDensity1' + pagePrefix] = ''
    formData['dryDensity2' + pagePrefix] = ''
    formData['avgDryDensity1' + pagePrefix] = ''
    formData['avgDryDensity2' + pagePrefix] = ''
    formData['compaction1' + pagePrefix] = ''
    formData['compaction2' + pagePrefix] = ''
    // 新增含水率盒 3、4 相关字段
    formData['boxNo3' + pagePrefix] = ''
    formData['boxMass3' + pagePrefix] = ''
    formData['boxWetMass3' + pagePrefix] = ''
    formData['boxDryMass3' + pagePrefix] = ''
    formData['moisture3' + pagePrefix] = ''
    formData['boxNo4' + pagePrefix] = ''
    formData['boxMass4' + pagePrefix] = ''
    formData['boxWetMass4' + pagePrefix] = ''
    formData['boxDryMass4' + pagePrefix] = ''
    formData['moisture4' + pagePrefix] = ''
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

onMounted(() => {
  // 初始化第一页的字段
  currentPage.value = 0
  formData.totalPages = 1
  initDynamicFieldsForPage(0)
  
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

const formatDate = (dateVal) => {
  if (!dateVal) return ''
  const d = new Date(dateVal)
  if (isNaN(d.getTime())) return ''
  const year = d.getFullYear()
  const month = ('0' + (d.getMonth() + 1)).slice(-2)
  const day = ('0' + d.getDate()).slice(-2)
  return `${year}-${month}-${day}`
}

// 加载指定页面的数据
const loadPageData = (pageIndex, parsedJson = null) => {
  const json = parsedJson || (currentRecord.value && currentRecord.value.dataJson ? JSON.parse(currentRecord.value.dataJson) : {})
  
  // 初始化该页面的字段
  initDynamicFieldsForPage(pageIndex)
  
  // 从 JSON 中加载该页面的数据
  for (let i_idx = 0; i_idx < 4; i_idx++) {
    const pagePrefix = '_page' + pageIndex + '_' + i_idx
    const fieldNames = [
      'location', 'boxMass2', 'boxWetMass2', 'ringWetMass', 'ringMass', 
      'boxDryMass2', 'moisture1', 'ringNo', 'compaction', 'boxWetMass1',
      'boxNo2', 'boxDryMass1', 'sampleNo', 'wetDensity', 'boxNo1',
      'dryDensity', 'avgDryDensity', 'ringVolume', 'avgMoisture', 
      'boxMass1', 'moisture2', 'status',
      // 第一、第二把环刀的结果字段
      'avgMoisture1', 'avgMoisture2',
      'wetDensity1', 'wetDensity2',
      'dryDensity1', 'dryDensity2',
      'avgDryDensity1', 'avgDryDensity2',
      'compaction1', 'compaction2',
      // 第二把环刀相关字段
      'ringNo2', 'ringMass2', 'ringWetMass2', 'ringVolume2',
      // 新增含水率盒 3、4 字段
      'boxNo3', 'boxMass3', 'boxWetMass3', 'boxDryMass3', 'moisture3',
      'boxNo4', 'boxMass4', 'boxWetMass4', 'boxDryMass4', 'moisture4'
    ]
    
    fieldNames.forEach(fieldName => {
      // 先尝试新格式：fieldName_pageX_Y
      const newKey = fieldName + pagePrefix
      if (json[newKey] !== undefined && json[newKey] !== null) {
        formData[newKey] = json[newKey]
      } else {
        // 兼容旧格式：fieldName_index（需要计算原始索引）
        const oldIndex = pageIndex * 5 + i_idx
        const oldKey = fieldName + '_' + oldIndex
        
        // 特殊处理：sampleId -> sampleNo（后端映射后的字段名）
        if (fieldName === 'sampleNo') {
          if (json['sampleId_' + oldIndex] !== undefined && json['sampleId_' + oldIndex] !== null) {
            formData[newKey] = json['sampleId_' + oldIndex]
          }
        } else {
          if (json[oldKey] !== undefined && json[oldKey] !== null) {
            formData[newKey] = json[oldKey]
          }
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
  formData.reviewerSignature = normalizeSignatureSrc(record.reviewSignaturePhoto || '')
  formData.testerSignature = normalizeSignatureSrc(record.inspectSignaturePhoto || '')

  let parsedJson = {}
  if (record.dataJson) {
    try {
      parsedJson = JSON.parse(record.dataJson)
      // 定义基础字段列表（这些字段应该优先从委托单获取，JSON 中的空值不应该覆盖）
      const basicFields = ['entrustingUnit', 'projectName', 'unifiedNumber', 'constructionLocation', 
                          'testType', 'standard', 'entrustmentId', 'totalPages']
      
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
          // 对于其他全局字段（如 remarks, maxDryDensity 等），直接合并
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
          // 检测数据字段的最大索引，计算页数（假设每页5个样品）
          const dataFieldPrefixes = [
            'sampleId_', 'location_', 'status_', 'ringNo_', 'ringMass_', 'ringWetMass_', 'ringVolume_',
            'boxNo1_', 'boxNo2_', 'boxMass1_', 'boxMass2_', 'boxWetMass1_', 'boxWetMass2_',
            'boxDryMass1_', 'boxDryMass2_', 'moisture1_', 'moisture2_', 'avgMoisture_',
            'wetDensity_', 'dryDensity_', 'avgDryDensity_', 'compaction_'
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
          
          // 计算页数：每页4个样品
          const samplesPerPage = 4
          formData.totalPages = maxIndex >= 0 ? Math.ceil((maxIndex + 1) / samplesPerPage) : 1
          
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
              // 计算应该属于哪一页和哪个位置（每页 4 个样品）
              const fieldIndex = parseInt(suffix)
              const pageIndex = Math.floor(fieldIndex / samplesPerPage)
              const positionInPage = fieldIndex % samplesPerPage
              
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

  if (record.constructionPart !== null && record.constructionPart !== undefined) formData.constructionLocation = record.constructionPart
  if (record.testCategory !== null && record.testCategory !== undefined) formData.testType = record.testCategory
  if ((record.standard !== null && record.standard !== undefined) || (record.testBasis !== null && record.testBasis !== undefined)) formData.standard = record.standard || record.testBasis
  if (record.testDate !== null && record.testDate !== undefined) formData.testDate = formatDate(record.testDate)
  
  // 这些字段通常不在委托单中，只在记录表的 JSON 中
  if (record.maxDryDensity !== null && record.maxDryDensity !== undefined) formData.maxDryDensity = record.maxDryDensity
  if (record.optMoisture !== null && record.optMoisture !== undefined) formData.optMoisture = record.optMoisture
  if (record.designCompaction !== null && record.designCompaction !== undefined) formData.designCompaction = record.designCompaction

  // Ensure entity fields override JSON if present
  if (record.reviewSignaturePhoto) formData.reviewerSignature = normalizeSignatureSrc(record.reviewSignaturePhoto)
  if (record.inspectSignaturePhoto) formData.testerSignature = normalizeSignatureSrc(record.inspectSignaturePhoto)

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

// 保存当前页面的数据到 formData（实际上数据已经在 formData 中，这个函数主要是为了确保数据完整性）
const saveCurrentPageData = () => {
  // 当前页面的所有字段已经在 formData 中，带页面后缀
  // 不需要额外操作，因为数据是双向绑定的
}

const saveCurrentRecordState = () => {
  if (!currentRecord.value) return
  
  // 保存当前页面的数据
  saveCurrentPageData()
  
  // 更新记录的基本信息
  currentRecord.value.id = formData.id
  currentRecord.value.entrustmentId = formData.entrustmentId
  currentRecord.value.reviewSignaturePhoto = formData.reviewerSignature
  currentRecord.value.inspectSignaturePhoto = formData.testerSignature
  
  currentRecord.value.recordTester = formData.recordTester
  currentRecord.value.recordReviewer = formData.recordReviewer
  currentRecord.value.tester = formData.recordTester
  currentRecord.value.reviewer = formData.recordReviewer
  
  // 更新 dataJson：包含所有页面的数据和全局字段
  const dataJsonObj = { ...formData }
  // 排除不需要存储的字段
  delete dataJsonObj.tester
  delete dataJsonObj.reviewer
  delete dataJsonObj.id
  delete dataJsonObj.entrustmentId
  
  currentRecord.value.dataJson = JSON.stringify(dataJsonObj)
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

const loadData = async (paramId) => {
  const idOrWtNum = paramId || props.wtNum || props.id
  if (idOrWtNum) {
    try {
      // 1. First, always load entrustment info to fill basic fields
      let entrustmentData = null
      try {
        const entrustmentResponse = await axios.get('/api/jc-core-wt-info/detail', {
          params: { unifiedNumber: idOrWtNum }
        })
        // If not found by unifiedNumber, try by ID
        if (!entrustmentResponse.data.success || !entrustmentResponse.data.data) {
          const byIdResponse = await axios.get('/api/jc-core-wt-info/by-id', {
            params: { id: idOrWtNum }
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
          formData.entrustmentId = idOrWtNum
          formData.unifiedNumber = entrustmentData.wtNum || idOrWtNum
          formData.entrustingUnit = entrustmentData.clientUnit || ''
          formData.projectName = entrustmentData.projectName || ''
          formData.constructionLocation = entrustmentData.constructionPart || ''
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
      const response = await axios.get('/api/cutting-ring/get-by-entrustment-id', {
        params: { entrustmentId: idOrWtNum }
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
          entrustmentId: idOrWtNum,
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
}

const saveData = async () => {
  try {
    // 保存当前页面的数据
    saveCurrentPageData()
    saveCurrentRecordState()
    
    const dataJsonObj = { ...formData }
    delete dataJsonObj.tester
    delete dataJsonObj.reviewer
    delete dataJsonObj.id
    delete dataJsonObj.entrustmentId

    const dataToSave = {
      id: formData.id,
      entrustmentId: formData.entrustmentId || props.id,
      dataJson: JSON.stringify(dataJsonObj),
      // Roles
      recordTester: formData.recordTester,
      recordReviewer: formData.recordReviewer,
      tester: formData.recordTester,
      reviewer: formData.recordReviewer,
      filler: formData.filler,
      
      // Signatures
      testerSignature: formData.testerSignature,
      reviewerSignature: formData.reviewerSignature
    }
    
    const response = await axios.post('/api/cutting-ring/save', dataToSave)
    if (response.data.success) {
      // 更新状态为待签字
      formData.status = 3
      alert('保存成功，状态已更新为待签字')
      // If new record, update ID
      if (!formData.id && response.data.data && response.data.data.id) {
        formData.id = response.data.data.id
        if (currentRecord.value) {
          currentRecord.value.id = response.data.data.id
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
      const currentName = user.fullName || user.nickName || currentAccount

      // Match Tester (记录检测人)
      if (!formData.recordTester || formData.recordTester === currentName || formData.recordTester === currentAccount) {
        if (!formData.recordTester) {
            formData.recordTester = currentName
        }
        formData.testerSignature = imgSrc
        signed = true
        signType = '检测人'
      }
      
      // Match Reviewer (记录审核人) - 如果检测人已经签了，或者当前用户是审核人
      if (!signed && (!formData.recordReviewer || formData.recordReviewer === currentName || formData.recordReviewer === currentAccount)) {
        if (!formData.recordReviewer) {
            formData.recordReviewer = currentName
        }
        formData.reviewerSignature = imgSrc
        signed = true
        signType = '审核人'
      }

      if (signed) {
        // 保存签名
        await saveData()
        // 如果两个人都签了，调用工作流处理，将状态从待签字(3)变为已签字待提交(4)
        if (formData.testerSignature && formData.reviewerSignature) {
          await submitWorkflow('SIGN_TEST')
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

const printDocument = () => {
  window.print()
}

const generatePdf = () => {
  if (pdfForm.value) {
    // 确保当前页面的数据和委托人数据被包含在表单中
    includeCurrentPageData()
    pdfForm.value.action = '/api/pdf/cutting_ring_record/generate'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}

const previewPdf = () => {
  if (pdfForm.value) {
    // 确保当前页面的数据和委托人数据被包含在表单中
    includeCurrentPageData()
    pdfForm.value.action = '/api/pdf/cutting_ring_record/preview'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}

// 将当前页面的数据和委托人数据添加到表单中
const includeCurrentPageData = () => {
  // 首先移除所有现有的隐藏字段
  const existingHiddenFields = pdfForm.value.querySelectorAll('input[type="hidden"]')
  existingHiddenFields.forEach(field => field.remove())
  
  // 只包含当前页面的数据
  const pageIndex = currentPage.value
  for (let i_idx = 0; i_idx < 4; i_idx++) {
    const pagePrefix = '_page' + pageIndex + '_' + i_idx
    const fieldNames = [
      'sampleNo', 'location', 'status', 'ringNo', 'ringMass', 'ringWetMass', 'ringVolume',
      'boxNo1', 'boxMass1', 'boxWetMass1', 'boxDryMass1', 'moisture1',
      'boxNo2', 'boxMass2', 'boxWetMass2', 'boxDryMass2', 'moisture2',
      'boxNo3', 'boxMass3', 'boxWetMass3', 'boxDryMass3', 'moisture3',
      'boxNo4', 'boxMass4', 'boxWetMass4', 'boxDryMass4', 'moisture4',
      'avgMoisture1', 'wetDensity1', 'dryDensity1',
      'avgMoisture2', 'wetDensity2', 'dryDensity2',
      'avgDryDensity1', 'compaction1',
      'ringNo2', 'ringMass2', 'ringWetMass2', 'ringVolume2'
    ]
    
    fieldNames.forEach(fieldName => {
      const fieldKey = fieldName + pagePrefix
      if (formData[fieldKey] !== undefined && formData[fieldKey] !== null) {
        const input = document.createElement('input')
        input.type = 'hidden'
        input.name = fieldKey
        input.value = formData[fieldKey]
        pdfForm.value.appendChild(input)
      }
    })
  }
  
  // 添加当前页面索引
  const currentPageInput = document.createElement('input')
  currentPageInput.type = 'hidden'
  currentPageInput.name = 'currentPage'
  currentPageInput.value = currentPage.value
  pdfForm.value.appendChild(currentPageInput)
  
  // 添加总页数信息
  const totalPagesInput = document.createElement('input')
  totalPagesInput.type = 'hidden'
  totalPagesInput.name = 'totalPages'
  totalPagesInput.value = formData.totalPages
  pdfForm.value.appendChild(totalPagesInput)
  
  // 添加委托人数据
  const clientDataFields = ['entrustingUnit', 'projectName', 'unifiedNumber', 'constructionLocation', 'testType', 'standard']
  clientDataFields.forEach(fieldName => {
    if (formData[fieldName] !== undefined && formData[fieldName] !== null) {
      const input = document.createElement('input')
      input.type = 'hidden'
      input.name = fieldName
      input.value = formData[fieldName]
      pdfForm.value.appendChild(input)
    }
  })
  
  // 添加承接人的电子签名
  if (formData.testerSignature) {
    const testerSignatureInput = document.createElement('input')
    testerSignatureInput.type = 'hidden'
    testerSignatureInput.name = 'testerSignature'
    testerSignatureInput.value = formData.testerSignature
    pdfForm.value.appendChild(testerSignatureInput)
  }
  
  if (formData.reviewerSignature) {
    const reviewerSignatureInput = document.createElement('input')
    reviewerSignatureInput.type = 'hidden'
    reviewerSignatureInput.name = 'reviewerSignature'
    reviewerSignatureInput.value = formData.reviewerSignature
    pdfForm.value.appendChild(reviewerSignatureInput)
  }
  
  // 添加承接人信息
  if (formData.recordTester) {
    const testerInput = document.createElement('input')
    testerInput.type = 'hidden'
    testerInput.name = 'recordTester'
    testerInput.value = formData.recordTester
    pdfForm.value.appendChild(testerInput)
  }
  
  if (formData.recordReviewer) {
    const reviewerInput = document.createElement('input')
    reviewerInput.type = 'hidden'
    reviewerInput.name = 'recordReviewer'
    reviewerInput.value = formData.recordReviewer
    pdfForm.value.appendChild(reviewerInput)
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
/* Add signature overlay style */
.signature-overlay {
  position: absolute;
  pointer-events: none;
  z-index: 10;
}
.signature-overlay img {
  width: 80px;
  height: auto;
  opacity: 0.8;
}

        .cuttingRingRecord-container {
            font-family: 'SimSun', 'Songti SC', serif;
            width: 260mm;
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
            .cuttingRingRecord-container {
                width: 100%;
                margin: 0;
                padding: 0;
            }
            .no-print {
                display: none;
            }
        }
    
</style>
