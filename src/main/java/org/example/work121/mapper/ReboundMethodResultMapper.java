package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.ReboundMethodResult;

@Mapper
public interface ReboundMethodResultMapper {

    @Select("SELECT " +
            "ID as id, " +
            "ENTRUSTMENT_ID as entrustmentId, " +
            "DATA_JSON as dataJson " +
            "FROM T_REBOUND_METHOD " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    ReboundMethodResult selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Insert("INSERT INTO T_REBOUND_METHOD (ID, ENTRUSTMENT_ID, DATA_JSON) " +
            "VALUES (#{id}, #{entrustmentId}, #{dataJson})")
    int insert(ReboundMethodResult result);

    @Update("UPDATE T_REBOUND_METHOD SET " +
            "DATA_JSON = #{dataJson} " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int update(ReboundMethodResult result);
}
