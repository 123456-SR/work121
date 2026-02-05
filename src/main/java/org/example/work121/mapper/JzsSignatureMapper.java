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
            "#{signatureId}, #{userAccount}, #{signatureType}, #{signatureBlob}, #{imageType}, #{imageSize}, #{createTime}, #{remarks}" +
            ")")
    int insert(JzsSignature signature);

    @Update("UPDATE JZS_SIGNATURE SET " +
            "SIGNATURE_BLOB = #{signatureBlob}, " +
            "IMAGE_TYPE = #{imageType}, " +
            "IMAGE_SIZE = #{imageSize}, " +
            "UPDATE_TIME = #{updateTime}, " +
            "REMARKS = #{remarks} " +
            "WHERE SIGNATURE_ID = #{signatureId}")
    int update(JzsSignature signature);

    @Delete("DELETE FROM JZS_SIGNATURE WHERE SIGNATURE_ID = #{signatureId}")
    int deleteById(String signatureId);
}
