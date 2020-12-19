package com.gitee.starblues.extension.resources;

import com.gitee.starblues.extension.ExtensionConfigUtils;
import com.gitee.starblues.extension.resources.resolver.PluginResourceResolver;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.post.PluginPostProcessorExtend;
import com.gitee.starblues.utils.OrderPriority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 * 插件资源处理器
 *
 * @author zhangzhuo
 * @version 2.2.1
 */
public class PluginResourceResolverProcess implements PluginPostProcessorExtend {

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginResourceResolverProcess.class);
    private static final String KEY = "PluginResourceResolverProcess";

    private final ApplicationContext applicationContext;

    PluginResourceResolverProcess(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
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
            if(pluginRegistryInfo == null){
                continue;
            }
            String pluginId = pluginRegistryInfo.getPluginWrapper().getPluginId();
            try {
                StaticResourceConfig staticResourceConfig = ExtensionConfigUtils.getConfig(
                        applicationContext, pluginId, StaticResourceConfig.class);
                PluginResourceResolver.parse(pluginRegistryInfo.getBasePlugin(), staticResourceConfig);
            } catch (Exception e){
                LOGGER.error("Parse plugin '{}' static resource failure.", pluginId, e);
            }
        }
    }

    @Override
    public void unRegistry(List<PluginRegistryInfo> pluginRegistryInfos) throws Exception {
        for (PluginRegistryInfo pluginRegistryInfo : pluginRegistryInfos) {
            try {
                PluginResourceResolver.remove(pluginRegistryInfo.getPluginWrapper().getPluginId());
            } catch (Exception e){
                LOGGER.error("Remove plugin '{}' static resource failure.",
                        pluginRegistryInfo.getPluginWrapper().getPluginId(),
                        e);
            }

        }
    }
}
