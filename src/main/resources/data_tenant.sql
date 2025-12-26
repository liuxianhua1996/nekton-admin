-- 插入默认角色
INSERT INTO tb_roles (name, description)
VALUES ('ADMIN', '系统管理员角色，拥有所有权限'),
       ('USER', '普通用户角色，拥有基本权限') ON CONFLICT (name) DO NOTHING;

-- 插入默认角色
INSERT INTO tb_user_roles (user_id, role)
VALUES ('a851255e-d9fa-4a3d-b238-ef7651f0c422','ADMIN');

-- 删除已存在的菜单数据（用于重新初始化）
DELETE FROM tb_menu;

-- 插入菜单数据
INSERT INTO tb_menu (code, name, path, parent_code, sort_order, create_time, update_time, create_user_id, update_user_id)
VALUES
-- 顶级菜单
('DASHBOARD', '首页', '/dashboard', NULL, 1,1696032000000, 1696032000000, 'system', 'system'),
('USER_MANAGE', '用户管理', '/user', NULL, 2,1696032000000, 1696032000000, 'system', 'system'),
('WORKFLOW', '工作流', '/workflow', NULL, 3,1696032000000, 1696032000000, 'system', 'system'),
('LOW_CODE', '低代码', '/lowcode', NULL, 4, 1696032000000, 1696032000000, 'system', 'system'),
('PERMISSION', '权限管理', '/permission', NULL, 5, 1696032000000, 1696032000000, 'system', 'system'),

-- 用户管理子菜单
('USER_LIST', '用户列表', '/user/list', 'USER_MANAGE', 1,1696032000000, 1696032000000, 'system',
 'system'),

-- 工作流子菜单
('WORKFLOW_LIST', '工作流列表', '/workflow/list', 'WORKFLOW', 1,1696032000000, 1696032000000,
 'system', 'system'),
-- 低代码
('LOW_CODE_LIST', '页面管理', '/lowcode', 'LOW_CODE', 1,1696032000000, 1696032000000,
 'system', 'system'),
-- 权限管理子菜单
('ROLE_MANAGE', '角色管理', '/permission/roles','PERMISSION', 1, 1696032000000, 1696032000000,
 'system', 'system'),
('PERMISSION_ASSIGN', '权限分配', '/permission/assign', 'PERMISSION', 2, 1696032000000,
 1696032000000, 'system', 'system'),
('ADMIN_MANAGE', '管理员管理', '/permission/admins','PERMISSION', 3, 1696032000000, 1696032000000,
 'system', 'system');