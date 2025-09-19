package com.jing.admin.controller;

import com.jing.admin.config.LoginUserUtil;
import com.jing.admin.core.HttpResult;
import com.jing.admin.core.constant.Role;
import com.jing.admin.model.domain.User;
import com.jing.admin.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private LoginUserUtil loginUserUtil;

    @PostMapping("/login")
    public HttpResult<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        
        Map<String, Object> response = authService.authenticate(username, password);
        return HttpResult.success(response);
    }

    @PostMapping("/register")
    public HttpResult<User> register(@RequestBody Map<String, String> registerRequest) {
        String username = registerRequest.get("username");
        String password = registerRequest.get("password");
        String email = registerRequest.get("email");
        
        User user = authService.register(username, password, email);
        return HttpResult.success(user);
    }

    @PostMapping("/register-with-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registerWithRole(@RequestBody Map<String, String> registerRequest) {
        String username = registerRequest.get("username");
        String password = registerRequest.get("password");
        String email = registerRequest.get("email");
        String roleName = registerRequest.get("role");
        
        Role role = Role.fromName(roleName);
        User user = authService.register(username, password, email, role);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/me")
    public HttpResult getCurrentUser(HttpServletRequest request) {
        String jwtToken = request.getHeader("Authorization").substring(7);
        return HttpResult.success(loginUserUtil.getLoginUser(jwtToken));
    }

    @PostMapping("/add-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addRole(@RequestBody Map<String, String> roleRequest) {
        String username = roleRequest.get("username");
        String roleName = roleRequest.get("role");
        
        Role role = Role.fromName(roleName);
        User user = authService.addRole(username, role);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/remove-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> removeRole(@RequestBody Map<String, String> roleRequest) {
        String username = roleRequest.get("username");
        String roleName = roleRequest.get("role");
        
        Role role = Role.fromName(roleName);
        User user = authService.removeRole(username, role);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/roles")
    public ResponseEntity<?> getAllRoles() {
       List roles =  Arrays.stream(Role.values())
                .map(role -> Map.of(
                        "name", role.name(),
                        "description", role.getDescription(),
                        "level", role.getLevel()
                ))
                .toList();
        
        return ResponseEntity.ok(roles);
    }
}