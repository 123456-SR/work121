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
import org.example.work121.service.TableGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    
    @Autowired
    private SimpleDirectoryMapper simpleDirectoryMapper;
    
    @Autowired
    private TableGenerationService tableGenerationService;

    @Override
    public List<Map<String, Object>> getAllPendingTasks(String status, String userAccount) {
        List<Map<String, Object>> base = convertTimeFields(pendingTasksMapper.getAllPendingTasks(status));
        if (userAccount == null || userAccount.trim().isEmpty()) return base;
        String ua = userAccount.trim();
        return base.stream()
                .filter(m -> {
                    Object reviewer = m.get("reviewer");
                    if (reviewer == null) reviewer = m.get("REVIEWER");
                    return reviewer != null && ua.equals(String.valueOf(reviewer));
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> searchPendingTasks(String taskType, String status, String userAccount) {
        List<Map<String, Object>> base = convertTimeFields(pendingTasksMapper.getAllPendingTasks(status));
        if (userAccount != null && !userAccount.trim().isEmpty()) {
            String ua = userAccount.trim();
            base = base.stream()
                    .filter(m -> {
                        Object reviewer = m.get("reviewer");
                        if (reviewer == null) reviewer = m.get("REVIEWER");
                        return reviewer != null && ua.equals(String.valueOf(reviewer));
                    })
                    .collect(Collectors.toList());
        }
        if (taskType == null || taskType.trim().isEmpty()) return base;
        String kw = taskType.trim();
        if ("audit".equalsIgnoreCase(kw) || "submit".equalsIgnoreCase(kw) || "approval".equalsIgnoreCase(kw)) {
            return base;
        }
        return base.stream()
                .filter(m -> {
                    Object t = m.get("table_type");
                    return t != null && String.valueOf(t).contains(kw);
                })
                .collect(Collectors.toList());
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
        List<Map<String, Object>> base = convertTimeFields(pendingTasksMapper.getAllPendingTasks(status));
        String ua = userAccount == null ? "" : userAccount.trim();
        List<Map<String, Object>> filtered = base.stream()
                .filter(m -> {
                    Object reviewer = m.get("reviewer");
                    if (reviewer == null) reviewer = m.get("REVIEWER");
                    return reviewer != null && ua.equals(String.valueOf(reviewer));
                })
                .collect(Collectors.toList());
        System.out.println("PendingTasksServiceImpl.getPendingTasksByUser: 返回 " + filtered.size() + " 个任务");
        return filtered;
    }

    @Override
    @Transactional
    public boolean approveTask(String taskType, String taskId, String userAccount, String jcTester, String jcReviewer, String bgApprover) {
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
                    JcCoreWtInfo entrustmentBefore = jcCoreWtInfoMapper.selectById(taskId);
                    if (entrustmentBefore == null) {
                        entrustmentBefore = jcCoreWtInfoMapper.selectExtById(taskId);
                    }
                    if (entrustmentBefore == null) {
                        throw new RuntimeException("未找到委托单，无法审核通过");
                    }
                    unifiedNumber = entrustmentBefore.getWtNum();
                    if (unifiedNumber == null || unifiedNumber.trim().isEmpty()) {
                        throw new RuntimeException("委托单统一编号为空，无法分配角色/创建记录表");
                    }

                    String jt = jcTester == null ? "" : jcTester.trim();
                    String jr = jcReviewer == null ? "" : jcReviewer.trim();
                    String ba = bgApprover == null ? "" : bgApprover.trim();
                    if (jt.isEmpty() || jr.isEmpty() || ba.isEmpty()) {
                        throw new RuntimeException("委托单审核通过前必须指定：记录表检测人、记录表审核人、报告/结果批准人");
                    }

                    SimpleDirectory directoryForRoles = simpleDirectoryService.getDirectoryByDirName(unifiedNumber);
                    if (directoryForRoles == null) {
                        directoryForRoles = new SimpleDirectory();
                        directoryForRoles.setDirName(unifiedNumber);
                        directoryForRoles.setTable1Type("ENTRUSTMENT_LIST");
                        directoryForRoles.setTable1Id(taskId);
                        directoryForRoles.setCreateBy((userAccount != null && !userAccount.trim().isEmpty()) ? userAccount.trim() : "admin");
                        directoryForRoles.setCreateTime(new java.util.Date());
                    }
                    directoryForRoles.setJcTester(jt);
                    directoryForRoles.setJcReviewer(jr);
                    directoryForRoles.setBgApprover(ba);
                    directoryForRoles.setUpdateBy((userAccount != null && !userAccount.trim().isEmpty()) ? userAccount.trim() : directoryForRoles.getCreateBy());
                    directoryForRoles.setUpdateTime(new java.util.Date());
                    simpleDirectoryService.saveDirectory(directoryForRoles);

                    boolean alreadyApproved = entrustmentBefore.getStatus() != null
                            && "5".equals(String.valueOf(entrustmentBefore.getStatus()).trim());
                    boolean success;
                    if (approveSignPhoto != null) {
                        success = jcCoreWtInfoMapper.updateStatusAndApproveSign(taskId, "5", approveSignPhoto) > 0 || alreadyApproved;
                    } else {
                        success = jcCoreWtInfoMapper.updateStatusById(taskId, "5") > 0 || alreadyApproved;
                    }
                    jcCoreWtInfoMapper.updateApproverById(taskId, ba, directoryForRoles.getUpdateBy(), directoryForRoles.getUpdateTime());
                    
                    // 委托单审核通过时，自动创建记录表
                    if (success) {
                        try {
                            // 获取委托单信息
                            JcCoreWtInfo entrustment = jcCoreWtInfoMapper.selectById(taskId);
                            if (entrustment == null) {
                                entrustment = jcCoreWtInfoMapper.selectExtById(taskId);
                            }
                            if (entrustment != null) {
                                unifiedNumber = entrustment.getWtNum();
                                if (unifiedNumber != null) {
                                    // 根据wtNum获取目录
                                    SimpleDirectory directory = simpleDirectoryService.getDirectoryByDirName(unifiedNumber);
                                    if (directory != null) {
                                        // 创建记录表（优先根据“检测(验)项目及依据”(WT_JCCS/TEST_ITEMS) 选择记录表类型）
                                        System.out.println("=== 开始创建记录表 ===");
                                        System.out.println("统一编号: " + unifiedNumber);
                                        
                                        String creator = directory.getCreateBy();
                                        if (creator == null) {
                                            creator = "admin";
                                        }

                                        java.util.List<String> desiredRecordTypes = inferRecordTableTypesFromTestItems(entrustment.getTestItems());
                                        if (desiredRecordTypes.isEmpty()) {
                                            desiredRecordTypes = inferRecordTypesFromDirectory(directory);
                                        }

                                        String category = desiredRecordTypes.isEmpty() ? "通用检测" : String.join(",", inferCategoryLabelsFromRecordTypes(desiredRecordTypes));
                                        System.out.println("检测类别: " + category);

                                        for (String recordType : desiredRecordTypes) {
                                            if (recordType == null || recordType.trim().isEmpty()) continue;

                                            boolean bound = false;
                                            for (int i = 1; i <= 10; i++) {
                                                String t = getDirectoryTableType(directory, i);
                                                if (t != null && t.trim().equalsIgnoreCase(recordType.trim())) {
                                                    String existingId = getDirectoryTableId(directory, i);
                                                    if (existingId != null && !existingId.trim().isEmpty()) {
                                                        bound = true;
                                                        break;
                                                    }
                                                    String found = findExistingRecordId(recordType, unifiedNumber);
                                                    if (found == null || found.isEmpty()) {
                                                        found = createRelatedRecord(recordType, unifiedNumber, creator, category, directory);
                                                    }
                                                    if (found != null && !found.isEmpty()) {
                                                        setDirectoryTableId(directory, i, found);
                                                    }
                                                    bound = true;
                                                    break;
                                                }
                                            }

                                            if (!bound) {
                                                String found = findExistingRecordId(recordType, unifiedNumber);
                                                if (found == null || found.isEmpty()) {
                                                    found = createRelatedRecord(recordType, unifiedNumber, creator, category, directory);
                                                }
                                                if (found == null || found.isEmpty()) {
                                                    continue;
                                                }
                                                int emptyIndex = findFirstEmptyDirectorySlot(directory);
                                                if (emptyIndex == -1) {
                                                    System.err.println("目录表已满，无法绑定记录表类型: " + recordType + ", recordId=" + found);
                                                    continue;
                                                }
                                                setDirectoryTableType(directory, emptyIndex, recordType);
                                                setDirectoryTableId(directory, emptyIndex, found);
                                            }
                                        }

                                        directory.setJcTester(jt);
                                        directory.setJcReviewer(jr);
                                        directory.setBgApprover(ba);
                                        directory.setUpdateBy((userAccount != null && !userAccount.trim().isEmpty()) ? userAccount.trim() : creator);
                                        directory.setUpdateTime(new java.util.Date());
                                        simpleDirectoryService.saveDirectory(directory);
                                        simpleDirectoryService.syncEntrustmentDataByWtNum(unifiedNumber);

                                        System.out.println("=== 记录表创建完成 ===");
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
                    org.example.work121.entity.BeckmanBeam beckmanBeam = beckmanBeamMapper.selectById(taskId);
                    if (beckmanBeam != null) {
                        unifiedNumber = beckmanBeam.getEntrustmentId();
                        String nextStatus = decideNextStatus(beckmanBeam.getStatus());
                        if ("4".equals(nextStatus)) {
                            if (approveSignPhoto != null) {
                                result = beckmanBeamMapper.updateStatusAndReviewSign(taskId, "4", approveSignPhoto) > 0;
                            } else {
                                result = beckmanBeamMapper.updateStatusById(taskId, "4") > 0;
                            }
                        } else {
                            if (approveSignPhoto != null) {
                                result = beckmanBeamMapper.updateStatusAndApproveSign(taskId, "5", approveSignPhoto) > 0;
                            } else {
                                result = beckmanBeamMapper.updateStatusById(taskId, "5") > 0;
                            }
                        }
                        if (result && unifiedNumber != null && !unifiedNumber.trim().isEmpty()) {
                            String testerSign = beckmanBeam.getInspectSignaturePhoto();
                            String reviewSign = "4".equals(nextStatus) ? approveSignPhoto : beckmanBeam.getReviewSignaturePhoto();
                            beckmanBeamMapper.updateRecordSignsByEntrustmentId(unifiedNumber, testerSign, reviewSign);
                        }
                    }
                    break;
                case "轻型动力触探":
                    org.example.work121.entity.LightDynamicPenetration lightDynamicPenetration = lightDynamicPenetrationMapper.selectById(taskId);
                    if (lightDynamicPenetration != null) {
                        unifiedNumber = lightDynamicPenetration.getEntrustmentId();
                        String nextStatus = decideNextStatus(lightDynamicPenetration.getStatus());
                        if ("4".equals(nextStatus)) {
                            if (approveSignPhoto != null) {
                                result = lightDynamicPenetrationMapper.updateStatusAndReviewSign(taskId, "4", approveSignPhoto) > 0;
                            } else {
                                result = lightDynamicPenetrationMapper.updateStatusById(taskId, "4") > 0;
                            }
                        } else {
                            if (approveSignPhoto != null) {
                                result = lightDynamicPenetrationMapper.updateStatusAndApproveSign(taskId, "5", approveSignPhoto) > 0;
                            } else {
                                result = lightDynamicPenetrationMapper.updateStatusById(taskId, "5") > 0;
                            }
                        }
                        if (result && unifiedNumber != null && !unifiedNumber.trim().isEmpty()) {
                            String testerSign = lightDynamicPenetration.getInspectSignaturePhoto();
                            String reviewSign = "4".equals(nextStatus) ? approveSignPhoto : lightDynamicPenetration.getReviewSignaturePhoto();
                            lightDynamicPenetrationMapper.updateRecordSignsByEntrustmentId(unifiedNumber, testerSign, reviewSign);
                        }
                    }
                    break;
                case "回弹法":
                    org.example.work121.entity.ReboundMethod reboundMethod = reboundMethodMapper.selectById(taskId);
                    if (reboundMethod != null) {
                        unifiedNumber = reboundMethod.getEntrustmentId();
                        String nextStatus = decideNextStatus(reboundMethod.getStatus());
                        if ("4".equals(nextStatus)) {
                            if (approveSignPhoto != null) {
                                result = reboundMethodMapper.updateStatusAndReviewSign(taskId, "4", approveSignPhoto) > 0;
                            } else {
                                result = reboundMethodMapper.updateStatusById(taskId, "4") > 0;
                            }
                        } else {
                            if (approveSignPhoto != null) {
                                result = reboundMethodMapper.updateStatusAndApproveSign(taskId, "5", approveSignPhoto) > 0;
                            } else {
                                result = reboundMethodMapper.updateStatusById(taskId, "5") > 0;
                            }
                        }
                        if (result && unifiedNumber != null && !unifiedNumber.trim().isEmpty()) {
                            String testerSign = reboundMethod.getInspectSignaturePhoto();
                            String reviewSign = "4".equals(nextStatus) ? approveSignPhoto : reboundMethod.getReviewSignaturePhoto();
                            reboundMethodMapper.updateRecordSignsByEntrustmentId(unifiedNumber, testerSign, reviewSign);
                        }
                    }
                    break;
                case "环刀法":
                    org.example.work121.entity.CuttingRing cuttingRing = cuttingRingMapper.selectById(taskId);
                    if (cuttingRing != null) {
                        unifiedNumber = cuttingRing.getEntrustmentId();
                        String nextStatus = decideNextStatus(cuttingRing.getStatus());
                        if ("4".equals(nextStatus)) {
                            if (approveSignPhoto != null) {
                                result = cuttingRingMapper.updateStatusAndReviewSign(taskId, "4", approveSignPhoto) > 0;
                            } else {
                                result = cuttingRingMapper.updateStatusById(taskId, "4") > 0;
                            }
                        } else {
                            if (approveSignPhoto != null) {
                                result = cuttingRingMapper.updateStatusAndApproveSign(taskId, "5", approveSignPhoto) > 0;
                            } else {
                                result = cuttingRingMapper.updateStatusById(taskId, "5") > 0;
                            }
                        }
                        if (result && unifiedNumber != null && !unifiedNumber.trim().isEmpty()) {
                            String testerSign = cuttingRing.getInspectSignaturePhoto();
                            String reviewSign = "4".equals(nextStatus) ? approveSignPhoto : cuttingRing.getReviewSignaturePhoto();
                            cuttingRingMapper.updateRecordSignsByEntrustmentId(unifiedNumber, testerSign, reviewSign);
                        }
                    }
                    break;
                case "灌水法":
                    org.example.work121.entity.WaterReplacement waterReplacement = waterReplacementMapper.selectById(taskId);
                    if (waterReplacement != null) {
                        unifiedNumber = waterReplacement.getEntrustmentId();
                        String nextStatus = decideNextStatus(waterReplacement.getStatus());
                        if ("4".equals(nextStatus)) {
                            if (approveSignPhoto != null) {
                                result = waterReplacementMapper.updateStatusAndReviewSign(taskId, "4", approveSignPhoto) > 0;
                            } else {
                                result = waterReplacementMapper.updateStatusById(taskId, "4") > 0;
                            }
                        } else {
                            if (approveSignPhoto != null) {
                                result = waterReplacementMapper.updateStatusAndApproveSign(taskId, "5", approveSignPhoto) > 0;
                            } else {
                                result = waterReplacementMapper.updateStatusById(taskId, "5") > 0;
                            }
                        }
                        if (result && unifiedNumber != null && !unifiedNumber.trim().isEmpty()) {
                            String testerSign = waterReplacement.getInspectSignaturePhoto();
                            String reviewSign = "4".equals(nextStatus) ? approveSignPhoto : waterReplacement.getReviewSignaturePhoto();
                            waterReplacementMapper.updateRecordSignsByEntrustmentId(unifiedNumber, testerSign, reviewSign);
                        }
                    }
                    break;
                case "灌砂法":
                    org.example.work121.entity.SandReplacement sandReplacement = sandReplacementMapper.selectById(taskId);
                    if (sandReplacement != null) {
                        unifiedNumber = sandReplacement.getEntrustmentId();
                        String nextStatus = decideNextStatus(sandReplacement.getStatus());
                        if ("4".equals(nextStatus)) {
                            if (approveSignPhoto != null) {
                                result = sandReplacementMapper.updateStatusAndReviewSign(taskId, "4", approveSignPhoto) > 0;
                            } else {
                                result = sandReplacementMapper.updateStatusById(taskId, "4") > 0;
                            }
                        } else {
                            if (approveSignPhoto != null) {
                                result = sandReplacementMapper.updateStatusAndApproveSign(taskId, "5", approveSignPhoto) > 0;
                            } else {
                                result = sandReplacementMapper.updateStatusById(taskId, "5") > 0;
                            }
                        }
                        if (result && unifiedNumber != null && !unifiedNumber.trim().isEmpty()) {
                            String testerSign = sandReplacement.getInspectSignaturePhoto();
                            String reviewSign = "4".equals(nextStatus) ? approveSignPhoto : sandReplacement.getReviewSignaturePhoto();
                            sandReplacementMapper.updateRecordSignsByEntrustmentId(unifiedNumber, testerSign, reviewSign);
                        }
                    }
                    break;
                case "核子密度":
                    org.example.work121.entity.NuclearDensity nuclearDensity = nuclearDensityMapper.selectById(taskId);
                    if (nuclearDensity != null) {
                        unifiedNumber = nuclearDensity.getEntrustmentId();
                        String nextStatus = decideNextStatus(nuclearDensity.getStatus());
                        if ("4".equals(nextStatus)) {
                            if (approveSignPhoto != null) {
                                result = nuclearDensityMapper.updateStatusAndReviewSign(taskId, "4", approveSignPhoto) > 0;
                            } else {
                                result = nuclearDensityMapper.updateStatusById(taskId, "4") > 0;
                            }
                        } else {
                            if (approveSignPhoto != null) {
                                result = nuclearDensityMapper.updateStatusAndApproveSign(taskId, "5", approveSignPhoto) > 0;
                            } else {
                                result = nuclearDensityMapper.updateStatusById(taskId, "5") > 0;
                            }
                        }
                        if (result && unifiedNumber != null && !unifiedNumber.trim().isEmpty()) {
                            String testerSign = nuclearDensity.getInspectSignaturePhoto();
                            String reviewSign = "4".equals(nextStatus) ? approveSignPhoto : nuclearDensity.getReviewSignaturePhoto();
                            nuclearDensityMapper.updateRecordSignsByEntrustmentId(unifiedNumber, testerSign, reviewSign);
                        }
                    }
                    break;
                case "密度试验":
                    org.example.work121.entity.DensityTest densityTest = densityTestMapper.selectById(taskId);
                    if (densityTest != null) {
                        unifiedNumber = densityTest.getEntrustmentId();
                        String nextStatus = decideNextStatus(densityTest.getStatus());
                        if ("4".equals(nextStatus)) {
                            if (approveSignPhoto != null) {
                                result = densityTestMapper.updateStatusAndReviewSign(taskId, "4", approveSignPhoto) > 0;
                            } else {
                                result = densityTestMapper.updateStatusById(taskId, "4") > 0;
                            }
                        } else {
                            if (approveSignPhoto != null) {
                                result = densityTestMapper.updateStatusAndApproveSign(taskId, "5", approveSignPhoto) > 0;
                            } else {
                                result = densityTestMapper.updateStatusById(taskId, "5") > 0;
                            }
                        }
                        if (result && unifiedNumber != null && !unifiedNumber.trim().isEmpty()) {
                            String testerSign = densityTest.getInspectSignaturePhoto();
                            String reviewSign = "4".equals(nextStatus) ? approveSignPhoto : densityTest.getReviewSignaturePhoto();
                            densityTestMapper.updateRecordSignsByEntrustmentId(unifiedNumber, testerSign, reviewSign);
                        }
                    }
                    break;
                default:
                    return false;
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String decideNextStatus(String currentStatus) {
        if (currentStatus == null) return "4";
        String s = currentStatus.trim();
        if ("1".equals(s)) return "4";
        if ("4".equals(s)) return "5";
        if ("5".equals(s)) return "5";
        return "4";
    }

    private java.util.List<String> inferRecordTableTypesFromTestItems(String testItems) {
        if (testItems == null || testItems.trim().isEmpty()) return java.util.Collections.emptyList();
        String normalized = testItems
                .replace('，', ',')
                .replace('；', ',')
                .replace(';', ',')
                .replace('|', ',')
                .replace('、', ',')
                .replace('\n', ',')
                .replace('\r', ',')
                .replace('\t', ',');
        String[] parts = normalized.split(",");
        java.util.LinkedHashSet<String> result = new java.util.LinkedHashSet<>();
        for (String raw : parts) {
            if (raw == null) continue;
            String item = raw.trim();
            if (item.isEmpty()) continue;

            if ("核子法".equals(item) || "核子密度".equals(item) || item.contains("核子")) {
                result.add("NUCLEAR_DENSITY_RECORD");
            } else if ("灌砂法".equals(item) || item.contains("灌砂")) {
                result.add("SAND_REPLACEMENT_RECORD");
            } else if ("灌水法".equals(item) || item.contains("灌水")) {
                result.add("WATER_REPLACEMENT_RECORD");
            } else if ("环刀法".equals(item) || item.contains("环刀")) {
                result.add("CUTTING_RING_RECORD");
            } else if ("回弹法".equals(item) || item.contains("回弹")) {
                result.add("REBOUND_METHOD_RECORD");
            } else if ("轻型动力触探".equals(item) || item.contains("动力触探") || item.contains("触探")) {
                result.add("LIGHT_DYNAMIC_PENETRATION_RECORD");
            } else if ("贝克曼梁".equals(item) || item.contains("贝克曼")) {
                result.add("BECKMAN_BEAM_RECORD");
            } else if ("密度试验".equals(item) || item.contains("密度试验")) {
                result.add("DENSITY_TEST_RECORD");
            }
        }
        return new java.util.ArrayList<>(result);
    }

    private java.util.List<String> inferRecordTypesFromDirectory(SimpleDirectory directory) {
        if (directory == null) return java.util.Collections.emptyList();
        java.util.LinkedHashSet<String> types = new java.util.LinkedHashSet<>();
        for (int i = 1; i <= 10; i++) {
            String type = getDirectoryTableType(directory, i);
            if (type == null || type.trim().isEmpty()) continue;
            String upper = type.trim().toUpperCase();
            if (upper.contains("ENTRUSTMENT")) continue;
            if (upper.contains("RECORD") ||
                    upper.contains("NUCLEAR_DENSITY") ||
                    upper.contains("SAND_REPLACEMENT") ||
                    upper.contains("WATER_REPLACEMENT") ||
                    upper.contains("CUTTING_RING") ||
                    upper.contains("REBOUND_METHOD") ||
                    upper.contains("LIGHT_DYNAMIC_PENETRATION") ||
                    upper.contains("BECKMAN_BEAM") ||
                    upper.contains("DENSITY_TEST")) {
                types.add(type.trim());
            }
        }
        return new java.util.ArrayList<>(types);
    }

    private java.util.List<String> inferCategoryLabelsFromRecordTypes(java.util.List<String> recordTypes) {
        if (recordTypes == null || recordTypes.isEmpty()) return java.util.Collections.emptyList();
        java.util.LinkedHashSet<String> labels = new java.util.LinkedHashSet<>();
        for (String t : recordTypes) {
            if (t == null) continue;
            String upper = t.toUpperCase();
            if (upper.contains("NUCLEAR")) labels.add("核子法");
            else if (upper.contains("SAND")) labels.add("灌砂法");
            else if (upper.contains("WATER")) labels.add("灌水法");
            else if (upper.contains("CUTTING")) labels.add("环刀法");
            else if (upper.contains("REBOUND")) labels.add("回弹法");
            else if (upper.contains("PENETRATION")) labels.add("轻型动力触探");
            else if (upper.contains("BECKMAN")) labels.add("贝克曼梁");
            else if (upper.contains("DENSITY_TEST")) labels.add("密度试验");
        }
        return new java.util.ArrayList<>(labels);
    }

    private int findFirstEmptyDirectorySlot(SimpleDirectory directory) {
        for (int i = 1; i <= 10; i++) {
            String type = getDirectoryTableType(directory, i);
            String id = getDirectoryTableId(directory, i);
            if ((type == null || type.trim().isEmpty()) && (id == null || id.trim().isEmpty())) {
                return i;
            }
        }
        return -1;
    }

    private String getDirectoryTableType(SimpleDirectory directory, int index) {
        switch (index) {
            case 1: return directory.getTable1Type();
            case 2: return directory.getTable2Type();
            case 3: return directory.getTable3Type();
            case 4: return directory.getTable4Type();
            case 5: return directory.getTable5Type();
            case 6: return directory.getTable6Type();
            case 7: return directory.getTable7Type();
            case 8: return directory.getTable8Type();
            case 9: return directory.getTable9Type();
            case 10: return directory.getTable10Type();
            default: return null;
        }
    }

    private void setDirectoryTableType(SimpleDirectory directory, int index, String value) {
        switch (index) {
            case 1: directory.setTable1Type(value); break;
            case 2: directory.setTable2Type(value); break;
            case 3: directory.setTable3Type(value); break;
            case 4: directory.setTable4Type(value); break;
            case 5: directory.setTable5Type(value); break;
            case 6: directory.setTable6Type(value); break;
            case 7: directory.setTable7Type(value); break;
            case 8: directory.setTable8Type(value); break;
            case 9: directory.setTable9Type(value); break;
            case 10: directory.setTable10Type(value); break;
        }
    }

    private String getDirectoryTableId(SimpleDirectory directory, int index) {
        switch (index) {
            case 1: return directory.getTable1Id();
            case 2: return directory.getTable2Id();
            case 3: return directory.getTable3Id();
            case 4: return directory.getTable4Id();
            case 5: return directory.getTable5Id();
            case 6: return directory.getTable6Id();
            case 7: return directory.getTable7Id();
            case 8: return directory.getTable8Id();
            case 9: return directory.getTable9Id();
            case 10: return directory.getTable10Id();
            default: return null;
        }
    }

    private void setDirectoryTableId(SimpleDirectory directory, int index, String value) {
        switch (index) {
            case 1: directory.setTable1Id(value); break;
            case 2: directory.setTable2Id(value); break;
            case 3: directory.setTable3Id(value); break;
            case 4: directory.setTable4Id(value); break;
            case 5: directory.setTable5Id(value); break;
            case 6: directory.setTable6Id(value); break;
            case 7: directory.setTable7Id(value); break;
            case 8: directory.setTable8Id(value); break;
            case 9: directory.setTable9Id(value); break;
            case 10: directory.setTable10Id(value); break;
        }
    }
    
    private String findExistingRecordId(String recordType, String unifiedNumber) {
        if (recordType == null || unifiedNumber == null) return null;
        String upper = recordType.toUpperCase();
        try {
            if (upper.contains("NUCLEAR_DENSITY")) {
                java.util.List<org.example.work121.entity.NuclearDensity> list = nuclearDensityMapper.selectByEntrustmentId(unifiedNumber);
                return (list != null && !list.isEmpty()) ? list.get(0).getId() : null;
            }
            if (upper.contains("SAND_REPLACEMENT")) {
                java.util.List<org.example.work121.entity.SandReplacement> list = sandReplacementMapper.selectByEntrustmentId(unifiedNumber);
                return (list != null && !list.isEmpty()) ? list.get(0).getId() : null;
            }
            if (upper.contains("WATER_REPLACEMENT")) {
                java.util.List<org.example.work121.entity.WaterReplacement> list = waterReplacementMapper.selectByEntrustmentId(unifiedNumber);
                return (list != null && !list.isEmpty()) ? list.get(0).getId() : null;
            }
            if (upper.contains("CUTTING_RING")) {
                java.util.List<org.example.work121.entity.CuttingRing> list = cuttingRingMapper.selectByEntrustmentId(unifiedNumber);
                return (list != null && !list.isEmpty()) ? list.get(0).getId() : null;
            }
            if (upper.contains("REBOUND_METHOD")) {
                java.util.List<org.example.work121.entity.ReboundMethod> list = reboundMethodMapper.selectByEntrustmentId(unifiedNumber);
                return (list != null && !list.isEmpty()) ? list.get(0).getId() : null;
            }
            if (upper.contains("LIGHT_DYNAMIC_PENETRATION")) {
                java.util.List<org.example.work121.entity.LightDynamicPenetration> list = lightDynamicPenetrationMapper.selectByEntrustmentId(unifiedNumber);
                return (list != null && !list.isEmpty()) ? list.get(0).getId() : null;
            }
            if (upper.contains("BECKMAN_BEAM")) {
                java.util.List<org.example.work121.entity.BeckmanBeam> list = beckmanBeamMapper.selectByEntrustmentId(unifiedNumber);
                return (list != null && !list.isEmpty()) ? list.get(0).getId() : null;
            }
            if (upper.contains("DENSITY_TEST")) {
                java.util.List<org.example.work121.entity.DensityTest> list = densityTestMapper.selectByEntrustmentId(unifiedNumber);
                return (list != null && !list.isEmpty()) ? list.get(0).getId() : null;
            }
        } catch (Exception ignore) {
            return null;
        }
        return null;
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
                if (tableGenerationService != null) {
                    tableGenerationService.fillTableFromEntrustment("DENSITY_TEST", dirName, densityTest);
                }
                // 设置角色信息
                if (directory != null) {
                    densityTest.setFiller(directory.getJcTester());
                    densityTest.setRecordTester(directory.getJcTester());
                    densityTest.setRecordReviewer(directory.getJcReviewer());
                    densityTest.setTester(directory.getJcTester());
                    densityTest.setReviewer(directory.getJcReviewer());
                    densityTest.setApprover(directory.getBgApprover());
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
                if (tableGenerationService != null) {
                    tableGenerationService.fillTableFromEntrustment("REBOUND_METHOD", dirName, reboundMethod);
                }
                // 设置角色信息
                if (directory != null) {
                    reboundMethod.setFiller(directory.getJcTester());
                    reboundMethod.setRecordTester(directory.getJcTester());
                    reboundMethod.setRecordReviewer(directory.getJcReviewer());
                    reboundMethod.setTester(directory.getJcTester());
                    reboundMethod.setReviewer(directory.getJcReviewer());
                    reboundMethod.setApprover(directory.getBgApprover());
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
                if (tableGenerationService != null) {
                    tableGenerationService.fillTableFromEntrustment("SAND_REPLACEMENT", dirName, sandReplacement);
                }
                // 设置角色信息
                if (directory != null) {
                    sandReplacement.setFiller(directory.getJcTester());
                    sandReplacement.setRecordTester(directory.getJcTester());
                    sandReplacement.setRecordReviewer(directory.getJcReviewer());
                    sandReplacement.setTester(directory.getJcTester());
                    sandReplacement.setReviewer(directory.getJcReviewer());
                    sandReplacement.setApprover(directory.getBgApprover());
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
                if (tableGenerationService != null) {
                    tableGenerationService.fillTableFromEntrustment("WATER_REPLACEMENT", dirName, waterReplacement);
                }
                // 设置角色信息
                if (directory != null) {
                    waterReplacement.setFiller(directory.getJcTester());
                    waterReplacement.setRecordTester(directory.getJcTester());
                    waterReplacement.setRecordReviewer(directory.getJcReviewer());
                    waterReplacement.setTester(directory.getJcTester());
                    waterReplacement.setReviewer(directory.getJcReviewer());
                    waterReplacement.setApprover(directory.getBgApprover());
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
                if (tableGenerationService != null) {
                    tableGenerationService.fillTableFromEntrustment("NUCLEAR_DENSITY", dirName, nuclearDensity);
                }
                // 设置角色信息
                if (directory != null) {
                    nuclearDensity.setFiller(directory.getJcTester());
                    nuclearDensity.setRecordTester(directory.getJcTester());
                    nuclearDensity.setRecordReviewer(directory.getJcReviewer());
                    nuclearDensity.setTester(directory.getJcTester());
                    nuclearDensity.setReviewer(directory.getJcReviewer());
                    nuclearDensity.setApprover(directory.getBgApprover());
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
                if (tableGenerationService != null) {
                    tableGenerationService.fillTableFromEntrustment("CUTTING_RING", dirName, cuttingRing);
                }
                // 设置角色信息
                if (directory != null) {
                    cuttingRing.setFiller(directory.getJcTester());
                    cuttingRing.setRecordTester(directory.getJcTester());
                    cuttingRing.setRecordReviewer(directory.getJcReviewer());
                    cuttingRing.setTester(directory.getJcTester());
                    cuttingRing.setReviewer(directory.getJcReviewer());
                    cuttingRing.setApprover(directory.getBgApprover());
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
                if (tableGenerationService != null) {
                    tableGenerationService.fillTableFromEntrustment("BECKMAN_BEAM", dirName, beckmanBeam);
                }
                // 设置角色信息
                if (directory != null) {
                    beckmanBeam.setFiller(directory.getJcTester());
                    beckmanBeam.setRecordTester(directory.getJcTester());
                    beckmanBeam.setRecordReviewer(directory.getJcReviewer());
                    beckmanBeam.setTester(directory.getJcTester());
                    beckmanBeam.setReviewer(directory.getJcReviewer());
                    beckmanBeam.setApprover(directory.getBgApprover());
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
                if (tableGenerationService != null) {
                    tableGenerationService.fillTableFromEntrustment("LIGHT_DYNAMIC_PENETRATION", dirName, lightDynamicPenetration);
                }
                // 设置角色信息
                if (directory != null) {
                    lightDynamicPenetration.setFiller(directory.getJcTester());
                    lightDynamicPenetration.setRecordTester(directory.getJcTester());
                    lightDynamicPenetration.setRecordReviewer(directory.getJcReviewer());
                    lightDynamicPenetration.setTester(directory.getJcTester());
                    lightDynamicPenetration.setReviewer(directory.getJcReviewer());
                    lightDynamicPenetration.setApprover(directory.getBgApprover());
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
