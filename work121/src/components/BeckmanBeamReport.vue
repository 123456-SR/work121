<template>
  <div class="beckmanBeamReport-container">

    <div class="no-print" style="margin-bottom: 20px;">
        <a href="/" style="text-decoration: none; color: blue;">&lt; 返回主页</a>
        <button @click="printDocument" style="float: right; margin-left: 10px;">打印此单</button>
        <button @click="generatePdf" style="float: right; margin-left: 10px;">下载PDF</button>
        <button @click="previewPdf" style="float: right; margin-left: 10px;">预览PDF</button>
    </div>

    <form id="pdfForm" ref="pdfForm" method="post">
    <h1>路基路面回弹弯沉（回弹模量）检测报告</h1>

    <div class="header-info">
        <div>委托单位：<input type="text" v-model="formData.entrustingUnit"   name="entrustingUnit" style="width: 200px; border-bottom: 1px solid black; text-align: left;"></div>
        <div>统一编号：<input type="text" v-model="formData.unifiedNumber"   name="unifiedNumber" style="width: 150px; border-bottom: 1px solid black;"></div>
    </div>

    <!-- Table 1: Header Info (Rows 1-6) -->
    <table>
        <colgroup>
            <col style="width: 15%;">
            <col style="width: 35%;">
            <col style="width: 15%;">
            <col style="width: 35%;">
        </colgroup>
        <tr>
            <td class="label">工程名称</td>
            <td><input type="text" v-model="formData.projectName"   name="projectName"></td>
            <td class="label">委托日期</td>
            <td><input type="text" v-model="formData.commissionDate"   name="commissionDate" class="date-input"></td>
        </tr>
        <tr>
            <td class="label">施工部位</td>
            <td><input type="text" v-model="formData.constructionPart"   name="constructionPart"></td>
            <td class="label">检测日期</td>
            <td><input type="text" v-model="formData.testDate"   name="testDate" class="date-input"></td>
        </tr>
        <tr>
            <td class="label">仪器设备及编码</td>
            <td><input type="text" v-model="formData.equipmentCode"   name="equipmentCode"></td>
            <td class="label">报告日期</td>
            <td><input type="text" v-model="formData.reportDate"   name="reportDate" class="date-input"></td>
        </tr>
        <tr>
            <td class="label">样品名称及状态</td>
            <td><input type="text" v-model="formData.sampleStatus"   name="sampleStatus"></td>
            <td class="label">检测类别</td>
            <td><input type="text" v-model="formData.testCategory"   name="testCategory"></td>
        </tr>
        <tr>
            <td class="label">依据标准</td>
            <td><input type="text" v-model="formData.standard"   name="standard"></td>
            <td class="label">检测方法</td>
            <td><input type="text" v-model="formData.testMethod"   name="testMethod"></td>
        </tr>
        <tr>
            <td class="label">见证单位</td>
            <td><input type="text" v-model="formData.witnessUnit"   name="witnessUnit"></td>
            <td class="label">见证人</td>
            <td><input type="text" v-model="formData.witness"   name="witness"></td>
        </tr>
    </table>

    <!-- Table 2: Technical Parameters (Rows 7-8) -->
    <table>
        <colgroup>
            <col style="width: 15%;">
            <col style="width: 20%;">
            <col style="width: 15%;">
            <col style="width: 15%;">
            <col style="width: 20%;">
            <col style="width: 15%;">
        </colgroup>
        <tr>
            <td class="label">路面结构类型</td>
            <td><input type="text" v-model="formData.pavementType"   name="pavementType"></td>
            <td class="label">路面厚度<br>(mm)</td>
            <td><input type="text" v-model="formData.pavementThickness"   name="pavementThickness"></td>
            <td class="label">温度修正系数</td>
            <td><input type="text" v-model="formData.tempCorrection"   name="tempCorrection"></td>
        </tr>
        <tr>
            <td class="label">设计弯沉值<br>(0.01mm)</td>
            <td><input type="text" v-model="formData.designDeflection"   name="designDeflection"></td>
            <td class="label">沥青面层平均温度 (℃)</td>
            <td><input type="text" v-model="formData.avgAsphaltTemp"   name="avgAsphaltTemp"></td>
            <td class="label">泊松比μ</td>
            <td><input type="text" v-model="formData.poissonRatio"   name="poissonRatio"></td>
        </tr>
    </table>

    <!-- Table 3: Data Table -->
    <table>
        <colgroup>
            <col style="width: 10%;"> <!-- 序号 -->
            <col style="width: 25%;"> <!-- 测点桩号 -->
            <col style="width: 15%;"> <!-- 车道 -->
            <col style="width: 15%;"> <!-- 左侧 -->
            <col style="width: 20%;"> <!-- 右侧 -->
            <col style="width: 15%;"> <!-- 备注 -->
        </colgroup>
        <thead>
            <tr>
                <th>序号</th>
                <th>测点桩号 (幅段)</th>
                <th>车道</th>
                <th>左侧回弹弯沉值 (0.01mm)</th>
                <th>右侧回弹弯沉值 (0.01mm)</th>
                <th>备注</th>
            </tr>
        </thead>
        <tbody>
            <template v-for="(n, i_idx) in 5" :key="i_idx">
            <tr>
                <td>{{ (i_idx + 1) }}</td>
                <td><input type="text" :name="'station_' + (i_idx + 1)" v-model="formData['station_' + (i_idx + 1)]"></td>
                <td><input type="text" :name="'lane_' + (i_idx + 1)" v-model="formData['lane_' + (i_idx + 1)]"></td>
                <td><input type="text" :name="'leftDeflection_' + (i_idx + 1)" v-model="formData['leftDeflection_' + (i_idx + 1)]"></td>
                <td><input type="text" :name="'rightDeflection_' + (i_idx + 1)" v-model="formData['rightDeflection_' + (i_idx + 1)]"></td>
                <td><input type="text" :name="'remarks_' + (i_idx + 1)" v-model="formData['remarks_' + (i_idx + 1)]"></td>
            </tr>
            </template>
        </tbody>
    </table>

    <!-- Table 4: Statistics -->
    <table>
        <colgroup>
            <col style="width: 15%;">
            <col style="width: 10%;">
            <col style="width: 15%;">
            <col style="width: 10%;">
            <col style="width: 15%;">
            <col style="width: 10%;">
            <col style="width: 15%;">
            <col style="width: 10%;">
        </colgroup>
        <tr>
            <td class="label">(温度修正后)<br>平均弯沉值<br>(0.01mm)</td>
            <td><input type="text" v-model="formData.avgDeflection"   name="avgDeflection"></td>
            <td class="label">标准差<br>(0.01mm)</td>
            <td><input type="text" v-model="formData.stdDev"   name="stdDev"></td>
            <td class="label">弯沉代表值<br>(0.01mm)</td>
            <td><input type="text" v-model="formData.repDeflection"   name="repDeflection"></td>
            <td class="label">回弹模量<br>E<sub>1</sub>(MPa)</td>
            <td><input type="text" v-model="formData.resilientModulus"   name="resilientModulus"></td>
        </tr>
    </table>

    <!-- Table 5: Conclusion and Notes -->
    <table>
        <colgroup>
            <col style="width: 15%;">
            <col style="width: 85%;">
        </colgroup>
        <tr>
            <td class="label">检测结论</td>
            <td><input type="text" v-model="formData.testConclusion"   name="testConclusion" style="width: 100%; text-align: left;"></td>
        </tr>
        <tr>
            <td class="label">报告说明</td>
            <td><input type="text" v-model="formData.reportDesc"   name="reportDesc" style="width: 100%; text-align: left;"></td>
        </tr>
    </table>

    <div class="footer-section">
        <div class="footer-line">
            <div>批准：<input type="text" v-model="formData.approver"   name="approver" style="width: 100px; border-bottom: 1px solid black;"></div>
            <div>审核：<input type="text" v-model="formData.reviewer"   name="reviewer" style="width: 100px; border-bottom: 1px solid black;"></div>
            <div>检测：<input type="text" v-model="formData.tester"   name="tester" style="width: 100px; border-bottom: 1px solid black;"></div>
        </div>

        <div class="company-info">
            <div>声明：</div>
            <div>1.对本检测报告的复印件未加盖公司检验检测专用章无效。2.对检测结果如有异议，应在收到报告之日起十五日之内向本公司提出。</div>
            <div style="margin-top: 10px;">公司名称：<input type="text" v-model="formData.companyName"   name="companyName" style="width: 300px; border-bottom: 1px solid black; text-align: left;"></div>
            <div class="footer-line" style="margin-top: 5px;">
                <div>公司地址：<input type="text" v-model="formData.companyAddress"   name="companyAddress" style="width: 300px; border-bottom: 1px solid black; text-align: left;"></div>
                <div>电话：<input type="text" v-model="formData.companyPhone"   name="companyPhone" style="width: 150px; border-bottom: 1px solid black; text-align: left;"></div>
            </div>
        </div>


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
  projectName: '',
  commissionDate: '',
  constructionPart: '',
  testDate: '',
  equipmentCode: '',
  reportDate: '',
  sampleStatus: '',
  testCategory: '',
  standard: '',
  testMethod: '',
  witnessUnit: '',
  witness: '',
  pavementType: '',
  pavementThickness: '',
  tempCorrection: '',
  designDeflection: '',
  avgAsphaltTemp: '',
  poissonRatio: '',
  avgDeflection: '',
  stdDev: '',
  repDeflection: '',
  resilientModulus: '',
  testConclusion: '',
  reportDesc: '',
  approver: '',
  reviewer: '',
  tester: '',
  companyName: '',
  companyAddress: '',
  companyPhone: '',
})

onMounted(() => {

  // Initialize dynamic fields for loop variable 'i_idx'
  // Please verify the loop count match the template
  for (let i_idx = 0; i_idx < 50; i_idx++) {
    formData['lane_' + i_idx] = ''
    formData['station_' + i_idx] = ''
    formData['leftDeflection_' + i_idx] = ''
    formData['remarks_' + i_idx] = ''
    formData['rightDeflection_' + i_idx] = ''
  }

})

const printDocument = () => {
  window.print()
}

const generatePdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/beckman_beam_report/generate'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}

const previewPdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/beckman_beam_report/preview'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}
</script>

<style scoped>

        .beckmanBeamReport-container {
            font-family: 'SimSun', 'Songti SC', serif;
            width: 210mm;
            margin: 0 auto;
            padding: 20px;
        }
        h1 {
            text-align: center;
            font-size: 24px;
            margin-bottom: 20px;
        }
        .header-info {
            display: flex;
            justify-content: space-between;
            margin-bottom: 5px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            border: 1px solid black;
            table-layout: fixed;
            margin-bottom: -1px; /* Collapse borders between tables */
        }
        td, th {
            border: 1px solid black;
            padding: 5px;
            text-align: center;
            font-size: 14px;
            vertical-align: middle;
            word-wrap: break-word;
        }
        input[type="text"] {
            width: 95%;
            border: none;
            outline: none;
            text-align: center;
            font-family: inherit;
            font-size: inherit;
            background: transparent;
        }
        .label {
            text-align: center;
        }
        /* Hide navigation buttons when printing */
        @media print {
            .no-print {
                display: none;
            }
            .beckmanBeamReport-container {
                width: 100%;
                margin: 0;
                padding: 0;
            }
            @page {
                size: A4 portrait;
                margin: 1cm;
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
        .footer-section {
            margin-top: 20px;
        }
        .footer-line {
            display: flex;
            justify-content: space-between;
            margin-bottom: 10px;
        }
        .company-info {
            margin-top: 20px;
            font-size: 14px;
        }
    
</style>
