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
            "INSPECT_SIGNATURE_PHOTO as inspectSignaturePhoto, " +
            "REVIEW_SIGNATURE_PHOTO as reviewSignaturePhoto, " +
            "APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto, " +
            "TESTER as tester, " +
            "REVIEWER as reviewer, " +
            "APPROVER as approver, " +
            "CREATE_BY as createBy, " +
            "CREATE_TIME as createTime, " +
            "UPDATE_BY as updateBy, " +
            "UPDATE_TIME as updateTime, " +
            "REMARKS as remarks " +
            "FROM T_LIGHT_DYNAMIC_PENETRATION " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    List<LightDynamicPenetrationResult> selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Insert("INSERT INTO T_LIGHT_DYNAMIC_PENETRATION (ID, ENTRUSTMENT_ID, DATA_JSON, STATUS, REJECT_REASON, NEXT_HANDLER, INSPECT_SIGNATURE_PHOTO, REVIEW_SIGNATURE_PHOTO, APPROVE_SIGNATURE_PHOTO, TESTER, REVIEWER, APPROVER, CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME, REMARKS) " +
            "VALUES (#{id}, #{entrustmentId}, #{dataJson,jdbcType=CLOB}, #{status,jdbcType=VARCHAR}, #{rejectReason,jdbcType=VARCHAR}, #{nextHandler,jdbcType=VARCHAR}, #{inspectSignaturePhoto,jdbcType=CLOB}, #{reviewSignaturePhoto,jdbcType=CLOB}, #{approveSignaturePhoto,jdbcType=CLOB}, #{tester,jdbcType=VARCHAR}, #{reviewer,jdbcType=VARCHAR}, #{approver,jdbcType=VARCHAR}, #{createBy,jdbcType=VARCHAR}, #{createTime,jdbcType=TIMESTAMP}, #{updateBy,jdbcType=VARCHAR}, #{updateTime,jdbcType=TIMESTAMP}, #{remarks,jdbcType=VARCHAR})")
    int insert(LightDynamicPenetrationResult result);

    @Update("UPDATE T_LIGHT_DYNAMIC_PENETRATION SET " +
            "DATA_JSON = #{dataJson,jdbcType=CLOB}, " +
            "STATUS = #{status,jdbcType=VARCHAR}, " +
            "REJECT_REASON = #{rejectReason,jdbcType=VARCHAR}, " +
            "NEXT_HANDLER = #{nextHandler,jdbcType=VARCHAR}, " +
            "INSPECT_SIGNATURE_PHOTO = #{inspectSignaturePhoto,jdbcType=CLOB}, " +
            "REVIEW_SIGNATURE_PHOTO = #{reviewSignaturePhoto,jdbcType=CLOB}, " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto,jdbcType=CLOB}, " +
            "TESTER = #{tester,jdbcType=VARCHAR}, " +
            "REVIEWER = #{reviewer,jdbcType=VARCHAR}, " +
            "APPROVER = #{approver,jdbcType=VARCHAR}, " +
            "UPDATE_BY = #{updateBy,jdbcType=VARCHAR}, " +
            "UPDATE_TIME = #{updateTime,jdbcType=TIMESTAMP}, " +
            "REMARKS = #{remarks,jdbcType=VARCHAR} " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int update(LightDynamicPenetrationResult result);

    @Select("SELECT " +
            "ID as id, " +
            "ENTRUSTMENT_ID as entrustmentId, " +
            "DATA_JSON as dataJson, " +
            "STATUS as status, " +
            "REJECT_REASON as rejectReason, " +
            "NEXT_HANDLER as nextHandler, " +
            "INSPECT_SIGNATURE_PHOTO as inspectSignaturePhoto, " +
            "REVIEW_SIGNATURE_PHOTO as reviewSignaturePhoto, " +
            "APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto, " +
            "TESTER as tester, " +
            "REVIEWER as reviewer, " +
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
            "ENTRUSTMENT_ID = #{entrustmentId}, " +
            "DATA_JSON = #{dataJson,jdbcType=CLOB}, " +
            "STATUS = #{status}, " +
            "REJECT_REASON = #{rejectReason}, " +
            "NEXT_HANDLER = #{nextHandler}, " +
            "INSPECT_SIGNATURE_PHOTO = #{inspectSignaturePhoto}, " +
            "REVIEW_SIGNATURE_PHOTO = #{reviewSignaturePhoto}, " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto}, " +
            "TESTER = #{tester}, " +
            "REVIEWER = #{reviewer}, " +
            "APPROVER = #{approver}, " +
            "UPDATE_BY = #{updateBy}, " +
            "UPDATE_TIME = #{updateTime}, " +
            "REMARKS = #{remarks,jdbcType=VARCHAR} " +
            "WHERE ID = #{id}")
    int updateById(LightDynamicPenetrationResult result);
}
