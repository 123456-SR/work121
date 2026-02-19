package org.example.work121.service;

import java.util.List;
import org.example.work121.entity.NuclearDensity;

public interface NuclearDensityService {
    List<NuclearDensity> getByEntrustmentId(String entrustmentId);
    void deleteById(String id);
    void save(NuclearDensity nuclearDensity);

    // Report methods
    org.example.work121.entity.NuclearDensityReport getReportByEntrustmentId(String entrustmentId);
    void saveReport(org.example.work121.entity.NuclearDensityReport report);

    /**
     * Generate Report and Result from Record
     * @param entrustmentId The entrustment ID
     */
    void generateReportAndResult(String entrustmentId);
}
