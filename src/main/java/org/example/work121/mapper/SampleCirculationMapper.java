package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.SampleCirculation;

import java.util.List;

@Mapper
public interface SampleCirculationMapper {
    @Insert("INSERT INTO T_SAMPLE_CIRCULATION (ID, TEST_LAB, SAMPLE_NAME, SPEC_MODEL, SAMPLE_NUMBER, RECEIVE_DATE, SAMPLE_STATUS, SAMPLE_QUANTITY, TEST_ITEMS, TEST_STANDARD, SAMPLE_HISTORY, RECEIVE_PERSON, RECEIVE_PERSON_ID, WITHDRAW_PERSON, WITHDRAW_PERSON_ID, REMARKS, CREATE_TIME, CREATE_BY, UPDATE_TIME, UPDATE_BY) " +
            "VALUES (#{id}, #{testLab}, #{sampleName}, #{specModel}, #{sampleNumber}, #{receiveDate}, #{sampleStatus}, #{sampleQuantity}, #{testItems}, #{testStandard}, #{sampleHistory}, #{receivePerson}, #{receivePersonId}, #{withdrawPerson}, #{withdrawPersonId}, #{remarks}, CURRENT_TIMESTAMP, #{createBy}, CURRENT_TIMESTAMP, #{updateBy})")
    int insert(SampleCirculation sampleCirculation);

    @Update("UPDATE T_SAMPLE_CIRCULATION SET TEST_LAB = #{testLab}, SAMPLE_NAME = #{sampleName}, SPEC_MODEL = #{specModel}, SAMPLE_NUMBER = #{sampleNumber}, RECEIVE_DATE = #{receiveDate}, SAMPLE_STATUS = #{sampleStatus}, SAMPLE_QUANTITY = #{sampleQuantity}, TEST_ITEMS = #{testItems}, TEST_STANDARD = #{testStandard}, SAMPLE_HISTORY = #{sampleHistory}, RECEIVE_PERSON = #{receivePerson}, RECEIVE_PERSON_ID = #{receivePersonId}, WITHDRAW_PERSON = #{withdrawPerson}, WITHDRAW_PERSON_ID = #{withdrawPersonId}, REMARKS = #{remarks}, UPDATE_TIME = CURRENT_TIMESTAMP, UPDATE_BY = #{updateBy} WHERE ID = #{id}")
    int update(SampleCirculation sampleCirculation);

    @Delete("DELETE FROM T_SAMPLE_CIRCULATION WHERE ID = #{id}")
    int deleteById(String id);

    @Select("SELECT * FROM T_SAMPLE_CIRCULATION WHERE ID = #{id}")
    SampleCirculation selectById(String id);

    @Select("SELECT * FROM T_SAMPLE_CIRCULATION WHERE SAMPLE_NUMBER = #{sampleNumber}")
    SampleCirculation selectBySampleNumber(String sampleNumber);

    @Select("SELECT * FROM T_SAMPLE_CIRCULATION ORDER BY CREATE_TIME DESC")
    List<SampleCirculation> selectAll();

    @Select("SELECT * FROM T_SAMPLE_CIRCULATION WHERE SAMPLE_NUMBER LIKE CONCAT('%', #{keyword}, '%') OR SAMPLE_NAME LIKE CONCAT('%', #{keyword}, '%') ORDER BY CREATE_TIME DESC")
    List<SampleCirculation> selectByKeyword(String keyword);
}