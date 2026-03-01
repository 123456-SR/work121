<template>
  <div class="lightDynamicPenetration-container">


    <div class="no-print toolbar">
      <div class="toolbar-left">
        <button @click="goToList" class="link-button">&lt; 返回列表</button>
        <span v-if="formData.status !== undefined" class="status-text">
          状态:
          <span :style="{ color: getStatusColor(formData.status) }" class="status-label">
            {{ getStatusText(formData.status) }}
          </span>
        </span>
      </div>

      <div class="toolbar-right">
        <button
          v-if="formData.status === 0 || formData.status === 2"
          @click="submitForm"
          class="btn btn-secondary btn-small"
        >
          保存
        </button>
        <button
          v-if="formData.status === 0 || formData.status === 2"
          @click="handleSign"
          class="btn btn-secondary btn-small"
        >
          签字
        </button>
        <button
          v-if="formData.status === 0 || formData.status === 2"
          @click="submitWorkflow('SUBMIT')"
          class="btn btn-primary btn-small"
        >
          提交
        </button>

        <button
          v-if="formData.status === 1"
          @click="submitWorkflow('AUDIT_PASS')"
          class="btn btn-primary btn-small"
        >
          审核通过
        </button>
        <button
          v-if="formData.status === 1"
          @click="submitWorkflow('REJECT')"
          class="btn btn-danger btn-small"
        >
          退回
        </button>

        <button
          v-if="formData.status === 3"
          @click="handleSign"
          class="btn btn-secondary btn-small"
        >
          签字
        </button>
        <button
          v-if="formData.status === 3"
          @click="submitWorkflow('SIGN_REVIEW')"
          class="btn btn-primary btn-small"
        >
          复核签字
        </button>

        <button
          v-if="formData.status === 4"
          @click="handleSign"
          class="btn btn-secondary btn-small"
        >
          签字
        </button>
        <button
          v-if="formData.status === 4"
          @click="submitWorkflow('SIGN_APPROVE')"
          class="btn btn-primary btn-small"
        >
          批准签字
        </button>

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
      </div>
    </div>
    
    <div v-if="formData.status === 2 && formData.rejectReason" style="background-color: #ffebee; color: #c62828; padding: 10px; border-radius: 4px; margin-bottom: 10px; border: 1px solid #ef9a9a;">
        <strong>打回原因：</strong> {{ formData.rejectReason }}
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
            <td class="label" style="width: 10%;">工程名称</td>
            <!-- 左侧内容占 6 列，右侧标签占 1 列，右侧内容占 3 列：1+6+1+2=10 -->
            <td colspan="6"><input type="text" v-model="formData.projectName"   name="projectName"></td>
            <td class="label" style="width: 10%;">委托日期</td>
            <td colspan="2"><input type="text" v-model="formData.entrustDate"   name="entrustDate"></td>
        </tr>
        <!-- Row 2 -->
        <tr>
            <td class="label">施工部位</td>
            <td colspan="6"><input type="text" v-model="formData.constructionPart"   name="constructionPart"></td>
            <td class="label">检测日期</td>
            <td colspan="2"><input type="text" v-model="formData.testDate"   name="testDate"></td>
        </tr>
        <!-- Row 3 -->
        <tr>
            <td class="label">岩土性状</td>
            <td colspan="6"><input type="text" v-model="formData.soilProperty"   name="soilProperty"></td>
            <td class="label">报告日期</td>
            <td colspan="2"><input type="text" v-model="formData.reportDate"   name="reportDate"></td>
        </tr>
        <!-- Row 4 -->
        <tr>
            <td class="label">见证单位</td>
            <td colspan="6"><input type="text" v-model="formData.witnessUnit"   name="witnessUnit"></td>
            <td class="label">见证人</td>
            <td colspan="2"><input type="text" v-model="formData.witness"   name="witness"></td>
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
                    <!-- 左栏：一个测点位置对应 7 行深度/锤击数 -->
                    <td v-if="subIndex === 0" rowspan="7">
                      <textarea
                        v-model="block.pos_L"
                        :name="`pos_L_${blockIndex}`"
                        style="height: 100%; width: 100%; border: none; text-align: center; padding-top: 10px;"
                      ></textarea>
                    </td>

                    <td><input type="text" v-model="depth.depth_L" :name="`depth_L_${blockIndex * 7 + subIndex}`"></td>
                    <td><input type="text" v-model="depth.actual_L" :name="`actual_L_${blockIndex * 7 + subIndex}`"></td>

                    <td v-if="subIndex === 0" rowspan="7">
                      <input type="text" v-model="block.avg_L" :name="`avg_L_${blockIndex}`" style="height: 100%;" />
                    </td>
                    <td v-if="subIndex === 0" rowspan="7">
                      <input type="text" v-model="block.capacity_L" :name="`capacity_L_${blockIndex}`" style="height: 100%;" />
                    </td>

                    <!-- 右栏 -->
                    <td v-if="subIndex === 0" rowspan="7">
                      <textarea
                        v-model="block.pos_R"
                        :name="`pos_R_${blockIndex}`"
                        style="height: 100%; width: 100%; border: none; text-align: center; padding-top: 10px;"
                      ></textarea>
                    </td>

                    <td><input type="text" v-model="block.depths_R[subIndex].depth_R" :name="`depth_R_${blockIndex * 7 + subIndex}`"></td>
                    <td><input type="text" v-model="block.depths_R[subIndex].actual_R" :name="`actual_R_${blockIndex * 7 + subIndex}`"></td>

                    <td v-if="subIndex === 0" rowspan="7">
                      <input type="text" v-model="block.avg_R" :name="`avg_R_${blockIndex}`" style="height: 100%;" />
                    </td>
                    <td v-if="subIndex === 0" rowspan="7">
                      <input type="text" v-model="block.capacity_R" :name="`capacity_R_${blockIndex}`" style="height: 100%;" />
                    </td>
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
        <div style="position: relative; display: inline-block;">
            <span>批准：<span style="display: inline-block; width: 100px; border-bottom: 1px solid black; height: 1em;"></span></span>
            <img v-if="formData.approverSignature" :src="formData.approverSignature" style="position: absolute; top: -30px; left: 40px; width: 80px; height: 40px; mix-blend-mode: multiply; pointer-events: none;" />
        </div>
        <div style="position: relative; display: inline-block;">
            <span>审核：<span style="display: inline-block; width: 100px; border-bottom: 1px solid black; height: 1em;"></span></span>
            <img v-if="formData.reviewerSignature" :src="formData.reviewerSignature" style="position: absolute; top: -30px; left: 40px; width: 80px; height: 40px; mix-blend-mode: multiply; pointer-events: none;" />
        </div>
        <div style="position: relative; display: inline-block;">
            <span>检验：<span style="display: inline-block; width: 100px; border-bottom: 1px solid black; height: 1em;"></span></span>
            <img v-if="formData.testerSignature" :src="formData.testerSignature" style="position: absolute; top: -30px; left: 40px; width: 80px; height: 40px; mix-blend-mode: multiply; pointer-events: none;" />
        </div>
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
import { reactive, ref, onMounted, defineProps, inject } from 'vue';
import axios from 'axios';

const navigateTo = inject('navigateTo');

const goToList = () => {
  if (typeof navigateTo === 'function') {
    navigateTo('LightDynamicPenetrationReportList');
  }
};

const props = defineProps({
  id: String
})

const pdfForm = ref(null)
const currentEntrustmentId = ref(null)
const fullDataJson = ref({})

const formData = reactive({
  status: 0,
  dataBlocks: Array.from({ length: 2 }, () => ({
    pos_L: "",
    depths: Array.from({ length: 7 }, () => ({ depth_L: "", actual_L: "" })),
    avg_L: "",
    capacity_L: "",
    pos_R: "",
    depths_R: Array.from({ length: 7 }, () => ({ depth_R: "", actual_R: "" })),
    avg_R: "",
    capacity_R: ""
  }))
})

;[
  "entrustingUnit",
  "unifiedNumber",
  "projectName",
  "entrustDate",
  "constructionPart",
  "testDate",
  "soilProperty",
  "reportDate",
  "witnessUnit",
  "witness",
  "designCapacity",
  "hammerWeight",
  "dropDistance",
  "testCategory",
  "testBasis",
  "equipment",
  "remarks",
  "approver",
  "recordTester",
  "recordReviewer",
  "filler",
  "approverSignature",
  "reviewerSignature",
  "testerSignature",
  "companyName",
  "companyAddress",
  "companyPhone",
  "conclusion"
].forEach((k) => (formData[k] = ""))

const formatDate = (d) => {
    if (!d) return ''
    const date = new Date(d)
    const year = date.getFullYear()
    const month = ('0' + (date.getMonth() + 1)).slice(-2)
    const day = ('0' + date.getDate()).slice(-2)
    return `${year}-${month}-${day}`
}

const normalizeSignatureSrc = (src) => {
  if (!src) return ''
  if (src.startsWith('data:image')) return src
  return `data:image/png;base64,${src}`
}

const getStatusText = (status) => {
  const s = parseInt(status)
  switch(s) {
    // 统一状态名称
    case 0: return '草稿'
    case 1: return '已提交待审核'
    case 2: return '已打回'
    case 3: return '待签字'
    case 4: return '已签字待提交'
    case 5: return '审核通过待批准'
    case 6: return '已批准'
    case 7: return '驳回'
    // 报告表状态 (10-17)
    case 10: return '草稿'
    case 11: return '已提交待审核'
    case 12: return '已打回'
    case 13: return '待签字'
    case 14: return '已签字待提交'
    case 15: return '审核通过待批准'
    case 16: return '已批准'
    case 17: return '驳回'
    // 结果表状态 (20-27)
    case 20: return '草稿'
    case 21: return '已提交待审核'
    case 22: return '已打回'
    case 23: return '待签字'
    case 24: return '已签字待提交'
    case 25: return '审核通过待批准'
    case 26: return '已批准'
    case 27: return '驳回'
    default: return '未知'
  }
}

const getStatusColor = (status) => {
  const s = parseInt(status)
  switch(s) {
    // 记录表状态 (0-5)
    case 0: return '#6c757d' // secondary
    case 1: return '#007bff' // primary
    case 2: return '#dc3545' // danger
    case 3: return '#ffc107' // warning
    case 4: return '#17a2b8' // info
    case 5: return '#ff8c00' // orange
    case 6: return '#28a745' // success
    case 7: return '#dc3545' // danger
    // 报告表状态 (10-17)
    case 10: return '#6c757d' // secondary
    case 11: return '#007bff' // primary
    case 12: return '#dc3545' // danger
    case 13: return '#ffc107' // warning
    case 14: return '#17a2b8' // info
    case 15: return '#ff8c00' // orange
    case 16: return '#28a745' // success
    case 17: return '#dc3545' // danger
    // 结果表状态 (20-27)
    case 20: return '#6c757d' // secondary
    case 21: return '#007bff' // primary
    case 22: return '#dc3545' // danger
    case 23: return '#ffc107' // warning
    case 24: return '#17a2b8' // info
    case 25: return '#ff8c00' // orange
    case 26: return '#28a745' // success
    case 27: return '#dc3545' // danger
    default: return '#6c757d'
  }
}

const submitWorkflow = async (action) => {
  if (!props.id) {
    alert('请先保存记录')
    return
  }

  const rawUser = localStorage.getItem('userInfo')
  const user = rawUser ? JSON.parse(rawUser) : null
  const currentAccount = user && (user.username || user.userAccount || user.userName)
  const currentName = user && (user.fullName || user.userName || currentAccount)
  if (!currentAccount) {
    alert('请先登录')
    return
  }

  let signatureData = null

  if (action === 'SUBMIT') {
    if (formData.recordTester && currentAccount !== formData.recordTester && currentName !== formData.recordTester) {
        alert('您不是该单据的检测人 (' + formData.recordTester + ')，无权提交')
        return
    }

    if (!formData.testerSignature) {
      alert('请先进行检测人签字')
      return
    }
    signatureData = formData.testerSignature.replace(/^data:image\/\w+;base64,/, '')
  } else if (action === 'AUDIT_PASS' || (action === 'REJECT' && formData.status === 1)) {
    if (formData.recordReviewer && currentAccount !== formData.recordReviewer && currentName !== formData.recordReviewer) {
         alert('您不是该单据的复核人 (' + formData.recordReviewer + ')，无权操作')
         return
    }
    
    if (action === 'AUDIT_PASS') {
        if (!formData.reviewerSignature) {
            try {
                const sigRes = await axios.post('/api/signature/get', { userAccount: currentAccount })
                if (sigRes.data.success && sigRes.data.data && sigRes.data.data.signatureBlob) {
                     formData.reviewerSignature = `data:image/png;base64,${sigRes.data.data.signatureBlob}`
                     if (!formData.recordReviewer) {
                        formData.recordReviewer = currentName
                     }
                } else {
                     alert('未找到您的电子签名，无法自动签名')
                     return
                }
            } catch (e) {
                console.error('Auto sign error', e)
                alert('自动签名失败')
                return
            }
        }
        signatureData = formData.reviewerSignature.replace(/^data:image\/\w+;base64,/, '')
    }
  } else if (action === 'SIGN_REVIEW') {
    if (formData.recordReviewer && currentAccount !== formData.recordReviewer && currentName !== formData.recordReviewer) {
        alert('您不是该单据的复核人 (' + formData.recordReviewer + ')，无权签字')
        return
    }

    if (!formData.reviewerSignature) {
        try {
            const sigRes = await axios.post('/api/signature/get', { userAccount: currentAccount })
            if (sigRes.data.success && sigRes.data.data && sigRes.data.data.signatureBlob) {
                    formData.reviewerSignature = `data:image/png;base64,${sigRes.data.data.signatureBlob}`
                    if (!formData.recordReviewer) {
                    formData.recordReviewer = currentName
                    }
            } else {
                    alert('未找到您的电子签名，无法自动签名')
                    return
            }
        } catch (e) {
            console.error('Auto sign error', e)
            alert('自动签名失败')
            return
        }
    }
    signatureData = formData.reviewerSignature.replace(/^data:image\/\w+;base64,/, '')
  } else if (action === 'SIGN_APPROVE' || (action === 'REJECT' && formData.status === 4)) {
    if (formData.approver && currentAccount !== formData.approver && currentName !== formData.approver) {
         alert('您不是该单据的批准人 (' + formData.approver + ')，无权操作')
         return
    }

    if (action === 'SIGN_APPROVE') {
        if (!formData.approverSignature) {
            try {
                const sigRes = await axios.post('/api/signature/get', { userAccount: currentAccount })
                if (sigRes.data.success && sigRes.data.data && sigRes.data.data.signatureBlob) {
                        formData.approverSignature = `data:image/png;base64,${sigRes.data.data.signatureBlob}`
                        if (!formData.approver) {
                        formData.approver = currentName
                        }
                } else {
                        alert('未找到您的电子签名，无法自动签名')
                        return
                }
            } catch (e) {
                console.error('Auto sign error', e)
                alert('自动签名失败')
                return
            }
        }
        signatureData = formData.approverSignature.replace(/^data:image\/\w+;base64,/, '')
    }
  }

  const request = {
    tableType: 'LIGHT_DYNAMIC_PENETRATION',
    recordId: props.id,
    action: action,
    userAccount: currentAccount,
    signatureData: signatureData,
    nextHandler: ''
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
      loadData()
    } else {
      alert('操作失败: ' + response.data.message)
    }
  } catch (e) {
    console.error('Workflow error', e)
    alert('操作异常')
  }
}

const loadData = async () => {
  if (!props.id) return
  try {
    let data = null;
    let ent = null;

    try {
      const entRes = await axios.get(`/api/jc-core-wt-info/by-id?id=${props.id}`)
      if (entRes.data && entRes.data.success && entRes.data.data) {
        ent = entRes.data.data
        currentEntrustmentId.value = ent.id
        const repRes = await axios.get('/api/light-dynamic-penetration/report/get-by-entrustment-id', { params: { entrustmentId: ent.id } })
        if (repRes.data && repRes.data.success && repRes.data.data) {
          data = repRes.data.data
        }
      }
    } catch (e) {}

    if (!data) {
      const res = await axios.get(`/api/light-dynamic-penetration/${props.id}`)
      if (res.data.success && res.data.data) data = res.data.data
    }

    if (data) {
      formData.entrustingUnit = data.clientUnit || (ent && (ent.client || ent.clientUnit)) || "";
      formData.unifiedNumber = data.wtNum || (ent && ent.wtNum) || "";
      formData.projectName = data.projectName || (ent && ent.projectName) || "";
      formData.entrustDate = formatDate(data.commissionDate || (ent && (ent.commissionDate || ent.wtDate)));
      formData.constructionPart = data.constructionPart || (ent && ent.constructionPart) || "";
      formData.soilProperty = data.soilProperty || "";
      formData.reportDate = formatDate(data.reportDate);
      formData.witnessUnit = data.witnessUnit || (ent && (ent.witnessUnit || ent.supervisionUnit)) || "";
      formData.witness = data.witness || (ent && ent.witness) || "";
      formData.designCapacity = data.designCapacity || "";
      formData.hammerWeight = data.hammerWeight || "";
      formData.dropDistance = data.dropDistance || "";
      formData.testCategory = data.testCategory || (ent && ent.testCategory) || "";
      formData.testBasis = data.testBasis || "";
      formData.equipment = data.equipment || "";
      formData.remarks = data.remarks || "";
      formData.status = (data.status !== undefined && data.status !== null) ? data.status : (ent && ent.status !== undefined ? ent.status : 0);

      
      // Load directory for fallback
      // const directory = JSON.parse(localStorage.getItem('currentDirectory') || '{}')
      
      // Filler - Priority: record.filler
      formData.filler = data.filler || ''
      
      // Record Tester - Priority: record.recordTester -> record.tester (legacy)
      formData.recordTester = data.recordTester || data.tester || ''
      
      // Record Reviewer - Priority: record.recordReviewer -> record.reviewer (legacy)
      formData.recordReviewer = data.recordReviewer || data.reviewer || ''
      
      // Approver - Priority: record.approver
      formData.approver = data.approver || ''

      // formData.approve = data.approver || ''
      // formData.review = data.reviewer || ''
      // formData.inspect = data.tester || ''
      formData.approverSignature = normalizeSignatureSrc(data.approveSignaturePhoto)
      formData.reviewerSignature = normalizeSignatureSrc(data.reviewSignaturePhoto)
      formData.testerSignature = normalizeSignatureSrc(data.inspectSignaturePhoto)
      formData.conclusion = data.conclusion || ''
      formData.status = data.status !== undefined ? data.status : 0
      formData.rejectReason = data.rejectReason || ''
      formData.companyName = '河北金涛建设工程质量检测有限公司'
      formData.companyAddress = '石家庄高新区方亿科技工业园A区第2号楼。'
      formData.companyPhone = '0311—86107634  0311—67300616'

      // 1. Fallback: If basic info is missing, fetch from Entrustment (JC_CORE_WT_INFO)
      if (!formData.projectName || !formData.entrustingUnit || !data.entrustmentId) {
          let ent = null
          try {
              // Try by entrustmentId if available
              if (data.entrustmentId) {
                  const entRes = await axios.get(`/api/jc-core-wt-info/by-id?id=${data.entrustmentId}`)
                  if (entRes.data.success) ent = entRes.data.data
              } 
              // If not found or no ID, try by wtNum
              if (!ent && formData.unifiedNumber) {
                  const entRes = await axios.get(`/api/jc-core-wt-info/by-wt-num?wtNum=${encodeURIComponent(formData.unifiedNumber)}`)
                  if (entRes.data.success) ent = entRes.data.data
              }

              if (ent) {
                  if (!formData.entrustingUnit) formData.entrustingUnit = ent.client || ent.clientUnit || ''
                  if (!formData.projectName) formData.projectName = ent.projectName || ''
                  if (!formData.entrustDate) formData.entrustDate = formatDate(ent.commissionDate || ent.wtDate)
                  if (!formData.constructionPart) formData.constructionPart = ent.constructionPart || ''
                  if (!formData.testCategory) formData.testCategory = ent.testCategory || ''
                  if (!formData.witnessUnit) formData.witnessUnit = ent.witnessUnit || ent.supervisionUnit || ''
                  if (!formData.witness) formData.witness = ent.witness || ''
                  
                  // Critical: Update entrustmentId if missing, so we can find the Record
                  if (!data.entrustmentId) {
                      data.entrustmentId = ent.id
                      currentEntrustmentId.value = ent.id
                  }
              }
          } catch (e) {
              console.warn('Failed to fetch entrustment info', e)
          }
      }

      // Ensure currentEntrustmentId is set if data has it
      if (data.entrustmentId && !currentEntrustmentId.value) {
          currentEntrustmentId.value = data.entrustmentId
      }

      let sourceJson = data.dataJson
      let isDataLoaded = false

      // 1. Try to parse Report's own dataJson
      if (sourceJson) {
        try {
          const json = JSON.parse(sourceJson)
          fullDataJson.value = json
          if (json.testDate) formData.testDate = json.testDate
          
          // Map fields from JSON
          if (json.soilProperty) formData.soilProperty = json.soilProperty
          if (json.soilProperties && !formData.soilProperty) formData.soilProperty = json.soilProperties
          if (json.designCapacity) formData.designCapacity = json.designCapacity
          if (json.hammerWeight) formData.hammerWeight = json.hammerWeight
          if (json.dropDistance) formData.dropDistance = json.dropDistance
          if (json.testCategory) formData.testCategory = json.testCategory
          if (json.testBasis) formData.testBasis = json.testBasis
          if (json.equipment) formData.equipment = json.equipment
          if (json.conclusion) formData.conclusion = json.conclusion
          if (json.remarks) formData.remarks = json.remarks
          if (json.constructionPart) formData.constructionPart = json.constructionPart
          if (json.projectName) formData.projectName = json.projectName
          if (json.clientUnit) formData.entrustingUnit = json.clientUnit
          if (json.witnessUnit) formData.witnessUnit = json.witnessUnit
          if (json.witness) formData.witness = json.witness
          if (json.commissionDate) formData.entrustDate = formatDate(json.commissionDate)

          // Detect format
          const keys = Object.keys(json)
          const depthKeys = keys.filter(k => k.startsWith('depth_L_'))
          const maxIdx = depthKeys.length > 0 ? Math.max(...depthKeys.map(k => parseInt(k.split('_')[2]))) : -1
          
          // Check if we actually have data
          if (maxIdx >= 0) {
              let is6Row = json.format === '6-row';
              if (!is6Row && json.format !== '5-row') {
                  // Heuristic: Record (5-row) usually fills 0-4, then 5-9. Index 5 has data (Block 1 Row 0).
                  // Report (6-row) maps 0-4 -> 0-4, 5-9 -> 6-10. Index 5 is usually empty (Block 0 Row 5).
                  
                  // If we see data at index 5, it's likely 5-row format (because that's the start of Block 1).
                  // If we see data at index 6 but NOT index 5, it's likely 6-row format.
                  const hasIdx5 = json['depth_L_5'] !== undefined && json['depth_L_5'] !== '';
                  const hasIdx6 = json['depth_L_6'] !== undefined && json['depth_L_6'] !== '';
                  const hasPos1 = json['pos_L_1'] !== undefined && json['pos_L_1'] !== '';
                  
                  if ((!hasIdx5 && hasIdx6) || maxIdx > 9) {
                      is6Row = true;
                  } else if (hasIdx5 && !hasPos1) {
                      // Data at index 5, but no Block 1 header. Likely Block 0 Row 5 (6-row format).
                      is6Row = true;
                  }
              }
              
              for (let b = 0; b < 2; b++) {
                // Ensure block exists
                if (!formData.dataBlocks[b]) {
                    formData.dataBlocks[b] = { pos_L: '', depths: Array.from({ length: 7 }, () => ({ depth_L: '', actual_L: '' })), avg_L: '', capacity_L: '', pos_R: '', depths_R: Array.from({ length: 7 }, () => ({ depth_R: '', actual_R: '' })), avg_R: '', capacity_R: '' }
                }
                formData.dataBlocks[b].pos_L = json[`pos_L_${b}`] || ''
                formData.dataBlocks[b].avg_L = json[`avg_L_${b}`] || ''
                formData.dataBlocks[b].capacity_L = json[`capacity_L_${b}`] || ''

                formData.dataBlocks[b].pos_R = json[`pos_R_${b}`] || ''
                formData.dataBlocks[b].avg_R = json[`avg_R_${b}`] || ''
                formData.dataBlocks[b].capacity_R = json[`capacity_R_${b}`] || ''

                if (is6Row) {
                  for (let s = 0; s < 6; s++) {
                    const idx = b * 6 + s
                    formData.dataBlocks[b].depths[s].depth_L = json[`depth_L_${idx}`] || ''
                    formData.dataBlocks[b].depths[s].actual_L = json[`actual_L_${idx}`] || ''

                    formData.dataBlocks[b].depths_R[s].depth_R = json[`depth_R_${idx}`] || ''
                    formData.dataBlocks[b].depths_R[s].actual_R = json[`actual_R_${idx}`] || ''
                  }
                } else {
                  // 5-row logic conversion to 6-row UI
                  // 5-row: Block 0 (0-4), Block 1 (5-9)
                  // 6-row: Block 0 (0-4), Block 1 (6-10)
                  for (let s = 0; s < 5; s++) {
                    const sourceIdx = b * 5 + s
                    
                    formData.dataBlocks[b].depths[s].depth_L = json[`depth_L_${sourceIdx}`] || ''
                    formData.dataBlocks[b].depths[s].actual_L = json[`actual_L_${sourceIdx}`] || ''

                    formData.dataBlocks[b].depths_R[s].depth_R = json[`depth_R_${sourceIdx}`] || ''
                    formData.dataBlocks[b].depths_R[s].actual_R = json[`actual_R_${sourceIdx}`] || ''
                  }
                }
              }
              isDataLoaded = true
          }
        } catch (e) {
          console.error('JSON parse error', e)
        }
      }

      // 2. Fallback: only fetch from Record (report precedes result in flow)
      // 轻型动力触探报告表只从记录表第一页（索引0）获取数据
      if (!isDataLoaded || (fullDataJson.value && Object.keys(fullDataJson.value).length < 5)) {
        const entrustmentId = currentEntrustmentId.value || data.entrustmentId;
        
        if (entrustmentId) {
          try {
            let fallbackData = null;
            const recordRes = await axios.get('/api/light-dynamic-penetration/get-by-entrustment-id', {
              params: { entrustmentId: entrustmentId }
            })
            if (recordRes.data.success && recordRes.data.data && recordRes.data.data.length > 0) {
              const records = recordRes.data.data;
              // 检查记录表状态，只有审核通过(状态值5)才自动填充数据
              const approvedRecords = records.filter(r => r.status === 5);
              // 报告表只从记录表第一页（索引0）获取数据
              if (approvedRecords.length > 0) {
                fallbackData = approvedRecords[0];
              } else {
                console.log('记录表状态未审核通过，不自动填充数据');
              }
            }


          if (fallbackData) {
               // Map entity fields if empty in report
               if (!formData.soilProperty && fallbackData.soilProperty) formData.soilProperty = fallbackData.soilProperty
               // Also check for soilProperties (plural) which might be in the entity or JSON
               if (!formData.soilProperty && fallbackData.soilProperties) formData.soilProperty = fallbackData.soilProperties

               if (!formData.testCategory && fallbackData.testCategory) formData.testCategory = fallbackData.testCategory
               if (!formData.designCapacity && fallbackData.designCapacity) formData.designCapacity = fallbackData.designCapacity
               if (!formData.hammerWeight && fallbackData.hammerWeight) formData.hammerWeight = fallbackData.hammerWeight
               if (!formData.dropDistance && fallbackData.dropDistance) formData.dropDistance = fallbackData.dropDistance
               if (!formData.testBasis && fallbackData.testBasis) formData.testBasis = fallbackData.testBasis
               if (!formData.equipment && fallbackData.equipment) formData.equipment = fallbackData.equipment
               
               if (!formData.testDate && fallbackData.testDate) formData.testDate = formatDate(fallbackData.testDate)

               if (!formData.recordTester && (fallbackData.recordTester || fallbackData.tester)) formData.recordTester = fallbackData.recordTester || fallbackData.tester
               if (!formData.recordReviewer && (fallbackData.recordReviewer || fallbackData.reviewer)) formData.recordReviewer = fallbackData.recordReviewer || fallbackData.reviewer
               if (!formData.testerSignature && fallbackData.inspectSignaturePhoto) formData.testerSignature = normalizeSignatureSrc(fallbackData.inspectSignaturePhoto)
               if (!formData.reviewerSignature && fallbackData.reviewSignaturePhoto) formData.reviewerSignature = normalizeSignatureSrc(fallbackData.reviewSignaturePhoto)
          }

          if (fallbackData && fallbackData.dataJson) {
               try {
                   const recordJson = JSON.parse(fallbackData.dataJson)
                   fullDataJson.value = recordJson
                   
                   // Map specific fields from JSON if not already set
                   if (!formData.soilProperty && recordJson.soilProperties) formData.soilProperty = recordJson.soilProperties
                   if (!formData.soilProperty && recordJson.soilProperty) formData.soilProperty = recordJson.soilProperty
                   

                   let isFallback6Row = false;

                   // Map Data Blocks
                   for (let b = 0; b < 2; b++) {
                      // Ensure block exists
                      if (!formData.dataBlocks[b]) {
                          formData.dataBlocks[b] = { pos_L: '', depths: Array.from({ length: 7 }, () => ({ depth_L: '', actual_L: '' })), avg_L: '', capacity_L: '', pos_R: '', depths_R: Array.from({ length: 7 }, () => ({ depth_R: '', actual_R: '' })), avg_R: '', capacity_R: '' }
                      }
                      // Map Headers
                      if (recordJson[`pos_L_${b}`]) formData.dataBlocks[b].pos_L = recordJson[`pos_L_${b}`]
                      if (recordJson[`avg_L_${b}`]) formData.dataBlocks[b].avg_L = recordJson[`avg_L_${b}`]
                      if (recordJson[`capacity_L_${b}`]) formData.dataBlocks[b].capacity_L = recordJson[`capacity_L_${b}`]
                      
                      if (recordJson[`pos_R_${b}`]) formData.dataBlocks[b].pos_R = recordJson[`pos_R_${b}`]
                      if (recordJson[`avg_R_${b}`]) formData.dataBlocks[b].avg_R = recordJson[`avg_R_${b}`]
                      if (recordJson[`capacity_R_${b}`]) formData.dataBlocks[b].capacity_R = recordJson[`capacity_R_${b}`]

                      // Map Rows
                      if (isFallback6Row) {
                          for (let s = 0; s < 6; s++) {
                            const idx = b * 6 + s
                            if (recordJson[`depth_L_${idx}`]) formData.dataBlocks[b].depths[s].depth_L = recordJson[`depth_L_${idx}`]
                            if (recordJson[`actual_L_${idx}`]) formData.dataBlocks[b].depths[s].actual_L = recordJson[`actual_L_${idx}`]
                            
                            if (recordJson[`depth_R_${idx}`]) formData.dataBlocks[b].depths_R[s].depth_R = recordJson[`depth_R_${idx}`]
                            if (recordJson[`actual_R_${idx}`]) formData.dataBlocks[b].depths_R[s].actual_R = recordJson[`actual_R_${idx}`]
                          }
                      } else {
                          // 5-row fallback -> 6-row UI
                          for (let s = 0; s < 5; s++) {
                              const rIdx = b * 5 + s
                              
                              if (recordJson[`depth_L_${rIdx}`]) formData.dataBlocks[b].depths[s].depth_L = recordJson[`depth_L_${rIdx}`]
                              if (recordJson[`actual_L_${rIdx}`]) formData.dataBlocks[b].depths[s].actual_L = recordJson[`actual_L_${rIdx}`]
                              
                              if (recordJson[`depth_R_${rIdx}`]) formData.dataBlocks[b].depths_R[s].depth_R = recordJson[`depth_R_${rIdx}`]
                              if (recordJson[`actual_R_${rIdx}`]) formData.dataBlocks[b].depths_R[s].actual_R = recordJson[`actual_R_${rIdx}`]
                          }
                      }
                   }
               } catch (e) {
                   console.error('Error parsing fallback json', e)
               }
            }
        } catch (e) {
          console.error('light dynamic report autofill error', e)
        }
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

const handleSign = async () => {
  const rawUser = localStorage.getItem('userInfo')
  const user = rawUser ? JSON.parse(rawUser) : null
  const currentAccount = user && (user.username || user.userAccount || user.userName)
  const currentName = user && (user.fullName || user.userName || currentAccount)
  if (!currentAccount) {
    alert('请先登录')
    return
  }

  try {
    const response = await axios.post('/api/signature/get', {
      userAccount: currentAccount
    })

    if (response.data.success && response.data.data && response.data.data.signatureBlob) {
      const signatureBlob = response.data.data.signatureBlob
      let imgSrc = ''
      
      if (typeof signatureBlob === 'string') {
        imgSrc = `data:image/png;base64,${signatureBlob}`
      } else {
        alert('签名数据格式不支持')
        return
      }

      let signed = false
      if (!formData.recordTester || formData.recordTester === currentAccount || formData.recordTester === currentName) {
        if (!formData.recordTester) formData.recordTester = currentName
        formData.testerSignature = imgSrc
        signed = true
      }
      
      if (signed) {
        alert('签名成功')
      } else {
        alert(`当前用户(${currentName})与表单中的检测人员(${formData.recordTester})不匹配，无法签名`)
      }
    } else {
      alert('未找到您的电子签名，请先去“电子签名”页面设置')
    }
  } catch (error) {
    console.error('Sign error:', error)
    alert('签名失败')
  }
}

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

const submitForm = async () => {
  try {
    // 获取当前登录用户信息
    const userInfoStr = localStorage.getItem('userInfo');
    const userInfo = userInfoStr ? JSON.parse(userInfoStr) : {};
    
    // Always use 6-row format for Report Table to avoid index ambiguity and data loss
    const use6Row = true;

    // Convert dataBlocks to flat keys, starting with preserved full data
    const dynamicData = { ...fullDataJson.value, format: '6-row' }
    if (formData.testDate) dynamicData.testDate = formData.testDate
    
    for (let b = 0; b < 2; b++) {
        const block = formData.dataBlocks[b]
        if (!block) continue; // Skip if block doesn't exist (shouldn't happen with 2 initialized)
        if (block.pos_L) dynamicData[`pos_L_${b}`] = block.pos_L
        if (block.avg_L) dynamicData[`avg_L_${b}`] = block.avg_L
        if (block.capacity_L) dynamicData[`capacity_L_${b}`] = block.capacity_L
        
        if (block.pos_R) dynamicData[`pos_R_${b}`] = block.pos_R
        if (block.avg_R) dynamicData[`avg_R_${b}`] = block.avg_R
        if (block.capacity_R) dynamicData[`capacity_R_${b}`] = block.capacity_R

        // Always loop 7 rows to save all data
        for (let s = 0; s < 7; s++) {
            const idx = b * 7 + s;
            
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
      approver: formData.approver,
      // reviewer: formData.reviewer, // Keep legacy fields for compatibility if needed
      // tester: formData.tester, // Keep legacy fields for compatibility if needed
      recordTester: formData.recordTester,
      recordReviewer: formData.recordReviewer,
      filler: formData.filler,
      tester: formData.recordTester, // Keep legacy fields for compatibility
      reviewer: formData.recordReviewer, // Keep legacy fields for compatibility
      approveSignaturePhoto: formData.approverSignature,
      reviewSignaturePhoto: formData.reviewerSignature,
      inspectSignaturePhoto: formData.testerSignature,
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
            margin-left: 8px;
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
