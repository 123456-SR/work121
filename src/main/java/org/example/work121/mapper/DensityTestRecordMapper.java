package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.DensityTestRecord;

@Mapper
public interface DensityTestRecordMapper {

    @Select("SELECT " +
            "t2.ID as id, " +
            "t2.ENTRUSTMENT_ID as entrustmentId, " +
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
            "FROM T_DENSITY_TEST t2 " +
            "LEFT JOIN JZS_ENTRUSTMENT t1 ON t2.ENTRUSTMENT_ID = t1.UNIFIED_NUMBER " +
            "WHERE t2.ENTRUSTMENT_ID = #{entrustmentId}")
    DensityTestRecord selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Select("SELECT " +
            "t2.ID as id, " +
            "t2.ENTRUSTMENT_ID as entrustmentId, " +
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
            "FROM T_DENSITY_TEST t2 " +
            "LEFT JOIN JZS_ENTRUSTMENT t1 ON t2.ENTRUSTMENT_ID = t1.UNIFIED_NUMBER " +
            "WHERE t2.ID = #{id}")
    DensityTestRecord selectById(@Param("id") String id);

    @Insert("INSERT INTO T_DENSITY_TEST (ID, ENTRUSTMENT_ID, DATA_JSON) " +
            "VALUES (#{id}, #{entrustmentId}, #{dataJson})")
    int insert(DensityTestRecord record);

    @Update("UPDATE T_DENSITY_TEST SET " +
            "DATA_JSON = #{dataJson} " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int update(DensityTestRecord record);

    @Update("UPDATE T_DENSITY_TEST SET " +
            "ENTRUSTMENT_ID = #{entrustmentId}, " +
            "DATA_JSON = #{dataJson} " +
            "WHERE ID = #{id}")
    int updateById(DensityTestRecord record);
}
