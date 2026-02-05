package org.example.work121.service.impl;

import org.example.work121.entity.NuclearDensity;
import org.example.work121.mapper.NuclearDensityMapper;
import org.example.work121.service.NuclearDensityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NuclearDensityServiceImpl implements NuclearDensityService {

    @Autowired
    private NuclearDensityMapper mapper;

    @Override
    public NuclearDensity getByEntrustmentId(String entrustmentId) {
        return mapper.selectByEntrustmentId(entrustmentId);
    }

    @Override
    @Transactional
    public void save(NuclearDensity nuclearDensity) {
        if (mapper.selectByEntrustmentId(nuclearDensity.getEntrustmentId()) != null) {
            mapper.update(nuclearDensity);
        } else {
            mapper.insert(nuclearDensity);
        }
    }
}
