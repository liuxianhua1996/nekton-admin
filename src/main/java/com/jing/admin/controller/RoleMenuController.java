package com.jing.admin.controller;

import com.jing.admin.core.HttpResult;
import com.jing.admin.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 角色菜单控制器
 * 提供角色菜单相关的操作接口
 */
@RestController
@RequestMapping("/role-menus")
public class RoleMenuController {
    
    @Autowired
    private MenuService menuService;
    
    /**
     * 刷新角色菜单缓存
     * 只有管理员可以调用此接口
     * @return 操作结果
     */
    @PostMapping("/refresh-cache")
    public HttpResult<String> refreshRoleMenuCache() {
        try {
            menuService.refreshRoleMenuCache();
            return HttpResult.success("角色菜单缓存刷新成功");
        } catch (Exception e) {
            return HttpResult.error("角色菜单缓存刷新失败: " + e.getMessage());
        }
    }
}