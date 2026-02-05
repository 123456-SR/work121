package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.ReboundMethodRecord;

@Mapper
public interface ReboundMethodRecordMapper {

    @Select("SELECT " +
            "ID as id, " +
            "ENTRUSTMENT_ID as entrustmentId, " +
            "DATA_JSON as dataJson " +
            "FROM T_REBOUND_METHOD " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    ReboundMethodRecord selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Insert("INSERT INTO T_REBOUND_METHOD (ID, ENTRUSTMENT_ID, DATA_JSON) " +
            "VALUES (#{id}, #{entrustmentId}, #{dataJson})")
    int insert(ReboundMethodRecord record);

    @Update("UPDATE T_REBOUND_METHOD SET " +
            "DATA_JSON = #{dataJson} " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int update(ReboundMethodRecord record);
}
