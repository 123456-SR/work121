package org.example.work121.service.impl;

import org.example.work121.entity.JzsSignature;
import org.example.work121.mapper.JzsSignatureMapper;
import org.example.work121.service.JzsSignatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * 签字存放表服务实现类
 */
@Service
public class JzsSignatureServiceImpl implements JzsSignatureService {
    
    @Autowired
    private JzsSignatureMapper jzsSignatureMapper;

    @Override
    public boolean saveSignature(JzsSignature signature) {
        try {
            // 检查是否已存在该用户的签名
            JzsSignature existingSignature = getSignatureByUserAccount(
                    signature.getUserAccount());

            if (existingSignature != null) {
                // 更新现有签名
                signature.setSignatureId(existingSignature.getSignatureId());
                signature.setUpdateTime(new java.util.Date());
                // 保留原有类型和其他信息，如果新对象没有设置
                if (signature.getSignatureType() == null) {
                    signature.setSignatureType(existingSignature.getSignatureType());
                }
                
                int result = jzsSignatureMapper.update(signature);
                return result > 0;
            } else {
                // 插入新签名
                if (signature.getSignatureId() == null) {
                    signature.setSignatureId(UUID.randomUUID().toString());
                }
                if (signature.getSignatureType() == null) {
                    signature.setSignatureType("DEFAULT");
                }
                if (signature.getCreateTime() == null) {
                    signature.setCreateTime(new java.util.Date());
                }

                int result = jzsSignatureMapper.insert(signature);
                return result > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public JzsSignature getSignatureByUserAccount(String userAccount) {
        try {
            List<JzsSignature> signatures = jzsSignatureMapper.selectListByUser(userAccount);
            return signatures.isEmpty() ? null : signatures.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean deleteSignature(String signatureId) {
        try {
            int result = jzsSignatureMapper.deleteById(signatureId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}