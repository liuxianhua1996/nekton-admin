-- 创建用户表
CREATE TABLE IF NOT EXISTS tb_users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    enabled  SMALLINT DEFAULT 1,
    tenant_id VARCHAR(255),
    create_time BIGINT,
    update_time BIGINT,
    create_user_id VARCHAR(255),
    update_user_id VARCHAR(255)
);

-- 创建用户角色关联表
CREATE TABLE IF NOT EXISTS tb_user_roles (
    user_id UUID,
    role VARCHAR(20),
    tenant_id VARCHAR(255),
    create_time BIGINT,
    update_time BIGINT,
    create_user_id VARCHAR(255),
    update_user_id VARCHAR(255),
    PRIMARY KEY (user_id, role)
);