package org.example.work121.service;

import java.util.List;
import org.example.work121.entity.CuttingRing;

public interface CuttingRingService {
    List<CuttingRing> getByEntrustmentId(String entrustmentId);
    void save(CuttingRing cuttingRing);
    void deleteById(String id);

    // Report methods
    org.example.work121.entity.CuttingRingReport getReportByEntrustmentId(String entrustmentId);
    void saveReport(org.example.work121.entity.CuttingRingReport report);

    /**
     * Generate Report and Result from Record
     * @param entrustmentId The entrustment ID
     */
    void generateReportAndResult(String entrustmentId);
}
