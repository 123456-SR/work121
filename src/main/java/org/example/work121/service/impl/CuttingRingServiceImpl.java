package org.example.work121.service.impl;

import org.example.work121.entity.CuttingRing;
import org.example.work121.mapper.CuttingRingMapper;
import org.example.work121.service.CuttingRingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CuttingRingServiceImpl implements CuttingRingService {

    @Autowired
    private CuttingRingMapper mapper;

    @Autowired
    private org.example.work121.mapper.CuttingRingReportMapper reportMapper;

    @Override
    public java.util.List<CuttingRing> getByEntrustmentId(String entrustmentId) {
        return mapper.selectByEntrustmentId(entrustmentId);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        mapper.deleteById(id);
    }

    @Override
    @Transactional
    public void save(CuttingRing cuttingRing) {
        if (cuttingRing.getId() != null && mapper.selectById(cuttingRing.getId()) != null) {
            mapper.updateById(cuttingRing);
        } else {
            if (cuttingRing.getId() == null) {
                cuttingRing.setId(java.util.UUID.randomUUID().toString());
            }
            mapper.insert(cuttingRing);
        }
    }

    @Override
    public org.example.work121.entity.CuttingRingReport getReportByEntrustmentId(String entrustmentId) {
        return reportMapper.selectByEntrustmentId(entrustmentId);
    }

    @Override
    @Transactional
    public void saveReport(org.example.work121.entity.CuttingRingReport report) {
        org.example.work121.entity.CuttingRingReport existing = reportMapper.selectByEntrustmentId(report.getEntrustmentId());
        if (existing != null) {
            reportMapper.update(report);
        } else {
            if (report.getId() == null) {
                report.setId(java.util.UUID.randomUUID().toString());
            }
            reportMapper.insert(report);
        }
    }

    @Override
    @Transactional
    public void generateReportAndResult(String entrustmentId) {
        // Since they share the same table (T_CUTTING_RING), the data is already there.
        // This method serves as a placeholder for any post-approval processing.
        java.util.List<CuttingRing> records = mapper.selectByEntrustmentId(entrustmentId);
        if (records == null || records.isEmpty()) {
            System.err.println("Warning: Record not found for entrustmentId " + entrustmentId + " during generation.");
        } else {
            System.out.println("Generated Report and Result for CuttingRing entrustment: " + entrustmentId);
        }
    }
}
