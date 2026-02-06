package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.ReboundMethodReport;

@Mapper
public interface ReboundMethodReportMapper {

    @Select("SELECT " +
            "ID as id, " +
            "ENTRUSTMENT_ID as entrustmentId, " +
            "DATA_JSON as dataJson, " +
            "REVIEW_SIGNATURE_PHOTO as reviewSignaturePhoto, " +
            "INSPECT_SIGNATURE_PHOTO as inspectSignaturePhoto, " +
            "APPROVE_SIGNATURE_PHOTO as approveSignaturePhoto " +
            "FROM T_REBOUND_METHOD " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    ReboundMethodReport selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Insert("INSERT INTO T_REBOUND_METHOD (ID, ENTRUSTMENT_ID, DATA_JSON, REVIEW_SIGNATURE_PHOTO, INSPECT_SIGNATURE_PHOTO, APPROVE_SIGNATURE_PHOTO) " +
            "VALUES (#{id}, #{entrustmentId}, #{dataJson}, #{reviewSignaturePhoto}, #{inspectSignaturePhoto}, #{approveSignaturePhoto})")
    int insert(ReboundMethodReport report);

    @Update("UPDATE T_REBOUND_METHOD SET " +
            "DATA_JSON = #{dataJson}, " +
            "REVIEW_SIGNATURE_PHOTO = #{reviewSignaturePhoto}, " +
            "INSPECT_SIGNATURE_PHOTO = #{inspectSignaturePhoto}, " +
            "APPROVE_SIGNATURE_PHOTO = #{approveSignaturePhoto} " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int update(ReboundMethodReport report);
}
