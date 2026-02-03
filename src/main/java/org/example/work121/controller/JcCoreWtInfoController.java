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
@RequestMapping("/api/jc-core-wt-info")
@CrossOrigin(origins = "*")
public class JcCoreWtInfoController {

    private static final Logger logger = LoggerFactory.getLogger(JcCoreWtInfoController.class);

    @Autowired
    private JcCoreWtInfoService jcCoreWtInfoService;

    @GetMapping("/by-wt-num")
    public Map<String, Object> getByWtNum(@RequestParam String wtNum) {
        Map<String, Object> result = new HashMap<>();
        
        String cleanWtNum = wtNum;
        if (wtNum.startsWith("{") && wtNum.endsWith("}")) {
            cleanWtNum = wtNum.substring(1, wtNum.length() - 1);
            logger.info("WT_NUM包含大括号，已清理: {} -> {}", wtNum, cleanWtNum);
        }
        
        logger.info("接收到查询请求，WT_NUM: {}", cleanWtNum);
        
        try {
            JcCoreWtInfo wtInfo = jcCoreWtInfoService.getByWtNum(cleanWtNum);
            
            if (wtInfo != null) {
                 logger.info("查询成功，找到委托信息");
                 result.put("success", true);
                 result.put("message", "查询成功");
                 result.put("data", wtInfo);
             } else {
                 logger.warn("查询失败，未找到委托信息，WT_NUM: {}", cleanWtNum);
                result.put("success", false);
                result.put("message", "未找到对应的委托信息");
                result.put("data", null);
            }
        } catch (Exception e) {
             logger.error("查询异常，WT_NUM: {}", cleanWtNum, e);
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
            result.put("data", null);
        }
        
        return result;
    }

    @GetMapping("/by-reg-name")
    public Map<String, Object> getByRegName(@RequestParam String regName) {
        Map<String, Object> result = new HashMap<>();
        logger.info("接收到根据登记人查询请求，REG_NAME: {}", regName);
        
        try {
            java.util.List<JcCoreWtInfo> list = jcCoreWtInfoService.getByRegName(regName);
            result.put("success", true);
            result.put("message", "查询成功");
            result.put("data", list);
            logger.info("查询成功，找到 {} 条记录", list.size());
        } catch (Exception e) {
            logger.error("查询异常，REG_NAME: {}", regName, e);
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
        }
        
        return result;
    }

    @GetMapping("/by-id")
    public Map<String, Object> getById(@RequestParam String id) {
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
}
