package com.jing.admin.model.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jing.admin.model.domain.Base;
import lombok.Data;

/**
 * @author lxh
 * @date 2025/10/30
 **/
@Data
public class TenantUseDTO {
    private String tenantId;

    private String tenantName;
}
