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
            "t2.SOIL_TYPE as soilType, " +
            "t2.PIT_VOLUME as pitVolume, " +
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
            // JZS_ENTRUSTMENT fields
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
            "FROM T_SAND_REPLACEMENT t2 " +
            "LEFT JOIN JZS_ENTRUSTMENT t1 ON t2.ENTRUSTMENT_ID = t1.UNIFIED_NUMBER " +
            "WHERE t2.ENTRUSTMENT_ID = #{entrustmentId}")
    List<SandReplacement> selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Select("SELECT " +
            "t2.ID as id, " +
            "t2.ENTRUSTMENT_ID as entrustmentId, " +
            "t2.SOIL_TYPE as soilType, " +
            "t2.PIT_VOLUME as pitVolume, " +
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
            "t2.REVIEW_SIGNATURE_PHOTO as reviewSignaturePhoto, " +
            "t2.INSPECT_SIGNATURE_PHOTO as inspectSignaturePhoto, " +
            "t2.APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto, " +
            "t2.CREATE_BY as createBy, " +
            "t2.CREATE_TIME as createTime, " +
            "t2.UPDATE_BY as updateBy, " +
            "t2.UPDATE_TIME as updateTime, " +
            // JZS_ENTRUSTMENT fields
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
            "FROM T_SAND_REPLACEMENT t2 " +
            "LEFT JOIN JZS_ENTRUSTMENT t1 ON t2.ENTRUSTMENT_ID = t1.UNIFIED_NUMBER " +
            "WHERE t2.ID = #{id}")
    SandReplacement selectById(@Param("id") String id);

    @Insert("INSERT INTO T_SAND_REPLACEMENT " +
            "(ID, ENTRUSTMENT_ID, SOIL_TYPE, PIT_VOLUME, WET_WEIGHT, DRY_WEIGHT, " +
            "WATER_CONTENT, WET_DENSITY, DRY_DENSITY, MAX_DRY_DENSITY, MIN_DRY_DENSITY, " +
            "COMPACTION_COEFFICIENT, QUALIFIED_RATE, DATA_JSON, " +
            "TESTER, REVIEWER, APPROVER, " +
            "REVIEW_SIGNATURE_PHOTO, INSPECT_SIGNATURE_PHOTO, APPROVE_SIGNATURE_PHOTO, " +
            "STATUS, REJECT_REASON, NEXT_HANDLER, " +
            "CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME) " +
            "VALUES " +
            "(#{id}, #{entrustmentId}, #{soilType}, #{pitVolume}, #{wetWeight}, #{dryWeight}, " +
            "#{waterContent}, #{wetDensity}, #{dryDensity}, #{maxDryDensity}, #{minDryDensity}, " +
            "#{compactionCoefficient}, #{qualifiedRate}, #{dataJson}, " +
            "#{tester}, #{reviewer}, #{approver}, " +
            "#{reviewSignaturePhoto}, #{inspectSignaturePhoto}, #{approveSignaturePhoto}, " +
            "#{status}, #{rejectReason}, #{nextHandler}, " +
            "#{createBy}, #{createTime}, #{updateBy}, #{updateTime})")
    int insert(SandReplacement entity);

    @Update("UPDATE T_SAND_REPLACEMENT SET " +
            "ENTRUSTMENT_ID = #{entrustmentId}, " +
            "SOIL_TYPE = #{soilType}, " +
            "PIT_VOLUME = #{pitVolume}, " +
            "WET_WEIGHT = #{wetWeight}, " +
            "DRY_WEIGHT = #{dryWeight}, " +
            "WATER_CONTENT = #{waterContent}, " +
            "WET_DENSITY = #{wetDensity}, " +
            "DRY_DENSITY = #{dryDensity}, " +
            "MAX_DRY_DENSITY = #{maxDryDensity}, " +
            "MIN_DRY_DENSITY = #{minDryDensity}, " +
            "COMPACTION_COEFFICIENT = #{compactionCoefficient}, " +
            "QUALIFIED_RATE = #{qualifiedRate}, " +
            "DATA_JSON = #{dataJson}, " +
            "TESTER = #{tester}, " +
            "REVIEWER = #{reviewer}, " +
            "APPROVER = #{approver}, " +
            "REVIEW_SIGNATURE_PHOTO = #{reviewSignaturePhoto}, " +
            "INSPECT_SIGNATURE_PHOTO = #{inspectSignaturePhoto}, " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto}, " +
            "STATUS = #{status}, " +
            "REJECT_REASON = #{rejectReason}, " +
            "NEXT_HANDLER = #{nextHandler}, " +
            "UPDATE_BY = #{updateBy}, " +
            "UPDATE_TIME = #{updateTime} " +
            "WHERE ID = #{id}")
    int update(SandReplacement entity);
}
