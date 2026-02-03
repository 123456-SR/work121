package org.example.work121.controller;

import org.example.work121.entity.JzsSignature;
import org.example.work121.service.JzsSignatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 签字存放表控制器
 */
@RestController
@RequestMapping("/api/signature")
public class JzsSignatureController {
    
    @Autowired
    private JzsSignatureService jzsSignatureService;

    /**
     * 上传签名图片（直接存储二进制数据）
     * @param file 图片文件
     * @param userAccount 用户账号
     * @param request 请求对象
     * @return 响应结果
     */
    @PostMapping("/upload")
    public Map<String, Object> uploadSignature(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "userAccount", required = false) String userAccount,
            HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取当前登录用户账号
            if (userAccount == null || userAccount.isEmpty()) {
                userAccount = getCurrentUserAccount(request);
            }
            
            if (userAccount == null) {
                result.put("success", false);
                result.put("message", "用户未登录");
                return result;
            }

            // 检查文件是否为空
            if (file.isEmpty()) {
                result.put("success", false);
                result.put("message", "请选择要上传的文件");
                return result;
            }

            // 检查文件类型
            String contentType = file.getContentType();
            if (!contentType.startsWith("image/")) {
                result.put("success", false);
                result.put("message", "请上传图片类型的文件");
                return result;
            }

            // 检查文件大小（限制为5MB）
            long fileSize = file.getSize();
            if (fileSize > 5 * 1024 * 1024) {
                result.put("success", false);
                result.put("message", "文件大小不能超过5MB");
                return result;
            }

            // 直接使用字节数组
            byte[] fileBytes = file.getBytes();

            // 创建签名实体
            JzsSignature signature = new JzsSignature();
            signature.setSignatureId(UUID.randomUUID().toString());
            signature.setUserAccount(userAccount);
            signature.setSignatureBlob(fileBytes);

            // 保存到数据库
            boolean success = jzsSignatureService.saveSignature(signature);

            if (success) {
                result.put("success", true);
                result.put("message", "签名上传成功");
                result.put("data", signature);
            } else {
                result.put("success", false);
                result.put("message", "签名保存失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "文件处理失败: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "上传异常: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取用户签名
     * @param params 请求参数
     * @return 响应结果
     */
    @PostMapping("/get")
    public Map<String, Object> getSignature(@RequestBody Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取用户账号
            String userAccount = params.get("userAccount");
            if (userAccount == null || userAccount.isEmpty()) {
                result.put("success", false);
                result.put("message", "用户账号不能为空");
                return result;
            }

            // 查询签名
            JzsSignature signature = jzsSignatureService.getSignatureByUserAccount(userAccount);

            if (signature != null) {
                // 构建返回数据，确保signatureBlob格式正确
                Map<String, Object> signatureData = new HashMap<>();
                signatureData.put("signatureId", signature.getSignatureId());
                signatureData.put("userAccount", signature.getUserAccount());
                
                // 将byte[]转换为Base64编码的字符串
                if (signature.getSignatureBlob() != null) {
                    String base64Signature = java.util.Base64.getEncoder().encodeToString(signature.getSignatureBlob());
                    signatureData.put("signatureBlob", base64Signature);
                }
                
                result.put("success", true);
                result.put("data", signatureData);
            } else {
                result.put("success", false);
                result.put("message", "未找到签名");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "查询异常: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 删除签名
     * @param signatureId 签名ID
     * @return 响应结果
     */
    @PostMapping("/delete")
    public Map<String, Object> deleteSignature(@RequestBody Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String signatureId = params.get("signatureId");
            if (signatureId == null || signatureId.isEmpty()) {
                result.put("success", false);
                result.put("message", "签名ID不能为空");
                return result;
            }

            boolean success = jzsSignatureService.deleteSignature(signatureId);

            if (success) {
                result.put("success", true);
                result.put("message", "签名删除成功");
            } else {
                result.put("success", false);
                result.put("message", "签名删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "删除异常: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取当前登录用户账号
     * @param request 请求对象
     * @return 用户账号
     */
    private String getCurrentUserAccount(HttpServletRequest request) {
        // 从请求参数中获取用户账号
        String userAccount = request.getParameter("userAccount");
        if (userAccount != null && !userAccount.isEmpty()) {
            return userAccount;
        }
        
        // 从请求头中获取用户账号
        userAccount = request.getHeader("userAccount");
        if (userAccount != null && !userAccount.isEmpty()) {
            return userAccount;
        }
        
        // 默认使用admin账号
        return "admin"; // 测试用默认值
    }
}
