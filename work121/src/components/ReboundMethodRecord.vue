<template>
  <div class="reboundMethodRecord-container">


    <div class="no-print" style="margin-bottom: 20px;">
        <button @click="goToHome" style="text-decoration: none; color: blue; background: none; border: none; cursor: pointer; padding: 0;">&lt; 返回主页</button>
        <div style="float: right;">
            <button @click="prevForm" style="margin-left: 10px; background-color: #6c757d; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;">上一页</button>
            <button @click="nextForm" style="margin-left: 10px; background-color: #6c757d; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;">下一页</button>
            <button @click="printDocument" style="margin-left: 10px;">打印此单</button>
            <button @click="generatePdf" style="margin-left: 10px;">下载PDF</button>
            <button @click="previewPdf" style="margin-left: 10px;">预览PDF</button>
            <button @click="submitForm" style="margin-left: 10px; background-color: #ffc107; color: #212529; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;">提交</button>
        </div>
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
            <td><input type="text" :name="'reboundValue_' + (i_idx + 1) + '_' + (j_idx + 1)" v-model="formData['reboundValue_' + (i_idx + 1) + '_' + (j_idx + 1)]"></td>
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
import { reactive, ref, onMounted, inject, defineProps } from 'vue'
import axios from 'axios'

// 注入导航方法
const navigateTo = inject('navigateTo')

// 定义props
const props = defineProps({
  id: {
    type: String,
    default: null
  }
})

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
  // 检查是否从目录跳转而来
  const currentDirectory = localStorage.getItem('currentDirectory')
  if (currentDirectory) {
    try {
      const directory = JSON.parse(currentDirectory)
      // 保存目录信息
      localStorage.setItem('currentDirectory', JSON.stringify(directory))
      
      // 获取当前表单类型
      const currentFormType = localStorage.getItem('currentFormType') || 'REBOUND_METHOD_RECORD'
      
      // 查找当前表单在目录中的id
      let formId = null
      for (let i = 1; i <= 10; i++) {
        if (directory[`table${i}Type`] === currentFormType && directory[`table${i}Id`]) {
          formId = directory[`table${i}Id`]
          break
        }
      }
      
      // 如果找到对应的id，加载数据
      if (formId) {
        loadData(formId)
      } else if (props.id) {
        // 如果目录中没有对应的id，但props中有id，也加载数据
        loadData(props.id)
      }
    } catch (e) {
      console.error('Failed to parse directory:', e)
    }
  } else if (props.id) {
    // 如果没有目录信息，但有props.id，加载数据
    loadData(props.id)
  } else {
    // 检查URL参数中是否有ID
    const urlParams = new URLSearchParams(window.location.search)
    const id = urlParams.get('id')
    if (id) {
      loadData(id)
    }
  }

  // Initialize dynamic fields for rebound values (10 rows x 16 columns)
  for (let i = 1; i <= 10; i++) {
    for (let j = 1; j <= 16; j++) {
      formData['reboundValue_' + i + '_' + j] = ''
    }
    // Initialize other dynamic fields for each row
    formData['avgRebound_' + i] = ''
    formData['correctedStrength_' + i] = ''
    formData['carbonDepth_' + i] = ''
    formData['estimatedStrength_' + i] = ''
  }

})

// 加载数据
const loadData = async (id) => {
  try {
    // 获取当前表单类型
    const currentFormType = localStorage.getItem('currentFormType') || 'REBOUND_METHOD_RECORD'
    
    // 调用通用的表单数据接口
    const response = await axios.get(`/api/form-data/get-by-type-and-id?formType=${currentFormType}&id=${id}`)
    if (response.data.success && response.data.data) {
      const data = response.data.data
      
      // 根据表单类型处理数据
      if (currentFormType === 'REBOUND_METHOD_RECORD') {
        // 处理回弹法检测记录数据
        formData.unifiedNumber = data.id || ''
        formData.entrustingUnit = data.entrustingUnit || ''
        formData.projectName = data.projectName || ''
        formData.structurePart = data.structurePart || ''
        formData.commissionDate = data.commissionDate || ''
        formData.testCategory = data.testCategory || ''
        formData.sampleStatus = data.sampleStatus || ''
        formData.testAngle = data.testAngle || ''
        formData.designIndex = data.designIndex || ''
        formData.aggregateSize = data.aggregateSize || ''
        formData.avgStrength = data.avgStrength || ''
        formData.stdDev = data.stdDev || ''
        formData.coefVariation = data.coefVariation || ''
        formData.compEstimatedStrength = data.compEstimatedStrength || ''
        formData.equipment = data.equipment || ''
        formData.avgCarbonation = data.avgCarbonation || ''
        formData.standard = data.standard || ''
        formData.calibrationBefore = data.calibrationBefore || ''
        formData.calibrationAfter = data.calibrationAfter || ''
        formData.conclusion = data.conclusion || ''
        formData.remarks = data.remarks || ''
        
        // 获取用户信息
        let userInfo = null
        const userInfoStr = localStorage.getItem('userInfo')
        if (userInfoStr) {
          userInfo = JSON.parse(userInfoStr)
        }
        
        formData.approver = data.approver || userInfo?.userName || ''
        formData.reviewer = data.reviewer || userInfo?.userName || ''
        formData.tester = data.tester || userInfo?.userName || ''
      } else {
        // 对于其他表单类型，可能需要根据实际情况处理数据
        console.log('Loading data for form type:', currentFormType, 'with data:', data)
      }
    }
  } catch (error) {
    console.error('Failed to load data:', error)
  }
}

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

const submitForm = async () => {
  try {
    // 获取当前登录用户信息
    const userInfoStr = localStorage.getItem('userInfo')
    let userInfo = null
    if (userInfoStr) {
      userInfo = JSON.parse(userInfoStr)
    }

    // 构建提交数据
    const submitData = {
      id: formData.unifiedNumber || '',
      entrustingUnit: formData.entrustingUnit || '',
      projectName: formData.projectName || '',
      structurePart: formData.structurePart || '',
      testCategory: formData.testCategory || '',
      sampleStatus: formData.sampleStatus || '',
      testAngle: formData.testAngle || '',
      designIndex: formData.designIndex || '',
      aggregateSize: formData.aggregateSize || '',
      avgStrength: formData.avgStrength || '',
      stdDev: formData.stdDev || '',
      coefVariation: formData.coefVariation || '',
      compEstimatedStrength: formData.compEstimatedStrength || '',
      equipment: formData.equipment || '',
      avgCarbonation: formData.avgCarbonation || '',
      standard: formData.standard || '',
      calibrationBefore: formData.calibrationBefore || '',
      calibrationAfter: formData.calibrationAfter || '',
      conclusion: formData.conclusion || '',
      remarks: formData.remarks || '',
      approver: formData.approver || userInfo?.userName || '',
      reviewer: formData.reviewer || userInfo?.userName || '',
      tester: formData.tester || userInfo?.userName || ''
    };

    // 发送请求
    const response = await axios.post('/api/reboundMethod/save', submitData);

    if (response.data.success) {
      alert('提交成功');
    } else {
      alert('提交失败: ' + response.data.message);
    }
  } catch (error) {
    console.error('提交错误:', error);
    alert('提交失败，请稍后重试');
  }
}

const prevForm = () => {
  navigateBetweenForms(-1)
}

const nextForm = () => {
  navigateBetweenForms(1)
}

const navigateBetweenForms = (direction) => {
  try {
    // 获取当前目录
    const directoryStr = localStorage.getItem('currentDirectory')
    if (!directoryStr) {
      alert('未找到流程信息');
      return;
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
      alert('该流程未关联任何表单');
      return;
    }

    // 动态获取当前表单类型
    const currentFormType = localStorage.getItem('currentFormType') || 'REBOUND_METHOD_RECORD'
    
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
      alert(direction === -1 ? '已经是第一个表单' : '已经是最后一个表单');
      return;
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
      alert('暂不支持该类型的页面跳转');
    }
  } catch (error) {
    console.error('导航错误:', error);
    alert('导航失败，请稍后重试');
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
