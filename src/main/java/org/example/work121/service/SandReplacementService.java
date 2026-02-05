package org.example.work121.service;

import org.example.work121.entity.SandReplacement;

public interface SandReplacementService {
    SandReplacement getByEntrustmentId(String entrustmentId);
    void save(SandReplacement sandReplacement);
}
