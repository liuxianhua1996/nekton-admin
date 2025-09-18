package com.jing.admin.service;

import com.jing.admin.config.JwtTokenUtil;
import com.jing.admin.model.domain.Role;
import com.jing.admin.model.domain.User;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Map<String, Object> authenticate(String username, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = userRepository.findByUsername(username).orElse(null);
            UserDTO userDTO = UserMapping.INSTANCE.toDTO(user);
            String token = jwtTokenUtil.generateToken(userDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", userDTO);
            
            return response;
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("用户名或密码不正确");
        }
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
}