package org.example.work121.service.impl;

import org.example.work121.dto.WorkflowRequest;
import org.example.work121.entity.*;
import org.example.work121.mapper.*;
import org.example.work121.service.BeckmanBeamService;
import org.example.work121.service.WorkflowService;
import org.example.work121.service.WaterReplacementService;
import org.example.work121.service.CuttingRingService;
import org.example.work121.service.SandReplacementService;
import org.example.work121.service.NuclearDensityService;
import org.example.work121.service.LightDynamicPenetrationService;
import org.example.work121.service.DensityTestService;
import org.example.work121.service.ReboundMethodService;
import org.example.work121.service.SimpleDirectoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class WorkflowServiceImpl implements WorkflowService {

    @Autowired private JcCoreWtInfoMapper jcCoreWtInfoMapper;
    @Autowired private SimpleDirectoryService simpleDirectoryService;
    @Autowired private DensityTestMapper densityTestMapper;
    @Autowired private ReboundMethodMapper reboundMethodMapper;
    @Autowired private SandReplacementMapper sandReplacementMapper;
    @Autowired private WaterReplacementMapper waterReplacementMapper;
    @Autowired private NuclearDensityMapper nuclearDensityMapper;
    @Autowired private CuttingRingMapper cuttingRingMapper;
    @Autowired private BeckmanBeamMapper beckmanBeamMapper;
    @Autowired private LightDynamicPenetrationMapper lightDynamicPenetrationMapper;

    @Autowired private BeckmanBeamService beckmanBeamService;
    @Autowired private WaterReplacementService waterReplacementService;
    @Autowired private CuttingRingService cuttingRingService;
    @Autowired private SandReplacementService sandReplacementService;
    @Autowired private NuclearDensityService nuclearDensityService;
    @Autowired private LightDynamicPenetrationService lightDynamicPenetrationService;
    @Autowired private DensityTestService densityTestService;
    @Autowired private ReboundMethodService reboundMethodService;

    // 状态常量
    private static final String STATUS_DRAFT = "0";
    private static final String STATUS_PENDING_AUDIT = "1";
    private static final String STATUS_RETURNED = "2";
    private static final String STATUS_PENDING_SIGN = "3";
    private static final String STATUS_PENDING_APPROVAL = "4";
    private static final String STATUS_APPROVED = "5";

    @Override
    @Transactional
    public boolean handleWorkflow(WorkflowRequest request) {
        String tableType = request.getTableType();
        String recordId = request.getRecordId();

        try {
            switch (tableType) {
                case "ENTRUSTMENT":
                    return handleEntrustment(recordId, request);
                case "DENSITY_TEST":
                    return handleDensityTest(recordId, request);
                case "REBOUND_METHOD":
                    return handleReboundMethod(recordId, request);
                case "SAND_REPLACEMENT":
                    return handleSandReplacement(recordId, request);
                case "WATER_REPLACEMENT":
                    return handleWaterReplacement(recordId, request);
                case "NUCLEAR_DENSITY":
                    return handleNuclearDensity(recordId, request);
                case "CUTTING_RING":
                    return handleCuttingRing(recordId, request);
                case "BECKMAN_BEAM":
                    return handleBeckmanBeam(recordId, request);
                case "LIGHT_DYNAMIC_PENETRATION":
                    return handleLightDynamicPenetration(recordId, request);
                default:
                    System.err.println("Unsupported table type: " + tableType);
                    return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean handleEntrustment(String id, WorkflowRequest request) {
        JcCoreWtInfo entity = jcCoreWtInfoMapper.selectById(id);
        if (entity == null) return false;
        
        applyChanges(entity, request);
        // Update both tables to ensure status consistency
        int extResult = jcCoreWtInfoMapper.updateExt(entity);
        
        // Map status to sampleStatus for legacy support if needed
        if (entity.getStatus() != null) {
            // Check if we need to map integer status to string WT_STATUS?
            // Assuming direct mapping for now as both seem to use int/string codes
            // In JcCoreWtInfoMapper.xml/annotations, WT_STATUS is mapped to sampleStatus
            // But applyChanges sets 'status' field.
            // We should sync them.
             // entity.setSampleStatus(String.valueOf(entity.getStatus())); // If types differ
        }
        
        // Also update the core table (JC_CORE_WT_INFO)
        // jcCoreWtInfoMapper.update(entity); // Optional: if you want to sync changes to old table
        
        // Sync data to other tables if Entrustment is approved or passes audit (Pending Sign)
        if (entity.getStatus() != null && (entity.getStatus() == STATUS_APPROVED || entity.getStatus() == STATUS_PENDING_SIGN)) {
            simpleDirectoryService.syncEntrustmentDataByWtNum(entity.getWtNum());
        }

        return extResult > 0;
    }

    private boolean handleDensityTest(String id, WorkflowRequest request) {
        DensityTest entity = densityTestMapper.selectById(id);
        if (entity == null) return false;

        applyChanges(entity, request);
        boolean success = densityTestMapper.updateById(entity) > 0;

        if (success && entity.getStatus() == STATUS_APPROVED) {
            densityTestService.generateReportAndResult(entity.getEntrustmentId());
        }
        return success;
    }

    private boolean handleReboundMethod(String id, WorkflowRequest request) {
        ReboundMethod entity = reboundMethodMapper.selectById(id);
        if (entity == null) return false;
        
        applyChanges(entity, request);
        boolean success = reboundMethodMapper.updateById(entity) > 0;

        if (success && entity.getStatus() == STATUS_APPROVED) {
            reboundMethodService.generateReportAndResult(entity.getEntrustmentId());
        }
        return success;
    }

    private boolean handleSandReplacement(String id, WorkflowRequest request) {
        SandReplacement entity = sandReplacementMapper.selectById(id);
        if (entity == null) return false;
        applyChanges(entity, request);
        boolean success = sandReplacementMapper.update(entity) > 0;

        if (success && entity.getStatus() == STATUS_APPROVED) {
            sandReplacementService.generateReportAndResult(entity.getEntrustmentId());
        }
        return success;
    }

    private boolean handleWaterReplacement(String id, WorkflowRequest request) {
        WaterReplacement entity = waterReplacementMapper.selectById(id);
        if (entity == null) return false;
        
        applyChanges(entity, request);
        boolean success = waterReplacementMapper.updateById(entity) > 0;
        
        if (success && entity.getStatus() == STATUS_APPROVED) {
            waterReplacementService.generateReportAndResult(entity.getEntrustmentId());
        }
        return success;
    }

    private boolean handleNuclearDensity(String id, WorkflowRequest request) {
        NuclearDensity entity = nuclearDensityMapper.selectById(id);
        if (entity == null) return false;
        applyChanges(entity, request);
        boolean success = nuclearDensityMapper.updateById(entity) > 0;

        if (success && entity.getStatus() == STATUS_APPROVED) {
            nuclearDensityService.generateReportAndResult(entity.getEntrustmentId());
        }
        return success;
    }

    private boolean handleCuttingRing(String id, WorkflowRequest request) {
        CuttingRing entity = cuttingRingMapper.selectById(id);
        if (entity == null) return false;
        applyChanges(entity, request);
        boolean success = cuttingRingMapper.updateById(entity) > 0;

        if (success && entity.getStatus() == STATUS_APPROVED) {
            cuttingRingService.generateReportAndResult(entity.getEntrustmentId());
        }
        return success;
    }

    private boolean handleBeckmanBeam(String id, WorkflowRequest request) {
        BeckmanBeam entity = beckmanBeamMapper.selectById(id);
        if (entity == null) return false;
        
        applyChanges(entity, request);
        boolean success = beckmanBeamMapper.updateById(entity) > 0;
        
        if (success && entity.getStatus() == STATUS_APPROVED) {
            beckmanBeamService.generateReportAndResult(entity.getEntrustmentId());
        }
        return success;
    }

    private boolean handleLightDynamicPenetration(String id, WorkflowRequest request) {
        LightDynamicPenetration entity = lightDynamicPenetrationMapper.selectById(id);
        if (entity == null) return false;
        applyChanges(entity, request);
        boolean success = lightDynamicPenetrationMapper.update(entity) > 0;

        if (success && entity.getStatus() == STATUS_APPROVED) {
            lightDynamicPenetrationService.generateReportAndResult(entity.getEntrustmentId());
        }
        return success;
    }

    private void applyChanges(BusinessEntity entity, WorkflowRequest request) {
        String action = request.getAction();
        String signature = request.getSignatureData();
        String nextHandler = request.getNextHandler();
        String rejectReason = request.getRejectReason();
        String user = request.getUserAccount();

        entity.setUpdateBy(user);
        entity.setUpdateTime(new Date());

        // Special handling for Entrustment (JcCoreWtInfo):
        // SUBMIT action from Undertaker should go to AUDIT.
        // The default switch case handles this correctly now (SUBMIT -> PENDING_AUDIT)
        /*
        if (entity instanceof JcCoreWtInfo) {
            if ("SUBMIT".equals(action)) {
                entity.setStatus(STATUS_APPROVED); // Directly set to Approved (5)
                if (signature != null) {
                    entity.setInspectSignaturePhoto(signature); // Use Tester/Undertaker signature slot
                    entity.setTester(user);
                }
                entity.setNextHandler(null); // No further steps
                return;
            }
        }
        */

        // Standard workflow for Test Records
        switch (action) {
            case "SUBMIT": // 提交 -> 待审核
                entity.setStatus(STATUS_PENDING_AUDIT);
                if (signature != null) {
                    entity.setInspectSignaturePhoto(signature);
                    entity.setTester(user); // Capture Tester Name
                }
                if (entity.getFiller() == null || entity.getFiller().isEmpty()) {
                    entity.setFiller(user);
                }
                if (entity.getRecordTester() == null || entity.getRecordTester().isEmpty()) {
                    entity.setRecordTester(user);
                }
                entity.setNextHandler(nextHandler);
                break;
            
            case "AUDIT_PASS": // 审核通过 -> 完成 (复核人审核通过，无签字)
                entity.setStatus(STATUS_APPROVED);
                entity.setReviewer(user); // Capture Reviewer Name
                if (entity.getRecordReviewer() == null || entity.getRecordReviewer().isEmpty()) {
                    entity.setRecordReviewer(user);
                }
                entity.setNextHandler(null);
                break;

            case "SIGN_REVIEW": // Deprecated or mapped to APPROVED
                entity.setStatus(STATUS_APPROVED);
                if (signature != null) {
                    entity.setReviewSignaturePhoto(signature);
                    entity.setReviewer(user);
                }
                if (entity.getRecordReviewer() == null || entity.getRecordReviewer().isEmpty()) {
                    entity.setRecordReviewer(user);
                }
                entity.setNextHandler(null);
                break;

            case "SIGN_APPROVE": // 批准 -> 完成
                entity.setStatus(STATUS_APPROVED);
                if (signature != null) {
                    entity.setApproveSignaturePhoto(signature);
                    entity.setApprover(user); // Capture Approver Name
                }
                entity.setNextHandler(null); // Process end
                break;

            case "REJECT": // 打回
                entity.setStatus(STATUS_RETURNED);
                entity.setRejectReason(rejectReason);
                entity.setNextHandler(null); // Or back to creator
                break;
                
            // Backward compatibility for old "SIGN" action if needed
            case "SIGN": 
                entity.setStatus(STATUS_PENDING_APPROVAL);
                if (signature != null) {
                    entity.setReviewSignaturePhoto(signature);
                    entity.setReviewer(user);
                }
                if (entity.getRecordReviewer() == null || entity.getRecordReviewer().isEmpty()) {
                    entity.setRecordReviewer(user);
                }
                entity.setNextHandler(nextHandler);
                break;
                
            // Backward compatibility for old "APPROVE" action
            case "APPROVE":
                entity.setStatus(STATUS_APPROVED);
                if (signature != null) {
                    entity.setApproveSignaturePhoto(signature);
                    entity.setApprover(user);
                }
                entity.setNextHandler(null);
                break;
        }
    }
}
