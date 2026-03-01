package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.CuttingRingRecord;

@Mapper
public interface CuttingRingRecordMapper {

    @Select("SELECT " +
            "t2.ID as id, " +
            "t2.ENTRUSTMENT_ID as entrustmentId, " +
            "t2.DATA_JSON as dataJson, " +
            "t2.TESTER as tester, " +
            "t2.REVIEWER as reviewer, " +
            "t2.APPROVER as approver, " +
            "t2.FILLER as filler, " +
            "t2.RECORD_TESTER as recordTester, " +
            "t2.RECORD_REVIEWER as recordReviewer, " +
            "t2.RECORD_REVIEW_SIGN as recordReviewSign, " +
            "t2.INSPECT_SIGNATURE_PHOTO as inspectSignaturePhoto, " +
            "t2.REVIEW_SIGNATURE_PHOTO as reviewSignaturePhoto, " +
            "t2.APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto, " +
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
    CuttingRingRecord selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Insert("INSERT INTO T_CUTTING_RING (ID, ENTRUSTMENT_ID, DATA_JSON, TESTER, REVIEWER, APPROVER, FILLER, RECORD_TESTER, RECORD_REVIEWER, RECORD_REVIEW_SIGN, INSPECT_SIGNATURE_PHOTO, REVIEW_SIGNATURE_PHOTO, APPROVE_SIGNATURE_PHOTO, CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME) " +
            "VALUES (#{id}, #{entrustmentId}, #{dataJson,jdbcType=CLOB}, #{tester,jdbcType=VARCHAR}, #{reviewer,jdbcType=VARCHAR}, #{approver,jdbcType=VARCHAR}, #{filler,jdbcType=VARCHAR}, #{recordTester,jdbcType=VARCHAR}, #{recordReviewer,jdbcType=VARCHAR}, #{recordReviewSign,jdbcType=VARCHAR}, #{inspectSignaturePhoto,jdbcType=CLOB}, #{reviewSignaturePhoto,jdbcType=CLOB}, #{approveSignaturePhoto,jdbcType=CLOB}, #{createBy,jdbcType=VARCHAR}, #{createTime,jdbcType=TIMESTAMP}, #{updateBy,jdbcType=VARCHAR}, #{updateTime,jdbcType=TIMESTAMP})")
    int insert(CuttingRingRecord record);

    @Update("UPDATE T_CUTTING_RING SET " +
            "DATA_JSON = #{dataJson,jdbcType=CLOB}, " +
            "TESTER = #{tester,jdbcType=VARCHAR}, " +
            "REVIEWER = #{reviewer,jdbcType=VARCHAR}, " +
            "APPROVER = #{approver,jdbcType=VARCHAR}, " +
            "FILLER = #{filler,jdbcType=VARCHAR}, " +
            "RECORD_TESTER = #{recordTester,jdbcType=VARCHAR}, " +
            "RECORD_REVIEWER = #{recordReviewer,jdbcType=VARCHAR}, " +
            "RECORD_REVIEW_SIGN = #{recordReviewSign,jdbcType=VARCHAR}, " +
            "RECORD_TESTER_SIGN = #{recordTesterSign,jdbcType=VARCHAR}, " +
            "INSPECT_SIGNATURE_PHOTO = #{inspectSignaturePhoto,jdbcType=CLOB}, " +
            "REVIEW_SIGNATURE_PHOTO = #{reviewSignaturePhoto,jdbcType=CLOB}, " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto,jdbcType=CLOB}, " +
            "UPDATE_BY = #{updateBy,jdbcType=VARCHAR}, " +
            "UPDATE_TIME = #{updateTime,jdbcType=TIMESTAMP} " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int update(CuttingRingRecord record);

    @Select("SELECT " +
            "t2.ID as id, " +
            "t2.ENTRUSTMENT_ID as entrustmentId, " +
            "t2.DATA_JSON as dataJson, " +
            "t2.TESTER as tester, " +
            "t2.REVIEWER as reviewer, " +
            "t2.APPROVER as approver, " +
            "t2.FILLER as filler, " +
            "t2.RECORD_TESTER as recordTester, " +
            "t2.RECORD_REVIEWER as recordReviewer, " +
            "t2.RECORD_REVIEW_SIGN as recordReviewSign, " +
            "t2.INSPECT_SIGNATURE_PHOTO as inspectSignaturePhoto, " +
            "t2.REVIEW_SIGNATURE_PHOTO as reviewSignaturePhoto, " +
            "t2.APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto, " +
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
    CuttingRingRecord selectById(@Param("id") String id);

    @Update("UPDATE T_CUTTING_RING SET " +
            "ENTRUSTMENT_ID = #{entrustmentId,jdbcType=VARCHAR}, " +
            "DATA_JSON = #{dataJson,jdbcType=CLOB}, " +
            "TESTER = #{tester,jdbcType=VARCHAR}, " +
            "REVIEWER = #{reviewer,jdbcType=VARCHAR}, " +
            "APPROVER = #{approver,jdbcType=VARCHAR}, " +
            "FILLER = #{filler,jdbcType=VARCHAR}, " +
            "RECORD_TESTER = #{recordTester,jdbcType=VARCHAR}, " +
            "RECORD_REVIEWER = #{recordReviewer,jdbcType=VARCHAR}, " +
            "RECORD_REVIEW_SIGN = #{recordReviewSign,jdbcType=VARCHAR}, " +
            "INSPECT_SIGNATURE_PHOTO = #{inspectSignaturePhoto,jdbcType=CLOB}, " +
            "REVIEW_SIGNATURE_PHOTO = #{reviewSignaturePhoto,jdbcType=CLOB}, " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto,jdbcType=CLOB}, " +
            "UPDATE_BY = #{updateBy,jdbcType=VARCHAR}, " +
            "UPDATE_TIME = #{updateTime,jdbcType=TIMESTAMP} " +
            "WHERE ID = #{id}")
    int updateById(CuttingRingRecord record);

    @Delete("DELETE FROM T_CUTTING_RING WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int deleteByEntrustmentId(@Param("entrustmentId") String entrustmentId);
}
