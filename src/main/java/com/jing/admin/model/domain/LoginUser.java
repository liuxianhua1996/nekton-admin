package com.jing.admin.model.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.jing.admin.core.constant.Role;
import com.jing.admin.model.dto.TenantUseDTO;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lxh
 * @date 2025/9/19
 **/
@Data
public class LoginUser extends User implements UserDetails {
    private String username;


    private String email;

    private int enabled;
    private String selectedTenant;
    private List<TenantUseDTO> tenant;
    private String uuid;
    private Collection<Role> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.getRoles() == null ? new ArrayList<>() : this.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
    }

    /**
     * 检查用户是否拥有指定角色
     * @param role 角色枚举
     * @return 如果用户拥有该角色，返回true
     */
    public boolean hasRole(Role role) {
        return this.getRoles().contains(role);
    }

    /**
     * 检查用户是否拥有指定角色或更高级别的角色
     * @param role 角色枚举
     * @return 如果用户拥有该角色或更高级别的角色，返回true
     */
    public boolean hasRoleOrHigher(Role role) {
        return this.getRoles().stream().anyMatch(userRole -> userRole.isHigherThanOrEqual(role));
    }

    /**
     * 获取用户的最高级别角色
     * @return 最高级别的角色
     */
    public Role getHighestRole() {
        return this.getRoles().stream()
                .min((r1, r2) -> Integer.compare(r1.getLevel(), r2.getLevel()))
                .orElse(Role.GUEST);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.getEnabled() == 1;
    }
}
