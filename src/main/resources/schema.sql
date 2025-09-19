-- 创建用户表
CREATE TABLE IF NOT EXISTS tb_users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    enabled BOOLEAN DEFAULT true
);

-- 创建用户角色关联表
CREATE TABLE IF NOT EXISTS tb_user_roles (
    user_id BIGINT REFERENCES users(id),
    role VARCHAR(20),
    PRIMARY KEY (user_id, role)
);