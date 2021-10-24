package com.gitee.starblues.integration.pf4j.descriptor;

import org.pf4j.DefaultPluginDescriptor;
import org.pf4j.PluginDescriptor;
import org.pf4j.PropertiesPluginDescriptorFinder;
import org.pf4j.RuntimeMode;
import org.pf4j.util.StringUtils;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * 读取 resources 目录下的 plugin.properties 文件
 * @author starBlues
 * @version 2.4.0
 */
public class ResourcesPluginDescriptorFinder extends PropertiesPluginDescriptorFinder {

    public static final String PLUGIN_CONFIG_FILE_NAME = "plugin.configFileName";
    public static final String PLUGIN_CONFIG_FILE_PROFILE = "plugin.configFileProfile";

    private final RuntimeMode runtimeMode;

    public ResourcesPluginDescriptorFinder(RuntimeMode runtimeMode) {
        this.runtimeMode = runtimeMode;
    }


    @Override
    public boolean isApplicable(Path pluginPath) {
        Path propFilePath = getPropFilePath(pluginPath);
        return super.isApplicable(propFilePath);
    }

    @Override
    public PluginDescriptor find(Path pluginPath) {
        Path propFilePath = getPropFilePath(pluginPath);
        return super.find(propFilePath);
    }

    @Override
    protected Properties readProperties(Path pluginPath) {
        Path propertiesPath = getPropertiesPath(pluginPath, propertiesFileName);
        return ResolvePropertiesPluginDescriptorFinder.getProperties(propertiesPath);
    }

    @Override
    protected PluginDescriptor createPluginDescriptor(Properties properties) {
        DefaultPluginDescriptorExtend pluginDescriptor = (DefaultPluginDescriptorExtend)
                super.createPluginDescriptor(properties);
        return resolvePluginDescriptor(properties, pluginDescriptor);
    }

    static PluginDescriptor resolvePluginDescriptor(Properties properties,
                                                    DefaultPluginDescriptorExtend pluginDescriptor){
        String configFileName = properties.getProperty(PLUGIN_CONFIG_FILE_NAME);
        if (!StringUtils.isNullOrEmpty(configFileName)) {
            pluginDescriptor.setConfigFileName(configFileName);
        }

        String configFileProfile = properties.getProperty(PLUGIN_CONFIG_FILE_PROFILE);
        if (!StringUtils.isNullOrEmpty(configFileProfile)) {
            pluginDescriptor.setConfigFileProfile(configFileProfile);
        }

        return pluginDescriptor;
    }

    @Override
    protected DefaultPluginDescriptor createPluginDescriptorInstance() {
        return new DefaultPluginDescriptorExtend();
    }

    private Path getPropFilePath(Path pluginPath){
        if(runtimeMode == RuntimeMode.DEPLOYMENT){
            // 生产环境
            return pluginPath;
        } else {
            // 开发环境
            return Paths.get(pluginPath.toString(), "src", "main", "resources");
        }
    }

}
