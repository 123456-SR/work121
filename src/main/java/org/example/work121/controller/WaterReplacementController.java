package org.example.work121.controller;

import org.example.work121.entity.WaterReplacement;
import org.example.work121.entity.WaterReplacementReport;
import org.example.work121.service.WaterReplacementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/water-replacement")
@CrossOrigin(origins = "*")
public class WaterReplacementController {

    private static final Logger logger = LoggerFactory.getLogger(WaterReplacementController.class);

    @Autowired
    private WaterReplacementService service;

    @GetMapping("/get-by-entrustment-id")
    public Map<String, Object> getByEntrustmentId(@RequestParam String entrustmentId) {
        Map<String, Object> result = new HashMap<>();
        try {
            java.util.List<WaterReplacement> list = service.getByEntrustmentId(entrustmentId);
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
    public Map<String, Object> save(@RequestBody WaterReplacement entity) {
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
            WaterReplacementReport entity = service.getReportByEntrustmentId(entrustmentId);
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
    public Map<String, Object> saveReport(@RequestBody WaterReplacementReport entity) {
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
}
