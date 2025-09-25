-- PostgreSQL数据库schema定义
-- 针对PostgreSQL设计，工作流表的json_data字段使用TEXT类型
-- 仅用于持久化存储JSON数据，不参与查询操作，TEXT类型提供更好的存储效率和写入性能

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

-- 工作流表，存储工作流定义信息
-- json_data字段使用TEXT类型，仅用于持久化存储JSON数据，不参与查询操作
CREATE TABLE IF NOT EXISTS tb_workflow (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    json_data TEXT NOT NULL,
    version INT DEFAULT 1,
    status VARCHAR(50) DEFAULT '草稿',
    create_user_id VARCHAR(36),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 菜单表
CREATE TABLE IF NOT EXISTS tb_menu (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) UNIQUE,
    name VARCHAR(50) NOT NULL,
    path VARCHAR(255),
    parent_code VARCHAR(50),
    sort_order INT DEFAULT 0,
    create_time BIGINT,
    update_time BIGINT,
    create_user_id VARCHAR(255),
    update_user_id VARCHAR(255)
);

-- 角色表
CREATE TABLE IF NOT EXISTS tb_roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    create_time BIGINT,
    update_time BIGINT,
    create_user_id VARCHAR(255),
    update_user_id VARCHAR(255)
);

-- 角色菜单关联表
CREATE TABLE IF NOT EXISTS tb_role_menu (
    role VARCHAR(20) NOT NULL,
    menu_id UUID NOT NULL,
    create_time BIGINT,
    update_time BIGINT,
    create_user_id VARCHAR(255),
    update_user_id VARCHAR(255),
    PRIMARY KEY (role, menu_id)
);