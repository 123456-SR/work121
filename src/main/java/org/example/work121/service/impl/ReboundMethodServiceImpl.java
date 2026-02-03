package org.example.work121.service.impl;

import org.example.work121.entity.ReboundMethod;
import org.example.work121.service.ReboundMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * 回弹法检测服务实现类
 */
@Service
public class ReboundMethodServiceImpl implements ReboundMethodService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public boolean saveReboundMethod(ReboundMethod reboundMethod) {
        try {
            // 检查是否已存在该统一编号的记录
            ReboundMethod existingRecord = getReboundMethodByUnifiedNumber(reboundMethod.getId());

            if (existingRecord != null) {
                // 更新现有记录
                String sql = "UPDATE T_REBOUND_METHOD SET " +
                        "STRUCTURE_PART = ?, " +
                        "CONCRETE_GRADE = ?, " +
                        "MOLDING_DATE = ?, " +
                        "AGE = ?, " +
                        "INSTRUMENT_MODEL = ?, " +
                        "CALIBRATION_NO = ?, " +
                        "ZONE_COUNT = ?, " +
                        "POURING_DIRECTION = ?, " +
                        "TEST_ANGLE = ?, " +
                        "PUMPING_METHOD = ?, " +
                        "PUMPING = ?, " +
                        "DESIGN_STRENGTH = ?, " +
                        "CARBONATION_DEPTH = ?, " +
                        "TEST_RESULT = ?, " +
                        "DATA_JSON = ? " +
                        "WHERE ID = ?";

                int result = jdbcTemplate.update(sql,
                        reboundMethod.getStructurePart(),
                        reboundMethod.getConcreteGrade(),
                        reboundMethod.getMoldingDate(),
                        reboundMethod.getAge(),
                        reboundMethod.getInstrumentModel(),
                        reboundMethod.getCalibrationNo(),
                        reboundMethod.getZoneCount(),
                        reboundMethod.getPouringDirection(),
                        reboundMethod.getTestAngle(),
                        reboundMethod.getPumpingMethod(),
                        reboundMethod.getPumping(),
                        reboundMethod.getDesignStrength(),
                        reboundMethod.getCarbonationDepth(),
                        reboundMethod.getTestResult(),
                        reboundMethod.getDataJson(),
                        reboundMethod.getId()
                );

                return result > 0;
            } else {
                // 插入新记录
                String sql = "INSERT INTO T_REBOUND_METHOD " +
                        "(ID, ENTRUSTMENT_ID, STRUCTURE_PART, CONCRETE_GRADE, MOLDING_DATE, " +
                        "AGE, INSTRUMENT_MODEL, CALIBRATION_NO, ZONE_COUNT, POURING_DIRECTION, " +
                        "TEST_ANGLE, PUMPING_METHOD, PUMPING, DESIGN_STRENGTH, CARBONATION_DEPTH, " +
                        "TEST_RESULT, DATA_JSON) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                String id = reboundMethod.getId() != null ? 
                        reboundMethod.getId() : UUID.randomUUID().toString();
                // 为ENTRUSTMENT_ID提供默认值，避免插入NULL
                String entrustmentId = id; // 使用当前记录的ID作为默认值

                int result = jdbcTemplate.update(sql,
                        id,
                        entrustmentId,
                        reboundMethod.getStructurePart(),
                        reboundMethod.getConcreteGrade(),
                        reboundMethod.getMoldingDate(),
                        reboundMethod.getAge(),
                        reboundMethod.getInstrumentModel(),
                        reboundMethod.getCalibrationNo(),
                        reboundMethod.getZoneCount(),
                        reboundMethod.getPouringDirection(),
                        reboundMethod.getTestAngle(),
                        reboundMethod.getPumpingMethod(),
                        reboundMethod.getPumping(),
                        reboundMethod.getDesignStrength(),
                        reboundMethod.getCarbonationDepth(),
                        reboundMethod.getTestResult(),
                        reboundMethod.getDataJson()
                );

                return result > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ReboundMethod getReboundMethodByUnifiedNumber(String unifiedNumber) {
        try {
            String sql = "SELECT * FROM T_REBOUND_METHOD WHERE ID = ?";
            List<ReboundMethod> records = jdbcTemplate.query(sql, new ReboundMethodRowMapper(), unifiedNumber);
            return records.isEmpty() ? null : records.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 行映射器
    private class ReboundMethodRowMapper implements RowMapper<ReboundMethod> {
        @Override
        public ReboundMethod mapRow(ResultSet rs, int rowNum) throws SQLException {
            ReboundMethod record = new ReboundMethod();
            record.setId(rs.getString("ID"));
            record.setStructurePart(rs.getString("STRUCTURE_PART"));
            record.setConcreteGrade(rs.getString("CONCRETE_GRADE"));
            record.setMoldingDate(rs.getDate("MOLDING_DATE"));
            record.setAge(rs.getString("AGE"));
            record.setInstrumentModel(rs.getString("INSTRUMENT_MODEL"));
            record.setCalibrationNo(rs.getString("CALIBRATION_NO"));
            record.setZoneCount(rs.getString("ZONE_COUNT"));
            record.setPouringDirection(rs.getString("POURING_DIRECTION"));
            record.setTestAngle(rs.getString("TEST_ANGLE"));
            record.setPumpingMethod(rs.getString("PUMPING_METHOD"));
            record.setPumping(rs.getString("PUMPING"));
            record.setDesignStrength(rs.getString("DESIGN_STRENGTH"));
            record.setCarbonationDepth(rs.getString("CARBONATION_DEPTH"));
            record.setTestResult(rs.getString("TEST_RESULT"));
            record.setDataJson(rs.getString("DATA_JSON"));
            return record;
        }
    }
}
