<template>
  <div class="sample-transfer-container">
    <div class="no-print toolbar">
      <div class="toolbar-left">
        <button @click="goToList" class="link-button">&lt; 返回列表</button>
      </div>
      <div class="toolbar-right">
        <button
          @click="saveForm"
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

    <form id="sampleTransferForm" ref="pdfForm" method="post">
      <h2>样品流转单</h2>

      <table class="sample-transfer-table">
        <!-- 第一行 -->
        <tr>
          <td class="label">检测室</td>
          <td><input type="text" v-model="formData.testingRoom" name="testingRoom"></td>
          <td class="label">样品名称</td>
          <td><input type="text" v-model="formData.sampleName" name="sampleName"></td>
          <td class="label">规格/型号</td>
          <td><input type="text" v-model="formData.spec" name="spec"></td>
        </tr>
        <!-- 第二行 -->
        <tr>
          <td class="label">样品编号</td>
          <td><input type="text" v-model="formData.sampleNumber" name="sampleNumber"></td>
          <td class="label">接样日期</td>
          <td><input type="text" v-model="formData.receiveDate" name="receiveDate"></td>
          <td class="label">样品状态</td>
          <td><input type="text" v-model="formData.sampleStatus" name="sampleStatus"></td>
        </tr>
        <!-- 第三行 -->
        <tr>
          <td class="label">样品数量</td>
          <td><input type="text" v-model="formData.sampleQuantity" name="sampleQuantity"></td>
          <td class="label" rowspan="3">样品历史及概况</td>
          <td colspan="3" rowspan="3">
            <textarea v-model="formData.sampleHistory" name="sampleHistory"></textarea>
          </td>
        </tr>
        <!-- 第四行 -->
        <tr>
          <td class="label" rowspan="2">检测项目<br>检测依据</td>
          <td rowspan="2">
            <textarea v-model="formData.testItems" name="testItems"></textarea>
          </td>
        </tr>
        <!-- 第五行 -->
        <tr>
          <!-- 空行，用于对齐 -->
        </tr>
        <!-- 第六行 -->
        <tr>
          <td class="label">领样人</td>
          <td><input type="text" v-model="formData.sampleTaker" name="sampleTaker"></td>
          <td class="label">接样人/发样人</td>
          <td colspan="3"><input type="text" v-model="formData.sampleReceiver" name="sampleReceiver"></td>
        </tr>
        <!-- 第七行 -->
        <tr>
          <td class="label">备注</td>
          <td colspan="5"><input type="text" v-model="formData.remarks" name="remarks"></td>
        </tr>
      </table>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, inject } from 'vue'
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

const pdfForm = ref(null)

const formData = reactive({
  testingRoom: '',
  sampleName: '',
  spec: '',
  sampleNumber: '',
  receiveDate: '',
  sampleStatus: '',
  sampleQuantity: '',
  sampleHistory: '',
  testItems: '',
  sampleTaker: '',
  sampleReceiver: '',
  remarks: ''
})

const currentId = ref(props.id)

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

const mapDataToForm = (data) => {
  currentId.value = data.id
  formData.testingRoom = data.testLab || ''
  formData.sampleName = data.sampleName || ''
  formData.spec = data.specModel || ''
  formData.sampleNumber = data.sampleNumber || ''
  formData.receiveDate = data.receiveDate ? formatDate(data.receiveDate, 'YYYY-MM-DD') : ''
  formData.sampleStatus = data.sampleStatus || ''
  formData.sampleQuantity = data.sampleQuantity || ''
  formData.sampleHistory = data.sampleHistory || ''
  formData.testItems = data.testItems || ''
  formData.sampleTaker = data.withdrawPerson || ''
  formData.sampleReceiver = data.receivePerson || ''
  formData.remarks = data.remarks || ''
}

const loadDataByWtNum = async (wtNum) => {
  if (!wtNum) return
  try {
    console.log('Fetching data for wtNum:', wtNum)
    const response = await axios.get(`/api/sample-circulation/by-sample-number?sampleNumber=${encodeURIComponent(wtNum)}`)
    if (response.data.success && response.data.data) {
      mapDataToForm(response.data.data)
    }
  } catch (error) {
    console.error('Error loading data by sample number:', error)
  }
}

const saveForm = async (silent = false) => {
  const idToSave = currentId.value || props.id
  
  // Get current user
  const user = JSON.parse(localStorage.getItem('userInfo') || '{}')
  const currentUserName = user ? (user.userName || user.username) : '未知用户'
  const currentUserId = user ? (user.username || user.id) : ''
  
  const payload = {
    id: idToSave,
    testLab: formData.testingRoom,
    sampleName: formData.sampleName,
    specModel: formData.spec,
    sampleNumber: formData.sampleNumber,
    receiveDate: formData.receiveDate,
    sampleStatus: formData.sampleStatus,
    sampleQuantity: formData.sampleQuantity,
    testItems: formData.testItems,
    testStandard: '', // 检测依据，前端表单中没有这个字段
    sampleHistory: formData.sampleHistory,
    receivePerson: formData.sampleReceiver,
    receivePersonId: '', // 接样人ID，前端表单中没有这个字段
    withdrawPerson: formData.sampleTaker,
    withdrawPersonId: '', // 领样人ID，前端表单中没有这个字段
    remarks: formData.remarks,
    createBy: currentUserName,
    updateBy: currentUserName
  }
  
  try {
    const response = await axios.post('/api/sample-circulation/save', payload)
    if (response.data.success) {
      if (response.data.data && response.data.data.id) {
        currentId.value = response.data.data.id
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

const printDocument = () => {
  window.print()
}

const openBackendPdfPreview = (actionUrl) => {
  if (!pdfForm.value) return
  const container = pdfForm.value.closest('.sample-transfer-container')
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
  const marginMm = 12
  const availableWidthPx = mmToPx(pageWidthMm - marginMm * 2)
  const availableHeightPx = mmToPx(pageHeightMm - marginMm * 2)
  const rect = container.getBoundingClientRect()
  const contentWidthPx = Math.max(container.scrollWidth || 0, rect.width || 0, 1)
  const contentHeightPx = Math.max(container.scrollHeight || 0, rect.height || 0, 1)
  const pdfScale = Math.min(1, availableWidthPx / contentWidthPx, availableHeightPx / contentHeightPx)
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
      html, body { margin: 0; padding: 0; background: #fff; }
      .pdf-sheet { width: 210mm; height: 297mm; padding: 12mm; box-sizing: border-box; overflow: hidden; }
      .pdf-page { width: 186mm; height: 273mm; overflow: hidden; position: relative; }
      .pdf-transform { position: absolute; left: 0; top: 0; display: inline-block; transform: translate(${pdfOffsetXPx}px, ${pdfOffsetYPx}px) scale(${pdfScale}); transform-origin: top left; }
      .pdf-preview input, .pdf-preview textarea, .pdf-preview select { display: none !important; }
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
  <body><div class="pdf-sheet"><div class="pdf-page"><div class="pdf-transform">${clone.outerHTML}</div></div></div></body>
</html>`
    return toBase64Utf8(html)
  }

  const fields = Array.from(pdfForm.value.querySelectorAll('input, textarea, select'))
  const snapshotBase64 = buildHtmlSnapshotBase64()
  const inputsHtml = fields.map((el) => {
    const name = el.getAttribute('name')
    if (!name) return ''

    if (el.tagName.toLowerCase() === 'select') {
      return `<input type="hidden" name="${escapeAttr(name)}" value="${escapeAttr(el.value)}" />`
    }

    if (el.tagName.toLowerCase() === 'textarea') {
      return `<input type="hidden" name="${escapeAttr(name)}" value="${escapeAttr(el.value)}" />`
    }

    const type = (el.getAttribute('type') || '').toLowerCase()
    if (type === 'file' || type === 'button' || type === 'submit' || type === 'reset') return ''

    if (type === 'checkbox' || type === 'radio') {
      if (!el.checked) return ''
      return `<input type="hidden" name="${escapeAttr(name)}" value="${escapeAttr(el.value || 'on')}" />`
    }

    return `<input type="hidden" name="${escapeAttr(name)}" value="${escapeAttr(el.value)}" />`
  }).join('\n') + `\n<input type="hidden" name="__pdf_html_base64" value="${escapeAttr(snapshotBase64)}" />\n`

  const html = `<!doctype html>
<html lang="zh-CN">
  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
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
  <body onload="var f=document.getElementById('pdfPostForm'); if (f) f.submit();">
    <div class="frame-shell">
      <iframe name="pdfFrame" title="PDF预览"></iframe>
    </div>
    <form id="pdfPostForm" method="post" action="${escapeAttr(actionUrl)}" target="pdfFrame">
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
  openBackendPdfPreview('/api/pdf/generate')
}

const previewPdf = () => {
  openBackendPdfPreview('/api/pdf/preview')
}

const goToList = () => {
  navigateTo('SampleTransferList')
}

onMounted(() => {
  if (props.wtNum) {
    loadDataByWtNum(props.wtNum)
  }
  // 设置默认值
  formData.receiveDate = new Date().toISOString().split('T')[0]
  formData.sampleStatus = '正常'
})
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
.btn-secondary {
  background-color: #fff;
  border-color: #d0d7de;
  color: #34495e;
}
.btn-secondary:hover {
  filter: brightness(0.95);
}

.sample-transfer-container {
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
  color: black;
}

.sample-transfer-table {
  width: 100%;
  border-collapse: collapse;
  border: 2px solid black;
  margin-top: 20px;
}

.sample-transfer-table td {
  border: 1px solid black;
  padding: 5px;
  vertical-align: middle;
  text-align: center;
  font-size: 14px;
}

.sample-transfer-table .label {
  font-weight: bold;
  background-color: transparent;
  text-align: left;
  padding-left: 10px;
  width: auto;
}

.sample-transfer-table input,
.sample-transfer-table textarea {
  width: 98%;
  border: none;
  outline: none;
  font-family: inherit;
  font-size: inherit;
  background-color: transparent;
  text-align: center;
}

.sample-transfer-table input:focus,
.sample-transfer-table textarea:focus {
  background-color: #f0f8ff;
}

.sample-transfer-table textarea {
  resize: none;
  overflow: hidden;
  text-align: left;
  height: 100%;
  min-height: 80px;
}

@media print {
  @page {
    size: A4 portrait;
    margin: 0;
  }
  .no-print {
    display: none;
  }
  .sample-transfer-container {
    width: 100%;
    margin: 0;
    padding: 0;
  }
  .sample-transfer-table {
    font-size: 12px;
  }
  .sample-transfer-table td {
    padding: 4px;
  }
}
</style>
