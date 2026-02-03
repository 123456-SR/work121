package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.LightDynamicPenetration;

@Mapper
public interface LightDynamicPenetrationMapper {

    @Select("SELECT " +
            "t1.WT_ID as id, " +
            "t1.WT_NUM as wtNum, " +
            "t1.WT_DATE as commissionDate, " +
            "t1.WT_UNIT as clientUnit, " +
            "t1.WT_MAN as client, " +
            "t1.GC_NAME as projectName, " +
            "t1.GC_AREA as projectArea, " +
            "t1.GC_SUB as constructionPart, " +
            "t1.SG_UNIT as constructionUnit, " +
            "t1.JS_UNIT as buildingUnit, " +
            "t1.JL_UNIT as supervisionUnit, " +
            "t1.JD_UNIT as witnessUnit, " +
            "t1.JD_MAN as witness, " +
            "t1.JZ_UNIT as buildingUnit2, " +
            "t1.JZ_MAN as buildingMan, " +
            "t1.KC_UNIT as surveyUnit, " +
            "t1.WT_REG_NAME as clientRegName, " +
            "t1.WT_STATUS as sampleStatus, " +
            "t1.WT_MAN_TEL as clientTel, " +
            "t1.WT_UNIT_ADDRESS as clientUnitAddress, " +
            "t1.WT_UNIT_TEL as clientUnitTel, " +
            "t1.YWDEPARTMENT as testCategory, " +
            "t2.SOIL_PROPERTY as soilProperty, " +
            "t2.DESIGN_CAPACITY as designCapacity, " +
            "t2.HAMMER_WEIGHT as hammerWeight, " +
            "t2.DROP_DISTANCE as dropDistance, " +
            "t2.DATA_JSON as dataJson, " +
            "t2.TEST_BASIS as testBasis, " +
            "t2.EQUIPMENT as equipment, " +
            "t2.CONCLUSION as conclusion, " +
            "t2.APPROVE_BY as approver, " +
            "t2.CHECK_BY as reviewer, " +
            "t2.TEST_BY as tester, " +
            "t2.REPORT_DATE as reportDate, " +
            "t2.REMARKS as remarks " +
            "FROM JC_CORE_WT_INFO t1 " +
            "LEFT JOIN LIGHT_DYNAMIC_PENETRATION t2 ON t1.WT_ID = t2.WT_ID " +
            "WHERE t1.WT_ID = #{id}")
    LightDynamicPenetration selectById(@Param("id") String id);

    @Insert("INSERT INTO LIGHT_DYNAMIC_PENETRATION " +
            "(WT_ID, SOIL_PROPERTY, DESIGN_CAPACITY, HAMMER_WEIGHT, DROP_DISTANCE, DATA_JSON, " +
            "TEST_BASIS, EQUIPMENT, CONCLUSION, APPROVE_BY, CHECK_BY, TEST_BY, REPORT_DATE, REMARKS) " +
            "VALUES " +
            "(#{id}, #{soilProperty}, #{designCapacity}, #{hammerWeight}, #{dropDistance}, #{dataJson}, " +
            "#{testBasis}, #{equipment}, #{conclusion}, #{approver}, #{reviewer}, #{tester}, #{reportDate}, #{remarks})")
    void insert(LightDynamicPenetration entity);

    @Update("UPDATE LIGHT_DYNAMIC_PENETRATION SET " +
            "SOIL_PROPERTY = #{soilProperty}, " +
            "DESIGN_CAPACITY = #{designCapacity}, " +
            "HAMMER_WEIGHT = #{hammerWeight}, " +
            "DROP_DISTANCE = #{dropDistance}, " +
            "DATA_JSON = #{dataJson}, " +
            "TEST_BASIS = #{testBasis}, " +
            "EQUIPMENT = #{equipment}, " +
            "CONCLUSION = #{conclusion}, " +
            "APPROVE_BY = #{approver}, " +
            "CHECK_BY = #{reviewer}, " +
            "TEST_BY = #{tester}, " +
            "REPORT_DATE = #{reportDate}, " +
            "REMARKS = #{remarks} " +
            "WHERE WT_ID = #{id}")
    void update(LightDynamicPenetration entity);

    @Select("SELECT COUNT(*) FROM LIGHT_DYNAMIC_PENETRATION WHERE WT_ID = #{id}")
    int countById(@Param("id") String id);
}
