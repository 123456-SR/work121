<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>轻型动力触探检测记录表</title>
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
        <button onclick="window.print()" style="float: right;">打印此单</button>
    </div>

    <h2>轻型动力触探检测记录表</h2>

    <div class="header-info">
        <span>委托单位：<input type="text" style="width: 250px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>统一编号：<input type="text" style="width: 150px; border-bottom: 1px solid black;"></span>
    </div>

    <table>
        <!-- Row 1 -->
        <tr>
            <td class="label">工程名称</td>
            <td colspan="4"><input type="text" name="projectName"></td>
            <td class="label">委托日期</td>
            <td colspan="4"><input type="text" name="commissionDate"></td>
        </tr>
        <!-- Row 2 -->
        <tr>
            <td class="label">施工部位</td>
            <td colspan="4"><input type="text" name="constructionPart"></td>
            <td class="label">检测日期</td>
            <td colspan="4"><input type="text" name="testDate"></td>
        </tr>
        <!-- Row 3 -->
        <tr>
            <td class="label">岩土性状</td>
            <td colspan="4"><input type="text" name="soilProperties" value="水泥土搅拌桩"></td>
            <td class="label">检测类别</td>
            <td colspan="4"><input type="text" name="testCategory"></td>
        </tr>
        <!-- Row 4: Params -->
        <tr>
            <td colspan="3" class="label">设计承载力<br>(kPa)</td>
            <td colspan="3" class="label">锤重量<br>(kg)</td>
            <td colspan="4" class="label">落距<br>(cm)</td>
        </tr>
        <tr>
            <td colspan="3"><input type="text" name="designCapacity"></td>
            <td colspan="3"><input type="text" name="hammerWeight"></td>
            <td colspan="4"><input type="text" name="dropDistance"></td>
        </tr>

        <!-- Row 5: Table Header for Data -->
        <tr>
            <td class="label" style="width: 10%;">测点<br>位置</td>
            <td class="label" style="width: 10%;">贯入<br>深度<br>(cm)</td>
            <td class="label" style="width: 10%;">实测<br>锤击数</td>
            <td class="label" style="width: 10%;">平均<br>锤击数<br>N<sub>10</sub></td>
            <td class="label" style="width: 10%;">承载力<br>特征值<br>(kPa)</td>
            <td class="label" style="width: 10%;">测点<br>位置</td>
            <td class="label" style="width: 10%;">贯入<br>深度<br>(cm)</td>
            <td class="label" style="width: 10%;">实测<br>锤击数</td>
            <td class="label" style="width: 10%;">平均<br>锤击数<br>N<sub>10</sub></td>
            <td class="label" style="width: 10%;">承载力<br>特征值<br>(kPa)</td>
        </tr>

        <!-- Data Rows -->
        <% 
        // 左右两栏，每栏2个大测点块，每个测点块包含5个小行（深度记录）
        // 总行数：2 * 5 = 10行
        for(int block=0; block<2; block++) { 
            for(int sub=0; sub<5; sub++) {
                int globalRowIndex = block * 5 + sub;
        %>
        <tr>
            <!-- 左栏 -->
            <% if(sub == 0) { %>
                <td rowspan="5"><textarea name="pos_L_<%=block%>" style="height: 100%; width: 100%; border: none; text-align: center; padding-top: 10px;"></textarea></td>
            <% } %>
            
            <td><input type="text" name="depth_L_<%=globalRowIndex%>"></td>
            <td><input type="text" name="actual_L_<%=globalRowIndex%>"></td>
            
            <% if(sub == 0) { %>
                <td rowspan="5"><input type="text" name="avg_L_<%=block%>" style="height: 100%;"></td>
                <td rowspan="5"><input type="text" name="capacity_L_<%=block%>" style="height: 100%;"></td>
            <% } %>

            <!-- 右栏 -->
            <% if(sub == 0) { %>
                <td rowspan="5"><textarea name="pos_R_<%=block%>" style="height: 100%; width: 100%; border: none; text-align: center; padding-top: 10px;"></textarea></td>
            <% } %>
            
            <td><input type="text" name="depth_R_<%=globalRowIndex%>"></td>
            <td><input type="text" name="actual_R_<%=globalRowIndex%>"></td>
            
            <% if(sub == 0) { %>
                <td rowspan="5"><input type="text" name="avg_R_<%=block%>" style="height: 100%;"></td>
                <td rowspan="5"><input type="text" name="capacity_R_<%=block%>" style="height: 100%;"></td>
            <% } %>
        </tr>
        <% 
            }
        } 
        %>

        <!-- Row: 检测依据 -->
        <tr>
            <td class="label">检测依据</td>
            <td colspan="9" class="left-align"><input type="text" name="testBasis"></td>
        </tr>
        <!-- Row: 仪器设备 -->
        <tr>
            <td class="label">仪器设备</td>
            <td colspan="9" class="left-align"><input type="text" name="equipment"></td>
        </tr>
        <!-- Row: 检测结论 -->
        <tr>
            <td class="label" style="height: 60px;">检测结论</td>
            <td colspan="9" class="left-align" style="vertical-align: top;">
                <textarea name="conclusion" rows="3" style="width: 100%; height: 100%;"></textarea>
            </td>
        </tr>
        <!-- Row: 备注 -->
        <tr>
            <td class="label">备注</td>
            <td colspan="9" class="left-align"><input type="text" name="remarks"></td>
        </tr>
    </table>

    <div class="footer-info">
        <span>审核：<input type="text" style="width: 100px; border-bottom: 1px solid black;"></span>
        <span>检验：<input type="text" style="width: 100px; border-bottom: 1px solid black;"></span>
    </div>

    <div class="page-footer" style="display: flex; justify-content: space-between; align-items: center;">
        <span>版次：<input type="text" style="width: 50px; border-bottom: 1px solid black; text-align: center;"></span>
        <span><input type="text" style="width: 40px; border-bottom: 1px solid black; text-align: center;">年<input type="text" style="width: 20px; border-bottom: 1px solid black; text-align: center;">月<input type="text" style="width: 20px; border-bottom: 1px solid black; text-align: center;">日</span>
        <span>第 <input type="text" style="width: 20px; border-bottom: 1px solid black; text-align: center;"> 页，共 <input type="text" style="width: 20px; border-bottom: 1px solid black; text-align: center;"> 页</span>
    </div>

</body>
</html>