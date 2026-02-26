package org.example.work121.service.impl;

import org.example.work121.entity.JcCoreWtInfo;
import org.example.work121.mapper.JcCoreWtInfoMapper;
import org.example.work121.service.JcCoreWtInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
public class JcCoreWtInfoServiceImpl implements JcCoreWtInfoService {

    private static final Logger logger = LoggerFactory.getLogger(JcCoreWtInfoServiceImpl.class);

    @Autowired
    private JcCoreWtInfoMapper jcCoreWtInfoMapper;

    @Override
    public JcCoreWtInfo getByWtNum(String wtNum) {
        logger.info("正在查询委托信息，WT_NUM: {}", wtNum);
        java.util.List<JcCoreWtInfo> list = jcCoreWtInfoMapper.selectByWtNum(wtNum);
        JcCoreWtInfo result = (list != null && !list.isEmpty()) ? list.get(0) : null;
        if (result != null) {
            logger.info("查询到委托信息，WT_ID: {}", result.getId());
        } else {
            logger.info("未找到委托信息，WT_NUM: {}", wtNum);
        }
        return result;
    }

    @Override
    public com.github.pagehelper.PageInfo<JcCoreWtInfo> getByRegName(java.util.List<String> names, String wtNum, int pageNum, int pageSize) {
        logger.info("正在根据登记人查询委托列表，NAMES: {}, WT_NUM: {}, pageNum: {}, pageSize: {}", names, wtNum, pageNum, pageSize);
        com.github.pagehelper.PageHelper.startPage(pageNum, pageSize);
        java.util.List<JcCoreWtInfo> list = jcCoreWtInfoMapper.selectByRegName(names, wtNum);
        return new com.github.pagehelper.PageInfo<>(list);
    }

    @Override
    public JcCoreWtInfo getById(String id) {
        logger.info("正在根据ID查询委托信息，WT_ID: {}", id);
        return jcCoreWtInfoMapper.selectById(id);
    }

    @Override
    public com.github.pagehelper.PageInfo<JcCoreWtInfo> getByCategory(java.util.List<String> categories, java.util.List<String> names, String wtNum, int pageNum, int pageSize) {
        logger.info("正在根据检测类别和登记人查询委托列表，CATEGORIES: {}, NAMES: {}, WT_NUM: {}, pageNum: {}, pageSize: {}", categories, names, wtNum, pageNum, pageSize);
        com.github.pagehelper.PageHelper.startPage(pageNum, pageSize);
        java.util.List<JcCoreWtInfo> list = jcCoreWtInfoMapper.selectByCategory(categories, names, wtNum);
        return new com.github.pagehelper.PageInfo<>(list);
    }

    @Override
    public java.util.List<JcCoreWtInfo> selectByCategory(java.util.List<String> categories, java.util.List<String> names, String wtNum) {
        return jcCoreWtInfoMapper.selectByCategory(categories, names, wtNum);
    }

    @Override
    public com.github.pagehelper.PageInfo<JcCoreWtInfo> getRecordsByCategory(java.util.List<String> categories, java.util.List<String> names, String wtNum, int pageNum, int pageSize) {
        logger.info("正在根据检测类别和记录表角色查询检测记录列表，CATEGORIES: {}, NAMES: {}, WT_NUM: {}, pageNum: {}, pageSize: {}", categories, names, wtNum, pageNum, pageSize);
        com.github.pagehelper.PageHelper.startPage(pageNum, pageSize);
        java.util.List<JcCoreWtInfo> list = jcCoreWtInfoMapper.selectRecordsByCategory(categories, names, wtNum);
        if (list != null && !list.isEmpty()) {
            JcCoreWtInfo first = list.get(0);
            logger.info("DEBUG getRecordsByCategory -> first record: wtNum={}, id={}, status={}, sampleStatus={}, wtStatus={}",
                    first.getWtNum(),
                    first.getId(),
                    first.getStatus(),
                    first.getSampleStatus(),
                    first.getWtStatus());
        } else {
            logger.info("DEBUG getRecordsByCategory -> no records returned for wtNum={}", wtNum);
        }
        return new com.github.pagehelper.PageInfo<>(list);
    }

    @Override
    public java.util.List<JcCoreWtInfo> selectRecordsByCategory(java.util.List<String> categories, java.util.List<String> names, String wtNum) {
        return jcCoreWtInfoMapper.selectRecordsByCategory(categories, names, wtNum);
    }

    @Override
    public java.util.List<JcCoreWtInfo> selectByRegName(java.util.List<String> names, String wtNum) {
        return jcCoreWtInfoMapper.selectByRegName(names, wtNum);
    }

    @Override
    public java.util.List<JcCoreWtInfo> debugSelectAll() {
        return jcCoreWtInfoMapper.debugSelectAll();
    }

    @Override
    @Transactional
    public boolean save(JcCoreWtInfo info) {
        logger.info("正在保存委托信息，ID: {}", info.getId());
        
        // Ensure customerId is not null (required by JC_CORE_WT_INFO table)
        if (info.getCustomerId() == null || info.getCustomerId().isEmpty()) {
            info.setCustomerId("0"); // Default value to satisfy NOT NULL constraint
            logger.info("Setting default customerId to 0");
        }

        // Ensure dataCid is not null (required by JC_CORE_WT_INFO table)
        if (info.getDataCid() == null || info.getDataCid().isEmpty()) {
            info.setDataCid("0"); // Default value to satisfy NOT NULL constraint
            logger.info("Setting default dataCid to 0");
        }

        try {
            // 如果没有ID，但有统一编号，优先根据 WT_NUM 查找已有记录并复用其ID，避免生成重复记录
            if ((info.getId() == null || info.getId().trim().isEmpty())
                    && info.getWtNum() != null && !info.getWtNum().trim().isEmpty()) {
                JcCoreWtInfo existing = getByWtNum(info.getWtNum());
                if (existing != null && existing.getId() != null && !existing.getId().trim().isEmpty()) {
                    info.setId(existing.getId());
                    logger.info("根据 WT_NUM {} 找到已有记录，复用 ID: {}", info.getWtNum(), info.getId());
                }
            }

            if (info.getId() == null || info.getId().trim().isEmpty()) {
                // 如果没有ID，生成新的UUID
                String newId = java.util.UUID.randomUUID().toString().replace("-", "");
                info.setId(newId);
                logger.info("生成新ID: {}", newId);
                
                // 新记录：同时插入 JC_CORE_WT_INFO 和 JC_CORE_WT_INFO_EXT
                jcCoreWtInfoMapper.insert(info);
                jcCoreWtInfoMapper.insertExt(info);
                return true;
            }

            // 更新 JC_CORE_WT_INFO
            int rows = jcCoreWtInfoMapper.update(info);
            if (rows == 0) {
                logger.info("JC_CORE_WT_INFO记录不存在，执行插入操作");
                jcCoreWtInfoMapper.insert(info);
            }

            // 更新 JC_CORE_WT_INFO_EXT
            int rowsExt = jcCoreWtInfoMapper.updateExt(info);
            if (rowsExt == 0) {
                logger.info("JC_CORE_WT_INFO_EXT记录不存在，执行插入操作");
                jcCoreWtInfoMapper.insertExt(info);
            }
            
            return true;
        } catch (Exception e) {
            logger.error("保存委托信息失败", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public boolean delete(String id) {
        logger.info("正在删除委托信息，ID: {}", id);
        try {
            // Delete from extension table
            int extRows = jcCoreWtInfoMapper.deleteExtById(id);
            logger.info("Deleted {} rows from JC_CORE_WT_INFO_EXT", extRows);
            
            // Delete from core table
            int coreRows = jcCoreWtInfoMapper.deleteCoreById(id);
            logger.info("Deleted {} rows from JC_CORE_WT_INFO", coreRows);
            
            return true;
        } catch (Exception e) {
            logger.error("删除委托信息失败，ID: {}", id, e);
            throw e;
        }
    }
}
