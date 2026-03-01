package org.example.work121.controller;

import org.example.work121.entity.LightDynamicPenetration;
import org.example.work121.entity.LightDynamicPenetrationReport;
import org.example.work121.service.LightDynamicPenetrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/light-dynamic-penetration")
@CrossOrigin(origins = "*")
public class LightDynamicPenetrationController {

    private static final Logger logger = LoggerFactory.getLogger(LightDynamicPenetrationController.class);

    @Autowired
    private LightDynamicPenetrationService service;

    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable String id) {
        Map<String, Object> result = new HashMap<>();
        try {
            LightDynamicPenetration entity = service.getById(id);
            if (entity != null) {
                result.put("success", true);
                result.put("data", entity);
            } else {
                result.put("success", false);
                result.put("message", "未找到记录");
            }
        } catch (Exception e) {
            logger.error("查询失败", e);
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/get-by-entrustment-id")
    public Map<String, Object> getByEntrustmentId(@RequestParam String entrustmentId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<LightDynamicPenetration> list = service.getByEntrustmentId(entrustmentId);
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
    public Map<String, Object> save(@RequestBody LightDynamicPenetration entity) {
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
            LightDynamicPenetrationReport entity = service.getReportByEntrustmentId(entrustmentId);
            if (entity != null) {
                result.put("success", true);
                result.put("data", entity);
            } else {
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
    public Map<String, Object> saveReport(@RequestBody LightDynamicPenetrationReport entity) {
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
            org.example.work121.entity.LightDynamicPenetrationResult entity = service.getResultByEntrustmentId(entrustmentId);
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
    public Map<String, Object> saveResult(@RequestBody org.example.work121.entity.LightDynamicPenetrationResult entity) {
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
