package org.example.work121.service;

import org.example.work121.entity.DensityTest;

public interface DensityTestService {
    DensityTest getByEntrustmentId(String entrustmentId);
    void save(DensityTest densityTest);
}
