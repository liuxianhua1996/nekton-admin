package com.jing.admin.service;

import com.jing.admin.config.JwtTokenUtil;
import com.jing.admin.core.constant.Role;
import com.jing.admin.model.domain.LoginUser;
import com.jing.admin.model.domain.User;
import com.jing.admin.model.dto.MenuDTO;
import com.jing.admin.model.dto.UserDTO;
import com.jing.admin.model.mapping.UserMapping;
import com.jing.admin.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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
    private MenuService menuService;

    public Map<String, Object> authenticate(String username, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = userRepository.findByUsername(username).orElse(null);
            LoginUser loginUser = UserMapping.INSTANCE.toLoginUser(user);
            String accessToken = jwtTokenUtil.generateToken(loginUser);
            String refreshToken = jwtTokenUtil.generateRefreshToken(loginUser);
            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("user", UserMapping.INSTANCE.toDTO(user));
            
            return response;
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("用户名或密码不正确");
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
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setRoles(Collections.singleton(role));
        user.setCreateTime(System.currentTimeMillis());
        user.setUpdateTime(user.getCreateTime());
        
        return userRepository.save(user);
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
        
        if (!user.getRoles().contains(role)) {
            user.getRoles().add(role);
            return userRepository.save(user);
        }
        
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
        
        if (user.getRoles().contains(role)) {
            user.getRoles().remove(role);
            return userRepository.save(user);
        }
        
        return user;
    }

    /**
     * 根据用户角色获取菜单树
     * @param user 用户
     * @return 菜单树结构
     */
    public List<MenuDTO> getMenusByUser(LoginUser user) {
        // 获取用户的角色，如果有管理员角色则直接返回所有菜单
        Collection<Role> roles = user.getRoles();
        if (roles.contains(Role.ADMIN)) {
            return menuService.getMenuTreeByRole("ADMIN");
        }
        
        // 如果用户有多个角色，合并所有角色的菜单
        List<MenuDTO> allMenus = roles.stream()
                .map(role -> menuService.getMenuTreeByRole(role.name()))
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
        
        return allMenus;
    }
}