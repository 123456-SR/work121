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
            "JD_UNIT as witnessUnit, " +
            "JD_MAN as witness, " +
            "JZ_UNIT as buildingUnit2, " +
            "JZ_MAN as buildingMan, " +
            "KC_UNIT as surveyUnit, " +
            "WT_REG_NAME as clientRegName, " +
            "WT_STATUS as sampleStatus, " +
            "WT_MAN_TEL as clientTel, " +
            "WT_UNIT_ADDRESS as clientUnitAddress, " +
            "WT_UNIT_TEL as clientUnitTel, " +
            "t.* " +
            "FROM JC_CORE_WT_INFO t " +
            "WHERE WT_NUM = #{wtNum}")
    JcCoreWtInfo selectByWtNum(@Param("wtNum") String wtNum);

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
            "JD_UNIT as witnessUnit, " +
            "JD_MAN as witness, " +
            "JZ_UNIT as buildingUnit2, " +
            "JZ_MAN as buildingMan, " +
            "KC_UNIT as surveyUnit, " +
            "WT_REG_NAME as clientRegName, " +
            "WT_STATUS as sampleStatus, " +
            "WT_MAN_TEL as clientTel, " +
            "WT_UNIT_ADDRESS as clientUnitAddress, " +
            "WT_UNIT_TEL as clientUnitTel, " +
            "t.* " +
            "FROM JC_CORE_WT_INFO t " +
            "WHERE WT_REG_NAME = #{regName} " +
            "OR WT_MAN = #{regName} " +
            "OR JD_MAN = #{regName} " +
            "OR JZ_MAN = #{regName} " +
            "OR CY_MAN = #{regName} " +
            "OR JC_TASK_JS_MAN = #{regName} " +
            "OR JC_TASK_REG_MAN = #{regName} " +
            "OR JC_TASK_SH_MAN = #{regName} " +
            "OR PRODUCE_GD_MAN = #{regName} " +
            "OR YW_MAN = #{regName} " +
            "OR JS_MAN = #{regName} " +
            "OR KC_MAN = #{regName} " +
            "OR SG_MAN = #{regName} " +
            "OR SJ_MAN = #{regName} " +
            "OR JC_TASK_XD_MAN = #{regName} " +
            "OR SEND_BACK_MAN = #{regName} " +
            "OR JC_TASK_TJ_MAN = #{regName} " +
            "OR JL_MAN = #{regName} " +
            "ORDER BY WT_DATE DESC")
    java.util.List<JcCoreWtInfo> selectByRegName(@Param("regName") String regName);

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
            "JD_UNIT as witnessUnit, " +
            "JD_MAN as witness, " +
            "JZ_UNIT as buildingUnit2, " +
            "JZ_MAN as buildingMan, " +
            "KC_UNIT as surveyUnit, " +
            "WT_REG_NAME as clientRegName, " +
            "WT_STATUS as sampleStatus, " +
            "WT_MAN_TEL as clientTel, " +
            "WT_UNIT_ADDRESS as clientUnitAddress, " +
            "WT_UNIT_TEL as clientUnitTel, " +
            "t.* " +
            "FROM JC_CORE_WT_INFO t " +
            "WHERE WT_ID = #{id}")
    JcCoreWtInfo selectById(@Param("id") String id);

}
