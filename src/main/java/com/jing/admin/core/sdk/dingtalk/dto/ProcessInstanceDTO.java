package com.jing.admin.core.sdk.dingtalk.dto;

import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
/**
 * ProcessInstanceDTO -
 *
 * @author zhicheng
 * @version 1.0
 * @see
 * @since 2025/12/10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessInstanceDTO {

    /**
     * 创建时间 (建议用String接收，格式如 2021-05-01)
     */
    private String createTimeGMT;

    /**
     * 修改时间
     */
    private String modifiedTimeGMT;

    /**
     * 流程实例ID
     */
    private String processInstanceId;

    /**
     * 审批结果 (agree, refuse 等)
     */
    private String approvedResult;

    /**
     * 表单模板ID
     */
    private String formUuid;

    /**
     * 流程编码
     */
    private String processCode;

    /**
     * 表单数据
     */
    private JSONObject data;

    /**
     * 标题
     */
    private String title;

    /**
     * 实例状态 (RUNNING, COMPLETED, TERMINATED)
     */
    private String instanceStatus;

    /**
     * 发起人信息
     */
    private UserInfo originator;

    /**
     * 执行人/审批人列表 (注意：这是一个数组)
     */
    private List<UserInfo> actionExecutor;

    // ==========================================
    // 内部类定义 (对应 JSON 中的嵌套对象)
    // ==========================================

    /**
     * 用户信息结构 (发起人和执行人共用)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        /**
         * 部门名称
         */
        private String deptName;

        /**
         * 用户ID (钉钉userId)
         */
        private String userId;

        /**
         * 邮箱
         */
        private String email;

        /**
         * 姓名详情对象
         */
        private NameInfo name;
    }

    /**
     * 姓名详情结构
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NameInfo {
        /**
         * 英文名/拼音
         */
        private String nameInEnglish;

        /**
         * 中文名
         */
        private String nameInChinese;

        /**
         * 类型 (如 i18n)
         */
        private String type;
    }
}
