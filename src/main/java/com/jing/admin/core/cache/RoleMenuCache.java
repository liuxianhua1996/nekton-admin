package com.jing.admin.core.cache;

import com.jing.admin.model.domain.Menu;
import com.jing.admin.model.dto.MenuDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 角色菜单缓存类
 * 用于在内存中存储角色与菜单的对应关系
 */
@Component
public class RoleMenuCache {
    
    /**
     * 使用ConcurrentHashMap存储角色菜单映射关系
     * key: 角色名称
     * value: 该角色对应的菜单列表
     */
    private final Map<String, List<Menu>> roleMenuMap = new ConcurrentHashMap<>();
    
    /**
     * 使用ConcurrentHashMap存储角色菜单树映射关系
     * key: 角色名称
     * value: 该角色对应的菜单树结构
     */
    private final Map<String, List<MenuDTO>> roleMenuTreeMap = new ConcurrentHashMap<>();
    
    /**
     * 设置角色的菜单列表
     * @param role 角色名称
     * @param menus 菜单列表
     */
    public void setRoleMenus(String role, List<Menu> menus) {
        if (menus == null) {
            roleMenuMap.remove(role);
        } else {
            roleMenuMap.put(role, menus);
        }
    }
    
    /**
     * 获取角色的菜单列表
     * @param role 角色名称
     * @return 菜单列表
     */
    public List<Menu> getRoleMenus(String role) {
        return roleMenuMap.get(role);
    }
    
    /**
     * 设置角色的菜单树结构
     * @param role 角色名称
     * @param menuTree 菜单树结构
     */
    public void setRoleMenuTree(String role, List<MenuDTO> menuTree) {
        if (menuTree == null) {
            roleMenuTreeMap.remove(role);
        } else {
            roleMenuTreeMap.put(role, menuTree);
        }
    }
    
    /**
     * 获取角色的菜单树结构
     * @param role 角色名称
     * @return 菜单树结构
     */
    public List<MenuDTO> getRoleMenuTree(String role) {
        return roleMenuTreeMap.get(role);
    }
    
    /**
     * 清空所有缓存
     */
    public void clearAll() {
        roleMenuMap.clear();
        roleMenuTreeMap.clear();
    }
}