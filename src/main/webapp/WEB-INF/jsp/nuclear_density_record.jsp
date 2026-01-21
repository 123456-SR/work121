<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>原位密度检测记录表（核子法）</title>
    <style>
        body {
            font-family: 'SimSun', 'Songti SC', serif;
            width: 210mm;
            margin: 0 auto;
            padding: 20px;
        }
        h2 {
            text-align: center;
            margin-bottom: 20px;
            font-size: 24px;
            font-weight: bold;
        }
        .header-info {
            display: flex;
            justify-content: space-between;
            margin-bottom: 5px;
            font-weight: bold;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            border: 2px solid black;
        }
        td {
            border: 1px solid black;
            padding: 5px;
            vertical-align: middle;
            text-align: center;
        }
        .label {
            font-weight: bold;
            white-space: nowrap;
        }
        .left-align {
            text-align: left;
            padding-left: 10px;
        }
        input[type="text"], textarea {
            width: 98%;
            border: none;
            outline: none;
            font-family: inherit;
            font-size: inherit;
            background-color: transparent;
            text-align: center;
        }
        .left-align input[type="text"] {
            text-align: left;
        }
        input[type="text"]:focus, textarea:focus {
            background-color: #f0f8ff;
        }
        textarea {
            resize: none;
            overflow: hidden;
            text-align: left;
        }
        .footer-info {
            display: flex;
            justify-content: space-between;
            margin-top: 30px;
            margin-bottom: 20px;
            font-size: 16px;
            font-weight: bold;
            padding: 0 50px;
        }
        .page-footer {
            margin-top: 10px;
            font-size: 14px;
            margin-bottom: 20px;
        }
        @media print {
            body {
                width: 100%;
                margin: 0;
                padding: 0;
            }
            input[type="text"], textarea {
                background-color: transparent !important;
            }
            .no-print {
                display: none;
            }
        }
    </style>
</head>
<body>

    <div class="no-print" style="margin-bottom: 20px;">
        <a href="/" style="text-decoration: none; color: blue;">&lt; 返回主页</a>
        <button onclick="window.print()" style="float: right; margin-left: 10px;">打印此单</button>
        <button onclick="generatePdf()" style="float: right; margin-left: 10px; background-color: #28a745; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;">下载PDF</button>
        <button onclick="previewPdf()" style="float: right; margin-left: 10px; background-color: #17a2b8; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;">预览PDF</button>
    </div>

    <h2>原位密度检测记录表（核子法）</h2>

    <div class="header-info">
        <span>委托单位：<input type="text" style="width: 250px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>统一编号：<input type="text" style="width: 150px; border-bottom: 1px solid black;"></span>
    </div>

    <table>
        <!-- Header Section -->
        <tr>
            <td class="label">工程名称</td>
            <td colspan="4"><input type="text" name="projectName"></td>
            <td class="label">委托日期</td>
            <td colspan="2"><input type="text" name="commissionDate"></td>
        </tr>
        <tr>
            <td class="label">施工部位</td>
            <td colspan="4"><input type="text" name="constructionPart"></td>
            <td class="label">检测类别</td>
            <td colspan="2"><input type="text" name="testCategory"></td>
        </tr>
        <tr>
            <td class="label">仪器设备</td>
            <td colspan="4"><input type="text" name="equipment"></td>
            <td class="label">检测方法</td>
            <td colspan="2"><input type="text" name="testMethod" value="核子法"></td>
        </tr>
        <tr>
            <td class="label">样品名称及<br>状态</td>
            <td colspan="4"><input type="text" name="sampleNameStatus"></td>
            <td class="label">依据标准</td>
            <td colspan="2"><input type="text" name="standard"></td>
        </tr>
        <tr>
            <td class="label">最大干密度<br>(g/cm³)</td>
            <td colspan="2"><input type="text" name="maxDryDensity"></td>
            <td class="label">最优含水率 %</td>
            <td colspan="2"><input type="text" name="optimumMoisture"></td>
            <td class="label">最小干密度<br>(g/cm³)</td>
            <td><input type="text" name="minDryDensity"></td>
        </tr>

        <!-- Data Header -->
        <tr>
            <td class="label" style="width: 10%;">样品编号</td>
            <td class="label" style="width: 20%;">检测部位<br>(桩号、高程)</td>
            <td class="label" style="width: 12%;">检测日期</td>
            <td class="label" style="width: 12%;">湿密度<br>(g/cm³)</td>
            <td class="label" style="width: 12%;">干密度<br>(g/cm³)</td>
            <td class="label" style="width: 12%;">含水率<br>%</td>
            <td class="label" style="width: 12%;">压实度%</td>
            <td class="label" style="width: 10%;">备注</td>
        </tr>

        <!-- Data Rows -->
        <%
        for(int i=0; i<15; i++) {
        %>
        <tr>
            <td><input type="text" name="sampleId_<%=i%>"></td>
            <td><input type="text" name="location_<%=i%>"></td>
            <td><input type="text" name="date_<%=i%>"></td>
            <td><input type="text" name="wetDensity_<%=i%>"></td>
            <td><input type="text" name="dryDensity_<%=i%>"></td>
            <td><input type="text" name="moisture_<%=i%>"></td>
            <td><input type="text" name="compaction_<%=i%>"></td>
            <td><input type="text" name="remarks_<%=i%>"></td>
        </tr>
        <%
        }
        %>
    </table>

    <div class="footer-info">
        <span>审核：<input type="text" style="width: 100px; border-bottom: 1px solid black;"></span>
        <span>检测：<input type="text" style="width: 100px; border-bottom: 1px solid black;"></span>
    </div>

    <div class="page-footer" style="display: flex; justify-content: space-between; align-items: center;">
        <span>版次：<input type="text" style="width: 50px; border-bottom: 1px solid black; text-align: center;"></span>
        <span><input type="text" style="width: 40px; border-bottom: 1px solid black; text-align: center;">年<input type="text" style="width: 20px; border-bottom: 1px solid black; text-align: center;">月<input type="text" style="width: 20px; border-bottom: 1px solid black; text-align: center;">日</span>
        <span>第 <input type="text" style="width: 20px; border-bottom: 1px solid black; text-align: center;"> 页，共 <input type="text" style="width: 20px; border-bottom: 1px solid black; text-align: center;"> 页</span>
    </div>

<script>
        function generatePdf() {
            const form = document.createElement('form');
            form.method = 'post';
            form.action = '/api/pdf/nuclear_density_record/generate';
            form.target = '_blank';

            const inputs = document.querySelectorAll('input[type="text"], textarea');
            inputs.forEach(input => {
                const inputClone = document.createElement('input');
                inputClone.type = 'hidden';
                inputClone.name = input.name;
                inputClone.value = input.value;
                form.appendChild(inputClone);
            });

            document.body.appendChild(form);
            form.submit();
            document.body.removeChild(form);
        }

        function previewPdf() {
            const form = document.createElement('form');
            form.method = 'post';
            form.action = '/api/pdf/nuclear_density_record/preview';
            form.target = '_blank';

            const inputs = document.querySelectorAll('input[type="text"], textarea');
            inputs.forEach(input => {
                const inputClone = document.createElement('input');
                inputClone.type = 'hidden';
                inputClone.name = input.name;
                inputClone.value = input.value;
                form.appendChild(inputClone);
            });

            document.body.appendChild(form);
            form.submit();
            document.body.removeChild(form);
        }
    </script>
</body>
</html>
