package com.jing.admin.config;

import com.jing.admin.model.domain.Role;
import com.jing.admin.model.dto.UserDTO;
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
    public UserDTO getLoginUser(String token){
        UserDTO user = new UserDTO();
        user.setUsername(jwtTokenUtil.getClaimFromToken(token, cl -> cl.get("username", String.class)));
        user.setId(jwtTokenUtil.getClaimFromToken(token, cl -> cl.get("id", Long.class)));
        Collection<String> roleNames = jwtTokenUtil.getClaimFromToken(token, cl -> (Collection<String>) cl.get("roles"));
        List<Role> roles = roleNames.stream()
                .map(Role::fromName) // 使用你定义的 Role.fromName(String) 方法
                .collect(Collectors.toList());
        user.setRoles(roles);
        return  user;
    }
}
