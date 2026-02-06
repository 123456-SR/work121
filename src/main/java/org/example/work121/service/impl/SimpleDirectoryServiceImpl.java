package org.example.work121.service.impl;

import org.example.work121.entity.*;
import org.example.work121.mapper.*;
import org.example.work121.service.SimpleDirectoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * 极简目录表服务实现类
 */
@Service
public class SimpleDirectoryServiceImpl implements SimpleDirectoryService {

    @Autowired
    private SimpleDirectoryMapper simpleDirectoryMapper;

    @Autowired
    private EntrustmentMapper entrustmentMapper;

    @Autowired
    private DensityTestRecordMapper densityTestRecordMapper;

    @Autowired
    private ReboundMethodMapper reboundMethodMapper;

    @Autowired
    private SandReplacementMapper sandReplacementMapper;

    @Autowired
    private LightDynamicPenetrationMapper lightDynamicPenetrationMapper;

    @Autowired
    private BeckmanBeamMapper beckmanBeamMapper;

    @Autowired
    private CuttingRingMapper cuttingRingMapper;

    @Autowired
    private NuclearDensityMapper nuclearDensityMapper;

    @Autowired
    private WaterReplacementMapper waterReplacementMapper;

    @Override
    public boolean saveDirectory(SimpleDirectory directory) {
        try {
            boolean isNew = false;
            // 检查是否已存在该目录
            SimpleDirectory existingRecord = null;
            if (directory.getDirId() != null) {
                existingRecord = simpleDirectoryMapper.selectByDirId(directory.getDirId());
            }

            int result;
            if (existingRecord != null) {
                // 更新现有记录
                directory.setId(existingRecord.getId()); // Ensure ID is set for update
                // 保留创建信息
                directory.setCreateBy(existingRecord.getCreateBy());
                directory.setCreateTime(existingRecord.getCreateTime());
                
                // 设置更新信息
                directory.setUpdateBy(directory.getUpdateBy() != null ? directory.getUpdateBy() : "admin"); // Or get from context
                directory.setUpdateTime(new java.util.Date());
                
                result = simpleDirectoryMapper.update(directory);
            } else {
                isNew = true;
                // 插入新记录
                if (directory.getId() == null || directory.getId().isEmpty()) {
                    directory.setId(UUID.randomUUID().toString());
                }
                if (directory.getDirId() == null || directory.getDirId().isEmpty()) {
                    directory.setDirId(UUID.randomUUID().toString());
                }
                
                // 设置创建信息
                if (directory.getCreateBy() == null) {
                    directory.setCreateBy("admin");
                }
                if (directory.getCreateTime() == null) {
                    directory.setCreateTime(new java.util.Date());
                }
                if (directory.getStatus() == null) {
                    directory.setStatus(1);
                }

                result = simpleDirectoryMapper.insert(directory);
            }

            if (result > 0) {
                // 同步委托单数据到关联表
                syncEntrustmentData(directory);
                return true;
            }
            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void syncEntrustmentData(SimpleDirectory directory) {
        try {
            // 1. 查找委托单 (Source)
            Entrustment entrustment = null;
            if (isEntrustment(directory.getTable1Type())) entrustment = entrustmentMapper.selectById(directory.getTable1Id());
            else if (isEntrustment(directory.getTable2Type())) entrustment = entrustmentMapper.selectById(directory.getTable2Id());
            else if (isEntrustment(directory.getTable3Type())) entrustment = entrustmentMapper.selectById(directory.getTable3Id());
            else if (isEntrustment(directory.getTable4Type())) entrustment = entrustmentMapper.selectById(directory.getTable4Id());
            else if (isEntrustment(directory.getTable5Type())) entrustment = entrustmentMapper.selectById(directory.getTable5Id());
            else if (isEntrustment(directory.getTable6Type())) entrustment = entrustmentMapper.selectById(directory.getTable6Id());
            else if (isEntrustment(directory.getTable7Type())) entrustment = entrustmentMapper.selectById(directory.getTable7Id());
            else if (isEntrustment(directory.getTable8Type())) entrustment = entrustmentMapper.selectById(directory.getTable8Id());
            else if (isEntrustment(directory.getTable9Type())) entrustment = entrustmentMapper.selectById(directory.getTable9Id());
            else if (isEntrustment(directory.getTable10Type())) entrustment = entrustmentMapper.selectById(directory.getTable10Id());

            if (entrustment == null) {
                return;
            }

            // 2. 同步到其他表 (Target)
            syncToTable(directory.getTable1Type(), directory.getTable1Id(), entrustment);
            syncToTable(directory.getTable2Type(), directory.getTable2Id(), entrustment);
            syncToTable(directory.getTable3Type(), directory.getTable3Id(), entrustment);
            syncToTable(directory.getTable4Type(), directory.getTable4Id(), entrustment);
            syncToTable(directory.getTable5Type(), directory.getTable5Id(), entrustment);
            syncToTable(directory.getTable6Type(), directory.getTable6Id(), entrustment);
            syncToTable(directory.getTable7Type(), directory.getTable7Id(), entrustment);
            syncToTable(directory.getTable8Type(), directory.getTable8Id(), entrustment);
            syncToTable(directory.getTable9Type(), directory.getTable9Id(), entrustment);
            syncToTable(directory.getTable10Type(), directory.getTable10Id(), entrustment);

        } catch (Exception e) {
            e.printStackTrace();
            // Log error but don't fail the save
        }
    }

    private boolean isEntrustment(String type) {
        return type != null && (
                "ENTRUSTMENT".equalsIgnoreCase(type) ||
                "T_ENTRUSTMENT".equalsIgnoreCase(type) ||
                "JZS_ENTRUSTMENT".equalsIgnoreCase(type) ||
                "委托单".equals(type)
        );
    }

    private void syncToTable(String type, String id, Entrustment source) {
        if (type == null || id == null || isEntrustment(type)) {
            return;
        }

        if (isTypeMatch(type, "DENSITY_TEST", "T_DENSITY_TEST", "密度试验")) {
            DensityTestRecord target = densityTestRecordMapper.selectById(id);
            if (target != null) {
                target.setEntrustmentId(source.getUnifiedNumber());
                copyFields(source, target);
                densityTestRecordMapper.updateById(target);
            }
        } else if (isTypeMatch(type, "REBOUND_METHOD", "T_REBOUND_METHOD", "回弹法")) {
            ReboundMethod target = reboundMethodMapper.selectById(id);
            if (target != null) {
                target.setEntrustmentId(source.getUnifiedNumber());
                copyFields(source, target);
                reboundMethodMapper.updateById(target);
            }
        } else if (isTypeMatch(type, "SAND_REPLACEMENT", "T_SAND_REPLACEMENT", "灌砂法")) {
            SandReplacement target = sandReplacementMapper.selectById(id);
            if (target != null) {
                target.setEntrustmentId(source.getUnifiedNumber());
                copyFields(source, target);
                sandReplacementMapper.update(target);
            }
        } else if (isTypeMatch(type, "LIGHT_DYNAMIC_PENETRATION", "JZS_LIGHT_DYNAMIC_PENETRATION", "轻型动力触探")) {
            LightDynamicPenetration target = lightDynamicPenetrationMapper.selectById(id);
            if (target != null) {
                copyFields(source, target);
                lightDynamicPenetrationMapper.update(target);
            }
        } else if (isTypeMatch(type, "BECKMAN_BEAM", "T_BECKMAN_BEAM", "贝克曼梁")) {
            BeckmanBeam target = beckmanBeamMapper.selectById(id);
            if (target != null) {
                target.setEntrustmentId(source.getUnifiedNumber());
                beckmanBeamMapper.updateById(target);
            }
        } else if (isTypeMatch(type, "CUTTING_RING", "T_CUTTING_RING", "环刀法")) {
            CuttingRing target = cuttingRingMapper.selectById(id);
            if (target != null) {
                target.setEntrustmentId(source.getUnifiedNumber());
                cuttingRingMapper.updateById(target);
            }
        } else if (isTypeMatch(type, "NUCLEAR_DENSITY", "T_NUCLEAR_DENSITY", "核子密度仪")) {
            NuclearDensity target = nuclearDensityMapper.selectById(id);
            if (target != null) {
                target.setEntrustmentId(source.getUnifiedNumber());
                nuclearDensityMapper.updateById(target);
            }
        } else if (isTypeMatch(type, "WATER_REPLACEMENT", "T_WATER_REPLACEMENT", "灌水法")) {
            WaterReplacement target = waterReplacementMapper.selectById(id);
            if (target != null) {
                target.setEntrustmentId(source.getUnifiedNumber());
                waterReplacementMapper.updateById(target);
            }
        }
    }

    private boolean isTypeMatch(String type, String... candidates) {
        for (String candidate : candidates) {
            if (candidate.equalsIgnoreCase(type)) {
                return true;
            }
        }
        return false;
    }

    private void copyFields(Entrustment source, BusinessEntity target) {
        if (source.getProjectName() != null) target.setProjectName(source.getProjectName());
        if (source.getClientUnit() != null) target.setClientUnit(source.getClientUnit());
        if (source.getWtNum() != null) target.setWtNum(source.getWtNum());
        if (source.getJcDate() != null) target.setCommissionDate(source.getJcDate());
        if (source.getConstructionUnit() != null) target.setConstructionUnit(source.getConstructionUnit());
        if (source.getSupervisionUnit() != null) target.setSupervisionUnit(source.getSupervisionUnit());
        if (source.getDesignUnit() != null) target.setDesignUnit(source.getDesignUnit());
        if (source.getWitnessUnit() != null) target.setWitnessUnit(source.getWitnessUnit());
        if (source.getWitness() != null) target.setWitness(source.getWitness());
        // Add more fields as needed
    }

    @Override
    public SimpleDirectory getDirectoryById(String id) {
        try {
            return simpleDirectoryMapper.selectById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public SimpleDirectory getDirectoryByDirId(String dirId) {
        try {
            return simpleDirectoryMapper.selectByDirId(dirId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<SimpleDirectory> getAllDirectories() {
        try {
            return simpleDirectoryMapper.selectAll();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean deleteDirectory(String id) {
        try {
            int result = simpleDirectoryMapper.deleteById(id);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
