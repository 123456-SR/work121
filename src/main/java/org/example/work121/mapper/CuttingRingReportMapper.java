package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.CuttingRingReport;

@Mapper
public interface CuttingRingReportMapper {

    @Select("SELECT " +
            "ID as id, " +
            "ENTRUSTMENT_ID as entrustmentId, " +
            "DATA_JSON as dataJson, " +
            "REVIEW_SIGNATURE_PHOTO as reviewSignaturePhoto, " +
            "INSPECT_SIGNATURE_PHOTO as inspectSignaturePhoto, " +
            "APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto " +
            "FROM T_CUTTING_RING " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    CuttingRingReport selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Insert("INSERT INTO T_CUTTING_RING (ID, ENTRUSTMENT_ID, DATA_JSON, REVIEW_SIGNATURE_PHOTO, INSPECT_SIGNATURE_PHOTO, APPROVE_SIGNATURE_PHOTO) " +
            "VALUES (#{id}, #{entrustmentId}, #{dataJson}, #{reviewSignaturePhoto}, #{inspectSignaturePhoto}, #{approveSignaturePhoto})")
    int insert(CuttingRingReport report);

    @Update("UPDATE T_CUTTING_RING SET " +
            "DATA_JSON = #{dataJson}, " +
            "REVIEW_SIGNATURE_PHOTO = #{reviewSignaturePhoto}, " +
            "INSPECT_SIGNATURE_PHOTO = #{inspectSignaturePhoto}, " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto} " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int update(CuttingRingReport report);
}
