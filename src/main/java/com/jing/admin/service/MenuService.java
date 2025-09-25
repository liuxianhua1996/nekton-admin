package com.jing.admin.service;

import com.jing.admin.core.utils.MenuUtil;
import com.jing.admin.mapper.MenuMapper;
import com.jing.admin.mapper.RoleMenuMapper;
import com.jing.admin.model.domain.Menu;
import com.jing.admin.model.domain.RoleMenu;
import com.jing.admin.model.dto.MenuDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 菜单服务实现类
 */
@Service
public class MenuService {
    
    @Autowired
    private MenuMapper menuMapper;
    
    @Autowired
    private RoleMenuMapper roleMenuMapper;

    
    /**
     * 获取所有菜单
     * @return 菜单列表
     */
    public List<Menu> getAllMenus() {
        return menuMapper.selectAll();
    }
    
    /**
     * 根据角色获取菜单
     * @param role 角色
     * @return 菜单列表
     */
    public List<Menu> getMenusByRole(String role) {
        return menuMapper.selectByRole(role);
    }
    
    /**
     * 根据角色获取菜单树
     * @param role 角色
     * @return 菜单树结构
     */
    public List<MenuDTO> getMenuTreeByRole(String role) {
        List<Menu> menus = menuMapper.selectByRole(role);
        return MenuUtil.buildMenuTree(menus);
    }
    
    /**
     * 为角色分配菜单
     * @param role 角色
     * @param menuIds 菜单ID列表
     */
    @Transactional
    public void assignMenusToRole(String role, List<String> menuIds) {
        // 先删除角色原有的菜单关联
        roleMenuMapper.deleteByRole(role);
        
        // 添加新的菜单关联
        long currentTime = System.currentTimeMillis();
        for (String menuId : menuIds) {
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setId(UUID.randomUUID().toString().replace("-", ""));
            roleMenu.setRole(role);
            roleMenu.setMenuId(menuId);
            roleMenu.setCreateTime(currentTime);
            roleMenu.setUpdateTime(currentTime);
            roleMenu.setCreateUserId("system");
            roleMenu.setUpdateUserId("system");
            roleMenuMapper.insert(roleMenu);
        }
    }
    
    /**
     * 获取角色的菜单ID列表
     * @param role 角色
     * @return 菜单ID列表
     */
    public List<String> getMenuIdsByRole(String role) {
        return roleMenuMapper.selectMenuIdsByRole(role);
    }
}