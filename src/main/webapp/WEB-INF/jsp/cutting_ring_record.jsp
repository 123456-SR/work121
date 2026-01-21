<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>原位密度检测记录表（环刀法）</title>
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
        .header-info {
            display: flex;
            justify-content: space-between;
            margin-bottom: 5px;
            font-weight: bold;
            flex-wrap: wrap;
        }
        .header-info span {
            margin-right: 20px;
            margin-bottom: 5px;
            white-space: nowrap;
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
            font-size: 14px;
        }
        .label {
            font-weight: bold;
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

    <h2>原位密度检测记录表（环刀法）</h2>

    <div class="header-info">
        <span>施工部位：<input type="text" style="width: 200px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>最大干密度 (g/cm³)：<input type="text" style="width: 80px; border-bottom: 1px solid black;"></span>
        <span>最优含水率 (%)：<input type="text" style="width: 80px; border-bottom: 1px solid black;"></span>
        <span>检测类别：<input type="text" style="width: 100px; border-bottom: 1px solid black;"></span>
    </div>
    <div class="header-info">
        <span>依据标准：<input type="text" style="width: 200px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>设计压实度：<input type="text" style="width: 80px; border-bottom: 1px solid black;"></span>
        <span>检测日期：<input type="text" style="width: 150px; border-bottom: 1px solid black;"></span>
    </div>

    <table>
        <thead>
            <tr>
                <td rowspan="2" class="label" style="width: 5%;">样品<br>编号</td>
                <td rowspan="2" class="label" style="width: 10%;">检测部位<br>(桩号、<br>高程)</td>
                <td rowspan="2" class="label" style="width: 5%;">样品<br>状态</td>
                <td rowspan="2" class="label">环刀<br>号</td>
                <td rowspan="2" class="label">环刀<br>质量g</td>
                <td rowspan="2" class="label">环刀+湿<br>土质量g</td>
                <td rowspan="2" class="label">环刀体<br>积cm³</td>
                <td colspan="5" class="label">含水率测定</td>
                <td rowspan="2" class="label">平均<br>含水<br>率%</td>
                <td rowspan="2" class="label">湿密度<br>g/cm³</td>
                <td rowspan="2" class="label">干密度<br>g/cm³</td>
                <td rowspan="2" class="label">平均干<br>密度<br>g/cm³</td>
                <td rowspan="2" class="label">压实<br>度%</td>
            </tr>
            <tr>
                <td class="label">盒号</td>
                <td class="label">盒质量<br>g</td>
                <td class="label">盒+湿土<br>质量g</td>
                <td class="label">盒+干土<br>质量g</td>
                <td class="label">含水率<br>%</td>
            </tr>
        </thead>
        <tbody>
            <% for(int i=0; i<5; i++) { %>
            <tr>
                <td rowspan="2"><input type="text" name="sampleNo_<%=i%>"></td>
                <td rowspan="2"><input type="text" name="location_<%=i%>"></td>
                <td rowspan="2"><input type="text" name="status_<%=i%>"></td>
                <td rowspan="2"><input type="text" name="ringNo_<%=i%>"></td>
                <td rowspan="2"><input type="text" name="ringMass_<%=i%>"></td>
                <td rowspan="2"><input type="text" name="ringWetMass_<%=i%>"></td>
                <td rowspan="2"><input type="text" name="ringVolume_<%=i%>"></td>
                
                <!-- Moisture Box 1 -->
                <td><input type="text" name="boxNo1_<%=i%>"></td>
                <td><input type="text" name="boxMass1_<%=i%>"></td>
                <td><input type="text" name="boxWetMass1_<%=i%>"></td>
                <td><input type="text" name="boxDryMass1_<%=i%>"></td>
                <td><input type="text" name="moisture1_<%=i%>"></td>

                <td rowspan="2"><input type="text" name="avgMoisture_<%=i%>"></td>
                <td rowspan="2"><input type="text" name="wetDensity_<%=i%>"></td>
                <td rowspan="2"><input type="text" name="dryDensity_<%=i%>"></td>
                <td rowspan="2"><input type="text" name="avgDryDensity_<%=i%>"></td>
                <td rowspan="2"><input type="text" name="compaction_<%=i%>"></td>
            </tr>
            <tr>
                <!-- Moisture Box 2 -->
                <td><input type="text" name="boxNo2_<%=i%>"></td>
                <td><input type="text" name="boxMass2_<%=i%>"></td>
                <td><input type="text" name="boxWetMass2_<%=i%>"></td>
                <td><input type="text" name="boxDryMass2_<%=i%>"></td>
                <td><input type="text" name="moisture2_<%=i%>"></td>
            </tr>
            <% } %>
            <!-- Empty rows to fill space if needed, or just these 5 sample blocks (10 rows) -->
        </tbody>
        <tfoot>
            <tr>
                <td colspan="2" class="label">备注</td>
                <td colspan="15" class="left-align" style="height: 60px; vertical-align: top;">
                    <textarea name="remarks" style="width: 100%; height: 100%;"></textarea>
                </td>
            </tr>
        </tfoot>
    </table>

    <div class="footer-info">
        <span>审核：<input type="text" style="width: 100px; border-bottom: 1px solid black;"></span>
        <span>试验：<input type="text" style="width: 100px; border-bottom: 1px solid black;"></span>
    </div>

    <div class="page-footer">
        <span>版次：<input type="text" style="width: 50px; border-bottom: 1px solid black; text-align: center;"></span>
        <span><input type="text" style="width: 100px; border-bottom: 1px solid black; text-align: center;" placeholder="YYYY-MM-DD"></span>
        <span>第 <input type="text" style="width: 20px; border-bottom: 1px solid black; text-align: center;"> 页，共 <input type="text" style="width: 20px; border-bottom: 1px solid black; text-align: center;"> 页</span>
    </div>

<script>
        function generatePdf() {
            const form = document.createElement('form');
            form.method = 'post';
            form.action = '/api/pdf/cutting_ring_record/generate';
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
            form.action = '/api/pdf/cutting_ring_record/preview';
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