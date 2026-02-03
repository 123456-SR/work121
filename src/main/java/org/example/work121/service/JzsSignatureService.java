package org.example.work121.service;

import org.example.work121.entity.JzsSignature;

/**
 * 签字存放表服务接口
 */
public interface JzsSignatureService {
    /**
     * 保存签名
     * @param signature 签名实体
     * @return 是否操作成功
     */
    boolean saveSignature(JzsSignature signature);
    
    /**
     * 根据用户账号查询签名
     * @param userAccount 用户账号
     * @return 签名实体
     */
    JzsSignature getSignatureByUserAccount(String userAccount);
    
    /**
     * 删除签名
     * @param signatureId 签名ID
     * @return 是否删除成功
     */
    boolean deleteSignature(String signatureId);
}