package org.example.work121.controller;

import org.example.work121.entity.JcCoreWtInfo;
import org.example.work121.service.JcCoreWtInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/jc-core-test-result")
@CrossOrigin(origins = "*")
public class JcCoreTestResultController {

    private static final Logger logger = LoggerFactory.getLogger(JcCoreTestResultController.class);

    @Autowired
    private JcCoreWtInfoService jcCoreWtInfoService;

    @Autowired
    private org.example.work121.service.UserService userService;

    @GetMapping("/by-category")
    public Map<String, Object> getByCategory(@RequestParam String category,
                                            @RequestParam String regName,
                                            @RequestParam(required = false) String wtNum,
                                            @RequestParam(defaultValue = "1") int pageNum,
                                            @RequestParam(defaultValue = "7") int pageSize) {
        Map<String, Object> result = new HashMap<>();
        logger.info("接收到检测结果列表查询请求，CATEGORY: {}, REG_NAME: {}, WT_NUM: {}, pageNum: {}, pageSize: {}", category, regName, wtNum, pageNum, pageSize);
        
        try {
            java.util.List<String> categories = java.util.Arrays.asList(category.split(","));
            
            java.util.List<String> names = new java.util.ArrayList<>();
            if (regName != null && !regName.isEmpty()) {
                names.add(regName);
                
                // 1. Try treating regName as Account -> Get Name
                try {
                    org.example.work121.entity.User user = userService.getUserByAccount(regName);
                    if (user != null && user.getUserName() != null && !user.getUserName().isEmpty()) {
                        if (!names.contains(user.getUserName())) {
                            names.add(user.getUserName());
                            logger.info("Found real name for account {}: {}", regName, user.getUserName());
                        }
                    }
                } catch (Exception e) {
                    logger.warn("Failed to look up user real name for account: {}", regName, e);
                }

                // 2. Try treating regName as Name -> Get Account(s)
                try {
                    java.util.List<org.example.work121.entity.User> users = userService.getUserByName(regName);
                    if (users != null && !users.isEmpty()) {
                        for (org.example.work121.entity.User u : users) {
                            if (u.getUserAccount() != null && !u.getUserAccount().isEmpty()) {
                                if (!names.contains(u.getUserAccount())) {
                                    names.add(u.getUserAccount());
                                    logger.info("Found account for real name {}: {}", regName, u.getUserAccount());
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.warn("Failed to look up user account for name: {}", regName, e);
                }

                // 3. Try treating regName as ID -> Get Account
                try {
                    org.example.work121.entity.User userById = userService.getUserById(regName);
                    if (userById != null && userById.getUserAccount() != null && !userById.getUserAccount().isEmpty()) {
                        if (!names.contains(userById.getUserAccount())) {
                            names.add(userById.getUserAccount());
                            logger.info("Found account for ID {}: {}", regName, userById.getUserAccount());
                        }
                        // Also add name just in case
                        if (userById.getUserName() != null && !names.contains(userById.getUserName())) {
                            names.add(userById.getUserName());
                        }
                    }
                } catch (Exception e) {
                    logger.warn("Failed to look up user account for ID: {}", regName, e);
                }
            }

            com.github.pagehelper.PageInfo<JcCoreWtInfo> pageInfo = jcCoreWtInfoService.getByCategory(categories, names, wtNum, pageNum, pageSize);
            result.put("success", true);
            result.put("message", "查询成功");
            result.put("data", pageInfo);
            logger.info("查询成功，找到 {} 条记录", pageInfo.getTotal());
        } catch (Exception e) {
            logger.error("查询异常，CATEGORY: {}", category, e);
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
        }
        
        return result;
    }
}
