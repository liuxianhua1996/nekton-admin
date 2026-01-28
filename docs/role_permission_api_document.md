# 角色与权限API文档

## 概述
角色模块用于审批流程或数据权限控制；系统“菜单访问权限”改为由“管理员身份（super_admin/admin）+ 管理员菜单授权”控制，并继续通过菜单 code 进行动态权限校验。

## 通用说明
- 所有API接口返回JSON格式数据
- 日期时间字段使用毫秒时间戳格式
- 所有接口均需提供有效的认证信息
- 接口路径默认前缀为 `/api`

## 权限标识
- 权限标识使用菜单 code
- 角色管理权限：ROLE_MANAGE
- 权限分配权限：PERMISSION_ASSIGN
- 管理员管理权限：ADMIN_MANAGE

## 角色管理接口
### 1. 创建角色
- **接口地址**: `POST /api/roles`
- **功能描述**: 创建新的角色
- **请求参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| name | String | 是 | 角色名称 |
| description | String | 否 | 角色描述 |

- **请求示例**:
```json
{
  "name": "MANAGER",
  "description": "经理角色"
}
```

- **响应参数**:
| 参数名 | 类型 | 描述 |
|--------|------|------|
| code | String | 响应状态码 |
| message | String | 响应消息 |
| data | Object | 角色信息 |
| data.id | String | 角色ID |
| data.name | String | 角色名称 |
| data.description | String | 角色描述 |
| data.createTime | Long | 创建时间 |
| data.updateTime | Long | 更新时间 |
| data.createUserId | String | 创建用户ID |
| data.updateUserId | String | 更新用户ID |

### 2. 更新角色
- **接口地址**: `PUT /api/roles/{id}`
- **功能描述**: 更新角色信息
- **路径参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | String | 是 | 角色ID |

- **请求参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| name | String | 是 | 角色名称 |
| description | String | 否 | 角色描述 |

- **请求示例**:
```json
{
  "name": "MANAGER",
  "description": "经理角色更新"
}
```

- **响应参数**: 同创建角色接口

### 3. 删除角色
- **接口地址**: `DELETE /api/roles/{id}`
- **功能描述**: 删除指定角色
- **路径参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | String | 是 | 角色ID |

- **响应参数**:
| 参数名 | 类型 | 描述 |
|--------|------|------|
| code | String | 响应状态码 |
| message | String | 响应消息 |
| data | Boolean | 删除结果 |

### 4. 角色详情
- **接口地址**: `GET /api/roles/{id}`
- **功能描述**: 获取角色详情
- **路径参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | String | 是 | 角色ID |

- **响应参数**: 同创建角色接口

### 5. 角色列表
- **接口地址**: `GET /api/roles`
- **功能描述**: 获取角色列表
- **响应参数**:

| 参数名 | 类型 | 描述 |
|--------|------|------|
| code | String | 响应状态码 |
| message | String | 响应消息 |
| data | Array | 角色列表 |

### 6. 角色名称是否存在
- **接口地址**: `GET /api/roles/exists/{name}`
- **功能描述**: 校验角色名称是否已存在
- **路径参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| name | String | 是 | 角色名称 |

- **响应参数**:

| 参数名 | 类型 | 描述 |
|--------|------|------|
| code | String | 响应状态码 |
| message | String | 响应消息 |
| data | Boolean | 是否存在 |

## 角色成员接口
### 1. 查询角色成员
- **接口地址**: `GET /api/roles/members`
- **功能描述**: 根据角色ID查询角色成员列表
- **请求参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| roleId | String | 是 | 角色ID |

- **响应参数**:
| 参数名 | 类型 | 描述 |
|--------|------|------|
| code | String | 响应状态码 |
| message | String | 响应消息 |
| data | Array | 角色成员列表 |
| data.id | String | 用户ID |
| data.uuid | String | 用户UUID |
| data.username | String | 用户名 |
| data.email | String | 邮箱 |
| data.enabled | Integer | 启用状态 |
| data.roles | Array | 角色列表 |
| data.tenant | Array | 租户信息列表 |
| data.selectedTenant | String | 当前选中租户 |

## 菜单权限接口
### 1. 分配角色菜单
- **接口地址**: `POST /api/menus/assign/{role}`
- **功能描述**: 通过角色名称分配菜单权限（用于审批/数据权限关联，不再用于系统菜单访问控制）
- **路径参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| role | String | 是 | 角色名称 |

- **请求参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| menuIds | Array | 是 | 菜单ID列表 |

- **响应参数**: 同“分配菜单权限”

### 2. 角色菜单ID列表
- **接口地址**: `GET /api/menus/role/{role}/ids`
- **功能描述**: 获取角色关联的菜单ID列表（用于审批/数据权限关联，不再用于系统菜单访问控制）
- **路径参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| role | String | 是 | 角色名称 |

- **响应参数**: 同“查询角色菜单”

### 3. 刷新角色菜单缓存
- **接口地址**: `POST /api/role/menus/refresh-cache`
- **功能描述**: 刷新角色菜单缓存
- **响应参数**:

| 参数名 | 类型 | 描述 |
|--------|------|------|
| code | String | 响应状态码 |
| message | String | 响应消息 |
| data | String | 操作结果信息 |

## 动态权限校验
- 采用 `@PreAuthorize("@permissionService.hasMenu('菜单CODE')")`
- super_admin 管理员默认拥有全部菜单权限
- admin 管理员仅能访问在“管理员菜单授权”中分配的菜单

## 重要说明
- 角色名称变更仍会同步更新用户角色与角色菜单关联，但不再影响系统菜单访问权限
- 系统菜单访问权限仅由“管理员身份 + 管理员菜单授权”决定
- 角色菜单分配为空只影响审批/数据权限，不影响管理员的菜单访问
