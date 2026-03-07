package org.example.work121.service.impl;

import org.example.work121.entity.SampleCirculation;
import org.example.work121.mapper.SampleCirculationMapper;
import org.example.work121.service.SampleCirculationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@Service
public class SampleCirculationServiceImpl implements SampleCirculationService {

    @Resource
    private SampleCirculationMapper sampleCirculationMapper;

    @Override
    public SampleCirculation save(SampleCirculation sampleCirculation) {
        if (sampleCirculation.getId() == null || sampleCirculation.getId().isEmpty()) {
            sampleCirculation.setId(UUID.randomUUID().toString());
            sampleCirculationMapper.insert(sampleCirculation);
        } else {
            sampleCirculationMapper.update(sampleCirculation);
        }
        return sampleCirculation;
    }

    @Override
    public SampleCirculation getById(String id) {
        return sampleCirculationMapper.selectById(id);
    }

    @Override
    public SampleCirculation getBySampleNumber(String sampleNumber) {
        return sampleCirculationMapper.selectBySampleNumber(sampleNumber);
    }

    @Override
    public List<SampleCirculation> getAll() {
        return sampleCirculationMapper.selectAll();
    }

    @Override
    public List<SampleCirculation> getByKeyword(String keyword) {
        return sampleCirculationMapper.selectByKeyword(keyword);
    }

    @Override
    public void deleteById(String id) {
        sampleCirculationMapper.deleteById(id);
    }
}