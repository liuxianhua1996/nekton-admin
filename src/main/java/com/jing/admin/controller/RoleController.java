package com.jing.admin.controller;

import com.jing.admin.core.HttpResult;
import com.jing.admin.model.domain.Role;
import com.jing.admin.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 创建角色
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public HttpResult<Role> createRole(@RequestBody Role role) {
        // 检查角色名称是否已存在
        if (roleService.existsByName(role.getName())) {
            return HttpResult.error("角色名称已存在");
        }
        
        Role createdRole = roleService.createRole(role);
        return HttpResult.success(createdRole);
    }

    /**
     * 更新角色
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public HttpResult<Role> updateRole(@PathVariable String id, @RequestBody Role role) {
        Role updatedRole = roleService.updateRole(id, role);
        return HttpResult.success(updatedRole);
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public HttpResult<Void> deleteRole(@PathVariable String id) {
        roleService.deleteRole(id);
        return HttpResult.success();
    }

    /**
     * 获取角色详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public HttpResult<Role> getRole(@PathVariable String id) {
        Role role = roleService.getRoleById(id);
        if (role == null) {
            return HttpResult.error("角色不存在");
        }
        return HttpResult.success(role);
    }

    /**
     * 获取所有角色
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public HttpResult<List<Role>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return HttpResult.success(roles);
    }

    /**
     * 检查角色名称是否存在
     */
    @GetMapping("/exists/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public HttpResult<Boolean> checkRoleExists(@PathVariable String name) {
        boolean exists = roleService.existsByName(name);
        return HttpResult.success(exists);
    }
}