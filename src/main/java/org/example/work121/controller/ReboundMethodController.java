package org.example.work121.controller;

import org.example.work121.entity.ReboundMethod;
import org.example.work121.service.ReboundMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 回弹法检测控制器
 */
@RestController
@RequestMapping("/api/reboundMethod")
public class ReboundMethodController {

    @Autowired
    private ReboundMethodService reboundMethodService;

    /**
     * 保存回弹法检测记录
     * @param reboundMethod 回弹法检测数据
     * @return 响应结果
     */
    @PostMapping("/save")
    public Map<String, Object> saveReboundMethod(@RequestBody ReboundMethod reboundMethod) {
        Map<String, Object> result = new HashMap<>();

        try {
            boolean success = reboundMethodService.saveReboundMethod(reboundMethod);

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
     * 根据统一编号查询回弹法检测记录
     * @param unifiedNumber 统一编号
     * @return 响应结果
     */
    @PostMapping("/getByUnifiedNumber")
    public Map<String, Object> getReboundMethodByUnifiedNumber(@RequestBody Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();

        try {
            String unifiedNumber = params.get("unifiedNumber");
            if (unifiedNumber == null || unifiedNumber.isEmpty()) {
                result.put("success", false);
                result.put("message", "统一编号不能为空");
                return result;
            }

            ReboundMethod record = reboundMethodService.getReboundMethodByUnifiedNumber(unifiedNumber);

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
