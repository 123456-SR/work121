package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.LightDynamicPenetration;
import java.util.List;

@Mapper
public interface LightDynamicPenetrationMapper {

    @Select("SELECT " +
            "t1.ID as id, " +
            "t1.ENTRUSTMENT_ID as entrustmentId, " +
            "t1.DATA_JSON as dataJson, " +
            "t1.STATUS as status, " +
            "t1.REJECT_REASON as rejectReason, " +
            "t1.NEXT_HANDLER as nextHandler, " +
            "t1.INSPECT_SIGNATURE_PHOTO as inspectSignaturePhoto, " +
            "t1.REVIEW_SIGNATURE_PHOTO as reviewSignaturePhoto, " +
            "t1.APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto, " +
            "t1.CREATE_BY as createBy, " +
            "t1.CREATE_TIME as createTime, " +
            "t1.UPDATE_BY as updateBy, " +
            "t1.UPDATE_TIME as updateTime, " +
            "t1.TESTER as tester, " +
            "t1.REVIEWER as reviewer, " +
            "t1.APPROVER as approver, " +
            "t2.WT_NUM as wtNum " +
            "FROM JZS_LIGHT_DYNAMIC_PENETRATION t1 " +
            "LEFT JOIN JC_CORE_WT_INFO t2 ON t1.ENTRUSTMENT_ID = t2.WT_ID " +
            "WHERE t1.ENTRUSTMENT_ID = #{entrustmentId}")
    List<LightDynamicPenetration> selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Select("SELECT " +
            "t1.ID as id, " +
            "t1.ENTRUSTMENT_ID as entrustmentId, " +
            "t1.DATA_JSON as dataJson, " +
            "t1.STATUS as status, " +
            "t1.REJECT_REASON as rejectReason, " +
            "t1.NEXT_HANDLER as nextHandler, " +
            "t1.INSPECT_SIGNATURE_PHOTO as inspectSignaturePhoto, " +
            "t1.REVIEW_SIGNATURE_PHOTO as reviewSignaturePhoto, " +
            "t1.APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto, " +
            "t1.CREATE_BY as createBy, " +
            "t1.CREATE_TIME as createTime, " +
            "t1.UPDATE_BY as updateBy, " +
            "t1.UPDATE_TIME as updateTime, " +
            "t1.TESTER as tester, " +
            "t1.REVIEWER as reviewer, " +
            "t1.APPROVER as approver, " +
            "t2.WT_NUM as wtNum " +
            "FROM JZS_LIGHT_DYNAMIC_PENETRATION t1 " +
            "LEFT JOIN JC_CORE_WT_INFO t2 ON t1.ENTRUSTMENT_ID = t2.WT_ID " +
            "WHERE t1.ID = #{id}")
    LightDynamicPenetration selectById(@Param("id") String id);

    @Insert("INSERT INTO JZS_LIGHT_DYNAMIC_PENETRATION " +
            "(ID, ENTRUSTMENT_ID, DATA_JSON, STATUS, REJECT_REASON, NEXT_HANDLER, " +
            "INSPECT_SIGNATURE_PHOTO, REVIEW_SIGNATURE_PHOTO, APPROVE_SIGNATURE_PHOTO, " +
            "CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME, " +
            "TESTER, REVIEWER, APPROVER) " +
            "VALUES " +
            "(#{id}, #{entrustmentId}, #{dataJson,jdbcType=CLOB}, #{status,jdbcType=VARCHAR}, #{rejectReason,jdbcType=VARCHAR}, #{nextHandler,jdbcType=VARCHAR}, " +
            "#{inspectSignaturePhoto,jdbcType=CLOB}, #{reviewSignaturePhoto,jdbcType=CLOB}, #{approveSignaturePhoto,jdbcType=CLOB}, " +
            "#{createBy,jdbcType=VARCHAR}, #{createTime,jdbcType=TIMESTAMP}, #{updateBy,jdbcType=VARCHAR}, #{updateTime,jdbcType=TIMESTAMP}, " +
            "#{tester,jdbcType=VARCHAR}, #{reviewer,jdbcType=VARCHAR}, #{approver,jdbcType=VARCHAR})")
    int insert(LightDynamicPenetration entity);

    @Update("UPDATE JZS_LIGHT_DYNAMIC_PENETRATION SET " +
            "ENTRUSTMENT_ID = #{entrustmentId}, " +
            "DATA_JSON = #{dataJson,jdbcType=CLOB}, " +
            "STATUS = #{status,jdbcType=VARCHAR}, " +
            "REJECT_REASON = #{rejectReason,jdbcType=VARCHAR}, " +
            "NEXT_HANDLER = #{nextHandler,jdbcType=VARCHAR}, " +
            "INSPECT_SIGNATURE_PHOTO = #{inspectSignaturePhoto,jdbcType=CLOB}, " +
            "REVIEW_SIGNATURE_PHOTO = #{reviewSignaturePhoto,jdbcType=CLOB}, " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto,jdbcType=CLOB}, " +
            "CREATE_BY = #{createBy,jdbcType=VARCHAR}, " +
            "CREATE_TIME = #{createTime,jdbcType=TIMESTAMP}, " +
            "UPDATE_BY = #{updateBy,jdbcType=VARCHAR}, " +
            "UPDATE_TIME = #{updateTime,jdbcType=TIMESTAMP}, " +
            "TESTER = #{tester,jdbcType=VARCHAR}, " +
            "REVIEWER = #{reviewer,jdbcType=VARCHAR}, " +
            "APPROVER = #{approver,jdbcType=VARCHAR} " +
            "WHERE ID = #{id}")
    int update(LightDynamicPenetration entity);

    @Select("SELECT COUNT(*) FROM JZS_LIGHT_DYNAMIC_PENETRATION WHERE ID = #{id}")
    int countById(@Param("id") String id);

    @Delete("DELETE FROM JZS_LIGHT_DYNAMIC_PENETRATION WHERE ID = #{id}")
    int deleteById(@Param("id") String id);
}
