package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.NuclearDensityReport;

@Mapper
public interface NuclearDensityReportMapper {

    @Delete("DELETE FROM T_NUCLEAR_DENSITY WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int deleteByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Select("SELECT " +
            "ID as id, " +
            "ENTRUSTMENT_ID as entrustmentId, " +
            "DATA_JSON as dataJson, " +
            "REVIEW_SIGNATURE_PHOTO as reviewSignaturePhoto, " +
            "INSPECT_SIGNATURE_PHOTO as inspectSignaturePhoto, " +
            "APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto, " +
            "CREATE_BY as createBy, " +
            "CREATE_TIME as createTime, " +
            "UPDATE_BY as updateBy, " +
            "UPDATE_TIME as updateTime " +
            "FROM T_NUCLEAR_DENSITY " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    NuclearDensityReport selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Insert("INSERT INTO T_NUCLEAR_DENSITY (ID, ENTRUSTMENT_ID, DATA_JSON, REVIEW_SIGNATURE_PHOTO, INSPECT_SIGNATURE_PHOTO, APPROVE_SIGNATURE_PHOTO, CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME) " +
            "VALUES (#{id}, #{entrustmentId}, #{dataJson,jdbcType=CLOB}, #{reviewSignaturePhoto,jdbcType=CLOB}, #{inspectSignaturePhoto,jdbcType=CLOB}, #{approveSignaturePhoto,jdbcType=CLOB}, #{createBy,jdbcType=VARCHAR}, #{createTime,jdbcType=TIMESTAMP}, #{updateBy,jdbcType=VARCHAR}, #{updateTime,jdbcType=TIMESTAMP})")
    int insert(NuclearDensityReport report);

    @Update("UPDATE T_NUCLEAR_DENSITY SET " +
            "DATA_JSON = #{dataJson,jdbcType=CLOB}, " +
            "REVIEW_SIGNATURE_PHOTO = #{reviewSignaturePhoto,jdbcType=CLOB}, " +
            "INSPECT_SIGNATURE_PHOTO = #{inspectSignaturePhoto,jdbcType=CLOB}, " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto,jdbcType=CLOB}, " +
            "UPDATE_BY = #{updateBy,jdbcType=VARCHAR}, " +
            "UPDATE_TIME = #{updateTime,jdbcType=TIMESTAMP} " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int update(NuclearDensityReport report);

    @Select("SELECT " +
            "ID as id, " +
            "ENTRUSTMENT_ID as entrustmentId, " +
            "DATA_JSON as dataJson, " +
            "REVIEW_SIGNATURE_PHOTO as reviewSignaturePhoto, " +
            "INSPECT_SIGNATURE_PHOTO as inspectSignaturePhoto, " +
            "APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto, " +
            "CREATE_BY as createBy, " +
            "CREATE_TIME as createTime, " +
            "UPDATE_BY as updateBy, " +
            "UPDATE_TIME as updateTime " +
            "FROM T_NUCLEAR_DENSITY " +
            "WHERE ID = #{id}")
    NuclearDensityReport selectById(@Param("id") String id);

    @Update("UPDATE T_NUCLEAR_DENSITY SET " +
            "ENTRUSTMENT_ID = #{entrustmentId}, " +
            "DATA_JSON = #{dataJson}, " +
            "REVIEW_SIGNATURE_PHOTO = #{reviewSignaturePhoto}, " +
            "INSPECT_SIGNATURE_PHOTO = #{inspectSignaturePhoto}, " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto}, " +
            "UPDATE_BY = #{updateBy}, " +
            "UPDATE_TIME = #{updateTime} " +
            "WHERE ID = #{id}")
    int updateById(NuclearDensityReport report);
}
