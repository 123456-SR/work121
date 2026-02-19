<template>
  <div class="beckmanBeamResult-container">

    <div class="no-print" style="margin-bottom: 20px;">
        <button @click="goToList" style="text-decoration: none; color: blue; background: none; border: none; cursor: pointer; padding: 0;">&lt; 返回列表</button>
        <button @click="handleSign" style="float: right; margin-left: 10px;">签字</button>
        <button @click="saveData" style="float: right; margin-left: 10px;">保存</button>
        <button @click="printDocument" style="float: right; margin-left: 10px;">打印此单</button>
        <button @click="generatePdf" style="float: right; margin-left: 10px;">下载PDF</button>
        <button @click="previewPdf" style="float: right; margin-left: 10px;">预览PDF</button>
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
            <div style="width: 33%;">
                批准：
                <div style="display: inline-block; position: relative; width: 100px;">
                    <input type="text" v-model="formData.approver" name="approver" style="width: 100%; border-bottom: 1px solid black;" :style="{ opacity: formData.approverSignature ? 0 : 1 }">
                    <img v-if="formData.approverSignature" :src="formData.approverSignature" style="position: absolute; bottom: 0; left: 0; width: 100%; height: 40px; object-fit: contain; pointer-events: none; background-color: transparent;">
                </div>
            </div>
            <div style="width: 33%;">
                审核：
                <div style="display: inline-block; position: relative; width: 100px;">
                    <input type="text" v-model="formData.reviewer" name="reviewer" style="width: 100%; border-bottom: 1px solid black;" :style="{ opacity: formData.reviewerSignature ? 0 : 1 }">
                    <img v-if="formData.reviewerSignature" :src="formData.reviewerSignature" style="position: absolute; bottom: 0; left: 0; width: 100%; height: 40px; object-fit: contain; pointer-events: none; background-color: transparent;">
                </div>
            </div>
            <div style="width: 33%;">
                检测：
                <div style="display: inline-block; position: relative; width: 100px;">
                    <input type="text" v-model="formData.tester" name="tester" style="width: 100%; border-bottom: 1px solid black;" :style="{ opacity: formData.testerSignature ? 0 : 1 }">
                    <img v-if="formData.testerSignature" :src="formData.testerSignature" style="position: absolute; bottom: 0; left: 0; width: 100%; height: 40px; object-fit: contain; pointer-events: none; background-color: transparent;">
                </div>
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
  reviewer: '',
  tester: '',
  approverSignature: '',
  reviewerSignature: '',
  testerSignature: '',
})

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
      formData.tester = ent.tester
      formData.reviewer = ent.reviewer
    }

    const response = await axios.get('/api/beckman-beam/result/get-by-entrustment-id', {
      params: { entrustmentId }
    })

    let sourceJson = null

    if (response.data.success && response.data.data) {
      const data = response.data.data
      formData.id = data.id
      formData.entrustmentId = data.entrustmentId

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
          if (record.dataJson) {
            sourceJson = record.dataJson
          }
        }
      } catch (e) {
        console.error('beckman result autofill error', e)
      }
    }

    if (sourceJson) {
      try {
        const parsedData = JSON.parse(sourceJson)
        Object.assign(formData, parsedData)
      } catch (e) {
        console.error('beckman result autofill parse error', e)
      }
    }
  } catch (error) {
    console.error('Failed to load data', error)
  }
}

const saveData = async () => {
    try {
        const dataToSave = {
            id: formData.id,
            entrustmentId: formData.entrustmentId || props.id,
            dataJson: JSON.stringify(formData),
            reviewSignaturePhoto: formData.reviewerSignature,
            inspectSignaturePhoto: formData.testerSignature,
            approveSignaturePhoto: formData.approverSignature
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

            // Match Tester
            if (formData.tester === currentName) {
                formData.testerSignature = imgSrc
                signed = true
            }

            // Match Reviewer
            if (formData.reviewer === currentName) {
                formData.reviewerSignature = imgSrc
                signed = true
            }

            // Match Approver
            if (formData.approver === currentName) {
                formData.approverSignature = imgSrc
                signed = true
            }
            
            if (signed) {
                alert('签名成功')
            } else {
                alert(`当前用户(${currentName})与表单中的检测/审核/批准人员不匹配，无法签名`)
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
