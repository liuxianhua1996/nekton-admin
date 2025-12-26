-- 插入默认管理员用户 admin or qweqwe123123
INSERT INTO tb_users (username, password, email, enabled)
VALUES ('admin', '$2a$10$IcYw33M9T9QfyrV0kkYVv.bK18r.MbrGwxXAkLl1nzNv4TgkxNbaW', 'admin@example.com',
        1) ON CONFLICT (username) DO NOTHING;

-- 2. 创建一个默认租户
INSERT INTO tb_tenants (tenant_code, tenant_name, description, status, create_time, update_time, create_user_id,
                        update_user_id)
VALUES ('fj_jxkj',
        '晋享科技',
        '系统默认创建的租户',
        'ACTIVE',
        EXTRACT(EPOCH FROM NOW()) * 1000,
        EXTRACT(EPOCH FROM NOW()) * 1000,
        'system',
        'system');
-- 3. 将 admin 用户关联到默认租户
INSERT INTO tb_user_tenant (user_id, tenant_id, is_default, status, create_time, update_time, create_user_id, update_user_id)
SELECT
    u.id,
    t.id,
    TRUE, -- 设置为默认租户
    'ACTIVE',
    EXTRACT(EPOCH FROM NOW()) * 1000,
    EXTRACT(EPOCH FROM NOW()) * 1000,
    'system',
    'system'
FROM tb_users u, tb_tenants t
WHERE u.username = 'admin' AND t.tenant_code = 'fj_jxkj';

INSERT INTO tb_tenant_metadata("id", "tenant_id", "db_name", "jdbc_url", "username", "password", "driver_class_name", "status", "max_pool_size", "min_idle", "created_at", "updated_at")
VALUES ('32d4e70c-e71c-42c4-947f-0a0a141ac211', '053cf49d-d1e7-4751-a26b-cd82c05e6af8', 'jxkj', 'jdbc:postgresql://192.168.10.100:5432/jxkj', 'jxjk',
        'jxkj123a!', 'org.postgresql.Driver', 'ACTIVE', 15, 3, '2025-12-26 06:43:03.474272+00', '2025-12-26 06:43:03.474272+00');
