package com.jing.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jing.admin.model.domain.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;

import java.util.List;
import java.util.Optional;

/**
 * 角色Mapper接口
 * 使用MyBatis-Plus进行数据访问
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    
    /**
     * 根据角色名称查找角色
     * @param name 角色名称
     * @return 角色对象
     */
    @Select("SELECT * FROM tb_roles WHERE name = #{name}")
    Optional<Role> findByName(String name);
    
    /**
     * 检查角色名称是否存在
     * @param name 角色名称
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM tb_roles WHERE name = #{name}")
    boolean existsByName(String name);
    
    /**
     * 获取所有角色
     * @return 角色列表
     */
    @Select("SELECT * FROM tb_roles")
    List<Role> findAll();

    @Insert("INSERT INTO tb_roles (name, description, create_time, update_time, create_user_id, update_user_id) " +
            "VALUES (#{name}, #{description}, #{createTime}, #{updateTime}, " +
            "CAST(#{createUserId} AS UUID), CAST(#{updateUserId} AS UUID))")
    int insertRole(Role role);
}
