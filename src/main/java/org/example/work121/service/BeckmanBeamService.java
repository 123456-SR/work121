package org.example.work121.service;

import org.example.work121.entity.BeckmanBeam;

public interface BeckmanBeamService {
    BeckmanBeam getByEntrustmentId(String entrustmentId);
    void save(BeckmanBeam beckmanBeam);
}
