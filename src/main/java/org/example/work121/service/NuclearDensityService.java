package org.example.work121.service;

import org.example.work121.entity.NuclearDensity;

public interface NuclearDensityService {
    NuclearDensity getByEntrustmentId(String entrustmentId);
    void save(NuclearDensity nuclearDensity);
}
