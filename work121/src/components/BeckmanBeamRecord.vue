<template>
  <div class="beckmanBeamRecord-container">

    <div class="no-print" style="margin-bottom: 20px;">
        <button @click="goToHome" style="text-decoration: none; color: blue; background: none; border: none; cursor: pointer; padding: 0;">&lt; 返回主页</button>
        <button @click="printDocument" style="float: right; margin-left: 10px;">打印此单</button>
        <button @click="generatePdf" style="float: right; margin-left: 10px;">下载PDF</button>
        <button @click="previewPdf" style="float: right; margin-left: 10px;">预览PDF</button>
    </div>

    <form id="pdfForm" ref="pdfForm" method="post">
    <h1>路基路面回弹弯沉试验检测记录表（贝克曼梁法）</h1>

    <div class="header-info">
        <div>委托单位：<input type="text" v-model="formData.entrustingUnit"   name="entrustingUnit" style="width: 200px; border-bottom: 1px solid black; text-align: left;"></div>
        <div>统一编号：<input type="text" v-model="formData.unifiedNumber"   name="unifiedNumber" style="width: 150px; border-bottom: 1px solid black;"></div>
    </div>

    <!-- Top Info Table -->
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
            <td class="label">检测类别</td>
            <td><input type="text" v-model="formData.testCategory"   name="testCategory"></td>
        </tr>
        <tr>
            <td class="label">样品名称及状态</td>
            <td><input type="text" v-model="formData.sampleStatus"   name="sampleStatus"></td>
            <td class="label">检测方法</td>
            <td>贝克曼梁法</td>
        </tr>
    </table>

    <!-- Detailed Info Table (6 columns) -->
    <table>
        <colgroup>
            <col style="width: 16%;">
            <col style="width: 17%;">
            <col style="width: 16%;">
            <col style="width: 17%;">
            <col style="width: 16%;">
            <col style="width: 18%;">
        </colgroup>
        <tr>
            <td class="label">依据标准</td>
            <td><input type="text" v-model="formData.standard"   name="standard"></td>
            <td class="label">设计弯沉值(0.01mm)</td>
            <td><input type="text" v-model="formData.designDeflection"   name="designDeflection"></td>
            <td class="label">温度修正系数K</td>
            <td><input type="text" v-model="formData.tempCorrectionK"   name="tempCorrectionK"></td>
        </tr>
        <tr>
            <td class="label">测试车车型</td>
            <td><input type="text" v-model="formData.vehicleModel"   name="vehicleModel"></td>
            <td class="label">沥青面层平均温度<br>t=(t<sub>25</sub>+t<sub>m</sub>+t<sub>e</sub>)/3</td>
            <td><input type="text" v-model="formData.avgAsphaltTemp"   name="avgAsphaltTemp"></td>
            <td class="label">轮胎传压面积(cm<sup>2</sup>)</td>
            <td><input type="text" v-model="formData.tireArea"   name="tireArea"></td>
        </tr>
        <tr>
            <td class="label">前5天平均气温的平均值(℃)</td>
            <td><input type="text" v-model="formData.avgTempPrev5Days"   name="avgTempPrev5Days"></td>
            <td class="label">路面结构类型</td>
            <td><input type="text" v-model="formData.pavementType"   name="pavementType"></td>
            <td class="label">路面厚度(mm)</td>
            <td><input type="text" v-model="formData.pavementThickness"   name="pavementThickness"></td>
        </tr>
        <tr>
            <td class="label">后轴重(kN)</td>
            <td><input type="text" v-model="formData.rearAxleWeight"   name="rearAxleWeight"></td>
            <td class="label">轮胎气压左(MPa)</td>
            <td><input type="text" v-model="formData.tirePressureLeft"   name="tirePressureLeft"></td>
            <td class="label">轮胎气压右(MPa)</td>
            <td><input type="text" v-model="formData.tirePressureRight"   name="tirePressureRight"></td>
        </tr>
    </table>

    <!-- Main Data Table -->
    <table>
        <colgroup>
            <col style="width: 5%;"> <!-- 序号 -->
            <col style="width: 11%;"> <!-- 测点桩号 -->
            <col style="width: 7%;"> <!-- 车道 -->
            <col style="width: 10%;"> <!-- 路表温度 -->
            <col style="width: 11%;"> <!-- 左-初 -->
            <col style="width: 11%;"> <!-- 左-终 -->
            <col style="width: 11%;"> <!-- 左-回 -->
            <col style="width: 11%;"> <!-- 右-初 -->
            <col style="width: 11%;"> <!-- 右-终 -->
            <col style="width: 12%;"> <!-- 右-回 -->
        </colgroup>
        <thead>
            <tr>
                <th rowspan="2">序号</th>
                <th rowspan="2">测点桩号<br>(幅段)</th>
                <th rowspan="2">车道</th>
                <th rowspan="2">路表温度<br>(℃)</th>
                <th colspan="3">左侧(0.01mm)</th>
                <th colspan="3">右侧(0.01mm)</th>
            </tr>
            <tr>
                <th>初读数</th>
                <th>终读数</th>
                <th>回弹弯沉</th>
                <th>初读数</th>
                <th>终读数</th>
                <th>回弹弯沉</th>
            </tr>
        </thead>
        <tbody>
            <template v-for="(n, i_idx) in 15" :key="i_idx">
            <tr>
                <td>{{ (i_idx + 1) }}</td>
                <td><input type="text" :name="'station_' + (i_idx + 1)" v-model="formData['station_' + (i_idx + 1)]"></td>
                <td><input type="text" :name="'lane_' + (i_idx + 1)" v-model="formData['lane_' + (i_idx + 1)]"></td>
                <td><input type="text" :name="'surfaceTemp_' + (i_idx + 1)" v-model="formData['surfaceTemp_' + (i_idx + 1)]"></td>
                <td><input type="text" :name="'leftInitial_' + (i_idx + 1)" v-model="formData['leftInitial_' + (i_idx + 1)]"></td>
                <td><input type="text" :name="'leftFinal_' + (i_idx + 1)" v-model="formData['leftFinal_' + (i_idx + 1)]"></td>
                <td><input type="text" :name="'leftDeflection_' + (i_idx + 1)" v-model="formData['leftDeflection_' + (i_idx + 1)]"></td>
                <td><input type="text" :name="'rightInitial_' + (i_idx + 1)" v-model="formData['rightInitial_' + (i_idx + 1)]"></td>
                <td><input type="text" :name="'rightFinal_' + (i_idx + 1)" v-model="formData['rightFinal_' + (i_idx + 1)]"></td>
                <td><input type="text" :name="'rightDeflection_' + (i_idx + 1)" v-model="formData['rightDeflection_' + (i_idx + 1)]"></td>
            </tr>
            </template>
        </tbody>
    </table>

    <!-- Statistics Table 1 -->
    <table>
        <colgroup>
            <col style="width: 10%;">
            <col style="width: 5%;">
            <col style="width: 5%;">
            <col style="width: 10%;">
            <col style="width: 10%;">
            <col style="width: 10%;">
            <col style="width: 10%;">
            <col style="width: 15%;">
            <col style="width: 5%;">
            <col style="width: 10%;">
        </colgroup>
        <tr>
            <td class="label">测试间距 (m)</td>
            <td class="label">车道数</td>
            <td class="label">测试总点数</td>
            <td class="label">测试公里 (km)</td>
            <td class="label">全部平均弯沉值<br>(0.01mm)</td>
            <td class="label">标准差<br>(0.01mm)</td>
            <td class="label">弯沉代表值<br>(0.01mm)</td>
            <td class="label">温度修正后平均弯沉值<br>(0.01mm)</td>
            <td class="label">特异点个数</td>
            <td class="label">是否合格</td>
        </tr>
        <tr>
            <td><input type="text" v-model="formData.testInterval"   name="testInterval"></td>
            <td><input type="text" v-model="formData.laneCount"   name="laneCount"></td>
            <td><input type="text" v-model="formData.totalPoints"   name="totalPoints"></td>
            <td><input type="text" v-model="formData.testKm"   name="testKm"></td>
            <td><input type="text" v-model="formData.totalAvgDeflection"   name="totalAvgDeflection"></td>
            <td><input type="text" v-model="formData.stdDev"   name="stdDev"></td>
            <td><input type="text" v-model="formData.repDeflection"   name="repDeflection"></td>
            <td><input type="text" v-model="formData.tempCorrectedAvg"   name="tempCorrectedAvg"></td>
            <td><input type="text" v-model="formData.outlierCount"   name="outlierCount"></td>
            <td><input type="text" v-model="formData.isQualified"   name="isQualified"></td>
        </tr>
    </table>

    <!-- Statistics Table 2 -->
    <table>
        <colgroup>
            <col style="width: 15%;">
            <col style="width: 10%;">
            <col style="width: 15%;">
            <col style="width: 15%;">
            <col style="width: 15%;">
            <col style="width: 20%;">
        </colgroup>
        <tr>
            <td class="label">特异值下限</td>
            <td class="label">特异值上限</td>
            <td class="label">去特异点平均弯沉值(0.01mm)</td>
            <td class="label">去特异点标准差<br>(0.01mm)</td>
            <td class="label">去特异点弯沉代表值(0.01mm)</td>
            <td class="label">去特异点温度修正平均弯沉值(0.01mm)</td>
        </tr>
        <tr>
            <td><input type="text" v-model="formData.outlierLower"   name="outlierLower"></td>
            <td><input type="text" v-model="formData.outlierUpper"   name="outlierUpper"></td>
            <td><input type="text" v-model="formData.cleanAvg"   name="cleanAvg"></td>
            <td><input type="text" v-model="formData.cleanStdDev"   name="cleanStdDev"></td>
            <td><input type="text" v-model="formData.cleanRepDeflection"   name="cleanRepDeflection"></td>
            <td><input type="text" v-model="formData.cleanTempCorrectedAvg"   name="cleanTempCorrectedAvg"></td>
        </tr>
    </table>

    <!-- Footer -->
    <table>
        <tr>
            <td style="width: 10%; text-align: center;">备注</td>
            <td><input type="text" v-model="formData.remarks"   name="remarks" style="width: 100%; text-align: left;"></td>
        </tr>
    </table>

    <div style="margin-top: 10px; display: flex; justify-content: space-between;">
        <div>审核：<input type="text" v-model="formData.reviewer"   name="reviewer" style="width: 100px; border-bottom: 1px solid black;"></div>
        <div>检测：<input type="text" v-model="formData.tester"   name="tester" style="width: 100px; border-bottom: 1px solid black;"></div>
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
  testDate: '',
  equipmentCode: '',
  testCategory: '',
  sampleStatus: '',
  standard: '',
  designDeflection: '',
  tempCorrectionK: '',
  vehicleModel: '',
  avgAsphaltTemp: '',
  tireArea: '',
  avgTempPrev5Days: '',
  pavementType: '',
  pavementThickness: '',
  rearAxleWeight: '',
  tirePressureLeft: '',
  tirePressureRight: '',
  testInterval: '',
  laneCount: '',
  totalPoints: '',
  testKm: '',
  totalAvgDeflection: '',
  stdDev: '',
  repDeflection: '',
  tempCorrectedAvg: '',
  outlierCount: '',
  isQualified: '',
  outlierLower: '',
  outlierUpper: '',
  cleanAvg: '',
  cleanStdDev: '',
  cleanRepDeflection: '',
  cleanTempCorrectedAvg: '',
  remarks: '',
  reviewer: '',
  tester: '',
})

onMounted(() => {

  // Initialize dynamic fields for loop variable 'i_idx'
  // Please verify the loop count match the template
  for (let i_idx = 0; i_idx < 50; i_idx++) {
    formData['rightDeflection_' + i_idx] = ''
    formData['lane_' + i_idx] = ''
    formData['station_' + i_idx] = ''
    formData['leftFinal_' + i_idx] = ''
    formData['leftDeflection_' + i_idx] = ''
    formData['rightFinal_' + i_idx] = ''
    formData['leftInitial_' + i_idx] = ''
    formData['rightInitial_' + i_idx] = ''
    formData['surfaceTemp_' + i_idx] = ''
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
    pdfForm.value.action = '/api/pdf/beckman_beam_record/generate'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}

const previewPdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/beckman_beam_record/preview'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}
</script>

<style scoped>

        .beckmanBeamRecord-container {
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
            .beckmanBeamRecord-container {
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
    
</style>
