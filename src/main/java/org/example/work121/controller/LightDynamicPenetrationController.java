package org.example.work121.controller;

import org.example.work121.entity.LightDynamicPenetration;
import org.example.work121.service.LightDynamicPenetrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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

    @PostMapping("/save")
    public Map<String, Object> save(@RequestBody LightDynamicPenetration entity) {
        Map<String, Object> result = new HashMap<>();
        try {
            service.save(entity);
            result.put("success", true);
            result.put("message", "保存成功");
        } catch (Exception e) {
            logger.error("保存失败", e);
            result.put("success", false);
            result.put("message", "保存失败: " + e.getMessage());
        }
        return result;
    }
}
