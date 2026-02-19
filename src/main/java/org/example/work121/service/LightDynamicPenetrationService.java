package org.example.work121.service;

import org.example.work121.entity.LightDynamicPenetration;
import org.example.work121.entity.LightDynamicPenetrationReport;
import org.example.work121.entity.LightDynamicPenetrationResult;
import java.util.List;

public interface LightDynamicPenetrationService {
    LightDynamicPenetration getById(String id);
    List<LightDynamicPenetration> getByEntrustmentId(String entrustmentId);
    void save(LightDynamicPenetration entity);
    void deleteById(String id);

    // Report methods
    LightDynamicPenetrationReport getReportByEntrustmentId(String entrustmentId);
    void saveReport(LightDynamicPenetrationReport report);

    // Result methods
    LightDynamicPenetrationResult getResultByEntrustmentId(String entrustmentId);
    void saveResult(LightDynamicPenetrationResult result);

    /**
     * Generate Report and Result from Record
     * @param entrustmentId The entrustment ID
     */
    void generateReportAndResult(String entrustmentId);
}
