<template>
  <div class="login-container">
    <div class="login-form">
      <h2 class="login-title">表格管理系统登录</h2>

      <!-- 用户登录/注册 -->
      <div class="step-container">
        <div class="step-header">
          <h3 class="step-title">{{ isRegister ? '用户注册' : '用户登录' }}</h3>
        </div>

        <div class="form-group">
          <label for="username">账号</label>
          <input
            type="text"
            id="username"
            v-model="userForm.username"
            placeholder="请输入账号"
            class="form-control"
          >
        </div>

        <div class="form-group">
          <label for="password">密码</label>
          <input
            type="password"
            id="password"
            v-model="userForm.password"
            placeholder="请输入密码"
            class="form-control"
          >
        </div>

        <div v-if="isRegister" class="form-group">
          <label for="fullName">真实姓名 (选填)</label>
          <input
            type="text"
            id="fullName"
            v-model="userForm.fullName"
            placeholder="请输入真实姓名"
            class="form-control"
          >
        </div>

        <div v-if="errorMessage" class="error-message">
          {{ errorMessage }}
        </div>

        <button
          @click="handleUserAction"
          class="login-button"
          :disabled="isLoading"
        >
          {{ isLoading ? '处理中...' : (isRegister ? '注册' : '登录') }}
        </button>

        <div class="toggle-action">
          <span v-if="!isRegister">
            没有账号? <a href="#" @click.prevent="toggleMode">立即注册</a>
          </span>
          <span v-else>
            已有账号? <a href="#" @click.prevent="toggleMode">去登录</a>
          </span>
        </div>
      </div>

    </div>

    <!-- 强制修改密码弹窗 -->
    <div v-if="showChangePasswordDialog" class="dialog-overlay">
      <div class="dialog">
        <div class="dialog-header">
          <h3>修改密码</h3>
        </div>
        <div class="dialog-body">
          <p>您正在使用默认密码登录，请修改密码以确保账户安全。</p>
          <div class="form-group">
            <label for="newPassword">新密码</label>
            <input
              type="password"
              id="newPassword"
              v-model="changePasswordForm.newPassword"
              placeholder="请输入新密码"
              class="form-control"
            >
          </div>
          <div class="form-group">
            <label for="confirmPassword">确认新密码</label>
            <input
              type="password"
              id="confirmPassword"
              v-model="changePasswordForm.confirmPassword"
              placeholder="请确认新密码"
              class="form-control"
            >
          </div>
          <div v-if="changePasswordError" class="error-message">
            {{ changePasswordError }}
          </div>
          <div class="form-actions">
            <button @click="changePassword" class="login-button" :disabled="isLoading">
              {{ isLoading ? '处理中...' : '确认修改' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import axios from 'axios'

const emit = defineEmits(['login-success'])

// 状态管理
const isRegister = ref(false)
const isLoading = ref(false)
const errorMessage = ref('')
const showChangePasswordDialog = ref(false)
const changePasswordError = ref('')

const userForm = reactive({
  username: '',
  password: '',
  fullName: ''
})

const changePasswordForm = reactive({
  newPassword: '',
  confirmPassword: ''
})

// 切换注册/登录模式
const toggleMode = () => {
  isRegister.value = !isRegister.value
  errorMessage.value = ''
}

// 处理用户登录或注册
const handleUserAction = async () => {
  if (!userForm.username || !userForm.password) {
    errorMessage.value = '用户名和密码不能为空'
    return
  }

  try {
    isLoading.value = true
    errorMessage.value = ''

    const endpoint = isRegister.value ? '/api/register' : '/api/login'

    const payload = {
      // User Info
      username: userForm.username,
      password: userForm.password,
      fullName: userForm.fullName
    }

    const response = await axios.post(endpoint, payload)

    if (response.data.success) {
      if (isRegister.value) {
        // 注册成功后自动切换到登录模式
        alert('注册成功，请登录')
        isRegister.value = false
        errorMessage.value = ''
      } else {
        // 登录成功
        const userInfo = response.data.data
        localStorage.setItem('userInfo', JSON.stringify(userInfo))
        
        // 检查密码是否为默认密码
        if (userForm.password === '000000') {
          // 显示强制修改密码弹窗
          showChangePasswordDialog.value = true
        } else {
          // 正常登录
          emit('login-success', userInfo)
        }
      }
    } else {
      errorMessage.value = response.data.message || (isRegister.value ? '注册失败' : '登录失败')
    }
  } catch (error) {
    console.error('请求错误:', error)
    errorMessage.value = '连接服务器失败'
  } finally {
    isLoading.value = false
  }
}

// 修改密码
const changePassword = async () => {
  if (!changePasswordForm.newPassword || !changePasswordForm.confirmPassword) {
    changePasswordError.value = '新密码和确认密码不能为空'
    return
  }

  if (changePasswordForm.newPassword !== changePasswordForm.confirmPassword) {
    changePasswordError.value = '两次输入的密码不一致'
    return
  }

  try {
    isLoading.value = true
    changePasswordError.value = ''

    const response = await axios.post('/api/user/change-password', {
      username: userForm.username,
      oldPassword: '000000',
      newPassword: changePasswordForm.newPassword
    })

    if (response.data.success) {
      alert('密码修改成功')
      showChangePasswordDialog.value = false
      // 重新登录
      const loginResponse = await axios.post('/api/login', {
        username: userForm.username,
        password: changePasswordForm.newPassword,
        fullName: ''
      })
      if (loginResponse.data.success) {
        localStorage.setItem('userInfo', JSON.stringify(loginResponse.data.data))
        emit('login-success', loginResponse.data.data)
      }
    } else {
      changePasswordError.value = response.data.message || '密码修改失败'
    }
  } catch (error) {
    console.error('修改密码失败:', error)
    changePasswordError.value = '连接服务器失败'
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-form {
  background: white;
  padding: 40px;
  border-radius: 10px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
  min-height: 400px;
  display: flex;
  flex-direction: column;
}

.login-title {
  text-align: center;
  margin-bottom: 20px;
  color: black;
  font-size: 24px;
  font-weight: 600;
}

.step-title {
  font-size: 18px;
  color: #555;
  margin-bottom: 20px;
  text-align: center;
  font-weight: 500;
}

.step-header {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
}

.back-button {
  position: absolute;
  left: 0;
  background: none;
  border: none;
  color: #667eea;
  cursor: pointer;
  font-size: 12px;
  padding: 0;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #555;
  font-size: 14px;
  font-weight: 500;
}

.form-control {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.3s ease;
  box-sizing: border-box;
}

.form-control:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.error-message {
  background: #fee;
  color: #d32f2f;
  padding: 10px;
  border-radius: 6px;
  margin-bottom: 20px;
  font-size: 14px;
}

.login-button {
  width: 100%;
  padding: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: opacity 0.3s ease;
  margin-top: 10px;
}

.login-button:hover:not(:disabled) {
  opacity: 0.9;
}

.login-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.toggle-action {
  margin-top: 20px;
  text-align: center;
  font-size: 14px;
  color: #666;
}

.toggle-action a {
  color: #667eea;
  text-decoration: none;
  font-weight: 500;
}

.toggle-action a:hover {
  text-decoration: underline;
}

@media (max-width: 480px) {
  .login-form {
    padding: 30px 20px;
  }

  .login-title {
    font-size: 20px;
  }
}

/* 弹窗样式 */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.dialog {
  background-color: white;
  border-radius: 8px;
  width: 400px;
  max-width: 90%;
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid #E0E0E0;
}

.dialog-header h3 {
  margin: 0;
  color: #333;
}

.dialog-body {
  padding: 24px;
}

.dialog-body p {
  margin-bottom: 20px;
  color: #555;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}
</style>
