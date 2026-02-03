package org.example.work121.service.impl;

import org.example.work121.entity.JzsSignature;
import org.example.work121.service.JzsSignatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * 签字存放表服务实现类
 */
@Service
public class JzsSignatureServiceImpl implements JzsSignatureService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public boolean saveSignature(JzsSignature signature) {
        try {
            // 检查是否已存在该用户的签名
            JzsSignature existingSignature = getSignatureByUserAccount(
                    signature.getUserAccount());

            if (existingSignature != null) {
                // 更新现有签名
                String sql = "UPDATE JZS_SIGNATURE SET " +
                        "SIGNATURE_BLOB = ? " +
                        "WHERE USER_ACCOUNT = ?";

                int result = jdbcTemplate.update(sql,
                        signature.getSignatureBlob(),
                        signature.getUserAccount());

                return result > 0;
            } else {
                // 插入新签名
                String sql = "INSERT INTO JZS_SIGNATURE " +
                        "(SIGNATURE_ID, USER_ACCOUNT, SIGNATURE_BLOB) " +
                        "VALUES (?, ?, ?)";

                String signatureId = signature.getSignatureId() != null ? 
                        signature.getSignatureId() : UUID.randomUUID().toString();

                int result = jdbcTemplate.update(sql,
                        signatureId,
                        signature.getUserAccount(),
                        signature.getSignatureBlob());

                return result > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public JzsSignature getSignatureByUserAccount(String userAccount) {
        try {
            String sql = "SELECT * FROM JZS_SIGNATURE WHERE USER_ACCOUNT = ?";
            List<JzsSignature> signatures = jdbcTemplate.query(sql, new SignatureRowMapper(), userAccount);
            return signatures.isEmpty() ? null : signatures.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean deleteSignature(String signatureId) {
        try {
            String sql = "DELETE FROM JZS_SIGNATURE WHERE SIGNATURE_ID = ?";
            int result = jdbcTemplate.update(sql, signatureId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 行映射器
    private class SignatureRowMapper implements RowMapper<JzsSignature> {
        @Override
        public JzsSignature mapRow(ResultSet rs, int rowNum) throws SQLException {
            JzsSignature signature = new JzsSignature();
            signature.setSignatureId(rs.getString("SIGNATURE_ID"));
            signature.setUserAccount(rs.getString("USER_ACCOUNT"));
            // 将Blob转换为byte[]
            java.sql.Blob blob = rs.getBlob("SIGNATURE_BLOB");
            if (blob != null) {
                signature.setSignatureBlob(blob.getBytes(1, (int) blob.length()));
            }
            return signature;
        }
    }
}