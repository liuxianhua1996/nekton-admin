package com.jing.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jing.admin.core.PageResult;
import com.jing.admin.core.constant.AdminType;
import com.jing.admin.core.exception.BusinessException;
import com.jing.admin.mapper.AdminMapper;
import com.jing.admin.mapper.AdminMenuMapper;
import com.jing.admin.mapper.UserMapper;
import com.jing.admin.model.api.AdminQueryRequest;
import com.jing.admin.model.api.AdminRequest;
import com.jing.admin.model.domain.Admin;
import com.jing.admin.model.domain.AdminMenu;
import com.jing.admin.model.domain.User;
import com.jing.admin.model.dto.AdminDTO;
import com.jing.admin.model.mapping.AdminMapping;
import com.jing.admin.service.AdminService;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private AdminMenuMapper adminMenuMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public AdminDTO createAdmin(AdminRequest request) {
        if (request == null || request.getUserId() == null || request.getUserId().isBlank()) {
            throw new BusinessException("用户ID不能为空");
        }
        if (request.getAdminType() == null || request.getAdminType().isBlank()) {
            throw new BusinessException("管理员类型不能为空");
        }
        validateAdminType(request.getAdminType());
        Admin existing = adminMapper.selectByUserId(request.getUserId());
        if (existing != null) {
            throw new BusinessException("该用户已是管理员");
        }
        User user = getUserById(request.getUserId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        Admin entity = AdminMapping.INSTANCE.toEntity(request);
        if (entity.getId() == null || entity.getId().isBlank()) {
            entity.setId(UUID.randomUUID().toString());
        }
        long currentTime = System.currentTimeMillis();
        String operatorId = MDC.get("userId");
        entity.setCreateTime(currentTime);
        entity.setUpdateTime(currentTime);
        entity.setCreateUserId(operatorId);
        entity.setUpdateUserId(operatorId);
        adminMapper.insert(entity);
        return getAdminById(entity.getId());
    }

    @Override
    public AdminDTO updateAdmin(String id, AdminRequest request) {
        Admin admin = getAdminEntityById(id);
        if (admin == null) {
            throw new RuntimeException("管理员不存在");
        }
        if (request != null) {
            if (request.getAdminType() != null && !request.getAdminType().isBlank()) {
                validateAdminType(request.getAdminType());
                admin.setAdminType(request.getAdminType());
            }
            if (request.getUserId() != null && !request.getUserId().isBlank() && !request.getUserId().equals(admin.getUserId())) {
                Admin existing = adminMapper.selectByUserId(request.getUserId());
                if (existing != null && !existing.getId().equals(admin.getId())) {
                    throw new RuntimeException("该用户已是管理员");
                }
                User user = getUserById(request.getUserId());
                if (user == null) {
                    throw new RuntimeException("用户不存在");
                }
                admin.setUserId(request.getUserId());
            }
        }
        admin.setUpdateTime(System.currentTimeMillis());
        admin.setUpdateUserId(MDC.get("userId"));
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", UUID.fromString(id));
        adminMapper.update(admin, queryWrapper);
        return getAdminById(id);
    }

    @Override
    @Transactional
    public Boolean deleteAdmin(String id) {
        Admin admin = getAdminEntityById(id);
        if (admin == null) {
            return false;
        }
        QueryWrapper<AdminMenu> adminMenuQuery = new QueryWrapper<>();
        adminMenuQuery.eq("admin_id", id);
        adminMenuMapper.delete(adminMenuQuery);
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", UUID.fromString(id));
        adminMapper.delete(queryWrapper);
        return true;
    }

    @Override
    public AdminDTO getAdminById(String id) {
        return adminMapper.selectAdminByIdWithUser(id);
    }

    @Override
    public PageResult<AdminDTO> getAdminPage(AdminQueryRequest queryRequest) {
        Page<AdminDTO> page = new Page<>(queryRequest.getCurrent(), queryRequest.getSize());
        IPage<AdminDTO> adminPage = adminMapper.selectAdminPageWithUser(page, queryRequest);
        return PageResult.of(
                adminPage.getRecords(),
                adminPage.getTotal(),
                adminPage.getCurrent(),
                adminPage.getSize()
        );
    }

    @Override
    @Transactional
    public void assignAdminMenus(String adminId, List<String> menuIds) {
        Admin admin = getAdminEntityById(adminId);
        if (admin == null) {
            throw new RuntimeException("管理员不存在");
        }
        QueryWrapper<AdminMenu> deleteQuery = new QueryWrapper<>();
        deleteQuery.eq("admin_id", adminId);
        adminMenuMapper.delete(deleteQuery);
        if (menuIds == null || menuIds.isEmpty()) {
            return;
        }
        long currentTime = System.currentTimeMillis();
        String operatorId = MDC.get("userId");
        for (String menuId : menuIds) {
            AdminMenu adminMenu = new AdminMenu();
            adminMenu.setId(UUID.randomUUID().toString().replace("-", ""));
            adminMenu.setAdminId(adminId);
            adminMenu.setMenuId(menuId);
            adminMenu.setCreateTime(currentTime);
            adminMenu.setUpdateTime(currentTime);
            adminMenu.setCreateUserId(operatorId);
            adminMenu.setUpdateUserId(operatorId);
            adminMenuMapper.insert(adminMenu);
        }
    }

    @Override
    public List<String> getAdminMenuIds(String adminId) {
        Admin admin = getAdminEntityById(adminId);
        if (admin == null) {
            throw new RuntimeException("管理员不存在");
        }
        return adminMenuMapper.selectMenuIdsByAdminId(adminId);
    }

    @Override
    @Transactional
    public void clearAdminMenus(String adminId) {
        Admin admin = getAdminEntityById(adminId);
        if (admin == null) {
            throw new RuntimeException("管理员不存在");
        }
        QueryWrapper<AdminMenu> deleteQuery = new QueryWrapper<>();
        deleteQuery.eq("admin_id", adminId);
        adminMenuMapper.delete(deleteQuery);
    }

    private Admin getAdminEntityById(String id) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", UUID.fromString(id));
        return adminMapper.selectOne(queryWrapper);
    }

    private User getUserById(String id) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", UUID.fromString(id));
        return userMapper.selectOne(queryWrapper);
    }

    private void validateAdminType(String adminType) {
        AdminType.fromCode(adminType);
    }
}
