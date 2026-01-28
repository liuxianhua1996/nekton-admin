package com.jing.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jing.admin.model.domain.AdminMenu;
import com.jing.admin.model.domain.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AdminMenuMapper extends BaseMapper<AdminMenu> {
    @Select("SELECT m.* FROM tb_menu m JOIN tb_admin_menu am ON m.id = am.menu_id WHERE am.admin_id = #{adminId} ORDER BY m.sort_order")
    List<Menu> selectMenusByAdminId(@Param("adminId") String adminId);

    @Select("SELECT menu_id FROM tb_admin_menu WHERE admin_id = #{adminId}")
    List<String> selectMenuIdsByAdminId(@Param("adminId") String adminId);

    @Select("SELECT COUNT(*) > 0 FROM tb_admin_menu WHERE admin_id = #{adminId} AND menu_id = #{menuId}")
    boolean existsByAdminIdAndMenuId(@Param("adminId") String adminId, @Param("menuId") String menuId);
}
