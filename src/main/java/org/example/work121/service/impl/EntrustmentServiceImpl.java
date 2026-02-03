package org.example.work121.service.impl;

import org.example.work121.entity.Entrustment;
import org.example.work121.service.EntrustmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * 检测委托单服务实现类
 */
@Service
public class EntrustmentServiceImpl implements EntrustmentService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public boolean saveEntrustment(Entrustment entrustment) {
        try {
            // 检查是否已存在该统一编号的记录
            Entrustment existingRecord = getEntrustmentByUnifiedNumber(entrustment.getId());

            if (existingRecord != null) {
                // 更新现有记录
                String sql = "UPDATE T_ENTRUSTMENT SET " +
                        "WT_NUM = ?, " +
                        "SAMPLE_NAME = ?, " +
                        "TEST_CATEGORY = ?, " +
                        "CLIENT_UNIT = ?, " +
                        "CLIENT = ?, " +
                        "CLIENT_TEL = ?, " +
                        "CLIENT_UNIT_POSTALCODE = ?, " +
                        "CLIENT_UNIT_PERSON = ?, " +
                        "PROJECT_NAME = ?, " +
                        "CONSTRUCTION_PART = ?, " +
                        "PROJECT_AREA = ?, " +
                        "BUILDING_UNIT = ?, " +
                        "SURVEY_UNIT = ?, " +
                        "CONSTRUCTION_UNIT = ?, " +
                        "WITNESS_UNIT = ?, " +
                        "WITNESS = ?, " +
                        "SUPERVISION_UNIT = ?, " +
                        "DESIGN_UNIT = ?, " +
                        "COMMISSION_DATE = ?, " +
                        "REMARKS = ?, " +
                        "SAMPLE_STATUS = ?, " +
                        "TEST_ITEMS = ? " +
                        "WHERE ID = ?";

                int result = jdbcTemplate.update(sql,
                        entrustment.getWtNum(),
                        entrustment.getSampleName(),
                        entrustment.getTestCategory(),
                        entrustment.getClientUnit(),
                        entrustment.getClient(),
                        entrustment.getClientTel(),
                        entrustment.getClientUnitPostalcode(),
                        entrustment.getClientUnitPerson(),
                        entrustment.getProjectName(),
                        entrustment.getConstructionPart(),
                        entrustment.getProjectArea(),
                        entrustment.getBuildingUnit(),
                        entrustment.getSurveyUnit(),
                        entrustment.getConstructionUnit(),
                        entrustment.getWitnessUnit(),
                        entrustment.getWitness(),
                        entrustment.getSupervisionUnit(),
                        entrustment.getDesignUnit(),
                        entrustment.getCommissionDate(),
                        entrustment.getRemarks(),
                        entrustment.getSampleStatus(),
                        entrustment.getTestItems(),
                        entrustment.getId()
                );

                return result > 0;
            } else {
                // 插入新记录
                String sql = "INSERT INTO T_ENTRUSTMENT " +
                        "(ID, WT_NUM, SAMPLE_NAME, TEST_CATEGORY, CLIENT_UNIT, CLIENT, CLIENT_TEL, " +
                        "CLIENT_UNIT_POSTALCODE, CLIENT_UNIT_PERSON, PROJECT_NAME, CONSTRUCTION_PART, " +
                        "PROJECT_AREA, BUILDING_UNIT, SURVEY_UNIT, CONSTRUCTION_UNIT, WITNESS_UNIT, " +
                        "WITNESS, SUPERVISION_UNIT, DESIGN_UNIT, COMMISSION_DATE, REMARKS, " +
                        "SAMPLE_STATUS, TEST_ITEMS) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                String id = entrustment.getId() != null ? 
                        entrustment.getId() : UUID.randomUUID().toString();

                int result = jdbcTemplate.update(sql,
                        id,
                        entrustment.getWtNum(),
                        entrustment.getSampleName(),
                        entrustment.getTestCategory(),
                        entrustment.getClientUnit(),
                        entrustment.getClient(),
                        entrustment.getClientTel(),
                        entrustment.getClientUnitPostalcode(),
                        entrustment.getClientUnitPerson(),
                        entrustment.getProjectName(),
                        entrustment.getConstructionPart(),
                        entrustment.getProjectArea(),
                        entrustment.getBuildingUnit(),
                        entrustment.getSurveyUnit(),
                        entrustment.getConstructionUnit(),
                        entrustment.getWitnessUnit(),
                        entrustment.getWitness(),
                        entrustment.getSupervisionUnit(),
                        entrustment.getDesignUnit(),
                        entrustment.getCommissionDate(),
                        entrustment.getRemarks(),
                        entrustment.getSampleStatus(),
                        entrustment.getTestItems()
                );

                return result > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Entrustment getEntrustmentByUnifiedNumber(String unifiedNumber) {
        try {
            String sql = "SELECT * FROM T_ENTRUSTMENT WHERE ID = ?";
            List<Entrustment> records = jdbcTemplate.query(sql, new EntrustmentRowMapper(), unifiedNumber);
            return records.isEmpty() ? null : records.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 行映射器
    private class EntrustmentRowMapper implements RowMapper<Entrustment> {
        @Override
        public Entrustment mapRow(ResultSet rs, int rowNum) throws SQLException {
            Entrustment record = new Entrustment();
            record.setId(rs.getString("ID"));
            record.setWtNum(rs.getString("WT_NUM"));
            record.setSampleName(rs.getString("SAMPLE_NAME"));
            record.setTestCategory(rs.getString("TEST_CATEGORY"));
            record.setClientUnit(rs.getString("CLIENT_UNIT"));
            record.setClient(rs.getString("CLIENT"));
            record.setClientTel(rs.getString("CLIENT_TEL"));
            record.setClientUnitPostalcode(rs.getString("CLIENT_UNIT_POSTALCODE"));
            record.setClientUnitPerson(rs.getString("CLIENT_UNIT_PERSON"));
            record.setProjectName(rs.getString("PROJECT_NAME"));
            record.setConstructionPart(rs.getString("CONSTRUCTION_PART"));
            record.setProjectArea(rs.getString("PROJECT_AREA"));
            record.setBuildingUnit(rs.getString("BUILDING_UNIT"));
            record.setSurveyUnit(rs.getString("SURVEY_UNIT"));
            record.setConstructionUnit(rs.getString("CONSTRUCTION_UNIT"));
            record.setWitnessUnit(rs.getString("WITNESS_UNIT"));
            record.setWitness(rs.getString("WITNESS"));
            record.setSupervisionUnit(rs.getString("SUPERVISION_UNIT"));
            record.setDesignUnit(rs.getString("DESIGN_UNIT"));
            record.setCommissionDate(rs.getDate("COMMISSION_DATE"));
            record.setRemarks(rs.getString("REMARKS"));
            record.setSampleStatus(rs.getString("SAMPLE_STATUS"));
            record.setTestItems(rs.getString("TEST_ITEMS"));
            return record;
        }
    }
}
