package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.LightDynamicPenetrationReport;

@Mapper
public interface LightDynamicPenetrationReportMapper {

    @Select("SELECT " +
            "UNIFIED_NUMBER as entrustmentId, " +
            "DATA_BLOCKS as dataJson " +
            "FROM JZS_LIGHT_DYNAMIC_PENETRATION " +
            "WHERE UNIFIED_NUMBER = #{entrustmentId}")
    LightDynamicPenetrationReport selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Insert("INSERT INTO JZS_LIGHT_DYNAMIC_PENETRATION (UNIFIED_NUMBER, DATA_BLOCKS) " +
            "VALUES (#{entrustmentId}, #{dataJson})")
    int insert(LightDynamicPenetrationReport report);

    @Update("UPDATE JZS_LIGHT_DYNAMIC_PENETRATION SET " +
            "DATA_BLOCKS = #{dataJson} " +
            "WHERE UNIFIED_NUMBER = #{entrustmentId}")
    int update(LightDynamicPenetrationReport report);
}
