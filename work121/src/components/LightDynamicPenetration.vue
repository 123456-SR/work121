<template>
  <div class="lightDynamicPenetration-container">


    <div class="no-print" style="margin-bottom: 20px;">
        <button @click="goToHome" style="text-decoration: none; color: blue; background: none; border: none; cursor: pointer; padding: 0;">&lt; 返回主页</button>
        <button @click="prevForm" style="float: left; margin-left: 10px; background-color: #6c757d; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;">上一页</button>
        <button @click="nextForm" style="float: left; margin-left: 10px; background-color: #6c757d; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;">下一页</button>
        <button @click="printDocument" style="float: right; margin-left: 10px;">打印此单</button>
        <button @click="generatePdf" style="float: right; margin-left: 10px; background-color: #28a745; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;">下载PDF</button>
        <button @click="previewPdf" style="float: right; margin-left: 10px; background-color: #17a2b8; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;">预览PDF</button>
        <button @click="submitForm" style="float: right; margin-left: 10px; background-color: #ffc107; color: #212529; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;">提交</button>
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
            formData.entrustDate = formatDate(data.commissionDate)
            formData.constructionPart = data.constructionPart || ''
            formData.soilProperty = data.soilProperty || ''
            formData.reportDate = formatDate(data.reportDate)
            formData.witnessUnit = data.witnessUnit || ''
            formData.witness = data.witness || ''
            formData.designCapacity = data.designCapacity || ''
            formData.hammerWeight = data.hammerWeight || ''
            formData.dropDistance = data.dropDistance || ''
            formData.testCategory = data.testCategory || ''
            formData.testBasis = data.testBasis || ''
            formData.equipment = data.equipment || ''
            formData.remarks = data.remarks || ''
            formData.approve = data.approver || ''
            formData.review = data.reviewer || ''
            formData.inspect = data.tester || ''
            formData.conclusion = data.conclusion || ''
            formData.companyName = '河北金涛建设工程质量检测有限公司' // Default
            formData.companyAddress = '石家庄高新区方亿科技工业园A区第2号楼。' // Default
            formData.companyPhone = '0311—86107634  0311—67300616' // Default

            if (data.dataJson) {
                try {
                    const json = JSON.parse(data.dataJson)
                    if (json.testDate) formData.testDate = json.testDate
                    
                    // Map flat keys to dataBlocks
                    for (let b = 0; b < 4; b++) {
                        formData.dataBlocks[b].pos_L = json[`pos_L_${b}`] || ''
                        formData.dataBlocks[b].avg_L = json[`avg_L_${b}`] || ''
                        formData.dataBlocks[b].capacity_L = json[`capacity_L_${b}`] || ''
                        
                        formData.dataBlocks[b].pos_R = json[`pos_R_${b}`] || ''
                        formData.dataBlocks[b].avg_R = json[`avg_R_${b}`] || ''
                        formData.dataBlocks[b].capacity_R = json[`capacity_R_${b}`] || ''

                        for (let s = 0; s < 2; s++) {
                            const idx = b * 2 + s
                            formData.dataBlocks[b].depths[s].depth_L = json[`depth_L_${idx}`] || ''
                            formData.dataBlocks[b].depths[s].actual_L = json[`actual_L_${idx}`] || ''
                            
                            formData.dataBlocks[b].depths_R[s].depth_R = json[`depth_R_${idx}`] || ''
                            formData.dataBlocks[b].depths_R[s].actual_R = json[`actual_R_${idx}`] || ''
                        }
                    }

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
    const currentFormType = localStorage.getItem('currentFormType') || 'LIGHT_DYNAMIC_PENETRATION'
    
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

const submitForm = async () => {
  try {
    // 获取当前登录用户信息
    const userInfoStr = localStorage.getItem('userInfo');
    const userInfo = userInfoStr ? JSON.parse(userInfoStr) : {};
    
    // Convert dataBlocks to flat keys
    const dynamicData = {}
    if (formData.testDate) dynamicData.testDate = formData.testDate

    for (let b = 0; b < 4; b++) {
        const block = formData.dataBlocks[b]
        if (block.pos_L) dynamicData[`pos_L_${b}`] = block.pos_L
        if (block.avg_L) dynamicData[`avg_L_${b}`] = block.avg_L
        if (block.capacity_L) dynamicData[`capacity_L_${b}`] = block.capacity_L
        
        if (block.pos_R) dynamicData[`pos_R_${b}`] = block.pos_R
        if (block.avg_R) dynamicData[`avg_R_${b}`] = block.avg_R
        if (block.capacity_R) dynamicData[`capacity_R_${b}`] = block.capacity_R

        for (let s = 0; s < 2; s++) {
            const idx = b * 2 + s
            if (block.depths[s].depth_L) dynamicData[`depth_L_${idx}`] = block.depths[s].depth_L
            if (block.depths[s].actual_L) dynamicData[`actual_L_${idx}`] = block.depths[s].actual_L
            
            if (block.depths_R[s].depth_R) dynamicData[`depth_R_${idx}`] = block.depths_R[s].depth_R
            if (block.depths_R[s].actual_R) dynamicData[`actual_R_${idx}`] = block.depths_R[s].actual_R
        }
    }

    // 构建提交数据
    const submitData = {
      id: props.id, // Ensure ID is passed
      unifiedNumber: formData.unifiedNumber, // These might be ignored if using ID, but good to have
      entrustingUnit: formData.entrustingUnit,
      projectName: formData.projectName,
      // entrustDate: formData.entrustDate, // Read-only usually
      // constructionPart: formData.constructionPart, // Read-only
      
      // Editable fields
      soilProperty: formData.soilProperty,
      designCapacity: formData.designCapacity,
      hammerWeight: formData.hammerWeight,
      dropDistance: formData.dropDistance,
      testBasis: formData.testBasis,
      equipment: formData.equipment,
      remarks: formData.remarks,
      approver: formData.approve,
      reviewer: formData.review,
      tester: formData.inspect,
      conclusion: formData.conclusion,
      reportDate: formData.reportDate ? new Date(formData.reportDate) : null,
      
      dataJson: JSON.stringify(dynamicData)
    };
    
    // 发送请求
    const response = await axios.post('/api/light-dynamic-penetration/save', submitData);
    
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
