package com.jing.admin.core.workflow.node.impl;

import com.jing.admin.core.workflow.core.conversion.ParameterConverter;
import com.jing.admin.core.workflow.core.context.WorkflowContext;
import com.jing.admin.core.workflow.model.NodeData;
import com.jing.admin.core.workflow.model.NodeDefinition;
import com.jing.admin.core.workflow.model.NodeResult;
import com.jing.admin.core.workflow.exception.NodeExecutionResult;
import com.jing.admin.core.workflow.node.BaseNode;
import org.graalvm.polyglot.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JavaScript节点处理器
 * 执行JavaScript代码的节点
 */
public class JsNode extends BaseNode {

    public JsNode(ParameterConverter parameterConverter) {
        super(parameterConverter);
    }

    @Override
    public NodeExecutionResult execute(NodeDefinition nodeDefinition, WorkflowContext context) {
        long startTime = System.currentTimeMillis();
        Map<String, Object> inputData = new HashMap<>();
        try {
            // 获取节点数据
            NodeData nodeData = nodeDefinition.getData();
            if (nodeData == null || nodeData.getContent() == null) {
                throw new RuntimeException("JavaScript节点数据为空");
            }

            // 获取JavaScript代码
            String jsCode = nodeData.getContent().getCode();
            if (jsCode == null || jsCode.trim().isEmpty()) {
                throw new RuntimeException("JavaScript代码为空");
            }

            // 处理输入参数
            Map<String, Object> inParams = (Map<String, Object>) nodeData.getContent().getInParams();
            Map<String, Object> processedInputParams = processInputParams(inParams, context);
            inputData = processedInputParams;

            // 准备JavaScript执行上下文
            Map<String, Object> jsContext = new HashMap<>();
            jsContext.put("inputs", processedInputParams);
            jsContext.put("outputs", new HashMap<>());

            // 执行JavaScript代码
            Map<String, Object> result = executeJavaScript(jsCode, jsContext);

            // 获取输出结果
            Map<String, Object> outputs = (Map<String, Object>) result.get("outputs");

            long executionTime = System.currentTimeMillis() - startTime;
            NodeExecutionResult executionResult = NodeExecutionResult.success(outputs);
            executionResult.setExecutionTime(executionTime);
            executionResult.setInputData(inputData);
            return executionResult;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            NodeExecutionResult result = NodeExecutionResult.failure("执行失败: " + e.getMessage());
            result.setExecutionTime(executionTime);
            result.setInputData(inputData);
            return result;
        }
    }

    /**
     * 处理输入参数中的引用
     *
     * @param inParams 原始输入参数
     * @param context  工作流执行上下文
     * @return 处理后的输入参数
     */
    private Map<String, Object> processInputParams(Map<String, Object> inParams, WorkflowContext context) {
        Map<String, Object> processedParams = new HashMap<>();

        if (inParams != null) {
            for (Map.Entry<String, Object> entry : inParams.entrySet()) {
                String paramName = entry.getKey();
                Map<String, Object> paramDetails = (Map<String, Object>) entry.getValue();

                // 获取参数值和类型
                Object value = paramDetails.get("value");
                String valueType = (String) paramDetails.get("valueType");

                // 使用参数转换器处理参数值
                Object convertedValue = parameterConverter.convertParameter(value, valueType, context);

                processedParams.put(paramName, convertedValue);
            }
        }
        return processedParams;
    }

    /**
     * 执行JavaScript代码
     */
    private Map<String, Object> executeJavaScript(String jsCode, Map<String, Object> jsContext) {
        // 1. 确保 inputs 存在，防止 JS 中 context.inputs 为空报错
        if (!jsContext.containsKey("inputs")) {
            jsContext.put("inputs", new HashMap<>());
        }
        try (Context context = Context.newBuilder("js")
                .allowHostAccess(HostAccess.ALL) // 允许访问 Java Map 的方法
                .allowValueSharing(true)
                .option("js.ecmascript-version", "2022") // 建议指定较新的 ES 版本
                .build()) {

            // 2. 加载脚本 (只定义函数，不立即执行 main)
            // 使用 Source 构建可以提供更好的报错信息（文件名、行号）
            Source source = Source.newBuilder("js", jsCode, "userScript.js").build();
            context.eval(source);

            // 3. 获取 main 函数
            Value bindings = context.getBindings("js");
            Value mainFunction = bindings.getMember("main");

            if (mainFunction == null || !mainFunction.canExecute()) {
                throw new RuntimeException("JS脚本中未找到名为 'main' 的函数");
            }

            // 4. 执行函数并传入参数
            Value jsResult = mainFunction.execute(jsContext);
            // 5. 处理结果
            Object result = toJavaObject(jsResult);
            if (result instanceof Map<?, ?>) {
                return (Map<String, Object>) result;
            }
            Map m = new HashMap<>();
            m.put("outputs", result);
            m.put("inputs", jsContext.get("inputs"));

            return m;
        } catch (PolyglotException e) {
            // 捕获 JS 内部抛出的错误
            throw new RuntimeException("JS执行运行时错误: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("脚本加载或系统错误: " + e.getMessage(), e);
        }
    }

    /**
     * 将 GraalVM 的 Value 递归转换为纯 Java 对象 (Map, List, 或基本类型)
     */
    public static Object toJavaObject(Value value) {
        if (value == null || value.isNull()) {
            return null;
        }
        if (value.isHostObject()) {
            return deepCopyJavaObject(value.asHostObject());
        }

        // 1. 处理数组/列表 [ ... ]
        if (value.hasArrayElements()) {
            List<Object> list = new ArrayList<>();
            long size = value.getArraySize();
            for (long i = 0; i < size; i++) {
                // 递归转换列表中的每一个元素
                list.add(toJavaObject(value.getArrayElement(i)));
            }
            return list;
        }

        // 2. 处理对象/Map { ... }
        if (value.hasMembers()) {
            Map<String, Object> map = new HashMap<>();
            for (String key : value.getMemberKeys()) {
                Value member = value.getMember(key);
                // 如果这个成员是一个“可执行的函数”(比如 toString, 或 JS里的 function)，通常不是数据，跳过
                if (member.canExecute()) {
                    continue;
                }
                map.put(key, toJavaObject(value.getMember(key)));
            }
            return map;
        }

        // 3. 处理基本类型 (String, Int, Boolean 等)
        return value.as(Object.class);
    }

    /**
     * 辅助方法：深度拷贝 Java 对象
     * 负责把 HashMap/ArrayList 里的数据拆出来，放到新的容器里
     */
    private static Object deepCopyJavaObject(Object obj) {
        // A. 如果 Java 对象里居然藏着一个 Graal Value，递归回去处理
        if (obj instanceof Value) {
            return toJavaObject((Value) obj);
        }

        // B. 如果是 Map，创建一个新的 HashMap (断开引用)
        if (obj instanceof Map) {
            Map<?, ?> rawMap = (Map<?, ?>) obj;
            Map<String, Object> newMap = new HashMap<>();
            for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
                // key 转 String，value 递归拷贝
                newMap.put(String.valueOf(entry.getKey()), deepCopyJavaObject(entry.getValue()));
            }
            return newMap;
        }

        // C. 如果是 List，创建一个新的 ArrayList (断开引用)
        if (obj instanceof List) {
            List<?> rawList = (List<?>) obj;
            List<Object> newList = new ArrayList<>(rawList.size());
            for (Object item : rawList) {
                newList.add(deepCopyJavaObject(item));
            }
            return newList;
        }

        // D. 其他普通 Java 对象 (String, Integer, POJO)，直接返回
        return obj;
    }

    @Override
    public boolean supports(String nodeType) {
        return "javascript".equals(nodeType);
    }
}