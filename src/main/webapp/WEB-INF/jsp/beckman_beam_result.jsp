<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>路基路面回弹弯沉(回弹模量) 检测结果</title>
    <style>
        body {
            font-family: "SimSun", "Songti SC", serif;
            width: 210mm;
            margin: 0 auto;
            padding: 20px;
            background-color: white;
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
            font-size: 14px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 10px;
            table-layout: fixed; /* Fixed layout for consistent column widths */
        }
        th, td {
            border: 1px solid black;
            padding: 5px;
            text-align: center;
            font-size: 14px;
            height: 30px; /* Consistent row height */
            overflow: hidden;
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
        .footer-info {
            display: flex;
            justify-content: space-between;
            margin-top: 20px;
            font-size: 14px;
            width: 100%;
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
            @page {
                size: A4 portrait;
                margin: 1cm;
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
        <h2>路基路面回弹弯沉(回弹模量) 检测结果</h2>
        
        <div class="header-info">
            <span>委托单位：<input type="text" name="entrustingUnit" style="width: 200px; text-align: left; border-bottom: 1px solid #ccc;" placeholder=""></span>
            <span>统一编号：<input type="text" name="unifiedNumber" style="width: 150px; text-align: left; border-bottom: 1px solid #ccc;" placeholder=""></span>
        </div>

        <table>
            <colgroup>
                <col style="width: 8%;"> <!-- 序号 -->
                <col style="width: 22%;"> <!-- 测点桩号（幅段） -->
                <col style="width: 15%;"> <!-- 车道 -->
                <col style="width: 20%;"> <!-- 左侧回弹弯沉值 -->
                <col style="width: 20%;"> <!-- 右侧回弹弯沉值 -->
                <col style="width: 15%;"> <!-- 备注 -->
            </colgroup>
            <thead>
                <tr>
                    <th>序号</th>
                    <th>测点桩号（幅段）</th>
                    <th>车道</th>
                    <th>左侧回弹弯沉值<br>(0.01mm)</th>
                    <th>右侧回弹弯沉值<br>(0.01mm)</th>
                    <th>备注</th>
                </tr>
            </thead>
            <tbody>
                <% for(int i=1; i<=25; i++) { %>
                <tr>
                    <td><input type="text" name="seq_<%= i %>"></td>
                    <td><input type="text" name="station_<%= i %>"></td>
                    <td><input type="text" name="lane_<%= i %>"></td>
                    <td><input type="text" name="left_val_<%= i %>"></td>
                    <td><input type="text" name="right_val_<%= i %>"></td>
                    <td><input type="text" name="remark_<%= i %>"></td>
                </tr>
                <% } %>
            </tbody>
        </table>


    </form>

    <script>
        function generatePdf() {
            var form = document.getElementById('pdfForm');
            form.action = '/api/pdf/beckman_beam_result/generate';
            form.target = '_blank'; // Open in new tab/download
            form.submit();
        }

        function previewPdf() {
            var form = document.getElementById('pdfForm');
            form.action = '/api/pdf/beckman_beam_result/preview';
            form.target = '_blank';
            form.submit();
        }
    </script>
</body>
</html>