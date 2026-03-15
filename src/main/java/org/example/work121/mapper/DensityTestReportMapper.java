package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.DensityTestReport;
import java.util.List;

@Mapper
public interface DensityTestReportMapper {

    @Delete("DELETE FROM T_DENSITY_TEST WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int deleteByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Select("SELECT " +
            "t1.ID as id, " +
            "t1.ENTRUSTMENT_ID as entrustmentId, " +
            "t1.DATA_JSON as dataJson, " +
            "t1.RECORD_REVIEW_SIGN as reviewSignaturePhoto, " +
            "t1.RECORD_TESTER_SIGN as inspectSignaturePhoto, " +
            "t1.APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto, " +
            "t1.RECORD_TESTER as tester, " +
            "t1.RECORD_REVIEWER as reviewer, " +
            "t1.APPROVER as approver, " +
            "t1.STATUS as status, " +
            "t1.REJECT_REASON as rejectReason, " +
            "t1.NEXT_HANDLER as nextHandler, " +
            "t1.CREATE_BY as createBy, " +
            "t1.CREATE_TIME as createTime, " +
            "t1.UPDATE_BY as updateBy, " +
            "t1.UPDATE_TIME as updateTime, " +
            "t2.PROJECT_NAME as projectName, " +
            "t2.COMMISSION_DATE as commissionDate, " +
            "t2.CONSTRUCTION_PART as constructionPart, " +
            "t2.TEST_CATEGORY as testCategory, " +
            "t2.CLIENT_UNIT as clientUnit " +
            "FROM T_DENSITY_TEST t1 " +
            "LEFT JOIN T_ENTRUSTMENT t2 ON t1.ENTRUSTMENT_ID = t2.WT_NUM " +
            "WHERE t1.ENTRUSTMENT_ID = #{entrustmentId}")
    List<DensityTestReport> selectListByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Select("SELECT " +
            "t1.ID as id, " +
            "t1.ENTRUSTMENT_ID as entrustmentId, " +
            "t1.DATA_JSON as dataJson, " +
            "t1.RECORD_REVIEW_SIGN as reviewSignaturePhoto, " +
            "t1.RECORD_TESTER_SIGN as inspectSignaturePhoto, " +
            "t1.APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto, " +
            "t1.RECORD_TESTER as tester, " +
            "t1.RECORD_REVIEWER as reviewer, " +
            "t1.APPROVER as approver, " +
            "t1.STATUS as status, " +
            "t1.REJECT_REASON as rejectReason, " +
            "t1.NEXT_HANDLER as nextHandler, " +
            "t1.CREATE_BY as createBy, " +
            "t1.CREATE_TIME as createTime, " +
            "t1.UPDATE_BY as updateBy, " +
            "t1.UPDATE_TIME as updateTime, " +
            "t2.PROJECT_NAME as projectName, " +
            "t2.COMMISSION_DATE as commissionDate, " +
            "t2.CONSTRUCTION_PART as constructionPart, " +
            "t2.TEST_CATEGORY as testCategory, " +
            "t2.CLIENT_UNIT as clientUnit " +
            "FROM T_DENSITY_TEST t1 " +
            "LEFT JOIN T_ENTRUSTMENT t2 ON t1.ENTRUSTMENT_ID = t2.WT_NUM " +
            "WHERE t1.ENTRUSTMENT_ID = #{entrustmentId} " +
            "AND ROWNUM <= 1")
    DensityTestReport selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Insert("INSERT INTO T_DENSITY_TEST (ID, ENTRUSTMENT_ID, RECORD_TESTER, RECORD_REVIEWER, APPROVER, CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME, DATA_JSON, RECORD_REVIEW_SIGN, RECORD_TESTER_SIGN, APPROVE_SIGNATURE_PHOTO) " +
            "VALUES (#{id,jdbcType=VARCHAR}, #{entrustmentId,jdbcType=VARCHAR}, #{tester,jdbcType=VARCHAR}, #{reviewer,jdbcType=VARCHAR}, #{approver,jdbcType=VARCHAR}, #{createBy,jdbcType=VARCHAR}, #{createTime,jdbcType=TIMESTAMP}, #{updateBy,jdbcType=VARCHAR}, #{updateTime,jdbcType=TIMESTAMP}, #{dataJson,jdbcType=CLOB}, NVL(#{recordReviewSign,jdbcType=CLOB}, #{reviewSignaturePhoto,jdbcType=CLOB}), NVL(#{recordTesterSign,jdbcType=CLOB}, #{inspectSignaturePhoto,jdbcType=CLOB}), #{approveSignaturePhoto,jdbcType=CLOB})")
    int insert(DensityTestReport report);

    @Update("UPDATE (SELECT * FROM T_DENSITY_TEST WHERE ENTRUSTMENT_ID = #{entrustmentId,jdbcType=VARCHAR}) SET " +
            "RECORD_TESTER = #{tester,jdbcType=VARCHAR}, " +
            "RECORD_REVIEWER = #{reviewer,jdbcType=VARCHAR}, " +
            "APPROVER = #{approver,jdbcType=VARCHAR}, " +
            "UPDATE_BY = #{updateBy,jdbcType=VARCHAR}, " +
            "UPDATE_TIME = #{updateTime,jdbcType=TIMESTAMP}, " +
            "DATA_JSON = #{dataJson,jdbcType=CLOB}, " +
            "RECORD_REVIEW_SIGN = NVL(#{recordReviewSign,jdbcType=CLOB}, #{reviewSignaturePhoto,jdbcType=CLOB}), " +
            "RECORD_TESTER_SIGN = NVL(#{recordTesterSign,jdbcType=CLOB}, #{inspectSignaturePhoto,jdbcType=CLOB}), " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto,jdbcType=CLOB}")
    int update(DensityTestReport report);

    @Select("SELECT " +
            "t1.ID as id, " +
            "t1.ENTRUSTMENT_ID as entrustmentId, " +
            "t1.DATA_JSON as dataJson, " +
            "t1.RECORD_REVIEW_SIGN as reviewSignaturePhoto, " +
            "t1.RECORD_TESTER_SIGN as inspectSignaturePhoto, " +
            "t1.APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto, " +
            "t1.RECORD_TESTER as tester, " +
            "t1.RECORD_REVIEWER as reviewer, " +
            "t1.APPROVER as approver, " +
            "t1.STATUS as status, " +
            "t1.REJECT_REASON as rejectReason, " +
            "t1.NEXT_HANDLER as nextHandler, " +
            "t1.CREATE_BY as createBy, " +
            "t1.CREATE_TIME as createTime, " +
            "t1.UPDATE_BY as updateBy, " +
            "t1.UPDATE_TIME as updateTime, " +
            "t2.PROJECT_NAME as projectName, " +
            "t2.COMMISSION_DATE as commissionDate, " +
            "t2.CONSTRUCTION_PART as constructionPart, " +
            "t2.TEST_CATEGORY as testCategory, " +
            "t2.CLIENT_UNIT as clientUnit " +
            "FROM T_DENSITY_TEST t1 " +
            "LEFT JOIN T_ENTRUSTMENT t2 ON t1.ENTRUSTMENT_ID = t2.WT_NUM " +
            "WHERE t1.ID = #{id}")
    DensityTestReport selectById(@Param("id") String id);

    @Update("UPDATE (SELECT * FROM T_DENSITY_TEST WHERE ID = #{id,jdbcType=VARCHAR}) SET " +
            "ENTRUSTMENT_ID = #{entrustmentId,jdbcType=VARCHAR}, " +
            "RECORD_TESTER = #{tester,jdbcType=VARCHAR}, " +
            "RECORD_REVIEWER = #{reviewer,jdbcType=VARCHAR}, " +
            "APPROVER = #{approver,jdbcType=VARCHAR}, " +
            "UPDATE_BY = #{updateBy,jdbcType=VARCHAR}, " +
            "UPDATE_TIME = #{updateTime,jdbcType=TIMESTAMP}, " +
            "DATA_JSON = #{dataJson,jdbcType=CLOB}, " +
            "RECORD_REVIEW_SIGN = NVL(#{recordReviewSign,jdbcType=CLOB}, #{reviewSignaturePhoto,jdbcType=CLOB}), " +
            "RECORD_TESTER_SIGN = NVL(#{recordTesterSign,jdbcType=CLOB}, #{inspectSignaturePhoto,jdbcType=CLOB}), " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto,jdbcType=CLOB}")
    int updateById(DensityTestReport report);
}
