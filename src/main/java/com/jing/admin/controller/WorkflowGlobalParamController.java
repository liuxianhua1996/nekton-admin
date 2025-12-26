package com.jing.admin.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.jing.admin.core.HttpResult;
import com.jing.admin.core.PageResult;
import com.jing.admin.model.api.WorkflowGlobalParamRequest;
import com.jing.admin.model.domain.WorkflowGlobalParam;
import com.jing.admin.service.WorkflowGlobalParamService;
import org.apache.catalina.util.StringUtil;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 工作流全局参数控制器
 * @author 
 * @date 
 */
@RestController
@RequestMapping("/workflow/global/param")
public class WorkflowGlobalParamController {
    
    @Autowired
    private WorkflowGlobalParamService workflowGlobalParamService;
    
    /**
     * 分页查询工作流全局参数
     * @param workflowId 工作流ID(可选)
     * @param paramType 参数用途类型(可选)
     * @param valueType 值的数据类型(可选)
     * @param paramKey 参数键(可选)
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    @GetMapping("/page")
    public HttpResult<PageResult<WorkflowGlobalParam>> getPage(
            @RequestParam(required = false) String workflowId,
            @RequestParam(required = false) String paramType,
            @RequestParam(required = false) String valueType,
            @RequestParam(required = false) String paramKey,
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "10") long pageSize) {
        return HttpResult.success(workflowGlobalParamService.getPage(workflowId, paramType, valueType, paramKey, pageNum, pageSize));
    }
    
    /**
     * 查询所有工作流全局参数（非分页）
     * @param workflowId 工作流ID(可选)
     * @param paramType 参数用途类型(可选)
     * @param valueType 值的数据类型(可选)
     * @param paramKey 参数键(可选)
     * @return 参数列表
     */
    @GetMapping
    public HttpResult<List<WorkflowGlobalParam>> getAll(
            @RequestParam String workflowId,
            @RequestParam(required = false) String paramType,
            @RequestParam(required = false) String valueType,
            @RequestParam(required = false) String paramKey) {
        List<WorkflowGlobalParam> params = workflowGlobalParamService.getAll(workflowId, paramType, valueType, paramKey);
        // Filter sensitive parameters
        filterSensitiveParams(params);
        return HttpResult.success(params);
    }
    /**
     * 保存工作流全局参数
     * @param request 参数信息请求
     * @return 保存结果
     */
    @PostMapping
    public HttpResult<String> save(@RequestBody WorkflowGlobalParamRequest request) {
        WorkflowGlobalParam param = new WorkflowGlobalParam();
        if (request.getId() != null) {
            param.setId(request.getId());
        }
        param.setParamKey(request.getParamKey());
        param.setParamValue(request.getParamValue());
        param.setParamType(request.getParamType());
        param.setValueType(request.getValueType());
        param.setWorkflowId(request.getWorkflowId());
        param.setDescription(request.getRemark());
        long currTime = System.currentTimeMillis();
        param.setCreateTime(currTime);
        param.setUpdateTime(currTime);
        workflowGlobalParamService.save(param);
        return HttpResult.success("保存成功");
    }
    
    /**
     * 批量保存或更新参数
     * @param request 参数列表请求
     * @return 操作结果
     */
    @PostMapping("/batch")
    public HttpResult<String> batchSaveOrUpdate(@RequestBody WorkflowGlobalParamRequest request) {
        workflowGlobalParamService.batchSaveOrUpdate(request.getParams());
        return HttpResult.success("批量操作成功");
    }
    
    /**
     * 更新工作流全局参数
     * @param request 参数信息请求
     * @return 更新结果
     */
    @PutMapping
    public HttpResult<String> update(@RequestBody WorkflowGlobalParamRequest request) {
        WorkflowGlobalParam param = new WorkflowGlobalParam();
        param.setId(request.getId());
        param.setParamKey(request.getParamKey());
        param.setParamValue(request.getParamValue());
        param.setParamType(request.getParamType());
        param.setValueType(request.getValueType());
        param.setWorkflowId(request.getWorkflowId());
        param.setDescription(request.getRemark());
        param.setUpdateTime(System.currentTimeMillis());
        workflowGlobalParamService.updateById(param);
        return HttpResult.success("更新成功");
    }
    
    /**
     * 删除工作流全局参数
     * @param id 参数ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public HttpResult<String> delete(@PathVariable String id) {
        workflowGlobalParamService.removeById(id);
        return HttpResult.success("删除成功");
    }
    
    /**
     * 根据参数键获取参数值
     * @param paramKey 参数键
     * @param workflowId 工作流ID(可选)
     * @return 参数值
     */
    @GetMapping("/value/{paramKey}")
    public HttpResult<String> getValue(@PathVariable String paramKey, 
                                      @RequestParam(required = false) String workflowId) {
        // Check if the parameter key is sensitive
        if (isSensitiveParamKey(paramKey)) {
            return HttpResult.success(""); // Return empty string for sensitive parameters
        }
        
        String value = workflowGlobalParamService.getParamValue(paramKey, workflowId);
        return HttpResult.success(value);
    }
    
    /**
     * 根据工作流ID删除所有参数
     * @param workflowId 工作流ID
     * @return 删除结果
     */
    @DeleteMapping("/workflow/{workflowId}")
    public HttpResult<String> deleteByWorkflowId(@PathVariable String workflowId) {
        workflowGlobalParamService.deleteByWorkflowId(workflowId);
        return HttpResult.success("删除成功");
    }
    
    /**
     * 敏感参数键黑名单
     */
    private static final List<String> SENSITIVE_PARAM_KEYS = List.of(
        "password","pwd"
    );
    
    /**
     * 过滤敏感参数，不返回敏感参数的值
     * @param params 参数列表
     */
    private void filterSensitiveParams(List<WorkflowGlobalParam> params) {
        if (params == null) {
            return;
        }
        
        for (WorkflowGlobalParam param : params) {
            if (param.getParamValue() != null && !"".equals(param.getParamValue()) && "json".equals(param.getValueType())) {
                String lowerKey = param.getParamKey().toLowerCase();
                JSONObject jsonObject = JSONObject.parse(param.getParamValue());
                for(String key : jsonObject.keySet()){
                    for (String sensitiveKey : SENSITIVE_PARAM_KEYS) {
                        if (lowerKey.contains(key)) {
                            // 将敏感参数的值设置为空字符串
                            jsonObject.put(key,"");
                            break;
                        }
                    }
                }

            }
        }
    }
    
    /**
     * 检查参数键是否为敏感参数
     * @param paramKey 参数键
     * @return 是否为敏感参数
     */
    private boolean isSensitiveParamKey(String paramKey) {
        if (paramKey == null) {
            return false;
        }
        
        String lowerKey = paramKey.toLowerCase();
        for (String sensitiveKey : SENSITIVE_PARAM_KEYS) {
            if (lowerKey.contains(sensitiveKey)) {
                return true;
            }
        }
        return false;
    }
}