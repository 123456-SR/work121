package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.LightDynamicPenetrationReport;
import java.util.List;

@Mapper
public interface LightDynamicPenetrationReportMapper {

    @Delete("DELETE FROM T_LIGHT_DYNAMIC_PENETRATION WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int deleteByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Delete("DELETE FROM T_LIGHT_DYNAMIC_PENETRATION WHERE ID = #{id}")
    int deleteById(@Param("id") String id);

    @Select("SELECT " +
            "ID as id, " +
            "ENTRUSTMENT_ID as entrustmentId, " +
            "DATA_JSON as dataJson, " +
            "STATUS as status, " +
            "REJECT_REASON as rejectReason, " +
            "NEXT_HANDLER as nextHandler, " +
            "RECORD_REVIEW_SIGN as reviewSignaturePhoto, " +
            "RECORD_TESTER_SIGN as inspectSignaturePhoto, " +
            "APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto, " +
            "RECORD_TESTER as tester, " +
            "RECORD_REVIEWER as reviewer, " +
            "APPROVER as approver, " +
            "CREATE_BY as createBy, " +
            "CREATE_TIME as createTime, " +
            "UPDATE_BY as updateBy, " +
            "UPDATE_TIME as updateTime, " +
            "REMARKS as remarks " +
            "FROM T_LIGHT_DYNAMIC_PENETRATION " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    List<LightDynamicPenetrationReport> selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Insert("INSERT INTO T_LIGHT_DYNAMIC_PENETRATION (ID, ENTRUSTMENT_ID, DATA_JSON, STATUS, REJECT_REASON, NEXT_HANDLER, RECORD_REVIEW_SIGN, RECORD_TESTER_SIGN, APPROVE_SIGNATURE_PHOTO, RECORD_TESTER, RECORD_REVIEWER, APPROVER, CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME, REMARKS) " +
            "VALUES (#{id}, #{entrustmentId}, #{dataJson,jdbcType=CLOB}, #{status}, #{rejectReason}, #{nextHandler}, #{reviewSignaturePhoto}, #{inspectSignaturePhoto}, #{approveSignaturePhoto}, NVL(#{recordTester}, #{tester}), NVL(#{recordReviewer}, #{reviewer}), #{approver}, #{createBy}, #{createTime}, #{updateBy}, #{updateTime}, #{remarks,jdbcType=VARCHAR})")
    int insert(LightDynamicPenetrationReport report);

    @Update("UPDATE T_LIGHT_DYNAMIC_PENETRATION SET " +
            "DATA_JSON = #{dataJson,jdbcType=CLOB}, " +
            "STATUS = #{status}, " +
            "REJECT_REASON = #{rejectReason}, " +
            "NEXT_HANDLER = #{nextHandler}, " +
            "RECORD_REVIEW_SIGN = #{reviewSignaturePhoto}, " +
            "RECORD_TESTER_SIGN = #{inspectSignaturePhoto}, " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto}, " +
            "RECORD_TESTER = NVL(#{recordTester}, #{tester}), " +
            "RECORD_REVIEWER = NVL(#{recordReviewer}, #{reviewer}), " +
            "APPROVER = #{approver}, " +
            "UPDATE_BY = #{updateBy}, " +
            "UPDATE_TIME = #{updateTime}, " +
            "REMARKS = #{remarks,jdbcType=VARCHAR} " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int update(LightDynamicPenetrationReport report);

    @Select("SELECT " +
            "ID as id, " +
            "ENTRUSTMENT_ID as entrustmentId, " +
            "DATA_JSON as dataJson, " +
            "STATUS as status, " +
            "REJECT_REASON as rejectReason, " +
            "NEXT_HANDLER as nextHandler, " +
            "RECORD_REVIEW_SIGN as reviewSignaturePhoto, " +
            "RECORD_TESTER_SIGN as inspectSignaturePhoto, " +
            "APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto, " +
            "RECORD_TESTER as tester, " +
            "RECORD_REVIEWER as reviewer, " +
            "APPROVER as approver, " +
            "CREATE_BY as createBy, " +
            "CREATE_TIME as createTime, " +
            "UPDATE_BY as updateBy, " +
            "UPDATE_TIME as updateTime, " +
            "REMARKS as remarks " +
            "FROM T_LIGHT_DYNAMIC_PENETRATION " +
            "WHERE ID = #{id}")
    LightDynamicPenetrationReport selectById(@Param("id") String id);

    @Update("UPDATE T_LIGHT_DYNAMIC_PENETRATION SET " +
            "ENTRUSTMENT_ID = #{entrustmentId}, " +
            "DATA_JSON = #{dataJson,jdbcType=CLOB}, " +
            "STATUS = #{status}, " +
            "REJECT_REASON = #{rejectReason}, " +
            "NEXT_HANDLER = #{nextHandler}, " +
            "RECORD_REVIEW_SIGN = #{reviewSignaturePhoto}, " +
            "RECORD_TESTER_SIGN = #{inspectSignaturePhoto}, " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto}, " +
            "RECORD_TESTER = NVL(#{recordTester}, #{tester}), " +
            "RECORD_REVIEWER = NVL(#{recordReviewer}, #{reviewer}), " +
            "APPROVER = #{approver}, " +
            "UPDATE_BY = #{updateBy}, " +
            "UPDATE_TIME = #{updateTime}, " +
            "REMARKS = #{remarks,jdbcType=VARCHAR} " +
            "WHERE ID = #{id}")
    int updateById(LightDynamicPenetrationReport report);
}
