


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

-- 用户表字段注释
COMMENT ON TABLE tb_users IS '用户表';
COMMENT ON COLUMN tb_users.id IS '用户ID';
COMMENT ON COLUMN tb_users.uuid IS '用户UUID';
COMMENT ON COLUMN tb_users.username IS '用户名';
COMMENT ON COLUMN tb_users.password IS '密码';
COMMENT ON COLUMN tb_users.email IS '邮箱';
COMMENT ON COLUMN tb_users.enabled IS '是否启用';
COMMENT ON COLUMN tb_users.create_time IS '创建时间';
COMMENT ON COLUMN tb_users.update_time IS '更新时间';
COMMENT ON COLUMN tb_users.create_user_id IS '创建用户ID';
COMMENT ON COLUMN tb_users.update_user_id IS '更新用户ID';


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

-- 用户角色关联表字段注释
COMMENT ON TABLE tb_user_roles IS '用户角色关联表';
COMMENT ON COLUMN tb_user_roles.id IS '关联ID';
COMMENT ON COLUMN tb_user_roles.user_id IS '用户ID';
COMMENT ON COLUMN tb_user_roles.role IS '角色';
COMMENT ON COLUMN tb_user_roles.tenant_id IS '租户ID';
COMMENT ON COLUMN tb_user_roles.create_time IS '创建时间';
COMMENT ON COLUMN tb_user_roles.update_time IS '更新时间';
COMMENT ON COLUMN tb_user_roles.create_user_id IS '创建用户ID';
COMMENT ON COLUMN tb_user_roles.update_user_id IS '更新用户ID';
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

-- 菜单表字段注释
COMMENT ON TABLE tb_menu IS '菜单表';
COMMENT ON COLUMN tb_menu.id IS '菜单ID';
COMMENT ON COLUMN tb_menu.code IS '菜单编码';
COMMENT ON COLUMN tb_menu.name IS '菜单名称';
COMMENT ON COLUMN tb_menu.path IS '菜单路径';
COMMENT ON COLUMN tb_menu.parent_code IS '父菜单编码';
COMMENT ON COLUMN tb_menu.sort_order IS '排序';
COMMENT ON COLUMN tb_menu.tenant_id IS '租户ID';
COMMENT ON COLUMN tb_menu.create_time IS '创建时间';
COMMENT ON COLUMN tb_menu.update_time IS '更新时间';
COMMENT ON COLUMN tb_menu.create_user_id IS '创建用户ID';
COMMENT ON COLUMN tb_menu.update_user_id IS '更新用户ID';

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

-- 角色表字段注释
COMMENT ON TABLE tb_roles IS '角色表';
COMMENT ON COLUMN tb_roles.id IS '角色ID';
COMMENT ON COLUMN tb_roles.name IS '角色名称';
COMMENT ON COLUMN tb_roles.description IS '角色描述';
COMMENT ON COLUMN tb_roles.tenant_id IS '租户ID';
COMMENT ON COLUMN tb_roles.create_time IS '创建时间';
COMMENT ON COLUMN tb_roles.update_time IS '更新时间';
COMMENT ON COLUMN tb_roles.create_user_id IS '创建用户ID';
COMMENT ON COLUMN tb_roles.update_user_id IS '更新用户ID';

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

-- 角色菜单关联表字段注释
COMMENT ON TABLE tb_role_menu IS '角色菜单关联表';
COMMENT ON COLUMN tb_role_menu.role IS '角色';
COMMENT ON COLUMN tb_role_menu.menu_id IS '菜单ID';
COMMENT ON COLUMN tb_role_menu.tenant_id IS '租户ID';
COMMENT ON COLUMN tb_role_menu.create_time IS '创建时间';
COMMENT ON COLUMN tb_role_menu.update_time IS '更新时间';
COMMENT ON COLUMN tb_role_menu.create_user_id IS '创建用户ID';
COMMENT ON COLUMN tb_role_menu.update_user_id IS '更新用户ID';


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

-- 工作流表字段注释
COMMENT ON TABLE tb_workflow IS '工作流表';
COMMENT ON COLUMN tb_workflow.id IS '工作流ID';
COMMENT ON COLUMN tb_workflow.name IS '工作流名称';
COMMENT ON COLUMN tb_workflow.description IS '工作流描述';
COMMENT ON COLUMN tb_workflow.json_data IS '工作流JSON数据';
COMMENT ON COLUMN tb_workflow.version IS '版本';
COMMENT ON COLUMN tb_workflow.status IS '状态';
COMMENT ON COLUMN tb_workflow.tenant_id IS '租户ID';
COMMENT ON COLUMN tb_workflow.create_time IS '创建时间';
COMMENT ON COLUMN tb_workflow.update_time IS '更新时间';
COMMENT ON COLUMN tb_workflow.create_user_id IS '创建用户ID';
COMMENT ON COLUMN tb_workflow.update_user_id IS '更新用户ID';

-- 工作流全局参数表
CREATE TABLE IF NOT EXISTS tb_workflow_global_param (
    id VARCHAR(255) NOT NULL,
    workflow_id VARCHAR(255) NOT NULL,
    param_key VARCHAR(255) NOT NULL,
    param_value TEXT,
    param_type VARCHAR(50) DEFAULT 'global_variable',
    value_type VARCHAR(50) DEFAULT 'string',
    description TEXT,
    tenant_id VARCHAR(255),
    create_time BIGINT,
    update_time BIGINT,
    create_user_id VARCHAR(255),
    update_user_id VARCHAR(255),
    UNIQUE (id, workflow_id)
);

-- 工作流全局参数表字段注释
COMMENT ON TABLE tb_workflow_global_param IS '工作流全局参数表';
COMMENT ON COLUMN tb_workflow_global_param.id IS '参数ID';
COMMENT ON COLUMN tb_workflow_global_param.workflow_id IS '工作流ID';
COMMENT ON COLUMN tb_workflow_global_param.param_key IS '参数键';
COMMENT ON COLUMN tb_workflow_global_param.param_value IS '参数值';
COMMENT ON COLUMN tb_workflow_global_param.param_type IS '参数用途类型：global_variable, db_config, api_config';
COMMENT ON COLUMN tb_workflow_global_param.value_type IS '值的数据类型：string, number, boolean, json等';
COMMENT ON COLUMN tb_workflow_global_param.description IS '参数描述';
COMMENT ON COLUMN tb_workflow_global_param.tenant_id IS '租户ID';
COMMENT ON COLUMN tb_workflow_global_param.create_time IS '创建时间';
COMMENT ON COLUMN tb_workflow_global_param.update_time IS '更新时间';
COMMENT ON COLUMN tb_workflow_global_param.create_user_id IS '创建用户ID';
COMMENT ON COLUMN tb_workflow_global_param.update_user_id IS '更新用户ID';

-- 调度工作流表
CREATE TABLE IF NOT EXISTS tb_schedule_job (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    workflow_id VARCHAR(255) NOT NULL,
    trigger_type VARCHAR(20) NOT NULL,
    trigger_config TEXT,
    status VARCHAR(20) DEFAULT 'DISABLED',
    description TEXT,
    tenant_id VARCHAR(255),
    create_time BIGINT,
    update_time BIGINT,
    create_user_id VARCHAR(255),
    update_user_id VARCHAR(255)
);

-- 调度工作流表字段注释
COMMENT ON TABLE tb_schedule_job IS '调度工作流表';
COMMENT ON COLUMN tb_schedule_job.name IS '调度名称';
COMMENT ON COLUMN tb_schedule_job.workflow_id IS '工作流ID';
COMMENT ON COLUMN tb_schedule_job.trigger_type IS '触发方式：cron-定时，webhook-Webhook，mqtt-MQTT';
COMMENT ON COLUMN tb_schedule_job.trigger_config IS '触发配置(json格式，如cron表达式、webhook地址、MQTT配置等)';
COMMENT ON COLUMN tb_schedule_job.status IS '状态：DISABLED-停用，ENABLED-启用';
COMMENT ON COLUMN tb_schedule_job.description IS '描述';

-- 调度工作流执行记录表
CREATE TABLE IF NOT EXISTS tb_schedule_job_log (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    job_id VARCHAR(255) NOT NULL,
    workflow_id VARCHAR(255) NOT NULL,
    trigger_type VARCHAR(20),
    status VARCHAR(20),
    result TEXT,
    start_time BIGINT,
    end_time BIGINT,
    execution_time BIGINT,
    error_message TEXT,
    tenant_id VARCHAR(255),
    create_time BIGINT,
    update_time BIGINT,
    create_user_id VARCHAR(255),
    update_user_id VARCHAR(255)
);

-- 调度工作流执行记录表字段注释
COMMENT ON TABLE tb_schedule_job_log IS '调度工作流执行记录表';
COMMENT ON COLUMN tb_schedule_job_log.job_id IS '调度任务ID';
COMMENT ON COLUMN tb_schedule_job_log.workflow_id IS '工作流ID';
COMMENT ON COLUMN tb_schedule_job_log.trigger_type IS '触发方式';
COMMENT ON COLUMN tb_schedule_job_log.status IS '执行状态：SUCCESS-成功，FAILED-失败';
COMMENT ON COLUMN tb_schedule_job_log.result IS '执行结果';
COMMENT ON COLUMN tb_schedule_job_log.start_time IS '开始执行时间';
COMMENT ON COLUMN tb_schedule_job_log.end_time IS '结束执行时间';
COMMENT ON COLUMN tb_schedule_job_log.execution_time IS '执行耗时(毫秒)';
COMMENT ON COLUMN tb_schedule_job_log.error_message IS '错误信息';