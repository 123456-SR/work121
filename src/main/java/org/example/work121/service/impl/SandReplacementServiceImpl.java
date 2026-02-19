package org.example.work121.service.impl;

import org.example.work121.entity.SandReplacement;
import org.example.work121.entity.SandReplacementReport;
import org.example.work121.mapper.SandReplacementMapper;
import org.example.work121.mapper.SandReplacementReportMapper;
import org.example.work121.service.SandReplacementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class SandReplacementServiceImpl implements SandReplacementService {

    @Autowired
    private SandReplacementMapper mapper;

    @Autowired
    private SandReplacementReportMapper reportMapper;

    @Override
    public java.util.List<SandReplacement> getByEntrustmentId(String entrustmentId) {
        return mapper.selectByEntrustmentId(entrustmentId);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        mapper.deleteById(id);
    }

    @Override
    @Transactional
    public void save(SandReplacement sandReplacement) {
        if (sandReplacement.getId() != null && mapper.selectById(sandReplacement.getId()) != null) {
            mapper.update(sandReplacement);
        } else {
            if (sandReplacement.getId() == null) {
                sandReplacement.setId(UUID.randomUUID().toString());
            }
            mapper.insert(sandReplacement);
        }
    }

    @Override
    public SandReplacementReport getReportByEntrustmentId(String entrustmentId) {
        return reportMapper.selectByEntrustmentId(entrustmentId);
    }

    @Override
    @Transactional
    public void saveReport(SandReplacementReport report) {
        SandReplacementReport existing = reportMapper.selectByEntrustmentId(report.getEntrustmentId());
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
    @Transactional
    public void generateReportAndResult(String entrustmentId) {
        // Since they share the same table, the data is already there.
        // Placeholder for post-approval logic.
        java.util.List<SandReplacement> records = mapper.selectByEntrustmentId(entrustmentId);
        if (records == null || records.isEmpty()) {
             System.err.println("Warning: Record not found for entrustmentId " + entrustmentId + " during generation.");
        } else {
             System.out.println("Generated Report and Result for SandReplacement entrustment: " + entrustmentId);
        }
    }
}
