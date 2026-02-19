package org.example.work121.service;

import org.example.work121.entity.DensityTest;
import org.example.work121.entity.DensityTestReport;

public interface DensityTestService {
    java.util.List<DensityTest> getByEntrustmentId(String entrustmentId);
    void save(DensityTest densityTest);
    void deleteById(String id);

    DensityTestReport getReportByEntrustmentId(String entrustmentId);
    void saveReport(DensityTestReport report);

    /**
     * Generate Report and Result from Record
     * @param entrustmentId The entrustment ID
     */
    void generateReportAndResult(String entrustmentId);
}
