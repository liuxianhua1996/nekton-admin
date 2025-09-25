package com.jing.admin.model.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单实体类
 * @author lxh
 * @date 2025/9/19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_menu")
public class Menu extends Base {
    /**
     * 菜单编码
     */
    private String code;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 路由路径
     */
    private String path;
    /**
     * 父级菜单code
     */
    private String parentCode;

    private int sortOrder;
}