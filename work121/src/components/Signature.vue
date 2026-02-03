<template>
  <div class="signature-container">

    <div class="container">
        <a href="/" class="back-link">&lt; 返回主页</a>
        
        <h1>电子签名</h1>
        
        <p class="instructions">请在下方画布中使用鼠标或手写板签名，完成后可保存为图片</p>
        
        <div class="color-picker">
            <label for="penColor">签名颜色：</label>
            <input type="color" id="penColor" v-model="penColor" @change="updatePenColor">
        </div>
        
        <div class="brush-size">
            <label for="brushSize">笔触粗细：</label>
            <input type="range" id="brushSize" min="1" max="10" v-model="brushSize" @change="updateBrushSize">
            <span id="brushSizeValue">{{ brushSize }}px</span>
        </div>
        
        <div class="canvas-container">
            <canvas ref="signatureCanvas" width="800" height="300"></canvas>
        </div>
        
        <div class="button-group">
            <button class="btn btn-danger" @click="clearCanvas">清除签名</button>
            <button class="btn btn-primary" @click="undoLastStroke" :disabled="history.length === 0">撤销</button>
            <button class="btn btn-success" @click="saveSignature" :disabled="isSignatureEmpty">保存签名</button>
        </div>
        
        <div class="preview-section">
            <h3>签名预览</h3>
            <div id="signaturePreview">
                <img v-if="signatureDataUrl" :src="signatureDataUrl" alt="签名预览" style="max-width: 100%;">
                <p v-else style="color: #999;">暂无签名</p>
            </div>
        </div>
    </div>

    

  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import axios from 'axios'

const signatureCanvas = ref(null)
const penColor = ref('#000000')
const brushSize = ref(3)
const signatureDataUrl = ref('')
const isSignatureEmpty = ref(true)
const history = ref([])
let ctx = null
let isDrawing = ref(false)
let lastX = 0
let lastY = 0
let currentUserAccount = ref('')

onMounted(() => {
  // 获取当前登录用户账号
  getCurrentUserAccount()
  initCanvas()
  // 加载用户签名
  if (currentUserAccount.value) {
    loadUserSignature()
  }
})

// 获取当前登录用户账号
const getCurrentUserAccount = () => {
  try {
    const userInfoStr = localStorage.getItem('userInfo')
    if (userInfoStr) {
      const userInfo = JSON.parse(userInfoStr)
      // 从localStorage中获取用户账号，与LoginController返回的结构匹配
      currentUserAccount.value = userInfo.username || 'admin'
      console.log('Current user account:', currentUserAccount.value)
    } else {
      // 默认使用admin账号
      currentUserAccount.value = 'admin'
      console.log('No user info found, using default account:', currentUserAccount.value)
    }
  } catch (error) {
    console.error('Error getting user account:', error)
    currentUserAccount.value = 'admin'
  }
}

// 加载用户签名
const loadUserSignature = async () => {
  try {
    console.log('Loading signature for user:', currentUserAccount.value)
    
    const response = await axios.post('/api/signature/get', {
      userAccount: currentUserAccount.value
    })
    
    if (response.data.success && response.data.data) {
      const signature = response.data.data
      console.log('Signature loaded successfully:', signature)
      
      // 如果有签名数据，显示在画布上
      if (signature.signatureBlob) {
        let url;
        if (typeof signature.signatureBlob === 'string') {
          // 如果是Base64编码的字符串，将其转换为Blob
          try {
            // 解码Base64字符串
            const binaryString = atob(signature.signatureBlob);
            // 创建一个字节数组
            const byteArray = new Uint8Array(binaryString.length);
            for (let i = 0; i < binaryString.length; i++) {
              byteArray[i] = binaryString.charCodeAt(i);
            }
            // 创建Blob
            const blob = new Blob([byteArray], { type: 'image/png' });
            url = URL.createObjectURL(blob);
          } catch (e) {
            console.error('Error decoding Base64 string:', e);
            return;
          }
        } else {
          // 如果是ArrayBuffer或Uint8Array，创建Blob URL
          const blob = new Blob([signature.signatureBlob], { type: 'image/png' })
          url = URL.createObjectURL(blob)
        }
        
        const img = new Image()
        img.onload = () => {
          ctx.clearRect(0, 0, signatureCanvas.value.width, signatureCanvas.value.height)
          ctx.drawImage(img, 0, 0)
          updateSignatureState()
          URL.revokeObjectURL(url)
        }
        img.src = url
      }
    } else {
      console.log('No signature found for user:', currentUserAccount.value)
    }
  } catch (error) {
    console.error('Error loading signature:', error)
  }
}

const initCanvas = () => {
  const canvas = signatureCanvas.value
  if (canvas) {
    console.log('Canvas initialized:', canvas)
    ctx = canvas.getContext('2d')
    console.log('Context created:', ctx)
    
    // 设置画布样式
    ctx.lineJoin = 'round'
    ctx.lineCap = 'round'
    ctx.strokeStyle = penColor.value
    ctx.lineWidth = brushSize.value
    
    // 清空画布
    clearCanvas()
    
    // 添加鼠标事件监听器（使用箭头函数确保作用域正确）
    canvas.addEventListener('mousedown', (e) => {
      console.log('Mouse down:', e.offsetX, e.offsetY)
      // 阻止默认行为，防止文本选择等
      e.preventDefault()
      isDrawing.value = true
      lastX = e.offsetX
      lastY = e.offsetY
      saveState()
    })
    
    canvas.addEventListener('mousemove', (e) => {
      if (!isDrawing.value) return
      console.log('Mouse move:', e.offsetX, e.offsetY)
      // 阻止默认行为，防止页面滚动
      e.preventDefault()
      ctx.beginPath()
      ctx.moveTo(lastX, lastY)
      ctx.lineTo(e.offsetX, e.offsetY)
      ctx.stroke()
      lastX = e.offsetX
      lastY = e.offsetY
      updateSignatureState()
    })
    
    canvas.addEventListener('mouseup', (e) => {
      console.log('Mouse up')
      e.preventDefault()
      isDrawing.value = false
      ctx.closePath()
    })
    
    canvas.addEventListener('mouseout', (e) => {
      console.log('Mouse out')
      e.preventDefault()
      isDrawing.value = false
      ctx.closePath()
    })
    
    // 添加触摸事件监听器（支持触摸设备）
    canvas.addEventListener('touchstart', (e) => {
      console.log('Touch start:', e)
      // 阻止默认行为，防止页面滚动和缩放
      e.preventDefault()
      const rect = canvas.getBoundingClientRect()
      const touch = e.touches[0]
      isDrawing.value = true
      lastX = touch.clientX - rect.left
      lastY = touch.clientY - rect.top
      saveState()
    }, { passive: false })
    
    canvas.addEventListener('touchmove', (e) => {
      console.log('Touch move:', e)
      // 阻止默认行为，防止页面滚动
      e.preventDefault()
      if (!isDrawing.value) return
      const rect = canvas.getBoundingClientRect()
      const touch = e.touches[0]
      ctx.beginPath()
      ctx.moveTo(lastX, lastY)
      ctx.lineTo(touch.clientX - rect.left, touch.clientY - rect.top)
      ctx.stroke()
      lastX = touch.clientX - rect.left
      lastY = touch.clientY - rect.top
      updateSignatureState()
    }, { passive: false })
    
    canvas.addEventListener('touchend', (e) => {
      console.log('Touch end')
      e.preventDefault()
      isDrawing.value = false
      ctx.closePath()
    }, { passive: false })
  } else {
    console.error('Canvas reference is null')
  }
}

// 这些函数已被内联到事件监听器中，不再需要
// const startDrawing = (e) => { ... }
// const startDrawingTouch = (e) => { ... }
// const draw = (e) => { ... }
// const drawTouch = (e) => { ... }
// const stopDrawing = () => { ... }

const saveState = () => {
  // 保存当前画布状态到历史记录
  const data = signatureCanvas.value.toDataURL()
  history.value.push(data)
  // 限制历史记录长度为20
  if (history.value.length > 20) {
    history.value.shift()
  }
}

const updatePenColor = () => {
  if (ctx) {
    ctx.strokeStyle = penColor.value
  }
}

const updateBrushSize = () => {
  if (ctx) {
    ctx.lineWidth = brushSize.value
  }
}

const clearCanvas = () => {
  if (ctx && signatureCanvas.value) {
    ctx.clearRect(0, 0, signatureCanvas.value.width, signatureCanvas.value.height)
    signatureDataUrl.value = ''
    isSignatureEmpty.value = true
    history.value = []
  }
}

const undoLastStroke = () => {
  if (history.value.length > 0) {
    // 移除最后一个历史记录
    history.value.pop()
    
    // 清除画布
    ctx.clearRect(0, 0, signatureCanvas.value.width, signatureCanvas.value.height)
    
    // 恢复到上一个状态
    if (history.value.length > 0) {
      const img = new Image()
      img.src = history.value[history.value.length - 1]
      img.onload = () => {
        ctx.drawImage(img, 0, 0)
        updateSignatureState()
      }
    } else {
      signatureDataUrl.value = ''
      isSignatureEmpty.value = true
    }
  }
}

const updateSignatureState = () => {
  // 检查画布是否为空
  const imageData = ctx.getImageData(0, 0, signatureCanvas.value.width, signatureCanvas.value.height)
  const data = imageData.data
  let hasContent = false
  
  for (let i = 0; i < data.length; i += 4) {
    const alpha = data[i + 3]
    if (alpha > 0) {
      hasContent = true
      break
    }
  }
  
  isSignatureEmpty.value = !hasContent
  if (hasContent) {
    signatureDataUrl.value = signatureCanvas.value.toDataURL()
  }
}

const saveSignature = async () => {
  if (isSignatureEmpty.value) {
    alert('请先签名再保存')
    return
  }
  
  try {
    console.log('Saving signature for user:', currentUserAccount.value)
    
    // 将Canvas转换为Blob
    const blob = await new Promise(resolve => {
      signatureCanvas.value.toBlob(resolve, 'image/png')
    })
    
    // 创建FormData
    const formData = new FormData()
    formData.append('file', blob, 'signature.png')
    formData.append('userAccount', currentUserAccount.value)
    
    // 发送到服务器
    const response = await axios.post('/api/signature/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    
    if (response.data.success) {
      alert('签名保存成功')
    } else {
      alert('签名保存失败: ' + response.data.message)
    }
  } catch (error) {
    console.error('保存签名失败:', error)
    alert('保存签名失败，请稍后重试')
  }
}

const printDocument = () => {
  window.print()
}

const generatePdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = ''
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}

const previewPdf = () => {
  if (pdfForm.value) {
    pdfForm.value.action = ''
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }
}
</script>

<style scoped>

        /* 防止页面滚动 */
        body {
            overflow: hidden;
        }
        
        .signature-container {
            font-family: 'Microsoft YaHei', Arial, sans-serif;
            text-align: center;
            margin: 20px;
            background-color: #f4f6f9;
            overflow: hidden;
        }
        h1 {
            color: #333;
            margin-bottom: 30px;
        }
        .container {
            max-width: 900px;
            margin: 0 auto;
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        .canvas-container {
            border: 2px solid #007bff;
            border-radius: 8px;
            margin-bottom: 20px;
            background: white;
            display: inline-block;
            position: relative;
            overflow: hidden;
        }
        #signatureCanvas {
            border: 1px solid #ddd;
            border-radius: 5px;
            cursor: crosshair;
            position: relative;
            z-index: 1;
        }
        .button-group {
            display: flex;
            justify-content: center;
            gap: 15px;
            margin-bottom: 20px;
            flex-wrap: wrap;
        }
        .btn {
            padding: 12px 24px;
            font-size: 16px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            transition: all 0.3s;
        }
        .btn-primary {
            background-color: #007bff;
            color: white;
        }
        .btn-primary:hover {
            background-color: #0056b3;
        }
        .btn-success {
            background-color: #28a745;
            color: white;
        }
        .btn-success:hover {
            background-color: #1e7e34;
        }
        .btn-danger {
            background-color: #dc3545;
            color: white;
        }
        .btn-danger:hover {
            background-color: #c82333;
        }
        .btn-secondary {
            background-color: #6c757d;
            color: white;
        }
        .btn-secondary:hover {
            background-color: #545b62;
        }
        .preview-section {
            margin-top: 30px;
            padding-top: 20px;
            border-top: 1px solid #eee;
        }
        .preview-section h3 {
            color: #666;
            margin-bottom: 15px;
        }
        #signaturePreview {
            max-width: 400px;
            border: 1px solid #ddd;
            border-radius: 5px;
            padding: 10px;
            background: white;
        }
        .instructions {
            color: #666;
            margin-bottom: 20px;
            font-size: 14px;
        }
        .color-picker {
            margin-bottom: 20px;
        }
        .color-picker label {
            margin-right: 10px;
            font-weight: bold;
        }
        .color-picker input[type="color"] {
            width: 50px;
            height: 30px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
        }
        .brush-size {
            margin-bottom: 20px;
        }
        .brush-size label {
            margin-right: 10px;
            font-weight: bold;
        }
        .brush-size input[type="range"] {
            width: 200px;
            vertical-align: middle;
        }
        .brush-size span {
            margin-left: 10px;
            font-weight: bold;
            color: #007bff;
        }
        .back-link {
            display: inline-block;
            margin-bottom: 20px;
            text-decoration: none;
            color: #007bff;
            font-size: 14px;
        }
        .back-link:hover {
            text-decoration: underline;
        }
    
</style>
