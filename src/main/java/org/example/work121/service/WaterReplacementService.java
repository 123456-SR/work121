package org.example.work121.service;

import org.example.work121.entity.WaterReplacement;

public interface WaterReplacementService {
    WaterReplacement getByEntrustmentId(String entrustmentId);
    void save(WaterReplacement waterReplacement);
}
