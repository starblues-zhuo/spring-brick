package com.gitee.starblues.core.launcher.plugin.involved;

import com.gitee.starblues.core.PluginLauncherManager;
import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;
import com.gitee.starblues.spring.ApplicationContext;
import com.gitee.starblues.spring.SpringPluginHook;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author starBlues
 * @version 3.0.0
 */
public class PluginApplicationContextGetter implements PluginLaunchInvolved{

    private static final Map<String, ApplicationContext> PLUGIN_CONTEXTS = new ConcurrentHashMap<>();

    @Override
    public void after(InsidePluginDescriptor descriptor, ClassLoader classLoader, SpringPluginHook pluginHook) throws Exception {
        PLUGIN_CONTEXTS.put(descriptor.getPluginId(), pluginHook.getApplicationContext());
    }

    @Override
    public void close(InsidePluginDescriptor descriptor, ClassLoader classLoader) throws Exception {
        PLUGIN_CONTEXTS.remove(descriptor.getPluginId());
    }

    public static ApplicationContext get(String pluginId){
        return PLUGIN_CONTEXTS.get(pluginId);
    }

    public static Map<String, ApplicationContext> get(){
        return Collections.unmodifiableMap(PLUGIN_CONTEXTS);
    }

}
