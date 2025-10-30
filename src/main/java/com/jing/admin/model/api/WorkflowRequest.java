package com.jing.admin.model.api;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.util.List;

/**
 * @author lxh
 * @date 2025/10/27
 **/
@Data
public class WorkflowRequest {
   private String id;
   private String description;
   private String name;
   private JSONObject jsonData;
   private int version;

}
