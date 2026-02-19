package org.example.work121.service;

import java.util.List;
import org.example.work121.entity.WaterReplacement;
import org.example.work121.entity.WaterReplacementReport;

public interface WaterReplacementService {
    List<WaterReplacement> getByEntrustmentId(String entrustmentId);
    void save(WaterReplacement waterReplacement);
    void deleteById(String id);

    WaterReplacementReport getReportByEntrustmentId(String entrustmentId);
    void saveReport(WaterReplacementReport report);

    // Result methods
    org.example.work121.entity.WaterReplacementResult getResultByEntrustmentId(String entrustmentId);
    void saveResult(org.example.work121.entity.WaterReplacementResult result);

    /**
     * Generate Report and Result from Record
     * @param entrustmentId The entrustment ID
     */
    void generateReportAndResult(String entrustmentId);
}
