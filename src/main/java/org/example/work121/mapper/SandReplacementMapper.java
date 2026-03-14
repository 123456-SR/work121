package org.example.work121.mapper;

import java.util.List;
import org.apache.ibatis.annotations.*;
import org.example.work121.entity.SandReplacement;

@Mapper
public interface SandReplacementMapper {

    @Delete("DELETE FROM T_SAND_REPLACEMENT WHERE ID = #{id}")
    int deleteById(@Param("id") String id);

    @Select("SELECT " +
            "t2.ID as id, " +
            "t2.ENTRUSTMENT_ID as entrustmentId, " +
            // 业务字段统一放到 DATA_JSON 里，这里只取 DATA_JSON 及通用字段
            "t2.DATA_JSON as dataJson, " +
            "t2.RECORD_REVIEWER as reviewer, " +
            "t2.RECORD_TESTER as tester, " +
            "t2.RECORD_REVIEW_SIGN as reviewSignaturePhoto, " +
            "t2.RECORD_TESTER_SIGN as inspectSignaturePhoto, " +
            "t2.APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto, " +
            "t2.STATUS as status, " +
            "t2.REJECT_REASON as rejectReason, " +
            "t2.NEXT_HANDLER as nextHandler, " +
            "t2.CREATE_BY as createBy, " +
            "t2.CREATE_TIME as createTime, " +
            "t2.UPDATE_BY as updateBy, " +
            "t2.UPDATE_TIME as updateTime, " +
            // JZS_ENTRUSTMENT fields
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
            "FROM T_SAND_REPLACEMENT t2 " +
            "LEFT JOIN T_ENTRUSTMENT t1 ON t2.ENTRUSTMENT_ID = t1.ID " +
            "WHERE t2.ENTRUSTMENT_ID = #{entrustmentId}")
    List<SandReplacement> selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Select("SELECT " +
            "t2.ID as id, " +
            "t2.ENTRUSTMENT_ID as entrustmentId, " +
            "t2.DATA_JSON as dataJson, " +
            "t2.RECORD_REVIEWER as reviewer, " +
            "t2.RECORD_TESTER as tester, " +
            "t2.RECORD_REVIEW_SIGN as reviewSignaturePhoto, " +
            "t2.RECORD_TESTER_SIGN as inspectSignaturePhoto, " +
            "t2.APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto, " +
            "t2.CREATE_BY as createBy, " +
            "t2.CREATE_TIME as createTime, " +
            "t2.UPDATE_BY as updateBy, " +
            "t2.UPDATE_TIME as updateTime, " +
            // JZS_ENTRUSTMENT fields
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
            "FROM T_SAND_REPLACEMENT t2 " +
            "LEFT JOIN T_ENTRUSTMENT t1 ON t2.ENTRUSTMENT_ID = t1.ID " +
            "WHERE t2.ID = #{id}")
    SandReplacement selectById(@Param("id") String id);

    @Insert("INSERT INTO T_SAND_REPLACEMENT " +
            "(ID, ENTRUSTMENT_ID, DATA_JSON, " +
            "RECORD_TESTER, RECORD_REVIEWER, APPROVER, " +
            "RECORD_REVIEW_SIGN, RECORD_TESTER_SIGN, APPROVE_SIGNATURE_PHOTO, " +
            "STATUS, REJECT_REASON, NEXT_HANDLER, " +
            "CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME) " +
            "VALUES " +
            "(#{id}, #{entrustmentId}, #{dataJson}, " +
            "#{tester}, #{reviewer}, #{approver}, " +
            "#{reviewSignaturePhoto}, #{inspectSignaturePhoto}, #{approveSignaturePhoto}, " +
            "#{status}, #{rejectReason}, #{nextHandler}, " +
            "#{createBy}, #{createTime}, #{updateBy}, #{updateTime})")
    int insert(SandReplacement entity);

    @Update("UPDATE T_SAND_REPLACEMENT SET " +
            "ENTRUSTMENT_ID = #{entrustmentId}, " +
            "DATA_JSON = #{dataJson}, " +
            "RECORD_TESTER = #{tester}, " +
            "RECORD_REVIEWER = #{reviewer}, " +
            "APPROVER = #{approver}, " +
            "RECORD_REVIEW_SIGN = #{reviewSignaturePhoto}, " +
            "RECORD_TESTER_SIGN = #{inspectSignaturePhoto}, " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto}, " +
            "STATUS = #{status}, " +
            "REJECT_REASON = #{rejectReason}, " +
            "NEXT_HANDLER = #{nextHandler}, " +
            "UPDATE_BY = #{updateBy}, " +
            "UPDATE_TIME = #{updateTime} " +
            "WHERE ID = #{id}")
    int update(SandReplacement entity);

    @Update("UPDATE T_SAND_REPLACEMENT SET STATUS = #{status} WHERE ID = #{id}")
    int updateStatusById(@Param("id") String id, @Param("status") String status);

    @Update("UPDATE T_SAND_REPLACEMENT SET STATUS = #{status}, RECORD_REVIEW_SIGN = #{reviewSignPhoto} WHERE ID = #{id}")
    int updateStatusAndReviewSign(@Param("id") String id, @Param("status") String status, @Param("reviewSignPhoto") String reviewSignPhoto);

    @Update("UPDATE T_SAND_REPLACEMENT SET STATUS = #{status}, APPROVE_SIGNATURE_PHOTO = #{approveSignPhoto} WHERE ID = #{id}")
    int updateStatusAndApproveSign(@Param("id") String id, @Param("status") String status, @Param("approveSignPhoto") String approveSignPhoto);

    @Update("UPDATE T_SAND_REPLACEMENT SET REPORT_STATUS = #{reportStatus}, RESULT_STATUS = #{resultStatus} WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int updateReportAndResultStatus(@Param("entrustmentId") String entrustmentId, @Param("reportStatus") String reportStatus, @Param("resultStatus") String resultStatus);
}

