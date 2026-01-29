import os

# 原位密度检测结果
density_test_result = '''<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>原位密度检测结果</title>
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
<div class="header-info">
<span>统一编号：<input type="text" v-model="formData.unifiedNumber" style="width: 150px; border-bottom: 1px solid black; text-align: left;"></span>
<span></span>
</div>

<h2>原位密度检测结果</h2>

<table>
<tr><td class="label" style="width: 15%;">施工部位</td><td colspan="9" class="left-align"><input type="text" v-model="formData.constructionPart"></td></tr>
<tr><td class="label">最大干密度<br>(g/cm³)</td><td colspan="2"><input type="text" v-model="formData.maxDryDensity"></td><td class="label">最优含水率 %</td><td colspan="2"><input type="text" v-model="formData.optimumMoisture"></td><td class="label">最小干密度<br>(g/cm³)</td><td colspan="3"><input type="text" v-model="formData.minDryDensity"></td></tr>

<tr><td class="label" style="width: 10%;">样品编号</td><td class="label" style="width: 25%;" colspan="2">检测部位<br>(桩号、高程)</td><td class="label" style="width: 15%;" colspan="2">检测日期</td><td class="label" style="width: 12.5%;">湿密度<br>(g/cm³)</td><td class="label" style="width: 12.5%;">干密度<br>(g/cm³)</td><td class="label" style="width: 12.5%;">含水率<br>%</td><td class="label" style="width: 12.5%;" colspan="2">压实度%</td></tr>

<template v-for="(row, index) in formData.dataRows" :key="index">
<tr>
<td rowspan="2"><input type="text" v-model="row.sampleId"></td>
<td rowspan="2" colspan="2"><input type="text" v-model="row.location"></td>
<td rowspan="2" colspan="2"><input type="text" v-model="row.date"></td>
<td><input type="text" v-model="row.wetDensity"></td>
<td><input type="text" v-model="row.dryDensity"></td>
<td><input type="text" v-model="row.moisture"></td>
<td rowspan="2" colspan="2"><input type="text" v-model="row.compaction"></td>
</tr>
<tr>
<td><input type="text" v-model="row.wetDensity2"></td>
<td><input type="text" v-model="row.dryDensity2"></td>
<td><input type="text" v-model="row.moisture2"></td>
</tr>
</template>
</table>

<div class="page-footer">
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
unifiedNumber: '', constructionPart: '', maxDryDensity: '', optimumMoisture: '', minDryDensity: '',
dataRows: Array.from({length: 20}, () => ({
sampleId: '', location: '', date: '', wetDensity: '', dryDensity: '', moisture: '',
wetDensity2: '', dryDensity2: '', moisture2: '', compaction: ''
})),
 year: new Date().getFullYear(), month: String(new Date().getMonth() + 1).padStart(2, '0'),
day: String(new Date().getDate()).padStart(2, '0'), page: '1', totalPages: '1'
}
},
methods: {
printDocument() { window.print(); },
generatePdf() { const f = document.getElementById('pdfForm'); f.action = '/api/pdf/density_test_result/generate'; f.target = '_blank'; f.submit(); },
previewPdf() { const f = document.getElementById('pdfForm'); f.action = '/api/pdf/density_test_result/preview'; f.target = '_blank'; f.submit(); }
}
});
</script>
</body>
</html>'''

# 回弹法检测报告
rebound_method_report = '''<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>回弹法检测混凝土抗压强度报告</title>
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
.company-info { display: flex; justify-content: space-between; font-size: 14px; margin-top: 5px; border-bottom: 2px solid black; padding-bottom: 5px; }
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
<h2>回弹法检测混凝土抗压强度报告</h2>

<div class="header-info">
<span>委托单位：<input type="text" v-model="formData.entrustingUnit" style="width: 250px; border-bottom: 1px solid black; text-align: left;"></span>
<span>统一编号：<input type="text" v-model="formData.unifiedNumber" style="width: 150px; border-bottom: 1px solid black;"></span>
</div>

<table>
<tr><td class="label">工程名称</td><td colspan="6"><input type="text" v-model="formData.projectName"></td><td class="label">委托日期</td><td colspan="3"><input type="text" v-model="formData.commissionDate"></td></tr>
<tr><td class="label">施工部位</td><td colspan="6"><input type="text" v-model="formData.constructionPart"></td><td class="label">检测日期</td><td colspan="3"><input type="text" v-model="formData.testDate"></td></tr>
<tr><td class="label">样品名称<br>及状态</td><td colspan="6"><input type="text" v-model="formData.sampleNameStatus"></td><td class="label">报告日期</td><td colspan="3"><input type="text" v-model="formData.reportDate"></td></tr>
<tr><td class="label">仪器设备</td><td colspan="6"><input type="text" v-model="formData.equipment"></td><td class="label">检测方法</td><td colspan="3"><input type="text" v-model="formData.testMethod"></td></tr>
<tr><td class="label">检测依据</td><td colspan="6"><input type="text" v-model="formData.testBasis"></td><td class="label">检测类别</td><td colspan="3"><input type="text" v-model="formData.testCategory"></td></tr>
<tr><td class="label">设计强度<br>等级</td><td colspan="3"><input type="text" v-model="formData.designStrength"></td><td class="label">碳化深度<br>(mm)</td><td colspan="3"><input type="text" v-model="formData.carbonationDepth"></td><td class="label">泵送情况</td><td colspan="2"><input type="text" v-model="formData.pumping"></td></tr>
<tr><td class="label">检测结果</td><td colspan="10" class="left-align"><input type="text" v-model="formData.testResult"></td></tr>

<tr><td class="label" style="width: 10%;">构件名称</td><td class="label" style="width: 20%;" colspan="2">构件部位</td><td class="label" style="width: 15%;" colspan="2">检测日期</td><td class="label" style="width: 12.5%;">回弹值<br>平均值</td><td class="label" style="width: 12.5%;">强度换算值<br>(MPa)</td><td class="label" style="width: 12.5%;">强度推定值<br>(MPa)</td><td class="label" style="width: 12.5%;" colspan="2">备注</td></tr>

<template v-for="(row, index) in formData.dataRows" :key="index">
<tr>
<td><input type="text" v-model="row.componentName"></td>
<td colspan="2"><input type="text" v-model="row.componentPart"></td>
<td colspan="2"><input type="text" v-model="row.date"></td>
<td><input type="text" v-model="row.reboundAvg"></td>
<td><input type="text" v-model="row.strengthConversion"></td>
<td><input type="text" v-model="row.strengthEstimation"></td>
<td colspan="2"><input type="text" v-model="row.remarks"></td>
</tr>
</template>

<tr><td class="label">检测结论</td><td colspan="10" class="left-align" style="height: 80px; vertical-align: top;"><textarea v-model="formData.conclusion" style="width: 100%; height: 100%;"></textarea></td></tr>
<tr><td class="label">备 注</td><td colspan="10" class="left-align" style="height: 60px; vertical-align: top;"><textarea v-model="formData.remarks" style="width: 100%; height: 100%;"></textarea></td></tr>
</table>

<div class="footer-info">
<span>批准：<input type="text" v-model="formData.approval" style="width: 100px; border-bottom: 1px solid black;"></span>
<span>审核：<input type="text" v-model="formData.reviewer" style="width: 100px; border-bottom: 1px solid black;"></span>
<span>检测：<input type="text" v-model="formData.tester" style="width: 100px; border-bottom: 1px solid black;"></span>
</div>

<div class="statement">
声明：<br>
1. 对本检测报告的复印件未加盖公司检验检测专用章无效。 2. 对检验结果如有异议，应在收到报告之日起十五日之内向本公司提出。
</div>

<div class="company-info">
<span>公司名称：<input type="text" v-model="formData.companyName" style="width: 200px; border: none;"></span>
<span>电话：<input type="text" v-model="formData.companyPhone" style="width: 150px; border: none;"></span>
</div>
<div class="company-info" style="border-bottom: none; margin-top:0;">
<span>公司地址：<input type="text" v-model="formData.companyAddress" style="width: 400px; border: none;"></span>
</div>

<div class="page-footer">
<span><input type="text" v-model="formData.footerDate" style="width: 100px; border-bottom: 1px solid black; text-align: center;"></span>
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
sampleNameStatus: '', reportDate: '', equipment: '', testMethod: '',
testBasis: '', testCategory: '', designStrength: '', carbonationDepth: '', pumping: '',
testResult: '',
dataRows: Array.from({length: 12}, () => ({
componentName: '', componentPart: '', date: '', reboundAvg: '', strengthConversion: '',
strengthEstimation: '', remarks: ''
})),
conclusion: '', remarks: '',
approval: '', reviewer: '', tester: '',
companyName: '', companyPhone: '', companyAddress: '',
        footerDate: new Date().toISOString().split('T')[0], page: '1', totalPages: '1'
}
},
methods: {
printDocument() { window.print(); },
generatePdf() { const f = document.getElementById('pdfForm'); f.action = '/api/pdf/rebound_method_report/generate'; f.target = '_blank'; f.submit(); },
previewPdf() { const f = document.getElementById('pdfForm'); f.action = '/api/pdf/rebound_method_report/preview'; f.target = '_blank'; f.submit(); }
}
});
</script>
</body>
</html>'''

# 保存文件
output_dir = r"d:\develop\IdeaWorkSpace\work121\src\main\resources\static"

pages = [
    ("density_test_result-vue.html", density_test_result),
    ("rebound_method_report-vue.html", rebound_method_report)
]

for filename, content in pages:
    file_path = os.path.join(output_dir, filename)
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f"Created: {file_path}")

print("\nPages generated successfully!")
