package com.jing.admin.core.workflow.node;

import com.jing.admin.core.workflow.core.context.WorkflowContext;
import com.jing.admin.core.workflow.core.conversion.ParameterConverter;
import com.jing.admin.core.workflow.exception.NodeExecutor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lxh
 * @date 2025/11/6
 **/
public abstract class BaseNode implements NodeExecutor {
    protected ParameterConverter parameterConverter;
    public BaseNode(ParameterConverter parameterConverter) {
        this.parameterConverter = parameterConverter;
    }

    public Map<String, Object> processOutParams(Map<String, Object> params, WorkflowContext context){
        Map<String, Object> processedParams = new HashMap<>();

        if (params != null) {
            Map<String, Object> processedParamsMap = new HashMap<>();

            for (Map.Entry<String, Object> entry : params.entrySet()) {
                String paramName = entry.getKey();
                Map<String, Object> paramDetails = (Map<String, Object>) entry.getValue();

                // 获取参数值和类型
                Object value = paramDetails.get("value");
                String valueType = (String) paramDetails.get("valueType");

                // 使用参数转换器处理参数值
                Object convertedValue = parameterConverter.convertParameter(value, valueType, context);

                // 创建新的参数详情
                Map<String, Object> newParamDetails = new HashMap<>(paramDetails);
                newParamDetails.put("value", convertedValue);

                processedParamsMap.put(paramName, newParamDetails);
            }

            processedParams.put("params", processedParamsMap);
        }
        return processedParams;
    }
}
