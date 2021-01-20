package com.gitee.starblues.factory.process.pipe.loader;

import com.gitee.starblues.extension.ExtensionInitializer;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.PluginPipeProcessor;
import com.gitee.starblues.factory.process.pipe.loader.load.PluginClassLoader;
import com.gitee.starblues.realize.BasePlugin;
import com.gitee.starblues.utils.CommonUtils;
import com.gitee.starblues.utils.OrderPriority;
import org.pf4j.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 插件资源加载者
 *
 * @author starBlues
 * @version 2.2.0
 */
public class PluginResourceLoadFactory implements PluginPipeProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(PluginResourceLoadFactory.class);


    private final List<PluginResourceLoader> pluginResourceLoaders = new ArrayList<>(5);


    public PluginResourceLoadFactory() {
        this.pluginResourceLoaders.add(new PluginClassLoader());
        // 添加扩展
        this.pluginResourceLoaders.addAll(ExtensionInitializer.getResourceLoadersExtends());
        CommonUtils.order(pluginResourceLoaders, (pluginResourceLoader -> {
            OrderPriority order = pluginResourceLoader.order();
            if (order == null) {
                order = OrderPriority.getMiddlePriority();
            }
            return order.getPriority();
        }));
    }

    @Override
    public void initialize() throws Exception {

    }

    @Override
    public void registry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        for (PluginResourceLoader pluginResourceLoader : pluginResourceLoaders) {
            if(pluginResourceLoader == null){
                continue;
            }
            String key = pluginResourceLoader.key();
            if(StringUtils.isNullOrEmpty(key)){
                LOG.error("pluginResourceLoader {} key is empty, skip!",
                        pluginResourceLoader.getClass().getName());
                continue;
            }
            try {
                ResourceWrapper resourceWrapper = pluginResourceLoader.load(pluginRegistryInfo);
                if(resourceWrapper != null){
                    pluginRegistryInfo.addPluginLoadResource(key, resourceWrapper);
                }
            } catch (Exception e){
                LOG.error("Plugin resource loader '{}' load error. {}", key, e.getMessage(), e);
            }
        }
    }

    @Override
    public void unRegistry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        for (PluginResourceLoader pluginResourceLoader : pluginResourceLoaders) {
            if(pluginResourceLoader == null){
                continue;
            }
            String key = pluginResourceLoader.key();
            try {
                ResourceWrapper resourceWrapper = pluginRegistryInfo.getPluginLoadResource(key);
                if(resourceWrapper == null){
                    continue;
                }
                pluginResourceLoader.unload(pluginRegistryInfo, resourceWrapper);
            } catch (Exception e){
                LOG.error("Plugin resource loader '{}' unload error. {}", key, e.getMessage(), e);
            }
        }
    }
}
