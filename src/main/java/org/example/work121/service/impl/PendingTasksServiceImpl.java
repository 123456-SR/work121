package org.example.work121.service.impl;

import org.example.work121.entity.JzsSignature;
import org.example.work121.entity.SimpleDirectory;
import org.example.work121.entity.DensityTest;
import org.example.work121.entity.ReboundMethod;
import org.example.work121.entity.SandReplacement;
import org.example.work121.entity.WaterReplacement;
import org.example.work121.entity.NuclearDensity;
import org.example.work121.entity.CuttingRing;
import org.example.work121.entity.BeckmanBeam;
import org.example.work121.entity.LightDynamicPenetration;
import org.example.work121.entity.JcCoreWtInfo;
import org.example.work121.mapper.*;
import org.example.work121.service.JzsSignatureService;
import org.example.work121.service.PendingTasksService;
import org.example.work121.service.SimpleDirectoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    
    @Autowired
    private SimpleDirectoryService simpleDirectoryService;

    @Override
    public List<Map<String, Object>> getAllPendingTasks(String status, String userAccount) {
        if (userAccount != null && !userAccount.trim().isEmpty()) {
            return convertTimeFields(pendingTasksMapper.getPendingTasksByUser(userAccount.trim(), status));
        }
        return convertTimeFields(pendingTasksMapper.getAllPendingTasks(status));
    }

    @Override
    public List<Map<String, Object>> searchPendingTasks(String taskType, String status, String userAccount) {
        // 现阶段 search SQL 只按 taskType 文本匹配（与历史实现保持一致），附带 status 过滤
        // userAccount 如有传入，复用 get-by-user 的 reviewer 过滤逻辑（结果再在内存中过滤类型）
        if (userAccount != null && !userAccount.trim().isEmpty()) {
            List<Map<String, Object>> base = convertTimeFields(pendingTasksMapper.getPendingTasksByUser(userAccount.trim(), status));
            if (taskType == null || taskType.trim().isEmpty()) return base;
            String kw = taskType.trim();
            return base.stream()
                    .filter(m -> {
                        Object t = m.get("table_type");
                        return t != null && String.valueOf(t).contains(kw);
                    })
                    .collect(java.util.stream.Collectors.toList());
        }
        return convertTimeFields(pendingTasksMapper.searchPendingTasks(taskType, status));
    }

    private List<Map<String, Object>> convertTimeFields(List<Map<String, Object>> tasks) {
        return tasks.stream().map(task -> {
            Map<String, Object> convertedTask = new java.util.HashMap<>(task);
            // 处理CREATE_TIME字段
            Object createTime = task.get("CREATE_TIME");
            if (createTime != null) {
                try {
                    // 尝试将oracle.sql.TIMESTAMP转换为java.util.Date
                    if (createTime instanceof oracle.sql.TIMESTAMP) {
                        convertedTask.put("CREATE_TIME", ((oracle.sql.TIMESTAMP) createTime).timestampValue());
                    }
                } catch (Exception e) {
                    // 忽略转换错误
                }
            }
            // 处理create_time字段
            Object createTimeLower = task.get("create_time");
            if (createTimeLower != null) {
                try {
                    // 尝试将oracle.sql.TIMESTAMP转换为java.util.Date
                    if (createTimeLower instanceof oracle.sql.TIMESTAMP) {
                        convertedTask.put("create_time", ((oracle.sql.TIMESTAMP) createTimeLower).timestampValue());
                    }
                } catch (Exception e) {
                    // 忽略转换错误
                }
            }
            return convertedTask;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getPendingTasksByUser(String userAccount, String status) {
        System.out.println("PendingTasksServiceImpl.getPendingTasksByUser: userAccount=" + userAccount + ", status=" + status);
        List<Map<String, Object>> result = pendingTasksMapper.getPendingTasksByUser(userAccount, status);
        System.out.println("PendingTasksServiceImpl.getPendingTasksByUser: 返回 " + result.size() + " 个任务");
        return convertTimeFields(result);
    }

    @Override
    public boolean approveTask(String taskType, String taskId, String userAccount) {
        try {
            String approveSignPhoto = null;
            
            if (userAccount != null && !userAccount.isEmpty()) {
                JzsSignature signature = jzsSignatureService.getSignatureByUserAccount(userAccount);
                if (signature != null && signature.getSignatureBlob() != null) {
                    approveSignPhoto = Base64.getEncoder().encodeToString(signature.getSignatureBlob());
                }
            }
            
            boolean result = false;
            String unifiedNumber = null;
            
            switch (taskType) {
                case "委托单":
                    boolean success;
                    if (approveSignPhoto != null) {
                        success = jcCoreWtInfoMapper.updateStatusAndApproveSign(taskId, "5", approveSignPhoto) > 0;
                    } else {
                        success = jcCoreWtInfoMapper.updateStatusById(taskId, "5") > 0;
                    }
                    
                    // 委托单审核通过时，自动创建记录表
                    if (success) {
                        try {
                            // 获取委托单信息
                            JcCoreWtInfo entrustment = jcCoreWtInfoMapper.selectById(taskId);
                            if (entrustment != null) {
                                unifiedNumber = entrustment.getWtNum();
                                if (unifiedNumber != null) {
                                    // 根据wtNum获取目录
                                    SimpleDirectory directory = simpleDirectoryService.getDirectoryByDirName(unifiedNumber);
                                    if (directory != null) {
                                        // 创建记录表
                                        System.out.println("=== 开始创建记录表 ===");
                                        System.out.println("统一编号: " + unifiedNumber);
                                        
                                        // 获取检测项目及依据
                                        String testItems = entrustment.getTestItems();
                                        System.out.println("检测项目及依据: " + testItems);
                                        
                                        // 根据检测项目及依据确定表类型
                                        java.util.List<String> tableTypes = determineTableTypesByTestItems(testItems);
                                        System.out.println("根据检测项目确定的表类型: " + tableTypes);
                                        
                                        // 确定检测类别
                                        java.util.Set<String> categories = new java.util.LinkedHashSet<>();
                                        for (String tableType : tableTypes) {
                                            if (tableType == null) continue;
                                            String upper = tableType.toUpperCase();
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
                                        
                                        String creator = directory.getCreateBy();
                                        if (creator == null) {
                                            creator = "admin";
                                        }
                                        
                                        // 根据确定的表类型创建记录表
                                        for (int i = 0; i < tableTypes.size(); i++) {
                                            String tableType = tableTypes.get(i);
                                            if (tableType != null) {
                                                System.out.println("创建表" + (i + 1) + " - 类型: " + tableType);
                                                String tableId = createRelatedRecord(tableType, unifiedNumber, creator, category, directory);
                                                // 根据索引设置相应的表ID
                                                switch (i) {
                                                    case 0: directory.setTable1Id(tableId); break;
                                                    case 1: directory.setTable2Id(tableId); break;
                                                    case 2: directory.setTable3Id(tableId); break;
                                                    case 3: directory.setTable4Id(tableId); break;
                                                    case 4: directory.setTable5Id(tableId); break;
                                                    case 5: directory.setTable6Id(tableId); break;
                                                    case 6: directory.setTable7Id(tableId); break;
                                                    case 7: directory.setTable8Id(tableId); break;
                                                    case 8: directory.setTable9Id(tableId); break;
                                                    case 9: directory.setTable10Id(tableId); break;
                                                }
                                                System.out.println("表" + (i + 1) + "创建成功，ID: " + tableId);
                                            }
                                        }
                                        
                                        // 更新目录
                                        simpleDirectoryService.update(directory);
                                        System.out.println("目录更新成功");
                                        System.out.println("=== 记录表创建完成 ====");
                                    }
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("创建记录表失败: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    result = success;
                    break;
                case "贝克曼梁":
                    if (approveSignPhoto != null) {
                        result = beckmanBeamMapper.updateStatusAndApproveSign(taskId, "5", approveSignPhoto) > 0;
                    } else {
                        result = beckmanBeamMapper.updateStatusById(taskId, "5") > 0;
                    }
                    if (result) {
                        org.example.work121.entity.BeckmanBeam beckmanBeam = beckmanBeamMapper.selectById(taskId);
                        if (beckmanBeam != null) {
                            unifiedNumber = beckmanBeam.getEntrustmentId();
                        }
                    }
                    break;
                case "轻型动力触探":
                    if (approveSignPhoto != null) {
                        result = lightDynamicPenetrationMapper.updateStatusAndApproveSign(taskId, "5", approveSignPhoto) > 0;
                    } else {
                        result = lightDynamicPenetrationMapper.updateStatusById(taskId, "5") > 0;
                    }
                    if (result) {
                        org.example.work121.entity.LightDynamicPenetration lightDynamicPenetration = lightDynamicPenetrationMapper.selectById(taskId);
                        if (lightDynamicPenetration != null) {
                            unifiedNumber = lightDynamicPenetration.getEntrustmentId();
                        }
                    }
                    break;
                case "回弹法":
                    if (approveSignPhoto != null) {
                        result = reboundMethodMapper.updateStatusAndApproveSign(taskId, "5", approveSignPhoto) > 0;
                    } else {
                        result = reboundMethodMapper.updateStatusById(taskId, "5") > 0;
                    }
                    if (result) {
                        org.example.work121.entity.ReboundMethod reboundMethod = reboundMethodMapper.selectById(taskId);
                        if (reboundMethod != null) {
                            unifiedNumber = reboundMethod.getEntrustmentId();
                        }
                    }
                    break;
                case "环刀法":
                    if (approveSignPhoto != null) {
                        result = cuttingRingMapper.updateStatusAndApproveSign(taskId, "5", approveSignPhoto) > 0;
                    } else {
                        result = cuttingRingMapper.updateStatusById(taskId, "5") > 0;
                    }
                    if (result) {
                        org.example.work121.entity.CuttingRing cuttingRing = cuttingRingMapper.selectById(taskId);
                        if (cuttingRing != null) {
                            unifiedNumber = cuttingRing.getEntrustmentId();
                        }
                    }
                    break;
                case "灌水法":
                    if (approveSignPhoto != null) {
                        result = waterReplacementMapper.updateStatusAndApproveSign(taskId, "5", approveSignPhoto) > 0;
                    } else {
                        result = waterReplacementMapper.updateStatusById(taskId, "5") > 0;
                    }
                    if (result) {
                        org.example.work121.entity.WaterReplacement waterReplacement = waterReplacementMapper.selectById(taskId);
                        if (waterReplacement != null) {
                            unifiedNumber = waterReplacement.getEntrustmentId();
                        }
                    }
                    break;
                case "灌砂法":
                    if (approveSignPhoto != null) {
                        result = sandReplacementMapper.updateStatusAndApproveSign(taskId, "5", approveSignPhoto) > 0;
                    } else {
                        result = sandReplacementMapper.updateStatusById(taskId, "5") > 0;
                    }
                    if (result) {
                        org.example.work121.entity.SandReplacement sandReplacement = sandReplacementMapper.selectById(taskId);
                        if (sandReplacement != null) {
                            unifiedNumber = sandReplacement.getEntrustmentId();
                        }
                    }
                    break;
                case "核子密度":
                    if (approveSignPhoto != null) {
                        result = nuclearDensityMapper.updateStatusAndApproveSign(taskId, "5", approveSignPhoto) > 0;
                    } else {
                        result = nuclearDensityMapper.updateStatusById(taskId, "5") > 0;
                    }
                    if (result) {
                        org.example.work121.entity.NuclearDensity nuclearDensity = nuclearDensityMapper.selectById(taskId);
                        if (nuclearDensity != null) {
                            unifiedNumber = nuclearDensity.getEntrustmentId();
                        }
                    }
                    break;
                case "密度试验":
                    if (approveSignPhoto != null) {
                        result = densityTestMapper.updateStatusAndApproveSign(taskId, "5", approveSignPhoto) > 0;
                    } else {
                        result = densityTestMapper.updateStatusById(taskId, "5") > 0;
                    }
                    if (result) {
                        org.example.work121.entity.DensityTest densityTest = densityTestMapper.selectById(taskId);
                        if (densityTest != null) {
                            unifiedNumber = densityTest.getEntrustmentId();
                        }
                    }
                    break;
                default:
                    return false;
            }
            
            // 更新报告表和结果表的reportStatus字段为1
            if (result && unifiedNumber != null) {
                try {
                    // 根据统一编号更新所有相关的报告表和结果表
                    System.out.println("更新报告表和结果表的reportStatus字段为1，统一编号: " + unifiedNumber);
                    
                    // 调用各个mapper的updateReportAndResultStatus方法
                    densityTestMapper.updateReportAndResultStatus(unifiedNumber, "1", "1");
                    nuclearDensityMapper.updateReportAndResultStatus(unifiedNumber, "1", "1");
                    sandReplacementMapper.updateReportAndResultStatus(unifiedNumber, "1", "1");
                    waterReplacementMapper.updateReportAndResultStatus(unifiedNumber, "1", "1");
                    cuttingRingMapper.updateReportAndResultStatus(unifiedNumber, "1", "1");
                    reboundMethodMapper.updateReportAndResultStatus(unifiedNumber, "1", "1");
                    lightDynamicPenetrationMapper.updateReportAndResultStatus(unifiedNumber, "1", "1");
                    beckmanBeamMapper.updateReportAndResultStatus(unifiedNumber, "1", "1");
                } catch (Exception e) {
                    System.err.println("更新报告表和结果表失败: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 根据检测项目及依据确定表类型
     * @param testItems 检测项目及依据
     * @return 表类型列表
     */
    private java.util.List<String> determineTableTypesByTestItems(String testItems) {
        java.util.List<String> tableTypes = new java.util.ArrayList<>();
        
        if (testItems == null || testItems.isEmpty()) {
            return tableTypes;
        }
        
        // 转换为大写进行匹配
        String upperTestItems = testItems.toUpperCase();
        
        // 模糊匹配表类型
        if (upperTestItems.contains("核子") || upperTestItems.contains("NUCLEAR")) {
            tableTypes.add("NUCLEAR_DENSITY");
        }
        if (upperTestItems.contains("灌砂") || upperTestItems.contains("SAND")) {
            tableTypes.add("SAND_REPLACEMENT");
        }
        if (upperTestItems.contains("灌水") || upperTestItems.contains("WATER")) {
            tableTypes.add("WATER_REPLACEMENT");
        }
        if (upperTestItems.contains("环刀") || upperTestItems.contains("CUTTING")) {
            tableTypes.add("CUTTING_RING");
        }
        if (upperTestItems.contains("回弹") || upperTestItems.contains("REBOUND")) {
            tableTypes.add("REBOUND_METHOD");
        }
        if (upperTestItems.contains("轻型动力触探") || upperTestItems.contains("PENETRATION")) {
            tableTypes.add("LIGHT_DYNAMIC_PENETRATION");
        }
        if (upperTestItems.contains("贝克曼梁") || upperTestItems.contains("BECKMAN")) {
            tableTypes.add("BECKMAN_BEAM");
        }
        if (upperTestItems.contains("密度") || upperTestItems.contains("DENSITY")) {
            tableTypes.add("DENSITY_TEST");
        }
        
        return tableTypes;
    }
    
    private String createRelatedRecord(String tableType, String dirName, String creator, String category, SimpleDirectory directory) {
        try {
            String recordId = java.util.UUID.randomUUID().toString().replace("-", "");
            java.util.Date now = new java.util.Date();
            
            // 生成与委托单相关联的记录表（包括报告和结果表）
            if (tableType.contains("DENSITY_TEST")) {
                org.example.work121.entity.DensityTest densityTest = new org.example.work121.entity.DensityTest();
                densityTest.setId(recordId);
                densityTest.setEntrustmentId(dirName);
                densityTest.setCreateBy(creator);
                densityTest.setCreateTime(now);
                densityTest.setStatus("0");
                // 设置角色信息
                if (directory != null) {
                    densityTest.setFiller(directory.getJcFiller());
                    densityTest.setRecordTester(directory.getJcTester());
                    densityTest.setRecordReviewer(directory.getJcReviewer());
                    densityTest.setTester(directory.getJcTester());
                    densityTest.setReviewer(directory.getJcReviewer());
                    densityTest.setApprover(directory.getJcTester());
                }
                densityTestMapper.insert(densityTest);
                System.out.println("创建密度试验记录表成功，ID: " + recordId);
            }
            else if (tableType.contains("REBOUND_METHOD")) {
                org.example.work121.entity.ReboundMethod reboundMethod = new org.example.work121.entity.ReboundMethod();
                reboundMethod.setId(recordId);
                reboundMethod.setEntrustmentId(dirName);
                reboundMethod.setCreateBy(creator);
                reboundMethod.setCreateTime(now);
                reboundMethod.setStatus("0");
                // 设置角色信息
                if (directory != null) {
                    reboundMethod.setFiller(directory.getJcFiller());
                    reboundMethod.setRecordTester(directory.getJcTester());
                    reboundMethod.setRecordReviewer(directory.getJcReviewer());
                    reboundMethod.setTester(directory.getJcTester());
                    reboundMethod.setReviewer(directory.getJcReviewer());
                    reboundMethod.setApprover(directory.getJcTester());
                }
                reboundMethodMapper.insert(reboundMethod);
                System.out.println("创建回弹法记录表成功，ID: " + recordId);
            }
            else if (tableType.contains("SAND_REPLACEMENT")) {
                org.example.work121.entity.SandReplacement sandReplacement = new org.example.work121.entity.SandReplacement();
                sandReplacement.setId(recordId);
                sandReplacement.setEntrustmentId(dirName);
                sandReplacement.setCreateBy(creator);
                sandReplacement.setCreateTime(now);
                sandReplacement.setStatus("0");
                // 设置角色信息
                if (directory != null) {
                    sandReplacement.setFiller(directory.getJcFiller());
                    sandReplacement.setRecordTester(directory.getJcTester());
                    sandReplacement.setRecordReviewer(directory.getJcReviewer());
                    sandReplacement.setTester(directory.getJcTester());
                    sandReplacement.setReviewer(directory.getJcReviewer());
                    sandReplacement.setApprover(directory.getJcTester());
                }
                sandReplacementMapper.insert(sandReplacement);
                System.out.println("创建灌砂法记录表成功，ID: " + recordId);
            }
            else if (tableType.contains("WATER_REPLACEMENT")) {
                org.example.work121.entity.WaterReplacement waterReplacement = new org.example.work121.entity.WaterReplacement();
                waterReplacement.setId(recordId);
                waterReplacement.setEntrustmentId(dirName);
                waterReplacement.setCreateBy(creator);
                waterReplacement.setCreateTime(now);
                waterReplacement.setStatus("0");
                // 设置角色信息
                if (directory != null) {
                    waterReplacement.setFiller(directory.getJcFiller());
                    waterReplacement.setRecordTester(directory.getJcTester());
                    waterReplacement.setRecordReviewer(directory.getJcReviewer());
                    waterReplacement.setTester(directory.getJcTester());
                    waterReplacement.setReviewer(directory.getJcReviewer());
                    waterReplacement.setApprover(directory.getJcTester());
                }
                waterReplacementMapper.insert(waterReplacement);
                System.out.println("创建灌水法记录表成功，ID: " + recordId);
            }
            else if (tableType.contains("NUCLEAR_DENSITY")) {
                org.example.work121.entity.NuclearDensity nuclearDensity = new org.example.work121.entity.NuclearDensity();
                nuclearDensity.setId(recordId);
                nuclearDensity.setEntrustmentId(dirName);
                nuclearDensity.setCreateBy(creator);
                nuclearDensity.setCreateTime(now);
                nuclearDensity.setStatus("0");
                // 设置角色信息
                if (directory != null) {
                    nuclearDensity.setFiller(directory.getJcFiller());
                    nuclearDensity.setRecordTester(directory.getJcTester());
                    nuclearDensity.setRecordReviewer(directory.getJcReviewer());
                    nuclearDensity.setTester(directory.getJcTester());
                    nuclearDensity.setReviewer(directory.getJcReviewer());
                    nuclearDensity.setApprover(directory.getJcTester());
                }
                nuclearDensityMapper.insert(nuclearDensity);
                System.out.println("创建核子密度记录表成功，ID: " + recordId);
            }
            else if (tableType.contains("CUTTING_RING")) {
                org.example.work121.entity.CuttingRing cuttingRing = new org.example.work121.entity.CuttingRing();
                cuttingRing.setId(recordId);
                cuttingRing.setEntrustmentId(dirName);
                cuttingRing.setCreateBy(creator);
                cuttingRing.setCreateTime(now);
                cuttingRing.setStatus("0");
                // 设置角色信息
                if (directory != null) {
                    cuttingRing.setFiller(directory.getJcFiller());
                    cuttingRing.setRecordTester(directory.getJcTester());
                    cuttingRing.setRecordReviewer(directory.getJcReviewer());
                    cuttingRing.setTester(directory.getJcTester());
                    cuttingRing.setReviewer(directory.getJcReviewer());
                    cuttingRing.setApprover(directory.getJcTester());
                }
                cuttingRingMapper.insert(cuttingRing);
                System.out.println("创建环刀法记录表成功，ID: " + recordId);
            }
            else if (tableType.contains("BECKMAN_BEAM")) {
                org.example.work121.entity.BeckmanBeam beckmanBeam = new org.example.work121.entity.BeckmanBeam();
                beckmanBeam.setId(recordId);
                beckmanBeam.setEntrustmentId(dirName);
                beckmanBeam.setCreateBy(creator);
                beckmanBeam.setCreateTime(now);
                beckmanBeam.setStatus("0");
                // 设置角色信息
                if (directory != null) {
                    beckmanBeam.setFiller(directory.getJcFiller());
                    beckmanBeam.setRecordTester(directory.getJcTester());
                    beckmanBeam.setRecordReviewer(directory.getJcReviewer());
                    beckmanBeam.setTester(directory.getJcTester());
                    beckmanBeam.setReviewer(directory.getJcReviewer());
                    beckmanBeam.setApprover(directory.getJcTester());
                }
                beckmanBeamMapper.insert(beckmanBeam);
                System.out.println("创建贝克曼梁记录表成功，ID: " + recordId);
            }
            else if (tableType.contains("LIGHT_DYNAMIC_PENETRATION")) {
                org.example.work121.entity.LightDynamicPenetration lightDynamicPenetration = new org.example.work121.entity.LightDynamicPenetration();
                lightDynamicPenetration.setId(recordId);
                lightDynamicPenetration.setEntrustmentId(dirName);
                lightDynamicPenetration.setCreateBy(creator);
                lightDynamicPenetration.setCreateTime(now);
                lightDynamicPenetration.setStatus("0");
                // 设置角色信息
                if (directory != null) {
                    lightDynamicPenetration.setFiller(directory.getJcFiller());
                    lightDynamicPenetration.setRecordTester(directory.getJcTester());
                    lightDynamicPenetration.setRecordReviewer(directory.getJcReviewer());
                    lightDynamicPenetration.setTester(directory.getJcTester());
                    lightDynamicPenetration.setReviewer(directory.getJcReviewer());
                    lightDynamicPenetration.setApprover(directory.getJcTester());
                }
                lightDynamicPenetrationMapper.insert(lightDynamicPenetration);
                System.out.println("创建轻型动力触探记录表成功，ID: " + recordId);
            }
            else if (tableType.contains("ENTRUSTMENT")) {
                // 委托单列表（主记录）的创建逻辑在 SimpleDirectoryServiceImpl 中，
                // 在此处仅做占位，不在本方法中创建。
            }
            else {
                System.err.println("Unsupported table type: " + tableType);
                return null;
            }
            
            return recordId;
        } catch (Exception e) {
            System.err.println("Failed to create related record for tableType: " + tableType + ", dirName: " + dirName);
            e.printStackTrace();
            return null;
        }
    }
}
