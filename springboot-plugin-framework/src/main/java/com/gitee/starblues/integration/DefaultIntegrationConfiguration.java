package com.gitee.starblues.integration;

/**
 * 默认的插件集成配置。给非必须配置设置了默认值
 *
 * @author zhangzhuo
 * @version 1.0
 */
public abstract class DefaultIntegrationConfiguration implements IntegrationConfiguration{


    @Override
    public String uploadTempPath(){
        return "temp";
    }

    @Override
    public String backupPath(){
        return "backupPlugin";
    }

    @Override
    public String pluginRestControllerPathPrefix(){
        return "/plugins";
    }

    @Override
    public boolean enablePluginIdRestControllerPathPrefix() {
        return true;
    }
}
