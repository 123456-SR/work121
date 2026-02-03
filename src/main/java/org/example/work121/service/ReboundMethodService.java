package org.example.work121.service;

import org.example.work121.entity.ReboundMethod;

/**
 * 回弹法检测服务接口
 */
public interface ReboundMethodService {

    /**
     * 保存回弹法检测记录
     * @param reboundMethod 回弹法检测实体
     * @return 是否保存成功
     */
    boolean saveReboundMethod(ReboundMethod reboundMethod);

    /**
     * 根据统一编号查询回弹法检测记录
     * @param unifiedNumber 统一编号
     * @return 回弹法检测实体
     */
    ReboundMethod getReboundMethodByUnifiedNumber(String unifiedNumber);
}
