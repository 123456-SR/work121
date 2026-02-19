package org.example.work121.service.impl;

import org.example.work121.entity.BeckmanBeam;
import org.example.work121.entity.BeckmanBeamReport;
import org.example.work121.entity.BeckmanBeamResult;
import org.example.work121.mapper.BeckmanBeamMapper;
import org.example.work121.service.BeckmanBeamService;
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
        return reportMapper.selectByEntrustmentId(entrustmentId);
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
        return resultMapper.selectByEntrustmentId(entrustmentId);
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
        List<BeckmanBeam> records = mapper.selectByEntrustmentId(entrustmentId);
        if (records == null || records.isEmpty()) {
            throw new RuntimeException("Cannot generate report/result: Record not found for entrustmentId " + entrustmentId);
        }

        BeckmanBeam record = records.get(0);

        Map<String, Object> data = new HashMap<>();

        if (record.getDataJson() != null && !record.getDataJson().isEmpty()) {
            try {
                Map<String, Object> parsed = new com.fasterxml.jackson.databind.ObjectMapper().readValue(record.getDataJson(), Map.class);
                if (parsed != null) {
                    data.putAll(parsed);
                }
            } catch (Exception e) {
                System.err.println("Error parsing BeckmanBeam record JSON: " + e.getMessage());
            }
        }

        if (record.getSubgradeType() != null) data.put("pavementType", record.getSubgradeType());
        if (record.getDeflectometerType() != null) data.put("equipmentCode", record.getDeflectometerType());
        if (record.getTestLength() != null) data.put("testKm", record.getTestLength());
        if (record.getAxleWeight() != null) data.put("rearAxleWeight", record.getAxleWeight());
        if (record.getTirePressure() != null) {
            data.put("tirePressureLeft", record.getTirePressure());
            data.put("tirePressureRight", record.getTirePressure());
        }

        String mergedJson;
        try {
            mergedJson = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(data);
        } catch (Exception e) {
            throw new RuntimeException("Cannot serialize BeckmanBeam data JSON: " + e.getMessage());
        }

        BeckmanBeamReport report = reportMapper.selectByEntrustmentId(entrustmentId);
        if (report != null) {
            report.setDataJson(mergedJson);
            reportMapper.update(report);
        }

        BeckmanBeamResult result = resultMapper.selectByEntrustmentId(entrustmentId);
        if (result != null) {
            result.setDataJson(mergedJson);
            resultMapper.update(result);
        }

        System.out.println("Generated Report and Result for BeckmanBeam entrustment: " + entrustmentId);
    }
}
