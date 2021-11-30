package com.gitee.starblues.factory.process.pipe;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 插件信息容器
 * @author starBlues
 * @version 2.4.0
 */
public class PluginInfoContainers {

    private final static Map<String, ConfigurableApplicationContext> PLUGIN_APPLICATION_CONTEXTS =
            new ConcurrentHashMap<>();

    public static void addPluginApplicationContext(String pluginId, ConfigurableApplicationContext applicationContext){
        PLUGIN_APPLICATION_CONTEXTS.put(pluginId, applicationContext);
    }

    public static void removePluginApplicationContext(String pluginId){
        PLUGIN_APPLICATION_CONTEXTS.remove(pluginId);
    }

    static public ConfigurableApplicationContext getPluginApplicationContext(String pluginId) {
        ConfigurableApplicationContext applicationContext = PLUGIN_APPLICATION_CONTEXTS.get(pluginId);
        if(applicationContext == null){
            return null;
        }
        return applicationContext;
    }

    static public List<ConfigurableApplicationContext> getPluginApplicationContexts() {
        Collection<ConfigurableApplicationContext> values = PLUGIN_APPLICATION_CONTEXTS.values();
        if(values.isEmpty()){
            return new ArrayList<>();
        }
        return new ArrayList<>(values);
    }


}
