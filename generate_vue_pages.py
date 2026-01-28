import os

pages = [
    ("nuclear_density_record", "原位密度检测记录表（核子法）"),
    ("density_test_report", "原位密度检测报告"),
    ("density_test_result", "原位密度检测结果"),
    ("rebound_method_report", "回弹法检测混凝土抗压强度报告"),
    ("beckman_beam_report", "路基路面回弹弯沉（回弹模量）检测报告"),
    ("beckman_beam_result", "路基路面回弹弯沉（回弹模量）检测结果")
]

template = '''<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>{title}</title>
<script src="https://cdn.jsdelivr.net/npm/vue@2.6.14/dist/vue.js"></script>
<style>
body {{ font-family: 'SimSun', 'Songti SC', serif; width: 210mm; margin: 0 auto; padding: 20px; }}
h2 {{ text-align: center; margin-bottom: 20px; font-size: 24px; font-weight: bold; }}
.header-info {{ display: flex; justify-content: space-between; margin-bottom: 5px; font-weight: bold; }}
table {{ width: 100%; border-collapse: collapse; border: 2px solid black; }}
td {{ border: 1px solid black; padding: 5px; vertical-align: middle; text-align: center; }}
.label {{ font-weight: bold; white-space: nowrap; }}
.left-align {{ text-align: left; padding-left: 10px; }}
input[type="text"], textarea {{ width: 98%; border: none; outline: none; font-family: inherit; font-size: inherit; background-color: transparent; text-align: center; }}
.left-align input[type="text"] {{ text-align: left; }}
input[type="text"]:focus, textarea:focus {{ background-color: #f0f8ff; }}
textarea {{ resize: none; overflow: hidden; text-align: left; }}
.footer-info {{ display: flex; justify-content: space-between; margin-top: 30px; margin-bottom: 20px; font-size: 16px; font-weight: bold; padding: 0 50px; }}
.page-footer {{ margin-top: 10px; font-size: 14px; margin-bottom: 20px; }}
.no-print {{ margin-bottom: 20px; }}
.no-print a {{ text-decoration: none; color: blue; }}
@media print {{ .no-print {{ display: none; }} }}
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
<h2>{title}</h2>

<div class="header-info">
<span>委托单位：<input type="text" v-model="formData.entrustingUnit" style="width: 250px; border-bottom: 1px solid black; text-align: left;"></span>
<span>统一编号：<input type="text" v-model="formData.unifiedNumber" style="width: 150px; border-bottom: 1px solid black;"></span>
</div>

<table>
<tr><td class="label">工程名称</td><td colspan="4"><input type="text" v-model="formData.projectName"></td><td class="label">委托日期</td><td colspan="4"><input type="text" v-model="formData.commissionDate"></td></tr>
<tr><td class="label">施工部位</td><td colspan="4"><input type="text" v-model="formData.constructionPart"></td><td class="label">检测日期</td><td colspan="4"><input type="text" v-model="formData.testDate"></td></tr>

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

<div class="page-footer">
<span>版次：<input type="text" v-model="formData.version" style="width: 50px; border-bottom: 1px solid black; text-align: center;"></span>
<span><input type="text" v-model="formData.year" style="width: 40px; border-bottom: 1px solid black; text-align: center;">年<input type="text" v-model="formData.month" style="width: 20px; border-bottom: 1px solid black; text-align: center;">月<input type="text" v-model="formData.day" style="width: 20px; border-bottom: 1px solid black; text-align: center;">日</span>
<span>第 <input type="text" v-model="formData.page" style="width: 20px; border-bottom: 1px solid black; text-align: center;"> 页，共 <input type="text" v-model="formData.totalPages" style="width: 20px; border-bottom: 1px solid black; text-align: center;"> 页</span>
</div>
</form>
</div>

<script>
new Vue({{
el: '#app',
data: {{
formData: {{
entrustingUnit: '', unifiedNumber: '', projectName: '', commissionDate: '', constructionPart: '', testDate: '',
testBasis: '', equipment: '', conclusion: '', remarks: '',
reviewer: '', calculator: '', tester: '',
version: '', year: new Date().getFullYear(), month: String(new Date().getMonth() + 1).padStart(2, '0'),
day: String(new Date().getDate()).padStart(2, '0'), page: '1', totalPages: '1'
}}
}},
methods: {{
printDocument() {{ window.print(); }},
generatePdf() {{ const f = document.getElementById('pdfForm'); f.action = '/api/pdf/{page_name}/generate'; f.target = '_blank'; f.submit(); }},
previewPdf() {{ const f = document.getElementById('pdfForm'); f.action = '/api/pdf/{page_name}/preview'; f.target = '_blank'; f.submit(); }}
}}
}});
</script>
</body>
</html>'''

output_dir = r"d:\develop\IdeaWorkSpace\work121\src\main\resources\static"

for page_name, title in pages:
    content = template.format(page_name=page_name, title=title)
    file_path = os.path.join(output_dir, f"{page_name}-vue.html")
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f"Created: {file_path}")

print("\nAll pages generated successfully!")