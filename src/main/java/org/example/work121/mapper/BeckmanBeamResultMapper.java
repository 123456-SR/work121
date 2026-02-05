package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.BeckmanBeamResult;

@Mapper
public interface BeckmanBeamResultMapper {

    @Select("SELECT " +
            "ID as id, " +
            "ENTRUSTMENT_ID as entrustmentId, " +
            "DATA_JSON as dataJson " +
            "FROM T_BECKMAN_BEAM " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    BeckmanBeamResult selectByEntrustmentId(@Param("entrustmentId") String entrustmentId);

    @Insert("INSERT INTO T_BECKMAN_BEAM (ID, ENTRUSTMENT_ID, DATA_JSON) " +
            "VALUES (#{id}, #{entrustmentId}, #{dataJson})")
    int insert(BeckmanBeamResult result);

    @Update("UPDATE T_BECKMAN_BEAM SET " +
            "DATA_JSON = #{dataJson} " +
            "WHERE ENTRUSTMENT_ID = #{entrustmentId}")
    int update(BeckmanBeamResult result);
}
