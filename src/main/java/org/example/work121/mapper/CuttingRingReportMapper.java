package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.CuttingRingReport;

@Mapper
public interface CuttingRingReportMapper {

    @Delete("DELETE FROM T_CUTTING_RING WHERE ENTRUSTMENT_ID = #{entrustmentId}")
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
            "FROM T_CUTTING_RING " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    CuttingRingReport selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Insert("INSERT INTO T_CUTTING_RING (ID, ENTRUSTMENT_ID, DATA_JSON, RECORD_REVIEW_SIGN, RECORD_TESTER_SIGN, APPROVE_SIGNATURE_PHOTO, RECORD_TESTER, RECORD_REVIEWER, APPROVER, CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME) " +
            "VALUES (#{id}, #{entrustmentId}, #{dataJson}, NVL(#{recordReviewSign}, #{reviewSignaturePhoto}), NVL(#{recordTesterSign}, #{inspectSignaturePhoto}), #{approveSignaturePhoto}, #{tester}, #{reviewer}, #{approver}, #{createBy}, #{createTime}, #{updateBy}, #{updateTime})")
    int insert(CuttingRingReport report);

    @Update("UPDATE T_CUTTING_RING SET " +
            "DATA_JSON = #{dataJson}, " +
            "RECORD_REVIEW_SIGN = NVL(#{recordReviewSign}, #{reviewSignaturePhoto}), " +
            "RECORD_TESTER_SIGN = NVL(#{recordTesterSign}, #{inspectSignaturePhoto}), " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto}, " +
            "RECORD_TESTER = #{tester}, " +
            "RECORD_REVIEWER = #{reviewer}, " +
            "APPROVER = #{approver}, " +
            "UPDATE_BY = #{updateBy}, " +
            "UPDATE_TIME = #{updateTime} " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int update(CuttingRingReport report);

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
            "FROM T_CUTTING_RING " +
            "WHERE ID = #{id}")
    CuttingRingReport selectById(@Param("id") String id);

    @Update("UPDATE T_CUTTING_RING SET " +
            "ENTRUSTMENT_ID = #{entrustmentId}, " +
            "DATA_JSON = #{dataJson}, " +
            "RECORD_REVIEW_SIGN = NVL(#{recordReviewSign}, #{reviewSignaturePhoto}), " +
            "RECORD_TESTER_SIGN = NVL(#{recordTesterSign}, #{inspectSignaturePhoto}), " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto}, " +
            "RECORD_TESTER = #{tester}, " +
            "RECORD_REVIEWER = #{reviewer}, " +
            "APPROVER = #{approver}, " +
            "UPDATE_BY = #{updateBy}, " +
            "UPDATE_TIME = #{updateTime} " +
            "WHERE ID = #{id}")
    int updateById(CuttingRingReport report);
}
