package com.gitee.starblues.plugin.pack;

import com.gitee.starblues.common.AbstractDependencyPlugin;
import lombok.Data;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;

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

    /**
     * 插件描述
     */
    private String description;

    /**
     * 插件提供者
     */
    private String provider;

    /**
     * 需要安装的主程序版本
     */
    private String requires;

    /**
     * 插件 license
     */
    private String license;

    /**
     * 依赖的插件
     */
    private List<DependencyPlugin> dependencyPlugins;

}
