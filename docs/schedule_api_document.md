# 调度模块API文档

## 概述

调度模块提供工作流调度功能，包括调度任务的创建、管理、执行和日志记录。

## 通用说明

- 所有API接口都返回JSON格式数据
- 日期时间字段使用毫秒时间戳格式
- 所有接口均需提供有效的认证信息

## 调度任务接口

### 1. 创建调度任务

- **接口地址**: `POST /api/schedule/job`
- **功能描述**: 创建新的调度任务
- **请求参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| name | String | 是 | 调度名称 |
| workflowId | String | 是 | 工作流ID |
| triggerType | String | 是 | 触发方式：cron-定时，webhook-Webhook，mqtt-MQTT |
| triggerConfig | String | 否 | 触发配置(json格式，如cron表达式、webhook地址、MQTT配置等) |
| status | String | 否 | 状态：DISABLED-停用，ENABLED-启用，默认为DISABLED |
| description | String | 否 | 描述 |
| tenantId | String | 否 | 租户ID |

- **请求示例**:
```json
{
  "name": "每日数据同步任务",
  "workflowId": "workflow_001",
  "triggerType": "cron",
  "triggerConfig": "0 0 2 * * ?",
  "status": "ENABLED",
  "description": "每日凌晨2点执行数据同步"
}
```

- **响应参数**:

| 参数名 | 类型 | 描述 |
|--------|------|------|
| code | String | 响应状态码 |
| message | String | 响应消息 |
| data | Object | 响应数据，包含以下字段： |
| data.id | String | 调度任务ID |
| data.name | String | 调度名称 |
| data.workflowId | String | 工作流ID |
| data.triggerType | String | 触发方式 |
| data.triggerConfig | String | 触发配置 |
| data.status | String | 状态 |
| data.description | String | 描述 |
| data.tenantId | String | 租户ID |
| data.createTime | Long | 创建时间 |
| data.updateTime | Long | 更新时间 |
| data.createUserId | String | 创建用户ID |
| data.updateUserId | String | 更新用户ID |

- **响应示例**:
```json
{
  "id": "job_001",
  "name": "每日数据同步任务",
  "workflowId": "workflow_001",
  "triggerType": "cron",
  "triggerConfig": "0 0 2 * * ?",
  "status": "ENABLED",
  "description": "每日凌晨2点执行数据同步",
  "tenantId": "tenant_001",
  "createTime": 1696032000000,
  "updateTime": 1696032000000,
  "createUserId": "user_001",
  "updateUserId": "user_001"
}
```

### 2. 更新调度任务

- **接口地址**: `PUT /api/schedule/job/{id}`
- **功能描述**: 更新调度任务信息
- **路径参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | String | 是 | 调度任务ID |

- **请求参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| name | String | 是 | 调度名称 |
| workflowId | String | 是 | 工作流ID |
| triggerType | String | 是 | 触发方式：cron-定时，webhook-Webhook，mqtt-MQTT |
| triggerConfig | String | 否 | 触发配置(json格式) |
| status | String | 否 | 状态：DISABLED-停用，ENABLED-启用 |
| description | String | 否 | 描述 |
| tenantId | String | 否 | 租户ID |

- **请求示例**:
```json
{
  "name": "更新后的每日数据同步任务",
  "workflowId": "workflow_001",
  "triggerType": "cron",
  "triggerConfig": "0 30 2 * * ?",
  "status": "ENABLED",
  "description": "每日凌晨2点30分执行数据同步"
}
```

- **响应参数**:

| 参数名 | 类型 | 描述 |
|--------|------|------|
| code | String | 响应状态码 |
| message | String | 响应消息 |
| data | Object | 响应数据，包含以下字段： |
| data.id | String | 调度任务ID |
| data.name | String | 调度名称 |
| data.workflowId | String | 工作流ID |
| data.triggerType | String | 触发方式 |
| data.triggerConfig | String | 触发配置 |
| data.status | String | 状态 |
| data.description | String | 描述 |
| data.tenantId | String | 租户ID |
| data.createTime | Long | 创建时间 |
| data.updateTime | Long | 更新时间 |
| data.createUserId | String | 创建用户ID |
| data.updateUserId | String | 更新用户ID |

### 3. 删除调度任务

- **接口地址**: `DELETE /api/schedule/job/{id}`
- **功能描述**: 删除指定调度任务
- **路径参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | String | 是 | 调度任务ID |

- **响应参数**:

| 参数名 | 类型 | 描述 |
|--------|------|------|
| code | String | 响应状态码 |
| message | String | 响应消息 |
| data | Boolean | 删除结果，true表示删除成功，false表示删除失败 |

### 4. 根据ID获取调度任务

- **接口地址**: `GET /api/schedule/job/{id}`
- **功能描述**: 获取指定调度任务的详细信息
- **路径参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | String | 是 | 调度任务ID |

- **响应参数**: 同创建调度任务接口

### 5. 获取调度任务列表

- **接口地址**: `GET /api/schedule/job`
- **功能描述**: 获取所有调度任务列表
- **响应参数**:

| 参数名 | 类型 | 描述 |
|--------|------|------|
| code | String | 响应状态码 |
| message | String | 响应消息 |
| data | Array | 调度任务列表，数组元素结构同创建调度任务接口 |

### 6. 获取调度任务分页列表

- **接口地址**: `GET /api/schedule/job/page`
- **功能描述**: 获取调度任务分页列表，支持按条件查询
- **请求参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| current | Long | 否 | 当前页码，默认为1 |
| size | Long | 否 | 每页显示条数，默认为10 |
| name | String | 否 | 调度名称 |
| workflowId | String | 否 | 工作流ID |
| triggerType | String | 否 | 触发方式 |
| status | String | 否 | 状态 |

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

### 6. 停用调度任务

- **接口地址**: `PUT /api/schedule/job/{id}/disable`
- **功能描述**: 停用指定调度任务
- **路径参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | String | 是 | 调度任务ID |

- **响应参数**:

| 参数名 | 类型 | 描述 |
|--------|------|------|
| code | String | 响应状态码 |
| message | String | 响应消息 |
| data | Boolean | 操作结果，true表示停用成功，false表示停用失败 |

### 7. 启用调度任务

- **接口地址**: `PUT /api/schedule/job/{id}/enable`
- **功能描述**: 启用指定调度任务
- **路径参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | String | 是 | 调度任务ID |

- **响应参数**:

| 参数名 | 类型 | 描述 |
|--------|------|------|
| code | String | 响应状态码 |
| message | String | 响应消息 |
| data | Boolean | 操作结果，true表示启用成功，false表示启用失败 |

### 8. 立即执行调度任务

- **接口地址**: `PUT /api/schedule/job/{id}/execute`
- **功能描述**: 立即执行指定调度任务
- **路径参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | String | 是 | 调度任务ID |

- **响应参数**:

| 参数名 | 类型 | 描述 |
|--------|------|------|
| code | String | 响应状态码 |
| message | String | 响应消息 |
| data | Boolean | 操作结果，true表示执行成功，false表示执行失败 |

## 状态值说明

### 调度任务状态
- `ENABLED`: 启用
- `DISABLED`: 停用

### 触发方式
- `cron`: 定时触发
- `webhook`: Webhook触发
- `mqtt`: MQTT触发