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
            "t1.DATA_BLOCKS as dataJson, " +
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
            "t1.DATA_BLOCKS as dataJson, " +
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
            "APPROVE, REVIEW, INSPECT, CONCLUSION, DATA_BLOCKS, " +
            "REVIEW_SIGNATURE_PHOTO, INSPECT_SIGNATURE_PHOTO, APPROVE_SIGNATURE_PHOTO, " +
            "STATUS, REJECT_REASON, NEXT_HANDLER, " +
            "CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME) " +
            "VALUES " +
            "(#{id}, #{entrustmentId}, #{clientUnit}, #{projectName}, #{commissionDate}, #{constructionPart}, " +
            "#{testDate}, #{soilProperty}, #{reportDate}, #{witnessUnit}, #{witness}, #{designCapacity}, " +
            "#{hammerWeight}, #{dropDistance}, #{testCategory}, #{testBasis}, #{equipment}, #{remarks}, " +
            "#{approver}, #{reviewer}, #{tester}, #{conclusion}, #{dataJson}, " +
            "#{reviewSignaturePhoto}, #{inspectSignaturePhoto}, #{approveSignaturePhoto}, " +
            "#{status}, #{rejectReason}, #{nextHandler}, " +
            "#{createBy}, #{createTime}, #{updateBy}, #{updateTime})")
    int insert(LightDynamicPenetration entity);

    @Update("UPDATE JZS_LIGHT_DYNAMIC_PENETRATION SET " +
            "ENTRUSTMENT_ID = #{entrustmentId}, " +
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
            "COMPANY_NAME = #{constructionUnit}, " +
            "REVIEW_SIGNATURE_PHOTO = #{reviewSignaturePhoto}, " +
            "INSPECT_SIGNATURE_PHOTO = #{inspectSignaturePhoto}, " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto}, " +
            "STATUS = #{status}, " +
            "REJECT_REASON = #{rejectReason}, " +
            "NEXT_HANDLER = #{nextHandler}, " +
            "CONCLUSION = #{conclusion}, " +
            "DATA_BLOCKS = #{dataJson}, " +
            "UPDATE_BY = #{updateBy}, " +
            "UPDATE_TIME = #{updateTime} " +
            "WHERE ID = #{id}")
    int update(LightDynamicPenetration entity);

    @Select("SELECT COUNT(*) FROM JZS_LIGHT_DYNAMIC_PENETRATION WHERE ID = #{id}")
    int countById(@Param("id") String id);

    @Delete("DELETE FROM JZS_LIGHT_DYNAMIC_PENETRATION WHERE ID = #{id}")
    int deleteById(@Param("id") String id);
}
