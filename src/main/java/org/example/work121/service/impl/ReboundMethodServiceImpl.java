package org.example.work121.service.impl;

import org.example.work121.entity.ReboundMethod;
import org.example.work121.mapper.ReboundMethodMapper;
import org.example.work121.service.ReboundMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 回弹法检测服务实现类
 */
@Service
public class ReboundMethodServiceImpl implements ReboundMethodService {

    @Autowired
    private ReboundMethodMapper mapper;

    @Override
    @Transactional
    public boolean saveReboundMethod(ReboundMethod reboundMethod) {
        try {
            if (reboundMethod.getEntrustmentId() == null) {
                // 如果没有关联委托单ID，无法保存
                return false;
            }

            ReboundMethod existing = mapper.selectByEntrustmentId(reboundMethod.getEntrustmentId());

            if (existing != null) {
                // 更新现有记录
                mapper.update(reboundMethod);
            } else {
                // 插入新记录
                if (reboundMethod.getId() == null) {
                    reboundMethod.setId(UUID.randomUUID().toString());
                }
                mapper.insert(reboundMethod);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ReboundMethod getReboundMethodByUnifiedNumber(String unifiedNumber) {
        return mapper.selectByEntrustmentId(unifiedNumber);
    }
}
