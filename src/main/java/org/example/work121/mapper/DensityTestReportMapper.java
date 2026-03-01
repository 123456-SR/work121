package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.DensityTestReport;

@Mapper
public interface DensityTestReportMapper {

    @Delete("DELETE FROM T_DENSITY_TEST WHERE ENTRUSTMENT_ID = #{entrustmentId}")
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
            "STATUS as status, " +
            "REJECT_REASON as rejectReason, " +
            "NEXT_HANDLER as nextHandler, " +
            "CREATE_BY as createBy, " +
            "CREATE_TIME as createTime, " +
            "UPDATE_BY as updateBy, " +
            "UPDATE_TIME as updateTime " +
            "FROM T_DENSITY_TEST " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    DensityTestReport selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Insert("INSERT INTO T_DENSITY_TEST (ID, ENTRUSTMENT_ID, DATA_JSON, REVIEW_SIGNATURE_PHOTO, INSPECT_SIGNATURE_PHOTO, APPROVE_SIGNATURE_PHOTO, TESTER, REVIEWER, APPROVER, CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME) " +
            "VALUES (#{id}, #{entrustmentId}, #{dataJson}, #{reviewSignaturePhoto}, #{inspectSignaturePhoto}, #{approveSignaturePhoto}, #{tester}, #{reviewer}, #{approver}, #{createBy}, #{createTime}, #{updateBy}, #{updateTime})")
    int insert(DensityTestReport report);

    @Update("UPDATE T_DENSITY_TEST SET " +
            "DATA_JSON = #{dataJson}, " +
            "REVIEW_SIGNATURE_PHOTO = #{reviewSignaturePhoto}, " +
            "INSPECT_SIGNATURE_PHOTO = #{inspectSignaturePhoto}, " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto}, " +
            "TESTER = #{tester}, " +
            "REVIEWER = #{reviewer}, " +
            "APPROVER = #{approver}, " +
            "UPDATE_BY = #{updateBy}, " +
            "UPDATE_TIME = #{updateTime} " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int update(DensityTestReport report);

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
            "STATUS as status, " +
            "REJECT_REASON as rejectReason, " +
            "NEXT_HANDLER as nextHandler, " +
            "CREATE_BY as createBy, " +
            "CREATE_TIME as createTime, " +
            "UPDATE_BY as updateBy, " +
            "UPDATE_TIME as updateTime " +
            "FROM T_DENSITY_TEST " +
            "WHERE ID = #{id}")
    DensityTestReport selectById(@Param("id") String id);

    @Update("UPDATE T_DENSITY_TEST SET " +
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
    int updateById(DensityTestReport report);
}
