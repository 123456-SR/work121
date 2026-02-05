package org.example.work121.service.impl;

import org.example.work121.entity.Entrustment;
import org.example.work121.mapper.EntrustmentMapper;
import org.example.work121.service.EntrustmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

/**
 * 检测委托单服务实现类
 */
@Service
public class EntrustmentServiceImpl implements EntrustmentService {

    @Autowired
    private EntrustmentMapper entrustmentMapper;

    @Override
    @Transactional
    public boolean saveEntrustment(Entrustment entrustment) {
        try {
            // 检查是否已存在该统一编号的记录
            String id = entrustment.getId();
            Entrustment existingRecord = null;
            
            if (id != null) {
                existingRecord = entrustmentMapper.selectById(id);
            }

            if (existingRecord != null) {
                // 更新现有记录
                int result = entrustmentMapper.update(entrustment);
                
                // 如果更新返回0，说明该记录只存在于旧表(JZS_ENTRUSTMENT)中，不存在于新表(T_ENTRUSTMENT)
                // 此时需要将其插入到新表中（相当于从旧表迁移并更新）
                if (result == 0) {
                    if (entrustment.getCreateTime() == null) {
                        entrustment.setCreateTime(existingRecord.getCreateTime() != null ? existingRecord.getCreateTime() : new Date());
                    }
                    if (entrustment.getCreateBy() == null) {
                        entrustment.setCreateBy(existingRecord.getCreateBy());
                    }
                    result = entrustmentMapper.insert(entrustment);
                }
                return result > 0;
            } else {
                // 插入新记录
                if (entrustment.getId() == null) {
                    entrustment.setId(UUID.randomUUID().toString());
                }
                
                if (entrustment.getCreateTime() == null) {
                    entrustment.setCreateTime(new Date());
                }

                int result = entrustmentMapper.insert(entrustment);
                return result > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Entrustment getEntrustmentByUnifiedNumber(String unifiedNumber) {
        try {
            return entrustmentMapper.selectById(unifiedNumber);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public java.util.List<Entrustment> getAllEntrustments() {
        try {
            return entrustmentMapper.selectAll();
        } catch (Exception e) {
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }
}
