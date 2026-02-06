package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.SimpleDirectory;

import java.util.List;

@Mapper
public interface SimpleDirectoryMapper {

    @Select("SELECT " +
            "ID, DIR_ID, DIR_NAME, " +
            "TABLE1_TYPE, TABLE1_ID, " +
            "TABLE2_TYPE, TABLE2_ID, " +
            "TABLE3_TYPE, TABLE3_ID, " +
            "TABLE4_TYPE, TABLE4_ID, " +
            "TABLE5_TYPE, TABLE5_ID, " +
            "TABLE6_TYPE, TABLE6_ID, " +
            "TABLE7_TYPE, TABLE7_ID, " +
            "TABLE8_TYPE, TABLE8_ID, " +
            "TABLE9_TYPE, TABLE9_ID, " +
            "TABLE10_TYPE, TABLE10_ID, " +
            "STATUS, " +
            "CREATE_MAN as createBy, " +
            "CREATE_TIME, " +
            "UPDATE_MAN as updateBy, " +
            "UPDATE_TIME " +
            "FROM T_SIMPLE_DIRECTORY " +
            "WHERE ID = #{id}")
    SimpleDirectory selectById(String id);

    @Select("SELECT " +
            "ID, DIR_ID, DIR_NAME, " +
            "TABLE1_TYPE, TABLE1_ID, " +
            "TABLE2_TYPE, TABLE2_ID, " +
            "TABLE3_TYPE, TABLE3_ID, " +
            "TABLE4_TYPE, TABLE4_ID, " +
            "TABLE5_TYPE, TABLE5_ID, " +
            "TABLE6_TYPE, TABLE6_ID, " +
            "TABLE7_TYPE, TABLE7_ID, " +
            "TABLE8_TYPE, TABLE8_ID, " +
            "TABLE9_TYPE, TABLE9_ID, " +
            "TABLE10_TYPE, TABLE10_ID, " +
            "STATUS, " +
            "CREATE_MAN as createBy, " +
            "CREATE_TIME, " +
            "UPDATE_MAN as updateBy, " +
            "UPDATE_TIME " +
            "FROM T_SIMPLE_DIRECTORY " +
            "WHERE DIR_ID = #{dirId}")
    SimpleDirectory selectByDirId(String dirId);

    @Select("SELECT " +
            "ID, DIR_ID, DIR_NAME, " +
            "TABLE1_TYPE, TABLE1_ID, " +
            "TABLE2_TYPE, TABLE2_ID, " +
            "TABLE3_TYPE, TABLE3_ID, " +
            "TABLE4_TYPE, TABLE4_ID, " +
            "TABLE5_TYPE, TABLE5_ID, " +
            "TABLE6_TYPE, TABLE6_ID, " +
            "TABLE7_TYPE, TABLE7_ID, " +
            "TABLE8_TYPE, TABLE8_ID, " +
            "TABLE9_TYPE, TABLE9_ID, " +
            "TABLE10_TYPE, TABLE10_ID, " +
            "STATUS, " +
            "CREATE_MAN as createBy, " +
            "CREATE_TIME, " +
            "UPDATE_MAN as updateBy, " +
            "UPDATE_TIME " +
            "FROM T_SIMPLE_DIRECTORY " +
            "ORDER BY CREATE_TIME DESC")
    List<SimpleDirectory> selectAll();

    @Insert("INSERT INTO T_SIMPLE_DIRECTORY (" +
            "ID, DIR_ID, DIR_NAME, " +
            "TABLE1_TYPE, TABLE1_ID, " +
            "TABLE2_TYPE, TABLE2_ID, " +
            "TABLE3_TYPE, TABLE3_ID, " +
            "TABLE4_TYPE, TABLE4_ID, " +
            "TABLE5_TYPE, TABLE5_ID, " +
            "TABLE6_TYPE, TABLE6_ID, " +
            "TABLE7_TYPE, TABLE7_ID, " +
            "TABLE8_TYPE, TABLE8_ID, " +
            "TABLE9_TYPE, TABLE9_ID, " +
            "TABLE10_TYPE, TABLE10_ID, " +
            "STATUS, CREATE_MAN, CREATE_TIME, UPDATE_MAN, UPDATE_TIME" +
            ") VALUES (" +
            "#{id}, #{dirId}, #{dirName}, " +
            "#{table1Type, jdbcType=VARCHAR}, #{table1Id, jdbcType=VARCHAR}, " +
            "#{table2Type, jdbcType=VARCHAR}, #{table2Id, jdbcType=VARCHAR}, " +
            "#{table3Type, jdbcType=VARCHAR}, #{table3Id, jdbcType=VARCHAR}, " +
            "#{table4Type, jdbcType=VARCHAR}, #{table4Id, jdbcType=VARCHAR}, " +
            "#{table5Type, jdbcType=VARCHAR}, #{table5Id, jdbcType=VARCHAR}, " +
            "#{table6Type, jdbcType=VARCHAR}, #{table6Id, jdbcType=VARCHAR}, " +
            "#{table7Type, jdbcType=VARCHAR}, #{table7Id, jdbcType=VARCHAR}, " +
            "#{table8Type, jdbcType=VARCHAR}, #{table8Id, jdbcType=VARCHAR}, " +
            "#{table9Type, jdbcType=VARCHAR}, #{table9Id, jdbcType=VARCHAR}, " +
            "#{table10Type, jdbcType=VARCHAR}, #{table10Id, jdbcType=VARCHAR}, " +
            "#{status}, #{createBy, jdbcType=VARCHAR}, #{createTime, jdbcType=TIMESTAMP}, #{updateBy, jdbcType=VARCHAR}, #{updateTime, jdbcType=TIMESTAMP}" +
            ")")
    int insert(SimpleDirectory directory);

    @Update("UPDATE T_SIMPLE_DIRECTORY SET " +
            "DIR_NAME = #{dirName}, " +
            "TABLE1_TYPE = #{table1Type, jdbcType=VARCHAR}, TABLE1_ID = #{table1Id, jdbcType=VARCHAR}, " +
            "TABLE2_TYPE = #{table2Type, jdbcType=VARCHAR}, TABLE2_ID = #{table2Id, jdbcType=VARCHAR}, " +
            "TABLE3_TYPE = #{table3Type, jdbcType=VARCHAR}, TABLE3_ID = #{table3Id, jdbcType=VARCHAR}, " +
            "TABLE4_TYPE = #{table4Type, jdbcType=VARCHAR}, TABLE4_ID = #{table4Id, jdbcType=VARCHAR}, " +
            "TABLE5_TYPE = #{table5Type, jdbcType=VARCHAR}, TABLE5_ID = #{table5Id, jdbcType=VARCHAR}, " +
            "TABLE6_TYPE = #{table6Type, jdbcType=VARCHAR}, TABLE6_ID = #{table6Id, jdbcType=VARCHAR}, " +
            "TABLE7_TYPE = #{table7Type, jdbcType=VARCHAR}, TABLE7_ID = #{table7Id, jdbcType=VARCHAR}, " +
            "TABLE8_TYPE = #{table8Type, jdbcType=VARCHAR}, TABLE8_ID = #{table8Id, jdbcType=VARCHAR}, " +
            "TABLE9_TYPE = #{table9Type, jdbcType=VARCHAR}, TABLE9_ID = #{table9Id, jdbcType=VARCHAR}, " +
            "TABLE10_TYPE = #{table10Type, jdbcType=VARCHAR}, TABLE10_ID = #{table10Id, jdbcType=VARCHAR}, " +
            "STATUS = #{status}, " +
            "UPDATE_MAN = #{updateBy, jdbcType=VARCHAR}, " +
            "UPDATE_TIME = #{updateTime, jdbcType=TIMESTAMP} " +
            "WHERE ID = #{id}")
    int update(SimpleDirectory directory);

    @Delete("DELETE FROM T_SIMPLE_DIRECTORY WHERE ID = #{id}")
    int deleteById(String id);
}
