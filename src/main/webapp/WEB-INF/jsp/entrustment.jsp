<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>检测(验)委托单(代合同)</title>
    <style>
        body {
            font-family: 'SimSun', 'Songti SC', serif; /* 宋体更像打印单据 */
            width: 210mm; /* A4 宽度 */
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
            padding: 8px 5px;
            vertical-align: middle;
        }
        .label {
            font-weight: bold;
            white-space: nowrap;
            width: 100px; /* 调整标签列宽 */
        }
        input[type="text"], textarea {
            width: 98%;
            border: none;
            outline: none;
            font-family: inherit;
            font-size: inherit;
            background-color: transparent;
        }
        input[type="text"]:focus, textarea:focus {
            background-color: #f0f8ff;
        }
        .checkbox-group label {
            margin-right: 15px;
            cursor: pointer;
        }
        .footer-info {
            display: flex;
            justify-content: space-between;
            margin-top: 20px;
            margin-bottom: 20px;
            font-size: 16px;
        }
        .page-footer {
            margin-top: 10px;
            text-align: right;
            font-size: 14px;
            margin-bottom: 50px;
        }
        .notes {
            font-size: 12px;
            line-height: 1.5;
        }
        .center-text {
            text-align: center;
        }
        textarea {
            resize: none;
            overflow: hidden;
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

    <form id="entrustmentForm" method="post">
    <h2>检测 (验) 委托单 (代合同)</h2>

    <div class="header-info">
        <span>统一编号：<input type="text" name="unifiedNumber" style="width: 150px; border-bottom: 1px solid black;"></span>
        <span>样品编号：<input type="text" name="sampleNumber" style="width: 150px; border-bottom: 1px solid black;"></span>
    </div>

    <table>
        <!-- Row 1 -->
        <tr>
            <td class="label">委托单位：</td>
            <td><input type="text" name="clientUnit"></td>
            <td class="label">委托日期：</td>
            <td><input type="text" name="clientDate"></td>
        </tr>
        <!-- Row 2 -->
        <tr>
            <td class="label">施工单位：</td>
            <td><input type="text" name="constructionUnit"></td>
            <td class="label">建设单位：</td>
            <td><input type="text" name="buildingUnit"></td>
        </tr>
        <!-- Row 3 -->
        <tr>
            <td class="label">工程名称：</td>
            <td colspan="2"><input type="text" name="projectName"></td>
            <td style="width: 150px;">
                <span class="label">施工 (使用) 部位：</span><br>
                <input type="text" name="constructionPart">
            </td>
        </tr>
        <!-- Row 4 -->
        <tr>
            <td colspan="4" style="padding: 0;">
                <table style="width: 100%; border: none; margin: -1px; border-collapse: collapse;">
                    <tr>
                        <td class="label" style="width: 15%; border: none; border-right: 1px solid black; padding: 5px;">样品名称：</td>
                        <td style="width: 18%; border: none; border-right: 1px solid black; padding: 5px;"><input type="text" name="sampleName"></td>
                        <td class="label" style="width: 15%; border: none; border-right: 1px solid black; padding: 5px;">规格/型号：</td>
                        <td style="width: 18%; border: none; border-right: 1px solid black; padding: 5px;"><input type="text" name="spec"></td>
                        <td class="label" style="width: 15%; border: none; border-right: 1px solid black; padding: 5px;">生产厂家<br>或产地</td>
                        <td style="width: 19%; border: none; padding: 5px;"><input type="text" name="manufacturer"></td>
                    </tr>
                </table>
            </td>
        </tr>
        <!-- Row 5 -->
        <tr>
            <td colspan="4" style="padding: 0;">
                <table style="width: 100%; border: none; margin: -1px;">
                    <tr>
                        <td style="border: none; border-right: 1px solid black; width: 25%;">
                            <div class="center-text label">样品<br>数量：</div>
                            <input type="text" name="sampleQuantity" class="center-text">
                        </td>
                        <td style="border: none; border-right: 1px solid black; width: 25%;">
                            <div class="center-text label">代表<br>批量：</div>
                            <input type="text" name="representativeBatch" class="center-text">
                        </td>
                        <td style="border: none; border-right: 1px solid black; width: 25%;">
                            <div class="center-text label">批号：</div>
                            <input type="text" name="batchNumber" class="center-text">
                        </td>
                        <td style="border: none; width: 25%;">
                            <div class="center-text label">检测(验)<br>类别：</div>
                            <input type="text" name="testCategory" class="center-text">
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <!-- Row 6: 样品历史及概况 & 样品状态 -->
        <tr>
            <td colspan="2" style="height: 100px; vertical-align: top;">
                <div class="label">样品历史及概况：</div>
                <textarea name="sampleHistory" rows="4" style="width: 100%; height: 80px;"></textarea>
            </td>
            <td colspan="2" style="height: 100px; vertical-align: top;">
                <div class="label">样品状态：</div>
                <textarea name="sampleStatus" rows="4" style="width: 100%; height: 80px;"></textarea>
            </td>
        </tr>
        <!-- Row 7: 检测项目及依据 -->
        <tr>
            <td colspan="4" style="height: 60px; vertical-align: top;">
                <div class="label">检测(验)项目及依据：</div>
                <textarea name="testItems" rows="2" style="width: 100%; height: 40px;"></textarea>
            </td>
        </tr>
        <!-- Row 8: 地址电话 -->
        <tr>
            <td class="label">委托单位地址<br>及电话(传真)：</td>
            <td colspan="3"><input type="text" name="clientAddressPhone"></td>
        </tr>
        <!-- Row 9: 报告发送 & 样品处置 -->
        <tr>
            <td class="label">报告发送</td>
            <td>
                <div class="checkbox-group">
                    <label><input type="checkbox" name="reportSend" value="1">1.自取</label>
                    <label><input type="checkbox" name="reportSend" value="2">2.邮寄</label>
                    <label><input type="checkbox" name="reportSend" value="3">3.其它</label>
                </div>
            </td>
            <td class="label">样品处置</td>
            <td>
                <div class="checkbox-group">
                    <label><input type="checkbox" name="sampleDisposal" value="1">1.取回</label>
                    <label><input type="checkbox" name="sampleDisposal" value="2">2.不取回</label>
                </div>
            </td>
        </tr>
        <!-- Row 10: 见证人 -->
        <tr>
            <td class="label">见证人</td>
            <td><input type="text" name="witness"></td>
            <td class="label">见证单位</td>
            <td><input type="text" name="witnessUnit"></td>
        </tr>
        <!-- Row 11: 交付日期 & 费用 -->
        <tr>
            <td class="label">检测报告<br>交付日期</td>
            <td>
                <div class="checkbox-group">
                    <div style="margin-bottom: 5px;">
                        <label><input type="radio" name="deliveryMode" value="1">可以为:</label>
                        <input type="text" name="deliveryDate1_y" style="width: 40px; border-bottom: 1px solid black;">年
                        <input type="text" name="deliveryDate1_m" style="width: 25px; border-bottom: 1px solid black;">月
                        <input type="text" name="deliveryDate1_d" style="width: 25px; border-bottom: 1px solid black;">日
                    </div>
                    <div style="margin-bottom: 5px;">
                        <label><input type="radio" name="deliveryMode" value="2">严格限定为:</label>
                        <input type="text" name="deliveryDate2_y" style="width: 40px; border-bottom: 1px solid black;">年
                        <input type="text" name="deliveryDate2_m" style="width: 25px; border-bottom: 1px solid black;">月
                        <input type="text" name="deliveryDate2_d" style="width: 25px; border-bottom: 1px solid black;">日
                    </div>
                    <div>
                        <label><input type="radio" name="deliveryMode" value="3" checked>不要求</label>
                    </div>
                </div>
            </td>
            <td class="label">应缴检测(验)费(元)</td>
            <td><input type="text" name="fee"></td>
        </tr>
        <!-- Row 12: 备注 -->
        <tr>
            <td class="label">备注</td>
            <td colspan="3"><input type="text" name="remarks"></td>
        </tr>
        <!-- Row 13: 说明 -->
        <tr>
            <td class="label" style="vertical-align: top;">说明：</td>
            <td colspan="3" class="notes">
                1、委托人(送样人)和见证人应对样品的代表性负责。<br>
                2、检测(验)单位对检测结果负责技术责任。<br>
                3、本委托单一式三份，委托方一份，检测方两份，自送样人收样人签字起生效。<br>
                4、检测(验)委托单由委托方技术人员或送样人填写，要求内容齐全清晰。<br>
                5、见证检验时，本委托单无见证人签字无效。<br>
                6、本单如有变动，双方应及时沟通，凭此委托单领取报告。<br>
                7、客户可在报告发出之日起30日内取回样品，不合格样品一律不予返还。
            </td>
        </tr>
    </table>

    <div class="footer-info">
        <span>委托(送样)人：<input type="text" style="width: 150px; border-bottom: 1px solid black;"></span>
        <span>承接(收样)人：<input type="text" style="width: 150px; border-bottom: 1px solid black;"></span>
    </div>

    </form>



    <script>
        function generatePdf() {
            var form = document.getElementById('entrustmentForm');
            form.action = '/api/pdf/generate';
            form.target = '_blank';
            form.submit();
        }
        
        function previewPdf() {
            var form = document.getElementById('entrustmentForm');
            form.action = '/api/pdf/preview';
            form.target = '_blank';
            form.submit();
        }
    </script>

</body>
</html>
