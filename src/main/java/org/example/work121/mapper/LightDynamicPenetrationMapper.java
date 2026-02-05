package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.LightDynamicPenetration;

@Mapper
public interface LightDynamicPenetrationMapper {

    @Select("SELECT " +
            "UNIFIED_NUMBER as id, " +
            "ENTRUSTING_UNIT as clientUnit, " +
            "PROJECT_NAME as projectName, " +
            "ENTRUST_DATE as commissionDate, " +
            "CONSTRUCTION_PART as constructionPart, " +
            "TEST_DATE as testDate, " +
            "SOIL_PROPERTY as soilProperty, " +
            "REPORT_DATE as reportDate, " +
            "WITNESS_UNIT as witnessUnit, " +
            "WITNESS as witness, " +
            "DESIGN_CAPACITY as designCapacity, " +
            "HAMMER_WEIGHT as hammerWeight, " +
            "DROP_DISTANCE as dropDistance, " +
            "TEST_CATEGORY as testCategory, " +
            "TEST_BASIS as testBasis, " +
            "EQUIPMENT as equipment, " +
            "REMARKS as remarks, " +
            "APPROVE as approver, " +
            "REVIEW as reviewer, " +
            "INSPECT as tester, " +
            "COMPANY_NAME as constructionUnit, " + // Assuming Company Name maps to Construction Unit or similar, or just map it if entity has field
            "CONCLUSION as conclusion, " +
            "DATA_BLOCKS as dataJson " +
            "FROM JZS_LIGHT_DYNAMIC_PENETRATION " +
            "WHERE UNIFIED_NUMBER = #{id}")
    LightDynamicPenetration selectById(@Param("id") String id);

    @Insert("INSERT INTO JZS_LIGHT_DYNAMIC_PENETRATION " +
            "(UNIFIED_NUMBER, ENTRUSTING_UNIT, PROJECT_NAME, ENTRUST_DATE, CONSTRUCTION_PART, " +
            "TEST_DATE, SOIL_PROPERTY, REPORT_DATE, WITNESS_UNIT, WITNESS, DESIGN_CAPACITY, " +
            "HAMMER_WEIGHT, DROP_DISTANCE, TEST_CATEGORY, TEST_BASIS, EQUIPMENT, REMARKS, " +
            "APPROVE, REVIEW, INSPECT, CONCLUSION, DATA_BLOCKS) " +
            "VALUES " +
            "(#{id}, #{clientUnit}, #{projectName}, #{commissionDate}, #{constructionPart}, " +
            "#{testDate}, #{soilProperty}, #{reportDate}, #{witnessUnit}, #{witness}, #{designCapacity}, " +
            "#{hammerWeight}, #{dropDistance}, #{testCategory}, #{testBasis}, #{equipment}, #{remarks}, " +
            "#{approver}, #{reviewer}, #{tester}, #{conclusion}, #{dataJson})")
    void insert(LightDynamicPenetration entity);

    @Update("UPDATE JZS_LIGHT_DYNAMIC_PENETRATION SET " +
            "ENTRUSTING_UNIT = #{clientUnit}, " +
            "PROJECT_NAME = #{projectName}, " +
            "ENTRUST_DATE = #{commissionDate}, " +
            "CONSTRUCTION_PART = #{constructionPart}, " +
            "TEST_DATE = #{testDate}, " +
            "SOIL_PROPERTY = #{soilProperty}, " +
            "REPORT_DATE = #{reportDate}, " +
            "WITNESS_UNIT = #{witnessUnit}, " +
            "WITNESS = #{witness}, " +
            "DESIGN_CAPACITY = #{designCapacity}, " +
            "HAMMER_WEIGHT = #{hammerWeight}, " +
            "DROP_DISTANCE = #{dropDistance}, " +
            "TEST_CATEGORY = #{testCategory}, " +
            "TEST_BASIS = #{testBasis}, " +
            "EQUIPMENT = #{equipment}, " +
            "REMARKS = #{remarks}, " +
            "APPROVE = #{approver}, " +
            "REVIEW = #{reviewer}, " +
            "INSPECT = #{tester}, " +
            "CONCLUSION = #{conclusion}, " +
            "DATA_BLOCKS = #{dataJson} " +
            "WHERE UNIFIED_NUMBER = #{id}")
    void update(LightDynamicPenetration entity);

    @Select("SELECT COUNT(*) FROM JZS_LIGHT_DYNAMIC_PENETRATION WHERE UNIFIED_NUMBER = #{id}")
    int countById(@Param("id") String id);
}
