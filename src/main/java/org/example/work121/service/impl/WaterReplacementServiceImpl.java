package org.example.work121.service.impl;

import org.example.work121.entity.WaterReplacement;
import org.example.work121.mapper.WaterReplacementMapper;
import org.example.work121.service.WaterReplacementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WaterReplacementServiceImpl implements WaterReplacementService {

    @Autowired
    private WaterReplacementMapper mapper;

    @Override
    public WaterReplacement getByEntrustmentId(String entrustmentId) {
        return mapper.selectByEntrustmentId(entrustmentId);
    }

    @Override
    @Transactional
    public void save(WaterReplacement waterReplacement) {
        if (mapper.selectByEntrustmentId(waterReplacement.getEntrustmentId()) != null) {
            mapper.update(waterReplacement);
        } else {
            mapper.insert(waterReplacement);
        }
    }
}
