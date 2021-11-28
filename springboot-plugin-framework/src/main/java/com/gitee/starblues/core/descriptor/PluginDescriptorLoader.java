package com.gitee.starblues.core.descriptor;

import com.gitee.starblues.core.PluginException;

import java.nio.file.Path;

/**
 * 插件描述加载者
 * @author starBlues
 * @version 3.0.0
 */
public interface PluginDescriptorLoader {

    String BOOTSTRAP_FILE_NAME = "plugin.properties";

    String PLUGIN_ID = "plugin.id";
    String PLUGIN_CLASS = "plugin.class";
    String PLUGIN_VERSION = "plugin.version";
    String PLUGIN_LIB_DIR = "plugin.libDir";
    String PLUGIN_DESCRIPTION = "plugin.description";
    String PLUGIN_PROVIDER = "plugin.provider";
    String PLUGIN_DEPENDENCIES = "plugin.dependencies";

    String PLUGIN_REQUIRES = "plugin.requires";
    String PLUGIN_LICENSE = "plugin.license";
    String PLUGIN_CONFIG_FILE_NAME = "plugin.configFileName";

    /**
     * 加载 PluginDescriptor
     * @param location 引导配置文件路径
     * @return PluginDescriptor
     * @throws PluginException 加载异常
     */
    PluginDescriptor load(Path location) throws PluginException;


}
