package com.gitee.starblues.core.checker;

import com.gitee.starblues.core.PluginInfo;
import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.core.exception.PluginException;

import java.nio.file.Path;

/**
 * 插件启动检查者
 * @author starBlues
 * @version 3.0.0
 */
public interface PluginLauncherChecker {

    /**
     * 检查是否能启动
     * @param pluginInfo 插件信息
     * @throws PluginException 不能启动, 抛出PluginException异常即可
     */
    void checkCanStart(PluginInfo pluginInfo) throws PluginException;

    /**
     * 检查是否能停止
     * @param pluginInfo 插件信息
     * @throws PluginException 不能停止, 抛出PluginException异常即可
     */
    void checkCanStop(PluginInfo pluginInfo) throws PluginException;


}
