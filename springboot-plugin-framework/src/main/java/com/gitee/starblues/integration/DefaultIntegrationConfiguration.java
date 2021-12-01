package com.gitee.starblues.integration;

import com.gitee.starblues.core.RuntimeMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 默认的插件集成配置。给非必须配置设置了默认值
 *
 * @author starBlues
 * @version 2.4.4
 */
public abstract class DefaultIntegrationConfiguration implements IntegrationConfiguration{

    public static final String DEFAULT_PLUGIN_REST_PATH_PREFIX = "plugins";
    public static final boolean DEFAULT_ENABLE_PLUGIN_ID_REST_PATH_PREFIX = true;

    @Override
    public boolean enable() {
        return true;
    }

    @Override
    public List<String> pluginPath() {
        List<String> pluginPath = new ArrayList<>(1);
        if(environment() == RuntimeMode.DEV){
            pluginPath.add("plugins");
        } else if(environment() == RuntimeMode.PROD){
            pluginPath.add("./plugins/");
        }
        return pluginPath;
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
    public boolean enablePluginRestController() {
        return true;
    }

    @Override
    public String pluginRestPathPrefix(){
        return DEFAULT_PLUGIN_REST_PATH_PREFIX;
    }

    @Override
    public boolean enablePluginIdRestPathPrefix() {
        return DEFAULT_ENABLE_PLUGIN_ID_REST_PATH_PREFIX;
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

    @Override
    public boolean stopDependents() {
        return false;
    }
}
