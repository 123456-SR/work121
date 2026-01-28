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
        .tabs {
            display: flex;
            justify-content: center;
            margin-bottom: 30px;
            gap: 10px;
        }
        .tab-button {
            padding: 10px 30px;
            font-size: 20px;
            cursor: pointer;
            background-color: #e9ecef;
            border: none;
            border-radius: 5px;
            color: #495057;
            transition: all 0.3s;
        }
        .tab-button.active {
            background-color: #007bff;
            color: white;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .section {
            display: none;
            animation: fadeIn 0.3s ease-in-out;
        }
        .section.active {
            display: block;
        }
        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(10px); }
            to { opacity: 1; transform: translateY(0); }
        }
    </style>
</head>
<body>
    <h1>表格管理系统</h1>

    <div class="tabs">
        <button class="tab-button active" onclick="showSection('preliminary')">前置</button>
        <button class="tab-button" onclick="showSection('report')">报告</button>
    </div>

    <div id="preliminary" class="section active">
        <div class="button-container">
            <a href="/entrustment" class="nav-button">检测（前）委托单（带合同）</a>
            <a href="/light_dynamic_penetration_record" class="nav-button">轻型动力触探检测记录表</a>
            <a href="/nuclear_density_record" class="nav-button">原位密度检测记录表（核子法）</a>
            <a href="/sand_replacement_record" class="nav-button">原位密度检测记录表（灌砂法）</a>
            <a href="/water_replacement_record" class="nav-button">相对密度试验记录表（灌水法）</a>
            <a href="/cutting_ring_record" class="nav-button">原位密度检测记录表（环刀法）</a>
            <a href="/rebound_method_record" class="nav-button">回弹法检测混凝土抗压强度记录表</a>
            <a href="/beckman_beam_record" class="nav-button">路基路面回弹弯沉试验检测记录表（贝克曼梁法）</a>
            <a href="/signature" class="nav-button" style="background-color: #28a745;">电子签名</a>
        </div>
    </div>

    <div id="report" class="section">
        <div class="button-container">
            <a href="/density_test_report" class="nav-button">原位密度检测报告</a>
            <a href="/density_test_result" class="nav-button">原位密度检测结果</a>
            <a href="/light_dynamic_penetration" class="nav-button">轻型动力触探检测报告</a>
            <a href="/light_dynamic_penetration_result" class="nav-button">轻型动力触探检测结果</a>
            <a href="/rebound_method_report" class="nav-button">回弹法检测混凝土抗压强度报告</a>
            <a href="/beckman_beam_report" class="nav-button">路基路面回弹弯沉（回弹模量）检测报告</a>
            <a href="/beckman_beam_result" class="nav-button">路基路面回弹弯沉（回弹模量）检测结果</a>
        </div>
    </div>

    <script>
        function showSection(id) {
            // Hide all sections
            document.querySelectorAll('.section').forEach(el => el.classList.remove('active'));
            document.querySelectorAll('.tab-button').forEach(el => el.classList.remove('active'));

            // Show selected section
            document.getElementById(id).classList.add('active');

            // Update tab style
            if (id === 'preliminary') {
                document.querySelector('.tab-button:nth-child(1)').classList.add('active');
            } else {
                document.querySelector('.tab-button:nth-child(2)').classList.add('active');
            }
        }
    </script>
</body>
</html>
