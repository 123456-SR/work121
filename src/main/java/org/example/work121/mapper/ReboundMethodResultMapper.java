package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.ReboundMethodResult;

@Mapper
public interface ReboundMethodResultMapper {

    @Delete("DELETE FROM T_REBOUND_METHOD WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int deleteByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Select("SELECT " +
            "ID as id, " +
            "ENTRUSTMENT_ID as entrustmentId, " +
            "DATA_JSON as dataJson, " +
            "REVIEW_SIGNATURE_PHOTO as reviewSignaturePhoto, " +
            "INSPECT_SIGNATURE_PHOTO as inspectSignaturePhoto, " +
            "APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto, " +
            "TESTER as tester, " +
            "REVIEWER as reviewer, " +
            "APPROVER as approver, " +
            "CREATE_BY as createBy, " +
            "CREATE_TIME as createTime, " +
            "UPDATE_BY as updateBy, " +
            "UPDATE_TIME as updateTime " +
            "FROM T_REBOUND_METHOD " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    ReboundMethodResult selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Insert("INSERT INTO T_REBOUND_METHOD (ID, ENTRUSTMENT_ID, DATA_JSON, REVIEW_SIGNATURE_PHOTO, INSPECT_SIGNATURE_PHOTO, APPROVE_SIGNATURE_PHOTO, TESTER, REVIEWER, APPROVER, CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME) " +
            "VALUES (#{id}, #{entrustmentId}, #{dataJson,jdbcType=CLOB}, #{reviewSignaturePhoto,jdbcType=CLOB}, #{inspectSignaturePhoto,jdbcType=CLOB}, #{approveSignaturePhoto,jdbcType=CLOB}, #{tester,jdbcType=VARCHAR}, #{reviewer,jdbcType=VARCHAR}, #{approver,jdbcType=VARCHAR}, #{createBy,jdbcType=VARCHAR}, #{createTime,jdbcType=TIMESTAMP}, #{updateBy,jdbcType=VARCHAR}, #{updateTime,jdbcType=TIMESTAMP})")
    int insert(ReboundMethodResult result);

    @Update("UPDATE T_REBOUND_METHOD SET " +
            "DATA_JSON = #{dataJson,jdbcType=CLOB}, " +
            "REVIEW_SIGNATURE_PHOTO = #{reviewSignaturePhoto,jdbcType=CLOB}, " +
            "INSPECT_SIGNATURE_PHOTO = #{inspectSignaturePhoto,jdbcType=CLOB}, " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto,jdbcType=CLOB}, " +
            "TESTER = #{tester,jdbcType=VARCHAR}, " +
            "REVIEWER = #{reviewer,jdbcType=VARCHAR}, " +
            "APPROVER = #{approver,jdbcType=VARCHAR}, " +
            "UPDATE_BY = #{updateBy,jdbcType=VARCHAR}, " +
            "UPDATE_TIME = #{updateTime,jdbcType=TIMESTAMP} " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int update(ReboundMethodResult result);

    @Select("SELECT " +
            "ID as id, " +
            "ENTRUSTMENT_ID as entrustmentId, " +
            "DATA_JSON as dataJson, " +
            "REVIEW_SIGNATURE_PHOTO as reviewSignaturePhoto, " +
            "INSPECT_SIGNATURE_PHOTO as inspectSignaturePhoto, " +
            "APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto, " +
            "TESTER as tester, " +
            "REVIEWER as reviewer, " +
            "APPROVER as approver, " +
            "CREATE_BY as createBy, " +
            "CREATE_TIME as createTime, " +
            "UPDATE_BY as updateBy, " +
            "UPDATE_TIME as updateTime " +
            "FROM T_REBOUND_METHOD " +
            "WHERE ID = #{id}")
    ReboundMethodResult selectById(@Param("id") String id);

    @Update("UPDATE T_REBOUND_METHOD SET " +
            "ENTRUSTMENT_ID = #{entrustmentId}, " +
            "DATA_JSON = #{dataJson}, " +
            "REVIEW_SIGNATURE_PHOTO = #{reviewSignaturePhoto}, " +
            "INSPECT_SIGNATURE_PHOTO = #{inspectSignaturePhoto}, " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto}, " +
            "TESTER = #{tester}, " +
            "REVIEWER = #{reviewer}, " +
            "APPROVER = #{approver}, " +
            "UPDATE_BY = #{updateBy}, " +
            "UPDATE_TIME = #{updateTime} " +
            "WHERE ID = #{id}")
    int updateById(ReboundMethodResult result);
}
