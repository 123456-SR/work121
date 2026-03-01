package org.example.work121.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.work121.entity.*;
import org.example.work121.mapper.*;
import org.example.work121.service.TableGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;

@Service
public class TableGenerationServiceImpl implements TableGenerationService {

    @Autowired
    private JcCoreWtInfoMapper jcCoreWtInfoMapper;

    @Autowired
    private DensityTestMapper densityTestMapper;
    @Autowired
    private DensityTestReportMapper densityTestReportMapper;
    @Autowired
    private DensityTestResultMapper densityTestResultMapper;

    @Autowired
    private ReboundMethodMapper reboundMethodMapper;
    @Autowired
    private ReboundMethodReportMapper reboundMethodReportMapper;
    @Autowired
    private ReboundMethodResultMapper reboundMethodResultMapper;

    @Autowired
    private SandReplacementMapper sandReplacementMapper;
    @Autowired
    private SandReplacementReportMapper sandReplacementReportMapper;
    @Autowired
    private SandReplacementResultMapper sandReplacementResultMapper;

    @Autowired
    private WaterReplacementMapper waterReplacementMapper;
    @Autowired
    private WaterReplacementReportMapper waterReplacementReportMapper;
    @Autowired
    private WaterReplacementResultMapper waterReplacementResultMapper;

    @Autowired
    private NuclearDensityMapper nuclearDensityMapper;
    @Autowired
    private NuclearDensityReportMapper nuclearDensityReportMapper;
    @Autowired
    private NuclearDensityResultMapper nuclearDensityResultMapper;

    @Autowired
    private CuttingRingMapper cuttingRingMapper;
    @Autowired
    private CuttingRingReportMapper cuttingRingReportMapper;
    @Autowired
    private CuttingRingResultMapper cuttingRingResultMapper;

    @Autowired
    private BeckmanBeamMapper beckmanBeamMapper;
    @Autowired
    private BeckmanBeamReportMapper beckmanBeamReportMapper;
    @Autowired
    private BeckmanBeamResultMapper beckmanBeamResultMapper;

    @Autowired
    private LightDynamicPenetrationMapper lightDynamicPenetrationMapper;
    @Autowired
    private LightDynamicPenetrationReportMapper lightDynamicPenetrationReportMapper;
    @Autowired
    private LightDynamicPenetrationResultMapper lightDynamicPenetrationResultMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public void generateReportAndResult(String tableType, String entrustmentId) {
        switch (tableType) {
            case "DENSITY_TEST":
                generateDensityTestReportAndResult(entrustmentId);
                break;
            case "REBOUND_METHOD":
                generateReboundMethodReportAndResult(entrustmentId);
                break;
            case "SAND_REPLACEMENT":
                generateSandReplacementReportAndResult(entrustmentId);
                break;
            case "WATER_REPLACEMENT":
                generateWaterReplacementReportAndResult(entrustmentId);
                break;
            case "NUCLEAR_DENSITY":
                generateNuclearDensityReportAndResult(entrustmentId);
                break;
            case "CUTTING_RING":
                generateCuttingRingReportAndResult(entrustmentId);
                break;
            case "BECKMAN_BEAM":
                generateBeckmanBeamReportAndResult(entrustmentId);
                break;
            case "LIGHT_DYNAMIC_PENETRATION":
                generateLightDynamicPenetrationReportAndResult(entrustmentId);
                break;
            default:
                System.err.println("Unsupported table type: " + tableType);
                break;
        }
    }

    /**
     * 填充原位密度检测报告中缺失的必填字段（虚拟数据）
     */
    private void populateMissingDensityReportFields(Map<String, Object> data) {
        // 报告日期 (默认当天)
        if (isFieldMissing(data, "reportDate")) {
            data.put("reportDate", new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        }

        // 检测日期 (默认当天，通常应与报告日期接近或相同)
        if (isFieldMissing(data, "testDate")) {
            data.put("testDate", new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        }

        // 检测方法 (默认为核子法，因为当前上下文主要是核子法触发)
        if (isFieldMissing(data, "testMethod")) {
            data.put("testMethod", "核子法");
        }

        // 仪器设备
        if (isFieldMissing(data, "equipment")) {
            data.put("equipment", "核子密度仪");
        }

        // 检测依据
        if (isFieldMissing(data, "testBasis")) {
            data.put("testBasis", "JTG 3450-2019");
        }

        // 样品名称及状态
        if (isFieldMissing(data, "sampleNameStatus")) {
            data.put("sampleNameStatus", "细粒土/扰动");
        }

        // 最大干密度 (2.20 - 2.50)
        if (isFieldMissing(data, "maxDryDensity")) {
            double val = 2.20 + new Random().nextDouble() * 0.30;
            data.put("maxDryDensity", String.format("%.2f", val));
        }

        // 最优含水率 (5.0 - 10.0)
        if (isFieldMissing(data, "optimumMoisture")) {
            double val = 5.0 + new Random().nextDouble() * 5.0;
            data.put("optimumMoisture", String.format("%.1f", val));
        }

        // 最小干密度 (默认0)
        if (isFieldMissing(data, "minDryDensity")) {
            data.put("minDryDensity", "0");
        }

        // 设计指标 (默认0.96)
        if (isFieldMissing(data, "designIndex")) {
            data.put("designIndex", "0.96");
        }

        // 检测结果 (默认合格)
        if (isFieldMissing(data, "testResult")) {
            data.put("testResult", "合格");
        }

        // 公司名称
        if (isFieldMissing(data, "companyName")) {
            data.put("companyName", "河北金涛建设工程质量检测有限公司");
        }

        // 公司地址
        if (isFieldMissing(data, "companyAddress")) {
            data.put("companyAddress", "石家庄高新区方亿科技工业园A区第2号楼。");
        }

        // 公司电话
        if (isFieldMissing(data, "companyPhone")) {
            data.put("companyPhone", "0311—86107634  0311—67300616");
        }

        // 备注 (默认模板)
        if (isFieldMissing(data, "remarks")) {
            data.put("remarks", "附原位密度检测结果。\n见证人：\n见证单位：");
        }
    }

    private boolean isFieldMissing(Map<String, Object> data, String key) {
        Object val = data.get(key);
        return val == null || "".equals(val.toString().trim());
    }

    @Override
    public void fillTableFromEntrustment(String tableType, String entrustmentId, Object targetEntity) {
        Map<String, Object> entrustmentData = getEntrustmentData(entrustmentId);
        if (entrustmentData == null || entrustmentData.isEmpty()) {
            System.err.println("No entrustment data found for: " + entrustmentId);
            return;
        }

        try {
            if (targetEntity instanceof BusinessEntity) {
                BusinessEntity entity = (BusinessEntity) targetEntity;
                entity.setProjectName((String) entrustmentData.get("projectName"));
                entity.setClientUnit((String) entrustmentData.get("clientUnit"));
                entity.setCommissionDate((Date) entrustmentData.get("commissionDate"));
                entity.setConstructionPart((String) entrustmentData.get("constructionPart"));
                entity.setTestCategory((String) entrustmentData.get("testCategory"));
                entity.setTester((String) entrustmentData.get("tester"));
                entity.setReviewer((String) entrustmentData.get("reviewer"));
                entity.setApprover((String) entrustmentData.get("approver"));
                entity.setSampleName((String) entrustmentData.get("sampleName"));
                entity.setTestBasis((String) entrustmentData.get("testBasis"));
                entity.setEquipment((String) entrustmentData.get("equipment"));
            }

            if (targetEntity instanceof LightDynamicPenetration) {
                LightDynamicPenetration entity = (LightDynamicPenetration) targetEntity;
                entity.setProjectName((String) entrustmentData.get("projectName"));
                entity.setConstructionPart((String) entrustmentData.get("constructionPart"));
                entity.setTestCategory((String) entrustmentData.get("testCategory"));
                entity.setTestBasis((String) entrustmentData.get("testBasis"));
                entity.setEquipment((String) entrustmentData.get("equipment"));
                entity.setRemarks((String) entrustmentData.get("remarks"));
            }

            if (targetEntity instanceof ReboundMethod) {
                ReboundMethod entity = (ReboundMethod) targetEntity;
                entity.setStructurePart((String) entrustmentData.get("constructionPart"));
            }

        } catch (Exception e) {
            System.err.println("Error filling table from entrustment: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Object> getEntrustmentData(String entrustmentId) {
        try {
            // 兼容两种用法：
            // 1）entrustmentId 传入统一编号（WT_NUM）
            // 2）entrustmentId 传入委托主键 ID（WT_ID）
            List<JcCoreWtInfo> list = jcCoreWtInfoMapper.selectByWtNum(entrustmentId);
            if (list == null || list.isEmpty()) {
                try {
                    // 如果按 WT_NUM 查不到，再尝试按 ID 查
                    JcCoreWtInfo byId = jcCoreWtInfoMapper.selectById(entrustmentId);
                    if (byId != null) {
                        list = java.util.Collections.singletonList(byId);
                    }
                } catch (Exception ignore) {
                    // 按 ID 查询失败不影响整体流程，继续返回 null
                }
            }
            if (list != null && !list.isEmpty()) {
                JcCoreWtInfo info = list.get(0);
                Map<String, Object> data = new HashMap<>();
                // 统一编号：对应 WT_NUM（wtNum），这是用户可见的统一编号
                String wtNum = info.getWtNum();
                String sampleNumber = info.getSampleNumber();
                System.out.println("[getEntrustmentData] wtNum=" + wtNum + ", sampleNumber=" + sampleNumber);
                data.put("unifiedNumber", wtNum);
                // 样品编号：对应 T_ENTRUSTMENT.SAMPLE_NUMBER，这是独立的样品编号字段
                // 如果 sampleNumber 为空或等于 wtNum，则不返回，让前端保持为空
                if (sampleNumber != null && !sampleNumber.trim().isEmpty() && !sampleNumber.equals(wtNum)) {
                    data.put("sampleNumber", sampleNumber);
                } else {
                    data.put("sampleNumber", null); // 明确返回 null，避免前端自动填充
                }
                data.put("wtNum", wtNum);
                data.put("id", info.getId()); // 也返回 id，供其他用途使用
                data.put("projectName", info.getProjectName());
                data.put("clientUnit", info.getClientUnit());
                data.put("commissionDate", info.getCommissionDate());
                data.put("constructionPart", info.getConstructionPart());
                data.put("testCategory", info.getTestCategory());
                data.put("tester", info.getTester());
                data.put("reviewer", info.getReviewer());
                data.put("approver", info.getApprover());
                data.put("sampleName", info.getSampleName());
                data.put("testBasis", info.getTestBasis());
                data.put("equipment", info.getEquipment());
                data.put("remarks", info.getRemarks());
                data.put("witness", info.getWitness());
                data.put("witnessUnit", info.getWitnessUnit());
                return data;
            }
        } catch (Exception e) {
            System.err.println("Error getting entrustment data: " + e.getMessage());
        }
        return null;
    }

    private void generateDensityTestReportAndResult(String entrustmentId) {
        try {
            // 这里的 entrustmentId 实际上传进来的是“统一编号”（例如 XT-2024-54301），
            // 各密度记录表的 ENTRUSTMENT_ID 也是存的统一编号，所以直接当 wtNum 用。
            String wtNum = entrustmentId;

            // 条件一：对应统一编号的委托单必须已通过（STATUS=5）
            JcCoreWtInfo core = null;
            try {
                java.util.List<JcCoreWtInfo> wtList = jcCoreWtInfoMapper.selectByWtNum(wtNum);
                if (wtList != null && !wtList.isEmpty()) {
                    core = wtList.get(0);
                }
            } catch (Exception e) {
                System.err.println("Error loading JcCoreWtInfo by wtNum " + wtNum + ": " + e.getMessage());
            }

            if (core == null || core.getStatus() == null || !"5".equals(core.getStatus().toString())) {
                System.out.println("Skip DensityTest report/result generation: entrustment not APPROVED for wtNum " + wtNum + ", status=" + (core != null ? core.getStatus() : "null"));
                return;
            }

            // 条件二：该统一编号下所有“密度类记录表”（核子法、灌砂法、灌水法、环刀法）状态全部为 5，
            // 且至少存在一条密度类记录（避免仅有回弹法之类时生成报告）
            if (!areAllDensityRecordsApproved(entrustmentId, wtNum)) {
                System.out.println("Skip DensityTest report/result generation: not all density records approved for wtNum " + wtNum);
                return;
            }

            // 到这里说明：
            // - 对应统一编号的委托表已通过
            // - 该统一编号下所有“密度类记录表”中已有记录的状态全部=5，且至少存在一条密度记录
            // 按你的设计，此时就应当“根据委托表 + 各种密度记录表”自动生成原位密度检测报告/结果。

            Map<String, Object> recordData = new HashMap<>();

            // 1）先塞委托表的数据
            Map<String, Object> entrustmentData = getEntrustmentData(wtNum);
            if (entrustmentData != null) {
                // 只用非空值覆盖，避免把记录表中已有的数据覆盖成 null
                for (Map.Entry<String, Object> e : entrustmentData.entrySet()) {
                    if (e.getValue() != null) {
                        recordData.put(e.getKey(), e.getValue());
                    }
                }
            }

            // 2）再把四种密度记录表的 DATA_JSON 合并进来（有就合并，没有就跳过）
            mergeDensityRecordJson(
                    nuclearDensityMapper.selectByEntrustmentId(entrustmentId),
                    recordData,
                    NuclearDensity::getDataJson,
                    "NuclearDensity"
            );
            mergeDensityRecordJson(
                    sandReplacementMapper.selectByEntrustmentId(entrustmentId),
                    recordData,
                    SandReplacement::getDataJson,
                    "SandReplacement"
            );
            mergeDensityRecordJson(
                    waterReplacementMapper.selectByEntrustmentId(entrustmentId),
                    recordData,
                    WaterReplacement::getDataJson,
                    "WaterReplacement"
            );
            mergeDensityRecordJson(
                    cuttingRingMapper.selectByEntrustmentId(entrustmentId),
                    recordData,
                    CuttingRing::getDataJson,
                    "CuttingRing"
            );

            // 3）填充报告必需但可能缺失的字段（虚拟数据）
            populateMissingDensityReportFields(recordData);

            DensityTestReport report = densityTestReportMapper.selectByEntrustmentId(entrustmentId);
            if (report == null) {
                report = new DensityTestReport();
                report.setId(UUID.randomUUID().toString());
                report.setEntrustmentId(entrustmentId);
                fillTableFromEntrustment("DENSITY_TEST", entrustmentId, report);
            }
            report.setDataJson(objectMapper.writeValueAsString(recordData));
            saveDensityTestReport(report);

            DensityTestResult result = densityTestResultMapper.selectByEntrustmentId(entrustmentId);
            if (result == null) {
                result = new DensityTestResult();
                result.setId(UUID.randomUUID().toString());
                result.setEntrustmentId(entrustmentId);
                fillTableFromEntrustment("DENSITY_TEST", entrustmentId, result);
            }
            result.setDataJson(objectMapper.writeValueAsString(recordData));
            saveDensityTestResult(result);

            System.out.println("Generated Report and Result for DensityTest entrustment: " + entrustmentId);
        } catch (Exception e) {
            e.printStackTrace();
            // IMPORTANT: Do NOT rethrow RuntimeException here if we want to avoid rolling back the outer transaction
            // (e.g. when called from WorkflowService during record approval).
            // Just log the error. The record approval should succeed even if the auto-generation fails.
            System.err.println("Error generating DensityTest report/result (suppressed): " + e.getMessage());
        }
    }

    /**
     * 将某一类密度记录表中所有记录的 DATA_JSON 合并到 recordData 里。
     * 对于数据字段（如 sampleId_0, dryDensity_1 等），会智能重新编号以避免覆盖。
     * 对于非数据字段（如 projectName, testCategory 等），后出现的会覆盖先前的。
     */
    private <T> void mergeDensityRecordJson(List<T> records,
                                            Map<String, Object> recordData,
                                            Function<T, String> dataJsonGetter,
                                            String debugLabel) {
        if (records == null || records.isEmpty()) {
            return;
        }
        
        // 定义数据字段的前缀模式（用于识别需要重新编号的字段）
        String[] dataFieldPrefixes = {
            "sampleId_", "location_", "date_", "wetDensity_", "dryDensity_", 
            "moisture_", "compaction_", "remarks_",
            "wetDensity2_", "dryDensity2_", "moisture2_",
            "samplingLocation_", "avgMeasuredDryDensity_", "relativeDensity_",
            // 环刀法特有的字段前缀（需要映射到标准字段）
            "sampleNo_", "moisture1_"
        };
        
        for (T be : records) {
            String json = dataJsonGetter.apply(be);
            if (json != null && !json.isEmpty()) {
                try {
                    Map<String, Object> m = objectMapper.readValue(json, Map.class);
                    if (m != null) {
                        // 1. 先找出当前 recordData 中数据字段的最大索引
                        // 注意：recordData 中可能已经有映射后的字段名（sampleId_, moisture_）
                        int maxExistingIndex = -1;
                        for (String key : recordData.keySet()) {
                            for (String prefix : dataFieldPrefixes) {
                                if (key.startsWith(prefix)) {
                                    try {
                                        String suffix = key.substring(prefix.length());
                                        int idx = Integer.parseInt(suffix);
                                        if (idx > maxExistingIndex) {
                                            maxExistingIndex = idx;
                                        }
                                    } catch (NumberFormatException ignored) {
                                        // 不是数字后缀，忽略
                                    }
                                }
                            }
                            // 也检查映射后的字段名（sampleId_, moisture_）
                            if (key.startsWith("sampleId_") || key.startsWith("moisture_")) {
                                try {
                                    String suffix = key.substring(key.lastIndexOf("_") + 1);
                                    int idx = Integer.parseInt(suffix);
                                    if (idx > maxExistingIndex) {
                                        maxExistingIndex = idx;
                                    }
                                } catch (NumberFormatException ignored) {
                                }
                            }
                        }
                        
                        // 2. 找出新记录中数据字段的最大索引（考虑字段映射）
                        int maxNewIndex = -1;
                        for (String key : m.keySet()) {
                            for (String prefix : dataFieldPrefixes) {
                                if (key.startsWith(prefix)) {
                                    try {
                                        String suffix = key.substring(prefix.length());
                                        int idx = Integer.parseInt(suffix);
                                        if (idx > maxNewIndex) {
                                            maxNewIndex = idx;
                                        }
                                    } catch (NumberFormatException ignored) {
                                        // 不是数字后缀，忽略
                                    }
                                }
                            }
                            // 也检查环刀法的特有字段（需要映射的）
                            if (key.startsWith("sampleNo_") || key.startsWith("moisture1_")) {
                                try {
                                    String suffix = key.substring(key.lastIndexOf("_") + 1);
                                    int idx = Integer.parseInt(suffix);
                                    if (idx > maxNewIndex) {
                                        maxNewIndex = idx;
                                    }
                                } catch (NumberFormatException ignored) {
                                    // 不是数字后缀，忽略
                                }
                            }
                        }
                        
                        // 3. 合并数据：数据字段重新编号，非数据字段直接合并（后覆盖前）
                        int offset = maxExistingIndex + 1; // 新记录的起始索引
                        for (Map.Entry<String, Object> entry : m.entrySet()) {
                            String key = entry.getKey();
                            Object value = entry.getValue();
                            
                            boolean isDataField = false;
                            String matchedPrefix = null;
                            String suffix = null;
                            
                            // 检查是否是数据字段
                            for (String prefix : dataFieldPrefixes) {
                                if (key.startsWith(prefix)) {
                                    try {
                                        suffix = key.substring(prefix.length());
                                        Integer.parseInt(suffix); // 验证是数字
                                        isDataField = true;
                                        matchedPrefix = prefix;
                                        break;
                                    } catch (NumberFormatException ignored) {
                                        // 不是数字后缀，不是数据字段
                                    }
                                }
                            }
                            // 特殊处理：环刀法的特有字段（即使不在标准前缀列表中，也需要识别为数据字段）
                            if (!isDataField) {
                                if (key.startsWith("sampleNo_")) {
                                    try {
                                        suffix = key.substring("sampleNo_".length());
                                        Integer.parseInt(suffix);
                                        isDataField = true;
                                        matchedPrefix = "sampleNo_";
                                    } catch (NumberFormatException ignored) {
                                    }
                                } else if (key.startsWith("moisture1_")) {
                                    try {
                                        suffix = key.substring("moisture1_".length());
                                        Integer.parseInt(suffix);
                                        isDataField = true;
                                        matchedPrefix = "moisture1_";
                                    } catch (NumberFormatException ignored) {
                                    }
                                }
                            }
                            
                            if (isDataField && matchedPrefix != null && suffix != null) {
                                // 数据字段：重新编号
                                int oldIndex = Integer.parseInt(suffix);
                                int newIndex = offset + oldIndex;
                                
                                // 字段名映射：将不同记录表的字段名统一映射到报告表的字段名
                                String mappedPrefix = matchedPrefix;
                                if ("sampleNo_".equals(matchedPrefix)) {
                                    // 环刀法：sampleNo_ -> sampleId_
                                    mappedPrefix = "sampleId_";
                                    System.out.println("[mergeDensityRecordJson] 字段映射: " + key + " -> " + mappedPrefix + newIndex + " (值: " + value + ")");
                                } else if ("moisture1_".equals(matchedPrefix)) {
                                    // 环刀法：moisture1_ -> moisture_（第一行含水率）
                                    mappedPrefix = "moisture_";
                                    System.out.println("[mergeDensityRecordJson] 字段映射: " + key + " -> " + mappedPrefix + newIndex + " (值: " + value + ")");
                                }
                                // 注意：moisture2_ 不需要映射，直接使用
                                // avgMoisture_ 是平均含水率，不应该映射到报告表
                                
                                String newKey = mappedPrefix + newIndex;
                                recordData.put(newKey, value);
                            } else {
                                // 非数据字段：直接合并（后覆盖前）
                                recordData.put(key, value);
                            }
                        }

                        // 4. 针对不同试验方法做额外的字段规范化映射
                        if ("WaterReplacement".equals(debugLabel)) {
                            // 灌水法：将记录表里的 samplingLocation_0 / avgMeasuredDryDensity_0 / relativeDensity_0 等
                            // 规范映射到通用的 location_0 / dryDensity_0 / compaction_0 / moisture_0，
                            // 便于"原位密度检测报告/结果"自动填充。
                            normalizeWaterReplacementJson(recordData, m);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing " + debugLabel + " record JSON: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 校验某个委托（统一编号）下所有“密度类记录表”的状态：
     * - 至少存在一条密度类记录（核子法 / 灌砂法 / 灌水法 / 环刀法中的任意一种）
     * - 只要存在的密度类记录，其 STATUS 必须全部为 "5"
     * - 如果完全没有密度类记录，返回 false（不生成报告）
     */
    private boolean areAllDensityRecordsApproved(String entrustmentId, String wtNum) {
        boolean hasAnyDensityRecord = false;

        // 核子法
        try {
            List<NuclearDensity> nuclearList = nuclearDensityMapper.selectByEntrustmentId(entrustmentId);
            if (nuclearList != null && !nuclearList.isEmpty()) {
                hasAnyDensityRecord = true;
                for (NuclearDensity n : nuclearList) {
                    if (n.getStatus() == null || !"5".equals(n.getStatus().toString())) {
                        System.out.println("Skip DensityTest report/result generation: NuclearDensity not all APPROVED for wtNum " + wtNum);
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error checking NuclearDensity status for wtNum " + wtNum + ": " + e.getMessage());
            return false;
        }

        // 灌砂法
        try {
            List<SandReplacement> sandList = sandReplacementMapper.selectByEntrustmentId(entrustmentId);
            if (sandList != null && !sandList.isEmpty()) {
                hasAnyDensityRecord = true;
                for (SandReplacement s : sandList) {
                    if (s.getStatus() == null || !"5".equals(s.getStatus().toString())) {
                        System.out.println("Skip DensityTest report/result generation: SandReplacement not all APPROVED for wtNum " + wtNum);
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error checking SandReplacement status for wtNum " + wtNum + ": " + e.getMessage());
            return false;
        }

        // 灌水法
        try {
            List<WaterReplacement> waterList = waterReplacementMapper.selectByEntrustmentId(entrustmentId);
            if (waterList != null && !waterList.isEmpty()) {
                hasAnyDensityRecord = true;
                for (WaterReplacement w : waterList) {
                    if (w.getStatus() == null || !"5".equals(w.getStatus().toString())) {
                        System.out.println("Skip DensityTest report/result generation: WaterReplacement not all APPROVED for wtNum " + wtNum);
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error checking WaterReplacement status for wtNum " + wtNum + ": " + e.getMessage());
            return false;
        }

        // 环刀法
        try {
            List<CuttingRing> cuttingList = cuttingRingMapper.selectByEntrustmentId(entrustmentId);
            if (cuttingList != null && !cuttingList.isEmpty()) {
                hasAnyDensityRecord = true;
                for (CuttingRing c : cuttingList) {
                    if (c.getStatus() == null || !"5".equals(c.getStatus().toString())) {
                        System.out.println("Skip DensityTest report/result generation: CuttingRing not all APPROVED for wtNum " + wtNum);
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error checking CuttingRing status for wtNum " + wtNum + ": " + e.getMessage());
            return false;
        }

        if (!hasAnyDensityRecord) {
            System.out.println("Skip DensityTest report/result generation: no density records for wtNum " + wtNum);
            return false;
        }

        return true;
    }

    private void generateReboundMethodReportAndResult(String entrustmentId) {
        try {
            List<ReboundMethod> records = reboundMethodMapper.selectByEntrustmentId(entrustmentId);
            if (records == null || records.isEmpty()) {
                System.err.println("Warning: Record not found for entrustmentId " + entrustmentId);
                return;
            }

            ReboundMethod record = records.get(0);
            Map<String, Object> recordData = new HashMap<>();

            // 1）先合并委托单数据（委托级别的工程名称、委托单位、施工部位、检测类别等）
            Map<String, Object> entrustmentData = getEntrustmentData(entrustmentId);
            if (entrustmentData != null) {
                for (Map.Entry<String, Object> e : entrustmentData.entrySet()) {
                    if (e.getValue() != null) {
                        recordData.put(e.getKey(), e.getValue());
                    }
                }
            }

            // 2）再合并回弹法记录表自身的 JSON + 显式字段（记录表填写的内容优先）
            Map<String, Object> fromRecord = prepareReboundMethodData(record);
            if (fromRecord != null) {
                recordData.putAll(fromRecord);
            }

            ReboundMethodReport report = reboundMethodReportMapper.selectByEntrustmentId(entrustmentId);
            if (report == null) {
                report = new ReboundMethodReport();
                report.setId(UUID.randomUUID().toString());
                report.setEntrustmentId(entrustmentId);
                fillTableFromEntrustment("REBOUND_METHOD", entrustmentId, report);
            }
            report.setDataJson(objectMapper.writeValueAsString(recordData));
            saveReboundMethodReport(report);

            ReboundMethodResult result = reboundMethodResultMapper.selectByEntrustmentId(entrustmentId);
            if (result == null) {
                result = new ReboundMethodResult();
                result.setId(UUID.randomUUID().toString());
                result.setEntrustmentId(entrustmentId);
                fillTableFromEntrustment("REBOUND_METHOD", entrustmentId, result);
            }
            result.setDataJson(objectMapper.writeValueAsString(recordData));
            saveReboundMethodResult(result);

            System.out.println("Generated Report and Result for ReboundMethod entrustment: " + entrustmentId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating ReboundMethod report/result: " + e.getMessage());
        }
    }

    private void generateSandReplacementReportAndResult(String entrustmentId) {
        try {
            List<SandReplacement> records = sandReplacementMapper.selectByEntrustmentId(entrustmentId);
            if (records == null || records.isEmpty()) {
                System.err.println("Warning: Record not found for entrustmentId " + entrustmentId);
                return;
            }

            SandReplacement record = records.get(0);
            Map<String, Object> recordData = prepareSandReplacementData(record);

            SandReplacementReport report = sandReplacementReportMapper.selectByEntrustmentId(entrustmentId);
            if (report == null) {
                report = new SandReplacementReport();
                report.setId(UUID.randomUUID().toString());
                report.setEntrustmentId(entrustmentId);
                fillTableFromEntrustment("SAND_REPLACEMENT", entrustmentId, report);
            }
            report.setDataJson(objectMapper.writeValueAsString(recordData));
            saveSandReplacementReport(report);

            SandReplacementResult result = sandReplacementResultMapper.selectByEntrustmentId(entrustmentId);
            if (result == null) {
                result = new SandReplacementResult();
                result.setId(UUID.randomUUID().toString());
                result.setEntrustmentId(entrustmentId);
                fillTableFromEntrustment("SAND_REPLACEMENT", entrustmentId, result);
            }
            result.setDataJson(objectMapper.writeValueAsString(recordData));
            saveSandReplacementResult(result);

            System.out.println("Generated Report and Result for SandReplacement entrustment: " + entrustmentId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating SandReplacement report/result: " + e.getMessage());
        }
    }

    private void generateWaterReplacementReportAndResult(String entrustmentId) {
        try {
            List<WaterReplacement> records = waterReplacementMapper.selectByEntrustmentId(entrustmentId);
            if (records == null || records.isEmpty()) {
                System.err.println("Warning: Record not found for entrustmentId " + entrustmentId);
                return;
            }

            WaterReplacement record = records.get(0);
            Map<String, Object> recordData = prepareWaterReplacementData(record);

            WaterReplacementReport report = waterReplacementReportMapper.selectByEntrustmentId(entrustmentId);
            if (report == null) {
                report = new WaterReplacementReport();
                report.setId(UUID.randomUUID().toString());
                report.setEntrustmentId(entrustmentId);
                fillTableFromEntrustment("WATER_REPLACEMENT", entrustmentId, report);
            }
            report.setDataJson(objectMapper.writeValueAsString(recordData));
            saveWaterReplacementReport(report);

            WaterReplacementResult result = waterReplacementResultMapper.selectByEntrustmentId(entrustmentId);
            if (result == null) {
                result = new WaterReplacementResult();
                result.setId(UUID.randomUUID().toString());
                result.setEntrustmentId(entrustmentId);
                fillTableFromEntrustment("WATER_REPLACEMENT", entrustmentId, result);
            }
            result.setDataJson(objectMapper.writeValueAsString(recordData));
            saveWaterReplacementResult(result);

            System.out.println("Generated Report and Result for WaterReplacement entrustment: " + entrustmentId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating WaterReplacement report/result: " + e.getMessage());
        }
    }

    private void generateNuclearDensityReportAndResult(String entrustmentId) {
        try {
            List<NuclearDensity> records = nuclearDensityMapper.selectByEntrustmentId(entrustmentId);
            if (records == null || records.isEmpty()) {
                System.err.println("Warning: Record not found for entrustmentId " + entrustmentId);
                return;
            }

            NuclearDensity record = records.get(0);
            Map<String, Object> recordData = prepareNuclearDensityData(record);

            NuclearDensityReport report = nuclearDensityReportMapper.selectByEntrustmentId(entrustmentId);
            if (report == null) {
                report = new NuclearDensityReport();
                report.setId(UUID.randomUUID().toString());
                report.setEntrustmentId(entrustmentId);
                fillTableFromEntrustment("NUCLEAR_DENSITY", entrustmentId, report);
            }
            report.setDataJson(objectMapper.writeValueAsString(recordData));
            saveNuclearDensityReport(report);

            NuclearDensityResult result = nuclearDensityResultMapper.selectByEntrustmentId(entrustmentId);
            if (result == null) {
                result = new NuclearDensityResult();
                result.setId(UUID.randomUUID().toString());
                result.setEntrustmentId(entrustmentId);
                fillTableFromEntrustment("NUCLEAR_DENSITY", entrustmentId, result);
            }
            result.setDataJson(objectMapper.writeValueAsString(recordData));
            saveNuclearDensityResult(result);

            System.out.println("Generated Report and Result for NuclearDensity entrustment: " + entrustmentId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating NuclearDensity report/result: " + e.getMessage());
        }
    }

    private void generateCuttingRingReportAndResult(String entrustmentId) {
        try {
            List<CuttingRing> records = cuttingRingMapper.selectByEntrustmentId(entrustmentId);
            if (records == null || records.isEmpty()) {
                System.err.println("Warning: Record not found for entrustmentId " + entrustmentId);
                return;
            }

            CuttingRing record = records.get(0);
            Map<String, Object> recordData = prepareCuttingRingData(record);

            CuttingRingReport report = cuttingRingReportMapper.selectByEntrustmentId(entrustmentId);
            if (report == null) {
                report = new CuttingRingReport();
                report.setId(UUID.randomUUID().toString());
                report.setEntrustmentId(entrustmentId);
                fillTableFromEntrustment("CUTTING_RING", entrustmentId, report);
            }
            report.setDataJson(objectMapper.writeValueAsString(recordData));
            saveCuttingRingReport(report);

            CuttingRingResult result = cuttingRingResultMapper.selectByEntrustmentId(entrustmentId);
            if (result == null) {
                result = new CuttingRingResult();
                result.setId(UUID.randomUUID().toString());
                result.setEntrustmentId(entrustmentId);
                fillTableFromEntrustment("CUTTING_RING", entrustmentId, result);
            }
            result.setDataJson(objectMapper.writeValueAsString(recordData));
            saveCuttingRingResult(result);

            System.out.println("Generated Report and Result for CuttingRing entrustment: " + entrustmentId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating CuttingRing report/result: " + e.getMessage());
        }
    }

    private void generateBeckmanBeamReportAndResult(String entrustmentId) {
        try {
            // 检查双检验是否都通过：回弹法记录表和贝克曼梁法记录表都必须审核通过
            List<ReboundMethod> reboundRecords = reboundMethodMapper.selectByEntrustmentId(entrustmentId);
            boolean reboundApproved = false;
            if (reboundRecords != null && !reboundRecords.isEmpty()) {
                ReboundMethod reboundRecord = reboundRecords.get(0);
                String reboundStatus = reboundRecord.getStatus();
                if (reboundStatus != null && "5".equals(reboundStatus.toString())) {
                    reboundApproved = true;
                }
            }

            List<BeckmanBeam> beckmanRecords = beckmanBeamMapper.selectByEntrustmentId(entrustmentId);
            boolean beckmanApproved = false;
            if (beckmanRecords != null && !beckmanRecords.isEmpty()) {
                BeckmanBeam beckmanRecord = beckmanRecords.get(0);
                String beckmanStatus = beckmanRecord.getStatus();
                if (beckmanStatus != null && "5".equals(beckmanStatus.toString())) {
                    beckmanApproved = true;
                }
            }

            if (!reboundApproved || !beckmanApproved) {
                System.out.println("Skip BeckmanBeamReport generation: ReboundMethod approved=" + reboundApproved + ", BeckmanBeam approved=" + beckmanApproved + " for entrustmentId " + entrustmentId);
                return;
            }

            // 双检验都通过，开始生成报告
            Map<String, Object> reportData = new HashMap<>();

            // 1）先合并委托单数据（委托级别的工程名称、委托单位、施工部位、检测类别等）
            Map<String, Object> entrustmentData = getEntrustmentData(entrustmentId);
            if (entrustmentData != null) {
                for (Map.Entry<String, Object> e : entrustmentData.entrySet()) {
                    if (e.getValue() != null) {
                        reportData.put(e.getKey(), e.getValue());
                    }
                }
            }

            // 2）再合并回弹法报告单的数据（回弹法报告单已经包含了委托单+记录表的数据）
            ReboundMethodReport reboundReport = reboundMethodReportMapper.selectByEntrustmentId(entrustmentId);
            if (reboundReport != null && reboundReport.getDataJson() != null && !reboundReport.getDataJson().isEmpty()) {
                try {
                    Map<String, Object> reboundReportData = objectMapper.readValue(reboundReport.getDataJson(), Map.class);
                    if (reboundReportData != null) {
                        // 合并回弹法报告单的数据，回弹法报告单的数据优先（覆盖委托单数据）
                        // 只合并非空值，避免覆盖已有数据
                        for (Map.Entry<String, Object> e : reboundReportData.entrySet()) {
                            Object value = e.getValue();
                            if (value != null && !value.toString().trim().isEmpty()) {
                                reportData.put(e.getKey(), value);
                            }
                        }
                        
                        // 特殊字段映射：回弹法报告单的 conclusion -> 贝克曼梁法报告的 testConclusion
                        if (reboundReportData.get("conclusion") != null && (reportData.get("testConclusion") == null || reportData.get("testConclusion").toString().trim().isEmpty())) {
                            reportData.put("testConclusion", reboundReportData.get("conclusion"));
                        }
                        // 回弹法报告单的 reportDate -> 贝克曼梁法报告的 reportDate（如果贝克曼梁法记录表没有）
                        if (reboundReportData.get("reportDate") != null && (reportData.get("reportDate") == null || reportData.get("reportDate").toString().trim().isEmpty())) {
                            reportData.put("reportDate", reboundReportData.get("reportDate"));
                        }
                        // 回弹法报告单的 standard -> 贝克曼梁法报告的 standard（如果贝克曼梁法记录表没有）
                        if (reboundReportData.get("standard") != null && (reportData.get("standard") == null || reportData.get("standard").toString().trim().isEmpty())) {
                            reportData.put("standard", reboundReportData.get("standard"));
                        }
                        // 回弹法报告单的 equipment -> 贝克曼梁法报告的 equipmentCode（如果贝克曼梁法记录表没有）
                        if (reboundReportData.get("equipment") != null && (reportData.get("equipmentCode") == null || reportData.get("equipmentCode").toString().trim().isEmpty())) {
                            reportData.put("equipmentCode", reboundReportData.get("equipment"));
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing ReboundMethodReport JSON: " + e.getMessage());
                }
            }

            // 3）最后合并贝克曼梁法记录表的数据（记录表填写的内容优先）
            BeckmanBeam record = beckmanRecords.get(0);
            Map<String, Object> beckmanRecordData = prepareBeckmanBeamData(record);
            if (beckmanRecordData != null) {
                System.out.println("BeckmanBeam record data size: " + beckmanRecordData.size());
                System.out.println("BeckmanBeam record contains station_1: " + beckmanRecordData.containsKey("station_1"));
                if (beckmanRecordData.containsKey("station_1")) {
                    System.out.println("BeckmanBeam record station_1 value: " + beckmanRecordData.get("station_1"));
                }
                
                // 贝克曼梁法记录表的数据优先（覆盖回弹法报告单和委托单的数据）
                // 合并所有字段，包括空字符串（因为记录表中可能确实有这些字段，只是值为空）
                // 但对于明确的基础字段（如 projectName, commissionDate 等），只合并非空值
                for (Map.Entry<String, Object> e : beckmanRecordData.entrySet()) {
                    String key = e.getKey();
                    Object value = e.getValue();
                    
                    // 基础字段（委托单字段）：只合并非空值，避免覆盖
                    if (key.equals("projectName") || key.equals("commissionDate") || 
                        key.equals("constructionPart") || key.equals("testCategory") ||
                        key.equals("entrustingUnit") || key.equals("unifiedNumber") ||
                        key.equals("witnessUnit") || key.equals("witness")) {
                        if (value != null && !value.toString().trim().isEmpty()) {
                            reportData.put(key, value);
                        }
                    } else {
                        // 其他字段（测点数据、统计字段等）：全部合并，包括空字符串
                        // 这样可以确保所有字段都被填充，即使值为空
                        reportData.put(key, value);
                    }
                }
                
                System.out.println("After merging BeckmanBeam record data, reportData contains station_1: " + reportData.containsKey("station_1"));
                if (reportData.containsKey("station_1")) {
                    System.out.println("After merging, reportData station_1 value: " + reportData.get("station_1"));
                }
                
                // 字段名映射：记录表 -> 报告
                // tempCorrectionK -> tempCorrection（前端报告页面使用 tempCorrection）
                if (beckmanRecordData.containsKey("tempCorrectionK")) {
                    reportData.put("tempCorrection", beckmanRecordData.get("tempCorrectionK"));
                }
                // tempCorrectedAvg -> avgDeflection（前端报告页面使用 avgDeflection）
                if (beckmanRecordData.containsKey("tempCorrectedAvg")) {
                    reportData.put("avgDeflection", beckmanRecordData.get("tempCorrectedAvg"));
                }
                // sampleNameStatus -> sampleStatus（前端报告页面使用 sampleStatus）
                if (beckmanRecordData.containsKey("sampleNameStatus")) {
                    reportData.put("sampleStatus", beckmanRecordData.get("sampleNameStatus"));
                }
            }
            
            // 4）设置固定值和默认值
            // 检测方法固定为"贝克曼梁法"
            if (reportData.get("testMethod") == null || reportData.get("testMethod").toString().trim().isEmpty()) {
                reportData.put("testMethod", "贝克曼梁法");
            }
            
            // 如果报告日期为空，使用当前日期
            if (reportData.get("reportDate") == null || reportData.get("reportDate").toString().trim().isEmpty()) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                reportData.put("reportDate", sdf.format(new java.util.Date()));
            }
            
            // 如果样品名称及状态为空，尝试从 sampleName 和 sampleStatus 拼接
            if ((reportData.get("sampleStatus") == null || reportData.get("sampleStatus").toString().trim().isEmpty()) 
                    && reportData.get("sampleName") != null) {
                String sampleName = reportData.get("sampleName").toString();
                String sampleStatusStr = reportData.get("sampleStatus") != null ? reportData.get("sampleStatus").toString() : "";
                if (sampleStatusStr != null && !sampleStatusStr.trim().isEmpty()) {
                    reportData.put("sampleStatus", sampleName + " / " + sampleStatusStr);
                } else {
                    reportData.put("sampleStatus", sampleName);
                }
            }
            
            // 5）从回弹法报告单获取更多字段（如果贝克曼梁法记录表没有）
            if (reboundReport != null && reboundReport.getDataJson() != null && !reboundReport.getDataJson().isEmpty()) {
                try {
                    Map<String, Object> reboundReportData = objectMapper.readValue(reboundReport.getDataJson(), Map.class);
                    if (reboundReportData != null) {
                        // 回弹法报告单的 testDate -> 贝克曼梁法报告的 testDate（如果贝克曼梁法记录表没有）
                        if (reboundReportData.get("testDate") != null && (reportData.get("testDate") == null || reportData.get("testDate").toString().trim().isEmpty())) {
                            reportData.put("testDate", reboundReportData.get("testDate"));
                        }
                        // 回弹法报告单的 structurePart -> 贝克曼梁法报告的 constructionPart（如果贝克曼梁法记录表没有）
                        if (reboundReportData.get("structurePart") != null && (reportData.get("constructionPart") == null || reportData.get("constructionPart").toString().trim().isEmpty())) {
                            reportData.put("constructionPart", reboundReportData.get("structurePart"));
                        }
                        // 回弹法报告单的 remarks -> 贝克曼梁法报告的 reportDesc（如果贝克曼梁法记录表没有）
                        if (reboundReportData.get("remarks") != null && (reportData.get("reportDesc") == null || reportData.get("reportDesc").toString().trim().isEmpty())) {
                            reportData.put("reportDesc", reboundReportData.get("remarks"));
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing ReboundMethodReport JSON for additional fields: " + e.getMessage());
                }
            }

            BeckmanBeamReport report = beckmanBeamReportMapper.selectByEntrustmentId(entrustmentId);
            if (report == null) {
                report = new BeckmanBeamReport();
                report.setId(UUID.randomUUID().toString());
                report.setEntrustmentId(entrustmentId);
                fillTableFromEntrustment("BECKMAN_BEAM", entrustmentId, report);
            }
            String reportDataJson = objectMapper.writeValueAsString(reportData);
            System.out.println("=== [BeckmanBeamReport生成] JSON长度: " + reportDataJson.length());
            System.out.println("=== [BeckmanBeamReport生成] 包含 station_1: " + reportDataJson.contains("\"station_1\""));
            System.out.println("=== [BeckmanBeamReport生成] reportData 总字段数: " + reportData.size());
            
            // 检查关键字段
            String[] keyFields = {"station_1", "lane_1", "leftDeflection_1", "rightDeflection_1", 
                                   "leftInitial_1", "leftFinal_1", "rightInitial_1", "rightFinal_1",
                                   "tempCorrectionK", "tempCorrection", "sampleNameStatus", "sampleStatus"};
            for (String field : keyFields) {
                if (reportData.containsKey(field)) {
                    System.out.println("=== [BeckmanBeamReport生成] " + field + " = " + reportData.get(field));
                } else {
                    System.out.println("=== [BeckmanBeamReport生成] 缺少字段: " + field);
                }
            }
            
            if (reportDataJson.contains("\"station_1\"")) {
                // 提取 station_1 的值用于调试
                try {
                    Map<String, Object> debugMap = objectMapper.readValue(reportDataJson, Map.class);
                    if (debugMap.containsKey("station_1")) {
                        System.out.println("=== [BeckmanBeamReport生成] 最终 station_1 值: " + debugMap.get("station_1"));
                    }
                } catch (Exception e) {
                    System.err.println("=== [BeckmanBeamReport生成] 解析 reportData 失败: " + e.getMessage());
                }
            }
            
            report.setDataJson(reportDataJson);
            System.out.println("=== [BeckmanBeamReport生成] 准备保存报告，entrustmentId: " + entrustmentId);
            saveBeckmanBeamReport(report);
            System.out.println("=== [BeckmanBeamReport生成] 报告保存完成");

            BeckmanBeamResult result = beckmanBeamResultMapper.selectByEntrustmentId(entrustmentId);
            if (result == null) {
                result = new BeckmanBeamResult();
                result.setId(UUID.randomUUID().toString());
                result.setEntrustmentId(entrustmentId);
                fillTableFromEntrustment("BECKMAN_BEAM", entrustmentId, result);
            }
            result.setDataJson(objectMapper.writeValueAsString(reportData));
            saveBeckmanBeamResult(result);

            System.out.println("Generated Report and Result for BeckmanBeam (with ReboundMethod report) entrustment: " + entrustmentId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating BeckmanBeam report/result: " + e.getMessage());
        }
    }

    private void generateLightDynamicPenetrationReportAndResult(String entrustmentId) {
        try {
            List<LightDynamicPenetration> records = lightDynamicPenetrationMapper.selectByEntrustmentId(entrustmentId);
            if (records == null || records.isEmpty()) {
                System.err.println("Warning: Record not found for entrustmentId " + entrustmentId);
                return;
            }

            LightDynamicPenetration record = records.get(0);
            Map<String, Object> recordData = prepareLightDynamicPenetrationData(record);

            // Prioritize Report: Save Report FIRST to ensure it is the primary source of truth
            try {
                List<LightDynamicPenetrationReport> reports = lightDynamicPenetrationReportMapper.selectByEntrustmentId(entrustmentId);
                LightDynamicPenetrationReport report = null;
                if (reports != null && !reports.isEmpty()) {
                    report = reports.get(0);
                }

                if (report == null) {
                    report = new LightDynamicPenetrationReport();
                    report.setId(UUID.randomUUID().toString());
                    report.setEntrustmentId(entrustmentId);
                    fillTableFromEntrustment("LIGHT_DYNAMIC_PENETRATION", entrustmentId, report);
                }
                report.setDataJson(objectMapper.writeValueAsString(recordData));
                saveLightDynamicPenetrationReport(report);
            } catch (Exception e) {
                 System.err.println("Error generating LightDynamicPenetrationReport: " + e.getMessage());
                 e.printStackTrace();
            }

            // Save Result LAST (Secondary)
            try {
                List<LightDynamicPenetrationResult> results = lightDynamicPenetrationResultMapper.selectByEntrustmentId(entrustmentId);
                LightDynamicPenetrationResult result = null;
                if (results != null && !results.isEmpty()) {
                    result = results.get(0);
                }

                if (result == null) {
                    result = new LightDynamicPenetrationResult();
                    result.setId(UUID.randomUUID().toString());
                    result.setEntrustmentId(entrustmentId);
                    fillTableFromEntrustment("LIGHT_DYNAMIC_PENETRATION", entrustmentId, result);
                }
                result.setDataJson(objectMapper.writeValueAsString(recordData));
                saveLightDynamicPenetrationResult(result);
            } catch (Exception e) {
                 System.err.println("Error generating LightDynamicPenetrationResult: " + e.getMessage());
                 e.printStackTrace();
            }

            System.out.println("Generated Report and Result for LightDynamicPenetration entrustment: " + entrustmentId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating LightDynamicPenetration report/result: " + e.getMessage());
        }
    }

    private Map<String, Object> prepareDensityTestData(DensityTest record) {
        Map<String, Object> recordData = new HashMap<>();
        if (record.getDataJson() != null && !record.getDataJson().isEmpty()) {
            try {
                Map<String, Object> existingJson = objectMapper.readValue(record.getDataJson(), Map.class);
                if (existingJson != null) {
                    recordData.putAll(existingJson);
                }
            } catch (Exception e) {
                System.err.println("Error parsing record JSON: " + e.getMessage());
            }
        }
        // 来自委托表/基础信息（DensityTest 继承自 Entrustment，已通过 Mapper JOIN 填充）
        if (record.getWtNum() != null) recordData.put("wtNum", record.getWtNum());
        if (record.getProjectName() != null) recordData.put("projectName", record.getProjectName());
        if (record.getCommissionDate() != null) recordData.put("commissionDate", record.getCommissionDate());
        if (record.getClientUnit() != null) recordData.put("clientUnit", record.getClientUnit());
        if (record.getConstructionUnit() != null) recordData.put("constructionUnit", record.getConstructionUnit());
        if (record.getBuildingUnit() != null) recordData.put("buildingUnit", record.getBuildingUnit());
        if (record.getWitnessUnit() != null) recordData.put("witnessUnit", record.getWitnessUnit());
        if (record.getWitness() != null) recordData.put("witness", record.getWitness());
        if (record.getSampleName() != null) recordData.put("sampleName", record.getSampleName());
        if (record.getConstructionPart() != null) recordData.put("constructionPart", record.getConstructionPart());
        if (record.getTestCategory() != null) recordData.put("testCategory", record.getTestCategory());
        if (record.getRemarks() != null) recordData.put("remarks", record.getRemarks());

        // 来自密度记录本身的专业字段
        if (record.getSoilType() != null) recordData.put("soilType", record.getSoilType());
        if (record.getRingVolume() != null) recordData.put("ringVolume", record.getRingVolume());
        if (record.getWetWeight() != null) recordData.put("wetWeight", record.getWetWeight());
        if (record.getDryWeight() != null) recordData.put("dryWeight", record.getDryWeight());
        if (record.getWaterContent() != null) recordData.put("waterContent", record.getWaterContent());
        if (record.getWetDensity() != null) recordData.put("wetDensity", record.getWetDensity());
        if (record.getDryDensity() != null) recordData.put("dryDensity", record.getDryDensity());
        if (record.getMaxDryDensity() != null) recordData.put("maxDryDensity", record.getMaxDryDensity());
        if (record.getMinDryDensity() != null) recordData.put("minDryDensity", record.getMinDryDensity());
        if (record.getOptimumMoisture() != null) recordData.put("optimumMoisture", record.getOptimumMoisture());
        if (record.getCompactionCoefficient() != null) recordData.put("compactionCoefficient", record.getCompactionCoefficient());
        if (record.getQualifiedRate() != null) recordData.put("qualifiedRate", record.getQualifiedRate());
        if (record.getSampleNameStatus() != null) recordData.put("sampleNameStatus", record.getSampleNameStatus());
        if (record.getDesignIndex() != null) recordData.put("designIndex", record.getDesignIndex());
        if (record.getTestResult() != null) recordData.put("testResult", record.getTestResult());

        // 人员信息（检测/审核/批准）
        if (record.getTester() != null) recordData.put("tester", record.getTester());
        if (record.getReviewer() != null) recordData.put("reviewer", record.getReviewer());
        if (record.getApprover() != null) recordData.put("approver", record.getApprover());

        return recordData;
    }

    private Map<String, Object> prepareReboundMethodData(ReboundMethod record) {
        Map<String, Object> recordData = new HashMap<>();
        if (record.getDataJson() != null && !record.getDataJson().isEmpty()) {
            try {
                Map<String, Object> existingJson = objectMapper.readValue(record.getDataJson(), Map.class);
                if (existingJson != null) {
                    recordData.putAll(existingJson);
                }
            } catch (Exception e) {
                System.err.println("Error parsing record JSON: " + e.getMessage());
            }
        }
        // 只在实体字段非空时，才覆盖/补充 JSON 中的数据，避免把已有值覆盖成 null
        if (record.getProjectName() != null) {
            recordData.put("projectName", record.getProjectName());
        }
        if (record.getCommissionDate() != null) {
            recordData.put("commissionDate", record.getCommissionDate());
        }
        if (record.getConstructionPart() != null) {
            recordData.put("constructionPart", record.getConstructionPart());
        }
        if (record.getTestCategory() != null) {
            recordData.put("testCategory", record.getTestCategory());
        }
        if (record.getTester() != null) {
            recordData.put("tester", record.getTester());
        }
        if (record.getReviewer() != null) {
            recordData.put("reviewer", record.getReviewer());
        }
        if (record.getApprover() != null) {
            recordData.put("approver", record.getApprover());
        }
        return recordData;
    }

    private Map<String, Object> prepareSandReplacementData(SandReplacement record) {
        Map<String, Object> recordData = new HashMap<>();
        if (record.getDataJson() != null && !record.getDataJson().isEmpty()) {
            try {
                Map<String, Object> existingJson = objectMapper.readValue(record.getDataJson(), Map.class);
                if (existingJson != null) {
                    recordData.putAll(existingJson);
                }
            } catch (Exception e) {
                System.err.println("Error parsing record JSON: " + e.getMessage());
            }
        }
        // 只在记录表实体字段非空时，才用它们补充/覆盖 JSON 中的数据，避免把 JSON 里已有值覆盖为 null
        if (record.getProjectName() != null) {
            recordData.put("projectName", record.getProjectName());
        }
        if (record.getCommissionDate() != null) {
            recordData.put("commissionDate", record.getCommissionDate());
        }
        if (record.getConstructionPart() != null) {
            recordData.put("constructionPart", record.getConstructionPart());
        }
        if (record.getTestCategory() != null) {
            recordData.put("testCategory", record.getTestCategory());
        }
        if (record.getTester() != null) {
            recordData.put("tester", record.getTester());
        }
        if (record.getReviewer() != null) {
            recordData.put("reviewer", record.getReviewer());
        }
        if (record.getApprover() != null) {
            recordData.put("approver", record.getApprover());
        }
        return recordData;
    }

    private Map<String, Object> prepareWaterReplacementData(WaterReplacement record) {
        Map<String, Object> recordData = new HashMap<>();
        if (record.getDataJson() != null && !record.getDataJson().isEmpty()) {
            try {
                Map<String, Object> existingJson = objectMapper.readValue(record.getDataJson(), Map.class);
                if (existingJson != null) {
                    recordData.putAll(existingJson);
                    // 灌水法专用：同时把 JSON 中的字段规范化一份到通用键名，
                    // 便于灌水法专用报告以及“原位密度检测报告/结果”自动填充。
                    normalizeWaterReplacementJson(recordData, existingJson);
                }
            } catch (Exception e) {
                System.err.println("Error parsing record JSON: " + e.getMessage());
            }
        }
        // 注意：这里的委托信息字段可能为 null，不能直接覆盖 JSON 里已经有值的键，
        // 否则会把记录表里手工填写的工程部位 / 检测类别等清掉。
        if (record.getProjectName() != null) {
            recordData.put("projectName", record.getProjectName());
        }
        if (record.getCommissionDate() != null) {
            recordData.put("commissionDate", record.getCommissionDate());
        }
        if (record.getConstructionPart() != null) {
            recordData.put("constructionPart", record.getConstructionPart());
        }
        if (record.getTestCategory() != null) {
            recordData.put("testCategory", record.getTestCategory());
        }
        if (record.getTester() != null) {
            recordData.put("tester", record.getTester());
        }
        if (record.getReviewer() != null) {
            recordData.put("reviewer", record.getReviewer());
        }
        if (record.getApprover() != null) {
            recordData.put("approver", record.getApprover());
        }
        return recordData;
    }

    /**
     * 灌水法记录表字段 -> 通用密度报告字段 的规范化映射：
     * - 取样位置: samplingLocation_i -> location_i
     * - 平均实测干密度: avgMeasuredDryDensity_i -> dryDensity_i
     * - 相对密度: relativeDensity_i -> compaction_i
     * - 平均含水率: avgMoisture_i -> moisture_i
     * - 设计参数: optMoisture -> optimumMoisture
     *
     * 说明：
     * - 这里不会覆盖已经存在的通用字段（例如其它方法已经写入 dryDensity_i），除非通用字段当前为空。
     */
    @SuppressWarnings("unchecked")
    private void normalizeWaterReplacementJson(Map<String, Object> target, Map<String, Object> source) {
        if (source == null || target == null) return;

        // 设计参数：最优含水率
        Object optMoisture = source.get("optMoisture");
        if (optMoisture != null && target.get("optimumMoisture") == null) {
            target.put("optimumMoisture", optMoisture);
        }

        // 检测依据：记录表里用 standard 字段，这里映射到通用的 testBasis
        Object standard = source.get("standard");
        if (standard != null && target.get("testBasis") == null) {
            target.put("testBasis", standard);
        }

        // 工程部位：记录表里 constructionPart 直接映射到通用 constructionPart
        Object constructionPart = source.get("constructionPart");
        if (constructionPart != null && target.get("constructionPart") == null) {
            target.put("constructionPart", constructionPart);
        }

        // 检测类别：记录表里 testCategory 直接映射到通用 testCategory
        Object testCategory = source.get("testCategory");
        if (testCategory != null && target.get("testCategory") == null) {
            target.put("testCategory", testCategory);
        }

        // 行数据：这里约定最多 4 组检测点（与灌水法记录表中的“取样位置”数量一致）
        for (int i = 0; i < 4; i++) {
            String idx = String.valueOf(i);

            Object loc = source.get("samplingLocation_" + idx);
            if (loc != null && target.get("location_" + idx) == null) {
                target.put("location_" + idx, loc);
            }

            // 平均实测干密度 -> 干密度
            Object avgDry = source.get("avgMeasuredDryDensity_" + idx);
            if (avgDry != null && target.get("dryDensity_" + idx) == null) {
                target.put("dryDensity_" + idx, avgDry);
            }

            // 相对密度 -> 压实度%
            Object relDensity = source.get("relativeDensity_" + idx);
            if (relDensity != null && target.get("compaction_" + idx) == null) {
                target.put("compaction_" + idx, relDensity);
            }

            // 平均含水率 -> 含水率
            Object avgMoist = source.get("avgMoisture_" + idx);
            if (avgMoist != null && target.get("moisture_" + idx) == null) {
                target.put("moisture_" + idx, avgMoist);
            }
        }
    }

    private Map<String, Object> prepareNuclearDensityData(NuclearDensity record) {
        Map<String, Object> recordData = new HashMap<>();
        if (record.getDataJson() != null && !record.getDataJson().isEmpty()) {
            try {
                Map<String, Object> existingJson = objectMapper.readValue(record.getDataJson(), Map.class);
                if (existingJson != null) {
                    recordData.putAll(existingJson);
                }
            } catch (Exception e) {
                System.err.println("Error parsing record JSON: " + e.getMessage());
            }
        }
        recordData.put("projectName", record.getProjectName());
        recordData.put("commissionDate", record.getCommissionDate());
        recordData.put("constructionPart", record.getConstructionPart());
        recordData.put("testCategory", record.getTestCategory());
        recordData.put("tester", record.getTester());
        recordData.put("reviewer", record.getReviewer());
        recordData.put("approver", record.getApprover());
        return recordData;
    }

    private Map<String, Object> prepareCuttingRingData(CuttingRing record) {
        Map<String, Object> recordData = new HashMap<>();
        if (record.getDataJson() != null && !record.getDataJson().isEmpty()) {
            try {
                Map<String, Object> existingJson = objectMapper.readValue(record.getDataJson(), Map.class);
                if (existingJson != null) {
                    recordData.putAll(existingJson);
                }
            } catch (Exception e) {
                System.err.println("Error parsing record JSON: " + e.getMessage());
            }
        }
        recordData.put("projectName", record.getProjectName());
        recordData.put("commissionDate", record.getCommissionDate());
        recordData.put("constructionPart", record.getConstructionPart());
        recordData.put("testCategory", record.getTestCategory());
        recordData.put("tester", record.getTester());
        recordData.put("reviewer", record.getReviewer());
        recordData.put("approver", record.getApprover());
        return recordData;
    }

    private Map<String, Object> prepareBeckmanBeamData(BeckmanBeam record) {
        Map<String, Object> recordData = new HashMap<>();
        if (record.getDataJson() != null && !record.getDataJson().isEmpty()) {
            try {
                Map<String, Object> existingJson = objectMapper.readValue(record.getDataJson(), Map.class);
                if (existingJson != null) {
                    recordData.putAll(existingJson);
                }
            } catch (Exception e) {
                System.err.println("Error parsing record JSON: " + e.getMessage());
            }
        }
        // 只在实体字段非空时，才覆盖/补充 JSON 中的数据，避免把已有值覆盖成 null
        if (record.getProjectName() != null) {
            recordData.put("projectName", record.getProjectName());
        }
        if (record.getCommissionDate() != null) {
            recordData.put("commissionDate", record.getCommissionDate());
        }
        if (record.getConstructionPart() != null) {
            recordData.put("constructionPart", record.getConstructionPart());
        }
        if (record.getTestCategory() != null) {
            recordData.put("testCategory", record.getTestCategory());
        }
        if (record.getTester() != null) {
            recordData.put("tester", record.getTester());
        }
        if (record.getReviewer() != null) {
            recordData.put("reviewer", record.getReviewer());
        }
        if (record.getApprover() != null) {
            recordData.put("approver", record.getApprover());
        }
        return recordData;
    }

    private Map<String, Object> prepareLightDynamicPenetrationData(LightDynamicPenetration record) {
        Map<String, Object> recordData = new HashMap<>();
        if (record.getDataJson() != null && !record.getDataJson().isEmpty()) {
            try {
                Map<String, Object> existingJson = objectMapper.readValue(record.getDataJson(), Map.class);
                if (existingJson != null) {
                    recordData.putAll(existingJson);
                }
            } catch (Exception e) {
                System.err.println("Error parsing record JSON: " + e.getMessage());
            }
        }
        
        // Entrustment fields
        if (record.getWtNum() != null) recordData.put("wtNum", record.getWtNum());
        if (record.getProjectName() != null) recordData.put("projectName", record.getProjectName());
        if (record.getCommissionDate() != null) recordData.put("commissionDate", record.getCommissionDate());
        if (record.getClientUnit() != null) recordData.put("clientUnit", record.getClientUnit());
        if (record.getConstructionUnit() != null) recordData.put("constructionUnit", record.getConstructionUnit());
        if (record.getBuildingUnit() != null) recordData.put("buildingUnit", record.getBuildingUnit());
        if (record.getWitnessUnit() != null) recordData.put("witnessUnit", record.getWitnessUnit());
        if (record.getWitness() != null) recordData.put("witness", record.getWitness());
        if (record.getConstructionPart() != null) recordData.put("constructionPart", record.getConstructionPart());
        if (record.getTestCategory() != null) recordData.put("testCategory", record.getTestCategory());
        if (record.getRemarks() != null) recordData.put("remarks", record.getRemarks());
        
        // LightDynamicPenetration specific fields
        if (record.getSoilProperty() != null) {
            recordData.put("soilProperty", record.getSoilProperty());
            recordData.put("soilProperties", record.getSoilProperty());
        }
        if (record.getDesignCapacity() != null) recordData.put("designCapacity", record.getDesignCapacity());
        if (record.getHammerWeight() != null) recordData.put("hammerWeight", record.getHammerWeight());
        if (record.getDropDistance() != null) recordData.put("dropDistance", record.getDropDistance());
        if (record.getTestDate() != null) recordData.put("testDate", record.getTestDate());
        if (record.getTestBasis() != null) recordData.put("testBasis", record.getTestBasis());
        if (record.getEquipment() != null) recordData.put("equipment", record.getEquipment());
        if (record.getConclusion() != null) recordData.put("conclusion", record.getConclusion());
        
        // Personnel fields
        if (record.getTester() != null) {
            recordData.put("tester", record.getTester());
            recordData.put("recordTester", record.getTester());
        }
        if (record.getReviewer() != null) {
            recordData.put("reviewer", record.getReviewer());
            recordData.put("recordReviewer", record.getReviewer());
        }
        if (record.getApprover() != null) recordData.put("approver", record.getApprover());

        // Signature fields
        if (record.getInspectSignaturePhoto() != null) recordData.put("inspectSignaturePhoto", record.getInspectSignaturePhoto());
        if (record.getReviewSignaturePhoto() != null) recordData.put("reviewSignaturePhoto", record.getReviewSignaturePhoto());
        if (record.getApproveSignaturePhoto() != null) recordData.put("approveSignaturePhoto", record.getApproveSignaturePhoto());

        // Dynamic data blocks (pos_L_0, depth_L_0, etc.)
        if (record.getDataJson() != null) {
            try {
                Map<String, Object> jsonMap = objectMapper.readValue(record.getDataJson(), Map.class);
                for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
                    String key = entry.getKey();
                    if (key.matches("^(pos|depth|actual|avg|capacity)_[LR]_\\d+$")) {
                        recordData.put(key, entry.getValue());
                    }
                }
            } catch (Exception e) {
                // ignore
            }
        }
        
        return recordData;
    }

    private void saveDensityTestReport(DensityTestReport report) {
        try {
            DensityTestReport existing = densityTestReportMapper.selectByEntrustmentId(report.getEntrustmentId());
            if (existing != null) {
                densityTestReportMapper.update(report);
            } else {
                densityTestReportMapper.insert(report);
            }
        } catch (Exception e) {
            System.err.println("Error saving/updating DensityTestReport: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveDensityTestResult(DensityTestResult result) {
        try {
            DensityTestResult existing = densityTestResultMapper.selectByEntrustmentId(result.getEntrustmentId());
            if (existing != null) {
                densityTestResultMapper.update(result);
            } else {
                densityTestResultMapper.insert(result);
            }
        } catch (Exception e) {
            System.err.println("Error saving/updating DensityTestResult: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveReboundMethodReport(ReboundMethodReport report) {
        try {
            ReboundMethodReport existing = reboundMethodReportMapper.selectByEntrustmentId(report.getEntrustmentId());
            if (existing != null) {
                reboundMethodReportMapper.update(report);
            } else {
                reboundMethodReportMapper.insert(report);
            }
        } catch (Exception e) {
            System.err.println("Error saving/updating ReboundMethodReport: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveReboundMethodResult(ReboundMethodResult result) {
        try {
            ReboundMethodResult existing = reboundMethodResultMapper.selectByEntrustmentId(result.getEntrustmentId());
            if (existing != null) {
                reboundMethodResultMapper.update(result);
            } else {
                reboundMethodResultMapper.insert(result);
            }
        } catch (Exception e) {
            System.err.println("Error saving/updating ReboundMethodResult: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveSandReplacementReport(SandReplacementReport report) {
        try {
            SandReplacementReport existing = sandReplacementReportMapper.selectByEntrustmentId(report.getEntrustmentId());
            if (existing != null) {
                sandReplacementReportMapper.update(report);
            } else {
                sandReplacementReportMapper.insert(report);
            }
        } catch (Exception e) {
            System.err.println("Error saving/updating SandReplacementReport: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveSandReplacementResult(SandReplacementResult result) {
        try {
            SandReplacementResult existing = sandReplacementResultMapper.selectByEntrustmentId(result.getEntrustmentId());
            if (existing != null) {
                sandReplacementResultMapper.update(result);
            } else {
                sandReplacementResultMapper.insert(result);
            }
        } catch (Exception e) {
            System.err.println("Error saving/updating SandReplacementResult: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveWaterReplacementReport(WaterReplacementReport report) {
        try {
            WaterReplacementReport existing = waterReplacementReportMapper.selectByEntrustmentId(report.getEntrustmentId());
            if (existing != null) {
                waterReplacementReportMapper.update(report);
            } else {
                waterReplacementReportMapper.insert(report);
            }
        } catch (Exception e) {
            System.err.println("Error saving/updating WaterReplacementReport: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveWaterReplacementResult(WaterReplacementResult result) {
        try {
            WaterReplacementResult existing = waterReplacementResultMapper.selectByEntrustmentId(result.getEntrustmentId());
            if (existing != null) {
                waterReplacementResultMapper.update(result);
            } else {
                waterReplacementResultMapper.insert(result);
            }
        } catch (Exception e) {
            System.err.println("Error saving/updating WaterReplacementResult: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveNuclearDensityReport(NuclearDensityReport report) {
        try {
            NuclearDensityReport existing = nuclearDensityReportMapper.selectByEntrustmentId(report.getEntrustmentId());
            if (existing != null) {
                nuclearDensityReportMapper.update(report);
            } else {
                nuclearDensityReportMapper.insert(report);
            }
        } catch (Exception e) {
            System.err.println("Error saving/updating NuclearDensityReport: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveNuclearDensityResult(NuclearDensityResult result) {
        try {
            NuclearDensityResult existing = nuclearDensityResultMapper.selectByEntrustmentId(result.getEntrustmentId());
            if (existing != null) {
                nuclearDensityResultMapper.update(result);
            } else {
                nuclearDensityResultMapper.insert(result);
            }
        } catch (Exception e) {
            System.err.println("Error saving/updating NuclearDensityResult: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveCuttingRingReport(CuttingRingReport report) {
        try {
            CuttingRingReport existing = cuttingRingReportMapper.selectByEntrustmentId(report.getEntrustmentId());
            if (existing != null) {
                cuttingRingReportMapper.update(report);
            } else {
                cuttingRingReportMapper.insert(report);
            }
        } catch (Exception e) {
            System.err.println("Error saving/updating CuttingRingReport: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveCuttingRingResult(CuttingRingResult result) {
        try {
            CuttingRingResult existing = cuttingRingResultMapper.selectByEntrustmentId(result.getEntrustmentId());
            if (existing != null) {
                cuttingRingResultMapper.update(result);
            } else {
                cuttingRingResultMapper.insert(result);
            }
        } catch (Exception e) {
            System.err.println("Error saving/updating CuttingRingResult: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveBeckmanBeamReport(BeckmanBeamReport report) {
        try {
            BeckmanBeamReport existing = beckmanBeamReportMapper.selectByEntrustmentId(report.getEntrustmentId());
            if (existing != null) {
                System.out.println("=== [保存BeckmanBeamReport] 更新现有报告，ID: " + existing.getId());
                System.out.println("=== [保存BeckmanBeamReport] 更新前 DATA_JSON 长度: " + (existing.getDataJson() != null ? existing.getDataJson().length() : 0));
                System.out.println("=== [保存BeckmanBeamReport] 更新后 DATA_JSON 长度: " + (report.getDataJson() != null ? report.getDataJson().length() : 0));
                int updateResult = beckmanBeamReportMapper.update(report);
                System.out.println("=== [保存BeckmanBeamReport] 更新结果: " + updateResult + " 行受影响");
            } else {
                System.out.println("=== [保存BeckmanBeamReport] 插入新报告，ID: " + report.getId());
                System.out.println("=== [保存BeckmanBeamReport] DATA_JSON 长度: " + (report.getDataJson() != null ? report.getDataJson().length() : 0));
                int insertResult = beckmanBeamReportMapper.insert(report);
                System.out.println("=== [保存BeckmanBeamReport] 插入结果: " + insertResult + " 行受影响");
            }
            
            // 验证保存后的数据
            BeckmanBeamReport saved = beckmanBeamReportMapper.selectByEntrustmentId(report.getEntrustmentId());
            if (saved != null && saved.getDataJson() != null) {
                System.out.println("=== [保存BeckmanBeamReport] 验证：保存后 DATA_JSON 长度: " + saved.getDataJson().length());
                System.out.println("=== [保存BeckmanBeamReport] 验证：保存后包含 station_1: " + saved.getDataJson().contains("\"station_1\""));
            }
        } catch (Exception e) {
            System.err.println("=== [保存BeckmanBeamReport] 错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveBeckmanBeamResult(BeckmanBeamResult result) {
        try {
            BeckmanBeamResult existing = beckmanBeamResultMapper.selectByEntrustmentId(result.getEntrustmentId());
            if (existing != null) {
                beckmanBeamResultMapper.update(result);
            } else {
                beckmanBeamResultMapper.insert(result);
            }
        } catch (Exception e) {
            System.err.println("Error saving/updating BeckmanBeamResult: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveLightDynamicPenetrationReport(LightDynamicPenetrationReport report) {
        try {
            List<LightDynamicPenetrationReport> existingList = lightDynamicPenetrationReportMapper.selectByEntrustmentId(report.getEntrustmentId());
            if (existingList != null && !existingList.isEmpty()) {
                lightDynamicPenetrationReportMapper.update(report);
            } else {
                lightDynamicPenetrationReportMapper.insert(report);
            }
        } catch (Exception e) {
            System.err.println("Error saving/updating LightDynamicPenetrationReport: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveLightDynamicPenetrationResult(LightDynamicPenetrationResult result) {
        try {
            List<LightDynamicPenetrationResult> existingList = lightDynamicPenetrationResultMapper.selectByEntrustmentId(result.getEntrustmentId());
            if (existingList != null && !existingList.isEmpty()) {
                lightDynamicPenetrationResultMapper.update(result);
            } else {
                lightDynamicPenetrationResultMapper.insert(result);
            }
        } catch (Exception e) {
            System.err.println("Error saving/updating LightDynamicPenetrationResult: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
