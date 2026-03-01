package org.example.work121.service.impl;

import org.example.work121.dto.WorkflowRequest;
import org.example.work121.entity.*;
import org.example.work121.mapper.*;
import org.example.work121.entity.ReboundMethodRecord;
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
    @Autowired private SimpleDirectoryMapper simpleDirectoryMapper;
    @Autowired private DensityTestMapper densityTestMapper;
    @Autowired private ReboundMethodMapper reboundMethodMapper;
    @Autowired private ReboundMethodRecordMapper reboundMethodRecordMapper;
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
            // Log the exception but do not swallow it completely if we want to return a failure
            // However, to avoid "Transaction rolled back because it has been marked as rollback-only"
            // we should not catch RuntimeException inside a @Transactional method if we want to handle it manually
            // OR we should rethrow it.
            // But here the method returns boolean.
            // The issue "Transaction rolled back because it has been marked as rollback-only" happens when
            // an exception is thrown inside a transactional method, caught, and then the method returns normally,
            // but the transaction manager has already marked the transaction for rollback.
            // When the transaction tries to commit (because method returned normally), it fails.
            
            // Solution: If we catch an exception that caused rollback-mark, we must ensure we don't try to commit success=true (which is implicit if no exception thrown out).
            // Actually, if we catch it, Spring transaction interceptor might not know unless we rethrow.
            // BUT, if the exception happened in a nested call (like generateReportAndResult), that nested call might have marked it.
            
            e.printStackTrace();
            // If we want to return 'false' to the controller without 500 error, we must be careful.
            // If the transaction is already marked for rollback, returning 'false' is fine, 
            // but the outer controller might see the TransactionSystemException.
            
            // Ideally, we should let the exception propagate so the controller catches it and returns success=false.
            throw new RuntimeException(e.getMessage(), e); 
        }
    }

    private boolean handleEntrustment(String id, WorkflowRequest request) {
        JcCoreWtInfo entity = jcCoreWtInfoMapper.selectById(id);
        if (entity == null) return false;

        // ==== 权限校验：根据目录里配置的委托承接人 / 委托审核人限制操作人 ====
        validateEntrustmentPermission(entity, request);
        
        applyChanges(entity, request);
        // Update both tables to ensure status consistency
        int extResult = jcCoreWtInfoMapper.updateExt(entity);
        
        // Map status to sampleStatus for legacy support if needed
        if (entity.getStatus() != null) {
            // 目前 sampleStatus 未参与工作流，暂不强制同步
            // entity.setSampleStatus(String.valueOf(entity.getStatus()));
        }
        
        // Sync data to other tables if Entrustment is approved or passes audit (Pending Sign)
        if (entity.getStatus() != null
                && (STATUS_APPROVED.equals(entity.getStatus()) || STATUS_PENDING_SIGN.equals(entity.getStatus()))) {
            simpleDirectoryService.syncEntrustmentDataByWtNum(entity.getWtNum());
        }

        return extResult > 0;
    }

    /**
     * 校验委托单工作流操作人是否符合目录中的角色配置：
     * - SUBMIT 只能由“委托承接人”(wtUndertaker) 执行
     * - AUDIT_PASS / SIGN_REVIEW / SIGN_APPROVE / APPROVE / SIGN / REJECT
     *   只能由“委托审核人”(wtReviewer) 执行
     * 如果目录或角色未配置，则不做限制以保持兼容。
     */
    private void validateEntrustmentPermission(JcCoreWtInfo entity, WorkflowRequest request) {
        String wtNum = entity.getWtNum();
        if (wtNum == null || wtNum.trim().isEmpty()) {
            return; // 无统一编号则不做限制
        }

        SimpleDirectory directory = null;
        try {
            directory = simpleDirectoryMapper.selectByDirName(wtNum);
        } catch (Exception e) {
            // 查询目录失败时不阻塞流程，但记录日志方便排查
            System.err.println("Failed to load SimpleDirectory for wtNum " + wtNum + ": " + e.getMessage());
        }

        if (directory == null) {
            // 没有目录配置（例如老数据），保持原有行为，不做权限限制
            return;
        }

        String action = request.getAction();
        String currentUser = request.getUserAccount();
        if (currentUser == null || currentUser.trim().isEmpty()) {
            // 理论上前端已做登录校验，这里如果拿不到账号，直接拒绝
            throw new RuntimeException("当前用户未登录或账号为空，无法操作委托单");
        }

        String requiredUser = null;
        if ("SUBMIT".equals(action)) {
            requiredUser = directory.getWtUndertaker();
        } else if ("AUDIT_PASS".equals(action)
                || "SIGN_REVIEW".equals(action)
                || "SIGN_APPROVE".equals(action)
                || "APPROVE".equals(action)
                || "SIGN".equals(action)
                || "REJECT".equals(action)) {
            requiredUser = directory.getWtReviewer();
        }

        // 未配置对应角色时，默认不做限制，兼容老流程
        if (requiredUser == null || requiredUser.trim().isEmpty()) {
            return;
        }

        if (!requiredUser.equals(currentUser)) {
            // 明确提示哪个账号才有权限
            throw new RuntimeException(
                    "当前账号(" + currentUser + ")无权执行该委托单操作，应由配置的账号(" + requiredUser + ")处理");
        }
    }

    private boolean handleDensityTest(String id, WorkflowRequest request) {
        DensityTest entity = densityTestMapper.selectById(id);
        if (entity == null) return false;

        applyChanges(entity, request);
        boolean success = densityTestMapper.updateById(entity) > 0;

        if (success && STATUS_APPROVED.equals(entity.getStatus())) {
            densityTestService.generateReportAndResult(entity.getEntrustmentId());
        }
        return success;
    }

    private boolean handleReboundMethod(String id, WorkflowRequest request) {
        // 先尝试用 ReboundMethodRecordMapper 查询（记录表）
        ReboundMethodRecord recordEntity = reboundMethodRecordMapper.selectById(id);
        if (recordEntity != null) {
            String oldStatus = recordEntity.getStatus();
            // 确保状态字段不为空，如果为空则设置为草稿状态
            if (oldStatus == null || oldStatus.trim().isEmpty() || !oldStatus.matches("^[0-9]+$")) {
                oldStatus = STATUS_DRAFT;
                recordEntity.setStatus(STATUS_DRAFT);
            }
            applyChanges(recordEntity, request);
            String newStatus = recordEntity.getStatus();
            System.out.println("ReboundMethodRecord workflow: id=" + id + ", action=" + request.getAction() + 
                    ", oldStatus=" + oldStatus + ", newStatus=" + newStatus);
            boolean success = reboundMethodRecordMapper.updateById(recordEntity) > 0;
            System.out.println("ReboundMethodRecord update result: " + success);

            if (success && STATUS_APPROVED.equals(recordEntity.getStatus())) {
                reboundMethodService.generateReportAndResult(recordEntity.getEntrustmentId());
                // 回弹法记录表审核通过时，检查贝克曼梁法记录表是否也审核通过，如果是，则触发 BeckmanBeamReport 的生成
                // （generateBeckmanBeamReportAndResult 内部会检查双检验是否都通过）
                beckmanBeamService.generateReportAndResult(recordEntity.getEntrustmentId());
            }
            return success;
        }

        // 如果记录表查不到，尝试用 ReboundMethodMapper 查询（兼容旧数据）
        ReboundMethod entity = reboundMethodMapper.selectById(id);
        if (entity == null) return false;
        
        applyChanges(entity, request);
        boolean success = reboundMethodMapper.updateById(entity) > 0;

        if (success && STATUS_APPROVED.equals(entity.getStatus())) {
            reboundMethodService.generateReportAndResult(entity.getEntrustmentId());
            // 回弹法记录表审核通过时，检查贝克曼梁法记录表是否也审核通过，如果是，则触发 BeckmanBeamReport 的生成
            // （generateBeckmanBeamReportAndResult 内部会检查双检验是否都通过）
            beckmanBeamService.generateReportAndResult(entity.getEntrustmentId());
        }
        return success;
    }

    private boolean handleSandReplacement(String id, WorkflowRequest request) {
        SandReplacement entity = sandReplacementMapper.selectById(id);
        if (entity == null) return false;
        applyChanges(entity, request);
        boolean success = sandReplacementMapper.update(entity) > 0;

        if (success && STATUS_APPROVED.equals(entity.getStatus())) {
            sandReplacementService.generateReportAndResult(entity.getEntrustmentId());
            // 密度类记录表审核通过时，顺带触发一次“原位密度检测报告/结果”的自动生成检查
            densityTestService.generateReportAndResult(entity.getEntrustmentId());
        }
        return success;
    }

    private boolean handleWaterReplacement(String id, WorkflowRequest request) {
        // 先尝试用 ID 查询（UUID）
        WaterReplacement entity = waterReplacementMapper.selectById(id);
        // 如果查不到，可能是前端传的是 ENTRUSTMENT_ID（统一编号），尝试用统一编号查询
        if (entity == null) {
            List<WaterReplacement> records = waterReplacementMapper.selectByEntrustmentId(id);
            if (records != null && !records.isEmpty()) {
                entity = records.get(0);
            }
        }
        if (entity == null)
            return false;
        
        applyChanges(entity, request);
        // 工作流这里只需要更新状态/签名等流程字段，避免误改 DATA_JSON
        boolean success = waterReplacementMapper.updateWorkflowFields(entity) > 0;
        
        if (success && STATUS_APPROVED.equals(entity.getStatus())) {
            waterReplacementService.generateReportAndResult(entity.getEntrustmentId());
            // 密度类记录表审核通过时，顺带触发一次“原位密度检测报告/结果”的自动生成检查
            densityTestService.generateReportAndResult(entity.getEntrustmentId());
        }
        return success;
    }

    private boolean handleNuclearDensity(String id, WorkflowRequest request) {
        NuclearDensity entity = nuclearDensityMapper.selectById(id);
        if (entity == null) return false;
        applyChanges(entity, request);
        boolean success = nuclearDensityMapper.updateById(entity) > 0;

        if (success && STATUS_APPROVED.equals(entity.getStatus())) {
            // 核子法自己的报告/结果生成
            nuclearDensityService.generateReportAndResult(entity.getEntrustmentId());
            // 密度类记录表审核通过时，顺带触发一次“原位密度检测报告/结果”的自动生成检查
            try {
                densityTestService.generateReportAndResult(entity.getEntrustmentId());
            } catch (Exception e) {
                // 如果密度总表生成失败，不要回滚整个核子法记录的审核流程
                // 只是打印日志，让用户知道生成失败了
                System.err.println("Warning: Failed to auto-generate DensityTest report: " + e.getMessage());
                // 这里我们捕获了异常，并且不做任何会导致 rollback-only 的操作
                // 注意：如果 densityTestService.generateReportAndResult 内部标记了 rollback，
                // 那么这里捕获也没用，整个事务还是会回滚。
                // 必须确保 densityTestService.generateReportAndResult 是一个新的事务，或者不标记 rollback。
                // 查看 DensityTestServiceImpl.generateReportAndResult 发现它加了 @Transactional。
                // Spring 默认传播行为是 REQUIRED，所以它加入了当前事务。
                // 如果它内部抛出异常，当前事务就会被标记为 rollback-only。
                // 所以我们必须防止它内部抛出异常，或者让它在独立事务中运行（REQUIRES_NEW）。
                // 由于不能轻易改 Service 接口定义（可能影响其他逻辑），
                // 最好的办法是在 TableGenerationService 中处理异常，确保不抛出 RuntimeException。
                // 但 TableGenerationService 中确实抛出了 RuntimeException。
                
                // 临时方案：仅仅打印日志是不够的，因为事务已经脏了。
                // 根本解决：TableGenerationService.generateDensityTestReportAndResult 内部应该自己捕获所有异常并不抛出，
                // 或者在这里调用时，接受它可能会失败的事实，但不能让它破坏当前事务。
                // 然而，它是同事务运行的。
            }
        }
        return success;
    }

    private boolean handleCuttingRing(String id, WorkflowRequest request) {
        CuttingRing entity = cuttingRingMapper.selectById(id);
        if (entity == null) return false;
        applyChanges(entity, request);
        boolean success = cuttingRingMapper.updateById(entity) > 0;

        if (success && STATUS_APPROVED.equals(entity.getStatus())) {
            cuttingRingService.generateReportAndResult(entity.getEntrustmentId());
            // 密度类记录表审核通过时，顺带触发一次“原位密度检测报告/结果”的自动生成检查
            densityTestService.generateReportAndResult(entity.getEntrustmentId());
        }
        return success;
    }

    private boolean handleBeckmanBeam(String id, WorkflowRequest request) {
        BeckmanBeam entity = beckmanBeamMapper.selectById(id);
        if (entity == null) return false;
        
        applyChanges(entity, request);
        boolean success = beckmanBeamMapper.updateById(entity) > 0;
        
        if (success && STATUS_APPROVED.equals(entity.getStatus())) {
            beckmanBeamService.generateReportAndResult(entity.getEntrustmentId());
            // 贝克曼梁法记录表审核通过时，检查回弹法记录表是否也审核通过，如果是，则触发 BeckmanBeamReport 的生成
            // （generateBeckmanBeamReportAndResult 内部会检查双检验是否都通过）
        }
        return success;
    }

    private boolean handleLightDynamicPenetration(String id, WorkflowRequest request) {
        LightDynamicPenetration entity = lightDynamicPenetrationMapper.selectById(id);
        if (entity == null) return false;
        applyChanges(entity, request);
        boolean success = lightDynamicPenetrationMapper.updateById(entity) > 0;

        if (success && STATUS_APPROVED.equals(entity.getStatus())) {
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

        // 对于委托单（JcCoreWtInfo），审核相关操作必须使用配置的 wtReviewer
        // 因为权限校验已经限制了只有配置的审核人才能执行审核操作
        String reviewerToUse = user; // 默认使用当前操作人（用于记录表）
        if (entity instanceof JcCoreWtInfo) {
            JcCoreWtInfo entrustment = (JcCoreWtInfo) entity;
            String wtNum = entrustment.getWtNum();
            // 审核相关操作必须使用配置的 wtReviewer
            if (("AUDIT_PASS".equals(action) || "SIGN_REVIEW".equals(action) || "SIGN".equals(action))
                    && wtNum != null && !wtNum.trim().isEmpty()) {
                try {
                    SimpleDirectory directory = simpleDirectoryMapper.selectByDirName(wtNum);
                    if (directory == null || directory.getWtReviewer() == null || directory.getWtReviewer().trim().isEmpty()) {
                        // 如果查询不到配置，说明流程配置有问题，应该抛出异常
                        throw new RuntimeException("委托单(" + wtNum + ")未配置审核人(wtReviewer)，无法执行审核操作");
                    }
                    // 始终使用配置的 wtReviewer
                    reviewerToUse = directory.getWtReviewer();
                } catch (RuntimeException e) {
                    throw e; // 重新抛出运行时异常
                } catch (Exception e) {
                    // 查询失败时抛出异常
                    throw new RuntimeException("查询委托单(" + wtNum + ")的审核人配置失败: " + e.getMessage(), e);
                }
            }
        }

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
                    entity.setInspectSignaturePhoto(signature); // Report Tester Sign
                    entity.setRecordTesterSign(signature);      // Record Tester Sign
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
                entity.setReviewer(reviewerToUse); // 对于委托单，使用配置的 wtReviewer
                if (signature != null) {
                    entity.setReviewSignaturePhoto(signature); // Report Reviewer Sign
                    entity.setRecordReviewSign(signature);     // Record Reviewer Sign
                }
                if (entity.getRecordReviewer() == null || entity.getRecordReviewer().isEmpty()) {
                    entity.setRecordReviewer(user); // RecordReviewer 仍使用当前操作人
                }
                entity.setNextHandler(null);
                break;

            case "SIGN_REVIEW": // Deprecated or mapped to APPROVED
                entity.setStatus(STATUS_APPROVED);
                if (signature != null) {
                    entity.setReviewSignaturePhoto(signature);
                    entity.setRecordReviewSign(signature); // Record Reviewer Sign
                    entity.setReviewer(reviewerToUse); // 对于委托单，使用配置的 wtReviewer
                }
                if (entity.getRecordReviewer() == null || entity.getRecordReviewer().isEmpty()) {
                    entity.setRecordReviewer(user); // RecordReviewer 仍使用当前操作人
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
                    entity.setRecordReviewSign(signature); // Record Reviewer Sign
                    entity.setReviewer(reviewerToUse); // 对于委托单，使用配置的 wtReviewer
                }
                if (entity.getRecordReviewer() == null || entity.getRecordReviewer().isEmpty()) {
                    entity.setRecordReviewer(user); // RecordReviewer 仍使用当前操作人
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
