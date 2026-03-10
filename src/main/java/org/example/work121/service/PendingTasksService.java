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

    /**
     * 获取当前用户的待审核任务列表
     * @param userAccount 当前用户账号
     * @return 待审核任务列表
     */
    List<Map<String, Object>> getPendingTasksByUser(String userAccount);

    /**
     * 审核通过任务
     * @param taskType 任务类型
     * @param taskId 任务ID
     * @param userAccount 用户账号（用于获取签名）
     * @return 是否审核成功
     */
    boolean approveTask(String taskType, String taskId, String userAccount);
}