package com.jing.admin.controller;

import com.jing.admin.config.LoginUserUtil;
import com.jing.admin.core.HttpResult;
import com.jing.admin.core.constant.Role;
import com.jing.admin.model.domain.LoginUser;
import com.jing.admin.model.domain.User;
import com.jing.admin.model.dto.MenuDTO;
import com.jing.admin.model.mapping.UserMapping;
import com.jing.admin.service.impl.AuthService;
import com.jing.admin.service.impl.MenuService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private LoginUserUtil loginUserUtil;
    @Autowired
    private MenuService menuService;

    @PostMapping("/login")
    public HttpResult<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        
        Map<String, Object> response = authService.authenticate(username, password);
        return HttpResult.success(response);
    }

    @PostMapping("/refresh")
    public HttpResult<Map<String, Object>> refreshToken(@RequestBody Map<String, String> refreshRequest) {
        String refreshToken = refreshRequest.get("refreshToken");
        
        Map<String, Object> response = authService.refreshToken(refreshToken);
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
    public HttpResult getCurrentUser() {
        String jwtToken = MDC.get("jwtToken");
        LoginUser loginUser = loginUserUtil.getLoginUser(jwtToken);
        return HttpResult.success(UserMapping.INSTANCE.toDTO(loginUser));
    }
    /**
     * 获取当前用户的菜单
     * @param request HTTP请求
     * @return 菜单树结构
     */
    @GetMapping("/menus")
    public HttpResult<List<MenuDTO>> getCurrentUserMenus(HttpServletRequest request) {
        try {
            String jwtToken = request.getHeader("Authorization");
            LoginUser user = loginUserUtil.getLoginUser(jwtToken);
            List<MenuDTO> menus = authService.getMenusByUser(user);
            return HttpResult.success(menus);
        } catch (Exception e) {
            e.printStackTrace();
            return HttpResult.error("获取菜单失败: " + e.getMessage());
        }
    }
    
    /**
     * 确认用户租户
     * @param confirmRequest 包含用户ID和租户ID的请求
     * @param request HTTP请求
     * @return 包含新JWT的响应
     */
    @PostMapping("/confirm-tenant")
    public HttpResult<Map<String, Object>> confirmTenant(@RequestBody Map<String, String> confirmRequest, HttpServletRequest request) {
        try {
            // 从请求中获取租户ID
            String tenantId = confirmRequest.get("tenantId");
            LoginUser user = loginUserUtil.getLoginUser(MDC.get("jwtToken"));
            String userId = user.getUuid() == null || user.getUuid().isBlank() ? user.getId() : user.getUuid();
            return HttpResult.success(authService.confirmTenant(userId, tenantId));
        } catch (Exception e) {
            e.printStackTrace();
            return HttpResult.error("确认租户失败: " + e.getMessage());
        }
    }
}
