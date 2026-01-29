<template>
  <div class="densityTestResult-container">


    <div class="no-print" style="margin-bottom: 20px;">
        <a href="/" style="text-decoration: none; color: blue;">&lt; 返回主页</a>
        <button @click="printDocument" style="float: right; margin-left: 10px;">打印此单</button>
        <button @click="generatePdf" style="float: right; margin-left: 10px;">下载PDF</button>
        <button @click="previewPdf" style="float: right; margin-left: 10px;">预览PDF</button>
    </div>

    <form id="pdfForm" ref="pdfForm" method="post">
    <div class="header-info" style="justify-content: space-between;">
        <span>统一编号：<input type="text" v-model="formData.unifiedNumber"   name="unifiedNumber" style="width: 150px; border-bottom: 1px solid black; text-align: left;"></span>
        <span></span> <!-- Placeholder for layout balance if needed -->
    </div>

    <h2>原位密度检测结果</h2>

    <table>
        <!-- Header Rows -->
        <tr>
            <td class="label" style="width: 15%;">施工部位</td>
            <td colspan="9" class="left-align"><input type="text" v-model="formData.constructionPart"   name="constructionPart"></td>
        </tr>
        <tr>
            <td class="label">最大干密度<br>(g/cm³)</td>
            <td colspan="2"><input type="text" v-model="formData.maxDryDensity"   name="maxDryDensity"></td>
            <td class="label">最优含水率 %</td>
            <td colspan="2"><input type="text" v-model="formData.optimumMoisture"   name="optimumMoisture"></td>
            <td class="label">最小干密度<br>(g/cm³)</td>
            <td colspan="3"><input type="text" v-model="formData.minDryDensity"   name="minDryDensity"></td>
        </tr>

        <!-- Data Header -->
        <tr>
            <td class="label" style="width: 10%;">样品编号</td>
            <td class="label" style="width: 25%;" colspan="2">检测部位<br>(桩号、高程)</td>
            <td class="label" style="width: 15%;" colspan="2">检测日期</td>
            <td class="label" style="width: 12.5%;">湿密度<br>(g/cm³)</td>
            <td class="label" style="width: 12.5%;">干密度<br>(g/cm³)</td>
            <td class="label" style="width: 12.5%;">含水率<br>%</td>
            <td class="label" style="width: 12.5%;" colspan="2">压实度%</td>
        </tr>

        <!-- Data Rows (20 rows to fill the page) -->
        <template v-for="(n, i_idx) in 20" :key="i_idx">
        <tr>
            <td rowspan="2"><input type="text" :name="'sampleId_' + i_idx" v-model="formData['sampleId_' + i_idx]"></td>
            <td rowspan="2" colspan="2"><input type="text" :name="'location_' + i_idx" v-model="formData['location_' + i_idx]"></td>
            <td rowspan="2" colspan="2"><input type="text" :name="'date_' + i_idx" v-model="formData['date_' + i_idx]"></td>
            <td><input type="text" :name="'wetDensity_' + i_idx" v-model="formData['wetDensity_' + i_idx]"></td>
            <td><input type="text" :name="'dryDensity_' + i_idx" v-model="formData['dryDensity_' + i_idx]"></td>
            <td><input type="text" :name="'moisture_' + i_idx" v-model="formData['moisture_' + i_idx]"></td>
            <td rowspan="2" colspan="2"><input type="text" :name="'compaction_' + i_idx" v-model="formData['compaction_' + i_idx]"></td>
        </tr>
        <tr>
            <td><input type="text" :name="'wetDensity2_' + i_idx" v-model="formData['wetDensity2_' + i_idx]"></td>
            <td><input type="text" :name="'dryDensity2_' + i_idx" v-model="formData['dryDensity2_' + i_idx]"></td>
            <td><input type="text" :name="'moisture2_' + i_idx" v-model="formData['moisture2_' + i_idx]"></td>
        </tr>
        </template>
    </table>


    </form>



  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'

const pdfForm = ref(null)

const formData = reactive({
  unifiedNumber: '',
  constructionPart: '',
  maxDryDensity: '',
  optimumMoisture: '',
  minDryDensity: '',
})

onMounted(() => {

  // Initialize dynamic fields for loop variable 'i_idx'
  // Please verify the loop count match the template
  for (let i_idx = 0; i_idx < 50; i_idx++) {
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

})

const printDocument = () => {
  window.print()
}

const generatePdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/density_test_result/generate'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}

const previewPdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/density_test_result/preview'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}
</script>

<style scoped>

        .densityTestResult-container {
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
            justify-content: flex-end; /* Align to right as per image top left corner seems to be for binding or just empty, unified number is top left/right? Let's assume standard right or left. Image shows '统一编号' on top left actually? Wait, looking at the image, '统一编号' is on top left. */
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
            justify-content: space-around;
            margin-top: 20px;
            margin-bottom: 10px;
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
            .densityTestResult-container {
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
