-- 创建签字存放表（直接存储二进制数据版本）
CREATE TABLE JZS_SIGNATURE (
    -- 主键
    SIGNATURE_ID VARCHAR2(36) PRIMARY KEY,
    
    -- 用户账号（外键，关联到JZS_USERS表的USER_ACCOUNT）
    USER_ACCOUNT VARCHAR2(36) NOT NULL,
    
    -- 签名类型（如：审核、检验、批准等）
    SIGNATURE_TYPE VARCHAR2(50) NOT NULL,
    
    -- 签名图片二进制数据（直接存储在数据库中）
    SIGNATURE_BLOB BLOB NOT NULL,
    
    -- 签名图片类型（如：png、jpg等）
    IMAGE_TYPE VARCHAR2(20) NOT NULL,
    
    -- 签名图片大小（字节）
    IMAGE_SIZE NUMBER NOT NULL,
    
    -- 签名图片宽度（像素）
    IMAGE_WIDTH NUMBER,
    
    -- 签名图片高度（像素）
    IMAGE_HEIGHT NUMBER,
    
    -- 创建时间
    CREATE_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- 更新时间
    UPDATE_TIME TIMESTAMP,
    
    -- 备注
    REMARKS VARCHAR2(500),
    
    -- 外键约束，关联到用户表
    CONSTRAINT FK_SIGNATURE_USER FOREIGN KEY (USER_ACCOUNT) REFERENCES JZS_USERS(USER_ACCOUNT),
    
    -- 唯一约束，确保每个用户每种签名类型只有一条记录
    CONSTRAINT UK_SIGNATURE_USER_TYPE UNIQUE (USER_ACCOUNT, SIGNATURE_TYPE)
);
COMMIT;