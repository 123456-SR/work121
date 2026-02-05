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
            "#{table1Type}, #{table1Id}, " +
            "#{table2Type}, #{table2Id}, " +
            "#{table3Type}, #{table3Id}, " +
            "#{table4Type}, #{table4Id}, " +
            "#{table5Type}, #{table5Id}, " +
            "#{table6Type}, #{table6Id}, " +
            "#{table7Type}, #{table7Id}, " +
            "#{table8Type}, #{table8Id}, " +
            "#{table9Type}, #{table9Id}, " +
            "#{table10Type}, #{table10Id}, " +
            "#{status}, #{createBy}, #{createTime}, #{updateBy}, #{updateTime}" +
            ")")
    int insert(SimpleDirectory directory);

    @Update("UPDATE T_SIMPLE_DIRECTORY SET " +
            "DIR_NAME = #{dirName}, " +
            "TABLE1_TYPE = #{table1Type}, TABLE1_ID = #{table1Id}, " +
            "TABLE2_TYPE = #{table2Type}, TABLE2_ID = #{table2Id}, " +
            "TABLE3_TYPE = #{table3Type}, TABLE3_ID = #{table3Id}, " +
            "TABLE4_TYPE = #{table4Type}, TABLE4_ID = #{table4Id}, " +
            "TABLE5_TYPE = #{table5Type}, TABLE5_ID = #{table5Id}, " +
            "TABLE6_TYPE = #{table6Type}, TABLE6_ID = #{table6Id}, " +
            "TABLE7_TYPE = #{table7Type}, TABLE7_ID = #{table7Id}, " +
            "TABLE8_TYPE = #{table8Type}, TABLE8_ID = #{table8Id}, " +
            "TABLE9_TYPE = #{table9Type}, TABLE9_ID = #{table9Id}, " +
            "TABLE10_TYPE = #{table10Type}, TABLE10_ID = #{table10Id}, " +
            "STATUS = #{status}, " +
            "UPDATE_MAN = #{updateBy}, " +
            "UPDATE_TIME = #{updateTime} " +
            "WHERE ID = #{id}")
    int update(SimpleDirectory directory);

    @Delete("DELETE FROM T_SIMPLE_DIRECTORY WHERE ID = #{id}")
    int deleteById(String id);
}
