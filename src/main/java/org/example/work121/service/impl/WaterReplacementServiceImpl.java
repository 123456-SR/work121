package org.example.work121.service.impl;

import org.example.work121.entity.WaterReplacement;
import org.example.work121.entity.WaterReplacementReport;
import org.example.work121.entity.WaterReplacementResult;
import org.example.work121.mapper.WaterReplacementMapper;
import org.example.work121.mapper.WaterReplacementReportMapper;
import org.example.work121.mapper.WaterReplacementResultMapper;
import org.example.work121.service.TableGenerationService;
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

    @Autowired
    private TableGenerationService tableGenerationService;

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
        // 兼容前端传入空字符串 ID（Oracle 中空字符串会当作 NULL 处理，导致 ORA-01400）
        String id = waterReplacement.getId();
        if (id != null && id.trim().isEmpty()) {
            id = null;
            waterReplacement.setId(null);
        }

        // 如果传了有效的 ID 且数据库中已存在，则执行更新
        if (id != null && mapper.selectById(id) != null) {
            mapper.updateById(waterReplacement);
        } else {
            // 否则视为新增，补充生成主键 ID
            if (waterReplacement.getId() == null || waterReplacement.getId().trim().isEmpty()) {
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
        java.util.List<WaterReplacement> records = mapper.selectByEntrustmentId(entrustmentId);
        if (records == null || records.isEmpty()) {
            throw new RuntimeException("Cannot generate WaterReplacement report/result: Record not found for entrustmentId " + entrustmentId);
        }

        // 委托 + 灌水法记录表状态校验以及具体字段映射，集中在 TableGenerationServiceImpl.generateWaterReplacementReportAndResult 中处理
        // 这里仅负责触发统一的报表生成服务
        tableGenerationService.generateReportAndResult("WATER_REPLACEMENT", entrustmentId);
    }
}
