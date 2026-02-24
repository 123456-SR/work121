package org.example.work121.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.work121.entity.*;
import org.example.work121.mapper.*;
import org.example.work121.service.TableGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
            List<DensityTest> records = densityTestMapper.selectByEntrustmentId(entrustmentId);
            if (records == null || records.isEmpty()) {
                System.err.println("Warning: Record not found for entrustmentId " + entrustmentId);
                return;
            }

            DensityTest record = records.get(0);
            Map<String, Object> recordData = prepareDensityTestData(record);

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
        recordData.put("projectName", record.getProjectName());
        recordData.put("commissionDate", record.getCommissionDate());
        recordData.put("constructionPart", record.getConstructionPart());
        recordData.put("testCategory", record.getTestCategory());
        recordData.put("tester", record.getTester());
        recordData.put("reviewer", record.getReviewer());
        recordData.put("approver", record.getApprover());
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
