# 管理员模块API文档

## 概述

管理员模块用于管理系统管理员身份（super_admin/admin）以及管理员与菜单的授权关系。管理员菜单授权用于控制系统菜单访问权限。

## 通用说明

- 所有API接口都返回JSON格式数据
- 日期时间字段使用毫秒时间戳格式
- 所有接口均需提供有效的认证信息
- 接口路径默认前缀为 `/api`

## 管理员接口

### 1. 创建管理员

- **接口地址**: `POST /api/admins`
- **功能描述**: 创建新的管理员
- **请求参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| userId | String | 是 | 用户ID |
| adminType | String | 是 | 管理员类型：super_admin / admin |

- **请求示例**:
```json
{
  "userId": "user_001",
  "adminType": "admin"
}
```

- **响应参数**:

| 参数名 | 类型 | 描述 |
|--------|------|------|
| code | String | 响应状态码 |
| message | String | 响应消息 |
| data | Object | 管理员信息 |
| data.id | String | 管理员ID |
| data.userId | String | 用户ID |
| data.userName | String | 用户名 |
| data.adminType | String | 管理员类型 |
| data.createTime | Long | 创建时间 |
| data.updateTime | Long | 更新时间 |
| data.createUserId | String | 创建用户ID |
| data.updateUserId | String | 更新用户ID |
| data.createUserName | String | 创建用户名称 |
| data.updateUserName | String | 更新用户名称 |

### 2. 更新管理员

- **接口地址**: `PUT /api/admins/{id}`
- **功能描述**: 更新管理员信息
- **路径参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | String | 是 | 管理员ID |

- **请求参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| userId | String | 否 | 用户ID |
| adminType | String | 否 | 管理员类型：super_admin / admin |

- **响应参数**: 同“创建管理员”

### 3. 删除管理员

- **接口地址**: `DELETE /api/admins/{id}`
- **功能描述**: 删除指定管理员
- **路径参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | String | 是 | 管理员ID |

- **响应参数**:

| 参数名 | 类型 | 描述 |
|--------|------|------|
| code | String | 响应状态码 |
| message | String | 响应消息 |
| data | Boolean | 删除结果 |

### 4. 根据ID获取管理员

- **接口地址**: `GET /api/admins/{id}`
- **功能描述**: 获取管理员详情
- **路径参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | String | 是 | 管理员ID |

- **响应参数**: 同“创建管理员”

### 5. 获取管理员分页列表

- **接口地址**: `GET /api/admins/page`
- **功能描述**: 获取管理员分页列表，支持条件查询
- **请求参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| current | Long | 否 | 当前页码，默认为1 |
| size | Long | 否 | 每页显示条数，默认为10 |
| userId | String | 否 | 用户ID |
| username | String | 否 | 用户名模糊查询 |
| adminType | String | 否 | 管理员类型 |

- **响应参数**:

| 参数名 | 类型 | 描述 |
|--------|------|------|
| code | String | 响应状态码 |
| message | String | 响应消息 |
| data | Object | 分页结果数据 |
| data.records | Array | 当前页数据列表，元素结构同“创建管理员” |
| data.total | Long | 总记录数 |
| data.current | Long | 当前页码 |
| data.size | Long | 每页显示条数 |

## 管理员菜单授权接口

### 1. 分配管理员菜单

- **接口地址**: `PUT /api/admins/{id}/menus`
- **功能描述**: 为管理员分配菜单
- **路径参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | String | 是 | 管理员ID |

- **请求参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| menuIds | Array | 是 | 菜单ID列表 |

- **请求示例**:
```json
{
  "menuIds": ["menu_001", "menu_002"]
}
```

- **响应参数**:

| 参数名 | 类型 | 描述 |
|--------|------|------|
| code | String | 响应状态码 |
| message | String | 响应消息 |
| data | Object | 空对象 |

### 2. 查询管理员菜单

- **接口地址**: `GET /api/admins/{id}/menus`
- **功能描述**: 查询管理员关联的菜单ID列表
- **路径参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | String | 是 | 管理员ID |

- **响应参数**:

| 参数名 | 类型 | 描述 |
|--------|------|------|
| code | String | 响应状态码 |
| message | String | 响应消息 |
| data | Array | 菜单ID列表 |

### 3. 清空管理员菜单

- **接口地址**: `DELETE /api/admins/{id}/menus`
- **功能描述**: 清空管理员菜单授权
- **路径参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | String | 是 | 管理员ID |

- **响应参数**:

| 参数名 | 类型 | 描述 |
|--------|------|------|
| code | String | 响应状态码 |
| message | String | 响应消息 |
| data | Object | 空对象 |
