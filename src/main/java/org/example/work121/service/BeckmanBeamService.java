package org.example.work121.service;

import java.util.List;
import org.example.work121.entity.BeckmanBeam;

public interface BeckmanBeamService {
    List<BeckmanBeam> getByEntrustmentId(String entrustmentId);
    void save(BeckmanBeam beckmanBeam);
    void deleteById(String id);

    // Report methods
    org.example.work121.entity.BeckmanBeamReport getReportByEntrustmentId(String entrustmentId);
    void saveReport(org.example.work121.entity.BeckmanBeamReport report);

    // Result methods
    org.example.work121.entity.BeckmanBeamResult getResultByEntrustmentId(String entrustmentId);
    void saveResult(org.example.work121.entity.BeckmanBeamResult result);

    /**
     * Generate Report and Result from Record
     * @param entrustmentId The entrustment ID
     */
    void generateReportAndResult(String entrustmentId);
}
