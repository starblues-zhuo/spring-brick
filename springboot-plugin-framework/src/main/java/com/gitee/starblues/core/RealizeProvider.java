package com.gitee.starblues.core;

import com.gitee.starblues.core.descriptor.PluginDescriptorLoader;
import com.gitee.starblues.core.loader.PluginLoader;
import com.gitee.starblues.core.scanner.PluginScanner;
import com.gitee.starblues.core.version.VersionInspector;
/**
 * 插件扩展配置
 * @author starBlues
 * @version 3.0.0
 */
public interface RealizeProvider {

    /**
     * 当前运行环境
     * @return RuntimeMode
     */
    RuntimeMode getRuntimeMode();

    /**
     * 得到 PluginScanner 实现
     * @return PluginScanner
     */
    PluginScanner getPluginScanner();

    /**
     * 得到 PluginDescriptorLoader 实现
     * @return PluginDescriptorLoader
     */
    PluginDescriptorLoader getPluginDescriptor();

    /**
     * 得到 PluginLoader 实现
     * @return PluginLoader
     */
    PluginLoader getPluginLoader();

    /**
     * 得到 PluginChecker 实现
     * @return PluginChecker
     */
    PluginChecker getPluginChecker();

    /**
     * 得到 VersionInspector 实现
     * @return VersionInspector
     */
    VersionInspector getVersionInspector();

}
