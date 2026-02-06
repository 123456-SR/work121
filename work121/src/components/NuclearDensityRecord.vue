<template>
  <div class="nuclearDensityRecord-container">


    <div class="no-print" style="margin-bottom: 20px;">
        <button @click="goToHome" style="text-decoration: none; color: blue; background: none; border: none; cursor: pointer; padding: 0;">&lt; 返回主页</button>
        <button @click="printDocument" style="float: right; margin-left: 10px;">打印此单</button>
        <button @click="generatePdf" style="float: right; margin-left: 10px;">下载PDF</button>
        <button @click="previewPdf" style="float: right; margin-left: 10px;">预览PDF</button>
    </div>

    <form id="pdfForm" ref="pdfForm" method="post">
    <h2>原位密度检测记录表（核子法）</h2>

    <div class="header-info">
        <span>委托单位：<input type="text" v-model="formData.entrustingUnit"   name="entrustingUnit" style="width: 250px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>统一编号：<input type="text" v-model="formData.unifiedNumber"   name="unifiedNumber" style="width: 150px; border-bottom: 1px solid black;"></span>
    </div>

    <table>
        <!-- Header Section -->
        <tr>
            <td class="label">工程名称</td>
            <td colspan="4"><input type="text" v-model="formData.projectName"   name="projectName"></td>
            <td class="label">委托日期</td>
            <td colspan="2"><input type="text" v-model="formData.commissionDate"   name="commissionDate"></td>
        </tr>
        <tr>
            <td class="label">施工部位</td>
            <td colspan="4"><input type="text" v-model="formData.constructionPart"   name="constructionPart"></td>
            <td class="label">检测类别</td>
            <td colspan="2"><input type="text" v-model="formData.testCategory"   name="testCategory"></td>
        </tr>
        <tr>
            <td class="label">仪器设备</td>
            <td colspan="4"><input type="text" v-model="formData.equipment"   name="equipment"></td>
            <td class="label">检测方法</td>
            <td colspan="2"><input type="text" v-model="formData.testMethod"   name="testMethod" value="核子法"></td>
        </tr>
        <tr>
            <td class="label">样品名称及<br>状态</td>
            <td colspan="4"><input type="text" v-model="formData.sampleNameStatus"   name="sampleNameStatus"></td>
            <td class="label">依据标准</td>
            <td colspan="2"><input type="text" v-model="formData.standard"   name="standard"></td>
        </tr>
        <tr>
            <td class="label">设计压实度%</td>
            <td colspan="7"><input type="text" v-model="formData.designCompaction"   name="designCompaction"></td>
        </tr>
        <tr>
            <td class="label">最大干密度<br>(g/cm³)</td>
            <td colspan="2"><input type="text" v-model="formData.maxDryDensity"   name="maxDryDensity"></td>
            <td class="label">最优含水率 %</td>
            <td colspan="2"><input type="text" v-model="formData.optimumMoisture"   name="optimumMoisture"></td>
            <td class="label">最小干密度<br>(g/cm³)</td>
            <td><input type="text" v-model="formData.minDryDensity"   name="minDryDensity"></td>
        </tr>

        <!-- Data Header -->
        <tr>
            <td class="label" style="width: 10%;">样品编号</td>
            <td class="label" style="width: 20%;">检测部位<br>(桩号、高程)</td>
            <td class="label" style="width: 12%;">检测日期</td>
            <td class="label" style="width: 12%;">湿密度<br>(g/cm³)</td>
            <td class="label" style="width: 12%;">干密度<br>(g/cm³)</td>
            <td class="label" style="width: 12%;">含水率<br>%</td>
            <td class="label" style="width: 12%;">压实度%</td>
            <td class="label" style="width: 10%;">备注</td>
        </tr>

        <!-- Data Rows -->
        <template v-for="(n, i_idx) in 20" :key="i_idx">
        <tr>
            <td><input type="text" :name="'sampleId_' + i_idx" v-model="formData['sampleId_' + i_idx]"></td>
            <td><input type="text" :name="'location_' + i_idx" v-model="formData['location_' + i_idx]"></td>
            <td><input type="text" :name="'date_' + i_idx" v-model="formData['date_' + i_idx]"></td>
            <td><input type="text" :name="'wetDensity_' + i_idx" v-model="formData['wetDensity_' + i_idx]"></td>
            <td><input type="text" :name="'dryDensity_' + i_idx" v-model="formData['dryDensity_' + i_idx]"></td>
            <td><input type="text" :name="'moisture_' + i_idx" v-model="formData['moisture_' + i_idx]"></td>
            <td><input type="text" :name="'compaction_' + i_idx" v-model="formData['compaction_' + i_idx]"></td>
            <td><input type="text" :name="'remarks_' + i_idx" v-model="formData['remarks_' + i_idx]"></td>
        </tr>
        </template>
    </table>

    <div class="footer-info">
        <span>审核：<input type="text" v-model="formData.reviewer"   name="reviewer" style="width: 100px; border-bottom: 1px solid black;"></span>
        <span>检测：<input type="text" v-model="formData.tester"   name="tester" style="width: 100px; border-bottom: 1px solid black;"></span>
    </div>


    </form>



  </div>
</template>

<script setup>
import { reactive, ref, onMounted, inject } from 'vue'

// 注入导航方法
const navigateTo = inject('navigateTo')

const pdfForm = ref(null)

const formData = reactive({
  entrustingUnit: '',
  unifiedNumber: '',
  projectName: '',
  commissionDate: '',
  constructionPart: '',
  testCategory: '',
  equipment: '',
  testMethod: '',
  sampleNameStatus: '',
  standard: '',
  designCompaction: '',
  maxDryDensity: '',
  optimumMoisture: '',
  minDryDensity: '',
  reviewer: '',
  tester: '',
})

onMounted(() => {

  // Initialize dynamic fields for loop variable 'i_idx'
  // Please verify the loop count match the template
  for (let i_idx = 0; i_idx < 50; i_idx++) {
    formData['sampleId_' + i_idx] = ''
    formData['location_' + i_idx] = ''
    formData['compaction_' + i_idx] = ''
    formData['wetDensity_' + i_idx] = ''
    formData['moisture_' + i_idx] = ''
    formData['date_' + i_idx] = ''
    formData['remarks_' + i_idx] = ''
    formData['dryDensity_' + i_idx] = ''
  }

})

const printDocument = () => {
  window.print()
}

// 返回主页（目录列表）
const goToHome = () => {
  if (navigateTo) {
    navigateTo('DirectoryList');
  }
}

const generatePdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/nuclear_density_record/generate'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}

const previewPdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/nuclear_density_record/preview'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}
</script>

<style scoped>

        .nuclearDensityRecord-container {
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
            .nuclearDensityRecord-container {
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
