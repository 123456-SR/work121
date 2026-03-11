package org.example.work121.service.impl;

import org.example.work121.entity.JzsSignature;
import org.example.work121.mapper.*;
import org.example.work121.service.JzsSignatureService;
import org.example.work121.service.PendingTasksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * 待处理任务服务实现类
 */
@Service
public class PendingTasksServiceImpl implements PendingTasksService {

    @Autowired
    private PendingTasksMapper pendingTasksMapper;
    
    @Autowired
    private JcCoreWtInfoMapper jcCoreWtInfoMapper;
    
    @Autowired
    private BeckmanBeamMapper beckmanBeamMapper;
    
    @Autowired
    private LightDynamicPenetrationMapper lightDynamicPenetrationMapper;
    
    @Autowired
    private ReboundMethodMapper reboundMethodMapper;
    
    @Autowired
    private CuttingRingMapper cuttingRingMapper;
    
    @Autowired
    private WaterReplacementMapper waterReplacementMapper;
    
    @Autowired
    private SandReplacementMapper sandReplacementMapper;
    
    @Autowired
    private NuclearDensityMapper nuclearDensityMapper;
    
    @Autowired
    private DensityTestMapper densityTestMapper;
    
    @Autowired
    private JzsSignatureService jzsSignatureService;

    @Override
    public List<Map<String, Object>> getAllPendingTasks(String status, String userAccount) {
        if (userAccount != null && !userAccount.trim().isEmpty()) {
            return pendingTasksMapper.getPendingTasksByUser(userAccount.trim(), status);
        }
        return pendingTasksMapper.getAllPendingTasks(status);
    }

    @Override
    public List<Map<String, Object>> searchPendingTasks(String taskType, String status, String userAccount) {
        // 现阶段 search SQL 只按 taskType 文本匹配（与历史实现保持一致），附带 status 过滤
        // userAccount 如有传入，复用 get-by-user 的 reviewer 过滤逻辑（结果再在内存中过滤类型）
        if (userAccount != null && !userAccount.trim().isEmpty()) {
            List<Map<String, Object>> base = pendingTasksMapper.getPendingTasksByUser(userAccount.trim(), status);
            if (taskType == null || taskType.trim().isEmpty()) return base;
            String kw = taskType.trim();
            return base.stream()
                    .filter(m -> {
                        Object t = m.get("table_type");
                        return t != null && String.valueOf(t).contains(kw);
                    })
                    .collect(java.util.stream.Collectors.toList());
        }
        return pendingTasksMapper.searchPendingTasks(taskType, status);
    }

    @Override
    public List<Map<String, Object>> getPendingTasksByUser(String userAccount, String status) {
        System.out.println("PendingTasksServiceImpl.getPendingTasksByUser: userAccount=" + userAccount + ", status=" + status);
        List<Map<String, Object>> result = pendingTasksMapper.getPendingTasksByUser(userAccount, status);
        System.out.println("PendingTasksServiceImpl.getPendingTasksByUser: 返回 " + result.size() + " 个任务");
        return result;
    }

    @Override
    public boolean approveTask(String taskType, String taskId, String userAccount) {
        try {
            String reviewSignPhoto = null;
            
            if (userAccount != null && !userAccount.isEmpty()) {
                JzsSignature signature = jzsSignatureService.getSignatureByUserAccount(userAccount);
                if (signature != null && signature.getSignatureBlob() != null) {
                    reviewSignPhoto = Base64.getEncoder().encodeToString(signature.getSignatureBlob());
                }
            }
            
            switch (taskType) {
                case "委托单":
                    if (reviewSignPhoto != null) {
                        return jcCoreWtInfoMapper.updateStatusAndReviewSign(taskId, "5", reviewSignPhoto) > 0;
                    }
                    return jcCoreWtInfoMapper.updateStatusById(taskId, "5") > 0;
                case "贝克曼梁":
                    if (reviewSignPhoto != null) {
                        return beckmanBeamMapper.updateStatusAndReviewSign(taskId, "5", reviewSignPhoto) > 0;
                    }
                    return beckmanBeamMapper.updateStatusById(taskId, "5") > 0;
                case "轻型动力触探":
                    if (reviewSignPhoto != null) {
                        return lightDynamicPenetrationMapper.updateStatusAndReviewSign(taskId, "5", reviewSignPhoto) > 0;
                    }
                    return lightDynamicPenetrationMapper.updateStatusById(taskId, "5") > 0;
                case "回弹法":
                    if (reviewSignPhoto != null) {
                        return reboundMethodMapper.updateStatusAndReviewSign(taskId, "5", reviewSignPhoto) > 0;
                    }
                    return reboundMethodMapper.updateStatusById(taskId, "5") > 0;
                case "环刀法":
                    if (reviewSignPhoto != null) {
                        return cuttingRingMapper.updateStatusAndReviewSign(taskId, "5", reviewSignPhoto) > 0;
                    }
                    return cuttingRingMapper.updateStatusById(taskId, "5") > 0;
                case "灌水法":
                    if (reviewSignPhoto != null) {
                        return waterReplacementMapper.updateStatusAndReviewSign(taskId, "5", reviewSignPhoto) > 0;
                    }
                    return waterReplacementMapper.updateStatusById(taskId, "5") > 0;
                case "灌砂法":
                    if (reviewSignPhoto != null) {
                        return sandReplacementMapper.updateStatusAndReviewSign(taskId, "5", reviewSignPhoto) > 0;
                    }
                    return sandReplacementMapper.updateStatusById(taskId, "5") > 0;
                case "核子密度":
                    if (reviewSignPhoto != null) {
                        return nuclearDensityMapper.updateStatusAndReviewSign(taskId, "5", reviewSignPhoto) > 0;
                    }
                    return nuclearDensityMapper.updateStatusById(taskId, "5") > 0;
                case "密度试验":
                    if (reviewSignPhoto != null) {
                        return densityTestMapper.updateStatusAndReviewSign(taskId, "5", reviewSignPhoto) > 0;
                    }
                    return densityTestMapper.updateStatusById(taskId, "5") > 0;
                default:
                    return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}