package com.jing.admin.model.dto;

import com.jing.admin.core.constant.Role;
import com.jing.admin.model.dto.TenantUseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;

/**
 * @author lxh
 * @date 2025/9/18
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String id;

    private String uuid;

    private String username;


    private String email;

    private int enabled;

    private Collection<Role> roles;
    private List<TenantUseDTO> tenant;
    private String selectedTenant;
}
