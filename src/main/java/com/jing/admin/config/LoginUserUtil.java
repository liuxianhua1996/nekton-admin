package com.jing.admin.config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.jing.admin.core.constant.Role;
import com.jing.admin.model.domain.LoginUser;
import com.jing.admin.model.dto.TenantUseDTO;
import com.jing.admin.model.dto.UserDTO;
import com.jing.admin.repository.TenantUserRepository;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lxh
 * @date 2025/9/18
 **/
@Component
public class LoginUserUtil {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private TenantUserRepository tenantUserRepository;
    public LoginUser getLoginUser(String token){
        if(token.startsWith("Bearer ")){
            token = token.substring(7);
        }
        LoginUser user = new LoginUser();
        user.setUsername(jwtTokenUtil.getClaimFromToken(token, cl -> cl.get("username", String.class)));
        user.setId(jwtTokenUtil.getClaimFromToken(token, cl -> cl.get("userId", String.class)));
        Collection<String> roleNames = jwtTokenUtil.getClaimFromToken(token, cl -> (Collection<String>) cl.get("roles"));
        List<Role> roles = roleNames.stream()
                .map(Role::fromName) // 使用你定义的 Role.fromName(String) 方法
                .collect(Collectors.toList());
        user.setRoles(roles);
        List<TenantUseDTO> tenantUseDTOS = JSONObject.parseObject( jwtTokenUtil.getClaimFromToken(token, cl -> cl.get("tenant", String.class)),List.class);
        user.setTenant(tenantUseDTOS);
        user.setSelectedTenant(MDC.get("tenantId"));
        return  user;
    }
    
    /**
     * 从JWT中获取租户ID
     * @param token JWT令牌
     * @return 租户ID，如果不存在则返回null
     */
    public String getTenantIdFromToken(String token) {
        if(token.startsWith("Bearer ")){
            token = token.substring(7);
        }
        return jwtTokenUtil.getClaimFromToken(token, cl -> cl.get("tenantId", String.class));
    }
}
