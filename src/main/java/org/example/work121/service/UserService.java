package org.example.work121.service;

import org.example.work121.entity.User;

public interface UserService {
    /**
     * 根据用户名查询用户信息
     * @param userAccount 用户名
     * @return 用户信息
     */
    User getUserByAccount(String userAccount);
}