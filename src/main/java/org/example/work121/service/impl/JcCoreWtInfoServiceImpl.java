package org.example.work121.service.impl;

import org.example.work121.entity.JcCoreWtInfo;
import org.example.work121.mapper.JcCoreWtInfoMapper;
import org.example.work121.service.JcCoreWtInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JcCoreWtInfoServiceImpl implements JcCoreWtInfoService {

    private static final Logger logger = LoggerFactory.getLogger(JcCoreWtInfoServiceImpl.class);

    @Autowired
    private JcCoreWtInfoMapper jcCoreWtInfoMapper;

    @Override
    public JcCoreWtInfo getByWtNum(String wtNum) {
        logger.info("正在查询委托信息，WT_NUM: {}", wtNum);
        JcCoreWtInfo result = jcCoreWtInfoMapper.selectByWtNum(wtNum);
        if (result != null) {
            logger.info("查询到委托信息，WT_ID: {}", result.getWtId());
        } else {
            logger.info("未找到委托信息，WT_NUM: {}", wtNum);
        }
        return result;
    }

}
