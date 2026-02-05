package org.example.work121.service.impl;

import org.example.work121.entity.User;
import org.example.work121.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Autowired
    private UserMapper userMapper;

    @Override
    public User getUserByAccount(String userAccount) {
        logger.info("根据用户名查询用户: {}", userAccount);
        
        try {
            User user = userMapper.selectByAccount(userAccount);
            if (user != null) {
                logger.info("查询到用户: {}", user.getUserId());
            } else {
                logger.warn("未找到用户: {}", userAccount);
            }
            return user;
        } catch (Exception e) {
            logger.warn("查询用户出错: {}", userAccount, e);
            return null;
        }
    }

    @Override
    public User getUserByAccount(String userAccount, String dbUrl, String dbUser, String dbPass) {
        logger.info("根据用户名查询用户(指定DB): {}, DB: {}", userAccount, dbUrl);
        
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(dbUser);
        dataSource.setPassword(dbPass);
        
        JdbcTemplate tempJdbcTemplate = new JdbcTemplate(dataSource);
        
        String sql = "SELECT * FROM JZS_USERS WHERE USER_ACCOUNT = ?";
        
        try {
            User user = tempJdbcTemplate.queryForObject(
                sql,
                new Object[]{userAccount},
                new BeanPropertyRowMapper<>(User.class)
            );
            logger.info("查询到用户: {}", user.getUserId());
            return user;
        } catch (Exception e) {
            logger.warn("未找到用户: {}", userAccount);
            return null;
        }
    }

    @Override
    public boolean registerUser(User user, String dbUrl, String dbUser, String dbPass) {
        logger.info("注册用户: {}, DB: {}", user.getUserAccount(), dbUrl);
        
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(dbUser);
        dataSource.setPassword(dbPass);
        
        JdbcTemplate tempJdbcTemplate = new JdbcTemplate(dataSource);
        
        return registerUserInternal(user, tempJdbcTemplate);
    }

    @Override
    public boolean registerUser(User user) {
        logger.info("注册用户(默认DB): {}", user.getUserAccount());
        return registerUserInternal(user, jdbcTemplate);
    }

    private boolean registerUserInternal(User user, JdbcTemplate template) {
        String sql = "INSERT INTO JZS_USERS (USER_ID, USER_ACCOUNT, USER_PASS, USER_NAME, USER_STATUS) VALUES (?, ?, ?, ?, ?)";
        
        try {
            // 如果ID为空，生成一个UUID
            if (user.getUserId() == null || user.getUserId().isEmpty()) {
                user.setUserId(UUID.randomUUID().toString().replace("-", ""));
            }
            // 默认状态为1 (启用)
            if (user.getUserStatus() == null || user.getUserStatus().isEmpty()) {
                user.setUserStatus("1");
            }
            
            int rows = template.update(sql, 
                user.getUserId(),
                user.getUserAccount(),
                user.getUserPass(),
                user.getUserName(),
                user.getUserStatus()
            );
            
            return rows > 0;
        } catch (Exception e) {
            logger.error("注册用户失败", e);
            return false;
        }
    }
}
