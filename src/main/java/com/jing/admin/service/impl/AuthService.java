package com.jing.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jing.admin.config.JwtTokenUtil;
import com.jing.admin.config.LoginUserUtil;
import com.jing.admin.core.constant.Role;
import com.jing.admin.core.exception.BusinessException;
import com.jing.admin.core.tenant.TenantContextHolder;
import com.jing.admin.model.domain.LoginUser;
import com.jing.admin.model.domain.User;
import com.jing.admin.model.dto.MenuDTO;
import com.jing.admin.model.dto.TenantUseDTO;
import com.jing.admin.model.dto.UserDTO;
import com.jing.admin.model.mapping.UserMapping;
import com.jing.admin.repository.TenantUserRepository;
import com.jing.admin.repository.UserRepository;
import com.jing.admin.mapper.UserRoleMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TenantUserRepository tenantUserRepository;
    @Autowired
    private MenuService menuService;
    @Autowired
    private com.jing.admin.mapper.TenantUserMapper tenantUserMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    LoginUserUtil loginUserUtil;
    @Autowired
    private RoleService roleService;

    public Map<String, Object> authenticate(String username, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = userRepository.findByUsername(username).orElse(null);

            List<TenantUseDTO> tenantUsers = this.tenantUserRepository.queryUserTenants(user.getId());
            LoginUser loginUser = UserMapping.INSTANCE.toLoginUser(user);
            loginUser.setUuid(user.getId());
            loginUser.setTenant(tenantUsers);
            String accessToken = jwtTokenUtil.generateToken(loginUser);
            String refreshToken = jwtTokenUtil.generateRefreshToken(loginUser);
            UserDTO userDTO = UserMapping.INSTANCE.toDTO(user);
            userDTO.setTenant(tenantUsers);
            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("user", userDTO);
            
            return response;
        } catch (BadCredentialsException e) {
            throw new BusinessException("用户名或密码不正确");
        }
    }

    /**
     * 刷新访问TOKEN
     * @param refreshToken 刷新TOKEN
     * @return 新的访问TOKEN和刷新TOKEN
     */
    public Map<String, Object> refreshToken(String refreshToken) {
        // 验证刷新TOKEN是否有效
        if (!jwtTokenUtil.validateToken(refreshToken)) {
            throw new BadCredentialsException("刷新TOKEN无效");
        }

        // 从刷新TOKEN中获取用户名
        String username = jwtTokenUtil.getUsernameFromToken(refreshToken);
        
        // 获取用户信息
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            throw new BadCredentialsException("用户不存在");
        }
        
        // 生成新的访问TOKEN和刷新TOKEN
        LoginUser loginUser = UserMapping.INSTANCE.toLoginUser(user);
        loginUser.setUuid(user.getId());
        String newAccessToken = jwtTokenUtil.generateToken(loginUser);
        String newRefreshToken = jwtTokenUtil.generateRefreshToken(loginUser);
        
        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", newAccessToken);
        response.put("refreshToken", newRefreshToken);
        response.put("user", UserMapping.INSTANCE.toDTO(user));
        
        return response;
    }

    public User register(String username, String password, String email) {
        return register(username, password, email, Role.USER);
    }

    public User register(String username, String password, String email, Role role) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("邮箱已被使用");
        }
        
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setCreateTime(System.currentTimeMillis());
        user.setUpdateTime(user.getCreateTime());
        
        return userRepository.saveUser(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * 为用户添加角色
     * @param username 用户名
     * @param role 要添加的角色
     * @return 更新后的用户
     */
    public User addRole(String username, Role role) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + username));
        
        return user;
    }

    /**
     * 移除用户的角色
     * @param username 用户名
     * @param role 要移除的角色
     * @return 更新后的用户
     */
    public User removeRole(String username, Role role) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + username));
        
        return user;
    }

    /**
     * 根据用户角色获取菜单树
     * @param user 用户
     * @return 菜单树结构
     */
    public List<MenuDTO> getMenusByUser(LoginUser user) {
        String adminUserId = user.getId() == null || user.getId().isBlank()
                ? user.getUuid()
                : user.getId();
        return menuService.getMenuTreeByAdmin(adminUserId);
    }
    
    /**
     * 验证用户是否有指定租户的访问权限
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @return 如果用户有权限访问该租户，返回true，否则返回false
     */
    public boolean validateUserTenantAccess(String userId, String tenantId) {
        return tenantUserMapper.checkUserTenantAccess(userId, tenantId);
    }
    
    /**
     * 确认用户租户并生成包含租户信息的JWT
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @return 包含新JWT的响应
     */
    public Map<String, Object> confirmTenant(String userId, String tenantId) {
        // 验证用户是否有权限访问该租户
        if (!validateUserTenantAccess(userId, tenantId)) {
            throw new BadCredentialsException("用户无权访问该租户");
        }
        // 创建LoginUser对象
        LoginUser user = loginUserUtil.getLoginUser(MDC.get("jwtToken"));
        user.setSelectedTenant(tenantId);
        String uuid = user.getUuid() == null || user.getUuid().isBlank() ? userId : user.getUuid();
        String tenantUserId = getTenantUserId(uuid, tenantId);
        user.setUuid(uuid);
        user.setId(tenantUserId);
        user.setRoles(getUserRoles(tenantUserId, tenantId));
        // 生成包含租户信息的新JWT
        String accessToken = jwtTokenUtil.generateTokenWithTenant(user, tenantId);
        String refreshToken = jwtTokenUtil.generateRefreshTokenWithTenant(user, tenantId);
        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);
        
        return response;
    }

    private String getTenantUserId(String userId, String tenantId) {
        String currentTenantId = TenantContextHolder.getTenantId();
        try {
            TenantContextHolder.setTenantId(tenantId);
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("uuid", userId);
            User tenantUser = userRepository.getOne(queryWrapper);
            if (tenantUser == null) {
                throw new BusinessException("租户用户不存在");
            }
            return tenantUser.getId();
        } finally {
            if (currentTenantId == null) {
                TenantContextHolder.clear();
            } else {
                TenantContextHolder.setTenantId(currentTenantId);
            }
        }
    }

    private List<Role> getUserRoles(String userId, String tenantId) {
        String currentTenantId = TenantContextHolder.getTenantId();
        try {
            TenantContextHolder.setTenantId(tenantId);
            List<String> roleIds = userRoleMapper.selectRoleIdsByUserId(userId);
            if (roleIds == null || roleIds.isEmpty()) {
                return List.of();
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
            return roles;
        } finally {
            if (currentTenantId == null) {
                TenantContextHolder.clear();
            } else {
                TenantContextHolder.setTenantId(currentTenantId);
            }
        }
    }
}
