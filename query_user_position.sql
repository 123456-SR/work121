-- 根据用户表中的 user account 匹配 JZS_USER_EXT 表中的 id，查询登录用户的职务 position
SELECT ue.POSITION
FROM JZS_USERS u
JOIN JZS_USER_EXT ue ON u.ID = ue.ID
WHERE u.USER_ACCOUNT = ?;

-- 说明：
-- 1. 假设 JZS_USERS 表存在，并且包含 ID（主键）和 USER_ACCOUNT 字段
-- 2. JZS_USER_EXT 表的 ID 与 JZS_USERS 表的 ID 关联
-- 3. 使用参数 ? 表示登录用户的 user account，实际使用时需要替换为具体的用户账号
-- 4. 查询结果将返回该用户的职务 position
