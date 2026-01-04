package com.jing.admin.core.workflow.node.impl;

import com.jing.admin.core.workflow.core.conversion.ParameterConverter;
import com.jing.admin.core.workflow.core.context.WorkflowContext;
import com.jing.admin.core.workflow.model.NodeData;
import com.jing.admin.core.workflow.model.NodeDefinition;
import com.jing.admin.core.workflow.exception.NodeExecutionResult;
import com.jing.admin.core.workflow.node.BaseNode;

import java.util.*;

/**
 * IF条件判断节点处理器
 * 根据条件判断执行不同的分支
 */
public class IfNode extends BaseNode {

    public IfNode(ParameterConverter parameterConverter) {
        super(parameterConverter);
    }

    @Override
    public NodeExecutionResult execute(NodeDefinition nodeDefinition, WorkflowContext context) {
        long startTime = System.currentTimeMillis();
        try {
            // 获取节点数据
            NodeData nodeData = nodeDefinition.getData();
            if (nodeData == null || nodeData.getContent() == null) {
                throw new RuntimeException("IF节点数据为空");
            }

            // 获取条件列表
            List<Map<String, Object>> conditions =  nodeData.getContent().getConditions();
            if (conditions == null || conditions.isEmpty()) {
                throw new RuntimeException("IF节点条件为空");
            }

            // 处理并评估每个条件
            List<Map <String,String>> matchedConditionIds = new ArrayList<>();
            for (Map<String, Object> condition : conditions) {
                String conditionId = (String) condition.get("id");
                String conditionName = (String) condition.get("name");
                String logicOperator = (String) condition.get("logicOperator");
                List<Map<String, Object>> subConditions = (List<Map<String, Object>>) condition.get("subConditions");

                if (evaluateConditionGroup(subConditions, logicOperator, context)) {
                    Map <String,String> matchedCondition = new HashMap<>(2);
                    matchedCondition.put("conditionId", conditionId);
                    matchedCondition.put("conditionName", conditionName);
                    matchedConditionIds.add(matchedCondition);
                }
            }

            // 将匹配的条件ID返回，用于确定下一步执行哪个分支
            Map<String, Object> outputData = new HashMap<>();
            outputData.put("matchedConditions", matchedConditionIds);
            outputData.put("allConditions", conditions);

            long executionTime = System.currentTimeMillis() - startTime;
            NodeExecutionResult result = NodeExecutionResult.success(outputData);
            result.setExecutionTime(executionTime);
            result.setData(outputData);
            
            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            NodeExecutionResult result = NodeExecutionResult.failure("IF节点执行失败: " + e.getMessage());
            result.setExecutionTime(executionTime);
            return result;
        }
    }

    /**
     * 评估条件组
     * 
     * @param subConditions 子条件列表
     * @param logicOperator 逻辑操作符 (AND/OR)
     * @param context 工作流执行上下文
     * @return 条件组是否匹配
     */
    private boolean evaluateConditionGroup(List<Map<String, Object>> subConditions, String logicOperator, WorkflowContext context) {
        if (subConditions == null || subConditions.isEmpty()) {
            return true; // 没有条件时默认为真
        }

        boolean result = "AND".equals(logicOperator); // AND操作符默认为true，OR操作符默认为false

        for (Map<String, Object> subCondition : subConditions) {
            String field = (String) subCondition.get("field");
            String operator = (String) subCondition.get("operator");
            Object value = subCondition.get("value");
            String valueType = (String) subCondition.get("valueType");

            // 解析字段引用（如 {{f9fe16cf8dc14c1f8f50aa857eb47986.appType}}）
            Object actualFieldValue = this.parameterConverter.convertParameter(field,"column",context);
            subCondition.put("field",actualFieldValue);
            // 根据valueType处理目标值
            Object actualTargetValue = this.parameterConverter.convertParameter(value,valueType,context);;
            subCondition.put("value",value);
            boolean conditionMatch = evaluateSingleCondition(actualFieldValue, operator, actualTargetValue);
            if ("AND".equals(logicOperator)) {
                result = result && conditionMatch;
                if (!result) { // AND操作符下，一旦有false就返回false
                    break;
                }
            } else if ("OR".equals(logicOperator)) {
                result = result || conditionMatch;
                if (result) { // OR操作符下，一旦有true就返回true
                    break;
                }
            }
        }

        return result;
    }

    /**
     * 评估单个条件
     * 
     * @param actualValue 实际值
     * @param operator 操作符
     * @param expectedValue 期望值
     * @return 条件是否匹配
     */
    private boolean evaluateSingleCondition(Object actualValue, String operator, Object expectedValue) {
        if (actualValue == null && expectedValue == null) {
            return true;
        }
        if (actualValue == null || expectedValue == null) {
            return false;
        }

        switch (operator) {
            case "eq":
                return Objects.equals(String.valueOf(actualValue), String.valueOf(expectedValue));
            case "neq":
                return !Objects.equals(String.valueOf(actualValue), String.valueOf(expectedValue));
            case "gt":
                return compareNumbers(actualValue, expectedValue) > 0;
            case "gte":
                return compareNumbers(actualValue, expectedValue) >= 0;
            case "lt":
                return compareNumbers(actualValue, expectedValue) < 0;
            case "lte":
                return compareNumbers(actualValue, expectedValue) <= 0;
            case "contains":
                return String.valueOf(actualValue).contains(String.valueOf(expectedValue));
            case "starts_with":
                return String.valueOf(actualValue).startsWith(String.valueOf(expectedValue));
            case "ends_with":
                return String.valueOf(actualValue).endsWith(String.valueOf(expectedValue));
            case "is_null":
                return actualValue == null || (actualValue instanceof String && ((String) actualValue).trim().isEmpty());
            case "is_not_null":
                return actualValue != null && !(actualValue instanceof String && ((String) actualValue).trim().isEmpty());
            default:
                return Objects.equals(String.valueOf(actualValue), String.valueOf(expectedValue));
        }
    }

    /**
     * 比较数值
     * 
     * @param val1 第一个值
     * @param val2 第二个值
     * @return 比较结果
     */
    private int compareNumbers(Object val1, Object val2) {
        try {
            double d1 = Double.parseDouble(String.valueOf(val1));
            double d2 = Double.parseDouble(String.valueOf(val2));
            return Double.compare(d1, d2);
        } catch (NumberFormatException e) {
            // 如果不能转换为数字，则按字符串比较
            return String.valueOf(val1).compareTo(String.valueOf(val2));
        }
    }

    @Override
    public boolean supports(String nodeType) {
        return "verify".equals(nodeType);
    }
}