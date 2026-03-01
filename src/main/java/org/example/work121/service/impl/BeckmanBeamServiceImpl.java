package org.example.work121.service.impl;

import org.example.work121.entity.BeckmanBeam;
import org.example.work121.entity.BeckmanBeamReport;
import org.example.work121.entity.BeckmanBeamResult;
import org.example.work121.mapper.BeckmanBeamMapper;
import org.example.work121.mapper.JcCoreWtInfoMapper;
import org.example.work121.entity.JcCoreWtInfo;
import org.example.work121.service.BeckmanBeamService;
import org.example.work121.service.TableGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BeckmanBeamServiceImpl implements BeckmanBeamService {

    @Autowired
    private BeckmanBeamMapper mapper;

    @Autowired
    private org.example.work121.mapper.BeckmanBeamReportMapper reportMapper;

    @Autowired
    private org.example.work121.mapper.BeckmanBeamResultMapper resultMapper;

    @Autowired
    private JcCoreWtInfoMapper jcCoreWtInfoMapper;

    @Autowired
    private TableGenerationService tableGenerationService;

    @Override
    public java.util.List<BeckmanBeam> getByEntrustmentId(String entrustmentId) {
        return mapper.selectByEntrustmentId(entrustmentId);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        mapper.deleteById(id);
    }

    @Override
    @Transactional
    public void save(BeckmanBeam beckmanBeam) {
        if (beckmanBeam.getId() != null && mapper.selectById(beckmanBeam.getId()) != null) {
            mapper.updateById(beckmanBeam);
        } else {
            if (beckmanBeam.getId() == null) {
                beckmanBeam.setId(java.util.UUID.randomUUID().toString());
            }
            mapper.insert(beckmanBeam);
        }
    }

    @Override
    public org.example.work121.entity.BeckmanBeamReport getReportByEntrustmentId(String entrustmentId) {
        // 兼容两种用法：
        // 1）entrustmentId 传入统一编号（WT_NUM），直接用作 ENTRUSTMENT_ID
        // 2）entrustmentId 传入委托主键 ID（WT_ID），需要先查出对应的 WT_NUM 再用作 ENTRUSTMENT_ID
        org.example.work121.entity.BeckmanBeamReport report = reportMapper.selectByEntrustmentId(entrustmentId);
        if (report == null && entrustmentId != null && entrustmentId.length() > 20) {
            try {
                JcCoreWtInfo entrustment = jcCoreWtInfoMapper.selectById(entrustmentId);
                if (entrustment != null && entrustment.getWtNum() != null) {
                    report = reportMapper.selectByEntrustmentId(entrustment.getWtNum());
                }
            } catch (Exception e) {
                System.err.println("Error looking up BeckmanBeamReport by entrustmentId " + entrustmentId + ": " + e.getMessage());
            }
        }
        return report;
    }

    @Override
    @Transactional
    public void saveReport(org.example.work121.entity.BeckmanBeamReport report) {
        org.example.work121.entity.BeckmanBeamReport existing = reportMapper.selectByEntrustmentId(report.getEntrustmentId());
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
    public org.example.work121.entity.BeckmanBeamResult getResultByEntrustmentId(String entrustmentId) {
        org.example.work121.entity.BeckmanBeamResult result = resultMapper.selectByEntrustmentId(entrustmentId);
        if (result == null && entrustmentId != null && entrustmentId.length() > 20) {
            try {
                JcCoreWtInfo entrustment = jcCoreWtInfoMapper.selectById(entrustmentId);
                if (entrustment != null && entrustment.getWtNum() != null) {
                    result = resultMapper.selectByEntrustmentId(entrustment.getWtNum());
                }
            } catch (Exception e) {
                System.err.println("Error looking up BeckmanBeamResult by entrustmentId " + entrustmentId + ": " + e.getMessage());
            }
        }
        return result;
    }

    @Override
    @Transactional
    public void saveResult(org.example.work121.entity.BeckmanBeamResult result) {
        org.example.work121.entity.BeckmanBeamResult existing = resultMapper.selectByEntrustmentId(result.getEntrustmentId());
        if (existing != null) {
            resultMapper.update(result);
        } else {
            if (result.getId() == null) {
                result.setId(java.util.UUID.randomUUID().toString());
            }
            resultMapper.insert(result);
        }
    }

    @Override
    @Transactional
    public void generateReportAndResult(String entrustmentId) {
        // 委托给 TableGenerationService 统一生成报告和结果
        // 具体的数据合并逻辑（委托单 + 回弹法报告单 + 贝克曼梁法记录表）在 TableGenerationServiceImpl.generateBeckmanBeamReportAndResult 中处理
        tableGenerationService.generateReportAndResult("BECKMAN_BEAM", entrustmentId);
    }
}
