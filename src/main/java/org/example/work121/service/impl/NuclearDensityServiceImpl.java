package org.example.work121.service.impl;

import org.example.work121.entity.JcCoreWtInfo;
import org.example.work121.entity.NuclearDensity;
import org.example.work121.entity.SimpleDirectory;
import org.example.work121.mapper.NuclearDensityMapper;
import org.example.work121.mapper.SimpleDirectoryMapper;
import org.example.work121.service.JcCoreWtInfoService;
import org.example.work121.service.NuclearDensityService;
import org.example.work121.service.TableGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NuclearDensityServiceImpl implements NuclearDensityService {

    @Autowired
    private NuclearDensityMapper mapper;

    @Autowired
    private org.example.work121.mapper.NuclearDensityReportMapper reportMapper;

    @Autowired
    private SimpleDirectoryMapper simpleDirectoryMapper;

    @Autowired
    private JcCoreWtInfoService jcCoreWtInfoService;

    @Autowired
    private TableGenerationService tableGenerationService;

    @Override
    public List<NuclearDensity> getByEntrustmentId(String entrustmentId) {
        return mapper.selectByEntrustmentId(entrustmentId);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        mapper.deleteById(id);
    }

    @Override
    @Transactional
    public void save(NuclearDensity nuclearDensity) {
        java.util.Date now = new java.util.Date();
        if (nuclearDensity.getCreateTime() == null) {
            nuclearDensity.setCreateTime(now);
        }
        nuclearDensity.setUpdateTime(now);

        populateRolesFromDirectory(nuclearDensity);

        String id = nuclearDensity.getId();
        boolean hasId = id != null && !id.trim().isEmpty();

        if (hasId && mapper.selectById(id) != null) {
            NuclearDensity existing = mapper.selectById(id);
            if (nuclearDensity.getTester() == null) {
                nuclearDensity.setTester(existing.getTester());
            }
            if (nuclearDensity.getReviewer() == null) {
                nuclearDensity.setReviewer(existing.getReviewer());
            }
            if (nuclearDensity.getApprover() == null) {
                nuclearDensity.setApprover(existing.getApprover());
            }
            if (nuclearDensity.getFiller() == null) {
                nuclearDensity.setFiller(existing.getFiller());
            }
            if (nuclearDensity.getRecordTester() == null) {
                nuclearDensity.setRecordTester(existing.getRecordTester());
            }
            if (nuclearDensity.getRecordReviewer() == null) {
                nuclearDensity.setRecordReviewer(existing.getRecordReviewer());
            }
            if (nuclearDensity.getRecordReviewSign() == null) {
                nuclearDensity.setRecordReviewSign(existing.getRecordReviewSign());
            }
            if (nuclearDensity.getStatus() == null) {
                nuclearDensity.setStatus(existing.getStatus());
            }
            if (nuclearDensity.getRejectReason() == null) {
                nuclearDensity.setRejectReason(existing.getRejectReason());
            }
            if (nuclearDensity.getNextHandler() == null) {
                nuclearDensity.setNextHandler(existing.getNextHandler());
            }
            if (nuclearDensity.getCreateBy() == null) {
                nuclearDensity.setCreateBy(existing.getCreateBy());
            }
            if (nuclearDensity.getCreateTime() == null) {
                nuclearDensity.setCreateTime(existing.getCreateTime());
            }
            mapper.updateById(nuclearDensity);
        } else {
            if (!hasId) {
                nuclearDensity.setId(java.util.UUID.randomUUID().toString());
            }
            mapper.insert(nuclearDensity);
        }
    }

    private void populateRolesFromDirectory(NuclearDensity nuclearDensity) {
        if (nuclearDensity == null) return;
        String entrustmentId = nuclearDensity.getEntrustmentId();
        if (entrustmentId == null || entrustmentId.trim().isEmpty()) return;

        SimpleDirectory directory = simpleDirectoryMapper.selectByDirName(entrustmentId);
        if (directory != null) {
            if (nuclearDensity.getFiller() == null || nuclearDensity.getFiller().isEmpty()) {
                nuclearDensity.setFiller(directory.getJcFiller());
            }
            if (nuclearDensity.getRecordTester() == null || nuclearDensity.getRecordTester().isEmpty()) {
                nuclearDensity.setRecordTester(directory.getJcTester());
            }
            if (nuclearDensity.getRecordReviewer() == null || nuclearDensity.getRecordReviewer().isEmpty()) {
                nuclearDensity.setRecordReviewer(directory.getJcReviewer());
            }
            if (nuclearDensity.getTester() == null || nuclearDensity.getTester().isEmpty()) {
                nuclearDensity.setTester(directory.getJcTester());
            }
            if (nuclearDensity.getReviewer() == null || nuclearDensity.getReviewer().isEmpty()) {
                nuclearDensity.setReviewer(directory.getJcReviewer());
            }
            if (nuclearDensity.getApprover() == null || nuclearDensity.getApprover().isEmpty()) {
                nuclearDensity.setApprover(directory.getBgApprover());
            }
        }

        JcCoreWtInfo entrustment = jcCoreWtInfoService.getByWtNum(entrustmentId);
        if (entrustment == null) return;

        if (nuclearDensity.getFiller() == null || nuclearDensity.getFiller().isEmpty()) {
            nuclearDensity.setFiller(entrustment.getFiller());
        }
        if (nuclearDensity.getRecordTester() == null || nuclearDensity.getRecordTester().isEmpty()) {
            nuclearDensity.setRecordTester(entrustment.getRecordTester());
        }
        if (nuclearDensity.getRecordReviewer() == null || nuclearDensity.getRecordReviewer().isEmpty()) {
            nuclearDensity.setRecordReviewer(entrustment.getRecordReviewer());
        }
        if (nuclearDensity.getTester() == null || nuclearDensity.getTester().isEmpty()) {
            nuclearDensity.setTester(entrustment.getTester());
        }
        if (nuclearDensity.getReviewer() == null || nuclearDensity.getReviewer().isEmpty()) {
            nuclearDensity.setReviewer(entrustment.getReviewer());
        }
        if (nuclearDensity.getApprover() == null || nuclearDensity.getApprover().isEmpty()) {
            nuclearDensity.setApprover(entrustment.getApprover());
        }
    }

    @Override
    public org.example.work121.entity.NuclearDensityReport getReportByEntrustmentId(String entrustmentId) {
        return reportMapper.selectByEntrustmentId(entrustmentId);
    }

    @Override
    @Transactional
    public void saveReport(org.example.work121.entity.NuclearDensityReport report) {
        org.example.work121.entity.NuclearDensityReport existing = reportMapper.selectByEntrustmentId(report.getEntrustmentId());
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
        // 与其它试验方法保持一致：先确保存在记录，然后交由 TableGenerationService 统一生成
        java.util.List<NuclearDensity> records = mapper.selectByEntrustmentId(entrustmentId);
        if (records == null || records.isEmpty()) {
            throw new RuntimeException("Cannot generate NuclearDensity report/result: Record not found for entrustmentId " + entrustmentId);
        }

        tableGenerationService.generateReportAndResult("NUCLEAR_DENSITY", entrustmentId);
    }
}
