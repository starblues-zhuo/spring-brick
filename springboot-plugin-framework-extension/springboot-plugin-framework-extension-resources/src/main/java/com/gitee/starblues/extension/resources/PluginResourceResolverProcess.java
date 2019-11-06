package com.gitee.starblues.extension.resources;

import com.gitee.starblues.extension.resources.resolver.PluginResourceResolver;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.post.PluginPostProcessorExtend;
import com.gitee.starblues.utils.OrderPriority;

import java.util.List;

/**
 * 插件资源处理器
 *
 * @author zhangzhuo
 * @version 2.2.1
 */
public class PluginResourceResolverProcess implements PluginPostProcessorExtend {

    private static final String KEY = "PluginResourceResolverProcess";


    PluginResourceResolverProcess() {
    }

    @Override
    public String key() {
        return KEY;
    }

    @Override
    public OrderPriority order() {
        return OrderPriority.getMiddlePriority();
    }

    @Override
    public void initialize() throws Exception {

    }

    @Override
    public synchronized void registry(List<PluginRegistryInfo> pluginRegistryInfos) throws Exception {
        for (PluginRegistryInfo pluginRegistryInfo : pluginRegistryInfos) {
            PluginResourceResolver.parse(pluginRegistryInfo.getBasePlugin());
        }
    }

    @Override
    public void unRegistry(List<PluginRegistryInfo> pluginRegistryInfos) throws Exception {
        for (PluginRegistryInfo pluginRegistryInfo : pluginRegistryInfos) {
            PluginResourceResolver.remove(pluginRegistryInfo.getPluginWrapper().getPluginId());
        }
    }
}
