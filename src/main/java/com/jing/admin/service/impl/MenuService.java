package com.jing.admin.service.impl;

import com.jing.admin.core.cache.RoleMenuCache;
import com.jing.admin.core.constant.AdminType;
import com.jing.admin.core.constant.Role;
import com.jing.admin.core.utils.MenuUtil;
import com.jing.admin.mapper.AdminMapper;
import com.jing.admin.mapper.AdminMenuMapper;
import com.jing.admin.mapper.MenuMapper;
import com.jing.admin.mapper.RoleMenuMapper;
import com.jing.admin.model.domain.Admin;
import com.jing.admin.model.domain.Menu;
import com.jing.admin.model.domain.RoleMenu;
import com.jing.admin.model.dto.MenuDTO;
import org.slf4j.MDC;
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

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private AdminMenuMapper adminMenuMapper;

    
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

    public List<MenuDTO> getMenuTreeByAdmin(String userId) {
        if (userId == null || userId.isBlank()) {
            return List.of();
        }
        Admin admin = adminMapper.selectByUserId(userId);
        if (admin == null) {
            return List.of();
        }
        AdminType adminType = AdminType.fromCode(admin.getAdminType());
        List<Menu> menus;
        if (AdminType.SUPER_ADMIN.equals(adminType)) {
            menus = menuMapper.selectAll();
        } else {
            menus = adminMenuMapper.selectMenusByAdminId(admin.getId());
        }
        return MenuUtil.buildMenuTree(menus);
    }
    
    /**
     * 为角色分配菜单
     * @param role 角色
     * @param menuIds 菜单ID列表
     */
    @Transactional
    public void assignMenusToRole(String role, List<String> menuIds) {
        // 先根据角色名称获取角色ID
        com.jing.admin.model.domain.Role roleEntity = roleService.getByName(role);
        if (roleEntity == null) {
            throw new RuntimeException("角色不存在: " + role);
        }
        String roleId = roleEntity.getId();
        
        // 先删除角色原有的菜单关联
        roleMenuMapper.deleteByRoleId(roleId);
        if (menuIds == null || menuIds.isEmpty()) {
            roleMenuCache.setRoleMenus(role, null);
            roleMenuCache.setRoleMenuTree(role, null);
            return;
        }
        
        // 添加新的菜单关联
        long currentTime = System.currentTimeMillis();
        String userId = MDC.get("userId");
        for (String menuId : menuIds) {
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setId(UUID.randomUUID().toString().replace("-", ""));
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenu.setCreateTime(currentTime);
            roleMenu.setUpdateTime(currentTime);
            roleMenu.setCreateUserId(userId);
            roleMenu.setUpdateUserId(userId);
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
        // 先根据角色名称获取角色ID
        com.jing.admin.model.domain.Role roleEntity = roleService.getByName(role);
        if (roleEntity == null) {
            return List.of();
        }
        String roleId = roleEntity.getId();
        return roleMenuMapper.selectMenuIdsByRoleId(roleId);
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
            // 使用统一的逻辑处理所有角色，包括admin和其他角色
            List<Menu> roleMenus;
            if (Role.ADMIN.equals(Role.fromName(role.getName()))) {
                // admin角色拥有所有菜单权限
                roleMenus = allMenus;
            } else {
                // 其他角色按实际分配的菜单初始化
                roleMenus = menuMapper.selectByRole(role.getName());
            }
            
            roleMenuCache.setRoleMenus(role.getName(), roleMenus);
            List<MenuDTO> menuTree = MenuUtil.buildMenuTree(roleMenus);
            roleMenuCache.setRoleMenuTree(role.getName(), menuTree);
        }
    }
}
