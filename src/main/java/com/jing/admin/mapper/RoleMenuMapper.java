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
     * 根据角色ID获取菜单ID列表
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    @Select("SELECT menu_id FROM tb_role_menu WHERE role_id = #{roleId}")
    List<String> selectMenuIdsByRoleId(@Param("roleId") String roleId);
    
    /**
     * 根据菜单ID删除角色菜单关联
     * @param menuId 菜单ID
     */
    @Delete("DELETE FROM tb_role_menu WHERE menu_id = #{menuId}")
    void deleteByMenuId(@Param("menuId") String menuId);
    
    /**
     * 根据角色ID删除角色菜单关联
     * @param roleId 角色ID
     */
    @Delete("DELETE FROM tb_role_menu WHERE role_id = #{roleId}")
    void deleteByRoleId(@Param("roleId") String roleId);

    @Update("UPDATE tb_role_menu SET role_id = #{newRoleId} WHERE role_id = #{oldRoleId}")
    void updateRoleId(@Param("oldRoleId") String oldRoleId, @Param("newRoleId") String newRoleId);

    @Select("<script>SELECT COUNT(*) &gt; 0 FROM tb_role_menu WHERE menu_id = #{menuId} AND role_id IN <foreach collection='roleIds' item='roleId' open='(' separator=',' close=')'>#{roleId}</foreach></script>")
    boolean existsByRoleIdsAndMenuId(@Param("menuId") String menuId, @Param("roleIds") List<String> roleIds);
}
