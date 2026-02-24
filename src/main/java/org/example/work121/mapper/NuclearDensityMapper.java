package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.NuclearDensity;
import java.util.List;

import java.util.List;

@Mapper
public interface NuclearDensityMapper {

    @Delete("DELETE FROM T_NUCLEAR_DENSITY WHERE ID = #{id}")
    int deleteById(@Param("id") String id);

    @Select("SELECT " +
            "t2.ID as id, " +
            "t2.ENTRUSTMENT_ID as entrustmentId, " +
            "t2.DATA_JSON as dataJson, " +
            "t2.TESTER as tester, " +
            "t2.REVIEWER as reviewer, " +
            "t2.APPROVER as approver, " +
            "t2.FILLER as filler, " +
            "t2.RECORD_TESTER as recordTester, " +
            "t2.RECORD_REVIEWER as recordReviewer, " +
            "t2.RECORD_REVIEW_SIGN as recordReviewSign, " +
            "t2.REVIEW_SIGNATURE_PHOTO as reviewSignaturePhoto, " +
            "t2.INSPECT_SIGNATURE_PHOTO as inspectSignaturePhoto, " +
            "t2.APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto, " +
            "TO_NUMBER(t2.STATUS) as status, " +
            "t2.REJECT_REASON as rejectReason, " +
            "t2.NEXT_HANDLER as nextHandler, " +
            "t2.CREATE_BY as createBy, " +
            "t2.CREATE_TIME as createTime, " +
            "t2.UPDATE_BY as updateBy, " +
            "t2.UPDATE_TIME as updateTime, " +
            "t1.SAMPLE_NUMBER as wtNum, " +
            "t1.CLIENT_UNIT as clientUnit, " +
            "t1.COMMISSION_DATE as commissionDate, " +
            "t1.PROJECT_NAME as projectName, " +
            "t1.CONSTRUCTION_PART as constructionPart, " +
            "t1.CONSTRUCTION_UNIT as constructionUnit, " +
            "t1.BUILDING_UNIT as buildingUnit, " +
            "t1.SAMPLE_NAME as sampleName, " +
            "t1.TEST_CATEGORY as testCategory, " +
            "t1.WITNESS_UNIT as witnessUnit, " +
            "t1.WITNESS as witness, " +
            "t1.BEIZHU as remarks " +
            "FROM T_NUCLEAR_DENSITY t2 " +
            "LEFT JOIN T_ENTRUSTMENT t1 ON t2.ENTRUSTMENT_ID = t1.ID " +
            "WHERE t2.ID = #{id}")
    NuclearDensity selectById(@Param("id") String id);

    @Update("UPDATE T_NUCLEAR_DENSITY SET " +
            "ENTRUSTMENT_ID = #{entrustmentId}, " +
            "DATA_JSON = #{dataJson,jdbcType=CLOB}, " +
            "TESTER = #{tester,jdbcType=VARCHAR}, " +
            "REVIEWER = #{reviewer,jdbcType=VARCHAR}, " +
            "APPROVER = #{approver,jdbcType=VARCHAR}, " +
            "FILLER = #{filler,jdbcType=VARCHAR}, " +
            "RECORD_TESTER = #{recordTester,jdbcType=VARCHAR}, " +
            "RECORD_REVIEWER = #{recordReviewer,jdbcType=VARCHAR}, " +
            "REVIEW_SIGNATURE_PHOTO = #{reviewSignaturePhoto,jdbcType=CLOB}, " +
            "INSPECT_SIGNATURE_PHOTO = #{inspectSignaturePhoto,jdbcType=CLOB}, " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto,jdbcType=CLOB}, " +
            "STATUS = TO_CHAR(#{status,jdbcType=INTEGER}), " +
            "REJECT_REASON = #{rejectReason,jdbcType=VARCHAR}, " +
            "NEXT_HANDLER = #{nextHandler,jdbcType=VARCHAR}, " +
            "UPDATE_BY = #{updateBy,jdbcType=VARCHAR}, " +
            "UPDATE_TIME = #{updateTime,jdbcType=TIMESTAMP} " +
            "WHERE ID = #{id}")
    int updateById(NuclearDensity nuclearDensity);

    @Select("SELECT " +
            "t2.ID as id, " +
            "t2.ENTRUSTMENT_ID as entrustmentId, " +
            "t2.DATA_JSON as dataJson, " +
            "t2.TESTER as tester, " +
            "t2.REVIEWER as reviewer, " +
            "t2.APPROVER as approver, " +
            "t2.FILLER as filler, " +
            "t2.RECORD_TESTER as recordTester, " +
            "t2.RECORD_REVIEWER as recordReviewer, " +
            "t2.RECORD_REVIEW_SIGN as recordReviewSign, " +
            "t2.REVIEW_SIGNATURE_PHOTO as reviewSignaturePhoto, " +
            "t2.INSPECT_SIGNATURE_PHOTO as inspectSignaturePhoto, " +
            "t2.APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto, " +
            "TO_NUMBER(t2.STATUS) as status, " +
            "t2.REJECT_REASON as rejectReason, " +
            "t2.NEXT_HANDLER as nextHandler, " +
            "t2.CREATE_BY as createBy, " +
            "t2.CREATE_TIME as createTime, " +
            "t2.UPDATE_BY as updateBy, " +
            "t2.UPDATE_TIME as updateTime, " +
            "t1.SAMPLE_NUMBER as wtNum, " +
            "t1.CLIENT_UNIT as clientUnit, " +
            "t1.COMMISSION_DATE as commissionDate, " +
            "t1.PROJECT_NAME as projectName, " +
            "t1.CONSTRUCTION_PART as constructionPart, " +
            "t1.CONSTRUCTION_UNIT as constructionUnit, " +
            "t1.BUILDING_UNIT as buildingUnit, " +
            "t1.SAMPLE_NAME as sampleName, " +
            "t1.TEST_CATEGORY as testCategory, " +
            "t1.WITNESS_UNIT as witnessUnit, " +
            "t1.WITNESS as witness, " +
            "t1.BEIZHU as remarks " +
            "FROM T_NUCLEAR_DENSITY t2 " +
            "LEFT JOIN T_ENTRUSTMENT t1 ON t2.ENTRUSTMENT_ID = t1.ID " +
            "WHERE t2.ENTRUSTMENT_ID = #{entrustmentId}")
    List<NuclearDensity> selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Insert("INSERT INTO T_NUCLEAR_DENSITY (" +
            "ID, ENTRUSTMENT_ID, DATA_JSON, " +
            "TESTER, REVIEWER, APPROVER, " +
            "FILLER, RECORD_TESTER, RECORD_REVIEWER, " +
            "REVIEW_SIGNATURE_PHOTO, INSPECT_SIGNATURE_PHOTO, APPROVE_SIGNATURE_PHOTO, " +
            "STATUS, REJECT_REASON, NEXT_HANDLER, " +
            "CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME) " +
            "VALUES (" +
            "#{id}, #{entrustmentId}, #{dataJson,jdbcType=CLOB}, " +
            "#{tester,jdbcType=VARCHAR}, #{reviewer,jdbcType=VARCHAR}, #{approver,jdbcType=VARCHAR}, " +
            "#{filler,jdbcType=VARCHAR}, #{recordTester,jdbcType=VARCHAR}, #{recordReviewer,jdbcType=VARCHAR}, " +
            "#{reviewSignaturePhoto,jdbcType=CLOB}, #{inspectSignaturePhoto,jdbcType=CLOB}, #{approveSignaturePhoto,jdbcType=CLOB}, " +
            "TO_CHAR(#{status,jdbcType=INTEGER}), #{rejectReason,jdbcType=VARCHAR}, #{nextHandler,jdbcType=VARCHAR}, " +
            "#{createBy,jdbcType=VARCHAR}, #{createTime,jdbcType=TIMESTAMP}, #{updateBy,jdbcType=VARCHAR}, #{updateTime,jdbcType=TIMESTAMP})")
    int insert(NuclearDensity nuclearDensity);
}
