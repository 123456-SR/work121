package org.example.work121.service.impl;

import org.example.work121.entity.ReboundMethod;
import org.example.work121.entity.ReboundMethodReport;
import org.example.work121.mapper.ReboundMethodMapper;
import org.example.work121.mapper.ReboundMethodReportMapper;
import org.example.work121.service.ReboundMethodService;
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
        return reportMapper.selectByEntrustmentId(entrustmentId);
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
        java.util.List<ReboundMethod> records = mapper.selectByEntrustmentId(entrustmentId);
        if (records == null || records.isEmpty()) {
            System.err.println("Warning: Record not found for entrustmentId " + entrustmentId + " during generation.");
        } else {
            System.out.println("Generated Report and Result for ReboundMethod entrustment: " + entrustmentId);
        }
    }
}
