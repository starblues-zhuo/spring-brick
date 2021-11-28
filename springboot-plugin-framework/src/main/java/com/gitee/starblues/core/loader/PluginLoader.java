package com.gitee.starblues.core.loader;

import com.gitee.starblues.core.descriptor.PluginDescriptor;

/**
 * 插件加载者
 * @author starBlues
 * @version 3.0.0
 */
public interface PluginLoader {


    /**
     * 根据插件描述加载插件
     * @param descriptor 插件描述
     * @return PluginWrapper
     * @throws Exception 加载异常
     */
    PluginWrapperInside load(PluginDescriptor descriptor) throws Exception;


}
