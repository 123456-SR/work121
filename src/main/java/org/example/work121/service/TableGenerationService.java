package org.example.work121.service;

import org.example.work121.entity.JcCoreWtInfo;

import java.util.Map;

public interface TableGenerationService {
    
    void generateReportAndResult(String tableType, String entrustmentId);
    
    void fillTableFromEntrustment(String tableType, String entrustmentId, Object targetEntity);
    
    Map<String, Object> getEntrustmentData(String entrustmentId);
}
