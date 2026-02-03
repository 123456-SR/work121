package org.example.work121.controller;

import org.example.work121.entity.Entrustment;
import org.example.work121.service.EntrustmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 检测委托单控制器
 */
@RestController
@RequestMapping("/api/entrustment")
public class EntrustmentController {

    @Autowired
    private EntrustmentService entrustmentService;

    /**
     * 保存检测委托单
     * @param entrustment 检测委托单数据
     * @return 响应结果
     */
    @PostMapping("/save")
    public Map<String, Object> saveEntrustment(@RequestBody Entrustment entrustment) {
        Map<String, Object> result = new HashMap<>();

        try {
            boolean success = entrustmentService.saveEntrustment(entrustment);

            if (success) {
                result.put("success", true);
                result.put("message", "保存成功");
            } else {
                result.put("success", false);
                result.put("message", "保存失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "保存异常: " + e.getMessage());
        }

        return result;
    }

    /**
     * 根据统一编号查询检测委托单
     * @param unifiedNumber 统一编号
     * @return 响应结果
     */
    @PostMapping("/getByUnifiedNumber")
    public Map<String, Object> getEntrustmentByUnifiedNumber(@RequestBody Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();

        try {
            String unifiedNumber = params.get("unifiedNumber");
            if (unifiedNumber == null || unifiedNumber.isEmpty()) {
                result.put("success", false);
                result.put("message", "统一编号不能为空");
                return result;
            }

            Entrustment record = entrustmentService.getEntrustmentByUnifiedNumber(unifiedNumber);

            if (record != null) {
                result.put("success", true);
                result.put("data", record);
            } else {
                result.put("success", false);
                result.put("message", "未找到记录");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "查询异常: " + e.getMessage());
        }

        return result;
    }
}
