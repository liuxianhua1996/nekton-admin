package com.jing.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jing.admin.model.domain.Tenant;
import com.jing.admin.model.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

/**
 * 用户Mapper接口
 * 使用MyBatis-Plus进行数据访问
 */
@Mapper
public interface TenantMapper extends BaseMapper<Tenant> {

}