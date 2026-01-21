<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>路基路面回弹弯沉（回弹模量）检测报告</title>
    <style>
        body {
            font-family: 'SimSun', 'Songti SC', serif;
            width: 210mm;
            margin: 0 auto;
            padding: 20px;
        }
        h1 {
            text-align: center;
            font-size: 24px;
            margin-bottom: 20px;
        }
        .header-info {
            display: flex;
            justify-content: space-between;
            margin-bottom: 5px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            border: 1px solid black;
            table-layout: fixed;
            margin-bottom: -1px; /* Collapse borders between tables */
        }
        td, th {
            border: 1px solid black;
            padding: 5px;
            text-align: center;
            font-size: 14px;
            vertical-align: middle;
            word-wrap: break-word;
        }
        input[type="text"] {
            width: 95%;
            border: none;
            outline: none;
            text-align: center;
            font-family: inherit;
            font-size: inherit;
            background: transparent;
        }
        .label {
            text-align: center;
        }
        /* Hide navigation buttons when printing */
        @media print {
            .no-print {
                display: none;
            }
            body {
                width: 100%;
                margin: 0;
                padding: 0;
            }
            @page {
                size: A4 portrait;
                margin: 1cm;
            }
        }
        .nav-button {
            display: inline-block;
            margin: 10px;
            padding: 10px 20px;
            background-color: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 5px;
        }
        .footer-section {
            margin-top: 20px;
        }
        .footer-line {
            display: flex;
            justify-content: space-between;
            margin-bottom: 10px;
        }
        .company-info {
            margin-top: 20px;
            font-size: 14px;
        }
    </style>
</head>
<body>
    <div class="no-print" style="text-align: center;">
        <a href="/" class="nav-button">返回主页</a>
    </div>

    <h1>路基路面回弹弯沉（回弹模量）检测报告</h1>

    <div class="header-info">
        <div>委托单位：<input type="text" style="width: 200px; border-bottom: 1px solid black; text-align: left;"></div>
        <div>统一编号：<input type="text" style="width: 150px; border-bottom: 1px solid black;"></div>
    </div>

    <!-- Table 1: Header Info (Rows 1-6) -->
    <table>
        <colgroup>
            <col style="width: 15%;">
            <col style="width: 35%;">
            <col style="width: 15%;">
            <col style="width: 35%;">
        </colgroup>
        <tr>
            <td class="label">工程名称</td>
            <td><input type="text" name="projectName"></td>
            <td class="label">委托日期</td>
            <td><input type="text" name="commissionDate" class="date-input"></td>
        </tr>
        <tr>
            <td class="label">施工部位</td>
            <td><input type="text" name="constructionPart"></td>
            <td class="label">检测日期</td>
            <td><input type="text" name="testDate" class="date-input"></td>
        </tr>
        <tr>
            <td class="label">仪器设备及编码</td>
            <td><input type="text" name="equipmentCode"></td>
            <td class="label">报告日期</td>
            <td><input type="text" name="reportDate" class="date-input"></td>
        </tr>
        <tr>
            <td class="label">样品名称及状态</td>
            <td><input type="text" name="sampleStatus"></td>
            <td class="label">检测类别</td>
            <td><input type="text" name="testCategory"></td>
        </tr>
        <tr>
            <td class="label">依据标准</td>
            <td><input type="text" name="standard"></td>
            <td class="label">检测方法</td>
            <td><input type="text" name="testMethod"></td>
        </tr>
        <tr>
            <td class="label">见证单位</td>
            <td><input type="text" name="witnessUnit"></td>
            <td class="label">见证人</td>
            <td><input type="text" name="witness"></td>
        </tr>
    </table>

    <!-- Table 2: Technical Parameters (Rows 7-8) -->
    <table>
        <colgroup>
            <col style="width: 15%;">
            <col style="width: 20%;">
            <col style="width: 15%;">
            <col style="width: 15%;">
            <col style="width: 20%;">
            <col style="width: 15%;">
        </colgroup>
        <tr>
            <td class="label">路面结构类型</td>
            <td><input type="text" name="pavementType"></td>
            <td class="label">路面厚度<br>(mm)</td>
            <td><input type="text" name="pavementThickness"></td>
            <td class="label">温度修正系数</td>
            <td><input type="text" name="tempCorrection"></td>
        </tr>
        <tr>
            <td class="label">设计弯沉值<br>(0.01mm)</td>
            <td><input type="text" name="designDeflection"></td>
            <td class="label">沥青面层平均温度 (℃)</td>
            <td><input type="text" name="avgAsphaltTemp"></td>
            <td class="label">泊松比μ</td>
            <td><input type="text" name="poissonRatio"></td>
        </tr>
    </table>

    <!-- Table 3: Data Table -->
    <table>
        <colgroup>
            <col style="width: 10%;"> <!-- 序号 -->
            <col style="width: 25%;"> <!-- 测点桩号 -->
            <col style="width: 15%;"> <!-- 车道 -->
            <col style="width: 15%;"> <!-- 左侧 -->
            <col style="width: 20%;"> <!-- 右侧 -->
            <col style="width: 15%;"> <!-- 备注 -->
        </colgroup>
        <thead>
            <tr>
                <th>序号</th>
                <th>测点桩号 (幅段)</th>
                <th>车道</th>
                <th>左侧回弹弯沉值 (0.01mm)</th>
                <th>右侧回弹弯沉值 (0.01mm)</th>
                <th>备注</th>
            </tr>
        </thead>
        <tbody>
            <% for(int i=1; i<=5; i++) { %>
            <tr>
                <td><%=i%></td>
                <td><input type="text" name="station_<%=i%>"></td>
                <td><input type="text" name="lane_<%=i%>"></td>
                <td><input type="text" name="leftDeflection_<%=i%>"></td>
                <td><input type="text" name="rightDeflection_<%=i%>"></td>
                <td><input type="text" name="remarks_<%=i%>"></td>
            </tr>
            <% } %>
        </tbody>
    </table>

    <!-- Table 4: Statistics -->
    <table>
        <colgroup>
            <col style="width: 15%;">
            <col style="width: 10%;">
            <col style="width: 15%;">
            <col style="width: 10%;">
            <col style="width: 15%;">
            <col style="width: 10%;">
            <col style="width: 15%;">
            <col style="width: 10%;">
        </colgroup>
        <tr>
            <td class="label">(温度修正后)<br>平均弯沉值<br>(0.01mm)</td>
            <td><input type="text" name="avgDeflection"></td>
            <td class="label">标准差<br>(0.01mm)</td>
            <td><input type="text" name="stdDev"></td>
            <td class="label">弯沉代表值<br>(0.01mm)</td>
            <td><input type="text" name="repDeflection"></td>
            <td class="label">回弹模量<br>E<sub>1</sub>(MPa)</td>
            <td><input type="text" name="resilientModulus"></td>
        </tr>
    </table>

    <!-- Table 5: Conclusion and Notes -->
    <table>
        <colgroup>
            <col style="width: 15%;">
            <col style="width: 85%;">
        </colgroup>
        <tr>
            <td class="label">检测结论</td>
            <td><input type="text" name="testConclusion" style="width: 100%; text-align: left;"></td>
        </tr>
        <tr>
            <td class="label">报告说明</td>
            <td><input type="text" name="reportDesc" style="width: 100%; text-align: left;"></td>
        </tr>
    </table>

    <div class="footer-section">
        <div class="footer-line">
            <div>批准：<input type="text" style="width: 100px; border-bottom: 1px solid black;"></div>
            <div>审核：<input type="text" style="width: 100px; border-bottom: 1px solid black;"></div>
            <div>检测：<input type="text" style="width: 100px; border-bottom: 1px solid black;"></div>
        </div>

        <div class="company-info">
            <div>声明：</div>
            <div>1.对本检测报告的复印件未加盖公司检验检测专用章无效。2.对检测结果如有异议，应在收到报告之日起十五日之内向本公司提出。</div>
            <div style="margin-top: 10px;">公司名称：<input type="text" style="width: 300px; border-bottom: 1px solid black; text-align: left;"></div>
            <div class="footer-line" style="margin-top: 5px;">
                <div>公司地址：<input type="text" style="width: 300px; border-bottom: 1px solid black; text-align: left;"></div>
                <div>电话：<input type="text" style="width: 150px; border-bottom: 1px solid black; text-align: left;"></div>
            </div>
        </div>

        <div class="footer-line" style="font-size: 12px; margin-top: 10px;">
            <div>版次：<input type="text" style="width: 100px; border-bottom: 1px solid black;"></div>
            <div>第 页，共 页</div>
        </div>
    </div>
</body>
</html>