package com.jing.admin.model;

/**
 * 角色枚举类，定义系统中所有可用的角色
 */
public enum Role {
    ADMIN("管理员", 1),
    USER("普通用户", 2),
    MANAGER("经理", 3),
    OPERATOR("操作员", 4),
    GUEST("访客", 5);

    private final String description;
    private final int level;

    Role(String description, int level) {
        this.description = description;
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public int getLevel() {
        return level;
    }

    /**
     * 根据角色名称获取角色枚举
     * @param name 角色名称
     * @return 角色枚举
     */
    public static Role fromName(String name) {
        for (Role role : Role.values()) {
            if (role.name().equalsIgnoreCase(name)) {
                return role;
            }
        }
        throw new IllegalArgumentException("未知的角色: " + name);
    }

    /**
     * 检查当前角色是否高于指定角色
     * @param other 另一个角色
     * @return 如果当前角色级别高于指定角色，返回true
     */
    public boolean isHigherThan(Role other) {
        return this.level < other.level;
    }

    /**
     * 检查当前角色是否等于或高于指定角色
     * @param other 另一个角色
     * @return 如果当前角色级别等于或高于指定角色，返回true
     */
    public boolean isHigherThanOrEqual(Role other) {
        return this.level <= other.level;
    }
}