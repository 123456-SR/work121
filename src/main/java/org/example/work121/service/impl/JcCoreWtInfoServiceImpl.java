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
            logger.info("查询到委托信息，WT_ID: {}", result.getId());
        } else {
            logger.info("未找到委托信息，WT_NUM: {}", wtNum);
        }
        return result;
    }

    @Override
    public com.github.pagehelper.PageInfo<JcCoreWtInfo> getByRegName(String regName, int pageNum, int pageSize) {
        logger.info("正在根据登记人查询委托列表，WT_REG_NAME: {}, pageNum: {}, pageSize: {}", regName, pageNum, pageSize);
        com.github.pagehelper.PageHelper.startPage(pageNum, pageSize);
        java.util.List<JcCoreWtInfo> list = jcCoreWtInfoMapper.selectByRegName(regName);
        return new com.github.pagehelper.PageInfo<>(list);
    }

    @Override
    public JcCoreWtInfo getById(String id) {
        logger.info("正在根据ID查询委托信息，WT_ID: {}", id);
        return jcCoreWtInfoMapper.selectById(id);
    }

}
