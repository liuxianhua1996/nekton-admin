package com.jing.admin.model.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jing.admin.model.domain.Base;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lxh
 * @date 2025/10/30
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenantUseDTO {
    private String tenantId;

    private String tenantName;
}
