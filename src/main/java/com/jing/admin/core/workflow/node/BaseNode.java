package com.jing.admin.core.workflow.node;

import com.jing.admin.core.workflow.core.conversion.ParameterConverter;
import com.jing.admin.core.workflow.exception.NodeExecutor;

/**
 * @author lxh
 * @date 2025/11/6
 **/
public abstract class BaseNode implements NodeExecutor {
    protected ParameterConverter parameterConverter;
    public BaseNode(ParameterConverter parameterConverter) {
        this.parameterConverter = parameterConverter;
    }
}
