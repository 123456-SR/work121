<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>原位密度检测记录表（灌砂法）</title>
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
        }
        .header-info span {
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
            justify-content: space-around;
            margin-top: 20px;
            margin-bottom: 10px;
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

    <form id="pdfForm" method="post">
    <h2>原位密度检测记录表（灌砂法）</h2>

    <div class="header-info">
        <span>单元工程名称：<input type="text" name="projectName" style="width: 200px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>试验日期：<input type="text" name="testDate" style="width: 150px; border-bottom: 1px solid black;"></span>
        <span>统一编号：<input type="text" name="unifiedNumber" style="width: 150px; border-bottom: 1px solid black;"></span>
    </div>
     <div class="header-info">
        <span>依据标准：<input type="text" name="standard" style="width: 150px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>最大干密度 (g/cm³)：<input type="text" name="maxDryDensity" style="width: 80px; border-bottom: 1px solid black;"></span>
        <span>最优含水率 (%)：<input type="text" name="optMoisture" style="width: 80px; border-bottom: 1px solid black;"></span>
        <span>最小干密度 (g/cm³)：<input type="text" name="minDryDensity" style="width: 80px; border-bottom: 1px solid black;"></span>
    </div>
    <div class="header-info">
        <span>量砂密度：<input type="text" name="sandDensity" style="width: 80px; border-bottom: 1px solid black;"> g/cm³</span>
        <span>仪器设备：<input type="text" name="equipment" style="width: 150px; border-bottom: 1px solid black;"></span>
        <span>检测类别：<input type="text" name="testCategory" style="width: 100px; border-bottom: 1px solid black;"></span>
        <span>设计压实度：<input type="text" name="designCompaction" style="width: 100px; border-bottom: 1px solid black;"></span>
    </div>


    <table>
        <!-- Table Header -->
        <tr>
            <td colspan="2" class="label" style="width: 20%;">检测部位<br>(桩号、高程)</td>
            <% for(int i=0; i<4; i++) { %>
            <td colspan="4"><input type="text" name="location_<%=i%>"></td>
            <% } %>
        </tr>

        <!-- Data Rows -->
        <tr>
            <td class="label">量砂容器+原有砂质量</td>
            <td class="label">g</td>
            <% for(int i=0; i<8; i++) { %>
            <td colspan="2"><input type="text" name="totalSandMass_<%=i%>"></td>
            <% } %>
        </tr>
        <tr>
            <td class="label">量砂容器+剩余砂质量</td>
            <td class="label">g</td>
            <% for(int i=0; i<8; i++) { %>
            <td colspan="2"><input type="text" name="remainingSandMass_<%=i%>"></td>
            <% } %>
        </tr>
        <tr>
            <td class="label">锥体内砂质量</td>
            <td class="label">g</td>
            <% for(int i=0; i<8; i++) { %>
            <td colspan="2"><input type="text" name="coneSandMass_<%=i%>"></td>
            <% } %>
        </tr>
        <tr>
            <td class="label">试坑耗砂质量</td>
            <td class="label">g</td>
            <% for(int i=0; i<8; i++) { %>
            <td colspan="2"><input type="text" name="pitSandMass_<%=i%>"></td>
            <% } %>
        </tr>
        <tr>
            <td class="label">试坑体积</td>
            <td class="label">cm³</td>
            <% for(int i=0; i<8; i++) { %>
            <td colspan="2"><input type="text" name="pitVolume_<%=i%>"></td>
            <% } %>
        </tr>
        <tr>
            <td class="label">试样质量</td>
            <td class="label">g</td>
            <% for(int i=0; i<8; i++) { %>
            <td colspan="2"><input type="text" name="sampleMass_<%=i%>"></td>
            <% } %>
        </tr>
        <tr>
            <td class="label">试样湿密度</td>
            <td class="label">g/cm³</td>
            <% for(int i=0; i<8; i++) { %>
            <td colspan="2"><input type="text" name="wetDensity_<%=i%>"></td>
            <% } %>
        </tr>

        <!-- Moisture Content Section -->
        <tr>
            <td rowspan="6" class="label">试样含水率</td>
            <td class="label">盒号</td>
            <% for(int i=0; i<16; i++) { %>
            <td><input type="text" name="boxNo_<%=i%>"></td>
            <% } %>
        </tr>
        <tr>
            <td class="label">盒质量 g</td>
            <% for(int i=0; i<16; i++) { %>
            <td><input type="text" name="boxMass_<%=i%>"></td>
            <% } %>
        </tr>
        <tr>
            <td class="label">盒+湿样质量 g</td>
            <% for(int i=0; i<16; i++) { %>
            <td><input type="text" name="boxWetMass_<%=i%>"></td>
            <% } %>
        </tr>
        <tr>
            <td class="label">盒+干样质量 g</td>
            <% for(int i=0; i<16; i++) { %>
            <td><input type="text" name="boxDryMass_<%=i%>"></td>
            <% } %>
        </tr>
        <tr>
            <td class="label">含水率 %</td>
            <% for(int i=0; i<16; i++) { %>
            <td><input type="text" name="moistureContent_<%=i%>"></td>
            <% } %>
        </tr>
         <tr>
            <td class="label">平均含水率 %</td>
            <% for(int i=0; i<8; i++) { %>
            <td colspan="2"><input type="text" name="avgMoisture_<%=i%>"></td>
            <% } %>
        </tr>


        <!-- Results -->
        <tr>
            <td colspan="2" class="label">实测干密度 g/cm³</td>
            <% for(int i=0; i<8; i++) { %>
            <td colspan="2"><input type="text" name="dryDensity_<%=i%>"></td>
            <% } %>
        </tr>
        <tr>
            <td colspan="2" class="label">平均实测干密度 g/cm³</td>
            <% for(int i=0; i<4; i++) { %>
            <td colspan="4"><input type="text" name="avgDryDensity_<%=i%>"></td>
            <% } %>
        </tr>
        <tr>
            <td colspan="2" class="label">实测压实度</td>
            <% for(int i=0; i<4; i++) { %>
            <td colspan="4"><input type="text" name="compaction_<%=i%>"></td>
            <% } %>
        </tr>
        <tr>
            <td colspan="2" class="label">备注</td>
            <td colspan="16" class="left-align"><input type="text" name="remarks"></td>
        </tr>
    </table>

    <div class="footer-info">
        <span>批准：<input type="text" name="approver" style="width: 100px; border-bottom: 1px solid black;"></span>
        <span>审核：<input type="text" name="reviewer" style="width: 100px; border-bottom: 1px solid black;"></span>
        <span>试验：<input type="text" name="tester" style="width: 100px; border-bottom: 1px solid black;"></span>
    </div>

    <div class="page-footer">
        <span>版次：<input type="text" name="version" style="width: 50px; border-bottom: 1px solid black; text-align: center;"></span>
        <span><input type="text" name="year" style="width: 40px; border-bottom: 1px solid black; text-align: center;">年<input type="text" name="month" style="width: 20px; border-bottom: 1px solid black; text-align: center;">月<input type="text" name="day" style="width: 20px; border-bottom: 1px solid black; text-align: center;">日</span>
        <span>第 <input type="text" name="page" style="width: 20px; border-bottom: 1px solid black; text-align: center;"> 页，共 <input type="text" name="totalPages" style="width: 20px; border-bottom: 1px solid black; text-align: center;"> 页</span>
    </div>
    </form>

<script>
        function generatePdf() {
            var form = document.getElementById('pdfForm');
            form.action = '/api/pdf/sand_replacement_record/generate';
            form.target = '_blank';
            form.submit();
        }

        function previewPdf() {
            var form = document.getElementById('pdfForm');
            form.action = '/api/pdf/sand_replacement_record/preview';
            form.target = '_blank';
            form.submit();
        }
    </script>
</body>
</html>