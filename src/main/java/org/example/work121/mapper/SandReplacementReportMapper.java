package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.SandReplacementReport;

@Mapper
public interface SandReplacementReportMapper {

    @Delete("DELETE FROM T_SAND_REPLACEMENT WHERE ENTRUSTMENT_ID = #{entrustmentId}")
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
            "FROM T_SAND_REPLACEMENT " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    SandReplacementReport selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Insert("INSERT INTO T_SAND_REPLACEMENT (ID, ENTRUSTMENT_ID, DATA_JSON, RECORD_REVIEW_SIGN, RECORD_TESTER_SIGN, APPROVE_SIGNATURE_PHOTO, RECORD_TESTER, RECORD_REVIEWER, APPROVER, CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME) " +
            "VALUES (#{id}, #{entrustmentId}, #{dataJson}, #{reviewSignaturePhoto}, #{inspectSignaturePhoto}, #{approveSignaturePhoto}, #{tester}, #{reviewer}, #{approver}, #{createBy}, #{createTime}, #{updateBy}, #{updateTime})")
    int insert(SandReplacementReport report);

    @Update("UPDATE T_SAND_REPLACEMENT SET " +
            "DATA_JSON = #{dataJson}, " +
            "RECORD_REVIEW_SIGN = #{reviewSignaturePhoto}, " +
            "RECORD_TESTER_SIGN = #{inspectSignaturePhoto}, " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto}, " +
            "RECORD_TESTER = #{tester}, " +
            "RECORD_REVIEWER = #{reviewer}, " +
            "APPROVER = #{approver}, " +
            "UPDATE_BY = #{updateBy}, " +
            "UPDATE_TIME = #{updateTime} " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int update(SandReplacementReport report);

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
            "FROM T_SAND_REPLACEMENT " +
            "WHERE ID = #{id}")
    SandReplacementReport selectById(@Param("id") String id);

    @Update("UPDATE T_SAND_REPLACEMENT SET " +
            "ENTRUSTMENT_ID = #{entrustmentId}, " +
            "DATA_JSON = #{dataJson}, " +
            "RECORD_REVIEW_SIGN = #{reviewSignaturePhoto}, " +
            "RECORD_TESTER_SIGN = #{inspectSignaturePhoto}, " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto}, " +
            "RECORD_TESTER = #{tester}, " +
            "RECORD_REVIEWER = #{reviewer}, " +
            "APPROVER = #{approver}, " +
            "UPDATE_BY = #{updateBy}, " +
            "UPDATE_TIME = #{updateTime} " +
            "WHERE ID = #{id}")
    int updateById(SandReplacementReport report);
}
