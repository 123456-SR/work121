package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.NuclearDensity;
import java.util.List;

import java.util.List;

@Mapper
public interface NuclearDensityMapper {

    @Delete("DELETE FROM T_NUCLEAR_DENSITY WHERE ID = #{id}")
    int deleteById(@Param("id") String id);

    @Select("SELECT " +
            "t2.ID as id, " +
            "t2.ENTRUSTMENT_ID as entrustmentId, " +
            "t2.DATA_JSON as dataJson, " +
            "t2.RECORD_TESTER as tester, " +
            "t2.RECORD_REVIEWER as reviewer, " +
            "t2.APPROVER as approver, " +
            "t2.FILLER as filler, " +
            "t2.RECORD_TESTER as recordTester, " +
            "t2.RECORD_REVIEWER as recordReviewer, " +
            "t2.RECORD_TESTER_SIGN as recordTesterSign, " +
            "t2.RECORD_REVIEW_SIGN as recordReviewSign, " +
            "t2.RECORD_TESTER_SIGN as inspectSignaturePhoto, " +
            "t2.RECORD_REVIEW_SIGN as reviewSignaturePhoto, " +
            "t2.APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto, " +
            "t2.STATUS as status, " +
            "t2.REJECT_REASON as rejectReason, " +
            "t2.NEXT_HANDLER as nextHandler, " +
            "t2.UPDATE_BY as updateBy, " +
            "t2.UPDATE_TIME as updateTime, " +
            "t1.SAMPLE_NUMBER as wtNum, " +
            "t1.CLIENT_UNIT as clientUnit, " +
            "t1.COMMISSION_DATE as commissionDate, " +
            "t1.PROJECT_NAME as projectName, " +
            "t1.CONSTRUCTION_PART as constructionPart, " +
            "t1.CONSTRUCTION_UNIT as constructionUnit, " +
            "t1.BUILDING_UNIT as buildingUnit, " +
            "t1.SAMPLE_NAME as sampleName, " +
            "t1.TEST_CATEGORY as testCategory, " +
            "t1.WITNESS_UNIT as witnessUnit, " +
            "t1.WITNESS as witness, " +
            "t1.BEIZHU as remarks " +
            "FROM T_NUCLEAR_DENSITY t2 " +
            "LEFT JOIN T_ENTRUSTMENT t1 ON t2.ENTRUSTMENT_ID = t1.WT_NUM " +
            "WHERE t2.ID = #{id}")
    NuclearDensity selectById(@Param("id") String id);

    @Update("UPDATE (SELECT * FROM T_NUCLEAR_DENSITY WHERE ID = #{id}) SET " +
            "ENTRUSTMENT_ID = #{entrustmentId,jdbcType=VARCHAR}, " +
            "APPROVER = #{approver,jdbcType=VARCHAR}, " +
            "FILLER = #{filler,jdbcType=VARCHAR}, " +
            "RECORD_TESTER = NVL(#{recordTester,jdbcType=VARCHAR}, #{tester,jdbcType=VARCHAR}), " +
            "RECORD_REVIEWER = NVL(#{recordReviewer,jdbcType=VARCHAR}, #{reviewer,jdbcType=VARCHAR}), " +
            "STATUS = TO_CHAR(#{status,jdbcType=INTEGER}), " +
            "REJECT_REASON = #{rejectReason,jdbcType=VARCHAR}, " +
            "NEXT_HANDLER = #{nextHandler,jdbcType=VARCHAR}, " +
            "UPDATE_BY = #{updateBy,jdbcType=VARCHAR}, " +
            "UPDATE_TIME = #{updateTime,jdbcType=TIMESTAMP}, " +
            "RECORD_REVIEW_SIGN = NVL(#{recordReviewSign,jdbcType=CLOB}, #{reviewSignaturePhoto,jdbcType=CLOB}), " +
            "RECORD_TESTER_SIGN = NVL(#{recordTesterSign,jdbcType=CLOB}, #{inspectSignaturePhoto,jdbcType=CLOB}), " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto,jdbcType=CLOB}, " +
            "DATA_JSON = #{dataJson,jdbcType=CLOB}")
    int updateById(NuclearDensity nuclearDensity);

    @Select("SELECT " +
            "t2.ID as id, " +
            "t2.ENTRUSTMENT_ID as entrustmentId, " +
            "t2.DATA_JSON as dataJson, " +
            "t2.RECORD_TESTER as tester, " +
            "t2.RECORD_REVIEWER as reviewer, " +
            "t2.APPROVER as approver, " +
            "t2.FILLER as filler, " +
            "t2.RECORD_TESTER as recordTester, " +
            "t2.RECORD_REVIEWER as recordReviewer, " +
            "t2.RECORD_TESTER_SIGN as recordTesterSign, " +
            "t2.RECORD_REVIEW_SIGN as recordReviewSign, " +
            "t2.RECORD_REVIEW_SIGN as reviewSignaturePhoto, " +
            "t2.RECORD_TESTER_SIGN as inspectSignaturePhoto, " +
            "t2.APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto, " +
            "TO_NUMBER(t2.STATUS) as status, " +
            "t2.REJECT_REASON as rejectReason, " +
            "t2.NEXT_HANDLER as nextHandler, " +
            "t2.CREATE_BY as createBy, " +
            "t2.CREATE_TIME as createTime, " +
            "t2.UPDATE_BY as updateBy, " +
            "t2.UPDATE_TIME as updateTime, " +
            "t1.SAMPLE_NUMBER as wtNum, " +
            "t1.CLIENT_UNIT as clientUnit, " +
            "t1.COMMISSION_DATE as commissionDate, " +
            "t1.PROJECT_NAME as projectName, " +
            "t1.CONSTRUCTION_PART as constructionPart, " +
            "t1.CONSTRUCTION_UNIT as constructionUnit, " +
            "t1.BUILDING_UNIT as buildingUnit, " +
            "t1.SAMPLE_NAME as sampleName, " +
            "t1.TEST_CATEGORY as testCategory, " +
            "t1.WITNESS_UNIT as witnessUnit, " +
            "t1.WITNESS as witness, " +
            "t1.BEIZHU as remarks " +
            "FROM T_NUCLEAR_DENSITY t2 " +
            "LEFT JOIN T_ENTRUSTMENT t1 ON t2.ENTRUSTMENT_ID = t1.WT_NUM " +
            "WHERE t2.ENTRUSTMENT_ID = #{entrustmentId}")
    List<NuclearDensity> selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Insert("INSERT INTO T_NUCLEAR_DENSITY (" +
            "ID, ENTRUSTMENT_ID, " +
            "RECORD_TESTER, RECORD_REVIEWER, APPROVER, " +
            "FILLER, " +
            "STATUS, REJECT_REASON, NEXT_HANDLER, " +
            "CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME, " +
            "RECORD_REVIEW_SIGN, RECORD_TESTER_SIGN, APPROVE_SIGNATURE_PHOTO, DATA_JSON) " +
            "VALUES (" +
            "#{id}, #{entrustmentId}, " +
            "NVL(#{recordTester,jdbcType=VARCHAR}, #{tester,jdbcType=VARCHAR}), NVL(#{recordReviewer,jdbcType=VARCHAR}, #{reviewer,jdbcType=VARCHAR}), #{approver,jdbcType=VARCHAR}, " +
            "#{filler,jdbcType=VARCHAR}, " +
            "TO_CHAR(#{status,jdbcType=INTEGER}), #{rejectReason,jdbcType=VARCHAR}, #{nextHandler,jdbcType=VARCHAR}, " +
            "#{createBy,jdbcType=VARCHAR}, #{createTime,jdbcType=TIMESTAMP}, #{updateBy,jdbcType=VARCHAR}, #{updateTime,jdbcType=TIMESTAMP}, " +
            "NVL(#{recordReviewSign,jdbcType=CLOB}, #{reviewSignaturePhoto,jdbcType=CLOB}), NVL(#{recordTesterSign,jdbcType=CLOB}, #{inspectSignaturePhoto,jdbcType=CLOB}), #{approveSignaturePhoto,jdbcType=CLOB}, #{dataJson,jdbcType=CLOB})")
    int insert(NuclearDensity nuclearDensity);

    @Update("UPDATE T_NUCLEAR_DENSITY SET STATUS = TO_CHAR(#{status,jdbcType=INTEGER}) WHERE ID = #{id}")
    int updateStatusById(@Param("id") String id, @Param("status") String status);

    @Update("UPDATE (SELECT * FROM T_NUCLEAR_DENSITY WHERE ID = #{id}) SET STATUS = TO_CHAR(#{status,jdbcType=INTEGER}), RECORD_REVIEW_SIGN = #{reviewSignPhoto,jdbcType=CLOB}")
    int updateStatusAndReviewSign(@Param("id") String id, @Param("status") String status, @Param("reviewSignPhoto") String reviewSignPhoto);

    @Update("UPDATE (SELECT * FROM T_NUCLEAR_DENSITY WHERE ID = #{id}) SET STATUS = TO_CHAR(#{status,jdbcType=INTEGER}), APPROVE_SIGNATURE_PHOTO = #{approveSignPhoto,jdbcType=CLOB}")
    int updateStatusAndApproveSign(@Param("id") String id, @Param("status") String status, @Param("approveSignPhoto") String approveSignPhoto);

    @Update("UPDATE T_NUCLEAR_DENSITY SET REPORT_STATUS = TO_CHAR(#{reportStatus,jdbcType=INTEGER}), RESULT_STATUS = TO_CHAR(#{resultStatus,jdbcType=INTEGER}) WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int updateReportAndResultStatus(@Param("entrustmentId") String entrustmentId, @Param("reportStatus") String reportStatus, @Param("resultStatus") String resultStatus);

    @Update("UPDATE (SELECT * FROM T_NUCLEAR_DENSITY WHERE ENTRUSTMENT_ID = #{entrustmentId,jdbcType=VARCHAR}) SET " +
            "RECORD_TESTER_SIGN = NVL(#{testerSign,jdbcType=CLOB,typeHandler=org.apache.ibatis.type.ClobTypeHandler}, RECORD_TESTER_SIGN), " +
            "RECORD_REVIEW_SIGN = NVL(#{reviewSign,jdbcType=CLOB,typeHandler=org.apache.ibatis.type.ClobTypeHandler}, RECORD_REVIEW_SIGN)")
    int updateRecordSignsByEntrustmentId(@Param("entrustmentId") String entrustmentId,
                                         @Param("testerSign") String testerSign,
                                         @Param("reviewSign") String reviewSign);
}
