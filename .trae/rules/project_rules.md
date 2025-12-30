## 代码规范

- 在写 controller 时，要使用@RestController 注解，而不是@Controller 注解
- 在写 controller 时，前端传的参数要在 api 包下创建 xxxxRequest 类来接受前端传的参数
- 路由要使用@RequestMapping 注解，路由地址不允许使用中划线(-).-要用“/”代替划分多层路由
- 每一个数据库实体类的创建,当有 DTO 的时候 一定要有一个 Mapping 文件来进行转换
- Controller 层返回要用 HttpResult 来返回结果
- 分页要注意使用自定义 Mapper 来进行分页查询，因为要关联用户表 举例:
Service.java
```java
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jing.admin.core.PageResult;
import com.jing.admin.repository.WorkflowRepository;

@Service
public class WorkflowService {
    private final WorkflowRepository workflowRepository;

 /**
     * 分页查询工作流
     *
     * @param queryRequest 查询请求参数
     * @return 分页结果
     */
    public PageResult<WorkflowDTO> getWorkflowPage(WorkflowQueryRequest queryRequest) {
        // 创建分页对象
        Page<WorkflowDTO> page = new Page<>(queryRequest.getCurrent(), queryRequest.getSize());

        // 执行分页查询（关联用户表）
        IPage<WorkflowDTO> workflowPage = workflowRepository.selectWorkflowPageWithUser(
                page,
                queryRequest
        );

        // 构建分页结果
        PageResult<WorkflowDTO> pageResult = PageResult.of(
                workflowPage.getRecords(),
                workflowPage.getTotal(),
                workflowPage.getCurrent(),
                workflowPage.getSize()
        );

        return pageResult;
    }
}
```
Mapper.xml
```xml
    <!-- 分页查询工作流（关联用户表） -->
    <select id="selectWorkflowPageWithUser" resultMap="WorkflowDTOMap">
        SELECT 
            w.id,
            w.name,
            w.description,
            w.json_data,
            w.version,
            w.status,
            w.create_user_id,
            cu.username AS create_user_name,
            w.update_user_id,
            uu.username AS update_user_name,
            w.create_time,
            w.update_time
        FROM 
            tb_workflow w
        LEFT JOIN
            tb_users cu ON w.create_user_id = cu.uuid
        LEFT JOIN
            tb_users uu ON w.update_user_id = uu.uuid
        <where>
            <if test="query.name != null and query.name != ''">
                AND w.name LIKE CONCAT('%', #{name}, '%')
            </if>
            <if test="query.status != null and query.status != ''">
                AND w.status = #{status}
            </if>
            <if test="query.createUserId != null and query.createUserId != ''">
                AND w.create_user_id = #{createUserId}
            </if>
        </where>
        ORDER BY w.create_time DESC
    </select>
```

## 数据库规范

- 数据库表名要使用下划线(\_)分隔单词，而不是驼峰命名法
- 数据库字段名要使用下划线(\_)分隔单词，而不是驼峰命名法
- 表名一定要以"tb\_"开头

## 其他规范

- 租户的数据库是独立的,不需要使用租户 id 去过滤数据
