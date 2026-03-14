package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.WaterReplacementResult;

@Mapper
public interface WaterReplacementResultMapper {

    @Delete("DELETE FROM T_WATER_REPLACEMENT WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int deleteByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Select("SELECT " +
            "ID as id, " +
            "ENTRUSTMENT_ID as entrustmentId, " +
            "DATA_JSON as dataJson, " +
            "RECORD_REVIEW_SIGN as reviewSignaturePhoto, " +
            "RECORD_TESTER_SIGN as inspectSignaturePhoto, " +
            "APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto, " +
            "RECORD_TESTER as tester, " +
            "RECORD_REVIEWER as reviewer, " +
            "APPROVER as approver, " +
            "CREATE_BY as createBy, " +
            "CREATE_TIME as createTime, " +
            "UPDATE_BY as updateBy, " +
            "UPDATE_TIME as updateTime " +
            "FROM T_WATER_REPLACEMENT " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    WaterReplacementResult selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Insert("INSERT INTO T_WATER_REPLACEMENT (ID, ENTRUSTMENT_ID, DATA_JSON, RECORD_REVIEW_SIGN, RECORD_TESTER_SIGN, APPROVE_SIGNATURE_PHOTO, RECORD_TESTER, RECORD_REVIEWER, APPROVER, CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME) " +
            "VALUES (#{id}, #{entrustmentId}, #{dataJson,jdbcType=CLOB}, #{reviewSignaturePhoto,jdbcType=CLOB}, #{inspectSignaturePhoto,jdbcType=CLOB}, #{approveSignaturePhoto,jdbcType=CLOB}, NVL(#{recordTester,jdbcType=VARCHAR}, #{tester,jdbcType=VARCHAR}), NVL(#{recordReviewer,jdbcType=VARCHAR}, #{reviewer,jdbcType=VARCHAR}), #{approver,jdbcType=VARCHAR}, #{createBy,jdbcType=VARCHAR}, #{createTime,jdbcType=TIMESTAMP}, #{updateBy,jdbcType=VARCHAR}, #{updateTime,jdbcType=TIMESTAMP})")
    int insert(WaterReplacementResult result);

    @Update("UPDATE T_WATER_REPLACEMENT SET " +
            "DATA_JSON = #{dataJson,jdbcType=CLOB}, " +
            "RECORD_REVIEW_SIGN = #{reviewSignaturePhoto,jdbcType=CLOB}, " +
            "RECORD_TESTER_SIGN = #{inspectSignaturePhoto,jdbcType=CLOB}, " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto,jdbcType=CLOB}, " +
            "RECORD_TESTER = NVL(#{recordTester,jdbcType=VARCHAR}, #{tester,jdbcType=VARCHAR}), " +
            "RECORD_REVIEWER = NVL(#{recordReviewer,jdbcType=VARCHAR}, #{reviewer,jdbcType=VARCHAR}), " +
            "APPROVER = #{approver,jdbcType=VARCHAR}, " +
            "UPDATE_BY = #{updateBy,jdbcType=VARCHAR}, " +
            "UPDATE_TIME = #{updateTime,jdbcType=TIMESTAMP} " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int update(WaterReplacementResult result);

    @Select("SELECT " +
            "ID as id, " +
            "ENTRUSTMENT_ID as entrustmentId, " +
            "DATA_JSON as dataJson, " +
            "RECORD_REVIEW_SIGN as reviewSignaturePhoto, " +
            "RECORD_TESTER_SIGN as inspectSignaturePhoto, " +
            "APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto, " +
            "RECORD_TESTER as tester, " +
            "RECORD_REVIEWER as reviewer, " +
            "APPROVER as approver, " +
            "CREATE_BY as createBy, " +
            "CREATE_TIME as createTime, " +
            "UPDATE_BY as updateBy, " +
            "UPDATE_TIME as updateTime " +
            "FROM T_WATER_REPLACEMENT " +
            "WHERE ID = #{id}")
    WaterReplacementResult selectById(@Param("id") String id);

    @Update("UPDATE T_WATER_REPLACEMENT SET " +
            "ENTRUSTMENT_ID = #{entrustmentId}, " +
            "DATA_JSON = #{dataJson}, " +
            "RECORD_REVIEW_SIGN = #{reviewSignaturePhoto}, " +
            "RECORD_TESTER_SIGN = #{inspectSignaturePhoto}, " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto}, " +
            "RECORD_TESTER = NVL(#{recordTester}, #{tester}), " +
            "RECORD_REVIEWER = NVL(#{recordReviewer}, #{reviewer}), " +
            "APPROVER = #{approver}, " +
            "UPDATE_BY = #{updateBy}, " +
            "UPDATE_TIME = #{updateTime} " +
            "WHERE ID = #{id}")
    int updateById(WaterReplacementResult result);
}
