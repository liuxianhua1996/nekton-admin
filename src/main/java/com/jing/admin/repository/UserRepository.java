package com.jing.admin.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jing.admin.mapper.UserMapper;
import com.jing.admin.mapper.UserRoleMapper;
import com.jing.admin.model.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 用户Repository类
 * 使用MyBatis-Plus的UserMapper进行数据访问
 */
@Repository
@Slf4j
public class UserRepository extends ServiceImpl<UserMapper, User> {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;

    public UserRepository(UserMapper userMapper, UserRoleMapper userRoleMapper) {
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
    }

    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(userMapper.selectUserWithRolesByUsername(username));
    }

    public Optional<User> findByEmail(String email) {
        return userMapper.findByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return userMapper.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userMapper.existsByEmail(email);
    }

    @Transactional
    public User saveUser(User user) {
        if (user.getId() == null) {
            // 新增用户
            userMapper.insert(user);
        } else {
            // 更新用户
            userMapper.updateById(user);
        }
        return user;
    }

    public Optional<User> findById(String id) {
        return Optional.ofNullable(userMapper.selectUserWithRoles(id));
    }

    /**
     * 根据ID列表批量查询用户
     *
     * @param ids 用户ID列表
     * @return 用户列表
     */
    public List<User> findAllByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return userMapper.selectBatchIds(ids);
    }
}