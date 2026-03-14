package org.example.work121.service;

import java.util.List;
import java.util.Map;

/**
 * 待处理任务服务接口
 */
public interface PendingTasksService {

    /**
     * 获取所有待审核任务列表
     * @param status 状态（例如：0=待提交，1=待审核）
     * @param userAccount 可选：当前用户账号（用于按审核人/处理人过滤）
     * @return 待审核任务列表
     */
    List<Map<String, Object>> getAllPendingTasks(String status, String userAccount);

    /**
     * 根据任务类型搜索待审核任务
     * @param taskType 任务类型
     * @param status 状态（例如：0=待提交，1=待审核）
     * @param userAccount 可选：当前用户账号
     * @return 待审核任务列表
     */
    List<Map<String, Object>> searchPendingTasks(String taskType, String status, String userAccount);

    /**
     * 获取当前用户的待审核任务列表
     * @param userAccount 当前用户账号
     * @param status 状态（例如：0=待提交，1=待审核）
     * @return 待审核任务列表
     */
    List<Map<String, Object>> getPendingTasksByUser(String userAccount, String status);

    /**
     * 审核通过任务
     * @param taskType 任务类型
     * @param taskId 任务ID
     * @param userAccount 用户账号（用于获取签名）
     * @param jcTester 记录表检测人（委托单审核通过后指定）
     * @param jcReviewer 记录表审核人（委托单审核通过后指定）
     * @param bgApprover 报告/结果批准人（委托单审核通过后指定）
     * @return 是否审核成功
     */
    boolean approveTask(String taskType, String taskId, String userAccount, String jcTester, String jcReviewer, String bgApprover);
}
