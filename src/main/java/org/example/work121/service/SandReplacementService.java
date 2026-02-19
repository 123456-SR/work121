package org.example.work121.service;

import java.util.List;
import org.example.work121.entity.SandReplacement;
import org.example.work121.entity.SandReplacementReport;

public interface SandReplacementService {
    List<SandReplacement> getByEntrustmentId(String entrustmentId);
    void deleteById(String id);
    void save(SandReplacement sandReplacement);

    SandReplacementReport getReportByEntrustmentId(String entrustmentId);
    void saveReport(SandReplacementReport report);

    /**
     * Generate Report and Result from Record
     * @param entrustmentId The entrustment ID
     */
    void generateReportAndResult(String entrustmentId);
}
