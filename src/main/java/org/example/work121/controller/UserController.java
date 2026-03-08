package org.example.work121.controller;

import org.example.work121.entity.User;
import org.example.work121.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    
    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @GetMapping("/list")
    public Map<String, Object> listUsers() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<User> users = userService.getAllUsers();
            result.put("success", true);
            result.put("data", users);
            result.put("message", "获取用户列表成功");
        } catch (Exception e) {
            logger.error("获取用户列表失败", e);
            result.put("success", false);
            result.put("message", "获取用户列表失败: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("/add")
    public Map<String, Object> addUser(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = userService.registerUser(user);
            if (success) {
                // 如果有position字段，保存到JZS_USER_EXT表
                if (user.getPosition() != null && !user.getPosition().isEmpty()) {
                    String extSql = "INSERT INTO JZS_USER_EXT (ID, POSITION) VALUES (?, ?)";
                    jdbcTemplate.update(extSql, user.getUserAccount(), user.getPosition());
                }
                result.put("success", true);
                result.put("message", "添加用户成功");
            } else {
                result.put("success", false);
                result.put("message", "添加用户失败");
            }
        } catch (Exception e) {
            logger.error("添加用户失败", e);
            result.put("success", false);
            result.put("message", "添加用户失败: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("/update")
    public Map<String, Object> updateUser(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 这里需要实现更新用户的逻辑
            // 由于UserService中没有直接的更新方法，我们需要先获取用户，然后更新字段，再保存
            User existingUser = userService.getUserById(user.getUserId());
            if (existingUser == null) {
                result.put("success", false);
                result.put("message", "用户不存在");
                return result;
            }
            
            // 更新用户字段
            existingUser.setUserAccount(user.getUserAccount());
            if (user.getUserPass() != null && !user.getUserPass().isEmpty()) {
                existingUser.setUserPass(user.getUserPass());
            }
            existingUser.setUserName(user.getUserName());
            existingUser.setPosition(user.getPosition());
            
            // 由于registerUser方法会插入新记录，我们需要使用JdbcTemplate直接更新
            String sql = "UPDATE JZS_USERS SET USER_ACCOUNT = ?, USER_PASS = ?, USER_NAME = ? WHERE ID = ?";
            int rows = jdbcTemplate.update(sql, 
                existingUser.getUserAccount(),
                existingUser.getUserPass(),
                existingUser.getUserName(),
                existingUser.getUserId()
            );
            
            // 更新JZS_USER_EXT表中的position字段
            String extSql = "MERGE INTO JZS_USER_EXT j USING DUAL ON (j.ID = ?) " +
                           "WHEN MATCHED THEN UPDATE SET j.POSITION = ? " +
                           "WHEN NOT MATCHED THEN INSERT (ID, POSITION) VALUES (?, ?)";
            jdbcTemplate.update(extSql, 
                existingUser.getUserAccount(),
                existingUser.getPosition(),
                existingUser.getUserAccount(),
                existingUser.getPosition()
            );
            
            if (rows > 0) {
                result.put("success", true);
                result.put("message", "更新用户成功");
            } else {
                result.put("success", false);
                result.put("message", "更新用户失败");
            }
        } catch (Exception e) {
            logger.error("更新用户失败", e);
            result.put("success", false);
            result.put("message", "更新用户失败: " + e.getMessage());
        }
        return result;
    }
}
