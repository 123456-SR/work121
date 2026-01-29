<template>
  <div class="lightDynamicPenetrationRecord-container">


    <div class="no-print" style="margin-bottom: 20px;">
        <a href="/" style="text-decoration: none; color: blue;">&lt; 返回主页</a>
        <button @click="printDocument" style="float: right; margin-left: 10px;">打印此单</button>
        <button @click="generatePdf" style="float: right; margin-left: 10px;">下载PDF</button>
        <button @click="previewPdf" style="float: right; margin-left: 10px;">预览PDF</button>
    </div>

    <form id="pdfForm" ref="pdfForm" method="post">
    <h2>轻型动力触探检测记录表</h2>

    <div class="header-info">
        <span>委托单位：<input type="text" v-model="formData.entrustingUnit"   name="entrustingUnit" style="width: 250px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>统一编号：<input type="text" v-model="formData.unifiedNumber"   name="unifiedNumber" style="width: 150px; border-bottom: 1px solid black;"></span>
    </div>

    <table>
        <!-- Row 1 -->
        <tr>
            <td class="label">工程名称</td>
            <td colspan="4"><input type="text" v-model="formData.projectName"   name="projectName"></td>
            <td class="label">委托日期</td>
            <td colspan="4"><input type="text" v-model="formData.commissionDate"   name="commissionDate"></td>
        </tr>
        <!-- Row 2 -->
        <tr>
            <td class="label">施工部位</td>
            <td colspan="4"><input type="text" v-model="formData.constructionPart"   name="constructionPart"></td>
            <td class="label">检测日期</td>
            <td colspan="4"><input type="text" v-model="formData.testDate"   name="testDate"></td>
        </tr>
        <!-- Row 3 -->
        <tr>
            <td class="label">岩土性状</td>
            <td colspan="4"><input type="text" v-model="formData.soilProperties"   name="soilProperties"></td>
            <td class="label">检测类别</td>
            <td colspan="4"><input type="text" v-model="formData.testCategory"   name="testCategory"></td>
        </tr>
        <!-- Row 4: Params -->
        <tr>
            <td colspan="3" class="label">设计承载力<br>(kPa)</td>
            <td colspan="3" class="label">锤重量<br>(kg)</td>
            <td colspan="4" class="label">落距<br>(cm)</td>
        </tr>
        <tr>
            <td colspan="3"><input type="text" v-model="formData.designCapacity"   name="designCapacity"></td>
            <td colspan="3"><input type="text" v-model="formData.hammerWeight"   name="hammerWeight"></td>
            <td colspan="4"><input type="text" v-model="formData.dropDistance"   name="dropDistance"></td>
        </tr>

        <!-- Row 5: Table Header for Data -->
        <tr>
            <td class="label" style="width: 10%;">测点<br>位置</td>
            <td class="label" style="width: 10%;">贯入<br>深度<br>(cm)</td>
            <td class="label" style="width: 10%;">实测<br>锤击数</td>
            <td class="label" style="width: 10%;">平均<br>锤击数<br>N<sub>10</sub></td>
            <td class="label" style="width: 10%;">承载力<br>特征值<br>(kPa)</td>
            <td class="label" style="width: 10%;">测点<br>位置</td>
            <td class="label" style="width: 10%;">贯入<br>深度<br>(cm)</td>
            <td class="label" style="width: 10%;">实测<br>锤击数</td>
            <td class="label" style="width: 10%;">平均<br>锤击数<br>N<sub>10</sub></td>
            <td class="label" style="width: 10%;">承载力<br>特征值<br>(kPa)</td>
        </tr>

        <!-- Data Rows -->
        <template v-for="(b, b_idx) in 2" :key="b_idx">
            <template v-for="(s, s_idx) in 5" :key="s_idx">
            <tr>
                <!-- 左栏 -->
                <td v-if="s_idx === 0" rowspan="5"><textarea :name="'pos_L_' + b_idx" v-model="formData['pos_L_' + b_idx]" style="height: 100%; width: 100%; border: none; text-align: center; padding-top: 10px;"></textarea></td>

                <td><input type="text" :name="'depth_L_' + (b_idx * 5 + s_idx)" v-model="formData['depth_L_' + (b_idx * 5 + s_idx)]"></td>
                <td><input type="text" :name="'actual_L_' + (b_idx * 5 + s_idx)" v-model="formData['actual_L_' + (b_idx * 5 + s_idx)]"></td>

                <td v-if="s_idx === 0" rowspan="5"><input type="text" :name="'avg_L_' + b_idx" v-model="formData['avg_L_' + b_idx]" style="height: 100%;"></td>
                <td v-if="s_idx === 0" rowspan="5"><input type="text" :name="'capacity_L_' + b_idx" v-model="formData['capacity_L_' + b_idx]" style="height: 100%;"></td>

                <!-- 右栏 -->
                <td v-if="s_idx === 0" rowspan="5"><textarea :name="'pos_R_' + b_idx" v-model="formData['pos_R_' + b_idx]" style="height: 100%; width: 100%; border: none; text-align: center; padding-top: 10px;"></textarea></td>

                <td><input type="text" :name="'depth_R_' + (b_idx * 5 + s_idx)" v-model="formData['depth_R_' + (b_idx * 5 + s_idx)]"></td>
                <td><input type="text" :name="'actual_R_' + (b_idx * 5 + s_idx)" v-model="formData['actual_R_' + (b_idx * 5 + s_idx)]"></td>

                <td v-if="s_idx === 0" rowspan="5"><input type="text" :name="'avg_R_' + b_idx" v-model="formData['avg_R_' + b_idx]" style="height: 100%;"></td>
                <td v-if="s_idx === 0" rowspan="5"><input type="text" :name="'capacity_R_' + b_idx" v-model="formData['capacity_R_' + b_idx]" style="height: 100%;"></td>
            </tr>
            </template>
        </template>

        <!-- Row: 检测依据 -->
        <tr>
            <td class="label">检测依据</td>
            <td colspan="9" class="left-align"><input type="text" v-model="formData.testBasis"   name="testBasis"></td>
        </tr>
        <!-- Row: 仪器设备 -->
        <tr>
            <td class="label">仪器设备</td>
            <td colspan="9" class="left-align"><input type="text" v-model="formData.equipment"   name="equipment"></td>
        </tr>
        <!-- Row: 检测结论 -->
        <tr>
            <td class="label" style="height: 60px;">检测结论</td>
            <td colspan="9" class="left-align" style="vertical-align: top;">
                <textarea v-model="formData.conclusion"  name="conclusion" rows="3" style="width: 100%; height: 100%;"></textarea>
            </td>
        </tr>
        <!-- Row: 备注 -->
        <tr>
            <td class="label">备注</td>
            <td colspan="9" class="left-align"><input type="text" v-model="formData.remarks"   name="remarks"></td>
        </tr>
    </table>

    <div class="footer-info">
        <span>审核：<input type="text" v-model="formData.reviewer"   name="reviewer" style="width: 100px; border-bottom: 1px solid black;"></span>
        <span>计算：<input type="text" v-model="formData.calculator"   name="calculator" style="width: 100px; border-bottom: 1px solid black;"></span>
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
  projectName: '',
  commissionDate: '',
  constructionPart: '',
  testDate: '',
  soilProperties: '',
  testCategory: '',
  designCapacity: '',
  hammerWeight: '',
  dropDistance: '',
  testBasis: '',
  equipment: '',
  remarks: '',
  reviewer: '',
  calculator: '',
  tester: '',
  conclusion: '',
})

onMounted(() => {

})

const printDocument = () => {
  window.print()
}

const generatePdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/light_dynamic_penetration_record/generate'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}

const previewPdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/light_dynamic_penetration_record/preview'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}
</script>

<style scoped>

        .lightDynamicPenetrationRecord-container {
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
            .lightDynamicPenetrationRecord-container {
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
