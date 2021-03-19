package com.gitee.starblues.integration;

import java.util.List;
import java.util.Set;

/**
 * 默认的插件集成配置。给非必须配置设置了默认值
 *
 * @author starBlues
 * @version 2.4.2
 */
public abstract class DefaultIntegrationConfiguration implements IntegrationConfiguration{

    @Override
    public boolean enable() {
        return true;
    }

    @Override
    public String uploadTempPath(){
        return "temp";
    }

    @Override
    public String backupPath(){
        return "backupPlugin";
    }

    @Override
    public String pluginRestPathPrefix(){
        return "/plugins";
    }

    @Override
    public boolean enablePluginIdRestPathPrefix() {
        return true;
    }

    @Override
    public Set<String> enablePluginIds() {
        return null;
    }

    @Override
    public Set<String> disablePluginIds() {
        return null;
    }

    @Override
    public boolean enableSwaggerRefresh() {
        return true;
    }

    @Override
    public List<String> sortInitPluginIds() {
        return null;
    }

    @Override
    public String version() {
        return "0.0.0";
    }

    @Override
    public boolean exactVersionAllowed() {
        return false;
    }

    @Override
    public boolean enableWebSocket() {
        return false;
    }
}
