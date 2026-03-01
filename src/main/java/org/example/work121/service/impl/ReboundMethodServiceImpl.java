package org.example.work121.service.impl;

import org.example.work121.entity.JcCoreWtInfo;
import org.example.work121.entity.ReboundMethod;
import org.example.work121.entity.ReboundMethodReport;
import org.example.work121.mapper.JcCoreWtInfoMapper;
import org.example.work121.mapper.ReboundMethodMapper;
import org.example.work121.mapper.ReboundMethodReportMapper;
import org.example.work121.service.ReboundMethodService;
import org.example.work121.service.TableGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 回弹法检测服务实现类
 */
@Service
public class ReboundMethodServiceImpl implements ReboundMethodService {

    @Autowired
    private ReboundMethodMapper mapper;

    @Autowired
    private ReboundMethodReportMapper reportMapper;

    @Autowired
    private TableGenerationService tableGenerationService;

    @Autowired
    private JcCoreWtInfoMapper jcCoreWtInfoMapper;

    @Override
    @Transactional
    public boolean saveReboundMethod(ReboundMethod reboundMethod) {
        try {
            if (reboundMethod.getEntrustmentId() == null) {
                // 如果没有关联委托单ID，无法保存
                return false;
            }

            if (reboundMethod.getId() != null && mapper.selectById(reboundMethod.getId()) != null) {
                // 更新现有记录
                mapper.updateById(reboundMethod);
            } else {
                // 插入新记录
                if (reboundMethod.getId() == null) {
                    reboundMethod.setId(UUID.randomUUID().toString());
                }
                mapper.insert(reboundMethod);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public java.util.List<ReboundMethod> getReboundMethodByUnifiedNumber(String unifiedNumber) {
        return mapper.selectByEntrustmentId(unifiedNumber);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        mapper.deleteById(id);
    }

    @Override
    public ReboundMethodReport getReportByEntrustmentId(String entrustmentId) {
        // 兼容两种用法：
        // 1）entrustmentId 传入统一编号（WT_NUM），直接查询
        // 2）entrustmentId 传入委托主键 ID（WT_ID），先查委托单获取统一编号，再用统一编号查询报告
        ReboundMethodReport report = reportMapper.selectByEntrustmentId(entrustmentId);
        if (report == null && entrustmentId != null && entrustmentId.length() > 20) {
            // 如果查不到且 entrustmentId 看起来像UUID（长度>20），尝试按委托单ID查询统一编号
            try {
                JcCoreWtInfo entrustment = jcCoreWtInfoMapper.selectById(entrustmentId);
                if (entrustment != null && entrustment.getWtNum() != null) {
                    report = reportMapper.selectByEntrustmentId(entrustment.getWtNum());
                }
            } catch (Exception e) {
                System.err.println("Error looking up entrustment by ID " + entrustmentId + ": " + e.getMessage());
            }
        }
        return report;
    }

    @Override
    @Transactional
    public boolean saveReport(ReboundMethodReport report) {
        try {
            ReboundMethodReport existing = reportMapper.selectByEntrustmentId(report.getEntrustmentId());
            if (existing != null) {
                reportMapper.update(report);
            } else {
                if (report.getId() == null) {
                    report.setId(UUID.randomUUID().toString());
                }
                reportMapper.insert(report);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional
    public void generateReportAndResult(String entrustmentId) {
        // 这里不再自己拼装报告，而是统一委托给 TableGenerationService，
        // 和灌水法、核子法等保持同一套“委托单 + 记录表 -> 报告/结果”的生成逻辑。
        java.util.List<ReboundMethod> records = mapper.selectByEntrustmentId(entrustmentId);
        if (records == null || records.isEmpty()) {
            throw new RuntimeException("Cannot generate ReboundMethod report/result: Record not found for entrustmentId " + entrustmentId);
        }

        tableGenerationService.generateReportAndResult("REBOUND_METHOD", entrustmentId);
    }
}
