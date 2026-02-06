package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.NuclearDensity;

@Mapper
public interface NuclearDensityMapper {

    @Select("SELECT " +
            "t2.ID as id, " +
            "t2.ENTRUSTMENT_ID as entrustmentId, " +
            "t2.NUCLEAR_MODEL as nuclearModel, " +
            "t2.TEST_DEPTH as testDepth, " +
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
            "FROM T_NUCLEAR_DENSITY t2 " +
            "LEFT JOIN JZS_ENTRUSTMENT t1 ON t2.ENTRUSTMENT_ID = t1.UNIFIED_NUMBER " +
            "WHERE t2.ID = #{id}")
    NuclearDensity selectById(@Param("id") String id);

    @Update("UPDATE T_NUCLEAR_DENSITY SET " +
            "ENTRUSTMENT_ID = #{entrustmentId}, " +
            "NUCLEAR_MODEL = #{nuclearModel}, " +
            "TEST_DEPTH = #{testDepth}, " +
            "DATA_JSON = #{dataJson} " +
            "WHERE ID = #{id}")
    int updateById(NuclearDensity nuclearDensity);

    @Select("SELECT " +
            "t2.ID as id, " +
            "t2.ENTRUSTMENT_ID as entrustmentId, " +
            "t2.NUCLEAR_MODEL as nuclearModel, " +
            "t2.TEST_DEPTH as testDepth, " +
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
            "FROM T_NUCLEAR_DENSITY t2 " +
            "LEFT JOIN JZS_ENTRUSTMENT t1 ON t2.ENTRUSTMENT_ID = t1.UNIFIED_NUMBER " +
            "WHERE t2.ENTRUSTMENT_ID = #{entrustmentId}")
    NuclearDensity selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Insert("INSERT INTO T_NUCLEAR_DENSITY (" +
            "ID, ENTRUSTMENT_ID, NUCLEAR_MODEL, TEST_DEPTH, DATA_JSON) " +
            "VALUES (" +
            "#{id}, #{entrustmentId}, #{nuclearModel}, #{testDepth}, #{dataJson})")
    int insert(NuclearDensity nuclearDensity);

    @Update("UPDATE T_NUCLEAR_DENSITY SET " +
            "NUCLEAR_MODEL = #{nuclearModel}, " +
            "TEST_DEPTH = #{testDepth}, " +
            "DATA_JSON = #{dataJson} " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int update(NuclearDensity nuclearDensity);
}
