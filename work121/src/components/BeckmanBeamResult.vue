<template>
  <div class="beckmanBeamResult-container">

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
            <span>统一编号：<input type="text" v-model="formData.unifiedNumber"   name="unifiedNumber" style="width: 150px; text-align: left; border-bottom: 1px solid #ccc;" placeholder="" disabled></span>
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
            const currentAccount = user.username
            const currentName = user.userName

            // Match Tester
            if (!formData.recordTester || formData.recordTester === currentAccount || formData.recordTester === currentName) {
                if (!formData.recordTester) formData.recordTester = currentName
                formData.testerSignature = imgSrc
                signed = true
            }

            if (signed) {
                // 保存签名到数据库
                await saveData()
                alert('签名成功并已保存')
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

const openClientPdfPreview = () => {
  if (!pdfForm.value) return
  const container = pdfForm.value.closest('.beckmanBeamResult-container')
  if (!container) return
  const mmToPx = (mm) => mm * 96 / 25.4
  const pageWidthMm = 210
  const pageHeightMm = 297
  const availableWidthPx = mmToPx(pageWidthMm)
  const availableHeightPx = mmToPx(pageHeightMm)
  const measureNode = container.cloneNode(true)
  measureNode.classList.add('pdf-preview')
  measureNode.querySelectorAll('.no-print').forEach(el => el.remove())
  measureNode.style.position = 'fixed'
  measureNode.style.left = '-100000px'
  measureNode.style.top = '0'
  measureNode.style.visibility = 'hidden'
  measureNode.style.width = `${pageWidthMm}mm`
  measureNode.style.height = 'auto'
  measureNode.style.maxHeight = 'none'
  measureNode.style.overflow = 'visible'
  measureNode.style.maxWidth = '100%'
  measureNode.style.minWidth = '0'
  measureNode.style.margin = '0'
  measureNode.style.padding = '0'
  measureNode.style.boxSizing = 'border-box'
  document.body.appendChild(measureNode)
  const rect = measureNode.getBoundingClientRect()
  const contentWidthPx = Math.max(measureNode.scrollWidth || 0, rect.width || 0, 1)
  const contentHeightPx = Math.max(measureNode.scrollHeight || 0, rect.height || 0, 1)
  measureNode.remove()
  const marginWantedMm = 2
  const marginWantedPx = mmToPx(marginWantedMm)
  const targetWidthPx = Math.max(availableWidthPx - marginWantedPx * 2, 1)
  const targetHeightPx = Math.max(availableHeightPx - marginWantedPx * 2, 1)
  const pdfScale = Math.min(targetWidthPx / contentWidthPx, targetHeightPx / contentHeightPx)
  const scaledWidthPx = contentWidthPx * pdfScale
  const scaledHeightPx = contentHeightPx * pdfScale
  const pdfOffsetXPx = Math.max(marginWantedPx, (availableWidthPx - scaledWidthPx) / 2)
  const pdfOffsetYPx = Math.max(marginWantedPx, (availableHeightPx - scaledHeightPx) / 2)
  const clone = container.cloneNode(true)
  clone.classList.add('pdf-preview')
  clone.querySelectorAll('.no-print').forEach(el => el.remove())
  const styleNodes = Array.from(document.querySelectorAll('link[rel="stylesheet"], style'))
  const stylesHtml = styleNodes.map(n => n.outerHTML).join('\n')
  const html = `<!doctype html>
<html lang="zh-CN">
  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    ${stylesHtml}
    <style>
      @page { size: A4 portrait; margin: 0; }
      html, body { margin: 0; padding: 0; background: #fff; width: 210mm; height: 297mm; overflow: hidden; }
      .pdf-sheet { width: 210mm; height: 297mm; padding: 0; box-sizing: border-box; overflow: hidden; }
      .pdf-page { width: 210mm; height: 297mm; overflow: hidden; box-sizing: border-box; position: relative; }
      .pdf-content { position: absolute; left: 0; top: 0; transform-origin: top left; }
      .pdf-preview.beckmanBeamResult-container { width: 100%; height: 100%; margin: 0; padding: 0; box-sizing: border-box; display: flex; flex-direction: column; }
      .pdf-preview { overflow: visible; }
      .pdf-preview * { page-break-inside: avoid; break-inside: avoid; }
      .pdf-preview #pdfForm { flex: 1; min-height: 0; display: flex; flex-direction: column; }
      .pdf-preview #pdfForm > table { flex: 0 0 auto; height: auto; margin-top: auto; margin-bottom: auto; }
    </style>
  </head>
  <body><div class="pdf-sheet"><div class="pdf-page"><div class="pdf-content" style="width: ${contentWidthPx}px; height: ${contentHeightPx}px; transform: translate(${pdfOffsetXPx}px, ${pdfOffsetYPx}px) scale(${pdfScale});">${clone.outerHTML}</div></div></div></body>
</html>`
  const w = window.open('', '_blank')
  if (!w) return
  w.document.open()
  w.document.write(html)
  w.document.close()
}
const openBackendPdfPreview = (actionUrl) => {
  if (!pdfForm.value) return
  const container = pdfForm.value.closest('.beckmanBeamResult-container')
  if (!container) return

  const escapeAttr = (v) => String(v ?? '')
    .replace(/&/g, '&amp;')
    .replace(/"/g, '&quot;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')

  const toBase64Utf8 = (text) => {
    const bytes = new TextEncoder().encode(text)
    let binary = ''
    for (let i = 0; i < bytes.length; i++) binary += String.fromCharCode(bytes[i])
    return btoa(binary)
  }

  const mmToPx = (mm) => mm * 96 / 25.4
  const pageWidthMm = 210
  const pageHeightMm = 297
  const marginMm = 6
  const availableWidthPx = mmToPx(pageWidthMm - marginMm * 2)
  const availableHeightPx = mmToPx(pageHeightMm - marginMm * 2)
  const measureNode = container.cloneNode(true)
  measureNode.classList.add('pdf-preview')
  measureNode.querySelectorAll('.no-print').forEach(el => el.remove())
  measureNode.style.position = 'fixed'
  measureNode.style.left = '-100000px'
  measureNode.style.top = '0'
  measureNode.style.visibility = 'hidden'
  measureNode.style.width = `${pageWidthMm}mm`
  measureNode.style.height = 'auto'
  measureNode.style.maxHeight = 'none'
  measureNode.style.overflow = 'visible'
  measureNode.style.maxWidth = '100%'
  measureNode.style.minWidth = '0'
  measureNode.style.margin = '0'
  measureNode.style.padding = '0'
  measureNode.style.boxSizing = 'border-box'
  document.body.appendChild(measureNode)
  const rect = measureNode.getBoundingClientRect()
  const contentWidthPx = Math.max(measureNode.scrollWidth || 0, rect.width || 0, 1)
  const contentHeightPx = Math.max(measureNode.scrollHeight || 0, rect.height || 0, 1)
  measureNode.remove()
  const pdfScale = Math.min(availableWidthPx / contentWidthPx, availableHeightPx / contentHeightPx)
  const scaledWidthPx = contentWidthPx * pdfScale
  const scaledHeightPx = contentHeightPx * pdfScale
  const pdfOffsetXPx = Math.max(0, (availableWidthPx - scaledWidthPx) / 2)
  const pdfOffsetYPx = Math.max(0, (availableHeightPx - scaledHeightPx) / 2)

  const buildHtmlSnapshotBase64 = () => {
    const clone = container.cloneNode(true)
    clone.classList.add('pdf-preview')
    clone.querySelectorAll('.no-print').forEach(el => el.remove())

    const originalFields = container.querySelectorAll('input, textarea, select')
    const clonedFields = clone.querySelectorAll('input, textarea, select')
    const len = Math.min(originalFields.length, clonedFields.length)

    for (let i = 0; i < len; i++) {
      const src = originalFields[i]
      const dst = clonedFields[i]
      const tag = dst.tagName.toLowerCase()

      if (tag === 'textarea') {
        dst.textContent = src.value || ''
        continue
      }

      if (tag === 'select') {
        const srcValue = src.value
        Array.from(dst.options).forEach(opt => {
          opt.selected = opt.value === srcValue
        })
        continue
      }

      const type = (dst.getAttribute('type') || '').toLowerCase()
      if (type === 'checkbox' || type === 'radio') {
        if (src.checked) dst.setAttribute('checked', '')
        else dst.removeAttribute('checked')
        continue
      }

      dst.setAttribute('value', src.value ?? '')
    }

    clone.querySelectorAll('input, textarea, select').forEach(el => {
      const tag = el.tagName.toLowerCase()
      const style = el.getAttribute('style') || ''
      const name = el.getAttribute('name') || ''

      if (tag === 'input') {
        const type = (el.getAttribute('type') || 'text').toLowerCase()
        if (type === 'hidden') {
          el.remove()
          return
        }

        if (type === 'checkbox' || type === 'radio') {
          const box = document.createElement('span')
          const checked = el.checked || el.hasAttribute('checked')
          box.className = checked ? 'pdf-box checked' : 'pdf-box'
          box.setAttribute('aria-hidden', 'true')
          box.setAttribute('style', style)
          el.replaceWith(box)
          return
        }

        const span = document.createElement('span')
        span.textContent = el.getAttribute('value') || el.value || ''
        span.setAttribute('data-name', name)
        span.setAttribute('style', `${style};display:inline-block;white-space:pre-wrap;`)
        el.replaceWith(span)
        return
      }

      if (tag === 'textarea') {
        const div = document.createElement('div')
        div.textContent = el.textContent || el.value || ''
        div.setAttribute('data-name', name)
        div.setAttribute('style', `${style};white-space:pre-wrap;`)
        el.replaceWith(div)
        return
      }

      if (tag === 'select') {
        const span = document.createElement('span')
        const selected = el.querySelector('option:checked')
        span.textContent = selected ? selected.textContent : (el.value || '')
        span.setAttribute('data-name', name)
        span.setAttribute('style', `${style};display:inline-block;white-space:pre-wrap;`)
        el.replaceWith(span)
      }
    })

    const styleNodes = Array.from(document.querySelectorAll('link[rel=\"stylesheet\"], style'))
    const stylesHtml = styleNodes.map(n => n.outerHTML).join('\n')
    const html = `<!doctype html>
<html lang=\"zh-CN\">
  <head>
    <meta charset=\"utf-8\" />
    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />
    ${stylesHtml}
    <style>
      @page { size: A4 portrait; margin: 0; }
      html, body { margin: 0; padding: 0; background: #fff; width: 210mm; height: 297mm; overflow: hidden; }
      .pdf-sheet { width: 210mm; height: 297mm; padding: 6mm; box-sizing: border-box; overflow: hidden; }
      .pdf-page { width: 198mm; height: 285mm; overflow: hidden; box-sizing: border-box; position: relative; }
      .pdf-content { position: absolute; left: 0; top: 0; transform-origin: top left; }
      .pdf-preview.beckmanBeamResult-container { width: 198mm; height: 285mm; max-width: 198mm; min-width: 0; margin: 0; padding: 0; box-sizing: border-box; display: flex; flex-direction: column; }
      .pdf-preview { overflow: visible; }
      .pdf-preview * { page-break-inside: avoid; break-inside: avoid; }
      .pdf-preview [data-name] { width: auto !important; max-width: 100% !important; box-sizing: border-box; overflow-wrap: anywhere; word-break: break-all; white-space: pre-wrap; }
      .pdf-preview #pdfForm { flex: 1; min-height: 0; display: flex; flex-direction: column; }
      .pdf-preview #pdfForm > table { flex: 0 0 auto; height: auto; }
      .pdf-preview table { width: 100%; height: auto; }
      .pdf-preview #pdfForm .footer-info { margin-top: auto !important; margin-bottom: 0 !important; align-items: flex-end; }
      .pdf-preview .pdf-box {
        width: 13px;
        height: 13px;
        border: 1px solid #000;
        display: inline-block;
        position: relative;
        vertical-align: middle;
        margin-right: 6px;
        box-sizing: border-box;
      }
      .pdf-preview .pdf-box.checked::after {
        content: '';
        position: absolute;
        top: 50%;
        left: 50%;
        width: 4px;
        height: 8px;
        border: solid #000;
        border-width: 0 2px 2px 0;
        transform: translate(-50%, -65%) rotate(45deg);
      }
    </style>
  </head>
  <body><div class="pdf-sheet"><div class="pdf-page"><div class="pdf-content" style="width: ${contentWidthPx}px; height: ${contentHeightPx}px; transform: translate(${pdfOffsetXPx}px, ${pdfOffsetYPx}px) scale(${pdfScale});">${clone.outerHTML}</div></div></div></body>
</html>`
    return toBase64Utf8(html)
  }

  const fields = Array.from(pdfForm.value.querySelectorAll('input, textarea, select'))
  const snapshotBase64 = buildHtmlSnapshotBase64()
  const inputsHtml = fields.map((el) => {
    const name = el.getAttribute('name')
    if (!name) return ''

    if (el.tagName.toLowerCase() === 'select') {
      return `<input type=\"hidden\" name=\"${escapeAttr(name)}\" value=\"${escapeAttr(el.value)}\" />`
    }

    if (el.tagName.toLowerCase() === 'textarea') {
      return `<input type=\"hidden\" name=\"${escapeAttr(name)}\" value=\"${escapeAttr(el.value)}\" />`
    }

    const type = (el.getAttribute('type') || '').toLowerCase()
    if (type === 'file' || type === 'button' || type === 'submit' || type === 'reset') return ''

    if (type === 'checkbox' || type === 'radio') {
      if (!el.checked) return ''
      return `<input type=\"hidden\" name=\"${escapeAttr(name)}\" value=\"${escapeAttr(el.value || 'on')}\" />`
    }

    return `<input type=\"hidden\" name=\"${escapeAttr(name)}\" value=\"${escapeAttr(el.value)}\" />`
  }).join('\n') + `\n<input type=\"hidden\" name=\"__pdf_html_base64\" value=\"${escapeAttr(snapshotBase64)}\" />\n`

  const html = `<!doctype html>
<html lang=\"zh-CN\">
  <head>
    <meta charset=\"utf-8\" />
    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />
    <title>PDF预览</title>
    <style>
      html, body { height: 100%; margin: 0; }
      body { padding: 28px 60px; box-sizing: border-box; background: #f2f2f2; }
      .frame-shell {
        height: calc(100vh - 56px);
        background: #fff;
        border-radius: 8px;
        box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
        overflow: hidden;
      }
      iframe { width: 100%; height: 100%; border: 0; background: #fff; }
    </style>
  </head>
  <body onload=\"var f=document.getElementById('pdfPostForm'); if (f) f.submit();\">
    <div class=\"frame-shell\">
      <iframe name=\"pdfFrame\" title=\"PDF预览\"></iframe>
    </div>
    <form id=\"pdfPostForm\" method=\"post\" action=\"${escapeAttr(actionUrl)}\" target=\"pdfFrame\">
      ${inputsHtml}
    </form>
  </body>
</html>`
  const w = window.open('', '_blank')
  if (!w) return
  w.document.open()
  w.document.write(html)
  w.document.close()
}


const generatePdf = () => {
  openBackendPdfPreview('/api/pdf/beckman_beam_result/generate')
}

const previewPdf = () => {
  openBackendPdfPreview('/api/pdf/beckman_beam_result/preview')
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
            flex-wrap: wrap;
            padding: 0 24px;
            box-sizing: border-box;
            width: 100%;
            max-width: 100%;
            overflow-x: hidden;
            background-color: var(--bg-card);
            border-radius: 8px;
            box-shadow: var(--shadow);
            padding: 16px 24px;
        }

        .toolbar-left {
            display: flex;
            align-items: center;
            gap: 16px;
            flex-wrap: wrap;
            margin-left: 0;
            flex: 1;
        }

        .toolbar-right {
            display: flex;
            align-items: center;
            gap: 12px;
            flex-wrap: wrap;
            flex: 1;
            justify-content: flex-end;
        }

        .link-button {
            background: var(--card-blue);
            border: none;
            border-radius: 4px;
            color: var(--color-blue);
            cursor: pointer;
            font-size: 14px;
            padding: 6px 12px;
            transition: all 0.2s;
            display: inline-flex;
            align-items: center;
            gap: 5px;
            white-space: nowrap;
        }

        .link-button:hover {
            background-color: var(--color-blue);
            color: white;
            text-decoration: none;
        }

        .status-text {
            font-size: 14px;
            font-weight: 500;
            color: var(--text-light);
            white-space: nowrap;
        }

        .status-label {
            margin-left: 6px;
        }

        .btn {
            padding: 6px 12px;
            border-radius: 4px;
            border: none;
            font-size: 14px;
            cursor: pointer;
            background-color: var(--card-blue);
            color: var(--color-blue);
            transition: all 0.2s;
            white-space: nowrap;
        }

        .btn-small {
            padding: 6px 12px;
            font-size: 14px;
        }

        .btn-primary {
            background-color: var(--color-blue);
            color: white;
        }

        .btn-secondary {
            background-color: var(--card-blue);
            color: var(--color-blue);
        }

        .btn-danger {
            background-color: #FFCDD2;
            color: #D32F2F;
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
            font-size: 16px;
            color: #000;
            max-width: 100%;
            min-width: 800px;
            margin: 0 auto;
            padding: 16px;
            background-color: var(--bg-card);
            border-radius: 8px;
            box-shadow: var(--shadow);
            box-sizing: border-box;
        }
        h2 {
            text-align: center;
            margin-bottom: 20px;
            font-size: 24px;
            font-weight: bold;
            color: black;
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
            margin-bottom: 10px;
            table-layout: fixed; /* Fixed layout for consistent column widths */
            word-break: break-all;
        }
        th, td {
            border: 1px solid black;
            padding: 8px 5px;
            text-align: center;
            font-size: inherit;
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
            font-size: 16px;
            font-weight: normal;
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
                margin: 0;
            }
        }
    
</style>
