package com.gitee.starblues.extension.resources;

import com.gitee.starblues.extension.resources.resolver.PluginResourceResolver;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.post.PluginPostProcessorExtend;
import com.gitee.starblues.utils.OrderPriority;
import com.gitee.starblues.utils.SpringBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.ObjectUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 插件资源处理器
 *
 * @author starBlues
 * @version 2.4.0
 */
public class PluginResourceResolverProcess implements PluginPostProcessorExtend {

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginResourceResolverProcess.class);
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
            if(pluginRegistryInfo == null){
                continue;
            }
            String pluginId = pluginRegistryInfo.getPluginWrapper().getPluginId();
            try {
                // 直接从配置文件获取, 后续版本移除从实现类中获取配置
                Set<String> locations = pluginRegistryInfo.getPluginBinder()
                        .bind(PropertyKey.STATIC_LOCATIONS, Bindable.setOf(String.class))
                        .orElseGet(()->null);
                if(ObjectUtils.isEmpty(locations)){
                    StaticResourceConfig config = SpringBeanUtils.getObjectByInterfaceClass(
                            pluginRegistryInfo.getConfigSingletons(),
                            StaticResourceConfig.class);
                    if(config != null){
                        locations = config.locations();
                    }
                }
                if(ObjectUtils.isEmpty(locations)){
                    return;
                }
                PluginResourceResolver.parse(pluginRegistryInfo, locations);
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
