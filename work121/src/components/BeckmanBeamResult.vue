<template>
  <div class="beckmanBeamResult-container">

    <div class="no-print toolbar">
      <div class="toolbar-left">
        <button @click="goToList" class="link-button">&lt; 返回列表</button>
        <div v-if="formData.status !== undefined" class="status-text">
          状态:
          <span :style="{ color: getStatusColor(formData.status) }" class="status-label">
            {{ getStatusText(formData.status) }}
          </span>
        </div>
      </div>
      <div class="toolbar-right">
        <template v-if="props.id">
          <button
            v-if="formData.status === 0 || formData.status === 2"
            @click="submitWorkflow('SUBMIT')"
            class="btn btn-primary btn-small"
          >
            提交审核
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
            打回
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
            @click="submitWorkflow('SIGN_APPROVE')"
            class="btn btn-primary btn-small"
          >
            批准签字
          </button>
          <button
            v-if="formData.status === 4"
            @click="submitWorkflow('REJECT')"
            class="btn btn-danger btn-small"
          >
            打回
          </button>
        </template>

        <button
          @click="handleSign"
          class="btn btn-secondary btn-small"
        >
          签字
        </button>
        <button
          @click="saveData"
          class="btn btn-secondary btn-small"
        >
          保存
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
    
    <div
      v-if="formData.status === 2 && formData.rejectReason"
      style="background-color: #ffebee; color: #c62828; padding: 10px; border-radius: 4px; margin-top: 10px; border: 1px solid #ef9a9a; clear: both;"
    >
      <strong>打回原因：</strong> {{ formData.rejectReason }}
    </div>
    <form id="pdfForm" ref="pdfForm" method="post">
        <h2>路基路面回弹弯沉(回弹模量) 检测结果</h2>
        
        <div class="header-info">
            <span>委托单位：<input type="text" v-model="formData.entrustingUnit"   name="entrustingUnit" style="width: 200px; text-align: left; border-bottom: 1px solid #ccc;" placeholder=""></span>
            <span>统一编号：<input type="text" v-model="formData.unifiedNumber"   name="unifiedNumber" style="width: 150px; text-align: left; border-bottom: 1px solid #ccc;" placeholder=""></span>
        </div>

        <table>
            <colgroup>
                <col style="width: 8%;"> <!-- 序号 -->
                <col style="width: 22%;"> <!-- 测点桩号（幅段） -->
                <col style="width: 15%;"> <!-- 车道 -->
                <col style="width: 20%;"> <!-- 左侧回弹弯沉值 -->
                <col style="width: 20%;"> <!-- 右侧回弹弯沉值 -->
                <col style="width: 15%;"> <!-- 备注 -->
            </colgroup>
            <thead>
                <tr>
                    <th>序号</th>
                    <th>测点桩号（幅段）</th>
                    <th>车道</th>
                    <th>左侧回弹弯沉值<br>(0.01mm)</th>
                    <th>右侧回弹弯沉值<br>(0.01mm)</th>
                    <th>备注</th>
                </tr>
            </thead>
            <tbody>
                <template v-for="(n, i_idx) in 25" :key="i_idx">
                <tr>
                    <td><input type="text" :name="'seq_' + (i_idx + 1)" v-model="formData['seq_' + (i_idx + 1)]"></td>
                    <td><input type="text" :name="'station_' + (i_idx + 1)" v-model="formData['station_' + (i_idx + 1)]"></td>
                    <td><input type="text" :name="'lane_' + (i_idx + 1)" v-model="formData['lane_' + (i_idx + 1)]"></td>
                    <td><input type="text" :name="'left_val_' + (i_idx + 1)" v-model="formData['left_val_' + (i_idx + 1)]"></td>
                    <td><input type="text" :name="'right_val_' + (i_idx + 1)" v-model="formData['right_val_' + (i_idx + 1)]"></td>
                    <td><input type="text" :name="'remark_' + (i_idx + 1)" v-model="formData['remark_' + (i_idx + 1)]"></td>
                </tr>
                </template>
            </tbody>
        </table>

    <div class="footer-info">
            <div class="signature-box">
                批准：
                <span class="signature-line"></span>
                <img v-if="formData.approverSignature" :src="formData.approverSignature" class="signature-img" alt="批准人签名" />
            </div>
            <div class="signature-box">
                审核：
                <span class="signature-line"></span>
                <img v-if="formData.reviewerSignature" :src="formData.reviewerSignature" class="signature-img" alt="审核人签名" />
            </div>
            <div class="signature-box">
                检测：
                <span class="signature-line"></span>
                <img v-if="formData.testerSignature" :src="formData.testerSignature" class="signature-img" alt="检测人签名" />
            </div>
        </div>

    </form>

    

  </div>
</template>

<script setup>
import { reactive, ref, onMounted, inject, defineProps } from 'vue'
import axios from 'axios'

const props = defineProps({
    id: String
})

const navigateTo = inject('navigateTo')

const goToList = () => {
  if (navigateTo) {
    navigateTo('BeckmanBeamResultList')
  }
}

const pdfForm = ref(null)

const formData = reactive({
  id: '',
  entrustmentId: '',
  entrustingUnit: '',
  unifiedNumber: '',
  approver: '',
  recordReviewer: '',
  recordTester: '',
  filler: '',
  approverSignature: '',
  reviewerSignature: '',
  testerSignature: '',
  status: 0,
  rejectReason: ''
})

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
    if (!formData.id) {
        alert('请先保存记录')
        return
    }
    
    const user = JSON.parse(localStorage.getItem('userInfo'))
    if (!user || !user.username) {
        alert('请先登录')
        return
    }

    let signatureData = null
    
    if (action === 'SUBMIT') {
        // Role check: Only recordTester can submit
        if (formData.recordTester && user.username !== formData.recordTester && user.fullName !== formData.recordTester) {
             alert('您不是该单据的检测人 (' + formData.recordTester + ')，无权提交')
             return
        }
        if (!formData.testerSignature) {
            alert('请先签字')
            return
        }
    } else if (action === 'AUDIT_PASS') {
        // Role check: Only reviewer can audit
        if (formData.recordReviewer && user.username !== formData.recordReviewer && user.fullName !== formData.recordReviewer) {
            alert('您不是该单据的复核人 (' + formData.recordReviewer + ')，无权操作')
            return
        }
        // Auto sign Reviewer
        if (!formData.reviewerSignature) {
             try {
                const sigRes = await axios.post('/api/signature/get', { userAccount: user.username })
                if (sigRes.data.success && sigRes.data.data && sigRes.data.data.signatureBlob) {
                     formData.reviewerSignature = `data:image/png;base64,${sigRes.data.data.signatureBlob}`
                     if (!formData.recordReviewer) {
                        formData.recordReviewer = user.fullName || user.username
                     }
                }
             } catch (e) {
                console.error('Auto sign error', e)
             }
        }
        if (formData.reviewerSignature) {
            signatureData = formData.reviewerSignature.replace(/^data:image\/\w+;base64,/, '')
        }
    } else if (action === 'SIGN_APPROVE') {
         // Role check: Only approver can approve
         if (formData.approver && user.username !== formData.approver && user.fullName !== formData.approver) {
            alert('您不是该单据的批准人 (' + formData.approver + ')，无权操作')
            return
         }
         if (!formData.approverSignature) {
             try {
                const sigRes = await axios.post('/api/signature/get', { userAccount: user.username })
                if (sigRes.data.success && sigRes.data.data && sigRes.data.data.signatureBlob) {
                     formData.approverSignature = `data:image/png;base64,${sigRes.data.data.signatureBlob}`
                     if (!formData.approver) {
                        formData.approver = user.fullName || user.username
                     }
                }
             } catch (e) {
                console.error('Auto sign error', e)
             }
         }
         if (formData.approverSignature) {
            signatureData = formData.approverSignature.replace(/^data:image\/\w+;base64,/, '')
         }
    }

    try {
        const payload = {
            recordId: formData.id,
            action: action,
            operatorName: user.fullName || user.username,
            operatorRole: action === 'SUBMIT' ? 'tester' : (action === 'AUDIT_PASS' ? 'reviewer' : 'approver'),
            comment: action === 'REJECT' ? prompt('请输入打回原因') : '',
            signatureData: signatureData,
            recordType: 'beckman_beam_result' // Ensure backend supports this
        }

        if (action === 'REJECT' && !payload.comment) {
            return
        }

        const response = await axios.post('/api/workflow/handle', payload)
        if (response.data.success) {
            alert('操作成功')
            // Refresh data
            loadData(formData.entrustmentId || props.id)
        } else {
            alert('操作失败: ' + response.data.message)
        }
    } catch (error) {
        console.error('Workflow error:', error)
        alert('操作失败')
    }
}

onMounted(() => {
  if (props.id) {
    loadData(props.id)
  }

  for (let i_idx = 0; i_idx < 25; i_idx++) {
    formData['remark_' + i_idx] = ''
    formData['lane_' + i_idx] = ''
    formData['right_val_' + i_idx] = ''
    formData['seq_' + i_idx] = ''
    formData['station_' + i_idx] = ''
    formData['left_val_' + i_idx] = ''
  }
})

const loadData = async (entrustmentId) => {
  try {
    const entrustResponse = await axios.get('/api/jc-core-wt-info/by-id', {
      params: { id: entrustmentId }
    })
    if (entrustResponse.data.success) {
      const ent = entrustResponse.data.data
      formData.entrustmentId = entrustmentId
      formData.entrustingUnit = ent.clientUnit
      formData.unifiedNumber = ent.wtNum
      // Don't set tester/reviewer here
    }

    const response = await axios.get('/api/beckman-beam/result/get-by-entrustment-id', {
      params: { entrustmentId }
    })

    let sourceJson = null
    let dbData = {}

    if (response.data.success && response.data.data) {
      const data = response.data.data
      dbData = data
      formData.id = data.id
      formData.entrustmentId = data.entrustmentId
      if (data.status !== undefined) formData.status = data.status
      if (data.rejectReason) formData.rejectReason = data.rejectReason

      if (data.dataJson) {
        sourceJson = data.dataJson
      }

      if (data.reviewSignaturePhoto) formData.reviewerSignature = data.reviewSignaturePhoto
      if (data.inspectSignaturePhoto) formData.testerSignature = data.inspectSignaturePhoto
      if (data.approveSignaturePhoto) formData.approverSignature = data.approveSignaturePhoto
    }

    if (!sourceJson) {
      try {
        const recordRes = await axios.get('/api/beckman-beam/get-by-entrustment-id', {
          params: { entrustmentId }
        })
        if (recordRes.data.success && recordRes.data.data && recordRes.data.data.length > 0) {
          const record = recordRes.data.data[0]
          // 检查记录表状态，只有审核通过(状态值5)才自动填充数据
          if (record.status === 5) {
            if (record.dataJson) {
              sourceJson = record.dataJson
            }
          } else {
            console.log('记录表状态未审核通过，不自动填充数据')
          }
        }
      } catch (e) {
        console.error('beckman result autofill error', e)
      }
    }

    if (sourceJson) {
      try {
        const parsedData = JSON.parse(sourceJson)
        // Map legacy fields
        if (parsedData.tester && !parsedData.recordTester) parsedData.recordTester = parsedData.tester
        if (parsedData.reviewer && !parsedData.recordReviewer) parsedData.recordReviewer = parsedData.reviewer
        Object.assign(formData, parsedData)
      } catch (e) {
        console.error('beckman result autofill parse error', e)
      }
    }
    
    // Directory Fallback
    // const currentDirectory = JSON.parse(localStorage.getItem('currentDirectory') || '{}')
    const ent = entrustResponse.data.data || {}
    
    // Filler
    if (!formData.filler) {
        formData.filler = ''
    }
    
    // Record Tester
    if (!formData.recordTester) {
        formData.recordTester = ent.tester || ''
    }
    
    // Record Reviewer
    if (!formData.recordReviewer) {
        formData.recordReviewer = ent.reviewer || ''
    }
    
    // Approver
    if (!formData.approver) {
        formData.approver = ''
    }
    
    // Set filler to current user if new
    const user = JSON.parse(localStorage.getItem('userInfo') || '{}')
    if (user.username && !formData.filler) {
       formData.filler = user.username
    }

  } catch (error) {
    console.error('Failed to load data', error)
  }
}

const saveData = async () => {
    try {
        const dataJsonObj = { ...formData }
        // Remove legacy fields from JSON
        delete dataJsonObj.tester
        delete dataJsonObj.reviewer

        const dataToSave = {
            id: formData.id,
            entrustmentId: formData.entrustmentId || props.id,
            dataJson: JSON.stringify(dataJsonObj),
            reviewSignaturePhoto: formData.reviewerSignature,
            inspectSignaturePhoto: formData.testerSignature,
            approveSignaturePhoto: formData.approverSignature,
            recordTester: formData.recordTester,
            recordReviewer: formData.recordReviewer,
            // Sync legacy fields
            tester: formData.recordTester,
            reviewer: formData.recordReviewer,
            filler: formData.filler,
            approver: formData.approver
        }
        
        const response = await axios.post('/api/beckman-beam/result/save', dataToSave)
        if (response.data.success) {
            alert('保存成功')
            // If new record, update id
            if (!formData.id && response.data.data && response.data.data.id) {
                 formData.id = response.data.data.id
            }
        } else {
            alert('保存失败: ' + response.data.message)
        }
    } catch (error) {
        console.error('Save error:', error)
        alert('保存失败')
    }
}

const handleSign = async () => {
  const user = JSON.parse(localStorage.getItem('userInfo'))
  if (!user || !user.username) {
        alert('请先登录')
        return
    }

    try {
        const response = await axios.post('/api/signature/get', {
            userAccount: user.username
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
            const currentName = user.fullName || user.username
            const currentAccount = user.username

            // Match Tester
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
    pdfForm.value.action = '/api/pdf/beckman_beam_result/generate'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}

const previewPdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = '/api/pdf/beckman_beam_result/preview'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}
</script>

<style scoped>
        /* Signature styles */
.signature-box {
  position: relative;
  display: inline-block;
  min-width: 120px;
  vertical-align: bottom;
}
.signature-line {
  display: inline-block;
  min-width: 80px;
  border-bottom: 1px solid black;
  text-align: center;
  padding: 0 5px;
  height: 20px;
  line-height: 20px;
}
.signature-img {
  position: absolute;
  left: 40px;
  top: -20px;
  width: 80px;
  height: 40px;
  mix-blend-mode: multiply;
}
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

        .beckmanBeamResult-container {
            font-family: "SimSun", "Songti SC", serif;
            width: 210mm;
            margin: 0 auto;
            padding: 20px;
            background-color: white;
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
            font-size: 14px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 10px;
            table-layout: fixed; /* Fixed layout for consistent column widths */
        }
        th, td {
            border: 1px solid black;
            padding: 5px;
            text-align: center;
            font-size: 14px;
            height: 30px; /* Consistent row height */
            overflow: hidden;
        }
        input[type="text"] {
            width: 95%;
            border: none;
            outline: none;
            text-align: center;
            font-family: inherit;
            font-size: inherit;
            background: transparent;
        }
        .footer-info {
            display: flex;
            justify-content: space-between;
            margin-top: 20px;
            font-size: 14px;
            width: 100%;
        }
        
        @media print {
            .beckmanBeamResult-container {
                width: 100%;
                margin: 0;
                padding: 0;
            }
            .no-print {
                display: none;
            }
            @page {
                size: A4 portrait;
                margin: 1cm;
            }
        }
    
</style>
