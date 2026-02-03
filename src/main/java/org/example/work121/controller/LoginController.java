package org.example.work121.controller;

import org.example.work121.entity.User;
import org.example.work121.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginData) {
        Map<String, Object> result = new HashMap<>();
        
        String username = loginData.get("username");
        String password = loginData.get("password");
        
        logger.info("接收到登录请求，用户名: {}", username);
        
        try {
            // 验证输入
            if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
                logger.warn("登录失败：用户名或密码为空");
                result.put("success", false);
                result.put("message", "用户名和密码不能为空");
                return result;
            }
            
            // 查询用户
            User user = userService.getUserByAccount(username);
            
            if (user == null) {
                logger.warn("登录失败：用户不存在，用户名: {}", username);
                result.put("success", false);
                result.put("message", "用户名或密码错误");
                return result;
            }
            
            // 验证密码
            if (!password.equals(user.getUserPass())) {
                logger.warn("登录失败：密码错误，用户名: {}", username);
                result.put("success", false);
                result.put("message", "用户名或密码错误");
                return result;
            }
            
            // 验证用户状态
            if ("0".equals(user.getUserStatus())) {
                logger.warn("登录失败：用户已禁用，用户名: {}", username);
                result.put("success", false);
                result.put("message", "用户已被禁用");
                return result;
            }
            
            // 登录成功，生成token
            String token = UUID.randomUUID().toString();
            
            logger.info("登录成功，用户名: {}", username);
            
            result.put("success", true);
            result.put("message", "登录成功");
            result.put("token", token);
            
            // 构建用户信息
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", user.getUserId());
            userInfo.put("username", user.getUserAccount());
            userInfo.put("status", user.getUserStatus());
            
            result.put("data", userInfo);
            
        } catch (Exception e) {
            logger.error("登录异常，用户名: {}", username, e);
            result.put("success", false);
            result.put("message", "登录失败: " + e.getMessage());
        }
        
        return result;
    }
    
    @PostMapping("/logout")
    public Map<String, Object> logout() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 这里可以添加token失效逻辑
            logger.info("用户退出登录");
            
            result.put("success", true);
            result.put("message", "退出成功");
        } catch (Exception e) {
            logger.error("退出登录异常", e);
            result.put("success", false);
            result.put("message", "退出失败: " + e.getMessage());
        }
        
        return result;
    }
}