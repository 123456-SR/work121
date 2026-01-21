<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>电子签名</title>
    <style>
        body {
            font-family: 'Microsoft YaHei', Arial, sans-serif;
            text-align: center;
            margin: 20px;
            background-color: #f4f6f9;
        }
        h1 {
            color: #333;
            margin-bottom: 30px;
        }
        .container {
            max-width: 900px;
            margin: 0 auto;
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        .canvas-container {
            border: 2px solid #007bff;
            border-radius: 8px;
            margin-bottom: 20px;
            background: white;
            display: inline-block;
        }
        #signatureCanvas {
            border: 1px solid #ddd;
            border-radius: 5px;
            cursor: crosshair;
        }
        .button-group {
            display: flex;
            justify-content: center;
            gap: 15px;
            margin-bottom: 20px;
            flex-wrap: wrap;
        }
        .btn {
            padding: 12px 24px;
            font-size: 16px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            transition: all 0.3s;
        }
        .btn-primary {
            background-color: #007bff;
            color: white;
        }
        .btn-primary:hover {
            background-color: #0056b3;
        }
        .btn-success {
            background-color: #28a745;
            color: white;
        }
        .btn-success:hover {
            background-color: #1e7e34;
        }
        .btn-danger {
            background-color: #dc3545;
            color: white;
        }
        .btn-danger:hover {
            background-color: #c82333;
        }
        .btn-secondary {
            background-color: #6c757d;
            color: white;
        }
        .btn-secondary:hover {
            background-color: #545b62;
        }
        .preview-section {
            margin-top: 30px;
            padding-top: 20px;
            border-top: 1px solid #eee;
        }
        .preview-section h3 {
            color: #666;
            margin-bottom: 15px;
        }
        #signaturePreview {
            max-width: 400px;
            border: 1px solid #ddd;
            border-radius: 5px;
            padding: 10px;
            background: white;
        }
        .instructions {
            color: #666;
            margin-bottom: 20px;
            font-size: 14px;
        }
        .color-picker {
            margin-bottom: 20px;
        }
        .color-picker label {
            margin-right: 10px;
            font-weight: bold;
        }
        .color-picker input[type="color"] {
            width: 50px;
            height: 30px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
        }
        .brush-size {
            margin-bottom: 20px;
        }
        .brush-size label {
            margin-right: 10px;
            font-weight: bold;
        }
        .brush-size input[type="range"] {
            width: 200px;
            vertical-align: middle;
        }
        .brush-size span {
            margin-left: 10px;
            font-weight: bold;
            color: #007bff;
        }
        .back-link {
            display: inline-block;
            margin-bottom: 20px;
            text-decoration: none;
            color: #007bff;
            font-size: 14px;
        }
        .back-link:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <div class="container">
        <a href="/" class="back-link">&lt; 返回主页</a>
        
        <h1>电子签名</h1>
        
        <p class="instructions">请在下方画布中使用鼠标或手写板签名，完成后可保存为图片</p>
        
        <div class="color-picker">
            <label for="penColor">签名颜色：</label>
            <input type="color" id="penColor" value="#000000">
        </div>
        
        <div class="brush-size">
            <label for="brushSize">笔触粗细：</label>
            <input type="range" id="brushSize" min="1" max="10" value="3">
            <span id="brushSizeValue">3px</span>
        </div>
        
        <div class="canvas-container">
            <canvas id="signatureCanvas" width="800" height="300"></canvas>
        </div>
        
        <div class="button-group">
            <button class="btn btn-danger" onclick="clearCanvas()">清除签名</button>
            <button class="btn btn-primary" onclick="undoLastStroke()">撤销</button>
            <button class="btn btn-success" onclick="saveSignature()">保存签名</button>
            <button class="btn btn-secondary" onclick="downloadSignature()">下载图片</button>
        </div>
        
        <div class="preview-section">
            <h3>签名预览</h3>
            <div id="signaturePreview">
                <p style="color: #999;">暂无签名</p>
            </div>
        </div>
    </div>

    <script>
        const canvas = document.getElementById('signatureCanvas');
        const ctx = canvas.getContext('2d');
        const previewDiv = document.getElementById('signaturePreview');
        const penColor = document.getElementById('penColor');
        const brushSize = document.getElementById('brushSize');
        const brushSizeValue = document.getElementById('brushSizeValue');
        
        let isDrawing = false;
        let lastX = 0;
        let lastY = 0;
        let history = [];
        
        brushSize.addEventListener('input', function() {
            brushSizeValue.textContent = this.value + 'px';
        });
        
        function saveState() {
            history.push(canvas.toDataURL());
            if (history.length > 20) {
                history.shift();
            }
        }
        
        function startDrawing(e) {
            isDrawing = true;
            saveState();
            [lastX, lastY] = getPosition(e);
        }
        
        function draw(e) {
            if (!isDrawing) return;
            
            const [x, y] = getPosition(e);
            
            ctx.beginPath();
            ctx.strokeStyle = penColor.value;
            ctx.lineWidth = brushSize.value;
            ctx.lineCap = 'round';
            ctx.lineJoin = 'round';
            
            ctx.moveTo(lastX, lastY);
            ctx.lineTo(x, y);
            ctx.stroke();
            
            [lastX, lastY] = [x, y];
        }
        
        function stopDrawing() {
            isDrawing = false;
        }
        
        function getPosition(e) {
            const rect = canvas.getBoundingClientRect();
            let clientX, clientY;
            
            if (e.touches) {
                clientX = e.touches[0].clientX;
                clientY = e.touches[0].clientY;
            } else {
                clientX = e.clientX;
                clientY = e.clientY;
            }
            
            return [
                clientX - rect.left,
                clientY - rect.top
            ];
        }
        
        function clearCanvas() {
            saveState();
            ctx.clearRect(0, 0, canvas.width, canvas.height);
            updatePreview();
        }
        
        function undoLastStroke() {
            if (history.length > 0) {
                const img = new Image();
                img.src = history.pop();
                img.onload = function() {
                    ctx.clearRect(0, 0, canvas.width, canvas.height);
                    ctx.drawImage(img, 0, 0);
                    updatePreview();
                };
            }
        }
        
        function saveSignature() {
            const dataURL = canvas.toDataURL('image/png');
            
            localStorage.setItem('signature', dataURL);
            
            alert('签名已保存！');
            updatePreview();
        }
        
        function downloadSignature() {
            const dataURL = canvas.toDataURL('image/png');
            const link = document.createElement('a');
            link.download = 'signature_' + new Date().getTime() + '.png';
            link.href = dataURL;
            link.click();
        }
        
        function updatePreview() {
            const dataURL = canvas.toDataURL('image/png');
            
            const hasContent = canvas.toDataURL() !== 
                'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==';
            
            if (hasContent) {
                previewDiv.innerHTML = '<img src="' + dataURL + '" style="max-width: 100%; border-radius: 5px;">';
            } else {
                previewDiv.innerHTML = '<p style="color: #999;">暂无签名</p>';
            }
        }
        
        window.onload = function() {
            const savedSignature = localStorage.getItem('signature');
            if (savedSignature) {
                const img = new Image();
                img.src = savedSignature;
                img.onload = function() {
                    ctx.drawImage(img, 0, 0);
                    updatePreview();
                };
            }
        };
        
        canvas.addEventListener('mousedown', startDrawing);
        canvas.addEventListener('mousemove', draw);
        canvas.addEventListener('mouseup', stopDrawing);
        canvas.addEventListener('mouseout', stopDrawing);
        
        canvas.addEventListener('touchstart', function(e) {
            e.preventDefault();
            startDrawing(e);
        });
        canvas.addEventListener('touchmove', function(e) {
            e.preventDefault();
            draw(e);
        });
        canvas.addEventListener('touchend', stopDrawing);
    </script>
</body>
</html>