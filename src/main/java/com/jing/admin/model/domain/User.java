package com.jing.admin.model.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jing.admin.core.constant.Role;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

@Data
@TableName("tb_users")
public class User implements UserDetails {

    @TableId
    private Long id;

    private String username;

    private String password;

    private String email;

    private Boolean enabled = true;

    // 不映射到数据库字段，用于存储用户角色
    @TableField(exist = false)
    private Collection<Role> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
    }

    /**
     * 检查用户是否拥有指定角色
     * @param role 角色枚举
     * @return 如果用户拥有该角色，返回true
     */
    public boolean hasRole(Role role) {
        return roles.contains(role);
    }

    /**
     * 检查用户是否拥有指定角色或更高级别的角色
     * @param role 角色枚举
     * @return 如果用户拥有该角色或更高级别的角色，返回true
     */
    public boolean hasRoleOrHigher(Role role) {
        return roles.stream().anyMatch(userRole -> userRole.isHigherThanOrEqual(role));
    }

    /**
     * 获取用户的最高级别角色
     * @return 最高级别的角色
     */
    public Role getHighestRole() {
        return roles.stream()
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
        return enabled != null ? enabled : false;
    }
}