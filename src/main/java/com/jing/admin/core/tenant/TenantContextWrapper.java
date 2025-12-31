package com.jing.admin.core.tenant;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 租户上下文包装器
 * 用于在多线程环境中正确传递租户上下文
 */
public class TenantContextWrapper {
    
    /**
     * 包装Runnable，使其在执行时保持租户上下文
     *
     * @param runnable 要包装的Runnable
     * @param tenantId 租户ID
     * @return 包装后的Runnable
     */
    public static Runnable wrap(final Runnable runnable, final String tenantId) {
        return () -> {
            String originalTenantId = TenantContextHolder.getTenantId();
            try {
                TenantContextHolder.setTenantId(tenantId);
                runnable.run();
            } finally {
                // 恢复原始租户上下文
                if (originalTenantId != null) {
                    TenantContextHolder.setTenantId(originalTenantId);
                } else {
                    TenantContextHolder.clear();
                }
            }
        };
    }
    
    /**
     * 包装Callable，使其在执行时保持租户上下文
     *
     * @param callable 要包装的Callable
     * @param tenantId 租户ID
     * @return 包装后的Callable
     */
    public static <T> Callable<T> wrap(final Callable<T> callable, final String tenantId) {
        return () -> {
            String originalTenantId = TenantContextHolder.getTenantId();
            try {
                TenantContextHolder.setTenantId(tenantId);
                return callable.call();
            } finally {
                // 恢复原始租户上下文
                if (originalTenantId != null) {
                    TenantContextHolder.setTenantId(originalTenantId);
                } else {
                    TenantContextHolder.clear();
                }
            }
        };
    }
    
    /**
     * 在指定租户上下文中执行Runnable
     *
     * @param runnable 要执行的Runnable
     * @param tenantId 租户ID
     */
    public static void executeInTenantContext(final Runnable runnable, final String tenantId) {
        String originalTenantId = TenantContextHolder.getTenantId();
        try {
            TenantContextHolder.setTenantId(tenantId);
            runnable.run();
        } finally {
            // 恢复原始租户上下文
            if (originalTenantId != null) {
                TenantContextHolder.setTenantId(originalTenantId);
            } else {
                TenantContextHolder.clear();
            }
        }
    }
    
    /**
     * 在指定租户上下文中执行Callable
     *
     * @param callable 要执行的Callable
     * @param tenantId 租户ID
     * @return 执行结果
     * @throws Exception 执行过程中可能抛出的异常
     */
    public static <T> T executeInTenantContext(final java.util.concurrent.Callable<T> callable, final String tenantId) throws Exception {
        String originalTenantId = TenantContextHolder.getTenantId();
        try {
            TenantContextHolder.setTenantId(tenantId);
            return callable.call();
        } finally {
            // 恢复原始租户上下文
            if (originalTenantId != null) {
                TenantContextHolder.setTenantId(originalTenantId);
            } else {
                TenantContextHolder.clear();
            }
        }
    }
}