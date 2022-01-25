package com.gitee.starblues.core;

import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.core.exception.PluginException;

import java.nio.file.Path;

/**
 * 插件检查者
 * @author starBlues
 * @version 3.0.0
 */
public interface PluginChecker {

    /**
     * 根据路径检查
     * @param path path
     * @throws Exception 检查异常
     */
    default void check(Path path) throws Exception{}

    /**
     * 检查插件描述是否合法
     * @param descriptor 插件信息
     * @throws PluginException 检查异常
     */
    default void checkDescriptor(PluginDescriptor descriptor) throws PluginException{}

    /**
     * 检查是否能启动
     * @param pluginInfo 插件信息
     * @throws PluginException 不能启动, 抛出PluginException异常即可
     */
    default void checkCanStart(PluginInfo pluginInfo) throws PluginException{}

    /**
     * 检查是否能停止
     * @param pluginInfo 插件信息
     * @throws PluginException 不能停止, 抛出PluginException异常即可
     */
    default void checkCanStop(PluginInfo pluginInfo) throws PluginException{}


}
