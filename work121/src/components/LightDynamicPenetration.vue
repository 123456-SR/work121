<template>
  <div class="lightDynamicPenetration-container">


    <div class="no-print" style="margin-bottom: 20px;">
        <a href="/" style="text-decoration: none; color: blue;">&lt; 返回主页</a>
        <button @click="printDocument" style="float: right; margin-left: 10px;">打印此单</button>
        <button @click="generatePdf" style="float: right; margin-left: 10px; background-color: #28a745; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;">下载PDF</button>
        <button @click="previewPdf" style="float: right; margin-left: 10px; background-color: #17a2b8; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;">预览PDF</button>
    </div>

    <form id="pdfForm" ref="pdfForm" method="post">
    <h2>轻型动力触探检测报告</h2>

    <div class="header-info">
        <span>委托单位：<input type="text" v-model="formData.entrustingUnit"   name="entrustingUnit" style="width: 200px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>统一编号：<input type="text" v-model="formData.unifiedNumber"   name="unifiedNumber" style="width: 150px; border-bottom: 1px solid black;"></span>
    </div>

    <table>
        <!-- Row 1 -->
        <tr>
            <td class="label" style="width: 15%;">工程名称</td>
            <td colspan="4"><input type="text" v-model="formData.projectName"   name="projectName"></td>
            <td class="label" style="width: 15%;">委托日期</td>
            <td colspan="4"><input type="text" v-model="formData.entrustDate"   name="entrustDate"></td>
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
            <td colspan="4"><input type="text" v-model="formData.soilProperty"   name="soilProperty"></td>
            <td class="label">报告日期</td>
            <td colspan="4"><input type="text" v-model="formData.reportDate"   name="reportDate"></td>
        </tr>
        <!-- Row 4 -->
        <tr>
            <td class="label">见证单位</td>
            <td colspan="4"><input type="text" v-model="formData.witnessUnit"   name="witnessUnit"></td>
            <td class="label">见证人</td>
            <td colspan="4"><input type="text" v-model="formData.witness"   name="witness"></td>
        </tr>
        <!-- Row 5 -->
        <tr>
            <td class="label">设计<br>承载力<br>(kPa)</td>
            <td colspan="2"><input type="text" v-model="formData.designCapacity"   name="designCapacity"></td>
            <td class="label">锤重量<br>(kg)</td>
            <td><input type="text" v-model="formData.hammerWeight"   name="hammerWeight"></td>
            <td class="label">落距<br>(cm)</td>
            <td><input type="text" v-model="formData.dropDistance"   name="dropDistance"></td>
            <td class="label">检测类别</td>
            <td colspan="2"><input type="text" v-model="formData.testCategory"   name="testCategory"></td>
        </tr>
        <!-- Row 6: Table Header for Data -->
        <tr>
            <td class="label">测点位置</td>
            <td class="label">贯入<br>深度<br>(cm)</td>
            <td class="label">实测<br>锤击数</td>
            <td class="label">平均<br>锤击数<br>N<sub>10</sub></td>
            <td class="label">承载力<br>特征值<br>(kPa)</td>
            <td class="label">测点位置</td>
            <td class="label">贯入<br>深度<br>(cm)</td>
            <td class="label">实测<br>锤击数</td>
            <td class="label">平均<br>锤击数<br>N<sub>10</sub></td>
            <td class="label">承载力<br>特征值<br>(kPa)</td>
        </tr>

        <!-- Data Rows -->
        <template v-for="(block, blockIndex) in formData.dataBlocks" :key="blockIndex">
            <template v-for="(depth, subIndex) in block.depths" :key="`${blockIndex}-${subIndex}`">
                <tr>
                    <!-- 左栏 -->
                    <td v-if="subIndex === 0" rowspan="2"><textarea v-model="block.pos_L" :name="`pos_L_${blockIndex}`" style="height: 100%; width: 100%; border: none; text-align: center; padding-top: 10px;"></textarea></td>
                    <td v-else></td>

                    <td><input type="text" v-model="depth.depth_L" :name="`depth_L_${blockIndex * 2 + subIndex}`"></td>
                    <td><input type="text" v-model="depth.actual_L" :name="`actual_L_${blockIndex * 2 + subIndex}`"></td>

                    <td v-if="subIndex === 0" rowspan="2"><input type="text" v-model="block.avg_L" :name="`avg_L_${blockIndex}`" style="height: 100%;"></td>
                    <td v-else></td>
                    <td v-if="subIndex === 0" rowspan="2"><input type="text" v-model="block.capacity_L" :name="`capacity_L_${blockIndex}`" style="height: 100%;"></td>
                    <td v-else></td>

                    <!-- 右栏 -->
                    <td v-if="subIndex === 0" rowspan="2"><textarea v-model="block.pos_R" :name="`pos_R_${blockIndex}`" style="height: 100%; width: 100%; border: none; text-align: center; padding-top: 10px;"></textarea></td>
                    <td v-else></td>

                    <td><input type="text" v-model="block.depths_R[subIndex].depth_R" :name="`depth_R_${blockIndex * 2 + subIndex}`"></td>
                    <td><input type="text" v-model="block.depths_R[subIndex].actual_R" :name="`actual_R_${blockIndex * 2 + subIndex}`"></td>

                    <td v-if="subIndex === 0" rowspan="2"><input type="text" v-model="block.avg_R" :name="`avg_R_${blockIndex}`" style="height: 100%;"></td>
                    <td v-else></td>
                    <td v-if="subIndex === 0" rowspan="2"><input type="text" v-model="block.capacity_R" :name="`capacity_R_${blockIndex}`" style="height: 100%;"></td>
                    <td v-else></td>
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
            <td class="label" style="height: 80px;">检测结论</td>
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
        <span>批准：<input type="text" v-model="formData.approve"   name="approve" style="width: 100px; border-bottom: 1px solid black;"></span>
        <span>审核：<input type="text" v-model="formData.review"   name="review" style="width: 100px; border-bottom: 1px solid black;"></span>
        <span>检验：<input type="text" v-model="formData.inspect"   name="inspect" style="width: 100px; border-bottom: 1px solid black;"></span>
    </div>

    <div class="statement" style="font-size: 12px; margin-top: 10px;">
        声明：<br>
        1. 对本检测报告的复印件未加盖公司检验检测专用章无效。<br>
        2. 对检测结果如有异议，应在收到报告之日起十五日之内向本公司提出。
    </div>

    <div class="company-info" style="display: block; margin-top: 5px; font-size: 14px; font-weight: bold;">
        <div>公司名称：<input type="text" v-model="formData.companyName"   name="companyName" style="width: 70%; border: none; text-align: left;" value="河北金涛建设工程质量检测有限公司"></div>
        <div style="display: flex; justify-content: space-between; margin-top: 5px;">
            <span>公司地址：<input type="text" v-model="formData.companyAddress"   name="companyAddress" style="width: 300px; border: none; text-align: left;" value="石家庄高新区方亿科技工业园A区第2号楼。"></span>
            <span>电话：<input type="text" v-model="formData.companyPhone"   name="companyPhone" style="width: 200px; border: none;" value="0311—86107634  0311—67300616"></span>
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
  entrustDate: '',
  constructionPart: '',
  testDate: '',
  soilProperty: '',
  reportDate: '',
  witnessUnit: '',
  witness: '',
  designCapacity: '',
  hammerWeight: '',
  dropDistance: '',
  testCategory: '',
  testBasis: '',
  equipment: '',
  remarks: '',
  approve: '',
  review: '',
  inspect: '',
  companyName: '',
  companyAddress: '',
  companyPhone: '',
  conclusion: '',
  dataBlocks: [
    { pos_L: '', depths: [{ depth_L: '', actual_L: '' }, { depth_L: '', actual_L: '' }], avg_L: '', capacity_L: '', pos_R: '', depths_R: [{ depth_R: '', actual_R: '' }, { depth_R: '', actual_R: '' }], avg_R: '', capacity_R: '' },
    { pos_L: '', depths: [{ depth_L: '', actual_L: '' }, { depth_L: '', actual_L: '' }], avg_L: '', capacity_L: '', pos_R: '', depths_R: [{ depth_R: '', actual_R: '' }, { depth_R: '', actual_R: '' }], avg_R: '', capacity_R: '' },
    { pos_L: '', depths: [{ depth_L: '', actual_L: '' }, { depth_L: '', actual_L: '' }], avg_L: '', capacity_L: '', pos_R: '', depths_R: [{ depth_R: '', actual_R: '' }, { depth_R: '', actual_R: '' }], avg_R: '', capacity_R: '' },
    { pos_L: '', depths: [{ depth_L: '', actual_L: '' }, { depth_L: '', actual_L: '' }], avg_L: '', capacity_L: '', pos_R: '', depths_R: [{ depth_R: '', actual_R: '' }, { depth_R: '', actual_R: '' }], avg_R: '', capacity_R: '' }
  ]
})

onMounted(() => {

})

const printDocument = () => {
  window.print()
}

const generatePdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/light_dynamic_penetration/generate'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}

const previewPdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/light_dynamic_penetration/preview'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}
</script>

<style scoped>

        .lightDynamicPenetration-container {
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
            justify-content: space-around;
            margin-top: 30px;
            margin-bottom: 20px;
            font-size: 16px;
            font-weight: bold;
        }
        .statement {
            font-size: 12px;
            line-height: 1.6;
            margin-top: 10px;
        }
        .page-footer {
            margin-top: 10px;
            text-align: right;
            font-size: 14px;
            margin-bottom: 20px;
        }
        @media print {
            .lightDynamicPenetration-container {
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
