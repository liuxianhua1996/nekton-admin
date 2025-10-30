package com.jing.admin.core.tenant;

import org.springframework.stereotype.Component;

/**
 * @author lxh
 * @date 2025/10/30
 **/
@Component
public class TenantContextHolder {
    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

    public static void setTenantId(String tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    public static String getTenantId() {
        return CURRENT_TENANT.get();
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
