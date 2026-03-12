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
            "u.ID as id, " +
            "u.USER_ACCOUNT as userAccount, " +
            "u.USER_PASS as userPass, " +
            "u.USER_NAME as userName, " +
            "u.USER_TEL as userTel, " +
            "u.USER_EMAIL as userEmail, " +
            "u.ORGNIZATION_NAME as orgnizationName, " +
            "u.SH_TYPE as shType, " +
            "'1' as userStatus " +
            "FROM JZS_USERS u " +
            "WHERE u.USER_NAME = #{userName}")
    List<User> selectByName(String userName);

    @Select("SELECT " +
            "u.ID as id, " +
            "u.USER_ACCOUNT as userAccount, " +
            "u.USER_PASS as userPass, " +
            "u.USER_NAME as userName, " +
            "u.USER_TEL as userTel, " +
            "u.USER_EMAIL as userEmail, " +
            "u.ORGNIZATION_NAME as orgnizationName, " +
            "u.SH_TYPE as shType, " +
            "'1' as userStatus " +
            "FROM JZS_USERS u")
    List<User> selectAll();

    @Select("SELECT " +
            "u.ID as id, " +
            "u.USER_ACCOUNT as userAccount, " +
            "u.USER_PASS as userPass, " +
            "u.USER_NAME as userName, " +
            "u.USER_TEL as userTel, " +
            "u.USER_EMAIL as userEmail, " +
            "u.ORGNIZATION_NAME as orgnizationName, " +
            "u.SH_TYPE as shType, " +
            "'1' as userStatus " +
            "FROM JZS_USERS u " +
            "WHERE u.USER_ACCOUNT = #{userAccount}")
    User selectByAccount(String userAccount);

    @Select("SELECT " +
            "u.ID as id, " +
            "u.USER_ACCOUNT as userAccount, " +
            "u.USER_PASS as userPass, " +
            "u.USER_NAME as userName, " +
            "u.USER_TEL as userTel, " +
            "u.USER_EMAIL as userEmail, " +
            "u.ORGNIZATION_NAME as orgnizationName, " +
            "u.SH_TYPE as shType, " +
            "'1' as userStatus " +
            "FROM JZS_USERS u " +
            "WHERE u.ID = #{id}")
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
