package com.jing.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jing.admin.core.cache.RoleMenuCache;
import com.jing.admin.mapper.RoleMapper;
import com.jing.admin.mapper.RoleMenuMapper;
import com.jing.admin.mapper.UserRoleMapper;
import com.jing.admin.model.api.RoleRequest;
import com.jing.admin.model.domain.Role;
import com.jing.admin.model.domain.RoleMenu;
import com.jing.admin.model.dto.RoleDTO;
import com.jing.admin.model.mapping.RoleMapping;
import org.slf4j.MDC;
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
    
    @Autowired
    private RoleMenuCache roleMenuCache;
    
    /**
     * 创建角色
     * @param role 角色信息
     * @return 创建的角色
     */
    public RoleDTO createRole(RoleRequest role) {
        Role entity = RoleMapping.INSTANCE.toEntity(role);
        if (entity.getId() == null || entity.getId().isEmpty()) {
            entity.setId(UUID.randomUUID().toString());
        }
        long currentTime = System.currentTimeMillis();
        String userId = MDC.get("userId");
        entity.setCreateTime(currentTime);
        entity.setUpdateTime(currentTime);
        entity.setCreateUserId(userId);
        entity.setUpdateUserId(userId);
        roleMapper.insert(entity);
        return RoleMapping.INSTANCE.toDTO(entity);
    }
    
    /**
     * 更新角色
     * @param id 角色ID
     * @param role 角色信息
     * @return 更新的角色
     */
    public RoleDTO updateRole(String id, RoleRequest role) {
        Role existingRole = getRoleEntityById(id);
        if (existingRole == null) {
            throw new RuntimeException("角色不存在");
        }
        
        // 更新角色信息
        String oldRoleName = existingRole.getName();
        String newRoleName = role.getName();
        if (!oldRoleName.equals(newRoleName) && existsByName(newRoleName)) {
            throw new RuntimeException("角色名称已存在");
        }
        existingRole.setName(newRoleName);
        existingRole.setDescription(role.getDescription());
        existingRole.setUpdateTime(System.currentTimeMillis());
        existingRole.setUpdateUserId(MDC.get("userId"));

        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", UUID.fromString(id));
        roleMapper.update(existingRole, queryWrapper);
        if (!oldRoleName.equals(newRoleName)) {
            roleMenuCache.setRoleMenus(oldRoleName, null);
            roleMenuCache.setRoleMenuTree(oldRoleName, null);
        }
        return RoleMapping.INSTANCE.toDTO(existingRole);
    }
    
    /**
     * 删除角色
     * @param id 角色ID
     */
    @Transactional
    public void deleteRole(String id) {
        // 先获取角色信息
        Role role = getRoleEntityById(id);
        if (role == null) {
            return;
        }
        
        // 删除角色
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", UUID.fromString(id));
        roleMapper.delete(queryWrapper);
        // 删除角色菜单关联
        roleMenuMapper.deleteByRoleId(id);
        // 删除用户角色关联
        userRoleMapper.deleteByRoleId(id);
        
        // 更新缓存：清除该角色的缓存
        roleMenuCache.setRoleMenus(role.getName(), null);
        roleMenuCache.setRoleMenuTree(role.getName(), null);
    }
    
    /**
     * 根据ID获取角色
     * @param id 角色ID
     * @return 角色信息
     */
    public RoleDTO getRoleById(String id) {
        Role role = getRoleEntityById(id);
        if (role == null) {
            return null;
        }
        return RoleMapping.INSTANCE.toDTO(role);
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
     * 根据名称获取角色
     * @param name 角色名称
     * @return 角色信息
     */
    public Role getByName(String name) {
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", name);
        return roleMapper.selectOne(queryWrapper);
    }
    
    /**
     * 根据ID获取角色
     * @param id 角色ID
     * @return 角色信息
     */
    public Role getById(String id) {
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", UUID.fromString(id));
        return roleMapper.selectOne(queryWrapper);
    }
    
    /**
     * 获取所有角色
     * @return 角色列表
     */
    public List<Role> getAllRoles() {
        return roleMapper.selectList(null);
    }

    public List<RoleDTO> getRoleList() {
        return roleMapper.selectList(null).stream()
                .map(RoleMapping.INSTANCE::toDTO)
                .toList();
    }
    
    /**
     * 检查角色名称是否存在
     * @param name 角色名称
     * @return 是否存在
     */
    public boolean existsByName(String name) {
        return roleMapper.existsByName(name);
    }

    @Transactional
    public void assignRoleMenus(String roleId, List<String> menuIds) {
        Role role = getRoleEntityById(roleId);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        String roleName = role.getName();
        roleMenuMapper.deleteByRoleId(roleId);
        if (menuIds == null || menuIds.isEmpty()) {
            roleMenuCache.setRoleMenus(roleName, null);
            roleMenuCache.setRoleMenuTree(roleName, null);
            return;
        }
        long currentTime = System.currentTimeMillis();
        String userId = MDC.get("userId");
        for (String menuId : menuIds) {
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setId(UUID.randomUUID().toString().replace("-", ""));
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenu.setCreateTime(currentTime);
            roleMenu.setUpdateTime(currentTime);
            roleMenu.setCreateUserId(userId);
            roleMenu.setUpdateUserId(userId);
            roleMenuMapper.insert(roleMenu);
        }
        roleMenuCache.setRoleMenus(roleName, null);
        roleMenuCache.setRoleMenuTree(roleName, null);
    }

    public List<String> getRoleMenuIds(String roleId) {
        Role role = getRoleEntityById(roleId);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        return roleMenuMapper.selectMenuIdsByRoleId(roleId);
    }

    @Transactional
    public void clearRoleMenus(String roleId) {
        Role role = getRoleEntityById(roleId);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        String roleName = role.getName();
        roleMenuMapper.deleteByRoleId(roleId);
        roleMenuCache.setRoleMenus(roleName, null);
        roleMenuCache.setRoleMenuTree(roleName, null);
    }

    public Role getRoleEntityById(String id) {
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", UUID.fromString(id));
        return roleMapper.selectOne(queryWrapper);
    }
}
