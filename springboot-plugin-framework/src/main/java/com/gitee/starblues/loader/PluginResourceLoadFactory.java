package com.gitee.starblues.loader;

import com.gitee.starblues.extension.AbstractExtension;
import com.gitee.starblues.extension.ExtensionFactory;
import com.gitee.starblues.loader.load.PluginClassLoader;
import com.gitee.starblues.realize.BasePlugin;
import com.gitee.starblues.utils.CommonUtils;
import com.gitee.starblues.utils.OrderPriority;
import org.pf4j.PluginException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 插件资源加载者
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class PluginResourceLoadFactory {

    private static final Logger LOG = LoggerFactory.getLogger(PluginResourceLoadFactory.class);

    private final Map<String, List<Resource>> pluginResourceMap = new ConcurrentHashMap<>();
    private final List<PluginResourceLoader> pluginResourceLoaders = new ArrayList<>(5);


    public PluginResourceLoadFactory() {
        this.pluginResourceLoaders.add(new PluginClassLoader());
        addExtension();
        CommonUtils.order(pluginResourceLoaders, (pluginResourceLoader -> {
            OrderPriority order = pluginResourceLoader.order();
            if (order == null) {
                order = OrderPriority.getMiddlePriority();
            }
            return order.getPriority();
        }));
    }


    /**
     * 添加扩展
     */
    private void addExtension() {
        ExtensionFactory extensionFactory = ExtensionFactory.getSingleton();
        extensionFactory.iteration(abstractExtension -> {
            List<PluginResourceLoader> pluginResourceLoaders = abstractExtension.getPluginResourceLoader();
            extensionFactory.iteration(pluginResourceLoaders, pluginResourceLoader -> {
                this.pluginResourceLoaders.add(pluginResourceLoader);
                LOG.info("Register Extension PluginResourceLoader : {}", pluginResourceLoader.key());
            });
        });
    }




    /**
     * 加载插件类
     * @param basePlugin basePlugin
     * @throws PluginException PluginException
     */
    public synchronized void load(BasePlugin basePlugin) throws PluginException {
        for (PluginResourceLoader pluginResourceLoader : pluginResourceLoaders) {
            String key = pluginResourceLoader.key();
            if(StringUtils.isEmpty(key)){
                LOG.error("pluginResourceLoader {} key is empty, skip!",
                        pluginResourceLoader.getClass().getName());
                continue;
            }
            try {
                List<Resource> resources = pluginResourceLoader.load(basePlugin);
                if(resources != null){
                    pluginResourceMap.put(key, resources);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new PluginException(e.getMessage());
            }
        }
    }

    /**
     * 根据资源加载者的key获取插件资源
     * @param key key
     * @return List
     */
    public List<Resource> getPluginResources(String key) {
        return pluginResourceMap.get(key);
    }




}
