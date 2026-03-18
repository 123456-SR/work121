<template>
  <div class="dashboard-container">
    <div v-if="showRejectBanner" class="reject-banner" :class="{ pulse: hasNewRejects }">
      <div class="reject-banner-left">
        <div class="reject-banner-title">被打回提醒</div>
        <div class="reject-banner-sub">你有 {{ rejectedCount }} 条被打回任务，请优先处理打回原因。</div>
      </div>
      <div class="reject-banner-actions">
        <button class="btn btn-small btn-alert" @click="scrollToRejected">查看</button>
        <button class="btn btn-small" @click="dismissRejectBanner">已知晓</button>
      </div>
    </div>

    <!-- 数据卡片区 -->
    <div class="data-cards">
      <div class="data-card blue">
        <div class="label">我的总任务</div>
        <div class="num">{{ totalTasks }}</div>
      </div>
      <div class="data-card yellow">
        <div class="label">待处理任务</div>
        <div class="num">{{ pendingTasks }}</div>
      </div>
      <div class="data-card green">
        <div class="label">已完成任务</div>
        <div class="num">{{ completedTasks }}</div>
      </div>
      <div class="data-card red clickable" @click="scrollToRejected">
        <div class="label">被打回</div>
        <div class="num">{{ rejectedCount }}</div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="chart-area">
      <div class="chart-card">
        <div class="chart-title">任务概览</div>
        <div class="overview-content">
          <div class="overview-left">
            <div class="chart-container" ref="statusChartEl" style="height: 300px;"></div>
          </div>
          <div class="overview-right">
            <div class="overview-right-grid">
              <div class="right-left">
                <div class="kpi-grid">
                  <div class="kpi-card">
                    <div class="kpi-label">类型数</div>
                    <div class="kpi-value">{{ kpi.typeCount }}</div>
                  </div>
                  <div class="kpi-card">
                    <div class="kpi-label">Top 类型</div>
                    <div class="kpi-value">{{ kpi.topTypeName }}</div>
                    <div class="kpi-sub">{{ kpi.topTypeCount }} 个 · {{ kpi.topTypePct }}%</div>
                  </div>
                  <div class="kpi-card">
                    <div class="kpi-label">完成率</div>
                    <div class="kpi-value">{{ kpi.doneRate }}%</div>
                    <div class="kpi-sub">已完成 {{ completedTasks }} / 全部 {{ totalTasks }}</div>
                  </div>
                  <div class="kpi-card">
                    <div class="kpi-label">待提交占比</div>
                    <div class="kpi-value">{{ kpi.submitPct }}%</div>
                    <div class="kpi-sub">待提交 {{ kpi.submitCount }} / 待处理 {{ kpi.pendingTotal }}</div>
                  </div>
                  <div class="kpi-card">
                    <div class="kpi-label">平均完成时长</div>
                    <div class="kpi-value">{{ kpi.avgDurationStr }}</div>
                    <div class="kpi-sub">基于已完成任务</div>
                  </div>
                </div>
                <div class="trend-wrapper">
                  <div class="trend-title">
                    近7天待处理新增
                    <span class="change-badge" :class="kpi.dodClass">日环比 {{ kpi.dodPct }}%</span>
                    <span class="change-badge" :class="kpi.wowClass">周环比 {{ kpi.wowPct }}%</span>
                  </div>
                  <div class="trend-chart" ref="trendChartEl"></div>
                </div>
              </div>
              <div class="right-right">
                <div class="summary-table">
                  <div class="summary-header">
                    <div class="col name">类型</div>
                    <div class="col count">数量</div>
                    <div class="col pct">占比</div>
                  </div>
                  <div class="summary-row clickable" v-for="row in topTypes" :key="row.name" @click="handleTopTypeClick(row)">
                    <div class="col name">{{ row.name }}</div>
                    <div class="col count">{{ row.count }}</div>
                    <div class="col pct">{{ row.pct }}%</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 被打回任务列表 -->
    <div class="list-card" ref="rejectedSectionEl">
      <div class="list-title">
        被打回任务
        <span v-if="currentTypeFilter" class="filter-badge">筛选：{{ currentTypeFilter }}</span>
        <button v-if="currentTypeFilter" class="btn btn-small" @click="clearTypeFilter">清除筛选</button>
      </div>
      <div class="search-bar">
        <input v-model="keyword" placeholder="搜索项目名/编号/打回原因" />
        <button class="btn" @click="applyKeywordFilter">查询</button>
      </div>
      <table class="task-table">
        <thead>
          <tr>
            <th>项目/编号</th>
            <th>类型</th>
            <th>打回原因</th>
            <th>打回时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="displayedTasks.length === 0">
            <td colspan="5" class="empty-row">暂无被打回任务</td>
          </tr>
          <tr v-for="row in displayedTasks" :key="row._id">
            <td>{{ row.project_name || row.unified_number || '-' }}</td>
            <td>{{ row.table_type }}</td>
            <td class="reason-cell" :title="getRejectReason(row)">
              <span v-if="isRecentReject(row)" class="new-pill">新</span>
              {{ getRejectReason(row) }}
            </td>
            <td>{{ formatTime(row.update_time || row.create_time) }}</td>
            <td>
              <button class="btn btn-small" @click="viewDetails(row)">查看</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, inject } from 'vue'
import axios from 'axios'
import * as echarts from 'echarts'

// 任务数据
const totalTasks = ref(0)
const pendingTasks = ref(0)
const completedTasks = ref(0)
const rejectedCount = ref(0)
const statusChartEl = ref(null)
let statusChart = null
const trendChartEl = ref(null)
let trendChart = null
const typeSummary = ref([])
const kpi = ref({
  typeCount: 0,
  topTypeName: '-',
  topTypeCount: 0,
  topTypePct: 0,
  submitPct: 0,
  submitCount: 0,
  pendingTotal: 0,
  last7Added: 0,
  doneRate: 0,
  avgDurationStr: '-',
  dodPct: 0,
  wowPct: 0,
  dodClass: '',
  wowClass: ''
})
const topTypes = ref([])
const topTypeHeadNames = ref([])
const currentTypeFilter = ref('')
const keyword = ref('')
const allTasks = ref([])
const displayedTasks = ref([])
const navigateTo = inject('navigateTo')
const rejectedSectionEl = ref(null)
const showRejectBanner = ref(false)
const hasNewRejects = ref(false)
const onResize = () => {
  statusChart && statusChart.resize()
  trendChart && trendChart.resize()
}

const pickAny = (it, keys) => {
  for (const k of keys) {
    const v = it?.[k]
    if (v !== undefined && v !== null && v !== '') return v
  }
  return null
}

const unwrapList = (data) => {
  if (Array.isArray(data)) return data
  if (!data) return []
  if (Array.isArray(data.records)) return data.records
  if (Array.isArray(data.list)) return data.list
  if (Array.isArray(data.rows)) return data.rows
  if (Array.isArray(data.items)) return data.items
  return []
}

const normalizeTask = (raw, statusOverride) => {
  const tableType = pickAny(raw, ['table_type', 'TABLE_TYPE', 'tableType', 'taskType', 'TASK_TYPE']) || '其他'
  const createTime = pickAny(raw, ['create_time', 'CREATE_TIME', 'createTime', 'created_at', 'CREATED_AT', 'createdTime'])
  const unifiedNumber = pickAny(raw, ['unified_number', 'UNIFIED_NUMBER', 'unifiedNumber', 'wtNum', 'WT_NUM'])
  const projectName = pickAny(raw, ['project_name', 'PROJECT_NAME', 'projectName'])
  const clientUnit = pickAny(raw, ['client_unit', 'CLIENT_UNIT', 'clientUnit'])
  const dataId = pickAny(raw, ['data_id', 'DATA_ID', 'dataId', 'id', 'ID'])
  const updateTime = pickAny(raw, ['update_time', 'UPDATE_TIME', 'updateTime', 'updated_at', 'UPDATED_AT'])
  const completedTime = pickAny(raw, ['completed_time', 'COMPLETED_TIME', 'complete_time', 'COMPLETE_TIME', 'finish_time', 'FINISH_TIME', 'finished_at', 'FINISHED_AT'])
  const rejectReason = pickAny(raw, ['rejectReason', 'REJECT_REASON', 'reject_reason', 'sendBackReason', 'SEND_BACK_REASON', 'send_back_reason'])
  const status = statusOverride ?? pickAny(raw, ['status', 'STATUS', 'task_status', 'TASK_STATUS'])
  return {
    ...raw,
    table_type: tableType,
    create_time: createTime,
    unified_number: unifiedNumber,
    project_name: projectName,
    client_unit: clientUnit,
    data_id: dataId,
    update_time: updateTime,
    completed_time: completedTime,
    reject_reason: rejectReason,
    status
  }
}

const parseStatusNum = (v) => {
  if (v === null || v === undefined || v === '') return null
  const n = parseInt(v)
  return Number.isNaN(n) ? null : n
}

const normalizeTaskStage = (status) => {
  const s = parseStatusNum(status)
  if (s === null) return null
  if (s === 15 || s === 25) return 4
  if (s === 5) return 4
  if (s >= 10) return s % 10
  return s
}

const isDashboardRejectedStatus = (status) => {
  const s = parseStatusNum(status)
  return s === 2 || s === 7
}

// 获取当前登录用户信息
const getCurrentUser = () => {
  try {
    const userInfoStr = localStorage.getItem('userInfo')
    if (userInfoStr) {
      const userInfo = JSON.parse(userInfoStr)
      return userInfo.userAccount || userInfo.username || userInfo.userName || ''
    }
    return ''
  } catch (error) {
    console.error('获取用户信息失败:', error)
    return ''
  }
}

// 加载用户任务数据
const loadUserTasks = async () => {
  const userAccount = getCurrentUser()
  if (!userAccount) return
  
  try {
    const endpoints = ['/api/pending-tasks/get-by-user', '/api/pending-tasks/get-all']

    const fetchList = async (status) => {
      const params = { taskStatus: status, userAccount }
      for (const url of endpoints) {
        try {
          const res = await axios.get(url, { params })
          if (res?.data?.success) {
            const list = unwrapList(res.data.data)
            if (list.length) return list.map(it => normalizeTask(it, status))
            return []
          }
        } catch (e) {
          continue
        }
      }
      return []
    }

    const fetchAll = async () => {
      const params = { userAccount }
      for (const url of endpoints) {
        try {
          const res = await axios.get(url, { params })
          if (res?.data?.success) {
            const list = unwrapList(res.data.data)
            return list.map(it => normalizeTask(it))
          }
        } catch (e) {
          continue
        }
      }
      return null
    }

    const dedupBy = (items) => {
      const map = new Map()
      for (const it of (Array.isArray(items) ? items : [])) {
        const key = String(it?.data_id || '') + '|' + String(it?.table_type || '') + '|' + String(it?.unified_number || '') + '|' + String(it?.create_time || '')
        if (!map.has(key)) map.set(key, it)
      }
      return Array.from(map.values())
    }

    const fetchMany = async (statuses) => {
      const lists = await Promise.all((statuses || []).map(s => fetchList(s)))
      return dedupBy(lists.flat())
    }

    const allList = await fetchAll()
    if (Array.isArray(allList) && allList.length) {
      const submitList = []
      const auditList = []
      const approvalList = []
      const doneList = []
      const rejectedList = []

      for (const it of allList) {
        const stage = normalizeTaskStage(it?.status)
        if (stage === 0) submitList.push(it)
        else if (stage === 1) auditList.push(it)
        else if (stage === 4) approvalList.push(it)
        else if (stage === 6) doneList.push(it)
        if (isDashboardRejectedStatus(it?.status)) rejectedList.push(it)
      }

      rejectedCount.value = rejectedList.length
      updateRejectBanner(rejectedList)

      const submitCount = submitList.length
      const auditCount = auditList.length
      const approvalCount = approvalList.length
      const doneCount = doneList.length
      pendingTasks.value = submitCount + auditCount + approvalCount
      completedTasks.value = doneCount
      totalTasks.value = pendingTasks.value + completedTasks.value

      renderStatusChart({ submit: submitCount, audit: auditCount, approval: approvalCount, done: doneCount })
      const trendSource = [...submitList, ...auditList, ...approvalList]
      const last7 = buildLast7Series(trendSource)
      renderTrendChart(last7)

      const typeMap = new Map()
      for (const item of [...submitList, ...auditList, ...approvalList]) {
        const key = item.table_type || '其他'
        typeMap.set(key, (typeMap.get(key) || 0) + 1)
      }
      typeSummary.value = Array.from(typeMap.entries()).sort((a, b) => b[1] - a[1]).map(([name, count]) => ({ name, count }))

      const pendingTotal = submitCount + auditCount + approvalCount
      const topItem = typeSummary.value[0]
      const topTypeName = topItem ? topItem.name : '-'
      const topTypeCount = topItem ? topItem.count : 0
      const toPct = (v, base) => base > 0 ? Math.round((v / base) * 100) : 0
      const submitPct = toPct(submitCount, pendingTotal)
      const topTypePct = toPct(topTypeCount, pendingTotal)
      const last7Added = countLast7Days([...submitList, ...auditList, ...approvalList])
      const doneRate = toPct(doneCount, pendingTotal + doneCount)
      const { avgStr } = calcAvgDuration(doneList)
      const { dodPct, wowPct, dodClass, wowClass } = calcChangeRates(trendSource)

      kpi.value = {
        typeCount: typeSummary.value.length,
        topTypeName,
        topTypeCount,
        topTypePct,
        submitPct,
        submitCount,
        pendingTotal,
        last7Added,
        doneRate,
        avgDurationStr: avgStr,
        dodPct,
        wowPct,
        dodClass,
        wowClass
      }

      const TOP_N = 5
      if (typeSummary.value.length > TOP_N) {
        const head = typeSummary.value.slice(0, TOP_N - 1)
        const tailSum = typeSummary.value.slice(TOP_N - 1).reduce((s, it) => s + it.count, 0)
        topTypeHeadNames.value = head.map(it => it.name)
        topTypes.value = [
          ...head.map(it => ({ name: it.name, count: it.count, pct: toPct(it.count, pendingTotal) })),
          { name: '其他(合并)', count: tailSum, pct: toPct(tailSum, pendingTotal) }
        ]
      } else {
        topTypeHeadNames.value = typeSummary.value.map(it => it.name)
        topTypes.value = typeSummary.value.map(it => ({ name: it.name, count: it.count, pct: toPct(it.count, pendingTotal) }))
      }

      allTasks.value = [...rejectedList]
        .map((it, idx) => ({ ...it, _id: (it.data_id || idx) + '_' + (it.status || '') }))
        .sort((a, b) => {
          const bt = b.update_time || b.create_time
          const at = a.update_time || a.create_time
          return new Date((bt?.replace?.(' ', 'T')) || bt || 0) - new Date((at?.replace?.(' ', 'T')) || at || 0)
        })
        .slice(0, 50)
      updateDisplayedTasks()
      return
    }

    const segments = [0, 10, 20]
    const expand = (baseCodes) => segments.flatMap(off => (baseCodes || []).map(c => String(c + off)))

    const submitStatuses = expand([0])
    const auditStatuses = expand([1])
    const approvalStatuses = Array.from(new Set([...expand([4]), ...expand([5])]))
    const doneStatuses = expand([6])
    const rejectedStatuses = ['2', '7']

    const [submitList, auditList, approvalList, doneList, rejectedList] = await Promise.all([
      fetchMany(submitStatuses),
      fetchMany(auditStatuses),
      fetchMany(approvalStatuses),
      fetchMany(doneStatuses),
      fetchMany(rejectedStatuses)
    ])
    rejectedCount.value = rejectedList.length
    updateRejectBanner(rejectedList)

    const submitCount = submitList.length
    const auditCount = auditList.length
    const approvalCount = approvalList.length
    const doneCount = doneList.length
    pendingTasks.value = submitCount + auditCount + approvalCount
    completedTasks.value = doneCount
    totalTasks.value = pendingTasks.value + completedTasks.value

    renderStatusChart({ submit: submitCount, audit: auditCount, approval: approvalCount, done: doneCount })
    const trendSource = [...submitList, ...auditList, ...approvalList]
    const last7 = buildLast7Series(trendSource)
    renderTrendChart(last7)

    // 类型分布：统计“待处理”（待提交+待审核+待批准）按 table_type 的数量
    const typeMap = new Map()
    for (const item of [...submitList, ...auditList, ...approvalList]) {
      const key = item.table_type || '其他'
      typeMap.set(key, (typeMap.get(key) || 0) + 1)
    }
    typeSummary.value = Array.from(typeMap.entries()).sort((a, b) => b[1] - a[1]).map(([name, count]) => ({ name, count }))

    const pendingTotal = submitCount + auditCount + approvalCount
    const topItem = typeSummary.value[0]
    const topTypeName = topItem ? topItem.name : '-'
    const topTypeCount = topItem ? topItem.count : 0
    const toPct = (v, base) => base > 0 ? Math.round((v / base) * 100) : 0
    const submitPct = toPct(submitCount, pendingTotal)
    const topTypePct = toPct(topTypeCount, pendingTotal)
    const last7Added = countLast7Days([...submitList, ...auditList, ...approvalList])
    const doneRate = toPct(doneCount, pendingTotal + doneCount)
    const { avgStr } = calcAvgDuration(doneList)
    const { dodPct, wowPct, dodClass, wowClass } = calcChangeRates(trendSource)

    kpi.value = {
      typeCount: typeSummary.value.length,
      topTypeName,
      topTypeCount,
      topTypePct,
      submitPct,
      submitCount,
      pendingTotal,
      last7Added,
      doneRate,
      avgDurationStr: avgStr,
      dodPct,
      wowPct,
      dodClass,
      wowClass
    }

    const TOP_N = 5
    if (typeSummary.value.length > TOP_N) {
      const head = typeSummary.value.slice(0, TOP_N - 1)
      const tailSum = typeSummary.value.slice(TOP_N - 1).reduce((s, it) => s + it.count, 0)
      topTypeHeadNames.value = head.map(it => it.name)
      topTypes.value = [
        ...head.map(it => ({ name: it.name, count: it.count, pct: toPct(it.count, pendingTotal) })),
        { name: '其他(合并)', count: tailSum, pct: toPct(tailSum, pendingTotal) }
      ]
    } else {
      topTypeHeadNames.value = typeSummary.value.map(it => it.name)
      topTypes.value = typeSummary.value.map(it => ({ name: it.name, count: it.count, pct: toPct(it.count, pendingTotal) }))
    }

    allTasks.value = [...rejectedList]
      .map((it, idx) => ({ ...it, _id: (it.data_id || idx) + '_' + (it.status || '') }))
      .sort((a, b) => {
        const bt = b.update_time || b.create_time
        const at = a.update_time || a.create_time
        return new Date((bt?.replace?.(' ', 'T')) || bt || 0) - new Date((at?.replace?.(' ', 'T')) || at || 0)
      })
      .slice(0, 50)
    updateDisplayedTasks()
  } catch (error) {
    console.error('加载用户任务失败:', error)
  }
}

const renderStatusChart = (data) => {
  if (!statusChart && statusChartEl.value) {
    statusChart = echarts.init(statusChartEl.value)
  }
  if (!statusChart) return
  const option = {
    tooltip: { trigger: 'item' },
    legend: { top: 'bottom' },
    color: ['#FFB703', '#219EBC', '#A78BFA', '#2A9D8F'],
    series: [
      {
        name: '任务状态',
        type: 'pie',
        radius: ['45%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
        label: { show: true, formatter: '{b}: {c} ({d}%)' },
        data: [
          { value: data.submit, name: '待提交' },
          { value: data.audit, name: '待审核' },
          { value: data.approval, name: '待批准' },
          { value: data.done, name: '已完成' }
        ]
      }
    ]
  }
  statusChart.setOption(option)
}

const buildLast7Series = (list) => {
  const dayMs = 24 * 60 * 60 * 1000
  const now = new Date()
  const start = new Date(new Date(now.getTime() - 6 * dayMs).toDateString())
  const labels = []
  const counts = []
  for (let i = 0; i < 7; i++) {
    const d = new Date(start.getTime() + i * dayMs)
    const key = d.toISOString().slice(0, 10)
    labels.push((d.getMonth() + 1) + '/' + d.getDate())
    counts.push(0)
    for (const it of list) {
      const raw = it.create_time
      if (!raw) continue
      const s = typeof raw === 'string' ? raw.replace(' ', 'T') : raw
      const dd = new Date(s)
      if (!isNaN(dd.getTime()) && dd.toISOString().slice(0, 10) === key) counts[i]++
    }
  }
  return { labels, counts }
}

const renderTrendChart = (series) => {
  if (!trendChart && trendChartEl.value) {
    trendChart = echarts.init(trendChartEl.value)
  }
  if (!trendChart) return
  const option = {
    grid: { left: 8, right: 8, top: 8, bottom: 8 },
    xAxis: { type: 'category', data: series.labels, axisTick: { show: false }, axisLine: { show: false }, axisLabel: { color: '#6c757d' } },
    yAxis: { type: 'value', splitLine: { show: false }, axisLabel: { show: false }, axisTick: { show: false }, axisLine: { show: false } },
    series: [{
      type: 'line',
      data: series.counts,
      smooth: true,
      symbol: 'none',
      lineStyle: { color: '#219EBC', width: 2 },
      areaStyle: { color: 'rgba(33,158,188,0.2)' }
    }]
  }
  trendChart.setOption(option)
}

const pickTime = (it, keys) => {
  for (const k of keys) {
    if (it && it[k]) return it[k]
  }
  return null
}

const calcAvgDuration = (list) => {
  let sum = 0
  let cnt = 0
  for (const it of list) {
    const startRaw = pickTime(it, ['create_time', 'created_at', 'createTime'])
    const endRaw = pickTime(it, ['completed_time', 'complete_time', 'finish_time', 'finished_at', 'update_time'])
    if (!startRaw || !endRaw) continue
    const s = new Date((typeof startRaw === 'string') ? startRaw.replace(' ', 'T') : startRaw)
    const e = new Date((typeof endRaw === 'string') ? endRaw.replace(' ', 'T') : endRaw)
    if (isNaN(s.getTime()) || isNaN(e.getTime())) continue
    const diff = e.getTime() - s.getTime()
    if (diff > 0) {
      sum += diff
      cnt++
    }
  }
  if (!cnt) return { avgStr: '-' }
  const avgMs = Math.round(sum / cnt)
  return { avgStr: formatDuration(avgMs) }
}

const formatDuration = (ms) => {
  const sec = Math.floor(ms / 1000)
  const min = Math.floor(sec / 60)
  const hr = Math.floor(min / 60)
  const day = Math.floor(hr / 24)
  const h = hr % 24
  const m = min % 60
  if (day > 0) return `${day}天${h}小时`
  if (hr > 0) return `${hr}小时${m}分`
  return `${Math.max(m, 1)}分`
}

const calcChangeRates = (list) => {
  const dayMs = 24 * 60 * 60 * 1000
  const now = new Date()
  const today = new Date(now.toDateString())
  const yday = new Date(today.getTime() - dayMs)
  const start7 = new Date(today.getTime() - 6 * dayMs)
  const prev7Start = new Date(start7.getTime() - 7 * dayMs)
  const prev7End = new Date(start7.getTime() - dayMs)
  const inRange = (d, a, b) => d >= a && d <= b
  let todayCnt = 0
  let ydayCnt = 0
  let last7Cnt = 0
  let prev7Cnt = 0
  for (const it of list) {
    const raw = it.create_time
    if (!raw) continue
    const d = new Date((typeof raw === 'string') ? raw.replace(' ', 'T') : raw)
    if (isNaN(d.getTime())) continue
    if (inRange(d, today, new Date(today.getTime() + dayMs - 1))) todayCnt++
    if (inRange(d, yday, new Date(today.getTime() - 1))) ydayCnt++
    if (inRange(d, start7, new Date(today.getTime() + dayMs - 1))) last7Cnt++
    if (inRange(d, prev7Start, prev7End)) prev7Cnt++
  }
  const toPct = (num, den) => {
    if (den <= 0) return 0
    return Math.round(((num - den) / den) * 100)
  }
  const dod = toPct(todayCnt, ydayCnt)
  const wow = toPct(last7Cnt, prev7Cnt)
  const classBy = (v) => v > 0 ? 'up' : (v < 0 ? 'down' : 'flat')
  return { dodPct: Math.abs(dod), wowPct: Math.abs(wow), dodClass: classBy(dod), wowClass: classBy(wow) }
}

const statusText = (s) => {
  if (isDashboardRejectedStatus(s)) return '已打回'
  const stage = normalizeTaskStage(s)
  if (stage === 0) return '待提交'
  if (stage === 1) return '待审核'
  if (stage === 4) return '待批准'
  if (stage === 6) return '已完成'
  return String(s || '').trim() || '-'
}

const statusClass = (s) => {
  if (isDashboardRejectedStatus(s)) return 'status-rejected'
  const stage = normalizeTaskStage(s)
  if (stage === 0) return 'status-pending'
  if (stage === 1) return 'status-audit'
  if (stage === 4) return 'status-audit'
  if (stage === 6) return 'status-done'
  return 'status-pending'
}

const formatTime = (t) => {
  if (!t) return '-'
  const d = new Date((typeof t === 'string' ? t.replace(' ', 'T') : t))
  if (isNaN(d.getTime())) return t
  const m = (d.getMonth() + 1).toString().padStart(2, '0')
  const day = d.getDate().toString().padStart(2, '0')
  const hh = d.getHours().toString().padStart(2, '0')
  const mm = d.getMinutes().toString().padStart(2, '0')
  return `${d.getFullYear()}-${m}-${day} ${hh}:${mm}`
}

const handleTopTypeClick = (row) => {
  currentTypeFilter.value = row.name
  updateDisplayedTasks()
}

const clearTypeFilter = () => {
  currentTypeFilter.value = ''
  updateDisplayedTasks()
}

const getListIdByTableType = (tableType) => {
  const raw = String(tableType || '').trim()
  const upper = raw.toUpperCase()
  if (raw === '委托单' || upper === 'ENTRUSTMENT' || upper === 'ENTRUSTMENT_LIST') return 'EntrustmentList'
  if (raw === '轻型动力触探' || upper === 'LIGHT_DYNAMIC_PENETRATION') return 'LightDynamicPenetrationRecordList'
  if (raw === '核子密度' || raw === '核子法' || upper === 'NUCLEAR_DENSITY') return 'NuclearDensityRecordList'
  if (raw === '灌砂法' || upper === 'SAND_REPLACEMENT') return 'SandReplacementRecordList'
  if (raw === '灌水法' || upper === 'WATER_REPLACEMENT') return 'WaterReplacementRecordList'
  if (raw === '环刀法' || upper === 'CUTTING_RING') return 'CuttingRingRecordList'
  if (raw === '回弹法' || upper === 'REBOUND_METHOD') return 'ReboundMethodRecordList'
  if (raw === '贝克曼梁' || upper === 'BECKMAN_BEAM') return 'BeckmanBeamRecordList'
  if (raw === '密度试验' || upper === 'DENSITY_TEST') return 'DensityTestReportList'
  return ''
}

const viewDetails = (row) => {
  if (typeof navigateTo !== 'function') return
  const wtNum = row?.unified_number
  if (!wtNum) return
  const listId = getListIdByTableType(row?.table_type)
  if (!listId) return
  navigateTo(listId, { presetWtNum: wtNum })
}

const applyKeywordFilter = () => {
  updateDisplayedTasks()
}

const getRejectReason = (row) => {
  const v = row?.reject_reason || row?.rejectReason || row?.sendBackReason || row?.SEND_BACK_REASON || ''
  return String(v || '').trim() || '-'
}

const parseDateSafe = (t) => {
  if (!t) return null
  const s = (typeof t === 'string') ? t.replace(' ', 'T') : t
  const d = new Date(s)
  if (isNaN(d.getTime())) return null
  return d
}

const isRecentReject = (row) => {
  const d = parseDateSafe(row?.update_time || row?.create_time)
  if (!d) return false
  return Date.now() - d.getTime() <= 24 * 60 * 60 * 1000
}

const calcRejectMaxTs = (list) => {
  let max = 0
  for (const it of list) {
    const d = parseDateSafe(it?.update_time || it?.create_time)
    if (d && d.getTime() > max) max = d.getTime()
  }
  return max
}

const updateRejectBanner = (list) => {
  if (!list.length) {
    showRejectBanner.value = false
    hasNewRejects.value = false
    return
  }
  const maxTs = calcRejectMaxTs(list)
  const seenTs = Number(localStorage.getItem('dashboard_seen_reject_ts') || 0)
  hasNewRejects.value = maxTs > seenTs
  showRejectBanner.value = true
}

const dismissRejectBanner = () => {
  showRejectBanner.value = false
  hasNewRejects.value = false
  const maxTs = calcRejectMaxTs(allTasks.value)
  if (maxTs) localStorage.setItem('dashboard_seen_reject_ts', String(maxTs))
}

const scrollToRejected = () => {
  const el = rejectedSectionEl.value
  if (el && typeof el.scrollIntoView === 'function') {
    el.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}

const updateDisplayedTasks = () => {
  const kw = keyword.value.trim()
  const type = currentTypeFilter.value.trim()
  displayedTasks.value = allTasks.value.filter(it => {
    let okType = true
    if (type) {
      if (type.startsWith('其他')) {
        okType = !topTypeHeadNames.value.includes(it.table_type)
      } else {
        okType = it.table_type === type
      }
    }
    const keyStr = (it.project_name || '') + ' ' + (it.unified_number || '') + ' ' + (it.reject_reason || '')
    const okKw = !kw || keyStr.includes(kw)
    return okType && okKw
  }).slice(0, 20)
}
const countLast7Days = (list) => {
  const now = new Date()
  const dayMs = 24 * 60 * 60 * 1000
  const start = new Date(now.getTime() - 6 * dayMs)
  let cnt = 0
  for (const it of list) {
    const raw = it.create_time
    if (!raw) continue
    // 兼容 "YYYY-MM-DD HH:mm:ss" 与 ISO 字符串
    const s = typeof raw === 'string' ? raw.replace(' ', 'T') : raw
    const d = new Date(s)
    if (!isNaN(d.getTime()) && d >= new Date(start.toDateString()) && d <= now) cnt++
  }
  return cnt
}

// 组件挂载时加载数据
onMounted(() => {
  loadUserTasks()
  window.addEventListener('resize', onResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', onResize)
  statusChart && statusChart.dispose()
  trendChart && trendChart.dispose()
})
</script>

<style scoped>
.dashboard-container {
  padding: 24px;
}

.reject-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 12px 14px;
  border-radius: 10px;
  border: 1px solid rgba(198,40,40,0.25);
  background: rgba(255,235,238,0.9);
  margin-bottom: 16px;
}

.reject-banner-title {
  font-weight: 600;
  color: #c62828;
  font-size: 14px;
}

.reject-banner-sub {
  margin-top: 2px;
  color: #6c757d;
  font-size: 12px;
}

.reject-banner-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.btn-alert {
  background-color: #c62828;
  color: #fff;
  border: 1px solid rgba(198,40,40,0.6);
}

.pulse {
  animation: pulseBorder 1.6s ease-in-out infinite;
}

@keyframes pulseBorder {
  0% { box-shadow: 0 0 0 0 rgba(198,40,40,0.28); }
  70% { box-shadow: 0 0 0 10px rgba(198,40,40,0); }
  100% { box-shadow: 0 0 0 0 rgba(198,40,40,0); }
}

/* 数据卡片区 */
.data-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 32px;
}

.data-card {
  background-color: var(--bg-card);
  border-radius: 8px;
  padding: 24px;
  box-shadow: var(--shadow);
  text-align: center;
  transition: transform 0.2s;
}

.data-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.data-card.blue {
  border-left: 4px solid var(--color-blue);
}

.data-card.yellow {
  border-left: 4px solid var(--color-yellow);
}

.data-card.green {
  border-left: 4px solid var(--color-green);
}

.data-card.red {
  border-left: 4px solid #c62828;
}

.data-card.clickable {
  cursor: pointer;
}

.data-card .label {
  font-size: 14px;
  color: var(--text-light);
  margin-bottom: 8px;
}

.data-card .num {
  font-size: 32px;
  font-weight: 600;
  color: var(--text-primary);
}

/* 图表区域 */
.chart-area {
  display: block;
  margin-bottom: 32px;
}

.chart-card {
  background-color: var(--bg-card);
  border-radius: 8px;
  padding: 20px;
  box-shadow: var(--shadow);
}

.chart-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--border-color);
}

.placeholder-chart {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  background-color: #f8f9fa;
  border-radius: 4px;
  color: #6c757d;
}

.overview-content {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 20px;
  align-items: stretch;
}

.overview-left .chart-container {
  height: 320px !important;
}

.overview-right-grid {
  display: grid;
  grid-template-columns: 1.5fr 1fr;
  gap: 16px;
}

.kpi-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 12px;
  margin-bottom: 16px;
}

.kpi-card {
  background-color: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  padding: 12px;
}

.kpi-label {
  font-size: 12px;
  color: var(--text-light);
}

.kpi-value {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
  margin-top: 6px;
}

.kpi-sub {
  font-size: 12px;
  color: var(--text-light);
  margin-top: 4px;
}

.trend-wrapper {
  background-color: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  padding: 12px;
}

.trend-title {
  font-size: 12px;
  color: var(--text-secondary);
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.trend-chart {
  width: 100%;
  height: 120px;
}

.change-badge {
  padding: 2px 6px;
  border-radius: 10px;
  font-size: 12px;
  border: 1px solid var(--border-color);
}

.change-badge.up {
  color: #2A9D8F;
  background: rgba(42,157,143,0.1);
  border-color: rgba(42,157,143,0.3);
}

.change-badge.down {
  color: #E76F51;
  background: rgba(231,111,81,0.1);
  border-color: rgba(231,111,81,0.3);
}

.change-badge.flat {
  color: #6c757d;
  background: rgba(108,117,125,0.1);
  border-color: rgba(108,117,125,0.3);
}

.summary-table {
  margin-top: 8px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  overflow: hidden;
}

.summary-header, .summary-row {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr;
  gap: 8px;
  padding: 10px 12px;
}

.summary-header {
  background-color: var(--bg-primary);
  color: var(--text-secondary);
  font-size: 12px;
}

.summary-row:nth-child(odd) {
  background-color: var(--bg-card);
}

.summary-row.clickable {
  cursor: pointer;
}

.summary-row.clickable:hover {
  background-color: var(--bg-primary);
}

.summary-row .col.count, .summary-row .col.pct {
  text-align: right;
}

/* 最近任务列表 */
.list-card {
  background-color: var(--bg-card);
  border-radius: 8px;
  padding: 20px;
  box-shadow: var(--shadow);
}

.list-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--border-color);
}

.filter-badge {
  display: inline-flex;
  align-items: center;
  margin-left: 10px;
  padding: 2px 8px;
  border-radius: 999px;
  font-size: 12px;
  background-color: var(--bg-primary);
  border: 1px solid var(--border-color);
  color: var(--text-secondary);
}

.search-bar {
  margin-bottom: 16px;
  display: flex;
  gap: 16px;
  align-items: center;
}

.search-bar input {
  flex: 1;
  max-width: 300px;
  padding: 8px 12px;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  outline: none;
}

.search-bar input:focus {
  border-color: var(--color-blue);
}

/* 表格样式 */
.task-table {
  width: 100%;
  border-collapse: collapse;
}

.task-table th {
  background-color: var(--card-blue);
  color: var(--color-blue);
  padding: 12px 16px;
  text-align: left;
  font-weight: 500;
}

.task-table td {
  padding: 12px 16px;
  border-bottom: 1px solid var(--border-color);
}

.empty-row {
  text-align: center;
  color: var(--text-light);
  padding: 24px 0;
}

.new-pill {
  display: inline-flex;
  align-items: center;
  height: 18px;
  padding: 0 6px;
  border-radius: 999px;
  font-size: 12px;
  margin-right: 6px;
  background: rgba(198,40,40,0.12);
  color: #c62828;
  border: 1px solid rgba(198,40,40,0.25);
  vertical-align: middle;
}

.reason-cell {
  max-width: 420px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: var(--text-secondary);
}

.task-table tr:hover {
  background-color: var(--bg-primary);
}

.status-tag {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.status-pending {
  background-color: var(--card-yellow);
  color: var(--color-yellow);
}

.status-audit {
  background-color: var(--card-blue);
  color: var(--color-blue);
}

.status-done {
  background-color: var(--card-green);
  color: var(--color-green);
}

.status-rejected {
  background-color: #ffebee;
  color: #c62828;
}

.status-active {
  background-color: var(--card-green);
  color: var(--color-green);
}

.btn-small {
  padding: 4px 8px;
  font-size: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .data-cards {
    grid-template-columns: 1fr;
  }
  .overview-content {
    grid-template-columns: 1fr;
  }
  .kpi-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
