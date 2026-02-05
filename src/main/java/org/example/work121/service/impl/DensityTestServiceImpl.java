package org.example.work121.service.impl;

import org.example.work121.entity.DensityTest;
import org.example.work121.mapper.DensityTestMapper;
import org.example.work121.service.DensityTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DensityTestServiceImpl implements DensityTestService {

    @Autowired
    private DensityTestMapper mapper;

    @Override
    public DensityTest getByEntrustmentId(String entrustmentId) {
        return mapper.selectByEntrustmentId(entrustmentId);
    }

    @Override
    @Transactional
    public void save(DensityTest densityTest) {
        if (mapper.selectByEntrustmentId(densityTest.getEntrustmentId()) != null) {
            mapper.update(densityTest);
        } else {
            mapper.insert(densityTest);
        }
    }
}
