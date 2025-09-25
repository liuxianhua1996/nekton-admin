package com.jing.admin.model.dto;

import lombok.Data;

import java.util.List;

/**
 * 菜单数据传输对象
 */
@Data
public class MenuDTO {
    private String id;
    private String code;
    private String name;
    private String path;
    private List<MenuDTO> routes;
}