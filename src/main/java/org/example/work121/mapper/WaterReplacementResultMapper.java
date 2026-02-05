package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.WaterReplacementResult;

@Mapper
public interface WaterReplacementResultMapper {

    @Select("SELECT " +
            "ID as id, " +
            "ENTRUSTMENT_ID as entrustmentId, " +
            "DATA_JSON as dataJson " +
            "FROM T_WATER_REPLACEMENT " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    WaterReplacementResult selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Insert("INSERT INTO T_WATER_REPLACEMENT (ID, ENTRUSTMENT_ID, DATA_JSON) " +
            "VALUES (#{id}, #{entrustmentId}, #{dataJson})")
    int insert(WaterReplacementResult result);

    @Update("UPDATE T_WATER_REPLACEMENT SET " +
            "DATA_JSON = #{dataJson} " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int update(WaterReplacementResult result);
}
