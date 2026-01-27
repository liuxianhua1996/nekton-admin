package com.jing.admin.service.impl;

import com.jing.admin.core.constant.Role;
import com.jing.admin.mapper.MenuMapper;
import com.jing.admin.mapper.RoleMenuMapper;
import com.jing.admin.model.domain.LoginUser;
import com.jing.admin.model.domain.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("permissionService")
public class PermissionService {
    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    public boolean hasMenu(String menuCode) {
        if (menuCode == null || menuCode.isBlank()) {
            return false;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof LoginUser loginUser)) {
            return false;
        }
        if (loginUser.getRoles() == null || loginUser.getRoles().isEmpty()) {
            return false;
        }
        if (loginUser.getRoles().contains(Role.ADMIN)) {
            return true;
        }
        Menu menu = menuMapper.selectByCode(menuCode);
        if (menu == null) {
            return false;
        }
        List<String> roleNames = loginUser.getRoles().stream().map(Role::name).toList();
        return roleMenuMapper.existsByRolesAndMenuId(menu.getId(), roleNames);
    }
}
