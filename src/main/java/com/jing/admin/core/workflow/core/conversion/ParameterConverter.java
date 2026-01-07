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
     * 正则表达式，用于匹配{{节点id.变量.子变量...}}格式的参数引用
     */
    private static final Pattern PARAMETER_PATTERN = Pattern.compile("\\{\\{([\\w\\.]+)\\}\\}");

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
            String fullVariablePath = matcher.group(1);
            String[] pathParts = fullVariablePath.split("\\.");
            if (pathParts.length < 1) {
                throw new BusinessException("参数引用格式错误");
            }

            String nodeId = pathParts[0];
            String[] variablePath = new String[pathParts.length - 1];
            System.arraycopy(pathParts, 1, variablePath, 0, pathParts.length - 1);

            if (globalParamsMap.containsKey(nodeId)) {
                GlobalParams globalParams = globalParamsMap.get(nodeId);
                // 全局参数只有一层，即 id.value，不需要多层访问
                return convertByDataType(globalParams.getParamValue(), globalParams.getValueType());
            }

            // 从上下文中获取节点执行结果
            NodeExecutionResult nodeResult = context.getVariable(nodeId);
            if (nodeResult != null) {
                // 获取节点的执行结果
                Object executeResult = nodeResult.getData();
                return getValueByPath(executeResult, variablePath);
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
                return JSONObject.parseObject((String) value, List.class);
            case "object":
                // 对象类型通常不需要转换，直接返回
                return JSONObject.parseObject((String) value, Map.class);
            case "file":
                // 文件类型，可能需要特殊处理，暂时返回原值
                return value;
            default:
                return value; // 默认返回原值
        }
    }

    /**
     * 根据路径获取嵌套值
     *
     * @param obj  起始对象
     * @param path 路径数组，如 ["user", "name"] 表示访问 obj.user.name
     * @return 最终值
     */
    private Object getValueByPath(Object obj, String[] path) {
        Object current = obj;
        for (String pathSegment : path) {
            if (current == null) {
                throw new BusinessException("路径访问中遇到null值: " + String.join(".", path));
            }

            if (current instanceof Map) {
                // 如果是Map类型，通过key获取值
                Map<?, ?> map = (Map<?, ?>) current;
                current = map.get(pathSegment);
            } else {
                // 如果是对象类型，通过反射获取属性值
                try {
                    java.lang.reflect.Method getterMethod = findGetterMethod(current.getClass(), pathSegment);
                    if (getterMethod != null) {
                        current = getterMethod.invoke(current);
                    } else {
                        java.lang.reflect.Field field = current.getClass().getDeclaredField(pathSegment);
                        field.setAccessible(true);
                        current = field.get(current);
                    }
                } catch (Exception e) {
                    throw new BusinessException("无法访问路径段: " + pathSegment + ", 错误: " + e.getMessage());
                }
            }
        }
        return current;
    }

    /**
     * 查找属性的getter方法
     *
     * @param clazz     类型
     * @param fieldName 属性名
     * @return getter方法，如果不存在返回null
     */
    private java.lang.reflect.Method findGetterMethod(Class<?> clazz, String fieldName) {
        String capitalizedFieldName = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        try {
            // 尝试标准getter方法
            java.lang.reflect.Method method = clazz.getMethod("get" + capitalizedFieldName);
            return method;
        } catch (NoSuchMethodException e) {
            try {
                // 尝试is方法（对于boolean类型）
                java.lang.reflect.Method method = clazz.getMethod("is" + capitalizedFieldName);
                return method;
            } catch (NoSuchMethodException ex) {
                return null;
            }
        }
    }
}