<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>轻型动力触探检测报告</title>
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
            margin-top: 30px;
            margin-bottom: 20px;
            font-size: 16px;
            font-weight: bold;
        }
        .statement {
            font-size: 12px;
            line-height: 1.6;
            margin-top: 10px;
        }
        .page-footer {
            margin-top: 10px;
            text-align: right;
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

    <h2>轻型动力触探检测报告</h2>

    <div class="header-info">
        <span>委托单位：<input type="text" style="width: 200px; border-bottom: 1px solid black; text-align: left;"></span>
        <span>统一编号：<input type="text" style="width: 150px; border-bottom: 1px solid black;"></span>
    </div>

    <table>
        <!-- Row 1 -->
        <tr>
            <td class="label" style="width: 15%;">工程名称</td>
            <td colspan="4"><input type="text" name="projectName"></td>
            <td class="label" style="width: 15%;">委托日期</td>
            <td colspan="4"><input type="text" name="entrustDate"></td>
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
            <td colspan="4"><input type="text" name="soilProperty"></td>
            <td class="label">报告日期</td>
            <td colspan="4"><input type="text" name="reportDate"></td>
        </tr>
        <!-- Row 4 -->
        <tr>
            <td class="label">见证单位</td>
            <td colspan="4"><input type="text" name="witnessUnit"></td>
            <td class="label">见证人</td>
            <td colspan="4"><input type="text" name="witness"></td>
        </tr>
        <!-- Row 5 -->
        <tr>
            <td class="label">设计<br>承载力<br>(kPa)</td>
            <td colspan="2"><input type="text" name="designCapacity"></td>
            <td class="label">锤重量<br>(kg)</td>
            <td><input type="text" name="hammerWeight"></td>
            <td class="label">落距<br>(cm)</td>
            <td><input type="text" name="dropDistance"></td>
            <td class="label">检测类别</td>
            <td colspan="2"><input type="text" name="testCategory"></td>
        </tr>
        <!-- Row 6: Table Header for Data -->
        <tr>
            <td class="label">测点位置</td>
            <td class="label">贯入<br>深度<br>(cm)</td>
            <td class="label">实测<br>锤击数</td>
            <td class="label">平均<br>锤击数<br>N<sub>10</sub></td>
            <td class="label">承载力<br>特征值<br>(kPa)</td>
            <td class="label">测点位置</td>
            <td class="label">贯入<br>深度<br>(cm)</td>
            <td class="label">实测<br>锤击数</td>
            <td class="label">平均<br>锤击数<br>N<sub>10</sub></td>
            <td class="label">承载力<br>特征值<br>(kPa)</td>
        </tr>

        <!-- Data Rows -->
        <% 
        // 左右两栏，每栏4个大测点块，每个测点块包含2个小行（深度记录）
        // 外层循环控制“大行”（测点），共4个（保持总行数8行不变：4*2=8）
        for(int block=0; block<4; block++) { 
            // 内层循环控制“小行”（深度），共2个
            for(int sub=0; sub<2; sub++) {
                // 计算全局行索引，用于唯一标识输入框名称
                int globalRowIndex = block * 2 + sub;
        %>
        <tr>
            <!-- 左栏 -->
            <% if(sub == 0) { %>
                <td rowspan="2"><textarea name="pos_L_<%=block%>" style="height: 100%; width: 100%; border: none; text-align: center; padding-top: 10px;"></textarea></td>
            <% } %>
            
            <td><input type="text" name="depth_L_<%=globalRowIndex%>"></td>
            <td><input type="text" name="actual_L_<%=globalRowIndex%>"></td>
            
            <% if(sub == 0) { %>
                <td rowspan="2"><input type="text" name="avg_L_<%=block%>" style="height: 100%;"></td>
                <td rowspan="2"><input type="text" name="capacity_L_<%=block%>" style="height: 100%;"></td>
            <% } %>

            <!-- 右栏 -->
            <% if(sub == 0) { %>
                <td rowspan="2"><textarea name="pos_R_<%=block%>" style="height: 100%; width: 100%; border: none; text-align: center; padding-top: 10px;"></textarea></td>
            <% } %>
            
            <td><input type="text" name="depth_R_<%=globalRowIndex%>"></td>
            <td><input type="text" name="actual_R_<%=globalRowIndex%>"></td>
            
            <% if(sub == 0) { %>
                <td rowspan="2"><input type="text" name="avg_R_<%=block%>" style="height: 100%;"></td>
                <td rowspan="2"><input type="text" name="capacity_R_<%=block%>" style="height: 100%;"></td>
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
            <td class="label" style="height: 80px;">检测结论</td>
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
        <span>批准：<input type="text" style="width: 100px; border-bottom: 1px solid black;"></span>
        <span>审核：<input type="text" style="width: 100px; border-bottom: 1px solid black;"></span>
        <span>检验：<input type="text" style="width: 100px; border-bottom: 1px solid black;"></span>
    </div>

    <div class="statement">
        声明：<br>
        1. 对本检测报告的复印件未加盖公司检验检测专用章无效。<br>
        2. 对检测结果如有异议，应在收到报告之日起十五日之内向本公司提出。<br>
        公司名称：河北金涛建设工程质量检测有限公司。<br>
        公司地址：石家庄高新区方亿科技工业园A区第2号楼。&nbsp;&nbsp;&nbsp;&nbsp;电话：0311—86107634&nbsp;&nbsp;0311—67300616
    </div>

    <div class="page-footer" style="display: flex; justify-content: space-between; align-items: center;">
        <span>版次：<input type="text" name="version" style="width: 50px; border-bottom: 1px solid black; text-align: center;"></span>
        <span><input type="text" name="year" style="width: 40px; border-bottom: 1px solid black; text-align: center;">年<input type="text" name="month" style="width: 20px; border-bottom: 1px solid black; text-align: center;">月<input type="text" name="day" style="width: 20px; border-bottom: 1px solid black; text-align: center;">日</span>
        <span>第 <input type="text" name="page" style="width: 20px; border-bottom: 1px solid black; text-align: center;"> 页，共 <input type="text" name="totalPages" style="width: 20px; border-bottom: 1px solid black; text-align: center;"> 页</span>
    </div>

<script>
        function generatePdf() {
            const form = document.createElement('form');
            form.method = 'post';
            form.action = '/api/pdf/light_dynamic_penetration/generate';
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
            form.action = '/api/pdf/light_dynamic_penetration/preview';
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
