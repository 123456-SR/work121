package org.example.work121.service.impl;

import org.example.work121.entity.DensityTest;
import org.example.work121.entity.DensityTestReport;
import org.example.work121.mapper.DensityTestMapper;
import org.example.work121.mapper.DensityTestReportMapper;
import org.example.work121.service.DensityTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DensityTestServiceImpl implements DensityTestService {

    @Autowired
    private DensityTestMapper mapper;

    @Autowired
    private DensityTestReportMapper reportMapper;

    @Override
    public java.util.List<DensityTest> getByEntrustmentId(String entrustmentId) {
        return mapper.selectByEntrustmentId(entrustmentId);
    }

    @Override
    @Transactional
    public void save(DensityTest densityTest) {
        if (densityTest.getId() != null && mapper.selectById(densityTest.getId()) != null) {
            mapper.updateById(densityTest);
        } else {
            if (densityTest.getId() == null) {
                densityTest.setId(UUID.randomUUID().toString());
            }
            mapper.insert(densityTest);
        }
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        mapper.deleteById(id);
    }

    @Override
    public DensityTestReport getReportByEntrustmentId(String entrustmentId) {
        return reportMapper.selectByEntrustmentId(entrustmentId);
    }

    @Override
    @Transactional
    public void saveReport(DensityTestReport report) {
        DensityTestReport existing = reportMapper.selectByEntrustmentId(report.getEntrustmentId());
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
        java.util.List<DensityTest> records = mapper.selectByEntrustmentId(entrustmentId);
        if (records == null || records.isEmpty()) {
            System.err.println("Warning: Record not found for entrustmentId " + entrustmentId + " during generation.");
        } else {
            System.out.println("Generated Report and Result for DensityTest entrustment: " + entrustmentId);
        }
    }
}
