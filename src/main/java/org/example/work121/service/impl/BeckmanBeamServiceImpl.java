package org.example.work121.service.impl;

import org.example.work121.entity.BeckmanBeam;
import org.example.work121.mapper.BeckmanBeamMapper;
import org.example.work121.service.BeckmanBeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BeckmanBeamServiceImpl implements BeckmanBeamService {

    @Autowired
    private BeckmanBeamMapper mapper;

    @Override
    public BeckmanBeam getByEntrustmentId(String entrustmentId) {
        return mapper.selectByEntrustmentId(entrustmentId);
    }

    @Override
    @Transactional
    public void save(BeckmanBeam beckmanBeam) {
        if (mapper.selectByEntrustmentId(beckmanBeam.getEntrustmentId()) != null) {
            mapper.update(beckmanBeam);
        } else {
            mapper.insert(beckmanBeam);
        }
    }
}
