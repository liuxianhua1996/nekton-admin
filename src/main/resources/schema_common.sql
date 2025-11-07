-- PostgreSQL数据库schema定义
-- 针对PostgreSQL设计，工作流表的json_data字段使用TEXT类型
-- 仅用于持久化存储JSON数据，不参与查询操作，TEXT类型提供更好的存储效率和写入性能

-- 创建租户用户表
CREATE TABLE IF NOT EXISTS tb_users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    enabled SMALLINT DEFAULT 1,
    -- 移除原来的tenant_id字段，因为现在一个用户可以有多个租户
    create_time BIGINT,
    update_time BIGINT,
    create_user_id VARCHAR(255),
    update_user_id VARCHAR(255)
);

-- 租户基本信息表
CREATE TABLE IF NOT EXISTS tb_tenants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_code VARCHAR(50) UNIQUE NOT NULL, -- 租户唯一编码
    tenant_name VARCHAR(100) NOT NULL, -- 租户名称
    description TEXT, -- 租户描述
    status VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, INACTIVE, SUSPENDED
    create_time BIGINT,
    update_time BIGINT,
    create_user_id VARCHAR(255),
    update_user_id VARCHAR(255)
    ;

-- 用户-租户关联表（解决多对多关系）
CREATE TABLE IF NOT EXISTS tb_user_tenant (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES tb_users(id) ON DELETE CASCADE,
    tenant_id UUID NOT NULL REFERENCES tb_tenants(id) ON DELETE CASCADE,
    is_default BOOLEAN DEFAULT FALSE, -- 是否为用户的默认租户
    status VARCHAR(20) DEFAULT 'ACTIVE', -- 用户在该租户的状态
    create_time BIGINT,
    update_time BIGINT,
    create_user_id VARCHAR(255),
    update_user_id VARCHAR(255),
    -- 唯一约束，确保一个用户在同一个租户中只能有一条记录
    UNIQUE(user_id, tenant_id)
    );

-- 在公共数据库中管理租户数据库连接信息
CREATE TABLE IF NOT EXISTS tb_tenant_metadata (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES tb_tenants(id) ON DELETE CASCADE,
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

-- 创建索引以提高查询性能
CREATE INDEX IF NOT EXISTS idx_user_tenant_user_id ON tb_user_tenant(user_id);
CREATE INDEX IF NOT EXISTS idx_user_tenant_tenant_id ON tb_user_tenant(tenant_id);
CREATE INDEX IF NOT EXISTS idx_user_tenant_default ON tb_user_tenant(user_id, is_default) WHERE is_default = true;
CREATE INDEX IF NOT EXISTS idx_tenants_status ON tb_tenants(status);
CREATE INDEX IF NOT EXISTS idx_tenant_metadata_tenant_id ON tb_tenant_metadata(tenant_id);