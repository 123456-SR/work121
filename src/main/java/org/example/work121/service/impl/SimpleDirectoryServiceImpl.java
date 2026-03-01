package org.example.work121.service.impl;

import org.example.work121.entity.*;
import org.example.work121.mapper.*;
import org.example.work121.service.SimpleDirectoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

/**
 * 极简目录表服务实现类
 */
@Service
public class SimpleDirectoryServiceImpl implements SimpleDirectoryService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private SimpleDirectoryMapper simpleDirectoryMapper;

    @Autowired
    private org.example.work121.service.JcCoreWtInfoService jcCoreWtInfoService;

    @Autowired
    private JcCoreWtInfoMapper jcCoreWtInfoMapper;

    @Autowired
    private DensityTestRecordMapper densityTestRecordMapper;

    @Autowired
    private ReboundMethodMapper reboundMethodMapper;

    @Autowired
    private SandReplacementMapper sandReplacementMapper;

    @Autowired
    private LightDynamicPenetrationMapper lightDynamicPenetrationMapper;

    @Autowired
    private BeckmanBeamMapper beckmanBeamMapper;

    @Autowired
    private CuttingRingMapper cuttingRingMapper;

    @Autowired
    private NuclearDensityMapper nuclearDensityMapper;

    @Autowired
    private WaterReplacementMapper waterReplacementMapper;

    @Autowired
    private DensityTestReportMapper densityTestReportMapper;

    @Autowired
    private ReboundMethodRecordMapper reboundMethodRecordMapper;

    @Autowired
    private ReboundMethodReportMapper reboundMethodReportMapper;

    @Autowired
    private LightDynamicPenetrationRecordMapper lightDynamicPenetrationRecordMapper;

    @Autowired
    private LightDynamicPenetrationReportMapper lightDynamicPenetrationReportMapper;

    @Autowired
    private BeckmanBeamRecordMapper beckmanBeamRecordMapper;

    @Autowired
    private BeckmanBeamReportMapper beckmanBeamReportMapper;

    @Autowired
    private CuttingRingRecordMapper cuttingRingRecordMapper;

    @Autowired
    private NuclearDensityRecordMapper nuclearDensityRecordMapper;

    @Autowired
    private WaterReplacementRecordMapper waterReplacementRecordMapper;

    @Autowired
    private SandReplacementRecordMapper sandReplacementRecordMapper;

    @Autowired
    private DensityTestResultMapper densityTestResultMapper;

    @Autowired
    private LightDynamicPenetrationResultMapper lightDynamicPenetrationResultMapper;

    @Autowired
    private BeckmanBeamResultMapper beckmanBeamResultMapper;

    @Autowired
    private ReboundMethodResultMapper reboundMethodResultMapper;

    @Autowired
    private CuttingRingReportMapper cuttingRingReportMapper;

    @Autowired
    private CuttingRingResultMapper cuttingRingResultMapper;

    @Autowired
    private NuclearDensityReportMapper nuclearDensityReportMapper;

    @Autowired
    private NuclearDensityResultMapper nuclearDensityResultMapper;

    @Autowired
    private WaterReplacementReportMapper waterReplacementReportMapper;

    @Autowired
    private WaterReplacementResultMapper waterReplacementResultMapper;

    @Autowired
    private SandReplacementReportMapper sandReplacementReportMapper;

    @Autowired
    private SandReplacementResultMapper sandReplacementResultMapper;

    @Override
    public boolean saveDirectory(SimpleDirectory directory) {
        try {
            boolean isNew = false;
            // 检查是否已存在该目录
            SimpleDirectory existingRecord = null;
            if (directory.getDirId() != null) {
                existingRecord = simpleDirectoryMapper.selectByDirId(directory.getDirId());
            }

            int result;
            if (existingRecord != null) {
                // 更新现有记录
                directory.setId(existingRecord.getId()); // Ensure ID is set for update
                // 保留创建信息
                directory.setCreateBy(existingRecord.getCreateBy());
                directory.setCreateTime(existingRecord.getCreateTime());
                
                // 设置更新信息
                directory.setUpdateBy(directory.getUpdateBy() != null ? directory.getUpdateBy() : "admin"); // Or get from context
                directory.setUpdateTime(new java.util.Date());
                
                result = simpleDirectoryMapper.update(directory);
            } else {
                isNew = true;
                // 插入新记录
                if (directory.getId() == null || directory.getId().isEmpty()) {
                    directory.setId(UUID.randomUUID().toString());
                }
                if (directory.getDirId() == null || directory.getDirId().isEmpty()) {
                    directory.setDirId(UUID.randomUUID().toString());
                }
                
                // 设置创建信息
                if (directory.getCreateBy() == null) {
                    directory.setCreateBy("admin");
                }
                if (directory.getCreateTime() == null) {
                    directory.setCreateTime(new java.util.Date());
                }
                if (directory.getStatus() == null) {
                    directory.setStatus("1");
                }

                // 自动创建关联表记录
                createAndLinkRecords(directory);

                result = simpleDirectoryMapper.insert(directory);
            }

            if (result > 0) {
                // 同步委托单数据到关联表
                syncEntrustmentData(directory);
                // 同步角色信息到所有关联表
                syncRoles(directory);
                return true;
            }
            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void createAndLinkRecords(SimpleDirectory directory) {
        String dirName = directory.getDirName();
        String creator = directory.getCreateBy();
        String category = determineAllTestCategories(directory);
        
        // Ensure Master Record exists (for list visibility)
        ensureMasterRecord(dirName, creator, category, directory);
        
        directory.setTable1Id(createRelatedRecord(directory.getTable1Type(), dirName, creator, category, directory));
        directory.setTable2Id(createRelatedRecord(directory.getTable2Type(), dirName, creator, category, directory));
        directory.setTable3Id(createRelatedRecord(directory.getTable3Type(), dirName, creator, category, directory));
        directory.setTable4Id(createRelatedRecord(directory.getTable4Type(), dirName, creator, category, directory));
        directory.setTable5Id(createRelatedRecord(directory.getTable5Type(), dirName, creator, category, directory));
        directory.setTable6Id(createRelatedRecord(directory.getTable6Type(), dirName, creator, category, directory));
        directory.setTable7Id(createRelatedRecord(directory.getTable7Type(), dirName, creator, category, directory));
        directory.setTable8Id(createRelatedRecord(directory.getTable8Type(), dirName, creator, category, directory));
        directory.setTable9Id(createRelatedRecord(directory.getTable9Type(), dirName, creator, category, directory));
        directory.setTable10Id(createRelatedRecord(directory.getTable10Type(), dirName, creator, category, directory));
    }

    private String determineAllTestCategories(SimpleDirectory directory) {
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
        
        if (categories.isEmpty()) return "通用检测";
        return String.join(",", categories);
    }

    private void ensureMasterRecord(String wtNum, String creator, String category, SimpleDirectory directory) {
        try {
            JcCoreWtInfo existing = jcCoreWtInfoService.getByWtNum(wtNum);
            if (existing == null) {
                JcCoreWtInfo info = new JcCoreWtInfo();
                info.setId(UUID.randomUUID().toString());
                info.setWtNum(wtNum);
                info.setCreateBy(creator);
                info.setCreateTime(new java.util.Date());
                
                setDefaultValues(info, directory, category);
                
                boolean saved = false;
                try {
                    jcCoreWtInfoService.save(info);
                    saved = true;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                
                if (!saved) {
                    try {
                        jcCoreWtInfoMapper.insertExt(info);
                    } catch (Exception ex2) {
                        ex2.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String determineTestCategory(SimpleDirectory directory) {
        return determineAllTestCategories(directory);
    }

    private void setDefaultValues(BusinessEntity entity, SimpleDirectory directory, String category) {
        entity.setProjectName("未命名工程");
        entity.setClientUnit("未填写单位");
        entity.setConstructionUnit("未填写单位");
        entity.setBuildingUnit("未填写单位");
        entity.setWitnessUnit("未填写单位");
        entity.setSampleName("未填写样品");
        entity.setTestCategory(category);
        entity.setCommissionDate(new java.util.Date());
        entity.setStatus("0"); // Default to Draft (0) so it must be Submitted (with signature) to reach Audit (1)
        
        // Expanded virtual data
        entity.setConstructionPart("未填写部位");
        entity.setDesignUnit("未填写单位");
        entity.setSupervisionUnit("未填写单位");
        entity.setTestBasis("未填写依据");
        entity.setEquipment("未填写设备");
        entity.setTestMethod("未填写方法");
        entity.setProjectArea("未填写区域");
        
        // Additional virtual data for non-empty fields
        entity.setClient("未填写");
        entity.setClientTel("000000");
        entity.setWitness("未填写");
        entity.setSurveyUnit("未填写单位");
        entity.setRemarks("无");
        
        if (entity instanceof Entrustment) {
            Entrustment ent = (Entrustment) entity;
            ent.setClientUnitAddress("未填写地址");
            ent.setClientUnitTel("000000");
            ent.setSpec("未填写");
            ent.setManufacturer("未填写");
            ent.setBatchNumber("0");
            ent.setTestItems("常规检测");
            ent.setSampleStatus("正常");
            ent.setProjectRemarks("无");
            ent.setWitnessIdCard("000000");
            ent.setSamplingManIdCard("000000");
            
            // These fields are in Entrustment and JcCoreWtInfo
            // We set them on Entrustment, if JcCoreWtInfo overrides them, it should be fine if it uses super or has its own logic.
            // If JcCoreWtInfo shadows them without linking to super, we might need to set on JcCoreWtInfo specifically.
            // But let's assume setting on Entrustment is enough or safe default.
            try {
                // Try to set buildingUnit2 if the method exists
                java.lang.reflect.Method m = Entrustment.class.getMethod("setBuildingUnit2", String.class);
                m.invoke(ent, "未填写单位");
            } catch (Exception e) {
                // Ignore
            }
        }
        
        if (entity instanceof JcCoreWtInfo) {
            JcCoreWtInfo info = (JcCoreWtInfo) entity;
            info.setSampleQuantity("0");
            info.setRepresentativeBatch("0");
            info.setSampleDisposal("留样");
            info.setFee("0");
            info.setReportSendMode("自取");
            info.setDeliveryMode("送样");
            info.setDeliveryDate(new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
            info.setSampleHistory("无");
            info.setClientAddressPhone("未填写");
        }
        
        if (directory == null) return;
        
        // Map granular roles based on category
        if ("ENTRUSTMENT_LIST".equals(category) || "检测委托单".equals(category)) {
             // For Entrustment, use wtUndertaker and wtReviewer
             // Note: Entrustment entity might use different field names, need to check Entrustment/JcCoreWtInfo
             if (entity instanceof JcCoreWtInfo) {
                 JcCoreWtInfo info = (JcCoreWtInfo) entity;
                 // Assuming JcCoreWtInfo uses tester/reviewer for process flow or specific fields?
                 // User requirement: "委托单单独使用自己的承接人和审核人"
                 // Map wtUndertaker -> receiver (YY_MAN)
                 // Map wtReviewer -> reviewer
                 if (directory.getWtUndertaker() != null) info.setReceiver(directory.getWtUndertaker()); 
                 if (directory.getWtReviewer() != null) info.setWtReviewer(directory.getWtReviewer());
             }
        } else if (category.contains("RECORD") || category.contains("记录表")) {
            // For Records: use jcFiller, jcTester, jcReviewer, bgApprover (shared)
            entity.setFiller(directory.getJcFiller());
            entity.setRecordTester(directory.getJcTester()); // Use recordTester field
            entity.setRecordReviewer(directory.getJcReviewer()); // Use recordReviewer field
            
            // Map to standard fields for compatibility if needed, OR keep them separate
            // User said: "记录表单独使用自己的审核人和检验人" -> So we use recordTester/recordReviewer
            // And "报告表和记录表使用同一个批准人" -> So we use bgApprover for approver
            if (directory.getJcTester() != null) entity.setTester(directory.getJcTester()); // Map to tester for workflow?
            if (directory.getJcReviewer() != null) entity.setReviewer(directory.getJcReviewer()); // Map to reviewer for workflow?
            if (directory.getBgApprover() != null) entity.setApprover(directory.getBgApprover());
            
        } else if (category.contains("REPORT") || category.contains("报告") || category.contains("RESULT") || category.contains("结果")) {
            // For Reports/Results: use bgTester, bgReviewer, bgApprover
            if (directory.getBgTester() != null) entity.setTester(directory.getBgTester());
            if (directory.getBgReviewer() != null) entity.setReviewer(directory.getBgReviewer());
            if (directory.getBgApprover() != null) entity.setApprover(directory.getBgApprover());
        }
    }

    private String createRelatedRecord(String type, String dirName, String creator, String category, SimpleDirectory directory) {
        if (type == null || type.isEmpty()) return null;
        
        String id = UUID.randomUUID().toString();
        java.util.Date now = new java.util.Date();
        
        try {
            if (isTypeMatch(type, "ENTRUSTMENT_LIST", "ENTRUSTMENT", "检测委托单")) {
                // Check if Master Record already exists (created by ensureMasterRecord)
                JcCoreWtInfo existing = jcCoreWtInfoService.getByWtNum(dirName);
                if (existing != null) {
                    return existing.getId();
                }
                
                // Fallback: Create if not exists (should rarely happen if ensureMasterRecord works)
                JcCoreWtInfo info = new JcCoreWtInfo();
                info.setId(id);
                info.setWtNum(dirName);
                info.setCreateBy(creator); // Maps to WT_REG_NAME
                info.setCreateTime(now); // For EXT table
                
                setDefaultValues(info, directory, category);
                
                jcCoreWtInfoMapper.insert(info);
                jcCoreWtInfoMapper.insertExt(info);

                return id;
            } else if (isTypeMatch(type, "REBOUND_METHOD_RECORD", "回弹法检测混凝土抗压强度记录表")) {
                ReboundMethodRecord entity = new ReboundMethodRecord();
                entity.setId(id);
                entity.setWtNum(dirName);
                entity.setCreateBy(creator);
                entity.setCreateTime(now);
                entity.setEntrustmentId(dirName);
                
                setDefaultValues(entity, directory, category);
                
                reboundMethodRecordMapper.insert(entity);
                return id;
            } else if (isTypeMatch(type, "REBOUND_METHOD_REPORT", "回弹法检测混凝土抗压强度报告")) {
                ReboundMethodReport entity = new ReboundMethodReport();
                entity.setId(id);
                entity.setWtNum(dirName);
                entity.setCreateBy(creator);
                entity.setCreateTime(now);
                entity.setEntrustmentId(dirName);
                
                setDefaultValues(entity, directory, category);
                
                reboundMethodReportMapper.insert(entity);
                return id;
            } else if (isTypeMatch(type, "LIGHT_DYNAMIC_PENETRATION_RECORD", "轻型动力触探检测记录表")) {
                // Get the actual entrustment ID from JC_CORE_WT_INFO
                String actualEntrustmentId = dirName;
                try {
                    JcCoreWtInfo wtInfo = jcCoreWtInfoService.getByWtNum(dirName);
                    if (wtInfo != null) {
                        actualEntrustmentId = wtInfo.getId();
                    }
                } catch (Exception e) {
                    System.err.println("Failed to get entrustment ID for dirName: " + dirName);
                }
                
                LightDynamicPenetrationRecord entity = new LightDynamicPenetrationRecord();
                entity.setId(id);
                entity.setWtNum(dirName);
                entity.setCreateBy(creator);
                entity.setCreateTime(now);
                entity.setEntrustmentId(actualEntrustmentId);
                
                setDefaultValues(entity, directory, category);
                
                lightDynamicPenetrationRecordMapper.insert(entity);
                return id;
            } else if (isTypeMatch(type, "LIGHT_DYNAMIC_PENETRATION", "LIGHT_DYNAMIC_PENETRATION_REPORT", "轻型动力触探检测报告")) {
                // Get the actual entrustment ID from JC_CORE_WT_INFO
                String actualEntrustmentId = dirName;
                try {
                    JcCoreWtInfo wtInfo = jcCoreWtInfoService.getByWtNum(dirName);
                    if (wtInfo != null) {
                        actualEntrustmentId = wtInfo.getId();
                    }
                } catch (Exception e) {
                    System.err.println("Failed to get entrustment ID for dirName: " + dirName);
                }
                
                LightDynamicPenetrationReport entity = new LightDynamicPenetrationReport();
                entity.setId(id);
                entity.setWtNum(dirName);
                entity.setCreateBy(creator);
                entity.setCreateTime(now);
                entity.setEntrustmentId(actualEntrustmentId);
                
                setDefaultValues(entity, directory, category);
                
                lightDynamicPenetrationReportMapper.insert(entity);
                return id;
            } else if (isTypeMatch(type, "NUCLEAR_DENSITY_RECORD", "原位密度检测记录表（核子法）")) {
                NuclearDensityRecord entity = new NuclearDensityRecord();
                entity.setId(id);
                entity.setWtNum(dirName);
                entity.setCreateBy(creator);
                entity.setCreateTime(now);
                entity.setEntrustmentId(dirName);
                
                setDefaultValues(entity, directory, category);
                
                // 尝试从委托单获取数据并填充到实体对象和 DATA_JSON
                try {
                    java.util.List<JcCoreWtInfo> entrustmentList = jcCoreWtInfoMapper.selectByWtNum(dirName);
                    if (entrustmentList != null && !entrustmentList.isEmpty()) {
                        JcCoreWtInfo entrustment = entrustmentList.get(0);
                        // 填充实体对象属性
                        copyFields(entrustment, entity);
                        // 设置默认虚拟数据
                        populateVirtualData(entity);
                        // 合并委托单数据到 JSON
                        mergeEntrustmentDataToJson(entity, entrustment);
                        System.out.println("创建核子法记录时已填充委托单数据");
                    } else {
                        // 如果没有找到委托单，只设置默认虚拟数据
                        populateVirtualData(entity);
                        System.out.println("创建核子法记录时未找到委托单数据，只设置默认值");
                    }
                } catch (Exception e) {
                    System.err.println("创建核子法记录时填充委托单数据失败: " + e.getMessage());
                    e.printStackTrace();
                    // 即使失败，也要设置默认虚拟数据
                    populateVirtualData(entity);
                }
                
                nuclearDensityRecordMapper.insert(entity);
                return id;
            } else if (isTypeMatch(type, "SAND_REPLACEMENT_RECORD", "原位密度检测记录表（灌砂法）")) {
                SandReplacementRecord entity = new SandReplacementRecord();
                entity.setId(id);
                entity.setWtNum(dirName);
                entity.setCreateBy(creator);
                entity.setCreateTime(now);
                entity.setEntrustmentId(dirName);
                
                setDefaultValues(entity, directory, category);
                
                sandReplacementRecordMapper.insert(entity);
                return id;
            } else if (isTypeMatch(type, "WATER_REPLACEMENT_RECORD", "相对密度试验记录表（灌水法）")) {
                WaterReplacementRecord entity = new WaterReplacementRecord();
                entity.setId(id);
                entity.setWtNum(dirName);
                entity.setCreateBy(creator);
                entity.setCreateTime(now);
                entity.setEntrustmentId(dirName);
                
                setDefaultValues(entity, directory, category);
                
                waterReplacementRecordMapper.insert(entity);
                return id;
            } else if (isTypeMatch(type, "CUTTING_RING_RECORD", "原位密度检测记录表（环刀法）")) {
                CuttingRingRecord entity = new CuttingRingRecord();
                entity.setId(id);
                entity.setWtNum(dirName);
                entity.setCreateBy(creator);
                entity.setCreateTime(now);
                entity.setEntrustmentId(dirName);
                
                setDefaultValues(entity, directory, category);
                
                cuttingRingRecordMapper.insert(entity);
                return id;
            } else if (isTypeMatch(type, "BECKMAN_BEAM_RECORD", "路基路面回弹弯沉试验检测记录表")) {
                BeckmanBeamRecord entity = new BeckmanBeamRecord();
                entity.setId(id);
                entity.setWtNum(dirName);
                entity.setCreateBy(creator);
                entity.setCreateTime(now);
                entity.setEntrustmentId(dirName);
                
                setDefaultValues(entity, directory, category);
                
                beckmanBeamRecordMapper.insert(entity);
                return id;
            } else if (isTypeMatch(type, "BECKMAN_BEAM_REPORT", "路基路面回弹弯沉检测报告")) {
                BeckmanBeamReport entity = new BeckmanBeamReport();
                entity.setId(id);
                entity.setWtNum(dirName);
                entity.setCreateBy(creator);
                entity.setCreateTime(now);
                entity.setEntrustmentId(dirName);
                
                setDefaultValues(entity, directory, category);
                
                beckmanBeamReportMapper.insert(entity);
                return id;
            } else if (isTypeMatch(type, "DENSITY_TEST_RECORD", "原位密度检测记录表")) {
                DensityTestRecord entity = new DensityTestRecord();
                entity.setId(id);
                entity.setWtNum(dirName);
                entity.setCreateBy(creator);
                entity.setCreateTime(now);
                entity.setEntrustmentId(dirName);
                
                setDefaultValues(entity, directory, category);
                
                densityTestRecordMapper.insert(entity);
                return id;
            } else if (isTypeMatch(type, "DENSITY_TEST_REPORT", "原位密度检测报告")) {
                DensityTestReport entity = new DensityTestReport();
                entity.setId(id);
                entity.setWtNum(dirName);
                entity.setCreateBy(creator);
                entity.setCreateTime(now);
                entity.setEntrustmentId(dirName);
                
                setDefaultValues(entity, directory, category);
                
                densityTestReportMapper.insert(entity);
                return id;
            } else if (isTypeMatch(type, "DENSITY_TEST_RESULT", "原位密度检测结果")) {
                DensityTestResult entity = new DensityTestResult();
                entity.setId(id);
                entity.setWtNum(dirName);
                entity.setCreateBy(creator);
                entity.setCreateTime(now);
                entity.setEntrustmentId(dirName);
                
                setDefaultValues(entity, directory, category);
                
                densityTestResultMapper.insert(entity);
                return id;
            } else if (isTypeMatch(type, "LIGHT_DYNAMIC_PENETRATION_RESULT", "轻型动力触探检测结果")) {
                LightDynamicPenetrationResult entity = new LightDynamicPenetrationResult();
                entity.setId(id);
                entity.setWtNum(dirName);
                entity.setCreateBy(creator);
                entity.setCreateTime(now);
                entity.setEntrustmentId(dirName);
                
                setDefaultValues(entity, directory, category);
                
                lightDynamicPenetrationResultMapper.insert(entity);
                return id;
            } else if (isTypeMatch(type, "BECKMAN_BEAM_RESULT", "路基路面回弹弯沉检测结果")) {
                BeckmanBeamResult entity = new BeckmanBeamResult();
                entity.setId(id);
                entity.setWtNum(dirName);
                entity.setCreateBy(creator);
                entity.setCreateTime(now);
                entity.setEntrustmentId(dirName);
                
                setDefaultValues(entity, directory, category);
                
                beckmanBeamResultMapper.insert(entity);
                return id;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public SimpleDirectory getDirectoryByDirName(String dirName) {
        return simpleDirectoryMapper.selectByDirName(dirName);
    }

    @Override
    public void syncEntrustmentDataByWtNum(String wtNum) {
        if (wtNum == null || wtNum.isEmpty()) return;
        
        SimpleDirectory directory = simpleDirectoryMapper.selectByDirName(wtNum);
        if (directory != null) {
            syncEntrustmentData(directory);
        }
    }

    private void syncEntrustmentData(SimpleDirectory directory) {
        try {
            // 1. 查找委托单 (Source)
            // 优先使用 getByWtNum 获取完整数据（包含 JOIN T_ENTRUSTMENT 的完整查询）
            JcCoreWtInfo entrustment = null;
            String dirName = directory.getDirName();
            
            // 先尝试通过 dirName (wtNum) 获取完整数据
            if (dirName != null && !dirName.isEmpty()) {
                java.util.List<JcCoreWtInfo> list = jcCoreWtInfoMapper.selectByWtNum(dirName);
                if (list != null && !list.isEmpty()) {
                    entrustment = list.get(0);
                    System.out.println("通过 wtNum 获取委托单数据: " + dirName);
                }
            }
            
            // 如果通过 wtNum 没找到，再尝试通过 ID 查找
            if (entrustment == null) {
            if (isEntrustment(directory.getTable1Type())) entrustment = jcCoreWtInfoMapper.selectById(directory.getTable1Id());
            else if (isEntrustment(directory.getTable2Type())) entrustment = jcCoreWtInfoMapper.selectById(directory.getTable2Id());
            else if (isEntrustment(directory.getTable3Type())) entrustment = jcCoreWtInfoMapper.selectById(directory.getTable3Id());
            else if (isEntrustment(directory.getTable4Type())) entrustment = jcCoreWtInfoMapper.selectById(directory.getTable4Id());
            else if (isEntrustment(directory.getTable5Type())) entrustment = jcCoreWtInfoMapper.selectById(directory.getTable5Id());
            else if (isEntrustment(directory.getTable6Type())) entrustment = jcCoreWtInfoMapper.selectById(directory.getTable6Id());
            else if (isEntrustment(directory.getTable7Type())) entrustment = jcCoreWtInfoMapper.selectById(directory.getTable7Id());
            else if (isEntrustment(directory.getTable8Type())) entrustment = jcCoreWtInfoMapper.selectById(directory.getTable8Id());
            else if (isEntrustment(directory.getTable9Type())) entrustment = jcCoreWtInfoMapper.selectById(directory.getTable9Id());
            else if (isEntrustment(directory.getTable10Type())) entrustment = jcCoreWtInfoMapper.selectById(directory.getTable10Id());
                System.out.println("通过 ID 获取委托单数据");
            }

            if (entrustment == null) {
                System.err.println("未找到委托单数据，dirName: " + dirName);
                return;
            }

            dirName = entrustment.getWtNum() != null ? entrustment.getWtNum() : directory.getDirName();
            System.out.println("委托单数据 - WtNum: " + entrustment.getWtNum() + ", ClientUnit: " + entrustment.getClientUnit() + ", ProjectName: " + entrustment.getProjectName());
            String creator = entrustment.getCreateBy() != null ? entrustment.getCreateBy() : directory.getCreateBy();
            String category = entrustment.getTestCategory() != null ? entrustment.getTestCategory() : determineAllTestCategories(directory);

            if (directory.getTable1Type() != null && !directory.getTable1Type().isEmpty() && !isEntrustment(directory.getTable1Type())) {
                if (directory.getTable1Id() == null || directory.getTable1Id().isEmpty()) {
                    String id = createRelatedRecord(directory.getTable1Type(), dirName, creator, category, directory);
                    directory.setTable1Id(id);
                }
            }
            if (directory.getTable2Type() != null && !directory.getTable2Type().isEmpty() && !isEntrustment(directory.getTable2Type())) {
                if (directory.getTable2Id() == null || directory.getTable2Id().isEmpty()) {
                    String id = createRelatedRecord(directory.getTable2Type(), dirName, creator, category, directory);
                    directory.setTable2Id(id);
                }
            }
            if (directory.getTable3Type() != null && !directory.getTable3Type().isEmpty() && !isEntrustment(directory.getTable3Type())) {
                if (directory.getTable3Id() == null || directory.getTable3Id().isEmpty()) {
                    String id = createRelatedRecord(directory.getTable3Type(), dirName, creator, category, directory);
                    directory.setTable3Id(id);
                }
            }
            if (directory.getTable4Type() != null && !directory.getTable4Type().isEmpty() && !isEntrustment(directory.getTable4Type())) {
                if (directory.getTable4Id() == null || directory.getTable4Id().isEmpty()) {
                    String id = createRelatedRecord(directory.getTable4Type(), dirName, creator, category, directory);
                    directory.setTable4Id(id);
                }
            }
            if (directory.getTable5Type() != null && !directory.getTable5Type().isEmpty() && !isEntrustment(directory.getTable5Type())) {
                if (directory.getTable5Id() == null || directory.getTable5Id().isEmpty()) {
                    String id = createRelatedRecord(directory.getTable5Type(), dirName, creator, category, directory);
                    directory.setTable5Id(id);
                }
            }
            if (directory.getTable6Type() != null && !directory.getTable6Type().isEmpty() && !isEntrustment(directory.getTable6Type())) {
                if (directory.getTable6Id() == null || directory.getTable6Id().isEmpty()) {
                    String id = createRelatedRecord(directory.getTable6Type(), dirName, creator, category, directory);
                    directory.setTable6Id(id);
                }
            }
            if (directory.getTable7Type() != null && !directory.getTable7Type().isEmpty() && !isEntrustment(directory.getTable7Type())) {
                if (directory.getTable7Id() == null || directory.getTable7Id().isEmpty()) {
                    String id = createRelatedRecord(directory.getTable7Type(), dirName, creator, category, directory);
                    directory.setTable7Id(id);
                }
            }
            if (directory.getTable8Type() != null && !directory.getTable8Type().isEmpty() && !isEntrustment(directory.getTable8Type())) {
                if (directory.getTable8Id() == null || directory.getTable8Id().isEmpty()) {
                    String id = createRelatedRecord(directory.getTable8Type(), dirName, creator, category, directory);
                    directory.setTable8Id(id);
                }
            }
            if (directory.getTable9Type() != null && !directory.getTable9Type().isEmpty() && !isEntrustment(directory.getTable9Type())) {
                if (directory.getTable9Id() == null || directory.getTable9Id().isEmpty()) {
                    String id = createRelatedRecord(directory.getTable9Type(), dirName, creator, category, directory);
                    directory.setTable9Id(id);
                }
            }
            if (directory.getTable10Type() != null && !directory.getTable10Type().isEmpty() && !isEntrustment(directory.getTable10Type())) {
                if (directory.getTable10Id() == null || directory.getTable10Id().isEmpty()) {
                    String id = createRelatedRecord(directory.getTable10Type(), dirName, creator, category, directory);
                    directory.setTable10Id(id);
                }
            }

            simpleDirectoryMapper.update(directory);

            // 2. 同步到其他表 (Target)
            syncToTable(directory.getTable1Type(), directory.getTable1Id(), entrustment);
            syncToTable(directory.getTable2Type(), directory.getTable2Id(), entrustment);
            syncToTable(directory.getTable3Type(), directory.getTable3Id(), entrustment);
            syncToTable(directory.getTable4Type(), directory.getTable4Id(), entrustment);
            syncToTable(directory.getTable5Type(), directory.getTable5Id(), entrustment);
            syncToTable(directory.getTable6Type(), directory.getTable6Id(), entrustment);
            syncToTable(directory.getTable7Type(), directory.getTable7Id(), entrustment);
            syncToTable(directory.getTable8Type(), directory.getTable8Id(), entrustment);
            syncToTable(directory.getTable9Type(), directory.getTable9Id(), entrustment);
            syncToTable(directory.getTable10Type(), directory.getTable10Id(), entrustment);

        } catch (Exception e) {
            e.printStackTrace();
            // Log error but don't fail the save
        }
    }

    private boolean isEntrustment(String type) {
        return type != null && (
                "ENTRUSTMENT".equalsIgnoreCase(type) ||
                "JZS_ENTRUSTMENT".equalsIgnoreCase(type) ||
                "ENTRUSTMENT_LIST".equalsIgnoreCase(type) ||
                "委托单".equals(type)
        );
    }

    private void syncToTable(String type, String id, JcCoreWtInfo source) {
        if (type == null || id == null || isEntrustment(type)) {
            return;
        }

        if (isTypeMatch(type, "DENSITY_TEST_RECORD", "原位密度检测记录表")) {
            DensityTestRecord target = densityTestRecordMapper.selectById(id);
            if (target != null) {
                target.setEntrustmentId(source.getWtNum());
                copyFields(source, target);
                populateVirtualData(target);
                mergeEntrustmentDataToJson(target, source);
                densityTestRecordMapper.updateById(target);
            }
        } else if (isTypeMatch(type, "DENSITY_TEST_REPORT", "原位密度检测报告")) {
            DensityTestReport target = densityTestReportMapper.selectById(id);
            if (target != null) {
                target.setEntrustmentId(source.getWtNum());
                copyFields(source, target);
                densityTestReportMapper.updateById(target);
            }
        } else if (isTypeMatch(type, "DENSITY_TEST_RESULT", "原位密度检测结果")) {
            DensityTestResult target = densityTestResultMapper.selectById(id);
            if (target != null) {
                target.setEntrustmentId(source.getWtNum());
                copyFields(source, target);
                densityTestResultMapper.updateById(target);
            }
        } else if (isTypeMatch(type, "REBOUND_METHOD_RECORD", "回弹法检测混凝土抗压强度记录表")) {
            ReboundMethodRecord target = reboundMethodRecordMapper.selectById(id);
            if (target != null) {
                target.setEntrustmentId(source.getWtNum());
                copyFields(source, target);
                populateVirtualData(target);
                mergeEntrustmentDataToJson(target, source);
                reboundMethodRecordMapper.updateById(target);
            }
        } else if (isTypeMatch(type, "REBOUND_METHOD_REPORT", "回弹法检测混凝土抗压强度报告")) {
            ReboundMethodReport target = reboundMethodReportMapper.selectById(id);
            if (target != null) {
                target.setEntrustmentId(source.getWtNum());
                copyFields(source, target);
                reboundMethodReportMapper.updateById(target);
            }
        } else if (isTypeMatch(type, "SAND_REPLACEMENT_RECORD", "原位密度检测记录表（灌砂法）")) {
            SandReplacementRecord target = sandReplacementRecordMapper.selectById(id);
            if (target != null) {
                target.setEntrustmentId(source.getWtNum());
                copyFields(source, target);
                populateVirtualData(target);
                mergeEntrustmentDataToJson(target, source);
                sandReplacementRecordMapper.updateById(target);
            }
        } else if (isTypeMatch(type, "LIGHT_DYNAMIC_PENETRATION_RECORD", "轻型动力触探检测记录表")) {
            LightDynamicPenetrationRecord target = lightDynamicPenetrationRecordMapper.selectById(id);
            if (target != null) {
                // 轻型动力触探表使用 ENTRUSTMENT_ID 关联 JC_CORE_WT_INFO.WT_ID
                target.setEntrustmentId(source.getId());
                copyFields(source, target);
                populateVirtualData(target);
                mergeEntrustmentDataToJson(target, source);
                lightDynamicPenetrationRecordMapper.updateById(target);
            }
        } else if (isTypeMatch(type, "LIGHT_DYNAMIC_PENETRATION", "LIGHT_DYNAMIC_PENETRATION_REPORT", "轻型动力触探检测报告")) {
            LightDynamicPenetrationReport target = lightDynamicPenetrationReportMapper.selectById(id);
            if (target != null) {
                // 轻型动力触探表使用 ENTRUSTMENT_ID 关联 JC_CORE_WT_INFO.WT_ID
                target.setEntrustmentId(source.getId());
                copyFields(source, target);
                lightDynamicPenetrationReportMapper.updateById(target);
            }
        } else if (isTypeMatch(type, "LIGHT_DYNAMIC_PENETRATION_RESULT", "轻型动力触探检测结果")) {
            LightDynamicPenetrationResult target = lightDynamicPenetrationResultMapper.selectById(id);
            if (target != null) {
                // 轻型动力触探表使用 ENTRUSTMENT_ID 关联 JC_CORE_WT_INFO.WT_ID
                target.setEntrustmentId(source.getId());
                copyFields(source, target);
                lightDynamicPenetrationResultMapper.updateById(target);
            }
        } else if (isTypeMatch(type, "BECKMAN_BEAM_RECORD", "路基路面回弹弯沉试验检测记录表")) {
            BeckmanBeamRecord target = beckmanBeamRecordMapper.selectById(id);
            if (target != null) {
                target.setEntrustmentId(source.getWtNum());
                copyFields(source, target);
                populateVirtualData(target);
                mergeEntrustmentDataToJson(target, source);
                beckmanBeamRecordMapper.updateById(target);
            }
        } else if (isTypeMatch(type, "BECKMAN_BEAM_REPORT", "路基路面回弹弯沉检测报告")) {
            BeckmanBeamReport target = beckmanBeamReportMapper.selectById(id);
            if (target != null) {
                target.setEntrustmentId(source.getWtNum());
                copyFields(source, target);
                beckmanBeamReportMapper.updateById(target);
            }
        } else if (isTypeMatch(type, "BECKMAN_BEAM_RESULT", "路基路面回弹弯沉检测结果")) {
            BeckmanBeamResult target = beckmanBeamResultMapper.selectById(id);
            if (target != null) {
                target.setEntrustmentId(source.getWtNum());
                copyFields(source, target);
                beckmanBeamResultMapper.updateById(target);
            }
        } else if (isTypeMatch(type, "CUTTING_RING_RECORD", "原位密度检测记录表（环刀法）")) {
            CuttingRingRecord target = cuttingRingRecordMapper.selectById(id);
            if (target != null) {
                target.setEntrustmentId(source.getWtNum());
                copyFields(source, target);
                populateVirtualData(target);
                mergeEntrustmentDataToJson(target, source);
                cuttingRingRecordMapper.updateById(target);
            }
        } else if (isTypeMatch(type, "NUCLEAR_DENSITY_RECORD", "原位密度检测记录表（核子法）")) {
            NuclearDensityRecord target = nuclearDensityRecordMapper.selectById(id);
            if (target != null) {
                target.setEntrustmentId(source.getWtNum());
                copyFields(source, target);
                populateVirtualData(target);
                // 将从委托单同步的业务字段写入 DATA_JSON，确保前端能读取到
                mergeEntrustmentDataToJson(target, source);
                nuclearDensityRecordMapper.updateById(target);
            }
        } else if (isTypeMatch(type, "WATER_REPLACEMENT_RECORD", "相对密度试验记录表（灌水法）")) {
            WaterReplacementRecord target = waterReplacementRecordMapper.selectById(id);
            if (target != null) {
                target.setEntrustmentId(source.getWtNum());
                copyFields(source, target);
                populateVirtualData(target);
                mergeEntrustmentDataToJson(target, source);
                waterReplacementRecordMapper.updateById(target);
            }
        } else if (isTypeMatch(type, "SAND_REPLACEMENT_REPORT", "原位密度检测报告（灌砂法）")) {
            SandReplacementReport target = sandReplacementReportMapper.selectById(id);
            if (target != null) {
                target.setEntrustmentId(source.getWtNum());
                copyFields(source, target);
                sandReplacementReportMapper.updateById(target);
            }
        } else if (isTypeMatch(type, "SAND_REPLACEMENT_RESULT", "原位密度检测结果（灌砂法）")) {
            SandReplacementResult target = sandReplacementResultMapper.selectById(id);
            if (target != null) {
                target.setEntrustmentId(source.getWtNum());
                copyFields(source, target);
                sandReplacementResultMapper.updateById(target);
            }
        } else if (isTypeMatch(type, "CUTTING_RING_REPORT", "原位密度检测报告（环刀法）")) {
            CuttingRingReport target = cuttingRingReportMapper.selectById(id);
            if (target != null) {
                target.setEntrustmentId(source.getWtNum());
                copyFields(source, target);
                cuttingRingReportMapper.updateById(target);
            }
        } else if (isTypeMatch(type, "CUTTING_RING_RESULT", "原位密度检测结果（环刀法）")) {
            CuttingRingResult target = cuttingRingResultMapper.selectById(id);
            if (target != null) {
                target.setEntrustmentId(source.getWtNum());
                copyFields(source, target);
                cuttingRingResultMapper.updateById(target);
            }
        } else if (isTypeMatch(type, "NUCLEAR_DENSITY_REPORT", "原位密度检测报告（核子法）")) {
            NuclearDensityReport target = nuclearDensityReportMapper.selectById(id);
            if (target != null) {
                target.setEntrustmentId(source.getWtNum());
                copyFields(source, target);
                nuclearDensityReportMapper.updateById(target);
            }
        } else if (isTypeMatch(type, "NUCLEAR_DENSITY_RESULT", "原位密度检测结果（核子法）")) {
            NuclearDensityResult target = nuclearDensityResultMapper.selectById(id);
            if (target != null) {
                target.setEntrustmentId(source.getWtNum());
                copyFields(source, target);
                nuclearDensityResultMapper.updateById(target);
            }
        } else if (isTypeMatch(type, "WATER_REPLACEMENT_REPORT", "相对密度试验报告（灌水法）")) {
            WaterReplacementReport target = waterReplacementReportMapper.selectById(id);
            if (target != null) {
                target.setEntrustmentId(source.getWtNum());
                copyFields(source, target);
                waterReplacementReportMapper.updateById(target);
            }
        } else if (isTypeMatch(type, "WATER_REPLACEMENT_RESULT", "相对密度试验结果（灌水法）")) {
            WaterReplacementResult target = waterReplacementResultMapper.selectById(id);
            if (target != null) {
                target.setEntrustmentId(source.getWtNum());
                copyFields(source, target);
                waterReplacementResultMapper.updateById(target);
            }
        }
    }

    private boolean isTypeMatch(String type, String... candidates) {
        for (String candidate : candidates) {
            if (candidate.equalsIgnoreCase(type)) {
                return true;
            }
        }
        return false;
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
        if (source.getSampleName() != null) target.setSampleName(source.getSampleName());
        
        // Add more fields as needed - expanded auto-fill fields
        if (source.getConstructionPart() != null) target.setConstructionPart(source.getConstructionPart());
        if (source.getProjectArea() != null) target.setProjectArea(source.getProjectArea());
        if (source.getSurveyUnit() != null) target.setSurveyUnit(source.getSurveyUnit());
        if (source.getClient() != null) target.setClient(source.getClient());
        if (source.getClientTel() != null) target.setClientTel(source.getClientTel());
        if (source.getTestCategory() != null) target.setTestCategory(source.getTestCategory());
        if (source.getRemarks() != null) target.setRemarks(source.getRemarks());
        
        // Sync Roles - DISABLED to preserve granular roles set in setDefaultValues
        // Entrustment roles (WT_*) should not overwrite Record (JC_*) or Report (BG_*) roles
        // if (source.getTester() != null) target.setTester(source.getTester());
        // if (source.getReviewer() != null) target.setReviewer(source.getReviewer());
        // if (source.getApprover() != null) target.setApprover(source.getApprover());
        
        // Add more fields as needed
    }

    /**
     * 将委托单的业务字段合并到记录表的 DATA_JSON 中
     * 确保前端能够从 DATA_JSON 中读取到从委托单同步的业务字段
     */
    private void mergeEntrustmentDataToJson(BusinessEntity target, JcCoreWtInfo source) {
        try {
            System.out.println("=== mergeEntrustmentDataToJson 开始 ===");
            System.out.println("Source WtNum: " + source.getWtNum());
            System.out.println("Source ClientUnit: " + source.getClientUnit());
            System.out.println("Source ProjectName: " + source.getProjectName());
            
            // 读取现有的 DATA_JSON
            String existingJson = null;
            if (target instanceof NuclearDensityRecord) {
                existingJson = ((NuclearDensityRecord) target).getDataJson();
            } else if (target instanceof SandReplacementRecord) {
                existingJson = ((SandReplacementRecord) target).getDataJson();
            } else if (target instanceof WaterReplacementRecord) {
                existingJson = ((WaterReplacementRecord) target).getDataJson();
            } else if (target instanceof CuttingRingRecord) {
                existingJson = ((CuttingRingRecord) target).getDataJson();
            } else if (target instanceof BeckmanBeamRecord) {
                existingJson = ((BeckmanBeamRecord) target).getDataJson();
            } else if (target instanceof LightDynamicPenetrationRecord) {
                existingJson = ((LightDynamicPenetrationRecord) target).getDataJson();
            } else if (target instanceof ReboundMethodRecord) {
                existingJson = ((ReboundMethodRecord) target).getDataJson();
            } else if (target instanceof DensityTestRecord) {
                existingJson = ((DensityTestRecord) target).getDataJson();
            }
            
            System.out.println("Existing JSON: " + existingJson);
            
            // 解析现有 JSON 或创建新 Map
            java.util.Map<String, Object> data = new java.util.HashMap<>();
            if (existingJson != null && !existingJson.isEmpty()) {
                try {
                    data = objectMapper.readValue(existingJson, java.util.Map.class);
                    System.out.println("Parsed existing JSON, size: " + data.size());
                } catch (Exception e) {
                    System.err.println("Failed to parse existing JSON: " + e.getMessage());
                    // 如果解析失败，使用空 Map
                    data = new java.util.HashMap<>();
                }
            }
            
            // 将从委托单同步的业务字段添加到 JSON 中（使用前端期望的字段名）
            // 注意：即使字段为 null，也要尝试从实体对象的属性中获取（因为 copyFields 已经设置了）
            int fieldsAdded = 0;
            
            // 优先使用 source 的字段，如果为 null，则使用 target 实体对象的属性
            String projectName = source.getProjectName();
            if (projectName == null || projectName.isEmpty()) projectName = target.getProjectName();
            if (projectName != null && !projectName.isEmpty()) {
                data.put("projectName", projectName);
                fieldsAdded++;
            }
            
            String clientUnit = source.getClientUnit();
            if (clientUnit == null || clientUnit.isEmpty()) clientUnit = target.getClientUnit();
            if (clientUnit != null && !clientUnit.isEmpty()) {
                data.put("entrustingUnit", clientUnit);
                fieldsAdded++;
            }
            
            String wtNum = source.getWtNum();
            if (wtNum == null || wtNum.isEmpty()) wtNum = target.getWtNum();
            if (wtNum != null && !wtNum.isEmpty()) {
                data.put("unifiedNumber", wtNum);
                fieldsAdded++;
            }
            
            if (source.getCommissionDate() != null) {
                data.put("commissionDate", new java.text.SimpleDateFormat("yyyy-MM-dd").format(source.getCommissionDate()));
                fieldsAdded++;
            } else if (target.getCommissionDate() != null) {
                data.put("commissionDate", new java.text.SimpleDateFormat("yyyy-MM-dd").format(target.getCommissionDate()));
                fieldsAdded++;
            }
            
            String constructionUnit = source.getConstructionUnit();
            if (constructionUnit == null || constructionUnit.isEmpty()) constructionUnit = target.getConstructionUnit();
            if (constructionUnit != null && !constructionUnit.isEmpty()) {
                data.put("constructionUnit", constructionUnit);
                fieldsAdded++;
            }
            
            String buildingUnit = source.getBuildingUnit();
            if (buildingUnit == null || buildingUnit.isEmpty()) buildingUnit = target.getBuildingUnit();
            if (buildingUnit != null && !buildingUnit.isEmpty()) {
                data.put("buildingUnit", buildingUnit);
                fieldsAdded++;
            }
            
            String supervisionUnit = source.getSupervisionUnit();
            if (supervisionUnit == null || supervisionUnit.isEmpty()) supervisionUnit = target.getSupervisionUnit();
            if (supervisionUnit != null && !supervisionUnit.isEmpty()) {
                data.put("supervisionUnit", supervisionUnit);
                fieldsAdded++;
            }
            
            String designUnit = source.getDesignUnit();
            if (designUnit == null || designUnit.isEmpty()) designUnit = target.getDesignUnit();
            if (designUnit != null && !designUnit.isEmpty()) {
                data.put("designUnit", designUnit);
                fieldsAdded++;
            }
            
            String witnessUnit = source.getWitnessUnit();
            if (witnessUnit == null || witnessUnit.isEmpty()) witnessUnit = target.getWitnessUnit();
            if (witnessUnit != null && !witnessUnit.isEmpty()) {
                data.put("witnessUnit", witnessUnit);
                fieldsAdded++;
            }
            
            String witness = source.getWitness();
            if (witness == null || witness.isEmpty()) witness = target.getWitness();
            if (witness != null && !witness.isEmpty()) {
                data.put("witness", witness);
                fieldsAdded++;
            }
            
            String sampleName = source.getSampleName();
            if (sampleName == null || sampleName.isEmpty()) sampleName = target.getSampleName();
            if (sampleName != null && !sampleName.isEmpty()) {
                data.put("sampleNameStatus", sampleName);
                fieldsAdded++;
            }
            
            String constructionPart = source.getConstructionPart();
            if (constructionPart == null || constructionPart.isEmpty()) constructionPart = target.getConstructionPart();
            if (constructionPart != null && !constructionPart.isEmpty()) {
                data.put("constructionPart", constructionPart);
                fieldsAdded++;
            }
            
            String projectArea = source.getProjectArea();
            if (projectArea == null || projectArea.isEmpty()) projectArea = target.getProjectArea();
            if (projectArea != null && !projectArea.isEmpty()) {
                data.put("projectArea", projectArea);
                fieldsAdded++;
            }
            
            String surveyUnit = source.getSurveyUnit();
            if (surveyUnit == null || surveyUnit.isEmpty()) surveyUnit = target.getSurveyUnit();
            if (surveyUnit != null && !surveyUnit.isEmpty()) {
                data.put("surveyUnit", surveyUnit);
                fieldsAdded++;
            }
            
            String client = source.getClient();
            if (client == null || client.isEmpty()) client = target.getClient();
            if (client != null && !client.isEmpty()) {
                data.put("client", client);
                fieldsAdded++;
            }
            
            String clientTel = source.getClientTel();
            if (clientTel == null || clientTel.isEmpty()) clientTel = target.getClientTel();
            if (clientTel != null && !clientTel.isEmpty()) {
                data.put("clientTel", clientTel);
                fieldsAdded++;
            }
            
            String testCategory = source.getTestCategory();
            if (testCategory == null || testCategory.isEmpty()) testCategory = target.getTestCategory();
            if (testCategory != null && !testCategory.isEmpty()) {
                data.put("testCategory", testCategory);
                fieldsAdded++;
            }
            
            String testBasis = source.getTestBasis();
            if (testBasis == null || testBasis.isEmpty()) testBasis = target.getTestBasis();
            if (testBasis != null && !testBasis.isEmpty()) {
                data.put("standard", testBasis);
                fieldsAdded++;
            }
            
            String equipment = source.getEquipment();
            if (equipment == null || equipment.isEmpty()) equipment = target.getEquipment();
            if (equipment != null && !equipment.isEmpty()) {
                data.put("equipment", equipment);
                fieldsAdded++;
            }
            
            String remarks = source.getRemarks();
            if (remarks == null || remarks.isEmpty()) remarks = target.getRemarks();
            if (remarks != null && !remarks.isEmpty()) {
                data.put("remarks", remarks);
                fieldsAdded++;
            }
            
            System.out.println("合并了 " + fieldsAdded + " 个字段到 JSON");
            
            // 写回 DATA_JSON
            String mergedJson = objectMapper.writeValueAsString(data);
            System.out.println("Merged JSON: " + mergedJson);
            
            if (target instanceof NuclearDensityRecord) {
                ((NuclearDensityRecord) target).setDataJson(mergedJson);
            } else if (target instanceof SandReplacementRecord) {
                ((SandReplacementRecord) target).setDataJson(mergedJson);
            } else if (target instanceof WaterReplacementRecord) {
                ((WaterReplacementRecord) target).setDataJson(mergedJson);
            } else if (target instanceof CuttingRingRecord) {
                ((CuttingRingRecord) target).setDataJson(mergedJson);
            } else if (target instanceof BeckmanBeamRecord) {
                ((BeckmanBeamRecord) target).setDataJson(mergedJson);
            } else if (target instanceof LightDynamicPenetrationRecord) {
                ((LightDynamicPenetrationRecord) target).setDataJson(mergedJson);
            } else if (target instanceof ReboundMethodRecord) {
                ((ReboundMethodRecord) target).setDataJson(mergedJson);
            } else if (target instanceof DensityTestRecord) {
                ((DensityTestRecord) target).setDataJson(mergedJson);
            }
            
            System.out.println("=== mergeEntrustmentDataToJson 完成 ===");
        } catch (Exception e) {
            System.err.println("Error merging entrustment data to JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void populateVirtualData(BusinessEntity entity) {
        try {
            java.util.Map<String, Object> data = new java.util.HashMap<>();
            String existingJson = null;

            if (entity instanceof DensityTestRecord) {
                existingJson = ((DensityTestRecord) entity).getDataJson();
                if (existingJson == null || existingJson.isEmpty()) {
                    data.put("dryDensity", "2.35");
                    data.put("moistureContent", "5.2");
                    data.put("maxDryDensity", "2.40");
                    data.put("compactionCoefficient", "98");
                    data.put("testDate", new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
                    ((DensityTestRecord) entity).setDataJson(objectMapper.writeValueAsString(data));
                }
            } else if (entity instanceof ReboundMethodRecord) {
                ReboundMethodRecord record = (ReboundMethodRecord) entity;
                existingJson = record.getDataJson();
                // 不再自动填充假数据，保持 dataJson 为空，由用户手动填写
                // if (existingJson == null || existingJson.isEmpty()) {
                //     // 已移除自动填充假数据的逻辑
                // }
            } else if (entity instanceof SandReplacementRecord) {
                existingJson = ((SandReplacementRecord) entity).getDataJson();
                if (existingJson == null || existingJson.isEmpty()) {
                    data.put("testDate", new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
                    data.put("holeVolume", "1500");
                    data.put("sandMass", "2000");
                    data.put("wetDensity", "2.1");
                    data.put("dryDensity", "1.9");
                    data.put("compactionCoefficient", "95");
                    ((SandReplacementRecord) entity).setDataJson(objectMapper.writeValueAsString(data));
                }
            } else if (entity instanceof WaterReplacementRecord) {
                existingJson = ((WaterReplacementRecord) entity).getDataJson();
                if (existingJson == null || existingJson.isEmpty()) {
                    data.put("testDate", new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
                    data.put("waterMass", "1500");
                    data.put("volume", "1500");
                    data.put("wetDensity", "2.1");
                    data.put("dryDensity", "1.9");
                    ((WaterReplacementRecord) entity).setDataJson(objectMapper.writeValueAsString(data));
                }
            } else if (entity instanceof NuclearDensityRecord) {
                existingJson = ((NuclearDensityRecord) entity).getDataJson();
                if (existingJson == null || existingJson.isEmpty()) {
                    data.put("testDate", new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
                    data.put("wetDensity", "2.1");
                    data.put("moisture", "10");
                    data.put("dryDensity", "1.9");
                    ((NuclearDensityRecord) entity).setDataJson(objectMapper.writeValueAsString(data));
                }
            } else if (entity instanceof CuttingRingRecord) {
                existingJson = ((CuttingRingRecord) entity).getDataJson();
                if (existingJson == null || existingJson.isEmpty()) {
                    data.put("testDate", new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
                    data.put("ringVolume", "200");
                    data.put("wetMass", "400");
                    data.put("dryMass", "350");
                    data.put("dryDensity", "1.75");
                    ((CuttingRingRecord) entity).setDataJson(objectMapper.writeValueAsString(data));
                }
            } else if (entity instanceof BeckmanBeamRecord) {
                existingJson = ((BeckmanBeamRecord) entity).getDataJson();
                if (existingJson == null || existingJson.isEmpty()) {
                    data.put("testDate", new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
                    data.put("deflection", "20");
                    data.put("temperature", "25");
                    ((BeckmanBeamRecord) entity).setDataJson(objectMapper.writeValueAsString(data));
                }
            } else if (entity instanceof LightDynamicPenetrationRecord) {
                existingJson = ((LightDynamicPenetrationRecord) entity).getDataJson();
                if (existingJson == null || existingJson.isEmpty()) {
                    data.put("testDate", new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
                    data.put("blowCount", "5");
                    data.put("depth", "30");
                    data.put("soilProperty", "黏土");
                    ((LightDynamicPenetrationRecord) entity).setDataJson(objectMapper.writeValueAsString(data));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public SimpleDirectory getDirectoryById(String id) {
        try {
            return simpleDirectoryMapper.selectById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public SimpleDirectory getDirectoryByDirId(String dirId) {
        try {
            return simpleDirectoryMapper.selectByDirId(dirId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<SimpleDirectory> getAllDirectories() {
        try {
            return simpleDirectoryMapper.selectAll();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDirectory(String id) {
        try {
            // 1. Get the directory to find the wtNum (dirName)
            SimpleDirectory directory = simpleDirectoryMapper.selectById(id);
            if (directory == null) {
                return false;
            }
            String wtNum = directory.getDirName();

            // 2. Delete from related tables using wtNum (entrustmentId)
            if (wtNum != null && !wtNum.isEmpty()) {
                // Entrustment / JcCoreWtInfo
                jcCoreWtInfoMapper.deleteExtByWtNum(wtNum);
                jcCoreWtInfoMapper.deleteCoreByWtNum(wtNum);

                // Density Test
                densityTestRecordMapper.deleteByEntrustmentId(wtNum);
                densityTestReportMapper.deleteByEntrustmentId(wtNum);
                densityTestResultMapper.deleteByEntrustmentId(wtNum);

                // Rebound Method
                reboundMethodRecordMapper.deleteByEntrustmentId(wtNum);
                reboundMethodReportMapper.deleteByEntrustmentId(wtNum);
                reboundMethodResultMapper.deleteByEntrustmentId(wtNum);

                // Sand Replacement
                sandReplacementRecordMapper.deleteByEntrustmentId(wtNum);
                sandReplacementReportMapper.deleteByEntrustmentId(wtNum);
                sandReplacementResultMapper.deleteByEntrustmentId(wtNum);

                // Light Dynamic Penetration
                lightDynamicPenetrationRecordMapper.deleteByEntrustmentId(wtNum);
                lightDynamicPenetrationReportMapper.deleteByEntrustmentId(wtNum);
                lightDynamicPenetrationResultMapper.deleteByEntrustmentId(wtNum);

                // Beckman Beam
                beckmanBeamRecordMapper.deleteByEntrustmentId(wtNum);
                beckmanBeamReportMapper.deleteByEntrustmentId(wtNum);
                beckmanBeamResultMapper.deleteByEntrustmentId(wtNum);

                // Cutting Ring
                cuttingRingRecordMapper.deleteByEntrustmentId(wtNum);
                cuttingRingReportMapper.deleteByEntrustmentId(wtNum);
                cuttingRingResultMapper.deleteByEntrustmentId(wtNum);

                // Nuclear Density
                nuclearDensityRecordMapper.deleteByEntrustmentId(wtNum);
                nuclearDensityReportMapper.deleteByEntrustmentId(wtNum);
                nuclearDensityResultMapper.deleteByEntrustmentId(wtNum);

                // Water Replacement
                waterReplacementRecordMapper.deleteByEntrustmentId(wtNum);
                waterReplacementReportMapper.deleteByEntrustmentId(wtNum);
                waterReplacementResultMapper.deleteByEntrustmentId(wtNum);
            }

            // 3. Delete the directory itself
            int result = simpleDirectoryMapper.deleteById(id);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to delete directory and related records", e);
        }
    }

    private void syncRoles(SimpleDirectory directory) {
        updateRolesForTable(directory.getTable1Type(), directory.getTable1Id(), directory);
        updateRolesForTable(directory.getTable2Type(), directory.getTable2Id(), directory);
        updateRolesForTable(directory.getTable3Type(), directory.getTable3Id(), directory);
        updateRolesForTable(directory.getTable4Type(), directory.getTable4Id(), directory);
        updateRolesForTable(directory.getTable5Type(), directory.getTable5Id(), directory);
        updateRolesForTable(directory.getTable6Type(), directory.getTable6Id(), directory);
        updateRolesForTable(directory.getTable7Type(), directory.getTable7Id(), directory);
        updateRolesForTable(directory.getTable8Type(), directory.getTable8Id(), directory);
        updateRolesForTable(directory.getTable9Type(), directory.getTable9Id(), directory);
        updateRolesForTable(directory.getTable10Type(), directory.getTable10Id(), directory);
    }

    private void updateRolesForTable(String type, String id, SimpleDirectory directory) {
        if (type == null || id == null || id.isEmpty()) return;
        
        try {
            String tester = null;
            String reviewer = null;
            String approver = null;
            String filler = null;
            
            // Record-specific roles
            String recordTester = null;
            String recordReviewer = null;

            if (isEntrustment(type)) {
                tester = directory.getWtUndertaker();
                reviewer = directory.getWtReviewer();
                // Entrustment approver?
            } else if (type.toUpperCase().contains("RECORD")) {
                // For Record: Use Record-specific roles + Shared Approver
                recordTester = directory.getJcTester();     // 记录表检验人
                recordReviewer = directory.getJcReviewer(); // 记录表审核人
                filler = directory.getJcFiller();           // 记录表填写人
                approver = directory.getBgApprover();       // Shared Approver
            } else {
                // Report/Result: Use Background roles
                tester = directory.getBgTester();
                reviewer = directory.getBgReviewer();
                approver = directory.getBgApprover();
            }

            if (isEntrustment(type)) {
                JcCoreWtInfo entity = jcCoreWtInfoMapper.selectById(id);
                if (entity != null) {
                    entity.setTester(tester);
                    entity.setReviewer(reviewer);
                    // entity.setApprover(approver);
                    jcCoreWtInfoMapper.update(entity);
                }
            } else if (isTypeMatch(type, "DENSITY_TEST_RECORD", "原位密度检测记录表")) {
                DensityTestRecord entity = densityTestRecordMapper.selectById(id);
                if (entity != null) {
                    entity.setFiller(filler);
                    entity.setRecordTester(recordTester);
                    entity.setRecordReviewer(recordReviewer);
                    entity.setApprover(approver);
                    densityTestRecordMapper.updateById(entity);
                }
            } else if (isTypeMatch(type, "DENSITY_TEST_REPORT", "原位密度检测报告")) {
                DensityTestReport entity = densityTestReportMapper.selectById(id);
                if (entity != null) {
                    entity.setTester(tester); entity.setReviewer(reviewer); entity.setApprover(approver);
                    densityTestReportMapper.updateById(entity);
                }
            } else if (isTypeMatch(type, "DENSITY_TEST_RESULT", "原位密度检测结果")) {
                DensityTestResult entity = densityTestResultMapper.selectById(id);
                if (entity != null) {
                    entity.setTester(tester); entity.setReviewer(reviewer); entity.setApprover(approver);
                    densityTestResultMapper.updateById(entity);
                }
            } else if (isTypeMatch(type, "REBOUND_METHOD_RECORD", "回弹法检测混凝土抗压强度记录表")) {
                ReboundMethodRecord entity = reboundMethodRecordMapper.selectById(id);
                if (entity != null) {
                    entity.setFiller(filler);
                    entity.setRecordTester(recordTester);
                    entity.setRecordReviewer(recordReviewer);
                    entity.setApprover(approver);
                    reboundMethodRecordMapper.updateById(entity);
                }
            } else if (isTypeMatch(type, "REBOUND_METHOD_REPORT", "回弹法检测混凝土抗压强度报告")) {
                ReboundMethodReport entity = reboundMethodReportMapper.selectById(id);
                if (entity != null) {
                    entity.setTester(tester); entity.setReviewer(reviewer); entity.setApprover(approver);
                    reboundMethodReportMapper.updateById(entity);
                }
            } else if (isTypeMatch(type, "SAND_REPLACEMENT_RECORD", "原位密度检测记录表（灌砂法）")) {
                SandReplacementRecord entity = sandReplacementRecordMapper.selectById(id);
                if (entity != null) {
                    entity.setFiller(filler);
                    entity.setRecordTester(recordTester);
                    entity.setRecordReviewer(recordReviewer);
                    entity.setApprover(approver);
                    sandReplacementRecordMapper.updateById(entity);
                }
            } else if (isTypeMatch(type, "LIGHT_DYNAMIC_PENETRATION_RECORD", "轻型动力触探检测记录表")) {
                LightDynamicPenetrationRecord entity = lightDynamicPenetrationRecordMapper.selectById(id);
                if (entity != null) {
                    entity.setFiller(filler);
                    entity.setRecordTester(recordTester);
                    entity.setRecordReviewer(recordReviewer);
                    entity.setApprover(approver);
                    lightDynamicPenetrationRecordMapper.updateById(entity);
                }
            } else if (isTypeMatch(type, "LIGHT_DYNAMIC_PENETRATION", "轻型动力触探检测报告")) {
                LightDynamicPenetrationReport entity = lightDynamicPenetrationReportMapper.selectById(id);
                if (entity != null) {
                    entity.setTester(tester); entity.setReviewer(reviewer); entity.setApprover(approver);
                    lightDynamicPenetrationReportMapper.updateById(entity);
                }
            } else if (isTypeMatch(type, "LIGHT_DYNAMIC_PENETRATION_RESULT", "轻型动力触探检测结果")) {
                LightDynamicPenetrationResult entity = lightDynamicPenetrationResultMapper.selectById(id);
                if (entity != null) {
                    entity.setTester(tester); entity.setReviewer(reviewer); entity.setApprover(approver);
                    lightDynamicPenetrationResultMapper.updateById(entity);
                }
            } else if (isTypeMatch(type, "BECKMAN_BEAM_RECORD", "路基路面回弹弯沉试验检测记录表")) {
                BeckmanBeamRecord entity = beckmanBeamRecordMapper.selectById(id);
                if (entity != null) {
                    entity.setFiller(filler);
                    entity.setRecordTester(recordTester);
                    entity.setRecordReviewer(recordReviewer);
                    entity.setApprover(approver);
                    beckmanBeamRecordMapper.updateById(entity);
                }
            } else if (isTypeMatch(type, "BECKMAN_BEAM_REPORT", "路基路面回弹弯沉检测报告")) {
                BeckmanBeamReport entity = beckmanBeamReportMapper.selectById(id);
                if (entity != null) {
                    entity.setTester(tester); entity.setReviewer(reviewer); entity.setApprover(approver);
                    beckmanBeamReportMapper.updateById(entity);
                }
            } else if (isTypeMatch(type, "BECKMAN_BEAM_RESULT", "路基路面回弹弯沉检测结果")) {
                BeckmanBeamResult entity = beckmanBeamResultMapper.selectById(id);
                if (entity != null) {
                    entity.setTester(tester); entity.setReviewer(reviewer); entity.setApprover(approver);
                    beckmanBeamResultMapper.updateById(entity);
                }
            } else if (isTypeMatch(type, "CUTTING_RING_RECORD", "原位密度检测记录表（环刀法）")) {
                CuttingRingRecord entity = cuttingRingRecordMapper.selectById(id);
                if (entity != null) {
                    entity.setFiller(filler);
                    entity.setRecordTester(recordTester);
                    entity.setRecordReviewer(recordReviewer);
                    entity.setApprover(approver);
                    cuttingRingRecordMapper.updateById(entity);
                }
            } else if (isTypeMatch(type, "NUCLEAR_DENSITY_RECORD", "原位密度检测记录表（核子法）")) {
                NuclearDensityRecord entity = nuclearDensityRecordMapper.selectById(id);
                if (entity != null) {
                    entity.setFiller(filler);
                    entity.setRecordTester(recordTester);
                    entity.setRecordReviewer(recordReviewer);
                    entity.setApprover(approver);
                    nuclearDensityRecordMapper.updateById(entity);
                }
            } else if (isTypeMatch(type, "WATER_REPLACEMENT_RECORD", "相对密度试验记录表（灌水法）")) {
                WaterReplacementRecord entity = waterReplacementRecordMapper.selectById(id);
                if (entity != null) {
                    entity.setFiller(filler);
                    entity.setRecordTester(recordTester);
                    entity.setRecordReviewer(recordReviewer);
                    entity.setApprover(approver);
                    waterReplacementRecordMapper.updateById(entity);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
