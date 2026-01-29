<template>
  <div class="reboundMethodRecord-container">


    <div class="no-print" style="margin-bottom: 20px;">
        <a href="/" style="text-decoration: none; color: blue;">&lt; 返回主页</a>
        <button @click="printDocument" style="float: right; margin-left: 10px;">打印此单</button>
        <button @click="generatePdf" style="float: right; margin-left: 10px;">下载PDF</button>
        <button @click="previewPdf" style="float: right; margin-left: 10px;">预览PDF</button>
    </div>

    <form id="pdfForm" ref="pdfForm" method="post">
    <h2>回弹法检测混凝土抗压强度记录表</h2>

    <div class="header-top">
        <span>委托单位：<input type="text" v-model="formData.entrustingUnit"   name="entrustingUnit" style="width: 200px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>统一编号：<input type="text" v-model="formData.unifiedNumber"   name="unifiedNumber" style="width: 150px; border-bottom: 1px solid black;"></span>
    </div>
    <div class="header-top">
        <span>样品编号：<input type="text" v-model="formData.sampleNo"   name="sampleNo" style="width: 200px; border-bottom: 1px solid black; text-align: left;"></span>
    </div>

    <table>
        <colgroup>
            <col style="width: 4%;"> <!-- Col 1: Index -->
            <template v-for="(n, i_idx) in 16" :key="i_idx">
            <col style="width: 3%;"> <!-- Cols 2-17: Values (48%) -->
            </template>
            <col style="width: 12%;"> <!-- Col 18: Avg -->
            <col style="width: 12%;"> <!-- Col 19: Depth -->
            <col style="width: 12%;"> <!-- Col 20: Est Str -->
            <col style="width: 12%;"> <!-- Col 21: Corr Str -->
        </colgroup>

        <!-- Header Info -->
        <tr>
            <td colspan="2" class="label">工程名称</td>
            <td colspan="13" class="left-align"><input type="text" v-model="formData.projectName"   name="projectName" style="text-align: left;"></td>
            <td colspan="3" class="label">委托日期</td>
            <td colspan="3"><input type="text" v-model="formData.commissionDate"   name="commissionDate"></td>
        </tr>
        <tr>
            <td colspan="2" class="label">结构部位</td>
            <td colspan="5"><input type="text" v-model="formData.structurePart"   name="structurePart"></td>
            <td colspan="3" class="label">检测类别</td>
            <td colspan="4"><input type="text" v-model="formData.testCategory"   name="testCategory"></td>
            <td colspan="3" class="label">浇筑日期</td>
            <td colspan="4"><input type="text" v-model="formData.pourDate"   name="pourDate"></td>
        </tr>
        <tr>
            <td colspan="2" class="label">样品状态</td>
            <td colspan="5"><input type="text" v-model="formData.sampleStatus"   name="sampleStatus"></td>
            <td colspan="3" class="label">测试角度</td>
            <td colspan="4"><input type="text" v-model="formData.testAngle"   name="testAngle"></td>
            <td colspan="3" class="label">检测日期</td>
            <td colspan="4"><input type="text" v-model="formData.testDate"   name="testDate"></td>
        </tr>
        <tr>
            <td colspan="2" class="label">设计指标</td>
            <td colspan="5"><input type="text" v-model="formData.designIndex"   name="designIndex"></td>
            <td colspan="7" class="label">构件厚度或骨料最大粒径</td>
            <td colspan="7"><input type="text" v-model="formData.aggregateSize"   name="aggregateSize"></td>
        </tr>

        <!-- Data Table Header -->
        <tr>
            <td rowspan="2" class="label">测<br>区</td>
            <td colspan="16" class="label">回弹值</td>
            <td rowspan="2" class="label">平均<br>回弹<br>值</td>
            <td rowspan="2" class="label">碳化<br>深度<br>mm</td>
            <td rowspan="2" class="label">推定<br>强度<br>值MPa</td>
            <td rowspan="2" class="label">碳化修<br>正强度<br>值MPa</td>
        </tr>
        <tr>
            <template v-for="(n, i_idx) in 16" :key="i_idx">
            <td class="label">{{ (i_idx + 1) }}</td>
            </template>
        </tr>

        <!-- Data Rows -->
        <template v-for="(n, i_idx) in 10" :key="i_idx">
        <tr>
            <td>{{ (i_idx + 1) }}</td>
            <template v-for="(n, j_idx) in 16" :key="j_idx">
            <td><input type="text" :name="'reboundValue_{{ (i_idx + 1) }}_' + (j_idx + 1)" v-model="formData['reboundValue_{{ (i_idx + 1) }}_' + (j_idx + 1)]"></td>
            </template>
            <td><input type="text" :name="'avgRebound_' + (i_idx + 1)" v-model="formData['avgRebound_' + (i_idx + 1)]"></td>
            <td><input type="text" :name="'carbonDepth_' + (i_idx + 1)" v-model="formData['carbonDepth_' + (i_idx + 1)]"></td>
            <td><input type="text" :name="'estimatedStrength_' + (i_idx + 1)" v-model="formData['estimatedStrength_' + (i_idx + 1)]"></td>
            <td><input type="text" :name="'correctedStrength_' + (i_idx + 1)" v-model="formData['correctedStrength_' + (i_idx + 1)]"></td>
        </tr>
        </template>

        <!-- Summary Section -->
        <tr>
            <td colspan="3" class="label">平均强度值<br>MPa</td>
            <td colspan="2"><input type="text" v-model="formData.avgStrength"   name="avgStrength"></td>
            <td colspan="3" class="label">标准差<br>MPa</td>
            <td colspan="2"><input type="text" v-model="formData.stdDev"   name="stdDev"></td>
            <td colspan="3" class="label">变异系<br>数%</td>
            <td colspan="2"><input type="text" v-model="formData.coefVariation"   name="coefVariation"></td>
            <td colspan="3" class="label">构件强度推<br>定值 MPa</td>
            <td colspan="3"><input type="text" v-model="formData.compEstimatedStrength"   name="compEstimatedStrength"></td>
        </tr>
        <tr>
            <td colspan="2" class="label">仪器设备</td>
            <td colspan="11" class="left-align"><input type="text" v-model="formData.equipment"   name="equipment" style="text-align: left;"></td>
            <td colspan="4" class="label">碳化平均值<br>mm</td>
            <td colspan="4"><input type="text" v-model="formData.avgCarbonation"   name="avgCarbonation"></td>
        </tr>
        <tr>
            <td colspan="2" class="label">依据标准</td>
            <td colspan="11" class="left-align"><input type="text" v-model="formData.standard"   name="standard" style="text-align: left;"></td>
            <td colspan="4" class="label">率定值</td>
            <td colspan="4" class="left-align">

                检测前：<input type="text" v-model="formData.calibrationBefore"   name="calibrationBefore" style="width: 60px; border-bottom: 1px solid #ccc; text-align: center;"><br>
                检测后：<input type="text" v-model="formData.calibrationAfter"   name="calibrationAfter" style="width: 60px; border-bottom: 1px solid #ccc; text-align: center;">
            </td>
        </tr>
        <tr>
            <td colspan="2" class="label">结论</td>
            <td colspan="19" class="left-align" style="height: 40px;">
                <input type="text" v-model="formData.conclusion"   name="conclusion" style="text-align: left; width: 100%;">
            </td>
        </tr>
        <tr>
            <td colspan="2" class="label" style="height: 150px; vertical-align: top;">测区示意图：</td>
            <td colspan="19" style="height: 150px;">
                <!-- Placeholder for diagram -->
            </td>
        </tr>
        <tr>
            <td colspan="2" class="label">备注</td>
            <td colspan="19" class="left-align" style="height: 40px;">
                <textarea v-model="formData.remarks"  name="remarks" style="width: 100%; height: 100%;"></textarea>
            </td>
        </tr>
    </table>

    <div class="footer-info">
        <span>批准：<input type="text" v-model="formData.approver"   name="approver" style="width: 100px; border-bottom: 1px solid black;"></span>
        <span>审核：<input type="text" v-model="formData.reviewer"   name="reviewer" style="width: 100px; border-bottom: 1px solid black;"></span>
        <span>检测：<input type="text" v-model="formData.tester"   name="tester" style="width: 100px; border-bottom: 1px solid black;"></span>
    </div>


    </form>



  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'

const pdfForm = ref(null)

const formData = reactive({
  entrustingUnit: '',
  unifiedNumber: '',
  sampleNo: '',
  projectName: '',
  commissionDate: '',
  structurePart: '',
  testCategory: '',
  pourDate: '',
  sampleStatus: '',
  testAngle: '',
  testDate: '',
  designIndex: '',
  aggregateSize: '',
  avgStrength: '',
  stdDev: '',
  coefVariation: '',
  compEstimatedStrength: '',
  equipment: '',
  avgCarbonation: '',
  standard: '',
  calibrationBefore: '',
  calibrationAfter: '',
  conclusion: '',
  approver: '',
  reviewer: '',
  tester: '',
  remarks: '',
})

onMounted(() => {

  // Initialize dynamic fields for loop variable 'j_idx'
  // Please verify the loop count match the template
  for (let j_idx = 0; j_idx < 50; j_idx++) {
    formData['reboundValue_{{ (i_idx + 1) }}_' + j_idx] = ''
  }

  // Initialize dynamic fields for loop variable 'i_idx'
  // Please verify the loop count match the template
  for (let i_idx = 0; i_idx < 50; i_idx++) {
    formData['avgRebound_' + i_idx] = ''
    formData['correctedStrength_' + i_idx] = ''
    formData['carbonDepth_' + i_idx] = ''
    formData['estimatedStrength_' + i_idx] = ''
  }

})

const printDocument = () => {
  window.print()
}

const generatePdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/rebound_method_record/generate'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}

const previewPdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/rebound_method_record/preview'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}
</script>

<style scoped>

        .reboundMethodRecord-container {
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
        .header-top {
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
        }
        td {
            border: 1px solid black;
            padding: 2px;
            vertical-align: middle;
            text-align: center;
            font-size: 14px;
            overflow: hidden;
        }
        .label {
            font-weight: bold;
            white-space: normal;
        }
        .left-align {
            text-align: left;
            padding-left: 5px;
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
            .reboundMethodRecord-container {
                width: 100%;
                margin: 0;
                padding: 0;
            }
            .no-print {
                display: none;
            }
        }
    
</style>
