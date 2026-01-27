package com.jing.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jing.admin.model.domain.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 菜单Mapper接口
 * 使用MyBatis-Plus进行数据访问
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {
    
    /**
     * 根据角色获取菜单列表
     * @param role 角色
     * @return 菜单列表
     */
    @Select("SELECT m.* FROM tb_menu m " +
            "JOIN tb_role_menu rm ON m.id = rm.menu_id " +
            "JOIN tb_roles r ON rm.role_id = r.id " +
            "WHERE r.name = #{role} " +
            "ORDER BY m.sort_order")
    List<Menu> selectByRole(@Param("role") String role);
    
    /**
     * 获取所有菜单列表
     * @return 菜单列表
     */
    @Select("SELECT * FROM tb_menu ORDER BY sort_order")
    List<Menu> selectAll();

    @Select("SELECT * FROM tb_menu WHERE code = #{code}")
    Menu selectByCode(@Param("code") String code);
}
