package org.example.work121.controller;

import org.example.work121.dto.WorkflowRequest;
import org.example.work121.service.WorkflowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 工作流控制器
 * 处理所有表格的签名、审核、批准流程
 */
@RestController
@RequestMapping("/api/workflow")
@CrossOrigin(origins = "*")
public class WorkflowController {

    private static final Logger logger = LoggerFactory.getLogger(WorkflowController.class);

    @Autowired
    private WorkflowService workflowService;

    /**
     * 处理工作流请求
     * @param request 工作流请求数据
     * @return 响应结果
     */
    @PostMapping("/handle")
    public Map<String, Object> handleWorkflow(@RequestBody WorkflowRequest request) {
        Map<String, Object> result = new HashMap<>();
        
        // 基本参数校验
        if (request.getTableType() == null || request.getRecordId() == null || request.getAction() == null) {
            result.put("success", false);
            result.put("message", "Missing required parameters: tableType, recordId, or action");
            return result;
        }

        try {
            boolean success = workflowService.handleWorkflow(request);
            
            if (success) {
                result.put("success", true);
                result.put("message", "Operation successful");
            } else {
                result.put("success", false);
                result.put("message", "Operation failed. Please check record existence or status.");
            }
        } catch (Exception e) {
            logger.error("Workflow error", e);
            result.put("success", false);
            result.put("message", "System error: " + e.getMessage());
        }

        return result;
    }
}
