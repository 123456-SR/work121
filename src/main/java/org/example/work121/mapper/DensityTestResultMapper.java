package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.DensityTestResult;

@Mapper
public interface DensityTestResultMapper {

    @Delete("DELETE FROM T_DENSITY_TEST WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int deleteByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Select("SELECT " +
            "ID as id, " +
            "ENTRUSTMENT_ID as entrustmentId, " +
            "DATA_JSON as dataJson, " +
            "REVIEW_SIGNATURE_PHOTO as reviewSignaturePhoto, " +
            "INSPECT_SIGNATURE_PHOTO as inspectSignaturePhoto, " +
            "APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto, " +
            "TESTER as tester, " +
            "REVIEWER as reviewer, " +
            "APPROVER as approver " +
            "FROM T_DENSITY_TEST " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    DensityTestResult selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Select("SELECT " +
            "ID as id, " +
            "ENTRUSTMENT_ID as entrustmentId, " +
            "DATA_JSON as dataJson, " +
            "REVIEW_SIGNATURE_PHOTO as reviewSignaturePhoto, " +
            "INSPECT_SIGNATURE_PHOTO as inspectSignaturePhoto, " +
            "APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto, " +
            "TESTER as tester, " +
            "REVIEWER as reviewer, " +
            "APPROVER as approver " +
            "FROM T_DENSITY_TEST " +
            "WHERE ID = #{id}")
    DensityTestResult selectById(@Param("id") String id);

    @Insert("INSERT INTO T_DENSITY_TEST (ID, ENTRUSTMENT_ID, DATA_JSON, REVIEW_SIGNATURE_PHOTO, INSPECT_SIGNATURE_PHOTO, APPROVE_SIGNATURE_PHOTO, TESTER, REVIEWER, APPROVER, CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME) " +
            "VALUES (#{id}, #{entrustmentId}, #{dataJson,jdbcType=CLOB}, #{reviewSignaturePhoto,jdbcType=CLOB}, #{inspectSignaturePhoto,jdbcType=CLOB}, #{approveSignaturePhoto,jdbcType=CLOB}, #{tester,jdbcType=VARCHAR}, #{reviewer,jdbcType=VARCHAR}, #{approver,jdbcType=VARCHAR}, #{createBy,jdbcType=VARCHAR}, #{createTime,jdbcType=TIMESTAMP}, #{updateBy,jdbcType=VARCHAR}, #{updateTime,jdbcType=TIMESTAMP})")
    int insert(DensityTestResult result);

    @Update("UPDATE T_DENSITY_TEST SET " +
            "DATA_JSON = #{dataJson,jdbcType=CLOB}, " +
            "REVIEW_SIGNATURE_PHOTO = #{reviewSignaturePhoto,jdbcType=CLOB}, " +
            "INSPECT_SIGNATURE_PHOTO = #{inspectSignaturePhoto,jdbcType=CLOB}, " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto,jdbcType=CLOB}, " +
            "TESTER = #{tester,jdbcType=VARCHAR}, " +
            "REVIEWER = #{reviewer,jdbcType=VARCHAR}, " +
            "APPROVER = #{approver,jdbcType=VARCHAR}, " +
            "UPDATE_BY = #{updateBy,jdbcType=VARCHAR}, " +
            "UPDATE_TIME = #{updateTime,jdbcType=TIMESTAMP} " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int update(DensityTestResult result);

    @Update("UPDATE T_DENSITY_TEST SET " +
            "ENTRUSTMENT_ID = #{entrustmentId,jdbcType=VARCHAR}, " +
            "DATA_JSON = #{dataJson,jdbcType=CLOB}, " +
            "REVIEW_SIGNATURE_PHOTO = #{reviewSignaturePhoto,jdbcType=CLOB}, " +
            "INSPECT_SIGNATURE_PHOTO = #{inspectSignaturePhoto,jdbcType=CLOB}, " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto,jdbcType=CLOB}, " +
            "TESTER = #{tester,jdbcType=VARCHAR}, " +
            "REVIEWER = #{reviewer,jdbcType=VARCHAR}, " +
            "APPROVER = #{approver,jdbcType=VARCHAR}, " +
            "UPDATE_BY = #{updateBy,jdbcType=VARCHAR}, " +
            "UPDATE_TIME = #{updateTime,jdbcType=TIMESTAMP} " +
            "WHERE ID = #{id}")
    int updateById(DensityTestResult result);
}
