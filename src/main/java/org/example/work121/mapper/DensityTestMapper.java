package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.DensityTest;

@Mapper
public interface DensityTestMapper {

    @Select("SELECT " +
            "t2.ID as id, " +
            "t2.ENTRUSTMENT_ID as entrustmentId, " +
            "t2.SOIL_TYPE as soilType, " +
            "t2.PIT_VOLUME as ringVolume, " + // 表结构里是 PIT_VOLUME，这里别名成 ringVolume
            "t2.WET_WEIGHT as wetWeight, " +
            "t2.DRY_WEIGHT as dryWeight, " +
            "t2.WATER_CONTENT as waterContent, " +
            "t2.WET_DENSITY as wetDensity, " +
            "t2.DRY_DENSITY as dryDensity, " +
            "t2.MAX_DRY_DENSITY as maxDryDensity, " +
            "t2.MIN_DRY_DENSITY as minDryDensity, " +
            "t2.COMPACTION_COEFFICIENT as compactionCoefficient, " +
            "t2.QUALIFIED_RATE as qualifiedRate, " +
            // 下面这些业务字段（optimumMoisture / sampleNameStatus / designIndex / testResult 等）
            // 统一放在 DATA_JSON 里，不再单独建物理列
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
    java.util.List<DensityTest> selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Select("SELECT " +
            "t2.ID as id, " +
            "t2.ENTRUSTMENT_ID as entrustmentId, " +
            "t2.SOIL_TYPE as soilType, " +
            "t2.PIT_VOLUME as ringVolume, " +
            "t2.WET_WEIGHT as wetWeight, " +
            "t2.DRY_WEIGHT as dryWeight, " +
            "t2.WATER_CONTENT as waterContent, " +
            "t2.WET_DENSITY as wetDensity, " +
            "t2.DRY_DENSITY as dryDensity, " +
            "t2.MAX_DRY_DENSITY as maxDryDensity, " +
            "t2.MIN_DRY_DENSITY as minDryDensity, " +
            "t2.COMPACTION_COEFFICIENT as compactionCoefficient, " +
            "t2.QUALIFIED_RATE as qualifiedRate, " +
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
    DensityTest selectById(@Param("id") String id);

    @Delete("DELETE FROM T_DENSITY_TEST WHERE ID = #{id}")
    int deleteById(@Param("id") String id);

    @Update("UPDATE (SELECT * FROM T_DENSITY_TEST WHERE ID = #{id}) SET " +
            "ENTRUSTMENT_ID = #{entrustmentId}, " +
            "SOIL_TYPE = #{soilType}, " +
            "PIT_VOLUME = #{ringVolume}, " +
            "WET_WEIGHT = #{wetWeight}, " +
            "DRY_WEIGHT = #{dryWeight}, " +
            "WATER_CONTENT = #{waterContent}, " +
            "WET_DENSITY = #{wetDensity}, " +
            "DRY_DENSITY = #{dryDensity}, " +
            "MAX_DRY_DENSITY = #{maxDryDensity}, " +
            "MIN_DRY_DENSITY = #{minDryDensity}, " +
            "COMPACTION_COEFFICIENT = #{compactionCoefficient}, " +
            "QUALIFIED_RATE = #{qualifiedRate}, " +
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
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto,jdbcType=CLOB}")
    int updateById(DensityTest densityTest);

    @Insert("INSERT INTO T_DENSITY_TEST (" +
            "ID, ENTRUSTMENT_ID, SOIL_TYPE, PIT_VOLUME, WET_WEIGHT, DRY_WEIGHT, " +
            "WATER_CONTENT, WET_DENSITY, DRY_DENSITY, MAX_DRY_DENSITY, MIN_DRY_DENSITY, " +
            "COMPACTION_COEFFICIENT, QUALIFIED_RATE, " +
            "RECORD_TESTER, RECORD_REVIEWER, APPROVER, " +
            "STATUS, REJECT_REASON, NEXT_HANDLER, " +
            "CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME, " +
            "DATA_JSON, RECORD_REVIEW_SIGN, RECORD_TESTER_SIGN, APPROVE_SIGNATURE_PHOTO) " +
            "VALUES (" +
            "#{id,jdbcType=VARCHAR}, #{entrustmentId,jdbcType=VARCHAR}, #{soilType,jdbcType=VARCHAR}, #{ringVolume,jdbcType=VARCHAR}, #{wetWeight,jdbcType=VARCHAR}, #{dryWeight,jdbcType=VARCHAR}, " +
            "#{waterContent,jdbcType=VARCHAR}, #{wetDensity,jdbcType=VARCHAR}, #{dryDensity,jdbcType=VARCHAR}, #{maxDryDensity,jdbcType=VARCHAR}, #{minDryDensity,jdbcType=VARCHAR}, " +
            "#{compactionCoefficient,jdbcType=VARCHAR}, #{qualifiedRate,jdbcType=VARCHAR}, " +
            "#{tester,jdbcType=VARCHAR}, #{reviewer,jdbcType=VARCHAR}, #{approver,jdbcType=VARCHAR}, " +
            "#{status,jdbcType=VARCHAR}, #{rejectReason,jdbcType=VARCHAR}, #{nextHandler,jdbcType=VARCHAR}, " +
            "#{createBy,jdbcType=VARCHAR}, #{createTime,jdbcType=TIMESTAMP}, #{updateBy,jdbcType=VARCHAR}, #{updateTime,jdbcType=TIMESTAMP}, " +
            "#{dataJson,jdbcType=CLOB}, NVL(#{recordReviewSign,jdbcType=CLOB}, #{reviewSignaturePhoto,jdbcType=CLOB}), NVL(#{recordTesterSign,jdbcType=CLOB}, #{inspectSignaturePhoto,jdbcType=CLOB}), #{approveSignaturePhoto,jdbcType=CLOB})")
    int insert(DensityTest densityTest);

    @Update("UPDATE (SELECT * FROM T_DENSITY_TEST WHERE ENTRUSTMENT_ID = #{entrustmentId}) SET " +
            "SOIL_TYPE = #{soilType}, " +
            "PIT_VOLUME = #{ringVolume}, " +
            "WET_WEIGHT = #{wetWeight}, " +
            "DRY_WEIGHT = #{dryWeight}, " +
            "WATER_CONTENT = #{waterContent}, " +
            "WET_DENSITY = #{wetDensity}, " +
            "DRY_DENSITY = #{dryDensity}, " +
            "MAX_DRY_DENSITY = #{maxDryDensity}, " +
            "MIN_DRY_DENSITY = #{minDryDensity}, " +
            "COMPACTION_COEFFICIENT = #{compactionCoefficient}, " +
            "QUALIFIED_RATE = #{qualifiedRate}, " +
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
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto,jdbcType=CLOB}")
    int update(DensityTest densityTest);

    @Update("UPDATE T_DENSITY_TEST SET STATUS = #{status} WHERE ID = #{id}")
    int updateStatusById(@Param("id") String id, @Param("status") String status);

    @Update("UPDATE (SELECT * FROM T_DENSITY_TEST WHERE ID = #{id}) SET STATUS = #{status}, RECORD_REVIEW_SIGN = #{reviewSignPhoto,jdbcType=CLOB}")
    int updateStatusAndReviewSign(@Param("id") String id, @Param("status") String status, @Param("reviewSignPhoto") String reviewSignPhoto);

    @Update("UPDATE (SELECT * FROM T_DENSITY_TEST WHERE ID = #{id}) SET STATUS = #{status}, APPROVE_SIGNATURE_PHOTO = #{approveSignPhoto,jdbcType=CLOB}")
    int updateStatusAndApproveSign(@Param("id") String id, @Param("status") String status, @Param("approveSignPhoto") String approveSignPhoto);

    @Update("UPDATE T_DENSITY_TEST SET REPORT_STATUS = #{reportStatus}, RESULT_STATUS = #{resultStatus} WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int updateReportAndResultStatus(@Param("entrustmentId") String entrustmentId, @Param("reportStatus") String reportStatus, @Param("resultStatus") String resultStatus);

    @Update("UPDATE (SELECT * FROM T_DENSITY_TEST WHERE ENTRUSTMENT_ID = #{entrustmentId,jdbcType=VARCHAR}) SET " +
            "RECORD_TESTER_SIGN = NVL(#{testerSign,jdbcType=CLOB,typeHandler=org.apache.ibatis.type.ClobTypeHandler}, RECORD_TESTER_SIGN), " +
            "RECORD_REVIEW_SIGN = NVL(#{reviewSign,jdbcType=CLOB,typeHandler=org.apache.ibatis.type.ClobTypeHandler}, RECORD_REVIEW_SIGN)")
    int updateRecordSignsByEntrustmentId(@Param("entrustmentId") String entrustmentId,
                                         @Param("testerSign") String testerSign,
                                         @Param("reviewSign") String reviewSign);
}
