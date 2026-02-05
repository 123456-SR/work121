package org.example.work121.service.impl;

import org.example.work121.entity.CuttingRing;
import org.example.work121.mapper.CuttingRingMapper;
import org.example.work121.service.CuttingRingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CuttingRingServiceImpl implements CuttingRingService {

    @Autowired
    private CuttingRingMapper mapper;

    @Override
    public CuttingRing getByEntrustmentId(String entrustmentId) {
        return mapper.selectByEntrustmentId(entrustmentId);
    }

    @Override
    @Transactional
    public void save(CuttingRing cuttingRing) {
        if (mapper.selectByEntrustmentId(cuttingRing.getEntrustmentId()) != null) {
            mapper.update(cuttingRing);
        } else {
            mapper.insert(cuttingRing);
        }
    }
}
