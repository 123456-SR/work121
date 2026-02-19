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
}
