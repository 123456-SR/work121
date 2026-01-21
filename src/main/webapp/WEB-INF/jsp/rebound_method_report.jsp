<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>回弹法检测混凝土抗压强度报告</title>
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
            white-space: nowrap;
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
        .disclaimer {
            margin-top: 20px;
            font-size: 12px;
            line-height: 1.5;
        }
        .company-info {
            margin-top: 10px;
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
        <button onclick="window.print()" style="float: right;">打印此单</button>
    </div>

    <h2>回弹法检测混凝土抗压强度报告</h2>

    <div class="header-top">
        <span>委托单位：<input type="text" style="width: 200px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>统一编号：<input type="text" style="width: 150px; border-bottom: 1px solid black;"></span>
    </div>
    <div class="header-top">
        <span>样品编号：<input type="text" style="width: 200px; border-bottom: 1px solid black; text-align: left;"></span>
    </div>

    <!-- Table with 12-column grid -->
    <table>
        <colgroup>
            <% for(int i=0; i<12; i++) { %>
            <col style="width: 8.33%;">
            <% } %>
        </colgroup>

        <!-- Row 1 -->
        <tr>
            <td colspan="2" class="label">工程名称</td>
            <td colspan="6"><input type="text" name="projectName"></td>
            <td colspan="2" class="label">委托日期</td>
            <td colspan="2"><input type="text" name="commissionDate"></td>
        </tr>

        <!-- Row 2 -->
        <tr>
            <td colspan="2" class="label">结构部位</td>
            <td colspan="6"><input type="text" name="structurePart"></td>
            <td colspan="2" class="label">检测日期</td>
            <td colspan="2"><input type="text" name="testDate"></td>
        </tr>

        <!-- Row 3 -->
        <tr>
            <td colspan="2" class="label">样品状态</td>
            <td colspan="2"><input type="text" name="sampleStatus"></td>
            <td colspan="2" class="label">测试角度</td>
            <td colspan="2"><input type="text" name="testAngle"></td>
            <td colspan="2" class="label">报告日期</td>
            <td colspan="2"><input type="text" name="reportDate"></td>
        </tr>

        <!-- Row 4 -->
        <tr>
            <td colspan="2" class="label">见证单位</td>
            <td colspan="6"><input type="text" name="witnessUnit"></td>
            <td colspan="2" class="label">见证人</td>
            <td colspan="2"><input type="text" name="witnessPerson"></td>
        </tr>

        <!-- Row 5 -->
        <tr>
            <td colspan="2" class="label">设计指标</td>
            <td colspan="2"><input type="text" name="designIndex"></td>
            <td colspan="2" class="label">检测类别</td>
            <td colspan="2"><input type="text" name="testCategory"></td>
            <td colspan="2" class="label">浇筑日期</td>
            <td colspan="2"><input type="text" name="pourDate"></td>
        </tr>

        <!-- Row 6 -->
        <tr>
            <td colspan="2" class="label">碳化深度 mm</td>
            <td colspan="2"><input type="text" name="carbonDepth"></td>
            <td colspan="4" class="label">构件厚度或骨料最大粒径</td>
            <td colspan="4"><input type="text" name="aggregateSize"></td>
        </tr>

        <!-- Row 7: Zone No (10 zones) -->
        <tr>
            <td colspan="2" class="label">测区号</td>
            <% for(int i=1; i<=10; i++) { %>
            <td colspan="1"><%=i%></td>
            <% } %>
        </tr>

        <!-- Row 8: Carbonation Corrected Strength -->
        <tr>
            <td colspan="2" class="label">碳化修正强度<br>MPa</td>
            <% for(int i=1; i<=10; i++) { %>
            <td colspan="1"><input type="text" name="correctedStrength_<%=i%>"></td>
            <% } %>
        </tr>

        <!-- Row 9: Strength Estimation -->
        <tr>
            <td colspan="2" class="label">构件强度推定<br>值 MPa</td>
            <td colspan="2"><input type="text" name="compEstimatedStrength"></td>
            <td colspan="2" class="label">标准差 MPa</td>
            <td colspan="2"><input type="text" name="stdDev"></td>
            <td colspan="2" class="label">变异系数%</td>
            <td colspan="2"><input type="text" name="coefVariation"></td>
        </tr>

        <!-- Row 10: Equipment -->
        <tr>
            <td colspan="2" class="label">仪器设备</td>
            <td colspan="10" class="left-align"><input type="text" name="equipment" style="text-align: left;"></td>
        </tr>

        <!-- Row 11: Standard -->
        <tr>
            <td colspan="2" class="label">依据标准</td>
            <td colspan="10" class="left-align"><input type="text" name="standard" style="text-align: left;"></td>
        </tr>

        <!-- Row 12: Conclusion -->
        <tr>
            <td colspan="2" class="label">结论</td>
            <td colspan="10" class="left-align" style="height: 60px;">
                <textarea name="conclusion" style="width: 100%; height: 100%;"></textarea>
            </td>
        </tr>

        <!-- Row 13: Diagram -->
        <tr>
            <td colspan="12" class="left-align" style="height: 150px; vertical-align: top;">
                <span class="label">测区示意图：</span>
                <!-- Placeholder for diagram -->
            </td>
        </tr>

        <!-- Row 14: Remarks -->
        <tr>
            <td colspan="1" class="label">备注：</td>
            <td colspan="11" class="left-align" style="height: 40px;">
                <textarea name="remarks" style="width: 100%; height: 100%;"></textarea>
            </td>
        </tr>
    </table>

    <div class="footer-info">
        <span>批准：<input type="text" style="width: 100px; border-bottom: 1px solid black;"></span>
        <span>审核：<input type="text" style="width: 100px; border-bottom: 1px solid black;"></span>
        <span>检验：<input type="text" style="width: 100px; border-bottom: 1px solid black;"></span>
    </div>

    <div class="disclaimer">
        声明：<br>
        1. 对本检验报告的复印件未加盖公司检验检测专用章无效。 2. 对检验结果如有异议，应在收到报告之日起十五日之内向本公司提出。
    </div>

    <div class="company-info">
        <div>公司名称：<input type="text" style="width: 70%; border-bottom: 1px solid black; text-align: left;"></div>
        <div style="display: flex; justify-content: space-between; margin-top: 5px;">
            <span>公司地址：<input type="text" style="width: 300px; border-bottom: 1px solid black; text-align: left;"></span>
            <span>电话：<input type="text" style="width: 150px; border-bottom: 1px solid black;"></span>
        </div>
    </div>

    <div class="page-footer">
        <span>版次：<input type="text" style="width: 50px; border-bottom: 1px solid black; text-align: center;"></span>
        <span>第 <input type="text" style="width: 20px; border-bottom: 1px solid black; text-align: center;"> 页，共 <input type="text" style="width: 20px; border-bottom: 1px solid black; text-align: center;"> 页</span>
    </div>

</body>
</html>