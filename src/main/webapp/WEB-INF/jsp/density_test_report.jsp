<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>原位密度检测报告</title>
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
            justify-content: space-around;
            margin-top: 20px;
            margin-bottom: 10px;
            font-size: 16px;
            font-weight: bold;
        }
        .statement {
            font-size: 12px;
            line-height: 1.6;
            margin-top: 5px;
            border-top: 2px solid black; /* Separate statement from footer info */
            padding-top: 5px;
        }
        .company-info {
             display: flex;
             justify-content: space-between;
             font-size: 14px;
             margin-top: 5px;
             border-bottom: 2px solid black; /* Bottom line */
             padding-bottom: 5px;
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
        <button onclick="window.print()" style="float: right;">打印此单</button>
    </div>

    <h2>原位密度检测报告</h2>

    <div class="header-info">
        <span>委托单位：<input type="text" style="width: 250px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>统一编号：<input type="text" style="width: 150px; border-bottom: 1px solid black;"></span>
    </div>

    <table>
        <!-- Header Rows -->
        <tr>
            <td class="label">工程名称</td>
            <td colspan="6"><input type="text" name="projectName"></td>
            <td class="label">委托日期</td>
            <td colspan="3"><input type="text" name="commissionDate"></td>
        </tr>
        <tr>
            <td class="label">施工部位</td>
            <td colspan="6"><input type="text" name="constructionPart"></td>
            <td class="label">检测日期</td>
            <td colspan="3"><input type="text" name="testDate"></td>
        </tr>
        <tr>
            <td class="label">样品名称<br>及状态</td>
            <td colspan="6"><input type="text" name="sampleNameStatus"></td>
            <td class="label">报告日期</td>
            <td colspan="3"><input type="text" name="reportDate"></td>
        </tr>
        <tr>
            <td class="label">仪器设备</td>
            <td colspan="6"><input type="text" name="equipment"></td>
            <td class="label">检测方法</td>
            <td colspan="3"><input type="text" name="testMethod"></td>
        </tr>
        <tr>
            <td class="label">检测依据</td>
            <td colspan="6"><input type="text" name="testBasis"></td>
            <td class="label">检测类别</td>
            <td colspan="3"><input type="text" name="testCategory"></td>
        </tr>
        <tr>
            <td class="label">最大干密度<br>(g/cm³)</td>
            <td colspan="3"><input type="text" name="maxDryDensity"></td>
            <td class="label">最优含水率 %</td>
            <td colspan="3"><input type="text" name="optimumMoisture"></td>
            <td class="label">最小干密度<br>(g/cm³)</td>
            <td colspan="2"><input type="text" name="minDryDensity"></td>
        </tr>
        <tr>
            <td class="label">设计指标</td>
            <td colspan="10" class="left-align"><input type="text" name="designIndex"></td>
        </tr>
        <tr>
            <td class="label">检测结果</td>
            <td colspan="10" class="left-align"><input type="text" name="testResult"></td>
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
            <td class="label" style="width: 10%;"> </td> <!-- Empty header for alignment if needed, or maybe merge -->
        </tr>

        <!-- Data Rows (8 rows as per visual estimation) -->
        <% 
        for(int i=0; i<8; i++) { 
        %>
        <tr>
            <td rowspan="2"><input type="text" name="sampleId_<%=i%>"></td>
            <td rowspan="2"><input type="text" name="location_<%=i%>"></td>
            <td rowspan="2"><input type="text" name="date_<%=i%>"></td>
            <td><input type="text" name="wetDensity_<%=i%>"></td>
            <td><input type="text" name="dryDensity_<%=i%>"></td>
            <td><input type="text" name="moisture_<%=i%>"></td>
            <td rowspan="2"><input type="text" name="compaction_<%=i%>"></td>
            <td rowspan="2"></td> <!-- Empty cell to match column count of 11 -->
        </tr>
        <tr>
            <td><input type="text" name="wetDensity2_<%=i%>"></td>
            <td><input type="text" name="dryDensity2_<%=i%>"></td>
            <td><input type="text" name="moisture2_<%=i%>"></td>
        </tr>
        <% 
        } 
        %>

        <!-- Remarks Section -->
        <tr>
            <td class="label">备 注</td>
            <td colspan="10" class="left-align" style="height: 100px; vertical-align: top;">
                <textarea name="remarks" style="width: 100%; height: 100%;">附原位密度检测结果。
见证人：
见证单位：</textarea>
            </td>
        </tr>
    </table>

    <div class="footer-info">
        <span>批准：<input type="text" style="width: 100px; border-bottom: 1px solid black;"></span>
        <span>审核：<input type="text" style="width: 100px; border-bottom: 1px solid black;"></span>
        <span>检测：<input type="text" style="width: 100px; border-bottom: 1px solid black;"></span>
    </div>

    <div class="statement">
        声明：<br>
        1. 对本检测报告的复印件未加盖公司检验检测专用章无效。 2. 对检验结果如有异议，应在收到报告之日起十五日之内向本公司提出。
    </div>

    <div class="company-info">
        <span>公司名称：<input type="text" style="width: 200px; border: none;"></span>
        <span>电话：<input type="text" style="width: 150px; border: none;"></span>
    </div>
     <div class="company-info" style="border-bottom: none; margin-top:0;">
        <span>公司地址：<input type="text" style="width: 400px; border: none;"></span>
    </div>

    <div class="page-footer">
        <span>版次：<input type="text" style="width: 50px; border-bottom: 1px solid black; text-align: center;"></span>
        <span><input type="text" style="width: 100px; border-bottom: 1px solid black; text-align: center;" placeholder="YYYY-MM-DD" value="2018-12-15"></span>
        <span>第 <input type="text" style="width: 20px; border-bottom: 1px solid black; text-align: center;"> 页，共 <input type="text" style="width: 20px; border-bottom: 1px solid black; text-align: center;"> 页</span>
    </div>

</body>
</html>