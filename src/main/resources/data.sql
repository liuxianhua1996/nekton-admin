-- 插入默认管理员用户
INSERT INTO users (username, password, email, enabled) VALUES 
('admin', '$2a$10$w9ziB3K2ZfzB1zZ7zZ7zZu1zZ7zZ7zZ7zZ7zZ7zZ7zZ7zZ7zZ7zZ7', 'admin@example.com', true)
ON CONFLICT (username) DO NOTHING;

-- 插入默认角色
INSERT INTO user_roles (user_id, role) 
SELECT id, 'ADMIN' FROM users WHERE username = 'admin'
ON CONFLICT DO NOTHING;