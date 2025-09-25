-- 插入默认角色
INSERT INTO tb_roles (name, description)
VALUES ('ADMIN', '系统管理员角色，拥有所有权限'),
       ('USER', '普通用户角色，拥有基本权限') ON CONFLICT (name) DO NOTHING;

-- 插入默认管理员用户
INSERT INTO tb_users (username, password, email, enabled)
VALUES ('admin', '$2a$10$IcYw33M9T9QfyrV0kkYVv.bK18r.MbrGwxXAkLl1nzNv4TgkxNbaW', 'admin@example.com',
        1) ON CONFLICT (username) DO NOTHING;

-- 插入默认角色
INSERT INTO tb_user_roles (user_id, role)
SELECT id, 'ADMIN'
FROM tb_users
WHERE username = 'admin' ON CONFLICT DO NOTHING;

-- 删除已存在的菜单数据（用于重新初始化）
DELETE
FROM tb_menu;

-- 插入菜单数据
INSERT INTO tb_menu (code, name, path, parent_code, sort_order, create_time, update_time, create_user_id, update_user_id)
VALUES
-- 顶级菜单
('DASHBOARD', '首页', '/dashboard', NULL, 1,1696032000000, 1696032000000, 'system', 'system'),
('USER_MANAGE', '用户管理', '/user', NULL, 2,1696032000000, 1696032000000, 'system', 'system'),
('WORKFLOW', '工作流', '/workflow', NULL, 3,1696032000000, 1696032000000, 'system', 'system'),
('PERMISSION', '权限管理', '/permission', NULL, 4, 1696032000000, 1696032000000, 'system', 'system'),

-- 用户管理子菜单
('USER_LIST', '用户列表', '/user/list', 'USER_MANAGE', 1,1696032000000, 1696032000000, 'system',
 'system'),
('USER_DETAIL', '用户详情', '/user/detail', 'USER_MANAGE', 2,1696032000000, 1696032000000, 'system',
 'system'),

-- 工作流子菜单
('WORKFLOW_LIST', '工作流列表', '/workflow/list', 'WORKFLOW', 1,1696032000000, 1696032000000,
 'system', 'system'),
('WORKFLOW_DETAIL', '工作流详情', '/workflow/:id','WORKFLOW', 2,1696032000000, 1696032000000,
 'system', 'system'),

-- 权限管理子菜单
('ROLE_MANAGE', '角色管理', '/permission/roles','PERMISSION', 1, 1696032000000, 1696032000000,
 'system', 'system'),
('PERMISSION_ASSIGN', '权限分配', '/permission/assign', 'PERMISSION', 2, 1696032000000,
 1696032000000, 'system', 'system'),
('ADMIN_MANAGE', '管理员管理', '/permission/admins','PERMISSION', 3, 1696032000000, 1696032000000,
 'system', 'system');