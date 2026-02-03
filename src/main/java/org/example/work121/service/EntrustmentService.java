package org.example.work121.service;

import org.example.work121.entity.Entrustment;

/**
 * 检测委托单服务接口
 */
public interface EntrustmentService {

    /**
     * 保存检测委托单
     * @param entrustment 检测委托单实体
     * @return 是否保存成功
     */
    boolean saveEntrustment(Entrustment entrustment);

    /**
     * 根据统一编号查询检测委托单
     * @param unifiedNumber 统一编号
     * @return 检测委托单实体
     */
    Entrustment getEntrustmentByUnifiedNumber(String unifiedNumber);
}
