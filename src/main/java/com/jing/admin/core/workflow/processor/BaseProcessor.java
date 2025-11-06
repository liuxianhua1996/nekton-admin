package com.jing.admin.core.workflow.processor;

import com.jing.admin.core.workflow.conversion.ParameterConverter;
import com.jing.admin.core.workflow.node.NodeExecutor;

/**
 * @author lxh
 * @date 2025/11/6
 **/
public abstract class BaseProcessor implements NodeExecutor {
    protected ParameterConverter parameterConverter;
    public BaseProcessor(ParameterConverter parameterConverter) {
        this.parameterConverter = parameterConverter;
    }
}
