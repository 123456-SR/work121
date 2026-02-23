<template>
  <div class="entrustment-container">


    <div class="no-print toolbar">
      <div class="toolbar-left">
        <button @click="goToList" class="link-button">&lt; 返回列表</button>
      </div>
      <div class="toolbar-right">
        <div v-if="formData.status !== undefined" class="status-text">
          状态:
          <span :style="{ color: getStatusColor(formData.status) }" class="status-label">
            {{ getStatusText(formData.status) }}
          </span>
        </div>

        <button
          v-if="showSubmitButton"
          @click="submitForm"
          :disabled="!formData.testerSignature"
          class="btn btn-primary btn-small"
        >
          提交
        </button>

        <template v-if="currentId">
          <button
            v-if="showAuditButtons"
            @click="submitWorkflow('AUDIT_PASS')"
            class="btn btn-primary btn-small"
          >
            审核通过
          </button>
          <button
            v-if="showAuditButtons"
            @click="submitWorkflow('REJECT')"
            class="btn btn-danger btn-small"
          >
            打回
          </button>
        </template>

        <button
          @click="printDocument"
          class="btn btn-secondary btn-small"
        >
          打印此单
        </button>
        <button
          @click="generatePdf"
          class="btn btn-secondary btn-small"
        >
          下载PDF
        </button>
        <button
          @click="previewPdf"
          class="btn btn-secondary btn-small"
        >
          预览PDF
        </button>
        <button
          @click="handleSign"
          class="btn btn-secondary btn-small"
        >
          签字
        </button>
        <button
          @click="saveForm"
          class="btn btn-secondary btn-small"
        >
          保存
        </button>
      </div>
    </div>

    <div v-if="formData.status === 2 && formData.rejectReason" style="background-color: #ffebee; color: #c62828; padding: 10px; border-radius: 4px; margin-bottom: 10px; border: 1px solid #ef9a9a;">
      <strong>打回原因：</strong> {{ formData.rejectReason }}
    </div>

<form id="entrustmentForm" ref="pdfForm" method="post">
<h2>检测 (验) 委托单 (代合同)</h2>

    <div class="header-info">
        <span>统一编号：<input type="text" v-model="formData.unifiedNumber"   name="unifiedNumber" style="width: 150px; border-bottom: 1px solid black;"></span>
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
                        <input type="text" v-model="formData.deliveryDate1"   name="deliveryDate1" style="width: 120px; border-bottom: 1px solid black;">
                    </div>
                    <div style="margin-bottom: 5px;">
                        <label><input type="radio" v-model="formData.deliveryMode"   name="deliveryMode" value="2">严格限定为:</label>
                        <input type="text" v-model="formData.deliveryDate2"   name="deliveryDate2" style="width: 120px; border-bottom: 1px solid black;">
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
        <span>委托(送样)人：<input type="text" v-model="formData.client" style="width: 150px; border-bottom: 1px solid black;"></span>
        <div style="position: relative; display: inline-block; margin-left: 20px;">
            <span>承接(收样)人：
                <div style="display: inline-block; width: 150px; border-bottom: 1px solid black; height: 30px; vertical-align: bottom; position: relative;">
                    <img v-if="formData.testerSignature" :src="formData.testerSignature" style="position: absolute; bottom: 0; left: 50%; transform: translateX(-50%); width: 100px; height: 50px; object-fit: contain;" />
                </div>
            </span>
        </div>

    </div>

    </form>




    



  </div>
</template>

<style scoped>
.no-print {
  margin-bottom: 20px;
}
.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}
.toolbar-left,
.toolbar-right {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}
.link-button {
  background: none;
  border: none;
  color: #3498db;
  cursor: pointer;
  font-size: 14px;
  padding: 0;
}
.link-button:hover {
  text-decoration: underline;
}
.status-text {
  font-size: 14px;
  font-weight: 500;
  color: #666;
}
.status-label {
  margin-left: 4px;
}
.btn {
  padding: 6px 12px;
  border-radius: 4px;
  border: 1px solid transparent;
  font-size: 13px;
  cursor: pointer;
  background-color: #f5f7fa;
  color: #333;
  transition: all 0.2s;
}
.btn-small {
  padding: 4px 10px;
  font-size: 12px;
}
.btn-primary {
  background-color: #3498db;
  color: #fff;
  border-color: #3498db;
}
.btn-secondary {
  background-color: #fff;
  border-color: #d0d7de;
  color: #34495e;
}
.btn-danger {
  background-color: #e74c3c;
  border-color: #e74c3c;
  color: #fff;
}
.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.btn:not(:disabled):hover {
  filter: brightness(0.95);
}
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}
.modal-content {
  background-color: white;
  padding: 20px;
  border-radius: 5px;
  width: 400px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}
@media print {
  .no-print {
    display: none;
  }
}
</style>

<script setup>
import { ref, reactive, onMounted, watch, inject, computed } from 'vue'
import axios from 'axios'

// 注入导航方法
const navigateTo = inject('navigateTo')

const props = defineProps({
  id: {
    type: String,
    default: null
  },
  wtNum: {
    type: String,
    default: null
  }
})

// 监听 wtNum 变化，确保组件复用或 prop 更新时能重新加载数据
watch(() => props.wtNum, (newVal) => {
  if (newVal) {
    console.log('Watch: wtNum changed to', newVal)
    loadDataByWtNum(newVal)
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
  deliveryDate1: '',
  deliveryDate2: '',
  fee: '',
  remarks: '',
  sampleHistory: '',
  sampleStatus: '',
  status: 0,
  testItems: '',
  reviewer: '', // Add reviewer field
  client: '',
  tester: '',
  clientRegName: '',
  testerSignature: '',
  reviewerSignature: '',
  approverSignature: '',
  rejectReason: ''
})

const currentId = ref(props.id)

const isSameTesterReviewer = computed(() => {
  return !!formData.tester && !!formData.reviewer && formData.tester === formData.reviewer
})

const showSubmitButton = computed(() => {
  return (formData.status == 0 || formData.status == 2 || formData.status > 5) && !isSameTesterReviewer.value
})

const showAuditButtons = computed(() => {
  if (!currentId.value) return false
  if (formData.status == 1) return true
  if (isSameTesterReviewer.value && (formData.status == 0 || formData.status == 2 || formData.status > 5)) return true
  return false
})

// 格式化日期
const formatDate = (dateStr, format) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  if (isNaN(date.getTime())) return ''
  
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  
  if (format === 'YYYY-MM-DD') {
    return `${y}-${m}-${d}`
  }
  return `${y}-${m}-${d}`
}

const computeTestCategoryFromDirectory = (directory) => {
  if (!directory) return ''
  const types = []
  for (let i = 1; i <= 10; i++) {
    const t = directory[`table${i}Type`]
    if (t) types.push(t)
  }
  const categories = []
  const addCategory = (c) => {
    if (!categories.includes(c)) categories.push(c)
  }
  types.forEach(type => {
    const upper = String(type || '').toUpperCase()
    if (upper.includes('NUCLEAR')) addCategory('核子法')
    else if (upper.includes('SAND')) addCategory('灌砂法')
    else if (upper.includes('WATER')) addCategory('灌水法')
    else if (upper.includes('CUTTING')) addCategory('环刀法')
    else if (upper.includes('REBOUND')) addCategory('回弹法')
    else if (upper.includes('PENETRATION')) addCategory('轻型动力触探')
    else if (upper.includes('BECKMAN')) addCategory('贝克曼梁')
    else if (upper.includes('DENSITY')) addCategory('密度试验')
  })
  if (categories.length === 0) return '通用检测'
  return categories.join(',')
}

const autoFillTestCategoryFromDirectory = (directory) => {
  if (!directory) return
  if (formData.testCategory && formData.testCategory.trim() !== '') return
  const category = computeTestCategoryFromDirectory(directory)
  formData.testCategory = category
}

const mapDataToForm = (data) => {
  console.log('Mapping data keys:', Object.keys(data))
  currentId.value = data.id
  formData.unifiedNumber = data.wtNum || data.unifiedNumber || ''
  formData.sampleNumber = data.wtNum || data.sampleNumber || '' 
  formData.clientUnit = data.clientUnit || ''
  formData.clientDate = data.commissionDate ? formatDate(data.commissionDate, 'YYYY-MM-DD') : ''
  formData.constructionUnit = data.constructionUnit || ''
  formData.buildingUnit = data.buildingUnit || ''
  formData.projectName = data.projectName || ''
  formData.constructionPart = data.projectArea || data.constructionPart || ''
  formData.sampleQuantity = data.sampleQuantity || ''
  formData.representativeBatch = data.representativeBatch || ''
  formData.fee = data.fee || ''
  formData.sampleHistory = data.sampleHistory || ''
  const statusVal = parseInt(data.status)
  formData.status = !isNaN(statusVal) ? statusVal : 0
  formData.clientAddressPhone = data.clientAddressPhone || data.clientUnitTel || ''
  formData.reportSend = data.reportSendMode ? data.reportSendMode.split(',') : []
  formData.sampleDisposal = data.sampleDisposal ? data.sampleDisposal.split(',') : []
  formData.deliveryMode = data.deliveryMode || '3'
  if (data.deliveryDate) {
      if (formData.deliveryMode === '1') {
          formData.deliveryDate1 = data.deliveryDate
      } else if (formData.deliveryMode === '2') {
          formData.deliveryDate2 = data.deliveryDate
      }
  }
  formData.witnessUnit = data.supervisionUnit || data.witnessUnit || '' 
  formData.witness = data.witness || '' 
  formData.client = data.client || ''
  
  // Load defaults from directory if available and data is empty
  const directory = JSON.parse(localStorage.getItem('currentDirectory') || '{}')
  
  // Tester (Undertaker) - Priority: data.receiver -> data.tester -> directory.wtUndertaker
  formData.tester = data.receiver || data.tester || directory.wtUndertaker || ''
  
  // Reviewer - Priority: data.wtReviewer -> data.reviewer -> directory.wtReviewer
  formData.reviewer = data.wtReviewer || data.reviewer || directory.wtReviewer || ''
  
  formData.approver = data.approver || ''
  formData.clientRegName = data.clientRegName || ''
  
  // Helper to ensure Base64 prefix
  const ensureBase64Prefix = (str) => {
    if (!str) return ''
    if (str.startsWith('data:image')) return str
    return `data:image/png;base64,${str}`
  }

  formData.testerSignature = ensureBase64Prefix(data.wtManSign || data.inspectSignaturePhoto)
  formData.reviewerSignature = ensureBase64Prefix(data.wtReviewSign || data.reviewSignaturePhoto)
  formData.approverSignature = ensureBase64Prefix(data.approveSignaturePhoto)
  formData.rejectReason = data.rejectReason || ''
  
  formData.sampleName = data.sampleName || ''
  formData.spec = data.spec || ''
  formData.manufacturer = data.manufacturer || ''
  formData.batchNumber = data.batchNumber || ''
  formData.testCategory = data.testCategory || ''
  formData.sampleStatus = data.sampleStatus || ''
  formData.testItems = data.testItems || ''
  formData.remarks = data.remarks || ''
  formData.reportSendUser = data.reportSendUser || ''
  formData.witnessIdCard = data.witnessIdCard || ''
  formData.samplingManIdCard = data.samplingManIdCard || ''
}

const loadDataByWtNum = async (wtNum) => {
  if (!wtNum) return
  try {
    console.log('Fetching data for wtNum:', wtNum)
    const response = await axios.get(`/api/jc-core-wt-info/by-wt-num?wtNum=${encodeURIComponent(wtNum)}`)
    console.log('API Response:', response.data)
    
    // Fill wtNum regardless of API success (it's the key we are looking for)
    formData.unifiedNumber = wtNum
    formData.sampleNumber = wtNum

    if (response.data.success && response.data.data) {
       console.log('Mapping data to form:', response.data.data)
       mapDataToForm(response.data.data)
       
       // Also fetch and update directory info to ensure navigation works
       fetchDirectoryInfo(wtNum)
    } else {
       console.warn('Backend data not found for wtNum:', wtNum, 'Using virtual data instead.')
       // If data not found, we still keep the wtNum we set above
    }
  } catch (error) {
    console.error('Error loading data by wtNum:', error)
  } finally {
    // Always populate virtual data for required fields if they are empty
    // This satisfies the requirement: "insert virtual data into non-empty fields" (mandatory fields)
    // Moving to finally block to ensure it runs even if API fails or returns error
    console.log('Populating virtual data...')
    populateVirtualData()
  }
}

const fetchDirectoryInfo = async (wtNum) => {
    try {
        const response = await axios.post('/api/directory/get-by-dirname', { dirName: wtNum })
        if (response.data.success && response.data.data) {
            console.log('Directory info updated for navigation:', response.data.data)
            localStorage.setItem('currentDirectory', JSON.stringify(response.data.data))
            autoFillTestCategoryFromDirectory(response.data.data)
        }
    } catch (e) {
        console.error('Failed to fetch directory info', e)
    }
}

// Populate virtual data for testing/demo purposes
const populateVirtualData = () => {
  const virtualData = {
    clientUnit: '测试委托单位',
    constructionUnit: '测试施工单位',
    buildingUnit: '测试建设单位',
    projectName: '测试工程项目',
    sampleName: '测试样品',
    client: '张三',
    constructionPart: '测试部位',
    witnessUnit: '测试见证单位',
    witness: '李四',
    spec: '测试规格',
    manufacturer: '测试厂家',
    clientAddressPhone: '测试地址 13800138000',
    sampleQuantity: '1组',
    representativeBatch: '100',
    batchNumber: 'P-001',
    sampleHistory: '无',
    testItems: '常规检测',
    fee: '100'
  }

  for (const [key, value] of Object.entries(virtualData)) {
    if (!formData[key] || formData[key].trim() === '') {
      formData[key] = value
    }
  }
  
  // Handle dates if empty
  if (!formData.clientDate) {
      formData.clientDate = formatDate(new Date(), 'YYYY-MM-DD')
  }
  
  // Handle arrays if empty
  if (!formData.reportSend || formData.reportSend.length === 0) {
      formData.reportSend = ['自取']
  }
  if (!formData.sampleDisposal || formData.sampleDisposal.length === 0) {
      formData.sampleDisposal = ['留样']
  }
}

// 加载数据
const loadData = async (id) => {
  if (!id) return
  
  try {
    const response = await axios.get(`/api/form-data/get-by-type-and-id?formType=ENTRUSTMENT_LIST&id=${id}`)
    if (response.data.success && response.data.data) {
      const data = response.data.data
      mapDataToForm(data)
    } else {
      console.error('Failed to load data:', response.data.message)
    }
  } catch (error) {
    console.error('Error loading data:', error)
  }
}

onMounted(() => {
  console.log('Entrustment mounted. props:', props)
  if (props.wtNum) {
      console.log('Loading by wtNum:', props.wtNum)
      loadDataByWtNum(props.wtNum)
      return
  }

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

const validateForm = () => {
  const defaults = [
    { field: 'clientUnit', value: '未填写单位', label: '委托单位' },
    { field: 'constructionUnit', value: '未填写单位', label: '施工单位' },
    { field: 'buildingUnit', value: '未填写单位', label: '建设单位' },
    { field: 'projectName', value: '未命名工程', label: '工程名称' },
    { field: 'sampleName', value: '未填写样品', label: '样品名称' },
    { field: 'client', value: '未填写', label: '委托人' },
    { field: 'constructionPart', value: '未填写部位', label: '施工部位' },
    { field: 'witnessUnit', value: '未填写单位', label: '见证单位' },
    { field: 'witness', value: '未填写', label: '见证人' },
    { field: 'spec', value: '未填写', label: '规格/型号' },
    { field: 'manufacturer', value: '未填写', label: '生产厂家' },
    { field: 'clientAddressPhone', value: '未填写', label: '委托单位地址及电话' },
    { field: 'clientAddressPhone', value: '未填写地址', label: '委托单位地址及电话' }, // Check both variants if unsure
    { field: 'testCategory', value: '未填写', label: '检测类别' }
  ]
  
  for (const item of defaults) {
    // Check for exact match with default value
    if (formData[item.field] === item.value) {
      alert(`请填写${item.label} (当前为默认值: ${item.value})`)
      return false
    }
    // Check for empty values
    if (!formData[item.field] || formData[item.field].trim() === '') {
       alert(`请填写${item.label}`)
       return false
    }
  }
  return true
}

const handleSign = async () => {
  const user = JSON.parse(localStorage.getItem('userInfo'))
  if (!user) {
    alert('请先登录')
    return
  }

  // 获取用户账号，支持多种字段名
  const currentAccount = user.username || user.userAccount || user.userName
  if (!currentAccount) {
    alert('无法获取用户账号信息')
    return
  }

  try {
    const response = await axios.post('/api/signature/get', {
      userAccount: currentAccount
    })

    if (response.data.success && response.data.data && response.data.data.signatureBlob) {
      const signatureBlob = response.data.data.signatureBlob
      let imgSrc = ''
      
      // Handle Base64 string or ArrayBuffer
      if (typeof signatureBlob === 'string') {
        imgSrc = `data:image/png;base64,${signatureBlob}`
      } else {
        // Assume it's handled or we need to convert. 
        // Based on Signature.vue, it might need conversion if not string.
        // But let's assume standard Jackson behavior for now (Base64 string).
        alert('签名数据格式不支持')
        return
      }

      let signed = false
      const currentRealName = user.fullName || currentAccount

      // Match Tester
      // 允许匹配账号或真实姓名，或者检测人为空 (Allow matching account, real name, or empty)
      // 用户要求：仅签名，不填充其他内容 (User request: Only sign, do not fill others)
      if (!formData.tester || formData.tester === currentAccount || formData.tester === currentRealName) {
        if (!validateForm()) return
        formData.testerSignature = imgSrc
        signed = true
      }

      // Match Reviewer
      // if (formData.reviewer === currentAccount || formData.reviewer === currentRealName) {
      //   formData.reviewerSignature = imgSrc
      //   signed = true
      // }

      // Match Approver
      // if (formData.approver === currentAccount || formData.approver === currentRealName) {
      //   formData.approverSignature = imgSrc
      //   signed = true
      // }
      
      if (signed) {
        alert('签名成功')
      } else {
        alert(`当前用户(${currentAccount}/${currentRealName})与表单中的人员(${formData.tester})不匹配，无法签名`)
      }
    } else {
      alert('未找到您的电子签名，请先去“电子签名”页面设置')
    }
  } catch (error) {
    console.error('Sign error:', error)
    alert('签名失败')
  }
}

const saveForm = async (silent = false) => {
  const idToSave = currentId.value || props.id
  if (!idToSave && !formData.unifiedNumber) {
    alert('缺少必要的ID或统一编号')
    return false
  }

  // Get current user
  const user = JSON.parse(localStorage.getItem('user'))
  const currentUserName = user ? (user.fullName || user.username) : '未知用户'
  
  // Set clientRegName if empty (new record)
  if (!formData.clientRegName) {
    formData.clientRegName = currentUserName
  }

  const payload = {
    id: idToSave, // Ensure ID is passed for updates
    wtNum: formData.unifiedNumber,
    commissionDate: formData.clientDate,
    clientUnit: formData.clientUnit,
    client: formData.client,
    projectName: formData.projectName,
    projectArea: formData.constructionPart,
    constructionUnit: formData.constructionUnit,
    buildingUnit: formData.buildingUnit,
    witnessUnit: formData.witnessUnit,
    witness: formData.witness,
    clientRegName: formData.clientRegName, 
    // Map other fields
    sampleStatus: formData.sampleStatus, // Maintain current status
    remarks: formData.remarks,
    testCategory: formData.testCategory,
    reviewer: formData.reviewer,
    tester: formData.tester,
    approver: formData.approver,
    inspectSignaturePhoto: formData.testerSignature,
    reviewSignaturePhoto: formData.reviewerSignature,
    approveSignaturePhoto: formData.approverSignature,
    
    // New Role Fields
    wtReviewer: formData.reviewer,
    wtManSign: formData.testerSignature,
    wtReviewSign: formData.reviewerSignature,
    
    // New fields
    sampleName: formData.sampleName,
    spec: formData.spec,
    manufacturer: formData.manufacturer,
    sampleQuantity: formData.sampleQuantity,
    representativeBatch: formData.representativeBatch,
    batchNumber: formData.batchNumber,
    fee: formData.fee,
    sampleHistory: formData.sampleHistory,
    clientAddressPhone: formData.clientAddressPhone,
    reportSendMode: formData.reportSend ? formData.reportSend.join(',') : '',
    sampleDisposal: formData.sampleDisposal ? formData.sampleDisposal.join(',') : '',
    deliveryMode: formData.deliveryMode,
    deliveryDate: (formData.deliveryMode === '1' && formData.deliveryDate1) ? formData.deliveryDate1 : 
                  (formData.deliveryMode === '2' && formData.deliveryDate2) ? formData.deliveryDate2 : '',
    testItems: formData.testItems
  }
  
  // If sampleStatus is empty, default to '1' (Draft)
  if (!payload.sampleStatus) payload.sampleStatus = '1'

  try {
    const response = await axios.post('/api/entrustment/save', payload)
    if (response.data.success) {
      if (response.data.id) {
        currentId.value = response.data.id
        // Also update the route or props if possible, but currentId is reactive
      }
      if (!silent) alert('保存成功')
      return true
    } else {
      if (!silent) alert('保存失败: ' + response.data.message)
      return false
    }
  } catch (error) {
    console.error('Save error:', error)
    alert('保存出错')
    return false
  }
}

const getStatusText = (status) => {
  const s = parseInt(status)
  switch(s) {
    case 0: return '草稿'
    case 1: return '待审核'
    case 2: return '已打回'
    case 3: return '待签字'
    case 4: return '待批准'
    case 5: return '已通过'
    default: return '未知/历史'
  }
}

const getStatusColor = (status) => {
  const s = parseInt(status)
  switch(s) {
    case 0: return '#9E9E9E' // Grey
    case 1: return '#2196F3' // Blue
    case 2: return '#F44336' // Red
    case 3: return '#FF9800' // Orange
    case 4: return '#9C27B0' // Purple
    case 5: return '#4CAF50' // Green
    default: return '#000000'
  }
}

const submitForm = async () => {
  // Check if saved, if not, auto-save silently
  if (!currentId.value) {
      const saved = await saveForm(true)
      if (!saved) return
      // Reload data to ensure we have the ID and latest state
      // Actually saveForm doesn't update currentId automatically from response unless we handle it
      // Let's check saveForm logic again. It doesn't update currentId!
      // We need to fetch the ID or rely on wtNum lookup if the backend created it.
      // But wait, saveForm uses currentId.value || props.id.
      // If it's a NEW record, saveForm posts. Backend creates ID.
      // Does backend return the ID?
      // We need to check EntrustmentController or the response in saveForm.
  }
  submitWorkflow('SUBMIT')
}

const confirmSubmit = async () => {
   submitWorkflow('SUBMIT')
}

const submitWorkflow = async (action) => {
    const idToSubmit = currentId.value || props.id
    if (!idToSubmit) {
        alert('请先保存记录')
        return
    }
    
    const user = JSON.parse(localStorage.getItem('userInfo'))
    if (!user || !user.username) {
        alert('请先登录')
        return
    }

    let signatureData = null
    let nextHandler = ''

    if (action === 'SUBMIT') {
        // Role check: Only tester can submit
        if (formData.tester && user.username !== formData.tester) {
            alert('您不是该单据的检测人 (' + formData.tester + ')，无权提交')
            return
        }

        // Check signature
        if (!formData.testerSignature) {
            alert('请先进行检测人签字')
            return
        }
        signatureData = formData.testerSignature.replace(/^data:image\/\w+;base64,/, '')
    } else if (action === 'AUDIT_PASS' || (action === 'REJECT' && formData.status === 1)) {
        // Role check: Only reviewer can audit/reject
        if (formData.reviewer && user.username !== formData.reviewer) {
            alert('您不是该单据的审核人 (' + formData.reviewer + ')，无权操作')
            return
        }

        if (action === 'AUDIT_PASS') {
            // Reviewer does not need signature
            // if (!formData.reviewerSignature) {
            //    alert('请先进行审核人签字')
            //    return
            // }
            // signatureData = formData.reviewerSignature.replace(/^data:image\/\w+;base64,/, '')
            signatureData = null
        }

        nextHandler = formData.reviewer // Stay with reviewer or move to next? 
        // Logic: Audit Pass usually moves to Approver or finishes if no approver.
        // For now, let's keep existing nextHandler logic or rely on backend to determine next step if nextHandler is null.
        // But wait, if I don't set nextHandler, what happens?
        // The original code didn't set nextHandler for AUDIT_PASS except 'formData.reviewer'.
        // Let's assume the backend or workflow engine handles the flow.
        // But I must ensure signature is sent.
        
    } else if (action === 'SIGN_REVIEW') {
         // Role check: Only reviewer can sign
        if (formData.reviewer && user.username !== formData.reviewer) {
            alert('您不是该单据的复核人 (' + formData.reviewer + ')，无权签字')
            return
        }
        
        if (!formData.reviewerSignature) {
            alert('请先进行复核人签字')
            return
        }
        signatureData = formData.reviewerSignature.replace(/^data:image\/\w+;base64,/, '')
        
    } else if (action === 'SIGN_APPROVE' || (action === 'REJECT' && formData.status === 4)) {
         // Role check: Only approver can sign/reject
         if (formData.approver && user.username !== formData.approver) {
            alert('您不是该单据的批准人 (' + formData.approver + ')，无权操作')
            return
        }

        if (action === 'SIGN_APPROVE') {
            if (!formData.approverSignature) {
                alert('请先进行批准人签字')
                return
            }
            signatureData = formData.approverSignature.replace(/^data:image\/\w+;base64,/, '')
        }
    }

    const request = {
        tableType: 'ENTRUSTMENT',
        recordId: idToSubmit,
        action: action,
        userAccount: user.username,
        signatureData: signatureData,
        nextHandler: nextHandler
    }

    if (action === 'REJECT') {
        const reason = prompt('请输入打回原因:')
        if (!reason) return
        request.rejectReason = reason
    }

    try {
        const response = await axios.post('/api/workflow/handle', request)
        if (response.data.success) {
            alert('操作成功')
            await loadData(idToSubmit)
        } else {
            alert('操作失败: ' + response.data.message)
        }
    } catch (e) {
        console.error('Workflow error', e)
        alert('操作出错')
    }
}

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
  if (formData.status < 3) {
    alert('委托单未审核通过，无法填写记录表')
    return
  }
  goToFirstRecordForm()
}

const goToFirstRecordForm = async () => {
  let currentDirectory = localStorage.getItem('currentDirectory')

  if (!currentDirectory && formData.unifiedNumber) {
    try {
      const response = await axios.post('/api/directory/get-by-dirname', { dirName: formData.unifiedNumber })
      if (response.data.success && response.data.data) {
        currentDirectory = JSON.stringify(response.data.data)
        localStorage.setItem('currentDirectory', currentDirectory)
      }
    } catch (e) {
      console.error('Failed to fetch directory info', e)
    }
  }

  if (!currentDirectory) {
    alert('未找到流程信息')
    return
  }

  try {
    const directory = JSON.parse(currentDirectory)
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

    const recordForm = formQueue.find(f => f.type.includes('_RECORD'))
    if (!recordForm) {
      navigateBetweenForms(1)
      return
    }

    const componentMap = {
      'ENTRUSTMENT': 'Entrustment',
      'ENTRUSTMENT_LIST': 'Entrustment',
      'REBOUND_METHOD_RECORD': 'ReboundMethodRecord',
      'LIGHT_DYNAMIC_PENETRATION_RECORD': 'LightDynamicPenetrationRecord',
      'NUCLEAR_DENSITY_RECORD': 'NuclearDensityRecord',
      'SAND_REPLACEMENT_RECORD': 'SandReplacementRecord',
      'WATER_REPLACEMENT_RECORD': 'WaterReplacementRecord',
      'CUTTING_RING_RECORD': 'CuttingRingRecord',
      'BECKMAN_BEAM_RECORD': 'BeckmanBeamRecord',
      'SIGNATURE': 'Signature',
      'DENSITY_TEST_RECORD': 'NuclearDensityRecord',
      'DENSITY_TEST_REPORT': 'DensityTestReport',
      'DENSITY_TEST_RESULT': 'DensityTestResult',
      'LIGHT_DYNAMIC_PENETRATION': 'LightDynamicPenetration',
      'LIGHT_DYNAMIC_PENETRATION_RESULT': 'LightDynamicPenetrationResult',
      'REBOUND_METHOD_REPORT': 'ReboundMethodReport',
      'BECKMAN_BEAM_REPORT': 'BeckmanBeamReport',
      'BECKMAN_BEAM_RESULT': 'BeckmanBeamResult'
    }

    const componentName = componentMap[recordForm.type]
    if (componentName && navigateTo) {
      const targetIndex = formQueue.findIndex(f => f.type === recordForm.type && f.id === recordForm.id)
      if (targetIndex >= 0) {
        localStorage.setItem('currentFormType', recordForm.type)
        localStorage.setItem('currentFormIndex', String(targetIndex))
      }

      const props = {}
      if (recordForm.id) {
        props.id = recordForm.id
      }
      if (formData.unifiedNumber) {
        props.wtNum = formData.unifiedNumber
      }
      props.draftMode = true

      navigateTo(componentName, props)
    } else {
      alert('暂不支持该类型的页面跳转: ' + recordForm.type)
    }
  } catch (e) {
    console.error('Navigation error:', e)
    alert('导航失败，请重试')
  }
}

// 表单导航
const navigateBetweenForms = async (direction) => {
  let currentDirectory = localStorage.getItem('currentDirectory')
  
  // If not in local storage, try to fetch by unifiedNumber
  if (!currentDirectory && formData.unifiedNumber) {
      try {
          const response = await axios.post('/api/directory/get-by-dirname', { dirName: formData.unifiedNumber })
          if (response.data.success && response.data.data) {
              currentDirectory = JSON.stringify(response.data.data)
              localStorage.setItem('currentDirectory', currentDirectory)
          }
      } catch (e) {
          console.error('Failed to fetch directory info', e)
      }
  }

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
    let currentFormType = localStorage.getItem('currentFormType') || 'ENTRUSTMENT_LIST'
    console.log('Current form type from storage:', currentFormType)
    
    // 找到当前表单在队列中的位置
    let currentIndex = -1
    for (let i = 0; i < formQueue.length; i++) {
      if (formQueue[i].type === currentFormType) {
        currentIndex = i
        break
      }
    }
    
    // 如果没找到，尝试匹配 Entrustment
    if (currentIndex === -1) {
      if (currentFormType === 'ENTRUSTMENT_LIST' || currentFormType === 'ENTRUSTMENT') {
          for (let i = 0; i < formQueue.length; i++) {
            if (formQueue[i].type === 'ENTRUSTMENT' || formQueue[i].type === 'ENTRUSTMENT_LIST') {
                currentIndex = i
                break
            }
          }
      }
    }

    // 如果还是没找到，且我们在Entrustment页面（通常是第一个），默认从第一个开始
    if (currentIndex === -1) {
      console.warn('Could not find current form in queue, defaulting to index 0')
      currentIndex = 0
    }
    
    console.log('Current index in queue:', currentIndex)

    // 计算目标表单的位置
    const targetIndex = currentIndex + direction
    if (targetIndex < 0 || targetIndex >= formQueue.length) {
      alert('已经是第' + (direction === -1 ? '一' : '最后') + '个表单了')
      return
    }
    
    // 跳转到目标表单
    const targetForm = formQueue[targetIndex]
    console.log('Target form:', targetForm)
    
    const componentMap = {
      'ENTRUSTMENT': 'Entrustment',
      'ENTRUSTMENT_LIST': 'Entrustment',
      'REBOUND_METHOD_RECORD': 'ReboundMethodRecord',
      'LIGHT_DYNAMIC_PENETRATION_RECORD': 'LightDynamicPenetrationRecord',
      'NUCLEAR_DENSITY_RECORD': 'NuclearDensityRecord',
      'SAND_REPLACEMENT_RECORD': 'SandReplacementRecord',
      'WATER_REPLACEMENT_RECORD': 'WaterReplacementRecord',
      'CUTTING_RING_RECORD': 'CuttingRingRecord',
      'BECKMAN_BEAM_RECORD': 'BeckmanBeamRecord',
      'SIGNATURE': 'Signature',
      'DENSITY_TEST_RECORD': 'DensityTestResult',
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
      // 传递 wtNum 确保子组件能加载数据
      if (formData.unifiedNumber) {
        props.wtNum = formData.unifiedNumber
      }
      
      console.log(`Navigating to ${componentName} with props:`, props)
      // 使用navigateTo方法导航到对应的组件
      navigateTo(componentName, props)
    } else {
      console.warn('Unknown form type or navigateTo missing:', targetForm.type)
      alert('暂不支持该类型的页面跳转: ' + targetForm.type)
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


// 返回列表
const goToList = () => {
  if (navigateTo) {
    navigateTo('EntrustmentList');
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
