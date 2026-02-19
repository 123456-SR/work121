package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.LightDynamicPenetrationResult;

@Mapper
public interface LightDynamicPenetrationResultMapper {

    @Select("SELECT " +
            "ID as id, " +
            "ENTRUSTMENT_ID as entrustmentId, " +
            "DATA_JSON as dataJson " +
            "FROM T_LIGHT_DYNAMIC_PENETRATION_RESULT " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    LightDynamicPenetrationResult selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Insert("INSERT INTO T_LIGHT_DYNAMIC_PENETRATION_RESULT (ID, ENTRUSTMENT_ID, DATA_JSON) " +
            "VALUES (#{id}, #{entrustmentId}, #{dataJson})")
    int insert(LightDynamicPenetrationResult result);

    @Update("UPDATE T_LIGHT_DYNAMIC_PENETRATION_RESULT SET " +
            "DATA_JSON = #{dataJson} " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int update(LightDynamicPenetrationResult result);
}
