package org.example.work121.service;

import org.example.work121.entity.User;
import java.util.List;

public interface UserService {
    /**
     * 根据用户名查询用户信息 (使用默认数据源)
     * @param userAccount 用户名
     * @return 用户信息
     */
    User getUserByAccount(String userAccount);

    List<User> getUserByName(String userName);

    /**
     * 根据ID查询用户信息
     * @param id 用户ID
     * @return 用户信息
     */
    User getUserById(String id);

    /**
     * 根据用户名查询用户信息 (使用指定数据库连接)
     * @param userAccount 用户名
     * @param dbUrl 数据库URL
     * @param dbUser 数据库用户名
     * @param dbPass 数据库密码
     * @return 用户信息
     */
    User getUserByAccount(String userAccount, String dbUrl, String dbUser, String dbPass);

    /**
     * 注册新用户 (使用指定数据库连接)
     * @param user 用户信息
     * @param dbUrl 数据库URL
     * @param dbUser 数据库用户名
     * @param dbPass 数据库密码
     * @return 是否注册成功
     */
    boolean registerUser(User user, String dbUrl, String dbUser, String dbPass);

    /**
     * 注册新用户 (使用默认数据源)
     * @param user 用户信息
     * @return 是否注册成功
     */
    boolean registerUser(User user);
    /**
     * 获取所有用户
     * @return 用户列表
     */
    java.util.List<User> getAllUsers();
}
