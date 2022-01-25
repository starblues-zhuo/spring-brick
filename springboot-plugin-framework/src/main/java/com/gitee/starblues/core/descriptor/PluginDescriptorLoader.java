package com.gitee.starblues.core.descriptor;

import com.gitee.starblues.core.exception.PluginException;

import java.nio.file.Path;

/**
 * 插件描述加载者
 * @author starBlues
 * @version 3.0.0
 */
public interface PluginDescriptorLoader extends AutoCloseable{



    /**
     * 加载 PluginDescriptor
     * @param location 引导配置文件路径
     * @return PluginDescriptor
     * @throws PluginException 加载异常
     */
    InsidePluginDescriptor load(Path location) throws PluginException;


}
