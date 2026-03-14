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

    @Insert("INSERT INTO T_BECKMAN_BEAM (ID, ENTRUSTMENT_ID, DATA_JSON, RECORD_REVIEW_SIGN, RECORD_TESTER_SIGN, APPROVE_SIGNATURE_PHOTO, CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME) " +
            "VALUES (#{id}, #{entrustmentId}, #{dataJson}, NVL(#{recordReviewSign}, #{reviewSignaturePhoto}), NVL(#{recordTesterSign}, #{inspectSignaturePhoto}), #{approveSignaturePhoto}, #{createBy}, #{createTime}, #{updateBy}, #{updateTime})")
    int insert(BeckmanBeamReport report);

    @Update("UPDATE T_BECKMAN_BEAM SET " +
            "DATA_JSON = #{dataJson}, " +
            "RECORD_REVIEW_SIGN = NVL(#{recordReviewSign}, #{reviewSignaturePhoto}), " +
            "RECORD_TESTER_SIGN = NVL(#{recordTesterSign}, #{inspectSignaturePhoto}), " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto}, " +
            "UPDATE_BY = #{updateBy}, " +
            "UPDATE_TIME = #{updateTime} " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
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

    @Update("UPDATE T_BECKMAN_BEAM SET " +
            "ENTRUSTMENT_ID = #{entrustmentId}, " +
            "DATA_JSON = #{dataJson}, " +
            "RECORD_REVIEW_SIGN = NVL(#{recordReviewSign}, #{reviewSignaturePhoto}), " +
            "RECORD_TESTER_SIGN = NVL(#{recordTesterSign}, #{inspectSignaturePhoto}), " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto}, " +
            "UPDATE_BY = #{updateBy}, " +
            "UPDATE_TIME = #{updateTime} " +
            "WHERE ID = #{id}")
    int updateById(BeckmanBeamReport report);
}
