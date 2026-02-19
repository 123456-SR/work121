package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.DensityTest;

@Mapper
public interface DensityTestMapper {

    @Select("SELECT " +
            "t2.ID as id, " +
            "t2.ENTRUSTMENT_ID as entrustmentId, " +
            "t2.SOIL_TYPE as soilType, " +
            "t2.RING_VOLUME as ringVolume, " +
            "t2.WET_WEIGHT as wetWeight, " +
            "t2.DRY_WEIGHT as dryWeight, " +
            "t2.WATER_CONTENT as waterContent, " +
            "t2.WET_DENSITY as wetDensity, " +
            "t2.DRY_DENSITY as dryDensity, " +
            "t2.MAX_DRY_DENSITY as maxDryDensity, " +
            "t2.MIN_DRY_DENSITY as minDryDensity, " +
            "t2.OPTIMUM_MOISTURE as optimumMoisture, " +
            "t2.COMPACTION_COEFFICIENT as compactionCoefficient, " +
            "t2.QUALIFIED_RATE as qualifiedRate, " +
            "t2.SAMPLE_NAME_STATUS as sampleNameStatus, " +
            "t2.DESIGN_INDEX as designIndex, " +
            "t2.TEST_RESULT as testResult, " +
            "t2.DATA_JSON as dataJson, " +
            "t2.REVIEW_SIGNATURE_PHOTO as reviewSignaturePhoto, " +
            "t2.INSPECT_SIGNATURE_PHOTO as inspectSignaturePhoto, " +
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
            "t1.CLIENT_DATE as commissionDate, " +
            "t1.PROJECT_NAME as projectName, " +
            "t1.CONSTRUCTION_PART as constructionPart, " +
            "t1.CONSTRUCTION_UNIT as constructionUnit, " +
            "t1.BUILDING_UNIT as buildingUnit, " +
            "t1.SAMPLE_NAME as sampleName, " +
            "t1.TEST_CATEGORY as testCategory, " +
            "t1.WITNESS_UNIT as witnessUnit, " +
            "t1.WITNESS as witness, " +
            "t1.REMARKS as remarks " +
            "FROM T_DENSITY_TEST t2 " +
            "LEFT JOIN JZS_ENTRUSTMENT t1 ON t2.ENTRUSTMENT_ID = t1.UNIFIED_NUMBER " +
            "WHERE t2.ENTRUSTMENT_ID = #{entrustmentId}")
    java.util.List<DensityTest> selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Select("SELECT * FROM T_DENSITY_TEST WHERE ID = #{id}")
    DensityTest selectById(@Param("id") String id);

    @Delete("DELETE FROM T_DENSITY_TEST WHERE ID = #{id}")
    int deleteById(@Param("id") String id);

    @Update("UPDATE T_DENSITY_TEST SET " +
            "ENTRUSTMENT_ID = #{entrustmentId}, " +
            "SOIL_TYPE = #{soilType}, " +
            "RING_VOLUME = #{ringVolume}, " +
            "WET_WEIGHT = #{wetWeight}, " +
            "DRY_WEIGHT = #{dryWeight}, " +
            "WATER_CONTENT = #{waterContent}, " +
            "WET_DENSITY = #{wetDensity}, " +
            "DRY_DENSITY = #{dryDensity}, " +
            "MAX_DRY_DENSITY = #{maxDryDensity}, " +
            "MIN_DRY_DENSITY = #{minDryDensity}, " +
            "OPTIMUM_MOISTURE = #{optimumMoisture}, " +
            "COMPACTION_COEFFICIENT = #{compactionCoefficient}, " +
            "QUALIFIED_RATE = #{qualifiedRate}, " +
            "SAMPLE_NAME_STATUS = #{sampleNameStatus}, " +
            "DESIGN_INDEX = #{designIndex}, " +
            "TEST_RESULT = #{testResult}, " +
            "TESTER = #{tester}, " +
            "REVIEWER = #{reviewer}, " +
            "APPROVER = #{approver}, " +
            "DATA_JSON = #{dataJson}, " +
            "REVIEW_SIGNATURE_PHOTO = #{reviewSignaturePhoto}, " +
            "INSPECT_SIGNATURE_PHOTO = #{inspectSignaturePhoto}, " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto}, " +
            "STATUS = #{status}, " +
            "REJECT_REASON = #{rejectReason}, " +
            "NEXT_HANDLER = #{nextHandler}, " +
            "UPDATE_BY = #{updateBy}, " +
            "UPDATE_TIME = #{updateTime} " +
            "WHERE ID = #{id}")
    int updateById(DensityTest densityTest);

    @Insert("INSERT INTO T_DENSITY_TEST (" +
            "ID, ENTRUSTMENT_ID, SOIL_TYPE, RING_VOLUME, WET_WEIGHT, DRY_WEIGHT, " +
            "WATER_CONTENT, WET_DENSITY, DRY_DENSITY, MAX_DRY_DENSITY, MIN_DRY_DENSITY, " +
            "OPTIMUM_MOISTURE, COMPACTION_COEFFICIENT, QUALIFIED_RATE, " +
            "SAMPLE_NAME_STATUS, DESIGN_INDEX, TEST_RESULT, TESTER, REVIEWER, APPROVER, DATA_JSON, " +
            "REVIEW_SIGNATURE_PHOTO, INSPECT_SIGNATURE_PHOTO, APPROVE_SIGNATURE_PHOTO, " +
            "STATUS, REJECT_REASON, NEXT_HANDLER, " +
            "CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME) " +
            "VALUES (" +
            "#{id}, #{entrustmentId}, #{soilType}, #{ringVolume}, #{wetWeight}, #{dryWeight}, " +
            "#{waterContent}, #{wetDensity}, #{dryDensity}, #{maxDryDensity}, #{minDryDensity}, " +
            "#{optimumMoisture}, #{compactionCoefficient}, #{qualifiedRate}, " +
            "#{sampleNameStatus}, #{designIndex}, #{testResult}, #{tester}, #{reviewer}, #{approver}, #{dataJson}, " +
            "#{reviewSignaturePhoto}, #{inspectSignaturePhoto}, #{approveSignaturePhoto}, " +
            "#{status}, #{rejectReason}, #{nextHandler}, " +
            "#{createBy}, #{createTime}, #{updateBy}, #{updateTime})")
    int insert(DensityTest densityTest);

    @Update("UPDATE T_DENSITY_TEST SET " +
            "SOIL_TYPE = #{soilType}, " +
            "RING_VOLUME = #{ringVolume}, " +
            "WET_WEIGHT = #{wetWeight}, " +
            "DRY_WEIGHT = #{dryWeight}, " +
            "WATER_CONTENT = #{waterContent}, " +
            "WET_DENSITY = #{wetDensity}, " +
            "DRY_DENSITY = #{dryDensity}, " +
            "MAX_DRY_DENSITY = #{maxDryDensity}, " +
            "MIN_DRY_DENSITY = #{minDryDensity}, " +
            "OPTIMUM_MOISTURE = #{optimumMoisture}, " +
            "COMPACTION_COEFFICIENT = #{compactionCoefficient}, " +
            "QUALIFIED_RATE = #{qualifiedRate}, " +
            "SAMPLE_NAME_STATUS = #{sampleNameStatus}, " +
            "DESIGN_INDEX = #{designIndex}, " +
            "TEST_RESULT = #{testResult}, " +
            "TESTER = #{tester}, " +
            "REVIEWER = #{reviewer}, " +
            "APPROVER = #{approver}, " +
            "DATA_JSON = #{dataJson}, " +
            "REVIEW_SIGNATURE_PHOTO = #{reviewSignaturePhoto}, " +
            "INSPECT_SIGNATURE_PHOTO = #{inspectSignaturePhoto}, " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto}, " +
            "STATUS = #{status}, " +
            "REJECT_REASON = #{rejectReason}, " +
            "NEXT_HANDLER = #{nextHandler}, " +
            "UPDATE_BY = #{updateBy}, " +
            "UPDATE_TIME = #{updateTime} " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int update(DensityTest densityTest);
}
