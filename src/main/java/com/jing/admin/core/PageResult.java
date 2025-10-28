package com.jing.admin.core;

import lombok.Data;

import java.util.List;

/**
 * 分页响应结果类
 * @author lxh
 * @date 2025/9/19
 */
@Data
public class PageResult<T> {
    /**
     * 数据列表
     */
    private List<T> records;
    
    /**
     * 总记录数
     */
    private Long total;
    
    /**
     * 当前页码
     */
    private Long current;
    
    /**
     * 每页显示条数
     */
    private Long size;
    
    /**
     * 总页数
     */
    private Long pages;
    
    public PageResult() {}
    
    public PageResult(List<T> records, Long total, Long current, Long size) {
        this.records = records;
        this.total = total;
        this.current = current;
        this.size = size;
        this.pages = (total + size - 1) / size; // 计算总页数
    }
    
    /**
     * 创建分页结果
     * @param records 数据列表
     * @param total 总记录数
     * @param current 当前页码
     * @param size 每页显示条数
     * @return 分页结果对象
     */
    public static <T> PageResult<T> of(List<T> records, Long total, Long current, Long size) {
        return new PageResult<>(records, total, current, size);
    }
}