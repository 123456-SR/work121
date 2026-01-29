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
    <script src="https://cdn.jsdelivr.net/npm/vue@2.6.14/dist/vue.js"></script>
</head>
<body>

    <div id="app">
    <div class="no-print" style="margin-bottom: 20px;">
        <a href="/" style="text-decoration: none; color: blue;">&lt; 返回主页</a>
        <button onclick="window.print()" style="float: right; margin-left: 10px;">打印此单</button>
        <button onclick="generatePdf()" style="float: right; margin-left: 10px; background-color: #28a745; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;">下载PDF</button>
        <button onclick="previewPdf()" style="float: right; margin-left: 10px; background-color: #17a2b8; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;">预览PDF</button>
    </div>

    <form id="pdfForm" method="post">
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
        <!-- Row 6: 行数控制 -->
        <tr>
            <td class="label">总行数</td>
            <td colspan="9" class="left-align">
                <input type="text" v-model.number="rowCount" style="width: 80px; text-align: center;">
                <input type="hidden" name="rowCount" :value="rowCount">
            </td>
        </tr>
        <!-- Row 7: 数据表头（与后端生成方法匹配） -->
        <tr>
            <td class="label">点号</td>
            <td class="label">深度<br>(m)</td>
            <td class="label">0.3m<br>锤击数</td>
            <td class="label">0.6m<br>锤击数</td>
            <td class="label">0.9m<br>锤击数</td>
            <td class="label">1.2m<br>锤击数</td>
            <td class="label">1.5m<br>锤击数</td>
            <td class="label">1.8m<br>锤击数</td>
            <td class="label">2.1m<br>锤击数</td>
            <td class="label">2.4m<br>锤击数</td>
        </tr>

        <!-- Data Rows（Vue渲染） -->
        <tbody>
            <tr v-for="i in rowCount">
                <td><input type="text" :name="'pointNumber_' + (i-1)"></td>
                <td><input type="text" :name="'depth_' + (i-1)"></td>
                <td><input type="text" :name="'hammerCount03_' + (i-1)"></td>
                <td><input type="text" :name="'hammerCount06_' + (i-1)"></td>
                <td><input type="text" :name="'hammerCount09_' + (i-1)"></td>
                <td><input type="text" :name="'hammerCount12_' + (i-1)"></td>
                <td><input type="text" :name="'hammerCount15_' + (i-1)"></td>
                <td><input type="text" :name="'hammerCount18_' + (i-1)"></td>
                <td><input type="text" :name="'hammerCount21_' + (i-1)"></td>
                <td><input type="text" :name="'hammerCount24_' + (i-1)"></td>
            </tr>
        </tbody>

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
        <span>批准：<input type="text" name="approval" style="width: 100px; border-bottom: 1px solid black;"></span>
        <span>审核：<input type="text" name="reviewer" style="width: 100px; border-bottom: 1px solid black;"></span>
        <span>检验：<input type="text" name="tester" style="width: 100px; border-bottom: 1px solid black;"></span>
    </div>

    <div class="statement" style="font-size: 12px; margin-top: 10px;">
        声明：<br>
        1. 对本检测报告的复印件未加盖公司检验检测专用章无效。<br>
        2. 对检测结果如有异议，应在收到报告之日起十五日之内向本公司提出。
    </div>

    <div class="company-info" style="display: block; margin-top: 5px; font-size: 14px; font-weight: bold;">
        <div>公司名称：<input type="text" name="companyName" style="width: 70%; border: none; text-align: left;" value="河北金涛建设工程质量检测有限公司"></div>
        <div style="display: flex; justify-content: space-between; margin-top: 5px;">
            <span>公司地址：<input type="text" name="companyAddress" style="width: 300px; border: none; text-align: left;" value="石家庄高新区方亿科技工业园A区第2号楼。"></span>
            <span>电话：<input type="text" name="companyPhone" style="width: 200px; border: none;" value="0311—86107634  0311—67300616"></span>
        </div>
    </div>


    </form>

    </div>
<script>
    new Vue({
        el: '#app',
        data: {
            rowCount: 10
        }
    });
    function generatePdf() {
        var form = document.getElementById('pdfForm');
        form.action = '/api/pdf/light_dynamic_penetration/generate';
        form.target = '_blank';
        form.submit();
    }

    function previewPdf() {
        var form = document.getElementById('pdfForm');
        form.action = '/api/pdf/light_dynamic_penetration/preview';
        form.target = '_blank';
        form.submit();
    }
</script>
</body>
</html>
