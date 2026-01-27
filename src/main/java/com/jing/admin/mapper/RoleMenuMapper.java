package com.jing.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jing.admin.model.domain.RoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 角色菜单关联Mapper接口
 * 使用MyBatis-Plus进行数据访问
 */
@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {
    
    /**
     * 根据角色获取菜单ID列表
     * @param role 角色
     * @return 菜单ID列表
     */
    @Select("SELECT menu_id FROM tb_role_menu WHERE role = #{role}")
    List<String> selectMenuIdsByRole(@Param("role") String role);
    
    /**
     * 根据菜单ID删除角色菜单关联
     * @param menuId 菜单ID
     */
    @Delete("DELETE FROM tb_role_menu WHERE menu_id = #{menuId}")
    void deleteByMenuId(@Param("menuId") String menuId);
    
    /**
     * 根据角色删除角色菜单关联
     * @param role 角色
     */
    @Delete("DELETE FROM tb_role_menu WHERE role = #{role}")
    void deleteByRole(@Param("role") String role);

    @Update("UPDATE tb_role_menu SET role = #{newRole} WHERE role = #{oldRole}")
    void updateRoleName(@Param("oldRole") String oldRole, @Param("newRole") String newRole);

    @Select("<script>SELECT COUNT(*) &gt; 0 FROM tb_role_menu WHERE menu_id = #{menuId} AND role IN <foreach collection='roles' item='role' open='(' separator=',' close=')'>#{role}</foreach></script>")
    boolean existsByRolesAndMenuId(@Param("menuId") String menuId, @Param("roles") List<String> roles);
}
