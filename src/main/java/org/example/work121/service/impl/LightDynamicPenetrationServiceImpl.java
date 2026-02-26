package org.example.work121.service.impl;

import org.example.work121.entity.LightDynamicPenetration;
import org.example.work121.entity.LightDynamicPenetrationReport;
import org.example.work121.entity.LightDynamicPenetrationResult;
import org.example.work121.mapper.LightDynamicPenetrationMapper;
import org.example.work121.mapper.LightDynamicPenetrationResultMapper;
import org.example.work121.service.LightDynamicPenetrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class LightDynamicPenetrationServiceImpl implements LightDynamicPenetrationService {

    @Autowired
    private LightDynamicPenetrationMapper mapper;

    @Autowired
    private org.example.work121.mapper.LightDynamicPenetrationReportMapper reportMapper;

    @Autowired
    private LightDynamicPenetrationResultMapper resultMapper;

    @Autowired
    private org.example.work121.mapper.JcCoreWtInfoMapper jcCoreWtInfoMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public LightDynamicPenetration getById(String id) {
        LightDynamicPenetration entity = mapper.selectById(id);
        if (entity != null && entity.getDataJson() != null && !entity.getDataJson().isEmpty()) {
            try {
                Map<String, Object> jsonData = objectMapper.readValue(entity.getDataJson(), Map.class);
                if (jsonData != null) {
                    if (jsonData.get("clientUnit") != null) entity.setClientUnit((String) jsonData.get("clientUnit"));
                    if (jsonData.get("projectName") != null) entity.setProjectName((String) jsonData.get("projectName"));
                    if (jsonData.get("commissionDate") != null) entity.setCommissionDate((java.util.Date) jsonData.get("commissionDate"));
                    if (jsonData.get("constructionPart") != null) entity.setConstructionPart((String) jsonData.get("constructionPart"));
                    if (jsonData.get("testDate") != null) entity.setTestDate((java.util.Date) jsonData.get("testDate"));
                    if (jsonData.get("soilProperty") != null) entity.setSoilProperty((String) jsonData.get("soilProperty"));
                    if (jsonData.get("reportDate") != null) entity.setReportDate((java.util.Date) jsonData.get("reportDate"));
                    if (jsonData.get("witnessUnit") != null) entity.setWitnessUnit((String) jsonData.get("witnessUnit"));
                    if (jsonData.get("witness") != null) entity.setWitness((String) jsonData.get("witness"));
                    if (jsonData.get("designCapacity") != null) entity.setDesignCapacity((String) jsonData.get("designCapacity"));
                    if (jsonData.get("hammerWeight") != null) entity.setHammerWeight((String) jsonData.get("hammerWeight"));
                    if (jsonData.get("dropDistance") != null) entity.setDropDistance((String) jsonData.get("dropDistance"));
                    if (jsonData.get("testCategory") != null) entity.setTestCategory((String) jsonData.get("testCategory"));
                    if (jsonData.get("testBasis") != null) entity.setTestBasis((String) jsonData.get("testBasis"));
                    if (jsonData.get("equipment") != null) entity.setEquipment((String) jsonData.get("equipment"));
                    if (jsonData.get("remarks") != null) entity.setRemarks((String) jsonData.get("remarks"));
                    if (jsonData.get("conclusion") != null) entity.setConclusion((String) jsonData.get("conclusion"));
                    if (jsonData.get("constructionUnit") != null) entity.setConstructionUnit((String) jsonData.get("constructionUnit"));
                }
            } catch (Exception e) {
                System.err.println("Error parsing DATA_JSON: " + e.getMessage());
            }
        }
        return entity;
    }

    @Override
    public List<LightDynamicPenetration> getByEntrustmentId(String entrustmentId) {
        // 兼容前端传 WT_NUM 或 WT_ID：若传入的是 WT_NUM，则先解析出 WT_ID
        String resolvedId = resolveEntrustmentIdToWtId(entrustmentId);
        return mapper.selectByEntrustmentId(resolvedId);
    }

    @Override
    @Transactional
    public void save(LightDynamicPenetration entity) {
        try {
            // Prepare data map for DATA_JSON
            Map<String, Object> recordData = new HashMap<>();
            
            // Parse existing DATA_JSON if present
            if (entity.getDataJson() != null && !entity.getDataJson().isEmpty()) {
                try {
                    Map<String, Object> existingJson = objectMapper.readValue(entity.getDataJson(), Map.class);
                    if (existingJson != null) {
                        recordData.putAll(existingJson);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing record JSON: " + e.getMessage());
                }
            }
            
            // Add all business fields to DATA_JSON
            recordData.put("clientUnit", entity.getClientUnit());
            recordData.put("projectName", entity.getProjectName());
            recordData.put("commissionDate", entity.getCommissionDate());
            recordData.put("constructionPart", entity.getConstructionPart());
            recordData.put("testDate", entity.getTestDate());
            recordData.put("soilProperty", entity.getSoilProperty());
            recordData.put("reportDate", entity.getReportDate());
            recordData.put("witnessUnit", entity.getWitnessUnit());
            recordData.put("witness", entity.getWitness());
            recordData.put("designCapacity", entity.getDesignCapacity());
            recordData.put("hammerWeight", entity.getHammerWeight());
            recordData.put("dropDistance", entity.getDropDistance());
            recordData.put("testCategory", entity.getTestCategory());
            recordData.put("testBasis", entity.getTestBasis());
            recordData.put("equipment", entity.getEquipment());
            recordData.put("remarks", entity.getRemarks());
            recordData.put("conclusion", entity.getConclusion());
            recordData.put("constructionUnit", entity.getConstructionUnit());
            
            // Serialize to JSON
            String mergedJson = objectMapper.writeValueAsString(recordData);
            entity.setDataJson(mergedJson);
            
            if (entity.getId() == null || entity.getId().trim().isEmpty()) {
                entity.setId(UUID.randomUUID().toString());
                mapper.insert(entity);
            } else {
                if (mapper.countById(entity.getId()) > 0) {
                    mapper.update(entity);
                } else {
                    mapper.insert(entity);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error saving LightDynamicPenetration: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        mapper.deleteById(id);
    }

    @Override
    public org.example.work121.entity.LightDynamicPenetrationReport getReportByEntrustmentId(String entrustmentId) {
        String resolvedId = resolveEntrustmentIdToWtId(entrustmentId);
        return reportMapper.selectByEntrustmentId(resolvedId);
    }

    @Override
    @Transactional
    public void saveReport(org.example.work121.entity.LightDynamicPenetrationReport report) {
        org.example.work121.entity.LightDynamicPenetrationReport existing = reportMapper.selectByEntrustmentId(report.getEntrustmentId());
        if (existing != null) {
            reportMapper.update(report);
        } else {
            if (report.getId() == null || report.getId().trim().isEmpty()) {
                report.setId(UUID.randomUUID().toString());
            }
            reportMapper.insert(report);
        }
    }

    @Override
    public LightDynamicPenetrationResult getResultByEntrustmentId(String entrustmentId) {
        String resolvedId = resolveEntrustmentIdToWtId(entrustmentId);
        return resultMapper.selectByEntrustmentId(resolvedId);
    }

    @Override
    @Transactional
    public void saveResult(LightDynamicPenetrationResult result) {
        LightDynamicPenetrationResult existing = resultMapper.selectByEntrustmentId(result.getEntrustmentId());
        if (existing != null) {
            resultMapper.update(result);
        } else {
            if (result.getId() == null || result.getId().trim().isEmpty()) {
                result.setId(UUID.randomUUID().toString());
            }
            resultMapper.insert(result);
        }
    }

    @Override
    @Transactional
    public void generateReportAndResult(String entrustmentId) {
        String resolvedId = resolveEntrustmentIdToWtId(entrustmentId);
        List<LightDynamicPenetration> records = mapper.selectByEntrustmentId(resolvedId);
        if (records == null || records.isEmpty()) {
            System.err.println("Warning: Record not found for entrustmentId " + entrustmentId + " during generation.");
            return;
        }

        LightDynamicPenetration record = records.get(0);

        try {
            // Prepare data map from record
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

            // Add specific fields to data map so they are available in Report/Result JSON
            // Using both property names to ensure frontend compatibility
            recordData.put("soilProperty", record.getSoilProperty());
            recordData.put("soilProperties", record.getSoilProperty());
            recordData.put("designCapacity", record.getDesignCapacity());
            recordData.put("hammerWeight", record.getHammerWeight());
            recordData.put("dropDistance", record.getDropDistance());
            
            // Add date fields nicely formatted if needed, or just as objects (Jackson handles dates)
            if (record.getTestDate() != null) recordData.put("testDate", record.getTestDate());
            if (record.getCommissionDate() != null) recordData.put("commissionDate", record.getCommissionDate());
            
            if (record.getConstructionPart() != null) recordData.put("constructionPart", record.getConstructionPart());
            if (record.getTestCategory() != null) recordData.put("testCategory", record.getTestCategory());

            String mergedJson = objectMapper.writeValueAsString(recordData);

            // Update Report
            LightDynamicPenetrationReport report = reportMapper.selectByEntrustmentId(resolvedId);
            if (report != null) {
                report.setDataJson(mergedJson);
                // Copy standard fields
                if (record.getProjectName() != null) report.setProjectName(record.getProjectName());
                if (record.getCommissionDate() != null) report.setCommissionDate(record.getCommissionDate());
                if (record.getTestDate() != null) report.setTestDate(record.getTestDate());
                if (record.getConstructionPart() != null) report.setConstructionPart(record.getConstructionPart());
                
                reportMapper.update(report);
            }

            // Update Result
            LightDynamicPenetrationResult result = resultMapper.selectByEntrustmentId(resolvedId);
            if (result != null) {
                result.setDataJson(mergedJson);
                if (record.getProjectName() != null) result.setProjectName(record.getProjectName());
                if (record.getCommissionDate() != null) result.setCommissionDate(record.getCommissionDate());
                if (record.getTestDate() != null) result.setTestDate(record.getTestDate());
                if (record.getConstructionPart() != null) result.setConstructionPart(record.getConstructionPart());
                
                resultMapper.update(result);
            }

            System.out.println("Generated Report and Result for LightDynamicPenetration entrustment: " + entrustmentId);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating report/result: " + e.getMessage());
        }
    }

    /**
     * 解析“委托ID”入参：兼容传 WT_NUM 或 WT_ID，最终返回 WT_ID
     */
    private String resolveEntrustmentIdToWtId(String entrustmentIdOrWtNum) {
        if (entrustmentIdOrWtNum == null || entrustmentIdOrWtNum.trim().isEmpty()) {
            return entrustmentIdOrWtNum;
        }
        String key = entrustmentIdOrWtNum.trim();
        try {
            // 若传入的是 WT_NUM，则可通过 selectByWtNum 得到 WT_ID
            List<org.example.work121.entity.JcCoreWtInfo> list = jcCoreWtInfoMapper.selectByWtNum(key);
            if (list != null && !list.isEmpty() && list.get(0) != null && list.get(0).getId() != null && !list.get(0).getId().trim().isEmpty()) {
                return list.get(0).getId();
            }
        } catch (Exception e) {
            // ignore, fallback to original
        }
        return key;
    }
}
