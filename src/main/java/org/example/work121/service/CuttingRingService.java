package org.example.work121.service;

import org.example.work121.entity.CuttingRing;

public interface CuttingRingService {
    CuttingRing getByEntrustmentId(String entrustmentId);
    void save(CuttingRing cuttingRing);
}
