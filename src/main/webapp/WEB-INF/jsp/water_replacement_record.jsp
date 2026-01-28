<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>相对密度试验记录表（灌水法）</title>
    <style>
        body {
            font-family: 'SimSun', 'Songti SC', serif;
            width: 260mm; /* Using wider width similar to sand replacement record */
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
        <button onclick="generatePdf()" style="float: right; margin-left: 10px;">下载PDF</button>
        <button onclick="previewPdf()" style="float: right; margin-left: 10px;">预览PDF</button>
    </div>

    <form id="pdfForm" method="post">
    <h2>相对密度试验记录表（灌水法）</h2>

    <div class="header-info">
        <span>工程部位：<input type="text" name="projectName" style="width: 200px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>试验日期：<input type="text" name="testDate" style="width: 150px; border-bottom: 1px solid black;"></span>
    </div>
    <div class="header-info">
        <span>依据标准：<input type="text" name="standard" style="width: 150px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>最大干密度：<input type="text" name="maxDryDensity" style="width: 80px; border-bottom: 1px solid black;"> g/cm³</span>
        <span>最小干密度：<input type="text" name="minDryDensity" style="width: 80px; border-bottom: 1px solid black;"> g/cm³</span>
        <span>最优含水率：<input type="text" name="optMoisture" style="width: 80px; border-bottom: 1px solid black;"> %</span>
        <span>相对密度：<input type="text" name="relativeDensity" style="width: 80px; border-bottom: 1px solid black;"></span>
        <span>统一编号：<input type="text" name="unifiedNumber" style="width: 150px; border-bottom: 1px solid black;"></span>
    </div>
    <div class="header-info">
        <span>水的密度：<input type="text" name="waterDensity" style="width: 80px; border-bottom: 1px solid black;"> g/cm³</span>
        <span>仪器设备：<input type="text" name="equipment" style="width: 150px; border-bottom: 1px solid black;"></span>
        <span>设计压实度：<input type="text" name="designCompaction" style="width: 80px; border-bottom: 1px solid black;"></span>
        <span>检测类别：<input type="text" name="testCategory" style="width: 100px; border-bottom: 1px solid black;"></span>
    </div>

    <table>
        <!-- Table Header -->
        <tr>
            <td colspan="4" class="label" style="width: 20%;">取样位置</td>
            <% for(int i=0; i<4; i++) { %>
            <td colspan="4"><input type="text" name="samplingLocation_<%=i%>"></td>
            <% } %>
        </tr>
        <tr>
            <td colspan="3" class="label">套环体积</td>
            <td class="label">cm³</td>
            <% for(int i=0; i<8; i++) { %>
            <td colspan="2"><input type="text" name="ringVolume_<%=i%>"></td>
            <% } %>
        </tr>
        <tr>
            <td rowspan="2" colspan="2" class="label">储水桶<br>水位</td>
            <td class="label">初始</td>
            <td class="label">cm</td>
            <% for(int i=0; i<8; i++) { %>
            <td colspan="2"><input type="text" name="initialWaterLevel_<%=i%>"></td>
            <% } %>
        </tr>
        <tr>
            <td class="label">终了</td>
            <td class="label">cm</td>
            <% for(int i=0; i<8; i++) { %>
            <td colspan="2"><input type="text" name="finalWaterLevel_<%=i%>"></td>
            <% } %>
        </tr>
        <tr>
            <td colspan="3" class="label">储水桶面积</td>
            <td class="label">cm²</td>
            <% for(int i=0; i<8; i++) { %>
            <td colspan="2"><input type="text" name="tankArea_<%=i%>"></td>
            <% } %>
        </tr>
        <tr>
            <td colspan="3" class="label">试坑体积</td>
            <td class="label">cm³</td>
            <% for(int i=0; i<8; i++) { %>
            <td colspan="2"><input type="text" name="pitVolume_<%=i%>"></td>
            <% } %>
        </tr>
        <tr>
            <td colspan="3" class="label">试样质量</td>
            <td class="label">g</td>
            <% for(int i=0; i<8; i++) { %>
            <td colspan="2"><input type="text" name="sampleMass_<%=i%>"></td>
            <% } %>
        </tr>
        <tr>
            <td colspan="3" class="label">试样湿密度</td>
            <td class="label">g/cm³</td>
            <% for(int i=0; i<8; i++) { %>
            <td colspan="2"><input type="text" name="wetDensity_<%=i%>"></td>
            <% } %>
        </tr>

        <!-- Moisture Content Section -->
        <tr>
            <td rowspan="6" colspan="2" class="label" style="width: 5%;">试样<br>含水率</td>
            <td class="label">编号</td>
            <td class="label">/</td>
            <% for(int i=0; i<16; i++) { %>
            <td><input type="text" name="moistureNo_<%=i%>"></td>
            <% } %>
        </tr>
        <tr>
            <td class="label">容器质量</td>
            <td class="label">g</td>
            <% for(int i=0; i<16; i++) { %>
            <td><input type="text" name="containerMass_<%=i%>"></td>
            <% } %>
        </tr>
        <tr>
            <td class="label">容器+湿样质量</td>
            <td class="label">g</td>
            <% for(int i=0; i<16; i++) { %>
            <td><input type="text" name="wetSampleMass_<%=i%>"></td>
            <% } %>
        </tr>
        <tr>
            <td class="label">容器+干样质量</td>
            <td class="label">g</td>
            <% for(int i=0; i<16; i++) { %>
            <td><input type="text" name="drySampleMass_<%=i%>"></td>
            <% } %>
        </tr>
        <tr>
            <td class="label">含水率</td>
            <td class="label">%</td>
            <% for(int i=0; i<16; i++) { %>
            <td><input type="text" name="moistureContent_<%=i%>"></td>
            <% } %>
        </tr>
        <tr>
            <td class="label">平均含水率</td>
            <td class="label">%</td>
            <% for(int i=0; i<8; i++) { %>
            <td colspan="2"><input type="text" name="avgMoisture_<%=i%>"></td>
            <% } %>
        </tr>

        <!-- Bottom Section -->
        <tr>
            <td colspan="3" class="label">实测干密度</td>
            <td class="label">g/cm³</td>
            <% for(int i=0; i<8; i++) { %>
            <td colspan="2"><input type="text" name="measuredDryDensity_<%=i%>"></td>
            <% } %>
        </tr>
        <tr>
            <td colspan="3" class="label">平均实测干密度</td>
            <td class="label">g/cm³</td>
            <% for(int i=0; i<4; i++) { %>
            <td colspan="4"><input type="text" name="avgMeasuredDryDensity_<%=i%>"></td>
            <% } %>
        </tr>
        <tr>
            <td colspan="3" class="label">相对密度</td>
            <td class="label"></td>
            <% for(int i=0; i<4; i++) { %>
            <td colspan="4"><input type="text" name="relativeDensity_<%=i%>"></td>
            <% } %>
        </tr>
        <tr>
            <td colspan="4" class="label">备注</td>
            <td colspan="16" class="left-align" style="height: 60px; vertical-align: top;">
                <textarea name="remarks" style="width: 100%; height: 100%;"></textarea>
            </td>
        </tr>
    </table>

    <div class="footer-info">
        <span>批准：<input type="text" name="approver" style="width: 100px; border-bottom: 1px solid black;"></span>
        <span>审核：<input type="text" name="reviewer" style="width: 100px; border-bottom: 1px solid black;"></span>
        <span>试验：<input type="text" name="tester" style="width: 100px; border-bottom: 1px solid black;"></span>
    </div>


    </form>

<script>
        function generatePdf() {
            var form = document.getElementById('pdfForm');
            form.action = '/api/pdf/water_replacement_record/generate';
            form.target = '_blank';
            form.submit();
        }

        function previewPdf() {
            var form = document.getElementById('pdfForm');
            form.action = '/api/pdf/water_replacement_record/preview';
            form.target = '_blank';
            form.submit();
        }
    </script>
</body>
</html>