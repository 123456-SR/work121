<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>路基路面回弹弯沉(回弹模量) 检测结果</title>
    <style>
        body {
            font-family: "SimSun", "Songti SC", serif;
            margin: 0;
            padding: 20px;
            background-color: #f0f0f0;
            display: flex;
            justify-content: center;
        }
        .page-container {
            width: 210mm; /* A4 width */
            min-height: 297mm; /* A4 height */
            background-color: white;
            padding: 20mm;
            box-sizing: border-box;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
            position: relative;
            display: flex;
            flex-direction: column;
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
            margin-top: auto;
            font-size: 14px;
            width: 100%;
        }
        
        @media print {
            body {
                background-color: white;
                padding: 0;
            }
            .page-container {
                box-shadow: none;
                margin: 0;
                width: 100%;
                height: auto;
                padding: 0; /* Let print margins handle it or keep minimal */
            }
            .no-print {
                display: none;
            }
        }
    </style>
</head>
<body>
    <div class="page-container">
        <h2>路基路面回弹弯沉(回弹模量) 检测结果</h2>
        
        <div class="header-info">
            <span>委托单位：<input type="text" style="width: 200px; text-align: left; border-bottom: 1px solid #ccc;" placeholder=""></span>
            <span>统一编号：<input type="text" style="width: 150px; text-align: left; border-bottom: 1px solid #ccc;" placeholder=""></span>
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

        <div class="footer-info">
            <span>版次：<input type="text" style="width: 100px; text-align: left;" value=""></span>
            <span>第 <input type="text" style="width: 30px;"> 页，共 <input type="text" style="width: 30px;"> 页</span>
        </div>
    </div>
</body>
</html>