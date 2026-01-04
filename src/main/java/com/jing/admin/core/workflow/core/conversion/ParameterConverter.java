package com.jing.admin.core.workflow.core.conversion;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.jing.admin.core.exception.BusinessException;
import com.jing.admin.core.workflow.core.context.WorkflowContext;
import com.jing.admin.core.workflow.exception.NodeExecutionResult;
import com.jing.admin.core.workflow.model.GlobalParams;
import com.jing.admin.core.workflow.model.NodeResult;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 参数转换器
 * 用于处理工作流中的参数引用，特别是当valueType为column类型时，从对应节点的输出数据中获取值
 */
public class ParameterConverter {

    /**
     * 正则表达式，用于匹配{{节点id.变量}}格式的参数引用
     */
    private static final Pattern PARAMETER_PATTERN = Pattern.compile("\\{\\{(\\w+)\\.(\\w+)\\}\\}");

    /**
     * 转换参数值
     *
     * @param value     原始参数值
     * @param valueType 参数类型
     * @param context   工作流执行上下文
     * @return 转换后的参数值
     */
    public Object convertParameter(Object value, String valueType, WorkflowContext context) {
        if (value == null) {
            return null;
        }

        // 如果不是column类型，直接返回原值
        if (!"column".equals(valueType)) {
            return value;
        }

        // 处理column类型的参数引用
        String stringValue = value.toString();
        Matcher matcher = PARAMETER_PATTERN.matcher(stringValue);
        Map<String, GlobalParams> globalParamsMap = context.getGlobalParams();
        if (matcher.matches()) {
            String nodeId = matcher.group(1);
            String variableName = matcher.group(2);
            if (globalParamsMap.containsKey(nodeId)) {
                GlobalParams globalParams = globalParamsMap.get(nodeId);
                return convertByDataType(globalParams.getParamValue(),globalParams.getValueType());
            }
            // 从上下文中获取节点执行结果
            NodeExecutionResult nodeResult = context.getVariable(nodeId);
            if (nodeResult != null) {
                // 获取节点的执行结果
                Object executeResult = nodeResult.getData();
                if (executeResult instanceof Map) {
                    Map<?, ?> resultMap = (Map<?, ?>) executeResult;
                    return resultMap.get(variableName);
                } else if (executeResult instanceof Object){
                    // 使用反射获取DTO对象的属性值
                    try {
                        java.lang.reflect.Field field = executeResult.getClass().getDeclaredField(variableName);
                        field.setAccessible(true); // 允许访问私有字段
                        return field.get(executeResult);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        // 如果反射获取失败，尝试使用其他方式获取属性
                         throw new BusinessException("未找到相关节点值");
                    }
                }
                // 如果executeResult不是Map，但变量名为""或为null，则返回整个executeResult
                if ("".equals(variableName) || variableName == null) {
                    return executeResult;
                }
            }
        }
        // 如果没有匹配的模式或找不到节点结果，返回原值
        throw new BusinessException("未找到相关节点值");
    }

    /**
     * 根据数据类型进行转换
     */
    private Object convertByDataType(Object value, String dataType) {
        if (value == null) {
            return null;
        }

        if (dataType == null) {
            return value; // 如果没有指定数据类型，返回原值
        }

        switch (dataType.toLowerCase()) {
            case "string":
                return value.toString();
            case "number":
                if (value instanceof Number) {
                    return value;
                }
                try {
                    String strValue = value.toString().trim();
                    if (strValue.contains(".")) {
                        return Double.parseDouble(strValue);
                    } else {
                        return Long.parseLong(strValue);
                    }
                } catch (NumberFormatException e) {
                    throw new BusinessException("无法将值转换为数字类型: " + value);
                }
            case "boolean":
                if (value instanceof Boolean) {
                    return value;
                }
                String boolStr = value.toString().trim().toLowerCase();
                return "true".equals(boolStr) || "1".equals(boolStr) || "yes".equals(boolStr) || "on".equals(boolStr);
            case "array":
                // 如果已经是数组或列表，直接返回
                if (value instanceof List || value.getClass().isArray()) {
                    return value;
                }
                // 否则将其作为字符串处理，可能需要进一步解析
                return JSONObject.parseObject((String) value,List.class);
            case "object":
                // 对象类型通常不需要转换，直接返回
                return JSONObject.parseObject((String) value,Map.class);
            case "file":
                // 文件类型，可能需要特殊处理，暂时返回原值
                return value;
            default:
                return value; // 默认返回原值
        }
    }
}