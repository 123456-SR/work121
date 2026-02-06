<template>
  <div class="entrustment-container">


    <div class="no-print" style="margin-bottom: 20px;">
        <button @click="goToHome" style="text-decoration: none; color: blue; background: none; border: none; cursor: pointer; padding: 0;">&lt; 返回主页</button>
        <button @click="prevForm" style="float: left; margin-left: 10px; background-color: #6c757d; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;">上一页</button>
        <button @click="nextForm" style="float: left; margin-left: 10px; background-color: #6c757d; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;">下一页</button>
        <button @click="printDocument" style="float: right; margin-left: 10px;">打印此单</button>
        <button @click="generatePdf" style="float: right; margin-left: 10px; background-color: #28a745; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;">下载PDF</button>
        <button @click="previewPdf" style="float: right; margin-left: 10px; background-color: #17a2b8; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;">预览PDF</button>
        <button @click="submitForm" style="float: right; margin-left: 10px; background-color: #ffc107; color: #212529; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;">提交</button>
    </div>

    <form id="entrustmentForm" ref="pdfForm" method="post">
    <h2>检测 (验) 委托单 (代合同)</h2>

    <div class="header-info">
        <span>统一编号：<input type="text" v-model="formData.unifiedNumber"   name="unifiedNumber" style="width: 150px; border-bottom: 1px solid black;">
        <button type="button" @click="queryEntrustment" class="query-btn" style="margin-left: 10px; background-color: #007bff; color: white; border: none; padding: 2px 8px; border-radius: 3px; cursor: pointer; font-size: 12px;">查询</button></span>
        <span>样品编号：<input type="text" v-model="formData.sampleNumber"   name="sampleNumber" style="width: 150px; border-bottom: 1px solid black;"></span>
    </div>

    <table>
        <!-- Row 1 -->
        <tr>
            <td class="label">委托单位：</td>
            <td><input type="text" v-model="formData.clientUnit"   name="clientUnit"></td>
            <td class="label">委托日期：</td>
            <td><input type="text" v-model="formData.clientDate"   name="clientDate"></td>
        </tr>
        <!-- Row 2 -->
        <tr>
            <td class="label">施工单位：</td>
            <td><input type="text" v-model="formData.constructionUnit"   name="constructionUnit"></td>
            <td class="label">建设单位：</td>
            <td><input type="text" v-model="formData.buildingUnit"   name="buildingUnit"></td>
        </tr>
        <!-- Row 3 -->
        <tr>
            <td class="label">工程名称：</td>
            <td colspan="2"><input type="text" v-model="formData.projectName"   name="projectName"></td>
            <td style="width: 150px;">
                <span class="label">施工 (使用) 部位：</span><br>
                <input type="text" v-model="formData.constructionPart"   name="constructionPart">
            </td>
        </tr>
        <!-- Row 4 -->
        <tr>
            <td colspan="4" style="padding: 0;">
                <table style="width: 100%; border: none; margin: -1px; border-collapse: collapse;">
                    <tr>
                        <td class="label" style="width: 15%; border: none; border-right: 1px solid black; padding: 5px;">样品名称：</td>
                        <td style="width: 18%; border: none; border-right: 1px solid black; padding: 5px;"><input type="text" v-model="formData.sampleName"   name="sampleName"></td>
                        <td class="label" style="width: 15%; border: none; border-right: 1px solid black; padding: 5px;">规格/型号：</td>
                        <td style="width: 18%; border: none; border-right: 1px solid black; padding: 5px;"><input type="text" v-model="formData.spec"   name="spec"></td>
                        <td class="label" style="width: 15%; border: none; border-right: 1px solid black; padding: 5px;">生产厂家<br>或产地</td>
                        <td style="width: 19%; border: none; padding: 5px;"><input type="text" v-model="formData.manufacturer"   name="manufacturer"></td>
                    </tr>
                </table>
            </td>
        </tr>
        <!-- Row 5 -->
        <tr>
            <td colspan="4" style="padding: 0;">
                <table style="width: 100%; border: none; margin: -1px;">
                    <tr>
                        <td style="border: none; border-right: 1px solid black; width: 25%;">
                            <div class="center-text label">样品<br>数量：</div>
                            <input type="text" v-model="formData.sampleQuantity"   name="sampleQuantity" class="center-text">
                        </td>
                        <td style="border: none; border-right: 1px solid black; width: 25%;">
                            <div class="center-text label">代表<br>批量：</div>
                            <input type="text" v-model="formData.representativeBatch"   name="representativeBatch" class="center-text">
                        </td>
                        <td style="border: none; border-right: 1px solid black; width: 25%;">
                            <div class="center-text label">批号：</div>
                            <input type="text" v-model="formData.batchNumber"   name="batchNumber" class="center-text">
                        </td>
                        <td style="border: none; width: 25%;">
                            <div class="center-text label">检测(验)<br>类别：</div>
                            <input type="text" v-model="formData.testCategory"   name="testCategory" class="center-text">
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <!-- Row 6: 样品历史及概况 & 样品状态 -->
        <tr>
            <td colspan="2" style="height: 100px; vertical-align: top;">
                <div class="label">样品历史及概况：</div>
                <textarea v-model="formData.sampleHistory"  name="sampleHistory" rows="4" style="width: 100%; height: 80px;"></textarea>
            </td>
            <td colspan="2" style="height: 100px; vertical-align: top;">
                <div class="label">样品状态：</div>
                <textarea v-model="formData.sampleStatus"  name="sampleStatus" rows="4" style="width: 100%; height: 80px;"></textarea>
            </td>
        </tr>
        <!-- Row 7: 检测项目及依据 -->
        <tr>
            <td colspan="4" style="height: 60px; vertical-align: top;">
                <div class="label">检测(验)项目及依据：</div>
                <textarea v-model="formData.testItems"  name="testItems" rows="2" style="width: 100%; height: 40px;"></textarea>
            </td>
        </tr>
        <!-- Row 8: 地址电话 -->
        <tr>
            <td class="label">委托单位地址<br>及电话(传真)：</td>
            <td colspan="3"><input type="text" v-model="formData.clientAddressPhone"   name="clientAddressPhone"></td>
        </tr>
        <!-- Row 9: 报告发送 & 样品处置 -->
        <tr>
            <td class="label">报告发送</td>
            <td>
                <div class="checkbox-group">
                    <label><input type="checkbox" v-model="formData.reportSend"   name="reportSend" value="1">1.自取</label>
                    <label><input type="checkbox" v-model="formData.reportSend"   name="reportSend" value="2">2.邮寄</label>
                    <label><input type="checkbox" v-model="formData.reportSend"   name="reportSend" value="3">3.其它</label>
                </div>
            </td>
            <td class="label">样品处置</td>
            <td>
                <div class="checkbox-group">
                    <label><input type="checkbox" v-model="formData.sampleDisposal"   name="sampleDisposal" value="1">1.取回</label>
                    <label><input type="checkbox" v-model="formData.sampleDisposal"   name="sampleDisposal" value="2">2.不取回</label>
                </div>
            </td>
        </tr>
        <!-- Row 10: 见证人 -->
        <tr>
            <td class="label">见证人</td>
            <td><input type="text" v-model="formData.witness"   name="witness"></td>
            <td class="label">见证单位</td>
            <td><input type="text" v-model="formData.witnessUnit"   name="witnessUnit"></td>
        </tr>
        <!-- Row 11: 交付日期 & 费用 -->
        <tr>
            <td class="label">检测报告<br>交付日期</td>
            <td>
                <div class="checkbox-group">
                    <div style="margin-bottom: 5px;">
                        <label><input type="radio" v-model="formData.deliveryMode"   name="deliveryMode" value="1">可以为:</label>
                        <input type="text" v-model="formData.deliveryDate1_y"   name="deliveryDate1_y" style="width: 40px; border-bottom: 1px solid black;">年
                        <input type="text" v-model="formData.deliveryDate1_m"   name="deliveryDate1_m" style="width: 25px; border-bottom: 1px solid black;">月
                        <input type="text" v-model="formData.deliveryDate1_d"   name="deliveryDate1_d" style="width: 25px; border-bottom: 1px solid black;">日
                    </div>
                    <div style="margin-bottom: 5px;">
                        <label><input type="radio" v-model="formData.deliveryMode"   name="deliveryMode" value="2">严格限定为:</label>
                        <input type="text" v-model="formData.deliveryDate2_y"   name="deliveryDate2_y" style="width: 40px; border-bottom: 1px solid black;">年
                        <input type="text" v-model="formData.deliveryDate2_m"   name="deliveryDate2_m" style="width: 25px; border-bottom: 1px solid black;">月
                        <input type="text" v-model="formData.deliveryDate2_d"   name="deliveryDate2_d" style="width: 25px; border-bottom: 1px solid black;">日
                    </div>
                    <div>
                        <label><input type="radio" v-model="formData.deliveryMode"   name="deliveryMode" value="3" checked>不要求</label>
                    </div>
                </div>
            </td>
            <td class="label">应缴检测(验)费(元)</td>
            <td><input type="text" v-model="formData.fee"   name="fee"></td>
        </tr>
        <!-- Row 12: 备注 -->
        <tr>
            <td class="label">备注</td>
            <td colspan="3"><input type="text" v-model="formData.remarks"   name="remarks"></td>
        </tr>
        <!-- Row 13: 说明 -->
        <tr>
            <td class="label" style="vertical-align: top;">说明：</td>
            <td colspan="3" class="notes">
                1、委托人(送样人)和见证人应对样品的代表性负责。<br>
                2、检测(验)单位对检测结果负责技术责任。<br>
                3、本委托单一式三份，委托方一份，检测方两份，自送样人收样人签字起生效。<br>
                4、检测(验)委托单由委托方技术人员或送样人填写，要求内容齐全清晰。<br>
                5、见证检验时，本委托单无见证人签字无效。<br>
                6、本单如有变动，双方应及时沟通，凭此委托单领取报告。<br>
                7、客户可在报告发出之日起30日内取回样品，不合格样品一律不予返还。
            </td>
        </tr>
    </table>

    <div class="footer-info">
        <span>委托(送样)人：<input type="text" style="width: 150px; border-bottom: 1px solid black;"></span>
        <span>承接(收样)人：<input type="text" style="width: 150px; border-bottom: 1px solid black;"></span>
    </div>

    </form>



    


  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, inject } from 'vue'
import axios from 'axios'

// 注入导航方法
const navigateTo = inject('navigateTo')

const props = defineProps({
  id: {
    type: String,
    default: null
  }
})

const pdfForm = ref(null)

const formData = reactive({
  unifiedNumber: '',
  sampleNumber: '',
  clientUnit: '',
  clientDate: '',
  constructionUnit: '',
  buildingUnit: '',
  projectName: '',
  constructionPart: '',
  sampleName: '',
  spec: '',
  manufacturer: '',
  sampleQuantity: '',
  representativeBatch: '',
  batchNumber: '',
  testCategory: '',
  clientAddressPhone: '',
  reportSend: [],
  sampleDisposal: [],
  witness: '',
  witnessUnit: '',
  deliveryMode: '',
  deliveryDate1_y: '',
  deliveryDate1_m: '',
  deliveryDate1_d: '',
  deliveryDate2_y: '',
  deliveryDate2_m: '',
  deliveryDate2_d: '',
  fee: '',
  remarks: '',
  sampleHistory: '',
  sampleStatus: '',
  testItems: '',
})

// 加载数据
const loadData = async (id) => {
  if (!id) return
  
  try {
    // 获取当前表单类型
    const currentFormType = localStorage.getItem('currentFormType') || 'ENTRUSTMENT_LIST'
    
    // 调用通用的表单数据接口
    const response = await axios.get(`/api/form-data/get-by-type-and-id?formType=${currentFormType}&id=${id}`)
    if (response.data.success && response.data.data) {
      const data = response.data.data
      
      // 根据表单类型处理数据
      if (currentFormType === 'ENTRUSTMENT_LIST') {
        // 映射字段
        formData.unifiedNumber = data.wtNum || data.unifiedNumber || ''
        formData.sampleNumber = data.wtNum || data.sampleNumber || '' // 根据实体类注释，样品编号可能与统一编号相同
        
        formData.clientUnit = data.clientUnit || ''
        formData.clientDate = data.commissionDate ? formatDate(data.commissionDate, 'YYYY-MM-DD') : ''
        
        formData.constructionUnit = data.constructionUnit || ''
        formData.buildingUnit = data.buildingUnit || ''
        
        formData.projectName = data.projectName || ''
        formData.constructionPart = data.projectArea || data.constructionPart || '' // 使用 projectArea 映射 constructionPart
        
        // 其他字段根据 JcCoreWtInfo 的字段进行映射
        // 由于 JcCoreWtInfo 字段有限，部分字段可能需要根据实际情况调整
        formData.witnessUnit = data.supervisionUnit || data.witnessUnit || '' // 监理单位映射为见证单位? 需要确认业务逻辑
        formData.witness = data.client || data.witness || '' // 委托人映射为见证人? 暂且如此，或者留空
        
        // 假设 JcCoreWtInfo 只有部分基础字段，其他字段可能需要从其他关联表获取或者为空
        // 这里尽量填充已有字段
      } else {
        // 对于其他表单类型，可能需要根据实际情况处理数据
        console.log('Loading data for form type:', currentFormType, 'with data:', data)
      }
      
    } else {
      console.error('Failed to load data:', response.data.message)
    }
  } catch (error) {
    console.error('Error loading data:', error)
  }
}

onMounted(() => {
  // 检查是否从目录跳转而来
  const currentDirectory = localStorage.getItem('currentDirectory')
  if (currentDirectory) {
    try {
      const directory = JSON.parse(currentDirectory)
      // 保存目录信息
      localStorage.setItem('currentDirectory', JSON.stringify(directory))
      
      // 获取当前表单类型
      const currentFormType = localStorage.getItem('currentFormType') || 'ENTRUSTMENT_LIST'
      
      // 查找当前表单在目录中的id
      let formId = null
      for (let i = 1; i <= 10; i++) {
        if (directory[`table${i}Type`] === currentFormType) {
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
    loadData(props.id)
  }
})

watch(() => props.id, (newId) => {
  if (newId) {
    loadData(newId)
  } else {
    // 重置表单
    Object.keys(formData).forEach(key => {
      if (Array.isArray(formData[key])) formData[key] = []
      else formData[key] = ''
    })
  }
})

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
  const currentDirectory = localStorage.getItem('currentDirectory')
  if (!currentDirectory) {
    alert('未找到流程信息')
    return
  }
  
  try {
    const directory = JSON.parse(currentDirectory)
    
    // 构建表单队列（包含type和id）
    const formQueue = []
    for (let i = 1; i <= 10; i++) {
      const type = directory[`table${i}Type`]
      const id = directory[`table${i}Id`]
      if (type) {
        formQueue.push({ type, id, index: i })
      }
    }
    
    if (formQueue.length === 0) {
      alert('流程中没有表单')
      return
    }
    
    // 动态获取当前表单类型
    const currentFormType = localStorage.getItem('currentFormType') || 'ENTRUSTMENT_LIST'
    
    // 找到当前表单在队列中的位置
    let currentIndex = -1
    for (let i = 0; i < formQueue.length; i++) {
      if (formQueue[i].type === currentFormType) {
        currentIndex = i
        break
      }
    }
    
    // 如果没找到，默认从第一个开始
    if (currentIndex === -1) {
      currentIndex = 0
    }
    
    // 计算目标表单的位置
    const targetIndex = currentIndex + direction
    if (targetIndex < 0 || targetIndex >= formQueue.length) {
      alert('已经是第' + (direction === -1 ? '一' : '最后') + '个表单了')
      return
    }
    
    // 跳转到目标表单
    const targetForm = formQueue[targetIndex]
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
  } catch (e) {
    console.error('Navigation error:', e)
    alert('导航失败，请重试')
  }
}

const printDocument = () => {
  window.print()
}

const generatePdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/generate'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}

const previewPdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/preview'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}

const queryEntrustment = () => {
  if (!formData.unifiedNumber) {
    alert('请输入统一编号');
    return;
  }

  axios.get('/api/jc-core-wt-info/by-wt-num', {
    params: {
      wtNum: formData.unifiedNumber
    }
  })
  .then(response => {
    if (response.data.success && response.data.data) {
      let data = response.data.data;
      
      formData.clientUnit = data.clientUnit || '';
      formData.clientDate = formatDate(data.commissionDate, 'YYYY-MM');
      formData.constructionUnit = data.constructionUnit || '';
      formData.buildingUnit = data.buildingUnit || '';
      formData.witnessUnit = data.witnessUnit || '';
      formData.witness = data.witness || '';
      formData.projectName = data.projectName || '';
      formData.constructionPart = data.projectArea || '';
      formData.clientAddressPhone = data.clientUnitAddress || '' + (data.clientTel || '');
      formData.projectName = data.projectName || '';
    } else {
      alert('未找到该统一编号的委托单');
    }
  })
  .catch(error => {
    console.error('查询委托单失败:', error);
    alert('查询失败，请稍后重试');
  });
}

const formatDate = (dateStr, format) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  if (isNaN(date.getTime())) return '';
  
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  
  return format.replace('YYYY', year).replace('MM', month).replace('DD', day);
}

// 返回主页（目录列表）
const goToHome = () => {
  if (navigateTo) {
    navigateTo('DirectoryList');
  }
}

const submitForm = async () => {
  try {
    // 获取当前登录用户信息
    const userInfoStr = localStorage.getItem('userInfo');
    const userInfo = userInfoStr ? JSON.parse(userInfoStr) : {};
    
    // 构建提交数据
    const submitData = {
      unifiedNumber: formData.unifiedNumber,
      sampleNumber: formData.sampleNumber,
      clientUnit: formData.clientUnit,
      clientDate: formData.clientDate,
      constructionUnit: formData.constructionUnit,
      buildingUnit: formData.buildingUnit,
      projectName: formData.projectName,
      constructionPart: formData.constructionPart,
      sampleName: formData.sampleName,
      spec: formData.spec,
      manufacturer: formData.manufacturer,
      sampleQuantity: formData.sampleQuantity,
      representativeBatch: formData.representativeBatch,
      batchNumber: formData.batchNumber,
      testCategory: formData.testCategory,
      clientAddressPhone: formData.clientAddressPhone,
      reportSend: formData.reportSend.join(','),
      sampleDisposal: formData.sampleDisposal.join(','),
      witness: formData.witness,
      witnessUnit: formData.witnessUnit,
      deliveryMode: formData.deliveryMode,
      deliveryDate1_y: formData.deliveryDate1_y,
      deliveryDate1_m: formData.deliveryDate1_m,
      deliveryDate1_d: formData.deliveryDate1_d,
      deliveryDate2_y: formData.deliveryDate2_y,
      deliveryDate2_m: formData.deliveryDate2_m,
      deliveryDate2_d: formData.deliveryDate2_d,
      fee: formData.fee,
      remarks: formData.remarks,
      sampleHistory: formData.sampleHistory,
      sampleStatus: formData.sampleStatus,
      testItems: formData.testItems,
      reviewer: formData.review || userInfo.userName || '',
      inspector: formData.inspect || userInfo.userName || '',
      approver: formData.approve || userInfo.userName || '',
      creator: userInfo.userName || userInfo.username || ''
    };
    
    // 发送请求
    const response = await axios.post('/api/entrustment/save', submitData);
    
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

        .entrustment-container {
            font-family: 'SimSun', 'Songti SC', serif; /* 宋体更像打印单据 */
            width: 210mm; /* A4 宽度 */
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
            padding: 8px 5px;
            vertical-align: middle;
        }
        .label {
            font-weight: bold;
            white-space: nowrap;
            width: 100px; /* 调整标签列宽 */
        }
        input[type="text"], textarea {
            width: 98%;
            border: none;
            outline: none;
            font-family: inherit;
            font-size: inherit;
            background-color: transparent;
        }
        input[type="text"]:focus, textarea:focus {
            background-color: #f0f8ff;
        }
        .checkbox-group label {
            margin-right: 15px;
            cursor: pointer;
        }
        .footer-info {
            display: flex;
            justify-content: space-between;
            margin-top: 20px;
            margin-bottom: 20px;
            font-size: 16px;
        }
        .page-footer {
            margin-top: 10px;
            text-align: right;
            font-size: 14px;
            margin-bottom: 50px;
        }
        .notes {
            font-size: 12px;
            line-height: 1.5;
        }
        .center-text {
            text-align: center;
        }
        textarea {
            resize: none;
            overflow: hidden;
        }
        @media print {
            .entrustment-container {
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
