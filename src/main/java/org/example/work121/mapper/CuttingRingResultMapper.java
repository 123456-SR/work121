package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.CuttingRingResult;

@Mapper
public interface CuttingRingResultMapper {

    @Select("SELECT " +
            "ID as id, " +
            "ENTRUSTMENT_ID as entrustmentId, " +
            "DATA_JSON as dataJson " +
            "FROM T_CUTTING_RING " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    CuttingRingResult selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Insert("INSERT INTO T_CUTTING_RING (ID, ENTRUSTMENT_ID, DATA_JSON) " +
            "VALUES (#{id}, #{entrustmentId}, #{dataJson})")
    int insert(CuttingRingResult result);

    @Update("UPDATE T_CUTTING_RING SET " +
            "DATA_JSON = #{dataJson} " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int update(CuttingRingResult result);
}
