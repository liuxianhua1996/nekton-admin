package com.jing.admin.core.workflow.conversion;

import com.jing.admin.core.exception.BusinessException;
import com.jing.admin.core.workflow.context.WorkflowContext;
import com.jing.admin.core.workflow.definition.NodeResult;
import org.springframework.stereotype.Component;

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

        if (matcher.matches()) {
            String nodeId = matcher.group(1);
            String variableName = matcher.group(2);

            // 从上下文中获取节点执行结果
            NodeResult nodeResult = context.getNodeResult(nodeId);
            if (nodeResult != null) {
                // 获取节点的执行结果
                Object executeResult = nodeResult.getExecuteResult();
                if (executeResult instanceof Map) {
                    Map<?, ?> resultMap = (Map<?, ?>) executeResult;
                    return resultMap.get(variableName);
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
}