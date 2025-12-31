# Nekton Admin 代码规范文档

## 1. 多租户架构规范

### 1.1 租户上下文管理
- 所有涉及多租户数据访问的代码必须使用 `TenantContextHolder` 来管理当前租户上下文
- 在多线程环境中，必须使用 `TenantContextWrapper` 来确保租户上下文正确传递到子线程
- 任何可能改变租户上下文的代码块结束后，必须清理或恢复租户上下文

### 1.2 数据库访问规范
- 每个租户拥有独立的数据库，通过 `TenantDynamicDataSource` 实现动态数据源路由
- 不需要在查询中添加 `tenant_id` 过滤条件，因为当前数据源已指向正确的租户数据库
- 所有数据库操作会自动路由到当前租户的数据库

### 1.3 线程池和异步执行规范
- 当在自定义线程池或异步任务中执行需要租户上下文的操作时，必须使用 `TenantContextWrapper` 包装任务
- 示例：
```java
// 错误做法
executorService.submit(() -> {
    // 执行需要租户上下文的操作
    workflowExecutionService.executeWorkflow(workflowId);
});

// 正确做法
String currentTenantId = TenantContextHolder.getTenantId();
Runnable wrappedTask = TenantContextWrapper.wrap(() -> {
    // 执行需要租户上下文的操作
    workflowExecutionService.executeWorkflow(workflowId);
}, currentTenantId);
executorService.submit(wrappedTask);
```

## 1.4 多租户定时任务管理规范
- 定时任务分为三种触发类型：cron（定时触发）、webhook（Webhook触发）、MQTT（MQTT消息触发）
- cron 类型的调度任务应使用 Quartz 框架进行管理，通过 `MultiTenantScheduleService` 统一注册和管理
- webhook 和 MQTT 类型的调度任务是被动触发，不需要主动执行
- 使用 `JobTaskManager` 统一管理基于 `schedule/job` 包的定时任务
- 所有定时任务执行时必须保持正确的租户上下文
- 示例：
```java
// 注册cron任务
AbstractJobTask<Void, Object> workflowJobTask = new AbstractJobTask<Void, Object>() {
    @Override
    public void run() {
        // 执行需要租户上下文的操作
        workflowExecutionService.executeWorkflow(workflowId);
    }
};
workflowJobTask.setTaskId(jobId);
workflowJobTask.setTaskName(jobName);

jobTaskManager.addCronJob(jobId, jobName, cronExpression, workflowJobTask);
```

## 2. 定时任务规范

### 2.1 多租户定时任务
- 使用 `MultiTenantScheduleService` 统一管理所有租户的cron定时任务
- 使用 `MultiTenantJobScheduler` 作为应用启动时的入口来初始化定时任务
- 定时任务执行时，必须确保在正确的租户上下文中执行
- 每个租户的定时任务独立执行，互不影响

### 2.2 定时任务线程池
- 定时任务应使用项目提供的线程池配置
- 在定时任务中执行需要租户上下文的操作时，必须使用 `TenantContextWrapper`

## 3. Controller 层规范

### 3.1 注解使用
- 使用 `@RestController` 注解，而不是 `@Controller` 注解
- 前端传的参数要在 api 包下创建 `xxxxRequest` 类来接受前端传的参数

### 3.2 路由规范
- 路由要使用 `@RequestMapping` 注解
- 路由地址不允许使用中划线(-)，要用"/"代替划分多层路由

### 3.3 返回值规范
- Controller 层返回要用 `HttpResult` 来返回结果

## 4. Service 层规范

### 4.1 DTO 和 Request 类
- 请求响应统一用 DTO 即可
- 请求参数用 Request 类来接受前端传的参数

### 4.2 分页查询
- 分页要注意使用自定义 Mapper 来进行分页查询，因为要关联用户表
- 使用 `PageResult` 和 `IPage` 进行分页查询

### 4.3 主键查询
- 由于主键都是 UUID，然后类里的 id 是 String，不能使用直接 `getById` 方法
- 使用 `QueryWrapper` 来查询，示例：
```java
public Workflow getById(String id) {
    QueryWrapper<Workflow> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("id", UUID.fromString(id));
    return this.getOne(queryWrapper);
}
```

## 5. 数据库规范

### 5.1 命名规范
- 数据库表名要使用下划线(_)分隔单词，而不是驼峰命名法
- 数据库字段名要使用下划线(_)分隔单词，而不是驼峰命名法
- 表名一定要以 "tb_" 开头

### 5.2 多租户数据隔离
- 租户的数据库是独立的，不需要使用租户 id 去过滤数据
- 每个租户的数据完全隔离在各自的数据库中

## 6. 实体类和 DTO 规范

### 6.1 实体类创建
- 每一个数据库实体类的创建，当有 DTO 的时候一定要有一个 Mapping 文件来进行转换

### 6.2 DTO 使用
- 统一使用 DTO 进行数据传输
- 避免直接暴露实体类给前端

## 7. 日志记录规范

### 7.1 调试日志
- 在涉及多租户操作的代码中，添加租户 ID 的调试日志
- 示例：`log.debug("执行工作流 {}，租户ID: {}", workflowId, currentTenantId);`

### 7.2 错误处理
- 所有异常应被适当捕获和记录
- 记录足够的上下文信息以便调试

## 8. 异常处理规范

### 8.1 统一异常处理
- 使用统一的异常处理机制
- 确保敏感信息不会在异常中泄露

### 8.2 租户上下文清理
- 在 try-finally 块中确保租户上下文被正确清理
- 防止租户上下文在异常情况下未被清理

## 9. 测试规范

### 9.1 多租户测试
- 编写测试用例时，确保测试多租户数据隔离
- 验证租户上下文在各种场景下的正确传递

### 9.2 定时任务测试
- 测试定时任务在多租户环境下的正确执行
- 验证执行结果保存到正确的租户数据库