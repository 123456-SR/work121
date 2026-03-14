package org.example.work121.service.impl;

import org.example.work121.dto.WorkflowRequest;
import org.example.work121.entity.*;
import org.example.work121.mapper.*;
import org.example.work121.entity.BusinessEntity;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class WorkflowServiceImpl implements WorkflowService {

    @Autowired private JcCoreWtInfoMapper jcCoreWtInfoMapper;
    @Autowired private JdbcTemplate jdbcTemplate;
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
        String fromStatus = entity.getStatus();

        // ==== 权限校验：根据目录里配置的委托承接人 / 委托审核人限制操作人 ====
        validateEntrustmentPermission(entity, request);
        
        applyChanges(entity, request);
        int extResult = jcCoreWtInfoMapper.updateExt(entity);
        if (extResult == 0) {
            extResult = jcCoreWtInfoMapper.insertExt(entity);
        }
        
        // Map status to sampleStatus for legacy support if needed
        if (entity.getStatus() != null) {
            // 目前 sampleStatus 未参与工作流，暂不强制同步
            // entity.setSampleStatus(String.valueOf(entity.getStatus()));
        }
        
        // 目录仅作为任务分配：不再在委托单审核阶段派生/创建其它记录表，也不做跨表同步

        if (extResult > 0) {
            insertWorkflowActionLog("ENTRUSTMENT", id, entity, request, fromStatus, entity.getStatus());
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
    private void createRecordsForDirectory(SimpleDirectory directory) {
        String dirName = directory.getDirName();
        String creator = directory.getCreateBy();
        if (creator == null) {
            creator = "admin";
        }
        
        // 输出创建信息
        System.out.println("=== 开始创建记录表 ===");
        System.out.println("统一编号: " + dirName);
        System.out.println("创建人: " + creator);
        
        // 确定检测类别
        java.util.Set<String> categories = new java.util.LinkedHashSet<>();
        String[] types = {
            directory.getTable1Type(), directory.getTable2Type(), directory.getTable3Type(),
            directory.getTable4Type(), directory.getTable5Type(), directory.getTable6Type(),
            directory.getTable7Type(), directory.getTable8Type(), directory.getTable9Type(),
            directory.getTable10Type()
        };
        
        for (String type : types) {
            if (type == null) continue;
            String upper = type.toUpperCase();
            if (upper.contains("NUCLEAR")) categories.add("核子法");
            else if (upper.contains("SAND")) categories.add("灌砂法");
            else if (upper.contains("WATER")) categories.add("灌水法");
            else if (upper.contains("CUTTING")) categories.add("环刀法");
            else if (upper.contains("REBOUND")) categories.add("回弹法");
            else if (upper.contains("PENETRATION")) categories.add("轻型动力触探");
            else if (upper.contains("BECKMAN")) categories.add("贝克曼梁");
            else if (upper.contains("DENSITY")) categories.add("密度试验");
        }
        
        String category = categories.isEmpty() ? "通用检测" : String.join(",", categories);
        System.out.println("检测类别: " + category);
        
        // 创建表1
        if (directory.getTable1Type() != null && directory.getTable1Id() == null) {
            System.out.println("创建表1 - 类型: " + directory.getTable1Type());
            String table1Id = createRelatedRecord(directory.getTable1Type(), dirName, creator, category, directory);
            directory.setTable1Id(table1Id);
            System.out.println("表1创建成功，ID: " + table1Id);
        }
        
        // 创建表2
        if (directory.getTable2Type() != null && directory.getTable2Id() == null) {
            System.out.println("创建表2 - 类型: " + directory.getTable2Type());
            String table2Id = createRelatedRecord(directory.getTable2Type(), dirName, creator, category, directory);
            directory.setTable2Id(table2Id);
            System.out.println("表2创建成功，ID: " + table2Id);
        }
        
        // 创建表3
        if (directory.getTable3Type() != null && directory.getTable3Id() == null) {
            System.out.println("创建表3 - 类型: " + directory.getTable3Type());
            String table3Id = createRelatedRecord(directory.getTable3Type(), dirName, creator, category, directory);
            directory.setTable3Id(table3Id);
            System.out.println("表3创建成功，ID: " + table3Id);
        }
        
        // 创建表4
        if (directory.getTable4Type() != null && directory.getTable4Id() == null) {
            System.out.println("创建表4 - 类型: " + directory.getTable4Type());
            String table4Id = createRelatedRecord(directory.getTable4Type(), dirName, creator, category, directory);
            directory.setTable4Id(table4Id);
            System.out.println("表4创建成功，ID: " + table4Id);
        }
        
        // 创建表5
        if (directory.getTable5Type() != null && directory.getTable5Id() == null) {
            System.out.println("创建表5 - 类型: " + directory.getTable5Type());
            String table5Id = createRelatedRecord(directory.getTable5Type(), dirName, creator, category, directory);
            directory.setTable5Id(table5Id);
            System.out.println("表5创建成功，ID: " + table5Id);
        }
        
        // 创建表6
        if (directory.getTable6Type() != null && directory.getTable6Id() == null) {
            System.out.println("创建表6 - 类型: " + directory.getTable6Type());
            String table6Id = createRelatedRecord(directory.getTable6Type(), dirName, creator, category, directory);
            directory.setTable6Id(table6Id);
            System.out.println("表6创建成功，ID: " + table6Id);
        }
        
        // 创建表7
        if (directory.getTable7Type() != null && directory.getTable7Id() == null) {
            System.out.println("创建表7 - 类型: " + directory.getTable7Type());
            String table7Id = createRelatedRecord(directory.getTable7Type(), dirName, creator, category, directory);
            directory.setTable7Id(table7Id);
            System.out.println("表7创建成功，ID: " + table7Id);
        }
        
        // 创建表8
        if (directory.getTable8Type() != null && directory.getTable8Id() == null) {
            System.out.println("创建表8 - 类型: " + directory.getTable8Type());
            String table8Id = createRelatedRecord(directory.getTable8Type(), dirName, creator, category, directory);
            directory.setTable8Id(table8Id);
            System.out.println("表8创建成功，ID: " + table8Id);
        }
        
        // 创建表9
        if (directory.getTable9Type() != null && directory.getTable9Id() == null) {
            System.out.println("创建表9 - 类型: " + directory.getTable9Type());
            String table9Id = createRelatedRecord(directory.getTable9Type(), dirName, creator, category, directory);
            directory.setTable9Id(table9Id);
            System.out.println("表9创建成功，ID: " + table9Id);
        }
        
        // 创建表10
        if (directory.getTable10Type() != null && directory.getTable10Id() == null) {
            System.out.println("创建表10 - 类型: " + directory.getTable10Type());
            String table10Id = createRelatedRecord(directory.getTable10Type(), dirName, creator, category, directory);
            directory.setTable10Id(table10Id);
            System.out.println("表10创建成功，ID: " + table10Id);
        }
        
        // 更新目录
        if (directory.getId() != null) {
            simpleDirectoryMapper.update(directory);
            System.out.println("目录更新成功，ID: " + directory.getId());
        }
        
        System.out.println("=== 记录表创建完成 ===");
    }
    
    private String createRelatedRecord(String type, String dirName, String creator, String category, SimpleDirectory directory) {
        if (type == null || type.isEmpty()) return null;
        
        String id = java.util.UUID.randomUUID().toString();
        java.util.Date now = new java.util.Date();
        
        try {
            if (isTypeMatch(type, "ENTRUSTMENT_LIST", "ENTRUSTMENT", "检测委托单")) {
                // 委托单已经存在，返回现有ID
                java.util.List<JcCoreWtInfo> existingList = jcCoreWtInfoMapper.selectByWtNum(dirName);
                if (existingList != null && !existingList.isEmpty()) {
                    JcCoreWtInfo existing = existingList.get(0);
                    return existing.getId();
                }
                
                // 创建委托单（如果不存在）
                JcCoreWtInfo info = new JcCoreWtInfo();
                info.setId(id);
                info.setWtNum(dirName);
                info.setCreateBy(creator);
                info.setCreateTime(now);
                
                setDefaultValues(info, directory, category);
                
                jcCoreWtInfoMapper.insert(info);
                jcCoreWtInfoMapper.insertExt(info);

                return id;
            } else if (isTypeMatch(type, "REBOUND_METHOD_RECORD", "回弹法检测混凝土抗压强度记录表")) {
                ReboundMethod entity = new ReboundMethod();
                entity.setId(id);
                entity.setWtNum(dirName);
                entity.setCreateBy(creator);
                entity.setCreateTime(now);
                entity.setEntrustmentId(dirName);
                
                setDefaultValues(entity, directory, category);
                
                reboundMethodMapper.insert(entity);
                return id;
            } else if (isTypeMatch(type, "LIGHT_DYNAMIC_PENETRATION_RECORD", "轻型动力触探检测记录表")) {
                // 获取实际的委托单ID
                String actualEntrustmentId = dirName;
                java.util.List<JcCoreWtInfo> wtInfoList = jcCoreWtInfoMapper.selectByWtNum(dirName);
                JcCoreWtInfo wtInfo = null;
                if (wtInfoList != null && !wtInfoList.isEmpty()) {
                    wtInfo = wtInfoList.get(0);
                    actualEntrustmentId = wtInfo.getId();
                }
                
                LightDynamicPenetration entity = new LightDynamicPenetration();
                entity.setId(id);
                entity.setWtNum(dirName);
                entity.setCreateBy(creator);
                entity.setCreateTime(now);
                entity.setEntrustmentId(actualEntrustmentId);
                
                setDefaultValues(entity, directory, category);
                
                if (wtInfo != null) {
                    copyFields(wtInfo, entity);
                    populateVirtualData(entity);
                    mergeEntrustmentDataToJson(entity, wtInfo);
                } else {
                    populateVirtualData(entity);
                }
                
                lightDynamicPenetrationMapper.insert(entity);
                return id;
            } else if (isTypeMatch(type, "DENSITY_TEST_RECORD", "原位密度检测记录表")) {
                DensityTest entity = new DensityTest();
                entity.setId(id);
                entity.setWtNum(dirName);
                entity.setCreateBy(creator);
                entity.setCreateTime(now);
                entity.setEntrustmentId(dirName);
                
                setDefaultValues(entity, directory, category);
                
                densityTestMapper.insert(entity);
                return id;
            } else if (isTypeMatch(type, "NUCLEAR_DENSITY_RECORD", "原位密度检测记录表（核子法）")) {
                NuclearDensity entity = new NuclearDensity();
                entity.setId(id);
                entity.setWtNum(dirName);
                entity.setCreateBy(creator);
                entity.setCreateTime(now);
                entity.setEntrustmentId(dirName);
                
                setDefaultValues(entity, directory, category);
                
                // 尝试从委托单获取数据并填充到实体对象和 DATA_JSON
                java.util.List<JcCoreWtInfo> entrustmentList = jcCoreWtInfoMapper.selectByWtNum(dirName);
                JcCoreWtInfo entrustment = null;
                if (entrustmentList != null && !entrustmentList.isEmpty()) {
                    entrustment = entrustmentList.get(0);
                    copyFields(entrustment, entity);
                    populateVirtualData(entity);
                    mergeEntrustmentDataToJson(entity, entrustment);
                } else {
                    populateVirtualData(entity);
                }
                
                nuclearDensityMapper.insert(entity);
                return id;
            } else if (isTypeMatch(type, "SAND_REPLACEMENT_RECORD", "原位密度检测记录表（灌砂法）")) {
                SandReplacement entity = new SandReplacement();
                entity.setId(id);
                entity.setWtNum(dirName);
                entity.setCreateBy(creator);
                entity.setCreateTime(now);
                entity.setEntrustmentId(dirName);
                
                setDefaultValues(entity, directory, category);
                
                sandReplacementMapper.insert(entity);
                return id;
            } else if (isTypeMatch(type, "WATER_REPLACEMENT_RECORD", "相对密度试验记录表（灌水法）")) {
                WaterReplacement entity = new WaterReplacement();
                entity.setId(id);
                entity.setWtNum(dirName);
                entity.setCreateBy(creator);
                entity.setCreateTime(now);
                entity.setEntrustmentId(dirName);
                
                setDefaultValues(entity, directory, category);
                
                waterReplacementMapper.insert(entity);
                return id;
            } else if (isTypeMatch(type, "CUTTING_RING_RECORD", "原位密度检测记录表（环刀法）")) {
                CuttingRing entity = new CuttingRing();
                entity.setId(id);
                entity.setWtNum(dirName);
                entity.setCreateBy(creator);
                entity.setCreateTime(now);
                entity.setEntrustmentId(dirName);
                
                setDefaultValues(entity, directory, category);
                
                cuttingRingMapper.insert(entity);
                return id;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private boolean isTypeMatch(String type, String... candidates) {
        for (String candidate : candidates) {
            if (candidate.equalsIgnoreCase(type)) {
                return true;
            }
        }
        return false;
    }
    
    private void setDefaultValues(Object entity, SimpleDirectory directory, String category) {
        if (entity instanceof BusinessEntity) {
            BusinessEntity be = (BusinessEntity) entity;
            be.setProjectName("未命名工程");
            be.setClientUnit("未填写单位");
            be.setConstructionUnit("未填写单位");
            be.setBuildingUnit("未填写单位");
            be.setWitnessUnit("未填写单位");
            be.setSampleName("未填写样品");
            be.setTestCategory(category);
            be.setCommissionDate(new java.util.Date());
            be.setStatus("0"); // 默认草稿状态
            
            // 扩展虚拟数据
            be.setConstructionPart("未填写部位");
            be.setDesignUnit("未填写单位");
            be.setSupervisionUnit("未填写单位");
            be.setTestBasis("未填写依据");
            be.setEquipment("未填写设备");
            be.setTestMethod("未填写方法");
            
            // 映射角色信息
            if (directory != null) {
                if (category.contains("RECORD") || category.contains("记录表")) {
                    be.setFiller(directory.getJcTester());
                    be.setRecordTester(directory.getJcTester());
                    be.setRecordReviewer(directory.getJcReviewer());
                    
                    // 映射到标准字段
                    if (directory.getJcTester() != null) be.setTester(directory.getJcTester());
                    if (directory.getJcReviewer() != null) be.setReviewer(directory.getJcReviewer());
                    if (directory.getJcTester() != null) be.setApprover(directory.getJcTester()); // 使用 jcTester 填充 APPROVER 字段
                } else if (category.contains("REPORT") || category.contains("报告") || category.contains("RESULT") || category.contains("结果")) {
                    if (directory.getBgApprover() != null) be.setApprover(directory.getBgApprover());
                }
            }
        }
    }
    
    private void copyFields(JcCoreWtInfo source, BusinessEntity target) {
        if (source.getProjectName() != null) target.setProjectName(source.getProjectName());
        if (source.getClientUnit() != null) target.setClientUnit(source.getClientUnit());
        if (source.getWtNum() != null) target.setWtNum(source.getWtNum());
        if (source.getCommissionDate() != null) target.setCommissionDate(source.getCommissionDate());
        if (source.getConstructionUnit() != null) target.setConstructionUnit(source.getConstructionUnit());
        if (source.getBuildingUnit() != null) target.setBuildingUnit(source.getBuildingUnit());
        if (source.getSupervisionUnit() != null) target.setSupervisionUnit(source.getSupervisionUnit());
        if (source.getDesignUnit() != null) target.setDesignUnit(source.getDesignUnit());
        if (source.getWitnessUnit() != null) target.setWitnessUnit(source.getWitnessUnit());
        if (source.getWitness() != null) target.setWitness(source.getWitness());
    }
    
    private void populateVirtualData(BusinessEntity entity) {
        // 设置虚拟数据
        entity.setConstructionPart("未填写部位");
        entity.setDesignUnit("未填写单位");
        entity.setSupervisionUnit("未填写单位");
        entity.setTestBasis("未填写依据");
        entity.setEquipment("未填写设备");
        entity.setTestMethod("未填写方法");
    }
    
    private void mergeEntrustmentDataToJson(BusinessEntity entity, JcCoreWtInfo source) {
        try {
            // 暂时注释掉 JSON 相关代码，因为 BusinessEntity 可能没有这些方法
            // String dataJson = entity.getDataJson();
            // java.util.Map<String, Object> data = new java.util.HashMap<>();
            // 
            // if (dataJson != null && !dataJson.isEmpty()) {
            //     try {
            //         data = new com.fasterxml.jackson.databind.ObjectMapper().readValue(dataJson, java.util.Map.class);
            //     } catch (Exception e) {
            //         e.printStackTrace();
            //     }
            // }
            // 
            // // 合并委托单数据到 JSON
            // if (source.getProjectName() != null) data.put("projectName", source.getProjectName());
            // if (source.getClientUnit() != null) data.put("entrustingUnit", source.getClientUnit());
            // if (source.getWtNum() != null) data.put("unifiedNumber", source.getWtNum());
            // if (source.getCommissionDate() != null) {
            //     java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            //     data.put("commissionDate", sdf.format(source.getCommissionDate()));
            // }
            // if (source.getConstructionPart() != null) data.put("constructionPart", source.getConstructionPart());
            // if (source.getSampleName() != null) data.put("sampleName", source.getSampleName());
            // 
            // // 更新 DATA_JSON
            // entity.setDataJson(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void syncRolesToRecords(SimpleDirectory directory) {
        // 同步角色信息到记录表
        // 这里可以根据需要实现角色同步逻辑
    }
    
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
        String fromStatus = entity.getStatus();

        applyChanges(entity, request);
        boolean success = densityTestMapper.updateById(entity) > 0;

        if (success && shouldGenerateReportAndResultByStatus(entity.getStatus())) {
            densityTestService.generateReportAndResult(entity.getEntrustmentId());
        }
        if (success) {
            insertWorkflowActionLog("DENSITY_TEST", id, entity, request, fromStatus, entity.getStatus());
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

            if (success && shouldGenerateReportAndResultByStatus(recordEntity.getStatus())) {
                reboundMethodService.generateReportAndResult(recordEntity.getEntrustmentId());
                // 回弹法记录表审核通过时，检查贝克曼梁法记录表是否也审核通过，如果是，则触发 BeckmanBeamReport 的生成
                // （generateBeckmanBeamReportAndResult 内部会检查双检验是否都通过）
                beckmanBeamService.generateReportAndResult(recordEntity.getEntrustmentId());
            }
            if (success) {
                insertWorkflowActionLog("REBOUND_METHOD", id, recordEntity, request, oldStatus, recordEntity.getStatus());
            }
            return success;
        }

        // 如果记录表查不到，尝试用 ReboundMethodMapper 查询（兼容旧数据）
        ReboundMethod entity = reboundMethodMapper.selectById(id);
        if (entity == null) return false;
        String fromStatus = entity.getStatus();
        
        applyChanges(entity, request);
        boolean success = reboundMethodMapper.updateById(entity) > 0;

        if (success && shouldGenerateReportAndResultByStatus(entity.getStatus())) {
            reboundMethodService.generateReportAndResult(entity.getEntrustmentId());
            // 回弹法记录表审核通过时，检查贝克曼梁法记录表是否也审核通过，如果是，则触发 BeckmanBeamReport 的生成
            // （generateBeckmanBeamReportAndResult 内部会检查双检验是否都通过）
            beckmanBeamService.generateReportAndResult(entity.getEntrustmentId());
        }
        if (success) {
            insertWorkflowActionLog("REBOUND_METHOD", id, entity, request, fromStatus, entity.getStatus());
        }
        return success;
    }

    private boolean handleSandReplacement(String id, WorkflowRequest request) {
        SandReplacement entity = sandReplacementMapper.selectById(id);
        if (entity == null) return false;
        String fromStatus = entity.getStatus();
        applyChanges(entity, request);
        boolean success = sandReplacementMapper.update(entity) > 0;

        if (success && shouldGenerateReportAndResultByStatus(entity.getStatus())) {
            sandReplacementService.generateReportAndResult(entity.getEntrustmentId());
            // 密度类记录表审核通过时，顺带触发一次“原位密度检测报告/结果”的自动生成检查
            densityTestService.generateReportAndResult(entity.getEntrustmentId());
        }
        if (success) {
            insertWorkflowActionLog("SAND_REPLACEMENT", id, entity, request, fromStatus, entity.getStatus());
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
        String fromStatus = entity.getStatus();
        
        applyChanges(entity, request);
        // 工作流这里只需要更新状态/签名等流程字段，避免误改 DATA_JSON
        boolean success = waterReplacementMapper.updateWorkflowFields(entity) > 0;
        
        if (success && shouldGenerateReportAndResultByStatus(entity.getStatus())) {
            waterReplacementService.generateReportAndResult(entity.getEntrustmentId());
            // 密度类记录表审核通过时，顺带触发一次“原位密度检测报告/结果”的自动生成检查
            densityTestService.generateReportAndResult(entity.getEntrustmentId());
        }
        if (success) {
            insertWorkflowActionLog("WATER_REPLACEMENT", id, entity, request, fromStatus, entity.getStatus());
        }
        return success;
    }

    private boolean handleNuclearDensity(String id, WorkflowRequest request) {
        NuclearDensity entity = nuclearDensityMapper.selectById(id);
        if (entity == null) return false;
        String fromStatus = entity.getStatus();
        applyChanges(entity, request);
        boolean success = nuclearDensityMapper.updateById(entity) > 0;

        if (success && shouldGenerateReportAndResultByStatus(entity.getStatus())) {
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
        if (success) {
            insertWorkflowActionLog("NUCLEAR_DENSITY", id, entity, request, fromStatus, entity.getStatus());
        }
        return success;
    }

    private boolean handleCuttingRing(String id, WorkflowRequest request) {
        CuttingRing entity = cuttingRingMapper.selectById(id);
        if (entity == null) return false;
        String fromStatus = entity.getStatus();
        applyChanges(entity, request);
        boolean success = cuttingRingMapper.updateById(entity) > 0;

        if (success && shouldGenerateReportAndResultByStatus(entity.getStatus())) {
            cuttingRingService.generateReportAndResult(entity.getEntrustmentId());
            // 密度类记录表审核通过时，顺带触发一次“原位密度检测报告/结果”的自动生成检查
            densityTestService.generateReportAndResult(entity.getEntrustmentId());
        }
        if (success) {
            insertWorkflowActionLog("CUTTING_RING", id, entity, request, fromStatus, entity.getStatus());
        }
        return success;
    }

    private boolean handleBeckmanBeam(String id, WorkflowRequest request) {
        BeckmanBeam entity = beckmanBeamMapper.selectById(id);
        if (entity == null) return false;
        String fromStatus = entity.getStatus();
        
        applyChanges(entity, request);
        boolean success = beckmanBeamMapper.updateById(entity) > 0;
        
        if (success && shouldGenerateReportAndResultByStatus(entity.getStatus())) {
            beckmanBeamService.generateReportAndResult(entity.getEntrustmentId());
            // 贝克曼梁法记录表审核通过时，检查回弹法记录表是否也审核通过，如果是，则触发 BeckmanBeamReport 的生成
            // （generateBeckmanBeamReportAndResult 内部会检查双检验是否都通过）
        }
        if (success) {
            insertWorkflowActionLog("BECKMAN_BEAM", id, entity, request, fromStatus, entity.getStatus());
        }
        return success;
    }

    private boolean handleLightDynamicPenetration(String id, WorkflowRequest request) {
        LightDynamicPenetration entity = lightDynamicPenetrationMapper.selectById(id);
        if (entity == null) return false;
        String fromStatus = entity.getStatus();
        applyChanges(entity, request);
        boolean success = lightDynamicPenetrationMapper.updateById(entity) > 0;

        if (success && shouldGenerateReportAndResultByStatus(entity.getStatus())) {
            lightDynamicPenetrationService.generateReportAndResult(entity.getEntrustmentId());
        }
        if (success) {
            insertWorkflowActionLog("LIGHT_DYNAMIC_PENETRATION", id, entity, request, fromStatus, entity.getStatus());
        }
        return success;
    }

    private boolean shouldGenerateReportAndResultByStatus(String status) {
        return STATUS_PENDING_APPROVAL.equals(status) || STATUS_APPROVED.equals(status);
    }

    private void insertWorkflowActionLog(String tableType,
                                         String recordId,
                                         Object entity,
                                         WorkflowRequest request,
                                         String fromStatus,
                                         String toStatus) {
        String wtNum = resolveWtNum(entity);
        String assignee = resolveAssignee(tableType, entity, request, wtNum, toStatus);
        try {
            jdbcTemplate.update(
                    "INSERT INTO T_WORKFLOW_ACTION_LOG (ID, WT_NUM, TABLE_TYPE, RECORD_ID, ACTION, FROM_STATUS, TO_STATUS, ASSIGNEE, ACTOR, REJECT_REASON, ACTION_TIME) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    UUID.randomUUID().toString(),
                    wtNum,
                    tableType,
                    recordId,
                    request.getAction(),
                    fromStatus,
                    toStatus,
                    assignee,
                    request.getUserAccount(),
                    request.getRejectReason(),
                    new Timestamp(System.currentTimeMillis())
            );
        } catch (Exception ignored) {
        }
    }

    private String resolveWtNum(Object entity) {
        if (entity instanceof BusinessEntity) {
            String wtNum = ((BusinessEntity) entity).getWtNum();
            if (wtNum != null && !wtNum.trim().isEmpty()) return wtNum;
        }

        String entrustmentId = resolveEntrustmentId(entity);
        if (entrustmentId != null && !entrustmentId.trim().isEmpty()) {
            try {
                JcCoreWtInfo info = jcCoreWtInfoMapper.selectById(entrustmentId);
                if (info != null) {
                    String wtNum = info.getWtNum();
                    if (wtNum != null && !wtNum.trim().isEmpty()) return wtNum;
                }
            } catch (Exception ignored) {
            }
            return entrustmentId;
        }
        return null;
    }

    private String resolveEntrustmentId(Object entity) {
        if (entity == null) return null;
        try {
            java.lang.reflect.Method m = entity.getClass().getMethod("getEntrustmentId");
            Object v = m.invoke(entity);
            return v == null ? null : String.valueOf(v);
        } catch (Exception ignored) {
        }
        return null;
    }

    private String resolveAssignee(String tableType, Object entity, WorkflowRequest request, String wtNum, String toStatus) {
        String nextHandler = request.getNextHandler();
        if (nextHandler != null && !nextHandler.trim().isEmpty()) return nextHandler;

        if (entity instanceof BusinessEntity) {
            String nh = ((BusinessEntity) entity).getNextHandler();
            if (nh != null && !nh.trim().isEmpty()) return nh;
        }

        String action = request.getAction();
        if ("ENTRUSTMENT".equalsIgnoreCase(tableType) && wtNum != null && !wtNum.trim().isEmpty()) {
            try {
                SimpleDirectory directory = simpleDirectoryMapper.selectByDirName(wtNum);
                if (directory != null) {
                    if ("SUBMIT".equals(action) && directory.getWtReviewer() != null && !directory.getWtReviewer().trim().isEmpty()) {
                        return directory.getWtReviewer();
                    }
                    if ("REJECT".equals(action) && directory.getWtUndertaker() != null && !directory.getWtUndertaker().trim().isEmpty()) {
                        return directory.getWtUndertaker();
                    }
                }
            } catch (Exception ignored) {
            }
        }

        if ("SUBMIT".equals(action) && STATUS_PENDING_AUDIT.equals(toStatus) && entity instanceof BusinessEntity) {
            BusinessEntity be = (BusinessEntity) entity;
            if (be.getRecordReviewer() != null && !be.getRecordReviewer().trim().isEmpty()) return be.getRecordReviewer();
            if (be.getReviewer() != null && !be.getReviewer().trim().isEmpty()) return be.getReviewer();
            if (be.getWtReviewer() != null && !be.getWtReviewer().trim().isEmpty()) return be.getWtReviewer();
        }

        if ("REJECT".equals(action) && STATUS_RETURNED.equals(toStatus) && entity instanceof BusinessEntity) {
            BusinessEntity be = (BusinessEntity) entity;
            if (be.getFiller() != null && !be.getFiller().trim().isEmpty()) return be.getFiller();
            if (be.getRecordTester() != null && !be.getRecordTester().trim().isEmpty()) return be.getRecordTester();
            if (be.getTester() != null && !be.getTester().trim().isEmpty()) return be.getTester();
        }

        return null;
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
            
            case "AUDIT_PASS": // 审核通过 -> 待批准（记录表）；委托单直接完成
                if (entity instanceof JcCoreWtInfo) {
                    entity.setStatus(STATUS_APPROVED);
                } else {
                    entity.setStatus(STATUS_PENDING_APPROVAL);
                }
                entity.setReviewer(reviewerToUse); // 对于委托单，使用配置的 wtReviewer
                if (signature != null) {
                    entity.setReviewSignaturePhoto(signature); // Report Reviewer Sign
                    entity.setRecordReviewSign(signature);     // Record Reviewer Sign
                }
                if (entity.getRecordReviewer() == null || entity.getRecordReviewer().isEmpty()) {
                    entity.setRecordReviewer(user); // RecordReviewer 仍使用当前操作人
                }
                entity.setNextHandler(entity instanceof JcCoreWtInfo ? null : nextHandler);
                break;

            case "SIGN_REVIEW": // Deprecated or mapped to APPROVED
                if (entity instanceof JcCoreWtInfo) {
                    entity.setStatus(STATUS_APPROVED);
                } else {
                    entity.setStatus(STATUS_PENDING_APPROVAL);
                }
                if (signature != null) {
                    entity.setReviewSignaturePhoto(signature);
                    entity.setRecordReviewSign(signature); // Record Reviewer Sign
                    entity.setReviewer(reviewerToUse); // 对于委托单，使用配置的 wtReviewer
                }
                if (entity.getRecordReviewer() == null || entity.getRecordReviewer().isEmpty()) {
                    entity.setRecordReviewer(user); // RecordReviewer 仍使用当前操作人
                }
                entity.setNextHandler(entity instanceof JcCoreWtInfo ? null : nextHandler);
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
