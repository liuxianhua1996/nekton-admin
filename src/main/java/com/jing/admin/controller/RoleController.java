package com.jing.admin.controller;

import com.jing.admin.core.HttpResult;
import com.jing.admin.model.api.RoleMemberQueryRequest;
import com.jing.admin.model.api.RoleMemberAssignRequest;
import com.jing.admin.model.api.RoleRequest;
import com.jing.admin.model.dto.RoleDTO;
import com.jing.admin.model.dto.UserDTO;
import com.jing.admin.service.impl.RoleService;
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
    @PreAuthorize("@permissionService.hasMenu('ROLE_MANAGE')")
    public HttpResult<RoleDTO> createRole(@RequestBody RoleRequest role) {
        // 检查角色名称是否已存在
        if (roleService.existsByName(role.getName())) {
            return HttpResult.error("角色名称已存在");
        }
        RoleDTO createdRole = roleService.createRole(role);
        return HttpResult.success(createdRole);
    }

    /**
     * 更新角色
     */
    @PutMapping("/{id}")
    @PreAuthorize("@permissionService.hasMenu('ROLE_MANAGE')")
    public HttpResult<RoleDTO> updateRole(@PathVariable String id, @RequestBody RoleRequest role) {
        RoleDTO updatedRole = roleService.updateRole(id, role);
        return HttpResult.success(updatedRole);
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@permissionService.hasMenu('ROLE_MANAGE')")
    public HttpResult<Void> deleteRole(@PathVariable String id) {
        roleService.deleteRole(id);
        return HttpResult.success();
    }

    /**
     * 获取角色详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("@permissionService.hasMenu('ROLE_MANAGE')")
    public HttpResult<RoleDTO> getRole(@PathVariable String id) {
        RoleDTO role = roleService.getRoleById(id);
        if (role == null) {
            return HttpResult.error("角色不存在");
        }
        return HttpResult.success(role);
    }

    /**
     * 获取所有角色
     */
    @GetMapping
    @PreAuthorize("@permissionService.hasMenu('ROLE_MANAGE')")
    public HttpResult<List<RoleDTO>> getAllRoles() {
        List<RoleDTO> roles = roleService.getRoleList();
        return HttpResult.success(roles);
    }

    /**
     * 检查角色名称是否存在
     */
    @GetMapping("/exists/{name}")
    @PreAuthorize("@permissionService.hasMenu('ROLE_MANAGE')")
    public HttpResult<Boolean> checkRoleExists(@PathVariable String name) {
        boolean exists = roleService.existsByName(name);
        return HttpResult.success(exists);
    }

    @GetMapping("/members")
    @PreAuthorize("@permissionService.hasMenu('ROLE_MANAGE')")
    public HttpResult<List<UserDTO>> getRoleMembers(RoleMemberQueryRequest request) {
        if (request == null || request.getRoleId() == null || request.getRoleId().isBlank()) {
            return HttpResult.error("角色ID不能为空");
        }
        RoleDTO role = roleService.getRoleById(request.getRoleId());
        if (role == null) {
            return HttpResult.error("角色不存在");
        }
        return HttpResult.success(roleService.getRoleMembers(request.getRoleId()));
    }

    @PostMapping("/members")
    @PreAuthorize("@permissionService.hasMenu('ROLE_MANAGE')")
    public HttpResult<Void> assignRoleMember(@RequestBody RoleMemberAssignRequest request) {
        roleService.assignRoleMember(request);
        return HttpResult.success();
    }
}
