package com.gitee.starblues.spring.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.CacheControl;

/**
 * 插件PluginStaticResourceConfig
 * @author starBlues
 * @version 3.0.0
 */
public class PluginStaticResourceConfig {

    public static final String DEFAULT_PLUGIN_STATIC_RESOURCE_PATH_PREFIX = "static-plugin";
    private static final Logger log = LoggerFactory.getLogger(PluginStaticResourceConfig.class);

    private String pathPrefix = DEFAULT_PLUGIN_STATIC_RESOURCE_PATH_PREFIX;
    private CacheControl cacheControl = CacheControl.noCache();

    public PluginStaticResourceConfig(){

    }

    public String getPathPrefix() {
        return pathPrefix;
    }

    public void setPathPrefix(String pathPrefix) {
        this.pathPrefix = pathPrefix;
    }

    public CacheControl getCacheControl() {
        return cacheControl;
    }

    public void setCacheControl(CacheControl cacheControl) {
        this.cacheControl = cacheControl;
    }

    public void logPathPrefix(){
        log.info("插件静态资源访问前缀配置为: /{}/{pluginId}", pathPrefix);
    }
}
