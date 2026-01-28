package com.jing.admin.service.impl;

import com.jing.admin.core.constant.AdminType;
import com.jing.admin.mapper.AdminMapper;
import com.jing.admin.mapper.AdminMenuMapper;
import com.jing.admin.mapper.MenuMapper;
import com.jing.admin.model.domain.Admin;
import com.jing.admin.model.domain.LoginUser;
import com.jing.admin.model.domain.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("permissionService")
public class PermissionService {
    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private AdminMenuMapper adminMenuMapper;

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
        String adminUserId = loginUser.getId() == null || loginUser.getId().isBlank()
                ? loginUser.getUuid()
                : loginUser.getId();
        if (adminUserId == null || adminUserId.isBlank()) {
            return false;
        }
        Admin admin = adminMapper.selectByUserId(adminUserId);
        if (admin == null) {
            return false;
        }
        AdminType adminType = AdminType.fromCode(admin.getAdminType());
        if (AdminType.SUPER_ADMIN.equals(adminType)) {
            return true;
        }
        Menu menu = menuMapper.selectByCode(menuCode);
        if (menu == null) {
            return false;
        }
        return adminMenuMapper.existsByAdminIdAndMenuId(admin.getId(), menu.getId());
    }
}
