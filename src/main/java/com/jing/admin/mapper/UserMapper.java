package com.jing.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jing.admin.model.domain.User;
import com.jing.admin.model.api.UserQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

/**
 * 用户Mapper接口
 * 使用MyBatis-Plus进行数据访问
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户对象
     */
    @Select("SELECT * FROM tb_users WHERE username = #{username}")
    Optional<User> findByUsername(String username);
    
    /**
     * 根据邮箱查找用户
     * @param email 邮箱
     * @return 用户对象
     */
    @Select("SELECT * FROM tb_users WHERE email = #{email}")
    Optional<User> findByEmail(String email);
    
    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM tb_users WHERE username = #{username}")
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     * @param email 邮箱
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM tb_users WHERE email = #{email}")
    boolean existsByEmail(String email);
    
    /**
     * 查询用户及其角色
     * @param id 用户ID
     * @return 用户对象
     */
    User selectUserWithRoles(String id);
    
    /**
     * 根据用户名查询用户及其角色
     * @param username 用户名
     * @return 用户对象
     */
    User selectUserWithRolesByUsername(String username);

    IPage<User> selectUserPage(
            Page<User> page,
            @Param("query") UserQueryRequest queryRequest
    );
}
