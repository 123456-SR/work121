package org.example.work121.mapper;

import java.util.List;
import org.apache.ibatis.annotations.*;
import org.example.work121.entity.CuttingRing;

@Mapper
public interface CuttingRingMapper {

    @Select("SELECT " +
            "t2.ID as id, " +
            "t2.ENTRUSTMENT_ID as entrustmentId, " +
            "t2.DATA_JSON as dataJson, " +
            "t2.RECORD_REVIEWER as reviewer, " +
            "t2.RECORD_TESTER as tester, " +
            "t2.RECORD_REVIEW_SIGN as reviewSignaturePhoto, " +
            "t2.RECORD_TESTER_SIGN as inspectSignaturePhoto, " +
            "t2.APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto, " +
            "CASE WHEN REGEXP_LIKE(t2.STATUS, '^[0-9]+$') THEN TO_NUMBER(t2.STATUS) ELSE 0 END as status, " +
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
            "FROM T_CUTTING_RING t2 " +
            "LEFT JOIN T_ENTRUSTMENT t1 ON t2.ENTRUSTMENT_ID = t1.ID " +
            "WHERE t2.ID = #{id}")
    CuttingRing selectById(@Param("id") String id);

    @Update("UPDATE T_CUTTING_RING SET " +
            "ENTRUSTMENT_ID = #{entrustmentId,jdbcType=VARCHAR}, " +
            "STATUS = #{status,jdbcType=VARCHAR}, " +
            "REJECT_REASON = #{rejectReason,jdbcType=VARCHAR}, " +
            "NEXT_HANDLER = #{nextHandler,jdbcType=VARCHAR}, " +
            "UPDATE_BY = #{updateBy,jdbcType=VARCHAR}, " +
            "UPDATE_TIME = #{updateTime,jdbcType=TIMESTAMP}, " +
            "DATA_JSON = #{dataJson,jdbcType=CLOB}, " +
            "RECORD_REVIEW_SIGN = NVL(#{recordReviewSign,jdbcType=CLOB}, #{reviewSignaturePhoto,jdbcType=CLOB}), " +
            "RECORD_TESTER_SIGN = NVL(#{recordTesterSign,jdbcType=CLOB}, #{inspectSignaturePhoto,jdbcType=CLOB}), " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto,jdbcType=CLOB} " +
            "WHERE ID = #{id,jdbcType=VARCHAR}")
    int updateById(CuttingRing cuttingRing);

    @Select("SELECT " +
            "t2.ID as id, " +
            "t2.ENTRUSTMENT_ID as entrustmentId, " +
            "t2.DATA_JSON as dataJson, " +
            "t2.RECORD_REVIEWER as reviewer, " +
            "t2.RECORD_TESTER as tester, " +
            "t2.RECORD_REVIEW_SIGN as reviewSignaturePhoto, " +
            "t2.RECORD_TESTER_SIGN as inspectSignaturePhoto, " +
            "t2.APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto, " +
            "CASE WHEN REGEXP_LIKE(t2.STATUS, '^[0-9]+$') THEN TO_NUMBER(t2.STATUS) ELSE 0 END as status, " +
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
            "FROM T_CUTTING_RING t2 " +
            "LEFT JOIN T_ENTRUSTMENT t1 ON t2.ENTRUSTMENT_ID = t1.ID " +
            "WHERE t2.ENTRUSTMENT_ID = #{entrustmentId}")
    List<CuttingRing> selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Insert("INSERT INTO T_CUTTING_RING (" +
            "ID, ENTRUSTMENT_ID, " +
            "RECORD_TESTER, RECORD_REVIEWER, APPROVER, " +
            "STATUS, REJECT_REASON, NEXT_HANDLER, " +
            "CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME, " +
            "DATA_JSON, RECORD_REVIEW_SIGN, RECORD_TESTER_SIGN, APPROVE_SIGNATURE_PHOTO) " +
            "VALUES (" +
            "#{id,jdbcType=VARCHAR}, #{entrustmentId,jdbcType=VARCHAR}, " +
            "#{tester,jdbcType=VARCHAR}, #{reviewer,jdbcType=VARCHAR}, #{approver,jdbcType=VARCHAR}, " +
            "#{status,jdbcType=VARCHAR}, #{rejectReason,jdbcType=VARCHAR}, #{nextHandler,jdbcType=VARCHAR}, " +
            "#{createBy,jdbcType=VARCHAR}, #{createTime,jdbcType=TIMESTAMP}, #{updateBy,jdbcType=VARCHAR}, #{updateTime,jdbcType=TIMESTAMP}, " +
            "#{dataJson,jdbcType=CLOB}, NVL(#{recordReviewSign,jdbcType=CLOB}, #{reviewSignaturePhoto,jdbcType=CLOB}), NVL(#{recordTesterSign,jdbcType=CLOB}, #{inspectSignaturePhoto,jdbcType=CLOB}), #{approveSignaturePhoto,jdbcType=CLOB})")
    int insert(CuttingRing cuttingRing);

    @Update("UPDATE T_CUTTING_RING SET " +
            "RECORD_TESTER = #{tester}, " +
            "RECORD_REVIEWER = #{reviewer}, " +
            "APPROVER = #{approver}, " +
            "STATUS = #{status}, " +
            "REJECT_REASON = #{rejectReason}, " +
            "NEXT_HANDLER = #{nextHandler}, " +
            "UPDATE_BY = #{updateBy}, " +
            "UPDATE_TIME = #{updateTime}, " +
            "DATA_JSON = #{dataJson,jdbcType=CLOB}, " +
            "RECORD_REVIEW_SIGN = NVL(#{recordReviewSign,jdbcType=CLOB}, #{reviewSignaturePhoto,jdbcType=CLOB}), " +
            "RECORD_TESTER_SIGN = NVL(#{recordTesterSign,jdbcType=CLOB}, #{inspectSignaturePhoto,jdbcType=CLOB}), " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto,jdbcType=CLOB} " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int update(CuttingRing cuttingRing);

    @Delete("DELETE FROM T_CUTTING_RING WHERE ID = #{id}")
    int deleteById(@Param("id") String id);

    @Update("UPDATE T_CUTTING_RING SET STATUS = #{status} WHERE ID = #{id}")
    int updateStatusById(@Param("id") String id, @Param("status") String status);

    @Update("UPDATE T_CUTTING_RING SET STATUS = #{status}, RECORD_REVIEW_SIGN = #{reviewSignPhoto,jdbcType=CLOB} WHERE ID = #{id}")
    int updateStatusAndReviewSign(@Param("id") String id, @Param("status") String status, @Param("reviewSignPhoto") String reviewSignPhoto);

    @Update("UPDATE T_CUTTING_RING SET STATUS = #{status}, APPROVE_SIGNATURE_PHOTO = #{approveSignPhoto,jdbcType=CLOB} WHERE ID = #{id}")
    int updateStatusAndApproveSign(@Param("id") String id, @Param("status") String status, @Param("approveSignPhoto") String approveSignPhoto);

    @Update("UPDATE T_CUTTING_RING SET REPORT_STATUS = #{reportStatus}, RESULT_STATUS = #{resultStatus} WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int updateReportAndResultStatus(@Param("entrustmentId") String entrustmentId, @Param("reportStatus") String reportStatus, @Param("resultStatus") String resultStatus);

    @Update("UPDATE T_CUTTING_RING SET " +
            "RECORD_TESTER_SIGN = NVL(#{testerSign,jdbcType=CLOB,typeHandler=org.apache.ibatis.type.ClobTypeHandler}, RECORD_TESTER_SIGN), " +
            "RECORD_REVIEW_SIGN = NVL(#{reviewSign,jdbcType=CLOB,typeHandler=org.apache.ibatis.type.ClobTypeHandler}, RECORD_REVIEW_SIGN) " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId,jdbcType=VARCHAR}")
    int updateRecordSignsByEntrustmentId(@Param("entrustmentId") String entrustmentId,
                                         @Param("testerSign") String testerSign,
                                         @Param("reviewSign") String reviewSign);
}
