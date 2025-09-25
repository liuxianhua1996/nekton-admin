package com.jing.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jing.admin.mapper.RoleMapper;
import com.jing.admin.mapper.RoleMenuMapper;
import com.jing.admin.mapper.UserRoleMapper;
import com.jing.admin.model.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 角色服务实现类
 */
@Service
public class RoleService {
    
    @Autowired
    private RoleMapper roleMapper;
    
    @Autowired
    private RoleMenuMapper roleMenuMapper;
    
    @Autowired
    private UserRoleMapper userRoleMapper;
    
    /**
     * 创建角色
     * @param role 角色信息
     * @return 创建的角色
     */
    public Role createRole(Role role) {
        if (role.getId() == null || role.getId().isEmpty()) {
            role.setId(UUID.randomUUID().toString().replace("-", ""));
        }
        long currentTime = System.currentTimeMillis();
        role.setCreateTime(currentTime);
        role.setUpdateTime(currentTime);
        roleMapper.insert(role);
        return role;
    }
    
    /**
     * 更新角色
     * @param id 角色ID
     * @param role 角色信息
     * @return 更新的角色
     */
    public Role updateRole(String id, Role role) {
        Role existingRole = roleMapper.selectById(id);
        if (existingRole == null) {
            throw new RuntimeException("角色不存在");
        }
        
        // 更新角色信息
        existingRole.setName(role.getName());
        existingRole.setDescription(role.getDescription());
        existingRole.setUpdateTime(System.currentTimeMillis());
        
        roleMapper.updateById(existingRole);
        return existingRole;
    }
    
    /**
     * 删除角色
     * @param id 角色ID
     */
    @Transactional
    public void deleteRole(String id) {
        // 删除角色
        roleMapper.deleteById(id);
        // 删除角色菜单关联
        roleMenuMapper.deleteByRole(id);
        // 删除用户角色关联
        userRoleMapper.deleteByRoleId(id);
    }
    
    /**
     * 根据ID获取角色
     * @param id 角色ID
     * @return 角色信息
     */
    public Role getRoleById(String id) {
        return roleMapper.selectById(id);
    }
    
    /**
     * 根据名称获取角色
     * @param name 角色名称
     * @return 角色信息
     */
    public Role getRoleByName(String name) {
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", name);
        return roleMapper.selectOne(queryWrapper);
    }
    
    /**
     * 获取所有角色
     * @return 角色列表
     */
    public List<Role> getAllRoles() {
        return roleMapper.selectList(null);
    }
    
    /**
     * 检查角色名称是否存在
     * @param name 角色名称
     * @return 是否存在
     */
    public boolean existsByName(String name) {
        return roleMapper.existsByName(name);
    }
}