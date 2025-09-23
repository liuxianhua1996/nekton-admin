package com.jing.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/all")
    public ResponseEntity<?> allAccess() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "公开内容，任何人都可以访问");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<?> userAccess() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "用户内容，需要USER、ADMIN或MANAGER角色");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adminAccess() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "管理员内容，需要ADMIN角色");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/manager")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> managerAccess() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "经理内容，需要MANAGER或ADMIN角色");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/operator")
    @PreAuthorize("hasRole('OPERATOR') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> operatorAccess() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "操作员内容，需要OPERATOR、MANAGER或ADMIN角色");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/guest")
    @PreAuthorize("hasAnyRole('GUEST', 'USER', 'OPERATOR', 'MANAGER', 'ADMIN')")
    public ResponseEntity<?> guestAccess() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "访客内容，任何角色都可以访问");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/hierarchy")
    @PreAuthorize("hasAnyRole('USER', 'OPERATOR', 'MANAGER', 'ADMIN')")
    public ResponseEntity<?> hierarchyAccess() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "角色层次测试，根据用户角色显示不同信息");
        response.put("username", username);
        
        // 这里可以根据用户的角色级别返回不同的信息
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            response.put("level", "管理员");
            response.put("access", "可以访问所有功能");
        } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"))) {
            response.put("level", "经理");
            response.put("access", "可以访问管理功能");
        } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_OPERATOR"))) {
            response.put("level", "操作员");
            response.put("access", "可以访问操作功能");
        } else {
            response.put("level", "普通用户");
            response.put("access", "可以访问基本功能");
        }
        
        return ResponseEntity.ok(response);
    }
}