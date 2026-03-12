package org.example.work121.controller;

import org.example.work121.service.PendingTasksService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/pending-tasks")
@CrossOrigin(origins = "*")
public class PendingTasksController {

    private static final Logger logger = LoggerFactory.getLogger(PendingTasksController.class);

    @Autowired
    private PendingTasksService pendingTasksService;

    /**
     * 获取待审核任务列表
     * 支持按状态过滤：0=待提交，1=待审核（默认）
     * @param taskStatus 状态值（0/1）；为空则默认 1
     * @param userAccount 可选：用户账号（用于按 reviewer 过滤）
     * @return 响应结果
     */
    @GetMapping("/get-all")
    public Map<String, Object> getAllPendingTasks(@RequestParam(required = false) String taskStatus,
                                                  @RequestParam(required = false) String userAccount) {
        Map<String, Object> result = new HashMap<>();
        try {
            String status = normalizeStatus(taskStatus);
            logger.info("获取待处理任务列表，status: {}, userAccount: {}", status, userAccount);
            java.util.List<Map<String, Object>> tasks = pendingTasksService.getAllPendingTasks(status, userAccount);
            result.put("success", true);
            result.put("data", tasks);
        } catch (Exception e) {
            logger.error("获取待审核任务列表失败", e);
            result.put("success", false);
            result.put("message", "获取待审核任务列表失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 根据任务类型搜索待审核任务
     * @param taskType 任务类型
     * @param taskStatus 状态（0/1）；为空则默认 1
     * @param userAccount 可选：用户账号（用于按 reviewer 过滤）
     * @return 响应结果
     */
    @GetMapping("/search")
    public Map<String, Object> searchPendingTasks(@RequestParam String taskType,
                                                  @RequestParam(required = false) String taskStatus,
                                                  @RequestParam(required = false) String userAccount) {
        Map<String, Object> result = new HashMap<>();
        try {
            String status = normalizeStatus(taskStatus);
            logger.info("搜索待处理任务，任务类型: {}, status: {}, userAccount: {}", taskType, status, userAccount);
            java.util.List<Map<String, Object>> tasks = pendingTasksService.searchPendingTasks(taskType, status, userAccount);
            result.put("success", true);
            result.put("data", tasks);
        } catch (Exception e) {
            logger.error("搜索待审核任务失败", e);
            result.put("success", false);
            result.put("message", "搜索待审核任务失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 审核通过任务
     * @param params 任务参数
     * @return 响应结果
     */
    @PostMapping("/approve")
    public Map<String, Object> approveTask(@RequestBody Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            String taskType = params.get("taskType");
            String taskId = params.get("taskId");
            String userAccount = params.get("userAccount");
            String taskStatus = params.get("taskStatus");
            
            // 根据任务状态显示不同的日志信息
            if ("approval".equals(taskStatus)) {
                logger.info("批准任务，任务类型: {}, 任务ID: {}, 用户账号: {}", taskType, taskId, userAccount);
            } else {
                logger.info("审核通过任务，任务类型: {}, 任务ID: {}, 用户账号: {}", taskType, taskId, userAccount);
            }
            
            boolean success = pendingTasksService.approveTask(taskType, taskId, userAccount);
            if (success) {
                result.put("success", true);
                if ("approval".equals(taskStatus)) {
                    result.put("message", "批准成功");
                } else {
                    result.put("message", "审核通过成功");
                }
            } else {
                result.put("success", false);
                if ("approval".equals(taskStatus)) {
                    result.put("message", "批准失败");
                } else {
                    result.put("message", "审核通过失败");
                }
            }
        } catch (Exception e) {
            logger.error("操作任务失败", e);
            result.put("success", false);
            result.put("message", "操作任务失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取当前用户的待审核任务列表
     * @param userAccount 当前用户账号
     * @param taskStatus 状态（0/1）；为空则默认 1
     * @return 响应结果
     */
    @GetMapping("/get-by-user")
    public Map<String, Object> getPendingTasksByUser(@RequestParam String userAccount,
                                                     @RequestParam(required = false) String taskStatus) {
        Map<String, Object> result = new HashMap<>();
        try {
            logger.info("接收到获取用户待处理任务请求，原始参数: userAccount={}, taskStatus={}", userAccount, taskStatus);
            String status = normalizeStatus(taskStatus);
            logger.info("处理后的参数: status={}", status);
            logger.info("调用 pendingTasksService.getPendingTasksByUser({}, {})", userAccount, status);
            java.util.List<Map<String, Object>> tasks = pendingTasksService.getPendingTasksByUser(userAccount, status);
            logger.info("获取到 {} 个任务", tasks.size());
            result.put("success", true);
            result.put("data", tasks);
        } catch (Exception e) {
            logger.error("获取用户待审核任务列表失败", e);
            result.put("success", false);
            result.put("message", "获取用户待审核任务列表失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 兼容前端传参：允许传 "0"/"1"/"5"，也允许传 "submit"/"audit"/"approval" 等（旧参数）。
     */
    private String normalizeStatus(String taskStatus) {
        if (taskStatus == null || taskStatus.trim().isEmpty()) {
            return "1";
        }
        String v = taskStatus.trim();
        if ("0".equals(v) || "1".equals(v) || "5".equals(v)) {
            return v;
        }
        // 兼容：submit=待提交(0)，audit=待审核(1)，approval=待批准(5)
        if ("submit".equalsIgnoreCase(v)) return "0";
        if ("audit".equalsIgnoreCase(v)) return "1";
        if ("approval".equalsIgnoreCase(v)) return "5";
        return "1";
    }
}