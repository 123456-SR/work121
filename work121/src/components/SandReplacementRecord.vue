<template>
  <div class="sandReplacementRecord-container">


    <div class="no-print" style="margin-bottom: 20px;">
        <a href="/" style="text-decoration: none; color: blue;">&lt; 返回主页</a>
        <button @click="printDocument" style="float: right; margin-left: 10px;">打印此单</button>
        <button @click="generatePdf" style="float: right; margin-left: 10px; background-color: #28a745; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;">下载PDF</button>
        <button @click="previewPdf" style="float: right; margin-left: 10px; background-color: #17a2b8; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;">预览PDF</button>
    </div>

    <form id="pdfForm" ref="pdfForm" method="post">
    <h2>原位密度检测记录表（灌砂法）</h2>

    <div class="header-info">
        <span>单元工程名称：<input type="text" v-model="formData.projectName"   name="projectName" style="width: 200px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>试验日期：<input type="text" v-model="formData.testDate"   name="testDate" style="width: 150px; border-bottom: 1px solid black;"></span>
        <span>统一编号：<input type="text" v-model="formData.unifiedNumber"   name="unifiedNumber" style="width: 150px; border-bottom: 1px solid black;"></span>
    </div>
     <div class="header-info">
        <span>依据标准：<input type="text" v-model="formData.standard"   name="standard" style="width: 150px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>最大干密度 (g/cm³)：<input type="text" v-model="formData.maxDryDensity"   name="maxDryDensity" style="width: 80px; border-bottom: 1px solid black;"></span>
        <span>最优含水率 (%)：<input type="text" v-model="formData.optMoisture"   name="optMoisture" style="width: 80px; border-bottom: 1px solid black;"></span>
        <span>最小干密度 (g/cm³)：<input type="text" v-model="formData.minDryDensity"   name="minDryDensity" style="width: 80px; border-bottom: 1px solid black;"></span>
    </div>
    <div class="header-info">
        <span>量砂密度：<input type="text" v-model="formData.sandDensity"   name="sandDensity" style="width: 80px; border-bottom: 1px solid black;"> g/cm³</span>
        <span>仪器设备：<input type="text" v-model="formData.equipment"   name="equipment" style="width: 150px; border-bottom: 1px solid black;"></span>
        <span>检测类别：<input type="text" v-model="formData.testCategory"   name="testCategory" style="width: 100px; border-bottom: 1px solid black;"></span>
        <span>设计压实度：<input type="text" v-model="formData.designCompaction"   name="designCompaction" style="width: 100px; border-bottom: 1px solid black;"></span>
    </div>


    <table>
        <!-- Table Header -->
        <tr>
            <td colspan="2" class="label" style="width: 20%;">检测部位<br>(桩号、高程)</td>
            <template v-for="(n, i_idx) in 4" :key="i_idx">
            <td colspan="4"><input type="text" :name="'location_' + i_idx" v-model="formData['location_' + i_idx]"></td>
            </template>
        </tr>

        <!-- Data Rows -->
        <tr>
            <td class="label">量砂容器+原有砂质量</td>
            <td class="label">g</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'totalSandMass_' + i_idx" v-model="formData['totalSandMass_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td class="label">量砂容器+剩余砂质量</td>
            <td class="label">g</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'remainingSandMass_' + i_idx" v-model="formData['remainingSandMass_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td class="label">锥体内砂质量</td>
            <td class="label">g</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'coneSandMass_' + i_idx" v-model="formData['coneSandMass_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td class="label">试坑耗砂质量</td>
            <td class="label">g</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'pitSandMass_' + i_idx" v-model="formData['pitSandMass_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td class="label">试坑体积</td>
            <td class="label">cm³</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'pitVolume_' + i_idx" v-model="formData['pitVolume_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td class="label">试样质量</td>
            <td class="label">g</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'sampleMass_' + i_idx" v-model="formData['sampleMass_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td class="label">试样湿密度</td>
            <td class="label">g/cm³</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'wetDensity_' + i_idx" v-model="formData['wetDensity_' + i_idx]"></td>
            </template>
        </tr>

        <!-- Moisture Content Section -->
        <tr>
            <td rowspan="6" class="label">试样含水率</td>
            <td class="label">盒号</td>
            <template v-for="(n, i_idx) in 16" :key="i_idx">
            <td><input type="text" :name="'boxNo_' + i_idx" v-model="formData['boxNo_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td class="label">盒质量 g</td>
            <template v-for="(n, i_idx) in 16" :key="i_idx">
            <td><input type="text" :name="'boxMass_' + i_idx" v-model="formData['boxMass_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td class="label">盒+湿样质量 g</td>
            <template v-for="(n, i_idx) in 16" :key="i_idx">
            <td><input type="text" :name="'boxWetMass_' + i_idx" v-model="formData['boxWetMass_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td class="label">盒+干样质量 g</td>
            <template v-for="(n, i_idx) in 16" :key="i_idx">
            <td><input type="text" :name="'boxDryMass_' + i_idx" v-model="formData['boxDryMass_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td class="label">含水率 %</td>
            <template v-for="(n, i_idx) in 16" :key="i_idx">
            <td><input type="text" :name="'moistureContent_' + i_idx" v-model="formData['moistureContent_' + i_idx]"></td>
            </template>
        </tr>
         <tr>
            <td class="label">平均含水率 %</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'avgMoisture_' + i_idx" v-model="formData['avgMoisture_' + i_idx]"></td>
            </template>
        </tr>


        <!-- Results -->
        <tr>
            <td colspan="2" class="label">实测干密度 g/cm³</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'dryDensity_' + i_idx" v-model="formData['dryDensity_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td colspan="2" class="label">平均实测干密度 g/cm³</td>
            <template v-for="(n, i_idx) in 4" :key="i_idx">
            <td colspan="4"><input type="text" :name="'avgDryDensity_' + i_idx" v-model="formData['avgDryDensity_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td colspan="2" class="label">实测压实度</td>
            <template v-for="(n, i_idx) in 4" :key="i_idx">
            <td colspan="4"><input type="text" :name="'compaction_' + i_idx" v-model="formData['compaction_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td colspan="2" class="label">备注</td>
            <td colspan="16" class="left-align"><input type="text" v-model="formData.remarks"   name="remarks"></td>
        </tr>
    </table>

    <div class="footer-info">
        <span>批准：<input type="text" v-model="formData.approver"   name="approver" style="width: 100px; border-bottom: 1px solid black;"></span>
        <span>审核：<input type="text" v-model="formData.reviewer"   name="reviewer" style="width: 100px; border-bottom: 1px solid black;"></span>
        <span>试验：<input type="text" v-model="formData.tester"   name="tester" style="width: 100px; border-bottom: 1px solid black;"></span>
    </div>

    </form>



  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'

const pdfForm = ref(null)

const formData = reactive({
  projectName: '',
  testDate: '',
  unifiedNumber: '',
  standard: '',
  maxDryDensity: '',
  optMoisture: '',
  minDryDensity: '',
  sandDensity: '',
  equipment: '',
  testCategory: '',
  designCompaction: '',
  remarks: '',
  approver: '',
  reviewer: '',
  tester: '',
})

onMounted(() => {

  // Initialize dynamic fields for loop variable 'i_idx'
  // Please verify the loop count match the template
  for (let i_idx = 0; i_idx < 50; i_idx++) {
    formData['totalSandMass_' + i_idx] = ''
    formData['location_' + i_idx] = ''
    formData['boxMass_' + i_idx] = ''
    formData['avgDryDensity_' + i_idx] = ''
    formData['pitVolume_' + i_idx] = ''
    formData['compaction_' + i_idx] = ''
    formData['moistureContent_' + i_idx] = ''
    formData['boxNo_' + i_idx] = ''
    formData['pitSandMass_' + i_idx] = ''
    formData['wetDensity_' + i_idx] = ''
    formData['avgMoisture_' + i_idx] = ''
    formData['dryDensity_' + i_idx] = ''
    formData['coneSandMass_' + i_idx] = ''
    formData['boxWetMass_' + i_idx] = ''
    formData['remainingSandMass_' + i_idx] = ''
    formData['boxDryMass_' + i_idx] = ''
    formData['sampleMass_' + i_idx] = ''
  }

})

const printDocument = () => {
  window.print()
}

const generatePdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/sand_replacement_record/generate'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}

const previewPdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/sand_replacement_record/preview'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}
</script>

<style scoped>

        .sandReplacementRecord-container {
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
        }
        .header-info span {
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
            .sandReplacementRecord-container {
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
