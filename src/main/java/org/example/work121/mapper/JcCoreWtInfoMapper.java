package org.example.work121.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.work121.entity.JcCoreWtInfo;

@Mapper
public interface JcCoreWtInfoMapper {

    @Select("SELECT " +
            "WT_ID as id, " +
            "WT_NUM as wtNum, " +
            "WT_DATE as commissionDate, " +
            "WT_UNIT as clientUnit, " +
            "WT_MAN as client, " +
            "GC_NAME as projectName, " +
            "GC_AREA as projectArea, " +
            "SG_UNIT as constructionUnit, " +
            "JS_UNIT as buildingUnit, " +
            "JL_UNIT as supervisionUnit, " +
            "KC_UNIT as surveyUnit, " +
            "t.* " +
            "FROM JC_CORE_WT_INFO t " +
            "WHERE WT_NUM = #{wtNum}")
    JcCoreWtInfo selectByWtNum(@Param("wtNum") String wtNum);

}
