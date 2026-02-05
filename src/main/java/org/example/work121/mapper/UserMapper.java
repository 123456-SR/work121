package org.example.work121.mapper;

import org.apache.ibatis.annotations.*;
import org.example.work121.entity.User;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT " +
            "USER_ID as id, " +
            "USER_ACCOUNT as userAccount, " +
            "USER_PASS as userPass, " +
            "USER_NAME as userName, " +
            "USER_STATUS as userStatus " +
            "FROM JZS_USERS " +
            "WHERE USER_ACCOUNT = #{userAccount}")
    User selectByAccount(String userAccount);

    @Select("SELECT " +
            "USER_ID as id, " +
            "USER_ACCOUNT as userAccount, " +
            "USER_PASS as userPass, " +
            "USER_NAME as userName, " +
            "USER_STATUS as userStatus " +
            "FROM JZS_USERS " +
            "WHERE USER_ID = #{id}")
    User selectById(String id);

    @Insert("INSERT INTO JZS_USERS (" +
            "USER_ID, USER_ACCOUNT, USER_PASS, USER_NAME, USER_STATUS" +
            ") VALUES (" +
            "#{id}, #{userAccount}, #{userPass}, #{userName}, #{userStatus}" +
            ")")
    int insert(User user);

    @Update("UPDATE JZS_USERS SET " +
            "USER_PASS = #{userPass}, " +
            "USER_NAME = #{userName}, " +
            "USER_STATUS = #{userStatus} " +
            "WHERE USER_ID = #{id}")
    int update(User user);
    
    @Delete("DELETE FROM JZS_USERS WHERE USER_ID = #{id}")
    int deleteById(String id);
}
