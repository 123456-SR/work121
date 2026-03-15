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
            "t2.RECORD_TESTER as tester, " +
            "t2.RECORD_REVIEWER as reviewer, " +
            "t2.APPROVER as approver, " +
            "t2.FILLER as filler, " +
            "t2.RECORD_TESTER as recordTester, " +
            "t2.RECORD_TESTER_SIGN as recordTesterSign, " +
            "t2.RECORD_REVIEWER as recordReviewer, " +
            "t2.RECORD_REVIEW_SIGN as recordReviewSign, " +
            "t2.RECORD_TESTER_SIGN as inspectSignaturePhoto, " +
            "t2.RECORD_REVIEW_SIGN as reviewSignaturePhoto, " +
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
            "t2.RECORD_TESTER as tester, " +
            "t2.RECORD_REVIEWER as reviewer, " +
            "t2.APPROVER as approver, " +
            "t2.FILLER as filler, " +
            "t2.RECORD_TESTER as recordTester, " +
            "t2.RECORD_TESTER_SIGN as recordTesterSign, " +
            "t2.RECORD_REVIEWER as recordReviewer, " +
            "t2.RECORD_REVIEW_SIGN as recordReviewSign, " +
            "t2.RECORD_TESTER_SIGN as inspectSignaturePhoto, " +
            "t2.RECORD_REVIEW_SIGN as reviewSignaturePhoto, " +
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
            "(ID, ENTRUSTMENT_ID, STATUS, REJECT_REASON, NEXT_HANDLER, " +
            "APPROVER, FILLER, RECORD_TESTER, RECORD_REVIEWER, " +
            "SOIL_PROPERTY, DESIGN_CAPACITY, HAMMER_WEIGHT, DROP_DISTANCE, TEST_BASIS, EQUIPMENT, CONCLUSION, " +
            "TEST_DATE, REPORT_DATE, REMARKS, " +
            "CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME, " +
            "DATA_JSON, RECORD_TESTER_SIGN, RECORD_REVIEW_SIGN, APPROVE_SIGNATURE_PHOTO, DATA_BLOCKS) " +
            "VALUES " +
            "(#{id,jdbcType=VARCHAR}, #{entrustmentId,jdbcType=VARCHAR}, #{status,jdbcType=VARCHAR}, #{rejectReason,jdbcType=VARCHAR}, #{nextHandler,jdbcType=VARCHAR}, " +
            "#{approver,jdbcType=VARCHAR}, #{filler,jdbcType=VARCHAR}, NVL(#{recordTester,jdbcType=VARCHAR}, #{tester,jdbcType=VARCHAR}), NVL(#{recordReviewer,jdbcType=VARCHAR}, #{reviewer,jdbcType=VARCHAR}), " +
            "#{soilProperty}, #{designCapacity}, #{hammerWeight}, #{dropDistance}, #{testBasis}, #{equipment}, #{conclusion}, " +
            "#{testDate}, #{reportDate}, #{remarks,jdbcType=VARCHAR}, " +
            "#{createBy,jdbcType=VARCHAR}, #{createTime,jdbcType=TIMESTAMP}, #{updateBy,jdbcType=VARCHAR}, #{updateTime,jdbcType=TIMESTAMP}, " +
            "#{dataJson,jdbcType=CLOB}, NVL(#{recordTesterSign,jdbcType=CLOB}, #{inspectSignaturePhoto,jdbcType=CLOB}), NVL(#{recordReviewSign,jdbcType=CLOB}, #{reviewSignaturePhoto,jdbcType=CLOB}), #{approveSignaturePhoto,jdbcType=CLOB}, #{dataBlocks,jdbcType=CLOB})")
    int insert(LightDynamicPenetration record);

    @Update("UPDATE (SELECT * FROM T_LIGHT_DYNAMIC_PENETRATION WHERE ENTRUSTMENT_ID = #{entrustmentId}) SET " +
            "STATUS = #{status}, " +
            "REJECT_REASON = #{rejectReason}, " +
            "NEXT_HANDLER = #{nextHandler}, " +
            "APPROVER = #{approver}, " +
            "FILLER = #{filler}, " +
            "RECORD_TESTER = NVL(#{recordTester}, #{tester}), " +
            "RECORD_REVIEWER = NVL(#{recordReviewer}, #{reviewer}), " +
            "SOIL_PROPERTY = #{soilProperty}, " +
            "DESIGN_CAPACITY = #{designCapacity}, " +
            "HAMMER_WEIGHT = #{hammerWeight}, " +
            "DROP_DISTANCE = #{dropDistance}, " +
            "TEST_BASIS = #{testBasis}, " +
            "EQUIPMENT = #{equipment}, " +
            "CONCLUSION = #{conclusion}, " +
            "TEST_DATE = #{testDate}, " +
            "REPORT_DATE = #{reportDate}, " +
            "REMARKS = #{remarks}, " +
            "UPDATE_BY = #{updateBy}, " +
            "UPDATE_TIME = #{updateTime}, " +
            "DATA_JSON = #{dataJson,jdbcType=CLOB}, " +
            "RECORD_TESTER_SIGN = NVL(#{recordTesterSign,jdbcType=CLOB}, #{inspectSignaturePhoto,jdbcType=CLOB}), " +
            "RECORD_REVIEW_SIGN = NVL(#{recordReviewSign,jdbcType=CLOB}, #{reviewSignaturePhoto,jdbcType=CLOB}), " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto,jdbcType=CLOB}, " +
            "DATA_BLOCKS = #{dataBlocks,jdbcType=CLOB}")
    int update(LightDynamicPenetration record);

    @Select("SELECT COUNT(*) FROM T_LIGHT_DYNAMIC_PENETRATION WHERE ID = #{id}")
    int countById(@Param("id") String id);

    @Update("UPDATE (SELECT * FROM T_LIGHT_DYNAMIC_PENETRATION WHERE ID = #{id}) SET " +
            "ENTRUSTMENT_ID = #{entrustmentId}, " +
            "STATUS = #{status}, " +
            "REJECT_REASON = #{rejectReason}, " +
            "NEXT_HANDLER = #{nextHandler}, " +
            "APPROVER = #{approver}, " +
            "FILLER = #{filler}, " +
            "RECORD_TESTER = NVL(#{recordTester}, #{tester}), " +
            "RECORD_REVIEWER = NVL(#{recordReviewer}, #{reviewer}), " +
            "SOIL_PROPERTY = #{soilProperty}, " +
            "DESIGN_CAPACITY = #{designCapacity}, " +
            "HAMMER_WEIGHT = #{hammerWeight}, " +
            "DROP_DISTANCE = #{dropDistance}, " +
            "TEST_BASIS = #{testBasis}, " +
            "EQUIPMENT = #{equipment}, " +
            "CONCLUSION = #{conclusion}, " +
            "TEST_DATE = #{testDate}, " +
            "REPORT_DATE = #{reportDate}, " +
            "REMARKS = #{remarks}, " +
            "UPDATE_BY = #{updateBy}, " +
            "UPDATE_TIME = #{updateTime}, " +
            "DATA_JSON = #{dataJson,jdbcType=CLOB}, " +
            "RECORD_TESTER_SIGN = NVL(#{recordTesterSign,jdbcType=CLOB}, #{inspectSignaturePhoto,jdbcType=CLOB}), " +
            "RECORD_REVIEW_SIGN = NVL(#{recordReviewSign,jdbcType=CLOB}, #{reviewSignaturePhoto,jdbcType=CLOB}), " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto,jdbcType=CLOB}, " +
            "DATA_BLOCKS = #{dataBlocks,jdbcType=CLOB}")
    int updateById(LightDynamicPenetration record);

    @Delete("DELETE FROM T_LIGHT_DYNAMIC_PENETRATION WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int deleteByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Delete("DELETE FROM T_LIGHT_DYNAMIC_PENETRATION WHERE ID = #{id}")
    int deleteById(@Param("id") String id);

    @Update("UPDATE T_LIGHT_DYNAMIC_PENETRATION SET STATUS = #{status} WHERE ID = #{id}")
    int updateStatusById(@Param("id") String id, @Param("status") String status);

    @Update("UPDATE (SELECT * FROM T_LIGHT_DYNAMIC_PENETRATION WHERE ID = #{id}) SET STATUS = #{status}, RECORD_REVIEW_SIGN = #{reviewSignPhoto,jdbcType=CLOB}")
    int updateStatusAndReviewSign(@Param("id") String id, @Param("status") String status, @Param("reviewSignPhoto") String reviewSignPhoto);

    @Update("UPDATE (SELECT * FROM T_LIGHT_DYNAMIC_PENETRATION WHERE ID = #{id}) SET STATUS = #{status}, APPROVE_SIGNATURE_PHOTO = #{approveSignPhoto,jdbcType=CLOB}")
    int updateStatusAndApproveSign(@Param("id") String id, @Param("status") String status, @Param("approveSignPhoto") String approveSignPhoto);

    @Update("UPDATE T_LIGHT_DYNAMIC_PENETRATION SET REPORT_STATUS = #{reportStatus}, RESULT_STATUS = #{resultStatus} WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int updateReportAndResultStatus(@Param("entrustmentId") String entrustmentId, @Param("reportStatus") String reportStatus, @Param("resultStatus") String resultStatus);

    @Update("UPDATE (SELECT * FROM T_LIGHT_DYNAMIC_PENETRATION WHERE ENTRUSTMENT_ID = #{entrustmentId,jdbcType=VARCHAR}) SET " +
            "RECORD_TESTER_SIGN = NVL(#{testerSign,jdbcType=CLOB,typeHandler=org.apache.ibatis.type.ClobTypeHandler}, RECORD_TESTER_SIGN), " +
            "RECORD_REVIEW_SIGN = NVL(#{reviewSign,jdbcType=CLOB,typeHandler=org.apache.ibatis.type.ClobTypeHandler}, RECORD_REVIEW_SIGN)")
    int updateRecordSignsByEntrustmentId(@Param("entrustmentId") String entrustmentId,
                                         @Param("testerSign") String testerSign,
                                         @Param("reviewSign") String reviewSign);
}
