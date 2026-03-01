package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.LightDynamicPenetrationRecord;
import java.util.List;

@Mapper
public interface LightDynamicPenetrationRecordMapper {

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
            "t2.RECORD_TESTER_SIGN as recordTesterSign, " +
            "t2.CREATE_BY as createBy, " +
            "t2.CREATE_TIME as createTime, " +
            "t2.UPDATE_BY as updateBy, " +
            "t2.UPDATE_TIME as updateTime, " +
            "t2.STATUS as status, " +
            "t2.REJECT_REASON as rejectReason, " +
            "t2.NEXT_HANDLER as nextHandler, " +
            "t2.INSPECT_SIGNATURE_PHOTO as inspectSignaturePhoto, " +
            "t2.REVIEW_SIGNATURE_PHOTO as reviewSignaturePhoto, " +
            "t2.APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto, " +
            "t1.WT_NUM as wtNum, " +
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
            "t2.SOIL_PROPERTY as soilProperty, " +
            "t2.DESIGN_CAPACITY as designCapacity, " +
            "t2.HAMMER_WEIGHT as hammerWeight, " +
            "t2.DROP_DISTANCE as dropDistance, " +
            "t2.TEST_BASIS as testBasis, " +
            "t2.EQUIPMENT as equipment, " +
            "t2.CONCLUSION as conclusion, " +
            "t2.TEST_DATE as testDate, " +
            "t2.REPORT_DATE as reportDate, " +
            "t2.DATA_BLOCKS as dataBlocks, " +
            "t2.REMARKS as remarks " +
            "FROM T_LIGHT_DYNAMIC_PENETRATION t2 " +
            "LEFT JOIN T_ENTRUSTMENT t1 ON t2.ENTRUSTMENT_ID = t1.ID " +
            "WHERE t2.ENTRUSTMENT_ID = #{entrustmentId}")
    List<LightDynamicPenetrationRecord> selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Insert("INSERT INTO T_LIGHT_DYNAMIC_PENETRATION (" +
            "ID, ENTRUSTMENT_ID, DATA_JSON, TESTER, REVIEWER, APPROVER, FILLER, RECORD_TESTER, RECORD_REVIEWER, RECORD_REVIEW_SIGN, RECORD_TESTER_SIGN, " +
            "CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME, " +
            "STATUS, REJECT_REASON, NEXT_HANDLER, " +
            "INSPECT_SIGNATURE_PHOTO, REVIEW_SIGNATURE_PHOTO, APPROVE_SIGNATURE_PHOTO, REMARKS, " +
            "SOIL_PROPERTY, DESIGN_CAPACITY, HAMMER_WEIGHT, DROP_DISTANCE, TEST_BASIS, EQUIPMENT, CONCLUSION, TEST_DATE, REPORT_DATE, DATA_BLOCKS) " +
            "VALUES (" +
            "#{id}, #{entrustmentId}, #{dataJson,jdbcType=CLOB}, #{tester,jdbcType=VARCHAR}, #{reviewer,jdbcType=VARCHAR}, #{approver,jdbcType=VARCHAR}, #{filler,jdbcType=VARCHAR}, #{recordTester,jdbcType=VARCHAR}, #{recordReviewer,jdbcType=VARCHAR}, #{recordReviewSign,jdbcType=VARCHAR}, #{recordTesterSign,jdbcType=VARCHAR}, " +
            "#{createBy,jdbcType=VARCHAR}, #{createTime,jdbcType=TIMESTAMP}, #{updateBy,jdbcType=VARCHAR}, #{updateTime,jdbcType=TIMESTAMP}, " +
            "#{status,jdbcType=VARCHAR}, #{rejectReason,jdbcType=VARCHAR}, #{nextHandler,jdbcType=VARCHAR}, " +
            "#{inspectSignaturePhoto,jdbcType=CLOB}, #{reviewSignaturePhoto,jdbcType=CLOB}, #{approveSignaturePhoto,jdbcType=CLOB}, #{remarks,jdbcType=VARCHAR}, " +
            "#{soilProperty,jdbcType=VARCHAR}, #{designCapacity,jdbcType=VARCHAR}, #{hammerWeight,jdbcType=VARCHAR}, #{dropDistance,jdbcType=VARCHAR}, #{testBasis,jdbcType=VARCHAR}, #{equipment,jdbcType=VARCHAR}, #{conclusion,jdbcType=VARCHAR}, #{testDate,jdbcType=TIMESTAMP}, #{reportDate,jdbcType=TIMESTAMP}, #{dataBlocks,jdbcType=CLOB})")
    int insert(LightDynamicPenetrationRecord record);

    @Update("UPDATE T_LIGHT_DYNAMIC_PENETRATION SET " +
            "DATA_JSON = #{dataJson,jdbcType=CLOB}, " +
            "TESTER = #{tester,jdbcType=VARCHAR}, " +
            "REVIEWER = #{reviewer,jdbcType=VARCHAR}, " +
            "APPROVER = #{approver,jdbcType=VARCHAR}, " +
            "FILLER = #{filler,jdbcType=VARCHAR}, " +
            "RECORD_TESTER = #{recordTester,jdbcType=VARCHAR}, " +
            "RECORD_REVIEWER = #{recordReviewer,jdbcType=VARCHAR}, " +
            "RECORD_REVIEW_SIGN = #{recordReviewSign,jdbcType=VARCHAR}, " +
            "RECORD_TESTER_SIGN = #{recordTesterSign,jdbcType=VARCHAR}, " +
            "UPDATE_BY = #{updateBy,jdbcType=VARCHAR}, " +
            "UPDATE_TIME = #{updateTime,jdbcType=TIMESTAMP}, " +
            "STATUS = #{status,jdbcType=VARCHAR}, " +
            "REJECT_REASON = #{rejectReason,jdbcType=VARCHAR}, " +
            "NEXT_HANDLER = #{nextHandler,jdbcType=VARCHAR}, " +
            "INSPECT_SIGNATURE_PHOTO = #{inspectSignaturePhoto,jdbcType=CLOB}, " +
            "REVIEW_SIGNATURE_PHOTO = #{reviewSignaturePhoto,jdbcType=CLOB}, " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto,jdbcType=CLOB}, " +
            "REMARKS = #{remarks,jdbcType=VARCHAR}, " +
            "SOIL_PROPERTY = #{soilProperty,jdbcType=VARCHAR}, " +
            "DESIGN_CAPACITY = #{designCapacity,jdbcType=VARCHAR}, " +
            "HAMMER_WEIGHT = #{hammerWeight,jdbcType=VARCHAR}, " +
            "DROP_DISTANCE = #{dropDistance,jdbcType=VARCHAR}, " +
            "TEST_BASIS = #{testBasis,jdbcType=VARCHAR}, " +
            "EQUIPMENT = #{equipment,jdbcType=VARCHAR}, " +
            "CONCLUSION = #{conclusion,jdbcType=VARCHAR}, " +
            "TEST_DATE = #{testDate,jdbcType=TIMESTAMP}, " +
            "REPORT_DATE = #{reportDate,jdbcType=TIMESTAMP}, " +
            "DATA_BLOCKS = #{dataBlocks,jdbcType=CLOB} " +
            "WHERE ID = #{id}")
    int update(LightDynamicPenetrationRecord record);

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
            "t2.RECORD_TESTER_SIGN as recordTesterSign, " +
            "t2.CREATE_BY as createBy, " +
            "t2.CREATE_TIME as createTime, " +
            "t2.UPDATE_BY as updateBy, " +
            "t2.UPDATE_TIME as updateTime, " +
            "t2.STATUS as status, " +
            "t2.REJECT_REASON as rejectReason, " +
            "t2.NEXT_HANDLER as nextHandler, " +
            "t2.INSPECT_SIGNATURE_PHOTO as inspectSignaturePhoto, " +
            "t2.REVIEW_SIGNATURE_PHOTO as reviewSignaturePhoto, " +
            "t2.APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto, " +
            "t1.WT_NUM as wtNum, " +
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
            "t2.SOIL_PROPERTY as soilProperty, " +
            "t2.DESIGN_CAPACITY as designCapacity, " +
            "t2.HAMMER_WEIGHT as hammerWeight, " +
            "t2.DROP_DISTANCE as dropDistance, " +
            "t2.TEST_BASIS as testBasis, " +
            "t2.EQUIPMENT as equipment, " +
            "t2.CONCLUSION as conclusion, " +
            "t2.TEST_DATE as testDate, " +
            "t2.REPORT_DATE as reportDate, " +
            "t2.DATA_BLOCKS as dataBlocks, " +
            "t2.REMARKS as remarks " +
            "FROM T_LIGHT_DYNAMIC_PENETRATION t2 " +
            "LEFT JOIN T_ENTRUSTMENT t1 ON t2.ENTRUSTMENT_ID = t1.ID " +
            "WHERE t2.ID = #{id}")
    LightDynamicPenetrationRecord selectById(@Param("id") String id);

    @Update("UPDATE T_LIGHT_DYNAMIC_PENETRATION SET " +
            "ENTRUSTMENT_ID = #{entrustmentId}, " +
            "DATA_JSON = #{dataJson,jdbcType=CLOB}, " +
            "TESTER = #{tester}, " +
            "REVIEWER = #{reviewer}, " +
            "APPROVER = #{approver}, " +
            "FILLER = #{filler}, " +
            "RECORD_TESTER = #{recordTester}, " +
            "RECORD_REVIEWER = #{recordReviewer}, " +
            "RECORD_REVIEW_SIGN = #{recordReviewSign}, " +
            "RECORD_TESTER_SIGN = #{recordTesterSign}, " +
            "UPDATE_BY = #{updateBy}, " +
            "UPDATE_TIME = #{updateTime}, " +
            "STATUS = #{status}, " +
            "REJECT_REASON = #{rejectReason}, " +
            "NEXT_HANDLER = #{nextHandler}, " +
            "INSPECT_SIGNATURE_PHOTO = #{inspectSignaturePhoto}, " +
            "REVIEW_SIGNATURE_PHOTO = #{reviewSignaturePhoto}, " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto}, " +
            "REMARKS = #{remarks}, " +
            "SOIL_PROPERTY = #{soilProperty}, " +
            "DESIGN_CAPACITY = #{designCapacity}, " +
            "HAMMER_WEIGHT = #{hammerWeight}, " +
            "DROP_DISTANCE = #{dropDistance}, " +
            "TEST_BASIS = #{testBasis}, " +
            "EQUIPMENT = #{equipment}, " +
            "CONCLUSION = #{conclusion}, " +
            "TEST_DATE = #{testDate}, " +
            "REPORT_DATE = #{reportDate}, " +
            "DATA_BLOCKS = #{dataBlocks} " +
            "WHERE ID = #{id}")
    int updateById(LightDynamicPenetrationRecord record);

    @Delete("DELETE FROM T_LIGHT_DYNAMIC_PENETRATION WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int deleteByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Delete("DELETE FROM T_LIGHT_DYNAMIC_PENETRATION WHERE ID = #{id}")
    int deleteById(@Param("id") String id);
}
