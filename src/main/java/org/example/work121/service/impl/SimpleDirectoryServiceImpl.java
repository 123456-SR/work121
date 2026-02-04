package org.example.work121.service.impl;

import org.example.work121.entity.SimpleDirectory;
import org.example.work121.service.SimpleDirectoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * 极简目录表服务实现类
 */
@Service
public class SimpleDirectoryServiceImpl implements SimpleDirectoryService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public boolean saveDirectory(SimpleDirectory directory) {
        try {
            // 检查是否已存在该目录
            SimpleDirectory existingRecord = getDirectoryByDirId(directory.getDirId());

            if (existingRecord != null) {
                // 更新现有记录
                String sql = "UPDATE T_SIMPLE_DIRECTORY SET " +
                        "DIR_NAME = ?, " +
                        "TABLE1_TYPE = ?, " +
                        "TABLE1_ID = ?, " +
                        "TABLE2_TYPE = ?, " +
                        "TABLE2_ID = ?, " +
                        "TABLE3_TYPE = ?, " +
                        "TABLE3_ID = ?, " +
                        "TABLE4_TYPE = ?, " +
                        "TABLE4_ID = ?, " +
                        "TABLE5_TYPE = ?, " +
                        "TABLE5_ID = ?, " +
                        "TABLE6_TYPE = ?, " +
                        "TABLE6_ID = ?, " +
                        "TABLE7_TYPE = ?, " +
                        "TABLE7_ID = ?, " +
                        "TABLE8_TYPE = ?, " +
                        "TABLE8_ID = ?, " +
                        "TABLE9_TYPE = ?, " +
                        "TABLE9_ID = ?, " +
                        "TABLE10_TYPE = ?, " +
                        "TABLE10_ID = ?, " +
                        "STATUS = ? " +
                        "WHERE ID = ?";

                int result = jdbcTemplate.update(sql,
                        directory.getDirName(),
                        directory.getTable1Type(),
                        directory.getTable1Id(),
                        directory.getTable2Type(),
                        directory.getTable2Id(),
                        directory.getTable3Type(),
                        directory.getTable3Id(),
                        directory.getTable4Type(),
                        directory.getTable4Id(),
                        directory.getTable5Type(),
                        directory.getTable5Id(),
                        directory.getTable6Type(),
                        directory.getTable6Id(),
                        directory.getTable7Type(),
                        directory.getTable7Id(),
                        directory.getTable8Type(),
                        directory.getTable8Id(),
                        directory.getTable9Type(),
                        directory.getTable9Id(),
                        directory.getTable10Type(),
                        directory.getTable10Id(),
                        directory.getStatus(),
                        directory.getId()
                );

                return result > 0;
            } else {
                // 插入新记录
                String sql = "INSERT INTO T_SIMPLE_DIRECTORY " +
                        "(ID, DIR_ID, DIR_NAME, TABLE1_TYPE, TABLE1_ID, " +
                        "TABLE2_TYPE, TABLE2_ID, TABLE3_TYPE, TABLE3_ID, " +
                        "TABLE4_TYPE, TABLE4_ID, TABLE5_TYPE, TABLE5_ID, " +
                        "TABLE6_TYPE, TABLE6_ID, TABLE7_TYPE, TABLE7_ID, " +
                        "TABLE8_TYPE, TABLE8_ID, TABLE9_TYPE, TABLE9_ID, " +
                        "TABLE10_TYPE, TABLE10_ID, STATUS, CREATE_MAN) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                String id = directory.getId() != null && !directory.getId().isEmpty() ? 
                        directory.getId() : UUID.randomUUID().toString();
                String dirId = directory.getDirId() != null && !directory.getDirId().isEmpty() ? 
                        directory.getDirId() : UUID.randomUUID().toString();

                int result = jdbcTemplate.update(sql,
                        id,
                        dirId,
                        directory.getDirName(),
                        directory.getTable1Type(),
                        directory.getTable1Id(),
                        directory.getTable2Type(),
                        directory.getTable2Id(),
                        directory.getTable3Type(),
                        directory.getTable3Id(),
                        directory.getTable4Type(),
                        directory.getTable4Id(),
                        directory.getTable5Type(),
                        directory.getTable5Id(),
                        directory.getTable6Type(),
                        directory.getTable6Id(),
                        directory.getTable7Type(),
                        directory.getTable7Id(),
                        directory.getTable8Type(),
                        directory.getTable8Id(),
                        directory.getTable9Type(),
                        directory.getTable9Id(),
                        directory.getTable10Type(),
                        directory.getTable10Id(),
                        directory.getStatus() != null ? directory.getStatus() : 1,
                        directory.getCreateBy() != null ? directory.getCreateBy() : "admin"
                );

                return result > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public SimpleDirectory getDirectoryById(String id) {
        try {
            String sql = "SELECT * FROM T_SIMPLE_DIRECTORY WHERE ID = ?";
            List<SimpleDirectory> records = jdbcTemplate.query(sql, new SimpleDirectoryRowMapper(), id);
            return records.isEmpty() ? null : records.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public SimpleDirectory getDirectoryByDirId(String dirId) {
        try {
            String sql = "SELECT * FROM T_SIMPLE_DIRECTORY WHERE DIR_ID = ?";
            List<SimpleDirectory> records = jdbcTemplate.query(sql, new SimpleDirectoryRowMapper(), dirId);
            return records.isEmpty() ? null : records.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<SimpleDirectory> getAllDirectories() {
        try {
            String sql = "SELECT * FROM T_SIMPLE_DIRECTORY ORDER BY CREATE_TIME DESC";
            return jdbcTemplate.query(sql, new SimpleDirectoryRowMapper());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean deleteDirectory(String id) {
        try {
            String sql = "DELETE FROM T_SIMPLE_DIRECTORY WHERE ID = ?";
            int result = jdbcTemplate.update(sql, id);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 行映射器
    private class SimpleDirectoryRowMapper implements RowMapper<SimpleDirectory> {
        @Override
        public SimpleDirectory mapRow(ResultSet rs, int rowNum) throws SQLException {
            SimpleDirectory directory = new SimpleDirectory();
            directory.setId(rs.getString("ID"));
            directory.setDirId(rs.getString("DIR_ID"));
            directory.setDirName(rs.getString("DIR_NAME"));
            directory.setTable1Type(rs.getString("TABLE1_TYPE"));
            directory.setTable1Id(rs.getString("TABLE1_ID"));
            directory.setTable2Type(rs.getString("TABLE2_TYPE"));
            directory.setTable2Id(rs.getString("TABLE2_ID"));
            directory.setTable3Type(rs.getString("TABLE3_TYPE"));
            directory.setTable3Id(rs.getString("TABLE3_ID"));
            directory.setTable4Type(rs.getString("TABLE4_TYPE"));
            directory.setTable4Id(rs.getString("TABLE4_ID"));
            directory.setTable5Type(rs.getString("TABLE5_TYPE"));
            directory.setTable5Id(rs.getString("TABLE5_ID"));
            directory.setTable6Type(rs.getString("TABLE6_TYPE"));
            directory.setTable6Id(rs.getString("TABLE6_ID"));
            directory.setTable7Type(rs.getString("TABLE7_TYPE"));
            directory.setTable7Id(rs.getString("TABLE7_ID"));
            directory.setTable8Type(rs.getString("TABLE8_TYPE"));
            directory.setTable8Id(rs.getString("TABLE8_ID"));
            directory.setTable9Type(rs.getString("TABLE9_TYPE"));
            directory.setTable9Id(rs.getString("TABLE9_ID"));
            directory.setTable10Type(rs.getString("TABLE10_TYPE"));
            directory.setTable10Id(rs.getString("TABLE10_ID"));
            directory.setStatus(rs.getInt("STATUS"));
            return directory;
        }
    }
}
