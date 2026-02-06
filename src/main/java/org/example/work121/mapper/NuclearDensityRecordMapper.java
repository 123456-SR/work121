package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.NuclearDensityRecord;

@Mapper
public interface NuclearDensityRecordMapper {

    @Select("SELECT " +
            "ID as id, " +
            "ENTRUSTMENT_ID as entrustmentId, " +
            "DATA_JSON as dataJson " +
            "FROM T_NUCLEAR_DENSITY " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    NuclearDensityRecord selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Insert("INSERT INTO T_NUCLEAR_DENSITY (ID, ENTRUSTMENT_ID, DATA_JSON) " +
            "VALUES (#{id}, #{entrustmentId}, #{dataJson})")
    int insert(NuclearDensityRecord record);

    @Update("UPDATE T_NUCLEAR_DENSITY SET " +
            "DATA_JSON = #{dataJson} " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int update(NuclearDensityRecord record);

    @Select("SELECT " +
            "ID as id, " +
            "ENTRUSTMENT_ID as entrustmentId, " +
            "DATA_JSON as dataJson " +
            "FROM T_NUCLEAR_DENSITY " +
            "WHERE ID = #{id}")
    NuclearDensityRecord selectById(@Param("id") String id);
}
