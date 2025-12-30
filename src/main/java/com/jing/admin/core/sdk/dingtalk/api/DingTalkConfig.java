package com.jing.admin.core.sdk.dingtalk.api;

/**
 * @author lxh
 * @date 2025/11/19
 **/
public class DingTalkConfig {
    private static String baseUrl = "https://api.dingtalk.com";
    private static String baseUrlV1 = "https://oapi.dingtalk.com";
    public static final String getToken = "/v1.0/oauth2/accessToken";

    public static final String getCheckinRecord = "/topapi/checkin/record/get?access_token=${access_token}";

    public static final String getAttendanceListRecord = "/attendance/listRecord?access_token=${access_token}";


    public static  String getBaseUrl() {
        return baseUrl;
    }

    public static String getBaseUrlV1() {
        return baseUrlV1;
    }
}
