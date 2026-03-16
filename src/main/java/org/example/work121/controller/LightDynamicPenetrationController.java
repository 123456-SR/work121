package org.example.work121.controller;

import org.example.work121.entity.LightDynamicPenetration;
import org.example.work121.entity.LightDynamicPenetrationReport;
import org.example.work121.service.LightDynamicPenetrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/light-dynamic-penetration")
@CrossOrigin(origins = "*")
public class LightDynamicPenetrationController {

    private static final Logger logger = LoggerFactory.getLogger(LightDynamicPenetrationController.class);

    @Autowired
    private LightDynamicPenetrationService service;

    @GetMapping("/export-formats")
    public Map<String, Object> exportFormats(@RequestParam(value = "baseName", required = false) String baseName) {
        Map<String, Object> result = new HashMap<>();
        String resolvedBaseName = (baseName == null || baseName.trim().isEmpty()) ? "动力触探记录表" : baseName.trim();
        try {
            Path templateDir = Paths.get(System.getProperty("user.dir"), "表");
            if (!Files.exists(templateDir) || !Files.isDirectory(templateDir)) {
                result.put("success", false);
                result.put("message", "模板目录不存在: " + templateDir.toString());
                return result;
            }

            List<Map<String, String>> templates = new ArrayList<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(templateDir)) {
                for (Path p : stream) {
                    if (p == null) continue;
                    String fileName = p.getFileName() == null ? "" : p.getFileName().toString();
                    if (fileName.isEmpty()) continue;
                    if (!fileName.startsWith(resolvedBaseName + ".")) continue;
                    String ext = "";
                    int idx = fileName.lastIndexOf('.');
                    if (idx >= 0 && idx < fileName.length() - 1) {
                        ext = fileName.substring(idx + 1).toLowerCase();
                    }
                    Map<String, String> item = new HashMap<>();
                    item.put("fileName", fileName);
                    item.put("ext", ext);
                    templates.add(item);
                }
            }

            Set<String> recommended = new LinkedHashSet<>();
            recommended.add("xls");
            recommended.add("xlsx");
            recommended.add("docx");
            for (Map<String, String> t : templates) {
                String ext = t.get("ext");
                if (ext != null && !ext.trim().isEmpty()) recommended.add(ext.trim().toLowerCase());
            }

            result.put("success", true);
            result.put("baseName", resolvedBaseName);
            result.put("templateDir", templateDir.toString());
            result.put("templates", templates);
            result.put("formats", new ArrayList<>(recommended));
            return result;
        } catch (Exception e) {
            logger.error("查询导出模板格式失败", e);
            result.put("success", false);
            result.put("message", "查询导出模板格式失败: " + e.getMessage());
            return result;
        }
    }

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

    @PostMapping("/export-excel")
    public Map<String, Object> exportExcel(@RequestBody Map<String, Object> payload) {
        Map<String, Object> result = new HashMap<>();
        try {
            String savedPath = service.exportRecordToExcel(payload);
            result.put("success", true);
            result.put("path", savedPath);
            result.put("message", "导出成功");
        } catch (Exception e) {
            logger.error("导出Excel失败", e);
            result.put("success", false);
            result.put("message", "导出Excel失败: " + e.getMessage());
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
