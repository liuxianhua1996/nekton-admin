package com.jing.admin.model.dto;

import com.jing.admin.core.constant.Role;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lxh
 * @date 2025/9/18
 **/
@Data
public class UserDTO {
    private String id;

    private String username;


    private String email;

    private int enabled;

    private Collection<Role> roles;


    private List tenant;
}
