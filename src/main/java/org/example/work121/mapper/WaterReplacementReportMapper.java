package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.WaterReplacementReport;

@Mapper
public interface WaterReplacementReportMapper {

    @Delete("DELETE FROM T_WATER_REPLACEMENT WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int deleteByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Select("SELECT " +
            "ID as id, " +
            "ENTRUSTMENT_ID as entrustmentId, " +
            "DATA_JSON as dataJson, " +
            "RECORD_REVIEW_SIGN as reviewSignaturePhoto, " +
            "RECORD_TESTER_SIGN as inspectSignaturePhoto, " +
            "APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto, " +
            "CREATE_BY as createBy, " +
            "CREATE_TIME as createTime, " +
            "UPDATE_BY as updateBy, " +
            "UPDATE_TIME as updateTime " +
            "FROM T_WATER_REPLACEMENT " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    WaterReplacementReport selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Insert("INSERT INTO T_WATER_REPLACEMENT (ID, ENTRUSTMENT_ID, DATA_JSON, RECORD_REVIEW_SIGN, RECORD_TESTER_SIGN, APPROVE_SIGNATURE_PHOTO, CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME) " +
            "VALUES (#{id}, #{entrustmentId}, #{dataJson}, #{reviewSignaturePhoto}, #{inspectSignaturePhoto}, #{approveSignaturePhoto}, #{createBy}, #{createTime}, #{updateBy}, #{updateTime})")
    int insert(WaterReplacementReport report);

    @Update("UPDATE T_WATER_REPLACEMENT SET " +
            "DATA_JSON = #{dataJson}, " +
            "RECORD_REVIEW_SIGN = #{reviewSignaturePhoto}, " +
            "RECORD_TESTER_SIGN = #{inspectSignaturePhoto}, " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto}, " +
            "UPDATE_BY = #{updateBy}, " +
            "UPDATE_TIME = #{updateTime} " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int update(WaterReplacementReport report);

    @Select("SELECT " +
            "ID as id, " +
            "ENTRUSTMENT_ID as entrustmentId, " +
            "DATA_JSON as dataJson, " +
            "RECORD_REVIEW_SIGN as reviewSignaturePhoto, " +
            "RECORD_TESTER_SIGN as inspectSignaturePhoto, " +
            "APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto, " +
            "CREATE_BY as createBy, " +
            "CREATE_TIME as createTime, " +
            "UPDATE_BY as updateBy, " +
            "UPDATE_TIME as updateTime " +
            "FROM T_WATER_REPLACEMENT " +
            "WHERE ID = #{id}")
    WaterReplacementReport selectById(@Param("id") String id);

    @Update("UPDATE T_WATER_REPLACEMENT SET " +
            "ENTRUSTMENT_ID = #{entrustmentId}, " +
            "DATA_JSON = #{dataJson}, " +
            "RECORD_REVIEW_SIGN = #{reviewSignaturePhoto}, " +
            "RECORD_TESTER_SIGN = #{inspectSignaturePhoto}, " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto}, " +
            "UPDATE_BY = #{updateBy}, " +
            "UPDATE_TIME = #{updateTime} " +
            "WHERE ID = #{id}")
    int updateById(WaterReplacementReport report);
}
