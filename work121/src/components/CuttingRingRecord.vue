<template>
  <div class="cuttingRingRecord-container">


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

        <template v-if="formData.id && !draftMode">
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
          @click="showAnalysisModal"
          class="btn btn-secondary btn-small"
          :disabled="!isEditable"
        >
          数据分析
        </button>
        <button
          @click="saveData"
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
    <h2>原位密度检测记录表（环刀法）</h2>

    <div class="header-info" style="display: none;">
        <span>统一编号：<input type="text" v-model="formData.unifiedNumber"   name="unifiedNumber" style="width: 150px; border-bottom: 1px solid black;" disabled></span>
    </div>
    <div class="header-info">
        <span>施工部位：<input type="text" v-model="formData.constructionLocation"   name="constructionLocation" style="width: 100px; border-bottom: 1px solid black; text-align: left;" :disabled="!isEditable"></span>
        <span>最大干密度 (g/cm³)：<input type="text" v-model="formData.maxDryDensity"   name="maxDryDensity" style="width: 50px; border-bottom: 1px solid black;" :disabled="!isEditable"></span>
        <span>最优含水率 (%)：<input type="text" v-model="formData.optMoisture"   name="optMoisture" style="width: 50px; border-bottom: 1px solid black;" :disabled="!isEditable"></span>
        <span>检测类别：<input type="text" v-model="formData.testType"   name="testType" style="width: 60px; border-bottom: 1px solid black;" :disabled="!isEditable"></span>
    </div>
    <div class="header-info">
        <span>依据标准：<input type="text" v-model="formData.standard"   name="standard" style="width: 150px; border-bottom: 1px solid black; text-align: left;" :disabled="!isEditable"></span>
        <span>设计压实度：<input type="text" v-model="formData.designCompaction"   name="designCompaction" style="width: 80px; border-bottom: 1px solid black;" :disabled="!isEditable"></span>
        <span>检测日期：<input type="text" v-model="formData.testDate"   name="testDate" style="width: 120px; border-bottom: 1px solid black;" :disabled="!isEditable"></span>
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
                <td rowspan="4"><textarea :name="'sampleNo_page' + currentPage + '_' + i_idx" v-model="formData['sampleNo_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
                <td rowspan="4"><textarea :name="'location_page' + currentPage + '_' + i_idx" v-model="formData['location_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
                <td rowspan="4"><textarea :name="'status_page' + currentPage + '_' + i_idx" v-model="formData['status_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
                <!-- 环刀相关：第一把环刀（上 2 行一格） -->
                <td rowspan="2"><textarea :name="'ringNo_page' + currentPage + '_' + i_idx" v-model="formData['ringNo_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
                <td rowspan="2"><textarea :name="'ringMass_page' + currentPage + '_' + i_idx" v-model="formData['ringMass_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
                <td rowspan="2"><textarea :name="'ringWetMass_page' + currentPage + '_' + i_idx" v-model="formData['ringWetMass_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
                <td rowspan="2"><textarea :name="'ringVolume_page' + currentPage + '_' + i_idx" v-model="formData['ringVolume_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
                
                <!-- Moisture Box 1 -->
                <td><textarea :name="'boxNo1_page' + currentPage + '_' + i_idx" v-model="formData['boxNo1_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
                <td><textarea :name="'boxMass1_page' + currentPage + '_' + i_idx" v-model="formData['boxMass1_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
                <td><textarea :name="'boxWetMass1_page' + currentPage + '_' + i_idx" v-model="formData['boxWetMass1_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
                <td><textarea :name="'boxDryMass1_page' + currentPage + '_' + i_idx" v-model="formData['boxDryMass1_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
                <td><textarea :name="'moisture1_page' + currentPage + '_' + i_idx" v-model="formData['moisture1_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>

                <!-- 结果列：第一把环刀对应的数据（行 1-2） -->
                <td rowspan="2"><textarea :name="'avgMoisture1_page' + currentPage + '_' + i_idx" v-model="formData['avgMoisture1_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
                <td rowspan="2"><textarea :name="'wetDensity1_page' + currentPage + '_' + i_idx" v-model="formData['wetDensity1_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
                <td rowspan="2"><textarea :name="'dryDensity1_page' + currentPage + '_' + i_idx" v-model="formData['dryDensity1_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
                <td rowspan="4"><textarea :name="'avgDryDensity1_page' + currentPage + '_' + i_idx" v-model="formData['avgDryDensity1_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
                <td rowspan="4"><textarea :name="'compaction1_page' + currentPage + '_' + i_idx" v-model="formData['compaction1_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
            </tr>
            <tr>
                <!-- Moisture Box 2 -->
                <td><textarea :name="'boxNo2_page' + currentPage + '_' + i_idx" v-model="formData['boxNo2_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
                <td><textarea :name="'boxMass2_page' + currentPage + '_' + i_idx" v-model="formData['boxMass2_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
                <td><textarea :name="'boxWetMass2_page' + currentPage + '_' + i_idx" v-model="formData['boxWetMass2_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
                <td><textarea :name="'boxDryMass2_page' + currentPage + '_' + i_idx" v-model="formData['boxDryMass2_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
                <td><textarea :name="'moisture2_page' + currentPage + '_' + i_idx" v-model="formData['moisture2_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
            </tr>
            <tr>
                <!-- 第二把环刀（下 2 行一格），使用独立字段 ringNo2_* 等。
                     注意：必须先画环刀列，再画含水率列，才能保证列与表头对齐。 -->
                <td rowspan="2"><textarea :name="'ringNo2_page' + currentPage + '_' + i_idx" v-model="formData['ringNo2_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
                <td rowspan="2"><textarea :name="'ringMass2_page' + currentPage + '_' + i_idx" v-model="formData['ringMass2_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
                <td rowspan="2"><textarea :name="'ringWetMass2_page' + currentPage + '_' + i_idx" v-model="formData['ringWetMass2_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
                <td rowspan="2"><textarea :name="'ringVolume2_page' + currentPage + '_' + i_idx" v-model="formData['ringVolume2_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
                <!-- Moisture Box 3 -->
                <td><textarea :name="'boxNo3_page' + currentPage + '_' + i_idx" v-model="formData['boxNo3_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
                <td><textarea :name="'boxMass3_page' + currentPage + '_' + i_idx" v-model="formData['boxMass3_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
                <td><textarea :name="'boxWetMass3_page' + currentPage + '_' + i_idx" v-model="formData['boxWetMass3_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
                <td><textarea :name="'boxDryMass3_page' + currentPage + '_' + i_idx" v-model="formData['boxDryMass3_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
                <td><textarea :name="'moisture3_page' + currentPage + '_' + i_idx" v-model="formData['moisture3_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
                <!-- 结果列：第二把环刀对应的数据（行 3-4） -->
                <td rowspan="2"><textarea :name="'avgMoisture2_page' + currentPage + '_' + i_idx" v-model="formData['avgMoisture2_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
                <td rowspan="2"><textarea :name="'wetDensity2_page' + currentPage + '_' + i_idx" v-model="formData['wetDensity2_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
                <td rowspan="2"><textarea :name="'dryDensity2_page' + currentPage + '_' + i_idx" v-model="formData['dryDensity2_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>

            </tr>
            <tr>
                <!-- Moisture Box 4 -->
                <td><textarea :name="'boxNo4_page' + currentPage + '_' + i_idx" v-model="formData['boxNo4_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
                <td><textarea :name="'boxMass4_page' + currentPage + '_' + i_idx" v-model="formData['boxMass4_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
                <td><textarea :name="'boxWetMass4_page' + currentPage + '_' + i_idx" v-model="formData['boxWetMass4_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
                <td><textarea :name="'boxDryMass4_page' + currentPage + '_' + i_idx" v-model="formData['boxDryMass4_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
                <td><textarea :name="'moisture4_page' + currentPage + '_' + i_idx" v-model="formData['moisture4_page' + currentPage + '_' + i_idx]" :disabled="!isEditable" class="table-textarea"></textarea></td>
            </tr>
            </template>
            <!-- Empty rows to fill space if needed, or just these 5 sample blocks (10 rows) -->
        </tbody>
        <tfoot>
            <tr>
                <td colspan="2" class="label">备注</td>
                <td colspan="15" class="left-align" style="height: 60px; vertical-align: top;">
                    <textarea v-model="formData.remarks"  name="remarks" style="width: 100%; height: 100%;" :disabled="!isEditable"></textarea>
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

    <!-- 数据分析模态窗口 -->
    <div v-if="showModal" class="modal-overlay" @click="closeAnalysisModal">
      <div class="modal-content" @click.stop style="position: relative;">
        <h3>数据分析</h3>
        <button class="close-btn" @click="closeAnalysisModal">&times;</button>
        <div class="modal-body">
          <div class="form-group">
            <label>数据范围：</label>
            <input type="number" v-model.number="analysisRange.start" min="1" max="4" placeholder="从" class="modal-input">
            <span>行</span>
            <input type="number" v-model.number="analysisRange.end" min="1" max="4" placeholder="至" class="modal-input">
            <span>行</span>
          </div>
          
          <div class="form-group">
            <label>环刀质量 (g)：</label>
            <input type="number" v-model.number="analysisResults.ringMassMin" step="0.01" placeholder="最小值" class="modal-input">
            <input type="number" v-model.number="analysisResults.ringMassMax" step="0.01" placeholder="最大值" class="modal-input">
          </div>
          
          <div class="form-group">
            <label>环刀+湿土质量 (g)：</label>
            <input type="number" v-model.number="analysisResults.ringWetMassMin" step="0.01" placeholder="最小值" class="modal-input">
            <input type="number" v-model.number="analysisResults.ringWetMassMax" step="0.01" placeholder="最大值" class="modal-input">
          </div>
          
          <div class="form-group">
            <label>环刀体积 (cm³)：</label>
            <input type="number" v-model.number="analysisResults.ringVolumeMin" step="0.01" placeholder="最小值" class="modal-input">
            <input type="number" v-model.number="analysisResults.ringVolumeMax" step="0.01" placeholder="最大值" class="modal-input">
          </div>
          
          <div class="form-group">
            <label>盒质量 (g)：</label>
            <input type="number" v-model.number="analysisResults.boxMassMin" step="0.01" placeholder="最小值" class="modal-input">
            <input type="number" v-model.number="analysisResults.boxMassMax" step="0.01" placeholder="最大值" class="modal-input">
          </div>
          
          <div class="form-group">
            <label>盒+湿土质量 (g)：</label>
            <input type="number" v-model.number="analysisResults.boxWetMassMin" step="0.01" placeholder="最小值" class="modal-input">
            <input type="number" v-model.number="analysisResults.boxWetMassMax" step="0.01" placeholder="最大值" class="modal-input">
          </div>
          
          <div class="form-group">
            <label>盒+干土质量 (g)：</label>
            <input type="number" v-model.number="analysisResults.boxDryMassMin" step="0.01" placeholder="最小值" class="modal-input">
            <input type="number" v-model.number="analysisResults.boxDryMassMax" step="0.01" placeholder="最大值" class="modal-input">
          </div>
          
          <div class="form-group">
            <label>含水率 (%)：</label>
            <input type="number" v-model.number="analysisResults.moistureMin" step="0.01" placeholder="最小值" class="modal-input">
            <input type="number" v-model.number="analysisResults.moistureMax" step="0.01" placeholder="最大值" class="modal-input">
          </div>
          
          <div class="form-group">
            <label>平均含水率 (%)：</label>
            <input type="number" v-model.number="analysisResults.avgMoistureMin" step="0.01" placeholder="最小值" class="modal-input">
            <input type="number" v-model.number="analysisResults.avgMoistureMax" step="0.01" placeholder="最大值" class="modal-input">
          </div>
          
          <div class="form-group">
            <label>湿密度 (g/cm³)：</label>
            <input type="number" v-model.number="analysisResults.wetDensityMin" step="0.01" placeholder="最小值" class="modal-input">
            <input type="number" v-model.number="analysisResults.wetDensityMax" step="0.01" placeholder="最大值" class="modal-input">
          </div>
          
          <div class="form-group">
            <label>干密度 (g/cm³)：</label>
            <input type="number" v-model.number="analysisResults.dryDensityMin" step="0.01" placeholder="最小值" class="modal-input">
            <input type="number" v-model.number="analysisResults.dryDensityMax" step="0.01" placeholder="最大值" class="modal-input">
          </div>
          
          <div class="form-group">
            <label>压实度 (%)：</label>
            <input type="number" v-model.number="analysisResults.compactionMin" step="0.01" placeholder="最小值" class="modal-input">
            <input type="number" v-model.number="analysisResults.compactionMax" step="0.01" placeholder="最大值" class="modal-input">
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
import { reactive, ref, onMounted, inject, defineProps, computed } from 'vue'
import axios from 'axios'

// 数据分析相关变量
const showModal = ref(false)
const analysisRange = reactive({ start: '', end: '' })
const analysisResults = reactive({
  ringMassMin: '',
  ringMassMax: '',
  ringWetMassMin: '',
  ringWetMassMax: '',
  ringVolumeMin: '',
  ringVolumeMax: '',
  boxMassMin: '',
  boxMassMax: '',
  boxWetMassMin: '',
  boxWetMassMax: '',
  boxDryMassMin: '',
  boxDryMassMax: '',
  moistureMin: '',
  moistureMax: '',
  avgMoistureMin: '',
  avgMoistureMax: '',
  wetDensityMin: '',
  wetDensityMax: '',
  dryDensityMin: '',
  dryDensityMax: '',
  compactionMin: '',
  compactionMax: ''
})

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

const isEditable = computed(() => {
  return formData.status == 0 || formData.status == 2
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
        case 4: return '已签字待提交'
        case 5: return '审核通过'
        // 报告表状态 (10-15)
        case 10: return '草稿'
        case 11: return '已提交待审核'
        case 12: return '已打回'
        case 14: return '已签字待提交'
        case 15: return '审核通过'
        // 结果表状态 (20-25)
        case 20: return '草稿'
        case 21: return '已提交待审核'
        case 22: return '已打回'
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
        case 4: return '#17a2b8' // Info (待批准)
        case 5: return '#4CAF50' // Green (已通过)
        // 报告表状态 (10-15)
        case 10: return '#9E9E9E' // Grey (报告草稿)
        case 11: return '#2196F3' // Blue (报告待审核)
        case 12: return '#F44336' // Red (报告已打回)
        case 14: return '#17a2b8' // Info (报告待批准)
        case 15: return '#4CAF50' // Green (报告已通过)
        // 结果表状态 (20-25)
        case 20: return '#9E9E9E' // Grey (结果草稿)
        case 21: return '#2196F3' // Blue (结果待审核)
        case 22: return '#F44336' // Red (结果已打回)
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
                     await saveData()
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
                     // Save signature to database
                     await saveData()
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
        // 检查委托单状态是否为审核通过（状态值为5），支持字符串和数字比较
      if (parseInt(entrustmentData.status) === 5) {
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
      reviewerSignature: formData.reviewerSignature,
      
      // Status
      status: formData.status
    }
    
    const response = await axios.post('/api/cutting-ring/save', dataToSave)
    if (response.data.success) {
      // 保存后状态不变
      alert('保存成功')
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
      const currentName = user.userName

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
      if (!signed && (formData.recordReviewer === currentName || formData.recordReviewer === currentAccount)) {
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

const openBackendPdfPreview = (actionUrl) => {
  if (!pdfForm.value) return
  const container = pdfForm.value.closest('.cuttingRingRecord-container')
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
      .pdf-preview.cuttingRingRecord-container { width: 285mm; height: 198mm; max-width: 285mm; min-width: 0; margin: 0; padding: 0; box-sizing: border-box; display: flex; flex-direction: column; }
      .pdf-preview #pdfForm { flex: 1; min-height: 0; display: flex; flex-direction: column; }
      .pdf-preview #pdfForm > table { flex: 0 0 auto; height: auto; }
      .pdf-preview table { width: 100%; height: auto; box-sizing: border-box; font-size: 16px; table-layout: fixed; word-break: break-all; }
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
    // 确保当前页面的数据和委托人数据被包含在表单中
    includeCurrentPageData()
    openBackendPdfPreview('/api/pdf/cutting_ring_record/generate')
  }
}

const previewPdf = () => {
  if (pdfForm.value) {
    // 确保当前页面的数据和委托人数据被包含在表单中
    includeCurrentPageData()
    openBackendPdfPreview('/api/pdf/cutting_ring_record/preview')
  }
}

// 数据分析相关函数
const showAnalysisModal = () => {
  showModal.value = true
}

const closeAnalysisModal = () => {
  showModal.value = false
}

const autoAnalyzeAndFill = () => {
  const start = (parseInt(analysisRange.start) || 1) - 1
  const end = (parseInt(analysisRange.end) || 4) - 1
  
  // 收集现有数据用于分析
  const ringMasses = []
  const ringWetMasses = []
  const ringVolumes = []
  const boxMasses = []
  const boxWetMasses = []
  const boxDryMasses = []
  const moistures = []
  const avgMoistures = []
  const wetDensities = []
  const dryDensities = []
  const compactions = []
  
  // 收集数据
  for (let i = start; i <= end; i++) {
    const pagePrefix = '_page' + currentPage.value + '_' + i
    
    // 环刀相关数据
    if (formData['ringMass' + pagePrefix]) ringMasses.push(parseFloat(formData['ringMass' + pagePrefix]))
    if (formData['ringWetMass' + pagePrefix]) ringWetMasses.push(parseFloat(formData['ringWetMass' + pagePrefix]))
    if (formData['ringVolume' + pagePrefix]) ringVolumes.push(parseFloat(formData['ringVolume' + pagePrefix]))
    
    // 含水率盒数据
    for (let j = 1; j <= 4; j++) {
      if (formData['boxMass' + j + pagePrefix]) boxMasses.push(parseFloat(formData['boxMass' + j + pagePrefix]))
      if (formData['boxWetMass' + j + pagePrefix]) boxWetMasses.push(parseFloat(formData['boxWetMass' + j + pagePrefix]))
      if (formData['boxDryMass' + j + pagePrefix]) boxDryMasses.push(parseFloat(formData['boxDryMass' + j + pagePrefix]))
      if (formData['moisture' + j + pagePrefix]) moistures.push(parseFloat(formData['moisture' + j + pagePrefix]))
    }
    
    // 结果数据
    if (formData['avgMoisture1' + pagePrefix]) avgMoistures.push(parseFloat(formData['avgMoisture1' + pagePrefix]))
    if (formData['avgMoisture2' + pagePrefix]) avgMoistures.push(parseFloat(formData['avgMoisture2' + pagePrefix]))
    if (formData['wetDensity1' + pagePrefix]) wetDensities.push(parseFloat(formData['wetDensity1' + pagePrefix]))
    if (formData['wetDensity2' + pagePrefix]) wetDensities.push(parseFloat(formData['wetDensity2' + pagePrefix]))
    if (formData['dryDensity1' + pagePrefix]) dryDensities.push(parseFloat(formData['dryDensity1' + pagePrefix]))
    if (formData['dryDensity2' + pagePrefix]) dryDensities.push(parseFloat(formData['dryDensity2' + pagePrefix]))
    if (formData['compaction1' + pagePrefix]) compactions.push(parseFloat(formData['compaction1' + pagePrefix]))
    if (formData['compaction2' + pagePrefix]) compactions.push(parseFloat(formData['compaction2' + pagePrefix]))
  }
  
  // 分析数据并设置默认值
  if (!analysisResults.ringMassMin || !analysisResults.ringMassMax) {
    if (ringMasses.length > 0) {
      const min = Math.min(...ringMasses)
      const max = Math.max(...ringMasses)
      analysisResults.ringMassMin = min.toFixed(2)
      analysisResults.ringMassMax = max.toFixed(2)
    } else {
      analysisResults.ringMassMin = '50.00'
      analysisResults.ringMassMax = '100.00'
    }
  }
  
  if (!analysisResults.ringWetMassMin || !analysisResults.ringWetMassMax) {
    if (ringWetMasses.length > 0) {
      const min = Math.min(...ringWetMasses)
      const max = Math.max(...ringWetMasses)
      analysisResults.ringWetMassMin = min.toFixed(2)
      analysisResults.ringWetMassMax = max.toFixed(2)
    } else {
      analysisResults.ringWetMassMin = '150.00'
      analysisResults.ringWetMassMax = '300.00'
    }
  }
  
  if (!analysisResults.ringVolumeMin || !analysisResults.ringVolumeMax) {
    if (ringVolumes.length > 0) {
      const min = Math.min(...ringVolumes)
      const max = Math.max(...ringVolumes)
      analysisResults.ringVolumeMin = min.toFixed(2)
      analysisResults.ringVolumeMax = max.toFixed(2)
    } else {
      analysisResults.ringVolumeMin = '100.00'
      analysisResults.ringVolumeMax = '200.00'
    }
  }
  
  if (!analysisResults.boxMassMin || !analysisResults.boxMassMax) {
    if (boxMasses.length > 0) {
      const min = Math.min(...boxMasses)
      const max = Math.max(...boxMasses)
      analysisResults.boxMassMin = min.toFixed(2)
      analysisResults.boxMassMax = max.toFixed(2)
    } else {
      analysisResults.boxMassMin = '10.00'
      analysisResults.boxMassMax = '30.00'
    }
  }
  
  if (!analysisResults.boxWetMassMin || !analysisResults.boxWetMassMax) {
    if (boxWetMasses.length > 0) {
      const min = Math.min(...boxWetMasses)
      const max = Math.max(...boxWetMasses)
      analysisResults.boxWetMassMin = min.toFixed(2)
      analysisResults.boxWetMassMax = max.toFixed(2)
    } else {
      analysisResults.boxWetMassMin = '30.00'
      analysisResults.boxWetMassMax = '50.00'
    }
  }
  
  if (!analysisResults.boxDryMassMin || !analysisResults.boxDryMassMax) {
    if (boxDryMasses.length > 0) {
      const min = Math.min(...boxDryMasses)
      const max = Math.max(...boxDryMasses)
      analysisResults.boxDryMassMin = min.toFixed(2)
      analysisResults.boxDryMassMax = max.toFixed(2)
    } else {
      analysisResults.boxDryMassMin = '25.00'
      analysisResults.boxDryMassMax = '45.00'
    }
  }
  
  if (!analysisResults.moistureMin || !analysisResults.moistureMax) {
    if (moistures.length > 0) {
      const min = Math.min(...moistures)
      const max = Math.max(...moistures)
      analysisResults.moistureMin = min.toFixed(2)
      analysisResults.moistureMax = max.toFixed(2)
    } else {
      analysisResults.moistureMin = '5.00'
      analysisResults.moistureMax = '15.00'
    }
  }
  
  if (!analysisResults.avgMoistureMin || !analysisResults.avgMoistureMax) {
    if (avgMoistures.length > 0) {
      const min = Math.min(...avgMoistures)
      const max = Math.max(...avgMoistures)
      analysisResults.avgMoistureMin = min.toFixed(2)
      analysisResults.avgMoistureMax = max.toFixed(2)
    } else {
      analysisResults.avgMoistureMin = '5.00'
      analysisResults.avgMoistureMax = '15.00'
    }
  }
  
  if (!analysisResults.wetDensityMin || !analysisResults.wetDensityMax) {
    if (wetDensities.length > 0) {
      const min = Math.min(...wetDensities)
      const max = Math.max(...wetDensities)
      analysisResults.wetDensityMin = min.toFixed(3)
      analysisResults.wetDensityMax = max.toFixed(3)
    } else {
      analysisResults.wetDensityMin = '1.500'
      analysisResults.wetDensityMax = '2.000'
    }
  }
  
  if (!analysisResults.dryDensityMin || !analysisResults.dryDensityMax) {
    if (dryDensities.length > 0) {
      const min = Math.min(...dryDensities)
      const max = Math.max(...dryDensities)
      analysisResults.dryDensityMin = min.toFixed(3)
      analysisResults.dryDensityMax = max.toFixed(3)
    } else {
      analysisResults.dryDensityMin = '1.300'
      analysisResults.dryDensityMax = '1.800'
    }
  }
  
  if (!analysisResults.compactionMin || !analysisResults.compactionMax) {
    if (compactions.length > 0) {
      const min = Math.min(...compactions)
      const max = Math.max(...compactions)
      analysisResults.compactionMin = min.toFixed(2)
      analysisResults.compactionMax = max.toFixed(2)
    } else {
      analysisResults.compactionMin = '90.00'
      analysisResults.compactionMax = '100.00'
    }
  }
  
  // 填充数据
  for (let i = start; i <= end; i++) {
    const pagePrefix = '_page' + currentPage.value + '_' + i
    
    // 填充环刀相关数据
    formData['ringMass' + pagePrefix] = (Math.random() * (parseFloat(analysisResults.ringMassMax) - parseFloat(analysisResults.ringMassMin)) + parseFloat(analysisResults.ringMassMin)).toFixed(2)
    formData['ringWetMass' + pagePrefix] = (Math.random() * (parseFloat(analysisResults.ringWetMassMax) - parseFloat(analysisResults.ringWetMassMin)) + parseFloat(analysisResults.ringWetMassMin)).toFixed(2)
    formData['ringVolume' + pagePrefix] = (Math.random() * (parseFloat(analysisResults.ringVolumeMax) - parseFloat(analysisResults.ringVolumeMin)) + parseFloat(analysisResults.ringVolumeMin)).toFixed(2)
    
    // 填充第二把环刀数据
    formData['ringMass2' + pagePrefix] = (Math.random() * (parseFloat(analysisResults.ringMassMax) - parseFloat(analysisResults.ringMassMin)) + parseFloat(analysisResults.ringMassMin)).toFixed(2)
    formData['ringWetMass2' + pagePrefix] = (Math.random() * (parseFloat(analysisResults.ringWetMassMax) - parseFloat(analysisResults.ringWetMassMin)) + parseFloat(analysisResults.ringWetMassMin)).toFixed(2)
    formData['ringVolume2' + pagePrefix] = (Math.random() * (parseFloat(analysisResults.ringVolumeMax) - parseFloat(analysisResults.ringVolumeMin)) + parseFloat(analysisResults.ringVolumeMin)).toFixed(2)
    
    // 填充含水率盒数据
    for (let j = 1; j <= 4; j++) {
      formData['boxMass' + j + pagePrefix] = (Math.random() * (parseFloat(analysisResults.boxMassMax) - parseFloat(analysisResults.boxMassMin)) + parseFloat(analysisResults.boxMassMin)).toFixed(2)
      formData['boxWetMass' + j + pagePrefix] = (Math.random() * (parseFloat(analysisResults.boxWetMassMax) - parseFloat(analysisResults.boxWetMassMin)) + parseFloat(analysisResults.boxWetMassMin)).toFixed(2)
      formData['boxDryMass' + j + pagePrefix] = (Math.random() * (parseFloat(analysisResults.boxDryMassMax) - parseFloat(analysisResults.boxDryMassMin)) + parseFloat(analysisResults.boxDryMassMin)).toFixed(2)
      formData['moisture' + j + pagePrefix] = (Math.random() * (parseFloat(analysisResults.moistureMax) - parseFloat(analysisResults.moistureMin)) + parseFloat(analysisResults.moistureMin)).toFixed(2)
    }
    
    // 计算平均含水率
    const moisture1 = parseFloat(formData['moisture1' + pagePrefix])
    const moisture2 = parseFloat(formData['moisture2' + pagePrefix])
    const moisture3 = parseFloat(formData['moisture3' + pagePrefix])
    const moisture4 = parseFloat(formData['moisture4' + pagePrefix])
    
    formData['avgMoisture1' + pagePrefix] = ((moisture1 + moisture2) / 2).toFixed(2)
    formData['avgMoisture2' + pagePrefix] = ((moisture3 + moisture4) / 2).toFixed(2)
    
    // 计算湿密度和干密度
    const ringMass = parseFloat(formData['ringMass' + pagePrefix])
    const ringWetMass = parseFloat(formData['ringWetMass' + pagePrefix])
    const ringVolume = parseFloat(formData['ringVolume' + pagePrefix])
    const avgMoisture1 = parseFloat(formData['avgMoisture1' + pagePrefix])
    
    const ringMass2 = parseFloat(formData['ringMass2' + pagePrefix])
    const ringWetMass2 = parseFloat(formData['ringWetMass2' + pagePrefix])
    const ringVolume2 = parseFloat(formData['ringVolume2' + pagePrefix])
    const avgMoisture2 = parseFloat(formData['avgMoisture2' + pagePrefix])
    
    // 计算湿密度
    const wetDensity1 = (ringWetMass - ringMass) / ringVolume
    const wetDensity2 = (ringWetMass2 - ringMass2) / ringVolume2
    
    // 计算干密度
    const dryDensity1 = wetDensity1 / (1 + avgMoisture1 / 100)
    const dryDensity2 = wetDensity2 / (1 + avgMoisture2 / 100)
    
    formData['wetDensity1' + pagePrefix] = wetDensity1.toFixed(3)
    formData['wetDensity2' + pagePrefix] = wetDensity2.toFixed(3)
    formData['dryDensity1' + pagePrefix] = dryDensity1.toFixed(3)
    formData['dryDensity2' + pagePrefix] = dryDensity2.toFixed(3)
    
    // 计算平均干密度
    const avgDryDensity = (dryDensity1 + dryDensity2) / 2
    formData['avgDryDensity1' + pagePrefix] = avgDryDensity.toFixed(3)
    
    // 计算压实度
    if (formData.maxDryDensity) {
      const maxDryDensity = parseFloat(formData.maxDryDensity)
      const compaction1 = (dryDensity1 / maxDryDensity) * 100
      const compaction2 = (dryDensity2 / maxDryDensity) * 100
      formData['compaction1' + pagePrefix] = compaction1.toFixed(2)
      formData['compaction2' + pagePrefix] = compaction2.toFixed(2)
    } else {
      formData['compaction1' + pagePrefix] = (Math.random() * (parseFloat(analysisResults.compactionMax) - parseFloat(analysisResults.compactionMin)) + parseFloat(analysisResults.compactionMin)).toFixed(2)
      formData['compaction2' + pagePrefix] = (Math.random() * (parseFloat(analysisResults.compactionMax) - parseFloat(analysisResults.compactionMin)) + parseFloat(analysisResults.compactionMin)).toFixed(2)
    }
  }
  
  // 填充全局数据
  if (!formData.maxDryDensity) {
    const dryDensityValues = []
    for (let i = start; i <= end; i++) {
      const pagePrefix = '_page' + currentPage.value + '_' + i
      if (formData['dryDensity1' + pagePrefix]) dryDensityValues.push(parseFloat(formData['dryDensity1' + pagePrefix]))
      if (formData['dryDensity2' + pagePrefix]) dryDensityValues.push(parseFloat(formData['dryDensity2' + pagePrefix]))
    }
    if (dryDensityValues.length > 0) {
      const max = Math.max(...dryDensityValues)
      formData.maxDryDensity = max.toFixed(3)
    } else {
      formData.maxDryDensity = '1.800'
    }
  }
  
  if (!formData.optMoisture) {
    const moistureValues = []
    for (let i = start; i <= end; i++) {
      const pagePrefix = '_page' + currentPage.value + '_' + i
      if (formData['avgMoisture1' + pagePrefix]) moistureValues.push(parseFloat(formData['avgMoisture1' + pagePrefix]))
      if (formData['avgMoisture2' + pagePrefix]) moistureValues.push(parseFloat(formData['avgMoisture2' + pagePrefix]))
    }
    if (moistureValues.length > 0) {
      const avg = moistureValues.reduce((sum, val) => sum + val, 0) / moistureValues.length
      formData.optMoisture = avg.toFixed(2)
    } else {
      formData.optMoisture = '10.00'
    }
  }
  
  if (!formData.designCompaction) {
    const compactionValues = []
    for (let i = start; i <= end; i++) {
      const pagePrefix = '_page' + currentPage.value + '_' + i
      if (formData['compaction1' + pagePrefix]) compactionValues.push(parseFloat(formData['compaction1' + pagePrefix]))
      if (formData['compaction2' + pagePrefix]) compactionValues.push(parseFloat(formData['compaction2' + pagePrefix]))
    }
    if (compactionValues.length > 0) {
      const min = Math.min(...compactionValues)
      formData.designCompaction = min.toFixed(2)
    } else {
      formData.designCompaction = '95.00'
    }
  }
  
  closeAnalysisModal()
  alert('数据填充完成！')
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
            table-layout: fixed;
            word-break: break-all;
        }
        td {
            border: 1px solid black;
            padding: 8px 5px;
            vertical-align: middle;
            text-align: center;
            font-size: inherit;
        }
        .label {
            font-weight: bold;
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
            font-family: inherit;
            font-size: inherit;
            color: inherit;
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
            color: inherit;
            font-family: inherit;
            font-size: inherit;
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
        .footer-info {
            display: flex;
            justify-content: space-between;
            margin-top: 10px;
            font-size: 16px;
            font-weight: normal;
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
