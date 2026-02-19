<template>
  <div class="cuttingRingRecord-container">


    <div class="no-print" style="margin-bottom: 20px;">
        <button @click="goToHome" style="text-decoration: none; color: blue; background: none; border: none; cursor: pointer; padding: 0;">&lt; 返回主页</button>
        <button @click="prevForm" style="float: left; margin-left: 10px; background-color: #6c757d; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;">上一页</button>
        <button @click="nextForm" style="float: left; margin-left: 10px; background-color: #6c757d; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;">下一页</button>
        <button @click="printDocument" style="float: right; margin-left: 10px;">打印此单</button>
        <button @click="generatePdf" style="float: right; margin-left: 10px;">下载PDF</button>
        <button @click="previewPdf" style="float: right; margin-left: 10px;">预览PDF</button>
    </div>

    <form id="pdfForm" ref="pdfForm" method="post">
    <h2>原位密度检测记录表（环刀法）</h2>

    <div class="header-info">
        <span>施工部位：<input type="text" v-model="formData.constructionLocation"   name="constructionLocation" style="width: 200px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>最大干密度 (g/cm³)：<input type="text" v-model="formData.maxDryDensity"   name="maxDryDensity" style="width: 80px; border-bottom: 1px solid black;"></span>
        <span>最优含水率 (%)：<input type="text" v-model="formData.optMoisture"   name="optMoisture" style="width: 80px; border-bottom: 1px solid black;"></span>
        <span>检测类别：<input type="text" v-model="formData.testType"   name="testType" style="width: 100px; border-bottom: 1px solid black;"></span>
    </div>
    <div class="header-info">
        <span>依据标准：<input type="text" v-model="formData.standard"   name="standard" style="width: 200px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>设计压实度：<input type="text" v-model="formData.designCompaction"   name="designCompaction" style="width: 80px; border-bottom: 1px solid black;"></span>
        <span>检测日期：<input type="text" v-model="formData.testDate"   name="testDate" style="width: 150px; border-bottom: 1px solid black;"></span>
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
            <template v-for="(n, i_idx) in 5" :key="i_idx">
            <tr>
                <td rowspan="2"><input type="text" :name="'sampleNo_' + i_idx" v-model="formData['sampleNo_' + i_idx]"></td>
                <td rowspan="2"><input type="text" :name="'location_' + i_idx" v-model="formData['location_' + i_idx]"></td>
                <td rowspan="2"><input type="text" :name="'status_' + i_idx" v-model="formData['status_' + i_idx]"></td>
                <td rowspan="2"><input type="text" :name="'ringNo_' + i_idx" v-model="formData['ringNo_' + i_idx]"></td>
                <td rowspan="2"><input type="text" :name="'ringMass_' + i_idx" v-model="formData['ringMass_' + i_idx]"></td>
                <td rowspan="2"><input type="text" :name="'ringWetMass_' + i_idx" v-model="formData['ringWetMass_' + i_idx]"></td>
                <td rowspan="2"><input type="text" :name="'ringVolume_' + i_idx" v-model="formData['ringVolume_' + i_idx]"></td>
                
                <!-- Moisture Box 1 -->
                <td><input type="text" :name="'boxNo1_' + i_idx" v-model="formData['boxNo1_' + i_idx]"></td>
                <td><input type="text" :name="'boxMass1_' + i_idx" v-model="formData['boxMass1_' + i_idx]"></td>
                <td><input type="text" :name="'boxWetMass1_' + i_idx" v-model="formData['boxWetMass1_' + i_idx]"></td>
                <td><input type="text" :name="'boxDryMass1_' + i_idx" v-model="formData['boxDryMass1_' + i_idx]"></td>
                <td><input type="text" :name="'moisture1_' + i_idx" v-model="formData['moisture1_' + i_idx]"></td>

                <td rowspan="2"><input type="text" :name="'avgMoisture_' + i_idx" v-model="formData['avgMoisture_' + i_idx]"></td>
                <td rowspan="2"><input type="text" :name="'wetDensity_' + i_idx" v-model="formData['wetDensity_' + i_idx]"></td>
                <td rowspan="2"><input type="text" :name="'dryDensity_' + i_idx" v-model="formData['dryDensity_' + i_idx]"></td>
                <td rowspan="2"><input type="text" :name="'avgDryDensity_' + i_idx" v-model="formData['avgDryDensity_' + i_idx]"></td>
                <td rowspan="2"><input type="text" :name="'compaction_' + i_idx" v-model="formData['compaction_' + i_idx]"></td>
            </tr>
            <tr>
                <!-- Moisture Box 2 -->
                <td><input type="text" :name="'boxNo2_' + i_idx" v-model="formData['boxNo2_' + i_idx]"></td>
                <td><input type="text" :name="'boxMass2_' + i_idx" v-model="formData['boxMass2_' + i_idx]"></td>
                <td><input type="text" :name="'boxWetMass2_' + i_idx" v-model="formData['boxWetMass2_' + i_idx]"></td>
                <td><input type="text" :name="'boxDryMass2_' + i_idx" v-model="formData['boxDryMass2_' + i_idx]"></td>
                <td><input type="text" :name="'moisture2_' + i_idx" v-model="formData['moisture2_' + i_idx]"></td>
            </tr>
            </template>
            <!-- Empty rows to fill space if needed, or just these 5 sample blocks (10 rows) -->
        </tbody>
        <tfoot>
            <tr>
                <td colspan="2" class="label">备注</td>
                <td colspan="15" class="left-align" style="height: 60px; vertical-align: top;">
                    <textarea v-model="formData.remarks"  name="remarks" style="width: 100%; height: 100%;"></textarea>
                </td>
            </tr>
        </tfoot>
    </table>

    <div class="footer-info">
        <span>审核：<input type="text" style="width: 100px; border-bottom: 1px solid black;"></span>
        <span>试验：<input type="text" style="width: 100px; border-bottom: 1px solid black;"></span>
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
  constructionLocation: '',
  maxDryDensity: '',
  optMoisture: '',
  testType: '',
  standard: '',
  designCompaction: '',
  testDate: '',
  remarks: '',
})

onMounted(() => {

  // Initialize dynamic fields for loop variable 'i_idx'
  // Please verify the loop count match the template
  for (let i_idx = 0; i_idx < 50; i_idx++) {
    formData['location_' + i_idx] = ''
    formData['boxMass2_' + i_idx] = ''
    formData['boxWetMass2_' + i_idx] = ''
    formData['ringWetMass_' + i_idx] = ''
    formData['ringMass_' + i_idx] = ''
    formData['boxDryMass2_' + i_idx] = ''
    formData['moisture1_' + i_idx] = ''
    formData['ringNo_' + i_idx] = ''
    formData['compaction_' + i_idx] = ''
    formData['boxWetMass1_' + i_idx] = ''
    formData['boxNo2_' + i_idx] = ''
    formData['boxDryMass1_' + i_idx] = ''
    formData['sampleNo_' + i_idx] = ''
    formData['wetDensity_' + i_idx] = ''
    formData['boxNo1_' + i_idx] = ''
    formData['dryDensity_' + i_idx] = ''
    formData['avgDryDensity_' + i_idx] = ''
    formData['ringVolume_' + i_idx] = ''
    formData['avgMoisture_' + i_idx] = ''
    formData['boxMass1_' + i_idx] = ''
    formData['moisture2_' + i_idx] = ''
    formData['status_' + i_idx] = ''
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
    pdfForm.value.action = '/api/pdf/cutting_ring_record/generate'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}

const previewPdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/cutting_ring_record/preview'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}

// 上一页
const prevForm = () => {
  navigateBetweenForms(-1)
}

// 下一页
const nextForm = () => {
  navigateBetweenForms(1)
}

// 表单导航
const navigateBetweenForms = (direction) => {
  try {
    // 获取当前流程
    const directoryStr = localStorage.getItem('currentDirectory')
    if (!directoryStr) {
      alert('未找到流程信息')
      return
    }

    const directory = JSON.parse(directoryStr)
    
    // 构建表单序列
    const formSequence = []
    for (let i = 1; i <= 10; i++) {
      const type = directory[`table${i}Type`]
      const id = directory[`table${i}Id`]
      if (type) {
        formSequence.push({ type, id, tableIndex: i })
      }
    }

    if (formSequence.length === 0) {
      alert('该流程未关联任何表单')
      return
    }

    // 动态获取当前表单类型
    const currentFormType = localStorage.getItem('currentFormType') || 'CUTTING_RING_RECORD'
    
    // 找到当前表单在序列中的位置
    let currentIndex = -1
    for (let i = 0; i < formSequence.length; i++) {
      if (formSequence[i].type === currentFormType) {
        currentIndex = i
        break
      }
    }

    // 如果没找到，默认从第一个开始
    if (currentIndex === -1) {
      currentIndex = 0
    }

    // 计算目标索引
    const targetIndex = currentIndex + direction
    if (targetIndex < 0 || targetIndex >= formSequence.length) {
      alert(direction === -1 ? '已经是第一个表单' : '已经是最后一个表单')
      return
    }

    // 跳转到目标表单
    const targetForm = formSequence[targetIndex]
    const componentMap = {
      'ENTRUSTMENT_LIST': 'Entrustment',
      'REBOUND_METHOD_RECORD': 'ReboundMethodRecord',
      'LIGHT_DYNAMIC_PENETRATION_RECORD': 'LightDynamicPenetrationRecord',
      'NUCLEAR_DENSITY_RECORD': 'NuclearDensityRecord',
      'SAND_REPLACEMENT_RECORD': 'SandReplacementRecord',
      'WATER_REPLACEMENT_RECORD': 'WaterReplacementRecord',
      'CUTTING_RING_RECORD': 'CuttingRingRecord',
      'BECKMAN_BEAM_RECORD': 'BeckmanBeamRecord',
      'SIGNATURE': 'Signature',
      'DENSITY_TEST_REPORT': 'DensityTestReport',
      'DENSITY_TEST_RESULT': 'DensityTestResult',
      'LIGHT_DYNAMIC_PENETRATION': 'LightDynamicPenetration',
      'LIGHT_DYNAMIC_PENETRATION_RESULT': 'LightDynamicPenetrationResult',
      'REBOUND_METHOD_REPORT': 'ReboundMethodReport',
      'BECKMAN_BEAM_REPORT': 'BeckmanBeamReport',
      'BECKMAN_BEAM_RESULT': 'BeckmanBeamResult'
    }
    
    const componentName = componentMap[targetForm.type]
    if (componentName && navigateTo) {
      // 保存当前表单的状态
      localStorage.setItem('currentFormType', targetForm.type)
      localStorage.setItem('currentFormIndex', targetIndex.toString())
      
      // 构建参数，传递表单的ID
      const props = {}
      if (targetForm.id) {
        props.id = targetForm.id
      }
      
      // 使用navigateTo方法导航到对应的组件
      navigateTo(componentName, props)
    } else {
      alert('暂不支持该类型的页面跳转')
    }
  } catch (error) {
    console.error('导航错误:', error)
    alert('导航失败，请稍后重试')
  }
}
</script>

<style scoped>

        .cuttingRingRecord-container {
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
        }
        td {
            border: 1px solid black;
            padding: 5px;
            vertical-align: middle;
            text-align: center;
            font-size: 14px;
        }
        .label {
            font-weight: bold;
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
