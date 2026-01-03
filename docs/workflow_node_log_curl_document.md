# 工作流节点执行日志API cURL文档

## 接口概述
工作流节点执行日志接口用于查询工作流节点执行过程中的日志信息，支持根据日志ID获取节点执行日志和分页查询节点执行日志等功能。

## 基础路径
```
/api/workflow/node/log
```

## 接口列表

### 1. 根据日志ID获取节点执行日志
- **请求方式**: GET
- **请求路径**: `/api/workflow/node/log/{id}`
- **描述**: 根据日志ID获取指定的节点执行日志详情

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | String | 是 | 日志ID |

#### cURL请求示例
```bash
curl -X GET "http://localhost:8080/api/workflow/node/log/log123" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### 响应示例
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "log123",
    "workflowInstanceId": "instance123",
    "workflowId": "workflow123",
    "nodeId": "node123",
    "nodeName": "数据处理节点",
    "nodeType": "PROCESS",
    "status": "SUCCESS",
    "inputData": "{\"input\": \"value\"}",
    "outputData": "{\"output\": \"result\"}",
    "errorMessage": null,
    "executionTime": 1500,
    "startTime": 1640995200000,
    "endTime": 1640995201500,
    "sortOrder": 1,
    "tenantId": "tenant123",
    "createTime": 1640995200000,
    "updateTime": 1640995201500,
    "createUserId": "user123",
    "updateUserId": "user123",
    "createUserName": "张三",
    "updateUserName": "李四"
  }
}
```

## 响应格式

### 成功响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 分页查询响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [],
    "total": 0,
    "current": 1,
    "size": 10
  }
}
```

## 状态说明

### status (执行状态)
- `SUCCESS`: 成功
- `FAILED`: 失败
- `RUNNING`: 执行中