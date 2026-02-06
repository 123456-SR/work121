package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.SandReplacementReport;

@Mapper
public interface SandReplacementReportMapper {

    @Select("SELECT " +
            "ID as id, " +
            "ENTRUSTMENT_ID as entrustmentId, " +
            "DATA_JSON as dataJson, " +
            "REVIEW_SIGNATURE_PHOTO as reviewSignaturePhoto, " +
            "INSPECT_SIGNATURE_PHOTO as inspectSignaturePhoto, " +
            "APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto " +
            "FROM T_SAND_REPLACEMENT " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    SandReplacementReport selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Insert("INSERT INTO T_SAND_REPLACEMENT (ID, ENTRUSTMENT_ID, DATA_JSON, REVIEW_SIGNATURE_PHOTO, INSPECT_SIGNATURE_PHOTO, APPROVE_SIGNATURE_PHOTO) " +
            "VALUES (#{id}, #{entrustmentId}, #{dataJson}, #{reviewSignaturePhoto}, #{inspectSignaturePhoto}, #{approveSignaturePhoto})")
    int insert(SandReplacementReport report);

    @Update("UPDATE T_SAND_REPLACEMENT SET " +
            "DATA_JSON = #{dataJson}, " +
            "REVIEW_SIGNATURE_PHOTO = #{reviewSignaturePhoto}, " +
            "INSPECT_SIGNATURE_PHOTO = #{inspectSignaturePhoto}, " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto} " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int update(SandReplacementReport report);
}
