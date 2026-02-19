package org.example.work121.service.impl;

import org.example.work121.entity.User;
import org.example.work121.mapper.UserMapper;
import org.example.work121.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> getAllUsers() {
        return userMapper.selectAll();
    }

    @Override
    public User getUserByAccount(String userAccount) {
        logger.info("根据用户名查询用户: {}", userAccount);
        return userMapper.selectByAccount(userAccount);
    }

    @Override
    public List<User> getUserByName(String userName) {
        logger.info("根据姓名查询用户: {}", userName);
        return userMapper.selectByName(userName);
    }

    @Override
    public User getUserById(String id) {
        logger.info("根据ID查询用户: {}", id);
        return userMapper.selectById(id);
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
        
        // 使用具体的列名以确保映射正确，并模拟userStatus
        String sql = "SELECT " +
                "ID as id, " +
                "USER_ACCOUNT as userAccount, " +
                "USER_PASS as userPass, " +
                "USER_NAME as userName, " +
                "USER_TEL as userTel, " +
                "USER_EMAIL as userEmail, " +
                "ORGNIZATION_NAME as orgnizationName, " +
                "SH_TYPE as shType, " +
                "'1' as userStatus " +
                "FROM JZS_USERS WHERE USER_ACCOUNT = ?";
        
        try {
            List<User> users = tempJdbcTemplate.query(
                sql,
                new Object[]{userAccount},
                new BeanPropertyRowMapper<>(User.class)
            );
            
            if (!users.isEmpty()) {
                User user = users.get(0);
                logger.info("查询到用户: {}", user.getUserId());
                return user;
            } else {
                logger.warn("未找到用户: {}", userAccount);
                return null;
            }
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
        String sql = "INSERT INTO JZS_USERS (ID, USER_ACCOUNT, USER_PASS, USER_NAME, USER_TEL, USER_EMAIL, ORGNIZATION_NAME, SH_TYPE) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try {
            // 如果ID为空，生成一个UUID
            if (user.getUserId() == null || user.getUserId().isEmpty()) {
                user.setUserId(UUID.randomUUID().toString().replace("-", ""));
            }
            // userStatus不再保存到数据库，但保留对象中的逻辑如果需要
            if (user.getUserStatus() == null || user.getUserStatus().isEmpty()) {
                user.setUserStatus("1");
            }
            
            int rows = template.update(sql, 
                user.getUserId(),
                user.getUserAccount(),
                user.getUserPass(),
                user.getUserName(),
                user.getUserTel(),
                user.getUserEmail(),
                user.getOrgnizationName(),
                user.getShType()
            );
            
            return rows > 0;
        } catch (Exception e) {
            logger.error("注册用户失败", e);
            return false;
        }
    }
}
