package org.example.work121.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.work121.entity.JcCoreWtInfo;

@Mapper
public interface JcCoreWtInfoMapper {

    @Select("SELECT * FROM JC_CORE_WT_INFO WHERE WT_NUM = #{wtNum}")
    JcCoreWtInfo selectByWtNum(@Param("wtNum") String wtNum);

}
