<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>系统主页</title>
    <style>
        body {
            font-family: 'Microsoft YaHei', Arial, sans-serif;
            text-align: center;
            margin-top: 50px;
            background-color: #f4f6f9;
        }
        h1 {
            color: #333;
            margin-bottom: 40px;
        }
        .button-container {
            display: flex;
            flex-wrap: wrap;
            justify-content: center;
            gap: 20px;
            max-width: 800px;
            margin: 0 auto;
        }
        .nav-button {
            padding: 20px 40px;
            font-size: 18px;
            cursor: pointer;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 8px;
            text-decoration: none;
            transition: background-color 0.3s;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        }
        .nav-button:hover {
            background-color: #0056b3;
            transform: translateY(-2px);
        }
    </style>
</head>
<body>
    <h1>表格管理系统</h1>
    <div class="button-container">
        <a href="/entrustment" class="nav-button">检测（前）委托单（带合同）</a>
        <a href="/light_dynamic_penetration" class="nav-button">轻型动力触探检测报告</a>
        <a href="/light_dynamic_penetration_record" class="nav-button">轻型动力触探检测记录表</a>
        <a href="/signature" class="nav-button" style="background-color: #28a745;">电子签名</a>
    </div>
</body>
</html>
