package com.jing.admin.controller;

import com.jing.admin.model.Role;
import com.jing.admin.model.User;
import com.jing.admin.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        
        Map<String, Object> response = authService.authenticate(username, password);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> registerRequest) {
        String username = registerRequest.get("username");
        String password = registerRequest.get("password");
        String email = registerRequest.get("email");
        
        User user = authService.register(username, password, email);
        return ResponseEntity.ok(user);
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
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        return authService.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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