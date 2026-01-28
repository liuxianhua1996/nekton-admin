package com.jing.admin.controller;

import com.jing.admin.core.HttpResult;
import com.jing.admin.core.PageResult;
import com.jing.admin.model.api.AdminMenuAssignRequest;
import com.jing.admin.model.api.AdminQueryRequest;
import com.jing.admin.model.api.AdminRequest;
import com.jing.admin.model.dto.AdminDTO;
import com.jing.admin.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admins")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping
    @PreAuthorize("@permissionService.hasMenu('ADMIN_MANAGE')")
    public HttpResult<AdminDTO> createAdmin(@RequestBody AdminRequest request) {
        return HttpResult.success(adminService.createAdmin(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@permissionService.hasMenu('ADMIN_MANAGE')")
    public HttpResult<AdminDTO> updateAdmin(@PathVariable String id, @RequestBody AdminRequest request) {
        return HttpResult.success(adminService.updateAdmin(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@permissionService.hasMenu('ADMIN_MANAGE')")
    public HttpResult<Boolean> deleteAdmin(@PathVariable String id) {
        return HttpResult.success(adminService.deleteAdmin(id));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@permissionService.hasMenu('ADMIN_MANAGE')")
    public HttpResult<AdminDTO> getAdminById(@PathVariable String id) {
        return HttpResult.success(adminService.getAdminById(id));
    }

    @GetMapping("/page")
    @PreAuthorize("@permissionService.hasMenu('ADMIN_MANAGE')")
    public HttpResult<PageResult<AdminDTO>> getAdminPage(AdminQueryRequest queryRequest) {
        return HttpResult.success(adminService.getAdminPage(queryRequest));
    }

    @PutMapping("/{id}/menus")
    @PreAuthorize("@permissionService.hasMenu('ADMIN_MANAGE')")
    public HttpResult<Void> assignAdminMenus(@PathVariable String id, @RequestBody AdminMenuAssignRequest request) {
        adminService.assignAdminMenus(id, request == null ? null : request.getMenuIds());
        return HttpResult.success();
    }

    @GetMapping("/{id}/menus")
    @PreAuthorize("@permissionService.hasMenu('ADMIN_MANAGE')")
    public HttpResult<List<String>> getAdminMenuIds(@PathVariable String id) {
        return HttpResult.success(adminService.getAdminMenuIds(id));
    }

    @DeleteMapping("/{id}/menus")
    @PreAuthorize("@permissionService.hasMenu('ADMIN_MANAGE')")
    public HttpResult<Void> clearAdminMenus(@PathVariable String id) {
        adminService.clearAdminMenus(id);
        return HttpResult.success();
    }
}
