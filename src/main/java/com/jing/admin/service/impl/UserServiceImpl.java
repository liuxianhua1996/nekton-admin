package com.jing.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jing.admin.core.PageResult;
import com.jing.admin.core.constant.Role;
import com.jing.admin.core.exception.BusinessException;
import com.jing.admin.mapper.UserMapper;
import com.jing.admin.mapper.UserRoleMapper;
import com.jing.admin.model.api.UserRequest;
import com.jing.admin.model.api.UserQueryRequest;
import com.jing.admin.model.domain.User;
import com.jing.admin.model.dto.UserDTO;
import com.jing.admin.model.mapping.UserMapping;
import com.jing.admin.service.UserService;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    @Autowired
    private PasswordEncoder passwordEncoder;

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

    @Override
    public UserDTO createUser(UserRequest request) {
        if (request == null) {
            throw new BusinessException("请求参数不能为空");
        }
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new BusinessException("用户名不能为空");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new BusinessException("密码不能为空");
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new BusinessException("邮箱不能为空");
        }
        if (userMapper.existsByUsername(request.getUsername())) {
            throw new BusinessException("用户名已存在");
        }
        if (userMapper.existsByEmail(request.getEmail())) {
            throw new BusinessException("邮箱已被使用");
        }
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setEnabled(request.getEnabled() == null ? 1 : request.getEnabled());
        long currentTime = System.currentTimeMillis();
        String operatorId = MDC.get("userId");
        if (operatorId != null && operatorId.isBlank()) {
            operatorId = null;
        }
        user.setCreateTime(currentTime);
        user.setUpdateTime(currentTime);
        user.setCreateUserId(operatorId);
        user.setUpdateUserId(operatorId);
        userMapper.insertUser(user);
        return UserMapping.INSTANCE.toDTO(user);
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
