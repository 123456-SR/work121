package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.LightDynamicPenetrationReport;

@Mapper
public interface LightDynamicPenetrationReportMapper {

    @Select("SELECT " +
            "UNIFIED_NUMBER as entrustmentId, " +
            "DATA_BLOCKS as dataJson, " +
            "REVIEW_SIGNATURE_PHOTO as reviewSignaturePhoto, " +
            "INSPECT_SIGNATURE_PHOTO as inspectSignaturePhoto, " +
            "APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto " +
            "FROM JZS_LIGHT_DYNAMIC_PENETRATION " +
            "WHERE UNIFIED_NUMBER = #{entrustmentId}")
    LightDynamicPenetrationReport selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Insert("INSERT INTO JZS_LIGHT_DYNAMIC_PENETRATION (UNIFIED_NUMBER, DATA_BLOCKS, REVIEW_SIGNATURE_PHOTO, INSPECT_SIGNATURE_PHOTO, APPROVE_SIGNATURE_PHOTO) " +
            "VALUES (#{entrustmentId}, #{dataJson}, #{reviewSignaturePhoto}, #{inspectSignaturePhoto}, #{approveSignaturePhoto})")
    int insert(LightDynamicPenetrationReport report);

    @Update("UPDATE JZS_LIGHT_DYNAMIC_PENETRATION SET " +
            "DATA_BLOCKS = #{dataJson}, " +
            "REVIEW_SIGNATURE_PHOTO = #{reviewSignaturePhoto}, " +
            "INSPECT_SIGNATURE_PHOTO = #{inspectSignaturePhoto}, " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto} " +
            "WHERE UNIFIED_NUMBER = #{entrustmentId}")
    int update(LightDynamicPenetrationReport report);
}
