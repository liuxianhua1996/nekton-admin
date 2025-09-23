-- 插入默认管理员用户
INSERT INTO tb_users (username, password, email, enabled) VALUES
('admin', '$2a$10$IcYw33M9T9QfyrV0kkYVv.bK18r.MbrGwxXAkLl1nzNv4TgkxNbaW', 'admin@example.com', 1)
ON CONFLICT (username) DO NOTHING;

-- 插入默认角色
INSERT INTO tb_user_roles (user_id, role)
SELECT id, 'ADMIN' FROM tb_users WHERE username = 'admin'
ON CONFLICT DO NOTHING;