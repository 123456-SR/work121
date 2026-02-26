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
        }
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
            List<JcCoreWtInfo> list = jcCoreWtInfoMapper.selectByWtNum(entrustmentId);
            if (list != null && !list.isEmpty()) {
                JcCoreWtInfo info = list.get(0);
                Map<String, Object> data = new HashMap<>();
                data.put("wtNum", info.getWtNum());
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
                recordData.putAll(entrustmentData);
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
            throw new RuntimeException("Error generating DensityTest report/result: " + e.getMessage());
        }
    }

    /**
     * 将某一类密度记录表中所有记录的 DATA_JSON 合并到 recordData 里。
     * 如果 JSON 字段重复，后出现的会覆盖先前的，同一大类一般字段含义一致，可以接受。
     */
    private <T> void mergeDensityRecordJson(List<T> records,
                                            Map<String, Object> recordData,
                                            Function<T, String> dataJsonGetter,
                                            String debugLabel) {
        if (records == null || records.isEmpty()) {
            return;
        }
        for (T be : records) {
            String json = dataJsonGetter.apply(be);
            if (json != null && !json.isEmpty()) {
                try {
                    Map<String, Object> m = objectMapper.readValue(json, Map.class);
                    if (m != null) {
                        recordData.putAll(m);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing " + debugLabel + " record JSON: " + e.getMessage());
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
            Map<String, Object> recordData = prepareReboundMethodData(record);

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
            List<BeckmanBeam> records = beckmanBeamMapper.selectByEntrustmentId(entrustmentId);
            if (records == null || records.isEmpty()) {
                System.err.println("Warning: Record not found for entrustmentId " + entrustmentId);
                return;
            }

            BeckmanBeam record = records.get(0);
            Map<String, Object> recordData = prepareBeckmanBeamData(record);

            BeckmanBeamReport report = beckmanBeamReportMapper.selectByEntrustmentId(entrustmentId);
            if (report == null) {
                report = new BeckmanBeamReport();
                report.setId(UUID.randomUUID().toString());
                report.setEntrustmentId(entrustmentId);
                fillTableFromEntrustment("BECKMAN_BEAM", entrustmentId, report);
            }
            report.setDataJson(objectMapper.writeValueAsString(recordData));
            saveBeckmanBeamReport(report);

            BeckmanBeamResult result = beckmanBeamResultMapper.selectByEntrustmentId(entrustmentId);
            if (result == null) {
                result = new BeckmanBeamResult();
                result.setId(UUID.randomUUID().toString());
                result.setEntrustmentId(entrustmentId);
                fillTableFromEntrustment("BECKMAN_BEAM", entrustmentId, result);
            }
            result.setDataJson(objectMapper.writeValueAsString(recordData));
            saveBeckmanBeamResult(result);

            System.out.println("Generated Report and Result for BeckmanBeam entrustment: " + entrustmentId);
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

            LightDynamicPenetrationReport report = lightDynamicPenetrationReportMapper.selectByEntrustmentId(entrustmentId);
            if (report == null) {
                report = new LightDynamicPenetrationReport();
                report.setId(UUID.randomUUID().toString());
                report.setEntrustmentId(entrustmentId);
                fillTableFromEntrustment("LIGHT_DYNAMIC_PENETRATION", entrustmentId, report);
            }
            report.setDataJson(objectMapper.writeValueAsString(recordData));
            saveLightDynamicPenetrationReport(report);

            LightDynamicPenetrationResult result = lightDynamicPenetrationResultMapper.selectByEntrustmentId(entrustmentId);
            if (result == null) {
                result = new LightDynamicPenetrationResult();
                result.setId(UUID.randomUUID().toString());
                result.setEntrustmentId(entrustmentId);
                fillTableFromEntrustment("LIGHT_DYNAMIC_PENETRATION", entrustmentId, result);
            }
            result.setDataJson(objectMapper.writeValueAsString(recordData));
            saveLightDynamicPenetrationResult(result);

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
        recordData.put("projectName", record.getProjectName());
        recordData.put("commissionDate", record.getCommissionDate());
        recordData.put("constructionPart", record.getConstructionPart());
        recordData.put("testCategory", record.getTestCategory());
        recordData.put("tester", record.getTester());
        recordData.put("reviewer", record.getReviewer());
        recordData.put("approver", record.getApprover());
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
        recordData.put("projectName", record.getProjectName());
        recordData.put("commissionDate", record.getCommissionDate());
        recordData.put("constructionPart", record.getConstructionPart());
        recordData.put("testCategory", record.getTestCategory());
        recordData.put("tester", record.getTester());
        recordData.put("reviewer", record.getReviewer());
        recordData.put("approver", record.getApprover());
        return recordData;
    }

    private Map<String, Object> prepareWaterReplacementData(WaterReplacement record) {
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
        recordData.put("projectName", record.getProjectName());
        recordData.put("commissionDate", record.getCommissionDate());
        recordData.put("constructionPart", record.getConstructionPart());
        recordData.put("testCategory", record.getTestCategory());
        recordData.put("tester", record.getTester());
        recordData.put("reviewer", record.getReviewer());
        recordData.put("approver", record.getApprover());
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
        recordData.put("soilProperty", record.getSoilProperty());
        recordData.put("soilProperties", record.getSoilProperty());
        recordData.put("designCapacity", record.getDesignCapacity());
        recordData.put("hammerWeight", record.getHammerWeight());
        recordData.put("dropDistance", record.getDropDistance());
        if (record.getTestDate() != null) recordData.put("testDate", record.getTestDate());
        if (record.getCommissionDate() != null) recordData.put("commissionDate", record.getCommissionDate());
        if (record.getConstructionPart() != null) recordData.put("constructionPart", record.getConstructionPart());
        if (record.getTestCategory() != null) recordData.put("testCategory", record.getTestCategory());
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
                beckmanBeamReportMapper.update(report);
            } else {
                beckmanBeamReportMapper.insert(report);
            }
        } catch (Exception e) {
            System.err.println("Error saving/updating BeckmanBeamReport: " + e.getMessage());
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
            LightDynamicPenetrationReport existing = lightDynamicPenetrationReportMapper.selectByEntrustmentId(report.getEntrustmentId());
            if (existing != null) {
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
            LightDynamicPenetrationResult existing = lightDynamicPenetrationResultMapper.selectByEntrustmentId(result.getEntrustmentId());
            if (existing != null) {
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
