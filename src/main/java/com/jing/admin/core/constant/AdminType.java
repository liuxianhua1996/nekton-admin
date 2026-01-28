package com.jing.admin.core.constant;

public enum AdminType {
    SUPER_ADMIN("super_admin"),
    ADMIN("admin");

    private final String code;

    AdminType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static AdminType fromCode(String code) {
        for (AdminType type : AdminType.values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的管理员类型: " + code);
    }
}
