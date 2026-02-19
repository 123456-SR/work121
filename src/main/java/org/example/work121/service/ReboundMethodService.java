package org.example.work121.service;

import java.util.List;
import org.example.work121.entity.ReboundMethod;
import org.example.work121.entity.ReboundMethodReport;

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
    List<ReboundMethod> getReboundMethodByUnifiedNumber(String unifiedNumber);

    void deleteById(String id);

    ReboundMethodReport getReportByEntrustmentId(String entrustmentId);
    boolean saveReport(ReboundMethodReport report);

    /**
     * Generate Report and Result from Record
     * @param entrustmentId The entrustment ID
     */
    void generateReportAndResult(String entrustmentId);
}
