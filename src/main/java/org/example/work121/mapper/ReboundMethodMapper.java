package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.ReboundMethod;

@Mapper
public interface ReboundMethodMapper {

    @Select("SELECT " +
            "t2.ID as id, " +
            "t2.ENTRUSTMENT_ID as entrustmentId, " +
            "t2.STRUCTURE_PART as structurePart, " +
            "t2.CONCRETE_GRADE as concreteGrade, " +
            "t2.MOLDING_DATE as moldingDate, " +
            "t2.AGE as age, " +
            "t2.INSTRUMENT_MODEL as instrumentModel, " +
            "t2.CALIBRATION_NO as calibrationNo, " +
            "t2.ZONE_COUNT as zoneCount, " +
            "t2.POURING_DIRECTION as pouringDirection, " +
            "t2.TEST_ANGLE as testAngle, " +
            "t2.PUMPING_METHOD as pumpingMethod, " +
            "t2.PUMPING as pumping, " +
            "t2.DESIGN_STRENGTH as designStrength, " +
            "t2.CARBONATION_DEPTH as carbonationDepth, " +
            "t2.TEST_RESULT as testResult, " +
            "t2.DATA_JSON as dataJson, " +
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
            "FROM T_REBOUND_METHOD t2 " +
            "LEFT JOIN JZS_ENTRUSTMENT t1 ON t2.ENTRUSTMENT_ID = t1.UNIFIED_NUMBER " +
            "WHERE t2.ID = #{id}")
    ReboundMethod selectById(@Param("id") String id);

    @Select("SELECT " +
            "t2.ID as id, " +
            "t2.ENTRUSTMENT_ID as entrustmentId, " +
            "t2.STRUCTURE_PART as structurePart, " +
            "t2.CONCRETE_GRADE as concreteGrade, " +
            "t2.MOLDING_DATE as moldingDate, " +
            "t2.AGE as age, " +
            "t2.INSTRUMENT_MODEL as instrumentModel, " +
            "t2.CALIBRATION_NO as calibrationNo, " +
            "t2.ZONE_COUNT as zoneCount, " +
            "t2.POURING_DIRECTION as pouringDirection, " +
            "t2.TEST_ANGLE as testAngle, " +
            "t2.PUMPING_METHOD as pumpingMethod, " +
            "t2.PUMPING as pumping, " +
            "t2.DESIGN_STRENGTH as designStrength, " +
            "t2.CARBONATION_DEPTH as carbonationDepth, " +
            "t2.TEST_RESULT as testResult, " +
            "t2.DATA_JSON as dataJson, " +
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
            "FROM T_REBOUND_METHOD t2 " +
            "LEFT JOIN JZS_ENTRUSTMENT t1 ON t2.ENTRUSTMENT_ID = t1.UNIFIED_NUMBER " +
            "WHERE t2.ENTRUSTMENT_ID = #{entrustmentId}")
    ReboundMethod selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Insert("INSERT INTO T_REBOUND_METHOD (" +
            "ID, ENTRUSTMENT_ID, STRUCTURE_PART, CONCRETE_GRADE, MOLDING_DATE, AGE, " +
            "INSTRUMENT_MODEL, CALIBRATION_NO, ZONE_COUNT, POURING_DIRECTION, " +
            "TEST_ANGLE, PUMPING_METHOD, PUMPING, DESIGN_STRENGTH, CARBONATION_DEPTH, " +
            "TEST_RESULT, DATA_JSON) " +
            "VALUES (" +
            "#{id}, #{entrustmentId}, #{structurePart}, #{concreteGrade}, #{moldingDate}, #{age}, " +
            "#{instrumentModel}, #{calibrationNo}, #{zoneCount}, #{pouringDirection}, " +
            "#{testAngle}, #{pumpingMethod}, #{pumping}, #{designStrength}, #{carbonationDepth}, " +
            "#{testResult}, #{dataJson})")
    int insert(ReboundMethod reboundMethod);

    @Update("UPDATE T_REBOUND_METHOD SET " +
            "STRUCTURE_PART = #{structurePart}, " +
            "CONCRETE_GRADE = #{concreteGrade}, " +
            "MOLDING_DATE = #{moldingDate}, " +
            "AGE = #{age}, " +
            "INSTRUMENT_MODEL = #{instrumentModel}, " +
            "CALIBRATION_NO = #{calibrationNo}, " +
            "ZONE_COUNT = #{zoneCount}, " +
            "POURING_DIRECTION = #{pouringDirection}, " +
            "TEST_ANGLE = #{testAngle}, " +
            "PUMPING_METHOD = #{pumpingMethod}, " +
            "PUMPING = #{pumping}, " +
            "DESIGN_STRENGTH = #{designStrength}, " +
            "CARBONATION_DEPTH = #{carbonationDepth}, " +
            "TEST_RESULT = #{testResult}, " +
            "DATA_JSON = #{dataJson} " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int update(ReboundMethod reboundMethod);

    @Update("UPDATE T_REBOUND_METHOD SET " +
            "ENTRUSTMENT_ID = #{entrustmentId}, " +
            "STRUCTURE_PART = #{structurePart}, " +
            "CONCRETE_GRADE = #{concreteGrade}, " +
            "MOLDING_DATE = #{moldingDate}, " +
            "AGE = #{age}, " +
            "INSTRUMENT_MODEL = #{instrumentModel}, " +
            "CALIBRATION_NO = #{calibrationNo}, " +
            "ZONE_COUNT = #{zoneCount}, " +
            "POURING_DIRECTION = #{pouringDirection}, " +
            "TEST_ANGLE = #{testAngle}, " +
            "PUMPING_METHOD = #{pumpingMethod}, " +
            "PUMPING = #{pumping}, " +
            "DESIGN_STRENGTH = #{designStrength}, " +
            "CARBONATION_DEPTH = #{carbonationDepth}, " +
            "TEST_RESULT = #{testResult}, " +
            "DATA_JSON = #{dataJson} " +
            "WHERE ID = #{id}")
    int updateById(ReboundMethod reboundMethod);
}
