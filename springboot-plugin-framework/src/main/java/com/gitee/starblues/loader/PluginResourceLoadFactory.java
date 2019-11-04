package com.gitee.starblues.loader;

import com.gitee.starblues.extension.ExtensionInitializer;
import com.gitee.starblues.loader.load.PluginClassLoader;
import com.gitee.starblues.realize.BasePlugin;
import com.gitee.starblues.utils.CommonUtils;
import com.gitee.starblues.utils.OrderPriority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 插件资源加载者
 *
 * @author zhangzhuo
 * @version 2.2.0
 */
public class PluginResourceLoadFactory {

    private static final Logger LOG = LoggerFactory.getLogger(PluginResourceLoadFactory.class);

    private final Map<String, ResourceWrapper> pluginResourceWrappers = new ConcurrentHashMap<>();
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

    /**
     * 加载插件类
     * @param basePlugin 当前插件信息
     */
    public synchronized void load(BasePlugin basePlugin) {
        for (PluginResourceLoader pluginResourceLoader : pluginResourceLoaders) {
            if(pluginResourceLoader == null){
                continue;
            }
            String key = pluginResourceLoader.key();
            if(StringUtils.isEmpty(key)){
                LOG.error("pluginResourceLoader {} key is empty, skip!",
                        pluginResourceLoader.getClass().getName());
                continue;
            }
            try {
                ResourceWrapper resourceWrapper = pluginResourceLoader.load(basePlugin);
                if(resourceWrapper != null){
                    pluginResourceWrappers.put(key, resourceWrapper);
                }
            } catch (Exception e){
                LOG.error("Plugin resource loader '{}' load error. {}", key, e.getMessage(), e);
            }

        }
    }

    /**
     * 卸载加载者加载的资源
     * @param basePlugin 当前插件信息
     */
    public synchronized void unload(BasePlugin basePlugin) {
        for (PluginResourceLoader pluginResourceLoader : pluginResourceLoaders) {
            if(pluginResourceLoader == null){
                continue;
            }
            String key = pluginResourceLoader.key();
            try {
                ResourceWrapper resourceWrapper = pluginResourceWrappers.get(key);
                pluginResourceLoader.unload(basePlugin, resourceWrapper);
            } catch (Exception e){
                LOG.error("Plugin resource loader '{}' unload error. {}", key, e.getMessage(), e);
            }
        }
    }



    /**
     * 根据资源加载者的key获取插件资源
     * @param key key
     * @return ResourceWrapper
     */
    public ResourceWrapper getPluginResources(String key) {
        return pluginResourceWrappers.get(key);
    }




}
