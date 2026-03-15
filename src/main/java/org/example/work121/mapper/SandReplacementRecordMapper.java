package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.SandReplacementRecord;

@Mapper
public interface SandReplacementRecordMapper {

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
            "FROM T_SAND_REPLACEMENT t2 " +
            "LEFT JOIN T_ENTRUSTMENT t1 ON t2.ENTRUSTMENT_ID = t1.ID " +
            "WHERE t2.ENTRUSTMENT_ID = #{entrustmentId}")
    SandReplacementRecord selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Insert("INSERT INTO T_SAND_REPLACEMENT (ID, ENTRUSTMENT_ID, APPROVER, FILLER, RECORD_TESTER, RECORD_REVIEWER, CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME, DATA_JSON, RECORD_REVIEW_SIGN, RECORD_TESTER_SIGN, APPROVE_SIGNATURE_PHOTO) " +
            "VALUES (#{id}, #{entrustmentId}, #{approver,jdbcType=VARCHAR}, #{filler,jdbcType=VARCHAR}, #{recordTester,jdbcType=VARCHAR}, #{recordReviewer,jdbcType=VARCHAR}, #{createBy,jdbcType=VARCHAR}, #{createTime,jdbcType=TIMESTAMP}, #{updateBy,jdbcType=VARCHAR}, #{updateTime,jdbcType=TIMESTAMP}, #{dataJson,jdbcType=CLOB}, NVL(#{recordReviewSign,jdbcType=CLOB}, #{reviewSignaturePhoto,jdbcType=CLOB}), NVL(#{recordTesterSign,jdbcType=CLOB}, #{inspectSignaturePhoto,jdbcType=CLOB}), #{approveSignaturePhoto,jdbcType=CLOB})")
    int insert(SandReplacementRecord record);

    @Update("UPDATE (SELECT * FROM T_SAND_REPLACEMENT WHERE ENTRUSTMENT_ID = #{entrustmentId}) SET " +
            "APPROVER = #{approver,jdbcType=VARCHAR}, " +
            "FILLER = #{filler,jdbcType=VARCHAR}, " +
            "RECORD_TESTER = #{recordTester,jdbcType=VARCHAR}, " +
            "RECORD_REVIEWER = #{recordReviewer,jdbcType=VARCHAR}, " +
            "UPDATE_BY = #{updateBy,jdbcType=VARCHAR}, " +
            "UPDATE_TIME = #{updateTime,jdbcType=TIMESTAMP}, " +
            "DATA_JSON = #{dataJson,jdbcType=CLOB}, " +
            "RECORD_REVIEW_SIGN = NVL(#{recordReviewSign,jdbcType=CLOB}, #{reviewSignaturePhoto,jdbcType=CLOB}), " +
            "RECORD_TESTER_SIGN = NVL(#{recordTesterSign,jdbcType=CLOB}, #{inspectSignaturePhoto,jdbcType=CLOB}), " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto,jdbcType=CLOB}")
    int update(SandReplacementRecord record);

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
            "FROM T_SAND_REPLACEMENT t2 " +
            "LEFT JOIN T_ENTRUSTMENT t1 ON t2.ENTRUSTMENT_ID = t1.ID " +
            "WHERE t2.ID = #{id}")
    SandReplacementRecord selectById(@Param("id") String id);

    @Update("UPDATE (SELECT * FROM T_SAND_REPLACEMENT WHERE ID = #{id}) SET " +
            "ENTRUSTMENT_ID = #{entrustmentId,jdbcType=VARCHAR}, " +
            "APPROVER = #{approver,jdbcType=VARCHAR}, " +
            "FILLER = #{filler,jdbcType=VARCHAR}, " +
            "RECORD_TESTER = #{recordTester,jdbcType=VARCHAR}, " +
            "RECORD_REVIEWER = #{recordReviewer,jdbcType=VARCHAR}, " +
            "UPDATE_BY = #{updateBy,jdbcType=VARCHAR}, " +
            "UPDATE_TIME = #{updateTime,jdbcType=TIMESTAMP}, " +
            "DATA_JSON = #{dataJson,jdbcType=CLOB}, " +
            "RECORD_REVIEW_SIGN = NVL(#{recordReviewSign,jdbcType=CLOB}, #{reviewSignaturePhoto,jdbcType=CLOB}), " +
            "RECORD_TESTER_SIGN = NVL(#{recordTesterSign,jdbcType=CLOB}, #{inspectSignaturePhoto,jdbcType=CLOB}), " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto,jdbcType=CLOB}")
    int updateById(SandReplacementRecord record);

    @Delete("DELETE FROM T_SAND_REPLACEMENT WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int deleteByEntrustmentId(@Param("entrustmentId") String entrustmentId);
}
