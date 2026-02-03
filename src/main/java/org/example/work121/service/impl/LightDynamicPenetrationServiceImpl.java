package org.example.work121.service.impl;

import org.example.work121.entity.LightDynamicPenetration;
import org.example.work121.mapper.LightDynamicPenetrationMapper;
import org.example.work121.service.LightDynamicPenetrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LightDynamicPenetrationServiceImpl implements LightDynamicPenetrationService {

    @Autowired
    private LightDynamicPenetrationMapper mapper;

    @Override
    public LightDynamicPenetration getById(String id) {
        return mapper.selectById(id);
    }

    @Override
    @Transactional
    public void save(LightDynamicPenetration entity) {
        if (mapper.countById(entity.getId()) > 0) {
            mapper.update(entity);
        } else {
            mapper.insert(entity);
        }
    }
}
