package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.DensityTestRecord;

@Mapper
public interface DensityTestRecordMapper {

    @Select("SELECT " +
            "ID as id, " +
            "ENTRUSTMENT_ID as entrustmentId, " +
            "DATA_JSON as dataJson " +
            "FROM T_DENSITY_TEST " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    DensityTestRecord selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Insert("INSERT INTO T_DENSITY_TEST (ID, ENTRUSTMENT_ID, DATA_JSON) " +
            "VALUES (#{id}, #{entrustmentId}, #{dataJson})")
    int insert(DensityTestRecord record);

    @Update("UPDATE T_DENSITY_TEST SET " +
            "DATA_JSON = #{dataJson} " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int update(DensityTestRecord record);
}
