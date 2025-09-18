package com.jing.admin.model.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Data
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    private boolean enabled = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
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
        return enabled;
    }
}