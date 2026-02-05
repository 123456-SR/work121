package org.example.work121.service.impl;

import org.example.work121.entity.SandReplacement;
import org.example.work121.mapper.SandReplacementMapper;
import org.example.work121.service.SandReplacementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SandReplacementServiceImpl implements SandReplacementService {

    @Autowired
    private SandReplacementMapper mapper;

    @Override
    public SandReplacement getByEntrustmentId(String entrustmentId) {
        return mapper.selectByEntrustmentId(entrustmentId);
    }

    @Override
    @Transactional
    public void save(SandReplacement sandReplacement) {
        if (mapper.selectByEntrustmentId(sandReplacement.getEntrustmentId()) != null) {
            mapper.update(sandReplacement);
        } else {
            mapper.insert(sandReplacement);
        }
    }
}
