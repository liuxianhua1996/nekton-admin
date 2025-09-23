package com.jing.admin.mapper;

import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 用户角色Mapper接口
 * 处理用户与角色的关联关系
 */
@Mapper
public interface UserRoleMapper {
    
    /**
     * 根据用户ID查询角色列表
     * @param userId 用户ID
     * @return 角色列表
     */
    @Select("SELECT role FROM tb_user_roles WHERE user_id = #{userId}")
    List<String> selectRolesByUserId(@Param("userId") String userId);
    
    /**
     * 为用户添加角色
     * @param userId 用户ID
     * @param role 角色
     */
    @Insert("INSERT INTO tb_user_roles (user_id, role,create_time,update_time) VALUES (#{userId}, #{role}, #{createTime},#{createTime}) ")
    void insertUserRole(@Param("userId") String userId, @Param("role") String role,@Param("createTime") long createTime);
    
    /**
     * 删除用户的角色
     * @param userId 用户ID
     * @param role 角色
     */
    @Delete("DELETE FROM tb_user_roles WHERE user_id = #{userId} AND role = #{role}")
    void deleteUserRole(@Param("userId") String userId, @Param("role") String role);
    
    /**
     * 删除用户的所有角色
     * @param userId 用户ID
     */
    @Delete("DELETE FROM tb_user_roles WHERE user_id = #{userId}")
    void deleteUserRolesByUserId(@Param("userId") String userId);
}