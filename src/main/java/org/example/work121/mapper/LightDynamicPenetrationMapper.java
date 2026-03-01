package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.LightDynamicPenetration;
import java.util.List;

@Mapper
public interface LightDynamicPenetrationMapper {

    @Select("SELECT " +
            "t2.ID as id, " +
            "t2.ENTRUSTMENT_ID as entrustmentId, " +
            "t2.DATA_JSON as dataJson, " +
            "t2.STATUS as status, " +
            "t2.REJECT_REASON as rejectReason, " +
            "t2.NEXT_HANDLER as nextHandler, " +
            "t2.TESTER as tester, " +
            "t2.REVIEWER as reviewer, " +
            "t2.APPROVER as approver, " +
            "t2.FILLER as filler, " +
            "t2.RECORD_TESTER as recordTester, " +
            "t2.RECORD_TESTER_SIGN as recordTesterSign, " +
            "t2.RECORD_REVIEWER as recordReviewer, " +
            "t2.RECORD_REVIEW_SIGN as recordReviewSign, " +
            "t2.INSPECT_SIGNATURE_PHOTO as inspectSignaturePhoto, " +
            "t2.REVIEW_SIGNATURE_PHOTO as reviewSignaturePhoto, " +
            "t2.APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto, " +
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
            "t2.REMARKS as remarks, " +
            "t1.WT_NUM as wtNum, " +
            "t1.CLIENT_UNIT as clientUnit, " +
            "t1.PROJECT_NAME as projectName, " +
            "t1.COMMISSION_DATE as commissionDate, " +
            "t1.CONSTRUCTION_PART as constructionPart, " +
            "t1.CONSTRUCTION_UNIT as constructionUnit, " +
            "t1.WITNESS_UNIT as witnessUnit, " +
            "t1.WITNESS as witness, " +
            "t1.SAMPLE_NAME as sampleName, " +
            "t1.TEST_CATEGORY as testCategory " +
            "FROM T_LIGHT_DYNAMIC_PENETRATION t2 " +
            "LEFT JOIN T_ENTRUSTMENT t1 ON t2.ENTRUSTMENT_ID = t1.ID " +
            "WHERE t2.ENTRUSTMENT_ID = #{entrustmentId}")
    List<LightDynamicPenetration> selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Select("SELECT " +
            "t2.ID as id, " +
            "t2.ENTRUSTMENT_ID as entrustmentId, " +
            "t2.DATA_JSON as dataJson, " +
            "t2.STATUS as status, " +
            "t2.REJECT_REASON as rejectReason, " +
            "t2.NEXT_HANDLER as nextHandler, " +
            "t2.TESTER as tester, " +
            "t2.REVIEWER as reviewer, " +
            "t2.APPROVER as approver, " +
            "t2.FILLER as filler, " +
            "t2.RECORD_TESTER as recordTester, " +
            "t2.RECORD_TESTER_SIGN as recordTesterSign, " +
            "t2.RECORD_REVIEWER as recordReviewer, " +
            "t2.RECORD_REVIEW_SIGN as recordReviewSign, " +
            "t2.INSPECT_SIGNATURE_PHOTO as inspectSignaturePhoto, " +
            "t2.REVIEW_SIGNATURE_PHOTO as reviewSignaturePhoto, " +
            "t2.APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto, " +
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
            "t2.REMARKS as remarks, " +
            "t1.WT_NUM as wtNum, " +
            "t1.CLIENT_UNIT as clientUnit, " +
            "t1.PROJECT_NAME as projectName, " +
            "t1.COMMISSION_DATE as commissionDate, " +
            "t1.CONSTRUCTION_PART as constructionPart, " +
            "t1.CONSTRUCTION_UNIT as constructionUnit, " +
            "t1.WITNESS_UNIT as witnessUnit, " +
            "t1.WITNESS as witness, " +
            "t1.SAMPLE_NAME as sampleName, " +
            "t1.TEST_CATEGORY as testCategory " +
            "FROM T_LIGHT_DYNAMIC_PENETRATION t2 " +
            "LEFT JOIN T_ENTRUSTMENT t1 ON t2.ENTRUSTMENT_ID = t1.ID " +
            "WHERE t2.ID = #{id}")
    LightDynamicPenetration selectById(@Param("id") String id);

    @Insert("INSERT INTO T_LIGHT_DYNAMIC_PENETRATION " +
            "(ID, ENTRUSTMENT_ID, DATA_JSON, STATUS, REJECT_REASON, NEXT_HANDLER, " +
            "TESTER, REVIEWER, APPROVER, FILLER, RECORD_TESTER, RECORD_TESTER_SIGN, RECORD_REVIEWER, RECORD_REVIEW_SIGN, " +
            "INSPECT_SIGNATURE_PHOTO, REVIEW_SIGNATURE_PHOTO, APPROVE_SIGNATURE_PHOTO, " +
            "SOIL_PROPERTY, DESIGN_CAPACITY, HAMMER_WEIGHT, DROP_DISTANCE, TEST_BASIS, EQUIPMENT, CONCLUSION, " +
            "TEST_DATE, REPORT_DATE, DATA_BLOCKS, REMARKS, " +
            "CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME) " +
            "VALUES " +
            "(#{id}, #{entrustmentId}, #{dataJson}, #{status}, #{rejectReason}, #{nextHandler}, " +
            "#{tester}, #{reviewer}, #{approver}, #{filler}, #{recordTester}, #{recordTesterSign}, #{recordReviewer}, #{recordReviewSign}, " +
            "#{inspectSignaturePhoto}, #{reviewSignaturePhoto}, #{approveSignaturePhoto}, " +
            "#{soilProperty}, #{designCapacity}, #{hammerWeight}, #{dropDistance}, #{testBasis}, #{equipment}, #{conclusion}, " +
            "#{testDate}, #{reportDate}, #{dataBlocks}, #{remarks}, " +
            "#{createBy}, #{createTime}, #{updateBy}, #{updateTime})")
    int insert(LightDynamicPenetration record);

    @Update("UPDATE T_LIGHT_DYNAMIC_PENETRATION SET " +
            "DATA_JSON = #{dataJson}, " +
            "STATUS = #{status}, " +
            "REJECT_REASON = #{rejectReason}, " +
            "NEXT_HANDLER = #{nextHandler}, " +
            "TESTER = #{tester}, " +
            "REVIEWER = #{reviewer}, " +
            "APPROVER = #{approver}, " +
            "FILLER = #{filler}, " +
            "RECORD_TESTER = #{recordTester}, " +
            "RECORD_TESTER_SIGN = #{recordTesterSign}, " +
            "RECORD_REVIEWER = #{recordReviewer}, " +
            "RECORD_REVIEW_SIGN = #{recordReviewSign}, " +
            "INSPECT_SIGNATURE_PHOTO = #{inspectSignaturePhoto}, " +
            "REVIEW_SIGNATURE_PHOTO = #{reviewSignaturePhoto}, " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto}, " +
            "SOIL_PROPERTY = #{soilProperty}, " +
            "DESIGN_CAPACITY = #{designCapacity}, " +
            "HAMMER_WEIGHT = #{hammerWeight}, " +
            "DROP_DISTANCE = #{dropDistance}, " +
            "TEST_BASIS = #{testBasis}, " +
            "EQUIPMENT = #{equipment}, " +
            "CONCLUSION = #{conclusion}, " +
            "TEST_DATE = #{testDate}, " +
            "REPORT_DATE = #{reportDate}, " +
            "DATA_BLOCKS = #{dataBlocks}, " +
            "REMARKS = #{remarks}, " +
            "UPDATE_BY = #{updateBy}, " +
            "UPDATE_TIME = #{updateTime} " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int update(LightDynamicPenetration record);

    @Select("SELECT COUNT(*) FROM T_LIGHT_DYNAMIC_PENETRATION WHERE ID = #{id}")
    int countById(@Param("id") String id);

    @Update("UPDATE T_LIGHT_DYNAMIC_PENETRATION SET " +
            "ENTRUSTMENT_ID = #{entrustmentId}, " +
            "DATA_JSON = #{dataJson}, " +
            "STATUS = #{status}, " +
            "REJECT_REASON = #{rejectReason}, " +
            "NEXT_HANDLER = #{nextHandler}, " +
            "TESTER = #{tester}, " +
            "REVIEWER = #{reviewer}, " +
            "APPROVER = #{approver}, " +
            "FILLER = #{filler}, " +
            "RECORD_TESTER = #{recordTester}, " +
            "RECORD_TESTER_SIGN = #{recordTesterSign}, " +
            "RECORD_REVIEWER = #{recordReviewer}, " +
            "RECORD_REVIEW_SIGN = #{recordReviewSign}, " +
            "INSPECT_SIGNATURE_PHOTO = #{inspectSignaturePhoto}, " +
            "REVIEW_SIGNATURE_PHOTO = #{reviewSignaturePhoto}, " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto}, " +
            "SOIL_PROPERTY = #{soilProperty}, " +
            "DESIGN_CAPACITY = #{designCapacity}, " +
            "HAMMER_WEIGHT = #{hammerWeight}, " +
            "DROP_DISTANCE = #{dropDistance}, " +
            "TEST_BASIS = #{testBasis}, " +
            "EQUIPMENT = #{equipment}, " +
            "CONCLUSION = #{conclusion}, " +
            "TEST_DATE = #{testDate}, " +
            "REPORT_DATE = #{reportDate}, " +
            "DATA_BLOCKS = #{dataBlocks}, " +
            "REMARKS = #{remarks}, " +
            "UPDATE_BY = #{updateBy}, " +
            "UPDATE_TIME = #{updateTime} " +
            "WHERE ID = #{id}")
    int updateById(LightDynamicPenetration record);

    @Delete("DELETE FROM T_LIGHT_DYNAMIC_PENETRATION WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int deleteByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Delete("DELETE FROM T_LIGHT_DYNAMIC_PENETRATION WHERE ID = #{id}")
    int deleteById(@Param("id") String id);
}
