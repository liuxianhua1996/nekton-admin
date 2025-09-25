package com.jing.admin.config;

import com.jing.admin.core.cache.RoleMenuCache;
import com.jing.admin.core.utils.MenuUtil;
import com.jing.admin.model.domain.Menu;
import com.jing.admin.model.domain.Role;
import com.jing.admin.model.dto.MenuDTO;
import com.jing.admin.service.MenuService;
import com.jing.admin.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 角色菜单初始化器
 * 在应用启动时初始化所有角色的菜单并存储到内存中
 */
@Component
@Order(1) // 确保在其他CommandLineRunner之前执行
public class RoleMenuInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(RoleMenuInitializer.class);
    
    @Autowired
    private RoleService roleService;
    
    @Autowired
    private MenuService menuService;
    
    @Autowired
    private RoleMenuCache roleMenuCache;
    
    @Override
    public void run(String... args) throws Exception {
        logger.info("开始初始化角色菜单缓存...");
        
        try {
            // 获取所有角色
            List<Role> roles = roleService.getAllRoles();
            logger.info("获取到 {} 个角色", roles.size());

            // 获取所有菜单（admin角色的菜单）
            List<Menu> allMenus = menuService.getAllMenus();
            logger.info("获取到 {} 个菜单项", allMenus.size());

            // 为每个角色初始化菜单缓存
            for (Role role : roles) {
                // admin角色拥有所有菜单权限
                if ("ADMIN".equals(role.getName())) {
                    roleMenuCache.setRoleMenus(role.getName(), allMenus);
                    List<MenuDTO> menuTree = MenuUtil.buildMenuTree(allMenus);
                    roleMenuCache.setRoleMenuTree(role.getName(), menuTree);
                    logger.info("已为角色 {} 初始化菜单缓存，包含 {} 个菜单项", role.getName(), allMenus.size());
                } else {
                    // 其他角色按实际分配的菜单初始化
                    List<Menu> roleMenus = menuService.getMenusByRole(role.getName());
                    roleMenuCache.setRoleMenus(role.getName(), roleMenus);
                    List<MenuDTO> menuTree = MenuUtil.buildMenuTree(roleMenus);
                    roleMenuCache.setRoleMenuTree(role.getName(), menuTree);
                    logger.info("已为角色 {} 初始化菜单缓存，包含 {} 个菜单项", role.getName(), roleMenus.size());
                }
            }

            logger.info("角色菜单缓存初始化完成");
        } catch (Exception e) {
            logger.error("初始化角色菜单缓存时发生错误", e);
            throw e;
        }
    }
}