package org.example.work121.controller;

import org.example.work121.entity.BeckmanBeam;
import org.example.work121.service.BeckmanBeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/beckman-beam")
@CrossOrigin(origins = "*")
public class BeckmanBeamController {

    private static final Logger logger = LoggerFactory.getLogger(BeckmanBeamController.class);

    @Autowired
    private BeckmanBeamService service;

    @GetMapping("/get-by-entrustment-id")
    public Map<String, Object> getByEntrustmentId(@RequestParam String entrustmentId) {
        Map<String, Object> result = new HashMap<>();
        try {
            java.util.List<BeckmanBeam> list = service.getByEntrustmentId(entrustmentId);
            result.put("success", true);
            result.put("data", list);
        } catch (Exception e) {
            logger.error("查询失败", e);
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("/save")
    public Map<String, Object> save(@RequestBody BeckmanBeam entity) {
        Map<String, Object> result = new HashMap<>();
        try {
            service.save(entity);
            result.put("success", true);
            result.put("message", "保存成功");
            result.put("data", entity);
        } catch (Exception e) {
            logger.error("保存失败", e);
            result.put("success", false);
            result.put("message", "保存失败: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("/delete")
    public Map<String, Object> delete(@RequestBody Map<String, String> payload) {
        Map<String, Object> result = new HashMap<>();
        try {
            String id = payload.get("id");
            service.deleteById(id);
            result.put("success", true);
            result.put("message", "删除成功");
        } catch (Exception e) {
            logger.error("删除失败", e);
            result.put("success", false);
            result.put("message", "删除失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/report/get-by-entrustment-id")
    public Map<String, Object> getReportByEntrustmentId(@RequestParam String entrustmentId) {
        Map<String, Object> result = new HashMap<>();
        try {
            logger.info("=== DEBUG: 查询贝克曼梁法报告，entrustmentId: {}", entrustmentId);
            org.example.work121.entity.BeckmanBeamReport entity = service.getReportByEntrustmentId(entrustmentId);
            if (entity != null) {
                logger.info("=== DEBUG: 找到报告，ID: {}, entrustmentId: {}", entity.getId(), entity.getEntrustmentId());
                if (entity.getDataJson() != null && !entity.getDataJson().isEmpty()) {
                    logger.info("=== DEBUG: DATA_JSON 长度: {}", entity.getDataJson().length());
                    logger.info("=== DEBUG: DATA_JSON 包含 station_1: {}", entity.getDataJson().contains("\"station_1\""));
                    // 尝试解析并提取 station_1 的值
                    try {
                        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                        Map<String, Object> jsonMap = mapper.readValue(entity.getDataJson(), Map.class);
                        if (jsonMap.containsKey("station_1")) {
                            logger.info("=== DEBUG: station_1 的值: {}", jsonMap.get("station_1"));
                        } else {
                            logger.warn("=== DEBUG: DATA_JSON 中不包含 station_1 字段");
                            // 列出前10个字段名用于调试
                            int count = 0;
                            for (String key : jsonMap.keySet()) {
                                if (count < 10) {
                                    logger.info("=== DEBUG: 字段名示例: {}", key);
                                    count++;
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.error("=== DEBUG: 解析 DATA_JSON 失败", e);
                    }
                } else {
                    logger.warn("=== DEBUG: DATA_JSON 为空或 null");
                }
                result.put("success", true);
                result.put("data", entity);
            } else {
                logger.warn("=== DEBUG: 未找到报告记录，entrustmentId: {}", entrustmentId);
                result.put("success", false);
                result.put("message", "未找到报告记录");
            }
        } catch (Exception e) {
            logger.error("查询报告失败", e);
            result.put("success", false);
            result.put("message", "查询报告失败: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("/report/save")
    public Map<String, Object> saveReport(@RequestBody org.example.work121.entity.BeckmanBeamReport entity) {
        Map<String, Object> result = new HashMap<>();
        try {
            service.saveReport(entity);
            result.put("success", true);
            result.put("message", "报告保存成功");
        } catch (Exception e) {
            logger.error("报告保存失败", e);
            result.put("success", false);
            result.put("message", "报告保存失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/result/get-by-entrustment-id")
    public Map<String, Object> getResultByEntrustmentId(@RequestParam String entrustmentId) {
        Map<String, Object> result = new HashMap<>();
        try {
            org.example.work121.entity.BeckmanBeamResult entity = service.getResultByEntrustmentId(entrustmentId);
            if (entity != null) {
                result.put("success", true);
                result.put("data", entity);
            } else {
                result.put("success", false);
                result.put("message", "未找到结果记录");
            }
        } catch (Exception e) {
            logger.error("查询结果失败", e);
            result.put("success", false);
            result.put("message", "查询结果失败: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("/result/save")
    public Map<String, Object> saveResult(@RequestBody org.example.work121.entity.BeckmanBeamResult entity) {
        Map<String, Object> result = new HashMap<>();
        try {
            service.saveResult(entity);
            result.put("success", true);
            result.put("message", "结果保存成功");
        } catch (Exception e) {
            logger.error("结果保存失败", e);
            result.put("success", false);
            result.put("message", "结果保存失败: " + e.getMessage());
        }
        return result;
    }
}
