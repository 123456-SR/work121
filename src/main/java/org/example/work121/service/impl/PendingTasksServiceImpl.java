package org.example.work121.service.impl;

import org.example.work121.mapper.PendingTasksMapper;
import org.example.work121.service.PendingTasksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 待处理任务服务实现类
 */
@Service
public class PendingTasksServiceImpl implements PendingTasksService {

    @Autowired
    private PendingTasksMapper pendingTasksMapper;

    @Override
    public List<Map<String, Object>> getAllPendingTasks() {
        return pendingTasksMapper.getAllPendingTasks();
    }

    @Override
    public List<Map<String, Object>> searchPendingTasks(String taskType) {
        return pendingTasksMapper.searchPendingTasks(taskType);
    }
}