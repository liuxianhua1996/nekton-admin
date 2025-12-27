/**
 * SDK包结构说明
 * 
 * com.jing.admin.core.workflow.node.impl.sdk
 * ├── ISdkClient.java           # SDK客户端通用接口
 * ├── SdkManager.java           # SDK管理器，负责注册和获取各种SDK客户端实例
 * ├── SdkClientRegistrar.java   # SDK客户端注册器，Spring组件，负责初始化SDK客户端
 * ├──
 * ├── dingtalk/                 # 钉钉相关SDK实现
 * │   └── DingtalkSDK.java      # 钉钉SDK实现类
 * ├── kingdee/                  # 金蝶相关SDK实现
 * │   └── KingdeeSkySDK.java    # 金蝶云星空SDK实现类
 * └── thirdparty/               # 其他第三方SDK实现
 *     └── WechatSDK.java        # 微信SDK实现类
 * 
 * 使用说明：
 * 1. 新增SDK实现需要实现ISdkClient接口
 * 2. 在SdkManager.initializeDefaultClients()中注册新的SDK
 * 3. 系统标识需要与executeSdkCall方法中的system参数对应
 */
package com.jing.admin.core.workflow.node.impl.sdk;