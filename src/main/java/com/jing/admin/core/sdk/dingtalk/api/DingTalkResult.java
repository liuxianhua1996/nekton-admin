package com.jing.admin.core.sdk.dingtalk.api;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

/**
 * DingTalkResult - 钉钉API结果
 *
 * @author zhicheng
 * @version 1.0
 * @see
 * @since 2025/12/30
 */
@Data
public class DingTalkResult<T> {
    private T result;

    private T recordresult;

    private int errcode;

    private String request_id;

    public void toJavaObject(Class<T> clazz) {
        if (result != null && !clazz.isInstance(result)) {
            this.result = JSONObject.parseObject(JSONObject.toJSONString(result), clazz);
        }
        else  if (recordresult != null && !clazz.isInstance(recordresult)) {
            this.result = JSONObject.parseObject(JSONObject.toJSONString(recordresult), clazz);
        }
    }
    public void toList(Class<T> clazz) {
        if (result != null && !clazz.isInstance(result)) {
            this.result =  (T) JSONArray.parseArray(JSONObject.toJSONString(result),clazz);
        }
        if (recordresult != null && !clazz.isInstance(recordresult)) {
            this.result = (T) JSONArray.parseArray(JSONObject.toJSONString(recordresult),clazz);
        }
    }
}
