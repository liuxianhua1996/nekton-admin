package com.jing.admin.model.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author lxh
 * @date 2025/9/19
 **/
@Data
public class Base {
    @TableId
    private String id;
    private String tenantId;
    private long createTime;
    private long updateTime;
    private String createUserId;
    private String updateUserId;
}
