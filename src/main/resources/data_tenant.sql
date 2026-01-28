INSERT INTO tb_users (uuid, username, password, email, enabled)
VALUES ("", 'admin', '$2a$10$IcYw33M9T9QfyrV0kkYVv.bK18r.MbrGwxXAkLl1nzNv4TgkxNbaW', 'admin@example.com',
        1) ON CONFLICT (username) DO NOTHING;
-- 插入默认角色
INSERT INTO tb_roles (name, description)
VALUES ('管理层', '管理层'),
       ('普通员工', '普通员工') ON CONFLICT (name) DO NOTHING;

-- 插入默认角色关联
-- INSERT INTO tb_user_roles (user_id, role_id, create_time, update_time, create_user_id, update_user_id)
-- SELECT 
--     u.id,
--     r.id,
--     1696032000000,
--     1696032000000,
--     'system',
--     'system'
-- FROM tb_users u, tb_roles r
-- WHERE u.username = 'admin' AND r.name = '超级管理员';

INSERT INTO tb_admins (user_id, admin_type, create_time, update_time, create_user_id, update_user_id)
SELECT
    u.id,
    'super_admin',
    1696032000000,
    1696032000000,
    'system',
    'system'
FROM tb_users u
WHERE u.username = 'admin' ON CONFLICT (user_id) DO NOTHING;

-- 删除已存在的菜单数据（用于重新初始化）
DELETE FROM tb_menu;

-- 插入菜单数据
INSERT INTO tb_menu (code, name, path, parent_code, sort_order, create_time, update_time, create_user_id, update_user_id)
VALUES
-- 基础功能
('DASHBOARD', '工作台', '/dashboard', NULL, 1, 1696032000000, 1696032000000, 'system', 'system'),

-- 用户管理模块
('USER_MANAGE', '用户管理', '/user', NULL, 2, 1696032000000, 1696032000000, 'system', 'system'),
('USER_LIST', '用户列表', '/user/list', 'USER_MANAGE', 1, 1696032000000, 1696032000000, 'system', 'system'),

-- 权限管理模块
('PERMISSION', '权限管理', '/permission', NULL, 3, 1696032000000, 1696032000000, 'system', 'system'),
('PERMISSION_ROLES', '角色管理', '/permission/roles', 'PERMISSION', 2, 1696032000000, 1696032000000, 'system', 'system'),
('ADMIN_MANAGE', '管理员管理', '/permission/admins','PERMISSION', 3, 1696032000000, 1696032000000, 'system', 'system'),

-- 调度管理模块
('SCHEDULE', '调度管理', '/schedule', NULL, 4, 1696032000000, 1696032000000, 'system', 'system'),
('SCHEDULE_LIST', '调度列表', '/schedule/list','SCHEDULE', 1, 1696032000000, 1696032000000, 'system', 'system'),

-- 工作流模块
('WORKFLOW', '工作流', '/workflow', NULL, 5, 1696032000000, 1696032000000, 'system', 'system'),
('WORKFLOW_LIST', '工作流列表', '/workflow/list', 'WORKFLOW', 1, 1696032000000, 1696032000000, 'system', 'system'),

-- 低代码模块
('LOW_CODE', '低代码', '/lowcode', NULL, 6, 1696032000000, 1696032000000, 'system', 'system'),
('LOW_CODE_LIST', '应用管理', '/lowcode', 'LOW_CODE', 1, 1696032000000, 1696032000000, 'system', 'system'),

-- AI智能模块
('AI_SMART', 'AI智能', '/ai', NULL, 7, 1696032000000, 1696032000000, 'system', 'system'),
('AI_QUERY', 'AI问数', '/ai/query', 'AI_SMART', 1, 1696032000000, 1696032000000, 'system', 'system'),
('AI_CHAT', 'AI问答', '/ai/chat', 'AI_SMART', 2, 1696032000000, 1696032000000, 'system', 'system'),

-- 数据中心模块
('DATA_CENTER', '数据中心', '/data-warehouse', NULL, 8, 1696032000000, 1696032000000, 'system', 'system'),
('DATA_WAREHOUSE', '数仓管理', '/data-warehouse', 'DATA_CENTER', 1, 1696032000000, 1696032000000, 'system', 'system'),
('KNOWLEDGE_BASE', '知识库', '/data-warehouse/knowledge', 'DATA_CENTER', 2, 1696032000000, 1696032000000, 'system', 'system');

-- 工作流全局参数示例数据
-- 注意：在实际部署时，需要根据当前租户ID来插入数据
-- INSERT INTO tb_workflow_global_param (param_key, param_value, param_type, description, tenant_id, create_time, update_time, create_user_id, update_user_id)
-- VALUES
-- ('api.base.url', 'https://api.example.com', 'string', 'API基础URL', 'your-tenant-id', 1696032000000, 1696032000000, 'system', 'system'),
-- ('timeout.duration', '30000', 'number', '请求超时时间(毫秒)', 'your-tenant-id', 1696032000000, 1696032000000, 'system', 'system'),
-- ('enable.logging', 'true', 'boolean', '是否启用详细日志', 'your-tenant-id', 1696032000000, 1696032000000, 'system', 'system');
