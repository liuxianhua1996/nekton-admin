


CREATE TABLE IF NOT EXISTS tb_users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    uuid VARCHAR(150) UNIQUE NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    enabled SMALLINT DEFAULT 1,
    create_time BIGINT,
    update_time BIGINT,
    create_user_id VARCHAR(255),
    update_user_id VARCHAR(255)
);


-- 创建用户角色关联表
CREATE TABLE IF NOT EXISTS tb_user_roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id VARCHAR(120),
    role VARCHAR(20),
    tenant_id VARCHAR(255),
    create_time BIGINT,
    update_time BIGINT,
    create_user_id VARCHAR(255),
    update_user_id VARCHAR(255),
    UNIQUE  (user_id, role)
    );
-- 菜单表
CREATE TABLE IF NOT EXISTS tb_menu (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) UNIQUE,
    name VARCHAR(50) NOT NULL,
    path VARCHAR(255),
    parent_code VARCHAR(50),
    sort_order INT DEFAULT 0,
    tenant_id VARCHAR(255),
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
    tenant_id VARCHAR(255),
    create_time BIGINT,
    update_time BIGINT,
    create_user_id VARCHAR(255),
    update_user_id VARCHAR(255)
    );

-- 角色菜单关联表
CREATE TABLE IF NOT EXISTS tb_role_menu (
    role VARCHAR(20) NOT NULL,
    menu_id UUID NOT NULL,
    tenant_id VARCHAR(255),
    create_time BIGINT,
    update_time BIGINT,
    create_user_id VARCHAR(255),
    update_user_id VARCHAR(255),
    PRIMARY KEY (role, menu_id)
    );


-- 工作流表，存储工作流定义信息
-- json_data字段使用TEXT类型，仅用于持久化存储JSON数据，不参与查询操作
CREATE TABLE IF NOT EXISTS tb_workflow (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    json_data TEXT NOT NULL,
    version INT DEFAULT 1,
    status char(2) NOT NULL,
    tenant_id VARCHAR(255),
    create_time BIGINT,
    update_time BIGINT,
    create_user_id VARCHAR(255),
    update_user_id VARCHAR(255)
);

-- 工作流全局参数表
CREATE TABLE IF NOT EXISTS tb_workflow_global_param (
    id VARCHAR(255) NOT NULL,
    workflow_id VARCHAR(255) NOT NULL, -- 工作流ID，可为空表示全局参数
    param_key VARCHAR(255) NOT NULL,
    param_value TEXT,
    param_type VARCHAR(50) DEFAULT 'global_variable', -- 参数用途类型：global_variable, db_config, api_config
    value_type VARCHAR(50) DEFAULT 'string', -- 值的数据类型：string, number, boolean, json等
    description TEXT,
    tenant_id VARCHAR(255),
    create_time BIGINT,
    update_time BIGINT,
    create_user_id VARCHAR(255),
    update_user_id VARCHAR(255),
    UNIQUE (id, workflow_id)
);