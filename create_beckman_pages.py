import os

output_dir = r"d:\develop\IdeaWorkSpace\work121\src\main\resources\static"

# 贝克曼梁检测报告
beckman_beam_report = '''<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>路基路面回弹弯沉（回弹模量）检测报告</title>
<script src="https://cdn.jsdelivr.net/npm/vue@2.6.14/dist/vue.js"></script>
<style>
body { font-family: 'SimSun', 'Songti SC', serif; width: 210mm; margin: 0 auto; padding: 20px; }
h1 { text-align: center; font-size: 24px; margin-bottom: 20px; }
.header-info { display: flex; justify-content: space-between; margin-bottom: 5px; }
table { width: 100%; border-collapse: collapse; border: 1px solid black; table-layout: fixed; margin-bottom: -1px; }
td, th { border: 1px solid black; padding: 5px; text-align: center; font-size: 14px; vertical-align: middle; word-wrap: break-word; }
input[type="text"] { width: 95%; border: none; outline: none; text-align: center; font-family: inherit; font-size: inherit; background: transparent; }
.label { text-align: center; }
.footer-section { margin-top: 20px; }
.footer-line { display: flex; justify-content: space-between; margin-bottom: 10px; }
.company-info { margin-top: 20px; font-size: 14px; }
.no-print { margin-bottom: 20px; }
.no-print a { text-decoration: none; color: blue; }
@media print { .no-print { display: none; } }
</style>
</head>
<body>
<div id="app">
<div class="no-print">
<a href="index.html" style="text-decoration: none; color: blue;">&lt; 返回主页</a>
<button @click="printDocument" style="float: right; margin-left: 10px;">打印此单</button>
<button @click="generatePdf" style="float: right; margin-left: 10px;">下载PDF</button>
<button @click="previewPdf" style="float: right; margin-left: 10px;">预览PDF</button>
</div>

<form id="pdfForm" method="post">
<h1>路基路面回弹弯沉（回弹模量）检测报告</h1>

<div class="header-info">
<div>委托单位：<input type="text" v-model="formData.entrustingUnit" style="width: 200px; border-bottom: 1px solid black; text-align: left;"></div>
<div>统一编号：<input type="text" v-model="formData.unifiedNumber" style="width: 150px; border-bottom: 1px solid black;"></div>
</div>

<table>
<colgroup><col style="width: 15%;"><col style="width: 35%;"><col style="width: 15%;"><col style="width: 35%;"></colgroup>
<tr><td class="label">工程名称</td><td><input type="text" v-model="formData.projectName"></td><td class="label">委托日期</td><td><input type="text" v-model="formData.commissionDate"></td></tr>
<tr><td class="label">施工部位</td><td><input type="text" v-model="formData.constructionPart"></td><td class="label">检测日期</td><td><input type="text" v-model="formData.testDate"></td></tr>
<tr><td class="label">仪器设备及编码</td><td><input type="text" v-model="formData.equipmentCode"></td><td class="label">报告日期</td><td><input type="text" v-model="formData.reportDate"></td></tr>
<tr><td class="label">样品名称及状态</td><td><input type="text" v-model="formData.sampleStatus"></td><td class="label">检测类别</td><td><input type="text" v-model="formData.testCategory"></td></tr>
<tr><td class="label">依据标准</td><td><input type="text" v-model="formData.standard"></td><td class="label">检测方法</td><td><input type="text" v-model="formData.testMethod"></td></tr>
<tr><td class="label">见证单位</td><td><input type="text" v-model="formData.witnessUnit"></td><td class="label">见证人</td><td><input type="text" v-model="formData.witness"></td></tr>
<tr><td class="label">弯沉仪类型</td><td><input type="text" v-model="formData.deflectometerType"></td><td class="label">后轴重(kN)</td><td><input type="text" v-model="formData.axleWeight"></td></tr>
<tr><td class="label">轮胎接地压强(MPa)</td><td><input type="text" v-model="formData.tirePressure"></td><td class="label">检测段长度(m)</td><td><input type="text" v-model="formData.testLength"></td></tr>
</table>

<table style="margin-top: 1px;">
<colgroup><col style="width: 12%;"><col style="width: 20%;"><col style="width: 15%;"><col style="width: 15%;"><col style="width: 15%;"><col style="width: 15%;"><col style="width: 8%;"></colgroup>
<tr><td class="label">测点桩号</td><td class="label">测点位置</td><td class="label">初读数(0.01mm)</td><td class="label">终读数(0.01mm)</td><td class="label">回弹弯沉值(0.01mm)</td><td class="label">代表弯沉值(0.01mm)</td><td class="label">备注</td></tr>
<template v-for="(row, index) in formData.dataRows" :key="index">
<tr><td><input type="text" v-model="row.station"></td><td><input type="text" v-model="row.location"></td><td><input type="text" v-model="row.initialReading"></td><td><input type="text" v-model="row.finalReading"></td><td><input type="text" v-model="row.reboundDeflection"></td><td><input type="text" v-model="row.representativeDeflection"></td><td><input type="text" v-model="row.remarks"></td></tr>
</template>
</table>

<table style="margin-top: 1px;">
<colgroup><col style="width: 15%;"><col style="width: 85%;"></colgroup>
<tr><td class="label">检测结论</td><td class="left-align" style="height: 80px; vertical-align: top;"><textarea v-model="formData.conclusion" style="width: 100%; height: 100%; border: none; resize: none;"></textarea></td></tr>
<tr><td class="label">备注</td><td class="left-align" style="height: 60px; vertical-align: top;"><textarea v-model="formData.remarks" style="width: 100%; height: 100%; border: none; resize: none;"></textarea></td></tr>
</table>

<div class="footer-section">
<div class="footer-line">
<span>批准：<input type="text" v-model="formData.approval" style="width: 80px; border-bottom: 1px solid black;"></span>
<span>审核：<input type="text" v-model="formData.reviewer" style="width: 80px; border-bottom: 1px solid black;"></span>
<span>检测：<input type="text" v-model="formData.tester" style="width: 80px; border-bottom: 1px solid black;"></span>
</div>
</div>

<div class="company-info">
<div style="border-top: 2px solid black; padding-top: 10px; margin-top: 10px;">
声明：1. 对本检测报告的复印件未加盖公司检验检测专用章无效。2. 对检验结果如有异议，应在收到报告之日起十五日之内向本公司提出。
</div>
<div style="display: flex; justify-content: space-between; margin-top: 10px; border-bottom: 2px solid black; padding-bottom: 5px;">
<span>公司名称：<input type="text" v-model="formData.companyName" style="width: 180px; border: none;"></span>
<span>电话：<input type="text" v-model="formData.companyPhone" style="width: 120px; border: none;"></span>
</div>
<div style="margin-top: 5px;">
<span>公司地址：<input type="text" v-model="formData.companyAddress" style="width: 350px; border: none;"></span>
</div>
<div style="display: flex; justify-content: space-between; margin-top: 10px; font-size: 12px;">
<span>版次：<input type="text" v-model="formData.version" style="width: 40px; border-bottom: 1px solid black; text-align: center;"></span>
<span><input type="text" v-model="formData.footerDate" style="width: 80px; border-bottom: 1px solid black; text-align: center;"></span>
<span>第 <input type="text" v-model="formData.page" style="width: 20px; border-bottom: 1px solid black; text-align: center;"> 页，共 <input type="text" v-model="formData.totalPages" style="width: 20px; border-bottom: 1px solid black; text-align: center;"> 页</span>
</div>
</div>
</form>
</div>

<script>
new Vue({
el: '#app',
data: {
formData: {
entrustingUnit: '', unifiedNumber: '', projectName: '', commissionDate: '', constructionPart: '', testDate: '',
equipmentCode: '', reportDate: '', sampleStatus: '', testCategory: '', standard: '', testMethod: '',
witnessUnit: '', witness: '', deflectometerType: '', axleWeight: '', tirePressure: '', testLength: '',
dataRows: Array.from({length: 15}, () => ({
station: '', location: '', initialReading: '', finalReading: '', reboundDeflection: '', representativeDeflection: '', remarks: ''
})),
conclusion: '', remarks: '', approval: '', reviewer: '', tester: '',
companyName: '', companyPhone: '', companyAddress: '',
version: '', footerDate: new Date().toISOString().split('T')[0], page: '1', totalPages: '1'
}
},
methods: {
printDocument() { window.print(); },
generatePdf() { const f = document.getElementById('pdfForm'); f.action = '/api/pdf/beckman_beam_report/generate'; f.target = '_blank'; f.submit(); },
previewPdf() { const f = document.getElementById('pdfForm'); f.action = '/api/pdf/beckman_beam_report/preview'; f.target = '_blank'; f.submit(); }
}
});
</script>
</body>
</html>'''

# 贝克曼梁检测结果
beckman_beam_result = '''<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>路基路面回弹弯沉（回弹模量）检测结果</title>
<script src="https://cdn.jsdelivr.net/npm/vue@2.6.14/dist/vue.js"></script>
<style>
body { font-family: 'SimSun', 'Songti SC', serif; width: 210mm; margin: 0 auto; padding: 20px; }
h1 { text-align: center; font-size: 24px; margin-bottom: 20px; }
.header-info { display: flex; justify-content: space-between; margin-bottom: 5px; }
table { width: 100%; border-collapse: collapse; border: 1px solid black; table-layout: fixed; margin-bottom: -1px; }
td, th { border: 1px solid black; padding: 5px; text-align: center; font-size: 14px; vertical-align: middle; word-wrap: break-word; }
input[type="text"] { width: 95%; border: none; outline: none; text-align: center; font-family: inherit; font-size: inherit; background: transparent; }
.label { text-align: center; }
.footer-line { display: flex; justify-content: space-between; margin-bottom: 10px; margin-top: 20px; }
.no-print { margin-bottom: 20px; }
.no-print a { text-decoration: none; color: blue; }
@media print { .no-print { display: none; } }
</style>
</head>
<body>
<div id="app">
<div class="no-print">
<a href="index.html" style="text-decoration: none; color: blue;">&lt; 返回主页</a>
<button @click="printDocument" style="float: right; margin-left: 10px;">打印此单</button>
<button @click="generatePdf" style="float: right; margin-left: 10px;">下载PDF</button>
<button @click="previewPdf" style="float: right; margin-left: 10px;">预览PDF</button>
</div>

<form id="pdfForm" method="post">
<h1>路基路面回弹弯沉（回弹模量）检测结果</h1>

<div class="header-info">
<div>统一编号：<input type="text" v-model="formData.unifiedNumber" style="width: 150px; border-bottom: 1px solid black;"></div>
</div>

<table>
<colgroup><col style="width: 15%;"><col style="width: 35%;"><col style="width: 15%;"><col style="width: 35%;"></colgroup>
<tr><td class="label">施工部位</td><td><input type="text" v-model="formData.constructionPart"></td><td class="label">弯沉仪类型</td><td><input type="text" v-model="formData.deflectometerType"></td></tr>
<tr><td class="label">后轴重(kN)</td><td><input type="text" v-model="formData.axleWeight"></td><td class="label">轮胎接地压强(MPa)</td><td><input type="text" v-model="formData.tirePressure"></td></tr>
</table>

<table style="margin-top: 1px;">
<colgroup><col style="width: 12%;"><col style="width: 20%;"><col style="width: 15%;"><col style="width: 15%;"><col style="width: 15%;"><col style="width: 15%;"><col style="width: 8%;"></colgroup>
<tr><td class="label">测点桩号</td><td class="label">测点位置</td><td class="label">初读数(0.01mm)</td><td class="label">终读数(0.01mm)</td><td class="label">回弹弯沉值(0.01mm)</td><td class="label">代表弯沉值(0.01mm)</td><td class="label">备注</td></tr>
<template v-for="(row, index) in formData.dataRows" :key="index">
<tr><td><input type="text" v-model="row.station"></td><td><input type="text" v-model="row.location"></td><td><input type="text" v-model="row.initialReading"></td><td><input type="text" v-model="row.finalReading"></td><td><input type="text" v-model="row.reboundDeflection"></td><td><input type="text" v-model="row.representativeDeflection"></td><td><input type="text" v-model="row.remarks"></td></tr>
</template>
</table>

<div class="footer-line">
<span>版次：<input type="text" v-model="formData.version" style="width: 40px; border-bottom: 1px solid black; text-align: center;"></span>
<span><input type="text" v-model="formData.year" style="width: 40px; border-bottom: 1px solid black; text-align: center;">年<input type="text" v-model="formData.month" style="width: 20px; border-bottom: 1px solid black; text-align: center;">月<input type="text" v-model="formData.day" style="width: 20px; border-bottom: 1px solid black; text-align: center;">日</span>
<span>第 <input type="text" v-model="formData.page" style="width: 20px; border-bottom: 1px solid black; text-align: center;"> 页，共 <input type="text" v-model="formData.totalPages" style="width: 20px; border-bottom: 1px solid black; text-align: center;"> 页</span>
</div>
</form>
</div>

<script>
new Vue({
el: '#app',
data: {
formData: {
unifiedNumber: '', constructionPart: '', deflectometerType: '', axleWeight: '', tirePressure: '',
dataRows: Array.from({length: 25}, () => ({
station: '', location: '', initialReading: '', finalReading: '', reboundDeflection: '', representativeDeflection: '', remarks: ''
})),
version: '', year: new Date().getFullYear(), month: String(new Date().getMonth() + 1).padStart(2, '0'),
day: String(new Date().getDate()).padStart(2, '0'), page: '1', totalPages: '1'
}
},
methods: {
printDocument() { window.print(); },
generatePdf() { const f = document.getElementById('pdfForm'); f.action = '/api/pdf/beckman_beam_result/generate'; f.target = '_blank'; f.submit(); },
previewPdf() { const f = document.getElementById('pdfForm'); f.action = '/api/pdf/beckman_beam_result/preview'; f.target = '_blank'; f.submit(); }
}
});
</script>
</body>
</html>'''

# 核子法记录表
nuclear_density_record = '''<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>原位密度检测记录表（核子法）</title>
<script src="https://cdn.jsdelivr.net/npm/vue@2.6.14/dist/vue.js"></script>
<style>
body { font-family: 'SimSun', 'Songti SC', serif; width: 210mm; margin: 0 auto; padding: 20px; }
h2 { text-align: center; margin-bottom: 20px; font-size: 24px; font-weight: bold; }
.header-info { display: flex; justify-content: space-between; margin-bottom: 5px; font-weight: bold; }
table { width: 100%; border-collapse: collapse; border: 2px solid black; }
td { border: 1px solid black; padding: 5px; vertical-align: middle; text-align: center; }
.label { font-weight: bold; white-space: nowrap; }
.left-align { text-align: left; padding-left: 10px; }
input[type="text"], textarea { width: 98%; border: none; outline: none; font-family: inherit; font-size: inherit; background-color: transparent; text-align: center; }
.left-align input[type="text"] { text-align: left; }
input[type="text"]:focus, textarea:focus { background-color: #f0f8ff; }
textarea { resize: none; overflow: hidden; text-align: left; }
.footer-info { display: flex; justify-content: space-around; margin-top: 20px; margin-bottom: 10px; font-size: 16px; font-weight: bold; }
.page-footer { margin-top: 5px; display: flex; justify-content: space-between; font-size: 14px; }
.no-print { margin-bottom: 20px; }
.no-print a { text-decoration: none; color: blue; }
@media print { .no-print { display: none; } }
</style>
</head>
<body>
<div id="app">
<div class="no-print">
<a href="index.html" style="text-decoration: none; color: blue;">&lt; 返回主页</a>
<button @click="printDocument" style="float: right; margin-left: 10px;">打印此单</button>
<button @click="generatePdf" style="float: right; margin-left: 10px;">下载PDF</button>
<button @click="previewPdf" style="float: right; margin-left: 10px;">预览PDF</button>
</div>

<form id="pdfForm" method="post">
<h2>原位密度检测记录表（核子法）</h2>

<div class="header-info">
<span>委托单位：<input type="text" v-model="formData.entrustingUnit" style="width: 250px; border-bottom: 1px solid black; text-align: left;"></span>
<span>统一编号：<input type="text" v-model="formData.unifiedNumber" style="width: 150px; border-bottom: 1px solid black;"></span>
</div>

<table>
<tr><td class="label">工程名称</td><td colspan="6"><input type="text" v-model="formData.projectName"></td><td class="label">委托日期</td><td colspan="3"><input type="text" v-model="formData.commissionDate"></td></tr>
<tr><td class="label">施工部位</td><td colspan="6"><input type="text" v-model="formData.constructionPart"></td><td class="label">检测日期</td><td colspan="3"><input type="text" v-model="formData.testDate"></td></tr>
<tr><td class="label">仪器设备</td><td colspan="6"><input type="text" v-model="formData.equipment"></td><td class="label">检测依据</td><td colspan="3"><input type="text" v-model="formData.testBasis"></td></tr>
<tr><td class="label">核子仪型号</td><td colspan="3"><input type="text" v-model="formData.nuclearModel"></td><td class="label">检测深度(cm)</td><td colspan="3"><input type="text" v-model="formData.testDepth"></td><td class="label">检测类别</td><td colspan="2"><input type="text" v-model="formData.testCategory"></td></tr>

<tr><td class="label" style="width: 10%;">样品编号</td><td class="label" style="width: 20%;" colspan="2">检测部位<br>(桩号、高程)</td><td class="label" style="width: 15%;" colspan="2">检测日期</td><td class="label" style="width: 12.5%;">湿密度<br>(g/cm³)</td><td class="label" style="width: 12.5%;">干密度<br>(g/cm³)</td><td class="label" style="width: 12.5%;">含水率<br>%</td><td class="label" style="width: 12.5%;" colspan="2">备注</td></tr>

<template v-for="(row, index) in formData.dataRows" :key="index">
<tr><td><input type="text" v-model="row.sampleId"></td><td colspan="2"><input type="text" v-model="row.location"></td><td colspan="2"><input type="text" v-model="row.date"></td><td><input type="text" v-model="row.wetDensity"></td><td><input type="text" v-model="row.dryDensity"></td><td><input type="text" v-model="row.moisture"></td><td colspan="2"><input type="text" v-model="row.remarks"></td></tr>
</template>

<tr><td class="label">检测结论</td><td colspan="10" class="left-align" style="height: 80px; vertical-align: top;"><textarea v-model="formData.conclusion" style="width: 100%; height: 100%;"></textarea></td></tr>
<tr><td class="label">备注</td><td colspan="10" class="left-align" style="height: 60px; vertical-align: top;"><textarea v-model="formData.remarks" style="width: 100%; height: 100%;"></textarea></td></tr>
</table>

<div class="footer-info">
<span>计算：<input type="text" v-model="formData.calculator" style="width: 100px; border-bottom: 1px solid black;"></span>
<span>检测：<input type="text" v-model="formData.tester" style="width: 100px; border-bottom: 1px solid black;"></span>
<span>复核：<input type="text" v-model="formData.reviewer" style="width: 100px; border-bottom: 1px solid black;"></span>
</div>

<div class="page-footer">
<span>版次：<input type="text" v-model="formData.version" style="width: 50px; border-bottom: 1px solid black; text-align: center;"></span>
<span><input type="text" v-model="formData.year" style="width: 40px; border-bottom: 1px solid black; text-align: center;">年<input type="text" v-model="formData.month" style="width: 20px; border-bottom: 1px solid black; text-align: center;">月<input type="text" v-model="formData.day" style="width: 20px; border-bottom: 1px solid black; text-align: center;">日</span>
<span>第 <input type="text" v-model="formData.page" style="width: 20px; border-bottom: 1px solid black; text-align: center;"> 页，共 <input type="text" v-model="formData.totalPages" style="width: 20px; border-bottom: 1px solid black; text-align: center;"> 页</span>
</div>
</form>
</div>

<script>
new Vue({
el: '#app',
data: {
formData: {
entrustingUnit: '', unifiedNumber: '', projectName: '', commissionDate: '', constructionPart: '', testDate: '',
equipment: '', testBasis: '', nuclearModel: '', testDepth: '', testCategory: '',
dataRows: Array.from({length: 15}, () => ({
sampleId: '', location: '', date: '', wetDensity: '', dryDensity: '', moisture: '', remarks: ''
})),
conclusion: '', remarks: '',
calculator: '', tester: '', reviewer: '',
version: '', year: new Date().getFullYear(), month: String(new Date().getMonth() + 1).padStart(2, '0'),
day: String(new Date().getDate()).padStart(2, '0'), page: '1', totalPages: '1'
}
},
methods: {
printDocument() { window.print(); },
generatePdf() { const f = document.getElementById('pdfForm'); f.action = '/api/pdf/nuclear_density_record/generate'; f.target = '_blank'; f.submit(); },
previewPdf() { const f = document.getElementById('pdfForm'); f.action = '/api/pdf/nuclear_density_record/preview'; f.target = '_blank'; f.submit(); }
}
});
</script>
</body>
</html>'''

# 轻型动力触探检测报告
light_dynamic_penetration = '''<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>轻型动力触探检测报告</title>
<script src="https://cdn.jsdelivr.net/npm/vue@2.6.14/dist/vue.js"></script>
<style>
body { font-family: 'SimSun', 'Songti SC', serif; width: 210mm; margin: 0 auto; padding: 20px; }
h2 { text-align: center; margin-bottom: 20px; font-size: 24px; font-weight: bold; }
.header-info { display: flex; justify-content: space-between; margin-bottom: 5px; font-weight: bold; }
table { width: 100%; border-collapse: collapse; border: 2px solid black; }
td { border: 1px solid black; padding: 5px; vertical-align: middle; text-align: center; }
.label { font-weight: bold; white-space: nowrap; }
.left-align { text-align: left; padding-left: 10px; }
input[type="text"], textarea { width: 98%; border: none; outline: none; font-family: inherit; font-size: inherit; background-color: transparent; text-align: center; }
.left-align input[type="text"] { text-align: left; }
input[type="text"]:focus, textarea:focus { background-color: #f0f8ff; }
textarea { resize: none; overflow: hidden; text-align: left; }
.footer-info { display: flex; justify-content: space-around; margin-top: 20px; margin-bottom: 10px; font-size: 16px; font-weight: bold; }
.statement { font-size: 12px; line-height: 1.6; margin-top: 5px; border-top: 2px solid black; padding-top: 5px; }
.page-footer { margin-top: 5px; display: flex; justify-content: space-between; font-size: 14px; }
.no-print { margin-bottom: 20px; }
.no-print a { text-decoration: none; color: blue; }
@media print { .no-print { display: none; } }
</style>
</head>
<body>
<div id="app">
<div class="no-print">
<a href="index.html" style="text-decoration: none; color: blue;">&lt; 返回主页</a>
<button @click="printDocument" style="float: right; margin-left: 10px;">打印此单</button>
<button @click="generatePdf" style="float: right; margin-left: 10px;">下载PDF</button>
<button @click="previewPdf" style="float: right; margin-left: 10px;">预览PDF</button>
</div>

<form id="pdfForm" method="post">
<h2>轻型动力触探检测报告</h2>

<div class="header-info">
<span>委托单位：<input type="text" v-model="formData.entrustingUnit" style="width: 250px; border-bottom: 1px solid black; text-align: left;"></span>
<span>统一编号：<input type="text" v-model="formData.unifiedNumber" style="width: 150px; border-bottom: 1px solid black;"></span>
</div>

<table>
<tr><td class="label" style="width: 15%;">工程名称</td><td colspan="4"><input type="text" v-model="formData.projectName"></td><td class="label" style="width: 15%;">委托日期</td><td colspan="4"><input type="text" v-model="formData.commissionDate"></td></tr>
<tr><td class="label">施工部位</td><td colspan="4"><input type="text" v-model="formData.constructionPart"></td><td class="label">检测日期</td><td colspan="4"><input type="text" v-model="formData.testDate"></td></tr>
<tr><td class="label">岩土性状</td><td colspan="4"><input type="text" v-model="formData.soilProperties"></td><td class="label">报告日期</td><td colspan="4"><input type="text" v-model="formData.reportDate"></td></tr>
<tr><td class="label">见证单位</td><td colspan="4"><input type="text" v-model="formData.witnessUnit"></td><td class="label">见证人</td><td colspan="4"><input type="text" v-model="formData.witness"></td></tr>
<tr><td class="label">设计<br>承载力<br>(kPa)</td><td colspan="2"><input type="text" v-model="formData.designCapacity"></td><td class="label">锤重量<br>(kg)</td><td><input type="text" v-model="formData.hammerWeight"></td><td class="label">落距<br>(cm)</td><td><input type="text" v-model="formData.dropDistance"></td><td class="label">检测类别</td><td colspan="2"><input type="text" v-model="formData.testCategory"></td></tr>

<tr><td class="label">测点位置</td><td class="label">贯入<br>深度<br>(cm)</td><td class="label">实测<br>锤击数</td><td class="label">平均<br>锤击数<br>N<sub>10</sub></td><td class="label">承载力<br>特征值<br>(kPa)</td><td class="label">测点位置</td><td class="label">贯入<br>深度<br>(cm)</td><td class="label">实测<br>锤击数</td><td class="label">平均<br>锤击数<br>N<sub>10</sub></td><td class="label">承载力<br>特征值<br>(kPa)</td></tr>

<template v-for="(row, index) in formData.dataRows" :key="index">
<tr v-if="index % 2 === 0">
<td v-if="row.subIndex === 0" :rowspan="2"><input type="text" v-model="row.position"></td>
<td><input type="text" v-model="row.depth"></td>
<td><input type="text" v-model="row.blows"></td>
<td v-if="row.subIndex === 0" :rowspan="2"><input type="text" v-model="row.avgBlows"></td>
<td v-if="row.subIndex === 0" :rowspan="2"><input type="text" v-model="row.capacity"></td>
<td v-if="row.rightPosition && row.subIndex === 0" :rowspan="2"><input type="text" v-model="row.rightPosition"></td>
<td v-if="row.rightDepth"><input type="text" v-model="row.rightDepth"></td>
<td v-if="row.rightBlows"><input type="text" v-model="row.rightBlows"></td>
<td v-if="row.rightAvgBlows && row.subIndex === 0" :rowspan="2"><input type="text" v-model="row.rightAvgBlows"></td>
<td v-if="row.rightCapacity && row.subIndex === 0" :rowspan="2"><input type="text" v-model="row.rightCapacity"></td>
</tr>
</template>

<tr><td class="label">检测依据</td><td colspan="9" class="left-align"><input type="text" v-model="formData.testBasis"></td></tr>
<tr><td class="label">仪器设备</td><td colspan="9" class="left-align"><input type="text" v-model="formData.equipment"></td></tr>
<tr><td class="label" style="height: 60px;">检测结论</td><td colspan="9" class="left-align" style="vertical-align: top;"><textarea v-model="formData.conclusion" rows="3" style="width: 100%; height: 100%;"></textarea></td></tr>
<tr><td class="label">备注</td><td colspan="9" class="left-align"><input type="text" v-model="formData.remarks"></td></tr>
</table>

<div class="footer-info">
<span>审核：<input type="text" v-model="formData.reviewer" style="width: 100px; border-bottom: 1px solid black;"></span>
<span>计算：<input type="text" v-model="formData.calculator" style="width: 100px; border-bottom: 1px solid black;"></span>
<span>检测：<input type="text" v-model="formData.tester" style="width: 100px; border-bottom: 1px solid black;"></span>
</div>

<div class="statement">
声明：<br>
1. 对本检测报告的复印件未加盖公司检验检测专用章无效。 2. 对检验结果如有异议，应在收到报告之日起十五日之内向本公司提出。
</div>

<div class="page-footer">
<span>版次：<input type="text" v-model="formData.version" style="width: 50px; border-bottom: 1px solid black; text-align: center;"></span>
<span><input type="text" v-model="formData.year" style="width: 40px; border-bottom: 1px solid black; text-align: center;">年<input type="text" v-model="formData.month" style="width: 20px; border-bottom: 1px solid black; text-align: center;">月<input type="text" v-model="formData.day" style="width: 20px; border-bottom: 1px solid black; text-align: center;">日</span>
<span>第 <input type="text" v-model="formData.page" style="width: 20px; border-bottom: 1px solid black; text-align: center;"> 页，共 <input type="text" v-model="formData.totalPages" style="width: 20px; border-bottom: 1px solid black; text-align: center;"> 页</span>
</div>
</form>
</div>

<script>
new Vue({
el: '#app',
data: {
formData: {
entrustingUnit: '', unifiedNumber: '', projectName: '', commissionDate: '', constructionPart: '', testDate: '',
soilProperties: '', reportDate: '', witnessUnit: '', witness: '',
designCapacity: '', hammerWeight: '', dropDistance: '', testCategory: '',
dataRows: Array.from({length: 8}, (_, i) => ({
position: '', depth: '', blows: '', avgBlows: '', capacity: '',
rightPosition: '', rightDepth: '', rightBlows: '', rightAvgBlows: '', rightCapacity: '',
subIndex: i % 2
})),
testBasis: '', equipment: '', conclusion: '', remarks: '',
reviewer: '', calculator: '', tester: '',
version: '', year: new Date().getFullYear(), month: String(new Date().getMonth() + 1).padStart(2, '0'),
day: String(new Date().getDate()).padStart(2, '0'), page: '1', totalPages: '1'
}
},
methods: {
printDocument() { window.print(); },
generatePdf() { const f = document.getElementById('pdfForm'); f.action = '/api/pdf/light_dynamic_penetration/generate'; f.target = '_blank'; f.submit(); },
previewPdf() { const f = document.getElementById('pdfForm'); f.action = '/api/pdf/light_dynamic_penetration/preview'; f.target = '_blank'; f.submit(); }
}
});
</script>
</body>
</html>'''

# 保存文件
pages = [
    ("beckman_beam_report-vue.html", beckman_beam_report),
    ("beckman_beam_result-vue.html", beckman_beam_result),
    ("nuclear_density_record-vue.html", nuclear_density_record),
    ("light_dynamic_penetration-vue.html", light_dynamic_penetration)
]

for filename, content in pages:
    file_path = os.path.join(output_dir, filename)
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f"Created: {file_path}")

print("\nAll pages generated successfully!")