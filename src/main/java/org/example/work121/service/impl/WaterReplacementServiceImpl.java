package org.example.work121.service.impl;

import org.example.work121.entity.WaterReplacement;
import org.example.work121.entity.WaterReplacementReport;
import org.example.work121.entity.WaterReplacementResult;
import org.example.work121.mapper.WaterReplacementMapper;
import org.example.work121.mapper.WaterReplacementReportMapper;
import org.example.work121.mapper.WaterReplacementResultMapper;
import org.example.work121.service.WaterReplacementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class WaterReplacementServiceImpl implements WaterReplacementService {

    @Autowired
    private WaterReplacementMapper mapper;

    @Autowired
    private WaterReplacementReportMapper reportMapper;

    @Autowired
    private WaterReplacementResultMapper resultMapper;

    @Override
    public java.util.List<WaterReplacement> getByEntrustmentId(String entrustmentId) {
        return mapper.selectByEntrustmentId(entrustmentId);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        mapper.deleteById(id);
    }

    @Override
    @Transactional
    public void save(WaterReplacement waterReplacement) {
        if (waterReplacement.getId() != null && mapper.selectById(waterReplacement.getId()) != null) {
            mapper.updateById(waterReplacement);
        } else {
            if (waterReplacement.getId() == null) {
                waterReplacement.setId(UUID.randomUUID().toString());
            }
            mapper.insert(waterReplacement);
        }
    }

    @Override
    public WaterReplacementReport getReportByEntrustmentId(String entrustmentId) {
        return reportMapper.selectByEntrustmentId(entrustmentId);
    }

    @Override
    @Transactional
    public void saveReport(WaterReplacementReport report) {
        WaterReplacementReport existing = reportMapper.selectByEntrustmentId(report.getEntrustmentId());
        if (existing != null) {
            reportMapper.update(report);
        } else {
            if (report.getId() == null) {
                report.setId(UUID.randomUUID().toString());
            }
            reportMapper.insert(report);
        }
    }

    @Override
    public WaterReplacementResult getResultByEntrustmentId(String entrustmentId) {
        return resultMapper.selectByEntrustmentId(entrustmentId);
    }

    @Override
    @Transactional
    public void saveResult(WaterReplacementResult result) {
        WaterReplacementResult existing = resultMapper.selectByEntrustmentId(result.getEntrustmentId());
        if (existing != null) {
            resultMapper.update(result);
        } else {
            if (result.getId() == null) {
                result.setId(UUID.randomUUID().toString());
            }
            resultMapper.insert(result);
        }
    }

    @Override
    @Transactional
    public void generateReportAndResult(String entrustmentId) {
        // Logic to generate report and result from record
        // Since they share the same table (T_WATER_REPLACEMENT), the data is already there.
        // This method serves as a placeholder for any post-approval processing.
        
        java.util.List<WaterReplacement> records = mapper.selectByEntrustmentId(entrustmentId);
        if (records == null || records.isEmpty()) {
            throw new RuntimeException("Cannot generate report/result: Record not found for entrustmentId " + entrustmentId);
        }
        
        System.out.println("Generated Report and Result for WaterReplacement entrustment: " + entrustmentId);
    }
}
