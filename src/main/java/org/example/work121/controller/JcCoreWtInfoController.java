package org.example.work121.controller;

import org.example.work121.entity.JcCoreWtInfo;
import org.example.work121.service.JcCoreWtInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jc-core-wt-info")
@CrossOrigin(origins = "*")
public class JcCoreWtInfoController {

    private static final Logger logger = LoggerFactory.getLogger(JcCoreWtInfoController.class);

    @Autowired
    private JcCoreWtInfoService jcCoreWtInfoService;

    @Autowired
    private org.example.work121.service.UserService userService;

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @GetMapping("/debug-check-data")
    public Map<String, Object> debugCheckData(@RequestParam(value = "username", required = false) String username) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 1. Get all users
            List<Map<String, Object>> users = jdbcTemplate.queryForList("SELECT USER_ACCOUNT, USER_NAME FROM JZS_USERS");
            result.put("users", users);

            // 2. Get distinct person values
            String[] cols = {"WT_MAN", "TESTER", "REVIEWER", "APPROVER", "SAMPLING_MAN"};
            Map<String, List<String>> colValues = new HashMap<>();
            for (String col : cols) {
                try {
                     String table = "JC_CORE_WT_INFO"; // Try extension table first for most fields
                     if ("BUILDING_MAN".equals(col) || "SAMPLING_MAN".equals(col) || "YW_MAN".equals(col)) {
                         table = "JC_CORE_WT_INFO_EXT";
                     }
                     // Check if column exists
                     try {
                        jdbcTemplate.queryForList("SELECT " + col + " FROM " + table + " WHERE ROWNUM <= 1");
                     } catch (Exception e) {
                         if (table.equals("JC_CORE_WT_INFO")) table = "JC_CORE_WT_INFO_EXT";
                         else table = "JC_CORE_WT_INFO";
                     }
                     
                     List<String> vals = jdbcTemplate.queryForList("SELECT DISTINCT " + col + " FROM " + table + " WHERE " + col + " IS NOT NULL AND ROWNUM <= 10", String.class);
                     colValues.put(col, vals);
                } catch (Exception e) {
                    colValues.put(col, java.util.Collections.singletonList("Error: " + e.getMessage()));
                }
            }
            result.put("columnValues", colValues);
            
            // 3. Test matching logic if username provided
            if (username != null) {
                Map<String, Object> matchInfo = new HashMap<>();
                matchInfo.put("inputUsername", username);
                
                // Lookup real name
                String realName = null;
                try {
                    org.example.work121.entity.User user = userService.getUserByAccount(username);
                    if (user != null) realName = user.getUserName();
                } catch (Exception e) {}
                matchInfo.put("realName", realName);
                
                result.put("matchTest", matchInfo);
            }

        } catch (Exception e) {
            result.put("error", e.getMessage());
        }
        return result;
    }

    @GetMapping("/detail")
    public Map<String, Object> getDetail(@RequestParam("unifiedNumber") String unifiedNumber) {
        return getByWtNum(unifiedNumber);
    }

    @GetMapping("/by-wt-num")
    public Map<String, Object> getByWtNum(@RequestParam("wtNum") String wtNum) {
        Map<String, Object> result = new HashMap<>();
        
        String cleanWtNum = wtNum;
        boolean hasBraces = false;
        if (wtNum != null) {
            String trimmed = wtNum.trim();
            if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
                cleanWtNum = trimmed.substring(1, trimmed.length() - 1).trim();
                hasBraces = true;
                logger.info("WT_NUM包含大括号，准备尝试清理后的值: {} -> {}", wtNum, cleanWtNum);
            }
        }
        
        logger.info("接收到查询请求，WT_NUM (raw): '{}'", wtNum);
        
        try {
            // 1. 优先尝试原始值查询 (防止数据库中本身就包含大括号)
            JcCoreWtInfo wtInfo = jcCoreWtInfoService.getByWtNum(wtNum.trim());
            
            // 2. 如果原始值没查到，且包含大括号，尝试清理后的值
            if (wtInfo == null && hasBraces) {
                logger.info("原始值查询为空，尝试使用清理后的值查询: {}", cleanWtNum);
                wtInfo = jcCoreWtInfoService.getByWtNum(cleanWtNum);
            }
            
            if (wtInfo != null) {
               logger.info("查询成功，找到委托信息, ID: {}", wtInfo.getId());
               result.put("success", true);
               result.put("message", "查询成功");
               result.put("data", wtInfo);
             } else {
               logger.warn("查询失败，未找到委托信息，WT_NUM: {}", wtNum);
              result.put("success", false);
              result.put("message", "未找到对应的委托信息");
              result.put("data", null);
            }
        } catch (Exception e) {
             logger.error("查询异常，WT_NUM: {}", wtNum, e);
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
            result.put("data", null);
        }
        
        return result;
    }

    @GetMapping("/debug-all")
    public Map<String, Object> debugAll() {
        Map<String, Object> result = new HashMap<>();
        try {
            java.util.List<JcCoreWtInfo> list = jcCoreWtInfoService.debugSelectAll();
            result.put("success", true);
            result.put("data", list);
            result.put("count", list.size());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/by-reg-name")
    public Map<String, Object> getByRegName(@RequestParam("regName") String regName,
                                            @RequestParam(value = "wtNum", required = false) String wtNum,
                                            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                            @RequestParam(value = "pageSize", defaultValue = "7") int pageSize) {
        Map<String, Object> result = new HashMap<>();
        logger.info("接收到根据登记人查询请求，REG_NAME: {}, WT_NUM: {}, pageNum: {}, pageSize: {}", regName, wtNum, pageNum, pageSize);
        
        try {
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

            if (names.isEmpty()) {
                com.github.pagehelper.PageInfo<JcCoreWtInfo> emptyPage = new com.github.pagehelper.PageInfo<>(new java.util.ArrayList<>());
                result.put("success", true);
                result.put("message", "查询成功");
                result.put("data", emptyPage);
                return result;
            }

            // Convert all names to UPPERCASE for case-insensitive matching
            java.util.List<String> upperNames = new java.util.ArrayList<>();
            for (String n : names) {
                if (n != null) {
                    upperNames.add(n.toUpperCase());
                }
            }
            logger.info("Executing query with names: {}", upperNames);

            
            com.github.pagehelper.PageInfo<JcCoreWtInfo> pageInfo = jcCoreWtInfoService.getByRegName(upperNames, wtNum, pageNum, pageSize);
            result.put("success", true);
            result.put("message", "查询成功");
            result.put("data", pageInfo);
            logger.info("查询成功，找到 {} 条记录", pageInfo.getTotal());
        } catch (Exception e) {
            logger.error("查询异常，REG_NAME: {}", regName, e);
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
        }
        
        return result;
    }

    @GetMapping("/by-category")
    public Map<String, Object> getByCategory(@RequestParam("category") String category,
                                            @RequestParam(value = "regName", required = false) String regName,
                                            @RequestParam(value = "wtNum", required = false) String wtNum,
                                            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                            @RequestParam(value = "pageSize", defaultValue = "7") int pageSize) {
        Map<String, Object> result = new HashMap<>();
        logger.info("接收到根据检测类别查询请求，CATEGORY: {}, REG_NAME: {}, WT_NUM: {}, pageNum: {}, pageSize: {}", category, regName, wtNum, pageNum, pageSize);
        
        try {
            // Split comma-separated categories
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
            }
            
            if (names.isEmpty()) {
                com.github.pagehelper.PageInfo<JcCoreWtInfo> emptyPage = new com.github.pagehelper.PageInfo<>(new java.util.ArrayList<>());
                result.put("success", true);
                result.put("message", "查询成功");
                result.put("data", emptyPage);
                return result;
            }
            
            // Convert all names to UPPERCASE for case-insensitive matching
            java.util.List<String> upperNames = new java.util.ArrayList<>();
            for (String n : names) {
                if (n != null) {
                    upperNames.add(n.toUpperCase());
                }
            }
            
            logger.info("Executing selectByCategory with categories: {}, names: {}, wtNum: {}", categories, upperNames, wtNum);
            
            
            com.github.pagehelper.PageInfo<JcCoreWtInfo> pageInfo = jcCoreWtInfoService.getByCategory(categories, upperNames, wtNum, pageNum, pageSize);
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

    @GetMapping("/by-id")
    public Map<String, Object> getById(@RequestParam("id") String id) {
        Map<String, Object> result = new HashMap<>();
        logger.info("接收到根据ID查询请求，ID: {}", id);
        
        try {
            JcCoreWtInfo wtInfo = jcCoreWtInfoService.getById(id);
            if (wtInfo != null) {
                result.put("success", true);
                result.put("message", "查询成功");
                result.put("data", wtInfo);
            } else {
                result.put("success", false);
                result.put("message", "未找到记录");
            }
        } catch (Exception e) {
            logger.error("查询异常，ID: {}", id, e);
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
        }
        
        return result;
    }

    @PostMapping("/save")
    public Map<String, Object> save(@RequestBody JcCoreWtInfo info) {
        Map<String, Object> result = new HashMap<>();
        logger.info("接收到保存请求，ID: {}", info.getId());
        
        try {
            boolean success = jcCoreWtInfoService.save(info);
            if (success) {
                result.put("success", true);
                result.put("message", "保存成功");
            } else {
                result.put("success", false);
                result.put("message", "保存失败");
            }
        } catch (Exception e) {
            logger.error("保存异常，ID: {}", info.getId(), e);
            result.put("success", false);
            result.put("message", "保存失败: " + e.getMessage());
        }
        
        return result;
    }

    @PostMapping("/delete")
    public Map<String, Object> delete(@RequestBody Map<String, String> payload) {
        Map<String, Object> result = new HashMap<>();
        String id = payload.get("id");
        logger.info("接收到删除请求，ID: {}", id);
        
        if (id == null || id.isEmpty()) {
            result.put("success", false);
            result.put("message", "ID不能为空");
            return result;
        }

        try {
            boolean success = jcCoreWtInfoService.delete(id);
            if (success) {
                result.put("success", true);
                result.put("message", "删除成功");
            } else {
                result.put("success", false);
                result.put("message", "删除失败");
            }
        } catch (Exception e) {
            logger.error("删除异常，ID: {}", id, e);
            result.put("success", false);
            result.put("message", "删除失败: " + e.getMessage());
        }
        
        return result;
    }
}
