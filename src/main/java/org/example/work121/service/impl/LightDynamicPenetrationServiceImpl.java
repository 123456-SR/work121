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
        return mapper.selectById(id);
    }

    @Override
    public List<LightDynamicPenetration> getByEntrustmentId(String entrustmentId) {
        return mapper.selectByEntrustmentId(entrustmentId);
    }

    @Override
    @Transactional
    public void save(LightDynamicPenetration entity) {
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
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        mapper.deleteById(id);
    }

    @Override
    public org.example.work121.entity.LightDynamicPenetrationReport getReportByEntrustmentId(String entrustmentId) {
        return reportMapper.selectByEntrustmentId(entrustmentId);
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
        return resultMapper.selectByEntrustmentId(entrustmentId);
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
        List<LightDynamicPenetration> records = mapper.selectByEntrustmentId(entrustmentId);
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
            LightDynamicPenetrationReport report = reportMapper.selectByEntrustmentId(entrustmentId);
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
            LightDynamicPenetrationResult result = resultMapper.selectByEntrustmentId(entrustmentId);
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
}
