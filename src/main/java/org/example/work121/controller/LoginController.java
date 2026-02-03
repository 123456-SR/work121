package org.example.work121.controller;

import org.example.work121.entity.User;
import org.example.work121.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Value("${spring.datasource.url}")
    private String defaultDbUrl;

    private String dbHost = "localhost";
    private String dbPort = "1521";

    @PostConstruct
    public void init() {
        if (defaultDbUrl != null && !defaultDbUrl.isEmpty()) {
            try {
                // Try to extract host and port from jdbc:oracle:thin:@HOST:PORT:SID or /SERVICE
                // Pattern: @([^:/]+)(:(\d+))?
                Pattern pattern = Pattern.compile("@([^:/]+)(:(\\d+))?");
                Matcher matcher = pattern.matcher(defaultDbUrl);
                if (matcher.find()) {
                    dbHost = matcher.group(1);
                    if (matcher.group(3) != null) {
                        dbPort = matcher.group(3);
                    }
                    logger.info("Parsed DB Config from properties - Host: {}, Port: {}", dbHost, dbPort);
                }
            } catch (Exception e) {
                logger.warn("Failed to parse default DB URL: {}", defaultDbUrl, e);
            }
        }
    }

    /**
     * Helper method to determine the correct JDBC URL (Service Name or SID)
     */
    private String getValidJdbcUrl(String dbName, String username, String password) throws SQLException {
        String urlService = "jdbc:oracle:thin:@" + dbHost + ":" + dbPort + "/" + dbName;
        String urlSid = "jdbc:oracle:thin:@" + dbHost + ":" + dbPort + ":" + dbName;

        try (Connection conn = DriverManager.getConnection(urlService, username, password)) {
            return urlService;
        } catch (SQLException e1) {
            try (Connection conn = DriverManager.getConnection(urlSid, username, password)) {
                return urlSid;
            } catch (SQLException e2) {
                logger.error("Connection failed for Service Name: {}", e1.getMessage());
                logger.error("Connection failed for SID: {}", e2.getMessage());
                throw new SQLException("Connection failed for both Service Name (" + e1.getMessage() + ") and SID (" + e2.getMessage() + ")");
            }
        }
    }

    /**
     * 第一步：验证数据库连接
     */
    @PostMapping("/db/connect")
    public Map<String, Object> connectDb(@RequestBody Map<String, String> dbData) {
        Map<String, Object> result = new HashMap<>();
        
        String dbName = dbData.get("dbName");
        String username = dbData.get("username"); // DB User
        String password = dbData.get("password"); // DB Pass
        
        if (dbName == null || dbName.isEmpty() || 
            username == null || username.isEmpty() || 
            password == null || password.isEmpty()) {
            result.put("success", false);
            result.put("message", "数据库信息不完整");
            return result;
        }
        
        try {
            String validUrl = getValidJdbcUrl(dbName, username, password);
            result.put("success", true);
            result.put("message", "数据库连接成功");
            // Optionally return the detected type or URL if we want to cache it frontend side, 
            // but for now we'll just re-detect on login/register to keep frontend simple.
        } catch (SQLException e) {
            logger.error("数据库连接失败: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "连接失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 第二步：用户登录 (需要同时提供DB信息)
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginData) {
        Map<String, Object> result = new HashMap<>();
        
        // DB Info (Optional now)
        String dbName = loginData.get("dbName");
        String dbUser = loginData.get("dbUser");
        String dbPass = loginData.get("dbPass");
        
        // App User Info
        String username = loginData.get("username");
        String password = loginData.get("password");
        
        if (username == null || password == null) {
            result.put("success", false);
            result.put("message", "信息不完整");
            return result;
        }
        
        try {
            User user;
            if (dbName != null && !dbName.isEmpty() && dbUser != null && dbPass != null) {
                String dbUrl = getValidJdbcUrl(dbName, dbUser, dbPass);
                // 使用提供的DB信息查询用户
                user = userService.getUserByAccount(username, dbUrl, dbUser, dbPass);
            } else {
                // 使用默认DB
                user = userService.getUserByAccount(username);
            }
            
            if (user == null) {
                result.put("success", false);
                result.put("message", "用户不存在");
                return result;
            }
            
            if (!password.equals(user.getUserPass())) {
                result.put("success", false);
                result.put("message", "密码错误");
                return result;
            }
            
            if ("0".equals(user.getUserStatus())) {
                result.put("success", false);
                result.put("message", "用户已禁用");
                return result;
            }
            
            String token = UUID.randomUUID().toString();
            
            result.put("success", true);
            result.put("message", "登录成功");
            result.put("token", token);
            
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", user.getUserId());
            userInfo.put("username", user.getUserAccount());
            userInfo.put("userName", user.getUserName());
            userInfo.put("dbName", dbName); // Keep track of current DB
            
            result.put("data", userInfo);
            
        } catch (Exception e) {
            logger.error("登录异常", e);
            result.put("success", false);
            result.put("message", "登录异常: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 第二步：用户注册
     */
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> regData) {
        Map<String, Object> result = new HashMap<>();
        
        // DB Info (Optional)
        String dbName = regData.get("dbName");
        String dbUser = regData.get("dbUser");
        String dbPass = regData.get("dbPass");
        
        // App User Info
        String username = regData.get("username");
        String password = regData.get("password");
        String fullName = regData.get("fullName"); // Optional
        
        if (username == null || password == null) {
            result.put("success", false);
            result.put("message", "信息不完整");
            return result;
        }
        
        try {
            User existingUser;
            boolean useDynamicDb = (dbName != null && !dbName.isEmpty() && dbUser != null && dbPass != null);
            String dbUrl = null;

            if (useDynamicDb) {
                dbUrl = getValidJdbcUrl(dbName, dbUser, dbPass);
                existingUser = userService.getUserByAccount(username, dbUrl, dbUser, dbPass);
            } else {
                existingUser = userService.getUserByAccount(username);
            }

            if (existingUser != null) {
                result.put("success", false);
                result.put("message", "用户名已存在");
                return result;
            }
            
            User newUser = new User();
            newUser.setUserAccount(username);
            newUser.setUserPass(password);
            newUser.setUserName(fullName != null ? fullName : username);
            newUser.setUserStatus("1");
            
            boolean success;
            if (useDynamicDb) {
                success = userService.registerUser(newUser, dbUrl, dbUser, dbPass);
            } else {
                success = userService.registerUser(newUser);
            }
            
            if (success) {
                result.put("success", true);
                result.put("message", "注册成功");
            } else {
                result.put("success", false);
                result.put("message", "注册失败");
            }
            
        } catch (Exception e) {
            logger.error("注册异常", e);
            result.put("success", false);
            result.put("message", "注册异常: " + e.getMessage());
        }
        
        return result;
    }
}
