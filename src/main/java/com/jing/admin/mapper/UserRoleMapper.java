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
     * 根据用户ID查询角色ID列表
     * @param userId 用户ID
     * @return 角色ID列表
     */
    @Select("SELECT role_id FROM tb_user_roles WHERE user_id = #{userId}")
    List<String> selectRoleIdsByUserId(@Param("userId") String userId);

    @Select("SELECT user_id FROM tb_user_roles WHERE role_id = #{roleId}")
    List<String> selectUserIdsByRoleId(@Param("roleId") String roleId);
    
    /**
     * 为用户添加角色
     * @param userId 用户ID
     * @param roleId 角色ID
     */
    @Insert("INSERT INTO tb_user_roles (user_id, role_id, create_time, update_time) VALUES (#{userId}, #{roleId}, #{createTime}, #{createTime}) ")
    void insertUserRole(@Param("userId") String userId, @Param("roleId") String roleId, @Param("createTime") long createTime);
    
    /**
     * 删除用户的角色
     * @param userId 用户ID
     * @param roleId 角色ID
     */
    @Delete("DELETE FROM tb_user_roles WHERE user_id = #{userId} AND role_id = #{roleId}")
    void deleteUserRole(@Param("userId") String userId, @Param("roleId") String roleId);
    
    /**
     * 删除用户的所有角色
     * @param userId 用户ID
     */
    @Delete("DELETE FROM tb_user_roles WHERE user_id = #{userId}")
    void deleteUserRolesByUserId(@Param("userId") String userId);
    
    /**
     * 根据角色ID删除用户角色关联
     * @param roleId 角色ID
     */
    @Delete("DELETE FROM tb_user_roles WHERE role_id = #{roleId}")
    void deleteByRoleId(@Param("roleId") String roleId);

    @Update("UPDATE tb_user_roles SET role_id = #{newRoleId} WHERE role_id = #{oldRoleId}")
    void updateRoleId(@Param("oldRoleId") String oldRoleId, @Param("newRoleId") String newRoleId);
}
