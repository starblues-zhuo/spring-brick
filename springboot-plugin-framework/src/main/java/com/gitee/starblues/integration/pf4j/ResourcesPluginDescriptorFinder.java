package com.gitee.starblues.integration.pf4j;

import org.pf4j.PluginDescriptor;
import org.pf4j.PropertiesPluginDescriptorFinder;
import org.pf4j.RuntimeMode;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 读取 resources 目录下的 plugin.properties 文件
 * @author zhangzhuo
 * @version 2.3.1
 */
public class ResourcesPluginDescriptorFinder extends PropertiesPluginDescriptorFinder {

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
