package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.JzsSignature;

import java.util.List;

@Mapper
public interface JzsSignatureMapper {

    @Select("SELECT " +
            "SIGNATURE_ID as signatureId, " +
            "USER_ACCOUNT as userAccount, " +
            "SIGNATURE_BLOB as signatureBlob, " +
            "SIGNATURE_TYPE as signatureType, " +
            "IMAGE_TYPE as imageType, " +
            "IMAGE_SIZE as imageSize, " +
            "CREATE_TIME as createTime, " +
            "UPDATE_TIME as updateTime, " +
            "REMARKS as remarks " +
            "FROM JZS_SIGNATURE " +
            "WHERE USER_ACCOUNT = #{userAccount} AND SIGNATURE_TYPE = #{signatureType}")
    JzsSignature selectByUserAndType(@Param("userAccount") String userAccount, @Param("signatureType") String signatureType);

    @Select("SELECT " +
            "SIGNATURE_ID as signatureId, " +
            "USER_ACCOUNT as userAccount, " +
            "SIGNATURE_BLOB as signatureBlob, " +
            "SIGNATURE_TYPE as signatureType, " +
            "IMAGE_TYPE as imageType, " +
            "IMAGE_SIZE as imageSize, " +
            "CREATE_TIME as createTime, " +
            "UPDATE_TIME as updateTime, " +
            "REMARKS as remarks " +
            "FROM JZS_SIGNATURE " +
            "WHERE USER_ACCOUNT = #{userAccount}")
    List<JzsSignature> selectListByUser(String userAccount);

    @Insert("INSERT INTO JZS_SIGNATURE (" +
            "SIGNATURE_ID, USER_ACCOUNT, SIGNATURE_TYPE, SIGNATURE_BLOB, IMAGE_TYPE, IMAGE_SIZE, CREATE_TIME, REMARKS" +
            ") VALUES (" +
            "#{signatureId, jdbcType=VARCHAR}, #{userAccount, jdbcType=VARCHAR}, #{signatureType, jdbcType=VARCHAR}, " +
            "#{signatureBlob, jdbcType=BLOB}, #{imageType, jdbcType=VARCHAR}, #{imageSize, jdbcType=NUMERIC}, " +
            "#{createTime, jdbcType=TIMESTAMP}, #{remarks, jdbcType=VARCHAR}" +
            ")")
    int insert(JzsSignature signature);

    @Update("UPDATE JZS_SIGNATURE SET " +
            "SIGNATURE_BLOB = #{signatureBlob, jdbcType=BLOB}, " +
            "IMAGE_TYPE = #{imageType, jdbcType=VARCHAR}, " +
            "IMAGE_SIZE = #{imageSize, jdbcType=NUMERIC}, " +
            "UPDATE_TIME = #{updateTime, jdbcType=TIMESTAMP}, " +
            "REMARKS = #{remarks, jdbcType=VARCHAR} " +
            "WHERE SIGNATURE_ID = #{signatureId, jdbcType=VARCHAR}")
    int update(JzsSignature signature);

    @Delete("DELETE FROM JZS_SIGNATURE WHERE SIGNATURE_ID = #{signatureId}")
    int deleteById(String signatureId);
}
