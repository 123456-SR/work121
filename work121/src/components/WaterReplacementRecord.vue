<template>
  <div class="waterReplacementRecord-container">


    <div class="no-print" style="margin-bottom: 20px;">
        <button @click="goToHome" style="text-decoration: none; color: blue; background: none; border: none; cursor: pointer; padding: 0;">&lt; 返回主页</button>
        <button @click="prevForm" style="float: left; margin-left: 10px; background-color: #6c757d; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;">上一页</button>
        <button @click="nextForm" style="float: left; margin-left: 10px; background-color: #6c757d; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;">下一页</button>
        <button @click="printDocument" style="float: right; margin-left: 10px;">打印此单</button>
        <button @click="generatePdf" style="float: right; margin-left: 10px;">下载PDF</button>
        <button @click="previewPdf" style="float: right; margin-left: 10px;">预览PDF</button>
    </div>

    <form id="pdfForm" ref="pdfForm" method="post">
    <h2>相对密度试验记录表（灌水法）</h2>

    <div class="header-info">
        <span>工程部位：<input type="text" v-model="formData.projectName"   name="projectName" style="width: 200px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>试验日期：<input type="text" v-model="formData.testDate"   name="testDate" style="width: 150px; border-bottom: 1px solid black;"></span>
    </div>
    <div class="header-info">
        <span>依据标准：<input type="text" v-model="formData.standard"   name="standard" style="width: 150px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>最大干密度：<input type="text" v-model="formData.maxDryDensity"   name="maxDryDensity" style="width: 80px; border-bottom: 1px solid black;"> g/cm³</span>
        <span>最小干密度：<input type="text" v-model="formData.minDryDensity"   name="minDryDensity" style="width: 80px; border-bottom: 1px solid black;"> g/cm³</span>
        <span>最优含水率：<input type="text" v-model="formData.optMoisture"   name="optMoisture" style="width: 80px; border-bottom: 1px solid black;"> %</span>
        <span>相对密度：<input type="text" v-model="formData.relativeDensity"   name="relativeDensity" style="width: 80px; border-bottom: 1px solid black;"></span>
        <span>统一编号：<input type="text" v-model="formData.unifiedNumber"   name="unifiedNumber" style="width: 150px; border-bottom: 1px solid black;"></span>
    </div>
    <div class="header-info">
        <span>水的密度：<input type="text" v-model="formData.waterDensity"   name="waterDensity" style="width: 80px; border-bottom: 1px solid black;"> g/cm³</span>
        <span>仪器设备：<input type="text" v-model="formData.equipment"   name="equipment" style="width: 150px; border-bottom: 1px solid black;"></span>
        <span>设计压实度：<input type="text" v-model="formData.designCompaction"   name="designCompaction" style="width: 80px; border-bottom: 1px solid black;"></span>
        <span>检测类别：<input type="text" v-model="formData.testCategory"   name="testCategory" style="width: 100px; border-bottom: 1px solid black;"></span>
    </div>

    <table>
        <!-- Table Header -->
        <tr>
            <td colspan="4" class="label" style="width: 20%;">取样位置</td>
            <template v-for="(n, i_idx) in 4" :key="i_idx">
            <td colspan="4"><input type="text" :name="'samplingLocation_' + i_idx" v-model="formData['samplingLocation_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td colspan="3" class="label">套环体积</td>
            <td class="label">cm³</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'ringVolume_' + i_idx" v-model="formData['ringVolume_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td rowspan="2" colspan="2" class="label">储水桶<br>水位</td>
            <td class="label">初始</td>
            <td class="label">cm</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'initialWaterLevel_' + i_idx" v-model="formData['initialWaterLevel_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td class="label">终了</td>
            <td class="label">cm</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'finalWaterLevel_' + i_idx" v-model="formData['finalWaterLevel_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td colspan="3" class="label">储水桶面积</td>
            <td class="label">cm²</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'tankArea_' + i_idx" v-model="formData['tankArea_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td colspan="3" class="label">试坑体积</td>
            <td class="label">cm³</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'pitVolume_' + i_idx" v-model="formData['pitVolume_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td colspan="3" class="label">试样质量</td>
            <td class="label">g</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'sampleMass_' + i_idx" v-model="formData['sampleMass_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td colspan="3" class="label">试样湿密度</td>
            <td class="label">g/cm³</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'wetDensity_' + i_idx" v-model="formData['wetDensity_' + i_idx]"></td>
            </template>
        </tr>

        <!-- Moisture Content Section -->
        <tr>
            <td rowspan="6" colspan="2" class="label" style="width: 5%;">试样<br>含水率</td>
            <td class="label">编号</td>
            <td class="label">/</td>
            <template v-for="(n, i_idx) in 16" :key="i_idx">
            <td><input type="text" :name="'moistureNo_' + i_idx" v-model="formData['moistureNo_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td class="label">容器质量</td>
            <td class="label">g</td>
            <template v-for="(n, i_idx) in 16" :key="i_idx">
            <td><input type="text" :name="'containerMass_' + i_idx" v-model="formData['containerMass_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td class="label">容器+湿样质量</td>
            <td class="label">g</td>
            <template v-for="(n, i_idx) in 16" :key="i_idx">
            <td><input type="text" :name="'wetSampleMass_' + i_idx" v-model="formData['wetSampleMass_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td class="label">容器+干样质量</td>
            <td class="label">g</td>
            <template v-for="(n, i_idx) in 16" :key="i_idx">
            <td><input type="text" :name="'drySampleMass_' + i_idx" v-model="formData['drySampleMass_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td class="label">含水率</td>
            <td class="label">%</td>
            <template v-for="(n, i_idx) in 16" :key="i_idx">
            <td><input type="text" :name="'moistureContent_' + i_idx" v-model="formData['moistureContent_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td class="label">平均含水率</td>
            <td class="label">%</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'avgMoisture_' + i_idx" v-model="formData['avgMoisture_' + i_idx]"></td>
            </template>
        </tr>

        <!-- Bottom Section -->
        <tr>
            <td colspan="3" class="label">实测干密度</td>
            <td class="label">g/cm³</td>
            <template v-for="(n, i_idx) in 8" :key="i_idx">
            <td colspan="2"><input type="text" :name="'measuredDryDensity_' + i_idx" v-model="formData['measuredDryDensity_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td colspan="3" class="label">平均实测干密度</td>
            <td class="label">g/cm³</td>
            <template v-for="(n, i_idx) in 4" :key="i_idx">
            <td colspan="4"><input type="text" :name="'avgMeasuredDryDensity_' + i_idx" v-model="formData['avgMeasuredDryDensity_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td colspan="3" class="label">相对密度</td>
            <td class="label"></td>
            <template v-for="(n, i_idx) in 4" :key="i_idx">
            <td colspan="4"><input type="text" :name="'relativeDensity_' + i_idx" v-model="formData['relativeDensity_' + i_idx]"></td>
            </template>
        </tr>
        <tr>
            <td colspan="4" class="label">备注</td>
            <td colspan="16" class="left-align" style="height: 60px; vertical-align: top;">
                <textarea v-model="formData.remarks"  name="remarks" style="width: 100%; height: 100%;"></textarea>
            </td>
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
import { reactive, ref, onMounted, inject } from 'vue'

// 注入导航方法
const navigateTo = inject('navigateTo')

const pdfForm = ref(null)

const formData = reactive({
  projectName: '',
  testDate: '',
  standard: '',
  maxDryDensity: '',
  minDryDensity: '',
  optMoisture: '',
  relativeDensity: '',
  unifiedNumber: '',
  waterDensity: '',
  equipment: '',
  designCompaction: '',
  testCategory: '',
  approver: '',
  reviewer: '',
  tester: '',
  remarks: '',
})

onMounted(() => {

  // Initialize dynamic fields for loop variable 'i_idx'
  // Please verify the loop count match the template
  for (let i_idx = 0; i_idx < 50; i_idx++) {
    formData['drySampleMass_' + i_idx] = ''
    formData['tankArea_' + i_idx] = ''
    formData['relativeDensity_' + i_idx] = ''
    formData['ringVolume_' + i_idx] = ''
    formData['pitVolume_' + i_idx] = ''
    formData['samplingLocation_' + i_idx] = ''
    formData['moistureContent_' + i_idx] = ''
    formData['avgMoisture_' + i_idx] = ''
    formData['wetDensity_' + i_idx] = ''
    formData['finalWaterLevel_' + i_idx] = ''
    formData['moistureNo_' + i_idx] = ''
    formData['initialWaterLevel_' + i_idx] = ''
    formData['measuredDryDensity_' + i_idx] = ''
    formData['avgMeasuredDryDensity_' + i_idx] = ''
    formData['containerMass_' + i_idx] = ''
    formData['wetSampleMass_' + i_idx] = ''
    formData['sampleMass_' + i_idx] = ''
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
    pdfForm.value.action = '/api/pdf/water_replacement_record/generate'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}

const previewPdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/water_replacement_record/preview'
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
    const currentFormType = localStorage.getItem('currentFormType') || 'WATER_REPLACEMENT_RECORD'
    
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

        .waterReplacementRecord-container {
            font-family: 'SimSun', 'Songti SC', serif;
            width: 260mm; /* Using wider width similar to sand replacement record */
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
            .waterReplacementRecord-container {
                width: 100%;
                margin: 0;
                padding: 0;
            }
            .no-print {
                display: none;
            }
        }
    
</style>
