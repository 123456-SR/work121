<template>
  <div id="app">
    <div v-if="!currentView" class="home-container">
      <h1>表格管理系统</h1>

      <div class="tabs">
        <button class="tab-button" :class="{ active: activeTab === 'preliminary' }" @click="activeTab = 'preliminary'">前置</button>
        <button class="tab-button" :class="{ active: activeTab === 'report' }" @click="activeTab = 'report'">报告</button>
      </div>

      <div v-show="activeTab === 'preliminary'" class="section active">
        <div class="button-container">
          <button class="nav-button" @click="currentView = 'Entrustment'">检测（前）委托单（带合同）</button>
          <button class="nav-button" @click="currentView = 'LightDynamicPenetrationRecord'">轻型动力触探检测记录表</button>
          <button class="nav-button" @click="currentView = 'NuclearDensityRecord'">原位密度检测记录表（核子法）</button>
          <button class="nav-button" @click="currentView = 'SandReplacementRecord'">原位密度检测记录表（灌砂法）</button>
          <button class="nav-button" @click="currentView = 'WaterReplacementRecord'">相对密度试验记录表（灌水法）</button>
          <button class="nav-button" @click="currentView = 'CuttingRingRecord'">原位密度检测记录表（环刀法）</button>
          <button class="nav-button" @click="currentView = 'ReboundMethodRecord'">回弹法检测混凝土抗压强度记录表</button>
          <button class="nav-button" @click="currentView = 'BeckmanBeamRecord'">路基路面回弹弯沉试验检测记录表（贝克曼梁法）</button>
          <button class="nav-button" style="background-color: #28a745;" @click="currentView = 'Signature'">电子签名</button>
        </div>
      </div>

      <div v-show="activeTab === 'report'" class="section active">
        <div class="button-container">
          <button class="nav-button" @click="currentView = 'DensityTestReport'">原位密度检测报告</button>
          <button class="nav-button" @click="currentView = 'DensityTestResult'">原位密度检测结果</button>
          <button class="nav-button" @click="currentView = 'LightDynamicPenetration'">轻型动力触探检测报告</button>
          <button class="nav-button" @click="currentView = 'LightDynamicPenetrationResult'">轻型动力触探检测结果</button>
          <button class="nav-button" @click="currentView = 'ReboundMethodReport'">回弹法检测混凝土抗压强度报告</button>
          <button class="nav-button" @click="currentView = 'BeckmanBeamReport'">路基路面回弹弯沉（回弹模量）检测报告</button>
          <button class="nav-button" @click="currentView = 'BeckmanBeamResult'">路基路面回弹弯沉（回弹模量）检测结果</button>
        </div>
      </div>
    </div>

    <div v-else>
      <component :is="components[currentView]" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import Entrustment from './components/Entrustment.vue'
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

const activeTab = ref('preliminary')
const currentView = ref('')

const components = {
  Entrustment,
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
  BeckmanBeamResult
}

// Handle Back Navigation (simple hash check or window event if needed, but for now simple state)
// Since components use <a href="/">, they will reload the page, which effectively resets currentView to '' (Home).
// This works without extra logic.
</script>

<style>
body {
  margin: 0;
  padding: 0;
}

.home-container {
  font-family: 'Microsoft YaHei', Arial, sans-serif;
  text-align: center;
  background-color: #f4f6f9;
  min-height: 100vh;
  padding-bottom: 20px;
  overflow: auto;
}

h1 {
  color: #333;
  margin-top: 50px;
  margin-bottom: 40px;
}

.button-container {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.nav-button {
  padding: 20px 40px;
  font-size: 18px;
  cursor: pointer;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 8px;
  text-decoration: none;
  transition: background-color 0.3s;
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
}

.nav-button:hover {
  background-color: #0056b3;
  transform: translateY(-2px);
}

.tabs {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
  gap: 10px;
}

.tab-button {
  padding: 10px 30px;
  font-size: 20px;
  cursor: pointer;
  background-color: #e9ecef;
  border: none;
  border-radius: 5px;
  color: #495057;
  transition: all 0.3s;
}

.tab-button.active {
  background-color: #007bff;
  color: white;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.section {
  animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>