-- PostgreSQL数据库schema定义
-- 针对PostgreSQL设计，工作流表的json_data字段使用TEXT类型
-- 仅用于持久化存储JSON数据，不参与查询操作，TEXT类型提供更好的存储效率和写入性能

-- 创建租户用户表
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
-- 在公共数据库中管理租户信息
CREATE TABLE tb_tenant_metadata (
     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
     tenant_id VARCHAR(50) UNIQUE NOT NULL,
     db_name VARCHAR(100) NOT NULL,
     jdbc_url VARCHAR(500) NOT NULL,
     username VARCHAR(100) NOT NULL,
     password VARCHAR(200) NOT NULL, -- 建议加密存储
     driver_class_name VARCHAR(100) DEFAULT 'org.postgresql.Driver',
     status VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, INACTIVE, SUSPENDED
     max_pool_size INT DEFAULT 15,
     min_idle INT DEFAULT 3,
     created_at TIMESTAMPTZ DEFAULT NOW(),
     updated_at TIMESTAMPTZ DEFAULT NOW()
);