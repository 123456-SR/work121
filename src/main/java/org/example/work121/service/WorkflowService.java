package org.example.work121.service;

import org.example.work121.dto.WorkflowRequest;

public interface WorkflowService {
    
    /**
     * 处理工作流请求
     * @param request 请求参数
     * @return 是否成功
     */
    boolean handleWorkflow(WorkflowRequest request);
}
