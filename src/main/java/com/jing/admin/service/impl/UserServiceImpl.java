package com.jing.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jing.admin.core.PageResult;
import com.jing.admin.core.constant.Role;
import com.jing.admin.mapper.UserMapper;
import com.jing.admin.mapper.UserRoleMapper;
import com.jing.admin.model.api.UserQueryRequest;
import com.jing.admin.model.domain.User;
import com.jing.admin.model.dto.UserDTO;
import com.jing.admin.model.mapping.UserMapping;
import com.jing.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private RoleService roleService;

    @Override
    public PageResult<UserDTO> getUserPage(UserQueryRequest queryRequest) {
        Page<User> page = new Page<>(queryRequest.getCurrent(), queryRequest.getSize());
        IPage<User> userPage = userMapper.selectUserPage(page, queryRequest);
        List<UserDTO> records = userPage.getRecords().stream()
                .map(this::toUserDTOWithRoles)
                .toList();
        return PageResult.of(
                records,
                userPage.getTotal(),
                userPage.getCurrent(),
                userPage.getSize()
        );
    }

    @Override
    public UserDTO getUserById(String id) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", UUID.fromString(id));
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            return null;
        }
        return toUserDTOWithRoles(user);
    }

    private UserDTO toUserDTOWithRoles(User user) {
        UserDTO dto = UserMapping.INSTANCE.toDTO(user);
        List<String> roleIds = userRoleMapper.selectRoleIdsByUserId(user.getId());
        if (roleIds == null || roleIds.isEmpty()) {
            dto.setRoles(List.of());
            return dto;
        }
        List<Role> roles = new ArrayList<>();
        // 根据角色ID获取角色名称
        for (String roleId : roleIds) {
            // 从角色表查询角色名称
            com.jing.admin.model.domain.Role role = roleService.getById(roleId);
            if (role != null) {
                try {
                    roles.add(Role.fromName(role.getName()));
                } catch (Exception ignored) {
                }
            }
        }
        dto.setRoles(roles);
        return dto;
    }
}
