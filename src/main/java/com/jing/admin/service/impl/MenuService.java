package com.jing.admin.service.impl;

import com.jing.admin.core.cache.RoleMenuCache;
import com.jing.admin.core.constant.Role;
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
    
    @Autowired
    private RoleService roleService;
    
    @Autowired
    private RoleMenuCache roleMenuCache;

    
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
        // 先从缓存中获取
        List<Menu> cachedMenus = roleMenuCache.getRoleMenus(role);
        if (cachedMenus != null) {
            return cachedMenus;
        }
        
        // 缓存中没有则从数据库获取
        List<Menu> menus = menuMapper.selectByRole(role);
        
        // 将结果存入缓存
        roleMenuCache.setRoleMenus(role, menus);
        
        return menus;
    }
    
    /**
     * 根据角色获取菜单树
     * @param role 角色
     * @return 菜单树结构
     */
    public List<MenuDTO> getMenuTreeByRole(String role) {
        // 先从缓存中获取
        List<MenuDTO> cachedMenuTree = roleMenuCache.getRoleMenuTree(role);
        if (cachedMenuTree != null) {
            return cachedMenuTree;
        }
        List<Menu> menus;
        if(Role.ADMIN.equals(Role.fromName(role))){
            menus = menuMapper.selectAll();
        } else {
            menus = menuMapper.selectByRole(role);
        }
        List<MenuDTO> menuTree = MenuUtil.buildMenuTree(menus);
        // 将结果存入缓存
        roleMenuCache.setRoleMenuTree(role, menuTree);
        
        return menuTree;
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
        
        // 更新缓存：清除该角色的缓存，下次获取时重新加载
        roleMenuCache.setRoleMenus(role, null);
        roleMenuCache.setRoleMenuTree(role, null);
    }
    
    /**
     * 获取角色的菜单ID列表
     * @param role 角色
     * @return 菜单ID列表
     */
    public List<String> getMenuIdsByRole(String role) {
        return roleMenuMapper.selectMenuIdsByRole(role);
    }
    
    /**
     * 刷新所有角色的菜单缓存
     * 该方法会在角色或菜单发生变化时调用
     */
    public void refreshRoleMenuCache() {
        // 清空所有缓存
        roleMenuCache.clearAll();
        
        // 重新加载所有角色的菜单缓存
        List<com.jing.admin.model.domain.Role> roles = roleService.getAllRoles();
        List<Menu> allMenus = menuMapper.selectAll();
        
        for (com.jing.admin.model.domain.Role role : roles) {
            // admin角色拥有所有菜单权限
            if ("ADMIN".equals(role.getName())) {
                roleMenuCache.setRoleMenus(role.getName(), allMenus);
                List<MenuDTO> menuTree = MenuUtil.buildMenuTree(allMenus);
                roleMenuCache.setRoleMenuTree(role.getName(), menuTree);
            } else {
                // 其他角色按实际分配的菜单初始化
                List<Menu> roleMenus = menuMapper.selectByRole(role.getName());
                roleMenuCache.setRoleMenus(role.getName(), roleMenus);
                List<MenuDTO> menuTree = MenuUtil.buildMenuTree(roleMenus);
                roleMenuCache.setRoleMenuTree(role.getName(), menuTree);
            }
        }
    }
}