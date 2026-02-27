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
          <label for="username">用户名</label>
          <input
            type="text"
            id="username"
            v-model="userForm.username"
            placeholder="请输入用户名"
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

const userForm = reactive({
  username: '',
  password: '',
  fullName: ''
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
        localStorage.setItem('userInfo', JSON.stringify(response.data.data))
        emit('login-success', response.data.data)
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
  color: #333;
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
</style>
