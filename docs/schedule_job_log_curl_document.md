# 调度任务执行记录API cURL文档

## 概述

调度任务执行记录模块提供工作流调度执行记录的创建、管理、查询功能。

## 通用说明

- 所有API接口都返回JSON格式数据
- 日期时间字段使用毫秒时间戳格式
- 所有接口均需提供有效的认证信息
- 以下示例中的 `YOUR_TOKEN` 需要替换为实际的认证令牌

## 调度任务执行记录接口

### 1. 创建调度任务执行记录

- **接口地址**: `POST /api/schedule/job/log`
- **功能描述**: 创建新的调度任务执行记录
- **请求参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| jobId | String | 是 | 调度任务ID |
| workflowId | String | 是 | 工作流ID |
| workflowInstanceId | String | 否 | 工作流实例ID |
| triggerType | String | 否 | 触发方式：cron-定时，webhook-Webhook，mqtt-MQTT |
| status | String | 否 | 执行状态：SUCCESS-成功，FAILED-失败 |
| result | String | 否 | 执行结果 |
| startTime | Long | 否 | 开始执行时间 |
| endTime | Long | 否 | 结束执行时间 |
| executionTime | Long | 否 | 执行耗时(毫秒) |
| errorMessage | String | 否 | 错误信息 |
| tenantId | String | 否 | 租户ID |

- **cURL示例**:

```bash
curl -X POST "http://localhost:8080/api/schedule/job/log" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "jobId": "job_001",
    "workflowId": "workflow_001",
    "workflowInstanceId": "instance_001",
    "triggerType": "cron",
    "status": "SUCCESS",
    "result": "执行成功",
    "startTime": 1696032000000,
    "endTime": 1696032060000,
    "executionTime": 60000,
    "errorMessage": null,
    "tenantId": "tenant_001"
  }'
```

- **响应参数**:

| 参数名 | 类型 | 描述 |
|--------|------|------|
| code | String | 响应状态码 |
| message | String | 响应消息 |
| data | Object | 响应数据，包含以下字段： |
| data.id | String | 记录ID |
| data.jobId | String | 调度任务ID |
| data.workflowId | String | 工作流ID |
| data.workflowInstanceId | String | 工作流实例ID |
| data.triggerType | String | 触发方式 |
| data.status | String | 执行状态 |
| data.result | String | 执行结果 |
| data.startTime | Long | 开始执行时间 |
| data.endTime | Long | 结束执行时间 |
| data.executionTime | Long | 执行耗时(毫秒) |
| data.errorMessage | String | 错误信息 |
| data.tenantId | String | 租户ID |
| data.createTime | Long | 创建时间 |
| data.updateTime | Long | 更新时间 |
| data.createUserId | String | 创建用户ID |
| data.updateUserId | String | 更新用户ID |
| data.createUserName | String | 创建用户名称 |
| data.updateUserName | String | 更新用户名称 |

- **响应示例**:
```json
{
  "id": "log_001",
  "jobId": "job_001",
  "workflowId": "workflow_001",
  "workflowInstanceId": "instance_001",
  "triggerType": "cron",
  "status": "SUCCESS",
  "result": "执行成功",
  "startTime": 1696032000000,
  "endTime": 1696032060000,
  "executionTime": 60000,
  "errorMessage": null,
  "tenantId": "tenant_001",
  "createTime": 1696032000000,
  "updateTime": 1696032000000,
  "createUserId": "user_001",
  "updateUserId": "user_001",
  "createUserName": "admin",
  "updateUserName": "admin"
}
```

### 2. 更新调度任务执行记录

- **接口地址**: `PUT /api/schedule/job/log/{id}`
- **功能描述**: 更新调度任务执行记录信息
- **路径参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | String | 是 | 记录ID |

- **请求参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| jobId | String | 是 | 调度任务ID |
| workflowId | String | 是 | 工作流ID |
| workflowInstanceId | String | 否 | 工作流实例ID |
| triggerType | String | 否 | 触发方式：cron-定时，webhook-Webhook，mqtt-MQTT |
| status | String | 否 | 执行状态：SUCCESS-成功，FAILED-失败 |
| result | String | 否 | 执行结果 |
| startTime | Long | 否 | 开始执行时间 |
| endTime | Long | 否 | 结束执行时间 |
| executionTime | Long | 否 | 执行耗时(毫秒) |
| errorMessage | String | 否 | 错误信息 |
| tenantId | String | 否 | 租户ID |

- **cURL示例**:

```bash
curl -X PUT "http://localhost:8080/api/schedule/job/log/log_001" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "jobId": "job_001",
    "workflowId": "workflow_001",
    "workflowInstanceId": "instance_001",
    "triggerType": "cron",
    "status": "FAILED",
    "result": "执行失败",
    "startTime": 1696032000000,
    "endTime": 1696032060000,
    "executionTime": 60000,
    "errorMessage": "连接超时",
    "tenantId": "tenant_001"
  }'
```

- **响应参数**:

| 参数名 | 类型 | 描述 |
|--------|------|------|
| code | String | 响应状态码 |
| message | String | 响应消息 |
| data | Object | 响应数据，包含以下字段： |
| data.id | String | 记录ID |
| data.jobId | String | 调度任务ID |
| data.workflowId | String | 工作流ID |
| data.workflowInstanceId | String | 工作流实例ID |
| data.triggerType | String | 触发方式 |
| data.status | String | 执行状态 |
| data.result | String | 执行结果 |
| data.startTime | Long | 开始执行时间 |
| data.endTime | Long | 结束执行时间 |
| data.executionTime | Long | 执行耗时(毫秒) |
| data.errorMessage | String | 错误信息 |
| data.tenantId | String | 租户ID |
| data.createTime | Long | 创建时间 |
| data.updateTime | Long | 更新时间 |
| data.createUserId | String | 创建用户ID |
| data.updateUserId | String | 更新用户ID |
| data.createUserName | String | 创建用户名称 |
| data.updateUserName | String | 更新用户名称 |

- **响应示例**:
```json
{
  "id": "log_001",
  "jobId": "job_001",
  "workflowId": "workflow_001",
  "workflowInstanceId": "instance_001",
  "triggerType": "cron",
  "status": "FAILED",
  "result": "执行失败",
  "startTime": 1696032000000,
  "endTime": 1696032060000,
  "executionTime": 60000,
  "errorMessage": "连接超时",
  "tenantId": "tenant_001",
  "createTime": 1696032000000,
  "updateTime": 1696032060000,
  "createUserId": "user_001",
  "updateUserId": "user_001",
  "createUserName": "admin",
  "updateUserName": "admin"
}
```

### 3. 删除调度任务执行记录

- **接口地址**: `DELETE /api/schedule/job/log/{id}`
- **功能描述**: 删除指定调度任务执行记录
- **路径参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | String | 是 | 记录ID |

- **cURL示例**:

```bash
curl -X DELETE "http://localhost:8080/api/schedule/job/log/log_001" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

- **响应参数**:

| 参数名 | 类型 | 描述 |
|--------|------|------|
| code | String | 响应状态码 |
| message | String | 响应消息 |
| data | Boolean | 删除结果，true表示删除成功，false表示删除失败 |

- **响应示例**:
```json
true
```

### 4. 根据ID获取调度任务执行记录

- **接口地址**: `GET /api/schedule/job/log/{id}`
- **功能描述**: 获取指定调度任务执行记录的详细信息
- **路径参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | String | 是 | 记录ID |

- **cURL示例**:

```bash
curl -X GET "http://localhost:8080/api/schedule/job/log/log_001" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

- **响应参数**:

| 参数名 | 类型 | 描述 |
|--------|------|------|
| code | String | 响应状态码 |
| message | String | 响应消息 |
| data | Object | 响应数据，包含以下字段： |
| data.id | String | 记录ID |
| data.jobId | String | 调度任务ID |
| data.workflowId | String | 工作流ID |
| data.workflowInstanceId | String | 工作流实例ID |
| data.triggerType | String | 触发方式 |
| data.status | String | 执行状态 |
| data.result | String | 执行结果 |
| data.startTime | Long | 开始执行时间 |
| data.endTime | Long | 结束执行时间 |
| data.executionTime | Long | 执行耗时(毫秒) |
| data.errorMessage | String | 错误信息 |
| data.tenantId | String | 租户ID |
| data.createTime | Long | 创建时间 |
| data.updateTime | Long | 更新时间 |
| data.createUserId | String | 创建用户ID |
| data.updateUserId | String | 更新用户ID |
| data.createUserName | String | 创建用户名称 |
| data.updateUserName | String | 更新用户名称 |

- **响应示例**:
```json
{
  "id": "log_001",
  "jobId": "job_001",
  "workflowId": "workflow_001",
  "workflowInstanceId": "instance_001",
  "triggerType": "cron",
  "status": "SUCCESS",
  "result": "执行成功",
  "startTime": 1696032000000,
  "endTime": 1696032060000,
  "executionTime": 60000,
  "errorMessage": null,
  "tenantId": "tenant_001",
  "createTime": 1696032000000,
  "updateTime": 1696032000000,
  "createUserId": "user_001",
  "updateUserId": "user_001",
  "createUserName": "admin",
  "updateUserName": "admin"
}
```

### 5. 获取调度任务执行记录列表

- **接口地址**: `GET /api/schedule/job/log`
- **功能描述**: 获取所有调度任务执行记录列表
- **cURL示例**:

```bash
curl -X GET "http://localhost:8080/api/schedule/job/log" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

- **响应参数**:

| 参数名 | 类型 | 描述 |
|--------|------|------|
| code | String | 响应状态码 |
| message | String | 响应消息 |
| data | Array | 调度任务执行记录列表，数组元素结构同创建调度任务执行记录接口 |

- **响应示例**:
```json
[
  {
    "id": "log_001",
    "jobId": "job_001",
    "workflowId": "workflow_001",
    "workflowInstanceId": "instance_001",
    "triggerType": "cron",
    "status": "SUCCESS",
    "result": "执行成功",
    "startTime": 1696032000000,
    "endTime": 1696032060000,
    "executionTime": 60000,
    "errorMessage": null,
    "tenantId": "tenant_001",
    "createTime": 1696032000000,
    "updateTime": 1696032000000,
    "createUserId": "user_001",
    "updateUserId": "user_001",
    "createUserName": "admin",
    "updateUserName": "admin"
  },
  {
    "id": "log_002",
    "jobId": "job_002",
    "workflowId": "workflow_002",
    "workflowInstanceId": "instance_002",
    "triggerType": "webhook",
    "status": "FAILED",
    "result": "执行失败",
    "startTime": 1696032100000,
    "endTime": 1696032160000,
    "executionTime": 60000,
    "errorMessage": "连接超时",
    "tenantId": "tenant_001",
    "createTime": 1696032100000,
    "updateTime": 1696032100000,
    "createUserId": "user_001",
    "updateUserId": "user_001",
    "createUserName": "admin",
    "updateUserName": "admin"
  }
]
```

### 6. 根据任务ID获取执行记录分页列表

- **接口地址**: `GET /api/schedule/job/log/job/{jobId}/page`
- **功能描述**: 根据任务ID获取对应的执行记录分页列表，支持按条件查询
- **路径参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| jobId | String | 是 | 调度任务ID |

- **请求参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| current | Long | 否 | 当前页码，默认为1 |
| size | Long | 否 | 每页显示条数，默认为10 |
| workflowId | String | 否 | 工作流ID |
| status | String | 否 | 状态 |

- **cURL示例**:

```bash
# 获取任务job_001的第一页执行记录，每页10条
curl -X GET "http://localhost:8080/api/schedule/job/log/job/job_001/page?current=1&size=10" \
  -H "Authorization: Bearer YOUR_TOKEN"

# 获取任务job_001的执行记录，按工作流ID过滤并分页
curl -X GET "http://localhost:8080/api/schedule/job/log/job/job_001/page?current=1&size=10&workflowId=workflow_001" \
  -H "Authorization: Bearer YOUR_TOKEN"

# 获取任务job_001的成功执行记录并分页
curl -X GET "http://localhost:8080/api/schedule/job/log/job/job_001/page?current=1&size=10&status=SUCCESS" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

- **响应参数**:

| 参数名 | 类型 | 描述 |
|--------|------|------|
| code | String | 响应状态码 |
| message | String | 响应消息 |
| data | Object | 分页结果数据，包含以下字段： |
| data.records | Array | 当前页数据列表 |
| data.total | Long | 总记录数 |
| data.size | Long | 每页显示条数 |
| data.current | Long | 当前页码 |

- **响应示例**:
```json
{
  "records": [
    {
      "id": "log_001",
      "jobId": "job_001",
      "workflowId": "workflow_001",
      "workflowInstanceId": "instance_001",
      "triggerType": "cron",
      "status": "SUCCESS",
      "result": "执行成功",
      "startTime": 1696032000000,
      "endTime": 1696032060000,
      "executionTime": 60000,
      "errorMessage": null,
      "tenantId": "tenant_001",
      "createTime": 1696032000000,
      "updateTime": 1696032000000,
      "createUserId": "user_001",
      "updateUserId": "user_001",
      "createUserName": "admin",
      "updateUserName": "admin"
    }
  ],
  "total": 100,
  "size": 10,
  "current": 1
}
```

## 状态值说明

### 执行记录状态
- `SUCCESS`: 成功
- `FAILED`: 失败
- `RUNNING`: 运行中

### 触发方式
- `cron`: 定时触发
- `webhook`: Webhook触发
- `mqtt`: MQTT触发
- `MANUAL`: 手动触发
- `SCHEDULED`: 调度触发