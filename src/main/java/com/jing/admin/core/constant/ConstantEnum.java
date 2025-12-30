package com.ylys.datacenter.common.enums;

/**
 * @author zhicheng
 * @date 2024/6/4
 **/
public enum ConstantEnum {
    SUCCESS("success","成功"),
    PROCESS("process","运行在"),
    FAIL("fail","成功");
    private String value;
    private String label;
    ConstantEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }
}
