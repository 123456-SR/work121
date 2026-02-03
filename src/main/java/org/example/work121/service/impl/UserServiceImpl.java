package org.example.work121.service.impl;

import org.example.work121.entity.User;
import org.example.work121.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public User getUserByAccount(String userAccount) {
        logger.info("根据用户名查询用户: {}", userAccount);
        
        String sql = "SELECT * FROM JZS_USERS WHERE USER_ACCOUNT = ?";
        
        try {
            User user = jdbcTemplate.queryForObject(
                sql,
                new Object[]{userAccount},
                new BeanPropertyRowMapper<>(User.class)
            );
            logger.info("查询到用户: {}", user.getUserId());
            return user;
        } catch (Exception e) {
            logger.warn("未找到用户: {}", userAccount, e);
            return null;
        }
    }
}