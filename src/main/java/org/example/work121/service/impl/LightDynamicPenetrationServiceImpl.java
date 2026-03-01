package org.example.work121.service.impl;

import org.example.work121.entity.LightDynamicPenetration;
import org.example.work121.entity.LightDynamicPenetrationReport;
import org.example.work121.entity.LightDynamicPenetrationResult;
import org.example.work121.mapper.LightDynamicPenetrationMapper;
import org.example.work121.mapper.LightDynamicPenetrationResultMapper;
import org.example.work121.service.LightDynamicPenetrationService;
import org.example.work121.service.TableGenerationService;
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

    @Autowired
    private TableGenerationService tableGenerationService;

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
                    if (jsonData.get("commissionDate") != null) {
                        Object val = jsonData.get("commissionDate");
                        if (val instanceof Long) entity.setCommissionDate(new java.util.Date((Long) val));
                    }
                    if (jsonData.get("constructionPart") != null) entity.setConstructionPart((String) jsonData.get("constructionPart"));
                    if (jsonData.get("testDate") != null) {
                        Object val = jsonData.get("testDate");
                        if (val instanceof Long) entity.setTestDate(new java.util.Date((Long) val));
                    }
                    if (jsonData.get("soilProperty") != null) {
                        entity.setSoilProperty((String) jsonData.get("soilProperty"));
                    } else if (jsonData.get("soilProperties") != null) {
                        entity.setSoilProperty((String) jsonData.get("soilProperties"));
                    }
                    if (jsonData.get("reportDate") != null) {
                        Object val = jsonData.get("reportDate");
                        if (val instanceof Long) entity.setReportDate(new java.util.Date((Long) val));
                    }
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
        String key = entrustmentId;
        String resolvedId = resolveEntrustmentIdToWtId(key);
        List<LightDynamicPenetration> list = mapper.selectByEntrustmentId(resolvedId);
        if ((list == null || list.isEmpty()) && key != null && !key.trim().isEmpty() && !resolvedId.equals(key)) {
            return mapper.selectByEntrustmentId(key);
        }
        return list;
    }

    @Override
    @Transactional
    public void save(LightDynamicPenetration entity) {
        try {
            if (entity != null && entity.getEntrustmentId() != null && !entity.getEntrustmentId().trim().isEmpty()) {
                entity.setEntrustmentId(resolveEntrustmentIdToWtId(entity.getEntrustmentId()));
            }

            // Prepare data map for DATA_JSON
            Map<String, Object> recordData = new HashMap<>();
            
            // Parse existing DATA_JSON if present
            if (entity.getDataJson() != null && !entity.getDataJson().isEmpty()) {
                try {
                    Map<String, Object> existingJson = objectMapper.readValue(entity.getDataJson(), Map.class);
                    if (existingJson != null) {
                        recordData.putAll(existingJson);
                        // Back-fill entity fields from JSON if missing
                        if (entity.getSoilProperty() == null && existingJson.get("soilProperties") != null) {
                            entity.setSoilProperty((String) existingJson.get("soilProperties"));
                        }
                        if (entity.getCommissionDate() == null && existingJson.get("entrustDate") != null) {
                            // Date conversion might be tricky here, skipping for now or handle string dates
                        }
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
                    mapper.updateById(entity);
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
        List<org.example.work121.entity.LightDynamicPenetrationReport> list = reportMapper.selectByEntrustmentId(resolvedId);
        return (list != null && !list.isEmpty()) ? list.get(0) : null;
    }

    @Override
    @Transactional
    public void saveReport(org.example.work121.entity.LightDynamicPenetrationReport report) {
        if (report.getId() != null && !report.getId().trim().isEmpty()) {
            reportMapper.updateById(report);
        } else {
            List<org.example.work121.entity.LightDynamicPenetrationReport> existingList = reportMapper.selectByEntrustmentId(report.getEntrustmentId());
            if (existingList != null && !existingList.isEmpty()) {
                // If exists, update the first one (or all? Here we assume update by ID is preferred if we knew it, but here we update by EntrustmentId using update())
                // Actually reportMapper.update() updates by entrustmentId.
                reportMapper.update(report);
            } else {
                if (report.getId() == null || report.getId().trim().isEmpty()) {
                    report.setId(UUID.randomUUID().toString());
                }
                reportMapper.insert(report);
            }
        }
    }

    @Override
    public LightDynamicPenetrationResult getResultByEntrustmentId(String entrustmentId) {
        String resolvedId = resolveEntrustmentIdToWtId(entrustmentId);
        List<LightDynamicPenetrationResult> list = resultMapper.selectByEntrustmentId(resolvedId);
        return (list != null && !list.isEmpty()) ? list.get(0) : null;
    }

    @Override
    @Transactional
    public void saveResult(LightDynamicPenetrationResult result) {
        if (result.getId() != null && !result.getId().trim().isEmpty()) {
            resultMapper.updateById(result);
        } else {
            List<LightDynamicPenetrationResult> existingList = resultMapper.selectByEntrustmentId(result.getEntrustmentId());
            if (existingList != null && !existingList.isEmpty()) {
                resultMapper.update(result);
            } else {
                if (result.getId() == null || result.getId().trim().isEmpty()) {
                    result.setId(UUID.randomUUID().toString());
                }
                resultMapper.insert(result);
            }
        }
    }

    @Override
    @Transactional
    public void generateReportAndResult(String entrustmentId) {
        String resolvedId = resolveEntrustmentIdToWtId(entrustmentId);
        // Delegate to TableGenerationService which has the complete logic including proper field mapping
        tableGenerationService.generateReportAndResult("LIGHT_DYNAMIC_PENETRATION", resolvedId);
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
