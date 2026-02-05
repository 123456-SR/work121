package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.SandReplacementRecord;

@Mapper
public interface SandReplacementRecordMapper {

    @Select("SELECT " +
            "ID as id, " +
            "ENTRUSTMENT_ID as entrustmentId, " +
            "DATA_JSON as dataJson " +
            "FROM T_SAND_REPLACEMENT " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    SandReplacementRecord selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Insert("INSERT INTO T_SAND_REPLACEMENT (ID, ENTRUSTMENT_ID, DATA_JSON) " +
            "VALUES (#{id}, #{entrustmentId}, #{dataJson})")
    int insert(SandReplacementRecord record);

    @Update("UPDATE T_SAND_REPLACEMENT SET " +
            "DATA_JSON = #{dataJson} " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int update(SandReplacementRecord record);
}
