package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.LightDynamicPenetration;
import java.util.List;

@Mapper
public interface LightDynamicPenetrationMapper {

    @Select("SELECT " +
            "t1.ID as id, " +
            "t1.ENTRUSTMENT_ID as entrustmentId, " +
            "t1.ENTRUSTING_UNIT as clientUnit, " +
            "t1.PROJECT_NAME as projectName, " +
            "t1.ENTRUST_DATE as commissionDate, " +
            "t1.CONSTRUCTION_PART as constructionPart, " +
            "t1.TEST_DATE as testDate, " +
            "t1.SOIL_PROPERTY as soilProperty, " +
            "t1.REPORT_DATE as reportDate, " +
            "t1.WITNESS_UNIT as witnessUnit, " +
            "t1.WITNESS as witness, " +
            "t1.DESIGN_CAPACITY as designCapacity, " +
            "t1.HAMMER_WEIGHT as hammerWeight, " +
            "t1.DROP_DISTANCE as dropDistance, " +
            "t1.TEST_CATEGORY as testCategory, " +
            "t1.TEST_BASIS as testBasis, " +
            "t1.EQUIPMENT as equipment, " +
            "t1.REMARKS as remarks, " +
            "t1.APPROVE as approver, " +
            "t1.REVIEW as reviewer, " +
            "t1.INSPECT as tester, " +
            "t1.COMPANY_NAME as constructionUnit, " +
            "t1.CONCLUSION as conclusion, " +
            "t1.DATA_JSON as dataJson, " +
            "t1.REVIEW_SIGNATURE_PHOTO as reviewSignaturePhoto, " +
            "t1.INSPECT_SIGNATURE_PHOTO as inspectSignaturePhoto, " +
            "t1.APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto, " +
            "t1.STATUS as status, " +
            "t1.REJECT_REASON as rejectReason, " +
            "t1.NEXT_HANDLER as nextHandler, " +
            "t1.CREATE_BY as createBy, " +
            "t1.CREATE_TIME as createTime, " +
            "t1.UPDATE_BY as updateBy, " +
            "t1.UPDATE_TIME as updateTime, " +
            "t2.WT_NUM as wtNum " +
            "FROM JZS_LIGHT_DYNAMIC_PENETRATION t1 " +
            "LEFT JOIN JC_CORE_WT_INFO t2 ON t1.ENTRUSTMENT_ID = t2.WT_ID " +
            "WHERE t1.ENTRUSTMENT_ID = #{entrustmentId}")
    List<LightDynamicPenetration> selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Select("SELECT " +
            "t1.ID as id, " +
            "t1.ENTRUSTMENT_ID as entrustmentId, " +
            "t1.ENTRUSTING_UNIT as clientUnit, " +
            "t1.PROJECT_NAME as projectName, " +
            "t1.ENTRUST_DATE as commissionDate, " +
            "t1.CONSTRUCTION_PART as constructionPart, " +
            "t1.TEST_DATE as testDate, " +
            "t1.SOIL_PROPERTY as soilProperty, " +
            "t1.REPORT_DATE as reportDate, " +
            "t1.WITNESS_UNIT as witnessUnit, " +
            "t1.WITNESS as witness, " +
            "t1.DESIGN_CAPACITY as designCapacity, " +
            "t1.HAMMER_WEIGHT as hammerWeight, " +
            "t1.DROP_DISTANCE as dropDistance, " +
            "t1.TEST_CATEGORY as testCategory, " +
            "t1.TEST_BASIS as testBasis, " +
            "t1.EQUIPMENT as equipment, " +
            "t1.REMARKS as remarks, " +
            "t1.APPROVE as approver, " +
            "t1.REVIEW as reviewer, " +
            "t1.INSPECT as tester, " +
            "t1.COMPANY_NAME as constructionUnit, " +
            "t1.CONCLUSION as conclusion, " +
            "t1.DATA_JSON as dataJson, " +
            "t1.REVIEW_SIGNATURE_PHOTO as reviewSignaturePhoto, " +
            "t1.INSPECT_SIGNATURE_PHOTO as inspectSignaturePhoto, " +
            "t1.APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto, " +
            "t1.STATUS as status, " +
            "t1.REJECT_REASON as rejectReason, " +
            "t1.NEXT_HANDLER as nextHandler, " +
            "t1.CREATE_BY as createBy, " +
            "t1.CREATE_TIME as createTime, " +
            "t1.UPDATE_BY as updateBy, " +
            "t1.UPDATE_TIME as updateTime, " +
            "t2.WT_NUM as wtNum " +
            "FROM JZS_LIGHT_DYNAMIC_PENETRATION t1 " +
            "LEFT JOIN JC_CORE_WT_INFO t2 ON t1.ENTRUSTMENT_ID = t2.WT_ID " +
            "WHERE t1.ID = #{id}")
    LightDynamicPenetration selectById(@Param("id") String id);

    @Insert("INSERT INTO JZS_LIGHT_DYNAMIC_PENETRATION " +
            "(ID, ENTRUSTMENT_ID, ENTRUSTING_UNIT, PROJECT_NAME, ENTRUST_DATE, CONSTRUCTION_PART, " +
            "TEST_DATE, SOIL_PROPERTY, REPORT_DATE, WITNESS_UNIT, WITNESS, DESIGN_CAPACITY, " +
            "HAMMER_WEIGHT, DROP_DISTANCE, TEST_CATEGORY, TEST_BASIS, EQUIPMENT, REMARKS, " +
            "APPROVE, REVIEW, INSPECT, CONCLUSION, DATA_JSON, " +
            "REVIEW_SIGNATURE_PHOTO, INSPECT_SIGNATURE_PHOTO, APPROVE_SIGNATURE_PHOTO, " +
            "STATUS, REJECT_REASON, NEXT_HANDLER, " +
            "CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME) " +
            "VALUES " +
            "(#{id,jdbcType=VARCHAR}, #{entrustmentId,jdbcType=VARCHAR}, #{clientUnit,jdbcType=VARCHAR}, #{projectName,jdbcType=VARCHAR}, #{commissionDate,jdbcType=TIMESTAMP}, #{constructionPart,jdbcType=VARCHAR}, " +
            "#{testDate,jdbcType=TIMESTAMP}, #{soilProperty,jdbcType=VARCHAR}, #{reportDate,jdbcType=TIMESTAMP}, #{witnessUnit,jdbcType=VARCHAR}, #{witness,jdbcType=VARCHAR}, #{designCapacity,jdbcType=VARCHAR}, " +
            "#{hammerWeight,jdbcType=VARCHAR}, #{dropDistance,jdbcType=VARCHAR}, #{testCategory,jdbcType=VARCHAR}, #{testBasis,jdbcType=VARCHAR}, #{equipment,jdbcType=VARCHAR}, #{remarks,jdbcType=VARCHAR}, " +
            "#{approver,jdbcType=VARCHAR}, #{reviewer,jdbcType=VARCHAR}, #{tester,jdbcType=VARCHAR}, #{conclusion,jdbcType=VARCHAR}, #{dataJson,jdbcType=CLOB}, " +
            "#{reviewSignaturePhoto,jdbcType=CLOB}, #{inspectSignaturePhoto,jdbcType=CLOB}, #{approveSignaturePhoto,jdbcType=CLOB}, " +
            "#{status,jdbcType=VARCHAR}, #{rejectReason,jdbcType=VARCHAR}, #{nextHandler,jdbcType=VARCHAR}, " +
            "#{createBy,jdbcType=VARCHAR}, #{createTime,jdbcType=TIMESTAMP}, #{updateBy,jdbcType=VARCHAR}, #{updateTime,jdbcType=TIMESTAMP})")
    int insert(LightDynamicPenetration entity);

    @Update("UPDATE JZS_LIGHT_DYNAMIC_PENETRATION SET " +
            "ENTRUSTMENT_ID = #{entrustmentId,jdbcType=VARCHAR}, " +
            "ENTRUSTING_UNIT = #{clientUnit,jdbcType=VARCHAR}, " +
            "PROJECT_NAME = #{projectName,jdbcType=VARCHAR}, " +
            "ENTRUST_DATE = #{commissionDate,jdbcType=TIMESTAMP}, " +
            "CONSTRUCTION_PART = #{constructionPart,jdbcType=VARCHAR}, " +
            "TEST_DATE = #{testDate,jdbcType=TIMESTAMP}, " +
            "SOIL_PROPERTY = #{soilProperty,jdbcType=VARCHAR}, " +
            "REPORT_DATE = #{reportDate,jdbcType=TIMESTAMP}, " +
            "WITNESS_UNIT = #{witnessUnit,jdbcType=VARCHAR}, " +
            "WITNESS = #{witness,jdbcType=VARCHAR}, " +
            "DESIGN_CAPACITY = #{designCapacity,jdbcType=VARCHAR}, " +
            "HAMMER_WEIGHT = #{hammerWeight,jdbcType=VARCHAR}, " +
            "DROP_DISTANCE = #{dropDistance,jdbcType=VARCHAR}, " +
            "TEST_CATEGORY = #{testCategory,jdbcType=VARCHAR}, " +
            "TEST_BASIS = #{testBasis,jdbcType=VARCHAR}, " +
            "EQUIPMENT = #{equipment,jdbcType=VARCHAR}, " +
            "REMARKS = #{remarks,jdbcType=VARCHAR}, " +
            "APPROVE = #{approver,jdbcType=VARCHAR}, " +
            "REVIEW = #{reviewer,jdbcType=VARCHAR}, " +
            "INSPECT = #{tester,jdbcType=VARCHAR}, " +
            "COMPANY_NAME = #{constructionUnit,jdbcType=VARCHAR}, " +
            "REVIEW_SIGNATURE_PHOTO = #{reviewSignaturePhoto,jdbcType=CLOB}, " +
            "INSPECT_SIGNATURE_PHOTO = #{inspectSignaturePhoto,jdbcType=CLOB}, " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto,jdbcType=CLOB}, " +
            "STATUS = #{status,jdbcType=VARCHAR}, " +
            "REJECT_REASON = #{rejectReason,jdbcType=VARCHAR}, " +
            "NEXT_HANDLER = #{nextHandler,jdbcType=VARCHAR}, " +
            "CONCLUSION = #{conclusion,jdbcType=VARCHAR}, " +
            "DATA_JSON = #{dataJson,jdbcType=CLOB}, " +
            "UPDATE_BY = #{updateBy,jdbcType=VARCHAR}, " +
            "UPDATE_TIME = #{updateTime,jdbcType=TIMESTAMP} " +
            "WHERE ID = #{id,jdbcType=VARCHAR}")
    int update(LightDynamicPenetration entity);

    @Select("SELECT COUNT(*) FROM JZS_LIGHT_DYNAMIC_PENETRATION WHERE ID = #{id}")
    int countById(@Param("id") String id);

    @Delete("DELETE FROM JZS_LIGHT_DYNAMIC_PENETRATION WHERE ID = #{id}")
    int deleteById(@Param("id") String id);
}
