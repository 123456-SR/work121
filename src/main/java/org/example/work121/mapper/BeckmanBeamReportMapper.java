package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.BeckmanBeamReport;

@Mapper
public interface BeckmanBeamReportMapper {

    @Delete("DELETE FROM T_BECKMAN_BEAM WHERE ENTRUSTMENT_ID = #{entrustmentId}")
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
            "STATUS as status, " +
            "REJECT_REASON as rejectReason, " +
            "NEXT_HANDLER as nextHandler, " +
            "CREATE_BY as createBy, " +
            "CREATE_TIME as createTime, " +
            "UPDATE_BY as updateBy, " +
            "UPDATE_TIME as updateTime " +
            "FROM T_BECKMAN_BEAM " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    BeckmanBeamReport selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Insert("INSERT INTO T_BECKMAN_BEAM (ID, ENTRUSTMENT_ID, CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME, DATA_JSON, RECORD_REVIEW_SIGN, RECORD_TESTER_SIGN, APPROVE_SIGNATURE_PHOTO) " +
            "VALUES (#{id,jdbcType=VARCHAR}, #{entrustmentId,jdbcType=VARCHAR}, #{createBy,jdbcType=VARCHAR}, #{createTime,jdbcType=TIMESTAMP}, #{updateBy,jdbcType=VARCHAR}, #{updateTime,jdbcType=TIMESTAMP}, #{dataJson,jdbcType=CLOB}, NVL(#{recordReviewSign,jdbcType=CLOB}, #{reviewSignaturePhoto,jdbcType=CLOB}), NVL(#{recordTesterSign,jdbcType=CLOB}, #{inspectSignaturePhoto,jdbcType=CLOB}), #{approveSignaturePhoto,jdbcType=CLOB})")
    int insert(BeckmanBeamReport report);

    @Update("UPDATE (SELECT * FROM T_BECKMAN_BEAM WHERE ENTRUSTMENT_ID = #{entrustmentId,jdbcType=VARCHAR}) SET " +
            "UPDATE_BY = #{updateBy,jdbcType=VARCHAR}, " +
            "UPDATE_TIME = #{updateTime,jdbcType=TIMESTAMP}, " +
            "DATA_JSON = #{dataJson,jdbcType=CLOB}, " +
            "RECORD_REVIEW_SIGN = NVL(#{recordReviewSign,jdbcType=CLOB}, #{reviewSignaturePhoto,jdbcType=CLOB}), " +
            "RECORD_TESTER_SIGN = NVL(#{recordTesterSign,jdbcType=CLOB}, #{inspectSignaturePhoto,jdbcType=CLOB}), " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto,jdbcType=CLOB}")
    int update(BeckmanBeamReport report);

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
            "STATUS as status, " +
            "REJECT_REASON as rejectReason, " +
            "NEXT_HANDLER as nextHandler, " +
            "CREATE_BY as createBy, " +
            "CREATE_TIME as createTime, " +
            "UPDATE_BY as updateBy, " +
            "UPDATE_TIME as updateTime " +
            "FROM T_BECKMAN_BEAM " +
            "WHERE ID = #{id}")
    BeckmanBeamReport selectById(@Param("id") String id);

    @Update("UPDATE (SELECT * FROM T_BECKMAN_BEAM WHERE ID = #{id,jdbcType=VARCHAR}) SET " +
            "ENTRUSTMENT_ID = #{entrustmentId,jdbcType=VARCHAR}, " +
            "UPDATE_BY = #{updateBy,jdbcType=VARCHAR}, " +
            "UPDATE_TIME = #{updateTime,jdbcType=TIMESTAMP}, " +
            "DATA_JSON = #{dataJson,jdbcType=CLOB}, " +
            "RECORD_REVIEW_SIGN = NVL(#{recordReviewSign,jdbcType=CLOB}, #{reviewSignaturePhoto,jdbcType=CLOB}), " +
            "RECORD_TESTER_SIGN = NVL(#{recordTesterSign,jdbcType=CLOB}, #{inspectSignaturePhoto,jdbcType=CLOB}), " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto,jdbcType=CLOB}")
    int updateById(BeckmanBeamReport report);
}
