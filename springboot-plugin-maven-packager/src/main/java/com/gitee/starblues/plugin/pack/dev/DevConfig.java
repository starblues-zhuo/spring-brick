package com.gitee.starblues.plugin.pack.dev;

import lombok.Data;

import java.util.List;

/**
 * 开发模式配置
 * @author starBlues
 * @version 3.0.0
 */
@Data
public class DevConfig {

    /**
     * 插件编译的class目录
     */
    private String pluginPath;

    /**
     * 插件所有依赖存放的路径
     */
    private String libPath;

    /**
     * 当前项目依赖其他模块的定义。
     * 主要定义依赖模块target->classes的目录, 方便开发调试
     */
    private List<Dependency> moduleDependencies;

}
