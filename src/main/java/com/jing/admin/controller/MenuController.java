package com.jing.admin.controller;

import com.jing.admin.core.HttpResult;
import com.jing.admin.model.domain.Menu;
import com.jing.admin.model.dto.MenuDTO;
import com.jing.admin.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menus")
public class MenuController {

    @Autowired
    private MenuService menuService;


    /**
     * 获取所有菜单
     */
    @GetMapping
    public HttpResult<List<Menu>> getAllMenus() {
        List<Menu> menus = menuService.getAllMenus();
        return HttpResult.success(menus);
    }

    /**
     * 根据角色获取菜单
     */
    @GetMapping("/role/{role}")
    public HttpResult<List<Menu>> getMenusByRole(@PathVariable String role) {
        List<Menu> menus = menuService.getMenusByRole(role);
        return HttpResult.success(menus);
    }

    /**
     * 根据角色获取菜单树
     */
    @GetMapping("/role/{role}/tree")
    public HttpResult<List<MenuDTO>> getMenuTreeByRole(@PathVariable String role) {
        List<MenuDTO> menuTree = menuService.getMenuTreeByRole(role);
        return HttpResult.success(menuTree);
    }

    /**
     * 为角色分配菜单
     */
    @PostMapping("/assign/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public HttpResult<Void> assignMenusToRole(@PathVariable String role, @RequestBody List<String> menuIds) {
        menuService.assignMenusToRole(role, menuIds);
        return HttpResult.success();
    }

    /**
     * 获取角色的菜单ID列表
     */
    @GetMapping("/role/{role}/ids")
    @PreAuthorize("hasRole('ADMIN')")
    public HttpResult<List<String>> getMenuIdsByRole(@PathVariable String role) {
        List<String> menuIds = menuService.getMenuIdsByRole(role);
        return HttpResult.success(menuIds);
    }
}