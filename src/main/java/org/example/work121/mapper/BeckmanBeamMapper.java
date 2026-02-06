package org.example.work121.mapper;

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
            "FROM T_BECKMAN_BEAM t2 " +
            "LEFT JOIN JZS_ENTRUSTMENT t1 ON t2.ENTRUSTMENT_ID = t1.UNIFIED_NUMBER " +
            "WHERE t2.ID = #{id}")
    BeckmanBeam selectById(@Param("id") String id);

    @Update("UPDATE T_BECKMAN_BEAM SET " +
            "ENTRUSTMENT_ID = #{entrustmentId}, " +
            "SUBGRADE_TYPE = #{subgradeType}, " +
            "DEFLECTOMETER_TYPE = #{deflectometerType}, " +
            "AXLE_WEIGHT = #{axleWeight}, " +
            "TIRE_PRESSURE = #{tirePressure}, " +
            "TEST_LENGTH = #{testLength}, " +
            "DATA_JSON = #{dataJson} " +
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
            "FROM T_BECKMAN_BEAM t2 " +
            "LEFT JOIN JZS_ENTRUSTMENT t1 ON t2.ENTRUSTMENT_ID = t1.UNIFIED_NUMBER " +
            "WHERE t2.ENTRUSTMENT_ID = #{entrustmentId}")
    BeckmanBeam selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Insert("INSERT INTO T_BECKMAN_BEAM (" +
            "ID, ENTRUSTMENT_ID, SUBGRADE_TYPE, DEFLECTOMETER_TYPE, AXLE_WEIGHT, " +
            "TIRE_PRESSURE, TEST_LENGTH, DATA_JSON) " +
            "VALUES (" +
            "#{id}, #{entrustmentId}, #{subgradeType}, #{deflectometerType}, #{axleWeight}, " +
            "#{tirePressure}, #{testLength}, #{dataJson})")
    int insert(BeckmanBeam beckmanBeam);

    @Update("UPDATE T_BECKMAN_BEAM SET " +
            "SUBGRADE_TYPE = #{subgradeType}, " +
            "DEFLECTOMETER_TYPE = #{deflectometerType}, " +
            "AXLE_WEIGHT = #{axleWeight}, " +
            "TIRE_PRESSURE = #{tirePressure}, " +
            "TEST_LENGTH = #{testLength}, " +
            "DATA_JSON = #{dataJson} " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int update(BeckmanBeam beckmanBeam);
}
