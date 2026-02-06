package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.WaterReplacementRecord;

@Mapper
public interface WaterReplacementRecordMapper {

    @Select("SELECT " +
            "ID as id, " +
            "ENTRUSTMENT_ID as entrustmentId, " +
            "DATA_JSON as dataJson " +
            "FROM T_WATER_REPLACEMENT " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    WaterReplacementRecord selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Insert("INSERT INTO T_WATER_REPLACEMENT (ID, ENTRUSTMENT_ID, DATA_JSON) " +
            "VALUES (#{id}, #{entrustmentId}, #{dataJson})")
    int insert(WaterReplacementRecord record);

    @Update("UPDATE T_WATER_REPLACEMENT SET " +
            "DATA_JSON = #{dataJson} " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int update(WaterReplacementRecord record);

    @Select("SELECT " +
            "ID as id, " +
            "ENTRUSTMENT_ID as entrustmentId, " +
            "DATA_JSON as dataJson " +
            "FROM T_WATER_REPLACEMENT " +
            "WHERE ID = #{id}")
    WaterReplacementRecord selectById(@Param("id") String id);
}
