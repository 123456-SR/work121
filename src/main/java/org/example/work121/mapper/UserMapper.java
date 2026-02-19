package org.example.work121.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.work121.entity.User;
import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT " +
            "ID as id, " +
            "USER_ACCOUNT as userAccount, " +
            "USER_PASS as userPass, " +
            "USER_NAME as userName, " +
            "USER_TEL as userTel, " +
            "USER_EMAIL as userEmail, " +
            "ORGNIZATION_NAME as orgnizationName, " +
            "SH_TYPE as shType, " +
            "'1' as userStatus " +
            "FROM JZS_USERS " +
            "WHERE USER_NAME = #{userName}")
    List<User> selectByName(String userName);

    @Select("SELECT " +
            "ID as id, " +
            "USER_ACCOUNT as userAccount, " +
            "USER_PASS as userPass, " +
            "USER_NAME as userName, " +
            "USER_TEL as userTel, " +
            "USER_EMAIL as userEmail, " +
            "ORGNIZATION_NAME as orgnizationName, " +
            "SH_TYPE as shType, " +
            "'1' as userStatus " +
            "FROM JZS_USERS")
    List<User> selectAll();

    @Select("SELECT " +
            "ID as id, " +
            "USER_ACCOUNT as userAccount, " +
            "USER_PASS as userPass, " +
            "USER_NAME as userName, " +
            "USER_TEL as userTel, " +
            "USER_EMAIL as userEmail, " +
            "ORGNIZATION_NAME as orgnizationName, " +
            "SH_TYPE as shType, " +
            "'1' as userStatus " +
            "FROM JZS_USERS " +
            "WHERE USER_ACCOUNT = #{userAccount}")
    User selectByAccount(String userAccount);

    @Select("SELECT " +
            "ID as id, " +
            "USER_ACCOUNT as userAccount, " +
            "USER_PASS as userPass, " +
            "USER_NAME as userName, " +
            "USER_TEL as userTel, " +
            "USER_EMAIL as userEmail, " +
            "ORGNIZATION_NAME as orgnizationName, " +
            "SH_TYPE as shType, " +
            "'1' as userStatus " +
            "FROM JZS_USERS " +
            "WHERE ID = #{id}")
    User selectById(String id);

    @Insert("INSERT INTO JZS_USERS (" +
            "ID, USER_ACCOUNT, USER_PASS, USER_NAME, USER_TEL, USER_EMAIL, ORGNIZATION_NAME, SH_TYPE" +
            ") VALUES (" +
            "#{id}, #{userAccount}, #{userPass}, #{userName}, #{userTel}, #{userEmail}, #{orgnizationName}, #{shType}" +
            ")")
    int insert(User user);

    @Update("UPDATE JZS_USERS SET " +
            "USER_PASS = #{userPass}, " +
            "USER_NAME = #{userName}, " +
            "USER_TEL = #{userTel}, " +
            "USER_EMAIL = #{userEmail}, " +
            "ORGNIZATION_NAME = #{orgnizationName}, " +
            "SH_TYPE = #{shType} " +
            "WHERE ID = #{id}")
    int update(User user);
    
    @Delete("DELETE FROM JZS_USERS WHERE ID = #{id}")
    int deleteById(String id);
}
