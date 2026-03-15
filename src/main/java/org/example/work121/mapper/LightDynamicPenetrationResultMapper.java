package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.LightDynamicPenetrationResult;
import java.util.List;

@Mapper
public interface LightDynamicPenetrationResultMapper {

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
            "RECORD_TESTER_SIGN as inspectSignaturePhoto, " +
            "RECORD_REVIEW_SIGN as reviewSignaturePhoto, " +
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
    List<LightDynamicPenetrationResult> selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Insert("INSERT INTO T_LIGHT_DYNAMIC_PENETRATION (ID, ENTRUSTMENT_ID, STATUS, REJECT_REASON, NEXT_HANDLER, RECORD_TESTER, RECORD_REVIEWER, APPROVER, CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME, REMARKS, DATA_JSON, RECORD_TESTER_SIGN, RECORD_REVIEW_SIGN, APPROVE_SIGNATURE_PHOTO) " +
            "VALUES (#{id}, #{entrustmentId}, #{status,jdbcType=VARCHAR}, #{rejectReason,jdbcType=VARCHAR}, #{nextHandler,jdbcType=VARCHAR}, NVL(#{recordTester,jdbcType=VARCHAR}, #{tester,jdbcType=VARCHAR}), NVL(#{recordReviewer,jdbcType=VARCHAR}, #{reviewer,jdbcType=VARCHAR}), #{approver,jdbcType=VARCHAR}, #{createBy,jdbcType=VARCHAR}, #{createTime,jdbcType=TIMESTAMP}, #{updateBy,jdbcType=VARCHAR}, #{updateTime,jdbcType=TIMESTAMP}, #{remarks,jdbcType=VARCHAR}, #{dataJson,jdbcType=CLOB}, #{inspectSignaturePhoto,jdbcType=CLOB}, #{reviewSignaturePhoto,jdbcType=CLOB}, #{approveSignaturePhoto,jdbcType=CLOB})")
    int insert(LightDynamicPenetrationResult result);

    @Update("UPDATE T_LIGHT_DYNAMIC_PENETRATION SET " +
            "STATUS = #{status,jdbcType=VARCHAR}, " +
            "REJECT_REASON = #{rejectReason,jdbcType=VARCHAR}, " +
            "NEXT_HANDLER = #{nextHandler,jdbcType=VARCHAR}, " +
            "RECORD_TESTER = NVL(#{recordTester,jdbcType=VARCHAR}, #{tester,jdbcType=VARCHAR}), " +
            "RECORD_REVIEWER = NVL(#{recordReviewer,jdbcType=VARCHAR}, #{reviewer,jdbcType=VARCHAR}), " +
            "APPROVER = #{approver,jdbcType=VARCHAR}, " +
            "UPDATE_BY = #{updateBy,jdbcType=VARCHAR}, " +
            "UPDATE_TIME = #{updateTime,jdbcType=TIMESTAMP}, " +
            "REMARKS = #{remarks,jdbcType=VARCHAR}, " +
            "DATA_JSON = #{dataJson,jdbcType=CLOB}, " +
            "RECORD_TESTER_SIGN = #{inspectSignaturePhoto,jdbcType=CLOB}, " +
            "RECORD_REVIEW_SIGN = #{reviewSignaturePhoto,jdbcType=CLOB}, " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto,jdbcType=CLOB} " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int update(LightDynamicPenetrationResult result);

    @Select("SELECT " +
            "ID as id, " +
            "ENTRUSTMENT_ID as entrustmentId, " +
            "DATA_JSON as dataJson, " +
            "STATUS as status, " +
            "REJECT_REASON as rejectReason, " +
            "NEXT_HANDLER as nextHandler, " +
            "RECORD_TESTER_SIGN as inspectSignaturePhoto, " +
            "RECORD_REVIEW_SIGN as reviewSignaturePhoto, " +
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
    LightDynamicPenetrationResult selectById(@Param("id") String id);

    @Update("UPDATE T_LIGHT_DYNAMIC_PENETRATION SET " +
            "ENTRUSTMENT_ID = #{entrustmentId,jdbcType=VARCHAR}, " +
            "STATUS = #{status,jdbcType=VARCHAR}, " +
            "REJECT_REASON = #{rejectReason,jdbcType=VARCHAR}, " +
            "NEXT_HANDLER = #{nextHandler,jdbcType=VARCHAR}, " +
            "RECORD_TESTER = NVL(#{recordTester,jdbcType=VARCHAR}, #{tester,jdbcType=VARCHAR}), " +
            "RECORD_REVIEWER = NVL(#{recordReviewer,jdbcType=VARCHAR}, #{reviewer,jdbcType=VARCHAR}), " +
            "APPROVER = #{approver,jdbcType=VARCHAR}, " +
            "UPDATE_BY = #{updateBy,jdbcType=VARCHAR}, " +
            "UPDATE_TIME = #{updateTime,jdbcType=TIMESTAMP}, " +
            "REMARKS = #{remarks,jdbcType=VARCHAR}, " +
            "DATA_JSON = #{dataJson,jdbcType=CLOB}, " +
            "RECORD_TESTER_SIGN = #{inspectSignaturePhoto,jdbcType=CLOB}, " +
            "RECORD_REVIEW_SIGN = #{reviewSignaturePhoto,jdbcType=CLOB}, " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto,jdbcType=CLOB} " +
            "WHERE ID = #{id,jdbcType=VARCHAR}")
    int updateById(LightDynamicPenetrationResult result);
}
