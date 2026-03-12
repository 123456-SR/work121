<template>
  <div class="user-management">
    <h2>用户管理</h2>
    <div class="toolbar">
      <button class="btn" @click="addUser">添加用户</button>
    </div>
    <table class="user-table">
      <thead>
        <tr>
          <th>账号</th>
          <th>姓名</th>
          <th>职务</th>
          <th>电话号码</th>
          <th>邮箱</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="user in users" :key="user.id">
          <td>{{ user.userAccount }}</td>
          <td>{{ user.userName }}</td>
          <td>{{ user.position }}</td>
          <td>{{ user.userTel || '-' }}</td>
          <td>{{ user.userEmail || '-' }}</td>
          <td>
            <button class="btn btn-edit" @click="editUser(user)">编辑</button>
            <button class="btn btn-delete" @click="deleteUser(user)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- 添加/编辑用户对话框 -->
    <div v-if="showDialog" class="dialog-overlay">
      <div class="dialog">
        <div class="dialog-header">
          <h3>{{ editingUser ? '编辑用户' : '添加用户' }}</h3>
          <button class="dialog-close" @click="showDialog = false">&times;</button>
        </div>
        <div class="dialog-body">
          <form @submit.prevent="saveUser">
            <div class="form-group">
              <label>账号</label>
              <input v-model="formData.userAccount" type="text" required>
            </div>
            <div class="form-group">
              <label>姓名</label>
              <input v-model="formData.userName" type="text" required>
            </div>
            <div class="form-group" v-if="editingUser">
              <label>密码</label>
              <input v-model="formData.password" type="password" placeholder="留空表示不修改密码">
            </div>
            <div class="form-group">
              <label>职务</label>
              <select v-model="formData.position" required>
                <option value="员工">检测人员</option>
                <option value="任务分配人员">任务分配人员</option>
                <option value="审核人员">审核人员</option>
                <option value="管理员">管理员</option>
              </select>
            </div>
            <div class="form-group">
              <label>电话号码</label>
              <input v-model="formData.userTel" type="text">
            </div>
            <div class="form-group">
              <label>邮箱</label>
              <input v-model="formData.userEmail" type="email">
            </div>
            <div class="form-group">
              <label>部门</label>
              <input v-model="formData.organizationName" type="text">
            </div>
            <div class="form-group">
              <label>3H对接标识</label>
              <input v-model="formData.shType" type="text">
            </div>
            <div class="form-actions">
              <button type="button" class="btn btn-cancel" @click="showDialog = false">取消</button>
              <button type="submit" class="btn btn-submit">保存</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const users = ref([])
const showDialog = ref(false)
const editingUser = ref(null)
const formData = ref({
  userAccount: '',
  userName: '',
  password: '',
  position: '员工',
  userTel: '',
  userEmail: '',
  organizationName: '',
  shType: ''
})

// 获取用户列表
const fetchUsers = async () => {
  try {
    const response = await fetch('/api/user/list')
    const result = await response.json()
    if (result.success) {
      users.value = result.data
    } else {
      console.error('获取用户列表失败:', result.message)
    }
  } catch (error) {
    console.error('获取用户列表异常:', error)
  }
}

// 添加用户
const addUser = () => {
  editingUser.value = null
  formData.value = {
    userAccount: '',
    userName: '',
    password: '000000',
    position: '员工',
    userTel: '',
    userEmail: '',
    organizationName: '',
    shType: ''
  }
  showDialog.value = true
}

// 删除用户
const deleteUser = async (user) => {
  if (confirm(`确定要删除用户 ${user.userName} 吗？`)) {
    try {
      const response = await fetch('/api/user/delete', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ userId: user.id })
      })
      const result = await response.json()
      if (result.success) {
        alert('删除用户成功')
        // 重新获取用户列表
        fetchUsers()
      } else {
        alert('删除用户失败: ' + result.message)
      }
    } catch (error) {
      console.error('删除用户失败:', error)
      alert('操作失败: 网络错误')
    }
  }
}

// 编辑用户
const editUser = (user) => {
  editingUser.value = user
  formData.value = {
    userAccount: user.userAccount,
    userName: user.userName,
    password: '',
    position: user.position,
    userTel: user.userTel || '',
    userEmail: user.userEmail || '',
    organizationName: user.organizationName || '',
    shType: user.shType || ''
  }
  showDialog.value = true
}

// 保存用户
const saveUser = async () => {
  try {
    let apiUrl = '/api/user/add'
    let successMessage = '添加用户成功'
    let errorMessage = '添加用户失败'

    const requestBody = {
      userAccount: formData.value.userAccount,
      userPass: editingUser.value ? formData.value.password : '000000',
      userName: formData.value.userName,
      position: formData.value.position,
      userTel: formData.value.userTel,
      userEmail: formData.value.userEmail,
      organizationName: formData.value.organizationName,
      shType: formData.value.shType
    }

    // 如果是编辑用户，调用更新API
    if (editingUser.value) {
      apiUrl = '/api/user/update'
      successMessage = '更新用户成功'
      errorMessage = '更新用户失败'
      requestBody.userId = editingUser.value.id
    }

    const response = await fetch(apiUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(requestBody)
    })
    const result = await response.json()
    if (result.success) {
      alert(successMessage)
      showDialog.value = false
      // 重新获取用户列表
      fetchUsers()
    } else {
      alert(errorMessage + ': ' + result.message)
    }
  } catch (error) {
    console.error('保存用户失败:', error)
    alert('操作失败: 网络错误')
  }
}

// 组件挂载时获取用户列表
onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.user-management {
  background-color: white;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.toolbar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 16px;
  margin-top: -10px;
}

.toolbar .btn {
  padding: 10px 20px;
  font-size: 16px;
  font-weight: 500;
}

.user-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 8px;
}

.user-table th,
.user-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #E0E0E0;
}

.user-table th {
  background-color: var(--card-blue);
  color: var(--color-blue);
  font-weight: 500;
}

.user-table tr:hover {
  background-color: var(--bg-primary);
}

.btn-edit {
  background-color: #FFE0B2;
  color: #F57C00;
  margin-right: 8px;
}

.btn-delete {
  background-color: #FFCDD2;
  color: #C62828;
}

/* 对话框样式 */
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
  color: var(--text-primary);
}

.dialog-close {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: var(--text-light);
}

.dialog-body {
  padding: 24px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: var(--text-normal);
  font-size: 14px;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #E0E0E0;
  border-radius: 4px;
  font-size: 14px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}

.btn-cancel {
  background-color: #F5F5F5;
  color: var(--text-normal);
}

.btn-submit {
  background-color: var(--color-blue);
  color: white;
}

.btn-submit:hover {
  background-color: #1976D2;
}
</style>
