<template>
  <div class="lightDynamicPenetrationRecord-container">


    <div class="no-print" style="margin-bottom: 20px;">
        <button @click="goToHome" style="text-decoration: none; color: blue; background: none; border: none; cursor: pointer; padding: 0;">&lt; 返回主页</button>
        <button @click="saveData" style="float: right; margin-left: 10px; background-color: #007bff; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;">保存</button>
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
import { reactive, ref, onMounted, defineProps, inject } from 'vue'
import axios from 'axios'

// 注入导航方法
const navigateTo = inject('navigateTo')

const props = defineProps({
  id: String
})

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

// Add dynamic fields to formData
for (let b = 0; b < 2; b++) {
    formData[`pos_L_${b}`] = ''
    formData[`avg_L_${b}`] = ''
    formData[`capacity_L_${b}`] = ''
    formData[`pos_R_${b}`] = ''
    formData[`avg_R_${b}`] = ''
    formData[`capacity_R_${b}`] = ''
    
    for (let s = 0; s < 5; s++) {
        const idx = b * 5 + s
        formData[`depth_L_${idx}`] = ''
        formData[`actual_L_${idx}`] = ''
        formData[`depth_R_${idx}`] = ''
        formData[`actual_R_${idx}`] = ''
    }
}

const formatDate = (d) => {
    if (!d) return ''
    const date = new Date(d)
    const year = date.getFullYear()
    const month = ('0' + (date.getMonth() + 1)).slice(-2)
    const day = ('0' + date.getDate()).slice(-2)
    return `${year}-${month}-${day}`
}

const loadData = async () => {
    if (!props.id) return
    try {
        const res = await axios.get(`/api/light-dynamic-penetration/${props.id}`)
        if (res.data.success && res.data.data) {
            const data = res.data.data
            formData.entrustingUnit = data.clientUnit || ''
            formData.unifiedNumber = data.wtNum || ''
            formData.projectName = data.projectName || ''
            formData.commissionDate = formatDate(data.commissionDate)
            formData.constructionPart = data.constructionPart || ''
            formData.testDate = data.testDate || '' // Keep as string if from JSON or other field? Entity has no testDate, only reportDate. Wait, dataJson has testDate.
            formData.soilProperties = data.soilProperty || ''
            formData.testCategory = data.testCategory || ''
            formData.designCapacity = data.designCapacity || ''
            formData.hammerWeight = data.hammerWeight || ''
            formData.dropDistance = data.dropDistance || ''
            formData.testBasis = data.testBasis || ''
            formData.equipment = data.equipment || ''
            formData.remarks = data.remarks || ''
            formData.reviewer = data.reviewer || ''
            // calculator is not in entity, maybe in dataJson?
            formData.tester = data.tester || ''
            formData.conclusion = data.conclusion || ''

            if (data.dataJson) {
                try {
                    const json = JSON.parse(data.dataJson)
                    Object.assign(formData, json)
                    if (json.testDate) formData.testDate = json.testDate
                } catch (e) {
                    console.error('JSON parse error', e)
                }
            }
        }
    } catch (e) {
        console.error('Load error', e)
    }
}

onMounted(() => {
    loadData()
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

const submitForm = async () => { // Note: There is no Submit button in template, but I should add one or use a save function
    // Template has buttons calling save? No, template has no save button in Record.vue currently.
    // I should add a save button.
}

const saveData = async () => {
    if (!props.id) {
        alert('无效的委托单ID')
        return
    }
    try {
        const dynamicData = {}
        if (formData.testDate) dynamicData.testDate = formData.testDate
        if (formData.calculator) dynamicData.calculator = formData.calculator
        
        for (const key in formData) {
            if (key.match(/^(pos|depth|actual|avg|capacity)_[LR]_\d+$/)) {
                dynamicData[key] = formData[key]
            }
        }

        const payload = {
            id: props.id,
            soilProperty: formData.soilProperties, // Note mapping difference: soilProperties vs soilProperty
            designCapacity: formData.designCapacity,
            hammerWeight: formData.hammerWeight,
            dropDistance: formData.dropDistance,
            testBasis: formData.testBasis,
            equipment: formData.equipment,
            remarks: formData.remarks,
            reviewer: formData.reviewer,
            tester: formData.tester,
            conclusion: formData.conclusion,
            // reportDate? Record doesn't have reportDate input.
            dataJson: JSON.stringify(dynamicData)
        }

        const res = await axios.post('/api/light-dynamic-penetration/save', payload)
        if (res.data.success) {
            alert('保存成功')
        } else {
            alert('保存失败: ' + (res.data.message || '未知错误'))
        }
    } catch (e) {
        alert('保存失败: ' + e.message)
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
