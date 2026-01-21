<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>回弹法检测混凝土抗压强度记录表</title>
    <style>
        body {
            font-family: 'SimSun', 'Songti SC', serif;
            width: 260mm;
            margin: 0 auto;
            padding: 20px;
        }
        h2 {
            text-align: center;
            margin-bottom: 20px;
            font-size: 24px;
            font-weight: bold;
        }
        .header-top {
            display: flex;
            justify-content: space-between;
            margin-bottom: 5px;
            font-weight: bold;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            border: 2px solid black;
            table-layout: fixed;
        }
        td {
            border: 1px solid black;
            padding: 2px;
            vertical-align: middle;
            text-align: center;
            font-size: 14px;
            overflow: hidden;
        }
        .label {
            font-weight: bold;
            white-space: normal;
        }
        .left-align {
            text-align: left;
            padding-left: 5px;
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
            margin-top: 10px;
            font-size: 16px;
            font-weight: bold;
        }
        .page-footer {
            margin-top: 5px;
            display: flex;
            justify-content: space-between;
            font-size: 14px;
        }
        @media print {
            body {
                width: 100%;
                margin: 0;
                padding: 0;
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

    <h2>回弹法检测混凝土抗压强度记录表</h2>

    <div class="header-top">
        <span>委托单位：<input type="text" style="width: 200px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>统一编号：<input type="text" style="width: 150px; border-bottom: 1px solid black;"></span>
    </div>
    <div class="header-top">
        <span>样品编号：<input type="text" style="width: 200px; border-bottom: 1px solid black; text-align: left;"></span>
    </div>

    <table>
        <colgroup>
            <col style="width: 4%;"> <!-- Col 1: Index -->
            <% for(int i=0; i<16; i++) { %>
            <col style="width: 3%;"> <!-- Cols 2-17: Values (48%) -->
            <% } %>
            <col style="width: 12%;"> <!-- Col 18: Avg -->
            <col style="width: 12%;"> <!-- Col 19: Depth -->
            <col style="width: 12%;"> <!-- Col 20: Est Str -->
            <col style="width: 12%;"> <!-- Col 21: Corr Str -->
        </colgroup>

        <!-- Header Info -->
        <tr>
            <td colspan="2" class="label">工程名称</td>
            <td colspan="13" class="left-align"><input type="text" name="projectName" style="text-align: left;"></td>
            <td colspan="3" class="label">委托日期</td>
            <td colspan="3"><input type="text" name="commissionDate"></td>
        </tr>
        <tr>
            <td colspan="2" class="label">结构部位</td>
            <td colspan="5"><input type="text" name="structurePart"></td>
            <td colspan="3" class="label">检测类别</td>
            <td colspan="4"><input type="text" name="testCategory"></td>
            <td colspan="3" class="label">浇筑日期</td>
            <td colspan="4"><input type="text" name="pourDate"></td>
        </tr>
        <tr>
            <td colspan="2" class="label">样品状态</td>
            <td colspan="5"><input type="text" name="sampleStatus"></td>
            <td colspan="3" class="label">测试角度</td>
            <td colspan="4"><input type="text" name="testAngle"></td>
            <td colspan="3" class="label">检测日期</td>
            <td colspan="4"><input type="text" name="testDate"></td>
        </tr>
        <tr>
            <td colspan="2" class="label">设计指标</td>
            <td colspan="5"><input type="text" name="designIndex"></td>
            <td colspan="7" class="label">构件厚度或骨料最大粒径</td>
            <td colspan="7"><input type="text" name="aggregateSize"></td>
        </tr>

        <!-- Data Table Header -->
        <tr>
            <td rowspan="2" class="label">测<br>区</td>
            <td colspan="16" class="label">回弹值</td>
            <td rowspan="2" class="label">平均<br>回弹<br>值</td>
            <td rowspan="2" class="label">碳化<br>深度<br>mm</td>
            <td rowspan="2" class="label">推定<br>强度<br>值MPa</td>
            <td rowspan="2" class="label">碳化修<br>正强度<br>值MPa</td>
        </tr>
        <tr>
            <% for(int i=1; i<=16; i++) { %>
            <td class="label"><%=i%></td>
            <% } %>
        </tr>

        <!-- Data Rows -->
        <% for(int i=1; i<=10; i++) { %>
        <tr>
            <td><%=i%></td>
            <% for(int j=1; j<=16; j++) { %>
            <td><input type="text" name="reboundValue_<%=i%>_<%=j%>"></td>
            <% } %>
            <td><input type="text" name="avgRebound_<%=i%>"></td>
            <td><input type="text" name="carbonDepth_<%=i%>"></td>
            <td><input type="text" name="estimatedStrength_<%=i%>"></td>
            <td><input type="text" name="correctedStrength_<%=i%>"></td>
        </tr>
        <% } %>

        <!-- Summary Section -->
        <tr>
            <td colspan="3" class="label">平均强度值<br>MPa</td>
            <td colspan="2"><input type="text" name="avgStrength"></td>
            <td colspan="3" class="label">标准差<br>MPa</td>
            <td colspan="2"><input type="text" name="stdDev"></td>
            <td colspan="3" class="label">变异系<br>数%</td>
            <td colspan="2"><input type="text" name="coefVariation"></td>
            <td colspan="3" class="label">构件强度推<br>定值 MPa</td>
            <td colspan="3"><input type="text" name="compEstimatedStrength"></td>
        </tr>
        <tr>
            <td colspan="2" class="label">仪器设备</td>
            <td colspan="11" class="left-align"><input type="text" name="equipment" style="text-align: left;"></td>
            <td colspan="4" class="label">碳化平均值<br>mm</td>
            <td colspan="4"><input type="text" name="avgCarbonation"></td>
        </tr>
        <tr>
            <td colspan="2" class="label">依据标准</td>
            <td colspan="11" class="left-align"><input type="text" name="standard" style="text-align: left;"></td>
            <td colspan="4" class="label">率定值</td>
            <td colspan="4" class="left-align">
                检测前：<input type="text" name="calibrationBefore" style="width: 60px; border-bottom: 1px solid #ccc; text-align: center;"><br>
                检测后：<input type="text" name="calibrationAfter" style="width: 60px; border-bottom: 1px solid #ccc; text-align: center;">
            </td>
        </tr>
        <tr>
            <td colspan="2" class="label">结论</td>
            <td colspan="19" class="left-align" style="height: 40px;">
                <input type="text" name="conclusion" style="text-align: left; width: 100%;">
            </td>
        </tr>
        <tr>
            <td colspan="2" class="label" style="height: 150px; vertical-align: top;">测区示意图：</td>
            <td colspan="19" style="height: 150px;">
                <!-- Placeholder for diagram -->
            </td>
        </tr>
        <tr>
            <td colspan="2" class="label">备注</td>
            <td colspan="19" class="left-align" style="height: 40px;">
                <textarea name="remarks" style="width: 100%; height: 100%;"></textarea>
            </td>
        </tr>
    </table>

    <div class="footer-info">
        <span>审核：<input type="text" style="width: 100px; border-bottom: 1px solid black;"></span>
        <span>检验：<input type="text" style="width: 100px; border-bottom: 1px solid black;"></span>
    </div>

    <div class="page-footer">
        <span>版次：<input type="text" style="width: 50px; border-bottom: 1px solid black; text-align: center;"></span>
        <span>第 <input type="text" style="width: 20px; border-bottom: 1px solid black; text-align: center;"> 页，共 <input type="text" style="width: 20px; border-bottom: 1px solid black; text-align: center;"> 页</span>
    </div>

<script>
        function generatePdf() {
            const form = document.createElement('form');
            form.method = 'post';
            form.action = '/api/pdf/rebound_method_record/generate';
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
            form.action = '/api/pdf/rebound_method_record/preview';
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