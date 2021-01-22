package com.gitee.starblues.factory.process.pipe;

import org.springframework.context.support.GenericApplicationContext;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 插件信息容器
 * @author starBlues
 * @version 2.4.0
 */
public class PluginInfoContainers {

    private final static Map<String, GenericApplicationContext> pluginApplicationContexts =
            new ConcurrentHashMap<>();


    static void addPluginApplicationContext(String pluginId, GenericApplicationContext applicationContext){
        pluginApplicationContexts.put(pluginId, applicationContext);
    }

    static void removePluginApplicationContext(String pluginId){
        pluginApplicationContexts.remove(pluginId);
    }

    static public GenericApplicationContext getPluginApplicationContext(String pluginId) {
        GenericApplicationContext applicationContext = pluginApplicationContexts.get(pluginId);
        if(applicationContext == null){
            return null;
        }
        return applicationContext;
    }

    static public List<GenericApplicationContext> getPluginApplicationContexts() {
        Collection<GenericApplicationContext> values = pluginApplicationContexts.values();
        if(values.isEmpty()){
            return Collections.emptyList();
        }
        return new ArrayList<>(values);
    }

}
