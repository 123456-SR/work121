package org.example.work121.service.impl;

import org.example.work121.entity.*;
import org.example.work121.mapper.*;
import org.example.work121.service.SimpleDirectoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * 极简目录表服务实现类
 */
@Service
public class SimpleDirectoryServiceImpl implements SimpleDirectoryService {

    @Autowired
    private SimpleDirectoryMapper simpleDirectoryMapper;

    @Autowired
    private EntrustmentMapper entrustmentMapper;

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
    private ReboundMethodRecordMapper reboundMethodRecordMapper;

    @Autowired
    private LightDynamicPenetrationRecordMapper lightDynamicPenetrationRecordMapper;

    @Autowired
    private NuclearDensityRecordMapper nuclearDensityRecordMapper;

    @Autowired
    private SandReplacementRecordMapper sandReplacementRecordMapper;

    @Autowired
    private WaterReplacementRecordMapper waterReplacementRecordMapper;

    @Autowired
    private CuttingRingRecordMapper cuttingRingRecordMapper;

    @Autowired
    private BeckmanBeamRecordMapper beckmanBeamRecordMapper;

    @Autowired
    private DensityTestReportMapper densityTestReportMapper;

    @Autowired
    private DensityTestResultMapper densityTestResultMapper;

    @Autowired
    private LightDynamicPenetrationReportMapper lightDynamicPenetrationReportMapper;

    @Autowired
    private LightDynamicPenetrationResultMapper lightDynamicPenetrationResultMapper;

    @Autowired
    private ReboundMethodReportMapper reboundMethodReportMapper;

    @Autowired
    private BeckmanBeamReportMapper beckmanBeamReportMapper;

    @Autowired
    private BeckmanBeamResultMapper beckmanBeamResultMapper;

    @Autowired
    private JzsSignatureMapper jzsSignatureMapper;

    @Override
    public boolean saveDirectory(SimpleDirectory directory) {
        try {
            boolean isNew = false;
            // 检查是否已存在该目录
            SimpleDirectory existingRecord = null;
            if (directory.getDirId() != null) {
                existingRecord = simpleDirectoryMapper.selectByDirId(directory.getDirId());
            }

            // 为每个表类型创建空表并生成ID
            createEmptyTables(directory);
            
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
                    directory.setStatus(1);
                }

                result = simpleDirectoryMapper.insert(directory);
            }

            if (result > 0) {
                // 同步委托单数据到关联表
                syncEntrustmentData(directory);
                return true;
            }
            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void syncEntrustmentData(SimpleDirectory directory) {
        try {
            // 1. 查找委托单 (Source)
            Entrustment entrustment = null;
            if (isEntrustment(directory.getTable1Type())) entrustment = entrustmentMapper.selectById(directory.getTable1Id());
            else if (isEntrustment(directory.getTable2Type())) entrustment = entrustmentMapper.selectById(directory.getTable2Id());
            else if (isEntrustment(directory.getTable3Type())) entrustment = entrustmentMapper.selectById(directory.getTable3Id());
            else if (isEntrustment(directory.getTable4Type())) entrustment = entrustmentMapper.selectById(directory.getTable4Id());
            else if (isEntrustment(directory.getTable5Type())) entrustment = entrustmentMapper.selectById(directory.getTable5Id());
            else if (isEntrustment(directory.getTable6Type())) entrustment = entrustmentMapper.selectById(directory.getTable6Id());
            else if (isEntrustment(directory.getTable7Type())) entrustment = entrustmentMapper.selectById(directory.getTable7Id());
            else if (isEntrustment(directory.getTable8Type())) entrustment = entrustmentMapper.selectById(directory.getTable8Id());
            else if (isEntrustment(directory.getTable9Type())) entrustment = entrustmentMapper.selectById(directory.getTable9Id());
            else if (isEntrustment(directory.getTable10Type())) entrustment = entrustmentMapper.selectById(directory.getTable10Id());

            if (entrustment == null) {
                return;
            }

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
                "T_ENTRUSTMENT".equalsIgnoreCase(type) ||
                "JZS_ENTRUSTMENT".equalsIgnoreCase(type) ||
                "委托单".equals(type)
        );
    }

    private void syncToTable(String type, String id, Entrustment source) {
        if (type == null || id == null || isEntrustment(type)) {
            return;
        }

        if (isTypeMatch(type, "DENSITY_TEST", "T_DENSITY_TEST", "密度试验")) {
            DensityTestRecord target = densityTestRecordMapper.selectById(id);
            if (target != null) {
                target.setEntrustmentId(source.getUnifiedNumber());
                copyFields(source, target);
                densityTestRecordMapper.updateById(target);
            }
        } else if (isTypeMatch(type, "REBOUND_METHOD", "T_REBOUND_METHOD", "回弹法")) {
            ReboundMethod target = reboundMethodMapper.selectById(id);
            if (target != null) {
                target.setEntrustmentId(source.getUnifiedNumber());
                copyFields(source, target);
                reboundMethodMapper.updateById(target);
            }
        } else if (isTypeMatch(type, "SAND_REPLACEMENT", "T_SAND_REPLACEMENT", "灌砂法")) {
            SandReplacement target = sandReplacementMapper.selectById(id);
            if (target != null) {
                target.setEntrustmentId(source.getUnifiedNumber());
                copyFields(source, target);
                sandReplacementMapper.update(target);
            }
        } else if (isTypeMatch(type, "LIGHT_DYNAMIC_PENETRATION", "JZS_LIGHT_DYNAMIC_PENETRATION", "轻型动力触探")) {
            LightDynamicPenetration target = lightDynamicPenetrationMapper.selectById(id);
            if (target != null) {
                copyFields(source, target);
                lightDynamicPenetrationMapper.update(target);
            }
        } else if (isTypeMatch(type, "BECKMAN_BEAM", "T_BECKMAN_BEAM", "贝克曼梁")) {
            BeckmanBeam target = beckmanBeamMapper.selectById(id);
            if (target != null) {
                target.setEntrustmentId(source.getUnifiedNumber());
                beckmanBeamMapper.updateById(target);
            }
        } else if (isTypeMatch(type, "CUTTING_RING", "T_CUTTING_RING", "环刀法")) {
            CuttingRing target = cuttingRingMapper.selectById(id);
            if (target != null) {
                target.setEntrustmentId(source.getUnifiedNumber());
                cuttingRingMapper.updateById(target);
            }
        } else if (isTypeMatch(type, "NUCLEAR_DENSITY", "T_NUCLEAR_DENSITY", "核子密度仪")) {
            NuclearDensity target = nuclearDensityMapper.selectById(id);
            if (target != null) {
                target.setEntrustmentId(source.getUnifiedNumber());
                nuclearDensityMapper.updateById(target);
            }
        } else if (isTypeMatch(type, "WATER_REPLACEMENT", "T_WATER_REPLACEMENT", "灌水法")) {
            WaterReplacement target = waterReplacementMapper.selectById(id);
            if (target != null) {
                target.setEntrustmentId(source.getUnifiedNumber());
                waterReplacementMapper.updateById(target);
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

    private void copyFields(Entrustment source, BusinessEntity target) {
        if (source.getProjectName() != null) target.setProjectName(source.getProjectName());
        if (source.getClientUnit() != null) target.setClientUnit(source.getClientUnit());
        if (source.getWtNum() != null) target.setWtNum(source.getWtNum());
        if (source.getJcDate() != null) target.setCommissionDate(source.getJcDate());
        if (source.getConstructionUnit() != null) target.setConstructionUnit(source.getConstructionUnit());
        if (source.getSupervisionUnit() != null) target.setSupervisionUnit(source.getSupervisionUnit());
        if (source.getDesignUnit() != null) target.setDesignUnit(source.getDesignUnit());
        if (source.getWitnessUnit() != null) target.setWitnessUnit(source.getWitnessUnit());
        if (source.getWitness() != null) target.setWitness(source.getWitness());
        // Add more fields as needed
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
    public boolean deleteDirectory(String id) {
        try {
            int result = simpleDirectoryMapper.deleteById(id);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void createEmptyTables(SimpleDirectory directory) {
        try {
            System.out.println("开始为流程创建空表，流程名称: " + directory.getDirName());
            
            // 为每个表类型创建空表并生成ID
            for (int i = 1; i <= 10; i++) {
                String tableType = null;
                
                // 获取当前表的类型
                switch (i) {
                    case 1:
                        tableType = directory.getTable1Type();
                        break;
                    case 2:
                        tableType = directory.getTable2Type();
                        break;
                    case 3:
                        tableType = directory.getTable3Type();
                        break;
                    case 4:
                        tableType = directory.getTable4Type();
                        break;
                    case 5:
                        tableType = directory.getTable5Type();
                        break;
                    case 6:
                        tableType = directory.getTable6Type();
                        break;
                    case 7:
                        tableType = directory.getTable7Type();
                        break;
                    case 8:
                        tableType = directory.getTable8Type();
                        break;
                    case 9:
                        tableType = directory.getTable9Type();
                        break;
                    case 10:
                        tableType = directory.getTable10Type();
                        break;
                }
                
                // 如果表类型不为空，创建空表并生成ID
                if (tableType != null && !tableType.isEmpty()) {
                    System.out.println("创建表类型: " + tableType + "，序号: " + i);
                    String generatedId = createEmptyTable(tableType);
                    if (generatedId != null) {
                        System.out.println("成功创建表，生成的ID: " + generatedId);
                        // 将生成的ID设置到目录对象中
                        switch (i) {
                            case 1:
                                directory.setTable1Id(generatedId);
                                System.out.println("设置table1Id: " + generatedId);
                                break;
                            case 2:
                                directory.setTable2Id(generatedId);
                                System.out.println("设置table2Id: " + generatedId);
                                break;
                            case 3:
                                directory.setTable3Id(generatedId);
                                System.out.println("设置table3Id: " + generatedId);
                                break;
                            case 4:
                                directory.setTable4Id(generatedId);
                                System.out.println("设置table4Id: " + generatedId);
                                break;
                            case 5:
                                directory.setTable5Id(generatedId);
                                System.out.println("设置table5Id: " + generatedId);
                                break;
                            case 6:
                                directory.setTable6Id(generatedId);
                                System.out.println("设置table6Id: " + generatedId);
                                break;
                            case 7:
                                directory.setTable7Id(generatedId);
                                System.out.println("设置table7Id: " + generatedId);
                                break;
                            case 8:
                                directory.setTable8Id(generatedId);
                                System.out.println("设置table8Id: " + generatedId);
                                break;
                            case 9:
                                directory.setTable9Id(generatedId);
                                System.out.println("设置table9Id: " + generatedId);
                                break;
                            case 10:
                                directory.setTable10Id(generatedId);
                                System.out.println("设置table10Id: " + generatedId);
                                break;
                        }
                    } else {
                        System.out.println("创建表失败，类型: " + tableType);
                    }
                }
            }
            
            System.out.println("空表创建完成，流程信息: ");
            System.out.println("table1Type: " + directory.getTable1Type() + ", table1Id: " + directory.getTable1Id());
            System.out.println("table2Type: " + directory.getTable2Type() + ", table2Id: " + directory.getTable2Id());
            System.out.println("table3Type: " + directory.getTable3Type() + ", table3Id: " + directory.getTable3Id());
            System.out.println("table4Type: " + directory.getTable4Type() + ", table4Id: " + directory.getTable4Id());
        } catch (Exception e) {
            System.out.println("创建空表时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String createEmptyTable(String tableType) {
        try {
            String id = UUID.randomUUID().toString();
            System.out.println("开始创建表，类型: " + tableType + "，生成的ID: " + id);
            
            // 根据表类型创建对应的数据对象并插入
            switch (tableType) {
                case "ENTRUSTMENT_LIST":
                    Entrustment entrustment = new Entrustment();
                    entrustment.setId(id);
                    entrustment.setCreateBy("admin");
                    entrustment.setCreateTime(new java.util.Date());
                    int insertResult = entrustmentMapper.insert(entrustment);
                    System.out.println("插入委托单结果: " + insertResult);
                    if (insertResult > 0) {
                        System.out.println("成功创建委托单，ID: " + id);
                        return id;
                    } else {
                        System.out.println("创建委托单失败");
                        return null;
                    }
                case "REBOUND_METHOD_RECORD":
                    ReboundMethodRecord reboundRecord = new ReboundMethodRecord();
                    reboundRecord.setId(id);
                    reboundRecord.setEntrustmentId(id);
                    reboundRecord.setDataJson("");
                    int insertReboundResult = reboundMethodRecordMapper.insert(reboundRecord);
                    System.out.println("插入回弹法记录结果: " + insertReboundResult);
                    if (insertReboundResult > 0) {
                        System.out.println("成功创建回弹法记录，ID: " + id);
                        return id;
                    } else {
                        System.out.println("创建回弹法记录失败");
                        return null;
                    }
                case "LIGHT_DYNAMIC_PENETRATION_RECORD":
                    LightDynamicPenetrationRecord lightRecord = new LightDynamicPenetrationRecord();
                    lightRecord.setId(id);
                    lightRecord.setEntrustmentId(id);
                    lightRecord.setDataJson("");
                    int insertLightResult = lightDynamicPenetrationRecordMapper.insert(lightRecord);
                    System.out.println("插入轻型动力触探记录结果: " + insertLightResult);
                    if (insertLightResult > 0) {
                        System.out.println("成功创建轻型动力触探记录，ID: " + id);
                        return id;
                    } else {
                        System.out.println("创建轻型动力触探记录失败");
                        return null;
                    }
                case "NUCLEAR_DENSITY_RECORD":
                    NuclearDensityRecord nuclearRecord = new NuclearDensityRecord();
                    nuclearRecord.setId(id);
                    nuclearRecord.setEntrustmentId(id);
                    nuclearRecord.setDataJson("");
                    int insertNuclearResult = nuclearDensityRecordMapper.insert(nuclearRecord);
                    System.out.println("插入核子密度仪记录结果: " + insertNuclearResult);
                    if (insertNuclearResult > 0) {
                        System.out.println("成功创建核子密度仪记录，ID: " + id);
                        return id;
                    } else {
                        System.out.println("创建核子密度仪记录失败");
                        return null;
                    }
                case "SAND_REPLACEMENT_RECORD":
                    SandReplacementRecord sandRecord = new SandReplacementRecord();
                    sandRecord.setId(id);
                    sandRecord.setEntrustmentId(id);
                    sandRecord.setDataJson("");
                    int insertSandResult = sandReplacementRecordMapper.insert(sandRecord);
                    System.out.println("插入灌砂法记录结果: " + insertSandResult);
                    if (insertSandResult > 0) {
                        System.out.println("成功创建灌砂法记录，ID: " + id);
                        return id;
                    } else {
                        System.out.println("创建灌砂法记录失败");
                        return null;
                    }
                case "WATER_REPLACEMENT_RECORD":
                    WaterReplacementRecord waterRecord = new WaterReplacementRecord();
                    waterRecord.setId(id);
                    waterRecord.setEntrustmentId(id);
                    waterRecord.setDataJson("");
                    int insertWaterResult = waterReplacementRecordMapper.insert(waterRecord);
                    System.out.println("插入灌水法记录结果: " + insertWaterResult);
                    if (insertWaterResult > 0) {
                        System.out.println("成功创建灌水法记录，ID: " + id);
                        return id;
                    } else {
                        System.out.println("创建灌水法记录失败");
                        return null;
                    }
                case "CUTTING_RING_RECORD":
                    CuttingRingRecord cuttingRecord = new CuttingRingRecord();
                    cuttingRecord.setId(id);
                    cuttingRecord.setEntrustmentId(id);
                    cuttingRecord.setDataJson("");
                    int insertCuttingResult = cuttingRingRecordMapper.insert(cuttingRecord);
                    System.out.println("插入环刀法记录结果: " + insertCuttingResult);
                    if (insertCuttingResult > 0) {
                        System.out.println("成功创建环刀法记录，ID: " + id);
                        return id;
                    } else {
                        System.out.println("创建环刀法记录失败");
                        return null;
                    }
                case "BECKMAN_BEAM_RECORD":
                    BeckmanBeamRecord beckmanRecord = new BeckmanBeamRecord();
                    beckmanRecord.setId(id);
                    beckmanRecord.setEntrustmentId(id);
                    beckmanRecord.setDataJson("");
                    int insertBeckmanResult = beckmanBeamRecordMapper.insert(beckmanRecord);
                    System.out.println("插入贝克曼梁记录结果: " + insertBeckmanResult);
                    if (insertBeckmanResult > 0) {
                        System.out.println("成功创建贝克曼梁记录，ID: " + id);
                        return id;
                    } else {
                        System.out.println("创建贝克曼梁记录失败");
                        return null;
                    }
                case "DENSITY_TEST_REPORT":
                    DensityTestReport densityReport = new DensityTestReport();
                    densityReport.setId(id);
                    densityReport.setEntrustmentId(id);
                    densityReport.setDataJson("");
                    int insertDensityReportResult = densityTestReportMapper.insert(densityReport);
                    System.out.println("插入密度试验报告结果: " + insertDensityReportResult);
                    if (insertDensityReportResult > 0) {
                        System.out.println("成功创建密度试验报告，ID: " + id);
                        return id;
                    } else {
                        System.out.println("创建密度试验报告失败");
                        return null;
                    }
                case "DENSITY_TEST_RESULT":
                    DensityTestResult densityResult = new DensityTestResult();
                    densityResult.setId(id);
                    densityResult.setEntrustmentId(id);
                    densityResult.setDataJson("");
                    int insertDensityResultResult = densityTestResultMapper.insert(densityResult);
                    System.out.println("插入密度试验检测结果: " + insertDensityResultResult);
                    if (insertDensityResultResult > 0) {
                        System.out.println("成功创建密度试验检测结果，ID: " + id);
                        return id;
                    } else {
                        System.out.println("创建密度试验检测结果失败");
                        return null;
                    }
                case "LIGHT_DYNAMIC_PENETRATION":
                    LightDynamicPenetrationReport lightReport = new LightDynamicPenetrationReport();
                    lightReport.setId(id);
                    lightReport.setEntrustmentId(id);
                    lightReport.setDataJson("");
                    int insertLightReportResult = lightDynamicPenetrationReportMapper.insert(lightReport);
                    System.out.println("插入轻型动力触探报告结果: " + insertLightReportResult);
                    if (insertLightReportResult > 0) {
                        System.out.println("成功创建轻型动力触探报告，ID: " + id);
                        return id;
                    } else {
                        System.out.println("创建轻型动力触探报告失败");
                        return null;
                    }
                case "LIGHT_DYNAMIC_PENETRATION_RESULT":
                    LightDynamicPenetrationResult lightResult = new LightDynamicPenetrationResult();
                    lightResult.setId(id);
                    lightResult.setEntrustmentId(id);
                    lightResult.setDataJson("");
                    int insertLightResultResult = lightDynamicPenetrationResultMapper.insert(lightResult);
                    System.out.println("插入轻型动力触探检测结果: " + insertLightResultResult);
                    if (insertLightResultResult > 0) {
                        System.out.println("成功创建轻型动力触探检测结果，ID: " + id);
                        return id;
                    } else {
                        System.out.println("创建轻型动力触探检测结果失败");
                        return null;
                    }
                case "REBOUND_METHOD_REPORT":
                    ReboundMethodReport reboundReport = new ReboundMethodReport();
                    reboundReport.setId(id);
                    reboundReport.setEntrustmentId(id);
                    reboundReport.setDataJson("");
                    int insertReboundReportResult = reboundMethodReportMapper.insert(reboundReport);
                    System.out.println("插入回弹法报告结果: " + insertReboundReportResult);
                    if (insertReboundReportResult > 0) {
                        System.out.println("成功创建回弹法报告，ID: " + id);
                        return id;
                    } else {
                        System.out.println("创建回弹法报告失败");
                        return null;
                    }
                case "BECKMAN_BEAM_REPORT":
                    BeckmanBeamReport beckmanReport = new BeckmanBeamReport();
                    beckmanReport.setId(id);
                    beckmanReport.setEntrustmentId(id);
                    beckmanReport.setDataJson("");
                    int insertBeckmanReportResult = beckmanBeamReportMapper.insert(beckmanReport);
                    System.out.println("插入贝克曼梁报告结果: " + insertBeckmanReportResult);
                    if (insertBeckmanReportResult > 0) {
                        System.out.println("成功创建贝克曼梁报告，ID: " + id);
                        return id;
                    } else {
                        System.out.println("创建贝克曼梁报告失败");
                        return null;
                    }
                case "BECKMAN_BEAM_RESULT":
                    BeckmanBeamResult beckmanResult = new BeckmanBeamResult();
                    beckmanResult.setId(id);
                    beckmanResult.setEntrustmentId(id);
                    beckmanResult.setDataJson("");
                    int insertBeckmanResultResult = beckmanBeamResultMapper.insert(beckmanResult);
                    System.out.println("插入贝克曼梁检测结果: " + insertBeckmanResultResult);
                    if (insertBeckmanResultResult > 0) {
                        System.out.println("成功创建贝克曼梁检测结果，ID: " + id);
                        return id;
                    } else {
                        System.out.println("创建贝克曼梁检测结果失败");
                        return null;
                    }
                case "SIGNATURE":
                    JzsSignature signature = new JzsSignature();
                    signature.setSignatureId(id);
                    signature.setUserAccount("admin");
                    signature.setSignatureType("default");
                    signature.setImageType("png");
                    signature.setImageSize(0L);
                    signature.setCreateTime(new java.util.Date());
                    signature.setRemarks("自动创建的签名记录");
                    int insertSignatureResult = jzsSignatureMapper.insert(signature);
                    System.out.println("插入签名记录结果: " + insertSignatureResult);
                    if (insertSignatureResult > 0) {
                        System.out.println("成功创建签名记录，ID: " + id);
                        return id;
                    } else {
                        System.out.println("创建签名记录失败");
                        return null;
                    }
                // 可以添加其他表类型的处理
                default:
                    System.out.println("未知表类型: " + tableType);
                    return null;
            }
        } catch (Exception e) {
            System.out.println("创建表时发生异常，类型: " + tableType + "，异常信息: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
