package com.jing.admin.model.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 工作流节点执行日志实体类
 * @author 
 * @date 
 */
@Data
@TableName("tb_workflow_node_log")
public class WorkflowNodeLog extends Base {
    /**
     * 工作流实例ID
     */
    private String workflowInstanceId;
    
    /**
     * 工作流ID
     */
    private String workflowId;
    
    /**
     * 节点ID
     */
    private String nodeId;
    
    /**
     * 节点名称
     */
    private String nodeName;
    
    /**
     * 节点类型
     */
    private String nodeType;
    
    /**
     * 执行状态：SUCCESS-成功，FAILED-失败，RUNNING-执行中
     */
    private String status;
    
    /**
     * 节点输入数据
     */
    private String inputData;
    
    /**
     * 节点输出数据
     */
    private String outputData;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 执行耗时(毫秒)
     */
    private Long executionTime;
    
    /**
     * 节点开始执行时间
     */
    private Long startTime;
    
    /**
     * 节点结束执行时间
     */
    private Long endTime;
    
    /**
     * 执行顺序
     */
    private Integer sortOrder;
}