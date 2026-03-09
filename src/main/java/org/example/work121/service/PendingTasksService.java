package org.example.work121.service;

import java.util.List;
import java.util.Map;

/**
 * 待处理任务服务接口
 */
public interface PendingTasksService {

    /**
     * 获取所有待审核任务列表
     * @return 待审核任务列表
     */
    List<Map<String, Object>> getAllPendingTasks();

    /**
     * 根据任务类型搜索待审核任务
     * @param taskType 任务类型
     * @return 待审核任务列表
     */
    List<Map<String, Object>> searchPendingTasks(String taskType);
}