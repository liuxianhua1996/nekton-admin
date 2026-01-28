package com.jing.admin.service;

import com.jing.admin.core.PageResult;
import com.jing.admin.model.api.AdminQueryRequest;
import com.jing.admin.model.api.AdminRequest;
import com.jing.admin.model.dto.AdminDTO;

import java.util.List;

public interface AdminService {
    AdminDTO createAdmin(AdminRequest request);

    AdminDTO updateAdmin(String id, AdminRequest request);

    Boolean deleteAdmin(String id);

    AdminDTO getAdminById(String id);

    PageResult<AdminDTO> getAdminPage(AdminQueryRequest queryRequest);

    void assignAdminMenus(String adminId, List<String> menuIds);

    List<String> getAdminMenuIds(String adminId);

    void clearAdminMenus(String adminId);
}
