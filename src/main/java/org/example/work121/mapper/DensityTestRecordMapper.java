package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.DensityTestRecord;

@Mapper
public interface DensityTestRecordMapper {

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
            "t2.RECORD_REVIEW_SIGN as recordReviewSign, " +
            "t2.RECORD_TESTER_SIGN as recordTesterSign, " +
            "t2.RECORD_TESTER_SIGN as inspectSignaturePhoto, " +
            "t2.RECORD_REVIEW_SIGN as reviewSignaturePhoto, " +
            "t2.APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto, " +
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
            "FROM T_DENSITY_TEST t2 " +
            "LEFT JOIN T_ENTRUSTMENT t1 ON t2.ENTRUSTMENT_ID = t1.ID " +
            "WHERE t2.ENTRUSTMENT_ID = #{entrustmentId}")
    DensityTestRecord selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

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
            "t2.RECORD_REVIEW_SIGN as recordReviewSign, " +
            "t2.RECORD_TESTER_SIGN as recordTesterSign, " +
            "t2.RECORD_TESTER_SIGN as inspectSignaturePhoto, " +
            "t2.RECORD_REVIEW_SIGN as reviewSignaturePhoto, " +
            "t2.APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto, " +
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
            "FROM T_DENSITY_TEST t2 " +
            "LEFT JOIN T_ENTRUSTMENT t1 ON t2.ENTRUSTMENT_ID = t1.ID " +
            "WHERE t2.ID = #{id}")
    DensityTestRecord selectById(@Param("id") String id);

    @Insert("INSERT INTO T_DENSITY_TEST (ID, ENTRUSTMENT_ID, DATA_JSON, APPROVER, FILLER, RECORD_TESTER, RECORD_REVIEWER, RECORD_REVIEW_SIGN, RECORD_TESTER_SIGN, APPROVE_SIGNATURE_PHOTO, CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME) " +
            "VALUES (#{id}, #{entrustmentId}, #{dataJson}, #{approver}, #{filler}, #{recordTester}, #{recordReviewer}, #{recordReviewSign}, #{recordTesterSign}, #{approveSignaturePhoto}, #{createBy}, #{createTime}, #{updateBy}, #{updateTime})")
    int insert(DensityTestRecord record);

    @Update("UPDATE T_DENSITY_TEST SET " +
            "DATA_JSON = #{dataJson}, " +
            "APPROVER = #{approver}, " +
            "FILLER = #{filler}, " +
            "RECORD_TESTER = #{recordTester}, " +
            "RECORD_REVIEWER = #{recordReviewer}, " +
            "RECORD_REVIEW_SIGN = #{recordReviewSign}, " +
            "RECORD_TESTER_SIGN = #{recordTesterSign}, " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto}, " +
            "UPDATE_BY = #{updateBy}, " +
            "UPDATE_TIME = #{updateTime} " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int update(DensityTestRecord record);

    @Update("UPDATE T_DENSITY_TEST SET " +
            "ENTRUSTMENT_ID = #{entrustmentId}, " +
            "DATA_JSON = #{dataJson}, " +
            "APPROVER = #{approver}, " +
            "FILLER = #{filler}, " +
            "RECORD_TESTER = #{recordTester}, " +
            "RECORD_REVIEWER = #{recordReviewer}, " +
            "RECORD_REVIEW_SIGN = #{recordReviewSign}, " +
            "RECORD_TESTER_SIGN = #{recordTesterSign}, " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto}, " +
            "UPDATE_BY = #{updateBy}, " +
            "UPDATE_TIME = #{updateTime} " +
            "WHERE ID = #{id}")
    int updateById(DensityTestRecord record);

    @Delete("DELETE FROM T_DENSITY_TEST WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int deleteByEntrustmentId(@Param("entrustmentId") String entrustmentId);
}
