package org.example.work121.mapper;

import java.util.List;
import org.apache.ibatis.annotations.*;
import org.example.work121.entity.BeckmanBeam;

@Mapper
public interface BeckmanBeamMapper {

    @Select("SELECT " +
            "t2.ID as id, " +
            "t2.ENTRUSTMENT_ID as entrustmentId, " +
            "t2.SUBGRADE_TYPE as subgradeType, " +
            "t2.DEFLECTOMETER_TYPE as deflectometerType, " +
            "t2.AXLE_WEIGHT as axleWeight, " +
            "t2.TIRE_PRESSURE as tirePressure, " +
            "t2.TEST_LENGTH as testLength, " +
            "t2.DATA_JSON as dataJson, " +
            "t2.RECORD_REVIEWER as reviewer, " +
            "t2.RECORD_TESTER as tester, " +
            "t2.RECORD_REVIEW_SIGN as reviewSignaturePhoto, " +
            "t2.RECORD_TESTER_SIGN as inspectSignaturePhoto, " +
            "t2.APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto, " +
            "CASE WHEN REGEXP_LIKE(t2.STATUS, '^[0-9]+$') THEN t2.STATUS ELSE '0' END as status, " +
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
            "FROM T_BECKMAN_BEAM t2 " +
            "LEFT JOIN T_ENTRUSTMENT t1 ON t2.ENTRUSTMENT_ID = t1.WT_NUM " +
            "WHERE t2.ID = #{id}")
    BeckmanBeam selectById(@Param("id") String id);

    @Update("UPDATE T_BECKMAN_BEAM SET " +
            "ENTRUSTMENT_ID = #{entrustmentId}, " +
            "SUBGRADE_TYPE = #{subgradeType}, " +
            "DEFLECTOMETER_TYPE = #{deflectometerType}, " +
            "AXLE_WEIGHT = #{axleWeight}, " +
            "TIRE_PRESSURE = #{tirePressure}, " +
            "TEST_LENGTH = #{testLength}, " +
            "DATA_JSON = #{dataJson}, " +
            "RECORD_REVIEW_SIGN = NVL(#{recordReviewSign}, #{reviewSignaturePhoto}), " +
            "RECORD_TESTER_SIGN = NVL(#{recordTesterSign}, #{inspectSignaturePhoto}), " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto}, " +
            "STATUS = #{status}, " +
            "REJECT_REASON = #{rejectReason}, " +
            "NEXT_HANDLER = #{nextHandler}, " +
            "UPDATE_BY = #{updateBy}, " +
            "UPDATE_TIME = #{updateTime} " +
            "WHERE ID = #{id}")
    int updateById(BeckmanBeam beckmanBeam);

    @Select("SELECT " +
            "t2.ID as id, " +
            "t2.ENTRUSTMENT_ID as entrustmentId, " +
            "t2.SUBGRADE_TYPE as subgradeType, " +
            "t2.DEFLECTOMETER_TYPE as deflectometerType, " +
            "t2.AXLE_WEIGHT as axleWeight, " +
            "t2.TIRE_PRESSURE as tirePressure, " +
            "t2.TEST_LENGTH as testLength, " +
            "t2.DATA_JSON as dataJson, " +
            "t2.RECORD_REVIEWER as reviewer, " +
            "t2.RECORD_TESTER as tester, " +
            "t2.RECORD_REVIEW_SIGN as reviewSignaturePhoto, " +
            "t2.RECORD_TESTER_SIGN as inspectSignaturePhoto, " +
            "t2.APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto, " +
            "CASE WHEN REGEXP_LIKE(t2.STATUS, '^[0-9]+$') THEN t2.STATUS ELSE '0' END as status, " +
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
            "FROM T_BECKMAN_BEAM t2 " +
            "LEFT JOIN T_ENTRUSTMENT t1 ON t2.ENTRUSTMENT_ID = t1.WT_NUM " +
            "WHERE t2.ENTRUSTMENT_ID = #{entrustmentId}")
    List<BeckmanBeam> selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Insert("INSERT INTO T_BECKMAN_BEAM (" +
            "ID, ENTRUSTMENT_ID, SUBGRADE_TYPE, DEFLECTOMETER_TYPE, AXLE_WEIGHT, " +
            "TIRE_PRESSURE, TEST_LENGTH, DATA_JSON, " +
            "RECORD_TESTER, RECORD_REVIEWER, APPROVER, " +
            "RECORD_REVIEW_SIGN, RECORD_TESTER_SIGN, APPROVE_SIGNATURE_PHOTO, " +
            "STATUS, REJECT_REASON, NEXT_HANDLER, " +
            "CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME) " +
            "VALUES (" +
            "#{id}, #{entrustmentId}, #{subgradeType}, #{deflectometerType}, #{axleWeight}, " +
            "#{tirePressure}, #{testLength}, #{dataJson}, " +
            "#{tester}, #{reviewer}, #{approver}, " +
            "NVL(#{recordReviewSign}, #{reviewSignaturePhoto}), NVL(#{recordTesterSign}, #{inspectSignaturePhoto}), #{approveSignaturePhoto}, " +
            "#{status}, #{rejectReason}, #{nextHandler}, " +
            "#{createBy}, #{createTime}, #{updateBy}, #{updateTime})")
    int insert(BeckmanBeam beckmanBeam);

    @Update("UPDATE T_BECKMAN_BEAM SET " +
            "SUBGRADE_TYPE = #{subgradeType}, " +
            "DEFLECTOMETER_TYPE = #{deflectometerType}, " +
            "AXLE_WEIGHT = #{axleWeight}, " +
            "TIRE_PRESSURE = #{tirePressure}, " +
            "TEST_LENGTH = #{testLength}, " +
            "DATA_JSON = #{dataJson}, " +
            "RECORD_TESTER = #{tester}, " +
            "RECORD_REVIEWER = #{reviewer}, " +
            "APPROVER = #{approver}, " +
            "RECORD_REVIEW_SIGN = NVL(#{recordReviewSign}, #{reviewSignaturePhoto}), " +
            "RECORD_TESTER_SIGN = NVL(#{recordTesterSign}, #{inspectSignaturePhoto}), " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto}, " +
            "UPDATE_BY = #{updateBy}, " +
            "UPDATE_TIME = #{updateTime} " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int update(BeckmanBeam beckmanBeam);

    @Delete("DELETE FROM T_BECKMAN_BEAM WHERE ID = #{id}")
    int deleteById(@Param("id") String id);

    @Update("UPDATE T_BECKMAN_BEAM SET STATUS = #{status} WHERE ID = #{id}")
    int updateStatusById(@Param("id") String id, @Param("status") String status);

    @Update("UPDATE T_BECKMAN_BEAM SET STATUS = #{status}, RECORD_REVIEW_SIGN = #{reviewSignPhoto} WHERE ID = #{id}")
    int updateStatusAndReviewSign(@Param("id") String id, @Param("status") String status, @Param("reviewSignPhoto") String reviewSignPhoto);

    @Update("UPDATE T_BECKMAN_BEAM SET STATUS = #{status}, APPROVE_SIGNATURE_PHOTO = #{approveSignPhoto} WHERE ID = #{id}")
    int updateStatusAndApproveSign(@Param("id") String id, @Param("status") String status, @Param("approveSignPhoto") String approveSignPhoto);

    @Update("UPDATE T_BECKMAN_BEAM SET REPORT_STATUS = #{reportStatus}, RESULT_STATUS = #{resultStatus} WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int updateReportAndResultStatus(@Param("entrustmentId") String entrustmentId, @Param("reportStatus") String reportStatus, @Param("resultStatus") String resultStatus);
}

