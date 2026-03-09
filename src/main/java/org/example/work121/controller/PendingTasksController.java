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
     * @return 响应结果
     */
    @GetMapping("/get-all")
    public Map<String, Object> getAllPendingTasks() {
        Map<String, Object> result = new HashMap<>();
        try {
            logger.info("获取待审核任务列表");
            java.util.List<Map<String, Object>> tasks = pendingTasksService.getAllPendingTasks();
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
     * @return 响应结果
     */
    @GetMapping("/search")
    public Map<String, Object> searchPendingTasks(@RequestParam String taskType) {
        Map<String, Object> result = new HashMap<>();
        try {
            logger.info("搜索待审核任务，任务类型: {}", taskType);
            java.util.List<Map<String, Object>> tasks = pendingTasksService.searchPendingTasks(taskType);
            result.put("success", true);
            result.put("data", tasks);
        } catch (Exception e) {
            logger.error("搜索待审核任务失败", e);
            result.put("success", false);
            result.put("message", "搜索待审核任务失败: " + e.getMessage());
        }
        return result;
    }
}