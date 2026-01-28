package com.jing.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jing.admin.model.api.AdminQueryRequest;
import com.jing.admin.model.domain.Admin;
import com.jing.admin.model.dto.AdminDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminMapper extends BaseMapper<Admin> {
    @Select("SELECT * FROM tb_admins WHERE user_id = #{userId}")
    Admin selectByUserId(@Param("userId") String userId);

    IPage<AdminDTO> selectAdminPageWithUser(
            Page<AdminDTO> page,
            @Param("query") AdminQueryRequest queryRequest
    );

    AdminDTO selectAdminByIdWithUser(@Param("id") String id);
}
