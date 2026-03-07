package org.example.work121.service;

import org.example.work121.entity.SampleCirculation;

import java.util.List;

public interface SampleCirculationService {
    SampleCirculation save(SampleCirculation sampleCirculation);
    SampleCirculation getById(String id);
    SampleCirculation getBySampleNumber(String sampleNumber);
    List<SampleCirculation> getAll();
    List<SampleCirculation> getByKeyword(String keyword);
    void deleteById(String id);
}