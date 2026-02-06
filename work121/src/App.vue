<template>
  <div id="app">
    <!-- 登录页面 -->
    <Login 
      v-if="!isLoggedIn" 
      @login-success="handleLoginSuccess" 
    />
    
    <!-- 主应用界面 -->
    <div v-else class="app-container">
      <!-- 左侧导航栏 -->
      <aside class="sidebar">
        <div class="sidebar-header">
          <h1>表格管理系统</h1>
        </div>
        <nav class="nav-menu">
          <div class="nav-section">
            <div class="section-title">前置表格</div>
            <div v-for="item in menuItems.preliminary" :key="item.id" @click="navigateTo(item)" :class="['nav-item', { active: currentView === item.id }]">
              <span class="nav-item-text">{{ item.name }}</span>
            </div>
          </div>
          <div class="nav-section">
            <div class="section-title">报告表格</div>
            <div v-for="item in menuItems.report" :key="item.id" @click="navigateTo(item)" :class="['nav-item', { active: currentView === item.id }]">
              <span class="nav-item-text">{{ item.name }}</span>
            </div>
          </div>
        </nav>
        <div class="sidebar-footer">
          <button @click="logout" class="logout-button">退出登录</button>
        </div>
      </aside>

      <!-- 右侧内容区域 -->
      <main class="main-content">
        <header class="content-header">
        <div class="header-title">{{ currentPageTitle }}</div>
        <div class="header-actions">
          <span class="user-info">{{ getCurrentUserName() }}</span>
          <button class="btn btn-primary" @click="refreshPage">刷新</button>
          <button class="btn btn-success" @click="printPage">打印</button>
        </div>
      </header>
        <div class="content-wrapper">
          <component v-if="currentView" :is="components[currentView]" v-bind="currentProps" />
          <div v-else class="welcome-message">
            <h2>欢迎使用表格管理系统</h2>
            <p>请从左侧导航栏选择要操作的表格</p>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, provide, shallowRef } from 'vue'
import Entrustment from './components/Entrustment.vue'
import EntrustmentList from './components/EntrustmentList.vue'
import LightDynamicPenetrationRecord from './components/LightDynamicPenetrationRecord.vue'
import NuclearDensityRecord from './components/NuclearDensityRecord.vue'
import SandReplacementRecord from './components/SandReplacementRecord.vue'
import WaterReplacementRecord from './components/WaterReplacementRecord.vue'
import CuttingRingRecord from './components/CuttingRingRecord.vue'
import ReboundMethodRecord from './components/ReboundMethodRecord.vue'
import BeckmanBeamRecord from './components/BeckmanBeamRecord.vue'
import Signature from './components/Signature.vue'
import DensityTestReport from './components/DensityTestReport.vue'
import DensityTestResult from './components/DensityTestResult.vue'
import LightDynamicPenetration from './components/LightDynamicPenetration.vue'
import LightDynamicPenetrationResult from './components/LightDynamicPenetrationResult.vue'
import ReboundMethodReport from './components/ReboundMethodReport.vue'
import BeckmanBeamReport from './components/BeckmanBeamReport.vue'
import BeckmanBeamResult from './components/BeckmanBeamResult.vue'
import DirectoryList from './components/DirectoryList.vue'
import Login from './components/Login.vue'

const currentView = ref('')
const currentProps = ref({})
const currentPageTitle = ref('欢迎使用表格管理系统')
const isLoggedIn = ref(false)

const components = {
  Entrustment,
  EntrustmentList,
  LightDynamicPenetrationRecord,
  NuclearDensityRecord,
  SandReplacementRecord,
  WaterReplacementRecord,
  CuttingRingRecord,
  ReboundMethodRecord,
  BeckmanBeamRecord,
  Signature,
  DensityTestReport,
  DensityTestResult,
  LightDynamicPenetration,
  LightDynamicPenetrationResult,
  ReboundMethodReport,
  BeckmanBeamReport,
  BeckmanBeamResult,
  DirectoryList
}

const menuItems = {
  preliminary: [
    { id: 'DirectoryList', name: '流程管理' },
    { id: 'EntrustmentList', name: '检测委托单' },
    { id: 'LightDynamicPenetrationRecord', name: '轻型动力触探检测记录表' },
    { id: 'NuclearDensityRecord', name: '原位密度检测记录表（核子法）' },
    { id: 'SandReplacementRecord', name: '原位密度检测记录表（灌砂法）' },
    { id: 'WaterReplacementRecord', name: '相对密度试验记录表（灌水法）' },
    { id: 'CuttingRingRecord', name: '原位密度检测记录表（环刀法）' },
    { id: 'ReboundMethodRecord', name: '回弹法检测混凝土抗压强度记录表' },
    { id: 'BeckmanBeamRecord', name: '路基路面回弹弯沉试验检测记录表' },
    { id: 'Signature', name: '电子签名' }
  ],
  report: [
    { id: 'DensityTestReport', name: '原位密度检测报告' },
    { id: 'DensityTestResult', name: '原位密度检测结果' },
    { id: 'LightDynamicPenetration', name: '轻型动力触探检测报告' },
    { id: 'LightDynamicPenetrationResult', name: '轻型动力触探检测结果' },
    { id: 'ReboundMethodReport', name: '回弹法检测混凝土抗压强度报告' },
    { id: 'BeckmanBeamReport', name: '路基路面回弹弯沉检测报告' },
    { id: 'BeckmanBeamResult', name: '路基路面回弹弯沉检测结果' }
  ]
}

// 导航逻辑
const navigateTo = (target, props = {}) => {
  if (typeof target === 'string') {
    // 查找标题
    let title = ''
    if (menuItems.preliminary.find(i => i.id === target)) title = menuItems.preliminary.find(i => i.id === target).name
    else if (menuItems.report.find(i => i.id === target)) title = menuItems.report.find(i => i.id === target).name
    else if (target === 'Entrustment') title = '检测委托单详情'
    
    currentView.value = target
    currentPageTitle.value = title
    currentProps.value = props
  } else {
    currentView.value = target.id
    currentPageTitle.value = target.name
    currentProps.value = props
  }
}

provide('navigateTo', navigateTo)

const refreshPage = () => {
  // For Vue components, we can just re-mount the component or trigger a refresh
  const currentComp = currentView.value
  currentView.value = ''
  setTimeout(() => {
    currentView.value = currentComp
  }, 100)
}

const printPage = () => {
  window.print()
}

// 检查登录状态
const checkLoginStatus = () => {
  const token = localStorage.getItem('token')
  const userInfo = localStorage.getItem('userInfo')
  isLoggedIn.value = !!token && !!userInfo
}

// 处理登录成功
const handleLoginSuccess = () => {
  isLoggedIn.value = true
  // 登录后默认显示目录列表
  navigateTo({ id: 'DirectoryList', name: '目录管理' })
}

// 退出登录
const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  isLoggedIn.value = false
  currentView.value = ''
  currentPageTitle.value = ''
}

// 获取当前登录用户的姓名
const getCurrentUserName = () => {
  try {
    const userInfoStr = localStorage.getItem('userInfo')
    if (userInfoStr) {
      const userInfo = JSON.parse(userInfoStr)
      return userInfo.userName || userInfo.username || '未知用户'
    }
    return ''
  } catch (error) {
    console.error('获取用户信息失败:', error)
    return ''
  }
}

// 组件挂载时检查登录状态
onMounted(() => {
  checkLoginStatus()
})
</script>

<style>
* { margin: 0; padding: 0; box-sizing: border-box; }
body { font-family: 'Microsoft YaHei', Arial, sans-serif; background-color: #f5f7fa; }
#app { width: 100%; height: 100vh; }
.app-container { display: flex; height: 100vh; overflow: hidden; }

/* 左侧导航栏 */
.sidebar {
width: 280px;
background: linear-gradient(180deg, #2c3e50 0%, #34495e 100%);
color: white;
display: flex;
flex-direction: column;
box-shadow: 2px 0 8px rgba(0,0,0,0.15);
}
.sidebar-header {
padding: 20px;
text-align: center;
border-bottom: 1px solid rgba(255,255,255,0.1);
}
.sidebar-header h1 {
font-size: 20px;
font-weight: 600;
background: linear-gradient(90deg, #3498db, #2ecc71);
-webkit-background-clip: text;
-webkit-text-fill-color: transparent;
background-clip: text;
margin: 0;
}
.nav-menu {
flex: 1;
overflow-y: auto;
padding: 15px 0;
}
.nav-section {
margin-bottom: 20px;
}
.section-title {
padding: 10px 20px;
font-size: 12px;
font-weight: 600;
color: #95a5a6;
text-transform: uppercase;
letter-spacing: 1px;
}
.nav-item {
 display: flex;
 align-items: center;
 padding: 12px 20px;
 cursor: pointer;
 transition: all 0.3s ease;
 border-left: 3px solid transparent;
}
.nav-item:hover {
background: rgba(255,255,255,0.1);
border-left-color: #3498db;
}
.nav-item.active {
background: rgba(52, 152, 219, 0.3);
border-left-color: #3498db;
}

.nav-item-text {
font-size: 14px;
}

/* 侧边栏底部 */
.sidebar-footer {
padding: 20px;
border-top: 1px solid rgba(255,255,255,0.1);
margin-top: auto;
}

.logout-button {
width: 100%;
padding: 10px;
background: rgba(231, 76, 60, 0.2);
color: #e74c3c;
border: 1px solid rgba(231, 76, 60, 0.4);
border-radius: 4px;
font-size: 14px;
cursor: pointer;
transition: all 0.3s ease;
}

.logout-button:hover {
background: rgba(231, 76, 60, 0.3);
}

/* 右侧内容区域 */
.main-content {
flex: 1;
display: flex;
flex-direction: column;
overflow: hidden;
}
.content-header {
height: 60px;
border-bottom: 1px solid #e0e0e0;
display: flex;
align-items: center;
justify-content: space-between;
padding: 0 30px;
background: white;
box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}
.header-title {
font-size: 18px;
font-weight: 600;
color: #2c3e50;
}
.header-actions {
 display: flex;
 gap: 10px;
 align-items: center;
}

.user-info {
 font-size: 14px;
 color: #333;
 font-weight: 500;
 margin-right: 10px;
 padding: 6px 12px;
 background: #f5f7fa;
 border-radius: 4px;
 border: 1px solid #e0e0e0;
}
.btn {
padding: 8px 16px;
border: none;
border-radius: 4px;
cursor: pointer;
font-size: 14px;
transition: all 0.3s ease;
}
.btn-primary {
background: #3498db;
color: white;
}
.btn-primary:hover {
background: #2980b9;
}
.btn-success {
background: #2ecc71;
color: white;
}
.btn-success:hover {
background: #27ae60;
}

/* 内容区域 */
.content-wrapper {
flex: 1;
overflow: auto;
padding: 20px;
background: #f5f7fa;
}
.welcome-message {
text-align: center;
padding: 100px 20px;
}
.welcome-message h2 {
color: #2c3e50;
margin-bottom: 20px;
}
.welcome-message p {
color: #7f8c8d;
font-size: 16px;
}

/* 响应式设计 */
@media (max-width: 768px) {
.sidebar {
width: 60px;
}
.sidebar-header h1 {
 display: none;
}
.nav-item-text {
 display: none;
}
.nav-item-icon {
 margin-right: 0;
}
.section-title {
 display: none;
}
}

/* 滚动条样式 */
::-webkit-scrollbar {
width: 8px;
height: 8px;
}
::-webkit-scrollbar-track {
background: #f1f1f1;
}
::-webkit-scrollbar-thumb {
background: #c1c1c1;
border-radius: 4px;
}
::-webkit-scrollbar-thumb:hover {
background: #a8a8a8;
}
</style>