# 用户模块API文档

## 概述

用户模块提供租户视角的用户查询与详情获取能力。登录、获取当前登录信息(/auth/me)、当前用户菜单(/auth/menus)接口结构保持不变，但“菜单访问权限”已由管理员体系控制：
- roles 字段用于审批/数据权限；不再用于系统菜单访问权限
- 系统菜单访问权限由管理员身份（super_admin/admin）及管理员菜单授权决定

## 通用说明

- 所有API接口都返回JSON格式数据
- 日期时间字段使用毫秒时间戳格式
- 所有接口均需提供有效的认证信息

## 用户接口

### 1. 获取用户分页列表

- **接口地址**: `GET /users/page`
- **功能描述**: 获取用户分页列表，支持条件筛选
- **请求参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| current | Long | 否 | 当前页码，默认为1 |
| size | Long | 否 | 每页显示条数，默认为10 |
| username | String | 否 | 用户名模糊查询 |
| email | String | 否 | 邮箱 |
| enabled | Integer | 否 | 启用状态：1-启用，0-禁用 |

- **响应参数**:

| 参数名 | 类型 | 描述 |
|--------|------|------|
| success | Boolean | 请求是否成功 |
| message | String | 提示信息 |
| error | String | 错误信息 |
| errorCode | String | 错误码 |
| traceId | String | 链路追踪ID |
| data | Object | 分页结果数据，包含以下字段： |
| data.records | Array | 当前页数据列表 |
| data.total | Long | 总记录数 |
| data.size | Long | 每页显示条数 |
| data.current | Long | 当前页码 |
| data.pages | Long | 总页数 |

- **数据字段说明(UserDTO)**:

| 字段名 | 类型 | 描述 |
|--------|------|------|
| id | String | 用户ID |
| username | String | 用户名 |
| email | String | 邮箱 |
| enabled | Integer | 启用状态 |
| roles | Array | 角色列表（用于审批/数据权限，不用于菜单权限） |
| tenant | Array | 租户列表 |
| tenant.tenantId | String | 租户ID |
| tenant.tenantName | String | 租户名称 |
| selectedTenant | String | 当前选中租户 |

- **响应示例**:
```json
{
  "success": true,
  "message": "",
  "data": {
    "records": [
      {
        "id": "user_001",
        "username": "admin",
        "email": "admin@example.com",
        "enabled": 1,
        "roles": [
          "ADMIN"
        ],
        "tenant": [
          {
            "tenantId": "tenant_001",
            "tenantName": "默认租户"
          }
        ],
        "selectedTenant": "tenant_001"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

### 2. 根据ID获取用户详情

- **接口地址**: `GET /users/{id}`
- **功能描述**: 获取指定用户的详细信息
- **路径参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | String | 是 | 用户ID |

- **响应参数**: 同用户分页列表接口中的UserDTO字段

- **响应示例**:
```json
{
  "success": true,
  "message": "",
  "data": {
    "id": "user_001",
    "username": "admin",
    "email": "admin@example.com",
    "enabled": 1,
    "roles": [
      "ADMIN"
    ],
    "tenant": [
      {
        "tenantId": "tenant_001",
        "tenantName": "默认租户"
      }
    ],
    "selectedTenant": "tenant_001"
  }
}
```
