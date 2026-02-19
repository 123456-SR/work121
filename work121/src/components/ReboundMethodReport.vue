<template>
  <div class="reboundMethodReport-container">


    <div class="no-print" style="margin-bottom: 20px;">
        <button @click="goToHome" style="text-decoration: none; color: blue; background: none; border: none; cursor: pointer; padding: 0;">&lt; 返回主页</button>
        <button @click="prevForm" style="float: left; margin-left: 10px; background-color: #6c757d; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;">上一页</button>
        <button @click="nextForm" style="float: left; margin-left: 10px; background-color: #6c757d; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;">下一页</button>
        <button @click="printDocument" style="float: right; margin-left: 10px;">打印此单</button>
        <button @click="generatePdf" style="float: right; margin-left: 10px;">下载PDF</button>
        <button @click="previewPdf" style="float: right; margin-left: 10px;">预览PDF</button>
    </div>

    <h2>回弹法检测混凝土抗压强度报告</h2>

    <form id="pdfForm" ref="pdfForm" method="post">
    <div class="header-top">
        <span>委托单位：<input type="text" v-model="formData.entrustingUnit"   name="entrustingUnit" style="width: 200px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>统一编号：<input type="text" v-model="formData.unifiedNumber"   name="unifiedNumber" style="width: 150px; border-bottom: 1px solid black;"></span>
    </div>
    <div class="header-top">
        <span>样品编号：<input type="text" v-model="formData.sampleNumber"   name="sampleNumber" style="width: 200px; border-bottom: 1px solid black; text-align: left;"></span>
    </div>

    <!-- Table with 12-column grid -->
    <table>
        <colgroup>
            <template v-for="(n, i_idx) in 12" :key="i_idx">
            <col style="width: 8.33%;">
            </template>
        </colgroup>

        <!-- Row 1 -->
        <tr>
            <td colspan="2" class="label">工程名称</td>
            <td colspan="6"><input type="text" v-model="formData.projectName"   name="projectName"></td>
            <td colspan="2" class="label">委托日期</td>
            <td colspan="2"><input type="text" v-model="formData.commissionDate"   name="commissionDate"></td>
        </tr>

        <!-- Row 2 -->
        <tr>
            <td colspan="2" class="label">结构部位</td>
            <td colspan="6"><input type="text" v-model="formData.structurePart"   name="structurePart"></td>
            <td colspan="2" class="label">检测日期</td>
            <td colspan="2"><input type="text" v-model="formData.testDate"   name="testDate"></td>
        </tr>

        <!-- Row 3 -->
        <tr>
            <td colspan="2" class="label">样品状态</td>
            <td colspan="2"><input type="text" v-model="formData.sampleStatus"   name="sampleStatus"></td>
            <td colspan="2" class="label">测试角度</td>
            <td colspan="2"><input type="text" v-model="formData.testAngle"   name="testAngle"></td>
            <td colspan="2" class="label">报告日期</td>
            <td colspan="2"><input type="text" v-model="formData.reportDate"   name="reportDate"></td>
        </tr>

        <!-- Row 4 -->
        <tr>
            <td colspan="2" class="label">见证单位</td>
            <td colspan="6"><input type="text" v-model="formData.witnessUnit"   name="witnessUnit"></td>
            <td colspan="2" class="label">见证人</td>
            <td colspan="2"><input type="text" v-model="formData.witnessPerson"   name="witnessPerson"></td>
        </tr>

        <!-- Row 5 -->
        <tr>
            <td colspan="2" class="label">设计指标</td>
            <td colspan="2"><input type="text" v-model="formData.designIndex"   name="designIndex"></td>
            <td colspan="2" class="label">检测类别</td>
            <td colspan="2"><input type="text" v-model="formData.testCategory"   name="testCategory"></td>
            <td colspan="2" class="label">浇筑日期</td>
            <td colspan="2"><input type="text" v-model="formData.pourDate"   name="pourDate"></td>
        </tr>

        <!-- Row 6 -->
        <tr>
            <td colspan="2" class="label">碳化深度 mm</td>
            <td colspan="2"><input type="text" v-model="formData.carbonDepth"   name="carbonDepth"></td>
            <td colspan="4" class="label">构件厚度或骨料最大粒径</td>
            <td colspan="4"><input type="text" v-model="formData.aggregateSize"   name="aggregateSize"></td>
        </tr>

        <!-- Row 7: Zone No (10 zones) -->
        <tr>
            <td colspan="2" class="label">测区号</td>
            <template v-for="(n, i_idx) in 10" :key="i_idx">
            <td colspan="1">{{ (i_idx + 1) }}</td>
            </template>
        </tr>

        <!-- Row 8: Carbonation Corrected Strength -->
        <tr>
            <td colspan="2" class="label">碳化修正强度<br>MPa</td>
            <template v-for="(n, i_idx) in 10" :key="i_idx">
            <td colspan="1"><input type="text" :name="'correctedStrength_' + (i_idx + 1)" v-model="formData['correctedStrength_' + (i_idx + 1)]"></td>
            </template>
        </tr>

        <!-- Row 9: Strength Estimation -->
        <tr>
            <td colspan="2" class="label">构件强度推定<br>值 MPa</td>
            <td colspan="2"><input type="text" v-model="formData.compEstimatedStrength"   name="compEstimatedStrength"></td>
            <td colspan="2" class="label">标准差 MPa</td>
            <td colspan="2"><input type="text" v-model="formData.stdDev"   name="stdDev"></td>
            <td colspan="2" class="label">变异系数%</td>
            <td colspan="2"><input type="text" v-model="formData.coefVariation"   name="coefVariation"></td>
        </tr>

        <!-- Row 10: Equipment -->
        <tr>
            <td colspan="2" class="label">仪器设备</td>
            <td colspan="10" class="left-align"><input type="text" v-model="formData.equipment"   name="equipment" style="text-align: left;"></td>
        </tr>

        <!-- Row 11: Standard -->
        <tr>
            <td colspan="2" class="label">依据标准</td>
            <td colspan="10" class="left-align"><input type="text" v-model="formData.standard"   name="standard" style="text-align: left;"></td>
        </tr>

        <!-- Row 12: Conclusion -->
        <tr>
            <td colspan="2" class="label">结论</td>
            <td colspan="10" class="left-align" style="height: 60px;">
                <textarea v-model="formData.conclusion"  name="conclusion" style="width: 100%; height: 100%;"></textarea>
            </td>
        </tr>

        <!-- Row 13: Diagram -->
        <tr>
            <td colspan="12" class="left-align" style="height: 150px; vertical-align: top;">
                <span class="label">测区示意图：</span>
                <!-- Placeholder for diagram -->
            </td>
        </tr>

        <!-- Row 14: Remarks -->
        <tr>
            <td colspan="1" class="label">备注：</td>
            <td colspan="11" class="left-align" style="height: 40px;">
                <textarea v-model="formData.remarks"  name="remarks" style="width: 100%; height: 100%;"></textarea>
            </td>
        </tr>
    </table>

    <div class="footer-info">
        <span>批准：<input type="text" v-model="formData.approver"   name="approver" style="width: 100px; border-bottom: 1px solid black;"></span>
        <span>审核：<input type="text" v-model="formData.reviewer"   name="reviewer" style="width: 100px; border-bottom: 1px solid black;"></span>
        <span>检验：<input type="text" v-model="formData.tester"   name="tester" style="width: 100px; border-bottom: 1px solid black;"></span>
    </div>

    <div class="disclaimer">
        声明：<br>
        1. 对本检验报告的复印件未加盖公司检验检测专用章无效。 2. 对检验结果如有异议，应在收到报告之日起十五日之内向本公司提出。
    </div>

    <div class="company-info">
        <div>公司名称：<input type="text" v-model="formData.companyName"   name="companyName" style="width: 70%; border-bottom: 1px solid black; text-align: left;" value="河北金涛建设工程质量检测有限公司"></div>
        <div style="display: flex; justify-content: space-between; margin-top: 5px;">
            <span>公司地址：<input type="text" v-model="formData.companyAddress"   name="companyAddress" style="width: 300px; border-bottom: 1px solid black; text-align: left;" value="石家庄高新区方亿科技工业园A区第2号楼。"></span>
            <span>电话：<input type="text" v-model="formData.companyPhone"   name="companyPhone" style="width: 200px; border-bottom: 1px solid black;" value="0311—86107634  0311—67300616"></span>
        </div>
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
  sampleNumber: '',
  projectName: '',
  commissionDate: '',
  structurePart: '',
  testDate: '',
  sampleStatus: '',
  testAngle: '',
  reportDate: '',
  witnessUnit: '',
  witnessPerson: '',
  designIndex: '',
  testCategory: '',
  pourDate: '',
  carbonDepth: '',
  aggregateSize: '',
  compEstimatedStrength: '',
  stdDev: '',
  coefVariation: '',
  equipment: '',
  standard: '',
  approver: '',
  reviewer: '',
  tester: '',
  companyName: '',
  companyAddress: '',
  companyPhone: '',
  conclusion: '',
  remarks: '',
})

onMounted(() => {

  // Initialize dynamic fields for loop variable 'i_idx'
  // Please verify the loop count match the template
  for (let i_idx = 0; i_idx < 50; i_idx++) {
    formData['correctedStrength_' + i_idx] = ''
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
    pdfForm.value.action = '/api/pdf/rebound_method_report/generate'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}

const previewPdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/rebound_method_report/preview'
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
    const currentFormType = localStorage.getItem('currentFormType') || 'REBOUND_METHOD_REPORT'
    
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

        .reboundMethodReport-container {
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
            white-space: nowrap;
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
        .disclaimer {
            margin-top: 20px;
            font-size: 12px;
            line-height: 1.5;
        }
        .company-info {
            margin-top: 10px;
            font-size: 14px;
        }
        @media print {
            .reboundMethodReport-container {
                width: 100%;
                margin: 0;
                padding: 0;
            }
            .no-print {
                display: none;
            }
        }
    
</style>
