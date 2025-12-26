# 工作流全局参数接口文档

## 接口概述
工作流全局参数接口用于管理工作流中的全局参数，支持参数的增删改查、批量操作和参数值获取等功能。

## 基础路径
```
/workflow/global/param
```

## 接口列表

### 1. 分页查询工作流全局参数
- **请求方式**: GET
- **请求路径**: `/workflow/global/param/page`
- **描述**: 分页查询工作流全局参数，支持按工作流ID、参数类型、数据类型和参数键进行筛选

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| workflowId | String | 否 | 工作流ID |
| paramType | String | 否 | 参数用途类型 (global_variable, db_config, api_config) |
| valueType | String | 否 | 值的数据类型 (string, number, boolean, json) |
| paramKey | String | 否 | 参数键 |
| pageNum | int | 否 | 页码，默认为1 |
| pageSize | int | 否 | 每页数量，默认为10 |

#### cURL请求示例
```bash
curl -X GET "http://localhost:8080/workflow/global/param/page?pageNum=1&pageSize=10&workflowId=workflow123&paramType=global_variable" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 2. 根据ID获取工作流全局参数
- **请求方式**: GET
- **请求路径**: `/workflow/global/param/{id}`
- **描述**: 根据ID获取指定的工作流全局参数详情

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | String | 是 | 参数ID |

#### cURL请求示例
```bash
curl -X GET "http://localhost:8080/workflow/global/param/param123" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 3. 保存工作流全局参数
- **请求方式**: POST
- **请求路径**: `/workflow/global/param`
- **描述**: 保存一个新的工作流全局参数

#### 请求体 (使用WorkflowGlobalParamRequest)
```json
{
  "id": "param123", // 可选，前端提供ID
  "paramKey": "database_url",
  "paramValue": "jdbc:mysql://localhost:3306/test",
  "paramType": "db_config",
  "valueType": "string",
  "workflowId": "workflow123",
  "remark": "数据库连接地址"
}
```

#### cURL请求示例
```bash
curl -X POST "http://localhost:8080/workflow/global/param" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "id": "param123",
    "paramKey": "database_url",
    "paramValue": "jdbc:mysql://localhost:3306/test",
    "paramType": "db_config",
    "valueType": "string",
    "workflowId": "workflow123",
    "remark": "数据库连接地址"
  }'
```

### 4. 更新工作流全局参数
- **请求方式**: PUT
- **请求路径**: `/workflow/global/param`
- **描述**: 更新现有的工作流全局参数

#### 请求体 (使用WorkflowGlobalParamRequest)
```json
{
  "id": "param123",
  "paramKey": "database_url",
  "paramValue": "jdbc:mysql://newhost:3306/test",
  "paramType": "db_config",
  "valueType": "string",
  "workflowId": "workflow123",
  "remark": "数据库连接地址(已更新)"
}
```

#### cURL请求示例
```bash
curl -X PUT "http://localhost:8080/workflow/global/param" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "id": "param123",
    "paramKey": "database_url",
    "paramValue": "jdbc:mysql://newhost:3306/test",
    "paramType": "db_config",
    "valueType": "string",
    "workflowId": "workflow123",
    "remark": "数据库连接地址(已更新)"
  }'
```

### 5. 删除工作流全局参数
- **请求方式**: DELETE
- **请求路径**: `/workflow/global/param/{id}`
- **描述**: 根据ID删除指定的工作流全局参数

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | String | 是 | 参数ID |

#### cURL请求示例
```bash
curl -X DELETE "http://localhost:8080/workflow/global/param/param123" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 6. 批量保存或更新参数
- **请求方式**: POST
- **请求路径**: `/workflow/global/param/batch`
- **描述**: 批量保存或更新工作流全局参数

#### 请求体 (使用WorkflowGlobalParamRequest)
```json
{
  "params": [
    {
      "id": "param123", // 可选，前端提供ID
      "paramKey": "api_key",
      "paramValue": "abc123",
      "paramType": "api_config",
      "valueType": "string",
      "workflowId": "workflow123",
      "remark": "API密钥"
    },
    {
      "id": "param456", // 可选，前端提供ID
      "paramKey": "timeout",
      "paramValue": "30000",
      "paramType": "global_variable",
      "valueType": "number",
      "workflowId": "workflow123",
      "remark": "超时时间"
    }
  ]
}
```

#### cURL请求示例
```bash
curl -X POST "http://localhost:8080/workflow/global/param/batch" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "params": [
      {
        "id": "param123",
        "paramKey": "api_key",
        "paramValue": "abc123",
        "paramType": "api_config",
        "valueType": "string",
        "workflowId": "workflow123",
        "remark": "API密钥"
      },
      {
        "id": "param456",
        "paramKey": "timeout",
        "paramValue": "30000",
        "paramType": "global_variable",
        "valueType": "number",
        "workflowId": "workflow123",
        "remark": "超时时间"
      }
    ]
  }'
```

### 7. 根据参数键获取参数值
- **请求方式**: GET
- **请求路径**: `/workflow/global/param/value/{paramKey}`
- **描述**: 根据参数键获取参数值，可指定工作流ID

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| paramKey | String | 是 | 参数键 |
| workflowId | String | 否 | 工作流ID，可为null表示全局参数 |

#### cURL请求示例
```bash
curl -X GET "http://localhost:8080/workflow/global/param/value/database_url?workflowId=workflow123" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 8. 根据工作流ID删除所有参数
- **请求方式**: DELETE
- **请求路径**: `/workflow/global/param/workflow/{workflowId}`
- **描述**: 根据工作流ID删除该工作流下的所有参数

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| workflowId | String | 是 | 工作流ID |

#### cURL请求示例
```bash
curl -X DELETE "http://localhost:8080/workflow/global/param/workflow/workflow123" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
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
    "records": [
      {
        "id": "param123",
        "paramKey": "database_url",
        "paramValue": "jdbc:mysql://localhost:3306/test",
        "paramType": "db_config",
        "valueType": "string",
        "workflowId": "workflow123",
        "description": "数据库连接地址",
        "createTime": 1640995200000,
        "updateTime": 1640995200000,
        "createUserId": "user123",
        "updateUserId": "user123"
      }
    ],
    "total": 1,
    "pageNum": 1,
    "pageSize": 10
  }
}
```

## 参数说明

### paramType (参数用途类型)
- `global_variable`: 全局变量
- `db_config`: 数据库配置
- `api_config`: 第三方API配置

### valueType (值的数据类型)
- `string`: 字符串
- `number`: 数字
- `boolean`: 布尔值
- `json`: JSON对象