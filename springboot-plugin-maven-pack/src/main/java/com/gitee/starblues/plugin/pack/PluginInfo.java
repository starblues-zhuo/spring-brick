package com.gitee.starblues.plugin.pack;

import lombok.Data;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * 插件信息
 * @author starBlues
 * @version 3.0.0
 */
@Data
public class PluginInfo {

    /**
     * 插件id
     */
    @Parameter(required = true)
    private String id;

    /**
     * 插件引导启动类
     */
    @Parameter(required = true)
    private String bootstrapClass;

    /**
     * 插件版本
     */
    @Parameter(required = true)
    private String version;

    /**
     * 插件配置文件名称
     */
    private String configFileName;
    private String description;
    private String provider;
    private String requires;
    private String dependencies;
    private String license;


}
