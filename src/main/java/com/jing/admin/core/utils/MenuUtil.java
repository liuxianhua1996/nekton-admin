package com.jing.admin.core.utils;

import com.jing.admin.model.domain.Menu;
import com.jing.admin.model.dto.MenuDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单工具类
 */
public class MenuUtil {

    /**
     * 将菜单列表转换为菜单树结构
     * @param menus 菜单列表
     * @return 菜单树结构
     */
    public static List<MenuDTO> buildMenuTree(List<Menu> menus) {
        // 获取顶级菜单（没有父级ID的菜单）
        List<Menu> rootMenus = menus.stream()
                .filter(menu -> menu.getParentCode() == null || menu.getParentCode().isEmpty())
                .sorted((m1, m2) -> {
                    int order1 = m1.getSortOrder();
                    int order2 = m2.getSortOrder();
                    return Integer.compare(order1, order2);
                })
                .collect(Collectors.toList());

        // 递归构建菜单树
        return rootMenus.stream()
                .map(menu -> buildMenuNode(menu, menus))
                .collect(Collectors.toList());
    }

    /**
     * 递归构建菜单节点
     * @param menu 当前菜单
     * @param allMenus 所有菜单列表
     * @return 菜单DTO
     */
    private static MenuDTO buildMenuNode(Menu menu, List<Menu> allMenus) {
        MenuDTO menuDTO = new MenuDTO();
        menuDTO.setId(menu.getId());
        menuDTO.setCode(menu.getCode());
        menuDTO.setName(menu.getName());
        menuDTO.setPath(menu.getPath());
        menuDTO.setSortOrder(menu.getSortOrder());

        // 查找子菜单
        List<Menu> children = allMenus.stream()
                .filter(m -> menu.getCode().equals(m.getParentCode()))
                .sorted((m1, m2) -> {
                    int order1 = m1.getSortOrder();
                    int order2 = m2.getSortOrder();
                    return Integer.compare(order1, order2);
                })
                .collect(Collectors.toList());

        // 递归构建子菜单
        if (!children.isEmpty()) {
            List<MenuDTO> childDTOs = new ArrayList<>();
            for (Menu child : children) {
                childDTOs.add(buildMenuNode(child, allMenus));
            }
            menuDTO.setRoutes(childDTOs);
        }

        return menuDTO;
    }
}